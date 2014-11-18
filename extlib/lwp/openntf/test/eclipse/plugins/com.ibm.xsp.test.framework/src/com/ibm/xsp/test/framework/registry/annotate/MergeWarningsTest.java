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
 * Date: 6 Jun 2008
 * MergeWarningsTest.java
 */

package com.ibm.xsp.test.framework.registry.annotate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.library.FacesClassLoader;
import com.ibm.xsp.library.LibraryServiceLoader;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.registry.AbstractFacesDefinition;
import com.ibm.xsp.registry.FacesConverterDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesRenderKitFragment;
import com.ibm.xsp.registry.FacesRendererDefinition;
import com.ibm.xsp.registry.FacesValidatorDefinition;
import com.ibm.xsp.registry.SharableRegistryImpl;
import com.ibm.xsp.registry.config.ConfigRegisterer;
import com.ibm.xsp.registry.config.FacesClassLoaderFactory;
import com.ibm.xsp.registry.parse.ConfigParserFactory;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com) 6 Jun 2008 Unit:
 *         MergeWarningsTest.java
 */
public class MergeWarningsTest extends AbstractXspTest {
    private static final String JSF_CORE_NAMESPACE = "http://www.ibm.com/xsp/jsf/core";
    public static final String JSF_HTML_NAMESPACE = "http://www.ibm.com/xsp/jsf/html";

    private SharableRegistryImpl _jsfRegistry;
	private FacesClassLoader _facesLoader;

