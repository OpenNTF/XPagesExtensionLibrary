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

/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 29 Jul 2011
* ExtlibCoreLogger.java
*/
package com.ibm.xsp.extlib.log;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * @author Andrejus Chaliapinas
 */
public class ExtlibCoreLogger extends Log {

    /**
     * Log group for problems in the controls in the /extlib-data.xsp-config file, or their renderers.
     * [Individual log groups may be toggled to a more verbose logging level, 
     * to give finer debugging logging, rather than the default level of info.]
     */
    public static final LogMgr COMPONENT_DATA = load("com.ibm.xsp.extlib.component.data"); //$NON-NLS-1$

    /**
     * Log group for relational support controls
     */
    public static final LogMgr RELATIONAL = load("com.ibm.xsp.extlib.relational"); //$NON-NLS-1$
    
    
    /**
     * Log group for the sbt plugin
     */
    public static final LogMgr SBT = load("com.ibm.xsp.extlib.sbt"); //$NON-NLS-1$
    /**
     */
    public static final LogMgr CORE = load("com.ibm.xsp.extlib.core"); //$NON-NLS-1$
}
