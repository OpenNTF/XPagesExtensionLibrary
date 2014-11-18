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

package com.ibm.domino.services.rest;

public class RestServiceConstants {
    
    // Common attributes names used in JsonWriter
    public static final String ATTR_UNID = "@unid"; //$NON-NLS-1$   
    public static final String ATTR_NOTEID = "@noteid"; //$NON-NLS-1$
    public static final String ATTR_LINK = "@link"; //$NON-NLS-1$
    public static final String ATTR_LINK_REL = "rel"; //$NON-NLS-1$
    public static final String ATTR_LINK_HREF = "href"; //$NON-NLS-1$
    public static final String ATTR_HREF = "@href"; //$NON-NLS-1$
    public static final String ATTR_TITLE = "@title"; //$NON-NLS-1$
    public static final String ATTR_UPDATED = "@updated"; //$NON-NLS-1$
    public static final String ATTR_CODE = "code"; //$NON-NLS-1$
    public static final String ATTR_DATA = "data"; //$NON-NLS-1$
    public static final String ATTR_CONTENT = "content"; //$NON-NLS-1$
    public static final String ATTR_MESSAGE = "message"; //$NON-NLS-1$
    public static final String ATTR_TYPE = "type"; //$NON-NLS-1$

    // View service attributes names used in JsonWriter
    public static final String ATTR_ENTRYID = "@entryid"; //$NON-NLS-1$
    public static final String ATTR_TIMESTAMP = "@timestamp"; //$NON-NLS-1$
    public static final String ATTR_TOPLEVELENTRIES = "@toplevelentries"; //$NON-NLS-1$
    public static final String ATTR_POSITION = "@position"; //$NON-NLS-1$
    public static final String ATTR_READ = "@read"; //$NON-NLS-1$
    public static final String ATTR_SIBLINGS = "@siblings"; //$NON-NLS-1$
    public static final String ATTR_DESCENDANTS = "@descendants"; //$NON-NLS-1$
    public static final String ATTR_CHILDREN = "@children"; //$NON-NLS-1$
    public static final String ATTR_INDENT = "@indent"; //$NON-NLS-1$
    public static final String ATTR_COLUMNNUMBER = "@columnnumber"; //$NON-NLS-1$
    public static final String ATTR_NAME = "@name"; //$NON-NLS-1$
    public static final String ATTR_FORM = "@form"; //$NON-NLS-1$   
    public static final String ATTR_CATEGORY = "@category"; //$NON-NLS-1$
    public static final String ATTR_RESPONSE = "@response"; //$NON-NLS-1$
    public static final String ATTR_ITEMS = "items"; //$NON-NLS-1$
    public static final String ATTR_VIEWENTRY = "viewentry"; //$NON-NLS-1$
    public static final String ATTR_ENTRYDATA = "entrydata"; //$NON-NLS-1$
    public static final String ATTR_TEXT = "text"; //$NON-NLS-1$
    public static final String ATTR_TEXTLIST = "textlist"; //$NON-NLS-1$
    public static final String ATTR_NUMBER = "number"; //$NON-NLS-1$
    public static final String ATTR_NUMBERLIST = "numberlist"; //$NON-NLS-1$
    public static final String ATTR_BOOLEAN = "boolean"; //$NON-NLS-1$
    public static final String ATTR_BOOLEANLIST = "booleanlist"; //$NON-NLS-1$
    public static final String ATTR_DATETIME = "datetime"; //$NON-NLS-1$
    public static final String ATTR_DATETIMELIST = "datetimelist"; //$NON-NLS-1$
    public static final String ATTR_ZERO = "0"; //$NON-NLS-1$

    // Document service attributes names used in JsonWriter
    public static final String ATTR_PARENTID = "@parentid"; //$NON-NLS-1$
    public static final String ATTR_CREATED = "@created"; //$NON-NLS-1$
    public static final String ATTR_MODIFIED = "@modified"; //$NON-NLS-1$
    public static final String ATTR_AUTHORS = "@authors"; //$NON-NLS-1$
    public static final String ATTR_CONTENTTYPE = "contentType"; //$NON-NLS-1$
    public static final String ATTR_CONTENT_TYPE = ATTR_CONTENTTYPE; //$NON-NLS-1$    
    public static final String ATTR_CONTENT_TRANSFER_ENCODING = "contentTransferEncoding";	//$NON-NLS-1$
	public static final String ATTR_CONTENT_CONTENT_DISPOSITION = "contentDisposition";	//$NON-NLS-1$
	public static final String ATTR_CONTENT_ID = "contentID";	//$NON-NLS-1$
	public static final String ATTR_CONTENT_ENCODING = "contentEncoding";	//$NON-NLS-1$
    public static final String ATTR_ATTACHMENTS = "attachments"; //$NON-NLS-1$
	public static final String ATTR_BOUNDARY = "boundary";	//$NON-NLS-1$

    
    // View design attribute
    public static final String ATTRIB_NAME = "@name"; // $NON-NLS-1$
    public static final String ATTRIB_COLUMNNUMBER = "@columnNumber"; // $NON-NLS-1$
    public static final String ATTRIB_TITLE = "@title"; // $NON-NLS-1$
    public static final String ATTRIB_WIDTH = "@width"; // $NON-NLS-1$
    public static final String ATTRIB_HIDDEN = "@hidden"; // $NON-NLS-1$
    public static final String ATTRIB_CATEGORY = "@category"; // $NON-NLS-1$
    public static final String ATTRIB_RESPONSE = "@response"; // $NON-NLS-1$
    public static final String ATTRIB_ALIGNMENT = "@alignment"; // $NON-NLS-1$
    public static final String ATTRIB_TWISTIE = "@twistie"; // $NON-NLS-1$
    public static final String ATTRIB_FIELD = "@field"; // $NON-NLS-1$

