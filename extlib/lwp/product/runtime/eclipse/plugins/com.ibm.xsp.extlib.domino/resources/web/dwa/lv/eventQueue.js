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

dojo.provide("dwa.lv.eventQueue");

dojo.require("dwa.lv.widgetListener");

dojo.declare(
	"dwa.lv.eventQueue",
	null,
{
	soQueue: 0,

	constructor: function(){
		this.oQueue = {};
	},
	attach: function(oWidget, sType, fStopPropagation){
		var oElem = dojo.doc.getElementById(oWidget.sId);
		var sTarget = oElem.getAttribute('com_ibm_dwa_ui_widget_eventQueueTarget');
		var asTarget = sTarget ? sTarget.split(' ') : [oWidget.sId];
		var sClass = oElem.getAttribute('com_ibm_dwa_ui_widget_class');
		var asClass = sClass ? sClass.split(' ') : [];
		var sName = oWidget.sId + ':' + sClass;
		var oQueue = this.oQueue;
	
		function dojo_ui_eventQueue_handleEvent(ev){
		 if( dojo.isIE ){
			var oEvent = document.createEventObject(ev);
			oQueue[sName] = [oEvent].concat(oQueue[sName] ? oQueue[sName] : []);
		 }else{ // I
			var oEvent = document.createEvent('MouseEvents');
			oEvent.initMouseEvent(ev.type, ev.bubbles, ev.cancelable, window, ev.detail, ev.screenX, ev.screenY,
			 ev.clientX, ev.clientY, ev.ctrlKey, ev.altKey, ev.shiftKey, ev.metaKey, ev.button, null);
			oQueue[sName] = [oEvent, ev.target].concat(oQueue[sName] ? oQueue[sName] : []);
		 } // end - GS
			for(var i=0;i<asClass.length;i++)
				new dwa.lv.widgetListener(oWidget.sId, asClass[i] + ':handleEvent');
			if (fStopPropagation) {
				(dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );
		// #XMXL7Z65M6: Firefox 3.6 also needs this to avoid default context menu.
		 if(dojo.isMozilla || dojo.isWebKit){
				(dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );
		 } // end - S
				return false;
			}
		}
	
		for (var i = 0; i < asTarget.length; i++)
			(dojo.isIE ? (dojo.doc.getElementById(asTarget[i]).attachEvent("on" + sType, dojo_ui_eventQueue_handleEvent)) : (dojo.isMozilla ? (dojo.doc.getElementById(asTarget[i]).addEventListener(sType, dojo_ui_eventQueue_handleEvent, false)) : (dojo.doc.getElementById(asTarget[i]).addEventListener(sType, dojo_ui_eventQueue_handleEvent, false)) ) );
	}
});

dwa.lv.eventQueue.get = function(){
	if( dwa.lv.eventQueue.prototype.soQueue === 0 ){
		dwa.lv.eventQueue.prototype.soQueue = new dwa.lv.eventQueue;
	}
	return dwa.lv.eventQueue.prototype.soQueue
};
