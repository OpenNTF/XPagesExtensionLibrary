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

dojo.provide("dwa.common.dropdownManager");

dojo.declare(
	"dwa.common.dropdownManager",
	null,
{
	isRTL: false,

	constructor: function(sId){
		if(!dojo._isBodyLtr()){
			this.isRTL = true;
		}
		this.sId = sId;
		this.oHtml = {};
		this.oStateChanged = {};
		this.oMenuFeeder = {};
		this.oDropdownMenus = {};
		if(!dojo.isMozilla && !dojo.isWebKit){
			this.oCover = dojo.doc.createElement('iframe');
			this.oCover.id = 'e-dropdown-cover';
			this.oCover.src = 'about:blank';
			this.oCover.className = 's-popup';
			this.oCover.style.borderWidth = '0px';
			this.oCover.style.zIndex = 200;
			this.oCover.style.width = "400px";
			dwa.common.utils.cssEditClassExistence(this.oCover, 's-nodisplay', true);
		}
		this.aoActiveDrops = [];
		this.nOrient = 0;
		this.nCurrentLevel = 0;
		dojo.connect(dojo.doc.body, 'click', this, "hide");
		dojo.connect(dojo.doc.body, 'keyup', this, "handleKey");
	},
	getDropdownMenu: function(){
		//var oDrop = this.aoActiveDrops[this.aoActiveDrops.length - 1];
		//return oDrop ? com_ibm_dwa_io_widgetListener.prototype.oWidgets[oDrop.id + ':com_ibm_dwa_ui_dropdownMenu'] : void 0;
	},
	handleKey: function(ev){
		if (this.fDisableDropdownMenuKey)
			return;
		var oDropdownMenu = this.getDropdownMenu();
		if (oDropdownMenu)
			return oDropdownMenu.handleKey(ev);
	},
	setPos: function(ev, oElem, fOppositeEdge, fCenter){
		this.fOppositeEdge = !!fOppositeEdge;
	
		oElem = !oElem ? ev.target : oElem;
	
		this.fCenter = !!fCenter;
		this.nContainerWidth = oElem.offsetWidth;
		this.nContainerHeight = oElem.offsetHeight;
	
		var nLeftDiff0 = !this.isRTL ? (oElem.scrollLeft - oElem.clientLeft) :
		 ((oElem.scrollWidth - oElem.scrollLeft) - (oElem.clientWidth - oElem.clientLeft));
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			var nLeftDiff1 = !this.isRTL ? (ev.clientX - ev.offsetX) :
			 (dojo.doc.body.clientWidth - ev.clientX - (oElem.offsetWidth - ev.offsetX));
			// SPR #KNAA84Q9RZ (Quickr8.5)
			// datepicker position is incorrect on IE when scroll bar is scrolled
			var oViewport = dijit.getViewport();
			var nLeftDiff2 = (dojo.doc.compatMode == "CSS1Compat" || !this.isRTL) ? Math.abs(oViewport.l) : dojo.doc.body.scrollWidth - dojo.doc.body.clientWidth - dojo.doc.body.scrollLeft;
			var nTopDiff = (dojo.doc.compatMode == "CSS1Compat" ? oViewport.t : 0);
			this.oPos = new dwa.common.utils.pos(nLeftDiff2 + nLeftDiff1 + nLeftDiff0, nTopDiff + ev.clientY - ev.offsetY + oElem.scrollTop - oElem.clientTop);
		// SPR #KNAA8267ZJ (Quickr8.5)
		// offsetLeft of span tag is not correct in bidi locale when appending child nodes to span tag dynamically.
		// Maybe below codes work correctly in no bidi locale.
		} else if(3 <= dojo.isMozilla && this.isRTL) {
			var x = dojo.doc.body.offsetWidth - Math.round(oElem.getBoundingClientRect().right) - dojo.doc.body.parentNode.scrollLeft;
			var y = Math.round(oElem.getBoundingClientRect().top) + dojo.doc.body.parentNode.scrollTop;
			this.oPos = new dwa.common.utils.pos(x, y);
		}else{
			if(dojo.isWebKit){
				// !!! Safari bug? !!!
				// Safari returns wrong offsetTop for the TD including only IMG tag. (looks like offset of the IMG)
				// Currently only action menu dropdown is the case and IE version logic works for it.
				if(oElem.id.search(/^e-dropdown.*button$/) != -1) {
					var nLeftDiff1 = !this.isRTL ? (ev.clientX - ev.offsetX) :
					 (dojo.doc.body.clientWidth - ev.clientX - (oElem.offsetWidth - ev.offsetX));
					this.oPos = new dwa.common.utils.pos(nLeftDiff1 + nLeftDiff0, ev.clientY - ev.offsetY + oElem.scrollTop - oElem.clientTop);
					 return;
				}
			}
			var fUseLayer = !this.isRTL && !this.fIgnoreLayer && typeof(ev.clientX) != 'undefined' && typeof(ev.layerX) != 'undefined'
			 && typeof(ev.clientY) != 'undefined' && typeof(ev.layerY) != 'undefined';

			this.oPos = fUseLayer ? new dwa.common.utils.pos(ev.clientX - ev.layerX, ev.clientY - ev.layerY) : new dwa.common.utils.pos(0, 0);

			for (var oDocument = oElem.ownerDocument, oTrace = oElem;
			 !fUseLayer || oDocument.defaultView.getComputedStyle(oTrace, null).getPropertyValue('position').toLowerCase() != 'absolute';
			 oTrace = oTrace.offsetParent) {
				this.oPos.x += !this.isRTL ? oTrace.offsetLeft : ((oTrace.offsetParent? oTrace.offsetParent: oTrace.parentNode).offsetWidth - oTrace.offsetWidth - oTrace.offsetLeft);
				this.oPos.y += oTrace.offsetTop - oTrace.scrollTop;
				if (!oTrace.offsetParent || oTrace == oTrace.offsetParent)
					break;
			}
		}
	},
	hide: function(ev, nIndex){
		if(dojo.isMozilla || dojo.isWebKit){
			// In Mac, context menu is shown Ctrl-MouseDown. When mouse is up right then, click event is fired up and hides drop down menu
			// Part of fix for #SLPI7CPB9T - asudoh 7/22/2008
			if ((navigator.userAgent.match(/Mac|iPhone/i)) && ev && ev.ctrlKey)
				return;
		}
	
		for (var i = this.aoActiveDrops.length - 1; i >= 0; i--) {
			if (typeof(nIndex) == 'number' && nIndex != i)
				continue;
	
			var oDrop = this.aoActiveDrops[i];
			var fActive = !(/s\-hidden/i).test(oDrop.className);
			dwa.common.utils.cssEditClassExistence(oDrop, 's-hidden', true);
			if(dojo.isMozilla){
				// SPR DYHG76H4XC - we have to hide the scrollbar first before we hide the menu, otherwise it will persist (Firefox/Mac)
				dwa.common.utils.cssEditClassExistence(oDrop, 's-menu-overflow-hidden', true);
			}
	
			// SPR # NBJC76X8F7 - we have to move the dropdown div out of sight
			// so that it will not block the focus of other elements
			// I have tried setting the zindex to -100, but not working.
			// Another incident found by NBJC7DC9BP for IE as well in 8.5
			oDrop.style.top = -1 * oDrop.offsetHeight + "px";

			if (this.oStateChanged[oDrop.id] && fActive)
				this.oStateChanged[oDrop.id](false);
	
			this.aoActiveDrops.splice(i, 1);
			if (this.nCurrentLevel > 0) {
				this.nCurrentLevel--;
			}
		}
		if(!dojo.isMozilla && !dojo.isWebKit){
			dwa.common.utils.cssEditClassExistence(this.oCover, 's-nodisplay', true);
		}
	},
	show: function(ev, sId, fNoToggle, fAdd){
		function com_ibm_dwa_ui_dropdownManager_show_compare(oElem1, oElem2){
			return oElem1 == oElem2 && oElem1.className.search(/s-hidden/) == -1;
		}
	
		var oElem = dojo.doc.getElementById(sId);
		var nIndex = dwa.common.utils.indexOf(this.aoActiveDrops, oElem, com_ibm_dwa_ui_dropdownManager_show_compare);
	
		if (!oElem) {
			var oDiv = dojo.doc.createElement('div');
			oDiv.innerHTML = this.oHtml[sId];
			oElem = oDiv.firstChild;
		}
	
//		var fHide = !fNoToggle && !fAdd && nIndex >= 0;
//		if ((fHide || nIndex < 0) && !fAdd)
//			this.hide(ev);
	
//		if (fHide)
//			return;
	
		this.aoActiveDrops.push(oElem);
	
		if (nIndex < 0) {
//			dwa.common.utils.cssEditClassExistence(oElem, 's-hidden', true);
	
			if(dojo.isMozilla){
				// SPR DYHG76H4XC - we have to hide the scrollbar first before we hide the menu, otherwise it will persist (Firefox/Mac)
//				dwa.common.utils.cssEditClassExistence(oElem, 's-menu-overflow-hidden', false);
			}
		}
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			if (!dojo.doc.getElementById(this.oCover.id))
				dojo.doc.body.appendChild(this.oCover);
		}
	
		if (!dojo.doc.getElementById(oElem.id))
			dojo.doc.body.appendChild(oElem);
	
//		com_ibm_dwa_ui_loadWidgets(oElem);
	
//		var sWidgetId = oElem.id + ':' + oElem.getAttribute('com_ibm_dwa_ui_widget_class');
//		var oWidget = com_ibm_dwa_io_widgetListener.prototype.oWidgets[sWidgetId];
		var oWidget = dijit.byNode(oElem);
		if (nIndex < 0 && oWidget)
			oWidget.activate(this);
	
//		// Code specific for non-classic iNotes - Keep track of the sequence of showing dropdown menu (DMS stands for: Dropdown Manager Show)
//		gCookie.DMS = ++com_ibm_dwa_ui_dropdownManager.prototype.nSeq;
//		gCookie.store();
	
		if (ev){
//			ev.stopPropagation();
			if(!dojo.isMozilla && !dojo.isWebKit){
				ev.cancelBubble = true;
			}else{
				ev.stopPropagation()
			}
		}
	},
	nSeq: 0
});

dwa.common.dropdownManager.get = function dwa_widget_dropdownManager_get(sId){
	if(!dwa.common.dropdownManager.oManagers){ dwa.common.dropdownManager.oManagers = {}; }; // kami
	return dwa.common.dropdownManager.oManagers[sId] ? dwa.common.dropdownManager.oManagers[sId] :
	 (dwa.common.dropdownManager.oManagers[sId] = new dwa.common.dropdownManager(sId));
};
