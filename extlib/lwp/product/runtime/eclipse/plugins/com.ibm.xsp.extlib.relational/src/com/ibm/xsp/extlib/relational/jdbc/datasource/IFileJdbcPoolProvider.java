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
/*
* Date: 20 Feb 2014
* IFileJdbcPoolProvider.java
*/

package com.ibm.xsp.extlib.relational.jdbc.datasource;

import org.w3c.dom.Document;

import com.ibm.commons.xml.XMLException;
import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


public interface IFileJdbcPoolProvider {

    abstract IJdbcResourceFactory loadConnection(Document doc, String name)  throws XMLException, PoolException;
    abstract String getType();
}
