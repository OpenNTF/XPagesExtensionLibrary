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
package com.ibm.xsp.extlib.designer;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;

/**
 * 
 *
 */
public class DesignerExtensionLogger extends Log{
    public static LogMgr CORE_LOGGER  = load("com.ibm.designer.extension.common", "Logger used for logging events relating the Designer Extension Library"); // $NON-NLS-1$ $NLX-DesignerExtensionLogger.Loggerusedforloggingeventsrelatin-2$
}