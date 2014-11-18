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
package com.ibm.xsp.test.framework.registry;

import java.util.ArrayList;
import java.util.List;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Stephen Renwick
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ComponentRendererTest extends AbstractXspTest {
    
    @Override
    public String getDescription() {
        return "that most components have a renderer-type and component-family";
    }

	public void testComponentRenderers() throws Exception{
	    List<Class<?>> renderTypesDiffer = getRenderTypesDifferSkips();
	    List<Class<?>> rendererTypesNull = getRendererTypesNullSkips();
	    List<Class<?>> rendererTypeConstNull = getRendererTypeConstNullSkips();

        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, 
                new DefinitionTagsAnnotater());
		FacesContext context = TestProject.createFacesContext(this);
		// a control tree is required because the renderKitId is read from the viewRoot,
		// so without it can't test context.getRenderKit().getRenderer
		TestProject.loadEmptyPage(this, context);
		

        // the per-renderKit expected values
		RenderKitCheckInfo[] kitInfos = getRenderKitInfos(context);

		StringBuffer fails = new StringBuffer();

		// Iterate through all components and check render details
		
        for (FacesComponentDefinition compDef : TestProject.getLibComponents(reg, this)) {
			// create the component instance
			UIComponent component = instanciateComponent(compDef);
			if (component == null) {
                // will often be null if !compDef.isTag
			    continue;
			}

			// check the component configuration
			if (!StringUtil.equals(component.getFamily(), compDef.getComponentFamily())) {
				addFailComponentFamilyWrong(fails, component, compDef);
			}
            if( !StringUtil.equals(component.getRendererType(), compDef.getRendererType()) ){
                if( !allowsNonEqualRendererType(renderTypesDiffer, component) ){
                    addFailRendererTypeWrong(fails, compDef, component);
                }
            }
            String rendererTypeConstant = rendererTypeConstant(component);
            if( null != rendererTypeConstant && !StringUtil.equals(component.getRendererType(), rendererTypeConstant) ){
                addFailRendererConstantWrong(fails, component, compDef, rendererTypeConstant);
            }
			if (!compDef.isTag()) {
				continue;
			}
            if ( null == component.getRendererType() ) {
                if( ! allowsNullRendererType(rendererTypesNull, component) ){
                    addFailRendererTypeNull(fails, component, compDef);
                }
                // no rendererType => not rendered 
                // so not test the renderers
            }
            if( null == rendererTypeConstant ){
                if( ! allowsNullRendererTypeConst(rendererTypeConstNull, component) ){
                    addFailRendererTypeConstantNull(fails, component, compDef);
                }
            }
            if( null == component.getFamily() ){
                addFailComponentFamilyNull(fails, component, compDef);
            }
            if( null != component.getFamily() && null != component.getRendererType() ){
                String componentFamily = component.getFamily();
                String rendererType = component.getRendererType();
                
                Renderer renderer = context.getRenderKit().getRenderer(componentFamily, rendererType);
                boolean hasRenderer = (null != renderer); 
                boolean expectRenderer = true;
                if( DefinitionTagsAnnotater.isTagged(compDef, "no-faces-config-renderer") ){
                    expectRenderer = false;
                }
                if( hasRenderer != expectRenderer ){
                    if( !hasRenderer ){
                        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                                + " No runtime renderer found.\n";
                        System.err.print(ComponentRendererTest.class.getName()
                                + ".testComponentRenderers() : " + message);
                        fails.append( message);
                    }else{
                        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                                + " Unexpected runtime renderer found,  when " 
                                + "<tags>" +"no-faces-config-renderer"+"< is present\n";
                        System.err.print(ComponentRendererTest.class.getName()
                                + ".testComponentRenderers() : " + message);
                        fails.append( message);
                    }
                }
            }
		}
        checkAllSkipsUsed(fails, rendererTypesNull, renderTypesDiffer, kitInfos, rendererTypeConstNull);
        
        fails = new StringBuffer(XspTestUtil.removeMultilineFailSkips(fails.toString(), 
                SkipFileContent.concatSkips(getSkipFails(), this, "testComponentRenderers")));
		if (fails.length() > 0) {
			fail(XspTestUtil.getMultilineFailMessage(fails.toString()));
		}
	}
	protected String[] getSkipFails(){
	    return StringUtil.EMPTY_STRING_ARRAY;
	}
	/**
	 * The list of component class names where isRendered returns true but
	 * there's no HTML renderer for the component-family and renderer-type
	 * combination. This list should be as short as possible.
	 * @return
	 */
	protected List<Class<?>> getHtmlNoRendererSkips() {
		List<Class<?>> skips = new ArrayList<Class<?>>();
		return skips;
	}

	protected List<Class<?>> getRenderTypesDifferSkips() {
		List<Class<?>> skips = new ArrayList<Class<?>>();
		return skips;
	}

	/**
	 * Note, components without a renderer-type cannot use
	 * renderer-specific properties.
	 * @return
	 */
	protected List<Class<?>> getRendererTypesNullSkips() {
		List<Class<?>> skips = new ArrayList<Class<?>>();
		return skips;
	}
	protected List<Class<?>> getRendererTypeConstNullSkips() {
		List<Class<?>> skips = new ArrayList<Class<?>>();
		return skips;
	}

	public List<FacesComponentDefinition> getNonLocalCompDefs(
            FacesSharableRegistry compReg) {
        List<FacesComponentDefinition> comps = new ArrayList<FacesComponentDefinition>();
        for (FacesSharableRegistry depend : compReg.getDepends()) {
            comps.addAll( depend.findComponentLocalDefs() );
        }
        return comps;
    }

    private String rendererTypeConstant(UIComponent component) {
        return XspTestUtil.getStringConstant(component.getClass(), "RENDERER_TYPE", /*declared*/false);
    }

    private void checkAllSkipsUsed(StringBuffer fails, List<Class<?>> rendererTypesNull, List<Class<?>> renderTypesDiffer, RenderKitCheckInfo[] kitInfos, List<Class<?>> rendererTypeConstNull) {
        
        checkSkipArrayEmpty(fails, renderTypesDiffer, "rendererType mismatch");
        checkSkipArrayEmpty(fails, rendererTypesNull, "null rendererType");
        checkSkipArrayEmpty(fails, rendererTypeConstNull, "RENDERER_TYPE null or not present");
        
        for (int i = 0; i < kitInfos.length; i++) {
            RenderKitCheckInfo info = kitInfos[i];
            
            List<Class<?>> skips = info.getSkippedComponents();
            String expected = "skip for kit:" +info.getRenderKitAlias();
            checkSkipArrayEmpty(fails, skips, expected);
        }
    }

    private void checkSkipArrayEmpty(StringBuffer fails, List<Class<?>> skipArray, String expected) {
        for (Class<?> skippedComponentClass : skipArray) {
            if( null != skippedComponentClass ){
                fails.append("skip not used, component:"
                        + skippedComponentClass.getName() + " expected "
                        + expected + " \n");
            }
        }
    }
    private void addFailRendererConstantWrong(StringBuffer fails, UIComponent component,
            FacesComponentDefinition compDef, String rendererTypeConstant) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                + " Mismatch RENDERER_TYPE != getRendererType() for "
                + XspTestUtil.getShortClass(component) + ". Expected "
                + component.getRendererType() + ", was "
                + rendererTypeConstant + "(RENDERER_TYPE)\n";
        System.err.print(ComponentRendererTest.class.getName()
                + ".testComponentRenderers() : " + message);
        fails.append(message);
    }
    private void addFailRendererTypeWrong(StringBuffer fails, FacesComponentDefinition compDef, UIComponent component) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                + " Mismatch xsp-config<renderer-type> != getRendererType() for "
                + XspTestUtil.getShortClass(component) + ". Expected "
                + component.getRendererType() + ", was >"
                +  compDef.getRendererType()+ "</renderer-type>\n";
        System.err.print(ComponentRendererTest.class.getName()
                + ".testComponentRenderers() : " + message);
        fails.append( message);
    }
    private void addFailComponentFamilyWrong(StringBuffer fails, UIComponent component, FacesComponentDefinition compDef) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
        		+ " Mismatch <component-family> != getFamily() for "
        		+ XspTestUtil.getShortClass(component) + ". Expected "
        		+ component.getFamily() + ", was >"
        		+  compDef.getComponentFamily() + "</component-family>\n";
        System.err.print(ComponentRendererTest.class.getName()
        		+ ".testComponentRenderers() : " + message);
        fails.append( message);
    }
    private void addFailRendererTypeNull(StringBuffer fails, UIComponent component, FacesComponentDefinition compDef) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                + " Problem getRendererType() was null for "
                + XspTestUtil.getShortClass(component) + " (should be set in constructor)\n";
        System.err.print(ComponentRendererTest.class.getName()
                + ".testComponentRenderers() : " + message);
        fails.append( message);
    }
    private void addFailComponentFamilyNull(StringBuffer fails,
            UIComponent component, FacesComponentDefinition compDef) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                + " Problem getFamily() was null for "
                + XspTestUtil.getShortClass(component) + " (getFamily() method should be overridden)\n";
        System.err.print(ComponentRendererTest.class.getName()
                + ".testComponentRenderers() : " + message);
        fails.append( message);
    }
    private void addFailRendererTypeConstantNull(StringBuffer fails,
            UIComponent component, FacesComponentDefinition compDef) {
        String message = compDef.getFile().getFilePath()+" "+ getCompId(compDef)
                + " Problem RENDERER_TYPE was null for "
                + XspTestUtil.getShortClass(component)
                + " It is required since 8.5.3 "
                + "to allow the rendererType to be set in a theme file.\n";
        System.err.print(ComponentRendererTest.class.getName()
                + ".testComponentRenderers() : " + message);
        fails.append(message);
    }

    
    private RenderKitCheckInfo[] getRenderKitInfos(FacesContext context) {
        // get from subclass
        RenderKitCheckInfo[] kitInfos = createRenderKitInfos();
        
        // validate RenderKitInfos RenderKits exist
		RenderKitFactory factory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
		for (int i = 0; i < kitInfos.length; i++) {
			RenderKit kit = kitInfos[i].findRenderKit(context, factory);
			if (null == kit) {
				assertNotNull("No faces-config containing the renderKit "+ kitInfos[i].getRenderKitId(), /*kit*/null);
			}
		}
        return kitInfos;
    }
    protected static class RenderKitCheckInfo {
        private String _renderKitId;
        private String _renderKitAlias;
        private List<Class<?>> _skippedComps;
        private RenderKit _renderKit;
        
        public RenderKitCheckInfo(String renderKitId, String renderKitAlias, List<Class<?>> skippedComps) {
            super();
            _renderKitId = renderKitId;
            _renderKitAlias = renderKitAlias;
            _skippedComps = skippedComps;
        }
        /**
         * Return a RenderKit, lazily created.
         */
        public RenderKit findRenderKit(FacesContext context, RenderKitFactory factory) {
          _renderKit = factory.getRenderKit(context, getRenderKitId());
          return _renderKit;
        }
        public RenderKit getRenderKit() {
            return _renderKit;
        }
        public List<Class<?>> getSkippedComponents() {
            return _skippedComps;
        }
        public String getRenderKitAlias() {
            return _renderKitAlias;
        }
        public String getRenderKitId() {
            return _renderKitId;
        }
    }

    /**
     * Return the renderKit infos, where none are null.
     * 
     * @return
     */
    protected RenderKitCheckInfo[] createRenderKitInfos() {
        RenderKitCheckInfo[] result = new RenderKitCheckInfo[1];
        result[0] = new RenderKitCheckInfo(RenderKitFactory.HTML_BASIC_RENDER_KIT, "html", getHtmlNoRendererSkips());
        return result;
    }
	private String getCompId(FacesComponentDefinition component) {
		return XspRegistryTestUtil.descr(component);
	}
    private boolean allowsNonEqualRendererType(List<Class<?>> renderTypesDiffer, UIComponent component) {
        return isStillInSkipList(renderTypesDiffer, component);
    }
    private boolean allowsNullRendererType(List<Class<?>> rendererTypesNull, UIComponent component) {
        return isStillInSkipList(rendererTypesNull, component);
    }
    private boolean allowsNullRendererTypeConst(List<Class<?>> rendererTypesNull, UIComponent component) {
        return isStillInSkipList(rendererTypesNull, component);
    }
    private boolean isStillInSkipList(List<Class<?>> skips, UIComponent component) {
        int i = 0;
        for (Class<?> skip : skips) {
            if ( null != skip && skip.equals(component.getClass()) ) {
                skips.set(i, null);
                return true;
            }
            i++;
        }
        return false;
    }

	private UIComponent instanciateComponent(FacesComponentDefinition compDef){
        Class<?> clazz = compDef.getJavaClass();
        Exception ex;
        try{ 
            return (UIComponent) clazz.newInstance();
        } catch (InstantiationException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        }
        if (compDef.isTag()) {
            System.err.println(ComponentRendererTest.class.getName()
                    + ".testComponentRenderers() : "
                    + "Problem creating an instance of " + clazz
                    + ". Defined in "
                    + compDef.getFile().getFilePath());
            ex.printStackTrace();
            fail("Problem creating an instance for the tag. Class is "
                    + clazz);
        }
        return null;
    }
}
