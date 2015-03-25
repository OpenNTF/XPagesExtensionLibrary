/*
 * © Copyright IBM Corp. 2013, 2014
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
* Date: 28-Jul-2010
*/
package com.ibm.xsp.test.framework;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.convert.Converter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.config.BootStrap;
import com.ibm.xsp.config.BootStrapFactory;
import com.ibm.xsp.context.DojoLibraryFactory;
import com.ibm.xsp.controller.FacesController;
import com.ibm.xsp.controller.FacesControllerFactory;
import com.ibm.xsp.controller.FacesControllerFactoryImpl;
import com.ibm.xsp.controller.FacesRequest;
import com.ibm.xsp.controller.FacesRequestImpl;
import com.ibm.xsp.library.ConfigFileMaintainer;
import com.ibm.xsp.library.CoreLibrary;
import com.ibm.xsp.library.DirectoryResourceBundleSource;
import com.ibm.xsp.library.FacesClassLoader;
import com.ibm.xsp.library.LibraryServiceLoader;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.page.PageExtensionUtil;
import com.ibm.xsp.page.file.DirectoryFileSystem;
import com.ibm.xsp.registry.ComponentDefinitionImpl;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesCompositeComponentDefinition;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.LibraryFragmentImpl;
import com.ibm.xsp.registry.SharableRegistryImpl;
import com.ibm.xsp.registry.config.FacesClassLoaderFactory;
import com.ibm.xsp.registry.config.ResourceBundleSource;
import com.ibm.xsp.registry.config.XspRegistryManager;
import com.ibm.xsp.registry.config.XspRegistryProvider;
import com.ibm.xsp.registry.parse.ConfigParserFactory;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.servlet.local.LocalHttpServletRequest;
import com.ibm.xsp.servlet.local.LocalHttpServletResponse;
import com.ibm.xsp.servlet.local.LocalServletContext;
import com.ibm.xsp.test.framework.render.TestControlInitializer;
import com.ibm.xsp.util.SessionUtil;
import com.sun.faces.context.FacesContextImpl;

@SuppressWarnings("deprecation")
public class TestProject {
    public static final String XSP_CORE_NAMESPACE = "http://www.ibm.com/xsp/core";

    public static FacesSharableRegistry createRegistry(AbstractXspTest test) {
        if( null != test.getTestLocalVars().get("registry") ){
        	throw new RuntimeException("Registry already exists");
        }
        FacesSharableRegistry registry;
        
        // createLocalRegistry
        // creates the registry
        ConfigFileMaintainer maintainer;
        
        // getUserDir
        if( ! ConfigUtil.isTargetLocalXspconfigs(test) ){
            // should only use the classloader from the target library
            String libraryId = ConfigUtil.getTargetLibrary(test);
            boolean isTestJsfHtml = ConfigUtil.isTestJsfHtml(test);
            boolean isTestAll = ConfigUtil.isTestAll(test);
            if( null == libraryId && !isTestJsfHtml ){
                if( !isTestAll ){
                    throw new RuntimeException("Not testing local xsp-configs, and not configured to test any library configs, so nothing to test.");
                }
                // isTestAll
                // create a registry with depending on all the libraries
                FacesSharableRegistry reg = StandardRegistryMaintainer.getStandardRegistry();
                // remove the statically cached standard reg:
                StandardRegistryMaintainer.clearStandardRegistry();
                test.getTestLocalVars().put("registry", reg);
                return reg;
            }
            
            XspRegistryManager manager = XspRegistryManager.getManager();
            
            FacesSharableRegistry libraryReg = null;
            if( null != libraryId ){
                XspRegistryProvider registryProvider = manager.getRegistryProvider(libraryId);
                if( null != registryProvider ){
                    libraryReg = registryProvider.getRegistry();
                }
                if( null == libraryReg ){
                    throw new RuntimeException("Registry not found for libraryId "+libraryId);
                }
            }
            
            FacesSharableRegistry htmlReg = handleHtmlReg(test);
            
            String id = "empty local registry";
            SharableRegistryImpl reg = new SharableRegistryImpl(id);
            reg.setRegistryType(FacesSharableRegistry.TYPE_APPLICATION);
            // register the project (before this the registry is in an invalid state) 
            reg.createProject(id);
            // add dependencies
            if( null != libraryReg ){
                for (FacesSharableRegistry libDepend : libraryReg.getDepends()) {
                    reg.addDepend(libDepend);
                }
                reg.addDepend(libraryReg);
            }
            if( null != htmlReg ){
                reg.addDepend(htmlReg);
            }
            String[] extraDepends = ConfigUtil.getExtraLibraryDependsDesignTimeNonApplication(test);
            for (String extraDependId : extraDepends) {
                XspRegistryProvider registryProvider = manager.getRegistryProvider(extraDependId);
                if( null == registryProvider ){
                    throw new RuntimeException("Registry not found for extra libraryId "+extraDependId);
                }
                FacesSharableRegistry extraReg = registryProvider.getRegistry();
                reg.addDepend(extraReg);
            }
            reg.refreshReferences();
            
            test.getTestLocalVars().put("registry", reg);
            return reg;
        }
        // else target local
        ClassLoader classLoader = test.getClass().getClassLoader();
        String dir = System.getProperty("user.dir") + "/";
        File userDir = new File(dir);
        
        File root = userDir;
        
        DirectoryFileSystem fileSystem = DirectoryFileSystem.create(root);
        
        // assume xsp-configs translated by custom.xsp-config_en.properties
        boolean extensionInPropertiesName = true;
        ResourceBundleSource bundleSource = new DirectoryResourceBundleSource(root, extensionInPropertiesName);
        
        FacesClassLoader facesClassLoader = FacesClassLoaderFactory.create(classLoader);
        
        maintainer = ConfigFileMaintainer.create( 
                new TestPageConfig(fileSystem, facesClassLoader, bundleSource));
        maintainer.init();

        registry = maintainer.getRegistry();
        
        FacesSharableRegistry riReg = handleHtmlReg(test);
        if( null != riReg && ! registry.getDepends().contains(riReg ) ){
            throw new RuntimeException("Testing local project and RI configs but local proj xsp.properties does not depend on RI library.");
        }
        
        test.getTestLocalVars().put("registry", registry);
        return registry;
    }

