/*
 * © Copyright IBM Corp. 2014
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

public class Customer {
    
    private String _id;
    private String _name;
    private boolean _hybrid;
    private boolean _selfTrial;
    
    public Customer(String id, String name, boolean hybrid, boolean selfTrial) {
        _id = id;
        _name = name;
        _hybrid = hybrid;
        _selfTrial = selfTrial;
    }

    /**
     * @return the id
     */
    public String getId() {
        return _id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return _name;
    }

    /**
     * @return the hybrid
     */
    public boolean isHybrid() {
        return _hybrid;
    }

    /**
     * @return the selfTrial
     */
    public boolean isSelfTrial() {
        return _selfTrial;
    }

}
