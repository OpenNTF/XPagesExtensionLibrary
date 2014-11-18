/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.utils;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;

/**
 * @author doconnor
 *
 */
public class XPagesKeyLookup extends StringLookup{
    private XPagesKey[] keys;

    /**
     * @param codes
     */
    public XPagesKeyLookup(XPagesKey[] keys, String[] codes) {
        super(codes);
        this.keys = keys;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.StringLookup#getCode(int)
     */
    @Override
    public String getCode(int index) {
        return super.getCode(index);
    }
    
    public XPagesKey getKey(int index){
        if(keys != null && keys.length > index){
            return keys[index];
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.iloader.node.lookups.api.StringLookup#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean equal = super.equals(obj);
        if(equal && obj instanceof XPagesKeyLookup){
            for(int i = 0; i < size(); i++){
                XPagesKey key1 = ((XPagesKeyLookup)obj).getKey(i);
                XPagesKey key2 = getKey(i);
                if(key1 == key2){
                    continue;
                }
                if(key1 == null || key2 == null){ //if we get to here it means that key1 and key2 are not identical 
                    equal = false;
                    break;
                }
                if(!key1.equals(key2)){
                    equal = false;
                    break;
                }
            }
        }
        return equal;
    }
}
