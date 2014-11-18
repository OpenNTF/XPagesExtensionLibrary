/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.renderkit.html_extended.oneuiv302.data;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.renderkit.html_extended.oneui.data.OneUIPagerDetailRenderer;

public class OneUIv302PagerDetailRenderer extends OneUIPagerDetailRenderer {
	
	@Override
	protected void writeMain(FacesContext context, ResponseWriter w, AbstractPager pager, FacesDataIterator dataIterator) throws IOException {
        writePagerContent(context, w, pager, dataIterator);
    }

}
