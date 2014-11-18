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
* Date: 24 May 2013
* InputReadOnlyRendererTest.java
* Copied from the 9.0.1 lwp04.wct-des FE on 2013-06-18
*/
package com.ibm.xsp.test.framework.registry.annotate;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.Renderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class InputReadOnlyRendererTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        // note InputReadOnlyPropertyTest tests the actual rendering when readonly
        return "that input controls should have a readonly renderer in the faces-config files";
    }
    public void testReadOnlyRendererExists() throws Exception {
        
        String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        FacesContext context = TestProject.createFacesContext(this);
        // a control tree is required because the renderKitId is read from the viewRoot,
        // so without it can't test context.getRenderKit().getRenderer
        TestProject.loadEmptyPage(this, context);
        RenderKit runtimeRenderKit = context.getRenderKit();
        
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) { 
            if( !def.isTag() ){
                // only check non-abstract controls
                continue;
            }
            if( !UIInput.class.isAssignableFrom(def.getJavaClass()) ){
                // only check input controls
                continue;
            }
            String componentFamily = def.getComponentFamily();
            String rendererType = def.getRendererType();
            Renderer renderer = runtimeRenderKit.getRenderer(componentFamily, rendererType);
            if( null == renderer ){
                // ComponentRendererTest will be failing if the normal renderer is absent
                continue;
            }
            boolean expectedReadOnlyRendererPresent = (true && isExpectReadOnlyRendererPresent(def));
            
            String readOnlyRendererType = rendererType+".ReadOnly"; // suffix is ReadOnlyRenderKit.READ_ONLY
            Renderer readOnlyRenderer = runtimeRenderKit.getRenderer(componentFamily, readOnlyRendererType);
            boolean actualReadOnlyRendererPresent = (null != readOnlyRenderer);
            
            if( expectedReadOnlyRendererPresent != actualReadOnlyRendererPresent ){
                if( ! actualReadOnlyRendererPresent ){
                    String msg = XspTestUtil.loc(def)+" Expected '.ReadOnly' renderer not found. (Runtime will fallback to normal renderer).";
                    fails +=  msg + "\n";
                    System.err.println("InputReadOnlyRendererTest.testReadOnlyRendererExists() fail: "+msg
                            + detail(componentFamily, readOnlyRendererType, readOnlyRenderer));
                }else{
                    String msg = XspTestUtil.loc(def)+" Unexpected '.ReadOnly' renderer found.";
                    fails +=  msg + "\n";
                    System.err.println("InputReadOnlyRendererTest.testReadOnlyRendererExists() fail: "+msg
                            + detail(componentFamily, readOnlyRendererType, readOnlyRenderer));
                }
            }
//            System.out.println("InputReadOnlyRendererTest.testReadOnlyRendererExists() "+ XspTestUtil.loc(def)+" is OK.");
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testReadOnlyRendererExists"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    private String detail(String componentFamily, String readOnlyRendererType,
            Renderer readOnlyRenderer) {
        String className = (null == readOnlyRenderer)? null : readOnlyRenderer.getClass().getName();
        return " family="+componentFamily+" readOnlyRendererType="+readOnlyRendererType+" readOnlyRendererClass="+className;
    }
    /**
     * Available to override in subclasses, e.g. the dojo controls expect not to have readonly renderers.
     * @param def
     * @return
     */
    protected boolean isExpectReadOnlyRendererPresent(FacesComponentDefinition def) {
        return true;
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
}
