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

dojo.provide("dwa.cv.calendarView");

dojo.require("dojo.i18n");
dojo.require("dijit._Widget");
dojo.require("dwa.date.calendar");
dojo.require("dwa.date.dateFormatter");
dojo.require("dwa.common.utils");
dojo.require("dwa.common.commonProperty");
dojo.require("dwa.common.notesValue");
dojo.require("dwa.common.graphics");
dojo.require("dwa.common.listeners");
dojo.require("dwa.cv.calendarDataStore");
dojo.require("dwa.date.altCalendarFormatter");

dojo.requireLocalization("dwa.date", "calendar");
dojo.requireLocalization("dwa.cv", "calendarView");

var D_LITECAL_MIN_CELL_WIDTH      = 40;	// width of grid
var D_LITECAL_MIN_CELL_WIDTH2     = 20;	// half size
var D_LITECAL_CELL_HEIGHT         = 48;	// height of grid
var D_LITECAL_CELL_HEIGHT2        = 24;	// half size
var D_LITECAL_CELL_HEIGHT4        = 20;	// quarter size
var D_LITECAL_MIN_CELL_HEIGHT	  = 20;	// minimum height of calendar entry
var D_LITECAL_MAX_TIME_HEADER_WIDTH   = 48;	// width of time indicator in time slot
var D_LITECAL_MIN_TIME_HEADER_WIDTH   = 24;	// width of time indicator in time slot
var D_LITECAL_TEXT_WIDTH           = 8;	// Width of normal text
var D_LITECAL_TEXT_HEIGHT         = 16;	// height of normal text (used for status bar, navigator and footer)

var D_ALIGN_DEFAULT = "left";
var D_ALIGN_REVERSE = "right";
var D_PADDING_DEFAULT = "padding-left";

var D_TimeFmt_NoAMPM = 100;
var D_DateFmt_Month4Yr = 16;

var D_RLE="&#8235";
var D_PDF="&#8236";

