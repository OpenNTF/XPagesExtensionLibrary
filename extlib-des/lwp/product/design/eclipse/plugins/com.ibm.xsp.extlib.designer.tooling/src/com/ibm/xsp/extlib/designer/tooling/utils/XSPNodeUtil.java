/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

/**
 * @author doconnor
 *
 */
public class XSPNodeUtil {
    private static final String WHITE_SPACE_REGEX = "[\\s|\\u0085|\\u2028|\\u2029]+"; // $NON-NLS-1$
    private static Pattern _whitespacePattern;
    
    static {
        _whitespacePattern = Pattern.compile(WHITE_SPACE_REGEX);
    }
    
    
    /**
     * test if a node is a text node that contains just whitespace.
     * 
     * @param node
     * @return true if the node contains all whitespace, false otherwise.
     */
    public static boolean isWhitespace(Node node) {

        if (node != null && node.getNodeType() == Node.TEXT_NODE) {
            String text = node.getNodeValue();
            Matcher m = _whitespacePattern.matcher(text);
            if (m.matches()) {
                return true;
            }
        }

        return false;
    }

    
    /**
     * test if a string to see if it contains just whitespace.
     * 
     * @param String
     * @return true if the string contains all whitespace, false otherwise.
     */
    public static boolean isWhitespace(String text) {

        if (text != null) {
            Matcher m = _whitespacePattern.matcher(text);
            if (m.matches()) {
                return true;
            }
        }

        return false;
    }
    
    public static Node getNextSibling(Node node) {

        // skip empty text nodes.
        Node sibling = node.getNextSibling();
        while (sibling != null && isWhitespace(sibling)) {
            sibling = sibling.getNextSibling();
        }

        return sibling;
    }
    
    public static Node getPreviousSibling(Node node) {
        // skip empty text nodes.
        Node sibling = node.getPreviousSibling();
        while (sibling != null && isWhitespace(sibling)) {
            sibling = sibling.getPreviousSibling();
        }

        return sibling;
    }
}
