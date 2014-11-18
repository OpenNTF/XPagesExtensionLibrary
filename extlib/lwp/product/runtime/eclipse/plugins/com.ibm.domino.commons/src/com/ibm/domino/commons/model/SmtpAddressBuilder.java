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

package com.ibm.domino.commons.model;

import java.net.URI;

import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * SMTP address builder.
 * 
 * <p>This class is intended to encapsulate the outbound address construction rules
 * in a Global Domain Document.
 */
public class SmtpAddressBuilder {
    
    private String _internetHost;
    private boolean _includeNotesDomain;
    private String _notesDomainSeparator;
    
    private SmtpAddressBuilder(String internetHost, boolean includeNotesDomain, String notesDomainSeparator) {
        _internetHost = internetHost;
        _includeNotesDomain = includeNotesDomain;
        _notesDomainSeparator = notesDomainSeparator;
    }
    
    /**
     * Get an SMTP address builder.
     * 
     * @param session
     * @return
     * @throws ModelException
     */
    public static SmtpAddressBuilder get(Session session) throws ModelException {
        
        SmtpAddressBuilder builder = null;
        
        try {
            String internetHost = null;
            String serverUrl = session.getHttpURL();
            URI uri = null;
            
            try {
                uri = URI.create(serverUrl);
            }
            catch (Throwable e) {
                // Handle unchecked exceptions from URI.create
                throw new ModelException(e.getMessage(), e);
            }
            
            internetHost = uri.getHost();

            // TODO: Get the build rules from the server's default Global
            // Domain Document
            
            //builder = new SmtpAddressBuilder(internetHost, true, "%");
            builder = new SmtpAddressBuilder(internetHost, false, null);
        }
        catch (NotesException e) {
            throw new ModelException(e.getMessage(), e);
        }

        return builder;
    }

    /**
     * Build an SMTP address based on the builder rules.
     * 
     * @param distinguishedName
     * @param notesDomain
     * @return
     */
    public String build(String distinguishedName, String notesDomain) {
        StringBuilder address = new StringBuilder();
        
        address.append(distinguishedName.replace(" ", "_"));
        if ( _includeNotesDomain ) {
            address.append(_notesDomainSeparator);
            address.append(notesDomain);
        }
        
        address.append("@");
        address.append(_internetHost);
        
        return address.toString();
    }
}
