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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.data;


public class ForumPostRenderer extends com.ibm.xsp.extlib.renderkit.html_extended.data.ForumPostRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_MAINCLASS:                return "xspForumPost"; // $NON-NLS-1$
            case PROP_AUTHORCLASS:              return "media-left";    // $NON-NLS-1$
            case PROP_AUTHORAVATARCLASS:        return "media-object";    // $NON-NLS-1$
            case PROP_POSTCLASS:                return "media-body"; // $NON-NLS-1$
            case PROP_POSTTITLECLASS:           return "media-heading"; // $NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}