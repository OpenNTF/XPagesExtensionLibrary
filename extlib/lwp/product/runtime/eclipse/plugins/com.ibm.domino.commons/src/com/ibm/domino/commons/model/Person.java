/*
 * © Copyright IBM Corp. 2012
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
 * Person object
 */
public class Person {
    
    private String _displayName;
    private String _distinguishedName;
    private String _emailAddress;
    
    public Person() {
    }
    
    public Person(String displayName, String distinguishedName, String emailAddress) {
        _displayName = displayName;
        _distinguishedName = distinguishedName;
        _emailAddress = emailAddress;
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

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return _emailAddress;
    }

    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    /**
     * @param distinguishedName the distinguishedName to set
     */
    public void setDistinguishedName(String distinguishedName) {
        _distinguishedName = distinguishedName;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        _emailAddress = emailAddress;
    }

}
