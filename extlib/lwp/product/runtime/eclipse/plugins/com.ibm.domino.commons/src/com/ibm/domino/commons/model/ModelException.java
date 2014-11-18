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
 * Generic exception for the com.ibm.domino.commons.model package
 */
public class ModelException extends Exception {

    public static final int ERR_GENERAL = 0;
    public static final int ERR_CONFLICT = 1;
    public static final int ERR_INVALID_INPUT = 2;
    public static final int ERR_NOT_FOUND = 3;
    public static final int ERR_NOT_ALLOWED = 4;
    public static final int ERR_OPENING_CLDBDIR = 5;

    private int _code = ERR_GENERAL;
    
    public ModelException(String message) {
        super(message);
    }

    public ModelException(String message, int code) {
        super(message);
        _code = code;
    }

    public ModelException(String message, Throwable t) {
        super(message, t);
    }
    
    public int getCode() {
        return _code;
    }
}
