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
* Date: 10-Jan-2006
*/
package com.ibm.xsp.test.framework.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.page.translator.ReflectUtil;
import com.ibm.xsp.registry.FacesComplexDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 10-Jan-2006
 * Plugin: xsp.core.test
 */
public class ComplexCheckTest extends AbstractXspTest {
    @Override
    public String getDescription() {
        return "that all complexes where isTag is true are instantiable";
    }
    public void testComplexes() throws Exception {
        
        String fails = "";
//        List<String> skippedNonTags = getSkippedNonTags();
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        
        for (FacesComplexDefinition complex : TestProject.getLibComplexDefs(reg, this)) {
            boolean isInstantiable = ReflectUtil.isClassInstantiable( complex.getJavaClass() );
            if( complex.isTag() && ! isInstantiable){
                fails += complex.getFile().getFilePath() + " "
                    + ParseUtil.getTagRef(complex) 
                    + " tag but not instantiable: " +complex.getJavaClass().getName()+"\n";
            }
            // note, don't want to discourage instantiable abstract classes
            // as making them instantiable improves the junit test coverage.
//            else if( ! complex.isTag() && isInstantiable ){
//                String fail = "not a tag but instantiable: " +getDetail(complex);
//                // this may not necessarily be a fail
//                if( skippedNonTags.contains(fail) ){
//                    skippedNonTags.remove(fail);
//                    continue;
//                }
//                fails += fail+ "\n";
//            }
            
            if( complex.isTag() && isInstantiable ){
                try{
                    Object object = complex.getJavaClass().newInstance();
                    assertNotNull(object);
                }catch( Exception ex ){
                    if( ex instanceof InvocationTargetException ){
                        ex = (Exception) ((InvocationTargetException)ex).getCause();
                    }
                    System.err.println("ComplexCheckTest.testComplexes() "+ ex);
                    ex.printStackTrace();
                    fails += complex.getFile().getFilePath() + " "
                        + ParseUtil.getTagRef(complex) 
                        + " tag but not instantiable, new " +complex.getJavaClass().getName()+"() threw " +ex+ "\n";
                }
            }
        }
//        for (String skip : skippedNonTags) {
//            fails += "unused non-tag skip "+skip+"\n";
//        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getSkipFails(), this, "testComplexes"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected String[] getSkipFails(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    protected List<String> getSkippedNonTags() {
        // overridden in subclasses
        List<String> skips = new ArrayList<String>();
        return skips;
    }

}
