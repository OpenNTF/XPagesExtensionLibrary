/*
 * © Copyright IBM Corp. 2010, 2013, 2015
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

import com.ibm.xsp.extlib.renderkit.html_extended.data.FormTableRenderer;
import com.ibm.xsp.extlib.resources.OneUIResources;


/**
 * One UI form table renderer.
 */
public class OneUIFormTableRenderer extends FormTableRenderer {

    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_BLANKIMG:                 return OneUIResources.get().BLANK_GIF;
            
            case PROP_TABLESTYLECLASS:          return "lotusFormTable"; // $NON-NLS-1$
            
            case PROP_STYLECLASSERRORSUMMARY:   return "lotusFormErrorSummary"; // $NON-NLS-1$
            case PROP_ERRORSUMMARYCLASS:        return "lotusFormRequired"; // $NON-NLS-1$
            case PROP_WARNSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
            case PROP_WARNSUMMARYCLASS:         return null;
            case PROP_INFOSUMMARYMAINTEXT:      return getProperty(PROP_ERRORSUMMARYMAINTEXT);
            case PROP_INFOSUMMARYCLASS:         return null;
            
            case PROP_TAGFORMTITLE:             return "h2"; // $NON-NLS-1$
            case PROP_STYLECLASSHEADER:         return "lotusFormTitle"; // $NON-NLS-1$
            case PROP_STYLECLASSFORMDESC:       return "lotusMeta"; // $NON-NLS-1$
            case PROP_TAGFORMDESC:              return "div"; // $NON-NLS-1$
            
            case PROP_STYLECLASSFOOTER:         return "lotusFormFooter"; // $NON-NLS-1$

            case PROP_ERRORROWCLASS:            return "lotusFormError"; // $NON-NLS-1$

            case PROP_ERRORIMGSTYLE:            return null;
            case PROP_ERRORIMGCLASS:            return null;
            case PROP_ERRORIMGSRC:              return OneUIResources.get().ICON_ERROR;
            case PROP_ERRORMSGALTTEXTCLASS:     return "lotusAltText"; // $NON-NLS-1$

            case PROP_WARNIMGSTYLE:             return null;
            case PROP_WARNIMGCLASS:             return null;
            case PROP_WARNIMGSRC:               return OneUIResources.get().ICON_WARN;
            case PROP_WARNMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$

            case PROP_INFOIMGSTYLE:             return null;
            case PROP_INFOIMGCLASS:             return null;
            case PROP_INFOIMGSRC:               return OneUIResources.get().ICON_INFO;
            case PROP_INFOMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$

            case PROP_FIELDROWCLASS:            return "lotusFormFieldRow"; // $NON-NLS-1$
            case PROP_FIELDLABELWIDTH:          return "15%"; // $NON-NLS-1$
            case PROP_FIELDLABELCLASS:          return "lotusFormLabel"; // $NON-NLS-1$
            case PROP_FIELDLABELREQUIREDCLASS:  return "lotusFormRequired"; // $NON-NLS-1$
            case PROP_FIELDLABELREQUIREDTEXT:   return "*"; // $NON-NLS-1$

            case PROP_HELPIMGCLASS:             return null;
            case PROP_HELPIMGSRC:               return OneUIResources.get().ICON_HELP;
            case PROP_HELPMSGALTTEXTCLASS:      return "lotusAltText"; // $NON-NLS-1$
        }
        
        return super.getProperty(prop);
    }
}