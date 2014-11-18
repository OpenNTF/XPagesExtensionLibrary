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

dojo.provide("dwa.lv.virtualScrollBar");

dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.benriFuncs");

dojo.declare(
	"dwa.lv.virtualScrollBar",
	null,
{
	constructor: function(oArg){
	    this.nTotal = 0;
	    this.nRow = 10;
	    this.nPos = 0;
	    
	    this.bInited = false;
	    this.oScrArea = null;
	    this.oScrBar = null;
	    this.oScrSpan = null;
	
	    this.nScrollBarWidth = 0;
	
	    this.hTimer = null;
	    this.fnCBOnScroll = null;
	    this.fnCBOnScrollHint = null;
	    this.fnCBOnBeforeScroll = null;
	    this.nCurrentIndex = 0;
	    
	    this.nPrevHeight = 0;
	
	    this.nCountProcess = 0;
	
	    this.nOrgPos = 0;
	    this.nNewPos = void 0;
	    this.sLastEvent = '';
	
	    this.oArg = oArg;
	},
	getScrollAreaWidth: function(){
	    //     Win     Mac     Linux
	    // IE  16-17-B ---
	    // FF2 16-16   17-17   17-17
	    // FF3 17-17   17-17   17-17   -> need to be 17 not to collapse on resize
	    // Saf 16-16   16-16   16-16
	 if( dojo.isMozilla ){
	    return (((navigator.userAgent.match(/Mac|iPhone/i)) || (-1 != navigator.userAgent.indexOf("Linux"))) && dwa.lv.globals.get().nIntBrowserVer < 1.09) || (dwa.lv.globals.get().nIntBrowserVer >= 1.09) ? (dwa.lv.benriFuncs.getScrollBarWidth() + 1) : dwa.lv.benriFuncs.getScrollBarWidth();
	 }else{ // G
	    return dwa.lv.benriFuncs.getScrollBarWidth();
	 } // end - IS
	},
	init: function(){
	    if(this.bInited)
	        return;
	
	    this.bInited = true;
	    var oThis = this;
	    setTimeout(function(){oThis.updateByTimeout()}, 0);
	},
	destroy: function(){
	    this.oScrArea = null;
	    this.oScrBar = null;
	    this.oScrSpan = null;
	},
	appendChild: function(oParent, oElemSizeTo){
	    // reset initialize status if regenerated elements
	    this.bInited = false;
	
	    this.oElemSizeTo = oElemSizeTo ? oElemSizeTo : oParent;
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	    oParent.style.verticalAlign = 'top';
	 } // end - GS
	
	    this.oScrArea = dwa.lv.benriFuncs.elGetOwnerDoc(oParent).createElement("DIV");
	    with (this.oScrArea.style){
	        //     Win     Mac     Linux
	        // IE  16-17-B ---
	        // FF2 16-16   17-17   17-17
	        // FF3 17-17   17-17   17-17   -> need to be 17 not to collapse on resize
	        // Saf 16-16   16-16   16-16
	        width       = dwa.lv.virtualScrollBar.prototype.getScrollAreaWidth() + 'px';
	        height      = "100%";
	        overflow   = "hidden";
	        position   = "relative";
	    }
	    this.oScrArea = oParent.appendChild(this.oScrArea);
	
	    this.oScrBar = dwa.lv.benriFuncs.elGetOwnerDoc(oParent).createElement("DIV");
	 if( dojo.isIE ){
	    this.oScrBar.dir = (dojo._isBodyLtr() ? "rtl" : "ltr" );
	 } // end - I
	    with (this.oScrBar.style){
	        position = "relative";
	 if( dojo.isMozilla || dojo.isWebKit ){
	        overflowX   = "hidden";
	        overflowY   = "auto";
	 }else{ // GS
	        overflowX   = "hidden";
	        overflowY   = "scroll";
	 } // end - I
	        height = "100%";
	        //     Win     Mac     Linux
	        // IE  16-17-B ---
	        // FF2 16-16   17-17   17-17
	        // FF3 17-17   17-17   17-17   -> need to be 17 not to collapse on resize
	        // Saf 16-16   16-16   16-16
	 if( dojo.isMozilla ){
	        width = ((((navigator.userAgent.match(/Mac|iPhone/i)) || (-1 != navigator.userAgent.indexOf("Linux"))) && dwa.lv.globals.get().nIntBrowserVer < 1.09) || (dwa.lv.globals.get().nIntBrowserVer >= 1.09) ? (dwa.lv.benriFuncs.getScrollBarWidth() + 1) : dwa.lv.benriFuncs.getScrollBarWidth()) + 'px';
	 }else if( dojo.isWebKit ){ // G
	        width = dwa.lv.benriFuncs.getScrollBarWidth() + 'px';
	 }else{ // S
	        width = (dwa.lv.benriFuncs.getScrollBarWidth() + 1) + 'px';
	 } // end - I
	 if( dojo.isWebKit ){
	        webkitBoxSizing = "content-box";
	 } // end - S
	    }
	    this.oScrBar.style[ (dojo._isBodyLtr() ? "left" : "right") ] = '0px';
	    // to not focus on scrollbar by tab key navigation
	    this.oScrBar.tabIndex = -1;
	    this.oScrBar = this.oScrArea.appendChild(this.oScrBar);
	
	    this.oScrSpan = this.oScrBar.appendChild(dwa.lv.benriFuncs.elGetOwnerDoc(oParent).createElement("DIV"));
	    with (this.oScrSpan.style){
	        position = "absolute";
	        width  = "1px";
	        height = "0px";
	    }
	
	    this.update(true);
	
	    var oThis = this;
	 if( dojo.isIE ){
	    this.oScrBar.attachEvent('onscroll', function(ev){return oThis.onScroll(ev);});
	    this.oScrBar.attachEvent('onclick', function(ev){(dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );(dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );});
	    this.oScrBar.attachEvent('ondblclick', function(ev){(dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );(dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );});
	 }else{ // I
	    this.oScrBar.addEventListener('scroll', function(ev){return oThis.onScroll(ev);}, false);
	    this.oScrBar.addEventListener('click', function(ev){(dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );(dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );}, false);
	    this.oScrBar.addEventListener('dblclick', function(ev){(dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );(dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );}, false);
	 } // end - GS
	    this.oElemSizeTo.nId = this.id;
	    this.oScrArea.nId = this.id;
	    this.oScrBar.nId = this.id;
	    
	    return this.oScrArea;
	},
	updateAll: function(nTotal, nRow, nPos, bNeedEvent){
	    var bHasChanged = this.nTotal != nTotal;
	    bHasChanged = this.nRow != nRow ? true : bHasChanged;
	    bHasChanged = this.nPos != nPos ? true : bHasChanged;
	    bHasChanged = this.nPrevHeight != this.getRealHeight() ? true : bHasChanged;
	    this.nTotal = nTotal;
	    this.nRow = nRow;
	    this.nPos = nPos;
	    if(bHasChanged)
	        this.update(true, bNeedEvent);
	    // to not show scroll hint on refresh
	    this.nOrgPos = this.nPos;
	},
	setNumRows: function(nRow){
	    var bHasChanged = this.nRow != nRow;
	    this.nRow = nRow;
	    if(bHasChanged)
	        this.update(true);
	},
	setNumEntries: function(nTotal){
	    var bHasChanged = this.nTotal != nTotal;
	    this.nTotal = nTotal;
	    if(bHasChanged)
	        this.update();
	},
	getEntryHeight: function(){
	    if (!this.nRow) return 0;
	    return dwa.lv.benriFuncs.elGetCurrentStyle(this.oScrBar, 'height', true) / this.nRow;
	},
	getCurrentPos: function(){
	    var nEntryHeight = this.getEntryHeight();
	    return Math.floor((this.oScrBar.scrollTop + nEntryHeight / 2) / nEntryHeight);
	},
	getOldPos: function(){
	    return this.nOrgPos;
	},
	calcNewScrollTop: function(newPos){
	    var nEntryHeight = this.getEntryHeight();
	    return nEntryHeight * newPos;
	},
	setPos: function(nPos, bNeedEvent){
	    var bHasChanged = this.nPos != nPos;
	    this.nPos = nPos;
	    if(bHasChanged)
	        this.update(true, bNeedEvent);
	},
	getHeight: function(){
	    return this.getRealHeight();
	},
	getRealHeight: function(){
	    var oParent = this.oScrBar.parentNode;
	    this.nCountProcess ++;
	    var nHeight = dwa.lv.benriFuncs.elGetCurrentStyle(oParent, 'height', true);
	    this.nCountProcess --;
	    return nHeight;
	},
	isTop: function(){
	    return this.getCurrentPos() == 0;
	},
	isBottom: function(){
	    return (this.getCurrentPos() + this.nRow) >= this.nTotal;
	},
	update: function(bChangePos, bNeedEvent){
	    if ( !this.oScrBar )
	        return;
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	    if (this.oScrSpan) {
	        // workaround for disappearance of scrollbar in Mozilla
	        this.oScrSpan.style.position = "relative";
	        this.oScrSpan.style.position = "absolute";
	    }
	 } // end - GS
	
	    this.init();
	
	    var nEntryHeight = this.nRow ? (this.getRealHeight() / this.nRow) : 0;
	
	    var areaHeight = this.nTotal * nEntryHeight;
	    this.oScrSpan.style.height = areaHeight + 'px';
	    if(bChangePos)
	    {
	        if(!bNeedEvent) this.nCountProcess ++;
	        this.oScrBar.scrollTop = this.nPos * nEntryHeight;
	        if(!bNeedEvent) {
	            // FF and IE does not fire onscroll event immediately
	            var oThis = this;
	            setTimeout(function (){oThis.nCountProcess--}, 100);
	        }
	    }
	    
	    this.nPrevHeight = this.getRealHeight();
	},
	updateVisibility: function(){
	    var bDisable = !(this.nPos || this.nRow < this.nTotal);
	    this.oScrBar.parentNode.style.visibility = (bDisable ? 'hidden' : '');
	},
	setCBOnScroll: function(fnCB){
	    this.fnCBOnScroll = fnCB;
	},
	setCBOnScrollHint: function(fnCB){
	    this.fnCBOnScrollHint = fnCB;
	},
	updateByTimeout: function(){
	    this.hTimerResize = null;
	    this.update(true);
	},
	onScroll: function(ev){
	    if(this.nCountProcess)
	        return;
	
	    this.sLastEvent = 'DROPED';
	    this.nNewPos = this.getCurrentPos();
	    if(this.hTimer)
	        clearTimeout(this.hTimer);
	    var oThis = this;
	    this.hTimer = setTimeout(function(){oThis.onTimeout()}, 200);
	    if(this.fnCBOnScrollHint)
	        this.fnCBOnScrollHint(this.oArg);
	    this.nOrgPos = this.getCurrentPos();
	},
	onTimeout: function(){
	    this.nCountProcess ++;
	    this.oScrBar.scrollTop = this.calcNewScrollTop(this.nNewPos);
	    this.nCountProcess --;
	    this.nPos = this.nNewPos;
	    this.hTimer = null;
	    this.notifyOnScroll();
	    this.nOrgPos = this.getCurrentPos();
	    this.nNewPos = void 0;
	},
	notifyOnScroll: function(){
	    this.nCountProcess ++;
	    if(this.fnCBOnScroll)
	        this.fnCBOnScroll(this.sLastEvent, this.getCurrentPos(), this.oArg);
	    this.nCountProcess --;
	},
	doScroll: function(sEvent, bNeedEvent){
	    this.nOrgPos = this.getCurrentPos();
	    switch(sEvent)
	    {
	        case 'TOP':
	            this.nNewPos = 0;
	            break;
	        case 'BOTTOM':
	            this.nNewPos = this.nTotal - this.nRow;
	            break;
	        case 'DOWN':
	            this.nNewPos = this.nOrgPos + 1;
	            break;
	        case 'UP':
	            this.nNewPos = this.nOrgPos - 1;
	            break;
	        case 'PAGEDOWN':
	            this.nNewPos = this.nOrgPos + this.nRow;
	            break;
	        case 'PAGEUP':
	            this.nNewPos = this.nOrgPos - this.nRow;
	            break;
	        default:
	            return false;
	    }
	
	    if(this.nNewPos < 0)
	        this.nNewPos = 0;
	    if(this.nNewPos + this.nRow > this.nTotal)
	        this.nNewPos = this.nTotal - this.nRow;
	
	    this.sLastEvent = sEvent;
	    this.setPos(this.nNewPos, bNeedEvent);
	    
	    // return bool to cancel/propagate mouse event to body scroll
	    var bReturn = this.nNewPos != this.nOrgPos;
	    this.nOrgPos = this.getCurrentPos();
	    return bReturn;
	}
});
