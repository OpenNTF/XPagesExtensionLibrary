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

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

/**
 * @author doconnor
 *
 */
public class ExtLibToolingLogger extends Log{
    public static LogMgr EXT_LIB_TOOLING_LOGGER  = load("com.ibm.xsp.extlib.designer.tooling", "Logger used for logging events relating the XPages Extension Library tooling"); // $NON-NLS-1$ $NLX-ExtLibToolingLogger.Loggerusedforloggingeventsrelatin-2$
}