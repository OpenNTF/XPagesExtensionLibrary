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
package com.ibm.xsp.extlib.relational.plugin;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.ibm.xsp.extlib.relational.RelationalLogger;

/**
 * Extlib Bundle Activator.
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */

public class RelationalActivator extends Plugin {
    public static RelationalActivator instance;
        
    public RelationalActivator() {
        instance = this;
        //Make sure the JNDI plugin is active
        AccessController.doPrivileged( new PrivilegedAction<Void>() {
            public Void run() {
                try {
                    Bundle bundle = Platform.getBundle( "com.ibm.pvc.jndi.provider.java"); // $NON-NLS-1$
                    if(bundle!=null) { // Empty during the unit tests
                        bundle.start();
                    }
                } catch (BundleException ex) {
                    if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                        RelationalLogger.RELATIONAL.errorp(this, "RelationalActivator", ex, "Exception occured activating the JNDI plugin"); // $NON-NLS-1$ $NLE-RelationalActivator.ExceptionoccuredactivatingtheJNDI-2$
                    }
                }
                return null;
            }
        });
    }
}