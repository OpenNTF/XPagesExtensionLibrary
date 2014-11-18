/*
 * Copyright IBM Corp. 2011
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mblout
 *
 * this is used currently to hide "UnsatisfiedLinkError" from not having 
 * nlsxbe on the path.
 * [com.ibm.commons.Platform does a e.printStackTrace()]
 * This really should be checking that that is the only exception we get, if any. 
 */
public class SquelchSystemErr {
    
    PrintStream saveerr;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    String output;
    
    
    static class PS extends PrintStream {
        
        boolean inStack = false;
        boolean squelched = false;
        
        final PrintStream origPS;
        
        @Override
        public void print(String s) {
            super.print(s);
            
            boolean startOf = check(s);
            if (startOf) {
                origPS.println("[squelch]:" + s);
            }
            else 
            if (!inStack) {
                origPS.println(s);
            }
        }


        public PS(OutputStream out , PrintStream origPS) {
            super(out);
            this.origPS = origPS;
        }
        
        private boolean check(String s) {
            
            if (squelched)
                return false;
            
            boolean startOfStack = false;
            
            if (inStack) {
                if (null == s || !s.startsWith("\t")) {
                    inStack = false;
                    squelched = true;
                }
            }
            else {
                if (null != s) {
                    int i = s.indexOf(":");
                    if (i > 0) {
                        String classname = s.substring(0, i);
                        if (classname.matches("^(([a-z])+.)+[A-Z]([A-Za-z])+$")) {
                            try {
                                Class<?> clazz = Class.forName(classname);
                                
                                if (null != clazz && UnsatisfiedLinkError.class.isInstance(clazz.newInstance())) {
                                    inStack = true;
                                    startOfStack = true;
                                }
                            }
                            catch(Exception e) {
                                inStack = false;
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return startOfStack;
        }
    };
    
    public SquelchSystemErr() {
        start();
    }
    
    
    
    public void start() {
        output = "";
        saveerr = System.err;
        PrintStream ps = new PS(baos, saveerr);
        System.setErr(ps);
    }
    
    public String stop() {
        System.setErr(saveerr);
        try {
            output = baos.toString("ISO-8859-1");
            
            // count \\n's...
            Pattern p = Pattern.compile("\\n");
            Matcher m = p.matcher(output);
            int count = 0;
            while (m.find()) count++;
            
            if (output.length() > 0) {
                System.err.println("This error was redirected by " + this.getClass().getName());
                m.reset();
                int max = 200;
                if (m.find())
                    max = m.start();
                System.err.println(output.substring(0, Math.min(max, output.length())));
            }
        }
        catch(UnsupportedEncodingException ex) {
            // do nothing
        }
        return output;
    }
    
    
}
