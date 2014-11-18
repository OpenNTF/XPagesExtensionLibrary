package com.ibm.xsp.extlib.relational;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.xsp.extlib.ExtLibResourceHandlerImpl;

public class ResourceHandler {
	private static ExtLibResourceHandlerImpl s_impl;
    private static ExtLibResourceHandlerImpl impl(){
        if (s_impl == null) {
            s_impl = AccessController.doPrivileged(new PrivilegedAction<ExtLibResourceHandlerImpl>() {
                public ExtLibResourceHandlerImpl run() {
                    // privileged code goes here:
                    return new ExtLibResourceHandlerImpl(ResourceHandler.class, 
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
