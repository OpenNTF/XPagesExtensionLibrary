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

dojo.provide("dwa.lv.widgetListener");

dojo.require("dwa.common.listeners");
dojo.require("dwa.lv.globals");

dojo.declare(
	"dwa.lv.widgetListener",
	dwa.common.scriptListener,
{
	constructor: function(sId, sName){
		this.sId = sId;
		this.sName = sName;
	// start - defined(LATER)
		this.nElapsed = 0;
	// end - defined(LATER)
		this.avArguments = Array.prototype.slice.call(arguments, 2);
	
		var asName = this.sName.split(':');
		var sUrl = dwa.lv.globals.get().dojo_io_widgetListener_getUrl(this.oClasses[this.sName]);
		var fFunctionLoaded = true; //window[asName[0]] && (!asName[1] || window[asName[0]].prototype && window[asName[0]].prototype[asName[1]]);
		var fLoaded = !this.oClasses[this.sName] && fFunctionLoaded || !sUrl;
	
		if (fLoaded && !fFunctionLoaded) {
			var sLog = 'No widget code for ' + this.sId + ':' + this.sName + ' is found.';
			dwa.lv.globals.get().oStatusManager.addEntry(3, '', sLog);
			return;
		}
	
		// oBeingInitialized should be available only for widget constructor
		if(!asName[1])
			this.oBeingInitialized[this.sId + ':' + asName[0]] = {};
	
		if (!fLoaded) {
			this.track();
			this.load(sUrl);
		} else {
			this.onDatasetComplete();
		}
	},
	onReadyStateChange: function (){
	  return this.onDatasetComplete(arguments);
	},
	onDatasetComplete: function(sReadyState){
		var asName = this.sName.split(':');
		var sWidgetId = this.sId + ':' + asName[0];
	
		// oBeingInitialized should be available only for widget constructor
		if (!asName[1] && !this.oBeingInitialized[sWidgetId]) {
			var sLog = 'Widget ' + sWidgetId + ' is being released... Bailing.';
			dwa.lv.globals.get().oStatusManager.addEntry(3, '', sLog);
			this.fToBeReleased = true;
			return;
		}
	
		var sUrl = dwa.lv.globals.get().dojo_io_widgetListener_getUrl(this.oClasses[this.sName]);
		var fFunctionLoaded = true; //window[asName[0]] && (!asName[1] || window[asName[0]].prototype && window[asName[0]].prototype[asName[1]]);
		var fLoaded = !this.oClasses[this.sName] && fFunctionLoaded || !sUrl;
		// oBeingInitialized should be available only for widget constructor
		if (!asName[1] && fLoaded)
			delete this.oBeingInitialized[sWidgetId];
		if (!fLoaded || !asName[1] && this.oWidgets[sWidgetId])
			return;
	
		var sLog = 'Widget ' + this.sId + ':' + this.sName + ' is being created.';
		dwa.lv.globals.get().oStatusManager.addEntry(3, '', sLog);
	
		if (!asName[1]) {
			this.oWidgets[sWidgetId] = new window[asName[0]](this.sId);
			if (this.oInitialized[sWidgetId])
				this.oInitialized[sWidgetId]();
	
		} else {
			window[asName[0]].prototype[asName[1]].apply(this.oWidgets[sWidgetId], this.avArguments);
		}
	
		this.fToBeReleased = true;
	},
	oWidgets: {},
	oClasses: {},
	oLoaded: {},
	oInitialized: {},
	oBeingInitialized: {},
	nAutoGenId: 0
});
