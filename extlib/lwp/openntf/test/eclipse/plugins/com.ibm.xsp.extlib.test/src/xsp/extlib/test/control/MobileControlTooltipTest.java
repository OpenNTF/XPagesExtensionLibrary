/*
 * © Copyright IBM Corp. 2012
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
* Date: 23 Nov 2012
* MobileControlTooltipTest.java
*/
package xsp.extlib.test.control;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.mobile.UIApplication;
import com.ibm.xsp.extlib.component.mobile.UIMobilePage;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.RenderBooleanPropertyTest;
import com.ibm.xsp.test.framework.render.RenderBooleanPropertyTest.TestedElsewhereContinueException;
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class MobileControlTooltipTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // to verify PHAN922SRC is fixed, and any new mobile controls
        // also do not have a tooltip property.
        return "that new mobile controls do not have a tooltip property";
    }
    public void testMobileControlTooltip() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistry(
                this);
        
        // to render the non-initial controls to verify they don't output tooltip
        FacesContextEx context = (FacesContextEx) TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRootEx root = TestProject.loadEmptyPage(this, context);
        UIPassThroughTag p = XspRenderUtil.createContainerParagraph(root);
        
        String fails = "";
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.getJavaClass().getName().contains(".mobile.") ){
                continue;
            }
            
            boolean inInitialVersion = (null == def.getSince());
            if( inInitialVersion ){
                if( null == def.getProperty("tooltip") ){
                    // Neither of UIMobilePage and UIApplication had a tooltip in 8.5.3.UP1
                    // [They inherit from UIComponent in the xsp-config instead of UIDojoWidgetBase]
                    Class<?> compClass = def.getJavaClass();
                    if( !(UIMobilePage.class.equals(compClass)|| UIApplication.class.equals(compClass)) ){
                        fails += def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "
                                +"Accidentally removed tooltip property in initial mobile control\n";
                    }
                }
                continue;
            }
            // new control
            if( null != def.getProperty("tooltip") ){
                fails += def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "
                        +"New mobile control should not have a tooltip property (no hover on touch devices)\n";
            }
            
            // create a control instance
            UIComponent instance;
            try{
                instance = newInstance(def);
            }catch(TestedElsewhereContinueException e){
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
                continue;
            }
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            
            String tooltipValue = def.getTagName()+"_tooltip";
            TypedUtil.getAttributes(instance).put("tooltip", tooltipValue);
            
            String page;
            try{
                page = encode(p, context);
            }catch(TestedElsewhereContinueException e){
                // note, ComponentRendererTest will fail for this, so not log here
                continue;
            }
            
            if( page.contains(tooltipValue) ){
                fails += def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "
                        +"New mobile control should not render a tooltip property (no hover on touch devices)\n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testMobileControlTooltip"));
        if( fails.length() > 0){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private UIComponent newInstance(FacesComponentDefinition def) throws TestedElsewhereContinueException{
        return RenderBooleanPropertyTest.newInstance(def);
    }
    private String encode(UIComponent p, FacesContext context) throws TestedElsewhereContinueException{
        return RenderBooleanPropertyTest.encode(p, context);
    }
}
