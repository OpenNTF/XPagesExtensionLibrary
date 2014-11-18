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

import java.util.List;

import lotus.domino.Session;

public interface ILookupProvider {
    
    /**
     * Finds a user in the public address book.
     * 
     * @param session
     * @param userName
     * @return
     * @throws ModelException
     */
    public MailUser findMailUser(Session session, String userName) throws ModelException;

    /**
     * Finds a server in the public address book.
     * 
     * @param session
     * @param serverName
     * @return
     * @throws ModelException
     */
    public Server findServer(Session session, String serverName) throws ModelException;
    
    /**
     * Finds all the replicas of a file listed in cldbdir.nsf.
     * 
     * @param session
     * @param cldbServer The name of the server from which to open cldbdir.nsf.
     * @param homeServer The name of the home server.
     * @param filePath The path of the file on the home server.
     * @return
     * @throws ModelException
     */
    public List<Replica> findClusterReplicas(Session session, String cldbServer, 
                            String homeServer, String filePath) throws ModelException;
}
