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

import static com.ibm.domino.services.rest.RestServiceConstants.*;
import static com.ibm.domino.services.HttpServiceConstants.*;

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
import com.ibm.domino.services.util.JsonWriter;


/**
 * Domino View Service.
 */
public class RestViewJsonLegacyService extends RestViewLegacyService {

    protected boolean jsonTyped;
    
    protected class XmlViewWriter extends LegacyWriter {

        JsonWriter g;
        
        public XmlViewWriter(Writer w, boolean compact) {
            this.g = new JsonWriter(w,compact);
        }
        
        public void flush() throws IOException {
            g.flush();
        }
        
        public void close() throws IOException {
            g.close();
        }
        
        @Override
        public void writeDecl() throws IOException {
        }
        
        @Override
        public void startDocument() throws IOException {
            g.startObject();
        }
        @Override
        public void endDocument() throws IOException {
            g.endObject();
        }
        
        @Override
        public void writeGlobalTimestamp(Date ts) throws IOException {
            g.startProperty(ATTR_TIMESTAMP);
            if(dateISO8601) {
                g.outDateLiteral(ts);
            } else {
                g.outStringLiteral(LEGACYDATEFORMAT.format((Date)ts));
            }
            g.endProperty();
        }
        @Override
        public void writeTopLevelEntries(int nEntries) throws IOException {
            g.startProperty(ATTR_TOPLEVELENTRIES);
            if(jsonTyped) {
                g.outIntLiteral(nEntries);
            } else {
                g.outStringLiteral(Integer.toString(nEntries));
            }
            g.endProperty();
        }
        
        @Override
        public void startTopLevelViewEntry() throws IOException {
            g.startProperty(ATTR_VIEWENTRY);
            g.startArray();
        }
        
        @Override
        public void endTopLevelViewEntry() throws IOException {
            g.endArray();
            g.endProperty();
        }
        
        @Override
        public void startViewEntry() throws IOException {
            g.startArrayItem();
            g.startObject();
            
        }
        @Override
        public void endViewEntry() throws IOException {
            g.endObject();
            g.endArrayItem();
        }

        @Override
        public void writeSystemUnid(String unid) throws IOException {
            g.startProperty(ATTR_UNID);
            g.outStringLiteral(unid);
            g.endProperty();
        }
        @Override
        public void writeSystemNoteid(String noteid) throws IOException {
            g.startProperty(ATTR_NOTEID);
            g.outStringLiteral(noteid);
            g.endProperty();
        }
        @Override
        public void writeSystemPosition(String position) throws IOException {
            g.startProperty(ATTR_POSITION);
            g.outStringLiteral(position);
            g.endProperty();
        }
        @Override
        public void writeSystemRead(boolean read) throws IOException {
            g.startProperty(ATTR_READ);
            if(jsonTyped) {
                g.outBooleanLiteral(read);
            } else {
                g.outStringLiteral(Boolean.toString(read));
            }
            g.endProperty();
        }
        @Override
        public void writeSystemSiblings(int count) throws IOException {
            g.startProperty(ATTR_SIBLINGS);
            if(jsonTyped) {
                g.outIntLiteral(count);
            } else {
                g.outStringLiteral(Integer.toString(count));
            }
            g.endProperty();
        }
        @Override
        public void writeSystemDescendants(int count) throws IOException {
            g.startProperty(ATTR_DESCENDANTS);
            if(jsonTyped) {
                g.outIntLiteral(count);
            } else {
                g.outStringLiteral(Integer.toString(count));
            }
            g.endProperty();
        }
        @Override
        public void writeSystemChildren(int count) throws IOException {
            g.startProperty(ATTR_CHILDREN);
            if(jsonTyped) {
                g.outIntLiteral(count);
            } else {
                g.outStringLiteral(Integer.toString(count));
            }
            g.endProperty();
        }
        @Override
        public void writeSystemIndent(int indent) throws IOException {
            g.startProperty(ATTR_INDENT);
            if(jsonTyped) {
                g.outIntLiteral(indent);
            } else {
                g.outStringLiteral(Integer.toString(indent));
            }
            g.endProperty();
        }
        
        @Override
        public void startEntryData() throws IOException {
            g.startProperty(ATTR_ENTRYDATA);
            g.startArray();
        }
        @Override
        public void endEntryData() throws IOException {
            g.endArray();
            g.endProperty();
        }

        @Override
        public void startColumnData() throws IOException {
            g.startArrayItem();
            g.startObject();
        }
        
        @Override
        public void endColumnData() throws IOException {
            g.endArrayItem();
            g.endObject();
        }

        @Override
        public void writeColumnNumber(int number) throws IOException {
            g.startProperty(ATTR_COLUMNNUMBER);
            if(jsonTyped) {
                g.outIntLiteral(number);
            } else {
                g.outStringLiteral(Integer.toString(number));
            }
            g.endProperty();
        }
        @Override
        public void writeColumnName(String name) throws IOException {
            g.startProperty(ATTR_NAME);
            g.outStringLiteral(name);
            g.endProperty();
        }
        
