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

dojo.provide("dwa.lv.searchDialog");

dojo.require("dwa.common.dialog");
dojo.require("dwa.common.commonProperty");

dojo.requireLocalization("dwa.lv", "listview_s");

var D_DateFmt_Medium = 3;

dojo.declare(
	"dwa.lv.searchDialog",
	null,
{
	context: null,
	id: "",
	
	isRTL: false,
	_dialog: null,
	_started: false,
	_isDateSelected: false,
	
	constructor: function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		if (!this.id) {
			return;
		}
		this.sId = this.id;
		this._smsgs = {};
		dojo.mixin(this._smsgs, dojo.i18n.getLocalization("dwa.lv", "listview_s", this.lang));
		if (!dojo._isBodyLtr()) {
			this.isRTL = true;
		}
		dwa.common.commonProperty.get('p-e-startswidth-navigate-currentselected').attach(this);
	},
	
	show: function(context) {
		this.context = context;
		if (!this._started) {
			this._createDialog();
			this._started = true;
		}
		for (var i = 0; i < this.context.sortInfo.sortCols.length; i++) {
			if (this.context.sortInfo.sortCols[i].index == this.context.sortInfo.sortColIdx) {
				dojo.byId(this.sId + '-column-select').value = i;
			}
		}
		this._changeInputField();
		this._getInputField().value = this.context.getBufferedSearchString() || "";
		this._dialog.show();
		this._getInputField().focus();
	},
	
	hide: function() {
		this._dialog.hide();
		this.context.cancelSearch();
	},
	
	destroy: function() {
		dwa.common.commonProperty.get('p-e-startswidth-navigate-currentselected').detach(this);
		this._dialog.destroy();
		if (this._datepick) {
			this._datepick.destroy();
		}
	},
	
	observe: function() {
		var sDate = dwa.common.commonProperty.get('p-e-startswidth-navigate-currentselected').vValue;
		var oCalendar = (new dwa.date.calendar).setDate(new Date(sDate.substring(0, 4), sDate.substring(4, 6) - 1, sDate.substring(6, 8)));
		if (!this.oFormatter) {
			this.oFormatter = new dwa.date.dateFormatter(D_DateFmt_Medium);
		}
		dojo.byId(this.sId + '-date-input').value = this.oFormatter.format(oCalendar);
	},
	
	search: function() {
		var column = this._getSelectedColumn();
		var searchKeyword = dojo.trim(this._getInputField().value || '');
		if (column && searchKeyword != '') {
			switch (column.type) {
				case "date":
					try {
						if (!this.oFormatter) {
							this.oFormatter = new dwa.date.dateFormatter(D_DateFmt_Medium);
						}
						oCalendar = this.oFormatter.validateDate(searchKeyword);
					} catch(e) {
						alert(this._smsgs["L_ERR_INVALIDDATE"]);
						this._getInputField().focus();
						return;
					}
					break;
				case "number":
					searchKeyword -= 0;
					if (isNaN(searchKeyword)) {
						alert(this._smsgs["L_STARTSWITH_INVALID_NUMBER"]);
						this._getInputField().focus();
						return;
					}
			}
			this.hide();
			this.context.processSearch(column.index, searchKeyword);
		} else {
			this.hide();
			this.context.cancelSearch();
		}
	},
	
	_showDatepicker: function(ev) {
		dojo["require"]("dwa.date.datepick");
		var _this = this;
		var _ev = ev;
		dojo.addOnLoad(function() {
	 		dwa.common.commonProperty.get('p-e-startswidth-navigate').setValue((new dwa.date.calendar).setDate(new Date()).getISO8601String());
			var oDropdownManager = dwa.common.dropdownManager.get('root');
			var sId = _this.sId + '-datepick';
			if (!_this._datepick) {
				var datePickNode = dojo.doc.createElement("div");
				datePickNode.id = sId;
				datePickNode.setAttribute("com_ibm_dwa_datepick_focus_on_close", _this.sId + '-date-input');
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar", "p-e-startswidth-navigate");
				datePickNode.setAttribute("com_ibm_dwa_misc_observes_calendar_navigate", "p-e-startswidth-navigate-currentselected");
				datePickNode.setAttribute("com_ibm_dwa_ui_datepick_dropdown", "true");
				datePickNode.style.cssText = "width:8em;height:8em;border:1px solid black;overflow:hidden;position:absolute;";
				dojo.doc.body.appendChild(datePickNode);
				_this._datepick = new dwa.date.datepick(null, datePickNode);
				_this._dialog.connect(_this._datepick, 'onBlur', function() {
					_this.isAfterClosingDatepick = true;
					setTimeout(function() {
						_this.isAfterClosingDatepick = false;
					}, 100);
				});
			}
			if (dojo.isMozilla || dojo.isWebKit){
				oDropdownManager.fIgnoreLayer = true;
			}
			oDropdownManager.setPos(_ev, dojo.byId(_this.sId + '-searchrow'));
			oDropdownManager.show(_ev, sId);
		});
	},
	
	_createDialog: function() {
		var dialogNode = dojo.doc.createElement('div');
		dialogNode.id = this.sId + "-search-dialog";
		dialogNode.innerHTML = this._generateDialogHtml();
		dojo.doc.body.appendChild(dialogNode);
		this._dialog = new dwa.common.dialog({
			label: this._smsgs["L_STARTSWITH_TITLE"],
			width: 300,
			height: 160,
			minWidth: 250,
			minHeight: 150,
			restoreSize: true
		}, dialogNode);
		this._dialog.startup();
		this._dialog.connect(dojo.byId(this.sId + '-ok'), 'onclick', dojo.hitch(this, this.search));
		this._dialog.connect(dojo.byId(this.sId + '-cancel'), 'onclick', dojo.hitch(this, this.hide));
		this._dialog.connect(dojo.byId(this.sId + '-column-select'), 'onchange', dojo.hitch(this, this._changeInputField));
		this._dialog.connect(dojo.byId(this.sId + '-searchdate'), 'onclick', dojo.hitch(this, function(ev) {
			if (dojo.isIE && ev.srcElement.id != (this.sId + '-searchdate')) {
				dojo.stopEvent(ev);
				dojo.byId(this.sId + '-searchdate').fireEvent("onclick");
			} else {
				this._showDatepicker(dojo.isIE ? dojo.mixin({}, ev) : ev);
			}
		}));
		this._dialog.connect(dojo.byId(this.sId + '-date-input'), 'onfocus', dojo.hitch(this, function(ev) {
			if (!this.isAfterClosingDatepick) {
				if (dojo.isIE) {
					dojo.stopEvent(ev);
					dojo.byId(this.sId + '-searchdate').fireEvent("onclick");
				} else {
					this._showDatepicker(dojo.isIE ? dojo.mixin({}, ev) : ev);
				}
			}
		}));
	},
	
	_changeInputField: function() {
		this._isDateSelected = (this._getSelectedColumn().type == "date");
		if (this._isDateSelected && dojo.hasClass(dojo.byId(this.sId + '-searchdate'), "s-nodisplay")) {
			dojo.byId(this.sId + '-date-input').value = dojo.byId(this.sId + '-searchtext').value;
		} else if (!this._isDateSelected && dojo.hasClass(dojo.byId(this.sId + '-searchtext'), "s-nodisplay")) {
			dojo.byId(this.sId + '-searchtext').value = dojo.byId(this.sId + '-date-input').value;
		}
		dojo.removeClass(dojo.byId(this.sId + (this._isDateSelected ? '-searchdate' : '-searchtext')), "s-nodisplay");
		dojo.addClass(dojo.byId(this.sId + (this._isDateSelected ? '-searchtext' : '-searchdate')), "s-nodisplay");
	},
	
	_getSelectedColumn: function() {
		return this.context.sortInfo.sortCols[dojo.byId(this.sId + '-column-select').value];
	},
	
	_getInputField: function() {
		return dojo.byId(this.sId + (this._isDateSelected ? '-date-input' : '-searchtext'));
	},
	
	_generateDialogHtml: function() {
		var sHtml = '<div id="'+ this.sId + '-body" style="padding: 0px 6px;">'
		+ '<div class="s-label-light" style="padding-top: 6px;">'
		+ '<label for="' + this.sId + '-column-select">' + this._smsgs["L_STARTSWITH_COLUMN"] + '</label>'
		+ '</div>'
		+ '<div id="'+ this.sId + '-column" style="width: 100%;">'
		+ '<select id="'+ this.sId + '-column-select" class="s-label-light" style="width: 100%;">';
		var sortCols = this.context.sortInfo.sortCols;
		for (var i = 0; i < sortCols.length; i++) {
			sHtml += '<option value="' + i + '">' + sortCols[i].label + '</option>';
		}
		sHtml += '</select>'
		+ '</div>'
		+ '<div class="s-label-light" style="padding-top: 8px;">'
		+ '<label for="' + this.sId + '-searchtext">' + this._smsgs["L_STARTSWITH_SEARCH"] + '</label>'
		+ '</div>'
		+ '<div id="'+ this.sId + '-search">'
		+ '<table id="' + this.sId + '-searchdate" class="s-nodisplay" style="border-width:0px;" cellpadding="0" cellspacing="0"><tbody><tr id="' + this.sId + '-searchrow">'
		+ '<td style="width:100%;border: solid gray 1px;background-color:#ffffff">'
		+ '<input style="border: 0px none; padding: 2px; width: 96%;" class="s-label-light" id="' + this.sId + '-date-input"/>'
		+ '<td unselectable="on" id="' + this.sId + '-date-down" style="border-top:1px solid gray;border-right:1px solid gray;border-bottom:1px solid gray;cursor:pointer;" class="s-toolbar-text">'
		+ '<div unselectable="on" style="position:relative;width:5px;height:3px;overflow:hidden">'
		+ '<img unselectable="on" alt="show-datepicker-icon" src="' + dojo.moduleUrl("dwa.common", "images/basicicons.gif") + '" style="display:block;top:-20px;left:-20px;position:absolute;border-width:0px;"/>'
		+ '</div></td>'
		+ '</tr></tbody></table>'
		+ '<input id="'+ this.sId + '-searchtext" title="title" class="s-label-light" style="width: 98%;" type="text" tabindex="0"/>'
		+ '</div>'
		+ '<div id="'+ this.sId + '-buttons" style="position: absolute; bottom: 0px; right: 0px;">';
		var sOk = '<input id="'+ this.sId + '-ok" class="s-basic-buttons" value="' + this._smsgs["L_SEARCH"] + '" type="button"/>';
		var sCancel = '<input id="'+ this.sId + '-cancel" class="s-basic-buttons" value="' + this._smsgs["L_CANCEL"] + '" type="button"/>';
		sHtml += !this.isRTL ? sOk + '&nbsp;' + sCancel : sCancel+ '&nbsp;' + sOk;
		sHtml += '</div></div>';
		return sHtml;
	}
});
