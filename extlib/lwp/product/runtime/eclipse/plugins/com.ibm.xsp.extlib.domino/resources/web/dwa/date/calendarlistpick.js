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

dojo.provide("dwa.date.calendarlistpick");

dojo.require("dwa.common.commonProperty");
dojo.require("dwa.date.calendar");
dojo.require("dwa.common.menu");

dojo.declare(
	"dwa.date.calendarlistpick",
	null,
{
	// ---------------------------------------------------------
	// calendar list picker base
	// extender need to set properties:
	//  nEntries: # of the entries
	// extender need to implement:
	//  getFormatter: specify formatter
	//  adjustCalendar: to get 'n'th index from given calendar.
	//  needUpdate: determine if caption should be updated.
	// --------------------------------------------------------
	constructor: function(sId){
		if(!sId)
			return;
		var oElem = dojo.doc.getElementById(this.sId = sId);
		var sStartOffset = oElem.getAttribute(this.sClass + '_startOffset');
		this.nStartOffset = sStartOffset ? sStartOffset * 1 : 0;
		var oProperty = dwa.common.commonProperty.get(this.com_ibm_dwa_misc_observes_calendar = oElem.getAttribute('com_ibm_dwa_misc_observes_calendar'));
		oProperty.attach(this);
		this.observe(oProperty);
		if(this.nStartOffset)
			this.nActiveIndex = 0 - this.nStartOffset;
		
		this.menuInfo = [];
		var captions = this.getCaptions();
		var actions = this.getActions();
		for(var i = 0; i < captions.length; i++){
			this.menuInfo[i] = {
				label: captions[i],
				action: actions[i],
				context: this,
				scope: this
			};
		}
		this.menu = new dwa.common.menu({
			menuInfo: this.menuInfo,
			activeIndex: this.nActiveIndex
		}, oElem);
		this.menu.startup();
	},
	observe: function(oProperty){
		if(oProperty.isLatest && !oProperty.isLatest())
			return;
		this.oCalendar = oProperty.vValue ? (new dwa.date.calendar).setISO8601String(oProperty.vValue)
		 : (new dwa.date.calendar).setDate(new Date);
		if('function' == typeof(this.onCalendarUpdated))
			this.onCalendarUpdated();
	},
	destroy: function() {
		this.menu.destroy();
		dwa.common.commonProperty.get(this.com_ibm_dwa_misc_observes_calendar).detach(this);
	},
	getCaptions: function(){
		if(this.needUpdate()){
			this.asCaptions = [];
			var oCalendar = this.oCalendar.clone();
			var oFormatter = this.getFormatter();
			if(this.nStartOffset)
				oCalendar = this.adjustCalendar(oCalendar, this.nStartOffset);
			for(var i = 0; i < this.nEntries; i++){
				this.asCaptions.push(oFormatter.format(oCalendar));
				oCalendar = this.adjustCalendar(oCalendar, 1);
			}
			this.oPrevCalendar = this.oCalendar.clone();
		}
		return this.asCaptions;
	},
	getActions: function(){
		if(this.asActions.length == 0){
			for(var i = 0; i < this.nEntries; i++) {
				this.asActions[i] = (function(i){
					return function(){
						return this.entrySelected(i);
					}
				})(i);
			}
		}
		return this.asActions;
	},
	refresh: function(){
		if(!this.needUpdate())
			return;
		var oElem = dojo.doc.getElementById(this.sId);
		var captions = this.getCaptions();
		for(var i = 0; i < this.nEntries; i++){
			this.menuInfo[i].label = captions[i];
		}
		this.menu.refresh();
	},
	entrySelected: function(nIndex){
		this.oCalendar = this.adjustCalendar(this.oCalendar, nIndex + (this.nStartOffset ? this.nStartOffset : 0));
		var oProp = dwa.common.commonProperty.get(this.com_ibm_dwa_misc_observes_calendar);
	
		if(oProp.vValue){
			var oOrgCalendar = (new dwa.date.calendar).setISO8601String(oProp.vValue);
			oOrgCalendar.set(this.oCalendar);
			oProp.setValue('' + oOrgCalendar);
		}else
			oProp.setValue('' + this.oCalendar);
	}
});
