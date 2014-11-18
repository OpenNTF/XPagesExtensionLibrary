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
* Date: 27 Apr 2011
* ViewSerializeTest.java
*/
package com.ibm.xsp.test.framework.serialize;

import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.page.translator.JavaUtil;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ViewSerializeTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that all view trees for .xsp files in the current project can be serialized";
    }
    protected String[] getSkipDirs() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected Object[][] getCompareSkips() {
        return XspTestUtil.EMPTY_OBJECT_ARRAY_ARRAY;
    }
    public void testAllViews() throws Exception{
        
        ApplicationEx application = TestProject.createApplication(this);
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        String[] views = TestProject.getAllViewIds(this);
        
        String fails = "";
        
        // index of the 1 view to check, for debugging purposes.
        int toCheck = getDebuggingIndex(-1);
        
//        String[] skipFails = getSkipFails();
        Object[][] compareSkips = getCompareSkips();
        
        for(int i=0; i<views.length; i++) {
            String viewId = views[i];
            if(isSkipFile(viewId) || isCustomControlPage(reg, viewId) ) {
                continue;
            }
            if( -1 != toCheck && i != toCheck ){
                continue;
            }
            System.out.println("ViewSerializeTest ["+i+"]"+viewId);
            
            StringBuffer saveErr = new StringBuffer();
            Throwable saveEx = null;
            boolean creatingView = false;
            boolean saveAndRestoringView = false;
            try {
                // create the view initially
                creatingView = true;
                FacesContext context = TestProject.createFacesContext(this);
                UIViewRoot root = TestProject.loadView(this, context, viewId);
                creatingView = false;
                
                // save and restore the view
                saveAndRestoringView = true;
                ReflectionCompareSerializer serializer = new ReflectionCompareSerializer(compareSkips);
                serializer.init(application, root, context, context);
                initSerializer(serializer);
                serializer.setCompareOnRestore(false); // will invoke compareRoot directly
                
                UIViewRoot restored = serializer.saveAndRestore();
                saveAndRestoringView = false;
                
                // compare the initial view and the restored view
                String viewFails = serializer.compareRoot(restored);
                saveErr.append(viewFails);
            }
            catch(Throwable e) {
                String eStr = e.toString();
                if( -1 != eStr.indexOf('\n') ){
                    eStr = JavaUtil.toJavaString(eStr);
                    eStr = eStr.substring(1, eStr.length() - 1);
                }
                if( creatingView ){
                    eStr = "Problem creating view: "+eStr;
                }else if( saveAndRestoringView ){
                    eStr = "Problem doing saveAndRestore view: "+eStr;
                }else{
                    eStr = "Problem comparing restored view: "+eStr;
                }
                saveErr.append(eStr);
                // only printStackTrace when not skipped:
                saveEx = e;
            }
            if( saveErr.length() > 0 ){
                String saveErrStr = saveErr.toString();
                if( '\n' == saveErrStr.charAt(saveErrStr.length()-1) ){
                    saveErrStr = saveErrStr.substring(0, saveErrStr.length() - 1);
                }
                String[] errArr = StringUtil.splitString(saveErrStr, '\n');
                for (String err : errArr) {
                    String viewErr = viewId + "  with: " + normalizeExMsg(err);
                    
//                    if( ! skip(skipFails, viewErr) ){
                        String printMsg = "Failed on view [" +i+"] " + viewErr;
                        System.err.println(printMsg);
                        if( null != saveEx ){ 
                            saveEx.printStackTrace();
                        }
                        fails += viewErr+"\n";
                    }
//                }
            }
        }
        
        fails += SerializationCompareContext.getUnusedFailList(compareSkips);
        if( -1 != toCheck ){
            fails += "Debugging page [" +toCheck+"]\n";
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testAllViews"));
        
        if(fails.length()>0){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected int getDebuggingIndex(int defaultValue) {
        // available to be overridden in subclasses when debugging a specific page
        return defaultValue;
    }
    
    protected void initSerializer(ReflectionCompareSerializer serializer) {
        // Available to override in subclasses.
    }
    
    @Override
    protected String[][] getExtraConfig() {
        return XspTestUtil.concat(super.getExtraConfig(), new String[][]{
            {"target.local.xspconfigs", "true"}, // TestProject.createRegistry should include local xsp-configs
        });
    }

    private List<String> ccPageNames;
    private boolean isCustomControlPage(FacesSharableRegistry reg, String viewId) {
        if( null == ccPageNames ){
            ccPageNames = TestProject.getCustomControlPageNames(reg);
        }
        return ccPageNames.contains(viewId);
    }

    /**
     * Available to extend in subclasses, used to normalize subsections like
     *  "java.util.Timer@3ba43ba4"
     * and 
     *  "java.util.Timer@7360736"
     * to both being the normalized form:  
     *  "java.util.Timer@????"
     * @param msg
     * @return
     */
    protected String normalizeExMsg(String msg) {
        return msg;
    }

    private String[] skipDirs;
    private boolean isSkipFile(String fileName) {
        if( null == skipDirs){
            skipDirs = getSkipDirs();
        }
        return isSkipped(fileName, skipDirs);
    }
    private static boolean isSkipped(String fileName, String[] skipDirs) {
        fileName = fileName.replaceAll("\\\\", "/");
        for (int j = 0; j < skipDirs.length; j++) {
            String skipDir = skipDirs[j];
            if( fileName.startsWith(skipDir) ){
                return true;
            }
            if (skipDir.indexOf('\\') != -1) {
                throw new IllegalArgumentException(
                        "The skipDirs should not contain \\, only /. "+skipDir);
            }
        }
        return false;
    }
}
