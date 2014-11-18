/*
 * © Copyright IBM Corp. 2010, 2013
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
* Date: 28-Jul-2010
*/
package com.ibm.xsp.test.framework;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.StreamUtil;

public class ConfigUtil {

	public static boolean isTargetLocalXspconfigs(AbstractXspTest test){
		return Boolean.valueOf(getValue(test, "target.local.xspconfigs", "false"));
	}
	private static String getValue(AbstractXspTest test, String key,
			String defaultValue) {
		Map<String, String> config = test.getConfig();
		String value = defaultValue; 
		if( null != config ){
			if( config.containsKey(key) ){
				value = config.get(key);
			}
		}
		return value;
	}
	public static String getTargetLibrary(AbstractXspTest test){
		String libId = getValue(test, "target.library", null);
		if( StringUtil.isEmpty(libId) ){
			return null;
		}
		return libId;
	}
	public static boolean isTestJsfHtml(AbstractXspTest test){
		return Boolean.valueOf(getValue(test, "target.jsf.html", null));
	}
	public static boolean isTestJsfAssignCoreTagNames(AbstractXspTest test){
		return Boolean.valueOf(getValue(test, "target.jsf.assign.core.tag.names", null));
	}
	public static boolean isTestAll(AbstractXspTest test){
		return Boolean.valueOf(getValue(test, "target.all", null));
	}
	public static boolean isLibraryNoControls(AbstractXspTest test){
		return Boolean.valueOf(getValue(test, "target.library.no-controls", null));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> readConfig(String[][] extraConfig) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		// ResourceBundle bundle = ResourceBundle.getBundle("com.ibm.xsp.test.framework.config");
		HashMap<String, String> config = new HashMap<String, String>();
		try {
			URL url = contextClassLoader.getResource("com/ibm/xsp/test/framework/config.properties");
			
			if (url != null) {
				String pathToConfig = url.getPath();
				if (StringUtil.isNotEmpty(pathToConfig)) {
					if (pathToConfig.contains("com.ibm.xsp.test.framework"))
						throw new RuntimeException("The first detected config.properties file is in the ...test.framework plugin (at "
										+ url.getPath()
										+ "). Please instead add a config.properties file in your library test project, " 
										+ "and rearrange the test project .classpath file to search in the test project first, " 
										+ "before the plugin dependancies.");
				}
			}
			InputStream in = url.openStream();
			try{
				Properties props = new Properties();
				props.load(in);
				config.putAll((Map)props);
			}finally{
				StreamUtil.close(in);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1.toString(), e1);
		}
		if( null != extraConfig && extraConfig.length > 0){
			for (String[] keyToValue : extraConfig) {
				config.put(keyToValue[0], keyToValue[1]);
			}
		}
		return config;
	}
	/**
	 * @return
	 */
	public static String[] getExtraLibraryDependsRuntime(AbstractXspTest test) {
		String value = getValue(test, "extra.library.depends.runtime", null);
		if( StringUtil.isEmpty(value) ){
			return StringUtil.EMPTY_STRING_ARRAY;
		}
		return StringUtil.splitString(value, ',', /*trim*/true);
	}
	public static boolean isLibraryDependsRuntimeAutoInstalledSuppress(AbstractXspTest test){
		String key = "library.depends.runtime.autoInstalled.suppress";
		String valueStr = getValue(test, key, "false");
		if( "true".equals(valueStr) || "false".equals(valueStr) ){
			return Boolean.valueOf(valueStr);
		}
		throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
	}
	public static String[] getExtraLibraryDependsDesignTimeNonApplication(AbstractXspTest test){
		//# Extra libraries whose xsp-config files should be loaded
		//# when creating a registry that does not use this test project's
		//# WEB-INF/xsp.properties list of depends libraries.
		//# e.g. extra.library.depends.designtime.nonapplication=com.ibm.xsp.designer.library
		//#extra.library.depends.designtime.nonapplication=
		String value = getValue(test, "extra.library.depends.designtime.nonapplication", null); 
		if( StringUtil.isEmpty(value) ){
			return StringUtil.EMPTY_STRING_ARRAY;
		}
		return StringUtil.splitString(value, ',', /*trim*/true);
	}
	public static String getNamingConventionPackagePrefix(AbstractXspTest test){
		return getValue(test, "NamingConvention.package.prefix", null);
	}
	public static String[] getDominoSearchLocations(AbstractXspTest test){
	    String searchLocationsStr = getValue(test, "domino.searchLocations", null);
	    if( null == searchLocationsStr ){
	        return null;
	    }
	    return StringUtil.splitString(searchLocationsStr, /*separator*/',', /*trim*/true);
	}
    public static String getDominoSearchLocationsStrategy(AbstractXspTest test){
        String key = "domino.searchLocations.strategy";
        String strategy = getValue(test, key, "concat");
        if( "concat".equals(strategy) || "override".equals(strategy) ){
            return strategy;
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+strategy+" Expected concat or override.");
    }
    public static boolean isIgnoreThemeFilesWithUnderscore(AbstractXspTest test){
        String key = "RenderThemeControl.ignoreFilesWithUnderscore";
        String valueStr = getValue(test, key, "true");
        if( "true".equals(valueStr) || "false".equals(valueStr) ){
            return Boolean.valueOf(valueStr);
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
    }
    public static boolean isPreventServiceControlInitializers(AbstractXspTest test){
        String key = "RenderControl.preventServiceControlInitializers";
        String valueStr = getValue(test, key, "false");
        if( "true".equals(valueStr) || "false".equals(valueStr) ){
            return Boolean.valueOf(valueStr);
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
    }
    public static boolean isRequireOneui302Theme(AbstractXspTest test ){
        String key = "RenderThemeControlTest.requireOneui302Theme";
        String valueStr = getValue(test, key, "true");
        if( "true".equals(valueStr) || "false".equals(valueStr) ){
            return Boolean.valueOf(valueStr);
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
    }
    public static boolean isRequireMobileThemes(AbstractXspTest test ){
        String key = "RenderThemeControlTest.requireMobileThemes";
        String valueStr = getValue(test, key, "false");
        if( "true".equals(valueStr) || "false".equals(valueStr) ){
            return Boolean.valueOf(valueStr);
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
    }
    public static boolean isRequireBootstrapTheme(AbstractXspTest test ){
        String key = "RenderThemeControlTest.requireBootstrapTheme";
        String valueStr = getValue(test, key, "false");
        if( "true".equals(valueStr) || "false".equals(valueStr) ){
            return Boolean.valueOf(valueStr);
        }
        throw new RuntimeException("Unexpected value for key: "+key+"="+valueStr+" Expected true or false.");
    }
}
