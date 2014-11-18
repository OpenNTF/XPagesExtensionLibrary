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

dojo.provide("dwa.cv.calendarDataStore");


dojo.declare(
	"dwa.cv.calendarDataStore",
	null,
{
	constructor: function(oWidget){
		this.oWidgets       = [];	// Registered Widgets
		this.oDataLoaders   = [];	// Registered calendar view listener
		this.aoEventsById   = {};	// Event summary by sequence number
		this.aoEventsByDate = {};	// Event summary
		this.afModified     = {};	// True if event summary is added
		this.afSorted       = {};	// True if event summary is sorted
		this.afChecked      = {};	// True if event summary is checked for conflicts
		this.afAdded        = {};	// True if event summary is added to specified date/unid
		this.nIndex         = 0;	// Sequential number
		this.fnFilters      = [];	// Filter functions to show/hide calendar entries
		this.aoFilterContexts = [];	// Contexts to pass to filter functions
	},
	registerViewWidget: function(oWidget){
		this.oWidgets[this.oWidgets.length] = oWidget;
	},
	unregisterViewWidget: function(oWidget){
		for(var i = 0; i < this.oWidgets.length; i++){
			if(this.oWidgets[i] == oWidget){
				this.oWidgets.splice(i, 1);
				delete(this.oStartDate);
				delete(this.oEndDate);
				break;
			}
		}
	},
	registerDataLoader: function(oDataLoader){
		this.oDataLoaders[this.oDataLoaders.length] = oDataLoader;
	
		// refresh view immediately
		if (!oDataLoader.fDisabled && this.oStartDate && this.oEndDate)
			this.refresh();
	},
	unregisterDataLoader: function(oDataLoader){
		for (var i=0; i<this.oDataLoaders.length; i++) {
			if (this.oDataLoaders[i] == oDataLoader) {
				if (this.oDataLoaders[i].fDisabled) {
					this.oDataLoaders.splice(i, 1);
				} else {
					// need recalc
					for (sDate in this.oDataLoaders[i].afCached)
						this.afModified[sDate] = true;
					// unregister
					this.oDataLoaders.splice(i, 1);
					// Refresh view without reloading
					this.refresh();
					break;
				}
			}
		}
	},
	load: function(oStartDate, oEndDate){
		this.nLoading = 0;
	
		for (var i=0; i<this.oDataLoaders.length; i++) {
			if (!this.oDataLoaders[i].fDisabled) {
				// check for cache for each single data store
				var fInCache = true;
				var oDate1 = oStartDate.clone();
				var oDate2 = oEndDate.clone();
				oDate1.fDateOnly = oDate2.fDateOnly = true;
				for (; oDate1.getDate().getTime() < oDate2.getDate().getTime(); oDate1.adjustDays(0,0,1)) {
					if (!this.oDataLoaders[i].afCached[oDate1.getISO8601String()]) {
						fInCache = false;
						break;
					}
				}
	
				// minimize date range to load
				if (!fInCache) { 
					oDate2.adjustDays(0,0,-1);
					for (; oDate1.getDate().getTime() < oDate2.getDate().getTime(); oDate2.adjustDays(0,0,-1)) {
						if (!this.oDataLoaders[i].afCached[oDate2.getISO8601String()])
							break;
					}
				}
	
				if (!fInCache) {
					// fixed wrong date range to be loaded in One day view (SPR VSEN7R728S)
					oDate2.adjustDays(0,0,1);
					// clear cache
					for (var oDate = oDate1.clone(); oDate.getDate().getTime() < oDate2.getDate().getTime(); oDate.adjustDays(0,0,1)) {
						var sDate = oDate.getISO8601String();
						this.afModified[sDate] = true;
						this.afAdded[sDate] = {};
						this.oDataLoaders[i].afCached[sDate] = false;
						this.oDataLoaders[i].aoEventsByDate[sDate] = [];
					}
					if (this.oDataLoaders[i].clear)
						this.oDataLoaders[i].clear();
//	#ifdef LATER	
					// No need to check fExternal if we apply the same fix to notes calendar as well.
//	#endif //LATER
					// load data - initial loading OR any reloading after initial loading is complete for a specific view range
					var sKey = oDate1.getDate().valueOf()+oDate2.getDate().valueOf();
					var fReadyState = this.oDataLoaders[i].fExternal &&  (!this.oDataLoaders[i].onReadyState[sKey] ||this.oDataLoaders[i].onReadyState[sKey] == 4);
					
					if (fReadyState)
						this.oDataLoaders[i].onReadyState[sKey] = 1; // Loading...
	
					if (!this.oDataLoaders[i].fExternal || fReadyState){
//						this.oDataLoaders[i].load(oDate1, oDate2);
						var store = this.oDataLoaders[i];
						var _this = this;
						var query = {};
						query.startDate = oDate1.getISO8601String();
						query.endDate = oDate2.getISO8601String();
						var fetch = {
							query: query,
							queryOptions: {}, 
							onComplete: function(result, requestObject){
								dojo.hitch(_this, "addEvents")(requestObject.store, result);
							},
							onError: function(errText){
								alert('calendarDataStore: ' + errText);
							}
						};
						store.fetch(fetch);
					}
					
					this.nLoading ++;
				}
			}
		}
		
		this.oStartDate = oStartDate;
		this.oEndDate = oEndDate;
	
		// at least one calendar loader already has stored data in cache. we need to display calendar entries immediately.
		if (this.nLoading < this.oDataLoaders.length)
			this.onDatasetComplete();
	},
	refresh: function(){
		if (this.oStartDate && this.oEndDate)
			this.load(this.oStartDate, this.oEndDate);
	},
	isLoading: function(){
		return (this.nLoading > 0);
	},
	onDatasetComplete: function(oCaller, oStart, oEnd){
		if (oCaller) {
			var oStartTime = oStart? oStart.clone(): this.oStartDate.clone();
			var oEndTime = oEnd? oEnd.clone(): this.oEndDate.clone();
			oStartTime.fDateOnly = oEndTime.fDateOnly = true;
			for (; oStartTime.getDate().getTime() < oEndTime.getDate().getTime(); oStartTime.adjustDays(0,0,1))
				oCaller.afCached[oStartTime.getISO8601String()] = true;
			this.nLoading --;
		}
	
		for (var i=0; i<this.oWidgets.length; i++)
			this.oWidgets[i].onDatasetComplete();
	},
	clear: function(){
		this.aoEventsById   = {};
		this.afAdded = {};
		this.aoEventsByDate = {};
		this.afModified     = {};
		this.afSorted       = {};
		this.afChecked      = {};
		this.nIndex         = 0;
	
		for (var i=0; i<this.oDataLoaders.length; i++) {
			this.oDataLoaders[i].afCached = {};
			this.oDataLoaders[i].aoEventsByDate = {};
			if (this.oDataLoaders[i].clear)
				this.oDataLoaders[i].clear();
		}
	},
	reload: function(){
		if (this.oStartDate && this.oEndDate) {
			this.clear();
			this.refresh();
		}
	},
	addEvent: function(oEvent, oCaller){
		if (!oEvent || !oCaller)
			return;
	
		oEvent.oStartDate.fDateOnly = true;
		var sDate = oEvent.oStartDate.getISO8601String();
		var sUnid = oEvent.sUnid;
	
		// prevent Google all day event being added duplicately (SPR VSEN7VUU54 and NBJC7R39K8)
		if (sUnid && this.afAdded[sDate] && this.afAdded[sDate][sUnid])
			return;
	
		if (!oCaller.aoEventsByDate[sDate])
			oCaller.aoEventsByDate[sDate] = [];
		
		if (!oEvent.sBGColor2)
			oEvent.sBGColor2 = this.gradiateHSV(oEvent.sBGColor1, 0.4, 0.7);
		
		oEvent.nIndex = this.nIndex;
		oCaller.aoEventsByDate[sDate][oCaller.aoEventsByDate[sDate].length] = oEvent;
		this.aoEventsById[this.nIndex++] = oEvent;
		this.afModified[sDate] = true;
	
		if (!this.afAdded[sDate])
			this.afAdded[sDate] = {};
		this.afAdded[sDate][sUnid] = true;
	},
	getEventsByDate: function(oStartDate, fDontCheckConflict){
		if (!oStartDate)
			return [];
	
		var sDate = oStartDate.getISO8601String();
		if (this.afModified[sDate]) {
			var n = 0;
			this.aoEventsByDate[sDate] = [];
			// gather event summaries from all data stores
			for (var i=0; i<this.oDataLoaders.length; i++) {
				if (!this.oDataLoaders[i].fDisabled) {
					var aoEvents = this.oDataLoaders[i].aoEventsByDate[sDate];
					if (!aoEvents)
						continue;
					for (var j=0; j<aoEvents.length; j++)
						if (!this.filterEvent(aoEvents[j]))
							this.aoEventsByDate[sDate][n++] = aoEvents[j];
				}
			}
			this.afModified[sDate] = this.afSorted[sDate] = this.afChecked[sDate] = false;
		}
	
		if (!this.aoEventsByDate[sDate])
			return [];
	
		if (!this.afSorted[sDate]) {
			this.sort(oStartDate);
			this.afSorted[sDate] = true;
		}
	
		if (!this.afChecked[sDate] && !fDontCheckConflict) {
			this.checkForConflicts(oStartDate);
			this.afChecked[sDate] = true;
		}
	
		return this.aoEventsByDate[sDate];
	},
	getEventById: function(nIndex){
		return this.aoEventsById[nIndex];
	},
	sort: function(oStartDate){
		var fnSort = function (oElem1, oElem2) {
			if (oElem1.bAllday)
				return -1;
			else if (oElem2.bAllday)
				return 1;
			else if (!oElem1.oStartTime.equals(oElem2.oStartTime))
				return (oElem1.oStartTime.getDate() - oElem2.oStartTime.getDate());
			else
				return (oElem1.oEndTime.getDate() - oElem2.oEndTime.getDate());
		};
	
		var sDate = oStartDate.getISO8601String();
		this.aoEventsByDate[sDate].sort(fnSort);
	},
	checkForConflicts: function(oStartDate){
		var fnSort = function (oElem1, oElem2) {
			if (oElem1.bAllday)
				return -1;
			else if (oElem2.bAllday)
				return 1;
			else if (!oElem1.oStartTime.equals(oElem2.oStartTime))
				return (oElem1.oStartTime.getDate() - oElem2.oStartTime.getDate());
			else
				return (oElem1.oEndTime.getDate() - oElem2.oEndTime.getDate());
		};
	
		var sDate = oStartDate.getISO8601String();
		this.aoEventsByDate[sDate].sort(fnSort);
	
		for (var i=0; i<this.aoEventsByDate[sDate].length; i++) {
			this.aoEventsByDate[sDate][i].bConflicted = this.aoEventsByDate[sDate][i].bExpandable = false;
			this.aoEventsByDate[sDate][i].nIndexInConflicts = 0;
			this.aoEventsByDate[sDate][i].nConflicts = 1;
			this.aoEventsByDate[sDate][i].anConflicts = [];
			if (!this.aoEventsByDate[sDate][i].bAllday && !this.aoEventsByDate[sDate][i].bHide) {
				var nMaxIndex = 0;
				var nMaxConflicts = 1;
				var anConflicts = {};
	
				var oStart1 = this.aoEventsByDate[sDate][i].oStartTime.getDate();
				var oEnd1 = this.aoEventsByDate[sDate][i].oEndTime ? this.aoEventsByDate[sDate][i].oEndTime.getDate(): null;
				if (oEnd1 == null) {
					var oEnd1 = new Date(oStart1);
					oEnd1.setTime(oEnd1.getTime() + 3600000);
				}
				for (var j=i-1; j>=0; j--) {
					if (!this.aoEventsByDate[sDate][j].bAllday && !this.aoEventsByDate[sDate][j].bHide) {
						var oStart2 = this.aoEventsByDate[sDate][j].oStartTime.getDate();
						var oEnd2 = this.aoEventsByDate[sDate][j].oEndTime? this.aoEventsByDate[sDate][j].oEndTime.getDate(): null;
						if (oEnd2 == null) {
							var oEnd2 = new Date(oStart2);
							oEnd2.setTime(oEnd2.getTime() + 3600000);
						}
						if (oStart1 < oEnd2 && oStart2 < oEnd1) {
							this.aoEventsByDate[sDate][i].bConflicted = this.aoEventsByDate[sDate][j].bConflicted = this.aoEventsByDate[sDate][i].bExpandable = true;
							this.aoEventsByDate[sDate][j].bExpandable = false;
							anConflicts[i] = anConflicts[j] = void 0;
							for (var n in this.aoEventsByDate[sDate][j].anConflicts)
								anConflicts[n] = void 0;
							nMaxIndex = Math.max(nMaxIndex, this.aoEventsByDate[sDate][j].nIndexInConflicts);
							nMaxConflicts = Math.max(nMaxConflicts, this.aoEventsByDate[sDate][j].nConflicts);
							oStart1 = oStart1 < oStart2? oStart1: oStart2;
							oEnd1   = oEnd1 < oEnd2? oEnd2: oEnd1;
						}
					}
				}
	
				if (this.aoEventsByDate[sDate][i].bConflicted) {
					this.aoEventsByDate[sDate][i].nIndexInConflicts = nMaxIndex + 1;
					this.aoEventsByDate[sDate][i].nConflicts        = Math.max(nMaxIndex + 2, nMaxConflicts);
					for (var n in anConflicts) {
						this.aoEventsByDate[sDate][n].nConflicts = Math.max(this.aoEventsByDate[sDate][n].nConflicts, this.aoEventsByDate[sDate][i].nConflicts);
						this.aoEventsByDate[sDate][n].anConflicts = anConflicts;
					}
				}
			}
		}
	},
	setActivityHandler: function(sActivityHandler){
		this.sActivityHandler = sActivityHandler;
	},
	enableDataLoader: function(oDataLoader, fEnable){
		for (var i=0; i<this.oDataLoaders.length; i++) {
			if (this.oDataLoaders[i] == oDataLoader) {
				if (this.oDataLoaders[i].fDisabled != !fEnable) {
					// change flag
					this.oDataLoaders[i].fDisabled = !fEnable;
					// need recalc
					for (sDate in this.oDataLoaders[i].afCached)
						this.afModified[sDate] = true;
					// Refresh view without reloading
					this.refresh();
					break;
				}
			}
		}
	},
	registerFilter: function(fnFilter, oContext){
		var bUpdated = false;
		for (var i=0; i<this.fnFilters.length; i++) {
			if (this.fnFilters[i] == fnFilter) {
				// already registered. just update oContext
				this.aoFilterContexts[i] = oContext;
				bUpdated = true;
				break;
			}
		}
	
		if (!bUpdated) {
			this.fnFilters[this.fnFilters.length] = fnFilter;
			this.aoFilterContexts[this.aoFilterContexts.length] = oContext;
		}
	
		// change modified flag
		for (sDate in this.afModified)
			this.afModified[sDate] = true;
	
		// verify immediately
		if (this.oStartDate && this.oEndDate)
			this.load(this.oStartDate, this.oEndDate);
	},
	unregisterFilter: function(fnFilter){
		for (var i=0; i<this.fnFilters.length; i++) {
			if (this.fnFilters[i] == fnFilter) {
				this.fnFilters.splice(i,1);
				this.aoFilterContexts.splice(i,1);
	
				// change modified flag
				for (sDate in this.afModified)
					this.afModified[sDate] = true;
	
				// verify immediately
				if (this.oStartDate && this.oEndDate)
					this.load(this.oStartDate, this.oEndDate);
				break;
			}
		}
	},
	filterEvents: function(oStartDate){
		var sDate = oStartDate.getISO8601String();
	
		if (!this.aoEventsByDate[sDate])
			return;
	
		for (i=0; i<this.aoEventsByDate[sDate].length; i++) {
			this.filterEvent(this.aoEventsByDate[sDate][i]);
		}
	},
	filterEvent: function(oEvent){
		var bHide = false;
		for (i=0; i<this.fnFilters.length; i++) {
			if (!this.fnFilters[i](oEvent, this.aoFilterContexts[i]))
				bHide = true;
		}
		oEvent.bHide = bHide;
		return bHide;
	},
	getEventList: function(oCaller){
		var aoEvents = [];
		for (sDate in oCaller.aoEventsByDate) {
			for (var i=0; i<oCaller.aoEventsByDate[sDate].length; i++) {
				aoEvents[aoEvents.length] = oCaller.aoEventsByDate[sDate][i];
			}
		}
		return aoEvents;
	},
	updateEvent: function(oEvent, oCaller){
		if (this.aoEventsById[oEvent.nIndex] != oEvent) {
			// This event is not added to data store
			this.addEvent(oEvent, oCaller);
		} else {
			// Event is already updated. Just set modified flag so that it is refreshed without data reloading.
			var sDate = oEvent.oStartTime.getISO8601String();
			this.afModified[sDate] = true;
		}
	},

	addEvents: function(store, result){
		var oListener = store;
		// came from notesCalendarLoader#format
		for (var i = 0; i < result.length; i++) {
			var item = result[i];
			if (!item)
				continue;
	
			var oStartTime, oStartDate, oEndTime, oEndDate;
			if(item.oStartDate){ // NotesCalendarStore has calculated objects
				oStartTime = item.oStartTime;
				oStartDate = item.oStartDate;
				oEndTime = item.oEndTime;
				oEndDate = item.oEndDate;
			}else{
				oStartTime = (new dwa.date.calendar).setISO8601String(store.getValue(item, "startDateTime"));
				oStartTime.setDate(oStartTime.getDate(), this.oWidgets[0].oCalendar.oZoneInfo);
				oStartDate = oStartTime.clone();
				oStartDate.fDateOnly = true;
				oEndTime = (new dwa.date.calendar).setISO8601String(store.getValue(item, "endDateTime"));
				oEndTime.setDate(oEndTime.getDate(), this.oWidgets[0].oCalendar.oZoneInfo);
				oEndDate = oEndTime.clone();
				oEndDate.fDateOnly = true;
			}
			// Fixed probem that all day event can be rescheduled when drag and drop within the same day
			if (store.getValue(item, "allDay"))
				oStartTime.fDateOnly = oEndTime.fDateOnly = true;

			var oEvent = {
				sUnid: store.getValue(item, "unid"),
				sType: store.getValue(item, "type"),
				sSubject: store.getValue(item, "subject"),
				sChair: store.getValue(item, "chair"),
				sAltChair: store.getValue(item, "altChair"),
				sLocation: store.getValue(item, "location"),
				oStartTime: oStartTime,
				oStartDate: oStartDate,
				oEndTime: oEndTime,
				oEndDate: oEndDate,
				sIconParam: store.getValue(item, "iconParam"),
				bAllday: store.getValue(item, "allDay"),
				sPrivate: store.getValue(item, "private"),
				sStatus: store.getValue(item, "status"),
				sBGColor1: store.getValue(item, "bgColor1"),
				sBGColor2: store.getValue(item, "bgColor2"),
				sFontColor: store.getValue(item, "fontColor"),
				sBorderColor: store.getValue(item, "borderColor"),
				sStoreTitle: store.getValue(item, "storeTitle"),
				_item: item,
				clone: function(){
					var oNewEvent = {};
					for (var s in this) {
						if (this[s] instanceof dwa.date.calendar)
							oNewEvent[s] = this[s].clone();
						else if (typeof this[s] != 'function')
							oNewEvent[s] = this[s];
					}
					return oNewEvent;
				}
			};
	
			if (oEvent.oEndTime.getDate().getTime() < oListener.oStartTime.getDate().getTime()) {
				// no need to add entries that ends before the date range (SPR MMII7TL446)
			} else if (oEvent.oStartDate.getISO8601String() != oEvent.oEndDate.getISO8601String()) {
				// add two entries if it overlaps next day (SPR MMII7TL446)
				var oNextEvent = oEvent.clone();
	
				oEvent.oEndDate = oEvent.oStartDate.clone();
				oEvent.oEndTime.nHours = oEvent.oEndTime.nMinutes = oEvent.oEndTime.nSeconds = oEvent.oEndTime.nMilliseconds = 0;
				this.addEvent(oEvent, store);
	
				
				oNextEvent.oStartDate = oNextEvent.oEndDate.clone();
				oNextEvent.oStartTime = oNextEvent.oEndDate.clone();
				oNextEvent.oStartTime.nHours = oNextEvent.oStartTime.nMinutes = oNextEvent.oStartTime.nSeconds = oNextEvent.oStartTime.nMilliseconds = 0;
				this.addEvent(oNextEvent, store);
			} else {
				this.addEvent(oEvent, store);
			}
		}
	
		oListener.oStartTime.fDateOnly = oListener.oEndTime.fDateOnly = true;
		var oStartTime = oListener.oStartTime.clone();
		var oEndTime = oListener.oEndTime.clone();
		for (; oStartTime.getDate().getTime() < oEndTime.getDate().getTime(); oStartTime.adjustDays(0,0,1))
			store.afCached[oStartTime.getISO8601String()] = true;
	
		this.onDatasetComplete(store, oListener.oStartTime, oListener.oEndTime);
	},
	gradiateHSV: function(darkerColor, saturationFactor, brillianceFactor){
		var rgb = this.convertRGB(darkerColor);						// convert string to r,g,b
		var hsv = this.convertRGB2HSV(rgb[0], rgb[1], rgb[2]);		// convert RGB to HSV
		var h = hsv[0];
		var s = hsv[1] * saturationFactor;
		var v = hsv[2] + ((1.0 - hsv[2])*( brillianceFactor ));
		rgb = this.convertHSV2RGB(h,s,v);							// convert HSV to RGB
		return 'rgb(' + rgb[0] + ',' + rgb[1] + ',' + rgb[2] + ')';
	},
	convertRGB: function(sColor1){
		// convert color from #RRBBGG format.
		if (sColor1.indexOf('#') == 0) {
			var r1 = parseInt(sColor1.slice(1,3), 16);
			var g1 = parseInt(sColor1.slice(3,5), 16);
			var b1 = parseInt(sColor1.slice(5,7), 16);
		}
		// convert color from rgb(RR,GG,BB) format.
		else if (sColor1.indexOf('rgb(') == 0) {
			var v = sColor1.replace('rgb(','').replace(')','').split(',');
			var r1 = v[0];
			var g1 = v[1];
			var b1 = v[2];
		}
		return [r1, g1, b1];
	},
	convertRGB2HSV: function(r,g,b){
		var max = Math.max(r,Math.max(g,b));
		var min = Math.min(r,Math.min(g,b));
		if (max == min)          var h = 0;
		else if (max == r)       var h = 60*(g-b)/(max-min);
		else if (max == g)       var h = 60*(b-r)/(max-min)+120;
		else /* if (max == b) */ var h = 60*(r-g)/(max-min)+240;
		if (h) h = (h+360)%360;
		if (max == 0) var s = 0;
		else          var s = (max-min)/max;
		var v = max/255;
		return [h,s,v];
	},
	convertHSV2RGB: function(h, s, v){
		var i = (Math.floor(h/60))%6;
		var f = h/60 - i;
		var p = v*(1.0-s);
		var q = v*(1.0-f*s);
		var t = v*(1.0-(1.0-f)*s);
		if (i==0)            var rgb = [v,t,p];
		else if (i==1)       var rgb = [q,v,p];
		else if (i==2)       var rgb = [p,v,t];
		else if (i==3)       var rgb = [p,q,v];
		else if (i==4)       var rgb = [t,p,v];
		else /* if (i==5) */ var rgb = [v,p,q];
		var r = Math.floor(255*rgb[0]);
		var g = Math.floor(255*rgb[1]);
		var b = Math.floor(255*rgb[2]);
		return [r,g,b];
	}
});

dwa.cv.calendarDataStore._instance = null;
dwa.cv.calendarDataStore.getInstance = function(){
	// summary: returns the calendarDataStore, creates one if it is not created yet
	if(!dwa.cv.calendarDataStore._instance){
		dwa.cv.calendarDataStore._instance = new dwa.cv.calendarDataStore();
	}
	return dwa.cv.calendarDataStore._instance;
};