    private static FacesSharableRegistry handleHtmlReg(
            AbstractXspTest test) {
        FacesSharableRegistry riReg = null;
        if(ConfigUtil.isTestJsfHtml(test) ){
            XspRegistryManager manager = XspRegistryManager.getManager();
            riReg = manager.getRegistryProvider("com.ibm.xsp.core-html.library").getRegistry();
            if( null == riReg ){
                throw new RuntimeException("Could not find JSF html library registry.");
            }
            if( ConfigUtil.isTestJsfAssignCoreTagNames(test) ){
                String coreId = "com.ibm.xsp.core.library";
                FacesSharableRegistry coreReg = manager.getRegistryProvider(coreId).getRegistry();
                assignTagName(coreReg, "javax.faces.ViewRoot", "view");
                assignTagName(coreReg, "javax.faces.SelectItem", "selectItem");
                assignTagName(coreReg, "javax.faces.SelectItems", "selectItems");
            }
        }
        return riReg;
    }
    private static void assignTagName(FacesSharableRegistry coreReg,
            String componentType, String newTagName) {
        // find the component with the given compType
        ComponentDefinitionImpl comp = (ComponentDefinitionImpl) coreReg.findDef(
                        componentType);
        
        // remove it from it's fragment
        LibraryFragmentImpl frag = (LibraryFragmentImpl) comp.getFile();
        frag.remove(comp);
        
        // set it's tag-name (which changes it's id)
        comp.setTagName(newTagName);
        
        // register it again, using the new id.
        frag.register(comp);
     }
//    public static FacesSharableRegistry getTargetRegistry(AbstractXspTest test){
	//        String targetLibId = ConfigUtil.getTargetLibrary(test);
	//        if( null == targetLibId ){
	//        	throw new RuntimeException("No target library");
	//        }
	//        FacesSharableRegistry reg = getRegistry(test);
	//        for (FacesSharableRegistry depend : reg.getDepends()) {
	//			if( depend.getId().equals(targetLibId) ){
	//				return depend;
	//			}
	//		}
	//    	throw new RuntimeException("Target library registry not found for "+targetLibId);
	//    }
	//    public static FacesSharableRegistry getTargetRegistry(AbstractXspTest test){
//        String targetLibId = ConfigUtil.getTargetLibrary(test);
//        if( null == targetLibId ){
//        	throw new RuntimeException("No target library");
//        }
//        FacesSharableRegistry reg = getRegistry(test);
//        for (FacesSharableRegistry depend : reg.getDepends()) {
//			if( depend.getId().equals(targetLibId) ){
//				return depend;
//			}
//		}
//    	throw new RuntimeException("Target library registry not found for "+targetLibId);
//    }
    public static List<FacesDefinition> getDefinitions(FacesSharableRegistry reg, AbstractXspTest test){
        return getLibDefinitions(reg, test);
    }
	public static List<FacesProject> getLibProjects(FacesSharableRegistry reg, AbstractXspTest test) {
        
        List<FacesProject> projs = new ArrayList<FacesProject>();
        
        String libraryId = ConfigUtil.getTargetLibrary(test);
        if( null != libraryId ){
            boolean libraryFound = false;
            for (FacesSharableRegistry depend : reg.getDepends()) {
                if( libraryId.equals(depend.getId()) ){
                    libraryFound = true;
                    projs.addAll(depend.getLocalProjectList());
                    break;
                }
            }
            if( ! libraryFound ){
                throw new RuntimeException("library not found: "+libraryId);
            }
        }
        boolean isTestJsfHtml = ConfigUtil.isTestJsfHtml(test);
        if( isTestJsfHtml ){
            String htmlId = "com.ibm.xsp.core-html.library";
            boolean htmlFound = false;
            for (FacesSharableRegistry depend : reg.getDepends()) {
                if( htmlId.equals(depend.getId()) ){
                    htmlFound = true;
                    projs.addAll(depend.getLocalProjectList());
                    break;
                }
            }
            if( ! htmlFound ){
                throw new RuntimeException("library not found: "+htmlId);
            }
        }
        
        boolean isTargetLocalXspconfigs = ConfigUtil.isTargetLocalXspconfigs(test);
        if( isTargetLocalXspconfigs ){
            projs.addAll(reg.getLocalProjectList());
        }
        if( null == libraryId && !isTestJsfHtml && ! isTargetLocalXspconfigs ){
            if( !ConfigUtil.isTestAll(test) ){
                throw new RuntimeException("Not testing local xsp-configs, and not configured to test any library configs, so nothing to test.");
            }// else
            projs.addAll(reg.getProjectList());
        }
        
        return projs;
    }
    public static List<FacesDefinition> getLibDefinitions(FacesSharableRegistry reg, AbstractXspTest test){
        List<FacesDefinition> defs = new ArrayList<FacesDefinition>();
        for (FacesProject proj : getLibProjects(reg, test)) {
            FacesSharableRegistry subReg = (FacesSharableRegistry) proj.getRegistry();
            List<FacesDefinition> localDefs = subReg.findLocalDefs();
            defs.removeAll(localDefs);
            defs.addAll(localDefs);
        }
        return defs;
    }
    public static List<FacesDefinition> getComponentsAndComplexes(FacesSharableRegistry reg, AbstractXspTest test){
        return getLibCompComplexDefs(reg, test);
    }
    public static List<FacesDefinition> getLibCompComplexDefs(FacesSharableRegistry reg, AbstractXspTest test){
        List<FacesDefinition> defs = getDefinitions(reg, test);
        for (int i = 0; i < defs.size(); ) {
            FacesDefinition def = defs.get(i);
            if( def instanceof FacesComponentDefinition || def instanceof FacesComplexDefinition ){
                i++;
            }else{
                defs.remove(i);
            }
        }
        return defs;
    }
    @SuppressWarnings("unchecked")
    public static List<FacesComponentDefinition> getLibComponents(FacesSharableRegistry reg, AbstractXspTest test){
        List<? extends FacesDefinition> defs = getDefinitions(reg, test);
        for (int i = 0; i < defs.size(); ) {
            FacesDefinition def = defs.get(i);
            if( def instanceof FacesComponentDefinition){
                i++;
            }else{
                defs.remove(i);
            }
        }
        return (List<FacesComponentDefinition>) defs;
    }@SuppressWarnings("unchecked")
    public static List<FacesComplexDefinition> getLibComplexDefs(FacesSharableRegistry reg, AbstractXspTest test){
        List<? extends FacesDefinition> defs = getDefinitions(reg, test);
        for (int i = 0; i < defs.size(); ) {
            FacesDefinition def = defs.get(i);
            if( def instanceof FacesComplexDefinition){
                i++;
            }else{
                defs.remove(i);
            }
        }
        return (List<FacesComplexDefinition>) defs;
    }
    public static FacesContext createFacesContext(AbstractXspTest test) throws Exception{
        HttpServletRequest request = createRequest(test, "/xsp/basic.xsp", null);
        return createFacesContext(test, request);
    }
    public static FacesContext createFacesContext(AbstractXspTest test,
            HttpServletRequest request) throws Exception {
        FacesController controller = (FacesController) test.getTestLocalVars().get("controller");
        if( null == controller ){
            bootstrap(test);
            controller = (FacesController) test.getTestLocalVars().get("controller");
        }
        LocalServletContext servletContext = (LocalServletContext) test.getTestLocalVars().get("servletContext");
        
        HttpServletResponse response = new LocalHttpServletResponse(servletContext, null);
        FacesContextFactory factory1 = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        
        FacesContext context = factory1.getFacesContext(servletContext, request, response, controller.getLifecycle());
        String sessionId = request.getSession().getId();
        SessionUtil.setSessionId(context, sessionId);
        
        test.getTestLocalVars().put("facesContext", context);
        return context;
    }
    /**
     * Note, usually if extraParams are passed, they should contain ("view:_id1", ""), representing the form control.
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public static HttpServletRequest createRequest(AbstractXspTest test, String viewName, Map<String,String> extraParams) throws Exception{
        if( null == test.getTestLocalVars().get("controller") ){
            bootstrap(test);
        }
        LocalServletContext servletContext = (LocalServletContext) test.getTestLocalVars().get("servletContext");
        
        // HttpServletRequest request = (HttpServletRequest) createServletRequest();
        FacesRequestImpl request;
        try {
            request = new FacesRequestImpl(viewName, FacesRequest.GET);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
        if( null != extraParams ){
            Map<String,String> params = request.getParameters();
            params.putAll(extraParams);
        }
        
        HttpServletRequest request2 = new LocalHttpServletRequest(servletContext, request);
        return request2;
    }
    private static void bootstrap(AbstractXspTest test)
            throws Exception {
        
        //TestUtil.getUserDir()
        String dir = System.getProperty("user.dir") + "/";
        File fileSystem = new File(dir);
        // _servletContext = TestUtil.createServletContext(TestUtil.getUserDir());
        
        // the libraries that have the faces-config paths we want to register
        List<String> runtimeLibraryIds = findRuntimeLibraryIds(test);
        
        // the CLBootStrap constructor always loads all the global library faces-config paths
        List<String> unexpectedGlobals;
        List<String> globalLibraryIds = new ArrayList<String>();
        {
            for (LibraryWrapper globalLib : LibraryServiceLoader.getGlobalLibraryServices()) {
                globalLibraryIds.add(globalLib.getLibraryId());
            }
            List<String> tmp = new ArrayList<String>(globalLibraryIds);
            tmp.removeAll(runtimeLibraryIds);
            unexpectedGlobals = tmp;
        }
        if( !unexpectedGlobals.isEmpty() ){
            throw new RuntimeException("Unexpected global libraryIds " 
                    +"not configured in config.properties, but found through environment libs: "
                    +XspTestUtil.concatStrings(StringUtil.toStringArray(unexpectedGlobals)));
        }
        // the CLBootStrap will load the extra library's faces-config paths in the init(servletContext) method
        List<String> requiredExtra;
        {
            List<String> tmp= new ArrayList<String>(runtimeLibraryIds);
            tmp.removeAll(globalLibraryIds);
            requiredExtra = tmp;
        }
        String extraDepends = XspTestUtil.concatStrings(StringUtil.toStringArray(requiredExtra)).replace(" ", "");
        // String extraDepends = context.getInitParameter();//$NON-NLS-1$
        Hashtable<Object, Object> initParams = new Hashtable<Object, Object>();
        initParams.put("xsp.library.extra", extraDepends);
        LocalServletContext servletContext = new LocalServletContext(fileSystem, initParams);
        // _bootStrap = BootStrapFactory.createBootStrap();
        
        BootStrap bootStrap = BootStrapFactory.createBootStrap();
        bootStrap.init(servletContext);
        
        test.getTestLocalVars().put("bootStrap", bootStrap);
        test.getTestLocalVars().put("servletContext", servletContext);
        
        // _controller = createFacesController(_servletContext)
        FacesControllerFactory factory2;
        if( runtimeLibraryIds.contains("com.ibm.xsp.designer.library") ){
            // if using DesignerApplicationFactoryImpl (referenced from
            // the designer-faces-config.xml file.):
            
            // Set up the ApplicationFinder
            //ApplicationExecutionContext context = new ApplicationExecutionContext(
            //        testName, TestUtil.getUserDir().getAbsolutePath());
            //final Application app = new Application(context);
            //Application.setApplicationFinder(new Application.IApplicationFinder() {
            //	public Application get() {
            //		return app;
            //	}
            //});
            String testName = null;
            String userDirPath = getUserDir(test).getAbsolutePath();
            ClassLoader classLoader = test.getClass().getClassLoader();
            Class<?> appExContextClass = classLoader.loadClass(
                    "com.ibm.designer.runtime.server.util.ApplicationExecutionContext");
            Object appExContext = appExContextClass.getConstructor(
                    String.class, String.class).newInstance(testName,
                    userDirPath);
            Class<?> appClass = classLoader.loadClass("com.ibm.designer.runtime.Application");
            Class<?> appExContextInterface = classLoader.loadClass("com.ibm.designer.runtime.IAppExecutionContext");
            final Object app =appClass.getConstructor(appExContextInterface).newInstance(appExContext);
            Class<?> appFinderClass = classLoader.loadClass("com.ibm.designer.runtime.Application$IApplicationFinder");
            Object appFinder = java.lang.reflect.Proxy.newProxyInstance(classLoader, 
                    new Class[]{appFinderClass}, 
                    new InvocationHandler() {
                public Object invoke(Object proxy, Method method, Object[] args)
                        throws Throwable {
                    if( "get".equals(method.getName()) && (null == args || args.length == 0 )){
                        return app;
                    }
                    return new RuntimeException("unsupported method "+method);
                }
            });
            appClass.getMethod("setApplicationFinder", appFinderClass).invoke(null, appFinder);
            // end set up application finder.
            
            factory2 = (FacesControllerFactory) Class.forName(
                    "com.ibm.xsp.controller.DesignerFacesControllerFactory")
                    .newInstance();
        }else{
            factory2 = new FacesControllerFactoryImpl();
        }
        FacesController controller = factory2.createFacesController(servletContext);
        controller.init(servletContext);
        test.getTestLocalVars().put("controller", controller);
        
        initDojoLibraries();
    }
	/**
	 * @param test
	 * @return
	 */
	public static List<String> findRuntimeLibraryIds(AbstractXspTest test) {
		String libraryId = ConfigUtil.getTargetLibrary(test);
        List<String> libIds = new ArrayList<String>();
        
        boolean isAddAutoInstalledLibraries;
        if (ConfigUtil.isLibraryDependsRuntimeAutoInstalledSuppress(test) ){
            isAddAutoInstalledLibraries = false;
        }else if( null == libraryId ){
            isAddAutoInstalledLibraries = true;
        }else{
            // if the target library under test is one of the 
            // auto-installed and auto-depended-on libraries,
            // then do NOT automatically add include all those libraries
            // - will only use the target&depends and the explicit extra depends
            boolean isTargetLibraryAutoInstalled;
            String[] autoInstalledIds = LibraryServiceLoader.getOrderedAutoInstalledLibraryIds();
            isTargetLibraryAutoInstalled = (-1 != XspTestUtil.indexOf(autoInstalledIds, libraryId) );
            isAddAutoInstalledLibraries = ! isTargetLibraryAutoInstalled;
        }
        if( isAddAutoInstalledLibraries ){
            // first depend on the XPages runtime library faces-config files
            String[] autoInstalledIds = LibraryServiceLoader.getOrderedAutoInstalledLibraryIds();
            Collections.addAll(libIds, autoInstalledIds);
            if( null == LibraryServiceLoader.getLibrary("com.ibm.xsp.rcp.library") ){
                // The ..xsp.rcp library can be absent in the test environment
                // and it is not necessarily a problem.
                libIds.remove("com.ibm.xsp.rcp.library");
            }
            for (String autoInstalledId : libIds) {
                LibraryWrapper depend = LibraryServiceLoader.getLibrary(autoInstalledId);
                if( null == depend ){ // &&! "com.ibm.xsp.rcp.library"
                    throw new RuntimeException("autoInstalled library not found: "+autoInstalledId);
                }
            }
        } 
        
        if (null != libraryId) {
            LibraryWrapper lib = LibraryServiceLoader.getLibrary(libraryId);
            if( null == lib ){
                throw new RuntimeException(
                        "Could not find the library for target library id "
                        + libraryId + "");
            }
            // Note, DO NOT load all the global libraries
            // only the auto-installed libraries and the explicitly depended on libraries
            for (String dependId : lib.getDependencies()) {
                if( libIds.contains(dependId) ){
                    continue;
                }
                libIds.add(dependId);
                LibraryWrapper depend = LibraryServiceLoader.getLibrary(dependId);
                if( null == depend ) throw new NullPointerException();
            }
            libIds.add(libraryId);
        }
        String[] extraDependIds = ConfigUtil.getExtraLibraryDependsRuntime(test);
        for (String extraLibId : extraDependIds) {
            if( libIds.contains(extraLibId) ){
//                if( isAddAutoInstalledLibraries ){
//                    String[] autoInstalledIds = LibraryServiceLoader.getOrderedAutoInstalledLibraryIds();
//                    boolean isExtraLibraryAutoInstalled = (-1 != XspTestUtil.indexOf(autoInstalledIds, extraLibId) );
//                    if( isExtraLibraryAutoInstalled ){
//                        throw new RuntimeException("extra library depend not needed - already auto-installed: "+extraLibId);
//                    }
//                }
                continue;
            }
            libIds.add(extraLibId);
            LibraryWrapper depend = LibraryServiceLoader.getLibrary(extraLibId);
            if( null == depend ){
               throw new RuntimeException("extra library not found: "+extraLibId);
            }
        }
        if( ! libIds.contains(CoreLibrary.LIBRARY_ID) ){
            throw new RuntimeException(
                    "Core library neither used by target library nor in extra library depends list");
        }
		return libIds;
	}
    
