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

import com.ibm.xsp.extlib.renderkit.html_extended.data.DataViewRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;


/**
 * One UI data view renderer.
 */
public class OneUICustomViewRenderer extends DataViewRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // TODO the AbstractWebDataViewRenderer has hard-coded the width and height of this gif
            // as 16x16 - the width and height should be specified here along with the gif name.
            case PROP_BLANKIMG:                 return OneUIResources.get().BLANK_GIF;
            // note, for an Alt, there's a difference between the empty string and null
            case PROP_BLANKIMGALT:              return ""; //$NON-NLS-1$
            case PROP_ALTTEXTCLASS:             return "lotusAltText"; // $NON-NLS-1$
            
            
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

            case PROP_TABLECLASS:               return "lotusTable"; // $NON-NLS-1$
            case PROP_TABLEFIRSTROWCLASS:       return "lotusFirst"; // $NON-NLS-1$
            case PROP_TABLELASTROWCLASS:        return "lotusLast"; // $NON-NLS-1$
            case PROP_TABLEFIRSTCELLCLASS:      return "lotusFirstCell"; // $NON-NLS-1$
            case PROP_TABLELASTCELLCLASS:       return "lotusLastCell"; // $NON-NLS-1$
            case PROP_TABLEROWEXTRA:            return "lotusMeta lotusNowrap"; // $NON-NLS-1$
            
            case PROP_TABLEHDRROWCLASS:         return "lotusFirst lotusSort"; // $NON-NLS-1$
            case PROP_TABLEHDRFIRSTCOLCLASS:    return "lotusFirst"; // $NON-NLS-1$
            //case PROP_TABLEHDRCOLCLASS:           return "";
            case PROP_TABLEHDRCOLLKASCCLASS:    return "lotusActiveSort lotusAscending"; // $NON-NLS-1$
            case PROP_TABLEHDRCOLLKDESCLASS:    return "lotusActiveSort lotusDescending"; // $NON-NLS-1$
            
            case PROP_TABLEROWINDENTPX:         return 10;
            case PROP_SHOWICONDETAILSCLASS:     return "lotusIcon16 lotusIconShow"; // $NON-NLS-1$
            case PROP_HIDEICONDETAILSCLASS:     return "lotusIcon16 lotusIconHide"; // $NON-NLS-1$
            
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_ASCENDING:  return OneUIResources.get().VIEW_COLUMN_SORT_BOTH_ASCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH_DESCENDING: return OneUIResources.get().VIEW_COLUMN_SORT_BOTH_DESCENDING;
            case PROP_TABLEHDRCOLIMAGE_SORTBOTH:            return OneUIResources.get().VIEW_COLUMN_SORT_NONE;
            case PROP_TABLEHDRCOLIMAGE_SORTED_ASCENDING:    return OneUIResources.get().VIEW_COLUMN_SORT_NORMAL;
            case PROP_TABLEHDRCOLIMAGE_SORTED_DESCENDING:   return OneUIResources.get().VIEW_COLUMN_SORT_REVERSE;
            // the OneUI sort header icons are 12x10 px
            case PROP_TABLEHDRCOLIMAGE_SORT_WIDTH:           return "12"; //$NON-NLS-1$
            case PROP_TABLEHDRCOLIMAGE_SORT_HEIGHT:          return "10"; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }
}