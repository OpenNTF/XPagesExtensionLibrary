/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.domino.commons.model;

public interface IGatekeeperProvider {

    public static final int FEATURE_MUTED_THREADS                     = 483; 
    public static final int FEATURE_REST_API_MAIL_ROOT                = 484;
    public static final int FEATURE_REST_API_MAIL_INBOX               = 485;
    public static final int FEATURE_REST_API_MAIL_OUTBOX              = 486;
    public static final int FEATURE_REST_API_MAIL_DRAFTS              = 487;
    public static final int FEATURE_REST_API_MAIL_SENT                = 488;
    public static final int FEATURE_REST_API_MAIL_TRASH               = 489;
    public static final int FEATURE_REST_API_MAIL_MESSAGE             = 490;
    public static final int FEATURE_REST_API_MAIL_OOO                 = 491;
    public static final int FEATURE_REST_API_MAIL_DELEGATES           = 492;
    public static final int FEATURE_REST_API_MAIL_ACCESS              = 493;
    public static final int FEATURE_REST_API_MAIL_QUOTA               = 494;
    public static final int FEATURE_REST_API_MAIL_RECENT_CONTACTS     = 495;
    public static final int FEATURE_REST_API_MAIL_FOLDERS             = 496;

    public static final int FEATURE_REST_API_CALENDAR_ROOT            = 497;
    public static final int FEATURE_REST_API_CALENDAR_EVENT_LIST      = 498;
    public static final int FEATURE_REST_API_CALENDAR_INVITATION_LIST = 499;
    public static final int FEATURE_REST_API_CALENDAR_EVENT           = 500;
    public static final int FEATURE_REST_API_CALENDAR_NOTICE          = 501;

    public static final int FEATURE_REST_API_FREEBUSY_ROOT            = 502;
    public static final int FEATURE_REST_API_FREEBUSY_BUSY_TIME       = 503;
    public static final int FEATURE_REST_API_FREEBUSY_FREE_ROOMS      = 504;
    public static final int FEATURE_REST_API_FREEBUSY_DIRECTORIES     = 505;
    public static final int FEATURE_REST_API_FREEBUSY_SITES           = 506;

    public static final int TEST_MUTED_THREADS                        = 544;

    public static final int FEATURE_REST_API_DATA_DB_COLLECTION         = 673;
    public static final int FEATURE_REST_API_DATA_DOCUMENT              = 674;
    public static final int FEATURE_REST_API_DATA_DOC_COLLECTION        = 675;
    public static final int FEATURE_REST_API_DATA_VIEW_COLLECTION       = 676;
    public static final int FEATURE_REST_API_DATA_VIEW_DESIGN           = 677;
    public static final int FEATURE_REST_API_DATA_VIEW_ENTRIES          = 678;
    public static final int FEATURE_REST_API_DATA_VIEW_ENTRY            = 679;
    
    public static final int FEATURE_REST_API_CONTACTS_ROOT              = 746;
    public static final int FEATURE_REST_API_CONTACTS_PEOPLE            = 747;
    public static final int FEATURE_REST_API_CONTACTS_PERSON            = 748;
    public static final int FEATURE_REST_API_CONTACTS_GROUPS            = 749;
    public static final int FEATURE_REST_API_CONTACTS_GROUP             = 750;
    public static final int FEATURE_REST_API_CONTACTS_RECENT            = 751;
    public static final int FEATURE_REST_API_DEBUG_IN_ERROR_RESPONSE    = 1121;

    public boolean isFeatureEnabled(int feature, String CustomerID, String userID);
}
