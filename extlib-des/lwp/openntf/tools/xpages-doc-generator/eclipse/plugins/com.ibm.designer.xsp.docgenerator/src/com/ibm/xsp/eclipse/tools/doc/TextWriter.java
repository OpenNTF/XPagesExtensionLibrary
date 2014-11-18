/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.eclipse.tools.doc;

import java.io.*;

import com.ibm.commons.util.StringUtil;


/**
 * 
 */
public class TextWriter {

    private File file;
    private Writer writer;
    
    public TextWriter(File file) throws IOException {
        this.file = file;
        this.writer = new BufferedWriter(new FileWriter(file));
    }
    
    public String encode(String str) {
        // TODO...
        return str;
    }
    
    public File getFile() {
        return file;
    }
    
    public void close() throws IOException {
        writer.close();
    }
    
    public void prt(String fmt) throws IOException {
        prt(fmt, null, null, null, null, null);
    }
    public void prt(String fmt, Object p1) throws IOException {
        prt(fmt, p1, null, null, null, null);
    }
    public void prt(String fmt, Object p1, Object p2) throws IOException {
        prt(fmt, p1, p2, null, null, null);
    }
    public void prt(String fmt, Object p1, Object p2, Object p3) throws IOException {
        prt(fmt, p1, p2, p3, null, null);
    }
    public void prt(String fmt, Object p1, Object p2, Object p3, Object p4) throws IOException {
        prt(fmt, p1, p2, p3, p4, null);
    }
    public void prt(String fmt, Object p1, Object p2, Object p3, Object p4, Object p5) throws IOException {
        String s = StringUtil.format(fmt,p1,p2,p3,p4,p5);
        writer.write(s);
    }
    
    public void prtln(String fmt) throws IOException {
        prtln(fmt, null, null, null, null, null);
    }
    public void prtln(String fmt, Object p1) throws IOException {
        prtln(fmt, p1, null, null, null, null);
    }
    public void prtln(String fmt, Object p1, Object p2) throws IOException {
        prtln(fmt, p1, p2, null, null, null);
    }
    public void prtln(String fmt, Object p1, Object p2, Object p3) throws IOException {
        prtln(fmt, p1, p2, p3, null, null);
    }
    public void prtln(String fmt, Object p1, Object p2, Object p3, Object p4) throws IOException {
        prtln(fmt, p1, p2, p3, p4, null);
    }
    public void prtln(String fmt, Object p1, Object p2, Object p3, Object p4, Object p5) throws IOException {
        prt(fmt, p1, p2, p3, p4, p5);
        nl();
    }
    
    public void nl() throws IOException {
        writer.write('\n');
    }
}
