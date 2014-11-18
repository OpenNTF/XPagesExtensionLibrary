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

/**
 * Immutable class representing a directory on a server.
 */
public class Directory {
    
    private String _serverName;
    private String _filePath;
    private String _displayName;
    
    /**
     * Constructs a directory object.
     * 
     * @param serverName The canonical server name.
     * @param filePath The file path including (the file name).
     * @param displayName The display name or title.
     */
    public Directory(String serverName, String filePath, String displayName) {
        _serverName = serverName;
        _filePath = filePath;
        _displayName = displayName;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return _serverName;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return _filePath;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return _displayName;
    }

}
