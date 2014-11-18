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

dojo.provide("dwa.lv.miscs");

dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.notesListViewProfileCache");

dwa.lv.miscs = {};

dwa.lv.miscs.getNotesListViewProfileCache = function(){
	    if(!dwa.lv.globals.get().oViewProfileCache)
	        dwa.lv.globals.get().oViewProfileCache = new dwa.lv.notesListViewProfileCache();
	    return dwa.lv.globals.get().oViewProfileCache;
};

dwa.lv.miscs.convertSnapshot = function(oResult){
		var aoResults = [];
		for (var i = 0; i < oResult.snapshotLength; i++)
			aoResults[i] = oResult.snapshotItem(i);
		return aoResults;
};

dwa.lv.miscs.deserialize = function(sXml){
	 if( dojo.isMozilla || dojo.isWebKit ){
			var parser = new DOMParser();
			oXmlDoc = parser.parseFromString(sXml, "text/xml");
			if (oXmlDoc.firstChild.tagName == 'parsererror') {
				throw new Error(oXmlDoc.firstChild.textContent);
			}
	 }else{ // GS
			var oXmlDoc = new ActiveXObject("MSXML2.DOMDocument");
			oXmlDoc.async=0;
			oXmlDoc.resolveExternals = 0;
			if(!oXmlDoc.loadXML(sXml)){
	  			var err = oXmlDoc.parseError;
	  			var sReason = err.reason + "Location " + oXmlDoc.url + " Line Number " + err.line + " Column " + err.linepos;
	  			dwa.lv.globals.get().oStatusManager.addEntry(0, '', sReason, true);
	  			throw new Error(sReason);
			}
	 } // end - I
		return oXmlDoc;
};

