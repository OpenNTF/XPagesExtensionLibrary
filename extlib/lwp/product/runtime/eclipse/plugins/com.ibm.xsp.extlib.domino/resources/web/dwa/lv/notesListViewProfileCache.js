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

dojo.provide("dwa.lv.notesListViewProfileCache");

dojo.require("dwa.lv.widgetListener");
dojo.require("dwa.lv.globals");

dojo.declare(
	"dwa.lv.notesListViewProfileCache",
	null,
{
	constructor: function(){
	    this.oProfileCache = {};
	},
	get: function(sItemName){
	    return this.oProfileCache[sItemName] ? this.oProfileCache[sItemName] : '';
	},
	set: function(sItemName, sValue, bUpdateProfile, sId){
	    if(typeof(this.oProfileCache[sItemName]) != 'undefined' && this.oProfileCache[sItemName] != sValue && bUpdateProfile && sId) {
	        dwa.lv.widgetListener.prototype.oClasses["com_ibm_dwa_io_actionStoreProfileField"] = ['Common'];
	        dwa.lv.globals.invokeActionDummy(null, sId, 'com_ibm_dwa_io_actionStoreProfileField', {sProfile:"iNotesViewProfile", sField:sItemName, sValue:sValue, oListener:null, bTextList:false});
	    }
	    this.oProfileCache[sItemName] = sValue;
	}
});
