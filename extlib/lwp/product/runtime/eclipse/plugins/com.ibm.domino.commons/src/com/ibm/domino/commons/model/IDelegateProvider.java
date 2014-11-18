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

import lotus.domino.Database;

/**
 * Interface for managing access to mail and calendar features.
 */
public interface IDelegateProvider {

    public static final String DEFAULT_DELEGATE_NAME = "default"; // $NON-NLS-1$
    
    public Delegate get(Database database, String name) throws ModelException;
    
    public void set(Database database, Delegate delegate) throws ModelException;
    
    public void add(Database database, Delegate delegate) throws ModelException;
    
    public void delete(Database database, String name) throws ModelException;
    
    public List<Delegate> getList(Database database) throws ModelException;
    
    public DelegateAccess getEffectiveAccess(Database database) throws ModelException;
}