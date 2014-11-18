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
package com.ibm.xsp.extlib.designer.xspprops.xsplibs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.constants.log.DesignerLoggerAdapter;
import com.ibm.designer.domino.ide.resources.dbproperties.XspPropertiesDesignTimeAttrs;
import com.ibm.xsp.extlib.designer.xspprops.XSPAllProperties;
import com.ibm.xsp.library.LibraryWrapper;

/**
 * @author doconnor
 *
 */
public class XPageLibraryCheckStateListener implements ICheckStateListener, XspPropertiesDesignTimeAttrs {
    private DataNode parentDataNode;
    private XSPAllProperties xspProperties;

    /**
     * 
     */
    public XPageLibraryCheckStateListener(DataNode dn, XSPAllProperties xspProps) {
        this.parentDataNode = dn;
        this.xspProperties = xspProps;
    }

    /*
     * (non-Javadoc) 
     * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
     */
    public void checkStateChanged(CheckStateChangedEvent event) {
        parentDataNode.setModelModified(true);
        if(event != null){
            Object element = event.getElement();
            
            DataNode root = parentDataNode.getRootNode();
            IMember member = root.getMember(ATTR_XSP_PROPS_BEAN); // $NON-NLS-1$
            IMember dep = null;
            if(member != null && member.getTypeDef() != null){
                dep = member.getTypeDef().getMember(ATTR_XSP_DEPENDENCIES); // $NON-NLS-1$
            }
            
            ILoader loader = root.getLoader();
            if(event.getChecked()){
                //add library
                if(element instanceof LibraryWrapper){
                    LibraryWrapper library = (LibraryWrapper)element;
                    xspProperties.appendDependencies(library.getLibraryId());
                    if(loader != null && dep instanceof IAttribute){
                        try {
                            loader.setValue(xspProperties, (IAttribute)dep, xspProperties.getDependencies(), null);
                        } catch (NodeException e) {
                            if(DesignerLoggerAdapter.DB_PROPS_LOG.isWarnEnabled()){
                                DesignerLoggerAdapter.DB_PROPS_LOG.warnp(this, "checkStateChanged", e, "Failed to add to dependencies in xsp.properties. Failed to add: {0}", library.getLibraryId()); // $NON-NLS-1$ $NLI-XPageLibraryCheckStateListener.Failedtoaddtodependenciesinxsppro-2$
                            }
                        }
                    }
                }
            }
            else{
                //remove library
                if(element instanceof LibraryWrapper){
                    LibraryWrapper library = (LibraryWrapper)element;                    
                    String ids = StringUtil.getNonNullString(xspProperties.getDependencies());
                    String id = library.getLibraryId();
                    List<String> list = new ArrayList<String>(Arrays.asList(ids.split(",")));
                    if(list.contains(id)){
                        list.remove(id);
                        xspProperties.setDependencies(null);
                        boolean first = true;
                        for(String newId : list){
                            if(first){
                                xspProperties.appendDependencies(newId);
                            }else{
                                xspProperties.appendDependencies("," + newId);
                            }
                        }
                        if(loader != null && dep instanceof IAttribute){
                            try {
                                loader.setValue(xspProperties, (IAttribute)dep, xspProperties.getDependencies(), null);
                            } catch (NodeException e) {
                                if(DesignerLoggerAdapter.DB_PROPS_LOG.isWarnEnabled()){
                                    DesignerLoggerAdapter.DB_PROPS_LOG.warnp(this, "checkStateChanged", e, "Failed to remove library dependency ({0}) from xsp.properties", id); // $NON-NLS-1$ $NLI-XPageLibraryCheckStateListener.Failedtoremovelibrarydependency0f-2$
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}