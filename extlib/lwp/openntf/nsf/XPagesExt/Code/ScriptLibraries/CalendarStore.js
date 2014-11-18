// TODO mkehoe this is only temporarily present in the NSF
// as part of the effort to build a calendar view for the teamroom,
// it is mostly copied from the iNotes/DWA notes calendar store.

dojo.provide("xpagesext.CalendarStore");

dojo.require("dojo.data.util.simpleFetch");
dojo.require("dwa.date.calendar");
dojo.require("dwa.common.notesValue");

dojo.declare("xpagesext.CalendarStore", null, {
	//	summary:
	//		A data store for Notes Calendar
	//	description:
	//		A data store for Notes Calendar.
	url: "",
	_storeRef: "_S",

//	fontColorAppointment: '346410',
//	bgColorAppointment: 'C2F79F',
//	borderColorAppointment: '6DB23A',

//	fontColorAnniversary: '9258C0',
//	bgColorAnniversary: 'D7B4F2',
//	borderColorAnniversary: 'B57FE0',

//	fontColorEvent: 'C95F00',
//	bgColorEvent: 'FEF8C6',
//	borderColorEvent: 'E4842E',

//	fontColorMeeting: '2A6BAB',
//	bgColorMeeting: 'C1DDF9',
//	borderColorMeeting: '5495D5',

//	fontColorReminder: 'C95F00',
//	bgColorReminder: 'FFD28A',
//	borderColorReminder: 'E48E2E',

//	fontColorTodo: 'E57700',
//	bgColorTodo: 'FFF38A',
//	borderColorTodo: 'FDBB09',

//	fontColorUnprocessed: '646464',
//	bgColorUnprocessed: 'E6E6E6',
//	borderColorUnprocessed: 'B2B2B2',

//	fontColorCancelled: '000000',
//	bgColorCancelled: 'D88870',
//	borderColorCancelled: '000000',

	storeTitle: "",
	
	sDefaultEntryType: 'Meeting',
	pathInfo: "",
	axtarget: "",

	_typeMap: {
		'0': 'Appointment',
		'1': 'Anniversary',
		'2': 'Event',
		'3': 'Meeting',
		'4': 'Reminder',
		'To Do': 'Todo',
		'Unprocessed': 'Unprocessed',
		'Cancelled': 'Cancelled'
	},
	
	oColumnMap: {
		'$152':      'vType',
		'$134':   'vCalDate',
		'$144': 'vStartDate',
		'$146':   'vEndDate',
		'$149':  'vIcon',
		'$160':    'vStatus',
		'$147':      'vDescription',
		'$151':  'vAltDescription',
		'$154':   'vPrivate',
		'$153':     'vChair',
		'$UserData':          'vUserData'
	},
	

	constructor: function constructor(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}
		this.fDisabled = false;
		this.afCached = {};
		this.aoEventsByDate = {};
	
//		// init color map
//		this.initColorMap();
	},

//	getFeatures: function(){
//		return {
//			'dojo.data.api.Read': true
//		};
//	},
	// override
	getFeatures: function getFeatures(){
		return {
			'dojo.data.api.Read': true
//			,
//			'dojo.data.api.Write': true,
//			'dojo.data.api.Notification': true
		};
	},

	getValue: function getValue(item, attribute, defaultValue){
		var values = this.getValues(item, attribute);
		if(values && values.length > 0){
			return values[0];
		}
		return defaultValue;
	},

	getAttributes: function getAttributes(item){
		return [
			"unid", "type", "subject", "chair", "location", "startDateTime", "endDateTime",
			"iconParam", "overlay", "allDay", "bgColor1",
			"bgColor2", "fontColor", "borderColor", "repeat", "status",
			"generator", "hide", "private", "external", "originalEventId", "altChair"
		]; 
	},

	hasAttribute: function hasAttribute(item, attribute){
		if(this.getValue(item, attribute)){
			return true;
		}
		return false;
	},

	isItemLoaded: function isItemLoaded(item){
		 return this.isItem(item);
	},

	loadItem: function loadItem(keywordArgs){
	},

	getLabel: function getLabel(item){
		return this.getValue(item, this.label);
	},
	
	getLabelAttributes: function getLabelAttributes(item){
		return [this.label];
	},

	containsValue: function containsValue(item, attribute, value){
		var values = this.getValues(item,attribute);
		for(var i = 0; i < values.length; i++){
			if(values[i] === value){
				return true;
			}
		}
		return false;
	},

	getValues: function getValues(item, attribute){
		return [item[attribute]];
	},

	isItem: function isItem(item){
		if(item && item[this._storeRef] === this){
			return true;
		}
		return false;
	},
	
	close: function close(request){
	},

	errorHandler: function errorHandler(errorData, requestObject){
		if(requestObject.onError){
			var scope = requestObject.scope || dojo.global;
			requestObject.onError.call(scope, errorData, requestObject);
		}
	},

	fetchHandler: function fetchHandler(items, requestObject, numRows){
		var scope = requestObject.scope || dojo.global;
		if(!requestObject.store){
			requestObject.store = this;
		}
		if(requestObject.onBegin){
			requestObject.onBegin.call(scope, numRows, requestObject);
		}
		if(requestObject.onItem){
			for(var i = 0; i < items.length; i++){
				var item = items[i];
				requestObject.onItem.call(scope, item, requestObject);
			}
		}
		if(requestObject.onComplete){
			requestObject.onComplete.call(scope, items, requestObject);   
		}
	},

	fetch: function fetch(request){
		request = request || {};
		if(!request.store){
			request.store = this;
		}
		if(request.query && request.query.startDate){
			this.oStartTime = (new dwa.date.calendar).setISO8601String(request.query.startDate);
		}
		if(request.query && request.query.endDate){
			this.oEndTime = (new dwa.date.calendar).setISO8601String(request.query.endDate);
		}
		this._fetchItems(request);
		return request;
	},

//	_fetchItems: function(request){
//		// subclass must implement
//	},
	_fetchItems: function _fetchItems(request){
		if(!request.query){ return null; }
		
		
		
		
        // TODO fix, find ancestor form, rathre than first on page.
        var formNode = window.document.forms[0];
        
		//var url = this.url;
		//if(url.indexOf("://") != -1){
		//	// collect data from 24 hours before to get entries that overlaps next day (SPR MMII7TL446)
		//	var oValueStart = new dwa.common.notesValue(this.oStartTime.adjustDays(0,0,-1).getDate(), dwa.date.zoneInfo.prototype.oUTC);
		//	var oValueEnd = new dwa.common.notesValue(this.oEndTime.getDate(), dwa.date.zoneInfo.prototype.oUTC);
		//
		//	url += '?OpenDocument&Form=s_ReadViewEntries_JSON&Count=-1&KeyType=time&TZType=UTC'
		//		+ '&StartKey=' + oValueStart + '&UntilKey=' + oValueEnd + '&PresetFields=FolderName;(%24Calendar),hc;$151|$152|$153|$154|$160|$UserData';
		//}
		
		var url = formNode.getAttribute('action');
		var axtarget = this.axtarget;
		url = url + this.pathInfo;
		console.log("url = " + url);

		var getArgs = {
			url: url,
			axtarget: axtarget,
			handleAs: "json",
			preventCache: false
			//form: formNode,
			//content: {}
		};
		var _this = this;
		//var deferred = dojo.xhrPost(getArgs);
		var deferred = dojo.xhrGet(getArgs);
		deferred.addCallback(function(data){
			_this.items = _this.format(data);
			_this.fetchHandler(_this.items, request, _this.items.length);
		});
		deferred.addErrback(function(error){
			_this.errorHandler(error, request);
		});
	},
	format: function format(oResult){
		var items = [];
		var entries = [];
		
		// came from viewEntriesListener#onDatasetComplete
		var oEntries = (oResult.entries ? oResult.entries : oResult);
		this._numRows = oEntries["@toplevelentries"] - 0;
		var aoEntries = oEntries['viewentry'] instanceof Array ? oEntries['viewentry'] : oEntries['viewentry'] ? [oEntries['viewentry']] : [];
		
		for (var i = 0; i < aoEntries.length; i++) {
			var oItem = aoEntries[i];
			var asPos = oItem['@position'].split('.');
			var nPos = asPos[0] - 0;
			entries[nPos] = {oItem: oItem, aoValues: []};
		}

		// came from notesCalendarLoader#format
		for (var i = 0; i < entries.length; i++) {
			var oEntry = entries[i];
			if (!oEntry)
				continue;
			
			var aoEntryData = oEntry.oItem['entrydata'] instanceof Array ? oEntry.oItem['entrydata'] : [oEntry.oItem['entrydata']];
			var oData = {unid: oEntry.oItem['@unid']};
			
			for (var j = 0; j < aoEntryData.length; j++) {
				var sProp = this.oColumnMap[aoEntryData[j]['@name']];
				if (sProp)
					oData[sProp] = (new dwa.common.notesValue).setJsonNode(aoEntryData[j]).vValue;
			}
	
			// hide offline google entries (SPR SYPK7SQS55)
			if (typeof(oData.vUserData) == 'string' && oData.vUserData.length != 0)
				continue;
			
			oData.oCalDate   = oData.vCalDate   instanceof Array ? oData.vCalDate[0]   : oData.vCalDate;
			oData.oStartDate = oData.vStartDate instanceof Array ? oData.vStartDate[0] : oData.vStartDate;
			oData.oEndDate   = oData.vEndDate   instanceof Array ? oData.vEndDate[0]   : oData.vEndDate;
			
			try {
				// All day event's CalDate sometimes doesn't have time or timezone portions. (SPR GTON7QYFKM)
				if (oData.oCalDate instanceof dwa.date.calendar) {
					oData.oCalDate = oData.oCalDate.getDate();
				}

				// dummy entry for OOO sometimes doesn't have start/end date. (SPR YQWG7F49L7)
				if (!oData.oStartDate)
					oData.oStartDate = new Date(oData.oCalDate);
				if (!oData.oEndDate) {
					oData.oEndDate = new Date(oData.oStartDate);
					oData.oEndDate.setTime(oData.oEndDate.getTime() + 3600000);
				}

				var oEvent = this.createDummyEventSummary(oData);

				// dummy entry for OOO sometimes doesn't have appointment type. (SPR YQWG7F49L7)
				if (!oEvent.type) {
					oEvent.type = this.sDefaultEntryType;
				} else if (oEvent.type!="Appointment" && oEvent.type!="Anniversary" && oEvent.type!="Event" && oEvent.type!="Meeting" && oEvent.type!="Reminder") {
					oEvent.sType = "To Do"; // localization needs to be avoided
				}
				var nIcon = (oData.vIcon instanceof Array? oData.vIcon[0]: 0);
				if (nIcon == 187)
					oEvent.type = 'Unprocessed';	// unprocessed notice
				else if (nIcon == 200)
					oEvent.type = 'Cancelled';	// cancelled meeting
				oEvent.sPrivate = '' + (oData.vPrivate instanceof Array ? oData.vPrivate[0]: oData.vPrivate);
				var oStatusMap = {12: "Tentatively Accepted", 58: "Draft", 187: "Ghosts", 200: "Ghosts"};
				if (nIcon in oStatusMap) {
					oEvent.status = oStatusMap[nIcon];
				} else {
					var sStatus = oData.vStatus instanceof Array? oData.vStatus[0]: oData.vStatus;
					var oStatusMap = {"Accepted": "Accepted", "Tentatively Accepted": "Tentatively Accepted", "Draft": "Draft"};
					oEvent.status = sStatus in oStatusMap ? oStatusMap[sStatus] : sStatus;
				}

				if (!oEvent.bgColor1)
					oEvent.bgColor1 = '#C1DDF9'; //'#FF0000';
				if (!oEvent.borderColor)
					oEvent.bgColor2 = '#5495D5'; //'#0000FF';

//				if (!oEvent.bgColor1)
//					oEvent.bgColor1 = this.oColorMap[oEvent.type + '-bg-dark'];
//				if (!oEvent.bgColor2)
//					oEvent.bgColor2 = this.oColorMap[oEvent.type + '-bg-light'];
//				if (!oEvent.fontColor)
//					oEvent.fontColor = this.oColorMap[oEvent.type + '-font'];
//				if (!oEvent.borderColor)
//					oEvent.borderColor = this.oColorMap[oEvent.type + '-border'];
				if (!oEvent.storeTitle)
					oEvent.storeTitle = this.storeTitle;

				if(oEvent.oStartTime){ oEvent.startDateTime = oEvent.oStartTime.getISO8601String(); }
				if(oEvent.oEndTime){ oEvent.endDateTime = oEvent.oEndTime.getISO8601String(); }
				oEvent.unid = oData.unid;
			} catch(e) {
				console.error("An error occurred reading a calendar document.");
			}

			oEvent[this._storeRef] = this;
			items.push(oEvent);
		}
		return items;
	},
	
	// came from eventSummary#createInstance
	createDummyEventSummary: function createDummyEventSummary(oEntry){
		var oEvent = {};
		oEvent.type = this._typeMap[(oEntry.vType instanceof Array ? oEntry.vType[0]: oEntry.vType)];
		oEvent.subject  = (oEntry.vDescription instanceof Array ? oEntry.vDescription[0]: oEntry.vDescription);
		if (oEntry.vDescription instanceof Array && oEntry.vDescription.length > 1) {
			if (oEntry.vDescription.length == 3) {
				oEvent.chair  = oEntry.vDescription[2];
				oEvent.altChair  = oEntry.vAltDescription && oEntry.vAltDescription instanceof Array && oEntry.vDescription.length == 3 ? oEntry.vAltDescription[2]: '';
				oEvent.location  = oEntry.vDescription[1];
			} else {
				oEvent.chair = (oEntry.vChair instanceof Array ? oEntry.vChair[0]: oEntry.vChair);
				oEvent.altChair = oEntry.vDescription[1] == oEvent.chair ? oEntry.vAltDescription[1]: '';
				// Chair name is set to vDescription[1] when Location is empty
				oEvent.location = oEntry.vDescription[1] != oEvent.chair ? oEntry.vDescription[1] : '';
			}
		} else {
			oEvent.chair = (oEntry.vChair instanceof Array ? oEntry.vChair[0]: oEntry.vChair);
			oEvent.altChair = '';
			oEvent.location = '';
		}
		oEvent.oStartTime = (new dwa.date.calendar).setDate(oEntry.oCalDate ? oEntry.oCalDate :oEntry.oStartDate);
		oEvent.oEndTime  = (oEntry.oEndDate ? (new dwa.date.calendar).setDate(oEntry.oEndDate) : null);
		var nIcon = (oEntry.vIcon instanceof Array? oEntry.vIcon[0]: 0);
		oEvent.iconParam = 'colicon1.gif' + ' 13 11 ' + (((nIcon-1)%10)*13) + ' ' + (Math.floor((nIcon-1)/10)*11);
		
		if (!oEvent.oStartDate && oEvent.oStartTime) {
			oEvent.oStartDate = oEvent.oStartTime.clone();
			oEvent.oStartDate.fDateOnly = true;
		}
		
		if (!oEvent.oEndTime && oEvent.oStartTime)
			oEvent.oEndTime = (new dwa.date.calendar).setDate(new Date(oEvent.oStartTime.getDate() + 3600000));
		else
			oEvent.hasEndTime = true;
		
		oEvent.oEndDate = (new dwa.date.calendar).setDate(oEntry.oEndDate? oEntry.oEndDate: oEvent.oEndTime);
		oEvent.oEndDate.fDateOnly = true;
		
		if (!oEvent.allday && oEvent.type) {
			oEvent.allday = (oEvent.type!="Appointment" && oEvent.type!="Meeting" && oEvent.type!="Reminder");
		}
		
		// Fixed probem that all day event can be rescheduled when drag and drop within the same day
		if (oEvent.allday)
			oEvent.oStartTime.fDateOnly = oEvent.oEndTime.fDateOnly = true;

		return oEvent;
//	},

//	initColorMap: function initColorMap(){
//		var types = [
//			'Appointment', 'Anniversary', 'Event', 'Meeting', 'Reminder', 'Todo', 'Unprocessed', 'Cancelled'
//		];
//		this.oColorMap = {};
//		for (var i = 0; i < types.length; i++) {
//			var type = types[i];
//			this.oColorMap[type + '-bg-dark']	= '#' + this['bgColor' + type];
//			this.oColorMap[type + '-font']		= '#' + this['fontColor' + type];
//			//this.oColorMap[type + '-bg-light']	= this.gradiateHSV('#' + this['bgColor' + type], 0.4, 0.7);
//			this.oColorMap[type + '-border']	= '#' + this['borderColor' + type];
//		}
	}
});
