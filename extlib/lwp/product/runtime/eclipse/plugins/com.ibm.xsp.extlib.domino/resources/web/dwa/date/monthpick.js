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

dojo.provide("dwa.date.monthpick");

dojo.require("dwa.date.calendarlistpick");
dojo.require("dwa.date.dateFormatter");

var D_DateFmt_ShortMonth4YrOnly = 17;

dojo.declare(
	"dwa.date.monthpick",
	dwa.date.calendarlistpick,
{
	sClass: "com_ibm_dwa_ui_monthpick",
	nEntries: 12,
	asActions: [],
	
	getFormatter: function(){
		if(!this.oFormatter)
			this.oFormatter = new dwa.date.dateFormatter(D_DateFmt_ShortMonth4YrOnly);
		return this.oFormatter;
	},
	adjustCalendar: function(oCalendar, idx){
		return oCalendar.adjustDays(0, idx, 0);
	},
	needUpdate: function(){
		return (!this.oPrevCalendar || (this.oPrevCalendar.nYear != this.oCalendar.nYear) || (this.oPrevCalendar.nMonth != this.oCalendar.nMonth));
	}
});
