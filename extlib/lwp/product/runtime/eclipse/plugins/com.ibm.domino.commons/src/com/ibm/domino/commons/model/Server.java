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

public class Server {

    private String _serverName;
    private String _hostName;
    private String _clusterName;
    private boolean _imsaServer;
    
    public Server(String serverName, String hostName, String clusterName, boolean imsaServer) {
        _serverName = serverName;
        _hostName = hostName;
        _clusterName = clusterName;
        _imsaServer = imsaServer;
    }

    /**
     * Gets the server's name.
     * 
     * @return the serverName
     */
    public String getServerName() {
        return _serverName;
    }

    /**
     * Gets the server's Internet host name.
     * 
     * @return the internetAddress
     */
    public String getHostName() {
        return _hostName;
    }

    /**
     * @return the clusterName
     */
    public String getClusterName() {
        return _clusterName;
    }

    /**
     * @return the imsaServer
     */
    public boolean isImsaServer() {
        return _imsaServer;
    }
    
}
