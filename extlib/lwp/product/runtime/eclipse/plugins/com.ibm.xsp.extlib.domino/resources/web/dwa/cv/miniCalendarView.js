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

dojo.provide("dwa.cv.miniCalendarView");

dojo.require("dojo.i18n");
dojo.require("dijit._Widget");
dojo.require("dwa.date.calendar");
dojo.require("dwa.date.dateFormatter");
dojo.require("dwa.common.utils");
dojo.require("dwa.common.commonProperty");
dojo.require("dwa.common.notesValue");
dojo.require("dwa.cv.calendarDataStore");
dojo.require("dwa.common.listeners");

dojo.requireLocalization("dwa.cv", "calendarView");

dojo.declare(
	"dwa.cv.miniCalendarView",
	dijit._Widget,
{
	date: "", // ex. 2010/01/24
	store: "",
	_stores: null,
	isRTL: false,
	autoRender: true,
	
	postMixInProperties: function() {
		this._msgs = {};
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.cv", "calendarView", this.lang));
		
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
		
		if(!dojo._isBodyLtr()){
			this.isRTL = true;
		}
	},
	postCreate: function(){
		var oElem = dojo.doc.getElementById(this.sId = this.id);
		dojo.addClass(this.domNode, "s-basicpanel");
		// support alternate name
		this.bShowAlternateName = this.sLanguagePreference && this.bNamePreference;
	
		// support calendar overlay
		var oProperty = dwa.common.commonProperty.get(oElem.getAttribute('com_ibm_dwa_misc_observes_caloverlay'));
		oProperty.attach(this);
	
		// Set the activity handler for use
		this.sActivityHandler = dojo.doc.getElementById(this.sId).getAttribute('e-sidebar-calendar-pane-activity');
		
		// nakakura
		this.oCalendarDataStore = dwa.cv.calendarDataStore.getInstance();//maybe to be revised.
		this.oCalendarDataStore.registerViewWidget(this);
		for(var i = 0; i < this._stores.length; i++){
			this.oCalendarDataStore.registerDataLoader(this._stores[i]);
		}
		this.oCalendarDataStore.setActivityHandler(this.sActivityHandler);

		var oProperty = dwa.common.commonProperty.get(oElem.getAttribute('com_ibm_dwa_misc_observes_calendar'));
		// nakakura
		this.oCalendar = this.date ? (new dwa.date.calendar).setISO8601String(this.date) : (new dwa.date.calendar).setDate(new Date());
		this.oCalendar.fDateOnly = true;
		oProperty.vValue = this.oCalendar.getISO8601BasicFormatString();
		
		oProperty.attach(this);
		this.observe(oProperty);
	},
	// nakakura
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
		dwa.common.commonProperty.get('p-e-datepick-sidebar-currentselected').detach(this);
		this.inherited(arguments);
	},
	render: function(){
		// to call observe function
		dwa.common.commonProperty.get('p-e-datepick-sidebar-currentselected').attach(this);
	},
	observe: function(oProperty){
		var oElem = dojo.byId(this.sId);
		/*
		if (oProperty.sName == oElem.getAttribute('com_ibm_dwa_misc_observes_caloverlay')) {
			this.bShowCalOverlay = this.fCalOverlaySidebar && oProperty.vValue;
			if (this.bShowCalOverlay && !this.bCalOverlayLoaded)
				this.initMyCalendars();
			else
				this.onDatasetComplete();
		} else
		if (oProperty.sName == oElem.getAttribute('com_ibm_dwa_misc_observes_calendar')) {*/
		if (!oProperty.vValue || oProperty.isLatest && !oProperty.isLatest() || (oProperty.vValue == this.sCurrent && !this.fForceReload))
			return;
		
		this.fForceReload = false;
		this.sCurrent = oProperty.vValue;
	
		var oCurrent = (new dwa.date.calendar).setISO8601String(this.sCurrent);
		oCurrent.fDateOnly = false;
		oCurrent.nHours = oCurrent.nMinutes = oCurrent.nSeconds = oCurrent.nMilliseconds = 0;
		oCurrent.setUTCDate(oCurrent.getUTCDate()); // Obtain day of the week
	
		// This class holds the list dates an array for historical reason (Started with two dates initally)
		// Given we might be changing it in future, we keep the mechanism as it is
		var oDateFormatter = new dwa.date.dateFormatter;
		this.asDates = [oDateFormatter.format(oCurrent)];
	
		var oMainDoc = dojo.doc;
		var sWidgetKey = this.sId + ':com_ibm_dwa_ui_sideCalendar';
		var sWidget = 'dwa.cv.miniCalendarView.prototype';//'com_ibm_dwa_globals.oScript.com_ibm_dwa_io_widgetListener.prototype.oWidgets[\'' + sWidgetKey + '\']';
		var sTransparent = this.buildResourcesUrl('transparent.gif');
	
		// NOTE: When user continuously click navigator button in IE, events come like, onclick, ondblclick, onclick, ondblclick... order.
		if (!oMainDoc.getElementById(this.sId + '-nav-0')) {
			var sNavHtml = '<div id="' + this.sId + '-nav-CURRENTDATEID"'
			 + ' class="s-sidecalendar-entry s-toppanel" style="padding:0px;background-color:rgb(233,236,241);height:1em;">'
			 + '<table width="100%" border="0" cellspacing="0" cellpadding="0">'
			 + '<tbody>'
			 + '<tr align="center">'
			 + '<td width="25%" class="s-sidecalendar-dateindicator"';
			if(!dojo.isMozilla && !dojo.isWebKit){
				sNavHtml += ' unselectable="on" onclick="' + sWidget + '.navigate(-1, \'' + this.sId + '\');" ondblclick="' + sWidget + '.navigate(-1, \'' + this.sId + '\');"';
			}else{
				sNavHtml += ' style="' + '' + '" onclick="' + sWidget + '.navigate(-1, \'' + this.sId + '\');"';
			}
			sNavHtml += ' onmouseover="' + sWidget + '.onDateMouseOverOut(event);" onmouseout="' + sWidget + '.onDateMouseOverOut(event);" title="' + "Previous day" + '">'
			 + '<img width="6" height="9" src="' + sTransparent + '" xoffset="' + (!this.isRTL ? '120' : '140') + '" yoffset="20">'
			 + '</td>'
			 + '<td id="' + this.sId + '-nav-CURRENTDATEID-dateindicator" width="50%" class="s-sidecalendar-dateindicator" style="font-weight:bold;"'
			 + ' onclick="' + sWidget + '.showDatepicker(event, this, \'' + this.sId + '\');"'
			 + ' onmouseover="' + sWidget + '.onDateMouseOverOut(event);" onmouseout="' + sWidget + '.onDateMouseOverOut(event);" title="' + "Go to date" + '">'
			 + '</td>'
			 + '<td width="25%" class="s-sidecalendar-dateindicator"';
			if(!dojo.isMozilla && !dojo.isWebKit){
				sNavHtml += ' unselectable="on" onclick="' + sWidget + '.navigate(1, \'' + this.sId + '\');" ondblclick="' + sWidget + '.navigate(1, \'' + this.sId + '\');"';
			}else{
				sNavHtml += ' style="' + '' + '" onclick="' + sWidget + '.navigate(1, \'' + this.sId + '\');"';
			}
			sNavHtml += ' onmouseover="' + sWidget + '.onDateMouseOverOut(event);" onmouseout="' + sWidget + '.onDateMouseOverOut(event);" title="' + "Next day" + '">'
			 + '<img width="6" height="9" src="' + sTransparent + '" xoffset="' + (!this.isRTL ? '140' : '120') + '" yoffset="20">'
			 + '</td>'
			 + '</tr>'
			 + '</tbody>'
			 + '</table>'
			 + '</div>';
	
			var asHtml = [];
	
			for (var i = 0; i < this.asDates.length; i++){
				asHtml.push('<div class="s-basicpanel" style="padding-top:1em;"><table cellpadding="0" cellspacing="0" width="100%"><tr><td id="' + this.sId + '-entries-' + i + '"></td></tr></table></div>');
				asHtml.push(sNavHtml.replace(/CURRENTDATEID/g, i));
			}
	
			oMainDoc.getElementById(this.sId).innerHTML = asHtml.join('');
			new dwa.common.consolidatedImageListener([this.sId], this.buildResourcesUrl('basicicons.gif'));
		}
	
		for (var i = 0; i < this.asDates.length; i++)
			dwa.common.utils.elSetInnerText(oMainDoc.getElementById(this.sId + '-nav-' + i + '-dateindicator'), this.asDates[i]);
	
		// load calendar data
		var oCalendarStart = (new dwa.date.calendar).setISO8601String(this.sCurrent);
		oCalendarStart.fDateOnly = false;
		oCalendarStart.nHours = oCalendarStart.nMinutes = oCalendarStart.nSeconds = oCalendarStart.nMilliseconds = 0;
		var oCalendarEnd = (new dwa.date.calendar).setDate(oCalendarStart.getDate());
		oCalendarEnd.adjustDays(0, 0, 1);
		this.oCalendarDataStore.load(oCalendarStart, oCalendarEnd);
		//}
	},
	onDatasetComplete: function(){
		var aoData = [];
		var oEventTypesWithTime = {'0': void 0, '3': void 0, '4': void 0, 'Unprocessed': void 0, 'Cancelled': void 0};
	
		var oDate = (new dwa.date.calendar).setISO8601String(this.sCurrent);
		aoData = this.oCalendarDataStore.getEventsByDate(oDate);
		var aoData2 = [];
		for (var i=0; i<aoData.length; i++) {
			if (!aoData[i].fExternal)
				aoData2[aoData2.length] = aoData[i];
		}
		aoData = aoData2;
	
		var oDateFormatter = new dwa.date.dateFormatter;
		var oTimeFormatter = new dwa.date.dateFormatter(101);
	
		var aasHtml = [[], []];
	
		var sWidgetKey = this.sId + ':com_ibm_dwa_ui_sideCalendar';
		var sWidget = 'dwa.cv.miniCalendarView.prototype';//'com_ibm_dwa_globals.oScript.com_ibm_dwa_io_widgetListener.prototype.oWidgets[\'' + sWidgetKey + '\']';
	
		for (var n = 0, i = 0; i < aoData.length; i++) {
			var oCalendar = (new dwa.date.calendar).setDate(aoData[i].oStartTime.getDate());
			var oCalendarEnd = (new dwa.date.calendar).setDate(aoData[i].oEndTime.getDate());
			if (this.asDates[n] != oDateFormatter.format(oCalendar))
				n++;
	
			if(!dojo.isMozilla && !dojo.isWebKit){
				var sStyle = ' style="color:' + aoData[i].sFontColor + ';background-color:' + aoData[i].sBGColor1 + ';" unselectable="on"';
				var sDateHtml = aoData[i].bAllday? '':
					  '<b unselectable="on">' + oTimeFormatter.format(oCalendar)
					+ (oCalendarEnd ? ' - ' + oTimeFormatter.format(oCalendarEnd) : '') + '</b>';
			}else{
				var sStyle = ' style="color:' + aoData[i].sFontColor + ';background-color:' + aoData[i].sBGColor1 + ';' + '' + '"';
				var sDateHtml = aoData[i].bAllday? '':
					  '<b>' + oTimeFormatter.format(oCalendar)
					+ (oCalendarEnd ? ' - ' + oTimeFormatter.format(oCalendarEnd) : '') + '</b>';
			}
	
			aoData[i].sThisStartDate = new dwa.common.notesValue(aoData[i].oStartTime.getDate(),
									dwa.date.zoneInfo.prototype.oUTC).toString().replace(',00', '');
	
			var sHtml = '<div id="' + this.sId + '-entry-' + i + '" unid="' + aoData[i].sUnid + '"'
			 + ' class="s-label-light s-handcursor s-sidecalendar-entry"'
			 + ' calendar_external="' + (aoData[i].fExternal? '1': '0') + '"'
			 + ' calendar_index="' + aoData[i].nIndex + '"'
			 + sStyle
			 + ' onclick="' + sWidget + '.selectEntry(this, \'' + this.sId + '\');" ondblclick="' + sWidget + '.openEntryAction(this);">'
			 + sDateHtml + '</div>';
	
			aasHtml[n].push(sHtml);
		}
	
		var oMainDoc = dojo.doc;
	
		for (var n = 0; n < this.asDates.length; n++) {
			if (!aasHtml[n].length)
				aasHtml[n][0] = '<div style="padding:1px 2px;">'
				 + '<em class="s-label-light">'
				 + dwa.common.utils.formatMessage(this._msgs["L_SIDECALENDAR_NOENTRY"], this.asDates[n])
				 + '</em>'
				 + '</div>';
	
			var oEntries = oMainDoc.getElementById(this.sId + '-entries-' + n);
			oEntries.innerHTML = aasHtml[n].join('');
		}
	
		for (var i = 0; i < aoData.length; i++) {
			var oParent = oMainDoc.getElementById(this.sId + '-entry-' + i);
			var oDiv  = oMainDoc.createElement('div');
			oDiv.style.paddingLeft = '3ex';
			if(!dojo.isMozilla && !dojo.isWebKit){
				oDiv.unselectable = 'on';
				oDiv.style.wordWrap = 'break-word';
			}
			oDiv = oParent.appendChild(oDiv);
	
			var asLines = [aoData[i].sSubject];
			var sChair = this.bShowAlternateName && aoData[i].sAltChair? aoData[i].sAltChair: aoData[i].sChair;
			if (aoData[i].sLocation && !aoData[i].bAllday)
				asLines.push(aoData[i].sLocation);
			if (sChair && aoData[i].sType == '3')
				asLines.push(sChair);
	
			for (var j = 0; j < asLines.length; j++) {
				if(asLines[j].length == 0)
					continue;
				if(oDiv.hasChildNodes())
					oDiv.appendChild(oMainDoc.createElement('br'));
				else if(asLines[j].length != asLines.join('').length){
					var oB = oDiv.appendChild(oMainDoc.createElement('b'));
					if(!dojo.isMozilla && !dojo.isWebKit){
						oB.unselectable = 'on';
						oB.appendChild(oMainDoc.createTextNode(asLines[j]));
					}else{
					oB.appendChild(oMainDoc.createTextNode(this.wrapText(asLines[j], 10)));
					}
					continue;
				}
				if(!dojo.isMozilla && !dojo.isWebKit){
					oDiv.appendChild(oMainDoc.createTextNode(asLines[j]));
				}else{
					oDiv.appendChild(oMainDoc.createTextNode(this.wrapText(asLines[j], 10)));
				}
			}
		}
	},
	wrapText: function(sText, nLen){
		var asText = sText.split(' ');
		for(var i = 0; i < asText.length; i++){
			if(asText[i].length > nLen)
				asText[i] = asText[i].split('').join(String.fromCharCode(8203));
		}
		return asText.join(' ');
	},
	navigate: function(nDate, widgetId){
		var oElem = dojo.byId(widgetId);
		var oProperty = dwa.common.commonProperty.get(oElem.getAttribute('com_ibm_dwa_misc_observes_calendar'));
		var oCalendar = (new dwa.date.calendar).setISO8601String(dijit.byId(widgetId).sCurrent);
		oCalendar.adjustDays(0, 0, nDate);
		oProperty.setValue('' + oCalendar);
	},
	onDateMouseOverOut: function(ev){
		var oElem = ev.target ? ev.target : ev.srcElement;
		if(oElem.tagName.match(/img/i)) oElem = oElem.parentNode;
		oElem.className = 's-sidecalendar-dateindicator' + (ev.type == 'mouseover' ? '-highlighted' : '');
	},
	showDatepicker: function(ev, oElem, widgetId){
		dojo["require"]("dwa.date.datepick");
		var _this = dijit.byId(widgetId);
		var _ev = ev;
		if(dojo.isIE){
			_ev = {};
			for(var i in ev){ _ev[i] = ev[i]; }
		}
		dojo.addOnLoad(function(){
			var oDropdownManager = dwa.common.dropdownManager.get('root');
			var sId = widgetId + '-calendar-datepick';
			dwa.common.commonProperty.get('p-e-datepick-sidebar-navigate').setValue(_this.sCurrent);
			
			var widget = dijit.byId(sId);
			if(!widget){
				var datePickNode = dojo.doc.createElement("DIV");
				datePickNode.id = sId;
				datePickNode.setAttribute("com_ibm_dwa_ui_widget_class", "com_ibm_dwa_ui_datepick");
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar", "p-e-datepick-sidebar-currentselected");
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar_navigate", "p-e-datepick-sidebar-navigate");
				datePickNode.setAttribute("com_ibm_dwa_ui_datepick_dropdown", "true");
				datePickNode.setAttribute("class", "s-popup");
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
	selectEntry: function(oSelected, widgetId){
		for (var oElem = null, i = 0; oElem = dojo.byId(widgetId + '-entry-' + i); i++){
			var fHighlight = (oSelected.getAttribute('unid') == oElem.getAttribute('unid'));
			dwa.common.utils.cssEditClassExistence(oElem, 's-sidecalendar-entry-highlighted', fHighlight);
		}
	},
	// nakakura
	buildResourcesUrl: function(path){
		return dojo.moduleUrl("dwa.common", "images/" + path);
	},
	openEntryAction: function(item){
		// Stub function to connect to from your application
	},
	enableStore: function(store, fEnabled){
		this.oCalendarDataStore.enableDataLoader(store, fEnabled);
	}
});
