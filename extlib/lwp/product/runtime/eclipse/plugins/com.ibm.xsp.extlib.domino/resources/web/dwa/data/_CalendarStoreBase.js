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

dojo.provide("dwa.data._CalendarStoreBase");

dojo.require("dojo.data.util.simpleFetch");

dojo.declare("dwa.data._CalendarStoreBase", null, {
	url: "",
	_storeRef: "_S",

	fontColorAppointment: '346410',
	bgColorAppointment: 'C2F79F',
	borderColorAppointment: '6DB23A',

	fontColorAnniversary: '9258C0',
	bgColorAnniversary: 'D7B4F2',
	borderColorAnniversary: 'B57FE0',

	fontColorEvent: 'C95F00',
	bgColorEvent: 'FEF8C6',
	borderColorEvent: 'E4842E',

	fontColorMeeting: '2A6BAB',
	bgColorMeeting: 'C1DDF9',
	borderColorMeeting: '5495D5',

	fontColorReminder: 'C95F00',
	bgColorReminder: 'FFD28A',
	borderColorReminder: 'E48E2E',

	fontColorTodo: 'E57700',
	bgColorTodo: 'FFF38A',
	borderColorTodo: 'FDBB09',

	fontColorUnprocessed: '646464',
	bgColorUnprocessed: 'E6E6E6',
	borderColorUnprocessed: 'B2B2B2',

	fontColorCancelled: '000000',
	bgColorCancelled: 'D88870',
	borderColorCancelled: '000000',

	storeTitle: "",

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}
		this.fDisabled = false;
		this.afCached = {};
		this.aoEventsByDate = {};
	
		// init color map
		this.initColorMap();
	},

	getFeatures: function(){
		return {
			'dojo.data.api.Read': true
		};
	},

	getValue: function(item, attribute, defaultValue){
		var values = this.getValues(item, attribute);
		if(values && values.length > 0){
			return values[0];
		}
		return defaultValue;
	},

	getAttributes: function(item){
		return [
			"unid", "type", "subject", "chair", "location", "startDateTime", "endDateTime",
			"iconParam", "overlay", "allDay", "bgColor1",
			"bgColor2", "fontColor", "borderColor", "repeat", "status",
			"generator", "hide", "private", "external", "originalEventId", "altChair"
		]; 
	},

	hasAttribute: function(item, attribute){
		if(this.getValue(item, attribute)){
			return true;
		}
		return false;
	},

	isItemLoaded: function(item){
		 return this.isItem(item);
	},

	loadItem: function(keywordArgs){
	},

	getLabel: function(item){
		return this.getValue(item, this.label);
	},
	
	getLabelAttributes: function(item){
		return [this.label];
	},

	containsValue: function(item, attribute, value){
		var values = this.getValues(item,attribute);
		for(var i = 0; i < values.length; i++){
			if(values[i] === value){
				return true;
			}
		}
		return false;
	},

	getValues: function(item, attribute){
		return [item[attribute]];
	},

	isItem: function(item){
		if(item && item[this._storeRef] === this){
			return true;
		}
		return false;
	},
	
	close: function(request){
	},

	errorHandler: function(errorData, requestObject){
		if(requestObject.onError){
			var scope = requestObject.scope || dojo.global;
			requestObject.onError.call(scope, errorData, requestObject);
		}
	},

	fetchHandler: function(items, requestObject, numRows){
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

	fetch: function(request){
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

	_fetchItems: function(request){
		// subclass must implement
	},

	initColorMap: function(){
		var types = [
			'Appointment', 'Anniversary', 'Event', 'Meeting', 'Reminder', 'Todo', 'Unprocessed', 'Cancelled'
		];
		this.oColorMap = {};
		for (var i = 0; i < types.length; i++) {
			var type = types[i];
			this.oColorMap[type + '-bg-dark']	= '#' + this['bgColor' + type];
			this.oColorMap[type + '-font']		= '#' + this['fontColor' + type];
			//this.oColorMap[type + '-bg-light']	= this.gradiateHSV('#' + this['bgColor' + type], 0.4, 0.7);
			this.oColorMap[type + '-border']	= '#' + this['borderColor' + type];
		}
	}
});
//dojo.extend(dwa.data._CalendarStoreBase, dojo.data.util.simpleFetch);
