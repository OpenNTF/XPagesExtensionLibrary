/*
 * © Copyright IBM Corp. 2012, 2014
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 17 Feb 2012
* BaseRenderThemeControlTest.java
*/

package com.ibm.xsp.test.framework.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.commons.extension.ExtensionManager;
import com.ibm.xsp.stylekit.StyleKitFactory;
import com.ibm.xsp.stylekit.StyleKitListFactory;

/**
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class BaseRenderThemeControlTest extends RenderThemeControlTest {
    
    @Override
    protected List<String> computeContributedThemes(List<String> themeFileIds) {
        // Note, the interface StyleKitListFactory is new in 9.0.0
        // - in earlier releases this method had a hard-coded list
        // of non-folder themeIds.
        
        List<String> list = new ArrayList<String>();
        
        List<StyleKitFactory> styleKitFactories = ExtensionManager.findServices(
                /*List<StyleKitFactory> styleKitFactories*/null, 
                /*ClassLoader loader*/this.getClass().getClassLoader(), 
                StyleKitFactory.STYLEKIT_FACTORY_SERVICE, 
                StyleKitFactory.class);

        for (StyleKitFactory styleKitFactory : styleKitFactories) {
            
            if( styleKitFactory instanceof StyleKitListFactory ){
                StyleKitListFactory testable = (StyleKitListFactory) styleKitFactory;
                String[] themeIds = testable.getThemeIds();
                list.addAll( Arrays.asList( themeIds ));
            }
        }
        validateContributedThemes(list, themeFileIds);
        return list;
    }
    // protected to allow extend in subclass, to add more validating.
    protected void validateContributedThemes(List<String> contributedThemes,
            List<String> fileSysThemeFileIds) {
    }
    
}
