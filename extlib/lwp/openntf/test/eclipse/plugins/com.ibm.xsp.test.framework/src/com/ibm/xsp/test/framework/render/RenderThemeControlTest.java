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
* Date: 17 Feb 2012
* RenderThemeControlTest.java
*/

package com.ibm.xsp.test.framework.render;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.render.Renderer;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DefinitionTagsAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;
import com.sun.faces.context.FacesContextImpl;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class RenderThemeControlTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that the controls render in different themes without exceptions";
    }
    public void testRenderControlsInThemes() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, 
                new DefinitionTagsAnnotater());
        if( TestProject.getLibComponents(reg, this).isEmpty() ){
            // no controls in this library, skip control rendering test
            String fails = "";
            fails = XspTestUtil.removeMultilineFailSkips(fails,
                    SkipFileContent.concatSkips(getSkipFails(), this, "testRenderControlsInThemes"));
            if( fails.length() > 0 ){
                fail(XspTestUtil.getMultilineFailMessage(fails));
            }
            return;
        }
        
        File dominoFolder = TestProject.detectDominoInstallLocation(this);
        // the platform is needed to load the .theme files from the Domino install location
        if(!TestProject.isNotesDLL()){
        TestProject.initPlatform(this, dominoFolder);
        }
        File themesFolder = new File(dominoFolder, "xsp/nsf/themes");
        List<String> themeFileIds = detectThemeFileIds(themesFolder);
        
        List<String> themeIds = new ArrayList<String>();
        themeIds.addAll(themeFileIds);
        themeIds.addAll(computeContributedThemes(themeIds));
        themeIds.add(0, null); // first use the default, not-configured theme (usually "webstandard")
