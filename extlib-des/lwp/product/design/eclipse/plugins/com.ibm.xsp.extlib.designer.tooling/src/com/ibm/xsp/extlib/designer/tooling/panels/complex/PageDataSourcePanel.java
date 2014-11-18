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
package com.ibm.xsp.extlib.designer.tooling.panels.complex;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.views.DataNodeBinding;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomText;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.scripting.api.IScriptData.PublishedObject;
import com.ibm.designer.domino.scripting.api.published.DesignerPublishedObject;
import com.ibm.designer.domino.scripting.api.published.PublishedUtil;
import com.ibm.designer.domino.xsp.api.panels.complex.DynamicPanel;
import com.ibm.designer.domino.xsp.registry.DefinitionDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.prj.resources.commons.DesignerProjectException;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This panel will be displayed within the viewData tag's Data panel. It will be displayed
 * when the viewData tag uses a data source that is defined outside of the viewData tag itself,
 * e.g. if the data source is setup at the page level..
 * This panel will display the attributes of the data source but will not allow the user
 * to edit those attributes as the data source may be in use by other controls also.
 * 
 * @author doconnor
 * 
 *
 */
public class PageDataSourcePanel extends DynamicPanel {
    private DataNode dataNode = null;
    /**
     * @param parent
     * @param style
     */
    public PageDataSourcePanel(Composite parent) {
        super(parent);
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout1#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createContents(Composite parent) {
        /*
         * Set up
         */
        initDataNode(parent);
        parent.addDisposeListener(new DisposeListener(){

            public void widgetDisposed(DisposeEvent e) {
                dispose();
            }
        });
        parent.setBackground(SWTUtils.getBackgroundColor(parent));
        //end setup
        
        //We are ONLY here because the value attribute on the node has been set
        //The control is using a data source defined at the page level (or at some level above
        //our current level, e.g. in a parent panel control).. We need to find that data source
        //and display some information about it!
        
        String dbName = null;
        String viewName = null;
        String dsName = "Page Data Source"; // $NLX-PageDataSourcePanel.PageDataSource-1$
        if(dataNode != null){
            //Get the 'value' attribute
            IMember value = dataNode.getMember("value"); // $NON-NLS-1$
            if(value instanceof IAttribute){ //This is almost certainly a given, but check for safety
                try {
                    String attrVal = dataNode.getValue((IAttribute)value);
                    if(StringUtil.isNotEmpty(attrVal)){
                        //The value attribute will be of the form value="#{viewDataSourceID}"
                        if(attrVal.startsWith("#{") && attrVal.endsWith("}")){
                            //Strip off the binding info
                            attrVal = attrVal.substring(2, attrVal.length() - 1);
                            Map<String, PublishedObject>published = new HashMap<String, PublishedObject>();
                            PublishedUtil.getAllPublishedObjects(published, getExtraData().getNode(), getExtraData().getDesignerProject());
                            if(!published.isEmpty()){
                                //The published object map should contain a data source by the name that is set in the value attribute
                                PublishedObject po = published.get(attrVal);
                                if(po != null){
                                    //Get the data source tag instance
                                    Node n = (Node)po.getProperty(DesignerPublishedObject.PROPERTY_NODE);
                                    if(n instanceof Element){
                                        //In the case of a domino view get the view name attribute and the db name
                                        viewName = ((Element)n).getAttribute("viewName"); // $NON-NLS-1$
                                        dbName = ((Element)n).getAttribute("databaseName"); // $NON-NLS-1$
                                        if(StringUtil.isEmpty(dbName)){
                                            dbName = "(current)"; // $NLX-PageDataSourcePanel.current-1$
                                        }
                                    }
                                    FacesRegistry registry = getExtraData().getDesignerProject().getFacesRegistry();
                                    FacesDefinition def = registry.findDef(n.getNamespaceURI(), n.getLocalName());
                                    if(def != null){
                                        //Get the user friendly display name
                                        DefinitionDesignerExtension ext = DesignerExtensionUtil.getDefinitionExtension(def);
                                        if(ext != null){
                                            dsName = ext.getDisplayName();
                                            dsName = StringUtil.format("Page Data Source: {0}", dsName); // $NLX-PageDataSourcePanel.PageDataSource0-1$
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (NodeException e) {
                    if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                        ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "createContents", e, "Failed to retrieve the \"value\" attribute from the current node!");  // $NON-NLS-1$ $NLE-PageDataSourcePanel.Failedtoretrievethevalueattribute-2$
                    }
                } catch (DesignerProjectException e) {
                    if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                        ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "createContents", e, "Failed to find the published objects (data sources) on the current page!");  // $NON-NLS-1$ $NLE-PageDataSourcePanel.Failedtofindthepublishedobjectsda-2$
                    }
                }
            }
        }
        //Create the controls!
        Group group = new Group(parent, SWT.NONE);
        group.setText(dsName);
        group.setLayout(SWTLayoutUtils.createLayoutDefaultSpacing(2));
        GridData data = SWTLayoutUtils.createGDFill();
        data.horizontalSpan = 2;
        group.setLayoutData(data);
        setCurrentParent(group);
        createLabel("View name:", null); // $NLX-PageDataSourcePanel.Viewname-1$
        CustomText viewNameCtrl = new CustomText(group, SWT.BORDER | SWT.READ_ONLY, "view.id"); // $NON-NLS-1$
        viewNameCtrl.setText(StringUtil.getNonNullString(viewName));
        viewNameCtrl.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        createLabel("Database name:", null); // $NLX-PageDataSourcePanel.Databasename-1$
        CustomText dbNameCtrl = new CustomText(group, SWT.BORDER | SWT.READ_ONLY, "db.id"); // $NON-NLS-1$
        dbNameCtrl.setText(StringUtil.getNonNullString(dbName));
        dbNameCtrl.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        setCurrentParent(parent);
        SWTUtils.setBackgroundColor(parent);
        
    }
    
    private void initDataNode(Composite parent){
        /*
         * Set up the data node that will be used by the panel to get attribute values
         * from the current tag.
         */
        DataNodeBinding binding = DCUtils.findDataNodeBinding(parent, true);
        if(binding != null){
            dataNode = binding.getDataNode();
        }
    }
}