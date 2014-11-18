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
* Date: 21 Feb 2014
* DojoCheckBoxDefaultValueDisabledTest.java
*/
package xsp.extlib.test.control;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspRenderUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.ResponseBuffer;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class DojoCheckBoxDefaultValueDisabledTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        // like InputDefaultValueDisabledTest except specifically testing the xe:djCheckBox behavior
        // which is different from the normal input behavior because the submitted values are "on" and (none)
        // while the saved values depend on the values of the checkedValue and uncheckedValue properties
        // that are set in the XPage source.
        return "that xe:djCheckBox controls handle having a default value & being disabled correctly";
    }
    public void testCheckBoxDefaultValueSaving() throws Exception {
        String fails = "";
        
        String pageName = "/pages/testCheckBoxDefaultValueDisabled.xsp";
        UIViewRoot root;
        String[][] controlClientIdsAndSaveLocations;
        String[] controlInitialDisplayStates;
        {
            // create view
            FacesContext contextForInitialPageDisplay = TestProject.createFacesContext(this);
            root = TestProject.loadView(this, contextForInitialPageDisplay, pageName);
            
            // renderResponse
            ResponseBuffer.initContext(contextForInitialPageDisplay);
            String page = ResponseBuffer.encode(root, contextForInitialPageDisplay);
            
            controlClientIdsAndSaveLocations = new String[][]{
                    {"view:_id1:inputText1", "value1"}, // [0]
                    {"view:_id1:inputText2", "value2"}, // [1]
                    // not-disabled checkboxes
                    {"view:_id1:djCheckBox1", "value3"}, // [2]
                    {"view:_id1:djCheckBox2", "value4"}, // [3]
                    {"view:_id1:djCheckBox3", "value5"}, // [4]
                    {"view:_id1:djCheckBox4", "value6"}, // [5]
                    {"view:_id1:djCheckBox5", "value7"}, // [6]
                    {"view:_id1:djCheckBox6", "value8"}, // [7]
                    // disabled checkboxes
                    {"view:_id1:djCheckBox7", "value9"}, // [8]
                    {"view:_id1:djCheckBox8", "value10"}, // [9]
                    {"view:_id1:djCheckBox9", "value11"}, // [10]
                    {"view:_id1:djCheckBox10", "value12"}, // [11]
                    {"view:_id1:djCheckBox11", "value13"}, // [12]
                    {"view:_id1:djCheckBox12", "value14"}, // [13]
            };
            String[] actualDisplayedInitialValues = getDisplayedValues(page,controlClientIdsAndSaveLocations);
            controlInitialDisplayStates = new String[]{
                    "aaa", // [0]
                    "aaa", // [1]
                    // not-disabled checkboxes
                    "unchecked", // [2]
                    "unchecked", // [3]
                    "checked", // [4]
                    "checked", // [5]
                    "unchecked", // [6]
                    "unchecked", // [7]
                    // disabled checkboxes
                    "unchecked", // [8]
                    "unchecked", // [9]
                    "checked", // [10]
                    "checked", // [11]
                    "unchecked", // [12]
                    "unchecked", // [13]
            };
            String[] expectedInitialDisplayValues = controlInitialDisplayStates;
            for (int i = 0; i < expectedInitialDisplayValues.length; i++) {
                String actual = actualDisplayedInitialValues[i];
                String expected = expectedInitialDisplayValues[i];
                if( ! StringUtil.equals(actual, expected) ){
                    String clientId = controlClientIdsAndSaveLocations[i][0];
                    fails += pageName+" "+clientId+" [Initial page display] Control state not as expected; expected: "+expected+", was: "+actual+"\n";
                }
            }
        }
        String[] initialSavedValues;
        {
            // next try faking a POST request, with control values
            // equivalent to just hitting submit on the already displayed page - no value changes.
            // fake the request from the browser
            Map<String, String> extraParams = new HashMap<String, String>();
            
            String[] controlInitialSubmittedStates = new String[controlInitialDisplayStates.length];
            // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
            // editBoxes and not-disabled checkboxes same as initial display states
            System.arraycopy(controlInitialDisplayStates, 0, controlInitialSubmittedStates, 0, 8);
            // disabled checkboxes all submit nothing (like unchecked state)
            //  fill(Object[] a, int fromIndex, int toIndex, Object val) 
            Arrays.fill(controlInitialSubmittedStates, 8, controlInitialSubmittedStates.length, "unchecked");
            
            for (int i = 0; i < controlInitialSubmittedStates.length; i++) {
                String clientId = controlClientIdsAndSaveLocations[i][0];
                String state = controlInitialSubmittedStates[i];
                if( "checked".equals(state) ){
                    extraParams.put(clientId, "on");
                }else if( "unchecked".equals(state) ){
                    // do not add the clientId to the submit valeus
                }else{ // edit box value
                    extraParams.put(clientId, state);
                }
            }
            FacesContext contextForPost = createContextForPost(pageName, root, extraParams);
            String page = doSubmitAndRedisplay(root, contextForPost);
            
            // verify the displayed values are still as the same after submit & redisplay
            String[] actualRedisplayStates = getDisplayedValues(page,controlClientIdsAndSaveLocations);
            String[] expectedRedisplayValues = controlInitialDisplayStates;
            for (int i = 0; i < expectedRedisplayValues.length; i++) {
                String actual = actualRedisplayStates[i];
                String expected = expectedRedisplayValues[i];
                if( ! StringUtil.equals(actual, expected) ){
                    String clientId = controlClientIdsAndSaveLocations[i][0];
                    fails += pageName+" "+clientId+" [Redisplay after Submit initial values] Control state not as expected; expected: "+expected+", was: "+actual+"\n";
                }
            }
            
            // verify the saved values are as expected
            initialSavedValues = new String[]{
                    "aaa", // [0]
                    "aaa", // [1]
                    // not-disabled checkboxes
                    "NotSelected", // [2]
                    "false", // [3]
                    "Selected", // [4]
                    "true", // [5]
                    "NotSelected", // [6]
                    "false", // [7]
                    // disabled checkboxes
                    "NotSelected", // [8]
                    "false", // [9]
                    "Selected", // [10]
                    "true", // [11]
                    "NotSelected", // [12]
                    "false", // [13]
            };
            Map<String,Object> viewScope = getViewMap(root);
            String[] expectedSavedValues = initialSavedValues;
            for (int i = 0; i < controlClientIdsAndSaveLocations.length; i++) {
                String saveLocation = controlClientIdsAndSaveLocations[i][1];
                String expected = expectedSavedValues[i];
                Object actual = viewScope.get(saveLocation);
                if( ! expected.equals(actual) ){
                    String clientId = controlClientIdsAndSaveLocations[i][0];
                    fails += pageName+" "+clientId+" [Submit initial values] Saved value not as expected; expected: "+expected+", was: "+actual+"\n";
                }
            }
        }
        {
            // next fake a POST request, with control values
            // equivalent to changing all the values on the already displayed page
            // (and changing the submitted values of the disabled controls to all checked, 
            // knowing that their submitted values will be ignored).
            // fake the request from the browser
            // the non-disabled modified control states should change
            // the disabled control states don't change
            String[] changedControlSubmittedStates =  new String[]{
                    "bbb", // [0]
                    "bbb", // [1]
                    // not-disabled checkboxes
                    "checked", // [2]
                    "checked", // [3]
                    "unchecked", // [4]
                    "unchecked", // [5]
                    "checked", // [6]
                    "checked", // [7]
                    // disabled checkboxes
                    // note disabled values all unchecked submission state, so doing fill for those.
                    "checked", // [8]
                    "checked", // [9]
                    "checked", // [10]
                    "checked", // [11]
                    "checked", // [12]
                    "checked", // [13]
            };
            Map<String, String> extraParams = new HashMap<String, String>();
            
            for (int i = 0; i < changedControlSubmittedStates.length; i++) {
                String clientId = controlClientIdsAndSaveLocations[i][0];
                String state = changedControlSubmittedStates[i];
                if( "checked".equals(state) ){
                    extraParams.put(clientId, "on");
                }else if( "unchecked".equals(state) ){
                    // do not add the clientId to the submit valeus
                }else{ // edit box value
                    extraParams.put(clientId, state);
                }
            }
            FacesContext contextForPost = createContextForPost(pageName, root, extraParams);
            String page = doSubmitAndRedisplay(root, contextForPost);
            
            // verify the changed displayed values are as expected after submit & redisplay
            String[] changedSubmitRedisplayValues = new String[changedControlSubmittedStates.length];
            // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
            // edit box values same as changedControlSubmittedValues
            // and non-disabled checkbox values same as changedControlSubmittedValues
            System.arraycopy(changedControlSubmittedStates, 0, changedSubmitRedisplayValues, 0, 8);
            // disabled checkbox values same as controlInitialDisplayStates
            System.arraycopy(controlInitialDisplayStates, 8, changedSubmitRedisplayValues, 8, 6);
            // Note, you would expect the 2nd edit box, since it is disabled,
            // to not save the changed submittedValue, but it does.
            
            String[] actualRedisplayStates = getDisplayedValues(page,controlClientIdsAndSaveLocations);
            String[] expectedRedisplayValues = changedSubmitRedisplayValues;
            for (int i = 0; i < expectedRedisplayValues.length; i++) {
                String actual = actualRedisplayStates[i];
                String expected = expectedRedisplayValues[i];
                if( ! StringUtil.equals(actual, expected) ){
                    String clientId = controlClientIdsAndSaveLocations[i][0];
                    fails += pageName+" "+clientId+" [Redisplay after Submit changed values] Control state not as expected; expected: "+expected+", was: "+actual+"\n";
                }
            }
            
            String[] changedSavedValues = new String[changedControlSubmittedStates.length];
            String[] differentValues = new String[]{
                    "bbb", // [0]
                    // Note, you would expect the 2nd edit box, since it is disabled,
                    // to not save the changed submittedValue, but it does.
                    "bbb", // [1]
                    // not-disabled checkboxes
                    "Selected", // [2]
                    "true", // [3]
                    "NotSelected", // [4]
                    "false", // [5]
                    "Selected", // [6]
                    "true", // [7]
                    // note disabled values same as initial, so doing arraycopy for those.
            };
            // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
            System.arraycopy(differentValues, 0, changedSavedValues, 0, 8);
            // disabled checkbox values same as initialSavedValues
            System.arraycopy(initialSavedValues, 8, changedSavedValues, 8, 6);
            
            Map<String,Object> viewScope = getViewMap(root);
            String[] expectedSavedValues = changedSavedValues;
            for (int i = 0; i < controlClientIdsAndSaveLocations.length; i++) {
                String saveLocation = controlClientIdsAndSaveLocations[i][1];
                String expected = expectedSavedValues[i];
                Object actual = viewScope.get(saveLocation);
                if( ! expected.equals(actual) ){
                    String clientId = controlClientIdsAndSaveLocations[i][0];
                    fails += pageName+" "+clientId+" [Submit changed values] Saved value not as expected; expected: "+expected+", was: "+actual+"\n";
                }
            }
        }
        
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testCheckBoxDefaultValueSaving"));
        if( fails.length() > 0 ){
            fail( XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    /**
     * @param root
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getViewMap(UIViewRoot root) {
        return root.getViewMap();
    }
    /**
     * @param root
     * @param contextForPost
     * @return
     * @throws IOException
     */
    private String doSubmitAndRedisplay(UIViewRoot root, FacesContext contextForPost) throws IOException {
        String page;
        // now fake the JSF lifecycle
        root.processDecodes(contextForPost);
        root.processValidators(contextForPost);
        if( contextForPost.getMessages().hasNext() ){
            fail("messages found after validate");
        }
        root.processUpdates(contextForPost);
        root.processApplication(contextForPost);
        if( contextForPost.getMessages().hasNext() ) fail("messages found");
        ResponseBuffer.initContext(contextForPost);
        page = ResponseBuffer.encode(root, contextForPost);
        return page;
    }
    /**
     * @param pageName
     * @param root
     * @param extraParams
     * @return
     * @throws Exception
     */
    private FacesContext createContextForPost(String pageName, UIViewRoot root, Map<String, String> extraParams) throws Exception {
        FacesContext contextForPost;
        extraParams.put("view:_id1", ""); // the <form element itself
        //String submittedValue = InputSaveValueTest.getSubmittedValue(instance, "");
        //extraParams.put("view:_id1:input1", submittedValue);
        HttpServletRequest request = TestProject.createRequest(this, pageName, extraParams);
        contextForPost = TestProject.createFacesContext(this,request);
        ResponseBuffer.initContext(contextForPost);
        contextForPost.setViewRoot(root);
        return contextForPost;
    }
    /**
     * @param page
     * @param clientIdAndOther
     * @return
     */
    private String[] getDisplayedValues(String page, String[][] clientIdAndOtherArr) {
        String[] results = new String[clientIdAndOtherArr.length];
        int i = 0;
        for (String[] clientIdAndOther : clientIdAndOtherArr) {
            String clientId = clientIdAndOther[0];
            String snippet = getInputSnippetFor(page, clientId);
            
            if( clientId.contains("inputText") ){
                // edit box
                String valueAttr = XspRenderUtil.findAttribute(snippet, "value");
                if( null == valueAttr ) throw new RuntimeException("No value attr found in edit box: "+snippet);
                results[i] = valueAttr;
            }else{
                // check box
                String checkedAttr = XspRenderUtil.findAttribute(snippet, "checked");
                String checkedState = (null == checkedAttr)? "unchecked" : "checked";
                results[i] = checkedState;
            }
            i++;
        }
        return results;
    }
    /**
     * @param clientId
     * @return
     */
    private String getInputSnippetFor(String page, String clientId) {
        String expectedIdAttr = " id=\"" +clientId+"\"";
        int idIndex = page.indexOf(expectedIdAttr);
        if( -1 == idIndex ){
            System.err.println("DojoCheckBoxDefaultValueDisabledTest.getInputSnippetFor() " 
                    + "Expected HTML output not found for "
                    + clientId + " in:\n" + page);
            throw new RuntimeException("Expected HTML output not found for " +clientId+
                    " in: "+page);
        }
        String expectedStartInput = "<input";
        int startInputIndex = page.lastIndexOf(expectedStartInput, idIndex);
        if( -1 == startInputIndex ) throw new RuntimeException();
        int endInputIndex = page.indexOf(">", idIndex);
        if( -1 == endInputIndex ) throw new RuntimeException();
        endInputIndex += 1;
        
        String snippet = page.substring(startInputIndex, endInputIndex);
        return snippet;
    }
}
