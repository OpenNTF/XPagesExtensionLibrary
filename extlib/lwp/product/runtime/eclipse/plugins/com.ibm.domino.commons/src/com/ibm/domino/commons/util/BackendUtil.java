/*
 * © Copyright IBM Corp. 2013
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

package com.ibm.domino.commons.util;

import java.util.Iterator;
import java.util.Vector;

import lotus.domino.Base;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.domino.commons.model.ITrustProvider;
import com.ibm.domino.commons.model.ProviderFactory;

public class BackendUtil {

    /**
     * Safely recycle a backend object.
     * 
     * @param obj
     */
    public static void safeRecycle(Base obj) {
        
        if ( obj != null ) {
            try {
                obj.recycle();
            }
            catch (NotesException e) {
                // Ignore exceptions inside recycle
            }
        }
    }
    
    /**
     * Safely recycle a vector of backend objects.
     * 
     * @param list
     */
    public static void safeRecycle(Vector list) {
        if ( list != null ) {
            Iterator iterator = list.iterator();
            while ( iterator.hasNext() ) {
                Object obj = iterator.next();
                if ( obj instanceof Base ) {
                    safeRecycle((Base)obj);
                }
            }
        }
    }

    /**
     * Gets a trusted session.
     * 
     * @param session
     * @return
     */
    public static Session getTrustedSession(Session session) {
        Session trustedSession = null;
        
        try {
            ITrustProvider provider = ProviderFactory.getTrustProvider();
            if ( provider != null ) {
                trustedSession = provider.getTrustedSession(session);
            }
        }
        catch (Throwable e) {
            // Ignore all exceptions
        }
        
        return trustedSession;
    }
}