//        String paths = StringUtil.concatStrings(StringUtil.toStringArray(themeIds), /*separator*/' ', /*trim*/false);
//        System.out.println("RenderThemeControlTest.testRenderControlsInThemes() testing themeIds: "+paths);
        
        String fails = "";
        ContextAndPage[] contexts = new ContextAndPage[themeIds.size()];
        int i = 0;
        for (String themeId : themeIds) {
//          System.out.println("RenderThemeControlTest.testRenderControlsInThemes()" 
//                  + " ===================== themeId= "+themeId);
            
            boolean problemWithTheme = false;
            // set up the page to be rendered
            FacesContextEx context = (FacesContextEx) TestProject.createFacesContext(this);
            ResponseBuffer.initContext(context);
            if( null != themeId ){
                context.setSessionProperty("xsp.theme", themeId);
            }
            
            UIViewRootEx root = TestProject.loadEmptyPage(this, context);
            String expectedThemeName = (null != themeId)? themeId : 
                    Platform.getInstance().isPlatform("Notes")? "notes" : "webstandard";
                if( !expectedThemeName.equals(context.getStyleKit().getName()) ){
                    String actualCurrentTheme = context.getStyleKit().getName();
                    fails += "Problem setting theme to " +themeId+
                            ", current theme is: " +actualCurrentTheme+"\n";
                    problemWithTheme = true;
                }
            UIPassThroughTag p = problemWithTheme? null : XspRenderUtil.createContainerParagraph(root);
            
            if( problemWithTheme ){
                contexts[i++] = null;
            }else{
                contexts[i++] = new ContextAndPage(themeId, context, root, p);
            }
        }
        // for each control
        for (FacesComponentDefinition def : TestProject.getLibComponents(reg, this)) {
            if( !def.isTag() ){
                continue; // skip non-tags
            }
            if( DefinitionTagsAnnotater.isTaggedNoRenderedOutput(def) ){
                // verified in RenderControlTest
                continue;
            }
            // verify can create a control instance
            try{
                def.getJavaClass().newInstance();
            }catch(Exception e){
                // no need to fail here as RenderControlTest 
                // will already be failing for the issue.
                continue;
            }
            String defFails = "";
            boolean testedFirstTheme = false;
            String firstDefFail = null;
            boolean allFailsSameAsFirst = true;
            for (ContextAndPage objs : contexts){
                if( null == objs ){
                    // reported above as Problem setting theme
                    continue;
                }
                String themeId = objs.themeId;
                FacesContextEx context = objs.context;
                UIViewRootEx root = objs.root;
                UIPassThroughTag p = objs.p;
                
                setCurrentContext(context);
                
                String currentDefFail = null;
                
                // create a control instance
                UIComponent instance = null;
                try{
                    instance = (UIComponent) def.getJavaClass().newInstance();
                }catch(Exception e){
                    currentDefFail = "Exception re-creating instance "+e;
                    defFails += XspTestUtil.loc(def)
                            +" [theme:" +themeId+"]"
                            +" " +currentDefFail +"\n";
                }
                String page = null;
                if( null != instance ){
                    
                    XspRenderUtil.resetContainerChild(root, p, instance);
                    XspRenderUtil.initControl(this, instance, context);
                    
                    // only apply the theme after adding the control to the page tree 
                    // so that instance.getParent() is non-null (prevents NullPointerEx)
                    context.getStyleKit().applyStyles(context, instance);
                    
                    // verify there is a renderer - the theme may modify the rendererType
                    // so the renderer would be different to that tested in ComponentRendererTest
                    String actualComponentFamily = instance.getFamily();
                    String actualRendererType = instance.getRendererType();
                    Renderer renderer = context.getRenderKit().getRenderer(actualComponentFamily, actualRendererType);
                    if( null == renderer ){
                        currentDefFail = "No renderer found for " 
                                +"component-family " +actualComponentFamily
                                +", renderer-type "+actualRendererType;
                        defFails += XspTestUtil.loc(def)
                                +" [theme:" +themeId+"]"
                                + " " +currentDefFail+"\n";
                    }

                    try{
                        page = ResponseBuffer.encode(p, context);
                    }catch(Exception e){
                        e.printStackTrace();
                        currentDefFail = "Problem rendering page: "+e;
                        defFails += XspTestUtil.loc(def)
                                +" [theme:" +themeId+"]"
                                + " " +currentDefFail+"\n";
                        ResponseBuffer.clear(context);
                    }
                }
                if( null != page ){
//                    System.out.println("RenderThemeControlTest.testRenderControlsInThemes() "
//                            +XspTestUtil.loc(def)
//                            +" [theme:" +themeId+"]"
//                            +"\n"+page);
                    if( ! page.startsWith("<p>") ){
                        currentDefFail = "Wrote attributes to the parent <p> tag: "+ page;
                        defFails += XspTestUtil.loc(def)
                                +" [theme:" +themeId+"]"
                                + " " + currentDefFail + "\n";
                    }
                    if( page.equals("<p></p>") ){
                        currentDefFail = "No output rendered.";
                        defFails += XspTestUtil.loc(def)
                                +" [theme:" +themeId+"]"
                                + " " +currentDefFail +"\n";
                    }
                }
                if( ! testedFirstTheme ){
                    firstDefFail = currentDefFail;
                    testedFirstTheme = true;
                }else{
                    if( !StringUtil.equals(firstDefFail, currentDefFail) ){
                        allFailsSameAsFirst = false;
                    }
                }
            }
            if( allFailsSameAsFirst ){
                if( null != firstDefFail ){
                    // no need to fail here as RenderControlTest 
                    // will already be failing for the issue.
//                    fails += XspTestUtil.loc(def)
//                            +" [all-themes]"
//                            + " " +firstDefFail +"\n";
                }
            }else{
                fails += defFails;
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testRenderControlsInThemes"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected List<String> computeContributedThemes(
            List<String> themeFileIds) {
        // TODO in Extlib, compute "oneui_mobile", etc.
        return Collections.emptyList();
    }
    /**
     * Available to override in subclasses.
     * @return
     */
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    
    /**
     * Utility method available to call from subclass. 
     * Uses {@link ConfigUtil#isIgnoreThemeFilesWithUnderscore(AbstractXspTest)}
     * @param themesFolder
     * @return
     */
    protected List<String> detectThemeFileIds(File themesFolder) {
        boolean excludeUnderscoredThemes = ConfigUtil.isIgnoreThemeFilesWithUnderscore(this);
        
        List<String> themeFileShortNames = new ArrayList<String>();
        for (File themeFile : themesFolder.listFiles()) {
            String nameWithSuffix = themeFile.getName();
            if( !nameWithSuffix.endsWith(".theme") ){
                continue;
            }
            String baseName = nameWithSuffix.substring(0, nameWithSuffix.length()-".theme".length());
            
            if( excludeUnderscoredThemes && -1 != baseName.indexOf('_')){
                continue;
            }
            themeFileShortNames.add(baseName);
        }
        
        return themeFileShortNames;
    }
    private void setCurrentContext(final FacesContextEx context ){
        new FacesContextImpl(){
            {
                setCurrentInstance(context);
            }
        }.getClass(); // getClass() to prevent compile warning: The allocated object is never used
    }
    private static class ContextAndPage{
        public String themeId;
        public FacesContextEx context;
        public UIViewRootEx root;
        public UIPassThroughTag p;
        public ContextAndPage(String themeId, FacesContextEx context, UIViewRootEx root, UIPassThroughTag p) {
            super();
            this.context = context;
            this.root = root;
            this.p = p;
            this.themeId = themeId;
        }
    }
    
}