	/**
	 * @param test
	 * @return
	 */
	public static List<String> findFacesConfigPaths(AbstractXspTest test) {
        List<String> libIds = findRuntimeLibraryIds(test);
        List<String> facesConfigPaths = new ArrayList<String>();
        for (String libId : libIds) {
            LibraryWrapper lib = LibraryServiceLoader.getLibrary(libId);
            for (String file : lib.getFacesConfigFiles() ) {
                facesConfigPaths.add(file);
            }
        }
        return facesConfigPaths;
	}
    private static boolean dojoLibrariesInitialized = false;
    public static void initDojoLibraries() {
        if( !dojoLibrariesInitialized ){
        	if(isNotesDLL())
        		dojoLibrariesInitialized=true;
        	else{
            // load the DojoLibraryFactory extension point
            DojoLibraryFactory.initializeLibraries();
            dojoLibrariesInitialized = true;
        	}
        }
    }
    public static boolean isNotesDLL(){
    	try{
    		System.loadLibrary("nlsxbe.dll");
    		return true;
    	}
    	catch (Throwable e){ 
    		return false;
    	}	
    }
    public static UIViewRoot loadEmptyPage(AbstractXspTest test, FacesContext context) throws Exception{
        String pageName = "/pages/pregenerated/empty.xsp";
        Application application = lazyApplication(test);
        ViewHandler viewHandler = application.getViewHandler();
        UIViewRoot root = viewHandler.createView(context, pageName);
        if( null == root ){
            throw new RuntimeException("JUnit test could not load the empty page "+pageName);
        }
        context.setViewRoot(root);
        return root;
    }
    private static Application lazyApplication(AbstractXspTest test) throws Exception{
        Application application;
        if( test.getTestLocalVars().containsKey("application") ){
            application = getApplication(test);
        }else{
            application = createApplication(test);
        }
        return application;
    }

