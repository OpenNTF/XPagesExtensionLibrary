/*
 * © Copyright IBM Corp. 2006, 2014
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
* Date: 19-Jan-2006
*/
package com.ibm.xsp.test.framework.translator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.page.compiled.PageToClassNameUtil;
import com.ibm.xsp.page.parse.ComponentElement;
import com.ibm.xsp.page.parse.FacesDeserializer;
import com.ibm.xsp.page.parse.FacesReader;
import com.ibm.xsp.page.translator.LogicalPage;
import com.ibm.xsp.page.translator.PhysicalPage;
import com.ibm.xsp.page.translator.TranslateErrorHandler;
import com.ibm.xsp.page.translator.Translator;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestFileUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * Note, this is using private classes that are not part of the public APIs 
 * and that may change in later releases, so the implementation of this class
 * may change over time, and the classes used here may not necessarily be reused
 * in your own code. For the public APIs, see 
 * http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_Extensibility_API_Documentation
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 19-Jan-2006
 * Plugin: xsp.core.test
 *
 */
public class GeneratePagesTest extends AbstractXspTest{
    private static final int TRANSLATOR_VERSION = Translator.TRANSLATOR_VERSION;
    private FacesSharableRegistry _tagRegistry;
    private DatestampList _datestampList;
    private String[] _pageNames;
    private String[] _skipDirsFromSubclass;

    @Override
    public String getDescription() {
        return "regenerates the compiled pages used in other tests";
    }
    public class TranslatedAndSavedSignal extends RuntimeException{
        private static final long serialVersionUID = 1L;
        public TranslatedAndSavedSignal(){}
    }
    @Override
    protected void setUp() throws Exception {
        
        super.setUp();
        
        AbstractXspTest test = this;
        _pageNames = TestProject.getAllViewIds(test, TestProject.getUserDir(test));
        
        _tagRegistry = TestProject.createRegistry(test);
        
        _datestampList = DatestampList.readLastChangeDates(TRANSLATOR_VERSION);
        
        _skipDirsFromSubclass = getSkipDirs();
    }

    public void testPagesNotChanged() throws Exception {
        
        // the index to load, where -1 => load all of them
        // (change this value to debug an individual file)
        int pageToLoad = getDebugIndex(-1); //all
        // if should print to System.out
        boolean debugging = isDebugging(false);
        
        String fails = "";

        // note, if translatePages fails it will fail this test
        List<PageInfo> pages = generatePageInfos(pageToLoad);
        if(pages.isEmpty()){
            fail("No pages found to translate.");
        }
        
        // list of String pageName to the old Date of the .xsp file 
        if( _datestampList.isTranslatorVersionChanged() ){
            if( debugging ){
                System.out.println(GeneratePagesTest.class.getName()
                        + ".testPagesNotChanged() : "
                        + "Translator changed. Will attempt to regenerate all pages.");
            }
        }
        
        Map<String, Object> projectObjs = new HashMap<String, Object>();
        File userDir = TestProject.getUserDir(this);
        String absoluteDir = userDir.getAbsolutePath();
        FacesSharableRegistry registry = getRegistry();
        List<String> customPages = TestProject.getCustomControlPageNames(registry);
        
        for (PageInfo pageInfo : pages) {
            int fileIndex = pageInfo.index;
            String name = pageInfo.pageName;
            
            long xspFileDate = _datestampList.getCurrentDateLong(pageInfo.getXspFile());
            File javaFile = getJavaFile(pageInfo);
            if( pageToLoad == -1 && javaFile.exists() ){
                // check if the .xsp file's date has changed
                if( !_datestampList.isFileDatestampChanged(name, xspFileDate) ){ 
                    if( debugging  ){
                        logNoChangeInPage(pageInfo, fileIndex);
                    }
                    continue;
                }
            }
            if( debugging ){
                logGeneratingPage(pageInfo, fileIndex, _datestampList.isTranslatorVersionChanged());
            }
            
            // set the new date into the map
            _datestampList.updateTimestamp(name, xspFileDate);
            
            // translate the page
            String page;
            try{
                page = translatePage(registry, customPages, projectObjs, pageInfo, debugging && 1 == pages.size());
                pageInfo.setTranslated(page);
            }catch(TranslatedAndSavedSignal signal){
                // one of the subclasses saves the file itself. 
                String failMsg = "* " + name+" File generated, refresh the project.";
                System.err.println("[" + fileIndex + "] " + failMsg);
                fails += failMsg +"\n";
                continue;
            }catch(Exception ex){
                String msg = ex.getMessage();
                if( null == msg ){
                    msg = ex.toString();
                }
                String failMsg = "# " + pageInfo.pageName + " Problem translating file: "+msg;
                System.err.println("[" + fileIndex + "] " + failMsg);
                fails += failMsg+"\n";
                continue;
            }
            
            if( ! fileContentsChanged(javaFile, page) ){
                continue;
            }
            // output the changed file.
            boolean success = writeToFile(javaFile, page);
            if( ! success ){
                String failMsg = "# "
                        + javaFile.getAbsolutePath().substring(
                                absoluteDir.length())
                        + " Problem writing to file (Hijack?)";
                System.err.println("[" + fileIndex + "] " + failMsg);
                fails += failMsg+"\n";
            }else{
                String failMsg = "* " + name+ " File generated, refresh the project.";
                System.err.println("[" + fileIndex + "] " + failMsg);
                fails += failMsg+"\n";
            }
        }
        if( _datestampList.isSomeDatestampChangedSinceRead() ){
            _datestampList.writeLastChangeDates(
                    userDir, getGeneratedSourceFolder(),
                    TRANSLATOR_VERSION);
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkippedFails(), this, "testPagesNotChanged"));
        if ( fails.length() > 0) {
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

	/**
     * @return
     */
    protected String[] getSkippedFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }

