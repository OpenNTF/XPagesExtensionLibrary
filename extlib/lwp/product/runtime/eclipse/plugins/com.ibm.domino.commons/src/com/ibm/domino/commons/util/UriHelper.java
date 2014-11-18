/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.domino.commons.util;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Helper methods for working with java.net.URI objects.
 */
public class UriHelper {
    
    /**
     * Creates a new URI from a string.
     * 
     * <p>WARNING: If the input string is not a legal URI, this method
     * will throw an unchecked exception.
     * 
     * @param str
     * @param makeRelative
     * @return
     */
    public static URI create(String str, boolean makeRelative) {
        URI uri = URI.create(str);
        
        if ( uri.isAbsolute() && makeRelative ) {
            uri = copy(uri, true);
        }
        
        return uri;
    }
    
    /**
     * Make a relative copy of a URI.
     * 
     * <p>If makeRelative is false, this may return the original instance.
     * That should be OK because a URI is immutable.
     * 
     * @param original
     * @param makeRelative
     * @return
     */
    public static URI copy(URI original, boolean makeRelative) {
        URI uri = original;
        
        if ( uri.isAbsolute() && makeRelative ) {
            String rel = uri.getRawPath();
            if ( uri.getQuery() != null ) {
                rel += "?" + uri.getRawQuery();
            }
            uri = URI.create(rel);
        }
        
        return uri;
    }

    /**
     * Trims a URI starting at the last occurence of a substring.
     * 
     * @param original
     * @param match
     * @return
     */
    public static URI trimAtLast(URI original, String match) {
        URI uri = null;
        
        String str = original.toString();
        int index = str.lastIndexOf(match);
        if ( index == -1 ) {
            uri = original;
        }
        else {
            uri = URI.create(str.substring(0, index));
        }
        
        return uri;
    }
    
    /**
     * Appends a new path segment to a URI, returning a new instance.
     * 
     * <p>The new path segment is encoded before it is appended.
     * 
     * @param original
     * @param segment
     * @return
     */
    public static URI appendPathSegment(URI original, String segment) {
        URI uri;
        String query = original.getQuery();
        
        try {

            String path = null;
            if ( query == null ) {
                path = original.toString();
            }
            else {
                path = UriHelper.trimAtLast(original, "?").toString();
            }
            
            if (!path.endsWith("/"))
                path = path + "/";  
            String encodedSegment = encodePathSegment(segment);
            
            if ( query == null) {
                uri = URI.create(path + encodedSegment);
            }
            else {
                uri = URI.create(path + encodedSegment + "?" + query);
            }
        }
        finally {
        } 
        
        return uri;
    }
    
    /**
     * Encode special characters in a path segment and replace each slash (/) with %2F.
     * 
     * @param segment
     * @return
     */
    public static String encodePathSegment(String segment) {
        String encoded = null;
        
        try {
            URI uri = new URI(null,null,segment,null);
            // We must encode / in segment by ourselves
            encoded = uri.getRawPath().replace("/", "%2F"); // $NON-NLS-1$
        } 
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Bad path segment", e); // $NLX-UriHelper.Badpathsegment-1$
        }
        
        return encoded;
    }
}