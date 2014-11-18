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

import com.ibm.xsp.extlib.renderkit.html_extended.data.ForumViewRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;


/**
 * One UI data view renderer.
 */
public class OneUIForumViewRenderer extends ForumViewRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMG:                 return OneUIResources.get().BLANK_GIF;
            // note, for an Alt, there's a difference between the empty string and null
            case PROP_BLANKIMGALT:              return ""; //$NON-NLS-1$
            case PROP_ALTTEXTCLASS:             return "lotusAltText";   // $NON-NLS-1$
            
            
            case PROP_HEADERCLASS:              return "lotusPaging"; // $NON-NLS-1$
            case PROP_HEADERLEFTSTYLE:          return null; 
            case PROP_HEADERLEFTCLASS:          return "lotusLeft"; // $NON-NLS-1$
            case PROP_HEADERRIGHTSTYLE:         return null; 
            case PROP_HEADERRIGHTCLASS:         return "lotusRight"; // $NON-NLS-1$

            case PROP_FOOTERCLASS:              return "lotusPaging"; // $NON-NLS-1$
            case PROP_FOOTERLEFTSTYLE:          return null; 
            case PROP_FOOTERLEFTCLASS:          return "lotusLeft"; // $NON-NLS-1$
            case PROP_FOOTERRIGHTSTYLE:         return null; 
            case PROP_FOOTERRIGHTCLASS:         return "lotusRight"; // $NON-NLS-1$

            case PROP_SHOWICONDETAILSCLASS:     return "lotusIcon16 lotusIconShow"; // $NON-NLS-1$
            case PROP_HIDEICONDETAILSCLASS:     return "lotusIcon16 lotusIconHide"; // $NON-NLS-1$
            
            
            case PROP_MAINDIVCLASS:             return "lotusForum"; // $NON-NLS-1$
            case PROP_MAINLISTCLASS:            return "lotusCommentList"; // $NON-NLS-1$
            case PROP_CHILDLISTCLASS:           return "lotusChild"; // $NON-NLS-1$
            case PROP_LISTITEMCLASS:            return "lotusCommentItem"; // $NON-NLS-1$
            
            //case PROP_COLLAPSIBLECONTENTSTYLE:    return "margin: 7px;";
            case PROP_COLLAPSIBLEDIVSTYLE:      return "float: right;"; // $NON-NLS-1$
            
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:  return OneUIResources.get().VIEW_COLUMN_SORT_BOTH_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING: return OneUIResources.get().VIEW_COLUMN_SORT_BOTH_DESCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:            return OneUIResources.get().VIEW_COLUMN_SORT_NONE;
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:    return OneUIResources.get().VIEW_COLUMN_SORT_NORMAL;
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:   return OneUIResources.get().VIEW_COLUMN_SORT_REVERSE;
        }
        return super.getProperty(prop);
    }
}