    public static ApplicationEx createApplication( AbstractXspTest test) throws Exception{
        FacesController controller = (FacesController) test.getTestLocalVars().get("controller");
        if( null == controller ){
            bootstrap(test);
            controller = (FacesController) test.getTestLocalVars().get("controller");
        }
        ApplicationEx application = controller.getApplication();
        test.getTestLocalVars().put("application", application);
        return application;
    }
    public static ApplicationEx getApplication( AbstractXspTest test) {
        ApplicationEx application = (ApplicationEx) test.getTestLocalVars().get("application");
        if( null == application ){
            throw new RuntimeException("TestUtil.createApplication(AbstractXspTest) has not yet been called");
        }
        return application;
    }
//    @SuppressWarnings("unused") // suppression not needed in eclipse 3.6.0
    public static void tearDown(AbstractXspTest test){
        Map<String, Object> localVars = test.getTestLocalVars();
        FacesContext context = (FacesContext) localVars.remove("facesContext");
        if( null != context ){
            context.setViewRoot(null);
            new FacesContextImpl(){
                {
                    setCurrentInstance(null);
                }
            }.getClass(); // call getClass to prevent Object is never used compile warning
        }
        FacesController controller = (FacesController) localVars.remove("controller");
        if( null != controller){
            controller.destroy();
        }
        BootStrap bootstrap = (BootStrap) localVars.remove("bootStrap");
        if( null != bootstrap ){
            ServletContext servletContext = (ServletContext) localVars.remove("servletContext");
            bootstrap.destroy(servletContext);
            // note, if you don't destroy the bootstrap then 
            // FactoryFinder.getFactory("javax.faces.application.ApplicationFactory");
            // hangs around, so the old application instance is used in the next
            // JUnit test and the managed beans don't resolve.
        }
        FacesSharableRegistry registry = (FacesSharableRegistry) localVars.remove("registry");
        if( null != registry ){
            // prevent unused local variable warning
            registry.getClass();
        }
        ApplicationEx app = (ApplicationEx) localVars.remove("application");
        if( null != app ){
            // prevent unused local variable warning
            app.getClass();
        }
        TestFrameworkPlatform platform = (TestFrameworkPlatform) localVars.remove("platform");
        if( null != platform ){
            // revert to the previous type of platform - stop using a TestFrameworkPlatform
            Class<?> defaultPlatformClass = (Class<?>) localVars.remove("platformShadowedClass");
            try{
                defaultPlatformClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Problem restoring the default platform.", e);
            }
        }
    }
    public static void initPlatform(AbstractXspTest test, File dominoInstallLocation){
        Map<String, Object> localVars = test.getTestLocalVars();
        
        if( null != localVars.get("platform") ){
            throw new RuntimeException("Platform already exists");
        }
        
        Platform defaultPlatformInstance = Platform.getInstance();
        Class<?> defaultPlatformClass = defaultPlatformInstance.getClass();
        // verify public zero-arg constructor, so will be able to recreate later
        try {
            defaultPlatformClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Will not be able to re-set the platform to a "+defaultPlatformClass, e);
        }
        
        TestFrameworkPlatform platform = new TestFrameworkPlatform(dominoInstallLocation);
        // verify the platform constructor initialized itself as *the* platform
        if( platform != Platform.getInstance() ){
            throw new RuntimeException("Problem setting TestFrameworkPlatform");
        }
        localVars.put("platform", platform);
        localVars.put("platformShadowedClass", defaultPlatformClass);
    }
    public static FacesSharableRegistry getRegistry(AbstractXspTest test){
        FacesSharableRegistry reg = (FacesSharableRegistry) test.getTestLocalVars().get("registry");
        if( null == reg ){
            throw new RuntimeException("TestUtil.createRegistry(AbstractXspTest) has not yet been called");
        }
        return reg;
    }
    /**
     * @param test
     * @param annotator
     * @return
     */
    public static FacesSharableRegistry createRegistryWithAnnotater(
            AbstractXspTest test, RegistryAnnotater ... annotaters) {
        FacesSharableRegistry reg;
        // since need to re-parse so the annotater is used, 
        // remove any already-parsed library registries:
        XspRegistryManager.initManager(null, /*discard*/true);
        if( null != annotaters ){
            for (RegistryAnnotater annotator : annotaters) {
                ConfigParserFactory.addAnnotater(annotator);
            }
        }
        try{
            reg = createRegistry(test);
        }finally{
            if( null != annotaters ){
                for (RegistryAnnotater annotator : annotaters) {
                    ConfigParserFactory.removeAnnotater(annotator);
                }
            }
        }
        return reg;
    }

