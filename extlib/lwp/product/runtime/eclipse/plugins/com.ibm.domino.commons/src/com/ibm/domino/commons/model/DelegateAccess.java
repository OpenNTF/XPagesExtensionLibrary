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
 * PIM delegate access level.
 */
public class DelegateAccess {
    
    private What _what;
    private boolean _read;
    private boolean _create;
    private boolean _delete;
    private boolean _edit;
    
    public enum What {
        NOTHING,
        CALENDAR,   // Implies access to contacts and tasks
        MAIL        // Implies access to calendar, contacts and tasks
    }
    
    /**
     * Constructs a VALID delegate access instance.
     * 
     * <p> If the input arguments are invalid, this constructor makes sure
     * the instance is internally consistent.  For example, when you have access
     * to calendar (not mail), you can't have edit & delete access.  Also, when you
     * have access to mail, edit access implies read & create access.
     * 
     * @param what
     * @param read
     * @param create
     * @param delete
     * @param edit
     */
    public DelegateAccess(What what, boolean read, boolean create, boolean delete, boolean edit) {
        if ( what == null ) {
            _what = What.NOTHING;
        }
        else {
            _what = what;
        }
        
        if ( _what == What.CALENDAR ) {
            // Access to calendar, precludes edit & delete access
            if ( create ) {
                _read = true;
                _create = true;
                _edit = true;
                _delete = true;
            }
            else if ( read ) {
                _read = true;
            }
        }
        else if ( _what == What.MAIL ) {
            _delete = delete;
            if ( edit ) {
                _read = true;
                _create = true;
                _edit = true;
            }
            else if ( create ) {
                _read = true;
                _create = true;
            }
            else if ( read ) {
                _read = true;
                _delete = false; // A reader can't delete things
            }

        }
    }

    /**
     * @return the what
     */
    public What getWhat() {
        return _what;
    }

    /**
     * @return the read
     */
    public boolean isRead() {
        return _read;
    }

    /**
     * @return the create
     */
    public boolean isCreate() {
        return _create;
    }

    /**
     * @return the delete
     */
    public boolean isDelete() {
        return _delete;
    }

    /**
     * @return the edit
     */
    public boolean isEdit() {
        return _edit;
    }

}
