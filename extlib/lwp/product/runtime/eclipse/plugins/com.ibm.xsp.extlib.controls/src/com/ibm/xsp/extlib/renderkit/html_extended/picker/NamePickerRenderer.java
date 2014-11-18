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

package com.ibm.xsp.extlib.renderkit.html_extended.picker;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.picker.AbstractPicker;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.resources.ExtLibResources;


/**
 * Name Picker renderer.
 */
public class NamePickerRenderer extends AbstractPickerRenderer {

    @Override
    protected String getDefaultDojoType() {
        return "extlib.dijit.PickerName"; // $NON-NLS-1$
    }
    
    @Override
    protected String getImageLink() {
        return ExtLibResources.iconNamePicker;
    }
    
    @Override
    protected String getDialogTitleSingleSelect() {
        return "Select A Name";  // $NLS-NamePickerRenderer.SelectAName-1$
    }
    @Override
    protected String getDialogTitleMultipleSelect() {
        return "Select One Or More Names";  // $NLS-NamePickerRenderer.SelectOneOrMoreNames-1$
    }

    @Override
    protected void encodeExtraResources(FacesContext context, AbstractPicker picker, IPickerData data, UIViewRootEx rootEx, String dojoType) {
        if(StringUtil.equals(dojoType, "extlib.dijit.PickerName")) { // $NON-NLS-1$
            ExtLibResources.addEncodeResource(rootEx, ExtLibResources.extlibPickerName);
        }
        super.encodeExtraResources(context, picker, data, rootEx, dojoType);
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.renderkit.html_extended.picker.AbstractPickerRenderer#initDojoAttributes(javax.faces.context.FacesContext, com.ibm.xsp.extlib.component.picker.AbstractPicker, javax.faces.component.UIComponent, com.ibm.xsp.extlib.component.picker.data.IPickerData, java.lang.String, com.ibm.commons.util.io.json.JsonJavaObject)
     */
    //@Override
    //protected void initDojoAttributes(FacesContext context, AbstractPicker picker, UIComponent _for, IPickerData data, String dojoType,
    //        JsonJavaObject json) throws IOException {
    //    super.initDojoAttributes(context, picker, _for, data, dojoType, json);
        
        //int maxRowCount=((UINamePicker)picker).getMaxRowCount();
        //json.putInt("maxRowCount",maxRowCount );
        
    //}
}