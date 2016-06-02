package com.ibm.xsp.theme.bootstrap;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.xsp.extlib.ExtLibResourceHandlerImpl;

public class BootstrapResourceHandler {
	private static ExtLibResourceHandlerImpl s_impl;
    private static ExtLibResourceHandlerImpl impl(){
        if (s_impl == null) {
            s_impl = AccessController.doPrivileged(new PrivilegedAction<ExtLibResourceHandlerImpl>() {
                @Override
				public ExtLibResourceHandlerImpl run() {
                    // privileged code goes here:
                    return new ExtLibResourceHandlerImpl(BootstrapResourceHandler.class, 
                            "messages", //$NON-NLS-1$
                            "logging", //$NON-NLS-1$
                            "specialAudience"); //$NON-NLS-1$
                }
            });
        }
        return s_impl;
    }
    public static String getLoggingString(String key) {
        return impl().getLoggingString(key);
    }
    public static String getString(String key) {
        return impl().getString(key);
    }
    public static String getSpecialAudienceString(String key) {
        return impl().getSpecialAudienceString(key);
    }
    
}
