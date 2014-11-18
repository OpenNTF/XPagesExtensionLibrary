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

dojo.provide("dwa.date.datepick");

dojo.require("dojo.i18n");
dojo.require("dijit._Widget");
dojo.require("dwa.date.calendar");
dojo.require("dwa.date.dateFormatter");
dojo.require("dwa.common.dropdownBox");
dojo.require("dwa.common.dropdownManager");
dojo.require("dwa.common.commonProperty");
dojo.require("dwa.common.graphics");
dojo.require("dwa.common.listeners");

dojo.requireLocalization("dwa.date", "datepick");

var D_ALIGN_DEFAULT = "left";
var D_ALIGN_REVERSE = "right";
var D_PADDING_DEFAULT = "padding-left";
var D_PADDING_REVERSE = "padding-right";
var D_DTFMT_MONTH = "MMMM";
var D_DTFMT_YEAR = "yyyy";
var D_Orient_Right = 3;

dojo.declare(
	"dwa.date.datepick",
	[dijit._Widget, dwa.common.dropdownBox],
{
	isRTL: false,

	postMixInProperties: function(){
		if(!dojo._isBodyLtr()){
			this.isRTL = true;
		}
		if(this.isRTL){
			D_ALIGN_DEFAULT = "right";
			D_ALIGN_REVERSE = "left";
			D_PADDING_DEFAULT = "padding-right";
			D_PADDING_REVERSE = "padding-left";
		}
		this._msgs = {};
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.date", "datepick", this.lang));
		dojo.mixin(this._msgs, dojo.i18n.getLocalization("dwa.date", "datePickExtra", this.lang));
		
		this.inherited(arguments);
	},

	postCreate: function(sId){
		var sId = this.sId = this.id;
		if (!sId)
			return;
		dojo.addClass(this.domNode, "s-datepick");
		var oElem = this.domNode; // kami
		var sWidgetKey = this.sId + ':com_ibm_dwa_ui_datepick';
		var sImg = this.buildResourcesUrl('transparent.gif');
	
		// NOTE: When user continuously click navigator button in IE, events come like, onclick, ondblclick, onclick, ondblclick... order.
		var ml = '<div id="' + this.sId + '-body" class="s-stack" style="Xpadding:1em 2px 2px 0px; background-color:white;" tabindex="0" hidefocus="true"></div>'
		 + '<div class="s-toppanel" style="height:1em;Xpadding:0px 4px;background-color:rgb(243,245,248);">';
		if(!dojo.isMozilla && !dojo.isWebkit){
			ml += '<v:rect class="s-toppanel">'
			 + '<v:stroke on="false" />'
			 + '<v:fill type="gradient" color="rgb(243,245,248)" color2="rgb(255,255,255)" />'
			 + '</v:rect>';
		}else{
			ml += '<canvas id="' + this.sId + '-gradient" class="s-stack"></canvas>';
		}
		// nakakura
		// add '<span class="s-datepick-arrowText"><</span>' and '<span class="s-datepick-arrowText">></span>' for High-Contrast Mode
		// add div, label, aria-labelledby/title attribute for sreen reader
		ml += '<table class="Xs-basicpanel s-panel-border" role="presentation" tabindex="0" aria-label="' + this._msgs["L_PICKER_HEADER"] + '" style="border-width:0px 0px 1px 0px;  height:1em;width:100%;position:relative;border:0px;" cellspacing="0" cellpadding="0">'
		 + '<tbody><tr><td ' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? ' role="button" aria-labelledby="' + this.sId + '-label-prev"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? 'title="' + this._msgs["L_PICKER_PREVIOUS"] + '"' : '')) + ' id="' + this.sId + '-tdL" class="s-cell-center s-datepick-nav-button s-handcursor" style="font-size:1px;padding:0px 3px;outline-width:0px;" tabindex=0 hidefocus=true>'
		 + '<span class="s-datepick-arrowText">&lt;</span><img id="' + this.sId + '-imgL" alt="' + this._msgs["L_PICKER_PREVIOUS"] + '" width="4" height="7" xalign="center" class="s-handcursor"'
		 + ' style=";-moz-user-focus:ignore;border:0px;" unselectable="on" src="' + sImg + '" xoffset="' + (!this.isRTL ? '120' : '140') + '" yoffset="20">'
		 + '</td>'
		 + '<td width="100%" class="s-label-light" style="text-align:center;">'
		 + '<span' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? ' role="button" aria-labelledby="' + this.sId + '-label-year"' : '') + ' id="' + this.sId + '-nav-year" class="s-datepick-nav-button s-handcursor" style="padding:0px 3px;outline-width:0px;display:inline-block;font-weight:bold;" hidefocus="true" tabindex="0">'
		 + '&nbsp;</span>'
		 + '<span' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? ' role="button" aria-labelledby="' + this.sId + '-label-month"' : '') + ' id="' + this.sId + '-nav-month" class="s-datepick-nav-button s-handcursor" style="padding:0px 3px;outline-width:0px;display:inline-block;font-weight:bold;" hidefocus="true" tabindex="0">'
		 + '&nbsp;</span></td>'
		 + '<td ' + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? ' role="button" aria-labelledby="' + this.sId + '-label-next"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? 'title="' + this._msgs["L_PICKER_NEXT"] + '"' : '')) + ' id="' + this.sId + '-tdR" class="s-cell-center s-datepick-nav-button s-handcursor" style="font-size:1px;padding:0px 3px;outline-width:0px;" tabindex=0 hidefocus=true>'
		 + '<span class="s-datepick-arrowText">&gt;</span><img id="' + this.sId + '-imgR" alt="' + this._msgs["L_PICKER_NEXT"] + '" width="4" height="7" Xalign="center" class="s-handcursor"'
		 + ' style="-moz-user-focus:ignore;border:0px;" unselectable="on" src="' + sImg + '" xoffset="' + (!this.isRTL ? '140' : '120') + '" yoffset="20">'
		 + '</td></tr></tbody></table></div>';
		 if (8 <= dojo.isIE || 3 <= dojo.isMozilla) {
		 	ml += '<label id="' + this.sId + '-label-slot" style="display:none"></label>'
			    + '<label id="' + this.sId + '-label-year" style="display:none"></label>'
			    + '<label id="' + this.sId + '-label-month" style="display:none"></label>'
			    + '<label id="' + this.sId + '-label-prev" style="display:none">'+ this._msgs["L_PICKER_PREVIOUS"]+'</label>'
			    + '<label id="' + this.sId + '-label-next" style="display:none">'+ this._msgs["L_PICKER_NEXT"]+'</label>';
		}
		oElem.innerHTML = ml;
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onclick", "onClickNavigate"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onclick", "onClickNavigate"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-body'), "onkeydown", "onKeyDownBody"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-body'), "onfocus", "onFocusBody"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-body'), "onblur", "onBlurBody"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onkeydown", "onKeyDownNavigate"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onkeydown", "onKeyDownNavigate"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onmouseover", "onMouseOver"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onmouseover", "onMouseOver"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onmouseout", "onMouseOut"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onmouseout", "onMouseOut"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onfocus", "onFocusPrev"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onfocus", "onFocusNext"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdL'), "onblur", "onBlurPrev"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-tdR'), "onblur", "onBlurNext"); // kami
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onclick", "onClickNavigate"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onclick", "onClickNavigate"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onkeydown", "onKeyDownNavigate"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onkeydown", "onKeyDownNavigate"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onmouseover", "onMouseOver"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onmouseover", "onMouseOver"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onmouseout", "onMouseOut"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onmouseout", "onMouseOut"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onfocus", "onFocusMonth"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onfocus", "onFocusYear"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-month'), "onblur", "onBlurMonth"); // nakakura
		this.connect(dojo.doc.getElementById(this.sId + '-nav-year'), "onblur", "onBlurYear"); // nakakura
		new dwa.common.consolidatedImageListener([this.sId], this.buildResourcesUrl('basicicons.gif'));
	
		if(dojo.isMozilla || dojo.isWebkit){
			var oCanvas = dojo.doc.getElementById(this.sId + '-gradient');
			oCanvas.setAttribute('width', oCanvas.offsetWidth);
			oCanvas.setAttribute('height', oCanvas.offsetHeight);
			dwa.common.graphics.colorStrokeGradient(oCanvas, null, [0, 255, 255, 255, 100, 243, 245, 248]);
			var oContext = oCanvas.getContext('2d');
			oContext.fillRect(0, 0, oCanvas.offsetWidth, oCanvas.offsetHeight);
		}
	
		var oMap = {'com_ibm_dwa_misc_observes_calendar':void 0, 'com_ibm_dwa_misc_observes_calendar_navigate':void 0};
		for (s in oMap) {
			if (oElem.getAttribute(s)) {
				var oProperty = dwa.common.commonProperty.get(this[s] = oElem.getAttribute(s));
				oProperty.attach(this);
			}
		}
		this.updateFocusDate();
		this.draw();
	},
	destroy: function() {
		if (this.oMonthpick)
			this.oMonthpick.destroy();
		if (this.oYearpick)
			this.oYearpick.destroy();
		
		if (this.com_ibm_dwa_misc_observes_calendar)
			dwa.common.commonProperty.get(this.com_ibm_dwa_misc_observes_calendar).detach(this);
		if (this.com_ibm_dwa_misc_observes_calendar_navigate)
			dwa.common.commonProperty.get(this.com_ibm_dwa_misc_observes_calendar).detach(this);
		this.inherited(arguments);
	},
	updateFocusDate: function(){
		var oElem = this.domNode;
		this.oFocusDate = this.getNavigateDate();
	},
	getNavigateDate: function(){
		var oElem = this.domNode;
		var oProperty = dwa.common.commonProperty.get(oElem.getAttribute('com_ibm_dwa_misc_observes_calendar_navigate')?
			oElem.getAttribute('com_ibm_dwa_misc_observes_calendar_navigate'):
			oElem.getAttribute('com_ibm_dwa_misc_observes_calendar'));
		var oCalendar = oProperty.vValue ? (new dwa.date.calendar).setISO8601String(oProperty.vValue) : (new dwa.date.calendar).setDate(new Date);
		oCalendar.fDateOnly = true;
		oCalendar.oZoneInfo = void 0;
		return oCalendar;
	},
	observe: function(oProperty){
		if (oProperty.isLatest && !oProperty.isLatest())
			return;
		this.updateFocusDate();
		this.draw();
	},
	draw: function(){
		this.oGrid = new dwa.date.monthGrid(this.oFocusDate);
		var oNavigateDate = this.getNavigateDate();
	
		var oDateFormatter = new dwa.date.dateFormatter();
		oDateFormatter.sFormat = D_DTFMT_MONTH;
		var sMonthLabel = oDateFormatter.format(this.oGrid.oCalendar);
		dwa.common.utils.elSetInnerText(dojo.byId(this.sId + '-nav-month'), sMonthLabel);
		oDateFormatter.sFormat = D_DTFMT_YEAR;
		var sYearLabel = oDateFormatter.format(this.oGrid.oCalendar);
		dwa.common.utils.elSetInnerText(dojo.byId(this.sId + '-nav-year'), sYearLabel);
	
		if(3 <= dojo.isMozilla || 8 <= dojo.isIE) {
			dojo.byId(this.sId + '-label-month').innerHTML = sMonthLabel;
			dojo.byId(this.sId + '-label-year').innerHTML = sYearLabel;
		} else if (dojo.isMozilla || dojo.isIE) {
			dojo.byId(this.sId + '-nav-month').title = sMonthLabel;
			dojo.byId(this.sId + '-nav-year').title = sYearLabel;
		}
	
		var oToday = (new dwa.date.calendar).setDate(new Date);
		oToday.fDateOnly = true;
	
		var asHtml = [];
		asHtml[0] = '<table role="grid" tabindex="0" summary="'+ this._msgs["L_PICKER_TABLE_SUMMARY"]+'" aria-label="'+ this._msgs["L_PICKER_TABLE"]+'" class="s-basicpanel" style="table-layout:fixed;border:0px;" cellspacing="0" cellpadding="0">'
		 + '<tbody role="rowgroup"><tr role="presentation" style="height:1em"><th style="display:none" id="hiddenHeader"></th></tr>';
	
		var asCharDays = [this._msgs["L_CHARDAY_SUN"], this._msgs["L_CHARDAY_MON"], this._msgs["L_CHARDAY_TUE"], this._msgs["L_CHARDAY_WED"], this._msgs["L_CHARDAY_THU"], this._msgs["L_CHARDAY_FRI"], this._msgs["L_CHARDAY_SAT"]];
		var asDays = [this._msgs["L_FULLDAY_SUN"], this._msgs["L_FULLDAY_MON"], this._msgs["L_FULLDAY_TUE"], this._msgs["L_FULLDAY_WED"],this._msgs["L_FULLDAY_THU"], this._msgs["L_FULLDAY_FRI"], this._msgs["L_FULLDAY_SAT"]];
		asHtml.push('<tr role="row">');
	
		for (var x = 0; x < this.oGrid.oCalendar.nDaysInWeek; x++) {
			var sCharDay = asCharDays[(new dwa.date.calendar).setUTCDate(this.oGrid[0][x]).nDay];
			var sDay = asDays[(new dwa.date.calendar).setUTCDate(this.oGrid[0][x]).nDay];
			asHtml.push('<th role="gridcell" tabindex="-1" id="' + this.sId + '-' + sDay + '" class="s-datepick-cell s-cell-center">' + sCharDay + '</th>');
		}
	
		asHtml.push('</tr>');
	
		var sWidgetKey = this.sId + ':com_ibm_dwa_ui_datepick';
	
		var ids = [];
		for (var y = 0; y < this.oGrid.nRows; y++) {
			asHtml.push('<tr role="row">');
	
			for (var x = 0; x < this.oGrid.oCalendar.nDaysInWeek; x++) {
				var oCalendar = (new dwa.date.calendar).setUTCDate(this.oGrid[y][x]);
				oCalendar.fDateOnly = true;
	
				var sClass = 's-datepick-cell s-handcursor' + (oToday.equals(oCalendar) ? ' s-datepick-cell-today' : '')
				 + ' s-datepick-cell-' + (oNavigateDate.equals(oCalendar) ? 'select' : 'normal')
				 + (this.oFocusDate.equals(oCalendar) ? ' s-datepick-cell-focus' : '');
				var sStyle = '-moz-user-focus:ignore;' + (this.oGrid.oCalendar.nMonth != oCalendar.nMonth ? 'color:gray;' : '');
	
				var sDay = asDays[(new dwa.date.calendar).setUTCDate(this.oGrid[0][x]).nDay]; 
				var sHeaders = 'headers="' + this.sId + '-' + sDay + '"'
				var sHtml = '<td role="gridcell" tabindex="0" id="' + this.sId + '-date-' + oCalendar + '" class="' + sClass + '" ' + sHeaders + ' unselectable="on" style="' + sStyle + '">'
				 + oCalendar.nDate
				 + '</td>';
	
				ids.push(this.sId + '-date-' + oCalendar);
				asHtml.push(sHtml);
			}
	
			asHtml.push('</tr>');
		}
	
		asHtml.push('</tbody></table>');
	
		dojo.doc.getElementById(this.sId + '-body').innerHTML = asHtml.join('');
		for(var i = 0, len = ids.length; i < len; i++){
			var td = dojo.doc.getElementById(ids[i]);
			this.connect(td, "onmouseover", "onCellMouseOver");
			this.connect(td, "onmouseout", "onCellMouseOut");
			this.connect(td, "onclick", "pickDate");
		}
	},
	onCellMouseOver: function(e){
		var oElem = e.currentTarget;
		dwa.common.utils.cssEditClassExistence(oElem, 's-datepick-cell-hover', true);
//		oElem.style.padding = '0px';
	},
	onCellMouseOut: function(e){
		var oElem = e.currentTarget;
		dwa.common.utils.cssEditClassExistence(oElem, 's-datepick-cell-hover', false);
//		oElem.style.padding = '';
	},
	pickDate: function(ev){
		var sId = ev.currentTarget.id;
		this.dispatchDate(sId.match(/\-date\-([[0-9]*)$/)[1]);
	},
	dispatchDate: function(sDate){
		if(sDate)
			this.oGrid.oCalendar.setISO8601String(sDate);
	
		var oElem = dojo.doc.getElementById(this.sId);
		var oMap = {'com_ibm_dwa_misc_observes_calendar':void 0, 'com_ibm_dwa_misc_observes_calendar_navigate':void 0};
		for (s in oMap) {
			if (oElem.getAttribute(s)) {
				var oProp = dwa.common.commonProperty.get(oElem.getAttribute(s));
				if(oProp.vValue){
					var oCalendar = (new dwa.date.calendar).setISO8601String(oProp.vValue);
					oCalendar.set(this.oGrid.oCalendar);
					oProp.setValue('' + oCalendar);
				}else
					oProp.setValue('' + this.oGrid.oCalendar);
			}
		}
	},
	navigate: function(nYear, nMonth, nDay){
		var oElem = this.domNode;
		if (oElem.getAttribute('com_ibm_dwa_ui_datepick_dropdown') == 'true' && nYear == 0 && nMonth == 0) {
			this.oFocusDate.adjustDays(nYear, nMonth, nDay);
			this.draw();
		} else {
			this.oGrid.oCalendar.adjustDays(nYear, nMonth, nDay);
			var oProp = dwa.common.commonProperty.get(oElem.getAttribute('com_ibm_dwa_misc_observes_calendar_navigate')?
				oElem.getAttribute('com_ibm_dwa_misc_observes_calendar_navigate'):
				oElem.getAttribute('com_ibm_dwa_misc_observes_calendar'));
			if(oProp.vValue){
				var oCalendar = (new dwa.date.calendar).setISO8601String(oProp.vValue);
				oCalendar.set(this.oGrid.oCalendar);
				oProp.setValue('' + oCalendar);
			}else
				oProp.setValue('' + this.oGrid.oCalendar);
		}
		// nakakura
		var bNoFocus = (nMonth == 1 || nMonth == -1) && nYear == 0 && nDay == 0;
		this.setAriaLabel(bNoFocus);
	},
	onClickNavigate: function(ev, bNoFocus){
		var nYear, nMonth;
		var oElem = ev.currentTarget;
		if(oElem.id.indexOf("-nav-month") != -1){
			return this.showMonthPicker(ev);
		}else if (oElem.id.indexOf("-nav-year") != -1){
			return this.showYearPicker(ev);
		}else if(oElem.id.indexOf("tdL") != -1){
			nYear = 0;
			nMonth = -1;
		}else if(oElem.id.indexOf("tdR") != -1){
			nYear = 0;
			nMonth = 1;
		}
		this.navigate(nYear, nMonth, 0);
		if(!bNoFocus)
			this.focusToBody();
		dojo.stopEvent(ev);
	},
	focus: function(){
		dojo.doc.getElementById(this.sId + '-body').tabIndex = 0;
		dojo.doc.getElementById(this.sId + '-tdL').tabIndex = 0;
		dojo.doc.getElementById(this.sId + '-tdR').tabIndex = 0;
		dojo.doc.getElementById(this.sId + '-nav-month').tabIndex = 0;
		dojo.doc.getElementById(this.sId + '-nav-year').tabIndex = 0;
		this.focusToBody();
	},
	blur: function(){
		dojo.doc.getElementById(this.sId + '-body').tabIndex = -1;
		dojo.doc.getElementById(this.sId + '-tdL').tabIndex = -1;
		dojo.doc.getElementById(this.sId + '-tdR').tabIndex = -1;
		dojo.doc.getElementById(this.sId + '-nav-month').tabIndex = -1;
		dojo.doc.getElementById(this.sId + '-nav-year').tabIndex = -1;
		var oElem = this.domNode;
		if(oElem && oElem.getAttribute('com_ibm_dwa_datepick_focus_on_close')) {
			dojo.doc.getElementById(oElem.getAttribute('com_ibm_dwa_datepick_focus_on_close')).focus();
		}
	},
	focusToBody: function(){
		dojo.doc.getElementById(this.sId + '-body').focus();
	},
	onKeyDownNavigate: function(ev){
		var oElem = ev.currentTarget;
		switch(ev.keyCode) {
			case 13: // return
			case 32: // space
				return this.onClickNavigate(ev, true);
			case 27: // esc
				// close datepicker
				dojo.stopEvent(ev);
				return this.deactivate();
			case 9: // tab
				if(oElem.id.indexOf("tdL") != -1 && ev.shiftKey) {
					// close datepicker
					dojo.stopEvent(ev);
					return this.deactivate();
				}
				if(oElem.id.indexOf("tdR") != -1 && !ev.shiftKey) {
					// focus to date grid
					dojo.stopEvent(ev);
					return this.focusToBody();
				}
				return;
		}
	},
	onKeyDownBody: function(ev){
		switch(ev.keyCode) {
			case 13: // return
			case 32: // space
				dojo.stopEvent(ev);
				this.dispatchDate();
				return this.deactivate();
			case 27: // esc
				// close datepicker
				dojo.stopEvent(ev);
				return this.deactivate();
			case 9: // tab
				if(ev.shiftKey) {
					// focus to next month button
					dojo.stopEvent(ev);
					dojo.doc.getElementById(this.sId + '-tdR').focus();
				} else {
					// close datepicker
					dojo.stopEvent(ev);
					return this.deactivate();
				}
				return;
			case 37: // left
				dojo.stopEvent(ev);
				this.navigate(0, 0, this.isRTL ? 1 : -1);
				return;
			case 39: // right
				dojo.stopEvent(ev);
				this.navigate(0, 0, this.isRTL ? -1 : 1);
				return;
			case 38: // up
				dojo.stopEvent(ev);
				this.navigate(0, 0, -7);
				return;
			case 40: // down
				dojo.stopEvent(ev);
				this.navigate(0, 0, 7);
				return;
			case 33: // pageup
				dojo.stopEvent(ev);
				this.navigate(ev.ctrlKey ? -1 : 0, ev.ctrlKey ? 0 : -1, 0);
				return;
			case 34: // pagedown
				dojo.stopEvent(ev);
				this.navigate(ev.ctrlKey ? 1 : 0, ev.ctrlKey ? 0 : 1, 0);
				return;
		}
	},
	onMouseOver: function(ev){
		dwa.common.utils.cssEditClassExistence(ev.currentTarget, 's-datepick-nav-button-hilighted', true);
	},
	onMouseOut: function(ev){
		dwa.common.utils.cssEditClassExistence(ev.currentTarget, 's-datepick-nav-button-hilighted', false);
	},
	onFocusPrev: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-tdL'),'s-datepick-nav-button-focused', true);
	},
	onFocusNext: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-tdR'),'s-datepick-nav-button-focused', true);
	},
	onFocusMonth: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-nav-month'),'s-datepick-nav-button-focused', true);
	},
	onFocusYear: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-nav-year'),'s-datepick-nav-button-focused', true);
	},
	getFocusedCell: function(){
		var oCalendar = (new dwa.date.calendar).setUTCDate(this.oFocusDate.getUTCDate());
		oCalendar.fDateOnly = true;
		var sId = this.sId + '-date-' + oCalendar;
		return dojo.doc.getElementById(sId);
	},
	onFocusBody: function(ev){
		var oElem = this.getFocusedCell();
		if(oElem)
			dwa.common.utils.cssEditClassExistence(oElem, 's-datepick-cell-focus', true);
	},
	onBlurPrev: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-tdL'),'s-datepick-nav-button-focused', false);
	},
	onBlurNext: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-tdR'),'s-datepick-nav-button-focused', false);
	},
	onBlurMonth: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-nav-month'),'s-datepick-nav-button-focused', false);
	},
	onBlurYear: function(ev){
		dwa.common.utils.cssEditClassExistence(dojo.doc.getElementById(this.sId + '-nav-year'),'s-datepick-nav-button-focused', false);
	},
	onBlurBody: function(ev){
		var oElem = this.getFocusedCell();
		if(oElem)
			dwa.common.utils.cssEditClassExistence(oElem, 's-datepick-cell-focus', false);
	},
	showMonthPicker: function(ev){
		dojo["require"]("dwa.date.monthpick");
		var _this = this;
		var _ev = dojo.isIE ? dojo.mixin({}, ev) : ev;
		dojo.addOnLoad(function(){
			var oDropdownManager = dwa.common.dropdownManager.get('ondatepick');
			var sId = _this.sId + "-monthpick";
			if(!dojo.byId(sId)){
				var oPickerNode = dojo.doc.createElement('DIV');
				dojo.addClass(oPickerNode, "s-hidden");
				oPickerNode.setAttribute("id", sId);
				oPickerNode.setAttribute("role", "region");
				oPickerNode.setAttribute("aria-label", _this._msgs["L_PICKER_MONTH"]);
				oPickerNode.setAttribute("com_ibm_dwa_ui_monthpick_startOffset", "-5");
				oPickerNode.setAttribute("com_ibm_dwa_misc_observes_calendar", (_this.com_ibm_dwa_misc_observes_calendar_navigate ? _this.com_ibm_dwa_misc_observes_calendar_navigate : _this.com_ibm_dwa_misc_observes_calendar));
				dojo.doc.body.appendChild(oPickerNode);
				_this.oMonthpick = new dwa.date.monthpick(sId);
			}else{
				_this.oMonthpick.refresh();
			}
			if(dojo.isMozilla || dojo.isWebkit){
				oDropdownManager.oOffset = new dwa.common.utils.pos(0, 1);
				oDropdownManager.fIgnoreLayer = true;
			}
			oDropdownManager.nOrient = D_Orient_Right;
			oDropdownManager.setPos(_ev, dojo.byId(_this.sId + "-nav-month"), false, true);
			oDropdownManager.show(_ev, sId);
		});
		dojo.stopEvent(ev);
	},
	showYearPicker: function(ev){
		dojo["require"]("dwa.date.yearpick");
		var _this = this;
		var _ev = dojo.isIE ? dojo.mixin({}, ev) : ev;
		dojo.addOnLoad(function(){
			var oDropdownManager = dwa.common.dropdownManager.get('ondatepick');
			var sId = _this.sId + "-yearpick";
			if(!dojo.byId(sId)){
				var oPickerNode = dojo.doc.createElement('DIV');
				dojo.addClass(oPickerNode, "s-hidden");
				oPickerNode.setAttribute("id", sId);
				oPickerNode.setAttribute("role", "region");
				oPickerNode.setAttribute("aria-label", _this._msgs["L_PICKER_YEAR"]);
				oPickerNode.setAttribute("com_ibm_dwa_ui_yearpick_startOffset", "-3");
				oPickerNode.setAttribute("com_ibm_dwa_misc_observes_calendar", (_this.com_ibm_dwa_misc_observes_calendar_navigate ? _this.com_ibm_dwa_misc_observes_calendar_navigate : _this.com_ibm_dwa_misc_observes_calendar));
				dojo.doc.body.appendChild(oPickerNode);
				_this.oYearpick = new dwa.date.yearpick(sId);
			}else{
				_this.oYearpick.refresh();
			}
			if(dojo.isMozilla || dojo.isWebkit){
				oDropdownManager.oOffset = new dwa.common.utils.pos(0, 1);
				oDropdownManager.fIgnoreLayer = true;
			}
			oDropdownManager.nOrient = D_Orient_Right;
			oDropdownManager.setPos(_ev, dojo.byId(_this.sId + "-nav-year"), false, true);
			oDropdownManager.show(_ev, sId);
		});
		dojo.stopEvent(ev);
	},
	buildResourcesUrl: function(path){
		return dojo.moduleUrl("dwa.common", "images/" + path);
	},
	// nakakura
	setAriaLabel: function(bNoFocus) {
		// JAWS10 does not support safari and chrome
		if (dojo.isIE || dojo.isMozilla) {
			if (!this.oFormatterA11y)
				this.oFormatterA11y = new dwa.date.dateFormatter();
			var sTitle = this.oFormatterA11y.asDays[this.oFocusDate.nDay] + " " + this.oFocusDate.nDate + " " +
				this.oFormatterA11y.asMonths[this.oFocusDate.nMonth] + " " + this.oFocusDate.nYear;
			if (8 <= dojo.isIE || 3 <= dojo.isMozilla) {
				var id = this.sId + '-label-slot';
				dojo.byId(id).innerHTML = sTitle;
				dijit.setWaiState(dojo.doc.getElementById(this.sId + '-body'), "labelledby", id);
			} else
				dojo.doc.getElementById(this.sId + '-body').title = sTitle;
			// JAWS does not aria-labelledby title attribute jsut by changing it.
			if (!bNoFocus) {
				dojo.doc.getElementById(this.sId + '-tdL').focus();
				dojo.doc.getElementById(this.sId + '-body').focus();
			}
		}
	}
});

dojo.declare(
	"dwa.date.monthGrid",
	null,
{
	constructor: function(oCalendar){
		var oDate = new Date(Date.UTC(oCalendar.nYear, oCalendar.nMonth, 1, 12, 0, 0, 0));
		var nDaysBefore = oDate.getUTCDay() - dwa.date.dateFormatter.prototype.oCalendarData.nFirstDayMonth;
	
		if (nDaysBefore < 0)
			nDaysBefore += oCalendar.nDaysInWeek;
		
		var nDaysInAndBefore = oCalendar.getDaysInMonth() + nDaysBefore;
	
		this.nRows = parseInt(nDaysInAndBefore / oCalendar.nDaysInWeek);
	
		if (nDaysInAndBefore > this.nRows * oCalendar.nDaysInWeek)
			this.nRows++;
	
		if (nDaysBefore)
			oDate.setTime(oDate.getTime() - nDaysBefore * 86400000);
	
		for (var y = 0; y < this.nRows; y++) {
			this[y] = [];
			for (var x = 0; x < oCalendar.nDaysInWeek; x++, oDate = new Date(oDate.getTime() + 86400000))
				this[y][x] = oDate;
		}
	
		this.oCalendar = oCalendar;
	}
});
