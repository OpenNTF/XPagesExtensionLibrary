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

dojo.provide("dwa.data.iCalReadStore");

dojo.require("dwa.data._CalendarStoreBase");
dojo.require("dwa.date.calendar");
dojo.require("dwa.common.utils");

dojo.declare("dwa.data.iCalReadStore",
	dwa.data._CalendarStoreBase,
{
	//	summary:
	//		A read only data store for iCal calendar data
	//	description:
	//		A data store for iCal calendar data. (cf. rfc2445)

	fontColorEvent: '395D94',
	bgColorEvent: 'FF8242',
	borderColorEvent: '5495D5',

	fontColorMeeting: '395D94',
	bgColorMeeting: 'FF82BD',
	borderColorMeeting: '5495D5',

	getUrl: function(request){
		return this.url;
	},
	_fetchItems: function(request){
		var _this = this;
		if(!request.query){ request.query = {}; }
		var filter = function(requestArgs, arrayOfItems){
			var start = -1, end = -1;
			var cal1, cal2;
			if(requestArgs.query.startDate){
				cal1 = (new dwa.date.calendar).setISO8601String(requestArgs.query.startDate);
				cal1.fDateOnly = false;
				start = cal1.getDate().getTime();
			}
			if(requestArgs.query.endDate){
				cal2 = (new dwa.date.calendar).setISO8601String(requestArgs.query.endDate);
				cal2.fDateOnly = false;
				end = cal2.getDate().getTime();
			}

			var items = [];
			for(var i = 0, len = arrayOfItems.length; i < len; i++){
				var item = arrayOfItems[i];

				if(start != -1 && end != -1 && item.vEvent["RRULE"]){
					_this._processRules(items, item, cal1, cal2);
					continue;
				}

				var cal3 = (new dwa.date.calendar).setISO8601String(item.startDateTime);
				cal3.fDateOnly = true;
				var t = cal3.getDate().getTime();
				if(start != -1 && start > t){ continue; }
				if(end != -1 && end < t){ continue; }
				items.push(item);
			}
			_this.fetchHandler(items, requestArgs, items.length);
		};

		if(this._loaded){
//cache code around this has not been working before, and, actually cache in the data store layer at this time is no meaning today because view widget already have the view cache in it.
//if we'd like to cache the data in this layer, it must be more complicated, for example, indexed by start date and end date duration regarding to the query parameter from view widget.//_ak
			filter(request, this._items);
		}else{
			var getArgs = {
				url: this.getUrl(request),
				handleAs: "text"
			};
			var _this = this;
			var deferred = dojo.xhrGet(getArgs);
			deferred.addCallback(function(data){
				_this._items = _this._processData(data);
				filter(request, _this._items);
//"_this" has been _this before, thus the "cache" intention here has not been worked , but it is meaningless as I wrote in the comment above.//_ak
//				_this._loaded = true;
			});
			deferred.addErrback(function(error){
				alert("errback called: "+error.description);
			});
		}
	},

	_createNewItem: function(item, cal, mDuration){
		var newItem = {};//dojo.clone(item);
		for(var k in item){
			if(k != this._storeRef)
				newItem[k] = dojo.clone(item[k]);
			else
				newItem[k] = item[k];
		}
		
		var _startCal = (new dwa.date.calendar).setDate(cal.getDate(), cal.oZoneInfo);
		var oDate = cal.getDate();
		oDate.setTime(oDate.getTime() + mDuration);
		var _endCal = (new dwa.date.calendar).setDate(oDate, cal.oZoneInfo);
		
		if(item.allDay){
			_startCal.fDateOnly = _endCal.fDateOnly = true;
		}
		newItem.startDateTime = _startCal.getISO8601String();
		newItem.endDateTime = _endCal.getISO8601String();
		return newItem;
	},

	_checkNthOccurrenceOfMonth: function(cal, pos){
		var _cal = cal.clone();
		var sign = dwa.common.utils.sign(pos);
		_cal.adjustDays(0, 0, -7 * (pos - sign));
		if(_cal.nMonth == cal.nMonth){
			_cal.adjustDays(0, 0, -7 * sign);
			if(_cal.nMonth != cal.nMonth){
				return true;
			}
		}
		return false;
	},

	_processRules: function(items, item, startCal, endCal){
		var specs = item.vEvent["RRULE"].split(/;/);
		var recur = {};
		for(var i = 0, len = specs.length; i < len; i++){
			var arr = specs[i].split(/=/);
			recur[arr[0]] = arr[1];
		}

		var itemStartCal = (new dwa.date.calendar).setISO8601String(item.startDateTime);
		var itemEndCal = (new dwa.date.calendar).setISO8601String(item.endDateTime);
		itemStartCal = itemStartCal.setDate(itemStartCal.getDate());
		itemEndCal = itemEndCal.setDate(itemEndCal.getDate());
		var mDuration = itemEndCal.getDate().getTime() - itemStartCal.getDate().getTime();
		var untilCal = recur["UNTIL"] ? (new dwa.date.calendar).setISO8601String(recur["UNTIL"]) : null;
//to be revised: may some enumlation frame and visitor classes needed to support various type of recurrence
		if(recur["COUNT"]){
			var nAdjust = (recur["COUNT"] * 1) - 1;
			untilCal = itemStartCal.clone();
			untilCal.adjustDays(0, (recur["FREQ"] == "MONTHLY" ?nAdjust : 0), (recur["FREQ"] == "WEEKLY" ? nAdjust * 7 : nAdjust));
			//dwa.date.calendar.compare needs the two instances in same timezone.
			untilCal.setDate(untilCal.getDate(), startCal.oZoneInfo);
		}

		var byday = [];
		if(recur["BYDAY"]){
			// ex. BYDAY=TU,TH
			var weekday = { SU:0, MO:1, TU:2, WE:3, TH:4, FR:5, SA:6 };
			var days = recur["BYDAY"].split(/,/);
			for(var i = 0, len = days.length; i < len; i++){
				byday[weekday[days[i]]] = true;
			}
		}else if(recur["COUNT"])
			byday[itemStartCal.getDate().getDay()] = true;

		var cal = null;
		var dailyEventStarted = false;
		do{
			if(!cal){
				cal = startCal.clone();
				var tmp = (new dwa.date.calendar).setDate(itemStartCal.getDate(), startCal.oZoneInfo);
				for(var k in {nHours:void 0,nMinutes:void 0,nSeconds:void 0, nMilliseconds:void 0}){
					cal[k] = tmp[k];
				}
				//dwa.date.calendar.compare needs the two instances in same timezone.
				cal.setDate(cal.getDate(), startCal.oZoneInfo);
			}else{
				cal.adjustDays(0, 0, 1);
			}
			if(untilCal && cal.compare(untilCal) > 0){ break; }
			if(cal.compare(itemStartCal) < 0){ continue; }
			if(recur["FREQ"] == "YEARLY"){
				var month, date;
				if(recur["BYMONTH"]){
					month = recur["BYMONTH"] - 0;
					if(recur["BYMONTHDAY"]){
						date = recur["BYMONTHDAY"] - 0;
					}else if(recur["BYDAY"]){
						if(byday[cal.getDate().getDay()]){
							if(recur["BYSETPOS"]){
								if(this._checkNthOccurrenceOfMonth(cal, recur["BYSETPOS"] - 0)){
									date = cal.nDate;
								}
							}else{
								date = cal.nDate;
							}
						}
					}
				}else{
					month = itemStartCal.nMonth + 1;
					date = itemStartCal.nDate;
				}
				if(month == cal.nMonth + 1 && date == cal.nDate){
					items.push(this._createNewItem(item, cal, mDuration));
				}
			}else if(recur["FREQ"] == "MONTHLY"){
				var date;
				if(recur["BYMONTHDAY"]){
					date = recur["BYMONTHDAY"] - 0;
				}else{
					date = itemStartCal.nDate;
				}
				if(date == cal.nDate){
					items.push(this._createNewItem(item, cal, mDuration));
				}
			}else if(recur["FREQ"] == "WEEKLY"){
				if(byday.length > 0){
					if(byday[cal.getDate().getDay()]){
						items.push(this._createNewItem(item, cal, mDuration));
					}
				}
			}else if(recur["FREQ"] == "DAILY"){
				if(cal.compare(itemStartCal) >= 0 && !dailyEventStarted){ dailyEventStarted = true; }
				if(dailyEventStarted){
					items.push(this._createNewItem(item, cal, mDuration));
				}
			}
			
		}while(cal.compare(endCal) <= 0);
	},

	_processVEvent: function(vEvent){
		var item = {vEvent: vEvent};
		var key;
//_ak removing Zoneinfo element until we support VTIMEZONE component in iCalendar.
		key = "DTSTART";
		if(vEvent[key]){
			var params = vEvent[key][0];
			var value = vEvent[key][1];
			if(params["VALUE"] == "DATE"){
				item.startDateTime = value + "T000000";
				item.type = "Event";
				item.iconParam = 'colicon1.gif 13 11 39 209';
				item.allDay = true;
			}else{
				item.startDateTime = value;
				item.type = "Meeting";
				item.iconParam = 'colicon1.gif 13 11 117 220';
			}
		}

		key = "DTEND";
		if(vEvent[key]){
			var params = vEvent[key][0];
			var value = vEvent[key][1];
			if(params["VALUE"] == "DATE"){
				item.endDateTime = value + "T000000";
//_ak tentative
//iCalendar is 
				if(item.allDay){//need to ensure if DTSTART is followed by DTEND when this logic is accepted..
					var tmpcal = (new dwa.date.calendar).setISO8601String(value + "T235959");
					tmpcal.adjustDays(0, 0, -1);
					tmpcal.fDateOnly = true;
					item.endDateTime = tmpcal.getISO8601String() + 'T235959';
				}
//_ak tentative
			}else{
				item.endDateTime = value;
			}
		}

		key = "SUMMARY";
		if(vEvent[key]){
			item.subject = vEvent[key];
		}

		key = "UID"
		if(vEvent[key]){
//_ak//todo: need to fill some information like calendar id and provider id here.
//also, need to consider about if unid is the proper name to be used for common calendar so that view widget can determine if it is from notes db or not..
//making new field like uid and put notes provider id and database id and unid in it would be a straightfoward way.
			item.unid = vEvent[key];
		}

		if (!item.bgColor1)
			item.bgColor1 = this.oColorMap[item.type + '-bg-dark'];
		if (!item.bgColor2)
			item.bgColor2 = this.oColorMap[item.type + '-bg-light'];
		if (!item.fontColor)
			item.fontColor = this.oColorMap[item.type + '-font'];
		if (!item.borderColor)
			item.borderColor = this.oColorMap[item.type + '-border'];
		if (!item.storeTitle)
			item.storeTitle = this.storeTitle;
		item[this._storeRef] = this;
		return item;
	},

	_processData: function(data){
		var items = [];
		var lines = data.split(/[\r\n]+/);
		var vEvent = null;
		for(var i = 0, len = lines.length; i < len; i++){
			var arr = lines[i].split(/:/);
			if(arr.length > 2){
				arr[1] = arr.slice(1).join(":");
			}
			if(arr[0] == "BEGIN" && arr[1] == "VEVENT"){
				vEvent = {};
			}else if(arr[0] == "END" && arr[1] == "VEVENT"){
				items.push(this._processVEvent(vEvent));
				vEvent = null;
			}else if(vEvent != null){
				if(arr[0].indexOf("DTSTART") == 0 || arr[0].indexOf("DTEND") == 0){
					var params = {};
					var a1 = arr[0].split(/;/);
					for(var j = 1; j < a1.length; j++){
						var a2 = a1[j].split(/=/);
						params[a2[0]] = a2[1];
					}
					vEvent[a1[0]] = [params, arr[1]];
				}else{
					vEvent[arr[0]] = arr[1];
				}
			}
		}
		return items;
	}
});
