/*
 * © Copyright IBM Corp. 2010, 2011
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

package xsp.extlib.test.registry;


import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.rest.RestServiceEngine;
import com.ibm.xsp.component.UIViewPanel;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.component.dynamicview.UIDynamicViewPanel;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;
import com.ibm.xsp.extlib.component.outline.UIOutlineGeneric;
import com.ibm.xsp.extlib.component.picker.data.DominoViewNamePickerData;
import com.ibm.xsp.extlib.component.picker.data.INamePickerData;
import com.ibm.xsp.extlib.component.picker.data.IPickerData;
import com.ibm.xsp.extlib.component.picker.data.IValuePickerData;
import com.ibm.xsp.extlib.component.picker.data.SimpleValuePickerData;
import com.ibm.xsp.extlib.component.rest.AbstractRestService;
import com.ibm.xsp.extlib.component.rest.UIBaseRestService;
import com.ibm.xsp.extlib.component.rest.UIRestService;
import com.ibm.xsp.extlib.component.tagcloud.UITagCloud;
import com.ibm.xsp.extlib.component.tagcloud.ViewTagCloudData;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.HtmlListRenderer;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicLeafTreeNode;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.registry.BasePropertiesHaveSettersTest;
import com.ibm.xsp.test.framework.registry.PropertiesHaveSettersTest;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 6 May 2010
 */
public class ExtlibPropertiesHaveSettersTest extends 
	BasePropertiesHaveSettersTest {

    private static final TestRestService TEST_REST_SERVICE = new TestRestService();
    // like new Object[] {"disabledClass", HtmlSelectOneMenu.class},
    private Object[][] _extlibTotallySkippedProperties = new Object[][]{
//    	new Object[] {"rendered", UserTreeNode.class},
//    	new Object[] {"rendered", LoginTreeNode.class},
    };
    
    private Object[][] s_propertyNotAllowValueBinding_core = new Object[][]{
            // Skips for issues in the XPages runtime, not problems in the extlib.
            // Start copied from ExtsnPropertiesHaveSettersTest:
            new Object[]{"width", UIViewPanel.class},
            new Object[]{"height", UIViewPanel.class},
            // end copied from ExtsnPropertiesHaveSettersTest.
    };
    private Object[][] s_propertyNotAllowValueBinding = new Object[][]{
            getUserTreeNodeLabelVBSkip(),
            getUIDynamicViewPanelVarNotVBSkip(),
    };
    @Override
    public void testPropertiesHaveSetters() throws Exception {
        // TODO should not need a FacesContext instance with a UIViewRootEx
        TestProject.createFacesContext(this).setViewRoot(new UIViewRootEx());
        
        super.testPropertiesHaveSetters();
    }


    /**
     * To be provided to
     * {@link PropertiesHaveSettersTest#getPropertyNotAllowValueBindings(FacesSharableRegistry)},
     * skips the UserTreeNode "label" property, because the get method gives an NPE:
     * javax.faces.FacesException: javax.faces.FacesException: 
     *                Can't instantiate class: 'com.ibm.xsp.extlib.beans.UserBean'.. null
     *     at com.sun.faces.application.ApplicationAssociate.createAndMaybeStoreManagedBeans(ApplicationAssociate.java:300)
     *     at com.sun.faces.el.VariableResolverImpl.resolveVariable(VariableResolverImpl.java:135)
     *     at com.ibm.xsp.el.VariableResolverImpl.resolveVariable(VariableResolverImpl.java:71)
     *     at com.ibm.xsp.extlib.beans.UserBean.get(UserBean.java:61)
     *     at com.ibm.xsp.extlib.tree.complex.UserTreeNode.getLabel(UserTreeNode.java:67)
     * ...
     * Caused by: java.lang.NullPointerException
     *     at com.ibm.xsp.extlib.social.SocialServicesFactory.getAuthenticatedUserId(SocialServicesFactory.java:71)
     *     at com.ibm.xsp.extlib.beans.UserBean.<init>(UserBean.java:50)
     *     ... 34 more
     * 
     * safe to ignore because the "var" cannot
     * have a runtime binding, would fail because the setValueBinding method has
     * been overridden to throw an IllegalArgumentException for the "var" property.
     */
    protected Object[] getUserTreeNodeLabelVBSkip(){
        return new Object[]{"label", UserTreeNode.class};
    }
    /**
     * To be provided to
     * {@link PropertiesHaveSettersTest#getPropertyNotAllowValueBindings(FacesSharableRegistry)},
     * skips the UIDynamicViewPanel "var" property, safe to ignore because the "var" cannot
     * have a runtime binding, would fail because the setValueBinding method in the superclass
     * UIData has been overridden to throw an IllegalArgumentException for the "var" property.
     */
    protected Object[] getUIDynamicViewPanelVarNotVBSkip(){
        return new Object[]{"var", UIDynamicViewPanel.class};
    }    
	@Override
    protected Object getSomeValue(FacesDefinition def, FacesProperty prop,
            Class<?> javaClass) throws Exception {
        if("service".equals(prop.getName()) 
                && UIRestService.class.equals(def.getJavaClass()) ){
            return TEST_REST_SERVICE;
        }
        if("treeRenderer".equals(prop.getName())
        		&& UIOutlineGeneric.class.equals(def.getJavaClass()) ){
        	return new HtmlListRenderer();
        }
        if("cloudData".equals(prop.getName()) && UITagCloud.class.equals(def.getJavaClass())){
            return new ViewTagCloudData();
        }
        if("configuration".equals(prop.getName()) && UIApplicationLayout.class.equals(def.getJavaClass()) ){
            return new BasicApplicationConfigurationImpl();
        }
        if( IValuePickerData.class.equals(prop.getJavaClass()) ){
            return new SimpleValuePickerData();
        }
        if( INamePickerData.class.equals(prop.getJavaClass()) ){
            return new DominoViewNamePickerData();
        }
        if( IPickerData.class.equals(prop.getJavaClass()) ){
            return new SimpleValuePickerData();
        }
        if( ITreeNode.class.equals( prop.getJavaClass() ) ){
            return new BasicLeafTreeNode();
        }
        return super.getSomeValue(def, prop, javaClass);
    }
	@Override
	protected List<Object[]> getTotallySkippedProperties(FacesSharableRegistry reg) {
		List<Object[]> list = super.getTotallySkippedProperties(reg);
        list.addAll(Arrays.asList(_extlibTotallySkippedProperties));
		return list;
	}
    @Override
    protected List<Object[]> getPropertyNotAllowValueBindings(FacesSharableRegistry reg) {
        List<Object[]> list = super.getPropertyNotAllowValueBindings(reg);
        list.addAll(Arrays.asList(s_propertyNotAllowValueBinding_core));
        list.addAll(Arrays.asList(s_propertyNotAllowValueBinding));
        return list;
    }
    public static class TestRestService extends AbstractRestService{
        public RestServiceEngine createEngine(FacesContext context,
                UIBaseRestService parent, HttpServletRequest httpRequest,
                HttpServletResponse httpResponse) {
            return null;
        }
    }

}
