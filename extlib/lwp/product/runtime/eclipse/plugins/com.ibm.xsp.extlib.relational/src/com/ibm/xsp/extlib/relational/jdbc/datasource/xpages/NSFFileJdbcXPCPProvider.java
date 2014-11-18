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
package com.ibm.xsp.extlib.relational.jdbc.datasource.xpages;

import org.w3c.dom.Document;

import com.ibm.commons.xml.DOMAccessor;
import com.ibm.commons.xml.XMLException;
import com.ibm.designer.runtime.util.pool.PoolException;
import com.ibm.xsp.extlib.relational.jdbc.datasource.AbstractFileJdbcPoolProvider;
import com.ibm.xsp.extlib.relational.resources.IJdbcResourceFactory;


/*
<jdbc type="simple">
<driver>com.ibm.db2.jcc.DB2Driver</driver>
<url>jdbc:db2://$host$:$port$/$DB$</url>
<user>$user$</user>
<password>$pwd$</password>
<simple>
    <maxPoolSize>8</maxPoolSize>
    <maxConnectionSize>6</maxConnectionSize>
</simple>
</jdbc>
*/

public class NSFFileJdbcXPCPProvider extends AbstractFileJdbcPoolProvider {
    
    public NSFFileJdbcXPCPProvider() {
        type = "simple"; // $NON-NLS-1$
    }

    public IJdbcResourceFactory loadConnection(Document doc, String name)  throws XMLException, PoolException  {
           
                // Common parameters
                String driver = getStringValue(doc, "/jdbc/driver", null); // $NON-NLS-1$
                String url = getStringValue(doc, "/jdbc/url", null); // $NON-NLS-1$
                String user = getStringValue(doc, "/jdbc/user", null); // $NON-NLS-1$
                String password = DOMAccessor.getStringValue(doc, "/jdbc/password"); // $NON-NLS-1$
                // Simple pool parameters
                int minPoolSize = getIntValue(doc, "/jdbc/simple/minPoolSize",JdbcPool.MIN_POOLSIZE); // $NON-NLS-1$
                int maxPoolSize = getIntValue(doc, "/jdbc/simple/maxPoolSize",JdbcPool.MAX_POOLSIZE); // $NON-NLS-1$
                int maxConnectionSize = getIntValue(doc, "/jdbc/simple/maxConnectionSize",JdbcPool.MAX_CONNECTION_NUMBER); // $NON-NLS-1$
                long useTimeout = getLongValue(doc, "/jdbc/simple/useTimeout",JdbcPool.USE_TIMEOUT); // $NON-NLS-1$
                long idleTimeout = getLongValue(doc, "/jdbc/simple/idleTimeout",JdbcPool.IDLE_TIMEOUT); // $NON-NLS-1$
                long maxLiveTime = getLongValue(doc, "/jdbc/simple/maxLiveTime",JdbcPool.MAXLIVETIME); // $NON-NLS-1$
                long acquireTimeout = getLongValue(doc, "/jdbc/simple/acquireTimeout",JdbcPool.ACQUIRE_TIMEOUT); // $NON-NLS-1$
                return new JdbcPoolDataSource(driver,url,user,password,minPoolSize,maxPoolSize,maxConnectionSize,useTimeout,idleTimeout,maxLiveTime,acquireTimeout);
           
    }    
}
    
    
    
    

