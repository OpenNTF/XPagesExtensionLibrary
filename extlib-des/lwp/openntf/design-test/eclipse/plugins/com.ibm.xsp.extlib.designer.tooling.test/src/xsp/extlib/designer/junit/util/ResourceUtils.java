/*
 *  Copyright IBM Corp. 2011
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
package xsp.extlib.designer.junit.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author mblout
 *
 */
public class ResourceUtils {
    
    public static String normalize(String x) {
        x = x.replaceAll("[\\n\\t\\r]", "");
        x = x.replaceAll(">\\s+<", "><");
        return x;
    }
    
    
    public static String getFileContents(String filename) {
        StringBuffer sb = new StringBuffer();
        String p = "xsp/extlib/designer/junit/resources/" + filename; // $NON-NLS-1$
        
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        
        int ch;
        InputStream in = null;
        try {
            URL url = contextClassLoader.getResource(p);
            in = url.openStream();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
            
        if (null == in)
            return null;
        
        try {
            while( (ch = in.read()) != -1)
                sb.append((char)ch);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {in.close();} catch(Exception e) {}
        }
        
        return sb.toString();
    }
}
