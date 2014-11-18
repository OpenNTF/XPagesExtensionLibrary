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
* Date: 1 Feb 2008
* ResponseBuffer.java
*/

package com.ibm.xsp.test.framework.render;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.util.FacesUtil;

public class ResponseBuffer extends ResponseWriter{

    /**
     * initialize the context for encoding
     * @param context
     */
    public static FacesContext initContext(FacesContext context) {
        // initialize the context for encoding
        context.setResponseWriter(new ResponseBuffer());
        return context;
    }
    
    public static String encode(UIComponent comp, FacesContext context)throws IOException {
        FacesUtil.renderComponent(context, comp);
        return readBuffer(context);
    }
    public static void clear(FacesContext context){
        // note, must call 
        // context.setResponseWriter(new ResponseBuffer());
        // before can clear the buffer
        ResponseBuffer buf = (ResponseBuffer)context.getResponseWriter();
        buf.clear();
    }

    private static String readBuffer(FacesContext context) {
        // note, must call 
        // context.setResponseWriter(new ResponseBuffer());
        // before encoding and reading this buffer
        ResponseBuffer buf = (ResponseBuffer)context.getResponseWriter();
        String output = buf.toString();
        buf.clear();
        return output;
    }

    private StringBuffer b = new StringBuffer();
    boolean openForAttributes = false;
    private Set<String> attrNames = new HashSet<String>();
    
    @Override
    public String toString(){
        return b.toString();
    }
    public void clear(){
        openForAttributes = false;
        b = new StringBuffer();
    }
    // === ResponseWriter methods
    @Override
    public void endElement(String element) throws IOException {
        endAttributes();
        b.append("</").append(element).append(">");
    }
    private void endAttributes() {
        if( openForAttributes ){
            b.append(">");
            openForAttributes = false;
            attrNames.clear();
        }
    }
    @Override
    public void endDocument() throws IOException {
        endAttributes();
    }
    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void flush() throws IOException {
        // do nothing
    }
    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }
    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }
    @Override
    public void startDocument() throws IOException {
        // do nothing
    }
    @Override
    public void startElement(String element, UIComponent comp)
            throws IOException {
        endAttributes();
        b.append("<").append(element);
        openForAttributes = true;
    }
    @Override
    public void writeAttribute(String name, Object value, String componentProperty)
            throws IOException {
        if( !openForAttributes ){
            throw new UnsupportedOperationException();
        }
        if( value instanceof Boolean ){
            // Boolean indicates HTML should use attribute minimization
            // when the attribute name is output without the attribute value
            // like <input type="checkbox" checked>
            // or the attribute is not output.
            // XHTML does not allow attribute minimization,
            // instead for true values, it's recommended to output
            // values like checked="checked" 
            // See http://www.w3.org/TR/2002/REC-xhtml1-20020801/#h-4.5
            if( !((Boolean)value).booleanValue() ){
                // do not output the attribute.
                return;
            }else{
                // output checked="checked"
                // Trim to workaround an issue in SelectManyCheckboxListRenderer
                // where the name is " checked" instead of "checked"
                value = name.trim();
                // fall through
            }
        }
        if( attrNames.contains(name) ){
            throw new UnsupportedOperationException("Writing duplicate attribute name: "+name);
        }
        attrNames.add(name);
        
        b.append(" ").append(name).append("=\"").append(doEscape(value, true)).append("\"");
    }
    @Override
    public void writeComment(Object value) throws IOException {
        endAttributes();
        b.append("<!-- ").append(value).append(" -->");
    }
    @Override
    public void writeText(Object value, String componentProperty) throws IOException {
        endAttributes();
        b.append(doEscape(value, false));
    }
    @Override
    public void writeText(char[] chars, int from, int to)
            throws IOException {
        String toWrite = new String(chars).substring(from, to);
        toWrite = doEscape(toWrite, false);
        writeText(toWrite, null);
    }
    private String doEscape(Object obj, boolean inAttr) {
        if( null == obj ){
            return null;
        }
        String toWrite = obj.toString();
        toWrite = toWrite.replace(">", "&gt;");
        if( ! inAttr ){
            toWrite = toWrite.replace("<", "&lt;");
        }
        return toWrite;
    }
    @Override
    public void writeURIAttribute(String name, Object value, String componentProperty)
            throws IOException {
        writeAttribute(name, value, componentProperty);
    }
    @Override
    public void close() throws IOException {
        // do nothing
    }
    @Override
    public void write(char[] chars, int from, int to) throws IOException {
        endAttributes();
        b.append(new String(chars).substring(from, to));
    }
}