    private void logNoChangeInPage(PageInfo pageInfo, int fileIndex) {
        System.out.println(GeneratePagesTest.class
                .getName()
                + ".testPageCompile() : "
                + "No change in ["
                + fileIndex + "] " + pageInfo.pageName);
    }

    private void logGeneratingPage(PageInfo pageInfo, int fileIndex,
            boolean translatorVersionChanged) {
        if( translatorVersionChanged ){
            System.out.println(GeneratePagesTest.class
                    .getName()
                    + ".testPageCompile() : "
                    + "Attempting to regenerate ["
                    + fileIndex + "] " + pageInfo.pageName);
        }else{
            System.out.println(GeneratePagesTest.class
                    .getName()
                    + ".testPageCompile() : "
                    + "Timestamp change in ["
                    + fileIndex + "] " + pageInfo.pageName);
        }
    }

    /**
     * Available to override in subclasses
     * @param defaultValue
     * @return
     */
    protected int getDebugIndex(int defaultValue) {
        return defaultValue;
    }

    /**
     * Available to override in subclasses
     */
    protected boolean isDebugging(boolean defaultVal) {
        return defaultVal;
    }
    /**
     * Available to be called in subclasses.
     * @return
     */
    protected DatestampList getDatestampList(){
        return _datestampList;
    }
    /**
     * Available to be called in subclasses.
     * @return
     */
    protected String[] getPageNames(){
        return _pageNames;
    }
    /**
     * Available to be called in subclasses.
     * @return
     */
    protected File toXspFile(String pageName){
        return new File(TestProject.getUserDir(this),pageName);
    }

    public static boolean writeToFile(File file, String page) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            if( ! file.canWrite() ){
                return false;
            }

