/*
 * © Copyright IBM Corp. 2010, 2014
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
package com.ibm.xsp.extlib.relational.resources;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.application.events.ApplicationListener;
import com.ibm.xsp.extlib.relational.RelationalLogger;


/**
 * Application listener for generating the data sources.
 * @author priand
 */
public class ApplicationInitializer implements ApplicationListener {
//public class ApplicationInitializer implements ApplicationListener2 { // POST CD5!
    
    public ApplicationInitializer() {
    }

    public void applicationCreated(ApplicationEx application) {
        try {
            JdbcDataSourceProvider.resetLocalProvider();
        } catch(Throwable ex) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "applicationCreated", ex, "Error occured resetting the local provider "); // $NON-NLS-1$ $NLE-ApplicationInitializer.Erroroccuredresettingthelocalprov-2$
            }
        }
    }

    public void applicationRefreshed(ApplicationEx application) {
        try {
            JdbcDataSourceProvider.resetLocalProvider();
        } catch(Throwable ex) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "applicationRefreshed", ex, "Error occured resetting the local provider "); // $NON-NLS-1$ $NLE-ApplicationInitializer.Erroroccuredresettingthelocalprov.1-2$
            }
        }
    }

    public void applicationDestroyed(ApplicationEx application) {
        try {
            JdbcDataSourceProvider.unregisterLocalProvider();
        } catch(Throwable ex) {
            if(RelationalLogger.RELATIONAL.isErrorEnabled()) {
                RelationalLogger.RELATIONAL.errorp(this, "applicationDestroyed", ex, "Error occured unregistering the local provider "); // $NON-NLS-1$ $NLE-ApplicationInitializer.Erroroccuredunregisteringthelocal-2$
            }
        }
    }
}