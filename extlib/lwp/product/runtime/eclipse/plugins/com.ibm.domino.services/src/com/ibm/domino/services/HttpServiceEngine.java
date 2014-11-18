/*
 * © Copyright IBM Corp. 2010, 2014
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

package com.ibm.domino.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.NotesException;

import com.ibm.commons.util.HtmlCommonsUtil;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.ByteStreamCache;
import com.ibm.commons.util.io.StreamUtil;
import com.ibm.domino.services.util.JsonWriter;

import static com.ibm.domino.services.HttpServiceConstants.*;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_CODE;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_DATA;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_MESSAGE;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_TEXT;
import static com.ibm.domino.services.rest.RestServiceConstants.ATTR_TYPE;


/**
 * Abstract Service Engine.
 * 
 * This provides the basic HTTP service required by REST services implementations. 
 */
public abstract class HttpServiceEngine implements ServiceEngine {
    
    private boolean preventCache;
    private boolean preventGzip;
    
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    
    private boolean streamFlushed;
    private ByteStreamCache bs;
    private OutputStream os;
    
    public HttpServiceEngine(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
    }
    
    public void recycle() {
    }
    
    public boolean isPreventGzip() {
        return preventGzip;
    }
    public void setPreventGzip(boolean preventGzip) {
        this.preventGzip = preventGzip;
    }

