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

package com.ibm.xsp.extlib.designer.bluemix;

import com.ibm.commons.log.Log;
import com.ibm.commons.log.LogMgr;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class BluemixLogger extends Log{
    
    public static LogMgr BLUEMIX_LOGGER  = load(BluemixPlugin.PLUGIN_ID, BluemixUtil.productizeString("Logger used for logging events relating to the Designer %BM_PRODUCT% tooling")); // $NLX-BluemixLogger.Loggerusedforloggingeventsrelatin-1$
}