    //Document item names
    public static final String ITEM_BODY = "body"; //$NON-NLS-1$
    public static final String ITEM_FORM = "Form"; //$NON-NLS-1$
    public static final String ITEM_REF = "$ref"; //$NON-NLS-1$
    public static final String ITEM_FLAGS = "$Flags"; //$NON-NLS-1$
    public static final String ITEM_FILE = "$FILE"; //$NON-NLS-1$"
    public static final String ITEM_FONTS = "$Fonts"; //$NON-NLS-1$"    

    //Document item types
    public static final String TYPE_DATETIME = "datetime"; //$NON-NLS-1$
    public static final String TYPE_RICHTEXT = "richtext"; //$NON-NLS-1$
    public static final String TYPE_MIMEPART = "mimepart"; //$NON-NLS-1$
    public static final String TYPE_MULTIPART = "multipart"; //$NON-NLS-1$
    
    // Json factory property names 
    public static final String JSON_FACTORY_PROPERTY_ID = "id"; //$NON-NLS-1$
    public static final String JSON_FACTORY_PROPERTY_METHOD = "method"; //$NON-NLS-1$
    public static final String JSON_FACTORY_PROPERTY_PARAMS = "params"; //$NON-NLS-1$
    public static final String JSON_FACTORY_PROPERTY_RESULT = "result"; //$NON-NLS-1$
    public static final String JSON_FACTORY_PROPERTY_ERROR = "error"; //$NON-NLS-1$
    
    // Json Object property names
    public static final String JSON_OBJECT_PROPERTY_ROWS = "rows"; //$NON-NLS-1$
    public static final String JSON_OBJECT_PROPERTY_OP = "op"; //$NON-NLS-1$
    public static final String JSON_OBJECT_PROPERTY_ID = "id"; //$NON-NLS-1$
    public static final String JSON_OBJECT_PROPERTY_ITEMS = "items"; //$NON-NLS-1$
    
    //Time Format
    public static final String TIME_FORMAT_A = "yyyyMMdd'T'HHmmss"; //$NON-NLS-1$
    public static final String TIME_FORMAT_B = "yyyy-MM-dd'T'HH:mm:ss"; //$NON-NLS-1$
    public static final String TIME_FORMAT_C = "yyyy-MM-dd'T'HH:mm:ssZ"; //$NON-NLS-1$
    public static final String TIME_FORMAT_D = "yyyy-MM-dd'T'HH:mm:ss'Z'"; //$NON-NLS-1$
    public static final String TIME_FORMAT_DO = "yyyy-MM-dd"; //$NON-NLS-1$
    public static final String TIME_FORMAT_TO = "HH:mm:ss"; //$NON-NLS-1$
    
    //Time Zone
    public static final String TIME_ZONE_UTC = "UTC"; //$NON-NLS-1$
    
    //Sort Order
    public static final String SORT_ORDER_DESCENDING = "descending"; //$NON-NLS-1$
    public static final String SORT_ORDER_ASCENDING = "ascending"; //$NON-NLS-1$

    // view attribute names used in XMLWriter
    public static final String ATTR_XML_UNID = "unid";   //$NON-NLS-1$
    public static final String ATTR_XML_NOTEID = "noteid"; //$NON-NLS-1$
    public static final String ATTR_XML_TIMESTAMP = "timestamp"; //$NON-NLS-1$
    public static final String ATTR_XML_TOPLEVELENTRIES = "toplevelentries"; //$NON-NLS-1$
    public static final String ATTR_XML_POSITION = "position"; //$NON-NLS-1$
    public static final String ATTR_XML_READ = "read"; //$NON-NLS-1$
    public static final String ATTR_XML_SIBLINGS = "siblings"; //$NON-NLS-1$
    public static final String ATTR_XML_DESCENDANTS = "descendants"; //$NON-NLS-1$
    public static final String ATTR_XML_CHILDREN = "children"; //$NON-NLS-1$
    public static final String ATTR_XML_INDENT = "indent"; //$NON-NLS-1$
    public static final String ATTR_XML_COLUMNNUMBER = "columnnumber"; //$NON-NLS-1$
    public static final String ATTR_XML_NAME = "name"; //$NON-NLS-1$
    public static final String ATTR_XML_VIEWENTRIES = "viewentries"; //$NON-NLS-1$
    public static final String ATTR_XML_VIEWENTRY = ATTR_VIEWENTRY; //$NON-NLS-1$
    public static final String ATTR_XML_ENTRYDATA = ATTR_ENTRYDATA; //$NON-NLS-1$
    public static final String ATTR_XML_TEXT = ATTR_TEXT; //$NON-NLS-1$
    public static final String ATTR_XML_TEXTLIST = ATTR_TEXTLIST; //$NON-NLS-1$
    public static final String ATTR_XML_NUMBER = ATTR_NUMBER; //$NON-NLS-1$
    public static final String ATTR_XML_NUMBERLIST = ATTR_NUMBERLIST; //$NON-NLS-1$
    public static final String ATTR_XML_BOOLEAN = ATTR_BOOLEAN; //$NON-NLS-1$
    public static final String ATTR_XML_BOOLEANLIST = ATTR_BOOLEANLIST; //$NON-NLS-1$
    public static final String ATTR_XML_DATETIME = ATTR_DATETIME; //$NON-NLS-1$
    public static final String ATTR_XML_DATETIMELIST = ATTR_DATETIMELIST; //$NON-NLS-1$
    
}