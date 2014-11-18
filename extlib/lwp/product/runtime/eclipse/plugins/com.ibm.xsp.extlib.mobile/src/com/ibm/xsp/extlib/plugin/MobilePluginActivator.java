package com.ibm.xsp.extlib.plugin;

import org.eclipse.core.runtime.Plugin;

import com.ibm.xsp.extlib.minifier.ExtLibLoaderExtension;
import com.ibm.xsp.extlib.minifier.MobileLoader;

public class MobilePluginActivator extends Plugin {

	public static MobilePluginActivator instance;
	
	public MobilePluginActivator() {
		instance = this;
		
        ExtLibLoaderExtension.getExtensions().add(new MobileLoader());
	}

}