    public boolean isPreventCache() {
        return preventCache;
    }
    public void setPreventCache(boolean preventCache) {
        this.preventCache = preventCache;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }
    
    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }
    
    public void resetStream() throws ServiceException {
        bs = null;
        os = null;
    }
    
    public void flushStream() throws ServiceException {
        if(streamFlushed) {
            return;
        }
        streamFlushed = true;
        if(os!=null) {
            try {
                os.flush();
                if(bs!=null) {
                    // Emit the GZIP header if necessary
                    if(os instanceof GZIPOutputStream) {
                        ((GZIPOutputStream)os).finish();
                        httpResponse.setHeader(HEADER_CONTENT_ENCODING, ENCODING_GZIP);
                    }
                    
                    // We have the length from the buffer
                    httpResponse.setContentLength((int)bs.getLength());
                    
                    // And copy the entire content
                    InputStream is = bs.getInputStream();
                    OutputStream out = getHttpResponse().getOutputStream();
                    StreamUtil.copyStream(is, out);
                    out.flush();
                } else {
                    // Finish the GZIP stream
                    if(os instanceof GZIPOutputStream) {
                        ((GZIPOutputStream)os).finish();
                        httpResponse.setHeader(HEADER_CONTENT_ENCODING, ENCODING_GZIP);
                    }
                }
            } catch(IOException ex) {
                throw new ServiceException(ex,"Error when sending data to the client"); // $NLX-HttpServiceEngine.Errorwhensendingdatatotheclient-1$
            }
        }
    }
    
    public OutputStream getOutputStream() throws ServiceException {
        if(streamFlushed) {
            throw new ServiceException(null,"Stream had previously been flushed"); // $NLX-HttpServiceEngine.Streamhadpreviouslybeenflushed-1$
        }
        if(os==null) {
            try {
                // Check if the Stream should a zipped one
                boolean gzip = false;
                if(!isPreventGzip()) {
                    String encodings=httpRequest.getHeader(HEADER_ACCEPT_ENCODING);
                    gzip = encodings!=null && encodings.indexOf(ENCODING_GZIP)>=0;
                }
                
                // Create the output stream
                if(isPreventCache()) {
                    os = getHttpResponse().getOutputStream();
                } else {
                    bs = new ByteStreamCache();
                    os = bs.getOutputStream();
                }
                
                if(gzip) {
                    os = new GZIPOutputStream(os);
                }
            } catch(IOException ex) {
                throw new ServiceException(ex,"Unable to get the output stream"); // $NLX-HttpServiceEngine.Unabletogettheoutputstream-1$
            }
        }
        return os;
    }

    public void processRequest() {
        try {
            renderService();
            flushStream();
        } catch(Exception ex) {
            displayError(ex);
        }
    }
    
    /**
     * Process the request.
     * @throws ServiceException
     */
    public abstract void renderService() throws ServiceException;

    /**
     * Method that emits an error as the response.
     */
    public void displayError(Exception ex) {
        try {
            // Log to the XPages log file
            if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                String debugMsg = "Problem encountered processing a request: {0}"; // $NON-NLS-1$
                Loggers.SERVICES_LOGGER.traceDebugp(this, "processRequest", ex, debugMsg, ex.toString()); // $NON-NLS-1$
            }
            
            resetStream();
    
            if (ex instanceof ServiceException) {               
                getHttpResponse().setStatus(((ServiceException)ex).getResponseCode().httpStatusCode);
            } else {
                getHttpResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }       
                                    
            Writer writer = new OutputStreamWriter(getOutputStream(),ENCODING_UTF8);
            
            String contentType = getHttpResponse().getContentType();
           
            if (StringUtil.isNotEmpty(contentType) && contentType.contains(CONTENTTYPE_APPLICATION_JSON)) {
            	writeJSONException(writer, ex);            
            }
            else{
                getHttpResponse().setContentType(CONTENTTYPE_APPLICATION_JSON);
            	writeJSONException(writer, ex);            
            }
            
            flushStream();
        } catch(Throwable t) {
            if( Loggers.SERVICES_LOGGER.isTraceDebugEnabled() ){
                String debugMsg = "Problem encountered when generating an error page."; // $NON-NLS-1$
                Loggers.SERVICES_LOGGER.traceDebugp(this, "displayError", t, debugMsg); // $NON-NLS-1$
            }
        }
    }
    
    /**
     * Writes a HTML exception.
     * 
     * @param jwriter
     * @param exception
     * @throws IOException
     */     
    public static void writeHTMLException(Writer writer, Exception exception) throws IOException {
        PrintWriter pw = new PrintWriter(writer);
        pw.println("<h2>" + // $NON-NLS-1$
                "Service error" + // $NLX-HttpServiceEngine.Serviceerror-1$
                "</h2>"); // $NON-NLS-1$
        String message = exception.getMessage();
        pw.println("<pre>"); // $NON-NLS-1$
        // SPR#MKEE9M5JQ2 issue 6c, need to escape < in message to &lt;
        pw.println(HtmlCommonsUtil.toXhtml(message));
        pw.println("</pre>"); // $NON-NLS-1$

        pw.println("<h2>" + // $NON-NLS-1$
                "Stack trace" + // $NLX-HttpServiceEngine.Stacktrace-1$
                "</h2>"); // $NON-NLS-1$
        pw.println("<pre>"); // $NON-NLS-1$
        writeExceptionTrace2(pw, exception, 0);
        pw.println("</pre>"); // $NON-NLS-1$
        
        pw.flush();
    }

    private static void writeExceptionTrace2(PrintWriter writer, Throwable e, int rows) {
        // SPR#MKEE9M5JQ2 issue 6c, need to escape < in stack trace to &lt;
        // code similar to com.ibm.xsp.webapp.XSPErrorPage.writeExceptionTrace2(PrintWriter, Throwable, int)
        if(e!=null) {
            StackTraceElement[] elt = e.getStackTrace();
            writer.println(HtmlCommonsUtil.toXhtml(e.toString()));
            for(int i=0; i<elt.length; i++) {
                writer.println("    "+HtmlCommonsUtil.toXhtml(elt[i].toString()) ); //$NON-NLS-1$
                
                // Limit the size of the trace, in case of an stackoverflow exception...
                rows++;
                if(rows>200) {
                    return;
                }
            }
            writeExceptionTrace2(writer, e.getCause(), rows);
        }
    }    
    
    /**
     * Writes a JSON exception.
     * 
     * @param jwriter
     * @param throwable
     * @throws IOException
     */     
    public static void writeJSONException(Writer writer, Throwable throwable) throws IOException {
        
        if (throwable == null) {
            return;
        }
        
        JsonWriter jwriter = new JsonWriter(writer, false);
        
        try {
        	jwriter.startObject();
        	
        	ResponseCode status = ResponseCode.UNINITIALIZED;
        	
            if (throwable instanceof ServiceException) {               
            	status = ((ServiceException)throwable).getResponseCode();
            }  
            
	        jwriter.startProperty(ATTR_CODE);
	        jwriter.outIntLiteral(status.httpStatusCode);
	        jwriter.endProperty();
	        
	        jwriter.startProperty(ATTR_TEXT);
	        jwriter.outStringLiteral(status.httpStatusText);
	        jwriter.endProperty();
                   
	        // message
	        jwriter.startProperty(ATTR_MESSAGE);        
	        String message = ""; 
	        Throwable t = throwable;
	        while ((message == null || message.length() == 0) && t != null)
	        {
	            if (t instanceof NotesException)
	                message = ((NotesException)t).text;
	            else
	                message = t.getMessage();
	
	            t = t.getCause();               
	        }
	        if (message == null)
	            jwriter.outStringLiteral("");  //$NON-NLS-1$
	        else
	            jwriter.outStringLiteral(message);
	        
	        jwriter.endProperty();
	        
	        // type
	        jwriter.startProperty(ATTR_TYPE);
	        jwriter.outStringLiteral(ATTR_TEXT);
	        jwriter.endProperty();
	        
	        // data
	        jwriter.startProperty(ATTR_DATA);       
	        StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        throwable.printStackTrace(pw);      
	        jwriter.outStringLiteral(sw.toString());
	        jwriter.endProperty();
        } 
        finally {
        	jwriter.endObject();
        }
        
        jwriter.flush();
    }

}