            FileWriter out = new FileWriter(file);
            out.write(page);
            out.close();
            return true; //success
        } catch (IOException e) {
            e.printStackTrace();
            fail("Couldn't write to the file : "+file.getAbsolutePath());
        }
        return false;
    }
    private boolean fileContentsChanged(File javaFilePath, String pageContents) {
        if( ! javaFilePath.exists() ){
            // changed
            return true;
        }
        if( javaFilePath.isDirectory() ){
            throw new IllegalArgumentException(
                    "The java file path is a directory: "
                            + javaFilePath.getAbsolutePath());
        }
        String existing = XspTestFileUtil.readFileContents(javaFilePath);
        return ! pageContents.equals( existing );
    }
    private File getJavaFile(PageInfo pageInfo) {
        File javaFile = pageInfo.getJavaFile();
        if( null != javaFile ){
            return javaFile;
        }
        String fileName = getGeneratedSourceFolder() +
                "/"+pageInfo.getClassName().replaceAll("\\.","/")+".java";
        javaFile = new File(TestProject.getUserDir(this), fileName);
        pageInfo.setJavaFile(javaFile);
        
        return javaFile;
    }
    protected String getGeneratedSourceFolder() {
        return "gen";
    }
    protected List<PageInfo> generatePageInfos(int pageToLoad){
        String[] pageNames = getPageNames();
        int numPages = pageNames.length;
        List<PageInfo> translated = new ArrayList<PageInfo>(numPages);
        
        for (int i = 0; i < numPages; i++) {

            if( pageToLoad >= 0 && pageToLoad != i ){
                continue;
            }
            String pageName = pageNames[i];
            
            // if the file is in one of the directories to skip.
            if( isSkipped( pageName ) ){
                // skip this file
                continue;
            }
            File xspFile = toXspFile(pageName);
            String fileName = xspFile.getAbsolutePath();
            
            PageInfo pageInfo = new PageInfo(i, pageName, fileName);
            pageInfo.setXspFile(xspFile);
            pageInfo.setEnsureMarkupTags(isEnsureMarkupTags(pageName));
            translated.add(pageInfo);
        }
        return translated;
    }

    /**
     * Available to be overridden in the subclass, may throw a TranslatedAndSavedSignal.
     * @param registry 
     * @param customPages 
     * @param projectObjs
     * @param pageInfo
     * @param debugging
     *            if <code>true</code> the page will be printed to System.out
     * @return the string containing the translated page .java
     * @throws Exception
     */
    protected String translatePage(FacesSharableRegistry registry,
            List<String> customPages, Map<String, Object> projectObjs,
            PageInfo pageInfo, boolean debugging) throws Exception {
        
        String result = translatePage(registry, projectObjs, customPages,
                getApplicationVersion(), pageInfo,
                getTranslateHandler());
        if( debugging ){
            System.out.println(GeneratePagesTest.class.getName()
                    + ".testPageCompile() : \n"+result);
        }
        return result;
    }

    public static String translatePage(FacesSharableRegistry registry,
            Map<String, Object> projectObjs, List<String> customsPageNames, String applicationVersion, 
            PageInfo pageInfo, TranslateErrorHandler errHandler) throws Exception {
        // read in the persistence tree.
        File xspFile = pageInfo.getXspFile();
        
        FacesDeserializer deserial;
        boolean ensureMarkupTags = pageInfo.isEnsureMarkupTags();
        String key = ensureMarkupTags? "ensureMarkupTags" : "plainDeserial";
        if( null != projectObjs && projectObjs.containsKey(key) ){
            deserial = (FacesDeserializer) projectObjs.get(key);
    	}else{
            Map<String, Object> options = new HashMap<String, Object>();
            // allowNamespacedMarkupTags defaults to true in FacesDeserializer
            // but defaults to false in the design-time code.
            options.put(FacesDeserializer.OPTION_ALLOW_NAMESPACED_MARKUP_TAGS, ensureMarkupTags);
            deserial = new FacesDeserializer(registry, options);
    		
    		if( null != projectObjs ){
    			projectObjs.put(key, deserial);
    		}
    	}
        
        InputStream in = new BufferedInputStream(new FileInputStream(xspFile));
        ComponentElement root;
        try{
            FacesReader reader = new FacesReader(in);
            root = deserial.readRoot(reader);
        }finally{
            in.close();
        }
        assertNotNull("Unable to deserialize the file : " + xspFile, root);
        
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Translator.OPTION_APPLICATION_VERSION, applicationVersion);
        options.put(Translator.OPTION_ERROR_HANDLER, errHandler);
        if( null != projectObjs ){
        	// array copied from ControlClassUtil.initCache(Translator, Map<String, Object>)
        	String[] keys = {"text","tag","custom-base","include-page","property-map-instance"};
        	for (String transKey : keys) {
        		Object value = projectObjs.get(transKey);
        		if( value != null ){
        			options.put(transKey, (Class<?>) value);
        		}
        	}
        }
        Translator compiler = new Translator(registry, options);
        
        boolean isCustomControl = customsPageNames.contains(pageInfo.pageName);
        LogicalPage logical = new LogicalPage(pageInfo.getClassName(), pageInfo.pageName, isCustomControl);
        // note, not handling pages composed of multiple .xsp files, so just assume a single physical page:
        PhysicalPage physical = new PhysicalPage("", root,"", 0);
        logical.addMainPage(physical);
        
        // generate the .java class
        String result = compiler.translate(logical);
        return result;
    }

	/**
     * Available to override in subclasses;
     */
    protected TranslateErrorHandler getTranslateHandler() {
        return null;
    }

    /**
     * Available to override in subclasses;
     */
    protected String getApplicationVersion(){
        return null;
    }
    /**
     * Available to be called in subclass implementations of {@link #generatePageInfos(int)}
     * Should not be overridden is subclasses - override {@link #getSkipDirs()} instead.
     * @param pageName
     * @return
     */
    protected boolean isSkipped(String pageName){
        return -1 != XspTestUtil.startsWithIndex(_skipDirsFromSubclass, pageName);
    }

    /**
     * Available to override in subclasses
     * @return
     */
    protected String[] getSkipDirs() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    /**
     * Available to be called in subclass implementations of {@link #generatePageInfos(int)},
     * and available to be overridden when the .xsp files use unknown HTML markup namespaces 
     * see SPR#MKEE89JPHG - this behavior differs from the Designer .java file generation,
     * as in Designer 8.5.3 the change in behavior has not been implemented,
     * so it is as if this method always returns true.
     * @param xspFileName
     * @return
     */
    protected boolean isEnsureMarkupTags(String xspFileName){
        return false;
    }
    protected FacesSharableRegistry getRegistry() {
        return _tagRegistry;
    }
    
    @Override
    protected String[][] getExtraConfig() {
        return XspTestUtil.concat(super.getExtraConfig(), new String[][]{
            // load the xsp-config files in this project
            {"target.local.xspconfigs","true"},
        });
    }
    public static class PageInfo{
        public final int index;
        public final String pageName;
        public final String xspFileName;
        private boolean ensureMarkupTags;
        private String translated;
        private String className;
        private File xspFile;
        private File javaFile;
        private Object detail;

        /**
         * @return the javaFile
         */
        public File getJavaFile() {
            return javaFile;
        }
        /**
         * @param javaFile the javaFile to set
         */
        public void setJavaFile(File javaFile) {
            this.javaFile = javaFile;
        }
        public PageInfo(final int index, final String pageName,
                final String xspFileName) {
            this.index = index;
            this.pageName = pageName;
            this.xspFileName = xspFileName;
        }
        public File getXspFile(){
            if( null == xspFile ){
                xspFile = new File(xspFileName);
            }
            return xspFile;
        }
        public void setXspFile(File xspFile){
            this.xspFile = xspFile;
        }
        
        public boolean isEnsureMarkupTags() {
            return ensureMarkupTags;
        }
        public void setEnsureMarkupTags(boolean ensureMarkupTags) {
            this.ensureMarkupTags = ensureMarkupTags;
        }
        public String getTranslated() {
            return translated;
        }
        public void setTranslated(String translated) {
            this.translated = translated;
        }
        public String getClassName() {
            if( null == className ){
                className = PageToClassNameUtil.getClassNameForPage(pageName);
            }
            return className;
        }
        public Object getDetail() {
            return detail;
        }
        public void setDetail(Object detail) {
            this.detail = detail;
        }
        @Override
        public String toString(){
            return new StringBuffer("[").append(index).append(']').append(
                    pageName).toString();
        }
    }
}
