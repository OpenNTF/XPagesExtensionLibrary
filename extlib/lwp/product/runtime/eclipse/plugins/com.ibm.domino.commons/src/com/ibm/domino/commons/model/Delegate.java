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
 * 
 */
public class Delegate {
    
    public static final String DEFAULT_NAME = "-Default-"; // $NON-NLS-1$

    private String _name;
    private String _email;
    private Type _type;
    private DelegateAccess _access;
    
    public enum Type {
        UNSPECIFIED,
        DEFAULT,
        PERSON,
        GROUP
    }

    /**
     * Constructs a VALID delegate object.
     * 
     * <p>For example, if access == null, this constructor creates a
     * default DelegateAccess object.
     * 
     * @param name
     * @param type
     * @param access
     */
    public Delegate(String name, Type type, DelegateAccess access) {
        if ( type == null ) {
            _type = Type.UNSPECIFIED;
        }
        else {
            _type = type;
        }
        
        if ( _type == Type.DEFAULT ) {
            _name = DEFAULT_NAME;
        }
        else {
            _name = name;
        }
        
        if ( access == null ) {
            _access = new DelegateAccess(DelegateAccess.What.NOTHING, false, false, false, false);
        }
        else {
            _access = access;
        }
    }

    /**
     * Constructs a delegate object with an email address instead of a name.
     * 
     * <p>You cannot use the resulting instance when adding a new delegate
     * or updating an existing delegate.  You must use the email address to
     * look up the delegate name.
     * 
     * @param email
     * @param access
     */
    public Delegate(String email, DelegateAccess access) {
        _type = Type.PERSON;
        _email = email;
        
        if ( access == null ) {
            _access = new DelegateAccess(DelegateAccess.What.NOTHING, false, false, false, false);
        }
        else {
            _access = access;
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return _type;
    }

    /**
     * @return the access
     */
    public DelegateAccess getAccess() {
        return _access;
    }

    public String getEmail() {
        return _email;
    }
}