    public static File getUserDir(AbstractXspTest test){
        String dir = System.getProperty("user.dir") + "/";
        File userDir = new File(dir);
        return userDir;
    }
    public static UIViewRoot loadView(AbstractXspTest test,
            FacesContext context, String viewId) throws Exception{
        
        String pageName = toPageName(viewId);
        Application application = lazyApplication(test);
        ViewHandler viewHandler = application.getViewHandler();
        UIViewRoot root = viewHandler.createView(context, pageName);
        if( null != root ){
            context.setViewRoot(root);
        }
        return root;
    }
    public static String toPageName(String viewName){
        if( null == viewName ){
            return null;
        }
        // /pages/basic -> /pages/basic.xsp
        viewName = PageExtensionUtil.endWithExtension( viewName );
        // pages/basic.xsp -> /pages/basic.xsp
        if( viewName.charAt(0) != '/' ){
            viewName = '/'+viewName;
        }
        return viewName;
    }
    /**
     * List all .xsp files in the test's project/plugin, using '/' instead of '\'.
     * @param test
     * @return
     */
    public static String[] getAllViewIds(AbstractXspTest test) {
        return getAllViewIds(test, getUserDir(test));
    }
    /**
     * List all .xsp files in the test's project/plugin, using '/' instead of '\'.
     * @param test
     * @return
     */
    public static String[] getAllViewIds(AbstractXspTest test, File userDir) {
        
        FilenameFilter filter =  new FilenameFilter() {
            public boolean accept(File parent, String fileName) {
                return fileName.endsWith(".xsp");
            }
        };
        List<String> fileAbsPaths = new ArrayList<String>();
        addFilePaths(userDir, fileAbsPaths, filter);
        
        String prefix = userDir.getAbsolutePath();
        
        List<String> viewIds = new ArrayList<String>();
        for (String absPath : fileAbsPaths) {
            String relativePath = absPath.substring(prefix.length());
            relativePath = relativePath.replace('\\', '/');
            
            viewIds.add(relativePath);
        }
        return StringUtil.toStringArray(viewIds);
    }
    private static FileFilter DIRECTORY_FILTER;
    private static void addFilePaths(File baseDir, List<String> fileAbsPaths, FilenameFilter filter) {
        // add the .xsp files to the List
        File[] files = baseDir.listFiles(filter);
        if( null != files ){
            for (File file : files) {
                fileAbsPaths.add(file.getAbsolutePath());
            }
        }
        // recurse through the folder
        if( null == DIRECTORY_FILTER ){
            DIRECTORY_FILTER = new FileFilter(){
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            };
        }
        File [] dirs = baseDir.listFiles(DIRECTORY_FILTER);
        if( null != dirs ){
            for (File dir : dirs) {
                addFilePaths( dir, fileAbsPaths,  filter );
            }
        }
    }