	@Override
    public String getDescription() {
        // note, to actually see the merge warnings, run MergeWarningsPrinter.
        return "that validator and converter ids are only declared once "
                + "in the faces-configs, to prevent merge warnings in the logs";
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        // note, don't need to discard the already-parsed registries
        // because this is parsing faces-config.xml files,
        // not xsp-config files.
        RegistryAnnotater annotator = new ConverterForClassAnnotater();
        ConfigParserFactory.addAnnotater(annotator);
        try{
            String id ="library_"+(int)(Math.random()*10000);
            _jsfRegistry = new SharableRegistryImpl(id);
            populateFacesConfigRegistry();
        }finally{
            ConfigParserFactory.removeAnnotater(annotator);
        }
    }
    public void testPresentInJsfConfig() throws Exception {
        FacesRegistry reg = _jsfRegistry;

        Map<String, FacesDefinition> keyToDependsDef = new HashMap<String, FacesDefinition>();
        
        List<String> libraryFilePaths;
        {
            String targetLibId = ConfigUtil.getTargetLibrary(this);
            LibraryWrapper targetLib = LibraryServiceLoader.getLibrary(targetLibId);
            String[] targetLibPaths = targetLib.getFacesConfigFiles();
            libraryFilePaths = new ArrayList<String>(Arrays.asList(targetLibPaths));
            Collections.sort(libraryFilePaths);
        }
        
        List<FacesDefinition> libraryDefs = new ArrayList<FacesDefinition>();
        // populate the Maps above with depends ids, and build a list of library defs
        for (FacesDefinition def : getDefs(reg)) {
            if( Collections.binarySearch(libraryFilePaths, def.getFile().getFilePath()) >= 0){
                // library def
                libraryDefs.add(def);
                continue;
            }// else depends def
            String key = computeKey(def);
            keyToDependsDef.put(key, def);
        }
        
        String fails = "";
        Map<String, FacesDefinition> keyToLibDef = new HashMap<String, FacesDefinition>();
        for (FacesDefinition def : libraryDefs) {
            String key = computeKey(def);
            // store the key/def & check for previously stored
            FacesDefinition existingDef = keyToLibDef.put(key, def);
            if( null == existingDef ){
                existingDef = keyToDependsDef.get(key);
            }
            if( null == existingDef ){
                // not overriding existing def
                continue;
            }
            // fail, overriding existing def.
			String fail = path(def)+ " Merge " + key 
					+ " (overrides: "+ path(existingDef) + ")";
			fails += fail + "\n";
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testPresentInJsfConfig"));
        if (fails.length() > 0) {
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    private String computeKey(FacesDefinition def) {
        if (def instanceof FacesValidatorDefinition) {
            FacesValidatorDefinition val = (FacesValidatorDefinition) def;
            String validatorId = val.getValidatorId();
            return "[validator-id:"+validatorId+"]";
        }
        if( def instanceof FacesRendererDefinition ){
            FacesRendererDefinition ren = (FacesRendererDefinition) def;
            String key = ren.getRenderKitFragment().getRenderKitId()+ " "+ren.getId();
            return "[renderer:"+key+"]";
        }
        if( def instanceof ConverterForClassDefinition ){
            ConverterForClassDefinition forClass = (ConverterForClassDefinition)def;
            return "[converter-for-class:"+forClass.getReferenceId()+"]";
        }
        if( def instanceof FacesConverterDefinition ){
            FacesConverterDefinition con = (FacesConverterDefinition) def;
            String converterId = con.getConverterId();
            return "[converter-id:" +converterId+ "]";
        }
        throw new RuntimeException("Unknown def type: "+def);
    }
    private String path(FacesDefinition def) {
        return def.getFile().getFilePath();
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    
    @SuppressWarnings("unchecked")
	private List<FacesDefinition> getDefs(FacesRegistry reg){
        List<FacesDefinition> defs = findNonRenDefsByProject(reg);
        
        for (Iterator<FacesDefinition> i = defs.iterator(); i.hasNext();) {
            FacesDefinition def = i.next();
            if (def instanceof FacesValidatorDefinition) {
                //keep
            }
            else if (def instanceof FacesConverterDefinition) {
                // keep
            }
            else{
                i.remove();
            }
        }
        List<FacesRendererDefinition> rens = findRendererDefsByProject(reg);
        defs.addAll(rens);
        for (FacesProject proj : reg.getProjectList()) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                List<ConverterForClassDefinition> forClassList = (List<ConverterForClassDefinition>) 
                        file.getExtension("converter-for-class-list");
                if( null != forClassList ){
                    defs.addAll(forClassList);
                }
            }
        }
//        Collections.reverse(defs);
        return defs;
    }
	private List<FacesRendererDefinition> findRendererDefsByProject(
			FacesRegistry reg) {
		List<FacesRendererDefinition> defs = new ArrayList<FacesRendererDefinition>();
		for (FacesProject proj : reg.getProjectList()) {
			for (FacesLibraryFragment file : proj.getFiles()) {
				for (String kitId : file.getRenderKitIds()) {
					FacesRenderKitFragment kitFrag = file.getRenderKitFragment(kitId);
					defs.addAll(kitFrag.getDefs());
				}
			}
		}
		return defs;
	}
	private List<FacesDefinition> findNonRenDefsByProject(FacesRegistry reg) {
		List<FacesDefinition> defs = new ArrayList<FacesDefinition>();
		for (FacesProject proj : reg.getProjectList()) {
			for (FacesLibraryFragment file : proj.getFiles()) {
				defs.addAll(file.getDefs());
			}
		}
		return defs;
	}
	private void populateFacesConfigRegistry() {
		for (String path : TestProject.findFacesConfigPaths(this)) {
			if( "META-INF/core-faces-config.xml".equals(path) ){
				// register the xsp-config jsf-base.xsp-config
				registerJsfBase();
				registerJsfImpl();
				// fall through to register core-faces-config.xml
			}
			URL url = getFacesConfigUrlEndingWith(path, path);
			if( null == url ){
				throw new RuntimeException("Cannot find " +path+
						" on classpath.");
			}
			String placeholderNamespace = "placeholderNamespaceUri";
			registerJsfFacesConfig(url, placeholderNamespace, path);
		}
	
		_jsfRegistry.refreshReferences();
	}
	private void registerJsfBase() {
		String container = "com.ibm.xsp.core/bin/";
		String namespace = JSF_CORE_NAMESPACE;
		String relativePath = "META-INF/jsf-base.xsp-config";
		URL coreCommon = getFacesConfigUrlEndingWith(container + relativePath,
				relativePath);
		if(coreCommon==null) {
			fail(relativePath);
		}
		registerJsfFacesConfig(coreCommon, namespace, relativePath);
	}
	private void registerJsfImpl() {
		String location = "jsf-impl.jar!/";
	
	    //check for JSF 1.0
	    String relativePath = "com/sun/faces/jsf-ri-config.xml";
	    String end = location + relativePath;
	    URL validUrl = getFacesConfigUrlEndingWith(end, relativePath);
	    
	    if( null != validUrl ){
	        //JSF 1.0 is available
	        // (note the jsf-ri-runtime.xml is available in both 1.0 and 1.1
	        // but the 1.0-only files have more detail)
	
	        // add the jsf core namespace validators and converters
	        // in the jsf core namespace
	        registerJsfFacesConfig(validUrl,
	                JSF_CORE_NAMESPACE, relativePath);
	        
	        // add the renderers to the jsf html namespace
	        relativePath = "com/sun/faces/standard-html-renderkit.xml";
	        end = location + relativePath;
	        validUrl = getFacesConfigUrlEndingWith(end, relativePath);
	        assertNotNull(relativePath, validUrl);
	        
	        registerJsfFacesConfig(validUrl,
	                JSF_HTML_NAMESPACE, relativePath);
	    }else{
	        // assume JSF 1.1
	
	        //JSF 1.1
	        relativePath = "com/sun/faces/jsf-ri-runtime.xml";
	        end = location + relativePath;
	        validUrl = getFacesConfigUrlEndingWith(end, relativePath);
	        assertNotNull(relativePath, validUrl);
	        
	        // Note, this file contains both core and html, but is
	        // registered only under html
	        // because that's were the renderers should be found.
	        
	        //register them all in the jsf html namespace
	        registerJsfFacesConfig(validUrl,
	                JSF_HTML_NAMESPACE, relativePath);
	    }
	}
	/**
	 * @param refs
	 * @param facesConfigUrl
	 * @param namespace
	 * @param relativePath
	 *            the path relative to the project. The facesConfigUrl is
	 *            expected to end with that path.
	 */
	private FacesLibraryFragment registerJsfFacesConfig(URL facesConfigUrl,
			String namespace, String relativePath) {
	
		// calculate the jar root by subtracting the relative path from the
		// configUrl
		String urlStr = facesConfigUrl.toString();
		assertTrue(urlStr.endsWith(relativePath));
		int rootIndex = urlStr.length() - relativePath.length();
		String root = urlStr.substring(0, rootIndex);
	
		// Note, this is being used to load the META-INF/faces-config.xml files
		FacesProject project = getProject(root);
		if (null == project) { // the project doesn't already exist
			project = _jsfRegistry.createProject(root);
		}
	    if (null == _facesLoader) {
	        _facesLoader = FacesClassLoaderFactory.createContext(this
	                .getClass());
	    }
	    FacesLibraryFragment file = ConfigRegisterer.getInstance().registerProjectConfig(project, _facesLoader, null, null,
	                    relativePath, facesConfigUrl, null, namespace);
	    assertNotNull(file);
	    return file;
	}
	/**
	 * Gets the project in the specified registry whose PageFileSystem has the
	 * specified id.
	 */
	private FacesProject getProject(String id){
	    for (FacesProject proj : _jsfRegistry.getProjectList()) {
	        if( id.equals( proj.getId() )){
	            return proj;
	        }
	    }
	    return null;
	}
	/**
	 * @param partial
	 *            a string found in the url of the file being searched for
	 * @param resourceRelative
	 *            the resource path and name relative to the root.
	 */
	private URL getFacesConfigUrlEndingWith(String end,
			String resourceRelative) {
		try {
			Enumeration<?> facesConfigs = FacesClassLoaderFactory
					.findContextClassLoader(this.getClass()).getResources(
							resourceRelative);
			while (facesConfigs.hasMoreElements()) {
				URL url = (URL) facesConfigs.nextElement();
				String urlPath = url.toString();
				if (urlPath.endsWith(end)) {
					return url;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			fail(ex.getMessage());
		}
		// this time don't check the container folder
		URL url = FacesClassLoaderFactory.findContextClassLoader(getClass()).getResource(resourceRelative);
		if( null != url ){
			return url;
		}
		
		//fail("No faces-config.xml file found whose path ends with the string \""
		//		+ end + "\"");
		return null;
	}
	protected static class ConverterForClassDefinition extends AbstractFacesDefinition{
        public Class<?> forClass;
        public ConverterForClassDefinition(FacesLibraryFragment file,
                Class<?> forClass, Class<?> javaClass) {
            super(file, /*id*/forClass.getName(), javaClass, null, /*referenceId*/forClass.getName());
            this.forClass = forClass;
        }
        public FacesDefinition getParent() {
            return null;
        }
        public Class<?> getForClass() {
            return forClass;
        }
        public boolean isTag() {
            return false;
        }
    }
    private class ConverterForClassAnnotater implements RegistryAnnotater{
        public void annotate(RegistryAnnotaterInfo info,
                FacesExtensibleNode parsed, Element elem) {
            if( parsed instanceof FacesLibraryFragment ){
                List<ConverterForClassDefinition> forClassDefs = createForClassDefs((FacesLibraryFragment)parsed, elem);
                if( ! forClassDefs.isEmpty() ){
                    parsed.setExtension("converter-for-class-list", forClassDefs);
                }
            }
        }
        private List<ConverterForClassDefinition> createForClassDefs(
                FacesLibraryFragment file, Element elem) {
            List<ConverterForClassDefinition> defs = new ArrayList<ConverterForClassDefinition>();
            for (Element converterElem : ElementUtil.getChildren(elem, "converter")) {
                String forNameStr = ElementUtil.extractValue(converterElem, "converter-for-class");
                if( null == forNameStr ){
                    continue;
                }
                Class<?> forNameClass = loadClass(forNameStr);
                
                String converterClassStr = ElementUtil.extractValue(converterElem, "converter-class");
                Class<?> converterClass = loadClass(converterClassStr);
                defs.add(new ConverterForClassDefinition(file, forNameClass, converterClass));
            }
            return defs;
        }
        private Class<?> loadClass(String className){
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
