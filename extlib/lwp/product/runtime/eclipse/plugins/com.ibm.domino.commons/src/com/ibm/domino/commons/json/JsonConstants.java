/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.domino.commons.json;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JsonConstants {

    // Mail message envelope
    
    public static String FROM_PROP = "from"; //$NON-NLS-1$
    public static String TO_PROP = "to"; //$NON-NLS-1$
    public static String CC_PROP = "cc"; //$NON-NLS-1$
    public static String BCC_PROP = "bcc"; //$NON-NLS-1$
    public static String SUBJECT_PROP = "subject"; //$NON-NLS-1$
    public static String MESSAGE_ID_PROP = "messageId"; //$NON-NLS-1$
    public static String IN_REPLY_TO_PROP = "inReplyTo"; //$NON-NLS-1$
    public static String RECEIPT_TO_PROP = "receiptTo"; //$NON-NLS-1$
    public static String DATE_PROP = "date"; //$NON-NLS-1$
    public static String HREF_PROP = "href"; //$NON-NLS-1$
    public static String CONTENT_PROP = "content"; //$NON-NLS-1$
    public static String THREADID_PROP = "threadId"; //$NON-NLS-1$
    public static String READ_PROP = "read"; //$NON-NLS-1$
    
    // Person & room objects
    
    public static String EMAIL_PROP = "email"; //$NON-NLS-1$
    public static String DISPLAY_NAME_PROP = "displayName"; //$NON-NLS-1$
    public static String DISTINGUISHED_NAME_PROP = "distinguishedName"; //$NON-NLS-1$
    public static String HOME_SERVER_PROP = "homeServer"; //$NON-NLS-1$

    // Room object
    
    public static String CAPACITY_PROP = "capacity"; //$NON-NLS-1$
    
    // Mail message body
    
    public static String CONTENT_TYPE_PROP = "contentType"; //$NON-NLS-1$
    public static String CONTENT_DISPOSITION_PROP = "contentDisposition"; //$NON-NLS-1$
    public static String CONTENT_ID_PROP = "contentID"; //$NON-NLS-1$
    public static String CONTENT_TRANSFER_ENCODING_PROP = "contentTransferEncoding"; //$NON-NLS-1$
    public static String DATA_PROP = "data"; //$NON-NLS-1$
    public static String BOUNDARY_PROP = "boundary"; //$NON-NLS-1$
    
    // Out of office
    
    public static final String JSON_OOO_ENABLED = "enabled"; //$NON-NLS-1$
    public static final String JSON_OOO_SEND_EXTERNAL = "sendToExternal"; //$NON-NLS-1$
    public static final String JSON_OOO_APPEND_RETURN = "appendReturnToSubject"; //$NON-NLS-1$
    public static final String JSON_OOO_START = "start"; //$NON-NLS-1$
    public static final String JSON_OOO_END = "end"; //$NON-NLS-1$
    public static final String JSON_OOO_SUBJECT = "subject"; //$NON-NLS-1$
    public static final String JSON_OOO_MESSAGE = "message"; //$NON-NLS-1$
    
    // Database quota

    public static final String JSON_QUOTA_ACTUAL = "actualSize"; //$NON-NLS-1$
    public static final String JSON_QUOTA_WARNING = "warningSize"; //$NON-NLS-1$
    public static final String JSON_QUOTA_SIZE = "quotaSize"; //$NON-NLS-1$
    public static final String JSON_QUOTA_USED = "usedSize"; //$NON-NLS-1$

    // Date formatter
    
    public static SimpleDateFormat ISO8601_UTC = getUtcFormatter();

    private static SimpleDateFormat getUtcFormatter() {
        TimeZone tz = TimeZone.getTimeZone("UTC"); // $NON-NLS-1$
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //$NON-NLS-1$
        formatter.setTimeZone(tz);
        return formatter;
    }
    
    // Access & delegation
    
    public static final String JSON_ACCESS_WHAT = "what"; // $NON-NLS-1$
    public static final String JSON_ACCESS_WHAT_NOTHING = "nothing"; // $NON-NLS-1$
    public static final String JSON_ACCESS_WHAT_CALENDAR = "calendar"; // $NON-NLS-1$
    public static final String JSON_ACCESS_WHAT_MAIL = "mail"; // $NON-NLS-1$
    public static final String JSON_ACCESS_READ = "read"; // $NON-NLS-1$
    public static final String JSON_ACCESS_CREATE = "create"; // $NON-NLS-1$
    public static final String JSON_ACCESS_EDIT = "edit"; // $NON-NLS-1$
    public static final String JSON_ACCESS_DELETE = "delete"; // $NON-NLS-1$
    
    public static final String JSON_DELEGATE_NAME = "name"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_TYPE = "type"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_TYPE_UNSPECIFIED = "unspecified"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_TYPE_DEFAULT = "default"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_TYPE_PERSON = "person"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_TYPE_GROUP = "group"; // $NON-NLS-1$
    public static final String JSON_DELEGATE_ACCESS = "access"; // $NON-NLS-1$

    // Date / time constants
    public static final String JSON_DATE = "date"; //$NON-NLS-1$
    public static final String JSON_TIME = "time"; //$NON-NLS-1$
    public static final String JSON_TZID = "tzid"; //$NON-NLS-1$
    public static final String JSON_UTC = "utc"; //$NON-NLS-1$
    // FreeBusy
    public static final String JSON_START = "start"; //$NON-NLS-1$
    public static final String JSON_END = "end"; //$NON-NLS-1$
    public static final String JSON_FREEBUSY_BUSYTIMES = "busyTimes"; //$NON-NLS-1$  
}