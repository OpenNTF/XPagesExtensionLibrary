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

dojo.provide("dwa.lv.globals");

dojo.require("dwa.lv.cookie");

dojo.declare(
	"dwa.lv.globals",
	null,
{
	sobj: 0,

	constructor: function(){
        if( dojo.isMozilla || dojo.isWebKit ){ this.oXPathEvaluator = new XPathEvaluator(); }
		//this.oUnreadCountManager = {};
		//this.oStatusManager = {};
		this.oSettings = {};

		if( dojo.isMozilla ){
			this.nIntBrowserVer = 0;
			var asMatch = navigator.userAgent.match(/rv:([0-9\.]+)/i);
			for (var asParts = asMatch[1] ? asMatch[1].split('.') : [], i = 0; i < asParts.length; i++)
				this.nIntBrowserVer += (asParts[i] - 0) * Math.pow(100, -1 * i);
		}
	},

	oStatusManager: {
		addEntry: function(nLevel, sId, sMessage){
			if (nLevel == 0)
				alert(sMessage);
			else
			 if( dojo.isMozilla ){
				dump(nLevel + ': ' + sMessage + '\n');
			 }else if( dojo.isWebKit ){ // G
				console.log(nLevel + ': ' + sMessage + '\n');
			 }else{ // S
				Debug.write(nLevel + ': ' + sMessage + '\n');
			 } // end - I
		}
	},
	oStrings: {
	//	sErrBadHttpStatus: "Bad HTTP status: %1 (%2)",
	//	sErrBadContentType: "Bad content type: %1",
	//	sErrLoadFailure: "Error encountered retrieving data: %1",
	//	sLoadingData: "Loading data...",
		sLoadingDataComplete: "Loading data complete."
	//	sCheckingSession: "Checking session...",
	//	sCheckingSessionComplete: "Checking session complete.",
	//	sEntryNotFound: "Entry not found"
	},

	dojo_io_widgetListener_getUrl: function(asChunks){
		if (!asChunks)
			return;

		var asChunksToLoad = [];

		for (var i = 0; i < asChunks.length; i++) {
			if (!dwa.lv.widgetListener.prototype.oLoaded[asChunks[i].toLowerCase()])
				asChunksToLoad.push(asChunks[i] + 'script');
		}

		if (!asChunksToLoad[0])
			return;

		return asChunksToLoad.join('_') + '.js';
	},
	buildResourcesUrl: function(sResource, bExt){
		return dojo.moduleUrl("dwa.common", "images/" + sResource); // kami
	}
});

dwa.lv.globals.get = function(){
	if( dwa.lv.globals.prototype.sobj === 0 ){
		dwa.lv.globals.prototype.sobj = new dwa.lv.globals;
	}
	return dwa.lv.globals.prototype.sobj;
};

dwa.lv.globals.invokeActionDummy = function(event, sId, sClass, oContext){
	// NOP
};

dwa.lv.globals.setupShimmerCookie = function setupShimmerCookie(oLocation){
		if (!oLocation)
			oLocation = location;
		// Create a cookie object which is valid for any path on current db but discarded when
		//  window is closed
		var sCookiePath = location.pathname.substring(0, location.pathname.toLowerCase().indexOf('.nsf') + 4);
		sCookiePath = (sCookiePath.indexOf('/') != 0 ? '/' : '') + sCookiePath + '/';
	
		var bStore = false;
		
		// Global gCookie var which will later be assigned to haiku object
		// bs == browser suffix (for design elements)
		// bo == browser overide (used to trigger override of current browser)
		dwa.lv.globals.get().gCookie = new dwa.lv.cookie( window.document, "Shimmer", null, sCookiePath );
		dwa.lv.globals.get().gCookie.load();
	
		var asMatch = oLocation.search.match(new RegExp('&' + "ui" + '=([^&]+)', 'i'));
		var sUI = asMatch ? asMatch[1] : '';
	
		if( sUI.length ) {
			if( dwa.lv.globals.get().gCookie.acc != null && sUI.toLowerCase()!= "inotes_acc")
				delete dwa.lv.globals.get().gCookie.acc;
	
			switch(sUI.toLowerCase()) {
			case "dwa_lite":
			case "inotes_lite":
				dwa.lv.globals.get().gCookie.ui = "L";
				break;
			case "inotes":
				dwa.lv.globals.get().gCookie.ui = "I";
				break;
	
			default:
				break;
			}
			if( dwa.lv.globals.get().gCookie.ui )
				bStore = true;
		}
		else if(-1 != oLocation.search.indexOf("h_SkinTypeOverride;"+"h_Portal")) {
			dwa.lv.globals.get().gCookie.ui = "P";
			bStore = true;
		}
	
		var oMap = {
			"client": ["wps", 'ct', "P"],
			"ra": ["0", 'ra'],
			"wa": ["0", 'wa'],
			"im": ["0", 'STCS', '' + 0]
		};
	
		for (var s in oMap) {
			var asMatch = oLocation.search.match(new RegExp('&' + s + '=([^&]+)', 'i'));
			if (!asMatch || !asMatch[1])
				continue;
	
			var sName = oMap[s][1];
	
			if (asMatch[1].toLowerCase() == oMap[s][0])
				dwa.lv.globals.get().gCookie[sName] = oMap[s][oMap[s][2] ? 2 : 0];
			else if (dwa.lv.globals.get().gCookie[sName])
				delete dwa.lv.globals.get().gCookie[sName];
	
			bStore = true;
		}
	
		if(bStore)
			dwa.lv.globals.get().gCookie.store();
	
};
