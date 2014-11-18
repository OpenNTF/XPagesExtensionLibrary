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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.social.PeopleService;
import com.ibm.xsp.extlib.social.Person;
import com.ibm.xsp.extlib.social.Resource;
import com.ibm.xsp.extlib.social.SocialService;
import com.ibm.xsp.extlib.social.SocialServicesFactory;
import com.ibm.xsp.extlib.util.debug.DumpAccessor;
import com.ibm.xsp.extlib.util.debug.DumpAccessorFactory;
import com.ibm.xsp.extlib.util.debug.DumpContext;
import com.ibm.xsp.extlib.util.debug.JavaDumpFactory;


/**
 * Social Object Dump Factory.
 */
public class SocialDumpFactory implements DumpAccessorFactory {

    private static final String DEFAULT_CATEGORY = "Common Fields"; // $NON-NLS-1$
    private static final String SCOPE_AND_SIZE = "_Scope & Size"; // $NON-NLS-1$

    public SocialDumpFactory() {
    }
    
    public DumpAccessor find(DumpContext dumpContext, Object o) {
        if(o instanceof Person) {
            return new PersonMap(dumpContext,(Person)o);
        }
        return null;
    }
    
    public static abstract class ResourceMap extends JavaDumpFactory.AbstractMap {
        private SocialService service;
        private Resource r;
        public ResourceMap(DumpContext dumpContext, Resource r) {
            super(dumpContext);
            this.r = r;
            this.service = getSocialService();
        }
        public Resource getResource() {
            return r;
        }
        @Override
        public String getStringLabel() {
            return null;
        }
        @Override
        public String getTypeAsString() {
            return "Person"; // $NON-NLS-1$
        }
        @Override
        public String[] getCategories() {
            ArrayList<String> l = new ArrayList<String>();
            l.add(DEFAULT_CATEGORY);
            ResourceDataProvider[] providers =  service.getResourceDataProviders();
            for(int i=0; i<providers.length; i++) {
                l.add(providers[i].getName());
            }
            return l.toArray(new String[l.size()]);
        }
        protected abstract SocialService getSocialService();
        @Override
        public void getAllPropertyKeys(String category, List<Object> list) {
            if(StringUtil.equals(DEFAULT_CATEGORY,category)) {
                list.add(Person.FIELD_ID);
                list.add(Person.FIELD_DISPLAYNAME);
                list.add(Person.FIELD_THUMBNAIL_URL);
                list.add(Person.FIELD_OWNER);
                list.add(Person.FIELD_VIEWER);
                return;
            }
            
            ResourceDataProvider p = findDataProvider(category);
            if(p!=null) {
                list.add(category+':'+SCOPE_AND_SIZE);
                Set<String> keys = new HashSet<String>(); 
                p.enumerateProperties(keys);
                for(String s: keys) {
                    list.add(category+':'+s);
                }
            }
        }
        @Override
        public Object getProperty(Object key) {
            String sk = (String)key;
            int pos = sk.indexOf(':');
            if(pos>=0) {
                String cat = sk.substring(0,pos);
                sk = sk.substring(pos+1);
                if(sk.equals(SCOPE_AND_SIZE)) {
                    ResourceDataProvider p = findDataProvider(cat);
                    return StringUtil.format("{0} [{1}]", cacheScopeString(p.getCacheScope()), Integer.toString(p.getCacheSize()));
                } else {
                    return r.getFieldByProvider(cat, sk);
                }
            }
            // Get the property as is...
            return r.getField(sk);
        }
        private static String cacheScopeString(int scope) {
            switch(scope) {
                case ResourceDataProvider.SCOPE_NONE:           return "none"; // $NON-NLS-1$
                case ResourceDataProvider.SCOPE_GLOBAL:         return "global"; // $NON-NLS-1$
                case ResourceDataProvider.SCOPE_APPLICATION:    return "application"; // $NON-NLS-1$
                case ResourceDataProvider.SCOPE_SESSION:        return "session"; // $NON-NLS-1$
                case ResourceDataProvider.SCOPE_REQUEST:        return "request"; // $NON-NLS-1$
            }
            return "???"+scope;
        }
        protected ResourceDataProvider findDataProvider(String name) {
            ResourceDataProvider[] providers =  service.getResourceDataProviders();
            for(int i=0; i<providers.length; i++) {
                if(StringUtil.equals(name,providers[i].getName())) {
                    return providers[i];
                }
            }
            return null;
        }
    }
    public static class PersonMap extends ResourceMap {
        public PersonMap(DumpContext dumpContext, Person p) {
            super(dumpContext,p);
        }
        @Override
        public String getTypeAsString() {
            return "Person"; // $NON-NLS-1$
        }
        @Override
        protected PeopleService getSocialService() {
            return SocialServicesFactory.getInstance().getPeopleService();
        }
   }
}