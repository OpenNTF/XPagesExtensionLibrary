/*
 * © Copyright IBM Corp. 2014
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
* Date: 28 Aug 2014
* RenderPageTest.java
*/
package com.ibm.xsp.test.framework.render;

import java.util.List;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class RenderPageTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that every XPage .xsp file in the current project can be rendered";
    }
    public void testAllPages() throws Exception{
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        String[] views = TestProject.getAllViewIds(this);
        String[] skipFails = getSkipFails();
        
        String fails = "";
        for(int i=0; i<views.length; i++) {
            String viewId = views[i];
            if(isSkipFile(viewId) || isCustomControlPage(reg, viewId) ) {
                continue;
            }
            FacesContext context = TestProject.createFacesContext(this);
            ResponseBuffer.initContext(context);
            UIViewRoot root;
            
            try {
                // create the view initially
                root = TestProject.loadView(this, context, viewId);
            }
            catch(Throwable e) {
                String failMsg = viewId + " Problem in createView: "+e;
                failMsg = normalizeExMsg(failMsg);
                fails += failMsg+"\n";
                if( -1 == XspTestUtil.indexOf(skipFails, failMsg) ){
                    System.err.println("RenderPageTest ["+i+"]"+viewId);
                    e.printStackTrace();
                }
                ResponseBuffer.clear(context);
                continue;
            }
            try{
                // render the view
                String page = ResponseBuffer.encode(root, context);
                page.toString();
            }catch(Throwable e){
                String failMsg = viewId + " Problem rendering page: "+e;
                failMsg = normalizeExMsg(failMsg);
                fails += failMsg+"\n";
                if( -1 == XspTestUtil.indexOf(skipFails, failMsg) ){
                    System.err.println("RenderPageTest ["+i+"]"+viewId);
                    e.printStackTrace();
                }
                ResponseBuffer.clear(context);
                continue;
            }
            // pass
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(skipFails, this, "testAllPages"));
        
        if(fails.length()>0){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipDirs() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
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

}
