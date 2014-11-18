/*
 * © Copyright IBM Corp. 2012, 2013
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 20 Feb 2012
* RelationalRenderThemeControlTest.java
*/
package xsp.extlib.relational.test.render;

import java.util.List;

import com.ibm.xsp.test.framework.ConfigUtil;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.render.BaseRenderThemeControlTest;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalRenderThemeControlTest extends BaseRenderThemeControlTest {
    @Override
    protected String[][] getExtraConfig() {
        String[][] extra = super.getExtraConfig();
        extra = XspTestUtil.concat(extra, new String[][]{
                // also test the _blue, etc files. [Not tested by default]
                {"RenderThemeControl.ignoreFilesWithUnderscore", "false"},
                // also test the android, iphone, blackberry themes [Not tested by default]
                {"RenderThemeControlTest.requireMobileThemes", "true"},
        });
        return extra;
    }
    
    protected void validateContributedThemes(List<String> contributedThemes,
    		List<String> fileSysThemeFileIds) {
    	if(contributedThemes.isEmpty() ){
    		throw new RuntimeException("Bad project configuration");
    	}
    	int minimumExpected = 0;
    	if( ConfigUtil.isRequireOneui302Theme(this) /*defaults to required*/ ){
    		if( ! contributedThemes.contains("oneuiv3.0.2") ){
    			throw new RuntimeException("The oneuiv3.0.2.theme file contribution was not found. "
    					+ "Please verify that the test project "
    					+ "depends on the plugin com.ibm.xsp.theme.oneuiv302.");
    		}
    		minimumExpected += (1/*oneuiv3.0.2*/+ 10/*oneuiv3.0.2_(variant)*/);
    	}
    	if( ConfigUtil.isRequireMobileThemes(this) /*defaults to not-required*/ ){
    		if( ! contributedThemes.contains("android") ){
    			throw new RuntimeException("The android.theme file contribution was not found. "
    					+ "Please verify that the test project "
    					+ "depends on the plugin com.ibm.xsp.extlib");
    		}
    		/* android.theme, iphone.theme, blackberry.theme
    		 * [blackberry theme was never supported]*/
    		minimumExpected += (3);
    	}
    	if( contributedThemes.size() < minimumExpected ){
    		throw new RuntimeException(
    				"Number of contributed themes is less than expected "
    						+ minimumExpected + ", was " + contributedThemes.size());
    	}
    }

}