    /**
     * Note, the parameter registry should be created using 
     * {@link AbstractXspTest#getExtraConfig()} including
     * <pre>{"target.local.xspconfigs", "true"},</pre> 
     * so that TestProject.createRegistry should include local xsp-configs.
     * @param reg
     * @return
     */
    public static List<String> getCustomControlPageNames(FacesSharableRegistry reg) {
        List<String> ccPageNames = new ArrayList<String>();
        for (FacesCompositeComponentDefinition def : reg.findCompositeLocalDefs()) {
            ccPageNames.add(def.getCompositeFile());
        }
        return ccPageNames;
    }

    /**
     * This is only called during {@link #loadEmptyPage(AbstractXspTest, FacesContext)}. 
     * @param context
     * @return
     */
    public static UIViewRoot createViewRootObject(FacesContext context) {
        UIViewRoot result;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if( null == classLoader ) classLoader = TestProject.class.getClassLoader();
        
        // if the META-INF/extsn-faces-config.xml file is loaded
        Converter dateConverter = context.getApplication().createConverter(Date.class);
        
        boolean isXspDateTimeConverter;
        try{
            String xspDateTimeConverterClassName = "com.ibm.xsp.convert.DateTimeConverter";
            Class<?> xspDateTimeConverterClass = classLoader.loadClass(xspDateTimeConverterClassName);
            isXspDateTimeConverter = xspDateTimeConverterClass.isAssignableFrom(dateConverter.getClass());
        } catch (ClassNotFoundException e) {
            isXspDateTimeConverter = false;
        }
        if( isXspDateTimeConverter ){
            try{
                String rootEx2ClassName = "com.ibm.xsp.component.UIViewRootEx2";
                Class<?> rootEx2Class = classLoader.loadClass(rootEx2ClassName);
                result = (UIViewRoot) rootEx2Class.newInstance();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }else{
            result = new UIViewRoot();
        }
        return result;
    }
    public static File detectDominoInstallLocation(AbstractXspTest test){
        if( null != test.getTestLocalVars().get("dominoInstallLocation") ){
            throw new RuntimeException("dominoInstallLocation already exists");
        }
        String[] dominoSearchLocations;
        String[] defaultDominoSearchLocations = new String[]{
                "c:\\Domino",
                "C:\\IBM\\Domino",
                "C:\\Program Files\\IBM\\Domino",
                "C:\\Program Files\\IBM\\Lotus\\Domino",
                "C:\\Program Files (x86)\\IBM\\Domino",
                "C:\\Program Files (x86)\\IBM\\Lotus\\Domino",
                "/opt/ibm/lotus", /*yeah, the folder name is lotus*/
                "/opt/lotus", 
        };
        String[] configuredDominoSearchLocations = ConfigUtil.getDominoSearchLocations(test);
        if( null == configuredDominoSearchLocations ){
            dominoSearchLocations = defaultDominoSearchLocations;
        }else{
            String combineStrategy = ConfigUtil.getDominoSearchLocationsStrategy(test);
            if( "concat".equals(combineStrategy) ){
                dominoSearchLocations = XspTestUtil.concat(configuredDominoSearchLocations, defaultDominoSearchLocations);
            }else{ // "override"
                dominoSearchLocations = configuredDominoSearchLocations;
            }
        }
        
        File dominoFolder = null;
        for (String path : dominoSearchLocations) {
            File possibleFolder = new File(path);
            if( possibleFolder.exists() ){
                dominoFolder = possibleFolder;
                break;
            }
        }
        if( null == dominoFolder ){
            String paths = StringUtil.concatStrings(dominoSearchLocations, /*separator*/',', /*trim*/false);
            throw new RuntimeException("Cannot find any of the suggested Domino install folders: "+paths);
        }
        test.getTestLocalVars().put("dominoInstallLocation", dominoFolder);
        return dominoFolder;
    }
    public static File getDominoInstallLocation(AbstractXspTest test){
        File dominoFolder = (File) test.getTestLocalVars().get("dominoInstallLocation");
        if( null == dominoFolder ){
            throw new RuntimeException("TestUtil.detectDominoInstallLocation(AbstractXspTest) has not yet been called");
        }
        return dominoFolder;
    }
    @SuppressWarnings("unchecked")
    public static List<TestControlInitializer> getControlInitializerList(AbstractXspTest test){
        List<TestControlInitializer> list = (List<TestControlInitializer>) 
                test.getTestLocalVars().get("controlInitializerList");
        if( null != list ){
            return list;
        }
        list = new ArrayList<TestControlInitializer>();
        if( ! ConfigUtil.isPreventServiceControlInitializers(test) ){
            ClassLoader classLoader = test.getClass() .getClassLoader();
            List<TestControlInitializer> serviceList = loadService(TestControlInitializer.class, classLoader);
            for (TestControlInitializer initializer : serviceList) {
                list.add(initializer);
            }
        }
        if( test instanceof TestControlInitializer ){
            list.add(0, (TestControlInitializer) test);
        }
        
        test.getTestLocalVars().put("controlInitializerList", list);
        return list;
    }
    @SuppressWarnings("unchecked")
    private static <T> List<T> loadService(Class<T> type, ClassLoader classLoader){
        // Re-implementation of the Java6 method
        // java.util.ServiceLoader.load(Class)
        String serviceName = type.getName();
        
        String path = "META-INF/services/"+serviceName;
        Enumeration<URL> fileUrls;
        try {
            fileUrls = classLoader.getResources(path);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        
        if( !fileUrls.hasMoreElements() ){
            return Collections.emptyList();
        }
        ArrayList<T> list = new ArrayList<T>();
        for(Enumeration<URL> i = fileUrls; i.hasMoreElements(); ){
            URL url = i.nextElement();
            String fileContents = XspTestFileUtil.readFileContents(url);
            String[] classNames = StringUtil.splitString(fileContents, '\n', /*trim*/true);
            for (String className : classNames) {
                if( StringUtil.isEmpty(className) ){
                    continue;
                }
                try {
                    Class<? extends T> classObj = (Class<? extends T>) classLoader.loadClass(className);
                    T instance = classObj.newInstance();
                    list.add(instance);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }
}
