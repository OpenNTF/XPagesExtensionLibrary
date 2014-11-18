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

package com.ibm.domino.services.util;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import lotus.domino.DateTime;
import lotus.domino.NotesException;

import com.ibm.commons.util.AbstractIOException;
import com.ibm.domino.services.rest.RestServiceConstants;
import static com.ibm.domino.services.HttpServiceConstants.*;



/**
 * Specialized XML writer.
 * 
 * @author Philippe Riand
 */
public class XmlWriter {
    
    private static final int CONTENT_NONE   = 0;
    private static final int CONTENT_TEXT   = 1;
    private static final int CONTENT_TAG    = 2;
    
    private Writer writer;
    private boolean compact;
    private int indentLevel;
    
    private boolean inElement;
    private int hasContent;
    
    public XmlWriter(Writer writer, boolean compact) {
        this.writer = writer;
        this.compact = compact;
    }
    
    public void flush() throws IOException {
        writer.flush();
    }
    
    public void close() throws IOException {
        writer.close();
    }

    public int getIndentLevel() {
        return indentLevel;
    }
    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }
    public void incIndent() {
        indentLevel++;
    }
    public void decIndent() {
        indentLevel--;
    }
    public boolean isCompact() {
        return compact;
    }

    public void writeDecl(String encoding) throws IOException {
        out("<?xml version=\"1.0\" encoding=\""); // $NON-NLS-1$
        out(encoding);
        out("\"?>");
    }
    public void out(char c) throws IOException {
        writer.write(c);
    }

    public void out(String s) throws IOException {
        writer.write(s);
    }
    
    public void startElement(String elementName) throws IOException {
        if(inElement) {
            closeElement();
        }
        nl(); indent();
        out('<');
        out(elementName);
        incIndent();
        inElement = true;
        hasContent = CONTENT_NONE;
    }
    public void closeElement() throws IOException {
        if(inElement) {
            out('>');
            inElement = false;
        }
    }
    public void endElement(String elementName) throws IOException {
        if(inElement) {
            closeElement();
        }
        decIndent();
        if(hasContent==CONTENT_TAG) {
            nl(); indent();
        }
        out('<');
        out('/');
        out(elementName);
        out('>');
        hasContent = CONTENT_TAG;
    }
    
    public void writeAttribute(String name, String value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(toXMLString(value));
        out('"');
    }
    
    public void writeAttribute(String name, int value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(Integer.toString(value));
        out('"');
    }
    
    public void writeAttribute(String name, long value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(Long.toString(value));
        out('"');
    }
    public void writeAttribute(String name, boolean value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(Boolean.toString(value));
        out('"');
    }
    
    public void writeAttribute(String name, double value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(Double.toString(value));
        out('"');
    }
    
    public void writeAttribute(String name, Date value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(dateToString(value));
        out('"');
    }
    
    public void writeAttribute(String name, DateTime value) throws IOException {
        out(' ');
        out(name);
        out('=');
        out('"');
        out(dateToString(value));
        out('"');
    }
    
    public void writeText(String text) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(toXMLString(text));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeInt(int value) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(Integer.toString(value));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeLong(long value) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(Long.toString(value));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeNumber(double value) throws IOException {
        if(inElement) {
            closeElement();
        }
        long l = (long)value;
        if((double)l==value) {
            out(Long.toString(l));
        } else {
            out(Double.toString(value));
        }
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeBoolean(boolean value) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(Boolean.toString(value));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeDate(Date value) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(dateToString(value));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }
    
    public void writeDate(DateTime value) throws IOException {
        if(inElement) {
            closeElement();
        }
        out(dateToString(value));
        if(hasContent!=CONTENT_TAG) {
            hasContent = CONTENT_TEXT;
        }
    }

    
    protected static String toXMLString(String s) {
        if( s==null ) {
            return null;
        }
        StringBuilder b = null;

        char[] chars = s.toCharArray();
        int length = chars.length;
        for( int i=0; i<length; i++ ) {
            char c = chars[i];

            // Is it a specific entity ?
            switch(c) {
                case '&':
                case '\'':
                case '>':
                case '<':
                case '\"': {
                    if( b==null ) {
                        b = new StringBuilder();
                        b.append(s, 0, i);
                    }
                    if( c=='&' )  { b.append( HTML_AMP ); break; } //$NON-NLS-1$
                    if( c=='\'' ) { b.append( HTML_APOS ); break; } //$NON-NLS-1$
                    if( c=='>' )  { b.append( HTML_GT ); break; } //$NON-NLS-1$
                    if( c=='<' )  { b.append( HTML_LT ); break; } //$NON-NLS-1$
                    if( c=='\"' ) { b.append( HTML_QUOT ); break; } //$NON-NLS-1$
                } break;
                default: {
                    if( b!=null ) {
                        b.append(c);
                    }
                }
            }
        }

        return b!=null ? b.toString() : s;
    }

    
    public void indent() throws IOException {
        if(!compact && indentLevel>0) {
            for(int i=0; i<indentLevel; i++) {
                out("  ");
            }
        }
    }
    
    public void nl() throws IOException {
        if(!compact) {
            out('\n');
        }
    }
    
    
    //TODO: What the TZ should be??
    private static SimpleDateFormat ISO8601 = new SimpleDateFormat(RestServiceConstants.TIME_FORMAT_B); //$NON-NLS-1$
    
    
    public String dateToString(Date value) throws IOException {
        return ISO8601.format((Date)value);
    }
    
    public String dateToString(DateTime value) throws IOException {
        try {
            return ISO8601.format(((DateTime)value).toJavaDate());
        } catch(NotesException ex) {
            throw new AbstractIOException(ex,"");
        }
    }

    public Date toJavaDate(DateTime value) throws IOException {
        try {
            return value.toJavaDate();
        } catch(NotesException ex) {
            throw new AbstractIOException(ex,"");
        }
    }
}   