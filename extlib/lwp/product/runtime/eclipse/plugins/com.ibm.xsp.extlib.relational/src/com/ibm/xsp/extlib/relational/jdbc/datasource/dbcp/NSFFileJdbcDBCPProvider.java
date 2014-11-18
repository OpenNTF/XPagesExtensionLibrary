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
package com.ibm.xsp.extlib.relational.jdbc.datasource.dbcp;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.w3c.dom.Document;

import com.ibm.commons.xml.DOMAccessor;
import com.ibm.commons.xml.XMLException;
import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.jdbc.datasource.AbstractFileJdbcPoolProvider;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


/*
<jdbc type="dbcp">
<driver>com.ibm.db2.jcc.DB2Driver</driver>
<url>jdbc:db2://$host$:$port$/$DB$</url>
<user>$user$</user>
<password>$pwd$</password>
<dbcp>
    <minIdle>2</minIdle>
    <maxActive>8</maxActive>
    <maxWait>2000</maxWait>
</dbcp>
</jdbc>
*/

public class NSFFileJdbcDBCPProvider extends AbstractFileJdbcPoolProvider {

    public NSFFileJdbcDBCPProvider() { 
        type = "dbcp"; // $NON-NLS-1$
    }
    
    public IJdbcResourceFactory loadConnection(Document doc, String name)  throws XMLException, PoolException  {
           
             // Common parameters
                String driver = getStringValue(doc, "/jdbc/driver", null); // $NON-NLS-1$
                String url = getStringValue(doc, "/jdbc/url", null); // $NON-NLS-1$
                String user = getStringValue(doc, "/jdbc/user", null); // $NON-NLS-1$
                String password = DOMAccessor.getStringValue(doc, "/jdbc/password"); // $NON-NLS-1$
                // dbcp pool parameters
                int minIdle = getIntValue(doc, "/jdbc/dbcp/minIdle",GenericObjectPool.DEFAULT_MIN_IDLE); // $NON-NLS-1$
                int maxIdle = getIntValue(doc, "/jdbc/dbcp/maxIdle",GenericObjectPool.DEFAULT_MAX_IDLE); // $NON-NLS-1$
                int maxActive = getIntValue(doc, "/jdbc/dbcp/maxActive",GenericObjectPool.DEFAULT_MAX_ACTIVE); // $NON-NLS-1$
                long maxWait = getLongValue(doc, "/jdbc/dbcp/maxWait",GenericObjectPool.DEFAULT_MAX_WAIT); // $NON-NLS-1$
                DbcpPoolDataSource dbcpDS =  new DbcpPoolDataSource(
                        name, 
                        driver,
                        url,
                        user,
                        password,
                        minIdle,
                        maxIdle,
                        maxActive,
                        maxWait
                        );
                return dbcpDS;
           
    }
    
}
    
    
    
    

