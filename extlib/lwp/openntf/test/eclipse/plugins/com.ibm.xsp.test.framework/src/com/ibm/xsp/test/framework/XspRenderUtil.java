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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

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
    public static UIComponent createContainerParagraph(UIViewRoot root) {
        List<UIComponent> rootKids = TypedUtil.getChildren(root);
        
        UIComponent insertParent;
        if( rootKids.isEmpty() ){
            insertParent = root;
        }else{
            UIComponent rootChild = rootKids.get(0);
            if( rootChild instanceof UIForm ){
                insertParent = (UIForm) rootChild;
            }else{
                // rootChild is a UIScriptCollector
                UIComponent scriptCollector = rootChild;
                insertParent = (UIForm) TypedUtil.getChildren(scriptCollector).get(0);
            }
        }
        UIComponent p;
		try {
	        //UIPassThroughTag p = new UIPassThroughTag();
	        //p.setTag("p");
			String className = "com.ibm.xsp.component.UIPassThroughTag";
			Class<?> tagClass = Class.forName(className);
			p = (UIComponent) tagClass.newInstance();
			TypedUtil.getAttributes(p).put("tag", "p");
		}catch(Exception e){
			// should only happen in ..xsp.core junit tests.
			p = new UITestParagraph();
		}
        
        TypedUtil.getChildren(insertParent).add(p);
        return p;
    }
    private static final class UITestParagraph extends UIComponentBase{
		@Override
		public String getFamily() {
			return null;
		}

		@Override
		public void encodeBegin(FacesContext context) throws IOException {
			context.getResponseWriter().startElement("p", this);
			super.encodeBegin(context);
		}

		@Override
		public void encodeEnd(FacesContext context) throws IOException {
			super.encodeEnd(context);
			context.getResponseWriter().endElement("p");
		}
    }
    /**
     * @param root
     * @param p
     * @param instance
     * @throws Exception
     */
    public static void resetContainerChild(UIViewRoot root, UIComponent p,
            UIComponent instance) throws Exception{
        
        // clear out any changes caused by the previous rendering/encode
        Map<String, Object> rootAttrs = TypedUtil.getAttributes(root);
        rootAttrs.put("dojoParseOnLoad", false);
        rootAttrs.put("dojoTheme", false);
        rootAttrs.put("loadXspClientDojoUI",false);
        if( ! UIViewRoot.class.equals(root.getClass()) ){
            // UIViewRootEx and the other subclass have this clear.. method
            // root.clearEncodeResources();
            Method mtd = root.getClass().getMethod("clearEncodeResources", new Class[0]);
            if( null != mtd ){
                mtd.invoke(root, new Object[0]);
            }
        //root._lastUniqueId = 100;
            Class<?> viewRootClass = Class.forName("com.ibm.xsp.component.UIViewRootEx");
        Field lastIdField = viewRootClass.getDeclaredField("_lastUniqueId");
        lastIdField.setAccessible(true); // change private to public
        try{
            lastIdField.set(root, 100);
        }finally{
            lastIdField.setAccessible(false);
            }
        }
        
        UIComponent rootChild = TypedUtil.getChildren(root).get(0);
        if( !(rootChild instanceof UIForm) && rootChild.getClass().getName().contains("ScriptCollector") ){
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
