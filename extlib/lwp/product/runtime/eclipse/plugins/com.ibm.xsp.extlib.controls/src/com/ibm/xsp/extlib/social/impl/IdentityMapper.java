/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.social.impl;


/**
 * Identity mapper.
 * <p>
 * An identity mapper does the conversion between the id of a user, as it is
 * stored in the runtime UserPrincipal, and an identity coming from an external 
 * system (intranet, facebook...).
 * </p>
 * @author Philippe Riand
 */
public interface IdentityMapper {

    // Some target constants
    // Note that the identity targets are not limited to this list and can
    // be any arbitrary string
    public static final String  TARGET_DOMINO       = "domino";        // $NON-NLS-1$
    public static final String  TARGET_CONNECTIONS  = "connections";        // $NON-NLS-1$
    public static final String  TARGET_SAMETIME     = "sametime";        // $NON-NLS-1$
    public static final String  TARGET_LOTUSLIVE    = "lotuslive";        // $NON-NLS-1$
    public static final String  TARGET_FACEBOOK     = "facebook";        // $NON-NLS-1$
    public static final String  TARGET_TWEETER      = "tweeter";        // $NON-NLS-1$
    public static final String  TARGET_DROPBOX      = "dropbox";        // $NON-NLS-1$
    public static final String  TARGET_MYSPACE      = "myspace";        // $NON-NLS-1$
    public static final String  TARGET_GOOGLE       = "google";        // $NON-NLS-1$
    public static final String  TARGET_SHAREPOINT   = "sharepoint";        // $NON-NLS-1$
    public static final String  TARGET_MSN          = "msn";        // $NON-NLS-1$
    
    
    /**
     * Get a user identity from a user id.
     * @param target the identity target system
     * @param id
     * @return the user identity, or null if not avalaible
     */
    public String getUserIdentityFromId(String target, String id);
    
    /**
     * Get a user id from an identity
     * @param target the identity target system
     * @param identity
     * @return the user id, or null if not avalaible
     */
    public String getUserIdFromIdentity(String target, String identity);
}