dojo.declare(
	"dwa.cv.calendarView",
	dijit._Widget,
{
	store: "",
	actions: "",
	type: "M",
	summarize: false,
	date: "", // ex. "2009/10/06"
	query: {},
	autoRender: true,

	isRTL: false,

	// com_ibm_dwa_globals.oSettings
	sLanguagePreference: "en",
	bNamePreference: "",
	bIsArchive: false,

	// com_ibm_dwa_globals.oSettings.oCalendarData
	fDisableInPlaceEdit: false,

	CalendarTimeSlotStart: "T0700",
	CalendarTimeSlotEnd: "T1900",
	CalendarTimeSlotDuration: '60',

	UseCurrentTimeZone: '',
	CurTimeZoneLabel: '',
	UseAddlTimeZone: '',
	AddlTimeZoneLabel: '',
	AdditionalTimeZone: '',
	WorkDays: '',

	sDateFormat: "",
	sDateSep: "",
	sDateFormatLong: "",
	sTimeFormat: "",
	sTimeSep: "",

	nFirstDayMonth: 1,
	nFirstDayFiveDay: -1,
	nFirstDayWeek: -1,

	// 0 - Alternate (Secondary) calendar is disabled
	// 1 - Hijri (Not implemented yet)
	// 2 - Hebrew
	// 3 - Japanese Six Day
	nCalViewAltCal: 0,

	// 0 - Drag and Drop is not supported in calendar view
	// 1 - Smooth Drag and Drop
	// 2 - Discrete (Snapping) Drag and Drop
	nCalViewDragDrop: 1,

	useFooterMenu: false,

	_stores: null,
	_actionsObjs: null,

	postMixInProperties: function(){
		this._msgs = {};
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.date", "calendar", this.lang));
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.cv", "calendarView", this.lang));
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.cv", "calendarViewExtra", this.lang));
		var c = dwa.date.dateFormatter.prototype.oCalendarData;
		dojo.forEach(["sDateFormat", "sDateSep", "sDateFormatLong", "sTimeFormat", "sTimeSep", "nFirstDayMonth", "nFirstDayFiveDay", "nFirstDayWeek"],
		function(s){
			if(this[s] === "" || this[s] === -1){
				this[s] = c[s]; // copy the default value
			}else{
				c[s] = this[s]; // use the given parameter
			}
		}, this);

		this.fSummarize = this.summarize;
		if(!dojo._isBodyLtr()){
			this.isRTL = true;
		}
		if(this.isRTL){
			D_ALIGN_DEFAULT = "right";
			D_ALIGN_REVERSE = "left";
			D_PADDING_DEFAULT = "padding-right";
		}
		this._stores = [];
		if(this.store){
			if(typeof(this.store) == "string"){
				var arr = this.store.split(',');
				for(var i in arr){
					this._stores.push(dojo.getObject(arr[i]));
				}
			}else if(this.store instanceof Array){
				this._stores = this.store;
			}else{
				this._stores.push(this.store);
			}
		}
		this._actionsObjs = [];
		if(this.actions){
			if(typeof(this.actions) == "string"){
				var arr = this.actions.split(',');
				for(var i in arr){
					this._actionsObjs.push(dojo.getObject(arr[i]));
				}
			}else if(this.actions instanceof Array){
				this._actionsObjs = this.actions;
			}else{
				this._actionsObjs.push(this.actions);
			}
		}
		for(var i = 0; i < this._actionsObjs; i++) {
			this._actionsObjs[i].widget = this;
		}
		this.inherited(arguments);
	},

	postCreate: function(){
		var sId = this.id;
		this.sId = sId;

		dojo.addClass(this.domNode, "s-panel-content-border");
		this.domNode.style.overflow = "hidden";
		if(dojo.isIE && dojo.hasClass(dojo.body(), "dijit_a11y"))
			dojo.addClass(this.domNode, "s-background-none-iebug-in-hc");
	
		this.sTransparent = this.buildResourcesUrl('transparent.gif');
		this.sBasicIcons = this.buildResourcesUrl('basicicons.gif');
		this.sColIcons = this.buildResourcesUrl('colicon1.gif');

		this.sTimeSlotStart = this.CalendarTimeSlotStart;
		this.sTimeSlotEnd = this.CalendarTimeSlotEnd;

		// set current date time
		if(this.date){
			this.oCalendar = (new dwa.date.calendar).setISO8601String(this.date);
		}else{
			this.oCalendar = (new dwa.date.calendar).setDate(new Date());
		}
		this.oCalendar.fDateOnly = false;
		this.oCalendar.nHours = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart).nHours;
		this.oCalendar.nMinutes = this.oCalendar.nSeconds = this.oCalendar.nMilliseconds = 0;
		this.oCalendar.setUTCDate(this.oCalendar.getUTCDate()); // Obtain day of the week
	
		// do not select time slot by default so that new entry has current time until user apparently selects time slot (SPR BCOE7F3SJY)
		this.bDateSelected = true;
	
		// clear array for calendar enrty
		this.aoData = [];
		this.nEntries = 0;
	
		// get width of scroll bar
		this.getScrollBarWidth('s-cv-timeslot');
	
		// get height of text element
		this.nTextHeight = this.getTextHeight('s-cv-text');
		this.nMinCellHeight	 = Math.max(Math.floor(this.nTextHeight * 1.25), D_LITECAL_MIN_CELL_HEIGHT);
	
		// Set the activity handler for use
		this.sActivityHandler = dojo.doc.getElementById(this.sId).getAttribute('com_ibm_dwa_ui_calendarView_indicatorTab');

//_ak//need to call this to make date related members be in sync with oCalendar
		this.setViewDateRange(this.oCalendar);
		// kami (register dojo.data datastores)
		this.oCalendarDataStore = dwa.cv.calendarDataStore.getInstance();//maybe to be revised.
		this.oCalendarDataStore.registerViewWidget(this);
		for(var i = 0; i < this._stores.length; i++){
			this.oCalendarDataStore.registerDataLoader(this._stores[i]);
		}
		this.oCalendarDataStore.setActivityHandler(this.sActivityHandler);

		// Message to display in status indictor
		this.sStatus = '';
	
		// True if this calendar widget is activated
		this.fActivated = true;
	
		// Display AM/PM
		this.f24Hour = this.f24Hour;
	
		// Support for additional time zone
		this.fUseTimeZone = (this.UseCurrentTimeZone == '1');
		// Always display time zone label in time slot views. System time zone is unavailable in Safari. (SPR JLJE7VQPD2)
		this.sTimeZone = this.CurTimeZoneLabel;
		this.fUseAddTimeZone = (this.UseAddlTimeZone == '1');
		if (this.fUseAddTimeZone) {
			this.oAddTimeZone = new dwa.date.zoneInfo(this.AdditionalTimeZone);
			this.sAddTimeZone = this.AddlTimeZoneLabel;
		}
	
		// Disable Drag&Drop and In-Place Edit (SPR NYWU7ES59M)
		this.fDisableDragDrop = this.nCalViewDragDrop == 0;
		this.enableDnD = !this.fDisableDragDrop;
	
		// Time slot duration
		var sTmp = this.CalendarTimeSlotDuration;
		if (sTmp == '15' || sTmp == '30') {
			this.nTimeSlotDur   = sTmp - 0;
			this.nMinGridHeight = (sTmp == '15'? D_LITECAL_CELL_HEIGHT4: D_LITECAL_CELL_HEIGHT2);
			this.nCellHeight    = (sTmp == '15'? 4: 2) * this.nMinGridHeight;
		} else {
			this.nTimeSlotDur   = 60;
			this.nMinGridHeight = this.nCellHeight = D_LITECAL_CELL_HEIGHT;
		}
	
		// show alternate name
		this.bShowAlternateName = this.sLanguagePreference && this.bNamePreference;
	
		// consolidated images
		this.asConsolidateImages = {};
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			// dotted border of <A> tag sometimes disappear in IE7 on Vista.
			// We can't put it under our control so we use <DIV> tag with dotted border for IE7.
			// The reason why we don't use it for other browser is that:
			//   IE6 - There is not problem with <A> tag in IE6. Also, dotted border looks like dashed border in IE6.
			//   Firefox - There is not problem with <A> tag in Firefox.
			//   Safari - There is not problem with <A> tag. Also, focus is displayed with blue shadow in Safari.
			var i = navigator.appVersion.indexOf('MSIE');
			this.fIE7Hack = (i != -1 && parseInt(navigator.appVersion.substr(i + 4)) >= 7);
		}
	
		this.sWorkDays = this.WorkDays;
		if (this.sWorkDays) {
			this.oWeekEnd = [];
			this.oDaysMap['F'] = 0;
			for (var i=0; i<7; i++) {
				this.oWeekEnd[i] = this.sWorkDays.indexOf(i + 1) == -1;
				this.oDaysMap['F'] += this.oWeekEnd[i]? 0: 1;
			}
		}
	
		this.nFirstDayInYear = this.nFirstDayInYear;
		this.nMinDaysInFirstWeek = this.nMinDaysInFirstWeek;
	
		this.bCanCreate = !this.bIsArchive;
		
		if(this.enableDnD){
			this.connect(dojo.doc, "onmousedown", "handleDrag"); // kami
		}
	},
	startup: function(){
		if(this._started){return;}
		this._started = true;
		if(this.autoRender){
			this.render();
		}
	},
	destroy: function(){
		var oDatepick = dijit.byId(this.sId + '-calendar-datepick');
		if(oDatepick)
			oDatepick.destroy();
		
		if(this.oCalendarDataStore){
			for(var i = 0; i < this._stores.length; i++){
				this.oCalendarDataStore.unregisterDataLoader(this._stores[i]);
			}
			this.oCalendarDataStore.unregisterViewWidget(this);
		}
		//ASHH9UB5RH
		dwa.cv.calendarDataStore.clearInstance();
		
		dwa.common.commonProperty.get('p-contentarea-width').detach(this);
		dwa.common.commonProperty.get('p-body-height').detach(this);
		dwa.common.commonProperty.get('p-e-calendarview-currentselected').detach(this);
		this.inherited(arguments);
	},
	resize: function(changeSize, resultSize){
		if(changeSize || resultSize){
			dojo.marginBox(this.domNode, changeSize||resultSize);
		}
		this.adjustCalendar();
	},
	render: function(){
		dwa.common.commonProperty.get('p-e-calendarview-currentselected').attach(this); // kami (to call observe())
		this.drawCalendar(this.type, this.fSummarize);
		this.onActivated();
	},
	oDaysMap: {
		'D':1, 'T':2, 'F':5, 'W':7, '2':14, 'M':30, 'Y':365
	},
	getScrollBarWidth: function(sClass){
		if(this.nScrollBarWidth)
			return this.nScrollBarWidth;
	
		var oDiv = dojo.doc.getElementById('com_ibm_dwa_ui_calendarView_scrollBarDiv');
		// create temporary element if not exist
		if (!oDiv) {
			oDiv = dojo.doc.body.appendChild(dojo.doc.createElement('DIV'));
			oDiv.id = 'com_ibm_dwa_ui_calendarView_scrollBarDiv';
			if (sClass)
				oDiv.className = sClass;
			this.setStyle(oDiv, {
				top: '0px',
				width: '100px',
				height: '100px',
				overflow: 'scroll',
				visibility: 'hidden',
				position: 'absolute'
			});
			this.isRTL ? (oDiv.style.right = "-100px") : (oDiv.style.left = "-100px");

			if(dojo.isMozilla || dojo.isWebKit){
				oDiv.style.mozBoxSizing = 'border-box';
			}
		}
	    this.nScrollBarWidth = oDiv.offsetWidth - oDiv.clientWidth;
		dojo.doc.body.removeChild(oDiv);
	    return this.nScrollBarWidth;
	},
	// nakakura
	// use this function only in timeslot view
	getScrollTopMinutes: function() {
		var nScrollTop = dojo.doc.getElementById(this.sId + '-timeslot').scrollTop;
		var nMinutes = Math.ceil(nScrollTop / this.nMinGridHeight) * this.nTimeSlotDur;
		return (0 <= nMinutes && nMinutes < 24 * 60) ? nMinutes : 0;
	},
	getTextSize: function(sClass, fForceReset, sHtml){
		if(this.tmpTextHeight && !fForceReset)
			return this.tmpTextHeight;
	
		var oDiv = dojo.doc.getElementById('com_ibm_dwa_ui_calendarView_textHeightDiv');
		// create temporary element if not exist
		if (!oDiv) {
			oDiv = this.domNode.appendChild(dojo.doc.createElement('DIV'));
			oDiv.id = 'com_ibm_dwa_ui_calendarView_textHeightDiv';
			oDiv.innerHTML = sHtml? sHtml: 'A';
			if (sClass)
				oDiv.className = sClass;
			this.setStyle(oDiv, {
				top: '0px',
				visibility: 'hidden',
				position: 'absolute',
				border: '1px solid black'
			});
			this.isRTL ? (oDiv.style.right = "-100px") : (oDiv.style.left = "-100px");
			if(dojo.isMozilla || dojo.isWebKit){
				oDiv.style.mozBoxSizing = 'border-box';
			}
		}
	    var nWidth = Math.max(oDiv.offsetWidth, D_LITECAL_TEXT_WIDTH);
	    var nHeight = Math.max(oDiv.offsetHeight, D_LITECAL_TEXT_HEIGHT);
	    this.domNode.removeChild(oDiv);
	    return {nWidth:nWidth, nHeight:nHeight};
	},
	getTextHeight: function(sClass, fForceReset, sHtml){
		var oSize = this.getTextSize(sClass, fForceReset, sHtml);
		return (oSize? oSize.nHeight: D_LITECAL_TEXT_HEIGHT);
	},
	getTextWidth: function(sClass, fForceReset, sHtml){
		var oSize = this.getTextSize(sClass, fForceReset, sHtml);
		return (oSize? oSize.nWidth: D_LITECAL_TEXT_WIDTH);
	},
	oColorMap: {
		'header-bg-light':			'rgb(255,255,255)',
		'header-bg-dark':			'rgb(233,236,241)',
		'header-today-light':		'rgb(224,242,253)',
		'header-today-dark':		'rgb(190,214,248)',
		'entry-selected-light':		'rgb(127,175,237)',
		'entry-selected-dark':		'rgb(98,148,214)',
		'entry-selected-border':	'rgb(56,112,176)',
		'entry-selected-font':		'rgb(255,255,255)'
	},
	generateNavigatorHtml: function(){
		var asHtml = [], n = 0;
	
		// status indicator
		// nakakura
		// add tabindex="0", keydown event, focus event, blur event, outline-width:0px and hidefocus="true"
		// add div tag and aria-labelledby/title for screen reader
		asHtml[n++] = '<div id=' + this.sId + '-status class="s-cv-grid s-cv-nav s-cv-text '
				+ (this.sStatus? '': 's-nodisplay') + '"'
				+ ' style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + this.nTextHeight + 'px;padding:0px 2px;text-align:' + D_ALIGN_DEFAULT + ';font-weight:bold;" unselectable="on">'
				+ this.sStatus + '</div>'
	
				// date navigator
				+ '<div id=' + this.sId + '-navigator class="s-cv-navigator s-cv-grid s-cv-nav" role="row" style="' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + this.nTextHeight + 'px;" unselectable="on">'
				+ '<table role="gridcell" id=' + this.sId + '-navigator-innerframe cellspacing="0" cellpadding="0" style="border-width:0px;position:relative;width:400px;height:100%;margin:auto;"><tr>'
				+ '<td><div ' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? 'aria-labelledby="' + this.sId + '-label-prev"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? 'title="'+ this._msgs["L_CAL_NAV_PREVIOUS"]+'"' : '')) + ' role="button" hidefocus="true" tabindex="0" id=' + this.sId + '-navigator-prev  class="s-cv-text s-cv-nav-button" style="width:50px;outline-width:0px;"';
		if(!dojo.isMozilla && !dojo.isWebKit){
			asHtml[n++] = this._attachEvent('mouseover|mouseout|click|dblclick|keydown|focus|blur') + ' unselectable="on">';
		}else{
			asHtml[n++] = this._attachEvent('mouseover|mouseout|click|keydown|focus|blur') + '>';
		}
		// nakakura
		// add '<span class="s-arrowText">&lt;</span>' and '<span class="arrowText">&gt;</span>' for High-Contrast Mode
		asHtml[n++] = '<span class="s-arrowText">&lt;</span><img alt="'+ this._msgs["L_CAL_NAV_PREVIOUS"]+'" style="margin:2px;" width="6" height="9" src="' + this.sTransparent + '" xoffset="' + (this.isRTL?140:120) + '" yoffset="20">'
				+ '</div></td>'
				+ '<td><div ' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? 'aria-labelledby="' + this.sId + '-label-date"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? 'title="'+ this._msgs["L_CAL_NAV_MONTH"]+'"' : '')) + ' aria-haspopup="true" role="button" hidefocus="true" tabindex="0" id=' + this.sId + '-navigator-current class="s-cv-text s-cv-nav-button" style="width:300px;font-weight:bold;outline-width:0px;overflow:hidden;"'
				+ this._attachEvent('mouseover|mouseout|click|keydown|focus|blur') + '>'
				+ '</div></td>'
				+ '<td><div ' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? 'aria-labelledby="' + this.sId + '-label-next"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? 'title="'+ this._msgs["L_CAL_NAV_NEXT"]+'"' : '')) + ' role="button" hidefocus="true" tabindex="0" id=' + this.sId + '-navigator-next class="s-cv-text s-cv-nav-button" style="width:50px;outline-width:0px;"';
		if(!dojo.isMozilla && !dojo.isWebKit){
			asHtml[n++] = this._attachEvent('mouseover|mouseout|click|dblclick|keydown|focus|blur') + ' unselectable="on">';
		}else{
			asHtml[n++] = this._attachEvent('mouseover|mouseout|click|keydown|focus|blur') + '>';
		}
		asHtml[n++] = '<span class="s-arrowText">&gt;</span><img alt="'+ this._msgs["L_CAL_NAV_NEXT"]+'" style="margin:2px;" width="6" height="9" src="' + this.sTransparent + '" xoffset="' + (this.isRTL?120:140) + '" yoffset="20">'
				+ '</div></td></tr></table></div>';
	
		return asHtml.join('');
	},
	generateSelectedAreaHtml: function(sId, sStyle, fNoEvent){
		// nakakura
		// remove target="' + this.sId + '-blankframe"
		// delete href attribute and add tabindex for screen reader
		// In the case of the element has href value, JAWS10 does not read the title attribute.
		if(!dojo.isMozilla && !dojo.isWebKit){
			if (!sStyle)
				sStyle = 'display:none;' + D_ALIGN_DEFAULT + ':0px;font-size:10%;';
			return this.fIE7Hack?
					('<div id=' + this.sId + sId + ' class="s-cv" style="' + sStyle + 'border:1px dotted gray;"'
						+ ' hidefocus="true" role="heading" tabindex="0" target="' + this.sId + '-blankframe"' + (fNoEvent? '': this._attachEvent('click|keydown|focus|blur')) + '></div>'):
					('<div id=' + this.sId + sId + ' class="s-cv" style="' + sStyle + '"'
						+ ' tabindex="0" target="' + this.sId + '-blankframe"' + (fNoEvent? '': this._attachEvent('click|keydown')) + '></div>');
		}else{
			if (!sStyle)
				sStyle = 'display:none;' + D_ALIGN_DEFAULT + ':0px;' + (this.bYearView? 'cursor:pointer;': ''); // change mouse cursor shape (SPR SQZO7QA83B)
			if (dojo.isMozilla)
				return '<div id=' + this.sId + sId + ' class="s-cv" style="' + sStyle + '"'
						+ ' role="heading" tabindex="0" ' + (fNoEvent? '': this._attachEvent('click|keydown')) + '></div>';
			else
				return '<a id=' + this.sId + sId + ' class="s-cv" style="' + sStyle + '"'
						+ ' href="javascript:void(0)" ' + (fNoEvent? '': this._attachEvent('click|keydown')) + '></a>'; // should not handle keyboard event here (SPR SQZO7Q9BSX)
		}
	},
	generateHeaderHtml: function(){
		var asHtml = [], n = 0;
		var nDateHeaderHeight = this.nTextHeight * (this.sType=='2' || this.sType=='M'? 1: 2);
		var nDaysInRow = (this.sType=='2' || this.sType=='M'? 7: this.fSummarize? 1: this.nDays);
	
		// header background
		// in date header, use same class name with view area (SPR KZHU7PFBDU)
		asHtml[n++] = '<div id=' + this.sId + '-header class="' + (this.bTimeSlotView? 's-cv-timeslot': 's-cv-grid') + ' s-cv-grid-header" role="row" aria-label="'+ this._msgs["L_CAL_HEADER_DAYS_WEEK"]+'" style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + nDateHeaderHeight + 'px;"'
					+ this._attachEvent('click|scroll') + '>'
					+ '<div id=' + this.sId + '-header-bg class="s-cv" style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:100%; height:100%;">'
					+ this.insertGradientHtml(this.sId + '-header-gradient', 's-cv', this.oColorMap['header-bg-light'], this.oColorMap['header-bg-dark'])
					+ '</div>'
		
		// selected area
					+ '<div id=' + this.sId + '-header-today class="s-cv" role="heading" aria-label="'+ this._msgs["L_CAL_HEADER"]+'" style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:0px; height:100%;">'
					+ this.insertGradientHtml(this.sId + '-header-today-gradient', 's-cv', this.oColorMap['header-today-light'], this.oColorMap['header-today-dark'])
					+ '</div>';
	
		// date header
		// nakakura
		// add class="s-dateHeader" for High-Contrast Mode
		if (this.bTimeSlotView) {
			asHtml[n++] = '<div id=' + this.sId + '-header-date class="s-dateHeader s-cv-grid s-cv-text"'
						+ ' style="top:0px; ' + D_ALIGN_DEFAULT + ':' + (this.fUseAddTimeZone? D_LITECAL_MAX_TIME_HEADER_WIDTH: 0) + 'px; width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px; height:100%;font-weight:bold;text-align:' + D_ALIGN_REVERSE + ';">'
						+ this.sTimeZone + '</div>';
			if (this.fUseAddTimeZone)
				asHtml[n++] = '<div id=' + this.sId + '-header-adddate class="s-cv-grid s-cv-text"'
							+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px;'
							+ 'height:100%;font-weight:bold;text-align:' + D_ALIGN_REVERSE + ';">'
							+ this.sAddTimeZone + '</div>';
		}
		for (var i=0; i<=nDaysInRow; i++) {
			var dayIndex = ""+(i+1);
			asHtml[n++] = '<div id=' + this.sId + '-header-date' + i + ' class="s-dateHeader s-cv-grid' + (this.bTimeSlotView? '':' s-cv-text') + '" role="gridcell" aria-label="' + dwa.common.utils.formatMessage(this._msgs["L_CAL_HEADER_DAY"], dayIndex) + '"'
						+ ' style="top:0px;height:100%;' + (this.sType=='2'||this.sType=='M'? 'text-align:center;': '') + '"></div>';
		}
		// selected area
		// selected area is not needed in grid views (SPR KZHU7PFBDU)
		asHtml[n++] = (this.bTimeSlotView? this.generateSelectedAreaHtml('-header-selected-area'): '')
					+ '</div>';
	
		return asHtml.join('');
	},
	generateTimeslotHtml: function(){
		var asHtml = [], n = 0;
	
		// all day events area
		// in all day events area, use same class name with view area (SPR KZHU7PFBDU)
		// nakakura
		// add this._attachEvent('keydown') to alldayDiv
		asHtml[n++] = '<div id=' + this.sId + '-allday class="s-cv-timeslot" role="row" style="' + D_ALIGN_DEFAULT + ':0px;width:100%;height:0px;overflow:scroll;"' + this._attachEvent('keydown') + ' tabindex="-1">'
					+ '<div id=' + this.sId + '-allday-date class="s-cv-grid" style="top:0px;' + D_ALIGN_DEFAULT + ':' + (this.fUseAddTimeZone? D_LITECAL_MAX_TIME_HEADER_WIDTH: 0) + 'px;width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px;"></div>';
		if (this.fUseAddTimeZone)
			asHtml[n++] = '<div id=' + this.sId + '-allday-adddate class="s-cv-grid" style="top:0px;' + D_ALIGN_DEFAULT + ':0px;width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px;"></div>';
		for (var i=0; i<=this.nDays; i++)
			asHtml[n++] = '<div id=' + this.sId + '-allday-date' + i + ' role="gridcell" aria-label="' + dwa.common.utils.formatMessage(this._msgs["L_CAL_EVENT_ALLDAY"], ""+(i+1)) + '" class="s-cv-grid" style="top:0px;"></div>';
		asHtml[n++] = '</div>';
	
		var oTimeFormatter = new dwa.date.dateFormatter(100);
		var oTimeFormatter2 = new dwa.date.dateFormatter(101);
	
		// time slot
		asHtml[n++] = '<div id=' + this.sId + '-timeslot class="s-cv-timeslot" role="row" style="' + D_ALIGN_DEFAULT + ':0px;overflow:scroll;background-color:white;"'
					+ this._attachEvent('click|dblclick|keydown|contextmenu|scroll') + ' tabindex="-1">';
	
		var oStart = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart);
		var oEnd = (new dwa.date.calendar).setISO8601String(this.sTimeSlotEnd);
		// "Display 24 hours" was enabled in classic UI. (SPR STER5TD6A8)
		var bDisplay24Hours = this.sTimeSlotStart == this.sTimeSlotEnd;
		if (oStart && oEnd) {
			if (!bDisplay24Hours)
				asHtml[n++] = '<div id=' + this.sId + '-timeslot-dark1 class="s-cv-timeslot-dark"'
							+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;'
							+ ' width:100%; height:' + Math.floor(this.nCellHeight*(oStart.nHours + oStart.nMinutes/60)) + 'px;"></div>'
							+ '<div id=' + this.sId + '-timeslot-dark2 class="s-cv-timeslot-dark"'
							+ ' style="top:' + Math.floor(this.nCellHeight*(oEnd.nHours + oEnd.nMinutes/60)) + 'px;' + D_ALIGN_DEFAULT + ':0px;'
							+ ' width:100%; height:' + Math.floor(this.nCellHeight*(24 - oEnd.nHours - oEnd.nMinutes/60)) + 'px;"></div>';
			asHtml[n++] = '<div id=' + this.sId + '-timeslot-today class="s-cv-timeslot-today"'
						+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;width:0px;height:' + this.nCellHeight*24 + 'px;"></div>';
		}
	
		var oDate = this.oCalendar.clone();
		oDate.nMinutes = oDate.nSeconds = oDate.nMilliseconds = 0;
		this.nTimeWidth = (this.fUseAddTimeZone? D_LITECAL_MAX_TIME_HEADER_WIDTH*2: D_LITECAL_MAX_TIME_HEADER_WIDTH);
		this.nTimeSlotTopHour = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart).nHours;
		for (i=0; i<24; i++) {
			oDate.nHours = i;
			// Always show AM/PM if additional time zone is selected (SPR VSEN7X95AQ)
			var bShowAM = this.fUseAddTimeZone || i==this.nTimeSlotTopHour? i<12: i==0;
			var bShowPM = this.fUseAddTimeZone || i==this.nTimeSlotTopHour? i>=12: i==12;
			asHtml[n++] = '<div id=' + this.sId + '-timeslot-time' + i + ' role="gridcell" class="s-cv-grid s-cv-text"'
						+ ' style="font-weight:bold; text-align:' + D_ALIGN_REVERSE + ';top:' + (this.nCellHeight * i) + 'px;'
						+ D_ALIGN_DEFAULT + ':' + (this.fUseAddTimeZone? D_LITECAL_MAX_TIME_HEADER_WIDTH: 0) + 'px;width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px;height:' + this.nCellHeight + 'px;">'
						+ oTimeFormatter.format(oDate)
						+ (!this.f24Hour? (bShowAM? '<br>' + this._msgs["L_AM_SUFFIX"]: '') + (bShowPM? '<br>' + this._msgs["L_PM_SUFFIX"]: ''): '') + '</div>';
	
			if (this.fUseAddTimeZone) {
				var oDate2 = (new dwa.date.calendar).setDate(oDate.getDate(), this.oAddTimeZone);
				asHtml[n++] = '<div id=' + this.sId + '-timeslot-addtime' + i + ' class="s-cv-grid s-cv-text"'
						+ ' style="font-weight:bold; text-align:' + D_ALIGN_REVERSE + ';top:' + (this.nCellHeight * i) + 'px;'
						+ D_ALIGN_DEFAULT + ':0px;width:' + D_LITECAL_MAX_TIME_HEADER_WIDTH + 'px;height:' + this.nCellHeight + 'px;">'
						+ oTimeFormatter.format(oDate2)
						+ (!this.f24Hour? (oDate2.nHours<12? '<br>' + this._msgs["L_AM_SUFFIX"]: '') + (oDate2.nHours>=12? '<br>' + this._msgs["L_PM_SUFFIX"]: ''): '') + '</div>';
			}
	
			asHtml[n++] = '<div id=' + this.sId + '-timeslot-grid' + i + ' class="s-cv-grid"'
						+ ' style="top:' + (this.nCellHeight * i) + 'px;' + D_ALIGN_DEFAULT + ':' + this.nTimeWidth + 'px;height:' + this.nCellHeight + 'px;"></div>';
		}
	
		if (this.nTimeSlotDur < 60) {
			for (i=0; i<24; i++)
				asHtml[n++] = '<div id=' + this.sId + '-timeslot-grid-half' + i + ' class="s-cv-grid-dotted"'
							+ ' style="top:' + (this.nCellHeight * i) + 'px;' + D_ALIGN_DEFAULT + ':' + this.nTimeWidth + 'px;height:' + this.nCellHeight/2 + 'px;"></div>';
			if (this.nTimeSlotDur == 15) {
				for (i=0; i<48; i++)
					asHtml[n++] = '<div id=' + this.sId + '-timeslot-grid-quarter' + i + ' class="s-cv-grid-dotted"'
								+ ' style="top:' + (this.nCellHeight/2 * i) + 'px;' + D_ALIGN_DEFAULT + ':' + this.nTimeWidth + 'px;height:' + this.nCellHeight/4 + 'px;"></div>';
			}
		}
	
		for (i=0; i<this.nDays; i++)
			asHtml[n++] = '<div id=' + this.sId + '-timeslot-date' + i + ' class="s-cv-grid"'
			            + ' style="top:0px; height:' + (this.nCellHeight * 24) + 'px;"></div>';
	
		// selected area
		asHtml[n++] = this.generateSelectedAreaHtml('-selected-area')
					+ '</div>';
	
		return asHtml.join('');
	},
	generateYearViewHtml: function(){
		var asHtml = [], n = 0;
	
		// year view
		asHtml[n++] = '<div id=' + this.sId + '-year class="s-cv" role="row" style="' + D_ALIGN_DEFAULT + ':0px;overflow:hidden;border-' + D_ALIGN_REVERSE + ':1px solid #7F9DB9;"';
		if(dojo.isWebKit){
			asHtml[n++] = this._attachEvent('click|contextmenu') + '>';
		}else{
			asHtml[n++] = this._attachEvent('click|keydown|contextmenu') + '>';
		}
	
		// grid for 12 months
		this.nMonthWidth = Math.floor(this.nTextHeight * 7.5);
		this.nMonthHeight = Math.floor(this.nTextHeight * 9);
		for (var i=0; i<12; i++)
			asHtml[n++] = '<div id=' + this.sId + '-month' + (i+1) + ' class="s-cv" style="width:' + this.nMonthWidth + 'px;height:' + this.nMonthHeight + 'px;"></div>';

		// selected area
		asHtml[n++] = this.generateSelectedAreaHtml('-selected-area')
					+ '</div>';
	
		return asHtml.join('');
	},
	generateSummaryViewHtml: function(){
		// summary view
		return '<div id=' + this.sId + '-summary class="s-cv" role="row" style="' + D_ALIGN_DEFAULT + ':0px;overflow:hidden;border-right:1px solid #7F9DB9;"'
					+ this._attachEvent('click|dblclick|keydown|contextmenu|scroll') + '>'
	
		// selected area
					+ this.generateSelectedAreaHtml('-selected-area')
	
		// display message if no entry
					+ '<div id=' + this.sId + '-noentry role="gridcell" aria-label="' + this._msgs["L_CAL_NOENTRY"] + '" class="s-cv s-cv-text" style="display:none;top:0px;' + D_ALIGN_DEFAULT + ':0px;font-style:italic;"></div>'
	
					+ '</div>';
	},
	generateGridViewHtml: function(){
		var asHtml = [], n = 0;
		var nRowsInMonth = (this.sType=='M'? 6: 2);
	
		// calendar grid
		// use same class names in header and view area (SPR KZHU7PFBDU)
		asHtml[n++] = '<div id=' + this.sId + '-grid class="s-cv-grid s-cv-grid-body" role="row" aria-label="'+ this._msgs["L_CAL_GRID"] + '" style="' + D_ALIGN_DEFAULT + ':0px;overflow:hidden;"'
					+ this._attachEvent('click|dblclick|keydown|contextmenu|scroll') + '>';
	
		for (var j=0;j<nRowsInMonth;j++) {
			for (var i=0; i<7; i++)
				asHtml[n++] = '<div id=' + this.sId + '-grid-date' + j + '-' + i + ' class="s-cv-grid" role="gridcell" style="top:0px;text-align:' + D_ALIGN_REVERSE + ';overflow:hidden;background-color:white;"></div>';
		}
	
		// selected area
		asHtml[n++] = this.generateSelectedAreaHtml('-selected-area')
					+ '</div>';
	
		return asHtml.join('');
	},
	generateFooterHtml: function(){
		// calendar footer
		if (this.useFooterMenu)
			return '<div id=' + this.sId + '-footer class="s-cv-text s-cv-nav s-cv-footer" tabindex="0" role="presentation"'
					+ ' style="' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + this.nTextHeight + 'px;"'
					+ this._attachEvent('mouseover|mouseout|click|contextmenu|focus|blur') + '></div>';
		else
			return '<div id=' + this.sId + '-footer class="s-cv-text s-cv-nav" role="presentation"'
					+ ' style="' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + this.nTextHeight + 'px;border:1px solid rgb(233,236,241);"></div>';
	},
	drawCalendar: function(sType, fSummarize){
		if (this.sType == sType && this.fSummarize == fSummarize)
			return;
	
		// SPR #XLLU7MH7UZ: Old value is still effective, so invalidate
		this.asWideTimeslotHeaderHtml = void 0;
	
		this.sType = sType;
		var oElem = dojo.doc.getElementById(this.sId);
		var asHtml = [], n = 0;
	
		if (oElem) {
			if (this.oDaysMap[sType]) {
				this.nDays = this.oDaysMap[sType];
				this.fSummarize = fSummarize && sType != 'Y';
				this.bYearView = sType == 'Y';
				this.bTimeSlotView = (!fSummarize && sType != '2' && sType != 'M' && sType != 'Y');
				this.bWeekEndView = (sType == 'W' || sType == '2' || sType == 'M');
				this.nAlldayEventsInWeek = 0;
	
				// status indicator and date navigator
				asHtml[n++] = this.generateNavigatorHtml();

				// date header
				if (!this.bYearView && !this.fSummarize)
					asHtml[n++] = this.generateHeaderHtml();

				// summary view
				if (this.bYearView)
					asHtml[n++] = this.generateYearViewHtml();
				else if (this.fSummarize)
					asHtml[n++] = this.generateSummaryViewHtml();
				else if (this.bTimeSlotView)
					asHtml[n++] = this.generateTimeslotHtml();
				else
					asHtml[n++] = this.generateGridViewHtml();

				// calendar footer
				if (!this.bYearView)
					asHtml[n++] = this.generateFooterHtml();
				
				// nakakura
				// generate label nodes for screen reading text
				if(8 <= dojo.isIE || 3 <= dojo.isMozilla) {
					var sPrevious = this._msgs["L_CAL_NAV_TIMESCALE_PREV_"+this.sType]
					var sNext = this._msgs["L_CAL_NAV_TIMESCALE_NEXT_"+this.sType]
					
					asHtml[n++] = '<label id="' + this.sId + '-label-slot" style="display:none"></label>'
								+ '<label id="' + this.sId + '-label-prev" style="display:none">'+ sPrevious +'</label>'
								+ '<label id="' + this.sId + '-label-next" style="display:none">'+ sNext +'</label>'
								+ '<label id="' + this.sId + '-label-date" style="display:none"></label>';
				}
			}
	
			oElem.innerHTML = asHtml.join('');
	
			if (!this.bInit) {
				dwa.common.commonProperty.get('p-contentarea-width').attach(this);
				dwa.common.commonProperty.get('p-body-height').attach(this);
				this.bInit = true;
			}
	
			new dwa.common.consolidatedImageListener([this.sId + '-navigator'], this.sBasicIcons);
	
			if (this.sType == '2' || this.sType == 'M') {
				this.gotoDay(this.oCalendar);
			} else {
				this.adjustCalendar(true /* do not update calendar entries at this time */);
				this.gotoDay(this.oCalendar);
			}
		}
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
	drawGradient: function(sId, sColor1, sColor2, sStrokeColor, nAlpha){
		if(dojo.hasClass(dojo.body(), "dijit_a11y"))
			return;
		var oCanvas = dojo.doc.getElementById(sId);
		if (oCanvas) {
			if(!dojo.isMozilla && !dojo.isWebKit){
				// prevent invalid argument error in IE when calendar entry DIV element is not displayed yet. (SPR PTHN7EURHN)
				// width of gradient area should be narrow in BiDi. (SPR KZHU7PFBDU)
				oCanvas.style.width = oCanvas.parentNode.offsetWidth? (oCanvas.parentNode.offsetWidth -(this.isRTL? 2: 1)) + 'px': oCanvas.parentNode.style.width;
				oCanvas.style.height = oCanvas.parentNode.offsetHeight? (oCanvas.parentNode.offsetHeight -2) + 'px': oCanvas.parentNode.style.height;
			}else{
				oCanvas.setAttribute('width', oCanvas.clientWidth);
				oCanvas.setAttribute('height', oCanvas.clientHeight);

				if (!sColor1)
					var sColor1 = oCanvas.getAttribute('color');
				if (!sColor2)
					var sColor2 = oCanvas.getAttribute('color2');
				if (!sStrokeColor)
					var sStrokeColor = oCanvas.getAttribute('color3');
				if (!nAlpha)
					var nAlpha = oCanvas.getAttribute('alpha') - 0;

				// draw round rect
				if (sStrokeColor)
					dwa.common.graphics.drawRoundRect(oCanvas);

				// convert color from #RRBBGG/rgb(RR,GG,BB) format.
				var vColor1 = this.convertRGB(sColor1);
				var vColor2 = this.convertRGB(sColor2);

				var oContext = oCanvas.getContext('2d');
				if (nAlpha)
					oContext.globalAlpha = nAlpha / 100;

				// draw gradient
				dwa.common.graphics.colorStrokeGradient(oCanvas, null, [0, vColor1[0], vColor1[1], vColor1[2], 100, vColor2[0], vColor2[1], vColor2[2]], sStrokeColor);
				if (!sStrokeColor)
					oContext.fillRect(0, 0, oCanvas.clientWidth, oCanvas.clientHeight);
			}
		}
	},
	setStyle: function(oElem, oStyle){
		if (typeof(oElem) == 'string')
			oElem = dojo.doc.getElementById((oElem.indexOf(this.sId) == -1? this.sId: '') + oElem);
		if (!oElem)
			return;
		for (var s in oStyle) {
			// fix error when set minus value to height or width
			if ((s == 'height' || s == 'width') && oStyle[s].charAt(0) == '-')
				oStyle[s] = 0;
			if (oStyle[s] != undefined) oElem.style[s] = oStyle[s]; // kami (check undefined)
		}
	},
	adjustCalendar: function(bNoupdateCalendarEntries){
		var q = (dojo.doc.compatMode == "CSS1Compat" && dojo.isIE < 8) ? 1 : 0;
		var oElem = dojo.doc.getElementById(this.sId);
		if (oElem) {
			var nCalendarWidth = oElem.clientWidth;
			// fix error when set minus value to height
			var nCalendarHeight = Math.max(100,oElem.clientHeight)-2*q;
			// fix layout problem when content area is too small
			oElem.scrollTop = oElem.scrollLeft = 0;
			// update text height when resize content frame
			var nTextHeight = this.getTextHeight('s-cv-text', true);
			var bTextHeightChanged = this.nTextHeight != nTextHeight;
			this.nTextHeight = nTextHeight;
			this.nMinCellHeight	 = Math.max(Math.floor(this.nTextHeight * 1.25), D_LITECAL_MIN_CELL_HEIGHT);
	
			if (this.fSummarize) {
				var fNarrowSummaryView = nCalendarWidth < 400;
				if (fNarrowSummaryView != this.fNarrowSummaryView)
					this.fNarrowSummaryView = fNarrowSummaryView;
					var bNeedRedraw = true;
			} else
				this.fNarrowSummaryView = false;

			var nTop = 0;
			this.nTimeWidth = D_LITECAL_MAX_TIME_HEADER_WIDTH + (this.fUseAddTimeZone? D_LITECAL_MAX_TIME_HEADER_WIDTH: 0);
			var nDaysInRow = (this.sType=='2'? 7: this.nDays);
	
			// status indicator
			if (this.sStatus) {
				this.setStyle('-status', {width:nCalendarWidth + 'px', height:this.nTextHeight + 'px'});
				nTop += this.nTextHeight;
			}
	
			// date navigator
			// use tall navigation bar in narrow summary view for some view types. always use short navigator in One Day/Month/Year view. (SPR VSEN7LRU2D)
			var nNavHeight = 2 + (this.nTextHeight * (this.sType != 'D' && this.sType != 'M'  && this.sType != 'Y' && this.fNarrowSummaryView? 2: 1)) - q + (dojo.isIE==8?1:0);
			var nNavWidth = this.fNarrowSummaryView? 200: 400;
			this.setStyle('-navigator', {top:nTop + 'px', width:nCalendarWidth + 'px', height:nNavHeight + 'px'});
			this.setStyle('-navigator-innerframe', {width:nNavWidth + 'px'});
			nTop += nNavHeight + q;
	
			// date header
			if (!this.bYearView && !this.fSummarize) {
				var nDateHeaderHeight = this.nTextHeight * (this.sType=='2' || this.sType=='M'? 1: 2) - q;
				var d = !dojo.isIE || dojo.doc.compatMode == "BackCompat" ? 1 : 2;
				this.setStyle('-header', {top:nTop + 'px', width:nCalendarWidth + d + 'px', height:nDateHeaderHeight + 'px'}); //#002
	
				this.drawGradient(this.sId + '-header-gradient', this.oColorMap['header-bg-dark'], this.oColorMap['header-bg-light']);
				nTop += nDateHeaderHeight + q;
			}
	
			var nFooterHeight = this.fHideFooter? 0: this.nTextHeight - 2*q;
	
			// year view
			if (this.bYearView) {
				this.nViewAreaWidth = nCalendarWidth;
				this.nViewAreaHeight = nCalendarHeight - nTop;
				this.bNeedHScroll = this.nViewAreaWidth < 520;
				this.bNeedVScroll = this.nViewAreaHeight < this.nMonthHeight * 3;
				var nWidth = this.nViewAreaWidth + (this.bNeedHScroll && !this.bNeedVScroll? this.nScrollBarWidth: 0);
				this.setStyle('-year', {
					top: nTop + 'px',
					width: nWidth + 'px',
					height: (this.nViewAreaHeight + (!this.bNeedHScroll && this.bNeedVScroll? this.nScrollBarWidth: 0)) + 'px',
					overflow: !(this.bNeedHScroll || this.bNeedVScroll)? 'hidden':'scroll',
					borderWidth: '1px'
				});
				var nWidth = Math.max(Math.floor(nWidth/4), this.nMonthWidth);
				var nMargin = Math.max(Math.floor((nWidth-this.nMonthWidth)/4), 0);
				for (var i=0; i<12; i++) {
					this.setStyle('-month' + (i+1), {
						left: (!this.isRTL?(nWidth * (i%4) + nMargin) + 'px':undefined),
						right: (this.isRTL?(nWidth * (i%4) + nMargin) + 'px':undefined),
						top: (this.nMonthHeight * Math.floor(i/4)) + 'px'
					});
				}
				nTop += this.nViewAreaHeight;
			}
			// summary view
			else if (this.fSummarize) {
				this.nViewAreaWidth = nCalendarWidth;
				this.nViewAreaHeight = nCalendarHeight - nFooterHeight - nTop;
				this.setStyle('-summary', {
					top: nTop + 'px',
					width: (this.nViewAreaWidth + (this.bNeedHScroll && !this.bNeedVScroll? this.nScrollBarWidth: 0))+ 'px',
					height: (this.nViewAreaHeight + (!this.bNeedHScroll && this.bNeedVScroll? this.nScrollBarWidth: 0))+ 'px',
					overflow: !(this.bNeedHScroll || this.bNeedVScroll)? 'hidden':'scroll',
					borderWidth: '1px'
				});
				nTop += this.nViewAreaHeight;
			} else
			// time slot view
			if (this.bTimeSlotView) {
				this.nTimeslotWidth = nCalendarWidth;
				this.nTimeslotInnerWidth = Math.max(400, nCalendarWidth - this.nTimeWidth - this.nScrollBarWidth);
				// horizon scroll bar not properly displayed with all day events (SPR YQWG7DK4N2)
				// need vertical scroll bar in all day events area (SPR MMII7Q935R)
				this.nTimeslotHeight = nCalendarHeight - nFooterHeight - nTop;
//				this.nAlldayEventsHeight = Math.min( parseInt(this.nTimeslotHeight/2), this.nMinCellHeight * this.nAlldayEventsInWeek + 1);
				this.nAlldayEventsHeight = Math.min( parseInt(this.nTimeslotHeight/2), this.nMinCellHeight * this.nAlldayEventsInWeek); // kami (+1 removed)
				this.bAlldayNeedVScroll = this.nAlldayEventsHeight == parseInt(this.nTimeslotHeight/2);
				this.nTimeslotHeight -= this.nAlldayEventsHeight;
				
				if (this.sType=='W') {
					var anPos = [], nPos = 0;
					for (var i=0, j=this.nFirstDayWeek; i<=8; i++, j++) {
						anPos[i] = nPos;
						nPos += (i >= 7 || this.isWeekEnd(j%7))? 0.5: 1;
					}
					var nDays = anPos[7];
				} else {
					// fixed layout problem if there are more than 5 work days.
					var anPos = [ 0, 1, 2, 3, 4, 5, 6, 7, 8 ];
					var nDays = nDaysInRow;
				}
				
				this.anLeft = [];
				this.anWidth = [];
				for (var i=0; i<=nDaysInRow; i++)
					this.anLeft[i] = this.nTimeWidth + Math.floor(this.nTimeslotInnerWidth * anPos[i] / nDays);
				for (var i=0; i<nDaysInRow; i++)
					this.anWidth[i] = this.anLeft[i+1] - this.anLeft[i];
	
				for (var i=0; i<=nDaysInRow; i++) {
					var oMap = {'-header':void 0, '-allday':void 0, '-timeslot':void 0};
					for (var s in oMap) {
						this.setStyle(s + '-date' + i, {
							left: !this.isRTL ? this.anLeft[i] + 'px' : undefined,
							right: this.isRTL ? this.anLeft[i] + 'px' : undefined,
							width: (i==nDaysInRow? this.nScrollBarWidth: this.anWidth[i]) + 'px'
						});
					}
				}
				// need vertical scroll bar in all day events area (SPR MMII7Q935R)
				// adjust position of scroll bar for BiDi (SPR MMII7Q935R)
				this.setStyle('-allday', {
					top    :    nTop + 'px',
					width  : nCalendarWidth + 'px',
					overflow : this.bAlldayNeedVScroll? 'scroll': 'hidden',
					height : (this.nAlldayEventsHeight + this.nScrollBarWidth) + 'px'
				});
				var nHeight = this.nMinCellHeight * this.nAlldayEventsInWeek;
				this.setStyle('-allday-date', {height: nHeight + 'px'});
				if (this.fUseAddTimeZone)
					this.setStyle('-allday-adddate', {height: nHeight + 'px'});
				for (var i=0; i<=nDaysInRow; i++)
					this.setStyle('-allday-date' + i, {height: nHeight + 'px'});
				this.setStyle('-allday-date7', {display: (this.bAlldayNeedVScroll? 'none': '')});
				nTop += this.nAlldayEventsHeight;
	
				// time slot
				this.setStyle('-timeslot', {
					top    : nTop - q + 'px', //#004
					width  : this.nTimeslotWidth + q + 'px',
					height : (this.nTimeslotHeight + (this.nTimeslotInnerWidth>400? this.nScrollBarWidth-1-2*q: 0)) + 'px'
				});
				nTop += this.nTimeslotHeight;
	
				var oTarget = dojo.doc.getElementById(this.sId + '-timeslot');
				this.nTimeslotOffsetX = 0;
				this.nTimeslotOffsetY = 0;
				while ((oTarget.tagName == 'DIV' || oTarget.tagName == 'IMG') && oTarget != dojo.doc.body) {
					this.nTimeslotOffsetX -= oTarget.offsetLeft;
					this.nTimeslotOffsetY -= oTarget.offsetTop;
					oTarget = oTarget.parentNode;
				}
	
				// need to adjust background color area not only in Safari but also in other browsers (SPR YQWG7DK4N2)
				var oMap = {'-header-bg':1, '-timeslot-dark1':0, '-timeslot-dark2':0};
				for (var s in oMap) {
					this.setStyle(s, {
						width: (this.nTimeslotInnerWidth + this.nTimeWidth + (oMap[s]? this.nScrollBarWidth - 2: 0)) + 'px'
					});
				}
	
				var oMap = {'-timeslot-grid':24, '-timeslot-grid-half':24, '-timeslot-grid-quarter':48};
				for (var s in oMap) {
					for (i=0; i<oMap[s]; i++) {
						this.setStyle(s + i, {
							width: this.nTimeslotInnerWidth + 'px'	// do not modify this line or time slot layout will be broken
						});
					}
				}
	
				// SPR #XLLU7MH7UZ: Time slot header contents should be changed (e.g. "January 2009" and "Jan 2009")
				// according to the time slot width
				if (bTextHeightChanged || !bNoupdateCalendarEntries) {
					this.drawTimeslotHeader();
				}
			}
			// grid view
			else {
				var nDaysInRow = 7;
				// fixed layout problem around DST boudary (SPR YKAA7XP667)
				var nRowsInMonth = Math.ceil(this.nDays / nDaysInRow);

				var d = (dojo.isIE < 8 && dojo.doc.compatMode == "CSS1Compat" && !this.isRTL) ? 0 : 1;
				d += (dojo.isIE < 8 && dojo.doc.compatMode == "BackCompat" && this.isRTL) ? 1 : 0;
				var nGridWidth = nCalendarWidth + d;
				var nGridHeight = nCalendarHeight - nFooterHeight - nTop;
				this.bNeedHScroll = nGridWidth < 600;
				this.bNeedVScroll = nGridHeight < 450;
				this.setStyle('-grid', {
					top : nTop + 'px',
					width : (nGridWidth + (this.bNeedHScroll && !this.bNeedVScroll? this.nScrollBarWidth: 0)) + 'px',
					height : (nGridHeight + (!this.bNeedHScroll && this.bNeedVScroll? this.nScrollBarWidth-2*q: 0)) + 'px',
					overflow : !(this.bNeedHScroll || this.bNeedVScroll)? 'hidden':'scroll'
				});
				nTop += nGridHeight;
	
				var oTarget = dojo.doc.getElementById(this.sId + '-grid');
				if(dojo.isIE < 7 && this.isRTL){
					oTarget.style.right = '-1px';
				}
				this.nTimeslotOffsetX = 0;
				this.nTimeslotOffsetY = 0;
				while ((oTarget.tagName == 'DIV' || oTarget.tagName == 'IMG') && oTarget != dojo.doc.body) {
					this.nTimeslotOffsetX -= oTarget.offsetLeft;
					this.nTimeslotOffsetY -= oTarget.offsetTop;
					oTarget = oTarget.parentNode;
				}
	
				nGridWidth = Math.max(600,nGridWidth) - (this.bNeedVScroll? this.nScrollBarWidth: 0) + q;
				nGridHeight = Math.max(450,nGridHeight) - (this.bNeedHScroll? this.nScrollBarWidth: 0);
				var anPos = [], nPos = 0;
				for (var i=0, oDate = this.oCalViewStart.clone(); i<=8; i++, oDate.adjustDays(0, 0, 1)) {
					anPos[i] = nPos;
					nPos += this.isWeekEnd(oDate)? 0.5: 1;
				}
				var nDays = anPos[7];
				this.anTop = [];
				this.anLeft = [];
				this.anHeight = [];
				this.anWidth = [];
				for (var i=0; i<=7; i++) {
					this.anTop[i] = Math.floor(nGridHeight * i / nRowsInMonth);
					this.anLeft[i] = Math.floor(nGridWidth * anPos[i] / nDays);
				}
				for (var i=0; i<=7; i++) {
					this.anHeight[i] = this.anTop[i+1] - this.anTop[i];
					this.anWidth[i] = this.anLeft[i+1] - this.anLeft[i];
				}
				if(dojo.isIE < 8 && this.isRTL){
					this.anWidth[6] -= 1; // kami (right-most cell is a bit too wide)
				}

				for (var i=0; i<=nDaysInRow; i++) {
					this.setStyle('-header-date' + i, {
						left: !this.isRTL ? this.anLeft[i] + 'px' : undefined,
						right: this.isRTL ? this.anLeft[i] + 'px' : undefined,
						width: (i==nDaysInRow? this.nScrollBarWidth: this.anWidth[i]) - q + 'px',
						height: nDateHeaderHeight + 'px'
					});
					for (var j=0;j<nRowsInMonth;j++) {
						this.setStyle('-grid-date' + j + '-' + i, {
							top: this.anTop[j] + 'px',
							height: this.anHeight[j] - q + 'px',
							left: !this.isRTL ? this.anLeft[i] + 'px' : undefined,
							right: this.isRTL ? this.anLeft[i] + 'px' : undefined,
							width: this.anWidth[i] - q + 'px'
						});
						this.setStyle('-grid-date' + j + '-' + i + '-innerframe', {
							height: (this.anHeight[j] - this.nTextHeight - q) + 'px',
							width: this.anWidth[i] - q + 'px'
						});
					}
				}
	
			// simplify scroll bar support for BiDi (SPR KZHU7PFBDU MMII7Q935R)
//				// header width should be narrow in BiDi to align start position to view area. (SPR KZHU7PFBDU)
//				this.setStyle('-header', {
//					width  : (nCalendarWidth - (this.isRTL && this.bNeedVScroll? this.nScrollBarWidth: 0)) + 'px'
//				});
	
				// need to adjust background color area (SPR YQWG7DK4N2)
				this.setStyle('-header-bg', {
					width: (nGridWidth + this.nScrollBarWidth - 2) + 'px'
				});
			}
	
			// calendar footer
			this.setStyle('-footer', {
				top: nTop + 'px',
				width: nCalendarWidth - 2*q + 'px',
				height: nFooterHeight + 'px',
				display: nFooterHeight? '': 'none'
			});
	
			// update selected area and calendar entries when flag is set
			if (!bNoupdateCalendarEntries) {
				if (bNeedRedraw) {
					this.clearCalendarEntries();
					this.drawCalendarEntries();
					this.drawNavigatorDateRange();
				} else {
					this.updateSelectedArea();
					this.updateCalendarEntries();
				}
			} else {
				oElem = dojo.doc.getElementById(this.sId + '-timeslot');
				var oStart = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart);
				if (oElem && oStart)
					oElem.scrollTop = this.nCellHeight*(oStart.nHours + oStart.nMinutes / 60);
			}
		}
	},
	clearCalendarEntries: function(){
		for (var i=0; i<this.nEntries; i++) {
			var oElem = dojo.doc.getElementById(this.sId + '-entry' + i);
			if (oElem)
				oElem.parentNode.removeChild(oElem);
			var oElem = dojo.doc.getElementById(this.sId + '-entry' + i + '-hover');
			if (oElem)
				oElem.parentNode.removeChild(oElem);
		}
		if (this.fSummarize) {
			var oElem = dojo.doc.getElementById(this.sId + '-summary');
			for (var i=oElem.childNodes.length-1; i>=0; i--) {
				if (oElem.childNodes[i].id && oElem.childNodes[i].id.indexOf(this.sId + '-date') != -1)
					oElem.removeChild(oElem.childNodes[i]);
			}
			this.bCollapse = {};
		}
	},
	load: function(){
		this.bNeedReload = true;
		setTimeout(dojo.hitch(this, "loadActual"), 0);
	},
	loadActual: function(){
		// it may be repeatedly called by com_ibm_dwa_ui_panelManager_processPassToCommand(). just ignore the second one. (SPR JCIK7SWNYP)
		if (!this.bNeedReload) return;
		this.bNeedReload = false;
		this.oCalendarDataStore.clear();
		this.gotoDay();
	},
	gotoDay: function(oCalendar, oCalViewStart){
		if (!oCalendar && this.oCalendar)
			var oCalendar = this.oCalendar.clone();
	
		this.clearCalendarEntries();
		// unselect calendar entry before redraw (SPR JYJY7JLBXM)
		this.unselectEntry();
	
		// SPR #XLLU7MH7UZ: Moved to a new method setViewDateRange() so that the logic can be invoked
		// from within adjustCalendar(), which can be invoked prior to gotoDay()
		this.setViewDateRange(oCalendar, oCalViewStart);
	
		// SPR #VSEN7LRU2D: use short date format (Mon, Feb 23, 2009) in narrow mode
		this.drawNavigatorDateRange();
	
		if (this.sType == 'Y') {
			var asCharDays = [this._msgs["L_CHARDAY_SUN"], this._msgs["L_CHARDAY_MON"], this._msgs["L_CHARDAY_TUE"], this._msgs["L_CHARDAY_WED"], this._msgs["L_CHARDAY_THU"], this._msgs["L_CHARDAY_FRI"], this._msgs["L_CHARDAY_SAT"]];
			var asMonths = [this._msgs["L_FULLMONTH_JAN"], this._msgs["L_FULLMONTH_FEB"], this._msgs["L_FULLMONTH_MAR"], this._msgs["L_FULLMONTH_APR"], this._msgs["L_FULLMONTH_MAY"], this._msgs["L_FULLMONTH_JUN"], this._msgs["L_FULLMONTH_JUL"], this._msgs["L_FULLMONTH_AUG"], this._msgs["L_FULLMONTH_SEP"],this._msgs["L_FULLMONTH_OCT"], this._msgs["L_FULLMONTH_NOV"], this._msgs["L_FULLMONTH_DEC"]];
			var oToday = (new dwa.date.calendar).setDate(new Date());
			oToday.fDateOnly = true;
			var oFormatter = new dwa.date.dateFormatter(); // kami
			for (var i=0; i<12; i++) {
				var oElem = dojo.doc.getElementById(this.sId + '-month' + (i+1));
				var asHtml = [], n = 0;
				var bThisMonth = oToday.nYear == this.oCalendar.nYear && oToday.nMonth == i;
				var sColor1 = this.oColorMap[bThisMonth? 'header-today-light': 'header-bg-light'];
				var sColor2 = this.oColorMap[bThisMonth? 'header-today-dark': 'header-bg-dark'];
				asHtml[n++] = '<table id=' + this.sId + '-month-innerframe' + (i+1) + ' tabindex="0" class="s-cv-text" role="grid" aria-label="' + asMonths[i] + '" summary="' + asMonths[i] + '" style="width:' + this.nMonthWidth + 'px;text-align:center;">'
				// month bar
				// nakakura
				// add class="s-monthBar" for High-Contrast Mode
					+ '<tr role="row" ><th role="gridcell" id='+this.sId+'-monthHeader' + (i+1) + ' colspan="7" style="' + (bThisMonth? 'font-weight:bold;': 'font-weight:normal;') + '">'
					+ '<div style="position:relative;width:' + this.nMonthWidth + 'px;height:' + this.nTextHeight + 'px;">'
					+ '<div style="position:absolute;' + D_ALIGN_DEFAULT + ':0px;width:' + this.nMonthWidth + 'px;height:' + this.nTextHeight + 'px;border-width:0 0 1px 0;border-color:#ACB7CD;">'
					+ this.insertGradientHtml(this.sId + '-month' + (i+1) + '-gradient', 's-cv', sColor2, sColor1)
					+ '</div>'
					+ '<div class="s-monthBar" id=' + this.sId + '-monthbar' + (i+1) + ' style="position:absolute;' + D_ALIGN_DEFAULT + ':0px;width:' + this.nMonthWidth + 'px;height:' + this.nTextHeight + 'px;cursor:pointer;">'
					+ oFormatter.asMonths[i]
					+ '</div>'
					+ '</div></th></tr>';
	
				// week bar
				asHtml[n++] = '<tr role="row" style="font-weight:bold;">';
				// Fixed problem that nFirstDayMonth not affect to Year view (SPR YKAA7XPAP2)
				var nFirstDay = this.nFirstDayMonth - 0;
				var nLastDay = (nFirstDay + 6) % 7;
				for (var j=0; j<7; j++)
					asHtml[n++] = '<th role="gridcell" id="'+this.sId+'-month' + (i+1) + '-weekdayHeader' + (j+1) + '">' + asCharDays[(nFirstDay + j) % 7] + '</td>';
				asHtml[n++] = '</tr>';
	
				// calendar grid
				var oDate = this.oCalendar.clone();
				oDate.nMonth = i;
				oDate.nDate = 1;
				oDate.fDateOnly = true;
				oDate.setDate(oDate.getDate(), this.oCalendar.oZoneInfo);
				for (var j=nFirstDay; (j%7)!=oDate.nDay; j++) {
					asHtml[n++] = (j == nFirstDay? '<tr role="row" style="cursor:pointer;">': '')
						+ '<td tabindex="0" headers="'+this.sId+'-month' + (i+1) + '-weekdayHeader' + j + ' ' + this.sId+'-monthHeader' + (i+1) + '"></td>';
				}
				while (oDate.nMonth == i) {
					var weekday = (j-1) + oDate.nDate % 7 == 0 ? '7' : (j-1) + oDate.nDate % 7;
					asHtml[n++] = (oDate.nDay == nFirstDay? '<tr role="row" style="cursor:pointer;">': '')
						+ '<td role="gridcell" headers="'+this.sId+'-month' + (i+1) + '-weekdayHeader' + weekday + ' '+this.sId+'-monthHeader' + (i+1) + '" tabindex="0" id="' + this.sId + '-date' + oDate.getISO8601String() + '"' + (oDate.equals(oToday)? ' style="font-weight:bold;color:rgb(255,0,0);"': '') + '>'
						+ oDate.nDate + '</td>'
						+ (oDate.nDay == nLastDay? '</tr>': '');
					oDate.adjustDays(0,0,1);
				}
				if (oDate.nDay != nLastDay)
					asHtml[n++] = '</tr>';
	
				oElem.innerHTML = asHtml.join('');
	
				this.drawGradient(this.sId + '-month' + (i+1) + '-gradient', sColor1, sColor2);
			}
		} else if (this.fSummarize) {
			var oFormatter = new dwa.date.dateFormatter(3);
			for (var i = 0, oDate = this.oCalViewStart.clone();i < 31; i++) {
				var oElem = dojo.doc.getElementById(this.sId + '-header-date' + i + '-innerframe');
				if (oElem)
					oElem.innerHTML = oFormatter.format(oDate);
				oDate.adjustDays(0, 0, 1);
			}
		} else if (this.sType == 'D' || this.sType == 'T' || this.sType == 'F' || this.sType == 'W') {
			this.drawTimeslotHeader();
		} else if (this.sType == '2' || this.sType == 'M') {
			var oFormatter = new dwa.date.dateFormatter(11);
			var nDaysInRow = 7;
			// fixed layout problem around DST boudary (SPR YKAA7XP667)
			var nRowsInMonth = Math.ceil(this.nDays / nDaysInRow);
			var oDate = this.oCalViewStart.clone();
			var oToday = (new dwa.date.calendar).setDate(new Date());
			oDate.fDateOnly = oToday.fDateOnly = true;
			for (var j=0;j<6;j++) {
				for (var i=0; i<nDaysInRow; i++) {
					if (j==0) {
						var oElem = dojo.doc.getElementById(this.sId + '-header-date' + i);
						if (oElem)
							oElem.innerHTML = oFormatter.asShortDays[oDate.nDay];
					}
	
					var oElem = dojo.doc.getElementById(this.sId + '-grid-date' + j + '-' + i);
					if (oElem) {
						var sClass = 's-cv-grid'
							+ (this.sType == 'M' && oDate.nMonth != oCalendar.nMonth? ' s-cv-grid-dark': '')
							+ (oDate.equals(oToday)? ' s-cv-grid-today': '');
						var sColor = sClass.indexOf('s-cv-grid-dark') != -1? 'rgb(233,236,241)': sClass.indexOf('s-cv-grid-today') != -1? 'rgb(229,241,254)': 'white';
						oElem.innerHTML = '<div class="s-cv-text">' + (this.sType == 'M' && oDate.nMonth == oCalendar.nMonth? oDate.nDate: oFormatter.format(oDate)) + '</div>'
						                + '<div id="' + this.sId + '-grid-date' + j + '-' + i + '-innerframe" class="s-cv-grid" style="top:' + this.nTextHeight + 'px;' + D_ALIGN_DEFAULT + ':0px;"></div>';
						oElem.className = sClass;
						oElem.style.display = (j<nRowsInMonth? '': 'none');
						oElem.style.backgroundColor = sColor;
						oElem.style.overflow = 'hidden';
					}
					oDate.adjustDays(0, 0, 1);
				}
			}
		}
	
		if (this.sType == '2' || this.sType == 'M')
			this.adjustCalendar(true /* do not update calendar entries at this time */);
	
		if (!this.fSummarize)
			this.updateSelectedArea();
	
		// no need to display calendar entries in year view
		if (this.sType != 'Y')
			setTimeout(dojo.hitch(this.oCalendarDataStore, "load", this.oCalViewStart, this.oCalViewEnd), 0);
	},
	setViewDateRange: function(oCalendar, oCalViewStart){
		oCalViewStart = (oCalViewStart? oCalViewStart: oCalendar).clone();
		oCalViewStart.setDate(oCalViewStart.getDate(), this.oCalendar.oZoneInfo);
		oCalViewStart.fDateOnly = false;
		oCalViewStart.nHours = oCalViewStart.nMinutes = oCalViewStart.nSeconds = oCalViewStart.nMilliseconds = 0;
	
		if (this.sType == 'D' || this.sType == 'T') {
			var oCalViewEnd = oCalViewStart.clone().adjustDays(0, 0, this.nDays);
		} else if (this.sType == 'F') {
			var nFirstDay = this.nFirstDayFiveDay;
			while (oCalViewStart.nDay != nFirstDay)
				oCalViewStart.adjustDays(0, 0, -1);
			for (var i=0, oDate = oCalViewStart.clone(); i<7; i++, oDate.adjustDays(0,0,1)) {
				if (!this.isWeekEnd(oDate))
					var oCalViewEnd = oDate.clone();
			}
			oCalViewEnd.adjustDays(0, 0, 1);
		} else if (this.sType == 'W' || this.sType == '2') {
			var nFirstDay = this.nFirstDayWeek;
			while (oCalViewStart.nDay != nFirstDay)
				oCalViewStart.adjustDays(0, 0, -1);
			var oCalViewEnd = oCalViewStart.clone().adjustDays(0, 0, this.nDays);
		} else if (this.sType == 'M') {
			var nFirstDay = this.nFirstDayMonth - 0;
			var oCalViewEnd = oCalViewStart.clone();
			oCalViewStart.adjustDays(0, 0, 1-oCalViewStart.nDate);
			while (!this.fSummarize && oCalViewStart.nDay != nFirstDay)
				oCalViewStart.adjustDays(0, 0, -1);
			while (oCalViewEnd.nMonth == oCalendar.nMonth)
				oCalViewEnd.adjustDays(0, 0, 1);
			while (!this.fSummarize && oCalViewEnd.nDay != nFirstDay)
				oCalViewEnd.adjustDays(0, 0, 1);
			this.nDays = (oCalViewEnd.getUTCDate() - oCalViewStart.getUTCDate()) / 86400000;
		} else if (this.sType == 'Y') {
			oCalViewStart.adjustDays(0, - oCalViewStart.nMonth, 1 - oCalViewStart.nDate);
			var oCalViewEnd = oCalViewStart.clone();
			oCalViewEnd.adjustDays(1, 0, 0);
			this.nDays = oCalViewStart.isLeapYear()? 366: 365;
		}
	
		if (!this.oCalViewStart || !this.oCalViewEnd || !oCalViewStart.equals(this.oCalViewStart) || !oCalViewEnd.equals(this.oCalViewEnd)) {
			// Need regeneration
			this.asWideTimeslotHeaderHtml = void 0;
		}
		this.oCalViewStart = oCalViewStart;
		this.oCalViewEnd = oCalViewEnd;
	},
	drawNavigatorDateRange: function(){
		var oElem = dojo.doc.getElementById(this.sId + '-navigator-current');
		if (oElem) {
			if (this.sType == 'Y') {
				var sText = this.oCalendar.nYear + '';
			} else
			if (this.sType == 'M') {
				var oFormatter = new dwa.date.dateFormatter(16);
				var sText = oFormatter.format(this.oCalendar);
			} else if (this.sType == 'D') {
				// use short date format in narrow summary view for One Day view. (SPR VSEN7LRU2D)
				var oFormatter = new dwa.date.dateFormatter(this.fNarrowSummaryView? 18: 4);
				var sText = oFormatter.format(this.oCalendar);
			} else {
				var oFormatter = new dwa.date.dateFormatter(3);
				var sText = oFormatter.format(this.oCalViewStart)
					+ ' - ' + oFormatter.format(this.oCalViewEnd.clone().adjustDays(0,0,-1));
			}
			// nakakura
			if (8 <= dojo.isIE || 3 <= dojo.isMozilla)
				dojo.byId(this.sId + '-label-date').innerHTML = sText;
			else if (dojo.isIE <= 7 || dojo.isMozilla <= 2)
				oElem.title = sText;
			oElem.innerHTML = sText;
			this.sDateRange = sText;
		}
	},
	drawTimeslotHeader: function(){
		var bHeaderUpdated = false;
		if (!this.asWideTimeslotHeaderHtml) {
			bHeaderUpdated = true;
			this.asWideTimeslotHeaderHtml = this.generateTimeslotHeaderHtml(false);
			this.nMaxTimeslotWidth = 0;
			for (var i = 0; i < this.asWideTimeslotHeaderHtml.length; i++) {
				this.nMaxTimeslotWidth = Math.max(this.nMaxTimeslotWidth, this.getTextWidth('s-cv-entry s-cv-text', true, this.asWideTimeslotHeaderHtml[i]));
			}
		}
		var fNarrowTimeslotView = this.anWidth[0] < this.nMaxTimeslotWidth + 3;
		if (bHeaderUpdated || fNarrowTimeslotView != this.fNarrowTimeslotView) {
			this.fNarrowTimeslotView = fNarrowTimeslotView;
			var asHtml = this.fNarrowTimeslotView? this.generateTimeslotHeaderHtml(true): this.asWideTimeslotHeaderHtml;
			for (var i = 0, oDate = this.oCalViewStart.clone();i < this.nDays; i++) {
				var oElem = dojo.doc.getElementById(this.sId + '-header-date' + i);
				if (oElem) {
					oElem.innerHTML = asHtml[i];
				}
			}
		}
	},
	generateTimeslotHeaderHtml: function(fNarrow){
		if (!this.oCalViewStart) {
			this.setViewDateRange(this.oCalendar);
		}
	
		var asHtml = [];
		var oFormatter = new dwa.date.dateFormatter(D_DateFmt_Month4Yr);
	
		if (this.nCalViewAltCal)
			var oAltCalFormatter = new dwa.date.altCalendarFormatter(this.nCalViewAltCal);
	
		for (var i = 0, oDate = this.oCalViewStart.clone();i < this.nDays; i++) {
			while (this.sType == 'F' && this.isWeekEnd(oDate))
				oDate.adjustDays(0, 0, 1);
			var bWeekEnd = this.bWeekEndView && this.isWeekEnd(oDate);
			oFormatter.sFormat = bWeekEnd? this._msgs["D_DTFMT_CALVIEW_TINY"]: fNarrow? this._msgs["D_DTFMT_CALVIEW_SHORT"]: this._msgs["D_DTFMT_CALVIEW_LONG"];
			var asText = oFormatter.format(oDate).split('|');
	
			if (this.nCalViewAltCal == 2) {                 //Hebrew
				var asAltText = oAltCalFormatter.format(oDate).split('|');
				var sRLE = this.isRTL? D_RLE : '';
				asHtml[i] = '<table border=0 role="presentation"><tr><td nowrap class="s-cv-text-large">' + asText[0] + '</td>'
					+ '<td nowrap width="100%" class="s-cv-text"><b>' + asText[1] + '</b>' + '<br>' + asText[2] + '</td>';
				if (bWeekEnd && (fNarrow || this.isRTL))
					asHtml[i] += '<td nowrap class="s-cv-text" align="' + D_ALIGN_REVERSE + '">' + sRLE + '<b>' + asAltText[2] + '</b><br>' + asAltText[1] + '</td>';
				else if(bWeekEnd || fNarrow)
					asHtml[i] += '<td nowrap class="s-cv-text" align="' + D_ALIGN_REVERSE + '">' + sRLE + asAltText[1] + '<br>' + asAltText[0]
					          +  '<td nowrap class="s-cv-text-large">' + sRLE + asAltText[2] + '</td>';
				else
					asHtml[i] += '<td nowrap class="s-cv-text" align="' + D_ALIGN_REVERSE + '">' + sRLE + '<br>' + asAltText[0] + '&nbsp;' + asAltText[1] + '</td>'
					          +  '<td nowrap class="s-cv-text-large">' + sRLE + asAltText[2] + '</td>';
				asHtml[i] += '</tr></table>';
			}
			else {
				asHtml[i] = '<table border=0 role="presentation"><tr><td nowrap class="s-cv-text-large">' + asText[0] + '</td>'
				          + '<td nowrap class="s-cv-text"><b>' + asText[1] + '</b>'
				          + (oAltCalFormatter? '&nbsp;' + oAltCalFormatter.format(oDate): '') + '<br>'
				          + asText[2] + '</td></tr></table>';
			}
			oDate.adjustDays(0, 0, 1);
		}
		return asHtml;
	},
	onDatasetComplete: function(){
		this.clearCalendarEntries();
		// this.checkForConflicts(); // already checked by calendarDataStore
		this.drawCalendarEntries();
	
		// Indicate the the first data has been available
//		com_ibm_dwa_globals.fFirstDataAvailable = true;
//		com_ibm_dwa_globals.fJesterReady = true;
	},
	generateImageHtml: function(sUrl, nWidth, nHeight, nXOffset, nYOffset, sAltText, sId){
		// nakakura
		// High-Contrast Mode
		if (dojo.hasClass(dojo.body(), "dijit_a11y")) {
			return '<img alt="" style="border-width:0px;" src="' + this.sTransparent + '"' + ' width="' + nWidth + '" height="' + nHeight + '"/>'
				+ '<span style="display:inline-block;top:5px;left:2px;position:absolute;width:'+nWidth+'px;height:'+nHeight+'px;overflow:hidden">'
				+ '<img alt="' + sAltText + '" src="' + sUrl + '" style="position:absolute;display:block;border-width:0px;top:-'+ nYOffset +'px;left:-'+ nXOffset +'px;"/></span>';
		}
		var sImg = '<img alt="' + sAltText + '" src="' + this.sTransparent + '"'
				+ ' width="' + nWidth + '" height="' + nHeight + '"'
				+ (sId? ' id="' + sId + '"': '');
		if (this.asConsolidateImages[sUrl] && this.asConsolidateImages[sUrl].fLoaded) {
			sImg += ' style="border-width:0px;background-position: -' + nXOffset + 'px -' + nYOffset  + 'px;'
				+ ' background-image: url(' + sUrl + ');">';
		} else {
			sImg += ' xoffset="' + nXOffset + '" yoffset="' + nYOffset  + '"'
				+ ' consolidatedImage="' + sUrl + '">';
			this.asConsolidateImages[sUrl] = {};
		}
		return sImg;
	},
	loadImage: function(asIds){
		for (sConsolidateImage in this.asConsolidateImages) {
			if (!this.asConsolidateImages[sConsolidateImage].fLoaded) {
				this.asConsolidateImages[sConsolidateImage].fLoaded = true;
				new dwa.common.consolidatedImageListener(asIds, sConsolidateImage);
			}
		}
	},
	escape: function(sHtmlText){
		// fixed error in displaying Google calendar entry if the entry doesn't have location information. (SPR MMII7VAC5W)
		if (!sHtmlText)
			return '';
		return sHtmlText
				.replace(/&/g, '&amp;')
				.replace(/&amp;#/g, '&#')
				.replace(/\"/g,'&quot;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
				.replace(/ /g, '&nbsp;');
	},
	generateCalendarEntryHtml: function(oEvent, nIndex, fSelected, oSelectedElem, fInDrag){
		var a = [], n = 0;
	
		// summary
		var sSubject = this.escape(oEvent.sSubject);
		var sLocation = this.escape(oEvent.sLocation);
		if (this.fSummarize || this.bTimeSlotView) {
			if (oEvent.bAllday || this.fNarrowSummaryView) {
				var sSummary = (this.fSummarize? '<b unselectable="on">': '') + sSubject.replace(/\s/g, '&nbsp;') + (this.fSummarize? '</b>': '');
			} else {
				var sChair = this.escape(this.bShowAlternateName && oEvent.sAltChair? oEvent.sAltChair: oEvent.sChair);
				var sSummary = (this.fSummarize? '<b unselectable="on">': '') + sSubject.replace(/\s/g, '&nbsp;') + (this.fSummarize? '</b>': '')
					+ (sLocation? '<br>' + sLocation.replace(/\s/g, '&nbsp;'): '')
					+ (oEvent.sType == 'Meeting' && sChair? '<br>' + sChair.replace(/\s/g, '&nbsp;'): '');
			}
		} else {
			var sSummary = sSubject;
		}
	
		// start of calendar entry
		if (fSelected) {
			var nWidth = oSelectedElem.offsetWidth;
			var nHeight = oSelectedElem.offsetHeight;
			var sId = this.sId + (fInDrag? '-entry-drag-innerframe': '-entry-selected-innerframe');
			a[n++] = '<div class="s-cv-entry" id=' + sId
				+ ' style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:100%; height:' + nHeight + 'px;"'
				+ ' unid="' + oEvent.sUnid + '"'
				+ ' calendar_start_notes="' + oEvent.sThisStartDate + '"'
				+ ' calendar_external="' + (oEvent.fExternal ? "1" : "0") + '"' 
				+ (oEvent.fExternal ? (' calendar_index="' + oEvent.nIndex + '"') : '');
		} else {
			a[n++] = '<div id=' + this.sId + '-entry' + nIndex + ' class="s-cv-entry" role="link" aria-haspopup="true"'
				+ ' calendar_type="' + oEvent.sType + '" unid="' + oEvent.sUnid + '" '
				+ ' calendar_date="' + oEvent.oStartDate.getISO8601String() + '"'
				+ ' calendar_index="' + oEvent.nIndex + '"'
				+ ' calendar_start="' + oEvent.oStartTime.getISO8601String() + '"'
				+ ' calendar_end="' + oEvent.oEndTime.getISO8601String() + '"'
				+ ' calendar_start_notes="' + oEvent.sThisStartDate + '"'
				+ ' calendar_bgcolor1="' + oEvent.sBGColor1 + '"'
				+ ' calendar_bgcolor2="' + oEvent.sBGColor2 + '"'
				+ ' calendar_fontcolor="' + oEvent.sFontColor + '"'
				+ ' calendar_bordercolor="' + oEvent.sBorderColor + '"'
				+ ' calendar_external="' + (oEvent.fExternal ? "1" : "0") + '"'
				+ (oEvent.fExternal ? (' calendar_generator="' + oEvent.sGenerator + '"') : '');
		}
	
		// event handler
		if (fSelected) {
			if (!fInDrag) {
				if(dojo.isWebKit || dojo.isMozilla){
					a[n++] = this._attachEvent('click');	// no need to handle double click event in Safari
				}else{
					a[n++] = this._attachEvent('click|dblclick');
				}
			}
			a[n++] = '>';
		} else {
			a[n++] = this._attachEvent('mouseover|mouseout|click|dblclick|contextmenu') + '>';
		}
	
		// gradient
		if (fSelected) {
			var sId = this.sId + (fInDrag? '-entry-drag-gradient': '-entry-selected-gradient');
			a[n++] = this.insertGradientHtml(sId, 's-cv-entry', this.oColorMap['entry-selected-light'], this.oColorMap['entry-selected-dark'], this.oColorMap['entry-selected-border'], fInDrag? 50: 0);
		} else {
			var sBorderColor = this.fSummarize? '#5f5f5f': oEvent.sBorderColor;
			a[n++] = this.insertGradientHtml(this.sId + '-entry' + nIndex + '-gradient', 's-cv-entry', oEvent.sBGColor1, oEvent.sBGColor2, sBorderColor);
		}
	
		// selected area
		if (fSelected) {
			if(dojo.isMozilla){
				a[n++] = this.generateSelectedAreaHtml('-entry-selected-area'
					, 'top:2px;' + D_ALIGN_DEFAULT + ':2px;width:' + (nWidth - 4) + 'px;height:' + (nHeight - 4) + 'px;'
					, true /* should not handle keyboard event here (SPR SQZO7Q9BSX) */);
			}else if(dojo.isWebKit){
				a[n++] = this.generateSelectedAreaHtml('-entry-selected-area'
					, 'top:4px;' + D_ALIGN_DEFAULT + ':4px;width:' + (nWidth - 8) + 'px;height:' + (nHeight - 8) + 'px;'
					, true /* should not handle keyboard event here (SPR SQZO7Q9BSX) */);
			}else{
				a[n++] = this.generateSelectedAreaHtml('-entry-selected-area'
					, 'top:1px;' + D_ALIGN_DEFAULT + ':1px;width:' + (nWidth - 2) + 'px;height:' + (nHeight - 3) + 'px;font-size:10%;'
					, true /* should not handle keyboard event here (SPR SQZO7Q9BSX) */);
			}
		}
	
		// start of inner frame
		var sFontColor = fSelected? this.oColorMap['entry-selected-font']: oEvent.sFontColor;
		// nakakura
		// add s-cv-entry-innerframe-height for High-Contrast Mode
		var ariaDescribeId = this.sId + '-entry' + nIndex + '-target';
		//console.log("CalEntry: ariaDescribeId = " + ariaDescribeId);	
		if (!this.fSummarize) {
			a[n++] = '<div tabindex="0" class="s-cv-entry-innerframe s-cv-entry-innerframe-height s-cv-text" unselectable="on"'
				+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;width:100%;color:' + sFontColor + ';white-space: nowrap;"'
				+ ((this.sType != 'D' || !oEvent.bAllday) && !this.fDisableDragDrop ? (' com_ibm_dwa_ui_draggable_redirect="' + this.sId + '"') : '') + '>';
		}
	
		// time range
		var oTimeFormatter = new dwa.date.dateFormatter(101);
		if (this.fSummarize && !oEvent.bAllday) {
			var sTime = oTimeFormatter.format(oEvent.oStartTime) + '&nbsp;-&nbsp;' + oTimeFormatter.format(oEvent.oEndTime);
			a[n++] = '<div class="s-cv-entry-innerframe s-cv-text" unselectable="on"'
				+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;' + D_PADDING_DEFAULT + ':10px;width:145px;height:' + this.nTextHeight + 'px;color:' + sFontColor + ';white-space:nowrap;">'
				+ sTime
				+ '</div>';
		} else if (!this.bTimeSlotView && !oEvent.bAllday) {
			a[n++] = oTimeFormatter.format(oEvent.oStartTime);
		}
	
		// icon
		if (this.fSummarize || this.bTimeSlotView) {
			// sIconParam = 'URL WIDTH HEIGHT XOFFSET YOFFSET'
			var sImg = "";
			if(oEvent.sIconParam){
				var asIconParam = oEvent.sIconParam.split(' ');
				sImg = this.generateImageHtml(this.buildResourcesUrl(asIconParam[0]), asIconParam[1], asIconParam[2], asIconParam[3], asIconParam[4], (oEvent.sType ? oEvent.sType : ""));
			}
			if (this.fSummarize) {
				if (this.fNarrowSummaryView)
					var sStyle = D_ALIGN_REVERSE + ':0px;width:20px;';
				else
					var sStyle = D_ALIGN_DEFAULT + ':0px;' + D_PADDING_DEFAULT + ':150px;width:170px;';
				a[n++] = '<div class="s-cv-entry-innerframe s-cv-text" unselectable="on"'
					+ ' style="' + sStyle + 'top:0px;height:20px;">'
					+ sImg
					+ '</div>';
			} else {
				a[n++] = sImg;
			}
		}
	
		// summary
		if (this.fSummarize) {
			oEvent.nTextHeight = this.getTextHeight('s-cv-entry-innerframe s-cv-text', true, sSummary);
			if (this.fNarrowSummaryView)
				var sStyle = D_ALIGN_DEFAULT + ':0px;padding-top:' + (oEvent.bAllday? 3: this.nTextHeight) + 'px;' + D_PADDING_DEFAULT + ':20px;'
					+ (oEvent.bAllday? "padding-right" + ':20px;': '');
			else
				var sStyle = D_ALIGN_DEFAULT + ':0px;' + D_PADDING_DEFAULT + ':170px;';
			a[n++] = '<div class="s-cv-entry-innerframe s-cv-text" unselectable="on"'
				+ ' style="top:0px;' + sStyle + 'width:100%;height:100%;">'
				+ '<div style="width:100%;height:100%;color:' + sFontColor + ';white-space:nowrap;overflow:hidden;">'
				+ sSummary
				+ '</div>'
				+ '</div>';
			oEvent.nTextHeight += (this.fNarrowSummaryView && !oEvent.bAllday? this.nTextHeight: 0);
		} else if (fSelected) {
			a[n++] = '<span id=' + this.sId + '-entry-selected-summary unselectable="on"'
				+ ((this.sType != 'D' || !oEvent.bAllday) && !this.fDisableDragDrop ? (' com_ibm_dwa_ui_draggable_redirect="' + this.sId + '"') : '') + '>'
				+ '&nbsp;'
				+ sSummary
				+ '</span>';
		} else {
			a[n++] = '&nbsp;' + sSummary;
		}
	
		// end of inner frame
		if (!this.fSummarize) {
			a[n++] = '</div>';
		}
	
		// end of calendar entry
		a[n++] = '</div>';
		
		// insert hidden aria div that matches the ariaDescribeBy target attr created above for screen reading the popup 
		var sChair = this.escape(this.bShowAlternateName && oEvent.sAltChair? oEvent.sAltChair: oEvent.sChair);			
		var ariaText = ((!oEvent.bAllday ? oTimeFormatter.format(oEvent.oStartTime) + (oEvent.hasEndTime ? ' - ' + oTimeFormatter.format(oEvent.oEndTime) + ' ' : '') : '')
				+ this.escape(oEvent.sSubject) + (oEvent.sLocation ? ' ' + this.escape(oEvent.sLocation): '') 
				+ (oEvent.sType == 'Meeting' && sChair ? ' ' + sChair: '')).replace(/\s/g, '&nbsp;');
		var ariaHiddenDiv = '<div id="' + ariaDescribeId + '" aria-hidden="true" style="display:none">' 
							+ ariaText +'</div>';
		
		a[n++] = ariaHiddenDiv;
		
		return a.join('');
	},
	drawCalendarEntries: function(){
		// no calendar entries in year view
		if (this.sType == 'Y')
			return;
	
		var oDate = this.oCalViewStart.clone();
		oDate.fDateOnly = true;
		
		var nDaysInRow = this.fSummarize? this.nDays: this.sType=='2' || this.sType=='M'? 7: this.nDays;
		var nRowsInMonth = this.fSummarize? 1: this.sType=='M'? 6: this.sType=='2'? 2: 1;
		var nIndex = 0;
		var sLastDate = '';
		var oFormatter = new dwa.date.dateFormatter(4);
		var asGridHtml = [''], asAllDayHtml = [''];
		if (this.nCalViewAltCal)
			var oAltCalFormatter = new dwa.date.altCalendarFormatter(this.nCalViewAltCal);
	
		for (var j=0; j<nRowsInMonth; j++) {
			for (var i=0; i<nDaysInRow; i++) {
				if (this.sType == 'F') {
					while (this.isWeekEnd(oDate))
						oDate.adjustDays(0, 0, 1);
				}
				var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
				for (var n=0; n<aoEvents.length; n++) {
					var oEvent = aoEvents[n];
					if (oEvent.bHide)
						continue;
					oEvent.nIndexInView = nIndex;
					oEvent.sThisStartDate = new dwa.common.notesValue(oEvent.oStartTime.getDate(),
											dwa.date.zoneInfo.prototype.oUTC).toString().replace(',00', '');
					if (this.fSummarize) {
						// category by date
						// note: no need date header in One Day view
						if (sLastDate != oDate.getISO8601String() && this.sType != 'D') {
							sLastDate = oDate.getISO8601String();
							var sAltHTML = oAltCalFormatter? oAltCalFormatter.format(oDate) : '';
							if (this.nCalViewAltCal == 2) {    //Hebrew
								var asAltText = oAltCalFormatter.format(oDate).split('|');
								var sRLE = this.isRTL? D_RLE : '';
								var sPDF = this.isRTL? D_PDF : '';
								sAltHTML = sRLE + asAltText[1] + '&nbsp;' + asAltText[2] + ',&nbsp;' + asAltText[0] + sPDF;
							}
							asGridHtml[asGridHtml.length]
								= '<div id="' + this.sId + '-date' + sLastDate + '" role="gridcell"'
								+ ' class="s-cv-entry s-cv-text" style="' + D_ALIGN_DEFAULT + ':1px;width:100%;font-weight:bold;padding-top:2px;' + D_PADDING_DEFAULT + ':2px;"'
								+ ' calendar_date="' + sLastDate + '"'
								+ this._attachEvent('click|dblclick') + '>'
								+ this.generateImageHtml(this.sColIcons, 10, 10, 81, 100, "collapse-expand", this.sId + '-dateicon' + sLastDate)
								+ '&nbsp;'
								+ oFormatter.format(oDate)
								+ (oAltCalFormatter? '&nbsp;(' + sAltHTML + ')': '')
								+ '</div>'
								+ '</div>';
						}
						asGridHtml[asGridHtml.length] = this.generateCalendarEntryHtml(oEvent, nIndex);
					} else if (this.bTimeSlotView && oEvent.bAllday) {
						asAllDayHtml[asAllDayHtml.length] = this.generateCalendarEntryHtml(oEvent, nIndex);
					} else {
						asGridHtml[asGridHtml.length] = this.generateCalendarEntryHtml(oEvent, nIndex);
					}
					nIndex ++;
				}
				if (asAllDayHtml.length > 1) {
					var oElem = dojo.doc.getElementById(this.sId + '-allday');
					if (oElem) {
						asAllDayHtml[0] = oElem.innerHTML;
						oElem.innerHTML = asAllDayHtml.join('');
					}
					asAllDayHtml = [''];
				}
				if (asGridHtml.length > 1 && !this.fSummarize && !this.bTimeSlotView) {
					var oElem = dojo.doc.getElementById(this.sId + '-grid-date' + j + '-' + i + '-innerframe');
					if (oElem) {
						// @kami@ For some reason, when some entries (e.g. iCal) are inserted after another type
						// of entries (e.g. Notes) using FederatedDataStore, existing entries move a few pixels
						// to the right. This happens only on IE in the BiDi mode.
						// The code below is a workaound to avoid that strange behavior.
						if(dojo.isIE && this.isRTL){ // kami
							var oDiv = dojo.doc.createElement('DIV');
							oDiv.innerHTML = asGridHtml.join('');
							while(oDiv.childNodes.length > 0){
								oElem.appendChild(oDiv.removeChild(oDiv.firstChild));
							}
						}else{
							asGridHtml[0] = oElem.innerHTML;
							oElem.innerHTML = asGridHtml.join(''); //#001
						}
					}
					asGridHtml = [''];
				}
				oDate.adjustDays(0,0,1);
			}
		}
	
		if (asGridHtml.length > 1 && (this.fSummarize || this.bTimeSlotView)) {
			var oElem = dojo.doc.getElementById(this.sId + (this.fSummarize? '-summary': '-timeslot'));
			if (oElem) {
				if(dojo.isIE && this.isRTL){ // kami
					var oDiv = dojo.doc.createElement('DIV');
					oDiv.innerHTML = asGridHtml.join('');
					while(oDiv.childNodes.length > 0){
						oElem.appendChild(oDiv.removeChild(oDiv.firstChild));
					}
				}else{
					asGridHtml[0] = oElem.innerHTML;
					oElem.innerHTML = asGridHtml.join('');
				}
			}
			asGridHtml = [''];
		}

		this.nEntries = nIndex;
		this.updateCalendarEntries();
	
		if (this.fSummarize)
			this.updateSelectedArea();
	
		this.loadImage(this.fSummarize? [this.sId + '-summary']: [this.sId + '-timeslot', this.sId + '-allday']);
	},
	insertGradientHtml: function(sId, sClass, sColor1, sColor2, sBorderColor, nAlpha){
		if(!dojo.isMozilla && !dojo.isWebKit){
			var sHtml = '<v:' + (sBorderColor? 'round': '') + 'rect id="' + sId + '"'
					+ (sClass? ' class="' + sClass + '"': '')
					// width of gradient area should be narrow in BiDi. (SPR KZHU7PFBDU)
					+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':' + (this.isRTL? 1: 0) + 'px;"'
					+ (sBorderColor? ' strokecolor="' + sBorderColor + '" arcsize="5%"': ' stroked="false"') + '>'
					+ '<v:fill type="gradient" color="' + sColor1 + '" color2="' + sColor2 + '"' + (nAlpha? ' opacity="' + nAlpha + '%"': '') + '>'
					+ '</v:fill></v:' + (sBorderColor? 'round': '') + 'rect>';
		}else{
			var sHtml = '<canvas id="' + sId + '"' + (sClass? ' class="' + sClass + '"': '')
					+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':0px;width:100%;height:100%;"'
					+ ' color2="' + sColor1 + '" color="' + sColor2 + '"' + (sBorderColor? ' color3="' + sBorderColor + '"': '')
					+ (nAlpha? ' alpha="' + nAlpha + '"': '') + '></canvas>';
		}
		return sHtml;
	},
	getSelectedDateTime: function(x, y, oElem){
		var oObj = this.getSelectedDateTimeAndPosition(x, y, oElem);
		return (oObj? oObj.oDateTime: null);
	},
	getSelectedDateTimeAndPosition: function(x, y, oElem, bInDrag){
		if (oElem) {
			var nIndex = oElem.getAttribute('calendar_index');
			var oEvent = this.oCalendarDataStore.getEventById(nIndex);
		}
	
		var nDaysInRow = (this.sType=='2' || this.sType=='M'? 7: this.nDays);
		var nRowsInMonth = (this.sType=='M'? 6: (this.sType=='2'? 2:1));
		var oDate = this.oCalViewStart.clone();
		var nOffsetX = 0, nOffsetY = 0;
	
		if (this.fSummarize) {
			for (var i=0; i<this.nDays; i++) {
				if (this.anTop[i] <= y && y < this.anTop[i+1])
					break;
				oDate.adjustDays(0, 0, 1);
			}
			// return null if drop out
			if (i>=this.nDays)
				oDate = null;
		} else if (this.bTimeSlotView) {
			if (bInDrag && (x < this.anLeft[0] || this.anLeft[nDaysInRow] <= x)) {
				// SPR #NYWU7FLBEB: Dragging to time column is not allowed in Hannover/Notes
				return null;
			}
			// get date
			for (var i=0; i<nDaysInRow; i++, oDate.adjustDays(0, 0, 1)) {
				if (this.sType == 'F') {
					while (this.isWeekEnd(oDate))
						oDate.adjustDays(0, 0, 1);
				}
				if (this.anLeft[i] <= x && x < this.anLeft[i+1]) {
					nOffsetX = this.anLeft[i];
					break;
				}
			}
			// get hour
			oDate.fDateOnly = false;
			// fix problem that select all day entry moves scroll bar to the top
			if (oEvent && oEvent.bAllday) {
				oDate.nHours = this.oCalendar.nHours;
				oDate.nMinutes = this.oCalendar.nMinutes;
				oDate.nSeconds = this.oCalendar.nSeconds;
				oDate.nMilliseconds = this.oCalendar.nMilliseconds;
				nOffsetY = 0;
			} else if (bInDrag && y < 0) {
				// do not allow drop on calendar header area (SPR KREY7TE4BD)
				return null;
			} else {
				oDate.nHours = Math.floor(y / this.nCellHeight);
				if (this.nTimeSlotDur < 60)
					oDate.nMinutes = this.nTimeSlotDur * Math.floor((y % this.nCellHeight)/ this.nMinGridHeight);
				nOffsetY = y - (y % this.nMinGridHeight);
			}
		} else {
			for (var j=0; j<nRowsInMonth; j++) {
				if (this.anTop[j] <= y && y < this.anTop[j+1])
					break;
				oDate.adjustDays(0, 0, nDaysInRow);
			}
			for (var i=0; i<nDaysInRow; i++) {
				if (this.anLeft[i] <= x && x < this.anLeft[i+1])
					break;
				oDate.adjustDays(0, 0, 1);
			}
			// set hour
			var oStart = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart);
			oDate.nHours = oStart.nHours;
			nOffsetX = this.anLeft[i];
			nOffsetY = this.anTop[j] + this.nTextHeight;
		}
	
		// return dwa.date.calendar object
		return {oDateTime:oDate, nOffsetX:nOffsetX, nOffsetY:nOffsetY};
	},
	focus: function(){
		// move focus to handle keyboard event
		var oElem = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
		if (!oElem) oElem = dojo.doc.getElementById(this.sId + '-entry-selected-area');
		if (!oElem) oElem = dojo.doc.getElementById(this.sId + (this.bTimeSlotView && this.bDateSelected? '-header-selected-area': '-selected-area'));
		if (oElem) {
			try { // prevent error in IE in resizing browser window when calendar view is hidden.
				// nakakura
				if (dojo.isIE || dojo.isMozilla) {
					if (8 <= dojo.isIE || 3 <= dojo.isMozilla) {
						var id = this.sId + '-label-slot';
						dojo.byId(id).innerHTML = this.getTitleForScreenReader();
						dijit.setWaiState(oElem, "labelledby", id);
					} else
						oElem.title = this.getTitleForScreenReader();
					// JAWS does not read aria-labelledby/title attribute jsut by changing it.
					dojo.doc.getElementById(this.sId + '-navigator-current').focus();
				}
				oElem.focus();
			}
			catch (e) {}
		}
	
		if(dojo.isWebKit){
			// scroll bar is automatically moved in IE and Firefox.
			if (oElem.id == this.sId + '-selected-area') {
				var oDiv = dojo.doc.getElementById(this.sId + (this.fSummarize? '-summary': this.bTimeSlotView? '-timeslot': this.bYearView? '-year' : '-grid'));
				if (oDiv.scrollTop > oElem.offsetTop)
					oDiv.scrollTop = oElem.offsetTop;
				else if ((oDiv.scrollTop + oDiv.clientHeight) < (oElem.offsetTop + oElem.offsetHeight))
					oDiv.scrollTop = oElem.offsetTop + oElem.offsetHeight - oDiv.clientHeight;
			}
		}
	},
	// nakakura
	getTitleForScreenReader: function() {
		// JAWS10 does not support safari
		if (!dojo.isIE && !dojo.isMozilla)
			return "";
		// focus is on the entry
		if (this.oSelectedElem && this.oSelectedEvent)
			return (this.oSelectedEvent.sStoreTitle ? this.oSelectedEvent.sStoreTitle + " " : "") +  this.oSelectedEvent.sType + " " + (dojo.isIE ? this.oSelectedElem.innerText : this.oSelectedElem.textContent);
		// focus is on the timeslot
		else if (this.bTimeSlotView && !this.bDateSelected) {
			return this.oCalendar.nHours + ":" + (this.oCalendar.nMinutes == 0 ? "0" : "") + this.oCalendar.nMinutes;
		// focus is on the date
		} else {
			if (!this.oFormatterA11y) 
				this.oFormatterA11y = new dwa.date.dateFormatter();
			return (this.oFormatterA11y.asDays[this.oCalendar.nDay] + " " + this.oCalendar.nDate + " "
					+ this.oFormatterA11y.asMonths[this.oCalendar.nMonth] + " " + this.oCalendar.nYear);
		}
	},
	setDate: function(oCalendar){
		var oProperty = dwa.common.commonProperty.get('p-e-calendarview-currentselected');
		oCalendar.fDateOnly = true;
		if (oProperty.vValue != oCalendar.getISO8601String()) {
			dwa.common.commonProperty.get('p-e-calendarview-currentselected').setValue(oCalendar.getISO8601String());
		}
		oCalendar.fDateOnly = false;
	},
	getDate: function(){
		if(this.oCalendar){
			return this.oCalendar.clone();
		}else{
			var oProperty = dwa.common.commonProperty.get('p-e-calendarview-currentselected');
			var oCalendar = (new dwa.date.calendar).setISO8601String(oProperty.vValue);
			return oCalendar;
		}
	},
	updateSelectedArea: function(){
		// calendar data is not retrived yet
		if (!this.oCalViewStart)
			return;
	
		var oToday = (new dwa.date.calendar).setDate(new Date());
		oToday.fDateOnly = false;
		oToday.nHours = this.oCalendar.nHours;
		oToday.nMinutes = this.oCalendar.nMinutes;
		oToday.nSeconds = this.oCalendar.nSeconds;
		oToday.nMilliseconds = this.oCalendar.nMilliseconds;
	
		if (this.bYearView) {
			var oDate = this.oCalendar.clone();
		} else {
			var oDate = this.oCalViewStart.clone();
			oDate.fDateOnly = false;
			oDate.nHours = this.oCalendar.nHours;
			oDate.nMinutes = this.oCalendar.nMinutes;
			oDate.nSeconds = this.oCalendar.nSeconds;
			oDate.nMilliseconds = this.oCalendar.nMilliseconds;
		}
		var nRowsInMonth = (this.bYearView || this.fSummarize)? 1: this.sType=='M'? 6: this.sType=='2'? 2: 1;
		var nDaysInRow = this.bYearView? 1: this.fSummarize? this.nDays: this.sType=='2' || this.sType=='M'? 7: this.nDays;
	
		var bHeaderSelectedAreaUpdated = false, bSelectedAreaUpdated = false, bHeaderCurrentUpdate = false;
	
		for (var j=0; j<nRowsInMonth; j++) {
			for (var i=0; i<nDaysInRow; i++) {
				if (this.sType == 'F') {
					while (this.isWeekEnd(oDate))
						oDate.adjustDays(0, 0, 1);
				}
				if (!this.bYearView && !this.fSummarize && oDate && oDate.equals(oToday)) {
					bHeaderCurrentUpdate = true;
					this.setStyle('-header-today', {
						display : '',
						left : !this.isRTL ? this.anLeft[i] + 'px' : undefined,
						right : this.isRTL ? this.anLeft[i] + 'px' : undefined,
						width : (this.anWidth[i]-1) + 'px'
					});
					this.setStyle('-timeslot-today', {
						display : '',
						left : !this.isRTL ? this.anLeft[i] + 'px' : undefined,
						right : this.isRTL ? this.anLeft[i] + 'px' : undefined,
						width : (this.anWidth[i]-1) + 'px'
					});
					this.drawGradient(this.sId + '-header-today-gradient', this.oColorMap['header-today-dark'], this.oColorMap['header-today-light']);
				}

				if (oDate && oDate.equals(this.oCalendar)) {
					if (this.bYearView) {
						bSelectedAreaUpdated = true;
						oDate.fDateOnly = true;
						var oMonth = dojo.doc.getElementById(this.sId + '-month' + (oDate.nMonth + 1));
						var oElem = dojo.doc.getElementById(this.sId + '-date' + oDate.getISO8601String());
						if (oElem && oMonth)
							var nTop = oMonth.offsetTop + oElem.offsetTop, nLeft = oMonth.offsetLeft + oElem.offsetLeft, nWidth = oElem.offsetWidth, nHeight = oElem.offsetHeight;
						else
							var nTop = -10, nLeft = -10, nWidth = 10, nHeight = 10;
					} else if (this.fSummarize) {
						bSelectedAreaUpdated = true;
						oDate.fDateOnly = true;
						var oElem = dojo.doc.getElementById(this.sId + '-date' + oDate.getISO8601String());
						if (oElem)
							var nTop = oElem.offsetTop, nLeft = 0, nWidth = oElem.offsetWidth, nHeight = this.nTextHeight;
						else
							var nTop = -10, nLeft = -10, nWidth = 10, nHeight = 10;
					} else if (this.bTimeSlotView && this.bDateSelected) {
						bHeaderSelectedAreaUpdated = true;
						var nTop = 0;
						var nHeight = this.nTextHeight * 2 - 1;
						var nLeft = this.anLeft[i];
						var nWidth = this.anWidth[i];
					} else {
						bSelectedAreaUpdated = true;
						var nTop = (this.bTimeSlotView? this.nCellHeight * this.oCalendar.nHours: this.anTop[j]);
						if (this.bTimeSlotView && this.nTimeSlotDur < 60)
							nTop += this.nMinGridHeight * Math.floor(this.oCalendar.nMinutes / this.nTimeSlotDur);
						var nHeight = (this.bTimeSlotView? this.nMinGridHeight-1: this.nTextHeight);
						var nLeft = this.anLeft[i];
						var nWidth = this.anWidth[i];
					}

					var obj;
					if(dojo.isMozilla){
						obj = {display : '', top : (nTop+1) + 'px', width : (nWidth-3) + 'px', height : (nHeight-2) + 'px'};
						obj[D_ALIGN_DEFAULT] = (nLeft+1) + 'px';
					}else if(dojo.isWebKit){
						obj = {display : '', top : (nTop+2) + 'px', width : (nWidth-5) + 'px', height : (nHeight-4) + 'px'};
						obj[D_ALIGN_DEFAULT] = (nLeft+2) + 'px';
					}else{
						var q = (this.bTimeSlotView && dojo.doc.compatMode == "CSS1Compat" && dojo.isIE < 8) ? 1 : 0;
						obj = {display : '', top : nTop+q + 'px', width : (nWidth-1) + 'px', height : nHeight + 'px'}; //#003
						obj[D_ALIGN_DEFAULT] = nLeft+q + 'px';
					}
					this.setStyle(bHeaderSelectedAreaUpdated? '-header-selected-area': '-selected-area', obj);

					if (this.fSummarize) {
						var oElem = dojo.doc.getElementById(this.sId + '-selected-area-gradient');
						if (oElem)
							this.drawGradient(oElem);
					}
					// move focus to handle keyboard event
					this.focus();
				}
				oDate.adjustDays(0, 0, 1);
			}
		}
	
		// update left days in calendar footer
		this.updateFooter();
	
		var oMap = {
			'-header-selected-area': bHeaderSelectedAreaUpdated,
			'-selected-area':        bSelectedAreaUpdated,
			'-header-today':         bHeaderCurrentUpdate,
			'-timeslot-today':       bHeaderCurrentUpdate
		};
		for (var s in oMap) {
			if (!oMap[s])
				this.setStyle(s, {display:'none', left:!this.isRTL?'0px':undefined, right:this.isRTL?'0px':undefined, top:'0px'});
		}
	},
	getWeekNumber: function(oCalendar){
		// last day in this week
		var oDate = oCalendar.clone();
		oDate.fDateOnly = true;
		var nDays = (7 + this.nFirstDayInYear - oDate.nDay - 1) % 7;
		oDate.adjustDays(0, 0, nDays);
	
		// Jan 1 in this year
		var oNewYear = oDate.clone();
		oNewYear.nMonth = 0; oNewYear.nDate = 1;
		oNewYear.setDate(oNewYear.getDate(), this.oCalendar.oZoneInfo);
	
		do {
			// last day in the first week
			var nDays = (7 + this.nFirstDayInYear - oNewYear.nDay - 1) % 7;
			oNewYear.adjustDays(0, 0, nDays);
	
			if (oNewYear.nDate < this.nMinDaysInFirstWeek) {
				var nDays = (oDate.getUTCDate() - oNewYear.getUTCDate()) / 86400000;
				if (nDays <= 0) {
					// Jan 1 in the last year
					oNewYear.nYear --; oNewYear.nMonth = 0; oNewYear.nDate = 1;
					oNewYear.setDate(oNewYear.getDate(), this.oCalendar.oZoneInfo);
				} else
					// adjust one week later
					oNewYear.adjustDays(0, 0, 7);
			} else {
				// first day in the year
				oNewYear.adjustDays(0, 0, -6);
				// total days from the first day in the year
				var nDays = (oDate.getUTCDate() - oNewYear.getUTCDate()) / 86400000;
				// week number in this year
				var nWeeks = 1 + Math.floor(nDays / 7);
			}
		} while (!nWeeks);
	
		return nWeeks;
	},
	generateFooterText: function(sType){
		if (sType == 'M') {
			var nMonths = this.oCalendar.nMonth + 1;
			var nLeftMonths = 12 - nMonths;
			var sFormat = nLeftMonths==0? this._msgs["L_CAL_LAST_MONTH"]: nLeftMonths==1? this._msgs["L_CAL_LEFT_MONTH"]: this._msgs["L_CAL_LEFT_MONTHS"];
			var sText = dwa.common.utils.formatMessage(sFormat, nMonths, nLeftMonths);
		} else if (sType == 'W') {
			// last day in this week
			var oDate = this.oCalendar.clone();
			var nWeeks = this.getWeekNumber(oDate);
			if (oDate.nMonth == 0 && nWeeks >= 52)
				var nLeftWeeks = 0;
			else {
				if (oDate.nMonth == 11 && nWeeks == 1)
					oDate.nYear ++;
				oDate.nMonth = 11; oDate.nDate = 31;
				oDate.setDate(oDate.getDate(), this.oCalendar.oZoneInfo);
				var nTotalWeeks = this.getWeekNumber(oDate);
				while (oDate.nMonth == 11 && nTotalWeeks == 1) {
					oDate.adjustDays(0, 0, -7);
					var nTotalWeeks = this.getWeekNumber(oDate);
				}
				var nLeftWeeks = nTotalWeeks - nWeeks;
			}
	
			var sFormat = nLeftWeeks==0? this._msgs["L_CAL_LAST_WEEK"]: nLeftWeeks==1? this._msgs["L_CAL_LEFT_WEEK"]: this._msgs["L_CAL_LEFT_WEEKS"];
			var sText = dwa.common.utils.formatMessage(sFormat, nWeeks, nLeftWeeks);
		} else {
			var oDate = this.oCalendar.clone();
			var oDateComp = this.oCalendar.clone();
			oDate.fDateOnly = oDateComp.fDateOnly = true;
			oDateComp.nMonth = oDateComp.nDate = 0;
			var nDays = (oDate.getUTCDate() - oDateComp.getUTCDate()) / 86400000;
			var nLeftDays = (oDate.isLeapYear()? 366: 365) - nDays;
			var sFormat = nLeftDays==0? this._msgs["L_CAL_LAST_DAY"]: nLeftDays==1? this._msgs["L_CAL_LEFT_DAY"]: this._msgs["L_CAL_LEFT_DAYS"];
			var sText = dwa.common.utils.formatMessage(sFormat, nDays, nLeftDays);
		}
		return sText;
	},
	updateFooter: function(sType){
		if (this.fHideFooter)
			return;
	
		if (sType)
			this.sFooterType = sType;
	
		var oDiv = dojo.doc.getElementById(this.sId + '-footer');
		if (oDiv) {
			var sText = this.generateFooterText(this.sFooterType);
			if (sText && oDiv.innerHTML != sText)
				oDiv.innerHTML = sText;
		}
	},
	updateCalendarEntries: function(){
		// calendar data is not retrived yet
		if (!this.oCalViewStart)
			return;
		// no calendar entries in year view
		if (this.sType == 'Y')
			return;
	
		var oDate = this.oCalViewStart.clone();
		oDate.fDateOnly = false;
		oDate.nHours = oDate.nSeconds = oDate.nMilliseconds = 0;
		var nDaysInRow = (this.fSummarize? this.nDays: this.sType=='2' || this.sType == 'M'? 7: this.nDays);
		var nRowsInMonth = (this.fSummarize? 1: this.sType=='M'? 6: (this.sType=='2'? 2:1));
		var sLastDate = '';
		var nTop = 0;
		var nIndex = 0;
		// calculate max height for summary view
		if (this.fSummarize) {
			var tmpDate = this.oCalViewStart.clone();
			tmpDate.fDateOnly = true;
			var tmpTop = 0;
			var sDate = '';
			this.anTop = [];
			for (var i=0; i<this.nDays; i++) {
				var aoEvents = this.oCalendarDataStore.getEventsByDate(tmpDate);
				this.anTop[i] = tmpTop;
				for (var n=0; n<aoEvents.length; n++) {
					var oEvent = aoEvents[n];
					if (oEvent.bHide)
						continue;
					if (sDate != tmpDate.getISO8601String() && this.sType != 'D') {
						sDate = tmpDate.getISO8601String();
						tmpTop += this.nTextHeight;
					}
					if (this.bCollapse[tmpDate.getISO8601String()])
						continue;
					tmpTop += oEvent.nTextHeight + 1;
				}
				tmpDate.adjustDays(0, 0, 1);
			}
			this.anTop[i] = tmpTop;
	
			this.bNeedHScroll = this.nViewAreaWidth < 200;
			this.bNeedVScroll = (this.nViewAreaHeight - (this.bNeedHScroll? this.nScrollBarWidth: 0)) < tmpTop;
	
			this.setStyle('-summary', {
				width: (this.nViewAreaWidth + (this.bNeedHScroll && !this.bNeedVScroll? this.nScrollBarWidth: 0))+ 'px',
				height: (this.nViewAreaHeight + (!this.bNeedHScroll && this.bNeedVScroll? this.nScrollBarWidth: 0)) + 'px',
				overflow: !(this.bNeedHScroll || this.bNeedVScroll)? 'hidden':'scroll'
			});
		}
	
		var nCurrentAlldayEventsInWeek = this.nAlldayEventsInWeek;
		this.nAlldayEventsInWeek = 0;
		var oDate = this.oCalViewStart.clone();
		oDate.fDateOnly = true;
		var oSelectedEntryElem = dojo.doc.getElementById(this.sId + '-entry-selected');
		for (var j=0; j<nRowsInMonth; j++) {
			for (var i=0; i<nDaysInRow; i++) {
				if (this.sType == 'F') {
					while (this.isWeekEnd(oDate))
						oDate.adjustDays(0, 0, 1);
				}
				var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
	
				if (!this.fSummarize && !this.bTimeSlotView) {
					var oElem = dojo.doc.getElementById(this.sId + '-grid-date' + j + '-' + i + '-innerframe');
					if (oElem) {
						var nMaxHeight = this.anHeight[j] - this.nTextHeight + 2;
						var nHeight = aoEvents.length * this.nMinCellHeight;
						var fScroll = nMaxHeight < nHeight;
						oElem.style.overflow = fScroll? 'scroll':'hidden';
						oElem.style.height = (nMaxHeight + (fScroll? this.nScrollBarWidth: 0)) + 'px';
					}
				}
	
				for (var n=0; n<aoEvents.length; n++) {
					var oEvent = aoEvents[n];
					if (oEvent.bHide)
						continue;
					if (this.fSummarize) {
						if (sLastDate != oDate.getISO8601String() && this.sType != 'D') {
							sLastDate = oDate.getISO8601String();
							this.setStyle('-date' + sLastDate, {
								top: nTop + 'px',
								height: this.nTextHeight + 'px',
								left: (!this.isRTL?'0px':undefined),
								right: (this.isRTL?'0px':undefined)
							});
							this.setStyle('-dateicon' + sLastDate, {
								backgroundPosition: '-' + (this.bCollapse[sLastDate]? 68: 81) + 'px -100px'
							});
							nTop += this.nTextHeight;
						}
						var sTop = nTop + 'px';
						var sHeight = (this.bCollapse[oDate.getISO8601String()]? 0: oEvent.nTextHeight) + 'px';
						var sLeft = '1px';
						var sWidth = ((this.nViewAreaWidth > 200? this.nViewAreaWidth: 200) - (this.bNeedVScroll? this.nScrollBarWidth: 0) - 3) + 'px';
						nTop += this.bCollapse[oDate.getISO8601String()]? 0: oEvent.nTextHeight + 1;
					} else if (this.bTimeSlotView) {
						if (oEvent.bAllday) {
							var sTop = n * this.nMinCellHeight + 'px';
							var sHeight = this.nMinCellHeight + 'px';
							var sLeft = this.anLeft[i] + 'px';
							var sWidth = this.anWidth[i] + 'px';
							this.nAlldayEventsInWeek = Math.max(this.nAlldayEventsInWeek, n+1);
						} else {
							var index = (oEvent.bConflicted? oEvent.nIndexInConflicts: 0);
							var total = (oEvent.bConflicted? oEvent.nConflicts: 1);
							// Last entry shoud be expanded. (SPR MMII7GVEH8)
							var expand = (oEvent.bExpandable? total - index: 1);
							// Fix problem that entry around 24:00 is not properly displayed. (SPR YQWG7DJBAR)
							var nTop = Math.min(24 * this.nCellHeight - this.nMinCellHeight,
							           Math.floor(((oEvent.oStartTime.nHours * 60 + oEvent.oStartTime.nMinutes) * this.nCellHeight) / 60));
							var nHeight = Math.max(this.nMinCellHeight,
										  Math.floor((oEvent.oEndTime.getDate().getTime() - oEvent.oStartTime.getDate().getTime()) * this.nCellHeight / 3600000));
							var sTop = nTop + 'px';
							var sHeight = Math.min(24 * this.nCellHeight - nTop, nHeight) + 'px';
							var sLeft = (this.anLeft[i] + this.anWidth[i]*index/total) + 'px';
							var sWidth = (this.anWidth[i]*expand/total) + 'px';
						}
					} else {
						var sTop = n * this.nMinCellHeight + 'px';
						var sHeight = this.nMinCellHeight + 'px';
						var sLeft = '0px';
						var sWidth = (this.anWidth[i] - (fScroll? this.nScrollBarWidth:0) - 2) + 'px';
					}
					var oElem = dojo.doc.getElementById(this.sId + '-entry' + nIndex);
					if (oElem) {
						this.setStyle(oElem, {
							top: sTop,
							left: (!this.isRTL?sLeft:undefined),
							right: (this.isRTL?sLeft:undefined),
							width: sWidth,
							height: sHeight,
							display: sHeight == '0px'? 'none': ''
						});
						if (oSelectedEntryElem && oSelectedEntryElem.parentNode == oElem) {
							oSelectedEntryElem.style.width = sWidth;
							this.drawGradient(this.sId + '-entry-selected-gradient');
	
							// adjust size of text area
							var nWidth = oSelectedEntryElem.offsetWidth? oSelectedEntryElem.offsetWidth: 20;
							var oTextArea = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
							if (oTextArea) {
								var x = this.bTimeSlotView ? 20 : 2;
								oTextArea.style.width = (nWidth - x - 4) + 'px';
							}
							// adjust size of selected area
							if(dojo.isMozilla){
								this.setStyle('-entry-selected-area', {width: (nWidth - 4) + 'px'});
							}else if(dojo.isWebKit){
								this.setStyle('-entry-selected-area', {width: (nWidth - 8) + 'px'});
							}else{
								this.setStyle('-entry-selected-area', {width: (nWidth - 2) + 'px'});
							}
						}
					}
					this.drawGradient(this.sId + '-entry' + nIndex + '-gradient');
					nIndex++;
				}
				oDate.adjustDays(0, 0, 1);
			}
		}
	
		if (this.bTimeSlotView && (nCurrentAlldayEventsInWeek != this.nAlldayEventsInWeek))
			this.adjustCalendar(true /* do not update calendar entries */);
	
		if (this.fSummarize) {
			oElem = dojo.doc.getElementById(this.sId + '-noentry');
			if (oElem) {
				this.setStyle(oElem, {
					display: this.nEntries == 0 && !this.oCalendarDataStore.isLoading()? '': 'none',
					height: this.nTextHeight + 'px'
				});
				if (this.nEntries == 0)
					oElem.innerHTML = dwa.common.utils.formatMessage(this._msgs["L_SIDECALENDAR_NOENTRY"], this.sDateRange);
			}
		}
	
		// move focus to handle keyboard event
		this.focus();
	},
	hoverEntry: function(ev, oElem, x, y){
		if (!oElem || !x || !y) return;
		if (this.oDragElem) return;		// return when processing drag & drop
	
		var nIndex = oElem.getAttribute('calendar_index');
		var oEvent = this.oCalendarDataStore.getEventById(nIndex);
		if (!oEvent)	return;
	
/*		if(!dojo.isMozilla && !dojo.isWebKit){
_ak//moved to buildAbsoluteResourceUrl
			// Fixed mixed contents warning in IE. IE may raise false alert if icon src doesn't start with https:// (SPR VSEN7SWRNR)
			if (oEvent.sIconParam && location.protocol != 'file:' && oEvent.sIconParam.indexOf(location.protocol) != 0) // kami (error check)
				oEvent.sIconParam = location.protocol + '//' + location.host + oEvent.sIconParam;
		}*/

		var oDiv = dojo.doc.getElementById(oElem.id + '-hover');
		if (ev.type == 'mouseover') {
			if (oDiv) return;
		} else {
			if (oDiv) oDiv.parentNode.removeChild(oDiv);
			return;
		}
	
		var oDateFormatter = new dwa.date.dateFormatter;
		var oTimeFormatter = new dwa.date.dateFormatter(101);
	
		var bAllday = oEvent.bAllday;
		var hasEndTime = oEvent.hasEndTime;
		var oRoot = dojo.doc.getElementById(this.sId);
	
		var sImg = "";
		if(oEvent.sIconParam){
			var asIconParam = oEvent.sIconParam.split(' ');
			sImg = this.generateImageHtml((!dojo.isMozilla && !dojo.isWebKit) ? this.buildAbsoluteResourcesUrl(asIconParam[0]) : this.buildResourcesUrl(asIconParam[0]), asIconParam[1], asIconParam[2], asIconParam[3], asIconParam[4], (oEvent.sType ? oEvent.sType : ""))
				+ '&nbsp;';
		}
		var sChair = this.escape(this.bShowAlternateName && oEvent.sAltChair? oEvent.sAltChair: oEvent.sChair);
		var sSummary = '<b unselectable="on">'
			+ (sImg? sImg: '')
			+ ((!bAllday? oTimeFormatter.format(oEvent.oStartTime) + (hasEndTime ? ' - ' + oTimeFormatter.format(oEvent.oEndTime) : '') + '</b><br>': '')
			+ this.escape(oEvent.sSubject) + (bAllday ? '</b>' : '') + (oEvent.sLocation? '<br>' + this.escape(oEvent.sLocation): '') + (oEvent.sType == 'Meeting' && sChair? '<br>' + sChair: '')).replace(/\s/g, '&nbsp;');
		var nWidth = Math.max(200, 16 + this.getTextWidth('s-cv-entry s-cv-text', true, sSummary));
		var nHeight = this.nCellHeight * 1.5;
	
		var oParent = dojo.doc.getElementById(this.sId + (this.fSummarize? '-summary': this.bTimeSlotView? (bAllday? '-allday': '-timeslot'): '-grid'));
		x += oParent.offsetLeft - oParent.scrollLeft;
		y += oParent.offsetTop  - oParent.scrollTop;
	
		var nLeft = x - nWidth / 2;
		var nTop = y + this.nScrollBarWidth;
		nLeft = Math.max(0, Math.min(nLeft, oRoot.clientWidth - nWidth - (this.bTimeSlotView? this.nScrollBarWidth: 0)));
		// Hover above mouse cursor at the bottom of time slot. (SPR YHAO7DGDGW)
		if (nTop > oRoot.clientHeight - nHeight - this.nTextHeight)
			nTop = y - this.nScrollBarWidth - nHeight;
		nTop = Math.max(0, nTop);
	
		var sHtml = '<div class="s-cv-entry"'
			// width of gradient area should be narrow in BiDi. (SPR KZHU7PFBDU)
			+ ' style="top:0px;' + D_ALIGN_DEFAULT + ':' + (this.isRTL? -1: 0) + 'px; width:100%;height:100%;">'
			+ this.insertGradientHtml(oElem.id + '-hover-gradient', 's-cv-entry', oEvent.sBGColor1, oEvent.sBGColor2, oEvent.sBorderColor)
			+ '<div class="s-cv-entry-innerframe s-cv-text" style="top:0px; ' + D_ALIGN_DEFAULT + ':0px; width:100%; height:100%; color:' + oEvent.sFontColor + ';">'
			+ sSummary
			+ '</div>'
			+ '</div>';
	
		var oDiv = dojo.doc.createElement('DIV');
		oDiv.id = oElem.id + '-hover';
		this.setStyle(oDiv, {
			position: 'absolute',
			top: nTop + 'px',
			width: nWidth + 'px',
			height: nHeight + 'px',
			display: 'none'
		});
		this.isRTL ? (oDiv.style.right = nLeft + 'px') : (oDiv.style.left = nLeft + 'px');
		oDiv.innerHTML = sHtml;
		oRoot.appendChild(oDiv);
		oDiv.style.display = '';
	
		// dg:  moved this code here since drawGradient sometimes throws an error on FF and oCurrentXX is not executed
		this.oCurrentEntryElem = oElem;
		this.oCurrentEntryEvent = oEvent;
	
		this.loadImage([oElem.id + '-hover']);
	
		this.drawGradient(oElem.id + '-hover-gradient');
	},
	selectEntry: function(ev, oElem){
		if (!oElem) return;
	
		var nIndex = oElem.getAttribute('calendar_index');
		var oEvent = this.oCalendarDataStore.getEventById(nIndex);
		if (!oEvent)	return;
	
		this.unselectEntry();
	
		var oDateFormatter = new dwa.date.dateFormatter;
		var oTimeFormatter = new dwa.date.dateFormatter(101);
	
		var bAllday = oEvent.bAllday;
		var nWidth = oElem.offsetWidth;
		var nHeight = oElem.offsetHeight;
	
		var sHtml = this.generateCalendarEntryHtml(oEvent, nIndex, true, oElem);
		var oDiv = dojo.doc.createElement('DIV');
		oDiv.id = this.sId + '-entry-selected';
		this.setStyle(oDiv, {
			position: 'absolute',
			top: '0px',
			width: nWidth + 'px',
			height: nHeight + 'px'
		});
		this.isRTL ? (oDiv.style.right = "0px") : (oDiv.style.left = "0px");
		oDiv.innerHTML = sHtml;
		oElem.appendChild(oDiv);
	
		this.loadImage([this.sId + '-entry-selected']);
	
		this.drawGradient(this.sId + '-entry-selected-gradient');
	
		this.oSelectedEvent = oEvent;
		this.oSelectedElem = dojo.doc.getElementById(this.sId + '-entry-selected');
	
		if (this.fSummarize)
			this.setStyle('-selected-area-gradient', {display: ''});
	
		// move focus
		this.focus();

		this.selectEntryAction([this.oSelectedEvent._item]);
	},
	unselectEntry: function(bDiscardInPlaceEdit){
		if (!bDiscardInPlaceEdit) {
			var oTextArea = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
			if (oTextArea) {
				// commit changes
				var oEvent = this.oSelectedEvent;
				var sNewSubject = oTextArea.value.replace(/\r\n/i, '\r');
				var sOrigSubject = oTextArea.defaultValue.replace(/\r\n/i, '\r');
				if (sOrigSubject != sNewSubject) {
					this.saveSubjectAction(oEvent, sNewSubject);
/*
					var sUrl = D_OpenShimmerDocUrlAbsolute('($Calendar)', oEvent.sUnid) + "&PresetFields="
					  + 'h_SetReadScene;s_ViewInPlaceEditHandler,h_SetEditScene;s_ViewInPlaceEditHandler,s_NoAdjWin;1'
					  + ',ThisStartDate;' + oEvent.sThisStartDate
					  + ',s_InstDate;' + oEvent.sThisStartDate
					  + ',s_NewSubject;1' + encodeURIComponent(sNewSubject)
					  + "&ui=" + "classic";
	
					var oIFrame = this.getDummyFrame();
					if (oIFrame)
						oIFrame.src = sUrl;
*/
				}
			}
		}
	
		var oSelected = dojo.doc.getElementById(this.sId + '-entry-selected');
		if (oSelected) {
			oSelected.parentNode.removeChild(oSelected);
		}
		// force to clear internal objects when unselect calendar entry (SPR JYJY7JLBXM)
		this.oSelectedEvent = null;
		this.oSelectedElem = null;
		var oInPlaceEdit = dojo.doc.getElementById(this.sId + '-entry-selected-inplaceedit');
		if (oInPlaceEdit) {
			oInPlaceEdit.parentNode.removeChild(oInPlaceEdit);
	 	}
	
		if (this.fSummarize) {
			var oElem = dojo.doc.getElementById(this.sId + '-selected-area-gradient');
			if (oElem)
				oElem.style.display = '';
		}
	},
	startInPlaceEdit: function(oEvent, oElem){
		if (!this.bWaitForEdit)
			return;

		if (!oEvent) {
			alert(this._msgs["L_NO_CAL_ENTRY_SELECTED"]);
			return;
		}

		// Do not allow inline edit if it's external entry.
		if (oEvent.fExternal)
			return;

		var oTextArea = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
		if (oTextArea)
			return;

		var oHoverElem = dojo.doc.getElementById(this.oCurrentEntryElem.id + '-hover');
		if (oHoverElem) oHoverElem.parentNode.removeChild(oHoverElem);
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-entry-selected-summary'), "s-nodisplay", true);

		var x = this.bTimeSlotView ? 20 : 2;
		var y = 0;
		var nWidth = oElem.offsetWidth - x - 4;
		var nHeight = oElem.offsetHeight - y - 4;

		sHtml = '<textarea id=' + this.sId + '-inplaceeditarea class="s-cv-textarea" style="width:100%;height:100%;overflow-y:hidden;"'
			+ this._attachEvent('blur|keydown') + '>' + oEvent.sSubject + '</textarea>';
		oDiv = dojo.doc.createElement('DIV');
		oDiv.id = this.sId + '-entry-selected-inplaceedit';
		oDiv.innerHTML = sHtml;
		with (oDiv.style) {
			position = 'absolute';
			left = x;
			top = y;
			width = nWidth;
			height = nHeight;
		}
		oElem.appendChild(oDiv);

		// move focus to handle keyboard event
		this.focus();
	},
	onActivated: function(){
		this.fActivated = true;
	
		// move focus to handle keyboard event
		this.focus();
	
		if (this.fNeedAdjust) {
			this.adjustCalendar();
			this.fNeedAdjust = false;
		}
	},
	onDeactivated: function(){
		this.fActivated = false;
	},
	syncHScrollBar: function(oElem, asIds){
		if (!oElem || !asIds)
			return;
	
		for (var i=0; i<asIds.length; i++) {
			var oElem2 = dojo.doc.getElementById(this.sId + asIds[i]);
			if (oElem2 && oElem2.scrollLeft != oElem.scrollLeft)
				oElem2.scrollLeft = oElem.scrollLeft;
		}
	},
	handleEvent: function(ev, oElem){
		// disallow user action during D&D (SPR KREY7TE4BD)
		if (this.bInDrag) {
			dojo.stopEvent(ev);
			return;
		}

		var sTarget = oElem.id;
		var bDone = (ev.type == 'keydown');

		var bInEntry = false, bInSelectedEntry = false, bInTextArea = false;
		var oTarget = ev.srcElement || ev.target;
		var x, y;
		if(!dojo.isMozilla && !dojo.isWebKit){
			x = ev.offsetX; y = ev.offsetY;
		}else{
			x = ev.layerX; y = ev.layerY;
		}
		var oMap = [ this.sId + '-allday', this.sId + '-timeslot', this.sId + '-grid', this.sId + '-header', this.sId + '-summary', this.sId + '-year' ];
		while (oTarget && oTarget.tagName != 'BODY' && dwa.common.utils.indexOf(oMap, oTarget.id) == -1) {
			if (oTarget.id) {
				if (oTarget.id.indexOf(this.sId + '-inplaceeditarea') != -1)
					bInTextArea = true;
				else if (oTarget.id.indexOf(this.sId + '-entry-selected') != -1)
					bInSelectedEntry = true;
				else if (oTarget.id.indexOf(this.sId + '-entry') != -1)
					bInEntry = true;
			}
			x += oTarget.offsetLeft - oTarget.scrollLeft;
			y += oTarget.offsetTop - oTarget.scrollTop;
			oTarget = oTarget.parentNode;
		}
		var oTextArea = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
		if (oTextArea)
			bInTextArea = true;
		// fixed problem that clicked position may slides by 16 pixels (width of scroll bar) in BiDi language. (SPR KZHU7PFBDU)
		if (this.isRTL)
			x = oTarget.clientWidth - x;

		// navigator button
		if (sTarget == this.sId + '-navigator-next' || sTarget == this.sId + '-navigator-prev' || 
		    sTarget == this.sId + '-navigator-current') {
			// nakakura
			// add ev.type == 'keydown' && ev.keyCode == 13
			if (ev.type == 'click' || ev.type == 'dblclick' || (ev.type == 'keydown' && ev.keyCode == 13) || (ev.type == 'keydown' && ev.keyCode == 32)) {
				if (sTarget == this.sId + '-navigator-current'){
					// show date picker
					this.showDatepicker(ev, oElem);
				} else {
					// goto next/previous day
					this.unselectEntry();

					var nMinus = (sTarget == this.sId + '-navigator-prev'? -1: 1);
					//	if (this.isRTL) nMinus *= -1;
					var nMulti = 1;
					var nDays  = (this.sType=='Y'||this.sType=='M'? 0: (this.sType=='F'? 7: this.nDays)) * nMulti * nMinus;
					var nMonths  = (this.sType=='M'? 1: 0) * nMulti * nMinus;
					var nYears  = (this.sType=='Y'? 1: 0) * nMulti * nMinus;
					this.oCalendar.adjustDays(nYears, nMonths, nDays);
					this.setDate(this.oCalendar);
					// nakakura
					if (this.bTimeSlotView)
						this.bDateSelected = true;
					var oCalViewStart = (this.sType == 'T'||this.sType == '2'? this.oCalViewStart.clone().adjustDays(nYears, nMonths, nDays): null);
					// fix regression FBUE7Q2HLL
					var fMoveMonth = this.sType == 'M';
					// fixed problem that right arrow button can't move to next day (SPR ESPR7F5QT5)
					if (!this.isInViewRange(this.oCalendar, fMoveMonth))
						this.gotoDay(this.oCalendar, oCalViewStart);
					else
						this.updateSelectedArea();
				}
			}
			// change background color
			// nakakura
			// add focus and blur
			else if (ev.type == 'mouseover' || ev.type == 'mouseout' || ev.type == 'focus' || ev.type == 'blur') {
				oElem.className = 's-cv-text s-cv-nav-button' + (ev.type == 'mouseover' || ev.type == 'focus' ? '-highlighted' : '');
			} else {
				bDone = false; // nakakura
			}
		}
		// calendar footer
		else if (sTarget == this.sId + '-footer') {
			if (ev.type == 'click' || ev.type == 'contextmenu') {
				// show footer options
//				com_ibm_dwa_io_widgetListener.prototype.oClasses['com_ibm_dwa_ui_actionShowFooterOptions'] = ['FullCalendarViewAction'];
//				com_ibm_dwa_ui_invokeAction(null, this.sId, 'com_ibm_dwa_ui_actionShowFooterOptions', {sMenuId:'e-dropdown-calendarview-footer', oEvent:ev, clientX:ev.clientX, clientY:ev.clientY});
				// cancel browser context menu
				bDone = true;
			} else
				// change background color
				if (ev.type == 'mouseover' || ev.type == 'mouseout' || ev.type == 'focus' || ev.type == 'blur') {
					oElem.className = 's-cv-text s-cv-nav s-cv-footer' + (ev.type == 'mouseover' || ev.type == 'focus' ? '-highlighted' : '');
				}
		}
		// IE7 hack for selected area
		else if (this.fIE7Hack && sTarget.indexOf('-selected-area') != -1 && (ev.type == 'focus' || ev.type == 'blur')) {
			this.setStyle(sTarget, {border: (ev.type == 'focus'? '1px dotted gray': 'none')});
		}
		// calendar entry
		else if (sTarget.indexOf(this.sId + '-entry') != -1) {
			// SPR #SQZO7NFAXZ: Need to allow unselectEntry() to be invoked so that in-place edit can be committed
			// // no action while editing
			// if (bInTextArea) {
			//	bDone = true; // do nothing
			// } else
			// open calendar entry
			if (ev.type == 'dblclick') {
				var oTmp = oElem;
				var sIndex = null;
				while(!(sIndex = oTmp.getAttribute('calendar_index')) && oTmp.parentNode)
					oTmp = oTmp.parentNode;
				// reset timer for in-place edit
				this.bWaitForEdit = false;
				this.unselectEntry(); // quit in-place editing
				var oEvent = this.oCalendarDataStore.getEventById(sIndex - 0);
				this.openEntryAction([oEvent._item]);
			}
			else if (ev.type == 'click') {
				if (bInTextArea) {
					bDone = true; // do nothing
				} else if (bInSelectedEntry && !this.fDisableInPlaceEdit) {
					// start in-place editing after 0.5 sec
					this.bWaitForEdit = true;
					setTimeout(dojo.hitch(this, "startInPlaceEdit", this.oSelectedEvent, this.oSelectedElem), 500);
				} else {
					// reset timer for in-place edit
					this.bWaitForEdit = false;
					if (this.fSummarize) {
						this.selectEntry(ev, oElem);
						if (this.oSelectedEvent) {
							var oDate = this.oSelectedEvent.oStartDate.clone();
							oDate.nHours = this.oCalendar.nHours;
							oDate.nMinutes = this.oCalendar.nMinutes;
							oDate.nSeconds = oDate.nMilliseconds = 0;
							this.oCalendar = oDate;
							this.setDate(this.oCalendar);
						}
					} else {
						// select calendar entry
					this.oCalendar = this.getSelectedDateTime(x, y, oElem);	// get date
						this.setDate(this.oCalendar);
						this.updateSelectedArea();									// move selected area
						this.selectEntry(ev, oElem);
						// move cursor to date header if all day event is selected
						if (this.oSelectedEvent.bAllday)
							this.bDateSelected = true;
					}
					//Update actions available for the selected entry
//					this.updateActionsForSelectedEntry(ev);
					bDone = true;
				}
			}
			// hover calendar entry
			else if (ev.type == 'mouseover' || ev.type == 'mouseout') {
				this.hoverEntry(ev, oElem, x, y);
			}
			// right mouse click
			else if (ev.type == 'contextmenu') {
				if (bInTextArea) {
					bDone = true; // do nothing
				} else {
					// reset timer for in-place edit
					this.bWaitForEdit = false;
					// select calendar entry
					this.oCalendar = this.getSelectedDateTime(x, y);	// get date
					this.setDate(this.oCalendar);
					this.updateSelectedArea();									// move selected area
					this.selectEntry(ev, oElem);

					// hide hover
					var oHoverElem = dojo.doc.getElementById(this.oCurrentEntryElem.id + '-hover');
					if (oHoverElem) oHoverElem.parentNode.removeChild(oHoverElem);

					// show context menu
					this.handleContextMenu(ev, this.oCalendar, [this.oSelectedEvent._item]);

					// cancel browser context menu
					bDone = true;
				}
			}
		}
		// year view
		else if (sTarget.indexOf(this.sId + '-year') != -1 || (this.bYearView && sTarget.indexOf(this.sId + '-selected-area') != -1)) {
			if (ev.type == 'click' || ev.type == 'contextmenu') {
				var sId = (ev.srcElement || ev.target).id;
				if (sId.indexOf(this.sId + '-monthbar') != -1) {
					var nMonth = parseInt(sId.slice((this.sId + '-monthbar').length)) - 1;
					var oDate = this.oCalendar.clone();
					oDate.nMonth = nMonth;
					var sType = 'M';
				} else if (sId.indexOf(this.sId + '-date') != -1) {
					var sDate = sId.slice((this.sId + '-date').length);
					var oDate = (new dwa.date.calendar).setISO8601String(sDate);
					oDate.setDate(oDate.getDate(), this.oCalendar.oZoneInfo);
					var sType = 'D';
				} else if (sId == (this.sId + '-selected-area')) {
					var oDate = (new dwa.date.calendar).setDate(this.oCalendar.getDate());
					var sType = 'D';
				}
				if (oDate) {
					this.oCalendar = oDate.clone();
					this.setDate(this.oCalendar);
				}
	
				// context menu
				if (ev.type == 'contextmenu') {
					this.handleContextMenu(ev, this.oCalendar);
					// cancel browser context menu
					bDone = true;
				} else if (sType)
					this.changeViewAction(sType);
				else
					this.focus();
			}
			// keyboard navigation
			else if (ev.type == 'keydown') {
				switch (ev.keyCode) {
					case 40:
					case 38:
						var nDays  = 7 * (ev.keyCode == 38? -1: 1);
						this.bDateSelected = true;
						this.oCalendar.adjustDays(0,0,nDays);
						break;
					case 37:
					case 39:
						var nDays  = (ev.keyCode == 37? -1: 1) * (this.isRTL? -1: 1);
						this.bDateSelected = true;
						this.oCalendar.adjustDays(0,0,nDays);
						break;
					case 33:
					case 34:
						var nYears = (ev.keyCode == 33? -1: 1);
						this.bDateSelected = true;
						this.oCalendar.adjustDays(nYears,0,0);
						break;
					case 13:
						this.changeViewAction('D');
					default:
						bDone = false;
				}
	
				switch (ev.keyCode) {
					case 40:
					case 38:
					case 37:
					case 39:
					case 33:
					case 34:
						this.setDate(this.oCalendar);
						if (!this.isInViewRange(this.oCalendar))
							this.gotoDay(this.oCalendar, oCalViewStart);
						else
							this.updateSelectedArea();
						break;
				}
			}
		}
		// timeslot
		// nakakura
		// add -allday
		else if (sTarget.indexOf(this.sId + '-summary') != -1 || sTarget.indexOf(this.sId + '-timeslot') != -1 || sTarget.indexOf(this.sId + '-grid') != -1 || sTarget.indexOf(this.sId + '-selected-area') != -1 || sTarget.indexOf(this.sId + '-allday') != -1) {
			// keyboard navigation
			if (ev.type == 'keydown') {
				switch (ev.keyCode) {
					case 40: // DOWNARROW
					case 38: // UPARROW
						if (!this.fSummarize) {
							// nakakura
							var nSelectedEventIndexInView = (this.oSelectedEvent) ? this.oSelectedEvent.nIndexInView : -1;
							this.unselectEntry();
						}
						break;
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
					case 33: // PAGEUP
					case 34: // PAGEDOWN
						this.unselectEntry();
						break;
				}

				switch (ev.keyCode) {
					case 120: // F9
						// fixed problem that F9 doesn't clear cache. (SPR YHAO7CQ5EN)
						this.refreshContent();
						break;
					case 40: // DOWNARROW
					case 38: // UPARROW
						if (this.fSummarize) {
							if (this.oSelectedEvent && this.oSelectedElem) {
								var nIndex = this.oSelectedEvent.nIndexInView + (ev.keyCode == 40? 1: -1);
								var oElem = dojo.doc.getElementById(this.sId + '-entry' + nIndex);
								if (oElem) {
									var sDate = oElem.getAttribute('calendar_date');
									if (sDate == this.oSelectedEvent.oStartDate.getISO8601String()) {
										this.selectEntry(null, oElem);
									} else {
										if (ev.keyCode == 40) {
											var oDate = (new dwa.date.calendar).setISO8601String(sDate);
											oDate.setDate(oDate.getDate(), this.oCalendar.oZoneInfo);
											oDate.fDateOnly = true;
											this.oCalendar = oDate.clone();
										}
										this.unselectEntry();
									}
								} else if (ev.keyCode == 38) {
									this.unselectEntry();
								}
							} else {
								var oDate = this.oCalendar.clone();
								oDate.fDateOnly = true;
								var sDate = oDate.getISO8601String();
								var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
								if (aoEvents && aoEvents.length) {
									if (this.bCollapse[sDate] && ev.keyCode == 40)
										var nIndex = aoEvents[aoEvents.length - 1].nIndexInView + 1;
									else
										var nIndex = aoEvents[0].nIndexInView + (ev.keyCode == 40? 0: -1);
									var oElem = dojo.doc.getElementById(this.sId + '-entry' + nIndex);
									if (oElem) {
										var sPrevDate = sDate;
										var sDate = oElem.getAttribute('calendar_date');
										var oDate = (new dwa.date.calendar).setISO8601String(sDate);
										oDate.setDate(oDate.getDate(), this.oCalendar.oZoneInfo);
										oDate.fDateOnly = true;
										this.oCalendar = oDate.clone();
										if (!this.bCollapse[sDate] && (!this.bCollapse[sPrevDate] || ev.keyCode != 40))
											this.selectEntry(null, oElem);
									}
								} else {
									var oDate = this.oCalViewStart.clone();
									oDate.fDateOnly = true;
									for (var i=0; i<this.nDays; i++) {
										var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
										if (aoEvents && aoEvents.length) {
											this.oCalendar = oDate.clone();
											break;
										}
										oDate.adjustDays(0, 0, 1);
									}
								}
							}
						} else {
							// nakakura
							var oDate = this.oCalendar.clone();
							oDate.fDateOnly = true;
							var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
							var nCurrentEntryIndexInDay = (0 <= nSelectedEventIndexInView && 0 < aoEvents.length) ? nSelectedEventIndexInView - aoEvents[0].nIndexInView : -1;
							if (this.bTimeSlotView) {
								if (ev.keyCode == 38 && ((this.oCalendar.nHours == 0 && this.oCalendar.nMinutes == 0 && (aoEvents.length == 0 || !aoEvents[0].bAllday) || nCurrentEntryIndexInDay == 0 && aoEvents[0].bAllday)
									|| nCurrentEntryIndexInDay == 0 && !aoEvents[0].bAllday && aoEvents[0].oStartTime.nHours * 60 + aoEvents[0].oStartTime.nMinutes < this.nTimeSlotDur)) {
									this.bDateSelected = true;
								} else {
									var nMinus = ev.keyCode == 38 ? -1 : 1;
									var nNextMinutes;
									var nNextEntryIndexInDay;
									// check next focus is either on the event or not
									// current focus and next focus are on the allday event
									if (aoEvents[nCurrentEntryIndexInDay] && aoEvents[nCurrentEntryIndexInDay].bAllday && aoEvents[nCurrentEntryIndexInDay + nMinus] && aoEvents[nCurrentEntryIndexInDay + nMinus].bAllday) {
										nNextEntryIndexInDay = nCurrentEntryIndexInDay + nMinus;
									} else {
										// focus is not on the entry now
										if (nCurrentEntryIndexInDay < 0)
											nNextMinutes = this.oCalendar.nHours * 60 + this.oCalendar.nMinutes + nMinus * this.nTimeSlotDur;
										// focus is on the entry now
										else if (aoEvents[nCurrentEntryIndexInDay].bAllday)
											nNextMinutes = this.getScrollTopMinutes();
										else if (ev.keyCode == 38)
											nNextMinutes = aoEvents[nCurrentEntryIndexInDay].oStartTime.nHours * 60 + (Math.floor(aoEvents[nCurrentEntryIndexInDay].oStartTime.nMinutes / this.nTimeSlotDur) - 1) * this.nTimeSlotDur;
										else
											nNextMinutes = aoEvents[nCurrentEntryIndexInDay].oEndTime.nHours * 60 + (Math.floor(aoEvents[nCurrentEntryIndexInDay].oEndTime.nMinutes / this.nTimeSlotDur) + ((aoEvents[nCurrentEntryIndexInDay].oEndTime.nMinutes % this.nTimeSlotDur == 0) ? 0 : 1)) * this.nTimeSlotDur;
	
										nNextEntryIndexInDay = this.getNextEntryIndex(aoEvents, nCurrentEntryIndexInDay, nNextMinutes, nMinus);
									}
									// set foucs to the entry
									if (0 <= nNextEntryIndexInDay) {
										var oElem = dojo.doc.getElementById(this.sId + '-entry' + aoEvents[nNextEntryIndexInDay].nIndexInView);
										this.selectEntry(ev, oElem);
									// set focus to the timeslot
									} else {
										if (nNextMinutes <= 24 * 60 - this.nTimeSlotDur) {
											var oDate = this.oCalendar.getDate();
											oDate.setHours(nNextMinutes / 60);
											oDate.setMinutes(nNextMinutes % 60);
											this.oCalendar.setDate(oDate);
										}
									}
								}
							// nakakura
							// Two Weeks and One Month
							} else {
								// set focus to the entry
								if ((ev.keyCode == 38 && 1 <= nCurrentEntryIndexInDay || ev.keyCode == 40 && nCurrentEntryIndexInDay < aoEvents.length - 1)) {
									var nNextEntryIndexInDay = nCurrentEntryIndexInDay + (ev.keyCode == 38 ? -1 : 1);
									var oElem = dojo.doc.getElementById(this.sId + '-entry' + aoEvents[nNextEntryIndexInDay].nIndexInView);
									this.selectEntry(ev, oElem);
								// set focus to date
								} else {
									this.bDateSelected = true;
									var nDays = 7 * (ev.keyCode == 38 ? -1 : 1) * (nCurrentEntryIndexInDay == 0 && ev.keyCode == 38 ? 0 : 1);
									var oDate = this.oCalendar.getDate();
									oDate.setTime(oDate.getTime() + nDays * 86400000);
									this.oCalendar.setDate(oDate);
								}
							}
						}
						break;
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
						// nakakura
						this.bDateSelected = true;
					case 187: // PLUS
					case 189: // MINUS
					case 107: // PLUS_TENKEY
					case 109: // MINUS_TENKEY
					case 61: // EQUAL		// keyCode 61 returns when you press Plus key in Firefox on Mac
						if (this.fSummarize) {
							if (this.sType == 'D')
								break;
							var bCollapse = ev.keyCode == 189 || ev.keyCode == 109 || ev.keyCode == (this.isRTL? 39: 37);
							if (bCollapse)
								this.unselectEntry();
							if (ev.shiftKey) {
								if (bCollapse) {
									var oDate = this.oCalViewStart.clone();
									oDate.fDateOnly = true;
									for (; oDate.getDate().getTime() < this.oCalViewEnd.getDate().getTime(); oDate.adjustDays(0,0,1))
										this.bCollapse[oDate.getISO8601String()] = true;
								} else
									this.bCollapse = {};
							} else {
								var oDate = this.oCalendar.clone();
								oDate.fDateOnly = true;
								this.bCollapse[oDate.getISO8601String()] = bCollapse;
							}
							this.updateCalendarEntries();
						} else {
							var nMinus = (ev.keyCode == 37? -1: 1) * (this.isRTL? -1: 1);
							var nDays  = nMinus; // 1 * nMinus
							this.oCalendar.adjustDays(0,0,nDays);
							if (this.sType == 'F') {
							while (this.isWeekEnd(this.oCalendar))
									this.oCalendar.adjustDays(0,0,nDays);
							}
							var oCalViewStart = (this.sType == 'T'||this.sType == '2'? this.oCalViewStart.clone().adjustDays(0, 0, this.nDays*nMinus): null);
						}
						break;
					case 33: // PAGEUP
					case 34: // PAGEDOWN
						var nMinus = (ev.keyCode == 33? -1: 1);
						var nDays  = (this.sType=='M'||this.sType=='Y'? 0: this.sType=='F'? 7: this.nDays) * nMinus;
						var nMonths  = (this.sType=='M'? 1: 0) * nMinus;
						var nYears  = (this.sType=='Y'? 1: 0) * nMinus;
						this.oCalendar.adjustDays(nYears, nMonths, nDays);
						var oCalViewStart = (this.sType == 'T'||this.sType == '2'? this.oCalViewStart.clone().adjustDays(nYears, nMonths, nDays): null);
						// nakakura
						if (this.bTimeSlotView)
							this.bDateSelected = true;
						break;
					case 13: // ENTER
						if (this.bYearView)
							this.changeViewAction('D');
						else {
							if (this.oSelectedEvent) {
								// hit enter to open selected entry
	//							com_ibm_dwa_io_widgetListener.prototype.oClasses['com_ibm_dwa_ui_actionOpenCalendarDocument'] = ['fullcalendarviewaction'];
	//							com_ibm_dwa_ui_invokeAction(null, this.sId, 'com_ibm_dwa_ui_actionOpenCalendarDocument', {asUnids: [this.oSelectedEvent.sUnid]});
								this.openEntryAction([this.oSelectedEvent._item]);
							} else if (this.bCanCreate)
								this.newEntryAction(this.oCalendar);
						}
						break;
					case 46: // DELETE
						this.deleteEntryAction([this.oSelectedEvent._item]);
						break;
					case 113: // F2
						// start in-place editing after 0.5 sec
						if (!this.fDisableInPlaceEdit) {
							this.bWaitForEdit = true;
							setTimeout(dojo.hitch(this, "startInPlaceEdit", this.oSelectedEvent, this.oSelectedElem), 500);
						}
						break;
					default:
						bDone = false;
				}

				switch (ev.keyCode) {
					case 40: // DOWNARROW
					case 38: // UPARROW
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
					case 33: // PAGEUP
					case 34: // PAGEDOWN
					case 187: // PLUS
					case 189: // MINUS
					case 107: // PLUS_TENKEY
					case 109: // MINUS_TENKEY
					case 61: // EQUAL
						this.setDate(this.oCalendar);
						// fix regression FBUE7Q2HLL
						var fMoveMonth = this.sType == 'M' && (ev.keyCode == 33 || ev.keyCode == 34);
						if (!this.isInViewRange(this.oCalendar, fMoveMonth))
							this.gotoDay(this.oCalendar, oCalViewStart);
						else
							this.updateSelectedArea();
						break;
				}
			}

			// click on timeslot/right mouse click
			else if (ev.type == 'click' || ev.type == 'dblclick' || ev.type == 'contextmenu') {
				if (!bInSelectedEntry && !bInEntry) {
					this.unselectEntry();
					var oCalendar = this.getSelectedDateTime(x, y);
					if (oCalendar){
						this.oCalendar = oCalendar;
						this.setDate(this.oCalendar);
						this.bDateSelected = false;
						this.updateSelectedArea();									// move selected area
					}
					if (ev.type == 'dblclick') {
						if (this.bCanCreate)
							this.newEntryAction(this.oCalendar);
					} else if (ev.type == 'contextmenu') {
						this.handleContextMenu(ev, this.oCalendar);
						// cancel browser context menu
						bDone = true;
					}
				}
			}
			// scroll on timeslot
			else if (ev.type == 'scroll') {
				this.syncHScrollBar(oElem, ['-header', '-allday', '-navigator']);
				
				// Label of AM/PM follows vertical scroll bar in timeslot (SPR VSEN7X95AQ)
				if (!this.f24Hour && !this.fUseAddTimeZone && this.bTimeSlotView) {
					var i = Math.ceil(oElem.scrollTop / this.nCellHeight);
					if (i != this.nTimeSlotTopHour) {
						var oTimeFormatter = new dwa.date.dateFormatter(D_TimeFmt_NoAMPM);
						var oDate = this.oCalendar.clone();
						oDate.nMinutes = oDate.nSeconds = oDate.nMilliseconds = 0;
						// Remove AM/PM from the previous row
						if (this.nTimeSlotTopHour != 0 && this.nTimeSlotTopHour != 12) {
							oDate.nHours = this.nTimeSlotTopHour;
							var oElem = dojo.doc.getElementById(this.sId + '-timeslot-time' + this.nTimeSlotTopHour);
							if (oElem)
								oElem.innerHTML = oTimeFormatter.format(oDate);
						}
						
						// Add AM/PM in the top vidible row
						if (i != 0 && i != 12) {
							oDate.nHours = i;
							var oElem = dojo.doc.getElementById(this.sId + '-timeslot-time' + i);
							if (oElem)
								oElem.innerHTML = oTimeFormatter.format(oDate) + '<br>' + (i<12? this._msgs["L_AM_SUFFIX"]: '') + (i>=12? this._msgs["L_PM_SUFFIX"]: '');
						}
						this.nTimeSlotTopHour = i;
					}
				}
			}
		}
		// in-place edit field
		else if (sTarget.indexOf(this.sId + '-inplaceeditarea') != -1) {
			// SPR #SQZO7NFAXZ: Unnecessary to handle blur events since committing in-place edit can be done elsewhere
			if (ev.type == 'keydown' && ev.keyCode == 13 && !ev.shiftKey) {
				this.unselectEntry();
			} else if (ev.type == 'keydown') {
				if (ev.keyCode == 27)
				  this.unselectEntry(true);
				else {
					// suppressing DWA's keyboard shortcuts (delete, arrow keys etc.) while editing
					bDone = false;
					dojo.stopEvent(ev);
				}
			}
		}
		// handle click on date header (SPR BCOE7F3SJY)
		else if (sTarget.indexOf(this.sId + '-header') != -1) {
			if (ev.type == 'click') {
				this.unselectEntry();
				var oDate = this.getSelectedDateTime(x, y);
				oDate.nHours = this.oCalendar.nHours;
				oDate.nMinutes = this.oCalendar.nMinutes;
				oDate.nSeconds = this.oCalendar.nSeconds;
				oDate.nMilliseconds = this.oCalendar.nMilliseconds;
				this.oCalendar = oDate.clone();
				this.setDate(this.oCalendar);
				this.bDateSelected = true;
				this.updateSelectedArea(); // move selected area
			} else if (ev.type == 'keydown') {
				switch (ev.keyCode) {
					case 40: // DOWNARROW
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
					case 33: // PAGEUP
					case 34: // PAGEDOWN
						this.unselectEntry();
						break;
				}

				switch (ev.keyCode) {
					case 120: // F9
						// fixed problem that F9 doesn't clear cache. (SPR YHAO7CQ5EN)
						this.refreshContent();
						break;
					case 40: // DOWNARROW
						this.bDateSelected = false;
					//	this.oCalendar.nHours = (new dwa.date.calendar).setISO8601String(this.sTimeSlotStart).nHours;
					//	this.oCalendar.nMinutes   = this.oCalendar.nSeconds   = this.oCalendar.nMilliseconds   = 0;
						// nakakura
						var oDate = this.oCalendar.clone();
						oDate.fDateOnly = true;
						var aoEvents = this.oCalendarDataStore.getEventsByDate(oDate);
						if (0 < aoEvents.length && aoEvents[0].bAllday) {
							var oElem = dojo.doc.getElementById(this.sId + '-entry' + aoEvents[0].nIndexInView);
							this.selectEntry(ev, oElem);
						} else {
							var nNextMinutes = this.getScrollTopMinutes();
							var nNextEntryIndexInDay = this.getNextEntryIndex(aoEvents, -1, nNextMinutes, 1);
							// set focus to the entry
							if (0 <= nNextEntryIndexInDay) {
								var oElem = dojo.doc.getElementById(this.sId + '-entry' + aoEvents[nNextEntryIndexInDay].nIndexInView);
								this.selectEntry(ev, oElem);
							// set focus to the timeslot
							} else {
								this.oCalendar.nHours = Math.floor(nNextMinutes / 60);
								this.oCalendar.nMinutes = nNextMinutes % 60;
							}
						}
						break;
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
						var nMinus = (ev.keyCode == 37? -1: 1) * (this.isRTL? -1: 1);
						var nDays  = nMinus; // 1 * nMinus
						this.oCalendar.adjustDays(0,0,nDays);
						if (this.sType == 'F') {
							while (this.isWeekEnd(this.oCalendar))
								this.oCalendar.adjustDays(0,0,nDays);
						}
						var oCalViewStart = (this.sType == 'T'||this.sType == '2'? this.oCalViewStart.clone().adjustDays(0, 0, this.nDays*nMinus): null);
						break;
					case 33: // PAGEUP
					case 34: // PAGEDOWN
						var nMinus = (ev.keyCode == 33? -1: 1);
						var nDays  = (this.sType=='M'? 0: (this.sType=='F'? 7: this.nDays)) * nMinus;
						var nMonths  = (this.sType=='M'? 1: 0) * nMinus;
						var nYears  = (this.sType=='Y'? 1: 0) * nMinus;
						this.oCalendar.adjustDays(nYears, nMonths, nDays);
						var oCalViewStart = (this.sType == 'T'||this.sType == '2'? this.oCalViewStart.clone().adjustDays(nYears, nMonths, nDays): null);
						break;
					case 13: // ENTER
						this.newEntryAction(this.oCalendar);
						break;
					default:
						bDone = false;
				}

				switch (ev.keyCode) {
					case 40: // DOWNARROW
					case 37: // LEFTARROW
					case 39: // RIGHTARROW
					case 33: // PAGEUP
					case 34: // PAGEDOWN
						this.setDate(this.oCalendar);
						if (!this.isInViewRange(this.oCalendar))
							this.gotoDay(this.oCalendar, oCalViewStart);
						else
							this.updateSelectedArea();
						break;
				}
			}
			// scroll on header
			else if (ev.type == 'scroll') {
				this.syncHScrollBar(oElem, ['-allday', '-timeslot']);
			}
		}
		// mouse events in summary view
		else if (sTarget.indexOf(this.sId + '-summary') != -1) {
			if (ev.type == 'click') {
				this.unselectEntry();
			// scroll on timeslot
			} else if (ev.type == 'scroll') {
				this.syncHScrollBar(oElem, ['-navigator']);
			}
		}
		// expand/collapse category in summary view
		else if (this.fSummarize && sTarget.indexOf(this.sId + '-date') != -1) {
			var oTextArea = dojo.doc.getElementById(this.sId + '-inplaceeditarea');
			this.unselectEntry();

			// SPR #SQZO7NFAXZ: No expand/collapse when in-place edit has been committed, as Notes client does
			if (!oTextArea) {
				var sDate = oElem.getAttribute('calendar_date');
				this.bCollapse[sDate] = this.bCollapse[sDate]? false: true;
				this.updateCalendarEntries();

				var oDate = (new dwa.date.calendar).setISO8601String(sDate);
				oDate.nHours = this.oCalendar.nHours;
				oDate.nMinutes = this.oCalendar.nMinutes;
				oDate.nSeconds = this.oCalendar.nSeconds;
				oDate.nMilliseconds = this.oCalendar.nMilliseconds;
				this.oCalendar = oDate.clone();
				this.setDate(this.oCalendar);
				this.bDateSelected = true;
				this.updateSelectedArea();
			}
		}
		if (bDone) {
			dojo.stopEvent(ev);
		}
	},
	// nakakura
	getNextEntryIndex: function(aoTodayEvents, nCurrentEntryIndexInDay, nNextMinutes, nMinus) {
		// current focus is on the entry
		if (0 <= nCurrentEntryIndexInDay) {
			if (aoTodayEvents[nCurrentEntryIndexInDay + nMinus] && !aoTodayEvents[nCurrentEntryIndexInDay + nMinus].bAllday) {
				var nBaseMinutes = (nMinus == -1) ? aoTodayEvents[nCurrentEntryIndexInDay - 1].oEndTime.nHours * 60 + aoTodayEvents[nCurrentEntryIndexInDay - 1].oEndTime.nMinutes
					: aoTodayEvents[nCurrentEntryIndexInDay + 1].oStartTime.nHours * 60 + aoTodayEvents[nCurrentEntryIndexInDay + 1].oStartTime.nMinutes;
				if (nMinus == -1 && nNextMinutes < nBaseMinutes ||
					nMinus == 1 && nBaseMinutes < nNextMinutes + this.nTimeSlotDur && (aoTodayEvents[nCurrentEntryIndexInDay].bAllday && nNextMinutes <= nBaseMinutes ||
					!aoTodayEvents[nCurrentEntryIndexInDay].bAllday))
					return nCurrentEntryIndexInDay + nMinus;
			// last entry in the day
			} else if (nCurrentEntryIndexInDay == aoTodayEvents.length - 1 && 24 * 60 - this.nTimeSlotDur < nNextMinutes && nMinus == 1) {
				return nCurrentEntryIndexInDay;
			// go to allday event
			} else if (aoTodayEvents[nCurrentEntryIndexInDay + nMinus] && aoTodayEvents[nCurrentEntryIndexInDay + nMinus].bAllday
					&& !aoTodayEvents[nCurrentEntryIndexInDay].bAllday && aoTodayEvents[nCurrentEntryIndexInDay].oStartTime.nHours * 60 + aoTodayEvents[nCurrentEntryIndexInDay].oStartTime.nMinutes < this.nTimeSlotDur) {
				return  nCurrentEntryIndexInDay + nMinus;
			}
		// current focus is not on the entry
		} else {
			for (var i = 0; i < aoTodayEvents.length; i++) {
				var nBaseMinutes = (nMinus == -1) ? aoTodayEvents[aoTodayEvents.length - i - 1].oEndTime.nHours * 60 + aoTodayEvents[aoTodayEvents.length - i - 1].oEndTime.nMinutes
					: aoTodayEvents[i].oStartTime.nHours * 60 + aoTodayEvents[i].oStartTime.nMinutes;
				if (nMinus == -1 && nBaseMinutes <= nNextMinutes + this.nTimeSlotDur && nNextMinutes < nBaseMinutes
					|| nMinus == 1 && nBaseMinutes < nNextMinutes + this.nTimeSlotDur && nNextMinutes <= nBaseMinutes) {
					if (nMinus == -1 && aoTodayEvents[aoTodayEvents.length - i - 1].bConflicted) {
						for (var j in aoTodayEvents[aoTodayEvents.length - i - 1].anConflicts)
							if (aoTodayEvents[j].nIndexInConflicts == aoTodayEvents[aoTodayEvents.length - i - 1].nConflicts - 1)
								return j;
					} else {
						return (nMinus == -1) ? aoTodayEvents.length - i - 1 : i;
					}
				}
			}
		}
		return -1;
	},
	_attachEvent: function(sEvents){
		var asEvents = sEvents.split('|');
		var sEventHandler = '';
		for (var i=0; i<asEvents.length; i++)
			sEventHandler += ' on' + asEvents[i] + '="dwa.cv.calendarView.prototype.cvHandler(event,this,\''+this.id+'\');"';
		return sEventHandler;
	},
	observe: function(oProperty){
		if (!this.sType)
			return;
		if (!oProperty.isLatest())
			return;
	
		if (oProperty.sName == 'p-contentarea-width' || oProperty.sName == 'p-body-height') {
			// adjust calendar size immediately if calendar widget is activated
			if (this.fActivated)
				this.adjustCalendar();
			else
				this.fNeedAdjust = true;
		}else if (oProperty.sName == 'p-e-calendarview-currentselected'){ // kami (moved from calendarController)
			if (oProperty.vValue != oProperty.vPrevValue) {
				var oCalendar = (new dwa.date.calendar).setISO8601String(oProperty.vValue);
				oCalendar.setDate(oCalendar.getDate(), this.oCalendar.oZoneInfo);
				oCalendar.fDateOnly = true;
				if (!oCalendar.equals(this.oCalendar)){
					this.oCalendar.nYear = oCalendar.nYear;
					this.oCalendar.nMonth = oCalendar.nMonth;
					this.oCalendar.nDate = oCalendar.nDate;
					this.oCalendar.setDate(this.oCalendar.getDate(), this.oCalendar.oZoneInfo);
					this.gotoDay(this.oCalendar);
				}
			}
		}
		else if (oProperty.sName == 'p-e-calendarview-show-summarize') {
			this.drawCalendar(this.sType, oProperty.vValue);
			this.onActivated();
		}
		else if (oProperty.sName == 'p-e-calendarview-show-footer') {
			this.fHideFooter = !oProperty.vValue;
			this.adjustCalendar();
		}
		else if (oProperty.sName == 'p-e-calendarview-footer-type') {
			this.updateFooter(oProperty.vValue);
		}
		else if (oProperty.sName == 'p-e-calendarview-show-filter-type' || oProperty.sName == 'p-e-calendarview-show-filter-text') {
			var oMap = {
				A:false, // Display All - filterText is NOT needed.
				C:true,  // Filter by Chair - filterText is needed.
				T:true,  // Filter by Type - filterText is needed.
				S:true,  // Filter by Status - filterText is needed.
				P:false  // Filter by Private - filterText is NOT needed.
			};
			if (oProperty.sName == 'p-e-calendarview-show-filter-type')
				this.sFilterType = oProperty.vValue in oMap? oProperty.vValue: '';
			else // if (oProperty.sName == 'p-e-calendarview-show-filter-text')
				this.sFilterText = oProperty.vValue;
			if (this.sFilterType && (!oMap[this.sFilterType] || this.sFilterText)) {
				this.filterCalendarEntries(this.sFilterType, this.sFilterText);
				this.sFilterType = this.sFilterText = '';
			}
		}
	},
	filterCalendarEntries: function dwa_widget_calendarView_filterCalendarEntries(sFilterType, sFilterText){
		if (sFilterType == 'A') {
			this.oCalendarDataStore.unregisterFilter(dwa.cv.calendarView.prototype.fnFilter);
			this.resetStatus();
			return;
		}
		var sFilterType2 = {C:'sChair|sAltChair', T:'sType', S:'sStatus', P:'sPrivate'}[sFilterType];
		switch (sFilterType) {
		  case 'C':
			var sFilterText2 = sFilterText;
			var sStatus = dwa.common.utils.formatMessage("Chair: %1", sFilterText);
			break;
		  case 'T':
			var sFilterText2 = {A:"0", M:"3", D:"2", N:"1", R:"4", T:"To Do"}[sFilterText];
			var sStatus = {A:"Appointments only", M:"Meetings only", D:"All Day Events only", N:"Anniversaries only", R:"Reminders only", T:"To Dos only"}[sFilterText];
			break;
		  case 'S':
			var sFilterText2 = {A:"Accepted", T:"Tentatively Accepted", D:"Draft"}[sFilterText];
			var sStatus = {A:"Accepted only", T:"Tentatively Accepted only", D:"Drafts only"}[sFilterText];
			break;
		  case 'P':
			var sFilterText2 = "1";
			var sStatus = "Private notices only";
			break;
		}
		if (sFilterType == 'P' || (sFilterType2 && sFilterText2)) {
			this.oCalendarDataStore.registerFilter(dwa.cv.calendarView.prototype.fnFilter, {sFilterType:sFilterType2, sFilterText:sFilterText2, bExactMatch:(sFilterType != 'C')});
			this.setStatus(sStatus);
			if (this.sType)
				this.gotoDay();
		}
	},
	fnFilter: function(oEvent, oContext) {
		var fDisplay = false;
		var asFilterType = oContext.sFilterType.split('|');
		for (var i=0; i<asFilterType.length; i++) {
			if (!oEvent[asFilterType[i]]) {
				// do nothing
			} else if (oContext.bExactMatch) {
				if (oEvent[asFilterType[i]].toUpperCase() == oContext.sFilterText.toUpperCase())
					fDisplay = true;
			} else {
				if (oEvent[asFilterType[i]].toUpperCase().indexOf(oContext.sFilterText.toUpperCase()) != -1)
					fDisplay = true;
			}
		}
		return fDisplay;
	},
	handleDrag: function(ev){
		var oCurrentPos = new dwa.common.utils.pos(ev.clientX, ev.clientY);
		var oStartPos = this.oStartPos;
		if (this.oCurrentEntryElem) {
			// Make sure that unauthorized user can't start drag&drop (SPR SDOY6Z8D73)
			if (this.oCurrentEntryEvent.fExternal || (this.oCurrentEntryEvent.bAllday && this.sType == 'D'))
				return;
			
			switch (ev.type) {
			case 'mousedown':
//FF in SUSE handles this function for body before handleEvent for the event cell.
// handler code is cancelled since bInDrag is turned on for such case.
//thus, need to verify which button is pushed here.
// ev.button seems always take 2 as the value for right button among the browsers as long as I see.(FF3.*, IE7, Safari) _ak
				if(ev.button && ev.button == 2)
					break;
				oStartPos = this.oStartPos = new dwa.common.utils.pos(ev.clientX, ev.clientY);
				// prevent event out of calendar entry
				var bInEntry = false;
				var oTarget = ev.srcElement || ev.target;
				this.nDragOffsetX = this.nDragOffsetY = 0;
				while (oTarget && oTarget.tagName != 'BODY') {
					if (oTarget.id && oTarget.id.indexOf(this.sId + '-entry') != -1)
						bInEntry = true;
					oTarget = (oTarget.offsetParent&&oTarget.offsetParent.tagName!='HTML')? oTarget.offsetParent: oTarget.parentNode;
				}
				if (!bInEntry)
					break;
	
				// position of root element
				var sId = this.sId + (this.fSummarize? '-summary': !this.bTimeSlotView? '-grid': this.oCurrentEntryEvent.bAllday? '-allday': '-timeslot');
				var oTarget = this.oRootElem = dojo.doc.getElementById(sId);
				this.oRootElemPos = {x:0, y:0};
				while (oTarget && oTarget.tagName != 'BODY') {
					this.oRootElemPos.x += oTarget.offsetLeft - oTarget.scrollLeft;
					this.oRootElemPos.y += oTarget.offsetTop - oTarget.scrollTop;
					oTarget = oTarget.offsetParent? oTarget.offsetParent: oTarget.parentNode;
				}
				if (this.isRTL)
					this.oRootElemPos.x = oTarget.offsetWidth - this.oRootElemPos.x - this.oCurrentEntryElem.offsetWidth;
	
				// start position of dragging element
				var oTarget = ev.target;
				this.oDragElemPos = {x:0, y:0};
				while (oTarget && oTarget.tagName != 'BODY') {
					this.oDragElemPos.x += oTarget.offsetLeft - oTarget.scrollLeft;
					this.oDragElemPos.y += oTarget.offsetTop - oTarget.scrollTop;
					oTarget = oTarget.offsetParent ? oTarget.offsetParent: oTarget.parentNode;
				}
	
				this.bInDrag = true;
	
				if (this.oCurrentEntryEvent != this.oSelectedEvent)
					this.unselectEntry();
				this.connect(dojo.doc, "onmousemove", "handleDrag"); // kami
				this.connect(dojo.doc, "onmouseup", "handleDrag"); // kami
				dojo.setSelectable(dojo.doc.body, false);
				break;
			case 'mousemove':
				if (!this.bInDrag) break;
				if(Math.abs(oCurrentPos.x - oStartPos.x) < 8 && Math.abs(oCurrentPos.y - oStartPos.y) < 8){ break; } // kami (delayed drag)
				if (this.nCalViewDragDrop == 2) {
					var x = oCurrentPos.x - this.oRootElemPos.x;
					var y = oCurrentPos.y - this.oRootElemPos.y - Math.floor(this.oCurrentEntryElem.offsetHeight / 2);
					// do not continue if unexpected error occurs (SPR KREY7TE4BD)
					if (isNaN(x) || isNaN(y))
						break;
					var oObj = this.getSelectedDateTimeAndPosition(x, y, this.oCurrentEntryElem, true);
					if (!oObj)
						break;
					x = oObj.nOffsetX;
					y = oObj.nOffsetY;
				} else {
					x = oCurrentPos.x - Math.floor(this.oCurrentEntryElem.offsetWidth / 2);
					y = oCurrentPos.y - Math.floor(this.oCurrentEntryElem.offsetHeight / 2);
				}
				if (!this.oDragElem) {
					var oHoverElem = dojo.doc.getElementById(this.oCurrentEntryElem.id + '-hover');
					if (oHoverElem) oHoverElem.parentNode.removeChild(oHoverElem);
					var oElem = this.oCurrentEntryElem;
					var oEvent = this.oCurrentEntryEvent;
					var oTimeFormatter = new dwa.date.dateFormatter(101);
					this.oDragElem = dojo.doc.getElementById(this.sId + '-entry-drag');
					if (!this.oDragElem) {
						if (this.nCalViewDragDrop == 2) {
							var sRootId = this.fSummarize? '-summary': !this.bTimeSlotView? '-grid': this.oCurrentEntryEvent.bAllday? '-allday': '-timeslot';
							var oRoot = dojo.doc.getElementById(this.sId + sRootId);
						} else {
							var oRoot = dojo.doc.body;
						}
						this.oDragElem = oRoot.appendChild(dojo.doc.createElement('DIV'));
						this.oDragElem.id = this.sId + '-entry-drag';
						this.oDragElem.style.position = 'absolute';
					}
					this.oDragElem.innerHTML = this.generateCalendarEntryHtml(oEvent, -1, true, oElem, true);
					this.setStyle(this.oDragElem, {
						left:   x + 'px',
						top:    y + 'px',
						width:  this.oCurrentEntryElem.offsetWidth + 'px',
						height: this.oCurrentEntryElem.offsetHeight + 'px'
					});
					this.loadImage([this.sId + '-entry-drag']);
					this.drawGradient(this.sId + '-entry-drag-gradient');
					this.oCurrentEntryElem.style.visibility = 'hidden';
				}
				else {
					this.setStyle(this.oDragElem, {left: x + 'px', top:  y + 'px'});
				}
				break;
			case 'mouseup':
				if (!this.bInDrag) break;
				this.bInDrag = false;
	
				this.disconnect(dojo.doc, "onmousemove", "handleDrag"); // kami
				this.disconnect(dojo.doc, "onmouseup", "handleDrag"); // kami
				dojo.setSelectable(dojo.doc.body, true);

				if (this.oDragElem) {
					if (this.isRTL)
						var nDragElemWidth = this.oDragElem.offsetWidth;
	
					this.oDragElem.parentNode.removeChild(this.oDragElem);
					this.oDragElem = null;
					this.oCurrentEntryElem.style.visibility = '';
	
					var oTarget = this.oCurrentEntryElem;
					//var x = oCurrentPos.x - this.oRootElemPos.x;
					//var y = oCurrentPos.y - this.oRootElemPos.y - Math.floor(this.oCurrentEntryElem.offsetHeight / 2);
					var x = oCurrentPos.x - oStartPos.x;
					var y = oCurrentPos.y - oStartPos.y;
					var oMap = [ this.sId + '-summary', this.sId + '-allday', this.sId + '-timeslot', this.sId + '-grid', this.sId + '-header' ];
					while (oTarget && oTarget.tagName != 'BODY' && dwa.common.utils.indexOf(oMap, oTarget.id) == -1) {
						var oParent = oTarget.offsetParent? oTarget.offsetParent: oTarget.parentNode;
						x += oTarget.offsetLeft - oTarget.scrollLeft;
						y += oTarget.offsetTop - oTarget.scrollTop;
						oTarget = oParent;
					}
					if (this.isRTL)
						x = oTarget.offsetWidth - x - nDragElemWidth;
					// do not continue if unexpected error occurs (SPR KREY7TE4BD)
					if (isNaN(x) || isNaN(y))
						break;
					var oObj = this.getSelectedDateTimeAndPosition(x, y, this.oCurrentEntryElem, true);
					var oCalendar = oObj ? oObj.oDateTime : null;
					if (oCalendar) {
						if (this.oCurrentEntryEvent.bAllday) {
							oCalendar.fDateOnly = true;
						}
						else if (!this.bTimeSlotView) {
							oCalendar.nHours = this.oCurrentEntryEvent.oStartTime.nHours;
							oCalendar.nMinutes = this.oCurrentEntryEvent.oStartTime.nMinutes;
							oCalendar.nSeconds = this.oCurrentEntryEvent.oStartTime.nSeconds;
							oCalendar.nMilliseconds = this.oCurrentEntryEvent.oStartTime.nMilliseconds;
						}
	
						if (this.oCurrentEntryEvent.oStartTime.equals(oCalendar)) {
							this.selectEntry(void 0, this.oCurrentEntryElem);
						} else {
							var nIndex = this.oCurrentEntryElem.getAttribute('calendar_index');
							var oEvent = this.oCalendarDataStore.getEventById(nIndex);
							this.rescheduleEntryAction(oEvent._item, oCalendar); // kami (give the selected item instead of the elem)
						}
					}
				}
				break;
			}
		}
	},
	showDatepicker: function(ev, oElem){
		// kami
		dojo["require"]("dwa.date.datepick");
		var _this = this;
		var _ev = ev;
		if(dojo.isIE){
			_ev = {};
			for(var i in ev){ _ev[i] = ev[i]; }
		}
		dojo.addOnLoad(function(){
			var oDropdownManager = dwa.common.dropdownManager.get('root');
			var sId = _this.sId + '-calendar-datepick';
			dwa.common.commonProperty.get('p-e-calendarview-navigate').setValue(_this.oCalendar.getISO8601String());

			var widget = dijit.byId(sId);
			if(!widget){
				var datePickNode = dojo.doc.createElement("DIV");
				datePickNode.id = sId;
				datePickNode.setAttribute("role", "application");
				datePickNode.setAttribute("aria-label", _this._msgs["L_CAL_PICKER_POPUP"]);
				datePickNode.setAttribute("tabindex", "0");
				datePickNode.setAttribute("com_ibm_dwa_datepick_focus_on_close", _this.sId + '-navigator-current');
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar", "p-e-calendarview-currentselected");
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar_navigate", "p-e-calendarview-navigate");
				datePickNode.setAttribute("com_ibm_dwa_ui_datepick_dropdown", "true");
				datePickNode.style.cssText = "width:8em;height:8em;border:1px solid black;overflow:hidden;position:absolute;";
				dojo.doc.body.appendChild(datePickNode);
				var widget = new dwa.date.datepick(null, datePickNode);
			}

			if(dojo.isMozilla || dojo.isWebKit){
				oDropdownManager.oOffset = new dwa.common.utils.pos(0, 1);
				oDropdownManager.fIgnoreLayer = true;
			}
			oDropdownManager.setPos(_ev, oElem, false, true);
			oDropdownManager.show(_ev, sId);
		});
	},
	setStatus: function(sStatus){
		if (this.sStatus != sStatus) {
			var fNeedToAdjust = (this.sStatus!='' && sStatus=='') || (this.sStatus=='' && sStatus!='');
			var oElem = dojo.doc.getElementById(this.sId + '-status');
			this.sStatus = sStatus;
			if (oElem) {
				oElem.innerHTML = this.sStatus;
				if (fNeedToAdjust) {
					dwa.common.utils.cssEditClassExistence(oElem, 's-nodisplay', this.sStatus=='');
					this.adjustCalendar(true /* no need to update calendar entries */);
				}
			}
		}
	},
	resetStatus: function(){
		this.setStatus('');
	},
	handleContextMenu: function(ev, oCalendar, items){
		/*
		var sMenuId = 'e-dropdown-rightmouse-calendarview';
		this.oDropdownManager = this.oDropdownManager ? this.oDropdownManager : dwa.common.dropdownManager.get('root');
		if (!this.oDropdownManager.oHtml[sMenuId])
			this.oDropdownManager.oHtml[sMenuId]
			 = '<div id="' + sMenuId + '" class="s-popup" com_ibm_dwa_ui_widget_class="com_ibm_dwa_ui_dropdownMenu"></div>';
	
		this.oDropdownManager.nContainerWidth = this.oDropdownManager.nContainerHeight = 0;
		this.oDropdownManager.oPos = new com_ibm_dwa_misc_pos((!this.isRTL? ev.clientX: dojo.doc.body.clientWidth - ev.clientX), ev.clientY);
		this.oDropdownManager.fOppositeEdge = this.oDropdownManager.fCenter = false;
	
		// Call both hide() and show() in order to update the position of drop down box
		this.oDropdownManager.hide(ev);
		this.oDropdownManager.show(ev, sMenuId);
		
	
		//Update actions available for the selected entry
		*/
	},
	getSelectedData: function(sType){
		 if (!sType || sType.toLowerCase() == 'unid') {	// unid of selected calendar entry
			var asUnids = [];
			if (this.oSelectedEvent && this.oSelectedEvent.sUnid)
				asUnids.push(this.oSelectedEvent.sUnid);
			return asUnids;
		} else if (sType.toLowerCase() == 'datetime') {	// selected date time
			var sStartDate = '';
			if (this.oSelectedEvent && this.oSelectedEvent.oStartDate) {
				var oDate = this.oSelectedEvent.oStartDate;
				var oZoneInfo = dwa.date.zoneInfo.prototype.oUTC;
			} else if (!this.bTimeSlotView || this.bDateSelected) {
				var oNewDate = new Date();
				oNewDate.setTime(oNewDate.getTime() + 60000 * (oNewDate.getMinutes() % this.nTimeSlotDur? this.nTimeSlotDur - (oNewDate.getMinutes() % this.nTimeSlotDur): 0));
				var oDate = (new dwa.date.calendar).setDate(oNewDate);
				// fix problem can't create new entry in selected date in two weeks/one month view. (SPR NYWU7FQEFU)
				oDate.nYear = this.oCalendar.nYear;
				oDate.nMonth = this.oCalendar.nMonth;
				oDate.nDate = this.oCalendar.nDate;
				oDate.nDay = this.oCalendar.nDay;
				// fixed problem that new calendar entry created in two weeks/one month view has non zero seconds and mili seconds. (SPR NYWU7FY6N5)
				oDate.nSeconds = oDate.nMilliseconds = 0;
				var oZoneInfo = null;
			} else if (this.oCalendar) {
				var oDate = this.oCalendar;
				var oZoneInfo = dwa.date.zoneInfo.prototype.oUTC;
			}
			if (oDate)
				sStartDate = new dwa.common.notesValue(oDate.getDate(), oZoneInfo).toString().replace(',00', '');
			return sStartDate;
		}
	},
	isInViewRange: function(oCalendar, fMoveMonth){
		var oStart = this.oCalViewStart.clone();
		var oEnd = this.oCalViewEnd.clone();
		// fix regression FBUE7Q2HLL
		if (this.sType == 'M' && fMoveMonth) {
			if (oStart.nDate > 20)
				oStart.adjustDays(0, 1, 1 - oStart.nDate);
			if (oEnd.nDate < 10)
				oEnd.adjustDays(0, 0, -oEnd.nDate);
		}
		return (oCalendar.compare(oStart) >= 0 && oEnd.compare(oCalendar) > 0);
	},

	isWeekEnd: function(oDay){
		var nDay = typeof(oDay) == 'number'? oDay: oDay.nDay;
		if (this.oWeekEnd)
			return this.oWeekEnd[nDay];
		else {
			// keep backward compatibility
			var nFirstDay = this.sType == 'F'? this.nFirstDayFiveDay:
							this.sType == 'W' || this.sType == '2'? this.nFirstDayWeek:
							this.nFirstDayMonth;
			nDay = (nDay + 7 - nFirstDay) % 7;
			return nDay >= 5;
		}
	},

	cvHandler: function(event, _this, widgetId){
		var widget = dijit.byId(widgetId);
		widget.handleEvent(event,_this);
	},

	buildResourcesUrl: function(path){
		return dojo.moduleUrl("dwa.common", "images/" + path);
	},

	buildAbsoluteResourcesUrl: function(path){
		var oUrl = dojo.moduleUrl("dwa.common", "images/" + path);
		if('file' == location.protocol || oUrl.scheme){
			return oUrl.toString();
		}else if(0 == oUrl.path.indexOf('/')){
			return location.protocol + '//' + location.host + oUrl.path;
		}else{
			var aTmp = location.href.split('/');
			aTmp.splice(aTmp.length - 1, 1, oUrl.path);
			return aTmp.join('/');
		}
	},

	saveComplete: function(){
		this.clearCalendarEntries();
		this.drawCalendarEntries();
	},

	findStore: function(item){
		for(var i = 0; i < this._stores.length; i++){
			var store = this._stores[i];
			if(store.isItem(item)){
				return store;
			}
		}
	},

	findActions: function(item){
		var store = this.findStore(item);
		for(var i = 0; i < this._actionsObjs.length; i++){
			var actions = this._actionsObjs[i];
			if(actions.storeRef == store){
				return actions;
			}
		}
	},

	saveSubjectAction: function(item, subject){
		var _this = this;
		this.store.setValue(item, "subject", subject);
		item.sSubject = subject; // @TODO: we should stop accessing items this way...
		this.store.save({
			scope: _this,
			onComplete: _this.saveComplete
		});
	},

	newEntryAction: function(oCalendar){
		if(this._actionsObjs.length == 1){
			this._actionsObjs[0].newEntryAction(oCalendar);
		}
	},

	openEntryAction: function(items){
		var actions = this.findActions(items[0]);
		if(actions){
			actions.openEntryAction(items);
		}
	},

	selectEntryAction: function(items){
		var actions = this.findActions(items[0]);
		if(actions){
			actions.selectEntryAction(items);
		}
	},

	deleteEntryAction: function(items){
		var actions = this.findActions(items[0]);
		if(actions){
			actions.deleteEntryAction(items);
		}
	},

	rescheduleEntryAction: function(item, oCalendar){
		var actions = this.findActions(item);
		if(actions){
			actions.rescheduleEntryAction(item, oCalendar);
		}
	},

	changeViewAction: function(type){
		// Stub function to connect to from your application
	},

	enableStore: function(store, fEnabled){
		this.oCalendarDataStore.enableDataLoader(store, fEnabled);
	}
});

dwa.cv.calendarView.prototype.refreshContent = dwa.cv.calendarView.prototype.load; // alias
