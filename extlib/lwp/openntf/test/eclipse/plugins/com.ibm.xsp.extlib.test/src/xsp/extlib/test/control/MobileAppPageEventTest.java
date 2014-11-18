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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 10 Jul 2013
* MobileAppPageEventTest.java
*/
package xsp.extlib.test.control;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIViewRoot;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.mobile.UIApplication;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspControlsUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.EventPropsHaveSubCategoryTest;
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class MobileAppPageEventTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that the event props on xe:appPage and xe:singlePageApp are output to the HTML";
    }
    public void testAppPageEvents() throws Exception {
        
        
        // using testMobileAppPage.xsp for the default structure, 
        // and then programmatically adding event values to the control tree
        FacesContextEx context = (FacesContextEx) TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRoot root = TestProject.loadView(this, context, "/pages/testMobileAppPage.xsp");
        
        // find the controls
        UIApplication appPageContainer = (UIApplication) XspControlsUtil.findComponent(root, "singlePageApp1");
        UIMobilePage appPage = (UIMobilePage) XspControlsUtil.findComponent(appPageContainer, "appPage1");
        
        List<String[]> propNameToExpectedOutputValue = new ArrayList<String[]>();
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new EventPropsHaveSubCategoryTest.EventPropertyAnnotater());
        FacesComponentDefinition appPageContainerDef = XspRegistryTestUtil.getFirstComponentDefinition(reg, UIApplication.class);
        for (String propName : appPageContainerDef.getPropertyNames()) {
            FacesProperty prop = appPageContainerDef.getProperty(propName);
            boolean isEventExt = null != prop.getExtension("event");
            if( ! isEventExt ){
                continue;
            }
            String propValue = "alert('"+propName+" occurred on singlePageApp1');";
            // add the event value to the singlePageApp control
            TypedUtil.getAttributes(appPageContainer).put(propName, propValue);
            // will check for the event value in the rendered page output
            propNameToExpectedOutputValue.add( new String[]{propName, propValue});
        }
        FacesComponentDefinition appPageDef = XspRegistryTestUtil.getFirstComponentDefinition(reg, UIMobilePage.class);
        for (String propName : appPageDef.getPropertyNames()) {
            FacesProperty prop = appPageDef.getProperty(propName);
            boolean isEventExt = null != prop.getExtension("event");
            if( ! isEventExt ){
                continue;
            }
            String propValue = "alert('"+propName+" occurred on appPage1');";
            // add the event value to the appPage control
            TypedUtil.getAttributes(appPage).put(propName, propValue);
            // will check for the event value in the rendered page output
            propNameToExpectedOutputValue.add( new String[]{propName, propValue});
        }
        
        String page = ResponseBuffer.encode(root, context);
//        System.out.println("MobileAppPageEventTest.testAppPageEvents()\n"+page);
        
        String fails = "";
        for (String[] nameAndValue : propNameToExpectedOutputValue) {
            String propName = nameAndValue[0];
            String propValue = nameAndValue[1];
            
            String expected = " "+propName+"=\""+propValue+"\"";
            if( -1 == page.indexOf(expected) ){
                // fail
                FacesComponentDefinition relevantDef;
                if( appPageContainerDef.isProperty(propName) ){
                    relevantDef = appPageContainerDef;
                }else{
                    relevantDef = appPageDef;
                }
                fails += XspTestUtil.loc(relevantDef)+" "+propName
                        +" Mobile event not in HTML, expected: " +expected+ "\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testAppPageEvents"));
        if( fails.length() > 0){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

}
