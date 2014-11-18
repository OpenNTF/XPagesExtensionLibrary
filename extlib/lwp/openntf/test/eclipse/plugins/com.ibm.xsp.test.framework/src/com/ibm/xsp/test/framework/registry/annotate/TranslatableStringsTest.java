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
* Date: 18 Jul 2011
* TranslatableStringsTest.java
*/
package com.ibm.xsp.test.framework.registry.annotate;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesGroupDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.XspRegistryTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.SpellCheckTest.DescriptionDisplayNameAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class TranslatableStringsTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "that <description> and <display-name>s are present";
    }
    private static final String[] s_allowNonCapitalCase = new String[]{
        // Note, Bob Perron sent on a link describing the capitalization
        // rules in English: http://grammartips.homestead.com/caps.html
        // These are allowed as lower case, except where they are the 
        // first or last word.
        // articles:
        "the",
        "a",
        "an",
        // conjunctions:
        "for",
        "and",
        "nor",
        "but",
        "or",
        "so",
        // prepositions:
        "on",
        "of",
        "in",
        "for",
        "at",
        "by",
        "with",
        "from",
        "as",
        // particle:
        "to",
    };
    private static final String[] s_allowCamelCased = new String[]{
        "JavaScript",
        "OneUI", // Lotus OneUI: http://www-12.lotus.com/ldd/doc/oneuidoc/docpublic/index.htm
        "iNotes", // Lotus iNotes: http://www-01.ibm.com/software/lotus/products/inotes/
        "iCal", // The iCal data format: http://en.wikipedia.org/wiki/ICalendar  http://tools.ietf.org/html/rfc5545
    };
    
    public void testTranslatableStrings() throws Exception {
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
                this, new DescriptionDisplayNameAnnotater());
        String fails = "";
        String[] allowNonCapitalCase = getAllowNonCapitalCase();
        String[] allowCamelCased = getAllowCamelCased();
        for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
            
            fails += checkPresent(def, null);
            fails += checkLabelReferences(def, null);
            fails += checkDescrLength(def, null);
            fails += checkNewlines(def, null);
            fails += checkDisplayNameCapitalized(def, null, allowNonCapitalCase, allowCamelCased);
            
            for (FacesProperty prop : RegistryUtil.getProperties(def, def.getDefinedInlinePropertyNames()) ) {
                fails += checkPresent(prop, def);
                fails += checkLabelReferences(prop, def);
                fails += checkDescrLength(prop, def);
                fails += checkNewlines(prop, def);
                fails += checkDisplayNameCapitalized(prop, def, allowNonCapitalCase, allowCamelCased);
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkipFails(), this, "testTranslatableStrings"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }

    /**
     * These are camelCased propert names (e.g. JavaScript, iNotes),
     * which should always be written as provided here,
     * that is, the rules about never using camel-case and 
     * capitalizing words do not apply.
     * @return
     */
    protected String[] getAllowCamelCased() {
        return s_allowCamelCased;
    }

    /**
     * @return
     */
    protected String[] getAllowNonCapitalCase() {
        return s_allowNonCapitalCase;
    }
    protected String[] getSkipFails() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }

    /**
     * @param def
     * @return
     */
    private String checkPresent(FacesExtensibleNode def, FacesDefinition surroundingTag) {
        
        if( def instanceof FacesGroupDefinition ){
            // <group>s don't need description/display-name
            return "";
        }
        
        String description = (String) def.getExtension("description");
        String displayName = (String) def.getExtension("display-name");
        
        if( null == description && null == displayName ){
            return tagRef(def, surroundingTag)+" description and display-name missing\n";
        }
        if( null == description ){
            return tagRef(def, surroundingTag)+" description missing. " +
                    "display-name=" + displayName+ "\n";
        }
        if( null == displayName ){
            return tagRef(def, surroundingTag) + " display-name missing. "
                    + "description=" + description + "\n";
        }
        return "";
    }
    private String checkLabelReferences(FacesExtensibleNode def, FacesDefinition surroundingTag){
        String fails = "";
        fails += checkLabelReferences(def, surroundingTag, "description");
        fails += checkLabelReferences(def, surroundingTag, "display-name");
        return fails;
    }
    private String checkLabelReferences(FacesExtensibleNode def, FacesDefinition surroundingTag, String toCheck){
        
        String message = (String) def.getExtension(toCheck);
        if( null == message ){
            // will already have failed checkPresent
            return "";
        }
        message = message.trim();
        if( message.length() == 0 ){
            // will already have failed checkPresent
            return "";
        }
        if( '%' == message.charAt(0) || '%' == message.charAt(message.length()-1)){
            if( message.startsWith("%/") ){
                // See Label reference mechanism,
                // %/ indicates referencing the description or display-name of a different property
                // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format#label-reference-mechanism
                return tagRef(def, surroundingTag) + " " + toCheck
                        + " has an unresolved label reference: "
                        + message + "\n";
            }else{
                // % indicates referencing a key in the corresponding _locale.properties file.
                return tagRef(def, surroundingTag) + " " + toCheck
                        + " has an unresolved properties file key: "
                        + message + "\n";
            }
        }
        return "";
    }

    private String checkDescrLength(FacesExtensibleNode def, FacesDefinition surroundingTag) {
        String description = (String) def.getExtension("description");
        if( null == description ){
            // will already have failed checkPresent
            return "";
        }
        String displayName = (String) def.getExtension("display-name");
        int nameLen = (null == displayName? 0 : displayName.length()) ;
        if( description.length() < nameLen + 5 ){
            // TODO skipping "Triggered" descriptions as they're often short
            if( description.startsWith("Triggered") ){
                return "";
            }
            return tagRef(def, surroundingTag)
                    + " description probably too short: "
                    + description.replaceAll("\n", "\\\\n") + "\n";
        }
        return "";
    }

    private String checkDisplayNameCapitalized(FacesExtensibleNode def,
            FacesDefinition surroundingTag, String[] allowNonCapitalCase, String[] allowCamelCased) {
        
        String displayName = (String) def.getExtension("display-name");
        if( null == displayName ){
            // will already have failed checkPresent
            return "";
        }
        String fails = "";
        String[] words = displayName.split("[ /()+-]");
        int i = 0;
        for (String word : words) {
            if( word.length() > 0 ){
                char firstChar = word.charAt(0);
                if( Character.isLowerCase(firstChar) ){
                    
                    // the hard-coded list of camelCased words (like iNotes), are not required
                    // to capitalize the first letter.
                    boolean isWordAllowCamelCase = (-1 != XspTestUtil.indexOf(allowCamelCased, word));
                    if( !isWordAllowCamelCase ){
                        
                    // These allowNonCapitalCase words allowed as lower case, 
                    // except where they are the first or last word.
                    boolean isFirstOrLastWord = (i == 0) || (i == words.length - 1);
                    boolean requireCapitalize = isFirstOrLastWord 
                        || (-1 == XspTestUtil.indexOf(allowNonCapitalCase, word));
                    if( requireCapitalize ){
                        fails += tagRef(def, surroundingTag)
                                + " display-name word not capitalized (" + word + ") in: "
                                + displayName.replaceAll("\n", "\\\\n") + "\n";
                    }
                        
                    }// end !isWordAllowCamelCase
                }
                if( isHasSubsequentUpperCaseChar(word) && !isAllUpperCase(word) ){
                    if( -1 == XspTestUtil.indexOf(allowCamelCased, word) ){
                        fails += tagRef(def, surroundingTag)
                            + " display-name has camelCased word (" + word + ") in: "
                            + displayName.replaceAll("\n", "\\\\n") + "\n";
                    }
                }
            }
            i++;
        }
        return fails;
    }
    /**
     * @param word
     * @return
     */
    private boolean isHasSubsequentUpperCaseChar(String word) {
        int startAt = 1;
        for (int i = startAt; i < word.length(); i++) {
            char nthChar = word.charAt(i);
            if( Character.isUpperCase(nthChar) ){
                return true;
            }
        }
        return false;
    }

    /**
     * @param word
     * @return
     */
    private boolean isAllUpperCase(String word) {
        for (int i = 0; i < word.length(); i++) {
            char nthChar = word.charAt(i);
            if( Character.isLowerCase(nthChar) ){
                return false;
            }
        }
        return true;
    }

    private String checkNewlines(FacesExtensibleNode def, FacesDefinition surroundingTag) {
        String fails = "";
        fails += checkMessageNewlines(def, surroundingTag, "description");
        fails += checkMessageNewlines(def, surroundingTag, "display-name");
        return fails;
    }    
    private String checkMessageNewlines(FacesExtensibleNode def, FacesDefinition surroundingTag, String toCheck) {
        String message = (String) def.getExtension(toCheck);
        if( null == message ){
            // will already have failed checkPresent
            return "";
        }
        if( -1 != message.indexOf('\n') || -1 != message.indexOf('\t')){
            return tagRef(def, surroundingTag) + " " + toCheck
                    + " contains newlines or tabs: "
                    + message.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t") + "\n";
        }
        return "";
    }    
    private String tagRef(FacesExtensibleNode node, FacesDefinition surroundingTag) {
        if( node instanceof FacesDefinition ){
            FacesDefinition def = (FacesDefinition) node;
            String filePath = def.getFile().getFilePath();
            return filePath+" "+XspRegistryTestUtil.descr(def);
        }
        if( node instanceof FacesProperty ){
            String filePath = surroundingTag.getFile().getFilePath();
            return filePath+" "+XspRegistryTestUtil.descr(surroundingTag, (FacesProperty)node);
        }
        throw new RuntimeException("Unhandled type "+node.getClass().getName());
//        return null;
    }

}
