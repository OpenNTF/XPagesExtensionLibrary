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

package com.ibm.xsp.extlib.renderkit.html_extended.oneui.data;

import com.ibm.xsp.extlib.renderkit.html_extended.data.ForumPostRenderer;



/**
 * One UI data view renderer.
 */
public class OneUIForumPostRenderer extends ForumPostRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_MAINCLASS:                return "lotusForum lotusPost"; // $NON-NLS-1$
            case PROP_MAINSTYLEVIEWFIX:         return "margin: 0"; // $NON-NLS-1$
            case PROP_AUTHORCLASS:              return "lotusPostAuthorInfo";    // $NON-NLS-1$
            case PROP_AUTHORAVATARCLASS:        return "lotusPostAvatar";    // $NON-NLS-1$
            case PROP_AUTHORNAMECLASS:          return "lotusPostName";  // $NON-NLS-1$
            case PROP_AUTHORMETACLASS:          return "lotusMeta";  // $NON-NLS-1$
            case PROP_POSTCLASS:                return "lotusPostContent"; // $NON-NLS-1$
            //case PROP_POSTTITLECLASS:         return "";  
            case PROP_POSTMETACLASS:            return "lotusMeta";  // $NON-NLS-1$
            case PROP_POSTDETAILSCLASS:         return "lotusPostDetails";   // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}