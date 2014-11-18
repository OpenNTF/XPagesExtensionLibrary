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

package com.ibm.domino.services.rest.das.view;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.DateTime;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.util.XmlWriter;

import static com.ibm.domino.services.rest.RestServiceConstants.*;
import static com.ibm.domino.services.HttpServiceConstants.*;

/**
 * Domino View Service.
 */
public class RestViewXmlLegacyService extends RestViewLegacyService {

    protected class XmlViewWriter extends LegacyWriter {

        XmlWriter g;
        
        public XmlViewWriter(Writer w, boolean compact) {
            this.g = new XmlWriter(w,compact);
        }
        
        public void flush() throws IOException {
            g.flush();
        }
        
        public void close() throws IOException {
            g.close();
        }
        
        @Override
        public void writeDecl() throws IOException {
            g.writeDecl(ENCODING_UTF8);
        }
        
        @Override
        public void startDocument() throws IOException {
            g.startElement(ATTR_XML_VIEWENTRIES);
        }
        @Override
        public void endDocument() throws IOException {
            g.endElement(ATTR_XML_VIEWENTRIES);
        }
        
        @Override
        public void writeGlobalTimestamp(Date ts) throws IOException {
            if(dateISO8601) {
                g.writeAttribute(ATTR_XML_TIMESTAMP,ts);
            } else {
                g.writeAttribute(ATTR_XML_TIMESTAMP,LEGACYDATEFORMAT.format(ts));
            }
        }
        @Override
        public void writeTopLevelEntries(int nEntries) throws IOException {
            g.writeAttribute(ATTR_XML_TOPLEVELENTRIES,nEntries);
        }
        
        @Override
        public void startViewEntry() throws IOException {
            g.startElement(ATTR_XML_VIEWENTRY);
        }
        @Override
        public void endViewEntry() throws IOException {
            g.endElement(ATTR_XML_VIEWENTRY);
        }

        @Override
        public void writeSystemUnid(String unid) throws IOException {
            g.writeAttribute(ATTR_XML_UNID,unid);
        }
        @Override
        public void writeSystemNoteid(String noteid) throws IOException {
            g.writeAttribute(ATTR_XML_NOTEID,noteid);
        }
        @Override
        public void writeSystemPosition(String position) throws IOException {
            g.writeAttribute(ATTR_XML_POSITION,position);
        }
        @Override
        public void writeSystemRead(boolean read) throws IOException {
            g.writeAttribute(ATTR_XML_READ,read);
        }
        @Override
        public void writeSystemSiblings(int count) throws IOException {
            g.writeAttribute(ATTR_XML_SIBLINGS,count);
        }
        @Override
        public void writeSystemDescendants(int count) throws IOException {
            g.writeAttribute(ATTR_XML_DESCENDANTS,count);
        }
        @Override
        public void writeSystemChildren(int count) throws IOException {
            g.writeAttribute(ATTR_XML_CHILDREN,count);
        }
        @Override
        public void writeSystemIndent(int indent) throws IOException {
            g.writeAttribute(ATTR_XML_INDENT,indent);
        }
        
        @Override
        public void startEntryData() throws IOException {
        }
        @Override
        public void endEntryData() throws IOException {
        }

        @Override
        public void startColumnData() throws IOException {
            g.startElement(ATTR_XML_ENTRYDATA);
        }
        @Override
        public void endColumnData() throws IOException {
            g.endElement(ATTR_XML_ENTRYDATA);
        }

        @Override
        public void writeColumnNumber(int number) throws IOException {
            g.writeAttribute(ATTR_XML_COLUMNNUMBER, number);
        }
        @Override
        public void writeColumnName(String name) throws IOException {
            g.writeAttribute(ATTR_XML_NAME, name);
        }
        
        @Override
        public void writeColumnValue(Object value) throws IOException {
            if(value==null) {
                return;
            }
            if(value instanceof String) {
                g.startElement(ATTR_XML_TEXT);
                g.writeText((String)value);
                g.endElement(ATTR_XML_TEXT);
                return;
            }
            if(value instanceof Number) {
                g.startElement(ATTR_XML_NUMBER);
                g.writeNumber(((Number)value).doubleValue());
                g.endElement(ATTR_XML_NUMBER);
                return;
            }
            if(value instanceof Boolean) {
                g.startElement(ATTR_XML_BOOLEAN);
                g.writeBoolean((Boolean)value);
                g.endElement(ATTR_XML_BOOLEAN);
                return;
            }
            if(value instanceof Date) {
                g.startElement(ATTR_XML_DATETIME);
                if(dateISO8601) {
                    g.writeDate((Date)value);
                } else {
                    g.writeText(LEGACYDATEFORMAT.format((Date)value));
                }
                g.endElement(ATTR_XML_DATETIME);
                return;
            }
            if(value instanceof DateTime) {
                g.startElement(ATTR_XML_DATETIME);
                if(dateISO8601) {
                    g.writeDate((DateTime)value);
                } else {
                    DateTime dt = (DateTime)value;
                    g.writeText(LEGACYDATEFORMAT.format(g.toJavaDate(dt)));
                }
                g.endElement(ATTR_XML_DATETIME);
                return;
            }
            if(value instanceof Vector) {
                Vector v = (Vector)value;
                int count = v.size();
                if(v.size()>0) {
                    Object first = v.get(0);
                    String elt = null;
                    if(first instanceof String) {
                        elt = ATTR_XML_TEXTLIST;
                    } else if(first instanceof Number) {
                        elt = ATTR_XML_NUMBERLIST;
                    } else if(first instanceof Boolean) {
                        elt = ATTR_XML_BOOLEANLIST;
                    } else if(first instanceof Date) {
                        elt = ATTR_XML_DATETIMELIST;
                    }
                    g.startElement(elt);
                    for(int i=0; i<count; i++) {
                        writeColumnValue(v.get(i));
                    }
                    g.endElement(elt);
                }
                return;
            }
            
            // Should not happen...
            writeColumnValue("???"+value.getClass().toString()+"???"); // $NON-NLS-1$ // $NON-NLS-2$
        }

        @Override
        public void endTopLevelViewEntry() throws IOException {
            // TODO Auto-generated method stub          
        }

        @Override
        public void startTopLevelViewEntry() throws IOException {
            // TODO Auto-generated method stub      
        }
    }
    
    public RestViewXmlLegacyService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
        super(httpRequest, httpResponse, parameters);
    }
    
    @Override
    public void renderService() throws ServiceException {
        if (HTTP_GET.equalsIgnoreCase(getHttpRequest().getMethod())) {
            renderServiceXMLGet();
        } else {
            // Use a different status for an error?
            //HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            throw new ServiceException(null,"Method {0} is not allowed with XML format",getHttpRequest().getMethod()); // $NLX-RestViewXmlLegacyService.Method0isnotallowedwithXMLformat-1$
        }
    }

    protected void renderServiceXMLGet() throws ServiceException {
        try {
            ViewParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_TEXT_XML;
            }
            getHttpResponse().setContentType(contentType);
            getHttpResponse().setCharacterEncoding(ENCODING_UTF8);
            
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            boolean compact = parameters.isCompact();
            XmlViewWriter g = new XmlViewWriter(writer,compact); 

            renderServiceGet(parameters, g);

            g.flush();
                
        } catch(UnsupportedEncodingException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        } catch(IOException ex) {
            throw new ServiceException(ex,""); // $NON-NLS-1$
        }
    }
}