
Maire Kehoe 2011-05-13

This ..xsp.test plugin is to enable JUnit testing an XPages library of controls.
It contains
- a set of generic tests that should be applied to your XPage library,
  e.g. verifying the xsp-config files parse without errors, 
    verifying there are set* methods corresponding to the <property> 
     declarations, 
    verifying the control instances can be created 
     and serialized (implement StateHolder correctly)
- a utility to do a "good enough" generation of .java files from .xsp files
    ["good enough" - the .java file generation is not exactly the same
     as in Domino Designer; the Domino Designer builder does extra checking 
     for error conditions to prevent compiling, where these utilities 
     will generate .java files instead of failing with errors. 
     e.g. Designer will check that required properties 
      are present in the .xsp; these utils do not,
     Also Designer will look into "javascript" computed values 
     and check for JS syntax errors; these utils do not.]
    [As yet there is no publically available way 
     to do a .java file generation using the actual Designer builder.
     There have been internal prototypes of a Designer "headless build",
     see this doc and search for "headless": 
     https://www-304.ibm.com/support/docview.wss?uid=swg21454387
     ]
- utilities for creating the control tree 
    and rendering the HTML corresponding to .xsp files.
   These, with the .java file generation, allow you to include .xsp files 
   within your test plugin, and to verify they render as expected, and to 
   fake some POST request to verify values are updated/saved correctly 
   and actions behave correctly.
   There are limitations there, in that there is no connection to a Domino
   instance, so your tests tend to have to save to viewScope variables
   instead of to document fields. So, some features of the View Panel, 
   and other Domino data source dependant controls, cannot be tested 
   with this framework - feel free to add support for Domino data sources.

To create a project using this plugin, to test a library plugin "com.example"
- Create a new plugin named xsp.example.test.
- Edit the Manifest to add dependancies on your "com.example" plugin.
- Create a new package "xsp.example.test".
- In the package, create a new Java Class, ExampleTestSuite.
- Copy in the contents of the SampleTestSuite, rename to match your class.
 It does not compile, as the "Test" and "TestSuite" classes cannot be resolved.
- In the Manifest, add a plugin dependancy on "org.junit" (probably version 3.8.2)
 Your ExampleTestSuite should now compile.
- Right-click on ExampleTestSuite, Run As, JUnit Test.
 It fails with:
 java.lang.NoClassDefFoundError: org/eclipse/core/runtime/CoreException
- In the Manifest, add a plugin dependancy on "org.eclipse.core.runtime"
- Run ExampleTestSuite again (using the Run button in the toolbar)
 The first test fails with:
 junit.framework.AssertionFailedError: No pages found to translate.
     at com.ibm.xsp.test.framework.translator.GeneratePagesTest.testPagesNotChanged(GeneratePagesTest.java:102)
 Ignore that test for a few moments.
- The 2nd test fails with:
  java.lang.RuntimeException: Not testing local xsp-configs, and not configured to test any library configs, so nothing to test.
      at com.ibm.xsp.test.framework.TestProject.createRegistry(TestProject.java:102)
      at com.ibm.xsp.test.framework.lifecycle.RegisteredDecodeTest.testDecodeRegisteredComponents(RegisteredDecodeTest.java:60)
 You need to configure a config.properties file pointing to the library.
