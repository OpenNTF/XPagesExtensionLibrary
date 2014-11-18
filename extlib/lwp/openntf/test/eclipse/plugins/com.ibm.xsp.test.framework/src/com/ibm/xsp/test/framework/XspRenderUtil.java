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
* Date: 15 Jun 2011
* XspRenderUtil.java
*/
package com.ibm.xsp.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIFormEx;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.test.framework.render.TestControlInitializer;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class XspRenderUtil {

    public static String findAttribute(String page, String attributeName) {
        String attributeValue = null;
        if( -1 != page.indexOf(" "+attributeName+"=") ){
            // Note, test with a space to see the difference
            // between aria-required="true" and required="true"
            String searchFor = " "+attributeName+"=\"";
            int startIndex = page.indexOf(searchFor);
            if( -1 == startIndex ) throw new RuntimeException();
            startIndex = startIndex+searchFor.length();
            int endIndex = page.indexOf('"', startIndex);
            attributeValue = page.substring(startIndex, endIndex);
        }
        return attributeValue;
    }

    /**
     * @param root
     * @return
     */
    public static UIPassThroughTag createContainerParagraph(UIViewRootEx root) {
        UIComponent rootChild = TypedUtil.getChildren(root).get(0);
        UIComponent scriptCollector = null;
        UIFormEx form;
        if( rootChild instanceof UIFormEx ){
            form = (UIFormEx) rootChild;
        }else{
            // rootChild is a UIScriptCollector
            scriptCollector = rootChild;
            form = (UIFormEx) TypedUtil.getChildren(scriptCollector).get(0);
        }
        UIPassThroughTag p = new UIPassThroughTag();
        p.setTag("p");
        TypedUtil.getChildren(form).add(p);
        return p;
    }

    /**
     * @param root
     * @param p
     * @param instance
     * @throws Exception
     */
    public static void resetContainerChild(UIViewRootEx root, UIPassThroughTag p,
            UIComponent instance) throws Exception{
        
        // clear out any changes caused by the previous rendering/encode
        root.setDojoParseOnLoad(false);
        root.setDojoTheme(false);
        root.setLoadXspClientDojoUI(false);
        root.clearEncodeResources();
        //root._lastUniqueId = 100;
        Class<?> viewRootClass = UIViewRootEx.class;
        Field lastIdField = viewRootClass.getDeclaredField("_lastUniqueId");
        lastIdField.setAccessible(true); // change private to public
        try{
            lastIdField.set(root, 100);
        }finally{
            lastIdField.setAccessible(false);
        }
        
        UIComponent rootChild = TypedUtil.getChildren(root).get(0);
        if( !(rootChild instanceof UIFormEx) ){
            // rootChild is a UIScriptCollector
            UIComponent scriptCollector = rootChild;
            
            // scriptCollector.reset();
            Method resetMethod = scriptCollector.getClass().getMethod("reset");
            resetMethod.invoke(scriptCollector);
        }
        
        // remove any previous child of the container
        List<UIComponent> kids = TypedUtil.getChildren(p);
        kids.clear();
        kids.add(instance);
        
        // ensure the child has an auto-generated ID.
        if (instance.getId() == null){ 
            instance.setId(root.createUniqueId());
        }
        
        FacesUtil.checkComponentIds(root, p);
    }
    
    public static void initControl(AbstractXspTest test, UIComponent control, FacesContext context){
        List<TestControlInitializer> list = TestProject.getControlInitializerList(test);
        for (TestControlInitializer controlInitializer : list) {
            controlInitializer.initControl(test, control, context);
        }
    }

}
