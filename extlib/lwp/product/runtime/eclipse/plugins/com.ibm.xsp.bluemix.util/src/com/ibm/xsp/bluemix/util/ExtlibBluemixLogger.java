/*
 * © Copyright IBM Corp. 2015
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
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 13 Apr 2015
* ExtlibBluemixLogger.java
*/
package com.ibm.xsp.bluemix.util;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibBluemixLogger extends Log {
    /**
     * Log group for relational support controls
     */
    public static final LogMgr BLUEMIX = load("com.ibm.xsp.bluemix.util"); //$NON-NLS-1$

}