- In your plugin, create the package "com.ibm.xsp.test.framework" (exactly that, 
   don't rename it to your plugin name.)
- In that package, create a file config.properties with the contents:
    # The XspLibrary.getLibraryId() value of the library 
    # whose contents should be tested, defaults to none, 
    # meaning that only local xsp-configs are loaded.
    target.library=com.example.library
  (without the leading whitespace). This file format is described 
  in the config.properties file in the ..xsp.test plugin.
- Change the value in the file to match your library name.
- Re-run the test suite. If the 2nd test is still giving the same error
  you may have an issue with your classpath dependancy ordering
  (verify by debugging to see that it is loading the config.properties
  in the ..xsp.test plugin.)
- In the Package Explorer view, click on the View Menu 
  (a little down triangle to the right of the "Package Explorer" view name)
  select "Filters", and uncheck the checkbox beside ".* resources"
 The .classpath and .project files in the plugin become visible.
- Open the .classpath file, and re-order the contents so the "src" folder
  entry is before the requiredPlugins entry, like:
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path="src"/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.6"/>
	<classpathentry kind="con" path="org.eclipse.pde.core.requiredPlugins"/>
	<classpathentry kind="output" path="bin"/>
</classpath>
- Save and rerun the test suite.
 The 2nd JUnit test now fails with:
   java.lang.ClassNotFoundException: com.ibm.commons.xml.XResult 
- Edit the mainfest to add a dependancy on:
    com.ibm.commons.xml
- Run the JUnit tests again. 
The 2nd test passes.
- The console contains the following in red (printed to System.err):
16-May-2011 11:04:10 com.ibm.xsp.library.ConfigFileMaintainerImpl createPrivateRegistry
WARNING: CLFAD0128W: The registry for the application C:\z\extlib853\c\workspaceC\xsp.example.test cannot depend on the library with id com.ibm.xsp.domino.library, because there is no such library.
16-May-2011 11:04:10 com.ibm.xsp.library.ConfigFileMaintainerImpl createPrivateRegistry
WARNING: CLFAD0128W: The registry for the application C:\z\extlib853\c\workspaceC\xsp.example.test cannot depend on the library with id com.ibm.xsp.extsn.library, because there is no such library.
16-May-2011 11:04:10 com.ibm.xsp.library.ConfigFileMaintainerImpl createPrivateRegistry
WARNING: CLFAD0128W: The registry for the application C:\z\extlib853\c\workspaceC\xsp.example.test cannot depend on the library with id com.ibm.xsp.designer.library, because there is no such library.
16-May-2011 11:04:10 com.ibm.xsp.library.ConfigFileMaintainerImpl createPrivateRegistry
WARNING: CLFAD0128W: The registry for the application C:\z\extlib853\c\workspaceC\xsp.example.test cannot depend on the library with id com.ibm.xsp.rcp.library, because there is no such library.
- The project has not been defined with a list of dependancy libraries,
  so it is attempting to depend on the XPages runtime libraries,
  but none of those plugins are listed in the Manifest file.
- In the Manifest, add dependancies on the XPages runtime plugins:
       com.ibm.xsp.core
       com.ibm.xsp.designer
       com.ibm.xsp.domino
       com.ibm.xsp.extsn
       com.ibm.xsp.rcp
- If the 2nd test fails with:
com.ibm.xsp.test.framework.lifecycle.RegisteredDecodeTest
testDecodeRegisteredComponents(com.ibm.xsp.test.framework.lifecycle.RegisteredDecodeTest)
com.ibm.xsp.FacesExceptionEx: java.lang.NoClassDefFoundError: lotus/domino/NotesException
	at com.ibm.xsp.config.CLBootStrap.initContext(CLBootStrap.java:89)
	at com.ibm.xsp.config.BootStrap.init(BootStrap.java:82)
	at com.ibm.xsp.test.framework.TestProject.bootstrap(TestProject.java:405)
	at com.ibm.xsp.test.framework.TestProject.createRequest(TestProject.java:368)
	at com.ibm.xsp.test.framework.TestProject.createFacesContext(TestProject.java:340)
	at com.ibm.xsp.test.framework.lifecycle.RegisteredDecodeTest.testDecodeRegisteredComponents(RegisteredDecodeTest.java:77)
Caused by: java.lang.NoClassDefFoundError: lotus/domino/NotesException
- Edit your JRE configuration to add the Notes.jar and njempcl.jar as follows:
  In eclipse, menu, Window, Preferences, Java, Installed JREs, select the JRE and click Edit.
  Add External JARs, browse to C:\Notes\jvm\lib\ext\, and select both .jars:
   "njempcl.jar" "Notes.jar" 
  OK, Finish, OK. Wait for it to finish compiling, rerun the JUnit tests.
- If the 2nd test now fails with:
java.lang.ClassNotFoundException: com.ibm.domino.napi.NException
- In the Manifest, add a dependancy on com.ibm.domino.napi
  Rerun the tests
- If the 2nd test now fails with:
java.lang.NoClassDefFoundError: com/ibm/designer/runtime/domino/adapter/util/PageNotFoundException
- In the Mainfest add a dependancy on com.ibm.domino.xsp.adapter
  Rerun the tests.
- The 2nd test passes, but the console still contains:
java.lang.NoClassDefFoundError: com/ibm/designer/runtime/domino/bootstrap/BootstrapEnvironment
	at com.ibm.domino.xsp.module.nsf.platform.Factory.createPlatform(Factory.java:34)
- In the Mainfest add a dependancy on com.ibm.domino.xsp.bootstrap
- The Console still contains:
java.lang.UnsatisfiedLinkError: no nlsxbe in java.library.path
	at java.lang.ClassLoader.loadLibrary(Unknown Source)
- You can ignore that.
- Now lets look at that first JUnit fail:
com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest
testPagesNotChanged(com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest)
junit.framework.AssertionFailedError: No pages found to translate.
	at com.ibm.xsp.test.framework.translator.GeneratePagesTest.testPagesNotChanged(GeneratePagesTest.java:102)
- That fail indicates that there are no test .xsp files within the test project
- At the project root, create a folder named "pages" (just a convention - it will actually search through all folders),
and create a .xsp file, containing a simple XPage that uses your control, like:
<?xml version="1.0" encoding="UTF-8"?>
<xp:view xmlns:xp="http://www.ibm.com/xsp/core" xmlns:eg="http://example.com/xsp/control">
	<eg:exampleControl/>
</xp:view>
- Re-run the test suite. Now the test fails with an unknown namespace problem:
com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest
testPagesNotChanged(com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest)
junit.framework.AssertionFailedError: 1 fail(s). :
[0] # /pages/simpleTestOfExampleControl.xsp Problem translating file: Unknown component namespace for the tag eg:exampleControl, with the namespace http://example.com/xsp/control.
	at com.ibm.xsp.test.framework.translator.GeneratePagesTest.testPagesNotChanged(GeneratePagesTest.java:200)
- That problem is occurring because the translator doesn't know that this
  test project depends on the library (the config.properties dependancy above
  is only used by the tests, it is not passed to the design-time code in the translator.
- Configure an xsp.properties file listing the libraries 
  this test project depends on (equivalent to changing an application's 
  Application Properties, Advanced tab, XPages libraries list):
  At the project root, create a folder "WEB-INF" 
  and create xsp.properties file with the contents 
  (the ..xsp.core library is always required):
xsp.library.depends=\
            com.ibm.xsp.core.library,\
            com.ibm.xsp.extsn.library,\
            com.ibm.xsp.designer.library,\
            com.ibm.xsp.domino.library,\
            com.example.library
- Now re-run the test suite. It now fails with:
com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest
testPagesNotChanged(com.ibm.xsp.test.framework.translator.BaseGeneratePagesTest)
junit.framework.AssertionFailedError: 1 fail(s). :
[0] * /pages/simpleTestOfExampleControl.xsp File generated, refresh the project.
	at com.ibm.xsp.test.framework.translator.GeneratePagesTest.testPagesNotChanged(GeneratePagesTest.java:200)
- That BaseGeneratePagesTest is not really a unit test, it is a utility class to
  generate the .java file corresponding to each .xsp file. When running the test suite,
  you must first generate the .java files, then refresh the project so that eclipse
  detects and compiles the .java files to .class files, then run the main test suite.
  The BaseGeneratePagesTest does actually function as a unit test, to remind you that
  you have forgotten to generate the .java files, so the .class files will not be available
  so any tests that attempt to load and render the .xsp pages will fail.
- It is best to make a subclass of BaseGeneratePagesTest, to make it easier 
  to run .java file generation outside of running the entire test suite.
- Create a ..translator package in your test project, and create a class
  ExampleGeneratePagesTest extending BaseGeneratePagesTest.
  Right-click on that class, Run As, JUnit test.
- Refresh the project, you will see a new gen/ folder created at the project root.
  You should configure that to be treated as a Java Source Folder, 
  so the contents are compiled.
- Right-click on the project, Properties, Java Build Path, first tab, Add Folder, 
  select the "gen" folder, OK, OK.
  You will see the gen/ source folder contains a translation.properties file,
  and a xsp.pages package with one .java file per .xsp file.
  If you create subfolders under your pages/ folder, there will be subpackages 
  under that package. 
  Do not check in the contents of the gen/ folder to source control, 
  though you can check in the gen/ folder itself.
- Run the *GeneratePages* test, refresh the project, run the test suite. 
  The first test will pass.
- This test will be failing:
com.ibm.xsp.test.framework.serialize.BaseViewSerializeTest
testAllViews(com.ibm.xsp.test.framework.serialize.BaseViewSerializeTest)
junit.framework.AssertionFailedError: 1 fail(s). :
Failed on view [0] /pages/simpleTestOfExampleControl.xsp  with: /pages/simpleTestOfExampleControl UIViewRootEx2.getFacetsAndChildren()[0] get method should not return a UIComponent
	at com.ibm.xsp.test.framework.serialize.ViewSerializeTest.testAllViews(ViewSerializeTest.java:151)
- That test creates the control tree for each .xsp file, 
  serializes and restores the tree and verifies the restored control tree
  matches the original tree.
- Create a new ..serialize package, and create an ExampleViewSerializeTest, 
  extending from BaseViewSerializeTest.
- In ExampleTestSuite, change the reference to BaseViewSerializeTest 
  to refer to the new test. 
- Edit the new test to add the method:
    @Override
    protected Object[][] getCompareSkips() {
        Object[][] skips = super.getCompareSkips();
        skips = XspTestUtil.concat(skips, getCompareSkips_UIComponent());
        return skips;
    }
- Rerun the test suite. It should probably pass.
- This test will be failing:
com.ibm.xsp.test.framework.registry.BaseNamingConventionTest
testNamingConventions(com.ibm.xsp.test.framework.registry.BaseNamingConventionTest)
java.lang.RuntimeException: Prefix not found in config.properties, like: NamingConvention.package.prefix=com.example.foo
	at com.ibm.xsp.test.framework.registry.NamingConventionTest.testNamingConventions(NamingConventionTest.java:91)
- In the test project config.properties file add the lines:
# Package name and component-type prefix, like "com.ibm.xsp" or 
# "com.ibm.xsp.extlib", used in the NamingConventionTest
#NamingConvention.package.prefix=
NamingConvention.package.prefix=com.example
- Rerun - the test still fails 
  because the control does not match the naming convention - it may be 
  that a test subclass is needed to change the convention tested.
- The build.properties file in the test project contains:
Description	Resource	Path	Location	Type
gen/ is missing from source..	build.properties	/xsp.example.test	line 1	Plug-in Problem
- Open the file, click on the warning, choose the action "Add gen/ to the source.. build entry"
The first line becomes:
source.. = src/,\
           gen/
- Run the test suite.
  There are still fails. 
- Either fix all the fails, or save a list of the failures, 
  and compare when next running the test, to ensure no further fails appear.
  In the eclipse JUnit view, right-click, Copy Failure List, paste into a .txt file for later comparisons.
- After following these instructions the remaining fails are:
2011-05-19 This test suite is failing with 0 Errors & 3 Failures. 

Example2TestSuite
xsp.example2.test.Example2TestSuite
com.ibm.xsp.test.framework.registry.BaseComponentRendererTest
testComponentRenderers(com.ibm.xsp.test.framework.registry.BaseComponentRendererTest)
junit.framework.AssertionFailedError: 3 fail(s). :
META-INF/exampleControl.xsp-config eg:exampleControl Mismatch <component-family> != getFamily() for ExampleControl. Expected com.example.examplecontrol, was >null</component-family>
META-INF/exampleControl.xsp-config eg:exampleControl Mismatch xsp-config<renderer-type> != getRendererType() for ExampleControl. Expected com.example.examplecontrol, was >null</renderer-type>
META-INF/exampleControl.xsp-config eg:exampleControl Problem RENDERER_TYPE was null for ExampleControl It is required since 8.5.3 to allow the rendererType to be set in a theme file.
	at junit.framework.Assert.fail(Assert.java:47)
	at com.ibm.xsp.test.framework.registry.ComponentRendererTest.testComponentRenderers(ComponentRendererTest.java:112)

com.ibm.xsp.test.framework.registry.BaseComponentTypeTest
testComponentType(com.ibm.xsp.test.framework.registry.BaseComponentTypeTest)
junit.framework.AssertionFailedError: 1 fail(s). :
No constant COMPONENT_FAMILY com.example.examplecontrol in ExampleControl
	at junit.framework.Assert.fail(Assert.java:47)
	at com.ibm.xsp.test.framework.registry.ComponentTypeTest.testComponentType(ComponentTypeTest.java:112)

com.ibm.xsp.test.framework.registry.BaseNamingConventionTest
testNamingConventions(com.ibm.xsp.test.framework.registry.BaseNamingConventionTest)
junit.framework.AssertionFailedError: 2 fail(s). :
META-INF/exampleControl.xsp-config/eg:exampleControl [Rule3] Bad class short name ExampleControl does not begin with Xsp
META-INF/exampleControl.xsp-config/eg:exampleControl [Rule7] Class and type short names do not match: component-class com.example.component.ExampleControl[ExampleControl] != component-type com.example.examplecontrol[examplecontrol]
	at junit.framework.Assert.fail(Assert.java:47)
	at com.ibm.xsp.test.framework.registry.NamingConventionTest.testNamingConventions(NamingConventionTest.java:269)

- A copy of the test project & library built while writing this doc is availabe in the DDD db.


