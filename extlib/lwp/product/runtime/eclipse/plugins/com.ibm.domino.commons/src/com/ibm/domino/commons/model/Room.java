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

public class Room {
    
    private String _displayName;
    private String _distinguishedName;
    private String _domain;
    private String _emailAddress;
    private int _capacity;

    public Room(String displayName, String distinguishedName, String domain,
                    String emailAddress, int capacity) {
        _displayName = displayName;
        _distinguishedName = distinguishedName;
        _domain = domain;
        _emailAddress = emailAddress;
        _capacity = capacity;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * @return the distinguishedName
     */
    public String getDistinguishedName() {
        return _distinguishedName;
    }
    
    public String getDomain() {
        return _domain;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return _emailAddress;
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return _capacity;
    }

}
