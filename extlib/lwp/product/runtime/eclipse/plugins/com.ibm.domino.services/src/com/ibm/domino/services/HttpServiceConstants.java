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

package com.ibm.domino.services;

public class HttpServiceConstants {
	
	// HTTP header field names
	public static final String HEADER_CONTENT_TYPE = "Content-Type";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_CONTENT_DISPOSITION = "Content-Disposition";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_ID = "Content-ID";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";	//$NON-NLS-1$
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";	//$NON-NLS-1$
	public static final String HEADER_X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";	//$NON-NLS-1$
	public static final String HEADER_RANGE = "Range";	//$NON-NLS-1$
	public static final String HEADER_LOCATION = "Location";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_RANGE = "Content-Range";	//$NON-NLS-1$
	public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";	//$NON-NLS-1$
	public static final String HEADER_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";	//$NON-NLS-1$
	public static final String HEADER_EXPIRES = "Expires";	//$NON-NLS-1$
	public static final String HEADER_LAST_MODIFIED = "Last-Modified";	//$NON-NLS-1$
	
	//HTTP header part value
	public static final String HEADER_RANGE_ITEMS = "items=";	//$NON-NLS-1$
	public static final String HEADER_CONTENT_RANGE_ITEMS = "items ";	//$NON-NLS-1$
	public static final String HEADER_MULTIPART = "multipart";	//$NON-NLS-1$
	public static final String HEADER_MIXED = "mixed";	//$NON-NLS-1$	
	public static final String HEADER_BOUNDARY = "boundary";	//$NON-NLS-1$
	public static final String HEADER_BOUNDARY_EQ = HEADER_BOUNDARY+"=";	//$NON-NLS-1$
	
	
	//HTTP action 
	public static final String HTTP_POST = "POST";	//$NON-NLS-1$
	public static final String HTTP_PUT = "PUT";	//$NON-NLS-1$
	public static final String HTTP_DELETE = "DELETE";	//$NON-NLS-1$
	public static final String HTTP_GET = "GET";	//$NON-NLS-1$
	public static final String HTTP_PATCH = "PATCH";	//$NON-NLS-1$
	
	// encoding
	public static final String ENCODING_UTF8 = "utf-8";	//$NON-NLS-1$
	public static final String ENCODING_GZIP = "gzip";	//$NON-NLS-1$
	public static final String ENCODING_BASE64 = "base64";	//$NON-NLS-1$	
	public static final String ENCODING_7BIT = "7bit";	//$NON-NLS-1$	
	public static final String ENCODING_8BIT = "8bit";	//$NON-NLS-1$
	public static final String ENCODING_BINARY = "binary";	//$NON-NLS-1$
	public static final String ENCODING_QUOTED_PRINTABLE = "quoted-printable";	//$NON-NLS-1$
	
	// content types
	public static final String CONTENTTYPE_APPLICATION_JSON = "application/json";	//$NON-NLS-1$
    public static final String CONTENTTYPE_TEXT_PLAIN = "text/plain";   //$NON-NLS-1$
	public static final String CONTENTTYPE_TEXT_JSON = "text/json";	//$NON-NLS-1$
	public static final String CONTENTTYPE_TEXT_HTML = "text/html";	//$NON-NLS-1$
	public static final String CONTENTTYPE_TEXT_XML = "text/xml";	//$NON-NLS-1$
    public static final String CONTENTTYPE_BINARY = "application/octet-stream";   //$NON-NLS-1$
	
	
	// content types with CHARSET
    public static final String CONTENTTYPE_TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";   //$NON-NLS-1$
	public static final String CONTENTTYPE_APPLICATION_JSON_UTF8 = "application/json; charset=utf-8";	//$NON-NLS-1$
    public static final String CONTENTTYPE_TEXT_XML_UTF8 = "text/xml; charset=utf-8"; //$NON-NLS-1$   
	public static final String CONTENTTYPE_TEXT_HTML_UTF8 = "text/html; charset=utf-8";	//$NON-NLS-1$	
	
	//HTML transferred meaning
	public static final String HTML_AMP = "&amp;";	//$NON-NLS-1$
	public static final String HTML_APOS = "&apos;";	//$NON-NLS-1$
	public static final String HTML_GT = "&gt;";	//$NON-NLS-1$
	public static final String HTML_LT = "&lt;";	//$NON-NLS-1$
	public static final String HTML_QUOT = "&quot;";	//$NON-NLS-1$
	
}
