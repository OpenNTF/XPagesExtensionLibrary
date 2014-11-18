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

import javax.faces.context.FacesContext;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;


public class DojoBorderPaneRenderer extends DojoContentPaneRenderer {
    
    @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDojoBorderPane) {
            UIDojoBorderPane c = (UIDojoBorderPane)dojoComponent;

            DojoRendererUtil.addDojoHtmlAttributes(attrs,"minSize",c.getMinSize()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"maxSize",c.getMaxSize(),Integer.MAX_VALUE); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"splitter",c.isSplitter()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"region",c.getRegion()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"layoutPriority",c.getLayoutPriority()); // $NON-NLS-1$
        }
    }

}