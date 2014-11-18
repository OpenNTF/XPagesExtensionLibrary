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

dojo.provide("dwa.common.dropdownBox");

var D_ALIGN_DEFAULT = "left";

dojo.declare(
	"dwa.common.dropdownBox",
	null,
{
	isRTL: false,
	
	constructor: function(sId){
		if(!dojo._isBodyLtr()){
			this.isRTL = true;
		}
		if(this.isRTL){
			D_ALIGN_DEFAULT = "right";
		}
		if (!sId)
			return;
	
		this.sId = sId;	
		var oManagers = dwa.common.dropdownManager.oManagers;
		for (var s in oManagers) {
			for (var aoActiveDrops = oManagers[s].aoActiveDrops, i = 0; i < aoActiveDrops.length; i++) {
				if (aoActiveDrops[i].id == this.sId && (/s\-hidden/i).test(aoActiveDrops[i].className))
					this.activate(oManagers[s]);
			}
		}
	},
	activate: function(oDropdownManager){
		// Clear the reference to active child drop down menu as there should be no such child menu when a drop down menu is activated
		// Keeping such "ghose" misleads com_ibm_dwa_ui_dropdownMenu_setActive() to hide a wrong drop down menu
		// SPR #YHAO7CLEVF - asudoh 6/2/2008
		this.sChild = '';
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			dwa.common.utils.cssEditClassExistence(oDropdownManager.oCover, 's-nodisplay', false);
		}
	
		var oElem = dojo.doc.getElementById(this.sId);
	
		var oReverse = {
			2: 0,
			3: 1,
			0: 2,
			1: 3
		};
		var oBody = dojo.doc.body;
		var fHorizontal = oDropdownManager.nOrient == 1 || oDropdownManager.nOrient == 3;
		var fOppositeEdge = oDropdownManager.fOppositeEdge;
		var nContainerLength = !oDropdownManager.fCenter ? 0 : (oDropdownManager[!fHorizontal ? 'nContainerWidth' : 'nContainerHeight']);
	
		var nLeft;
		var nTop;
		var fFlipped;
	
		if(!dojo.isMozilla){
			var nBodyWidth = oBody.clientWidth;
		}else{
			// Gecko browsers appears to shrink "floated" elements to some extent from the right edge
			var nBodyWidth = oBody.clientWidth - 16;
		}
	
		for (var anOrient = [oDropdownManager.nOrient, oReverse[oDropdownManager.nOrient]], i = 0; i < anOrient.length; i++) {
			var nOrient = anOrient[i];
	
			// The width or height of drop down box for centering the drop down box
			var nDropLength = nOrient == !fHorizontal ? oElem.offsetWidth : oElem.offsetHeight;
	
			// Centering adjustment width, by the difference in the width of the referred element and the drop down box
			var nAdjustCenterWidth = oDropdownManager.fCenter && !fHorizontal ? Math.floor((nContainerLength - nDropLength) / 2) : 0;
			// Centering adjustment height, by the difference in the height of the referred element and the drop down box
			var nAdjustCenterHeight = oDropdownManager.fCenter && fHorizontal ? Math.floor((nContainerLength - nDropLength) / 2) : 0;
	
			// Move the left by the width of drop down box and the centering adjustment, as needed
			var nAdjustWidth = nOrient == 1 || nOrient != 3 && fOppositeEdge ?
			 oElem.offsetWidth + nAdjustCenterWidth : -nAdjustCenterWidth;
			// Move the top by the height of drop down box and the centering adjustment, as needed
			var nAdjustHeight = nOrient == 2 || nOrient != 0 && fOppositeEdge ?
			 oElem.offsetHeight + nAdjustCenterHeight : -nAdjustCenterHeight;
	
			nLeft = oDropdownManager.oPos.x - nAdjustWidth
			 + (nOrient == 3 || nOrient != 1 && fOppositeEdge ? oDropdownManager.nContainerWidth : 0)
			 + (oDropdownManager.oOffset ? oDropdownManager.oOffset.x : 0);
			nTop = oDropdownManager.oPos.y - nAdjustHeight
			 + (nOrient == 0 || nOrient != 2 && fOppositeEdge ? oDropdownManager.nContainerHeight : 0)
			 + (oDropdownManager.oOffset ? oDropdownManager.oOffset.y : 0);
	
			// If the drop down box goes out of body in the same direction as the drop down box's direction, flip the drop down box's direction
			if (!fFlipped && !this.fNoFlip)
				fFlipped = (nLeft < 0 || nLeft + oElem.offsetWidth > nBodyWidth) && fHorizontal
				 || (nTop < 0 || nTop + oElem.offsetHeight > oBody.clientHeight) && !fHorizontal;
			if (!fFlipped)
				break;
		}
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			nTop += oBody.scrollTop;
		}

		nLeft = nLeft + oElem.offsetWidth < nBodyWidth && nLeft >= 0 ? nLeft :
		 nLeft >= 0 && (!fHorizontal || nBodyWidth >= oElem.offsetWidth) ? (nBodyWidth - oElem.offsetWidth) :
		 0;
		nTop = nTop + oElem.offsetHeight < oBody.clientHeight && nTop >= 0 ? nTop :
		 nTop >= 0 && (fHorizontal || oBody.clientHeight >= oElem.offsetHeight) ? (oBody.clientHeight - oElem.offsetHeight) :
		 0;
	
		oElem.style[D_ALIGN_DEFAULT] = nLeft + 'px';
		oElem.style.top = nTop + 'px';
		oElem.style.zIndex = 201 + oDropdownManager.nCurrentLevel;
	
		dwa.common.utils.cssEditClassExistence(oElem, 's-hidden', false);
	
		if(!dojo.isMozilla && !dojo.isWebKit){
			var oMargins = oDropdownManager.oMargins ? oDropdownManager.oMargins : {top: 0, right: 0, bottom: 0, left: 0};
			var q = (dojo.doc.compatMode == "CSS1Compat" && dojo.isIE <= 8) ? 8 : 0; // kami (iframe has 8px padding)
			var oCoords = {
				top: nTop + oMargins.top + 'px',
				width: oElem.offsetWidth - oMargins.left - oMargins.right - q + 'px',
				height: oElem.offsetHeight - oMargins.top - oMargins.bottom - q + 'px'
			};
			oCoords[D_ALIGN_DEFAULT] = nLeft + oMargins.left + 'px';

			for (var s in oCoords)
				oDropdownManager.oCover.style[s] = oCoords[s];
		}
	
		if (oDropdownManager.oStateChanged[this.sId])
			oDropdownManager.oStateChanged[this.sId](true);
	
		// reset highlight of drop down menu in the second time (SPR PTHN7DLMPT)
		if (this.fInitialized && this.setActive && oElem.getAttribute('com_ibm_dwa_ui_dropdownMenu_resetActive') != 'false')
			this.setActive(this.nActiveIndex ? this.nActiveIndex : 0);
	
		if(this.focus)
			this.focus();
	
		this.fInitialized = true;
	},
	deactivate: function(fAll){
		var oManagers = dwa.common.dropdownManager.oManagers;
		for (var s in oManagers) {
			var nIndex
			 = dwa.common.utils.indexOf(oManagers[s].aoActiveDrops, this, function(oElem, oWidget){ return oElem.id == oWidget.sId; });
			var oDrop = oManagers[s].aoActiveDrops[nIndex];
			if (oDrop) {
				oManagers[s].hide(null, !fAll ? nIndex : void 0);
				if(dojo.isMozilla || dojo.isWebKit){
					for (var i = 0; i < oManagers[s].aoActiveDrops.length; i++) {
						if (fAll || i == nIndex)
							dwa.common.utils.cssEditClassExistence(oDrop, 's-menu-overflow-hidden', true);
					}
				}
			}
		}
		if(this.blur)
			this.blur();
	},
	getManagers: function(){
		var aoManagers = [];
		var oManagers = dwa.common.dropdownManager.oManagers;
		for (var s in oManagers) {
			if (oManagers[s].oHtml[this.sId])
				aoManagers.push(oManagers[s]);
		}
		return aoManagers;
	}
});
