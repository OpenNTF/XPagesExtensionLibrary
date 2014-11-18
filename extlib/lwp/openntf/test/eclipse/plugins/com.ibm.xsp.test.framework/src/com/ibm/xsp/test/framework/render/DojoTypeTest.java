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
* Date: 12 Apr 2011
* DojoTypeTest.java
*/

package com.ibm.xsp.test.framework.render;

import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.library.LibraryServiceLoader;
import com.ibm.xsp.page.translator.JavaUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.ibm.xsp.util.TypedUtil;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DojoTypeTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that the controls in the registry with a dojoType property support it correctly";
    }
    public void testDojoType() throws Exception {
        
        // create an empty view
        FacesContext context = TestProject.createFacesContext(this);
        ResponseBuffer.initContext(context);
        UIViewRootEx root = TestProject.loadEmptyPage(this, context);
        UIPassThroughTag p = XspRenderUtil.createContainerParagraph(root);
        
        String fails = "";
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        boolean isShouldLoadXspClientDojoUI = isShouldLoadXspClientDojoUI();
        Object[][] renderDefaultDojoTypes = getRenderDefaultDojoTypes();
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() || !def.isProperty("dojoType") ){
                continue;
            }
            
            final String expectedRenderDefaultDojoType = getExpectedDojoType(renderDefaultDojoTypes, def);
            
            // create a control instance
            UIComponent instance;
            try{
                instance = (UIComponent) def.getJavaClass().newInstance();
            }catch(Exception e){
                fails += def.getFile().getFilePath()+" "+ XspTestUtil.getShortClass(def.getJavaClass())
                    +" Exception creating instance "+e+"\n";
                continue;
            }
            if ( !(instance instanceof FacesDojoComponent) ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Has dojoType property but not a "
                        + XspTestUtil.getShortClass(FacesDojoComponent.class)
                        + ".\n";
                
                // inspect the getter
                try{ 
                    def.getJavaClass().getMethod("getDojoType");
                }catch(Exception e ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                            +" No getDojoType() method.\n";
                }
            }
            String existingGetterDojoType = (String) instance.getAttributes().get("dojoType");
            if( null != existingGetterDojoType ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        +" Has getDojoType() default: " +
                        existingGetterDojoType+"\n";
            }
            
            // render the un-modified blank control instance 
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            // before encode, pre-process ids:
            String page;
            try{
                page = ResponseBuffer.encode(p, context);
            }catch(Exception e){
                e.printStackTrace();
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Problem rendering page: "+e+"\n";
                ResponseBuffer.clear(context);
                continue;
            }
            
            
            String existingRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            if( null == expectedRenderDefaultDojoType ){
            	if( null != existingRenderDojoType ){
            		fails += def.getFile().getFilePath()
                    		+" "+XspTestUtil.getShortClass(def.getJavaClass())
                    		+ " Outputs dojoType= when not explicitly set: "
                    		+ existingRenderDojoType + "\n";
            	}
            }else{
            	if( ! StringUtil.equals(expectedRenderDefaultDojoType, existingRenderDojoType) ){
            		fails += def.getFile().getFilePath()
                		+" "+XspTestUtil.getShortClass(def.getJavaClass())
                		+ " Wrong dojoType= when not explicitly set, was: "
                		+ existingRenderDojoType + ", expected: " 
                		+expectedRenderDefaultDojoType+"\n";
            	}
            }
        	if( null != existingGetterDojoType ){
        		if( !StringUtil.equals(existingGetterDojoType, existingRenderDojoType) ){
        			fails += def.getFile().getFilePath()+" "
                			+ XspTestUtil.getShortClass(def.getJavaClass())
                			+ " dojoType= not same as getDojoType() when not explicitly set: " 
                			+ "dojoType= "
                			+ existingRenderDojoType
                			+ " getDojoType():"
                			+ existingGetterDojoType + " \n";
        		}
        	}
            
            boolean outputtedDojoType = null != existingRenderDojoType;
            if( !StringUtil.equals(outputtedDojoType, root.isDojoParseOnLoad()) ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " rootEx.isDojoParseOnLoad not as expected when render un-modified blank control: "
                        + root.isDojoParseOnLoad() + "\n";
            }
            if( !StringUtil.equals(outputtedDojoType, root.isDojoTheme()) ){
                fails += def.getFile().getFilePath()+" "
                    +XspTestUtil.getShortClass(def.getJavaClass())
                    + " rootEx.isDojoTheme not as expected when render un-modified blank control: "
                    + root.isDojoTheme() + "\n";
            }
            if( isShouldLoadXspClientDojoUI ){
            	if( !StringUtil.equals(outputtedDojoType, root.isLoadXspClientDojoUI()) ){
            		fails += def.getFile().getFilePath()+" "
            			+XspTestUtil.getShortClass(def.getJavaClass())
            			+ " rootEx.isLoadXspClientDojoUI not as expected when render un-modified blank control: "
            			+ root.isLoadXspClientDojoUI() + "\n";
            	}
            }else{ // !isCanLoadXspClientDojoUI
                if( root.isLoadXspClientDojoUI() ){
            		fails += def.getFile().getFilePath()+" "
            				+XspTestUtil.getShortClass(def.getJavaClass())
            				+ " rootEx.isLoadXspClientDojoUI is true when render un-modified blank control,"
            				+" expected false since not testing an XPages runtime core library.\n";
                }
            }
            
            String existingRenderId = XspRenderUtil.findAttribute(page, "id");
            if( outputtedDojoType ){
                if( null == existingRenderId ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                            + " Did not output an id= even though a dojoType was output"
                            + "\n";
                }
            }else{ // !outputtedDojoType
                if( null != existingRenderId ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                                    + " Output an id= when !HtmlUtil.isUserId(String)"
                                    + " and dojoType not present" 
                                    + "\n";
                }
            }
            
            // set a dojoType and render that
            String testDojoType = "com.example.test.SomeDijit";
            TypedUtil.getAttributes(instance).put("dojoType", testDojoType);
            // before encode, pre-process ids:
            if (instance.getId() == null) instance.setId(root.createUniqueId());
            page = ResponseBuffer.encode(p, context);
            String actualRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            if( ! StringUtil.equals(testDojoType, actualRenderDojoType) ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                            + " Output wrong dojoType= when setDojoType called: "
                            + actualRenderDojoType + ", expected "
                            + testDojoType+"\n";
            }
            
            outputtedDojoType = null != actualRenderDojoType;
            if( ! StringUtil.equals(true, root.isDojoParseOnLoad()) ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " rootEx.isDojoParseOnLoad not as expected when render dojoType control: "
                        + root.isDojoParseOnLoad() + "\n";
            }
            boolean expectDojoTheme = null != existingRenderDojoType; // if the control uses dojo by default
            if( !StringUtil.equals(expectDojoTheme, root.isDojoTheme()) ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " rootEx.isDojoTheme not as expected when render dojoType control: "
                        + root.isDojoTheme() + "\n";
            }
            if( isShouldLoadXspClientDojoUI ){
                if( ! StringUtil.equals(true, root.isLoadXspClientDojoUI()) ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                            + " rootEx.isLoadXspClientDojoUI not as expected when render dojoType control: "
                            + root.isLoadXspClientDojoUI() + "\n";
                }
            }else{ // !isCanLoadXspClientDojoUI
                if( root.isLoadXspClientDojoUI() ){
                    fails += def.getFile().getFilePath()+" "
                            +XspTestUtil.getShortClass(def.getJavaClass())
                            + " rootEx.isLoadXspClientDojoUI is true when render dojoTypeControl,"
            				+" expected false since not testing an XPages runtime core library.\n";
                }
            }
            
            String actualRenderId = XspRenderUtil.findAttribute(page, "id");
            if( null == actualRenderId ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Did not force an id= when a dojoType was set"
                        + "\n";
            }
            // reset the instance
            instance = (UIComponent) def.getJavaClass().newInstance();
            
            // set some dojoAttributes and render those
            Method method = null;
            try{
                method = instance.getClass().getMethod("addDojoAttribute", DojoAttribute.class);
            }catch(Exception e){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Method not found: addDojoAttribute\n";
            }
            if( null != method ){
                DojoAttribute[] dojoAttrs = new DojoAttribute[]{
                        new DojoAttribute("aaaName", "aaaValue"),
                        new DojoAttribute("bbbName", "bbbValue"),
                };
                for (DojoAttribute dojoAttr : dojoAttrs) {
                    method.invoke(instance, dojoAttr);
                }
                
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
                page = ResponseBuffer.encode(p, context);
                
                // when dojoType not present, dojo attributes shouldn't be present either 
                for (DojoAttribute dojoAttr : dojoAttrs) {
                    String name = dojoAttr.getName();
                    String actualDojoAttrValue = XspRenderUtil.findAttribute(page, name);
                    if( null == existingRenderDojoType ){
                        if( null != actualDojoAttrValue ){
                            fails += def.getFile().getFilePath()+" "
                                    +XspTestUtil.getShortClass(def.getJavaClass())
                                    + " DojoAttribute was output when dojoType absent\n";
                        }
                    }else{
                        if( null == actualDojoAttrValue ){
                            fails += def.getFile().getFilePath()+" "
                                    +XspTestUtil.getShortClass(def.getJavaClass())
                                    + " DojoAttribute not output when default dojoType\n";
                        }
                    }
                }
                
                // set a dojoType and verify the dojoAttributes are output
                TypedUtil.getAttributes(instance).put("dojoType", testDojoType);
                
                XspRenderUtil.resetContainerChild(root, p, instance);
                XspRenderUtil.initControl(this, instance, context);
                page = ResponseBuffer.encode(p, context);
                for (DojoAttribute dojoAttr : dojoAttrs) {
                    String name = dojoAttr.getName();
                    String value = dojoAttr.getValue();
                    String actualDojoAttrValue = XspRenderUtil.findAttribute(page, name);
                    if( !StringUtil.equals(value, actualDojoAttrValue) ){
                        if( null == existingRenderDojoType ){
                            fails += def.getFile().getFilePath()+" "
                                    +XspTestUtil.getShortClass(def.getJavaClass())
                                    + " DojoAttribute not output as expected: " 
                                    +actualDojoAttrValue+"\n";
                        }else{
                            fails += def.getFile().getFilePath()+" "
                                    +XspTestUtil.getShortClass(def.getJavaClass())
                                    + " DojoAttribute not output as expected when custom dojoType: " 
                                    +actualDojoAttrValue+"\n";
                            
                        }
                    }
                }
            }
            
            // reset the instance
            instance = (UIComponent) def.getJavaClass().newInstance();
            
            // set a userId and verify the dojoType is output and the id only appears once.
            String testId = "myId";
            // (note, the "view:" part of the clientID is only present for UIViewRootEx2.getClientId)
            String rootClientId = UIViewRootEx.class.equals(root.getClass())? "" : "view:";
            String testClientId = rootClientId+"_id1:"+testId;
            TypedUtil.getAttributes(instance).put("id", testId);
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            page = ResponseBuffer.encode(p, context);
            
            actualRenderId = XspRenderUtil.findAttribute(page, "id");
            if( !StringUtil.equals(testClientId, actualRenderId) ){
                fails += def.getFile().getFilePath()+" "
                    +XspTestUtil.getShortClass(def.getJavaClass())
                    + " id not output as expected: " +actualRenderId+"\n";
            }
            actualRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            
            if( null == expectedRenderDefaultDojoType ){
            	if( null != actualRenderDojoType ){
            		fails += def.getFile().getFilePath()
                    		+" "+XspTestUtil.getShortClass(def.getJavaClass())
                    		+ " Outputs dojoType= when not explicitly set & userId is set: "
                    		+ actualRenderDojoType + "\n";
            	}
            }else{
            	if( ! StringUtil.equals(expectedRenderDefaultDojoType, actualRenderDojoType) ){
            		fails += def.getFile().getFilePath()
                		+" "+XspTestUtil.getShortClass(def.getJavaClass())
                		+ " Wrong dojoType= when not explicitly set & userId is set, was: "
                		+ actualRenderDojoType + ", expected: " 
                		+ expectedRenderDefaultDojoType+"\n";
            	}
            }
            
            TypedUtil.getAttributes(instance).put("dojoType", testDojoType);
            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            page = ResponseBuffer.encode(p, context);
            if( !StringUtil.equals(testClientId, actualRenderId) ){
                fails += def.getFile().getFilePath()+" "
                    +XspTestUtil.getShortClass(def.getJavaClass())
                    + " id not output as expected when dojoType present: " +actualRenderId+"\n";
            }
            actualRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            if( !StringUtil.equals(testDojoType, actualRenderDojoType) ){
                fails += def.getFile().getFilePath()+" "
                    +XspTestUtil.getShortClass(def.getJavaClass())
                    + " dojoType= not as expected when userId set: " 
                    + actualRenderDojoType+", expected " 
                    + testDojoType+ "\n";
            }
            
            // reset the instance
            instance = (UIComponent) def.getJavaClass().newInstance();
            
            // verify that getDojoType is only computed once.
            Map<String, Object> viewScope = getViewMap(root);
            DojoTypeInvokeCounter counter = new DojoTypeInvokeCounter(testDojoType);
            viewScope.put("counter", counter);
            String expr = "#{viewScope.counter.dojoType}";
            ValueBinding vb = TestProject.getApplication(this).createValueBinding(expr);
            instance.setValueBinding("dojoType", vb);

            XspRenderUtil.resetContainerChild(root, p, instance);
            XspRenderUtil.initControl(this, instance, context);
            page = ResponseBuffer.encode(p, context);
            int invokeCount = counter.count;

            actualRenderDojoType = XspRenderUtil.findAttribute(page, "dojoType");
            if( !StringUtil.equals(testDojoType, actualRenderDojoType) ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " dojoType= not as expected when compute dojoType: " +actualRenderDojoType
                        +", expected " +testDojoType+"\n";
            }
            if( 1 != invokeCount ){
                fails += def.getFile().getFilePath()+" "
                        +XspTestUtil.getShortClass(def.getJavaClass())
                        + " Computed dojoType, expected compute once, was " +invokeCount+" times\n";
            }
        }
        for (Object[] classToDojoType : renderDefaultDojoTypes) {
            if( null != classToDojoType ){
                fails += "Unused expected render default dojoType: {" 
                		+ XspTestUtil.getShortClass((Class<?>)classToDojoType[0])
                		+ ".class, " 
                		+ JavaUtil.toJavaString((String)classToDojoType[1])
                		+"} \n";
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testDojoType"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails) );
        }
    }
    /**
     * new Object[][]{
     * 	new Object[]{ controlClass1, expectedDojoTypeString1},
     * 	new Object[]{ controlClass2, expectedDojoTypeString2},
     * },
	 * @return
	 */
	protected Object[][] getRenderDefaultDojoTypes() {
		return new Object[0][];
	}
	private String getExpectedDojoType(Object[][] renderDefaultDojoTypes, FacesComponentDefinition def){
		int foundIndex = -1;
		Class<?> defClass = def.getJavaClass();
		int i = 0;
		for (Object[] classToDojoType : renderDefaultDojoTypes) {
			if( null != classToDojoType ){
				Class<?> compClass = (Class<?>) classToDojoType[0];
				if( compClass.equals(defClass) ){
					foundIndex = i;
					break;
				}
			}
			i++;
		}
		if( -1 == foundIndex ){
			return null;
		}
		Object[] classToDojoType = renderDefaultDojoTypes[foundIndex];
		renderDefaultDojoTypes[foundIndex] = null;
		
		String dojoType = (String) classToDojoType[1];
		if( null == dojoType ){
			throw new RuntimeException();
		}
		return dojoType;
	}
	/**
     * Available to override in subclasses
     * @return
     */
    private boolean isShouldLoadXspClientDojoUI() {
        String libraryId = ConfigUtil.getTargetLibrary(this);
        if( null == libraryId ){
            return false;
        }
        // if it's one of ..xsp.core, ..xsp.extsn, etc - the libraries
        // in the XPages core runtime. Not an extension to the runtime. 
        return LibraryServiceLoader.isXPagesRuntimeLibrary(libraryId);
    }
    @SuppressWarnings("unchecked")
    private Map<String, Object> getViewMap(UIViewRootEx root) {
        return root.getViewMap();
    }
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    public static class DojoTypeInvokeCounter{
        public int count = 0;
        private String dojoType;
        public DojoTypeInvokeCounter(String dojoType) {
            super();
            this.dojoType = dojoType;
        }
        public String getDojoType() {
            count++;
            return dojoType;
        }
    }
}
