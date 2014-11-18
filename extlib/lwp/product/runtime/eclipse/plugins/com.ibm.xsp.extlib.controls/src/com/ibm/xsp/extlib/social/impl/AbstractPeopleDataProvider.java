/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.social.impl;



/**
 * People data provider.
 * <p>
 * Basic implementation of a people provider.
 * </p>
 * @author Philippe Riand
 */
public abstract class AbstractPeopleDataProvider extends AbstractResourceProvider implements PeopleDataProvider {

    public static final String KEY_CACHE = "extlib.social.peoplecache"; // $NON-NLS-1$

    public AbstractPeopleDataProvider() {
    }
    
    @Override
    protected final String getCacheScopeProperty() {
        return "extlib.social.people."+getName().toLowerCase()+".cachescope"; // $NON-NLS-1$ $NON-NLS-2$
    }
    @Override
    protected final String getCacheSizeProperty() {
        return "extlib.social.people."+getName().toLowerCase()+".cachesize"; // $NON-NLS-1$ $NON-NLS-2$
    }
    
    /**
     * Get a value from the person and a key.
     * This call might require a call to an actual service/database, or it returns it
     * from the object cache.
     * @param person the person object to fill
     * @param key the property key
     * @return the value of the property
     */
    public abstract Object getValue(PersonImpl person, Object key);
    
    /**
     * Get the type of a property.
     * @param person the person object to query
     * @param key the property key
     * @return the property type
     */
    public abstract Class<?> getType(PersonImpl person, Object key);
    
    /**
     * Ensure that a set of persons has its property is memory. 
     * @param persons
     */
    public abstract void readValues(PersonImpl[] persons);
}