        @Override
        public void writeColumnValue(Object value) throws IOException {
            if(value==null) {
                return;
            }
            if(value instanceof String) {
                g.startProperty(ATTR_TEXT);
                g.startObject();
                g.startProperty(ATTR_ZERO);
                g.outStringLiteral((String)value);
                g.endProperty();
                g.endObject();
                g.endProperty();
                return;
            }
            if(value instanceof Number) {
                g.startProperty(ATTR_NUMBER);
                g.startObject();
                g.startProperty(ATTR_ZERO);
                g.outNumberLiteral(((Number)value).doubleValue());
                g.endProperty();
                g.endObject();
                g.endProperty();
                return;
            }
            if(value instanceof Boolean) {
                g.startProperty(ATTR_BOOLEAN);
                g.startObject();
                g.startProperty(ATTR_ZERO);
                g.outBooleanLiteral((Boolean)value);
                g.endProperty();
                g.endObject();
                g.endProperty();
                return;
            }
            if(value instanceof Date) {
                g.startProperty(ATTR_DATETIME);
                g.startObject();
                g.startProperty(ATTR_ZERO);
                if(dateISO8601) {
                    g.outDateLiteral((Date)value);
                } else {
                    g.outStringLiteral(LEGACYDATEFORMAT.format((Date)value));
                }
                g.endProperty();
                g.endObject();
                g.endProperty();
                return;
            }
            if(value instanceof DateTime) {
                g.startProperty(ATTR_DATETIME);
                g.startObject();
                g.startProperty(ATTR_ZERO);
                if(dateISO8601) {
                    g.outDateLiteral((DateTime)value);
                } else {
                    DateTime dt = (DateTime)value;
                    g.outStringLiteral(LEGACYDATEFORMAT.format(g.toJavaDate(dt)));
                }
                g.endProperty();
                g.endObject();
                g.endProperty();
                return;
            }
            if(value instanceof Vector) {
                Vector v = (Vector)value;
                int count = v.size();
                if(v.size()>0) {
                    Object first = v.get(0);
                    if(first instanceof String) {
                        g.startProperty(ATTR_TEXTLIST);
                        g.startObject();
                        g.startProperty(ATTR_TEXT);
                        g.startArray();
                        for(int i=0; i<count; i++) {
                            g.startArrayItem();
                            g.startObject();
                            g.startProperty(ATTR_ZERO);
                            g.outStringLiteral((String)v.get(i));
                            g.endProperty();
                            g.endObject();
                            g.endArrayItem();
                        }
                        g.endArray();
                        g.endProperty();
                        g.endObject();
                        g.endProperty();
                    } else if(first instanceof Number) {
                        g.startProperty(ATTR_NUMBERLIST);
                        g.startObject();
                        g.startProperty(ATTR_NUMBER);
                        g.startArray();
                        for(int i=0; i<count; i++) {
                            g.startArrayItem();
                            g.startObject();
                            g.startProperty(ATTR_ZERO);
                            g.outNumberLiteral(((Number)v.get(i)).doubleValue());
                            g.endProperty();
                            g.endObject();
                            g.endArrayItem();
                        }
                        g.endArray();
                        g.endProperty();
                        g.endObject();
                        g.endProperty();
                    } else if(first instanceof Boolean) {
                        g.startProperty(ATTR_BOOLEANLIST);
                        g.startObject();
                        g.startProperty(ATTR_BOOLEAN);
                        g.startArray();
                        for(int i=0; i<count; i++) {
                            g.startArrayItem();
                            g.startObject();
                            g.startProperty(ATTR_ZERO);
                            g.outBooleanLiteral((Boolean)v.get(i));
                            g.endProperty();
                            g.endObject();
                            g.endArrayItem();
                        }
                        g.endArray();
                        g.endProperty();
                        g.endObject();
                        g.endProperty();
                    } else if(first instanceof Date) {
                        g.startProperty(ATTR_DATETIMELIST);
                        g.startObject();
                        g.startProperty(ATTR_DATETIME);
                        g.startArray();
                        for(int i=0; i<count; i++) {
                            g.startArrayItem();
                            g.startObject();
                            g.startProperty(ATTR_ZERO);
                            if(dateISO8601) {
                                g.outDateLiteral((Date)v.get(i));
                            } else {
                                g.outStringLiteral(LEGACYDATEFORMAT.format((Date)v.get(i)));
                            }
                            g.endProperty();
                            g.endObject();
                            g.endArrayItem();
                        }
                        g.endArray();
                        g.endProperty();
                        g.endObject();
                        g.endProperty();
                    }
                }
                return;
            }
            
            // Should not happen...
            writeColumnValue("???"+value.getClass().toString()+"???"); // $NON-NLS-1$ // $NON-NLS-2$
        }
    }
    
    public RestViewJsonLegacyService(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ViewParameters parameters) {
        super(httpRequest, httpResponse, parameters);
    }
    
    @Override
    public void renderService() throws ServiceException {
        if (HTTP_GET.equalsIgnoreCase(getHttpRequest().getMethod())) {
            renderServiceJSONGet();
        } else {
            // Use a different status for an error?
            //HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            throw new ServiceException(null,"Method {0} is not allowed with JSON format",getHttpRequest().getMethod()); // $NLX-RestViewJsonLegacyService.Method0isnotallowedwithJSONformat-1$
        }
    }

    protected void renderServiceJSONGet() throws ServiceException {
        try {
            ViewParameters parameters = getParameters();
            String contentType = parameters.getContentType();
            if(StringUtil.isEmpty(contentType)) {
                contentType = CONTENTTYPE_TEXT_JSON;
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