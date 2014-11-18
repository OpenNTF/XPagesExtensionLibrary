/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.renderkit.dojo.layout;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;


public class DojoTabPaneRenderer extends DojoContentPaneRenderer {
    
    @Override
    protected String getDefaultDojoType(FacesContext context, FacesDojoComponent component) {
        return UIDojoTabPane.DEFAULT_DOJO_TYPE;
    }
    
    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context, FacesDojoComponent component) {
        return ExtLibResources.extlibTabPane;
    }

    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoTabPane) {
            UIDojoTabPane c = (UIDojoTabPane)dojoComponent;

            String tabKey = c.getTabUniqueKey();
            if(StringUtil.isEmpty(tabKey)) {
                tabKey = c.getId();
            }
            
            // Find if the pane is selected
            boolean selected = false;
            UIComponent p = c.getParent();
            if(p instanceof UIDojoTabContainer) {
                String sel = ((UIDojoTabContainer)p).getSelectedTab();
                selected = StringUtil.equals(sel, tabKey);
            }
            if(selected) {
                DojoRendererUtil.addDojoHtmlAttributes(attrs,"selected",true); // $NON-NLS-1$
            }

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"closable",c.isClosable()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"title",c.getTitle()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"tabUniqueKey",tabKey); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"onClose",c.getOnClose()); // $NON-NLS-1$
        }
    }
}