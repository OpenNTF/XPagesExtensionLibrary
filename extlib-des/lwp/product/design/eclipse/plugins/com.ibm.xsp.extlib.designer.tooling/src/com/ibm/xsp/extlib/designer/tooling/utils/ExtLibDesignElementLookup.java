/*
 * © Copyright IBM Corp. 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.xsp.extlib.designer.tooling.utils;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.LookupListener;
import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerDesignElement;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;

/**
 * @author mblout
 * 
 * This class will evolve to include other element types, but for now
 * it only really returns XPages, and changes based on additions and removals of XPages.
 * 
 * @deprecated Use com.ibm.designer.domino.ide.resources.DesignerDELookup instead
 * 
 */
public class ExtLibDesignElementLookup implements ILookup {
    
    
    IResourceChangeListener resListener = new IResourceChangeListener() {
        public void resourceChanged(IResourceChangeEvent event) {
            // @TODO: need to filter resource events!
            try {
                IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
                    
                    boolean keepVisiting = true;
                    
                    public boolean visit(IResourceDelta delta) {
                        
                        if (!keepVisiting || null == designerProject)
                            return false;
                        
                        int k = delta.getKind();
                        if ( ! (IResourceDelta.ADDED == k || 
                                IResourceDelta.REMOVED == k || 
                                IResourceDelta.CHANGED == k) )  // an update could be a name change
                           return true;

//                       if ((delta.getFlags() ...?
                       IResource resource = delta.getResource();
                       if (resource.getType() == IResource.FILE && resource.getProject().equals(designerProject.getProject())) {
                           String resExt = resource.getFileExtension();
                           String typeExt = extForType(); 
                           if (typeExt.length() > 0 && resExt.length() > 0 &&
                               typeExt.equalsIgnoreCase(resExt)) {
                               keepVisiting = false;
                               updateDesignElements();
                           }
                       }
                       return keepVisiting;
                    }
                 };
                 
                 event.getDelta().accept(visitor);
            }
            catch(Exception e) {
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());            }
        }
    };

    // add things as we need them
    private String extForType() {
        if (StringUtil.equals(typeId, DesignerResource.TYPE_XPAGE)) {
            return ".xsp"; // $NON-NLS-1$
        }
        else if (StringUtil.equals(typeId, DesignerResource.TYPE_THEME)) {
            return ".theme"; // $NON-NLS-1$
        }
        return "";
    }
    
    
    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.ILookup#getLabelFromCode(java.lang.String)
     */
    public String getLabelFromCode(String code) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.ILookup#getImage(int)
     */
    public Image getImage(int index) {
        return null;
    }

    
    protected DesignerDesignElement[] designElements = null;
    protected DesignerProject designerProject;
    protected String typeId;
    protected DesignerDesignElementFilter filter;
    protected int folderCount;
    private boolean showExtension = false;
    private boolean bStartResultWithSep = true;
    
    public interface DesignerDesignElementFilter{
        public boolean accept(DesignerDesignElement de);
    }
   
    /**
     * 
     */
    public ExtLibDesignElementLookup(DesignerProject designerProject, String typeId) {
        this(designerProject, typeId, false, null);
    }
    
    private ExtLibDesignElementLookup(DesignerProject designerProject, String typeId, boolean showExtension, DesignerDesignElementFilter filter) {
        this(designerProject, typeId, false, true, null);
    }
    
    public ExtLibDesignElementLookup(DesignerProject designerProject, String typeId, boolean showExtension, boolean bIncludeSep, DesignerDesignElementFilter filter) {
        this.designerProject = designerProject;
        this.typeId = typeId;
        this.filter = filter;
        this.bStartResultWithSep = bIncludeSep;
        
        updateDesignElements();
    }
    protected DesignerDesignElement[] getDesignElements() {
        return designElements;
    }
    
    protected void setDesignElements(DesignerDesignElement[] elements) {
        designElements = elements;
    }
    
    /**
     * 
     */
    protected void updateDesignElements() {
        designElements = designerProject.getDesignElements(typeId);
        folderCount = getNavigatorFolderCount();
        new QuickSort.ObjectArray(designElements) {
            public int compare(Object o1, Object o2) {
                if(null != o1 && null != o2){
                    return StringUtil.compareToIgnoreCase(o1.toString(),o2.toString());
                }
                return 0;
            }
        }.sort();
        notifyLookupChanged();
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.node.ILookup#size()
     */
    public int size() {
        return designElements.length;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.node.ILookup#getCode(int)
     */
    public String getCode(int index) {
        DesignerDesignElement designElement = getDesignElement( index );
        IPath desPath = designElement.getResource().getProjectRelativePath();
        if (bStartResultWithSep)
            return IPath.SEPARATOR + desPath.removeFirstSegments(folderCount).toString();
        else
            return desPath.removeFirstSegments(folderCount).toString();
    }

    /**
     * @param index
     * @return
     */
    public DesignerDesignElement getDesignElement(int index) {
        if ( index < 0 || index >= designElements.length ){
            return null;
        }
        return designElements[ index ];
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.node.ILookup#getLabel(int)
     */
    public String getLabel(int index) {
        DesignerDesignElement designElement = getDesignElement( index );
        String name = designElement == null ? "" : designElement.getName();
        if(StringUtil.isNotEmpty(name)){
            if(!showExtension){
                int i = name.lastIndexOf(".");
                if(i > 0){
                    return name.substring(0, i);
                }
            }
        }
        return name;
    }
    
    /**
     * adds a DisposeListener to the given control to remove workspace listener.
     * Normally, the workspace listener will be removed when all LookupListeners are removed,
     * but this gives us another way to insure it is removed.
     * @param c
     */
    public void setControl(Control c) {
        c.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                listeners.clear();
                removeNotify();
            }
        });
        
    }

    
    /**
     * @todo: revisit the ILookup and LookupListener interface in the API
     * need to make this easier.
     */
    
    private DesignerDesignElement[] cachedElements;
    
    protected void notifyLookupChanged() {
        DesignerDesignElement[] elements = getDesignElements();
        if(null == cachedElements){
            cachedElements = elements;
        }
        else {
            //weird refresh issue here. When you do a save of the xpage with the computed combos, it fires a refresh, which
            //in turn fires this notifyLookupChanged which causes a selection event in the combo, which causes the value of the
            //attribute to be set in the source again. So when you save an xpage, it saves, but then dirties itself again immediately
            //as the attribute is set again.
            //The workaround is to check for the old design list and new design list - if the elements are the
            //same then it was not really and update and does not need a refresh
            if(cachedElements.length == elements.length){
                boolean equal = true;
                try{
                    int i = 0;
                    for(DesignerDesignElement cachedElement : cachedElements){
                        if(!(cachedElement.equals(elements[i]))){
                            equal = false;
                            break;
                        }
                        if(!StringUtil.equals(cachedElement.getName(), elements[i].getName())){
                            equal = false;
                            break;
                        }
                        if(!StringUtil.equals(cachedElement.getAlias(), elements[i].getAlias())){
                            equal = false;
                            break;
                        }
                        i++;
                    }
                }
                catch(Exception e){
                    //if there are any errors, do the update as normal. Should never happen, but want to handle any
                    //IndexOutOfBounds exceptions etc.. if they arise. 
                    equal = false;
                }
                if(equal){
                    return;
                }
            }
        }
        
        notifyLookupChangedInternal();
    }

    public static ExtLibDesignElementLookup getXPagesLookup(DesignerProject project){
        return new ExtLibDesignElementLookup(project, DesignerResource.TYPE_XPAGE); 
    }

    public static ExtLibDesignElementLookup getThemeLookup(DesignerProject project){
        return new ExtLibDesignElementLookup(project, DesignerResource.TYPE_THEME); 
    }

    private ArrayList<LookupListener> listeners;
    
    
    protected void addNotify() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resListener);
    }

    protected void removeNotify() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resListener);
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.ILookup#addLookupListener(com.ibm.commons.iloader.node.lookups.api.LookupListener)
     */
    public void addLookupListener( LookupListener listener ) {
        if(listeners==null) {
            listeners = new ArrayList<LookupListener>();
        }
        listeners.add(listener);
        if(listeners.size()==1) {
            addNotify();
        }
    }
    /*
     * (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.ILookup#removeLookupListener(com.ibm.commons.iloader.node.lookups.api.LookupListener)
     */
    public void removeLookupListener( LookupListener listener ) {
        if(listeners!=null) {
            listeners.remove(listener);
            if(listeners.size()==0) {
                removeNotify();
            }
        }
    }

    /*
     * Called when the lookup is changed, to notify listeners in an syncExec.
     * 
     */
    protected void notifyLookupChangedInternal() {
        if(listeners!=null) {
            // Ensure that we execute in the SWT thread
            Display.getDefault().syncExec( new Runnable() {
                public void run() {
                    int count = listeners.size();
                    for( int i=0; i<count; i++ ) {
                        LookupListener l = listeners.get(i);
                        l.lookupChanged(ExtLibDesignElementLookup.this);
                    }
                }
            });
        }
    }
    protected int getNavigatorFolderCount() {
        if (typeId.equals(DesignerResource.TYPE_FRAMESET) ||
            typeId.equals(DesignerResource.TYPE_FORM) ||
            typeId.equals(DesignerResource.TYPE_XPAGE) ||
            typeId.equals(DesignerResource.TYPE_VIEW) ||
            typeId.equals(DesignerResource.TYPE_FOLDER) ||
            typeId.equals(DesignerResource.TYPE_CUSTOMCTRL) ||
            typeId.equals(DesignerResource.TYPE_PAGE))
            return 1;
        else
            return 2;
    }

}