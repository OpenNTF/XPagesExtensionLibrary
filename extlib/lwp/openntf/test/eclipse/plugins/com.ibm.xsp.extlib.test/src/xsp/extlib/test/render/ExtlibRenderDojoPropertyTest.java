/*
 * © Copyright IBM Corp. 2012, 2014
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 9 Nov 2012
* ExtlibRenderDojoPropertyTest.java
*/
package xsp.extlib.test.render;

import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.BaseRenderDojoPropertyTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibRenderDojoPropertyTest extends BaseRenderDojoPropertyTest {

    private Object[][] propertyGivesExceptionSkips = new Object[][]{
            // xe:tooltip for="testString" gives exception:
            //com.ibm.xsp.FacesExceptionEx: Unknown 'for' component testString
            //    at com.ibm.xsp.extlib.renderkit.html_extended.tooltip.TooltipRenderer.initDojoAttributes(TooltipRenderer.java:87)
            //    at com.ibm.xsp.extlib.renderkit.html_extended.tooltip.TooltipRenderer.encodeBegin(TooltipRenderer.java:71)
            //    at com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer.encodeBegin(ReadOnlyAdapterRenderer.java:146)
            //    at javax.faces.component.UIComponentBase.encodeBegin(UIComponentBase.java:956)
            //    at com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl.encodeBegin(UIDynamicControl.java:218)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:842)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:853)
            //    at com.ibm.xsp.test.framework.render.ResponseBuffer.encode(ResponseBuffer.java:48)
            //    at com.ibm.xsp.test.framework.render.RenderDojoPropertyTest.testRenderDojoPropertyTest(RenderDojoPropertyTest.java:167)
            {"xe:tooltip", /*for="testString"*/"for"},
            
            // xe:djxDataGrid storeComponentId="testString" gives exception:
            //com.ibm.xsp.FacesExceptionEx: Cannot find Rest Service component testString
            //    at com.ibm.xsp.extlib.component.rest.UIBaseRestService.findRestServiceStoreId(UIBaseRestService.java:180)
            //    at com.ibm.xsp.extlib.renderkit.dojo.grid.DojoGridRenderer.initDojoAttributes(DojoGridRenderer.java:77)
            //    at com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetBaseRenderer.writeDojoAttributes(DojoWidgetBaseRenderer.java:143)
            //    at com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetBaseRenderer.writeTag(DojoWidgetBaseRenderer.java:92)
            //    at com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetBaseRenderer.encodeBegin(DojoWidgetBaseRenderer.java:56)
            //    at com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer.encodeBegin(ReadOnlyAdapterRenderer.java:146)
            //    at javax.faces.component.UIComponentBase.encodeBegin(UIComponentBase.java:956)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:842)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:853)
            //    at com.ibm.xsp.test.framework.render.ResponseBuffer.encode(ResponseBuffer.java:48)
            //    at com.ibm.xsp.test.framework.render.RenderDojoPropertyTest.testRenderDojoPropertyTest(RenderDojoPropertyTest.java:167)
            {"xe:djxDataGrid", /*storeComponentId="testString"*/"storeComponentId"},
            
            // xe:djRadioButton skipContainers="10" gives exception:
            //java.lang.NullPointerException
            //    at com.ibm.xsp.util.FacesUtil.getNamingContainer(FacesUtil.java:497)
            //    at com.ibm.xsp.extlib.component.dojo.form.UIDojoRadioButton.getClientGroupName(UIDojoRadioButton.java:105)
            //    at com.ibm.xsp.extlib.renderkit.dojo.form.DojoRadioButtonRenderer.getNameAttribute(DojoRadioButtonRenderer.java:65)
            //    at com.ibm.xsp.extlib.renderkit.dojo.form.DojoFormWidgetRenderer.encodeBegin(DojoFormWidgetRenderer.java:172)
            //    at com.ibm.xsp.renderkit.ReadOnlyAdapterRenderer.encodeBegin(ReadOnlyAdapterRenderer.java:146)
            //    at javax.faces.component.UIComponentBase.encodeBegin(UIComponentBase.java:956)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:842)
            //    at com.ibm.xsp.util.FacesUtil.renderComponent(FacesUtil.java:853)
            //    at com.ibm.xsp.test.framework.render.ResponseBuffer.encode(ResponseBuffer.java:48)
            //    at com.ibm.xsp.test.framework.render.RenderDojoPropertyTest.testRenderDojoPropertyTest(RenderDojoPropertyTest.java:167)
            {"xe:djRadioButton", /*skipContainers="10"*/"skipContainers"},
    };

    @Override
    protected String nonRandom(String stringInHtmlPage) {
        if( null != stringInHtmlPage && stringInHtmlPage.endsWith("!") && stringInHtmlPage.contains("$$viewid=!")){
            // /pages/pregenerated/empty.xsp?$$ajaxid=view:_id1:_id101&$$ajaxinner=content&$$viewid=!dbetypkypz!
            // to end with: $$viewid=!aaaaaaaaaa!
            int startRandomIndex = stringInHtmlPage.indexOf("$$viewid=!") +"$$viewid=!".length();
            String before = stringInHtmlPage.substring(0, startRandomIndex);
            String nonRandom = "aaaaaaaaaa";
            return before+nonRandom+"!";
        }
        return super.nonRandom(stringInHtmlPage);
    }

    @Override
    protected Object[][] getPropertyTestGivesExceptionSkips() {
        Object[][] existing = super.getPropertyTestGivesExceptionSkips();
        Object[][] allSkips = XspTestUtil.concat(existing, propertyGivesExceptionSkips );
        return allSkips;
    }

}
