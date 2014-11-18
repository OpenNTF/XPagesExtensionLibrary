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

dojo.provide("dwa.lv.virtualList");

dojo.require("dwa.lv.listViewBase");
dojo.require("dwa.lv.virtualScrollBar");
dojo.require("dwa.lv.dragdropControl");
dojo.require("dwa.lv.benriFuncs");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.columnInfo");
dojo.require("dwa.lv.autoConsolidatedImageListener");
dojo.require("dwa.lv.autoConsolidatedImageListenerA11y"); // nakakura
dojo.require("dwa.common.utils");
dojo.require("dwa.lv.miscs");
dojo.require("dwa.lv.colpos");
dojo.require("dwa.lv.treeViewCollection");
dojo.require("dwa.lv.treeViewEntry");
dojo.require("dwa.common.graphics");

dojo.declare(
	"dwa.lv.virtualList",
	dwa.lv.listViewBase,
{
	store: null,

	init: function(sContainerId    ,nCols          ,bAltRowClr     ,nColHdrOffset  ,sColHdrID          ,bContainerHasAbsHeight    ,sTargetTumbler,bShowUnread    ){
	    
	
	    dwa.lv.listViewBase.prototype.init.call(
	        this
	        ,sContainerId
	        ,nCols
	        ,bAltRowClr
	        ,nColHdrOffset
	        ,sColHdrID
	        ,bContainerHasAbsHeight
	        ,sTargetTumbler
	        ,bShowUnread
	    );
	
	    // misc
	    this.isFlat = false;
	    this.topVisibleRow = 0;
	    this.iScrollTop = 0;
	    this.bProcessingSearch = false;
	    // use this flag to force data replacement on a jumpTo
	    this.bInvalidData = false;
	    this.iCurrentPosition = 0;
	    this.totalEntries = -1;
	
	    this.aSelected    = null;
	    this.iSelectedPosition = 0;
	    this.sSelectedTumbler = '';
	    this.invalidateForcedCaretMoving();
	    this.bResetObjects=1;
	    this.bResetMinRowHeight=1;
	
	    // dwa.lv.virtualList Objects
	    this.header = void 0;
	    try {
	    	this.listTd = void 0;
	    } catch (e) {
	    	console.log("dwa.lv.virtualList: " + e.toString());
	    }
	    this.tbl = void 0;
	    this.tblContainer = void 0;
	    this.hintText = void 0;
	 if( dojo.isMozilla ){
	    this.hintTextBinding = void 0;
	 } // end - G
	    this.hintBg = void 0;
	    this.vList = void 0;
	    this.scrBar = void 0;
	    this.wBox = void 0;
	    this.scrObj = void 0;
	
	    if(!this.bIsPrintMode)
	    {
	        this.oScrollBar = new dwa.lv.virtualScrollBar(this.sId);
	        var v$ = this;
	        this.oScrollBar.setCBOnScroll(function(ev){
                if( v$.oScrollBar.isBottom() && 'BOTTOM' !=ev ) ev='BOTTOM';
                return v$.onPosChanged(ev);
            });
	        this.oScrollBar.setCBOnScrollHint(function(ev){return v$.onScrollHint(ev);});
	    }
	    this.oRoot = this.getNewRoot();
	    this.bOnFirstLoadingProcess = true;
	
	    // for performance issue
	    // cached values for some functions
	    this.cachedViewPos = void 0;
	    this.cachedCalcTopRow = void 0;
	    this.cachedCalcBottomRow = void 0;
	
	    // Enabled if row was inserted to top or deleted from top
	    this.bCachedViewPosIsInvalid = true;
	    
	    // Enabled if this.tbl.style.top has been changed or resized
	    this.bCachedCalcTopRowIsInvalid = true;
	    this.bCachedCalcBottomRowIsInvalid = true;
	
	    this.sLastPath = '';
	
	    // data fetching
	    this.start      = '1';
	    this.count      = 20;
	    this.profileModifier = '_';
	    this.bHasWaitMsg = false;
	    this.bCtrlShiftOnMousedown = false;
	
	    this.hUpdatingViewTimer = null;
	
	    this.nScrollHintStatus = 0;
	    this.aScrollHints = [];
	    this.nScrollHintLastTime = 0;
	    this.nScrollHintCumulativeDiffs = 0;
	    this.sScrollHintLastHtml = '';
	
	    this.sUpdatingViewMessage = this._smsgs[ "L_UPDATINGVIEW_MESSAGE" ];
	
	    this.bCheckUnidForRefresh = false;
	
	    // init column data
	    this.initColumnArray(nCols);
	
	    // Initialize the Drag&Drop helper
	    dwa.lv.dragdropControl.prototype.getDragDropControlStatic().bEnabled = false;
	},
	destroy: function(bPreventRoot){
	    // ------------------------------------------
	    // destructor for this class
	    //  destroys all known arrays and objects
	    //  created with the "new" operator
	    // ------------------------------------------
	    var i;
	
	    this.oGoTo = null;
	    for(i=0;i<this.nColumnSpan;i++){ this.aDragBar[i]=null; }
	    this.aDragBar=null;
	    for(i=0;i<this.nColumnSpan;i++) { this.aColHdr[i]=null; }
	    this.aColHdr = null;
	    for(i=0;i<this.nColumnSpan;i++){ this.aColInfo[i]=null; }
	    this.aColInfo=null;
	
	    this.aSelected  = null;
	
	    this.oXmlData = null;
	    this.oTmpData = null;
	
	    this.wBox      = null;
	    this.scrObj    = null;
	
	    this.tbl     = null;
	    this.scrBar  = null;
	    this.vList   = null;
	    this.oColHdr  = null;
	    this.oNarrowHdr = null;
	    this.oContainer = null;
	    
	    if(this.oScrollBar)
	        this.oScrollBar.destroy();
	    if(!bPreventRoot)
	        this.destroyRoot();
	    this.releaseDD();
	
	},
	postInit: function(bForce){
	    this.updateOrgColumnWidths();
	    this.changeNarrowMode(this.needToBeNarrow(), true);
	
	
	    if(this.fnAfterGenerateContainer)
	        this.fnAfterGenerateContainer(this.sId);
	    
	    this.bPostInit = 1;
	    return true;
	},
	changeNarrowMode: function(bNewMode, bOnInit){
	    
	
	    var v$=this;
	    v$.bNarrowMode = bNewMode;
	    
	    var maxC=v$.nColumnSpan-1;
	    for(var i=0;i<=maxC;i++)
	        v$.aColInfo[i].nWidth = typeof(v$.getFixedColumnWidth(i))=='number' ? v$.getFixedColumnWidth(i) : v$.bNarrowMode ? Math.max(v$.getMinColumnWidth(i), v$.aColInfo[i].nOrgWidth) : v$.aColInfo[i].nOrgWidth;
	
	    if(!bOnInit) {
	        var nOldRow = v$.iSelectedPosition - v$.viewPos(0);
	        var nOldMaxRowsPerPage = v$.iMaxRowsPerPage;
	    }
	
	    v$.oContainer.innerHTML = v$.generateHTML();
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	 } // end - GS
	
	    if(!bOnInit)
	        v$.oGoTo = null;
	    v$.bResetMinRowHeight = 0;
	    v$.resetObjects(true);
	    v$.setGotoPosition();
	
	    if(!bOnInit) {
	        v$.aLayoutInfo = null;
	        v$.getLayoutInfo();
	    }
	
	    v$.bResetMinRowHeight = 1;
	    v$.calcMinRowHeight();
	
	    if(!bOnInit) {
	        v$.bCachedCalcTopRowIsInvalid = true;
	        v$.bCachedCalcBottomRowIsInvalid = true;
	
	        v$.iMaxRowsPerPage = 20;
	        v$.adjustCount();
	        v$.syncColSS(0,0,0);
	        if(nOldRow >= v$.iMaxRowsPerPage)
	            v$.iCurrentPosition += (nOldRow - v$.iMaxRowsPerPage + 1);
	
	        if(v$.fnNarrowStyleChanged)
	            v$.fnNarrowStyleChanged();
	
	        v$.refresh(void 0, void 0, void 0, void 0, true);
	    }
	
	    this.adjustElementSizes();
	    
	},
	needToBeNarrow: function(){
	    var v$=this;
	    
	    if(!v$.bCanBeNarrowMode)
	        return false;
	    var oLayoutInfo = v$.generateLayoutInfo(v$.aColInfo, v$.nColumnSpan, true);
	    var aRowInfo = v$.getSingleRowInfo(oLayoutInfo, 1);
	    if(!aRowInfo || !aRowInfo.length)
	        return false;
	
	    var nAvailWidth = v$.getListWidth();
	    for(var i=0;i<v$.nColumnSpan;i++)
	        nAvailWidth -= v$.aColInfo[i].nOrgWidth;
	
	    return nAvailWidth<0;
	},
	setUpdatingViewTimer: function(){
	    var v$ = this;
	    this.clearUpdatingViewTimer();
	    this.hUpdatingViewTimer = setTimeout(function(){v$.showUpdatingViewMessage()}, 5000);
	},
	clearUpdatingViewTimer: function(){
	    if(this.hUpdatingViewTimer)
	        clearTimeout(this.hUpdatingViewTimer);
	    this.hUpdatingViewTimer = null;
	},
	showUpdatingViewMessage: function(){
	    if(!this.tbl.childNodes || this.tbl.childNodes.length) return;
	    this.tbl.style.height = '100%';
	    this.tbl.innerHTML = '<table id="' + this.sId + '-message" width="100%" height="100%"><tr><td class="vl-updatingview-message">' + this.sUpdatingViewMessage.replace(/\n/g, '<br>') + '</td></tr></table>';
	},
	clearUpdatingViewMessage: function(){
	    this.clearUpdatingViewTimer();
	    var oMessage = dojo.doc.getElementById(this.sId + '-message');
	    if(oMessage) {
	        this.tbl.removeChild(oMessage);
	        this.tbl.style.height = '';
	    }
	},
	generateHTML: function(){
	    // ------------------------------------------
	    // returns the HTML necessary to show a 
	    // table of data with a scrollbar
	    // ------------------------------------------
	    
	    var s1,el,a1=[],n=0;
	    var v$=this;
	    v$.adjusting = false;
	    v$.bResetObjects=1;
	
	
	    // ===========================================
	    // VIRTUAL LIST CONTAINER and DATA TABLE
	    // ===========================================
	
	    // TABLE
	    // two columns, first is for data, second is for scrollbar
	    a1[n++]='<table id="'+v$.sId+'-vlist" idvl="'+v$.sId+'" style="border:0px" cellspacing="0" cellpadding="0" height="100%" width="100%"><tbody>';
	
	    // Insert Column Header into dwa.lv.virtualList
	    a1[n++]='<tr' + (v$.bShowHeader ? '' : ' style="display:none"') + '><td valign=top height=1 nowrap>'
	           + '<div id="'+v$.sId+'-header" class="s-basicpanel vl-column-header">';
	 if( dojo.isIE ){
	 }else{ // I
	 } // end - GS
	    a1[n++]= '<div nowrap id="' + v$.sId +'-columnheader" idvl="' + v$.sId + '" class="s-stack vl-column-header"'
	           + ' onselectstart="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	           + ' ondblclick="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '">'
	           + v$.generateHeaderContentsHTML()
	           + '</div>'
	           + '</div>'
	           + '</td></tr>';
	
	    // Left Pane (Scrollable List)
	    a1[n++] ='<tr><td id="'+v$.sId+'-listtd" height="100%" width="100%" valign="top">'
	            +'<table style="border:0px" cellspacing="0" cellpadding="0" height="100%" width="100%"><tr>'
	            +'<td height="100%" width="100%" valign="top"><div';
	    if( !v$.bIsPrintMode ){
	        a1[n++] =' style="height:100%;width:100%;overflow:hidden;position:relative;top:0px;' + (dojo._isBodyLtr() ? "left" : "right") + ':0px;z-index:1;"';
	 if( dojo.isIE ){
	        a1[n++] =' tabindex="0" hidefocus="true"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                +' onfocus="' + 'dwa.lv.virtualList.prototype.onFocusStatic(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onBlurStatic(event)' + '"';
	 } // end - I
	        a1[n++] =' onclick="' + 'dwa.lv.benriFuncs.eventPreventDefault(event)' + '"'
	                +' onmousedown="' + 'dwa.lv.virtualList.prototype.onMouseDownStatic(event)' + '"'
	                +' onmouseup="' + 'dwa.lv.virtualList.prototype.onClickStatic(event)' + '"'
	                +' ondblclick="' + 'dwa.lv.virtualList.prototype.onDblClickStatic(event)' + '"'
	                +' onselectstart="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' oncontrolselect="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' ondragstart="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onmousewheel="' + 'dwa.lv.virtualList.prototype.onMouseWheelStatic(event)' + '"';
	    }
	    a1[n++] ='>';
	    a1[n++] ='<div id="' + v$.sId + '-tbl-container';
	 if( dojo.isMozilla || dojo.isWebKit ){
	    a1[n++] =(v$.bIsPrintMode ? '' : '" style="min-width:100%;height:100%;position:absolute;top:0px;' + (dojo._isBodyLtr() ? "left" : "right") + ':0px;z-index:0;');
	 }else{ // GS
	    a1[n++] =(v$.bIsPrintMode ? '' : '" style="width:100%;height:100%;position:absolute;top:0px;' + (dojo._isBodyLtr() ? "left" : "right") + ':0px;z-index:0;');
	 } // end - I
	    a1[n++] ='" oncontextmenu="' + 'dwa.lv.virtualList.prototype.onContextMenuStatic(event)';
	    a1[n++] ='">';
	    a1[n++] ='<div id="' + v$.sId + '-tbl';
	 if( dojo.isMozilla || dojo.isWebKit ){
	    a1[n++] =(v$.bIsPrintMode ? '' : '" style="min-width:100%;height:100%;overflow:visible;');
	 }else{ // GS
	    a1[n++] =(v$.bIsPrintMode ? '' : '" style="width:100%;height:100%;overflow:visible;');
	 } // end - I
	    a1[n++] ='"></div>';

        a1[n++] = v$.generateFocusElement();

	    a1[n++] ='</div>';
	    a1[n++] ='<div id="' + v$.sId + '-hinttext" class="vl-scrollhint-text" style="visibility:hidden"></div>';
	 if( dojo.isMozilla || dojo.isWebKit ){
	 if( dojo.isIE || dojo.isMozilla ){
	    a1[n++] ='<div id="' + v$.sId + '-hinttextbinding" class="vl-scrollhint-text" style="visibility:hidden"></div>';
	 } // end - IG
	    a1[n++] ='<canvas id="' + v$.sId + '-hintbg" class="vl-scrollhint-bg" style="visibility:hidden"></canvas>';
	 }else{ // GS
	    a1[n++] ='<div id="' + v$.sId + '-hintbg" class="vl-scrollhint-bg" style="visibility:hidden"></div>';
	 } // end - I
	    a1[n++] ='</div></td>';
	
	    // Right Pane (scroll bar)
	    if( !v$.bIsPrintMode)
	        a1[n++]='<td id="' + v$.sId + '-rp" height="100%" valign="top"></td>';
	    a1[n++]='</tr></table></tr></tbody></table>';
	
	
	    // ===========================================
	    // XML Data
	    // ===========================================
	
	    // Get an existing XML element, if not found, create one
	    el = dojo.doc.getElementById(v$.sContainerId + "XML");
	    if( !el ){
	        el = dojo.doc.createElement("XML");
	        el.className = "s-hidden-iframe";
	        dojo.doc.body.appendChild(el);
	    }
	    v$.oXmlData = el;
	
	    // Get an existing temporary XML element, if not found, create one
	    el = dojo.doc.getElementById(v$.sContainerId + "XMLTmp");
	    if( !el ){
	        el = dojo.doc.createElement("XML");
	        el.className = "s-hidden-iframe";
	        dojo.doc.body.appendChild(el);
	    }
	    v$.oTmpData = el;
	    
	    
	    // ===========================================
	    // Mozilla focus handler
	    // ===========================================
	 if( dojo.isWebKit ){
	    a1[n++] ='<input id="'+v$.sId+'-keytrap" idvl="'+v$.sId+'" class="s-input-keytrap" readonly';
	    if( !v$.bIsPrintMode ){
	        a1[n++] =' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                +' onfocus="' + 'dwa.lv.virtualList.prototype.onFocusStatic(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onBlurStatic(event)' + '"';
	    }
	    a1[n++] ='></input>';
	 }else if( dojo.isMozilla ){ // S
	    // nakakura
	    if(dojo.hasClass(dojo.body(), 'dijit_a11y')){
	        a1[n++] ='<a id="'+v$.sId+'-keytrap" idvl="'+v$.sId+'" class="s-hidden-iframe" style="visibility:visible"';
	        if( !v$.bIsPrintMode ){
	            a1[n++] =' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                    +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                    +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                    +' onfocus="' + 'dwa.lv.virtualList.prototype.onFocusStatic(event)' + '"'
	                    +' onblur="' + 'dwa.lv.virtualList.prototype.onBlurStatic(event)' + '"';
	        }
	        a1[n++] ='></a>';
	    } else if(dwa.lv.globals.get().nIntBrowserVer >= 1.09) {
	        a1[n++] ='<input id="'+v$.sId+'-keytrap" idvl="'+v$.sId+'" class="s-input-keytrap" readonly';
	        if( !v$.bIsPrintMode ){
	            a1[n++] =' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                    +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                    +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                    +' onfocus="' + 'dwa.lv.virtualList.prototype.onFocusStatic(event)' + '"'
	                    +' onblur="' + 'dwa.lv.virtualList.prototype.onBlurStatic(event)' + '"';
	        }
	        a1[n++] ='></input>';
	    }
	    else {
	        a1[n++] ='<a id="'+v$.sId+'-keytrap" idvl="'+v$.sId+'" class="s-hidden-iframe"';
	        if( !v$.bIsPrintMode ){
	            a1[n++] =' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                    +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                    +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                    +' onfocus="' + 'dwa.lv.virtualList.prototype.onFocusStatic(event)' + '"'
	                    +' onblur="' + 'dwa.lv.virtualList.prototype.onBlurStatic(event)' + '"';
	        }
	        a1[n++] ='></a>';
	    }
	 } // end - G
	
	 // nakakura
	    if((3 <= dojo.isMozilla || 8 <= dojo.isIE) && this.bSupportScreenReader) {
	        a1[n++] = '<label id="' + this.sId + '-label-column" style="position:absolute;display:none"></label>'
	         + '<label id="' + this.sId + '-label-focus" style="position:absolute;display:none"></label>'
	         + '<label id="' + this.sId + '-label-keyTrap" style="position:absolute;display:none"></label>';
	    }
	    return a1.join('');
	},
    generateFocusElement: function(){
	    var a1=[],n=0;
        var v$ = this;

	    a1[n++] ='<div id="' + v$.sId + '-focus-outer" class="vl-focus-outer" style="visibility:hidden" isfocus="true">';
	 if( dojo.isMozilla ){
	    a1[n++] ='<div id="' + v$.sId + '-focus" class="vl-focus" isfocus="true"'
	                +' ' + 'com_ibm_dwa_ui_draggable' + '="' + (dojo._isBodyLtr() ? "left" : "right") + '"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                +' onclick="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onRowBlurStatic(event)' + '"'
	                +'></div>';
	 }else if( dojo.isWebKit ){ // G
	    a1[n++] ='<a id="' + v$.sId + '-focus" href="javascript:void(0)" class="vl-focus" isfocus="true"'
	                +' ' + 'com_ibm_dwa_ui_draggable' + '="' + (dojo._isBodyLtr() ? "left" : "right") + '"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onkeypress="' + 'dwa.lv.virtualList.prototype.onKeyPressStatic(event)' + '"'
	                +' onclick="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onRowBlurStatic(event)' + '"'
	                +'></a>';
	 }else{ // S
	    a1[n++] ='<div id="' + v$.sId + '-focus" class="vl-focus" isfocus="true" tabindex="0"'
	                +' ' + 'com_ibm_dwa_ui_draggable' + '="' + (dojo._isBodyLtr() ? "left" : "right") + '"'
	                +' onclick="' + 'dwa.lv.benriFuncs.eventPreventDefault(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onRowBlurStatic(event)' + '"'
	                +'></div>';
	 } // end - I
	    a1[n++] ='</div>';

        return a1.join('');
    },
	initColumnArray: function(nCols){
	    var v$=this;
	    v$.nColumnSpan   = (nCols=="none" )? 0 : Math.max(nCols ? nCols : 1, 1);
	    v$.aColInfo     ={};    // changed from array to object
	    v$.aDragBar     ={};
	    v$.aColHdr      ={};
	
	    for (var c=0; c < v$.nColumnSpan; c++ ){
	        v$.aColInfo[c] = new dwa.lv.columnInfo(c);
	    }
	    v$.aLayoutInfo = null;
	},
	resetObjects: function(p1){
	    
	
	    var v$=this,c,cMax;
	    var idVL=v$.sId;
	    if( 1==v$.bResetObjects ){
	        v$.vList=dojo.doc.getElementById(idVL+'-vlist');
	        if( !v$.vList ) return;
	        cMax=v$.nColumnSpan;
	        
	        with(v$){
	            header=dojo.doc.getElementById(idVL+'-header');
	            listTd=dojo.doc.getElementById(idVL+'-listtd');
	            tbl=dojo.doc.getElementById(idVL+'-tbl');
	            tblContainer=dojo.doc.getElementById(idVL+'-tbl-container');
	            hintText=dojo.doc.getElementById(idVL+'-hinttext');
	 if( dojo.isMozilla ){
	            hintTextBinding=dojo.doc.getElementById(idVL+'-hinttextbinding');
	 } // end - G
	            hintBg=dojo.doc.getElementById(idVL+'-hintbg');
	 if( dojo.isMozilla || dojo.isWebKit ){
	            oKeyTrapAnchor=dojo.doc.getElementById(idVL+'-keytrap');
	 }else{ // GS
	            oKeyTrapAnchor=this.tblContainer.parentNode;
	 } // end - I
	            for (c=0; c<cMax; c++ ){
	                aDragBar[c]=dojo.doc.getElementById(idVL+'-sb-'+c);
	                aColHdr[c] =dojo.doc.getElementById(idVL+'-ch-'+c);
	            }
	            oColHdr=dojo.doc.getElementById(idVL+'-columnheader');
	            oNarrowHdr=dojo.doc.getElementById(idVL+'-narrowheader');
	            oColumnFocus=dojo.doc.getElementById(idVL+'-columnfocus');
	            oColumnFocusOuter=dojo.doc.getElementById(idVL+'-columnfocus-outer');
	            oFocus=dojo.doc.getElementById(idVL+'-focus');
	            oFocusRight=dojo.doc.getElementById(idVL+'-focusRight');
	            oFocusTop=dojo.doc.getElementById(idVL+'-focusTop');
	            oFocusBottom=dojo.doc.getElementById(idVL+'-focusBottom');
	            oFocusOuter=dojo.doc.getElementById(idVL+'-focus-outer') || oFocus;
	            bResetObjects=0;
	        }
	        
	 if( dojo.isMozilla ){
	        if(!v$.bIsPrintMode)
	            v$.tblContainer.parentNode.addEventListener('DOMMouseScroll', dwa.lv.virtualList.prototype.onMouseWheelStatic, false);
	 } // end - G
	        
	        // fixup any null DragBars (problem with Notebook view)
	        for (c=0; c<cMax; c++ ){
	            if( !v$.aDragBar[c] && c>0 ) v$.aDragBar[c]=v$.aDragBar[c-1];
	        }
	
	        var el=dojo.doc.getElementById(idVL+'-rp');
	        if( p1 && el ) v$.scrBar=v$.oScrollBar.appendChild(el);
	
	        // Try to determine min cell height.  We only need to do this once.  
	        v$.calcMinRowHeight();
	
	        v$.setSortGif();
	        v$.setNarrowTitle();
	    }
	
	    
	},
	setGotoPosition: function(){
	    var v$=this;
	    if( !v$.fnSearch )return;
	    
	    // --------------------------------
	    // Activate the GoTo Button
	    // --------------------------------
	
	    if( !v$.oGoTo ){
	        v$.oGoTo=dojo.doc.getElementById(v$.sId+'-goto');
	        // initialization (goto element won't be available if in print or category mode)
	        if( v$.oGoTo ){
	            with(v$.oGoTo ){
	                title = this._smsgs[ "L_STARTSWITH_TITLE" ];
	            }
	 if( dojo.isMozilla || dojo.isWebKit ){
	            v$.oGoTo.addEventListener("click", function(ev){v$.onGotoClick(ev)}, false);
	 }else{ // GS
	            v$.oGoTo.attachEvent("onclick", function(ev){v$.onGotoClick(ev)});
	 } // end - I
	        }
	    }
	    if( v$.oGoTo )
	        v$.oGoTo.style.visibility = this.hasResortableColumn() ? 'inherit' : '';
	},
	resetColumnDesign: function(nCols){
	    this.initColumnArray(nCols);
	    this.nSortBy = this.nSortCol = -1;
	    if(this.tbl)this.deleteRows(-1);
	},
	updateColumnDesign: function(){
	    if(this.oColHdr){
	        this.updateOrgColumnWidths();
	        this.changeNarrowMode(this.needToBeNarrow(), true);
	
	        //reset the goto button
	        this.oGoTo = null;
	        this.setGotoPosition();
	
	    }
	},
	getCollectionContext: function(){
	    return {iSelectedPosition:this.iSelectedPosition, iCurrentPosition:this.iCurrentPosition, sSelectedTumbler:this.sSelectedTumbler, oRoot:this.oRoot};
	},
	applyCollectionContext: function(oCollCtx){
	    if(oCollCtx) {
	        this.iCurrentPosition = oCollCtx.iCurrentPosition;
	        this.iSelectedPosition = oCollCtx.iSelectedPosition;
	        this.sSelectedTumbler = oCollCtx.sSelectedTumbler;
	        this.oRoot = oCollCtx.oRoot;
	        this.bOnFirstLoadingProcess = false;
	    }
	},
	generateHeaderContentsHTML: function(){
	    // ===========================================
	    // COLUMN HEADER CONTENTS
	    // ===========================================
	    
	    var v$=this,a0,a1=[],n=0,rMax,c,cMax,i0,sd0,sd1,sd2,sd3,sd4,sd5,sd6,sc0,sc1,sc2,sc3,sc4,sc5;
	    var idVL=v$.sId;
	    var p1=v$.bIsPrintMode, t1=v$.sThreadRoot, b1=v$.bShowDefaultSort;
	    v$.bResetObjects=1;
	
	    v$.finalizeColumns();
	
	    i0      =0;
	    cMax    =v$.nColumnSpan;
	    a0      =v$.aColInfo;
	    
	    sd0     ='<div onmousedown="' + 'dwa.lv.virtualList.prototype.onColumnResizeStatic(event)' + '" ondblclick="' + 'dwa.lv.virtualList.prototype.onColumnResetStatic(event)' + '" class="vl-column-bar" style="top:1px;' + (dojo._isBodyLtr() ? "left" : "right") + ':';
	    sd1     ='<div ondblclick="' + 'dwa.lv.virtualList.prototype.onColumnResetStatic(event)' + '" class="vl-column-bar" style="top:1px;' + (dojo._isBodyLtr() ? "left" : "right") + ':';
	    sd2     =';cursor:col-resize';
	    sd3     =';z-index:-1';
	    sd4     =';visibility:hidden';
	    sd5     ='';
	    sd6     ='"><table style="border:0px" cellspacing=0 cellpadding=0 width=100% height=100%><tr><td></td></tr></table></div>'; // need this table to show move cursor on overall on dragbar
	    
	    sc0     ='<div class="vl-column-title ' + v$.sColumnClassName;
	    sc1     ='" onmouseover="try{' + 'dwa.lv.virtualList.prototype.onColumnHoverStatic(event)' + '}catch(e){}" onmouseout="try{' + 'dwa.lv.virtualList.prototype.onColumnHoverStatic(event)' + '}catch(e){}" resortable=true onclick="' + 'dwa.lv.virtualList.prototype.onColumnClickStatic(event)' + '" style="' + (dojo._isBodyLtr() ? "left" : "right") + ':';
	    sc2     ='" onmouseover="try{' + 'dwa.lv.virtualList.prototype.onColumnHoverStatic(event)' + '}catch(e){}" onmouseout="try{' + 'dwa.lv.virtualList.prototype.onColumnHoverStatic(event)' + '}catch(e){}" style="' + (dojo._isBodyLtr() ? "left" : "right") + ':';
	    sc3     =';cursor:'+(window.CSS2Properties ? 'pointer' : 'hand');
	    sc4     =';cursor:default';
	    sc5     = v$.bNarrowMode ? ';visibility:hidden' : '';
	
	    var cLast=cMax-1;
	    var bHasResortable = false;
	    for( c=0; c < cMax; c++ ){
	        // do not show a column for response-style columns
	        if( a0[c].bIsResponse ) continue;
	        
	        // left-position tracking variable
	        // ===========================================
	        // MOVEABLE COLUMNS
	        // ===========================================
	        a1[n++] =sc0 +c
	                +(a0[c].isSortable(p1,t1,b1)? sc1:sc2)+i0+'px'
	                +(a0[c].isSortable(p1,t1,b1)? sc3:sc4)
	                +sc5+';height:100%'
	                +'" id="'+idVL+'-ch-'+c
	                +'" columnid="'+idVL+'-ch-'+c
	                +'" i_col="'+c
	                +'" title="'+a0[c].sTitle
	                +'">'
	                +a0[c].sLabel
	                +'</div>';
	
	        // left-position tracking variable
	        i0 += a0[c].nWidth;
	        // ===========================================
	        // MOVEABLE DRAGBARS
	        // ===========================================
	        a1[n++] =( (cLast!=c && a0[c].isMoveable(p1) && !a0[c].bIsExtend)? sd0:sd1 )
	                +i0+'px'
	                +( (cLast==c||v$.bNarrowMode)? sd4:sd5 )
	                +( (cLast!=c && a0[c].isMoveable(p1) && !a0[c].bIsExtend)? sd2:sd3 )
	                +'" id="'+idVL+'-sb-'+c
	                +'" i_col="'+c
	                +sd6;
	        bHasResortable |= a0[c].isSortable(p1,t1,b1);
	    }
	
	    // dummy element to hold height
	    a1[n++] =sc0 + '0'
	            +sc1 + '0px;' + sc3 + ';'
	            +'visibility:hidden;position:static;'
	            +'">'
	            +'&nbsp'
	            +'</div>';
	
	    // ===========================================
	    // NARROW VIEW HEADER
	    // ===========================================
	    a1[n++] ='<div id="' +idVL +'-narrowheader" class="vl-column-title" isnarrowhdr="1"'
	            + (bHasResortable ? ' resortable=true onclick="' + 'dwa.lv.virtualList.prototype.onResortMenuStatic(event)' + '"' : '')
	            + ' oncontextmenu="dwa.lv.virtualList.prototype.onHeaderContextMenuStatic(event)"'
	            + ' style="left:0px;top:0px;width:100%;height:100%;text-align:' + "right"
	            +';cursor:'+(v$.sThreadRoot ? 'default' : (window.CSS2Properties ? 'pointer' : 'hand'))+';text-decoration:none;'+(v$.bNarrowMode ? '' : 'display:none;')
	            +this._cmsgs[ "D_PADDING_REVERSE" ]+':'+this.getScrollBarWidth()+'px'
	            +'">'
	            +'&nbsp;'
	            +'</div>';
	
	    // ===========================================
	    // COLUMN FOCUS
	    // ===========================================
	    a1[n++] ='<div id="' + v$.sId + '-columnfocus-outer" class="vl-focus-outer" style="visibility:hidden" isfocus="true">';
	 if( dojo.isMozilla ){
	    a1[n++] ='<div id="' + v$.sId + '-columnfocus" class="vl-focus" isfocus="true"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onmouseover="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onmouseout="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onclick="' + 'dwa.lv.virtualList.prototype.onColumnClickStatic(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onColumnBlurStatic(event)' + '"'
	                +'></div>';
	 }else if( dojo.isWebKit ){ // G
	    a1[n++] ='<a id="' + v$.sId + '-columnfocus" href="javascript:void(0)" class="vl-focus" isfocus="true"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onmouseover="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onmouseout="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onclick="' + 'dwa.lv.virtualList.prototype.onColumnClickStatic(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onColumnBlurStatic(event)' + '"'
	                +'></a>';
	 }else{ // S
	    a1[n++] ='<div id="' + v$.sId + '-columnfocus" class="vl-focus" isfocus="true" tabindex="0"'
	                +' onkeydown="' + 'dwa.lv.virtualList.prototype.onKeyDownStatic(event)' + '"'
	                +' onkeyup="' + 'dwa.lv.virtualList.prototype.onKeyUpStatic(event)' + '"'
	                +' onmouseover="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onmouseout="' + 'dwa.lv.benriFuncs.eventCancel(event)' + '"'
	                +' onclick="' + 'dwa.lv.virtualList.prototype.onColumnClickStatic(event)' + '"'
	                +' onblur="' + 'dwa.lv.virtualList.prototype.onColumnBlurStatic(event)' + '"'
	                +'></div>';
	 } // end - I
	    a1[n++] ='</div>';
	
	    // ===========================================
	    // "GoTo" button
	    // ===========================================
	    if( !(p1 || t1))
	    {
	        // goto position will be fixed up when it is made visible
	        a1[n++] ='<div id="'+idVL+'-goto" class="s-goto"><a href="javascript:void(0)">';
	        if (dojo.hasClass(dojo.body(), 'dijit_a11y')) {
	            a1[n++] = '<span'
	                    + dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(16, 16), new dwa.common.utils.pos(182, 0), dwa.lv.globals.get().buildResourcesUrl("basicicons.gif"), false, "border:0px")
	                    + ' class="s-bidi-flip" alt="' + this._smsgs["L_STARTSWITH_TITLE"] + '"></span>';
	        } else {
	            a1[n++] = '<img class="s-bidi-flip" ' + dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(16, 16), new dwa.common.utils.pos(182, 0), dwa.lv.globals.get().buildResourcesUrl("basicicons.gif"), false, "border:0px") + ' alt="' + this._smsgs["L_STARTSWITH_TITLE"] + '">';
	        }
	        a1[n++] = '</a></div>';
	    }
	    return a1.join('');
	},
	getCount: function(){
	return this.count;
	},
	setCount: function(nCount){
	this.count = nCount;
	},
	getHeightDiff: function(){
	    return(dojo.doc.body.clientHeight - this.iHeight);
	},
	saveHeight: function(){
	    this.iHeight = dojo.doc.body.clientHeight;
	},
	getWidthDiff: function(){
	    return(this.getContainerWidth() - this.iWidth);
	},
	saveWidth: function(){
	    this.iWidth = this.getContainerWidth();
	},
	focus: function(){
	    // ------------------------------------------
	    // provides a mechanism to put focus on the VList
	    // ------------------------------------------
	
		;
	    this.updateFocus();
	},
	blur: function(ev){
	    ;
	    var v$=this;
	    var bDragging = !!dwa.lv.virtualList.prototype.oDragInfoStatic.bar;
	    if(bDragging)
	        v$.endDrag();
	    
	    v$.onBlur(ev);
	},
	getScrollElement: function(){
	    // ------------------------------------------
	    // find the parent element with a horizontal scroll
	    // ------------------------------------------
	    if (this.bIsPrintMode) return null;
	    if (this.scrObj) return this.scrObj;
	    var obj=this.tbl;
	    var i=0;
	    while(obj){
	        // no infinite loops!
	        i++; if (i>25) return null;
	        // has scroll?
	        if (obj.scrollLeft) break;
	        obj = obj.parentNode;
	    }
	    if (obj && obj.scrollLeft){
	        this.scrObj=obj;
	        return this.scrObj;
	    }
	    return null;
	},
	updateOrgColumnWidths: function(bSet){
	    var v$=this;
	    var maxC=v$.nColumnSpan-1;
	    for(var i=0;i<=maxC;i++) {
	        if(v$.aColInfo[i].nChars>0) {
	            v$.aColInfo[i].nWidth = v$.aColInfo[i].nChars * this.iCharWidth;
	            v$.aColInfo[i].nChars = 0;
	        }
	        if(!v$.aColInfo[i].nOrgWidth)
	            v$.aColInfo[i].nDesignWidth = v$.aColInfo[i].nOrgWidth = v$.aColInfo[i].nWidth;
	        if(bSet)
	            v$.aColInfo[i].nOrgWidth = v$.aColInfo[i].nWidth;
	    }
	},
	resetColumnWidths: function(){
	    var v$=this;
	    var maxC=v$.nColumnSpan-1;
	    for(var i=0;i<=maxC;i++) {
	        if(v$.aColInfo[i].nDesignWidth)
	            v$.aColInfo[i].nOrgWidth = v$.aColInfo[i].nDesignWidth;
	    }
	},
	getOrgColumnWidths: function(){
	    var v$=this;
	    var maxC=v$.nColumnSpan-1;
	    var a_colW=[];
	    for(var i=0;i<=maxC;i++) {
	        if(v$.aColInfo[i].bIsResponse)
	            continue;
	        a_colW.push(v$.aColInfo[i].nOrgWidth ? v$.aColInfo[i].nOrgWidth : v$.aColInfo[i].nWidth);
	    }
	    return a_colW;
	},
	syncColSS: function(bLast,bSet,bHeight){
	    // ------------------------------------------
	    // used with manual drag-drop of column width
	    //  and resizing of the window
	    // ------------------------------------------
	    var v$=this;
	    // get out if we are still setting col widths below
	    if( v$.adjusting )   return;
	
	    // This is here so all returns go through the end macro
	    
	    
	    // No objects available?
	    var bUpdateOnly=(1==v$.bResetObjects);
	
	    // Save current width and height
	    v$.saveWidth();v$.saveHeight();
	
	    // adjust the far right components
	    if( !bUpdateOnly ){
	        v$.resetObjects(false);
	
	        // make sure scroll height is good
	        if( 0!=bHeight ){ v$.updateScrollBar(); }
	    }
	    else {
	        v$.updateOrgColumnWidths(bSet);
	    }
	
	    // sync columns to col-header width
	    {
	        v$.adjusting = true;
	
	        var a_colW = v$.recalcColWidths();
	
	        // #3:  apply the column widths in the view
	        
	
	        var sum=0;
	
	        for (var i=0;i < v$.nColumnSpan;i++ ){
	            // set table column width
	            v$.aColInfo[i].nWidth = a_colW[i];
	            if(!v$.aColInfo[i].bIsResponse)
	                sum += a_colW[i];
	            if(!bUpdateOnly) {
	                var oDragBar = v$.aDragBar[i];
	                oDragBar.style[ (dojo._isBodyLtr() ? "left" : "right") ] = sum + 'px';
	                v$.syncColHdr(i);
	            }
	
	            this.anStyleWidths[i] = a_colW[i];
	        }
	
	        this.setSS();
	
	        // there is no need to set the response style rules if this is a flat view
	        if( this.isCategorizedView() ){
	            v$.syncCSS(a_colW);
	        }
	        
	
	        // store the new column widths
	        if( !bUpdateOnly ){
	            if( 0!=bSet ){
	                v$.setColumnCookie();
	                v$.bNonDefaultWidths=true;
	                // replace the table
	                v$.showWaitBox(true, 99);
	                setTimeout(function(){v$.resetFlags()}, 0);
	            }
	            else {
	                v$.resetFlags();
	            }
	        }
	
	        // Cleanup
	        v$.adjusting = false;
	        a_colW=null;
	
	    }
	    
	},
	getColumnMaxWidth: function(nCol){
	    var v$ = this;
	    var aNewWidth = [];
	    var nAvailWidth = v$.getListWidth();
	    for(var i=0;i<v$.nColumnSpan;i++) {
	        var oColInfo = v$.aColInfo[i];
	        aNewWidth[i] = oColInfo.nOrgWidth ? oColInfo.nOrgWidth : oColInfo.nWidth;
	    }
	    
	    var aRowInfo = v$.getSingleRowInfo(v$.getLayoutInfo(), false);
	    var maxC=aRowInfo.length-1;
	    var extC=v$.getExtendColumn(aRowInfo);
	    var nExtIndex = aRowInfo[extC].nRealIndex;
	    var nTotal = 0;
	    for(var c=0;c<=maxC;c++) {
	        if(c==nCol)
	            continue;
	        if(c==extC)
	            nTotal += v$.getMinColumnWidth(c);
	        else if(!v$.aColInfo[aRowInfo[c].nRealIndex].bIsResponse)
	            nTotal += aNewWidth[aRowInfo[c].nRealIndex];
	    }
	    return nAvailWidth - nTotal;
	},
	recalcColWidths: function(bUpdateOrgWidth){
	    var v$ = this;
	    var aNewWidth = [];
	    var nAvailWidth = v$.getListWidth();
	    for(var i=0;i<v$.nColumnSpan;i++) {
	        var oColInfo = v$.aColInfo[i];
	        if(bUpdateOrgWidth)
	            oColInfo.nOrgWidth = oColInfo.nWidth;
	        var nWidth = typeof(v$.getFixedColumnWidth(i))=='number' ? v$.getFixedColumnWidth(i) : oColInfo.nOrgWidth ? oColInfo.nOrgWidth : oColInfo.nWidth;
	        aNewWidth[i] = v$.bNarrowMode ? Math.max(nWidth, v$.getMinColumnWidth(i)) : nWidth;
	    }
	    
	    // extend column
	    for(var row=0;row<2;row++) {
	        var aRowInfo = v$.getSingleRowInfo(v$.getLayoutInfo(), !!row);
	        if(!aRowInfo || !aRowInfo.length)
	            continue;
	
	        var maxC=aRowInfo.length-1;
	        var extC=v$.getExtendColumn(aRowInfo);
	        var nExtIndex = aRowInfo[extC].nRealIndex;
	        var nTotal = 0;
	        for(var c=0;c<=maxC;c++) {
	            if(!v$.aColInfo[aRowInfo[c].nRealIndex].bIsResponse)
	                nTotal += aNewWidth[aRowInfo[c].nRealIndex];
	        }
	        var nDiff = nAvailWidth - nTotal;
	        if(nDiff < 0) {
	            var nMinWidth = v$.getMinColumnWidth(nExtIndex);
	            if(aNewWidth[nExtIndex] + nDiff < nMinWidth) {
	                nDiff += aNewWidth[nExtIndex] - nMinWidth;
	                aNewWidth[nExtIndex] = nMinWidth;
	            }
	            else {
	                aNewWidth[nExtIndex] += nDiff;
	                nDiff = 0;
	            }
	        }
	        else if (nDiff > 0) {
	            aNewWidth[nExtIndex] += nDiff;
	            nDiff = 0;
	        }
	    }
	    
	    // other columns
	    for(var row=0;row<2;row++) {
	        var aRowInfo = v$.getSingleRowInfo(v$.getLayoutInfo(), !!row);
	        if(!aRowInfo || !aRowInfo.length)
	            continue;
	
	        var maxC=aRowInfo.length-1;
	        var extC=v$.getExtendColumn(aRowInfo);
	        var nExtIndex = aRowInfo[extC].nRealIndex;
	        var maxR = nAvailWidth;
	        var nTotal = 0;
	        for(var c=0;c<=maxC;c++) {
	            if(!v$.aColInfo[aRowInfo[c].nRealIndex].bIsResponse)
	                nTotal += aNewWidth[aRowInfo[c].nRealIndex];
	        }
	        var nDiff = nAvailWidth - nTotal;
	        for(var c=maxC;nDiff&&c>=0;c--) {
	            var nIndex = aRowInfo[c].nRealIndex;
	            if(v$.aColInfo[nIndex].bIsResponse)
	                continue;
	            var nMinWidth = v$.getMinColumnWidth(nIndex);
	            if(aNewWidth[nIndex] + nDiff < nMinWidth) {
	                nDiff += aNewWidth[nIndex] - nMinWidth;
	                aNewWidth[nIndex] = nMinWidth;
	            }
	            else {
	                aNewWidth[nIndex] += nDiff;
	                nDiff = 0;
	            }
	        }
	    }
	    return aNewWidth;
	},
	resetFlags: function( bKeepCaret ){
	    // ------------------------------------------
	    // reset flags
	    // ------------------------------------------
	    var v$=this;
	    v$.adjusting = false;
	    if(!bKeepCaret)
	        v$.invalidateForcedCaretMoving();
	    v$.showWaitBox(false);
	},
	setColumnFormat: function(nCol,nFormat,bBytes,nDigits,bOmitThisYear){
	    // ------------------------------------------
	    // type of data contained in the column
	    //  datetime == 1
	    // ------------------------------------------
	    this.aColInfo[nCol].nColFmt = parseInt(nFormat,10);
	    this.aColInfo[nCol].bBytes = bBytes;
	    this.aColInfo[nCol].nDigits = nDigits;
	    this.aColInfo[nCol].bOmitThisYear = bOmitThisYear;
	},
	isCategorizedView: function(){
	    if(this.getListMode())
	        return true;
	    for(var i=0;i<this.nColumnSpan;i++)
	        if(this.aColInfo[i].bIsTwistie || this.aColInfo[i].bIsResponse)
	            return true;
	    return false;
	},
	setColumnWidth: function(nCol,nWidth,bFixedWidth,bChars,bTwistie,bResponse,bExtend,sNarDisp,nSeqNum,bWrapUnder,sIMColName,bIcon,bThin,bGrad,bAlignGrad,hUnhideWhenWrap){
	    // ------------------------------------------
	    // assigns a width to a visible column
	    // ------------------------------------------
	    
	    var bFixed = -1;
	    if( "undefined" != typeof(bFixedWidth) ){
	        if( bFixedWidth)
	            bFixed = 1;
	        else 
	            bFixed = 0;
	    }
	    if( bResponse )
	        bFixed = 1;
	
	    // if the number of characters are passed,
	    //  convert this to a pixel width
	    var c0 = 0, w0 = bThin ? nWidth : Math.max((bChars?1:20),nWidth);
	    if( bChars ){ c0 = w0; w0 = w0 * this.iCharWidth; }
	
	    // store the properties
	    this.aColInfo[nCol].bIsTwistie  = bTwistie;
	    this.aColInfo[nCol].nWidth      = (bResponse? 0:w0);
	    this.aColInfo[nCol].bIsResponse = bResponse;
	    this.aColInfo[nCol].bIsExtend   = !!bExtend;
	    this.aColInfo[nCol].sNarrowDisp = sNarDisp;
	    this.aColInfo[nCol].nSeqNum     = typeof(nSeqNum) == 'number' ? nSeqNum : 1;
	    this.aColInfo[nCol].bWrapUnder  = bWrapUnder;
	    this.aColInfo[nCol].sIMColName  = sIMColName;
	    this.aColInfo[nCol].bIsIcon     = bIcon;
	    this.aColInfo[nCol].bIsThin     = bThin;
	    this.aColInfo[nCol].bShowGradientColor  = bGrad;
	    this.aColInfo[nCol].bAlignGradientColor = bAlignGrad;
	    this.aColInfo[nCol].bUnhideWhenWrapped  = hUnhideWhenWrap;
	    
	    // real width of column will be calced later.
	    this.aColInfo[nCol].nChars = c0;
	
	    if(-1!=bFixed) this.aColInfo[nCol].bIsFixed=bFixed;
	    
	},
	finalizeColumns: function(){
	    // ------------------------------------------
	    // called by getFirstPage
	    // ------------------------------------------
	    
	    var v$=this;
	    var p1=v$.bIsPrintMode,p2=v$.sThreadRoot,a0,a1,i,iMax;
	
	    // allow the user-set widths to override the view-design
	    a0 = v$.getColumnCookie();
	    iMax=v$.nColumnSpan;
	   if( a0 && a0.length==iMax ){
	        v$.bNonDefaultWidths=true;
	        a1=v$.aColInfo;
	        for (i=0; i<iMax; i++ ){
	            // don't change the width if the column has a fixed size
	            if( a1[i].isMoveable(p1,p2) ){
	                a1[i].nOrgWidth = a1[i].nWidth = Math.max(v$.getMinColumnWidth(i),parseInt(a0[i],10));
	                a1[i].nChars=0; // prevent recalc width from num of chars
	            }
	        }
	        a0=null;
	    }
	
	    // sync stylesheet positions
	    v$.syncColSS(0,0,0);
	
	    
	},
	syncColHdr: function(c0){
	    // ------------------------------------------
	    // adjust column width to the position of the surrounding dragbars
	    // ------------------------------------------
	    
	    var v$=this;
	
	    if( 1==v$.bResetObjects ){
	        return;;
	    }
	
	    var iWth,iRgt,iLft;
	    var c1   = c0-1;
	    var cLast= v$.nColumnSpan-1;
	    var oCH  = v$.aColHdr[c0];
	    var oCI  = v$.aColInfo[c0];
	
	    if( oCI.bIsResponse ){
	        return;;
	    }
	
	    // calculate the width from the positions of the surrounding drag-bars
	    if( cLast==c0 ){
	        iRgt = v$.getListWidth();
	        v$.aDragBar[c0].style[ (dojo._isBodyLtr() ? "left" : "right") ]=iRgt + 'px';
	    }
	    else{
	        iRgt = parseInt(v$.aDragBar[c0].style[ (dojo._isBodyLtr() ? "left" : "right") ]);
	    }
	    iLft = c1>=0? parseInt(v$.aDragBar[c1].style[ (dojo._isBodyLtr() ? "left" : "right") ]) : 0;
	    iWth = Math.max(v$.getMinColumnWidth(c0), iRgt - iLft);
	
	    // set the column header properties
	    oCI.nWidth = iWth;
	    oCH.style[ (dojo._isBodyLtr() ? "left" : "right") ]  = iLft + 'px';
	
	    // sync col headers following this column with their dragbars
	    for(var i=c0+1;i<=cLast;i++) {
	        var c1   = i-1;
	        var oCH  = v$.aColHdr[i];
	        iLft = c1>=0? parseInt(v$.aDragBar[c1].style[ (dojo._isBodyLtr() ? "left" : "right") ]) : 0;
	        if(oCH) // for null colhdr on notebook view
	            oCH.style[ (dojo._isBodyLtr() ? "left" : "right") ]  = iLft + 'px';
	    }
	
	    
	},
	setColumnTitle: function(nCol,sTitle,nResortType,nViewSortType,sHiddenTitle,nHeaderIcon){
	    // ------------------------------------------
	    // assigns a title to a visible column
	    // ------------------------------------------
	    var v$=this;
	    if('undefined'==typeof(nResortType))    nResortType=0;
	    if('undefined'==typeof(nViewSortType))  nViewSortType=0;
	    
	    if( nCol>=0 && nCol<this.nColumnSpan ){
	        var s    = sTitle ? sTitle : "";
	        var ic   = dwa.lv.benriFuncs.generateIconsImgURLString(nHeaderIcon, !this.bIsPrintMode, true, true);
	        var h    = sHiddenTitle? sHiddenTitle : "";
	        var s1   = this.formatColumnHeader(s + ic, 0!=nResortType && !this.sThreadRoot);
	
	        this.aColInfo[nCol].sLabel       = s1;
	        this.aColInfo[nCol].sText        = s;
	        this.aColInfo[nCol].sTitle       = h;
	        this.aColInfo[nCol].sHiddenTitle = h;
	        this.aColInfo[nCol].bResortable  = nResortType;
	        this.aColInfo[nCol].iViewSort    = nViewSortType;
	        this.aColInfo[nCol].nHeaderIcon  = nHeaderIcon;
	    }
	},
	setSortInfo: function(nCol){
	    // ------------------------------------------
	    // sets the sorted column and info about the sort direction
	    // ------------------------------------------
	    var v$=this;
	    v$.getNewSortInfo(nCol);
	    v$.nSortBy  = nCol;
	    return( dwa.lv.listViewBase.prototype.gSortStatic[0]!=v$.sSortType );
	},
	getNewSortInfo: function(nCol, oObj){
	    var v$=this;
	    if (!oObj)
	        oObj = this;
	
	    if (-1 == nCol){
	        oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[0];
	    }
	    else if (v$.aColInfo[nCol].bUnhideWhenWrapped){
	        // YHAO7PCBXG : stored column in profile may be not match with resortable column because we've started to support UnHideWhenWrapped column such as Padding columns in Draft & Junk.
	        oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[0];
	    }
	    else{
	        var iType = v$.aColInfo[nCol].bResortable;
	        switch (iType){
	
	        // Column can be resorted either way, so advance to the next sort type
	        case 5:
	            if (nCol==v$.nSortBy){
	                // set the sSortType based on the current state of the sSortType
	                switch (v$.sSortType){
	                case dwa.lv.listViewBase.prototype.gSortStatic[0]:
	                    // was null, now sort down
	                    oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[1];
	                    break;
	                case dwa.lv.listViewBase.prototype.gSortStatic[1]:
	                    // was down, now sort up
	                    oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[2];
	                    break;
	                case dwa.lv.listViewBase.prototype.gSortStatic[2]:
	                    // was up, now remove sorting
	                    oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[0];
	                    break;
	                }
	            }
	            else{
	                // Notes always sorts down first
	                oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[1];
	            }
	            break;
	
	
	        // Column can be resorted ascending
	        case 3:
	        case 2:
	
	        // Column can be resorted descending
	        case 4:
	        case 1:
	
	        // Column is not sortable
	        case 0:
	
	            if (nCol==v$.nSortBy){
	                oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[0];
	            }
	            else{
	                oObj.sSortType  = dwa.lv.listViewBase.prototype.gSortStatic[iType];
	            }
	            break;
	        }
	    }
	},
	getFirstViewSort: function(){
	    // ------------------------------------------
	    // returns the first column number in the default view sort
	    // ------------------------------------------
	    var v$=this;
	    if(!v$.bShowDefaultSort)
	        return -1;
	    for( var i=0,iMax=v$.nColumnSpan; i<iMax ; i++ ){
	        if( 0!=v$.aColInfo[i].iViewSort )return i;
	    }
	    return -1;
	},
	setSortGif: function(){
	    // ------------------------------------------
	    // sets the icon for the sorted column
	    // ------------------------------------------
	    var v$=this;
	    if( 1==v$.bResetObjects ) return;
	    var oImg,oCol,nCol,oPrevCol,nPrevCol,iType;
	
	    // undo previously sorted column
	    nPrevCol = v$.nSortCol;
	    if( -1 != nPrevCol ){
	        oPrevCol = v$.aColHdr[nPrevCol];
	        if( oPrevCol ){
	            oImg = v$.getSortImage(oPrevCol);
	            if( oImg ){
	                oImg.style.visibility="hidden";
	            }
	            dwa.common.utils.cssEditClassExistence(oPrevCol, 'vl-column-title-resorted', false);
	            dwa.common.utils.cssEditClassExistence(v$.oNarrowHdr, 'vl-column-title-resorted', false);
	        }
	    }
	
	    // set the new sorted column
	    nCol = -1!=v$.nSortBy? v$.nSortBy: v$.getFirstViewSort(v$.sId);
	    if( -1 != nCol ){
	        iType = -1!=v$.nSortBy? v$.aColInfo[nCol].bResortable : v$.aColInfo[nCol].iViewSort;
	        oCol = v$.aColHdr[nCol];
	        if( oCol ){
	            oImg = v$.getSortImage(oCol);
	            if( oImg ){
	                var oInfo = v$.getSortGifInfo(iType, v$.sSortType, v$.bUseHCSortIcon);
	                if(oInfo) {
	                    oImg.style.display = "inline";
	                    oImg.style.visibility = "inherit";
						// nakakura
	                    (dojo.hasClass(dojo.body(), 'dijit_a11y') ? dwa.lv.autoConsolidatedImageListenerA11y : dwa.lv.autoConsolidatedImageListener).prototype.applyConsolidatedImageAttrsByPosStatic(oImg, oInfo.oSize, oInfo.oOffset, oInfo.sUrl);
	                }
	                else {
	                    oImg.style.display = "none";
	                    oImg.style.visibility = "hidden";
	                }
	            }
	            dwa.common.utils.cssEditClassExistence(oCol, 'vl-column-title-resorted', true);
	            dwa.common.utils.cssEditClassExistence(v$.oNarrowHdr, 'vl-column-title-resorted', true);
	        }
	    }
	
	    v$.nSortCol = nCol;
	
	    // hover previously sorted column again
	//    if(nCol!=nPrevCol && oPrevCol && !!oPrevCol.getAttribute('hovered'))
	//        v$.showColumnHover(oPrevCol, true);
	    if(oPrevCol && !!oPrevCol.getAttribute('hovered'))
	        v$.showColumnHover(oPrevCol, true);
	    if(oCol && !!oCol.getAttribute('hovered'))
	        v$.showColumnHover(oCol, true);
	
	},
	showColumnHover: function(oCol, bShow){
	    // ------------------------------------------
	    // sets the icon for the sorted column
	    // ------------------------------------------
	    var v$=this;
	    if( 1==v$.bResetObjects ) return;
	    var oImg;
	
	    var nCol = dwa.lv.benriFuncs.findAttr('i_col', oCol);
	    var bNarrowHdr = !!dwa.lv.benriFuncs.findAttr('isnarrowhdr', oCol);
	    var oColHdr = v$.aColHdr[nCol];
	    if( oColHdr )
	        oImg = v$.getSortImage(oColHdr);
	
	    // to not hover on non resortable column
	    if(!bNarrowHdr && !v$.aColInfo[nCol].bResortable)
	        return;
	
	
	    if (bShow) {
	        var iType = v$.aColInfo[nCol].bResortable;
	        var oInfo = v$.getHoverGifInfo(iType, nCol == v$.nSortCol ? v$.sSortType : '', v$.bUseHCSortIcon);
	        if(oImg && oInfo) {
	            oImg.style.display = "inline";
	            oImg.style.visibility = "inherit";
				// nakakura
	            (dojo.hasClass(dojo.body(), 'dijit_a11y') ? dwa.lv.autoConsolidatedImageListenerA11y : dwa.lv.autoConsolidatedImageListener).prototype.applyConsolidatedImageAttrsByPosStatic(oImg, oInfo.oSize, oInfo.oOffset, oInfo.sUrl);
	            v$.setNarrowTitle(nCol);
	        }
	    } else {
	        if (oImg) {
	            if(oColHdr.className.indexOf('-resorted')==-1) {
	                oImg.style.display = "none";
	                oImg.style.visibility = "hidden";
	            } else {
	                var iType = -1!=v$.nSortBy? v$.aColInfo[nCol].bResortable : v$.aColInfo[nCol].iViewSort;
	                var oInfo = v$.getSortGifInfo(iType, v$.sSortType, v$.bUseHCSortIcon);
	                if(oInfo) {
	                    oImg.style.display = "inline";
	                    oImg.style.visibility = "inherit";
						// nakakura
	                    (dojo.hasClass(dojo.body(), 'dijit_a11y') ? dwa.lv.autoConsolidatedImageListenerA11y : dwa.lv.autoConsolidatedImageListener).prototype.applyConsolidatedImageAttrsByPosStatic(oImg, oInfo.oSize, oInfo.oOffset, oInfo.sUrl);
	                }
	                else {
	                    oImg.style.display = "none";
	                    oImg.style.visibility = "hidden";
	                }
	            }
	            v$.setNarrowTitle();
	        }
	    }
	
	    dwa.common.utils.cssEditClassExistence(oCol, 'vl-column-title-hover', bShow);
	    dwa.common.utils.cssEditClassExistence(v$.oNarrowHdr, 'vl-column-title-hover', bShow);
	},
	setNarrowTitle: function(nCol){
	    // ------------------------------------------
	    // sets the acption and the icon for the narrow header
	    // ------------------------------------------
	    var v$=this;
	    if( 1==v$.bResetObjects ) return;
	
	    // set the new sorted column
	    var oColHdr = v$.oNarrowHdr;
	    var bHover = typeof(nCol) != 'undefined';
	    nCol = bHover ? nCol : -1!=v$.nSortBy? v$.nSortBy : v$.getFirstViewSort(v$.sId);
	    if( -1 != nCol ){
	        oColHdr.innerHTML =
	            this.formatNarrowHeader(
	                dwa.common.utils.formatMessage(this._smsgs[ "L_NARROW_SORTBY" ], v$.aColInfo[nCol].sText ? v$.aColInfo[nCol].sText : (v$.aColInfo[nCol].sHiddenTitle ? v$.aColInfo[nCol].sHiddenTitle : this._smsgs[ "L_NARROW_NOTITLE" ]))
	                , true, bHover);
	    }
	    else {
	        oColHdr.innerHTML =
	            this.formatNarrowHeader(this._smsgs[ "L_NARROW_DEFAULT" ], false);
	    }
	
	},
	setColumnCookie: function(){
	    // ------------------------------------------
	    // stores the desired column widths
	    // ------------------------------------------
	    if( !this.bAllowStoreColumnCookie || this.bNarrowMode ) return;
	    
	    var a_info = this.getOrgColumnWidths();
	    if (!dojo._isBodyLtr()) {
	       // check that sum of all width are same as container's width for BiDi.
	       var ListWidth = this.getListWidth();
	       var iTotal = (20 * a_info.length);
	       for(var i=0; i < a_info.length; i++)
	       {
	          iTotal += (a_info[i] - 20);
	          if(iTotal > ListWidth)
	          {
	             for(var j=i; j < a_info.length - 1; j++)
	                a_info[j] = 20;
	
	             a_info[a_info.length - 1] = ListWidth;
	             for(var k=0; k < a_info.length - 1; k++)
	                a_info[a_info.length - 1] -= a_info[k];
	
	             break;
	          }
	       }
	    }
	
	    var VL_ColInfo = "A" + "_" + this.getContainerWidth() + "_" + a_info.join("_");
	
	    // replace special characters in the view name with an underscore char
	    var fldName = "VL"  + (this.sThreadRoot? 'Thread':'') + this.profileModifier + this.sFolderName.replace(/[  $\(\)]/g,"_");
	
	    dwa.lv.miscs.getNotesListViewProfileCache().set(fldName, VL_ColInfo, true, this.sId);
	},
	getColumnCookie: function(){
	    // ------------------------------------------
	    // returns an array of stored column widths
	    // ------------------------------------------
	    var fldName = "VL"  + (this.sThreadRoot? 'Thread':'') + this.profileModifier + this.sFolderName.replace(/[  $\(\)]/g,"_");
	    var VL_ColInfo = dwa.lv.miscs.getNotesListViewProfileCache().get(fldName);
	    if (VL_ColInfo) {
	        var a = VL_ColInfo.split("_");
	        if (a.length){
	            // handle particular cookie storage formats here
	            if ("A" == a[0]){
	                // km: Mozilla doesn't set offsetWidth for objects dynamically created by createElement...
	                // remove first 2 items from the array, 
	                // then pass the rest back to the caller
	                var i = 2 + "A".length + a[1].length;
	                var s = a.join("_");
	                a = null;
	                return( s.substr(i).split("_") );
	            }
	        }
	    }
	    // else, no stored info
	    return null;
	},
	bindColumnData: function(nCol,nDataColumn,nSortColumn,sName){
	    // ------------------------------------------
	    // binds a visible column to the data-source column
	    // ------------------------------------------
	    if (nCol >=0 && nCol < this.nColumnSpan) {
	        this.aColInfo[nCol].nXmlCol =this.aColInfo[nCol].nColSort =nDataColumn;
	        if( nSortColumn ) this.aColInfo[nCol].nColSort = nSortColumn;
	        if( sName ) this.aColInfo[nCol].sName = sName;
	    }
	},
	getSelectedValues: function(){
	    return this.getSelectedData("UNID");
	},
	getSelectedData: function(sColumn, bAdd){
	    // ------------------------------------------
	    // return the selected rows XML data
	    // ------------------------------------------
	    // return null if no value is selected
	    if(!this.getDD().getSelectedCount(this))return null;
	
	    // which column value should this function return?
	    if("undefined"==typeof(sColumn)) sColumn="UNID";
	
	    var vals=[],vpos=[],vdata={},aItems=this.getDD().getSelected(false, this);
	    switch( typeof(sColumn) == "string" ? sColumn.toUpperCase() : sColumn ){
	    case "ENTRYDATA":
	        for( var i=0,j=0,iMax=aItems.length; i<iMax; i++ ){
	            if(aItems[i]){
	                var t1     = aItems[i].getAttribute("tumbler");
	                var oEntry = this.oRoot.getEntryByTumbler(t1, true);
	                var item  = oEntry ? oEntry.getViewEntry() : aItems[i].getAttribute("item");
	                if(item){
	                    vals[j++]=item;
	                }
	            }
	        }
		break;
	    case "NOTEID":
	        for( var i=0,j=0,iMax=aItems.length; i<iMax; i++ ){
	            if(aItems[i]){
	                var t1     = aItems[i].getAttribute("tumbler");
	                var oEntry = this.oRoot.getEntryByTumbler(t1, true);
	                var sData  = oEntry? oEntry.getNoteId():null;
	                if(sData){
	                    vals[j++]=sData;
	                }
	            }
	        }
	        break;
	    case "UNID":
	        for( var i=0,j=0,iMax=aItems.length; i<iMax; i++ ){
	            if(aItems[i]){
	                var t1     = aItems[i].getAttribute("tumbler");
	                var sData  = aItems[i].getAttribute("unid");
	                if(sData){
	                    vals[j++]=sData;
	                }
	            }
	        }
	        break;
	    case "POSITION":
	        for( var i=0,j=0,iMax=aItems.length; i<iMax; i++ ){
	            if(aItems[i]){
	                var t1     = aItems[i].getAttribute("tumbler");
	                if(t1){
	                    vals[j++]=t1;
	                }
	            }
	        }
	        break;
	    default:
	        for( var i=0,j=0,iMax=aItems.length; i<iMax; i++ ){
	            if(aItems[i]){
	                var t1     = aItems[i].getAttribute("tumbler");
	                var oEntry = this.oRoot.getEntryByTumbler(t1, true);
	                var sData  = oEntry? this.getColumnValue(oEntry.getViewEntry(),sColumn,true):"";
	                if(sData||bAdd){
	                    vals[j++]=sData;
	                }
	            }
	        }
	        break;
	    }
	    
	    return vals;
	},
	getSelectedCount: function(){
	    return this.getDD().getSelectedCount(this);
	},
	getScrollBarWidth: function(){
	    return dwa.lv.virtualScrollBar.prototype.getScrollAreaWidth();
	},
	getListWidth: function(){
	    // get the width of the containing TD
	    var v$=this;
	    // Gecko seems to be 1 extra scrollbar off, if the body has a scrollbar
	 if( dojo.isMozilla || dojo.isWebKit ){
	    return( v$.tblContainer? v$.tblContainer.parentNode.parentNode.offsetWidth : (v$.getContainerWidth()-v$.getScrollBarWidth()-((dojo.doc.body.scrollWidth>dojo.doc.body.offsetWidth)?v$.getScrollBarWidth():0)) );
	 }else{ // GS
	    return( v$.tblContainer? v$.tblContainer.parentNode.parentNode.offsetWidth : (v$.getContainerWidth()-v$.getScrollBarWidth()) );
	 } // end - I
	},
	getListHeight: function(){
	    // get the height of the containing TD
	    var v$=this;
	    return( v$.tblContainer? v$.tblContainer.parentNode.parentNode.offsetHeight : v$.getContainerHeight() );
	},
	getContainerWidth: function(){
	    var v$=this;
	    if( !v$.oContainer ) {
	        v$.oContainer=dojo.doc.getElementById(v$.sContainerId);
	        v$.minW = 20 * v$.nColumnSpan;
	    }
	    var w=v$.oContainer.clientWidth;
	    return w;
	},
	getContainerHeight: function(){
	    var v$=this;
	    if( !v$.oContainer ){
	        v$.oContainer=dojo.doc.getElementById(v$.sContainerId);
	    }
	    return (v$.oContainer? v$.oContainer.clientHeight : 0);
	},
	isSelected: function(sUnid){
	    // ------------------------------------------
	    // determines if the object is part of the
	    //  selected rows
	    // ------------------------------------------
	    var v$ = this;
	    if (typeof sUnid == 'object')
	    {
	        var oTr=v$.getRowElement(sUnid);
	        if( oTr ){
	            var t$=v$.getTreeViewEntry(oTr);
	            if( t$ ) sUnid=t$.getUnid();
	        }
	        else{
	            sUnid=null;
	        }
	    }
	    if (!sUnid) return false;
	    var aUnids = v$.getSelectedData('UNID');
	    if (aUnids){
	        // make sure that the row being dragged is one
	        //  of the selected rows
	        var iMax = aUnids.length;
	        for (var i=0; i < iMax; i++) {
	            if (sUnid == aUnids[i]){
	                return true;
	            }
	        }
	    }
	    return false;
	},
	refresh: function(bReload, bResetPosition, bExpand, bCollapse, bFocus, oCollCtx, nSelectionMode){
	    // ------------------------------------------
	    // reset contents of data table
	    // ------------------------------------------
	    
	    
	    var v$ = this;
	    if(v$.fnBeforeRefresh)
	        v$.fnBeforeRefresh();
	
	    if ( typeof(bFocus) == "undefined" )
	        bFocus = true;
	
	    // should deselect all the entries
	    v$.deselectEntries();
	
	    // set the sorted column image
	    v$.setSortGif();
	    v$.setNarrowTitle();
	
	    // are we searching?
	    if (v$.bProcessingSearch) {
	        // then jump to the desired position
	        var sTumbler = v$.findEntry();
	
	        // confirm the found entry is in range of view if in contacts view.
	        if(v$.isQuickSearchMode() && sTumbler && sTumbler != "last")
	        {
	            var sTopTumbler = v$.getTopBorderEntry();
	            var sBottomTumbler = v$.getBottomBorderEntry();
	            
	            if(sTopTumbler && dwa.lv.colpos.compare(sTumbler, sTopTumbler) == 1)
	            {
	                sTumbler = '';
	                window.alert( this._smsgs[ "L_ENTRY_NOT_FOUND" ] );
	            }
	            else
	            if(sBottomTumbler && dwa.lv.colpos.compare(sBottomTumbler, sTumbler) >= 0)
	            {
	                sTumbler = '';
	                window.alert( this._smsgs[ "L_ENTRY_NOT_FOUND" ] );
	            }
	        }
	        v$.showWaitBox(true);
	        setTimeout(function(){v$.jumpToAfterRefresh(sTumbler)}, 0);
	    }
	    // just refresh
	    else{
	        var bStatusChanged = false;
	        bStatusChanged = bResetPosition ? true : bStatusChanged;
	        bStatusChanged = typeof bExpand != 'undefined' ? true : bStatusChanged;
	        bStatusChanged = typeof bCollapse != 'undefined' ? true : bStatusChanged;
	        if(bStatusChanged)
	            v$.iCurrentPosition = 0;
	
	        v$.bExpandAll = typeof bExpand != 'undefined' ? !!bExpand : v$.bExpandAll;
	        v$.bCollapseAll = typeof bCollapse != 'undefined' ? !!bCollapse : v$.bCollapseAll;
	        setTimeout(function(){v$.refreshAfterTimeout(bReload, bStatusChanged, bFocus, oCollCtx, nSelectionMode)}, 0);
	        if(bReload || bStatusChanged)
	            v$.showWaitBox(true);
	    }
	    
	},
	getSelectedRows: function(p1){
	    // p1== Don't return array if only oneR row is selected
	    return this.getDD().getSelected(p1,this);
	},
	refreshAfterTimeout: function( bReload, bResetPos, bFocus, oCollCtx, nSelectionMode ){
	    // ------------------------------------------
	    // reset contents of data table
	    // ------------------------------------------

	    var v$=this;
	
	    if (oCollCtx)
	        v$.applyCollectionContext(oCollCtx);
	
	    // Keep trying to refresh the UI until it behaves properly
	    if (!v$.bPostInit)
	        v$.postInit();
	
	    ;
	
	    if (bResetPos) {
	        // reset back to top of view
	        v$.start = v$.sThreadRoot ? dwa.lv.colpos.getTopLevel(v$.sThreadRoot ): '1';
	    }
	    else{
	        // save current position in view
	        v$.start = v$.sThreadRoot ? dwa.lv.colpos.getTopLevel(v$.sThreadRoot ): (v$.oRoot.getTumblerByIndex(v$.iCurrentPosition )? v$.oRoot.getTumblerByIndex(v$.iCurrentPosition ): '1');
	    }
	
	    // replace table
	    if(bReload)
	        v$.clear();
	    
	    var sFocusTumbler = v$.sThreadRoot ? v$.sThreadRoot : v$.sSelectedTumbler;
	    v$.adjusting = true;
	    v$.adjustCount();
	    v$.updateScrollBar();
	
	    if(0 == v$.adjustTable(0, v$.sThreadRoot, false, function(){v$.processRefreshAfterTimeout(bReload, bResetPos, bFocus, nSelectionMode)}, bReload))
	        v$.processRefreshAfterTimeout(bReload, bResetPos, bFocus, nSelectionMode);
	
	    
	},
	processRefreshAfterTimeout: function(bReload, bResetPos, bFocus, nSelectionMode){
	    
	
	    var v$ = this;
	
	    v$.updateScrollBar();
	    v$.adjusting = false;
	
	    // keep position if needed, otherwise focus first entry on screen
	    if(v$.sThreadRoot) {
	        v$.sSelectedTumbler = v$.sThreadRoot;
	        v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	        v$.selectEntry(v$.getRowByIndex(v$.iSelectedPosition - v$.viewPos(0)), void 0, void 0, void 0, nSelectionMode);
	    }
	    else
	    {
	        if(bResetPos)
	            v$.iSelectedPosition = v$.iCurrentPosition;
	        else if(v$.iSelectedPosition > v$.getLastEntryPosInView())
	            v$.iSelectedPosition = v$.getLastEntryPosInView();
	        else
	            v$.iSelectedPosition = !v$.oRoot.getEntryByTumbler(v$.sSelectedTumbler) ? v$.iCurrentPosition : v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	        v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	        v$.selectEntry(v$.getRowByIndex(v$.iSelectedPosition - v$.viewPos(0)), void 0, void 0, void 0, nSelectionMode);
	    }
	
	    v$.showWaitBox(false);
	    if(v$.fnAfterRefresh)
	        v$.fnAfterRefresh();
	
	    this.adjustElementSizes();
	
	    if (bFocus)
	        v$.focus();
	
	    return 0;
	},
	recalcHeight: function(){
	    this.adjusting = true;
	    this.adjustCount();
	    this.adjustTable(false, this.sSelectedTumbler);
	    this.updateScrollBar();
	    this.adjusting = false;
	},
	clear: function(){
	    this.destroyRoot();
	    this.oRoot = this.getNewRoot();
	    this.bOnFirstLoadingProcess = true;
	    if(this.bExpandAll)
	        this.oRoot.setAllExpanded(true);
	    
	    this.adjusting = true;
	    this.deleteRows(-1);
	    this.showScrollHint(false);
	    this.nScrollHintStatus = 0;
	    this.aScrollHints = [];
	    this.nScrollHintLastTime = 0;
	    this.nScrollHintCumulativeDiffs = 0;
	    if(this.hScrollHintTimer)
	        clearTimeout(this.hScrollHintTimer);
	    this.hScrollHintTimer = null;
	    this.sLastPath = '';
	    this.adjusting = false;
	},
	update: function(bFocus, asUnids, nSelectionMode){
	    // this function is designed to do a limited update after 
	    //  a soft deletion of rows is performed
	    
	
	    if( this.hasResponseColumn())
	    {
	        // we must reload entries at this time if has response column
	        // because the structure of viewentries always depends on the design of the view after deletion
	        return this.refresh(true, void 0, void 0, void 0, bFocus);
	    }
	    
	    var aUNIDs = asUnids ? asUnids : this.getSelectedValues();
	    this.deselectEntries();
	    if( aUNIDs && aUNIDs.length>0 )
	    {
	        for(var i=0;i<aUNIDs.length;i++) {
	            // delete the TreeViewEntry
	            var aEntries = this.getEntriesByUnid(aUNIDs[i]);
	            if(!aEntries || aEntries.length == 0) {
	                // deleted not yet loaded entries, can not reconstruct tree. excute refresh.
	                return this.refresh(true, void 0, void 0, void 0, bFocus);
	            }
	
	            // delete the HTML row
	            this.deleteRowsByUnid(aUNIDs[i]);
	
	            for(var j=0;j<aEntries.length;j++) {
	                var oEntry = aEntries[j];
	                if(oEntry) {
	                    if(this.iSelectedPosition > this.oRoot.getIndexByTumbler(oEntry.getTumbler()))
	                        this.iSelectedPosition--;
	                    this.oRoot.removeEntry(oEntry);
	                }
	            }
	        }
	        this.oRoot.update();
	        this.updateTable();
	    }
	
	    this.bCachedViewPosIsInvalid = true;
	    this.bCachedCalcTopRowIsInvalid = true;
	    this.bCachedCalcBottomRowIsInvalid = true;
	
	    this.adjusting = true;
	    this.deleteRows(-1);
	    this.adjustCount();
	    this.adjustTable();
	    this.adjusting = false;
	
	    if(this.iSelectedPosition > this.getLastEntryPosInView())
	        this.iSelectedPosition = this.getLastEntryPosInView();
	    this.sSelectedTumbler = this.oRoot.getTumblerByIndex(this.iSelectedPosition);
	    this.selectEntry(this.getRowByIndex(this.getSelectedRowIndex()), void 0, void 0, void 0, nSelectionMode);
	
	    this.adjustElementSizes();
	
	    if(bFocus)
	        this.focus();
	
	    
	},
	resortByColumn: function(nCol, oCollCtx, bByUser, bFocus){
	    // ------------------------------------------
	    // do a refresh using "&ResortAscending" URL tag
	    // ------------------------------------------
	    if ("undefined" != typeof(nCol)) {
	        if (nCol < 0 || nCol >= this.nColumnSpan) {
	            nCol = -1;
	        }
	
	        // sort by new column
	        //  (if applicable)
	        if( this.setSortInfo(nCol) ){
	            // set a flag to force data replacement
	            this.bInvalidData = true;
	        }
	        else {
	/*            // a reference to the (Rules) folder enters an infinite loop here
	
	            // comment from DG:
	            // it is WRONG to change a generic tool like the VList with
	            // specifics from a calling app, but since the damage is done,
	            // we must live with this bandage for now.
	            // i have reordered the code because the check for "s_ViewName"
	            // will bypass the original code if "s_ViewName" is not found
	
	            // if go back to default sorting, use nDefaultSortBy property if it is set.
	            if ( this.nDefaultSortBy != VL_UNSET && this.nDefaultSortBy != this.nSortBy){
	                if (window.s_ViewName && D_NotesInboxRulesFolder == window.s_ViewName){
	                    return;
	                }
	                // use default sorting
	                this.resortByColumn(this.nDefaultSortBy, oCollCtx, bByUser );
	                // exit the function when done.
	                return;
	            }
	            else
	*/
	                this.setSortInfo(-1);
	        }
	    }
	    
	    //notify the caller the sort order has been changed.
	    if (this.fnSortChanged)
	        this.fnSortChanged( this.nSortBy, bByUser );
	
	    // refresh view
	    this.refresh(!oCollCtx, !oCollCtx, void 0, void 0, bFocus, oCollCtx);
	},
	doScroll: function(event_type, sFocusTumbler){
	    // ------------------------------------------
	    // scroll data to the position dictated by 
	    // the event, then adjust scroll bar
	    // ------------------------------------------
	    
	
	    var v$ = this;
	    var iOldCurrentPosition = this.iCurrentPosition;
	    var iOldDiffPosition = this.iDiffPosition;
	
	    this.sOldTumbler = this.oRoot.getTumblerByIndex(this.iCurrentPosition);
	    var iNewPosition = this.oScrollBar.getCurrentPos();
	    this.iDiffPosition = iNewPosition - this.iCurrentPosition;
	    var bUp = (this.iDiffPosition < 0);
	    this.iCurrentPosition = iNewPosition;
        // event_type is changed in onPosChanged() not here
	    //if( this.oScrollBar.isBottom() && 'BOTTOM'!=event_type ) event_type='BOTTOM';
	    
	    switch(event_type)
	    {
	        case 'UP':
	        case 'PAGEUP':
	        case 'PAGEDOWN':
	            break;
	        case 'DOWN':
	            break;
	        case 'DROPED':
	            this.sOldTumbler = '';
	            this.iDiffPosition = 0;
	            break;
	        case 'TOP':
	            this.iCurrentPosition = 0;
	            this.sOldTumbler = '';
	            this.iDiffPosition = 0;
	            break;
	        case 'BOTTOM':
	            if(this.isRangeMode())
	            {
	                this.iCurrentPosition = Math.max(0, this.getTotalEntriesInView() - (this.iMaxRowsPerPage - 1));
	                sFocusTumbler = this.oRoot.getTumblerByIndex(this.getLastEntryPosInView());
	            }
	            else
	            {
	                this.iCurrentPosition = Math.max(0, this.getTotalEntriesInView() - (this.iMaxRowsPerPage - 1));
	                sFocusTumbler = 'last';
	            }
	            this.sOldTumbler = '';
	            this.iDiffPosition = 0;
	            break;
	    }
	    
	    var nErrorStatus = this.adjustTable(0, sFocusTumbler, false, function(){return v$.moveCaretAfterScroll()}, false, bUp);
	    if(nErrorStatus == 2) {
	        this.iCurrentPosition = iOldCurrentPosition;
	        this.sOldTumbler = this.oRoot.getTumblerByIndex(this.iCurrentPosition);
	        this.iDiffPosition = iOldDiffPosition;
	    }
	    else {
	        v$.moveCaretAfterScroll(nErrorStatus == 1);
	    }
	    return nErrorStatus;;
	},
	getSelectedRowIndex: function(){
	    return this.iSelectedPosition - this.viewPos(0);
	},
	getTopVisibleRowIndex: function(){
	    return this.iCurrentPosition - this.viewPos(0);
	},
	getNumVisibleRows: function(){
	    return this.calcBottomRow() - this.calcTopRow() + 1;
	},
	calcTopRow: function(){
	    // ------------------------------------------
	    // attempt to find the top-most visible row
	    // ------------------------------------------
	
	    // cache return value of this.calcRow() for performance issue
	    if(this.bCachedCalcTopRowIsInvalid)
	    {
	        var iTop = this.calcOffset();
	        var iDef = this.getTopVisibleRowIndex();
	
	        this.bCachedCalcTopRowIsInvalid = false;
	        this.cachedCalcTopRow = this.calcRow(iDef,iTop);
	
	    }
	
	
	    return this.cachedCalcTopRow;
	},
	calcBottomRow: function(){
	    // ------------------------------------------
	    // attempt to find the bottom-most visible row
	    // ------------------------------------------
	
	    // cache return value of this.calcRow() for performance issue
	    if(this.bCachedCalcBottomRowIsInvalid)
	    {
	        var iTop = this.tblContainer.parentNode.offsetHeight - this.calcOffset();
	        var iDef = this.getRowIndex(this.getLastEntryPosInView());
	
	        // return the calculated row
	        var iMax = this.getTopVisibleRowIndex();
	        var iBtmRow = Math.max(iMax, this.calcRow(iDef,iTop));
	        var iPage   = iMax + this.iMaxRowsPerPage;
	        this.bCachedCalcBottomRowIsInvalid = false;
	        this.cachedCalcBottomRow = Math.min(iBtmRow, iPage);
	
	    }
	    
	
	    return this.cachedCalcBottomRow;
	},
	calcRow: function(iDefault, iTopOffset){
	    // ------------------------------------------
	    // common code for calcBottomRow and calcTopRow
	    // ------------------------------------------
	    var v$ = this;
	    var iRow = -1;
	//#if defined(GECKO) || defined(SAFARI)
	    // use this code for all the cases sinse getComponentFromPoint always returns focus element inspite of its zIndex==-1
	    var posY = iTopOffset;
	    var posX = 0;
	    var oTr  = null;
	    var nChild = 0;
	    var currentTop = dwa.lv.benriFuncs.elGetCurrentStyle(v$.tblContainer, 'top', true);
	    while(oTr = v$.getRowByIndex(nChild))
	    {
	        currentTop += oTr.offsetHeight;
	        if(iTopOffset < currentTop)
	            return nChild;
	        nChild++;
	    }
	    if(!oTr)
	        return (nChild-1);
	
	    // return the default row, since we could not find the bottom row
	    return iDefault;
	},
	calcNewTopRow: function(iDefault, nIndexBottomRow){
	    var iTopOffset = 0;
	
	    var oBottom = this.getRowByIndex(nIndexBottomRow);
	    if(!oBottom)
	        return iDefault;
	    
	    var iTopRowOffset = (oBottom.offsetTop + oBottom.offsetHeight) - this.getListHeight() + iTopOffset;
	    var nRowIndex = this.calcTopRow();
	    var oTopRow = this.getRowByIndex(nRowIndex);
	
	    // move to next row, for in the case of last entry can not be focused in contacts view
	    if(oTopRow.offsetTop < iTopRowOffset && iTopRowOffset < oTopRow.offsetTop + oTopRow.offsetHeight)
	        return nRowIndex + 1;
	
	    if(iTopRowOffset < oTopRow.offsetTop)
	    {
	        // search backward
	        var nLastIndex = -1;
	        for(var i=nRowIndex;i>=0;i--)
	        {
	            var oTR = this.getRowByIndex(i);
	            if(!oTR)
	                return iDefault;
	            if(oTR.offsetTop < iTopRowOffset)
	                return nLastIndex!=-1 ? nLastIndex : i;
	            nLastIndex = i;
	        }
	    }
	    else
	    {
	        // search forward
	        for(var i=nRowIndex;i<=nIndexBottomRow;i++)
	        {
	            var oTR = this.getRowByIndex(i);
	            if(!oTR)
	                return iDefault;
	            if(iTopRowOffset <= oTR.offsetTop + oTR.offsetHeight)
	                return i;
	        }
	    }
	    
	    return iDefault;
	},
	calcOffset: function(){
	    return this.iOneRowHeight - 1;
	},
	isRowFullyDisplayed: function(oRow){
	    if(!oRow || !oRow.parentNode)
	       return false;
	    
	    var bInArea = (oRow.offsetTop + this.tblContainer.offsetTop >= 0) && (oRow.offsetTop + oRow.offsetHeight + this.tblContainer.offsetTop <= this.tblContainer.parentNode.clientHeight);
	    var bTooFat = oRow.offsetHeight >= this.tblContainer.parentNode.clientHeight;
	    return bInArea || bTooFat;
	},
	adjustTable: function(nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, bEntryUpdated, bUp, sCollapsedTumbler){

	    var v$ = this;
	    var bRequestedAsync = false;
	    if(v$.bOnFirstLoadingProcess)
	    {
	        var sTumbler = '';
	        for(var i=0;i<=dwa.lv.colpos.getDepth(v$.start);i++)
	        {
	            sTumbler += (sTumbler ? '.' : '') + dwa.lv.colpos.getNDepthIndex(v$.start, i);
	            var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
	            if(!oEntry) {
	                // check parent for refresh ftsearched view & blank view
	                var oParent = v$.oRoot.getEntryByTumbler(dwa.lv.colpos.getParent(sTumbler));
	                if(!oParent || oParent.getChildLoaded())
	                     break;
	                
	                // Ignore MinChunk to reduce size of response data on first fetch for performance
	                nErrorStatus = v$.loadData(sTumbler, v$.getMinChunk(true), false, nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, false, bUp);
	                if(nErrorStatus == 2)
	                    v$.iCurrentPosition = 0;
	                if(nErrorStatus == 1) {
	                    bRequestedAsync = true;
	                    v$.setUpdatingViewTimer();
	                }
	                if(nErrorStatus != 0)
	                    break;
	                
	                oEntry = v$.oRoot.getEntryByTumbler(sTumbler);
	            }
	            if(i<dwa.lv.colpos.getDepth(v$.start) && oEntry && oEntry.hasChildren())
	            {
	                // oEntry.update will be called when child nodes loaded
	                oEntry.expand(true);
	            }
	        }
	        // need to reset position after first loading process
	        if(!bRequestedAsync && v$.bOnFirstLoadingProcess) {
	            v$.bOnFirstLoadingProcess = false;
	            if(sTumbler == v$.start && v$.oRoot.getIndexByTumbler(sTumbler))
	                v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(sTumbler);
	            else
	                v$.iCurrentPosition = 0;
	        }
	    }
	
	    var nMaxRows = v$.nLimit ? v$.nLimit : v$.iMaxRowsPerPage;
	    var nFocusIndex = sFocusTumbler ? (sFocusTumbler == 'last' ? v$.getLastEntryPosInView() : v$.oRoot.getIndexByTumbler(sFocusTumbler)) : 0;
	
	    if(sFocusTumbler == 'last' && v$.getTotalEntriesInView())
	    {
	        if(!v$.oRoot.getEntryByIndex(nFocusIndex, true) && dwa.lv.colpos.getDepth(v$.oRoot.getTumblerByIndex(nFocusIndex))==0)
	        {
	            nErrorStatus = v$.loadData(v$.isRangeMode() ? v$.oRoot.getTumblerByIndex(nFocusIndex) : sFocusTumbler, v$.isRangeMode() ? 1 : v$.getMinChunk(), false, nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, false, bUp);
	            if(nErrorStatus == 2)
	            {
	                v$.iCurrentPosition = 0;
	                return nErrorStatus;
	            }
	            if(nErrorStatus == 1) {
	                bRequestedAsync = true;
	                ;
	            }
	        }
	        if(v$.iCurrentPosition > v$.getLastEntryPosInView())
	            v$.iCurrentPosition = v$.getTotalEntriesInView() - v$.iMaxRowsPerPage;
	        if(v$.iCurrentPosition < v$.getTotalEntriesInView() - v$.iMaxRowsPerPage)
	            v$.iCurrentPosition = v$.getTotalEntriesInView() - v$.iMaxRowsPerPage;
	        if(v$.iCurrentPosition < 0)
	            v$.iCurrentPosition = 0;
	    }
	    else if(sFocusTumbler && bFocusToBeTop && v$.iCurrentPosition != nFocusIndex)
	    {
	        v$.iCurrentPosition = Math.max(0, nFocusIndex);
	    }
	    else if(sFocusTumbler && v$.iCurrentPosition < nFocusIndex - nMaxRows + 1)
	    {
	        v$.iCurrentPosition = Math.max(0, nFocusIndex - nMaxRows + 1);
	    }
	
	    v$.adjustCount();
	
	    var oInfo = v$.calcInvalidateInfo(void 0, bEntryUpdated || v$.getListMode());
	
	    var iStart = oInfo.iStart;
	    var nCount = oInfo.nCount;
	    var iTblIndex = oInfo.iTblIndex;
	    var nDeleteType = oInfo.nDeleteType;
	    var nDeleteRows = oInfo.nDeleteRows;
	    var bAddedToTop = oInfo.bAddToTop;
	    
	    
	
	    if(nDeleteType != 0)
	        v$.deleteRows(nDeleteType, nDeleteRows);
	
	    if(nCount)
	    {
	        var bLoaded = bRequestedAsync;
	        var nTotalEntries = v$.getTotalEntriesInView();
	        var nEndPos = v$.getLastEntryPosInView();

            if( v$.oRoot.indexForDummy ){
                if( !bLoaded ){
                    var oRet = v$.oRoot.getRequestRangeEx( oInfo, v$.getMinChunk(), bUp, sCollapsedTumbler );

                    if( oRet.position ){
                        nErrorStatus = v$.loadData( oRet.position, oRet.nCount, oRet.bNavigateReverse, nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, false ,bUp);
                        if(nErrorStatus == 2)
                            return nErrorStatus;
                        // just return to load entries background and to not draw entries on screen at v$ time to not show script busy warning.
                        if(nErrorStatus == 1 && v$.bIsPrintMode)
                            return nErrorStatus;
                        if(v$.sOldTumbler)
                        {
                            v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(v$.sOldTumbler) + v$.iDiffPosition;
                            if(v$.iCurrentPosition < 0) v$.iCurrentPosition = 0;
                        }
                    }
                }
            }else{
    	        for(var i=iStart,iMax=Math.min(iStart+nCount,nTotalEntries);i<iMax;i++)
    	        {
    	            var oEntry = v$.oRoot.getEntryByIndex(i);
    	            if(!oEntry && !bLoaded)
    	            {
    	                var sTumbler = v$.oRoot.getTumblerByIndex(i);
    	                // FTsearched view offten requests out of range entries
    	                if(!sTumbler)
    	                    continue;
    	                var sParentTumbler = dwa.lv.colpos.getParent(sTumbler);
    	                var oParent = v$.oRoot.getEntryByTumbler(sParentTumbler);
    	                var nIndex = dwa.lv.colpos.getLeafIndex(sTumbler);
    	                
    	                if(!oParent || ((nIndex < oParent.getStartIndex() || oParent.getEndIndex() < nIndex || oParent.oIgnoreCollection.find(nIndex)) && oParent.getChildLoaded()))
    	                    continue;
    	                
    	                if(oParent.oCollection.find(nIndex))
    	                    continue;
    	                
    	                var oRet = oParent.getRequestRange(nIndex, v$.getMinChunk(), bUp);
    	                // try to load more entries if no one is loaded next to range.
    	                var nReqCount = oRet.nEnd - oRet.nStart + 1;
    	                var nTryReqCount = i + nReqCount;
    	                if(!(nTryReqCount <= nEndPos && v$.oRoot.getEntryByIndex(nTryReqCount)) && !v$.isRangeMode()/*YCDL7FS866*/)
    	                    nReqCount = Math.max(nReqCount, v$.getMinChunk());
    	                nErrorStatus = v$.loadData(dwa.lv.colpos.getLeaf(sParentTumbler, oRet.bNavigateReverse ? oRet.nEnd : oRet.nStart), nReqCount, oRet.bNavigateReverse, nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, false, bUp);
    	                if(nErrorStatus == 2)
    	                    return nErrorStatus;
    	                // just return to load entries background and to not draw entries on screen at v$ time to not show script busy warning.
    	                if(nErrorStatus == 1 && v$.bIsPrintMode)
    	                    return nErrorStatus;
    	                
    	                if(v$.sOldTumbler)
    	                {
    	                    v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(v$.sOldTumbler) + v$.iDiffPosition;
    	                    if(v$.iCurrentPosition < 0) v$.iCurrentPosition = 0;
    	                }
    	                bLoaded = true;
    	            }
    	        }
            }


	
	        // generate list view HTML
	        var clrEven = ' vl-row-even';
	        var clrOdd  = v$.bAlternateRows? ' vl-row-odd':clrEven;
	    
	        var a1=[],n=0;
	        var bColor=iStart%2, nRowsAdded=0;
	        for(var i=iStart,iMax=Math.min(iStart+nCount,nTotalEntries);i<iMax;i++)
	        {
	            var oEntry = v$.oRoot.getEntryByIndex(i, true);
	            n=v$.insertRowHTML(oEntry,(bColor? clrOdd : clrEven),v$.oRoot.getTumblerByIndex(i),a1,n);
	            
	            bColor=!bColor;
	            nRowsAdded++;
	        }
	
	        // Insert the huge HTML block
	        var b0 = v$.getRowByIndex(iTblIndex);
	
	        // insert the new row before the first row or append the new row to the end of the view
	 if( dojo.isIE ){
	        v$.tbl.insertAdjacentHTML(b0 ? 'afterBegin' : 'beforeEnd', a1.join(''));
	 }else{ // I
	        var oRange = dojo.doc.createRange();
	        oRange.setStartBefore(v$.tbl);
	        v$.tbl.insertBefore(oRange.createContextualFragment(a1.join('')), b0 ? v$.tbl.firstChild : null);
	 } // end - GS
	        
	        // reselect again to hilight
            var iSaveSelectedPosition = this.iSelectedPosition;

	        for(var i=0,imax=Math.min(nRowsAdded, v$.tbl.childNodes.length),j=v$.tbl.childNodes.length-1;i<imax;i++,j--){
	            var oRow = v$.tbl.childNodes[b0?i:j];
	            if(oRow.getAttribute('tumbler') && v$.getDD().isSelected(oRow, v$, oRow.getAttribute('tumbler')))
	                v$.selectEntryWithoutCB(oRow, true);
	        }
            this.iSelectedPosition = iSaveSelectedPosition;
	
	        v$.bCachedViewPosIsInvalid = true;
	        v$.bCachedCalcTopRowIsInvalid = true;
	        v$.bCachedCalcBottomRowIsInvalid = true;
	    }
	    v$.sOldTumbler = '';
	    v$.iDiffPosition = 0;
	
	    // recalc position if the entry that should be focused is out of screen.
	    if(typeof v$.viewPos(0) != 'undefined' && !v$.bIsPrintMode)
	    {
	        var iOrgPosition = v$.iCurrentPosition;
	        
	        // need to move table to new position to retrieve correct value from calcBottomRow, calcTopRow, getTopVisibleRowIndex
	        var oTR = v$.getRowByIndex(v$.getTopVisibleRowIndex());
	        if (oTR)
	            v$.tblContainer.style.top = (0 - oTR.offsetTop) + 'px';
	        else
	            v$.tblContainer.style.top = '0px';
	
	        oTR = null;
	        if(sFocusTumbler)
	        {
	            var bAdjustTop=false;
	            var oLastRow = v$.getRowByIndex(v$.calcBottomRow());
	            if(oLastRow && dwa.lv.colpos.compare(oLastRow.getAttribute("tumbler"), sFocusTumbler) > 0)
	            {
	                bAdjustTop=true;
	            }
	            else{
	                var oTopRow = v$.getRowByIndex(v$.calcTopRow());
	                if(oTopRow && dwa.lv.colpos.compare(oTopRow.getAttribute("tumbler"), sFocusTumbler) < 0)
	                {
	                    bAdjustTop=true;
	                }
	            }
	            if(bAdjustTop){
	                var iNewTopRow = v$.calcNewTopRow(v$.getTopVisibleRowIndex(), nFocusIndex - v$.viewPos(0));
	                iNewTopRow = Math.max(iNewTopRow, v$.getTopVisibleRowIndex());
	                
	                oTR = v$.getRowByIndex(iNewTopRow);
	                if(oTR)
	                    v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(oTR.getAttribute("tumbler"));
	            }
	        }
	        if(!oTR)
	        {
	            oTR = v$.getRowByIndex(v$.getTopVisibleRowIndex());
	        }
	        if (oTR)
	            v$.tblContainer.style.top = (0 - oTR.offsetTop) + 'px';
	        else
	            v$.tblContainer.style.top = '0px';
	
	        v$.bCachedCalcTopRowIsInvalid = true;
	        v$.bCachedCalcBottomRowIsInvalid = true;
	
	        if(iOrgPosition != v$.iCurrentPosition)
	            return v$.adjustTable(nErrorStatus, sFocusTumbler, false, fnCBOnLoaded, bEntryUpdated, bUp);
	    }
	    else
	    {
	        v$.tblContainer.style.top = '0px';
	
	        v$.bCachedCalcTopRowIsInvalid = true;
	        v$.bCachedCalcBottomRowIsInvalid = true;
	    }
	
	    v$.updateScrollBar();
	
	    
	    ;
	    return nErrorStatus;
	},
	getMinChunk: function(bIgnoreMinChunk){
	    var nCalcChunk = ((Math.floor((this.iMaxRowsPerPage - 1) / 20) + 1) * 20);
	    var nMinChunk = bIgnoreMinChunk ? this.iMaxRowsPerPage + (this.isCategorizedView() ? 0 : this.bNarrowMode ? 3 : 5) : (/* for print mode */ dwa.lv.globals.get().oSettings.nMaxViewRows ? Math.min(dwa.lv.globals.get().oSettings.nMaxViewRows, nCalcChunk) : nCalcChunk);
	    return this.nLimit ? this.nLimit : nMinChunk;
	},
	calcInvalidateInfo: function(nNewPos, bClearIfDummyExist){
	    var oRet = {};
	
	    
	    
	    
	    
	    if(typeof nNewPos == 'undefined')
	        nNewPos = this.iCurrentPosition;
	    
	    oRet.bAddToTop = false;
	    oRet.iStart = 0;
	    oRet.nCount = 0;
	    oRet.iTblIndex = 0;
	    oRet.nDeleteType = 0;
	    oRet.nDeleteRows = 0;
	
	    var iTop = this.viewPos(0);
	    var iLast = this.viewPos("last");
	    var nCount = iLast - iTop + 1;
	    var nMinChunks = this.getMinChunk();
	    var nMaxRows = this.nLimit ? Math.min(this.nLimit, this.iMaxRowsPerPage) : this.iMaxRowsPerPage;
	    var nEndPos = Math.min(this.getLastEntryPosInView() ,nNewPos + nMaxRows - 1);
	    
	    
	    
	    
	    
	    
	
	    // check if rows have dummy entries
	    var bHasDummyEntry = false;
	    for(var i=0;this.tbl && i<this.tbl.childNodes.length;i++) {
	        if(this.tbl.childNodes[i].getAttribute('isempty')) {
	            bHasDummyEntry = true;
	            break;
	        }
	    }
	    
	
	    if(bHasDummyEntry && bClearIfDummyExist) {
	        // need to clear once to remove dummy entries
	        oRet.iStart = nNewPos;
	        oRet.nCount = nMaxRows;
	        oRet.iTblIndex = 0;
	        oRet.nDeleteType = -1;
	        oRet.nDeleteRows = nCount;
	        
	        
	        
	        
	        
	        
	        return oRet;
	    }
	
	    if(typeof iTop == 'undefined')
	    {
	        
	        
	        // no rows on this.tbl
	        oRet.iStart = nNewPos;
	        oRet.nCount = nMaxRows;
	        oRet.iTblIndex = 0;
	        
	        
	        
	        
	        
	        
	        return oRet;
	    }
	
	    else
	    if(!(nNewPos <= (iLast+1) &&  nEndPos >= (iTop-1)))
	    {
	        
	        
	        // no valid rows on this.tbl
	        oRet.iStart = nNewPos;
	        oRet.nCount = nMaxRows;
	        oRet.iTblIndex = 0;
	        oRet.nDeleteType = -1;
	        oRet.nDeleteRows = nCount;
	        
	        
	        
	        
	        
	        
	        return oRet;
	    }
	
	    // some valid rows on this.tbl
	    if(nNewPos < iTop)
	    {
	        
	        
	        // performance issue
	        var nNewTop = Math.max(0, iTop - nMinChunks);
	        oRet.bAddToTop = true;
	        oRet.iStart = nNewTop;
	        oRet.nCount = iTop - nNewTop;
	        oRet.iTblIndex = 0;
	        
	
	        var nNewCount = nCount + oRet.nCount;
	        
	        if(nNewCount > nMaxRows * 2)
	        {
	            oRet.nDeleteType = 2;
	            oRet.nDeleteRows = nNewCount - nMaxRows * 2;
	            
	            
	            
	            // check if going to remove entry on screen
	            var nTooMuch = Math.max(0, oRet.nDeleteRows - Math.max(0, iLast - (nNewPos + nMaxRows - 1)));
	            
	            
	            if(nTooMuch>0) {
	                oRet.nDeleteRows -= nTooMuch;
	                oRet.iStart += nTooMuch;
	                oRet.nCount -= nTooMuch;
	                
	                
	            }
	        }
	        
	    }
	    else
	    if(nEndPos > iLast)
	    {
	        
	        
	        // performance issue
	        var nNewEndPos = Math.min(this.getLastEntryPosInView(), this.bisPrintMode ? nMaxRows : (iLast + nMinChunks));
	        oRet.iStart = iLast + 1;
	        oRet.nCount = nNewEndPos - iLast;
	        oRet.iTblIndex = this.tbl.childNodes.length;
	        
	
	        var nNewCount = nCount + oRet.nCount;
	        
	        
	        if(nNewCount > nMaxRows * 2)
	        {
	            // remove from top
	            oRet.nDeleteType = 1;
	            oRet.nDeleteRows = nNewCount - nMaxRows * 2;
	            oRet.iTblIndex -= oRet.nDeleteRows;
	            
	            
	            
	            
	            // check if going to remove entry on screen
	            var nTooMuch = oRet.nDeleteRows - (nNewPos - iTop);
	            
	            
	            if(nTooMuch>0) {
	                oRet.nDeleteRows -= nTooMuch;
	                oRet.iTblIndex += nTooMuch;
	                oRet.nCount -= nTooMuch;
	                
	                
	                
	            }
	
	        }
	        
	    }
	    
	    
	    
	    
	    return oRet;
	},
	jumpTo: function(iPosition, bDontStealFocus, collapsedTumbler){
	    // ------------------------------------------
	    // moves view to a specified position in 
	    // the current Notes View or Folder
	    // ------------------------------------------
	    var v$ = this;
	    var iPosMax = this.getLastEntryPosInView();
	    if (0 == iPosMax && 0 == this.getTotalEntriesInView()){ v$.resetFlags(); return; }
	
	    // iPos should be between the first and last entries in the view (or tab)
	    //  check against the last position
	    var iPos = Math.min(typeof iPosition != 'undefined' ? iPosition : this.iCurrentPosition, iPosMax);
	    this.iCurrentPosition = Math.min(iPos, iPosMax);
	
	    this.adjusting = true;
        if( collapsedTumbler ){
            var x;
    	    this.adjustTable(x,x,x,x,x,x, collapsedTumbler);
        }else{
    	    this.adjustTable();
        }
	    this.updateScrollBar();
	
	    this.adjustElementSizes();
	
	    // select the entry found
	    if(!bDontStealFocus)
	    {
	        this.iSelectedPosition = this.iCurrentPosition;
	        this.sSelectedTumbler = this.oRoot.getTumblerByIndex(this.iSelectedPosition);
	        this.selectEntry(this.getRowByIndex(this.getSelectedRowIndex()));
	        this.focus();
	    }
	    this.showSelections();
	
	    // reset flags
	    v$.resetFlags();
	},
	jumpToAfterRefresh: function( sTumbler ){
	    // ------------------------------------------
	    // moves view to a specified position in 
	    // the current Notes View or Folder
	    // ------------------------------------------
	    
	    var v$=this;
	    v$.clear();
	    v$.iCurrentPosition = 0;
	    v$.start = sTumbler ? sTumbler : '1';
	
	    v$.adjusting = true;
	    if(0 == v$.adjustTable(0, sTumbler ? sTumbler : void 0, sTumbler ? true : false, function(){v$.processJumpToAfterRefresh(sTumbler)}))
	        this.processJumpToAfterRefresh(sTumbler);
	
	    return 0;
	},
	processJumpToAfterRefresh: function(sTumbler){
	    var v$=this;
	    v$.updateScrollBar();
	    v$.adjusting = false;
	
	    if(sTumbler)
	    {
	        v$.iSelectedPosition = (sTumbler=="last") ? v$.viewPos("last") : v$.oRoot.getIndexByTumbler(sTumbler);
	        v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	        v$.selectEntry(v$.getRowByIndex(v$.getSelectedRowIndex()));
	    }
	    else
	    {
	        // select the first row
	        if(v$.tbl.firstChild)
	            v$.selectEntry(v$.tbl.firstChild);
	    }
	
	    v$.showWaitBox(false);
	    
	    v$.focus();
	},
	calcMinRowHeight: function(){
	    // ------------------------------------------
	    //    inserts a new row into the data table
	    // ------------------------------------------
	    
	    
	    var v$=this;
	    if(v$.bResetMinRowHeight!=1) {
	        
	    }
	    
	    if( !v$.oContainer ) {
	        v$.oContainer=dojo.doc.getElementById(v$.sContainerId);
	        v$.minW = 20 * v$.nColumnSpan;
	    }
	
	    var doc = dojo.doc;
	    var oRow,oCell;
	    var oTbl = v$.tbl ? v$.tbl : v$.oContainer;
	
	    oRow = oTbl.appendChild(doc.createElement(v$.getRowTagName()));
	    if (!oRow){ return; }
	    oRow.className = v$.sRowFmt + ' ' + v$.sUnreadFmt;
	
	    // add data to the new row
	    // insert cell
	    oCell = oRow.appendChild(doc.createElement(v$.getCellTagName()));
	    if (!oCell){ return; }
	    oCell.className = v$.getCellFmt();
	    oCell.innerHTML = "W";
	    // calc width
	    v$.iCharWidth    = dwa.lv.benriFuncs.elGetCurrentStyle(oCell, 'width', true);
	    oRow.removeChild(oCell);
	
	    var sIconCellHtml
	     = '<IMG width="' + 13 + '" height="' + 11 + '" src="' + dwa.lv.globals.get().buildResourcesUrl('transparent.gif') + '" style="border:0px">';
	
	    // categorized view having no response column needs to be determined as non narrow view to calculate iOneRowHeight sinse category row does not expand 2 line.
	    var oLayoutInfo = v$.generateLayoutInfo(v$.aColInfo, v$.nColumnSpan, v$.isCategorizedView() && !v$.hasResponseColumn() ? false : v$.bNarrowMode);
	
	    for(var row=0;row<2;row++) {
	        var aRowInfo = v$.getSingleRowInfo(oLayoutInfo, !!row);
	        if(!aRowInfo || !aRowInfo.length)
	            continue;
	
	        if(row)
	            oRow.appendChild(doc.createElement('br'));
	
	        var bHasIMColInRow = false;
	        for(var i=0;i<aRowInfo.length;i++) {
	            var oColInfo = v$.aColInfo[aRowInfo[i].nRealIndex];
	            if(oColInfo.sIMColName)
	                bHasIMColInRow = true;
	        }
	
	        for(var i=0;i<aRowInfo.length;i++) {
	            var oCell = oRow.appendChild(doc.createElement(v$.getCellTagName()));
	            oCell.className = v$.getCellFmt();
	            var oColInfo = this.aColInfo[aRowInfo[i].nRealIndex];
	
	            var sHtml = oColInfo.sIMColName ? buildIMLink(dwa.lv.globals.get().oSettings.CalendarProfileOwnerName) :
	             oColInfo.bIsIcon ? v$.getHtmlInIconCell(sIconCellHtml, '', bHasIMColInRow) :
	             this._smsgs[ "L_VIEWMAIL" ];
	
	 if( dojo.isIE ){
	            oCell.insertAdjacentHTML('beforeEnd', sHtml);
	 }else{ // I
	            var oRange = dojo.doc.createRange();
	            oRange.setStartBefore(oCell);
	            oCell.appendChild(oRange.createContextualFragment(sHtml));
	 } // end - GS
	        }
	    }
	
	    // calc height
	    v$.iOneRowHeight = v$.iAvgRowHeight = Math.max(10, oRow.offsetHeight);
	
	    // remove this row
	    oTbl.removeChild(oRow); 
	
	    // cleanup
	    oRow=null;cell=null;
	
	    v$.bResetMinRowHeight=0;
	
	    
	},
	insertRowHTML: function(p1     ,p2     ,p3     ,a1     ,n      ){
	    // ------------------------------------------
	    // appends view row HTML to given array
	    // ------------------------------------------
	    
	    var v$=this;
	    var bEmptyEntry = !p1;
	    var sUnid   = !bEmptyEntry ? p1.getUnid() : '';
	    var ve$     = !bEmptyEntry ? p1.getViewEntry() : null;
	    var bUnread = v$.sUnreadFmt && ve$ ? v$.oDataStore.getAttributeBoolean(ve$, "unread") : false;
	    var s1      = (sUnid? (bUnread? v$.sUnreadFmt:'vl-font-n'):'vl-font-n');
	    var sTumbler= (!bEmptyEntry ? p1.getTumbler() : p3);
	
	    // dont write dummy entry if in print mode.
	    if(v$.bIsPrintMode && bEmptyEntry)
	        return n;
	
	    if( v$.fnClassRow ){
	        // let calling app format the row
	        s1=v$.fnClassRow(sUnid,p1,ve$,ve$,v$.getCellFmt(),v$.sColumnClassName,v$.getCellTagName(),v$.sId,s1);
	    }
	
	    a1[n++] ='<'+v$.getRowTagName()
	            // SPR: CXDI6BNCTG ... allow focus
	            + ' hidefocus="true" class="'+v$.sRowFmt+p2+' '+s1
	            + '" ' + 'com_ibm_dwa_ui_draggable' + '="' + (dojo._isBodyLtr() ? "left" : "right")
	            + '" tumbler="' 
	            +  sTumbler + '" id="' + v$.sId + '-row-' + sTumbler + '"'
	            + ' nowrap style="white-space:nowrap;"';
	
	    // row:  misc properties
	    if( sUnid )
	        a1[n++]=' unid="'+sUnid+'"';
	    if( bEmptyEntry )
	        a1[n++]=' isempty="true"';
	
	    var a2=[],n2=0,aTitles=[];
	    if( v$.fnFormatRow ){
	        // let calling app format the row
	        n2=v$.fnFormatRow(sUnid,p1,ve$,v$.getCellFmt(),v$.sColumnClassName,v$.getCellTagName(),v$.sId,a2,n2,aTitles);
	    }
	    else {
	        // --------------------------------------------------------------------
	        // Previously, there was Sametime awareness code mixed into the default
	        // row formatting code below. We should really make the default code 
	        // more generic and add special processing code to scenes that need ST awareness.
	        // --------------------------------------------------------------------
	
	        // add data to the new row
	        var aColData = !bEmptyEntry ? v$.getColumnValueArray(p1.getViewEntry()) : null;
	        var sStartCellTag = dwa.lv.listViewBase.prototype.LV$Static.getStartTag(v$.getCellTagName());
	        var sEndCellTag   = dwa.lv.listViewBase.prototype.LV$Static.getEndTag();
	        for (var ix=0; ix < v$.aLayoutInfo.length; ix++){
	            var oLayoutInfo = v$.aLayoutInfo[ix];
	            var i = oLayoutInfo.nRealIndex;
	            a2[n2++]=sStartCellTag
	                   +' class="'+v$.getCellFmt() + ((v$.aColInfo[i].bShowGradientColor || (oLayoutInfo.bWrap && v$.aColInfo[i].bAlignGradientColor)) ? '-gray' : '')
	                   +' '
	                   +v$.sColumnClassName +i
	                   +'" style="'+v$.aColInfo[i].sFmt
	                   + '">'
	                   +(!bEmptyEntry ? aColData[i] : '')
	                   +sEndCellTag;
	            if(oLayoutInfo.bWrapAfter)
	                a2[n2++]= '<br>';
	        }
	    }
	
	    // nakakura
	    if(this.bSupportScreenReader && aTitles.join(' ')){
	        a1[n++] = ' aria_label_row="' + dwa.lv.benriFuncs.escapeHtmlKeywords(aTitles.join(' '), 1|2|4|8) + '"';
	        for(var i = 0; i < aTitles.length; i++)
	            a1[n++] = ' aria_label_cell_' + i + '="' + dwa.lv.benriFuncs.escapeHtmlKeywords(aTitles[i], 1|2|4|8) + '"';
	    }
	
	    a1[n++]='>';
	    a1[n++]=a2.join('');
	    a1[n++] ='</'+v$.getRowTagName()+'>';
	    return n;
	},
	deleteRows: function(RemoveType, nQty){
	    // ------------------------------------------
	    // Clears the Data Table
	    //   nQuantity represents the new rows to be added
	    // ------------------------------------------
	    
	    var nTrimmed = 0;
	    
	
	    switch (RemoveType) {
	
	    case 2:
	        if (nQty) {
	            var totalRows    = this.tbl.childNodes.length;
	            var rowsToDelete = Math.min(nQty, totalRows);
	
	            // trim the table and the xml island
	            while (nTrimmed < rowsToDelete) {
	 if( dojo.isIE ){
	                if(dwa.lv.globals.get().nIntBrowserVer==6)
	                    this.tbl.lastChild.innerHTML = '';
	 } // end - I
	                this.tbl.removeChild(this.tbl.lastChild);
	                nTrimmed++;
	            }
	        }
	        break;
	
	    case -1:
	        nTrimmed = this.tbl.childNodes.length;
	        while (this.tbl.childNodes.length > 0) {
	 if( dojo.isIE ){
	                if(dwa.lv.globals.get().nIntBrowserVer==6)
	                    this.tbl.firstChild.innerHTML = '';
	 } // end - I
	            this.tbl.removeChild(this.tbl.firstChild);
	        }
	        this.tbl.style.top = '0px';
	        break;
	
	    case 1:
	        if (nQty) {
	            var totalRows    = this.tbl.childNodes.length;
	            var rowsToDelete = Math.min(nQty, totalRows);
	            var nTotalHeight = 0;
	
	            // trim the table and the xml island
	            while (nTrimmed < rowsToDelete) {
	                nTotalHeight += this.tbl.firstChild.offsetHeight;
	 if( dojo.isIE ){
	                if(dwa.lv.globals.get().nIntBrowserVer==6)
	                    this.tbl.firstChild.innerHTML = '';
	 } // end - I
	                this.tbl.removeChild(this.tbl.firstChild);
	                nTrimmed++;
	            }
	            // reset position as before removing
	            this.tbl.style.top = (this.tbl.offsetTop + nTotalHeight) + 'px';
	        }
	        break;
	
	    case 3:
	        var totalRows    = this.tbl.childNodes.length;
	        var rowsToDelete = Math.min(totalRows, totalRows - nQty);
	
	        // trim the table and the xml island
	        while (nTrimmed < rowsToDelete) {
	 if( dojo.isIE ){
	                if(dwa.lv.globals.get().nIntBrowserVer==6)
	                    this.tbl.lastChild.innerHTML = '';
	 } // end - I
	            this.tbl.removeChild(this.tbl.lastChild);
	            nTrimmed++;
	        }
	        break;
	    }
	
	    this.bCachedViewPosIsInvalid = true;
	    this.bCachedCalcTopRowIsInvalid = true;
	    this.bCachedCalcBottomRowIsInvalid = true;
	
	    // cleanup
	    xmlDoc=null;
	    return nTrimmed;
	},
	updateTable: function(){
	    var t$=this.oRoot,e$;
	    if( !t$ ) return;
	
	    var nPosIndex=this.iSelectedPosition;
	    if( nPosIndex < this.getLastEntryPosInView() ){
	        var a=this.tbl.childNodes;
	        var nRowIndex=nPosIndex-this.viewPos(0);
	        if( a && a.length && nRowIndex < a.length && nRowIndex > 0 ){
	            for( var i=nRowIndex,j=nPosIndex,iMax=a.length; i<iMax; i++,j++ ){
	                e$=t$.getEntryByIndex(j);
	                if( e$ ){
	                    a[i].setAttribute("tumbler",e$.getTumbler());
	                }
	            }
	        }
	    }
	},
	deleteRowsByUnid: function(sUnid){
	    var a = this.getRowsByUnid(sUnid);
	    if( a && a.length ){
	        for( var i=a.length-1; i>=0; i-- ){
	 if( dojo.isIE ){
	            if(dwa.lv.globals.get().nIntBrowserVer==6)
	                a[i].innerHTML = '';
	 } // end - I
	            this.tbl.removeChild(a[i]);
	        }
	    }
	},
	updateScrollBar: function(){
	    this.adjustCount();
	    if(!this.bIsPrintMode){
	        var nRows = parseInt( ''+this.getNumVisibleRows(), 10 );
	        if( isNaN(nRows) || !nRows ) nRows = this.iMaxRowsPerPage;
	        this.oScrollBar.updateAll(this.getTotalEntriesInView(), nRows, this.iCurrentPosition);
	    }
	},
	showWaitBox: function(bShow, iDir){
	    // ------------------------------------------
	    // displays/hides a box to indicate that the
	    //  browser is updating the display
	    // ------------------------------------------
	    var sMsg;
	    if (bShow){
	        switch (iDir) {
	        case -1:
	            sMsg = this._smsgs[ "L_FETCHINGPREV" ];
	            break;
	        case 0:
	            sMsg = this._smsgs[ "L_FETCHING" ];
	            break;
	        case 1:
	            sMsg = this._smsgs[ "L_FETCHINGNEXT" ];
	            break;
	        default:
	            sMsg = this._smsgs[ "L_FETCHING" ];
	            break;
	        }
	        dwa.lv.benriFuncs.setStatusText(sMsg, 'com_ibm_dwa_ui_virtualList.prototype.showWaitBox');
	        this.bHasWaitMsg = true;
	    }
	    else{
	        if(this.bHasWaitMsg) {
	            dwa.lv.benriFuncs.setStatusText("", 'com_ibm_dwa_ui_virtualList.prototype.showWaitBox');
	            this.bHasWaitMsg = false;
	        }
	        // window.status = "Top row: " + this.viewPos(this.topVisibleRow) + " of " + this.getLastEntryPosInView();
	    }
	},
	doSelection: function(bUp, bShift){
	    // ------------------------------------------
	    // moves the selected row up or down
	    // ------------------------------------------
	    var v$ = this;
	    if(typeof v$.viewPos(0) == 'undefined')
	        return;
	
	    var rZero=v$.viewPos(0);
	    var rTop =v$.calcTopRow();
	    var rBtm =v$.calcBottomRow();
	    var rLast=v$.getLastEntryPosInView();
	
	    // store the last selected view-entry
	    if(!v$.lastTumbler)v$.lastTumbler=dwa.lv.benriFuncs.elGetAttr(v$.getDD().getLastSelected(v$),"tumbler");
	
	    if( v$.iSelectedPosition < (rZero + rTop) )
	    {
	        v$.iSelectedPosition = rZero + rTop;
	        v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	    }
	    else
	    if( v$.iSelectedPosition > (rZero + rBtm) )
	    {
	        v$.iSelectedPosition = rZero + rBtm;
	        v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	    }
	    else
	    if (bUp){
	        if (v$.iSelectedPosition > rZero)
	        {
	            if( v$.lastTumbler && dwa.lv.colpos.compare(v$.sSelectedTumbler, v$.lastTumbler)==-1 ){
	                var s1=v$.lastTumbler,r1=v$.getRowByIndex(v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler) - v$.viewPos(0));
	                v$.deselectEntries([r1]);
	                v$.lastTumbler=s1;
	            }
	            v$.iSelectedPosition--;
	            v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	            if (v$.getSelectedRowIndex() < rTop){
	                v$.oScrollBar.doScroll('UP');
	                v$.doScroll('UP', v$.sSelectedTumbler);
	            }
	        }
	        else return 1;
	    }
	    else {
	        if (v$.iSelectedPosition < rLast)
	        {
	            if( v$.lastTumbler && dwa.lv.colpos.compare(v$.sSelectedTumbler, v$.lastTumbler)==1 ){
	                var s1=v$.lastTumbler,r1=v$.getRowByIndex(v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler) - v$.viewPos(0));
	                v$.deselectEntries([r1]);
	                v$.lastTumbler=s1;
	            }
	            v$.iSelectedPosition++;
	            v$.sSelectedTumbler = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	            if (v$.getSelectedRowIndex() > rBtm){
	                v$.oScrollBar.doScroll('DOWN');
	                v$.doScroll('DOWN', v$.sSelectedTumbler);
	            }
	        }
	    }
	
	    // just adjust ... don't scroll
	    v$.adjustSelection(!!bUp,!!bShift,false);
	    v$.resetFlags();
	},
	adjustSelection: function(bUp, bSh, bAdj, bNoSelect){
	    // ------------------------------------------
	    // adjust the selected rows
	    // ------------------------------------------
	    if (bAdj){
	        if (bUp)
	            this.iSelectedPosition = this.viewPos(this.getTopVisibleRowIndex());
	        else
	            this.iSelectedPosition = this.viewPos(this.calcBottomRow());
	    }
	    else
	    {
	        if (bUp && this.getSelectedRowIndex() < this.getTopVisibleRowIndex())
	            this.iSelectedPosition = this.viewPos(this.getTopVisibleRowIndex());
	    }
	
	    if(!bSh){ this.deselectEntries(); }

	    // select the row
        if( !bNoSelect ){
    	    var oRow = this.getRowByIndex(this.getSelectedRowIndex());
    	    if(oRow)
    	        this.selectEntry(oRow,bSh,false,false,1);
        }
	
	    this.focus();
	},
	saveSelection: function(oRow){
	    // ------------------------------------------
	    // save selected entry, but don't add duplicates
	    // ------------------------------------------
	    var v$ = this;
	    if( !oRow ){
	        // TagName is not TR or v$ is not a row with data
	        v$.iSelectedPosition = v$.viewPos("last");
	        v$.sSelectedTumbler  = v$.oRoot.getTumblerByIndex(v$.iSelectedPosition);
	        return;
	    }
	    var oTreeViewEntry = v$.getTreeViewEntry( oRow );
	    if(!oTreeViewEntry) return;
	
	    //var sUnid = oTreeViewEntry.getUnid();
	    var sTumbler = oTreeViewEntry.getTumbler();
	    
	    v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(sTumbler);
	    v$.sSelectedTumbler  = sTumbler;
	},
	deselectEntries: function(p1){
	    // ------------------------------------------
	    // deselect all entries
	    //  return the UNID of the first entry
	    // ------------------------------------------
	    var v$=this;
	    if( 1==v$.bResetObjects ) return;
	
	
	    // reset selections
	    if(p1){
	        for( var i=0,iMax=p1.length; i<iMax; i++ ){
	            this.getDD().removeSelected(p1[i],null,null,this,p1[i].getAttribute('tumbler'));
	        }
	    }
	    else{
	        this.getDD().setSelected(null,null,null,this);
	    }
	
	    // give a visual indication of the number of selected entries
	    this.showSelections();
	
	    if(this.fnSelect)
	        this.fnSelect();
	
	    // clear the last selected entry
	    this.lastTumbler=null;
	},
	DDUnselected: function(oRow){
	    this.setRowColor(0, [oRow]);
	},
	selectEntryWithoutCB: function(p1 ,p2 ,p3 ,p4 ,p5 ){
	    return this.selectEntry(p1,p2,p3,p4,p5,true);
	},
	selectEntry: function(p1 ,p2 ,p3 ,p4 ,p5 ,p6 ){
	    var v$ = this;
	    if (!p1 || v$.bIsPrintMode) return;
	
	    p1=v$.getRowElement(p1);
	    if( p1 && p2 ) v$.getDD().addSelected(p1,p3,null,v$,p1.getAttribute('tumbler'));
	    else if( p1 ) v$.getDD().setSelected(p1,p3,null,v$,false,p1.getAttribute('tumbler'));
	    
	    if( p1 && !p4 ){
	        v$.setRowColor(1,[p1]);
	    }
	    v$.saveSelection(p1);
	
	    // give a visual indication of the number of selected entries
	    v$.showSelections();
	
	    if(!p6 && v$.fnSelect)
	        v$.fnSelect(p5);
	},
	selectAllEntries: function(){
	    var v$=this;
	    
	    if(v$.isCategorizedView()) return;
	    
	    v$.deselectEntries();
	    if(this.getNumRows())
	        v$.selectMultiEntries(0, v$.getLastEntryPosInView());
	},
	selectMultiEntries: function(p1 ,p2 ,p3 ,p4 ){
	    var v$=this;
	    var nStart = p1 > p2 ? p2 : p1;
	    var nEnd = p1 > p2 ? p1 : p2;
	    // enumerate tumbler of entries that are not loaded yet.
	    
	    var oUnids = {};
	    var oItems = {};

        if( v$.isCategorizedView() ){
            var count = nEnd - nStart + 1;
            var bUp = (p1 > p2);
            var oRet = v$.oRoot.getRequestRangeEx( {iStart: nStart, nCount: count}, 0, bUp);
            var sSavedTumbler = v$.oRoot.getTumblerByIndex(p1);

            var oRequest = v$.loadUnidList(oRet.position, oRet.nCount, oRet.bNavigateReverse);
            var aEntries = v$.oDataStore.getViewEntries(oRequest);
            for(j=0;j<v$.oDataStore.getLength(aEntries);j++) {
    			var item = v$.oDataStore.getItem(aEntries, j);
	            var sPosition = v$.oDataStore.getAttribute( item, 'position');
	            var sUnid = v$.oDataStore.getAttribute( item, 'unid');
	            if(sPosition && sUnid){
				    oUnids['S'+sPosition] = sUnid;
				    oItems['S'+sPosition] = item;
    			}
            }

            var nIndex = v$.oRoot.getIndexByTumbler(sSavedTumbler);
            var delta = (bUp ? -1 : 1);
            for( var i = 0; i < count-1; i++, nIndex += delta ){
    	        var sTumbler = v$.oRoot.getTumblerByIndex( nIndex );
    	        var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
    	        var sUnid = oEntry ? oEntry.getUnid() : (oUnids['S'+sTumbler] ? oUnids['S'+sTumbler] : '');
    		    var item = oEntry ? oEntry.getViewEntry() : (oItems['S'+sTumbler] ? oItems['S'+sTumbler] : null);
    	        var oRow = v$.getRowByIndex(nIndex - v$.viewPos(0));
    	        var oRowObj = oRow ? oRow : {tumbler:sTumbler, unid:sUnid, item:item, isDummy:true, style:{}, getAttribute:function(attr){return this[attr]}};
    	        v$.selectEntryWithoutCB(oRowObj,true,p4,true,2);
            }

    	    // SPR PTHN7GLPNT
    	    // If mulit-selecting UP, then "selectEntry" is called first in the loop
    	    // "selectEntry" should be called last because the getSelectedData is incomplete
    	    //  until the end of the loop
    	    if(p3){
    	        var sTumbler = v$.oRoot.getTumblerByIndex(nIndex);
    	        var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
    	        var sUnid = oEntry ? oEntry.getUnid() : (oUnids['S'+sTumbler] ? oUnids['S'+sTumbler] : '');
    	        var oRow = v$.getRowByIndex(nIndex - v$.viewPos(0));
    	        var oRowObj = oRow ? oRow : {tumbler:sTumbler, unid:sUnid, isDummy:true, style:{}, getAttribute:function(attr){return this[attr]}};
    	        v$.selectEntry(oRowObj,true,p4,true,2);
    	    }
    	    
    	    // now show the highlight for all selected rows
    	    v$.setRowColor(1);

        }else{
    	    var oCollection = new dwa.lv.treeViewCollection(dwa.lv.globals.get().oSettings.nMaxViewRows);
    	    for(var i=nStart;i<=nEnd;i++) {
    	        var sTumbler = v$.oRoot.getTumblerByIndex(i);
    	        var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
    	        if(!oEntry) oCollection.addToTail(parseInt(sTumbler));
    	    }
    	    // retrieve unid of entries.
    	    if(oCollection.getCount()) {
    	        for(var i=0;i<oCollection.getNumChunks();i++) {
    	            var oChunk = oCollection.getChunk(i);
    	            if(!oChunk)
    	                break;
    	            var oRequest = v$.loadUnidList(oChunk.nStart, (oChunk.nEnd - oChunk.nStart + 1));
    	            var aEntries = v$.oDataStore.getViewEntries(oRequest);
    	            for(j=0;j<v$.oDataStore.getLength(aEntries);j++) {
    			var item = v$.oDataStore.getItem(aEntries, j);
    	                var sPosition = v$.oDataStore.getAttribute( item, 'position');
    	                var sUnid = v$.oDataStore.getAttribute( item, 'unid');
    	                if(sPosition && sUnid){
    				oUnids['S'+sPosition] = sUnid;
    				oItems['S'+sPosition] = item;
    			}
    	            }
    	        }
    	    }
    	    
    	    // select entries with unid info for GetSelectedData().
    	    for(var i=nStart;i<=nEnd;i++) {
    	        var sTumbler = v$.oRoot.getTumblerByIndex(i);
    	        var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
    	        var sUnid = oEntry ? oEntry.getUnid() : (oUnids['S'+sTumbler] ? oUnids['S'+sTumbler] : '');
    		var item = oEntry ? oEntry.getViewEntry() : (oItems['S'+sTumbler] ? oItems['S'+sTumbler] : null);
    	        var oRow = v$.getRowByIndex(i - v$.viewPos(0));
    	        var oRowObj = oRow ? oRow : {tumbler:v$.oRoot.getTumblerByIndex(i), unid:sUnid, item:item, isDummy:true, style:{}, getAttribute:function(attr){return this[attr]}};
    	        v$.selectEntryWithoutCB(oRowObj,true,p4,true,2);
    	    }
    	    // SPR PTHN7GLPNT
    	    // If mulit-selecting UP, then "selectEntry" is called first in the loop
    	    // "selectEntry" should be called last because the getSelectedData is incomplete
    	    //  until the end of the loop
    	    if(p3){
    	        var sTumbler = v$.oRoot.getTumblerByIndex(p2);
    	        var oEntry = v$.oRoot.getEntryByTumbler(sTumbler, true);
    	        var sUnid = oEntry ? oEntry.getUnid() : (oUnids['S'+sTumbler] ? oUnids['S'+sTumbler] : '');
    	        var oRow = v$.getRowByIndex(p2 - v$.viewPos(0));
    	        var oRowObj = oRow ? oRow : {tumbler:v$.oRoot.getTumblerByIndex(p2), unid:sUnid, isDummy:true, style:{}, getAttribute:function(attr){return this[attr]}};
    	        v$.selectEntry(oRowObj,true,p4,true,2);
    	    }
    	    
    	    // now show the highlight for all selected rows
    	    v$.setRowColor(1);
        }


	},
	showSelections: function(){
	    // ------------------------------------------
	    // display the number of selected rows
	    // ------------------------------------------
	    var n=this.getDD().getSelectedCount(this);
	    // SPR# JFOR7QNMLE: show indication of documents selected
	    if (1 == n) n = 0;
	    dwa.lv.benriFuncs.setStatusText(n ? dwa.common.utils.formatMessage(this._smsgs[ "L_DOCUMENTS_SELECTED" ],n) : "", 'com_ibm_dwa_ui_virtualList.prototype.showSelections');
	},
	setRowColor: function(iState,oRows){
	    // ------------------------------------------
	    // selected rows can either be blue or gray
	    //  blue = selected, and vList has focus
	    //  gray = selected, but some other control has focus
	    // (03/28/2002) iState=2 has been removed
	    // ------------------------------------------
	    var v$ = this;
	    var bSelect = iState==1 || iState==2;
	    var aSelect=(oRows?oRows:v$.getDD().getSelected(false, v$));
	    if(!aSelect || 0==aSelect.length)return;
	
	    // If there are more than 25 rows to change, Windows OS has trouble repainting the screen.
	    // Break up into chunks of 25 that are changed after a timeout.
	    if(aSelect.length>25 || v$.$doColor) return v$.setColorArray(aSelect,iState);
	    
	    // change the bgcolor and text color
	    for( var j=0,jMax=aSelect.length; j<jMax; j++ ){
	        if(aSelect[j].isDummy)
	            continue;
	        var sClass = v$.getClassSelected();
	        if (bSelect ^ (dwa.common.utils.indexOf(aSelect[j].className.split(' '), sClass) >= 0)) {
	            dwa.common.utils.cssEditClassExistence(aSelect[j], sClass, bSelect);
	            v$.enumlateCellsInRow(aSelect[j], bSelect ? v$.setSelected : v$.setUnselected);
	        }
	    }
	},
	setColorArray: function(a1,b1){
	    var v$ = this;
	    if(!this.$tumblers)this.$tumblers={};
	    if(!a1) return;
	    for( var i=0,imax=a1.length;i<imax;i++ ){
	        if(!a1[i].isDummy)
	        this.$tumblers[dwa.lv.benriFuncs.elGetAttr(a1[i],"tumbler")]=!!b1;
	    }
	    if(!this.$doColor){
	        this.$doColor=true;
	        setTimeout(function(){v$.doColorChange()}, 0);
	    }
	},
	getRowByIndex: function(nIndex){
	    if(nIndex < 0 || nIndex >= this.tbl.childNodes.length)
	        return null;
	    try {
	        return this.tbl.childNodes.item(nIndex);
	    }
	    catch(er) {
	    }
	    return null;
	},
	getRowsByUnid: function(sUnid){
	    var rows = [];
	
	    var oEntries = this.getEntriesByUnid(sUnid);
	    for(var i=0; oEntries && i<oEntries.length; i++)
	    {
	        var oRow = this.getRowByIndex(this.oRoot.getIndexByTumbler(oEntries[i].getTumbler()) - this.viewPos(0));
	        if(oRow)
	            rows.push(oRow);
	    }
	    return rows;
	},
	getRows: function(sUnid, sTumbler){
	    // ------------------------------------------
	    // returns the row given the UNID
	    // ------------------------------------------
	    var aNodes,aRows = [],oRow;
	    try{
	        if(sTumbler)
	        {
	            var nIndex = this.oRoot.getIndexByTumbler(sTumbler);
	            if(typeof nIndex == 'undefined')
	                return aRows;
	            if(nIndex < this.viewPos(0) || nIndex > this.viewPos("last"))
	                return aRows;
	            var oRow = this.getRowByIndex(nIndex - this.viewPos(0));
	            if(oRow)
	                aRows.push(oRow);
	        }
	        else
	        {
	            aNodes = this.getEntriesByUnid(sUnid);
	            for(var i=0;aNodes && i<aNodes.length;i++)
	            {
	                var nIndex = this.oRoot.getIndexByTumbler(aNodes[i].getTumbler());
	                if(typeof nIndex == 'undefined')
	                    continue;
	                if(nIndex < this.viewPos(0) || nIndex > this.viewPos("last"))
	                    continue;
	                var oRow = this.getRowByIndex(nIndex - this.viewPos(0));
	                if(oRow)
	                    aRows.push(oRow);
	            }
	        }
	    }
	    catch(er){
	    }
	    oRow = null;
	    return aRows;
	},
	getNumRows: function(){
	    return this.tbl ? this.tbl.childNodes.length : 0;
	},
	viewPos: function(i_row){
	    // ------------------------------------------
	    //    returns the view position of a row
	    // ------------------------------------------
	    // last row = last child of the tbl
	    if (!this.getNumRows())
	        return void 0;
	
	    // cache return value of this.oRoot.getIndexByTumbler() for performance issue
	    if(this.bCachedViewPosIsInvalid)
	    {
	        this.bCachedViewPosIsInvalid = false;
	        this.cachedViewPos = this.oRoot.getIndexByTumbler(this.tbl.firstChild.getAttribute("tumbler"));
	
	    }
	
	    // first row = first child of the tbl
	    if ("last" == i_row)
	        return this.cachedViewPos + this.tbl.childNodes.length - 1;
	    
	
	    return this.cachedViewPos + i_row;
	},
	getRowIndex: function(iPosition){
	    // ------------------------------------------
	    // returns the row index of an xml item
	    //  opposite of getPosition
	    // ------------------------------------------
	    var nIndex = iPosition - this.viewPos(0);
	    return ( nIndex < this.tbl.childNodes.length ? nIndex : this.tbl.childNodes.length );
	},
	getLastEntryPosInView: function(){
	    // ------------------------------------------
	    // gets the last row-position in the view
	    //  or the last row-position on the contact tab
	    // ------------------------------------------
	    return Math.max(this.getTotalEntriesInView() - 1, 0);
	},
	getTotalEntriesInView: function(){
	    return this.nLimit ? Math.min(this.oRoot.getTotalEntries(), this.nLimit) : this.oRoot.getTotalEntries();
	},
	isQuickSearchMode: function(){
	    return this.sStartKey;
	},
	isRangeMode: function(){
	    return !!this.sStartKey && (!!this.sUntilKey || !!this.bUseStartKeyOnly);
	},
	enumlateCellsInRow: function(oRow, fnCB){
	    var v$ = this;
	    var nColsOnTop = 0;
	    var nRespCols = 0;
	    var aCells = oRow.getElementsByTagName(v$.getCellTagName());
	    // for the case that v$.aColInfo is not available such as chaging preview pane position.
	    if(!v$.aColInfo)
	        return;
	    for(var row=0;row<2;row++) {
	        var aRowInfo = v$.getSingleRowInfo(v$.getLayoutInfo(), !!row);
	        if(!aRowInfo || !aRowInfo.length)
	            continue;
	
	        for(var c=0;c<aRowInfo.length;c++) {
	            var oColInfo = v$.aColInfo[aRowInfo[c].nRealIndex];
	            if(oColInfo.bIsResponse) {
	                nRespCols++;
	                continue;
	            }
	            var nCellIdx = c + nColsOnTop - nRespCols;
	            try {
	                var nCell = 0;
	                for(var c2=0;c2<aCells.length;c2++) {
	                    var oCell = aCells[c2];
	                    if(oCell.getAttribute('iscell')!='1')
	                        continue;
	                    if(nCell==nCellIdx)
	                        fnCB(oColInfo, oCell);
	                    nCell++;
	                }
	            } catch(e) {}
	        }
	        nColsOnTop = aRowInfo.length;
	    }
	},
	getColumnValueArray: function(oXmlEntry){
	    // ------------------------------------------
	    // returns an array of xml columns 
	    // ------------------------------------------
	    var v$ = this;
	    var aValue = [];
	    var aXmlColumns = v$.oDataStore.getEntryDatas(oXmlEntry);
	    for (var i=0; i < v$.nColumnSpan; i++) {
	        if (-1 != v$.aColInfo[i].nXmlCol)
		    aValue[aValue.length] = this.formatText(v$.oDataStore.getItem(aXmlColumns, v$.aColInfo[i].nXmlCol));
	    }
	    return aValue;
	},
	getColumnValue: function(oXmlEntry, i_XmlColumn, bISO8601String){
	    // ------------------------------------------
	    // returns the xml column specified
	    // ------------------------------------------
	    var v$ = this;
	    var sValue,aNodes,dataItem;
	    sValue='';
	    try {
	        var nColumn = i_XmlColumn - 0;
	        dataItem = isNaN(nColumn) ? v$.oDataStore.getEntryDataByName(oXmlEntry, i_XmlColumn) : v$.oDataStore.getEntryDataByNumber(oXmlEntry, i_XmlColumn);
	
	        // get the column value
	        if (dataItem != null)
	            sValue = bISO8601String ? ('' + this.oNotesValue.setNode(dataItem)) : this.oNotesValue.setNode(dataItem).format();
	    }
	    catch (er) {
	    }
	    // cleanup
	    aNodes=null;dataItem=null;
	    return sValue;
	},
	loadData: function(start, count, reverse, nErrorStatus, sFocusTumbler, bFocusToBeTop, fnCBOnLoaded, bPercentileInfo, bUp){
	    // ------------------------------------------
	    // replaces an existing xml island with new data
	    //   returns -1 if an error occurred
	    // ------------------------------------------
	    
	    ;
	
	    var v$ = this;
	    var bIsReversed = false;
	    var i_count = count;

	    var oQuery = v$.getBaseQuery();
	
	    // ensure count is large enough, bump count in chunks
	    if(i_count == -1)
	    {
	        i_count = 20;
	        while (i_count < v$.iMaxRowsPerPage) {
	            i_count += 20;
	        }
	    }
	    if (v$.nLimit)
	        i_count = Math.min(i_count, v$.nLimit);
	
	    // use sStartKey if specified
	    if (v$.isQuickSearchMode() && (!v$.oRoot || !v$.oRoot.getChildLoaded())) {
	        oQuery.StartKey = encodeURIComponent(v$.sStartKey);
	
	        if (v$.bUseStartKeyOnly) {
	            // search next character of startkey in the server instead of untilkey
	            oQuery.UseStartKeyOnly = '1';
	        }
	        else if (v$.sUntilKey) {
	            // use sUntilKey if present
	            oQuery.UntilKey = encodeURIComponent(v$.sUntilKey);
	            // include sUntilKey to search
	            if (v$.bIncludeUntilKey)
	                oQuery.IncludeUntilKey = '1';
	        }
	
	        // use sKeyType if present
	        if (v$.sKeyType) {
	            oQuery.KeyType = v$.sKeyType;
	        }
	    }
	
	    // use start if sStartKey is not specified
	    else {
	        if( v$.bAlwaysSendStartKey ){
	            oQuery.StartKey = encodeURIComponent(v$.sStartKey);
	
	            if (v$.bUseStartKeyOnly) {
	                // search next character of startkey in the server instead of untilkey
	                oQuery.UseStartKeyOnly = '1';
	            }
	            else if (v$.sUntilKey) {
	                // use sUntilKey if present
	                oQuery.UntilKey = encodeURIComponent(v$.sUntilKey);
	                // include sUntilKey to search
	                if (v$.bIncludeUntilKey)
	                    oQuery.IncludeUntilKey = '1';
	            }
	
	            // use sKeyType if present
	            if( v$.sKeyType ){
	                oQuery.KeyType = v$.sKeyType;
	            }
	        }
	        
	        if( start == 'last')
	            if( v$.sThreadRoot)
	            {
	                oQuery.Navigate = '9';
	                oQuery.start = dwa.lv.colpos.getNext(dwa.lv.colpos.getTopLevel(v$.sThreadRoot));
	                bIsReversed = true;
	            }
	            else
	                oQuery.EndView = '1';
	        else
	            oQuery.start = (dwa.lv.colpos.getDepth(start)>=32 ? dwa.lv.colpos.getNext(dwa.lv.colpos.getTopLevel(start)) : start);
	        
	        if(reverse) {
	            oQuery.NavigateReverse = '1';
	            bIsReversed = true;
	        }
	    }
	    if(v$.bExpandAll)
	        oQuery.ExpandView = '1';
	    if(v$.bCollapseAll && dwa.lv.colpos.getDepth(start) == 0)
	        oQuery.CollapseView = '1';
	
	    oQuery.count = i_count;
	
	    // resort, if specified
	    v$.setSortArgument(oQuery);
	    
	    if(v$.sHiddenColumns)
	        oQuery.hc = v$.sHiddenColumns;
	    if(typeof(v$.nDirIndex) !== 'undefined' )
	        oQuery.DirIndex = v$.nDirIndex;
	    if(v$.sReadViewEntriesForm){ oQuery.Form = v$.sReadViewEntriesForm; }

	    if( !v$.oRoot)
	        v$.oRoot = v$.getNewRoot();
	
	    if( v$.sThreadRoot != 'none')
	    {
	        // load the new data
	        var sUrl = this.oDataStore.getUrl(oQuery);
	        // to not fire the request for same url
	        if(v$.sLastPath == sUrl) {
	            return 1;
	        }
	        v$.sLastPath = sUrl;
	        
	        this.oDataStore.load({
	            oClass : v$
	            ,fnCallback : v$.loadedData
	            ,nXmlRequest : ++v$.nXmlRequest
	            ,start : start
	            ,i_count : i_count
	            ,bIsReversed : bIsReversed
                ,bUp : bUp
	            ,sFocusTumbler : sFocusTumbler
	            ,bFocusToBeTop : bFocusToBeTop
	            ,fnCBOnLoaded : fnCBOnLoaded
	            ,bIsThreadPanel : v$.sThreadRoot
	            ,sTabId : v$.sTabId
	            ,oQuery : oQuery
	        });
	        return 1;
	    }
	
	    var sTumbler = v$.oRoot.getTumblerByIndex(v$.iCurrentPosition);
	    v$.oRoot.update();
	    if(sTumbler && typeof(v$.oRoot.getIndexByTumbler(sTumbler))=='number')
	        v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(sTumbler);
	    if(v$.sSelectedTumbler && typeof(v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler))=='number')
	        v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	
	    if(v$.fnLoadedData)
	        v$.fnLoadedData(v$.oRoot);
	
	    v$.bCachedViewPosIsInvalid = true;
	    v$.bInvalidData = false;
	
	    return nErrorStatus;
	},
	loadedData: function(oRequest){
		var v$ = this;
	    // handles multiple replies -- if(oRequest.nXmlRequest != v$.nXmlRequest)
	    //    return;
	    // return if obj has been destroyed
	    if(!v$.oRoot)
	        return;
	
	    ;
	
	

	    if(v$.fnSweepEntries)
	        oRequest.i_count = v$.fnSweepEntries(v$.sThreadRoot, oRequest, oRequest.i_count, oRequest.bIsReversed);
	
	    oRequest.i_count = v$.removeOutScopeXmlEntriesForThreads(v$.sThreadRoot, oRequest, oRequest.i_count, oRequest.bIsReversed);
	
	    if(v$.bCheckUnidForRefresh && v$.checkUnidForRefresh(oRequest)) {
	        v$.refresh(true);
	        return;
	    }
	
	    v$.oRoot.applyXmlEntries(oRequest.start, oRequest.i_count, oRequest, v$.isQuickSearchMode(), oRequest.bIsReversed);
	    v$.setRangeForThreads(v$.sThreadRoot);
	
	    // for in case of no entry in view
	    v$.oRoot.setChildLoaded(true);
	
	    var sTumbler = v$.oRoot.getTumblerByIndex(v$.iCurrentPosition);
	    v$.oRoot.update();
	    if(sTumbler && typeof(v$.oRoot.getIndexByTumbler(sTumbler))=='number')
	        v$.iCurrentPosition = v$.oRoot.getIndexByTumbler(sTumbler);
	    if(v$.sSelectedTumbler && typeof(v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler))=='number')
	        v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	
	    if(v$.fnLoadedData)
	        v$.fnLoadedData(v$.oRoot);
	
	    v$.bCachedViewPosIsInvalid = true;
	    v$.bInvalidData = false;
	    
	    v$.clearUpdatingViewMessage();
	    var nErrorStatus = v$.adjustTable(0, oRequest.sFocusTumbler, oRequest.bFocusToBeTop, oRequest.fnCBOnLoaded, true, oRequest.bUp);
	    if(oRequest.fnCBOnLoaded && nErrorStatus==0)
	        oRequest.fnCBOnLoaded();
	
        v$.focus();

	    this.adjustElementSizes();
	},
	setSortArgument: function(oQuery){
	    var v$ = this;
	    if(-1!=v$.nSortBy && dwa.lv.listViewBase.prototype.gSortStatic[0]!=v$.sSortType)
	        oQuery[v$.sSortType] = v$.aColInfo[v$.nSortBy].nColSort;
	},
	loadPercentileInfo: function(nErrorStatus){
	    
	    ;
	
	    var v$ = this;
	    var oQuery = v$.getBaseQuery();
	    oQuery.PercentileEntries = dwa.lv.globals.get().oSettings.nPercentileEntries;
	    v$.setSortArgument(oQuery);
	
	    // load the new data
	    if(!this.oDataStore.loadPrecentileInfo({
	            oClass : v$
	            ,fnCallback : v$.loadedPercentileInfo
	            ,bIsThreadPanel : true
	            ,sTabId : v$.sTabId
	            ,oQuery : oQuery
	        })) {
	        // load method returns false if percentileinfo is not avaiable in datastore
	        return 1;;
	    }
	    v$.nScrollHintStatus = 1;
	    return 1;
	},
	loadedPercentileInfo: function(oRequest){
	    
		var v$ = this;
	    this.processPercentileInfo(oRequest);
	    this.nScrollHintStatus = 2;
	    this.showScrollHint(!!this.hScrollHintTimer);
	    
	},
	processPercentileInfo: function(oRequest){
		var v$ = this;
	    if(!v$.requireScrollHint())
	        return;
	    
	    var aViewEntries = v$.oDataStore.getViewEntries(oRequest);
	    if(!aViewEntries)
	        return;
	    
	    v$.aScrollHints = aViewEntries;
	},
	removeOutScopeXmlEntriesForThreads: function(p1 ,p2 ,p3 ,p4 ){
	    var sTargetTumbler = dwa.lv.colpos.getTopLevel(p1);
	    if(!sTargetTumbler)
	        return p3;
	
	    var aEntries = this.oDataStore.getViewEntries(p2);
	    for(var i=this.oDataStore.getLength(aEntries)-1;i>=0;i--)
	    {
	        var oEntry = this.oDataStore.getItem(aEntries, i);
	        var sTumbler = this.oDataStore.getAttribute(oEntry, 'position') + '';
	        if(sTumbler && dwa.lv.colpos.getTopLevel(sTumbler) != sTargetTumbler)
	        {
	            this.oDataStore.removeChild(aEntries, i);
	            if(p4)p3--;
	        }
	    }
	    
	    var nIndex = parseInt(sTargetTumbler);
	    if(this.oRoot && this.oRoot.getStartIndex() < nIndex)
	        this.oRoot.setStartIndex(nIndex);
	    if(this.oRoot && this.oRoot.getNumChilds() != 1)
	        this.oRoot.setNumChilds(1);
	    
	    return p3;
	},
	checkUnidForRefresh: function(    oRequest){
		var v$ = this;
	    var aEntries = v$.oDataStore.getViewEntries(oRequest);
	    for(var i=v$.oDataStore.getLength(aEntries)-1;i>=0;i--)
	    {
	        var oEntry = v$.oDataStore.getItem(aEntries, i);
	        var sUnid = v$.oDataStore.getAttribute(oEntry, 'unid');
	        var sTumbler = v$.oDataStore.getAttribute(oEntry, 'position');
	        if(sUnid) {
	            var aNodes = this.oRoot.getEntriesByUnid(sUnid);
	            if(aNodes && aNodes.length) {
	                for(var j=0;j<aNodes.length;j++) {
	                    if(sTumbler != aNodes[j].getTumbler())
	                        return true;
	                }
	            }
	        }
	    }
	    
	    return false;
	},
	setRangeForThreads: function(p1 ){
	    var sTargetTumbler = dwa.lv.colpos.getTopLevel(p1);
	    if(!sTargetTumbler)
	        return;
	
	    var nIndex = parseInt(sTargetTumbler);
	    if(this.oRoot && this.oRoot.getStartIndex() < nIndex)
	        this.oRoot.setStartIndex(nIndex);
	    if(this.oRoot && this.oRoot.getNumChilds() != 1)
	        this.oRoot.setNumChilds(1);
	},
	findEntry: function(){
	    // ------------------------------------------
	    // finds an entry in the view, then returns
	    //  the position of the entry
	    // ------------------------------------------
	    var v$ = this;
	    var sPos = '';
	    var oQuery = v$.getBaseQuery();
	
	    if(v$.sKeyType)
	        oQuery.KeyType = v$.sKeyType;
	
	    oQuery.StartKey = encodeURIComponent(v$.sSearchKey);
	    oQuery.count = 1;
	
	    v$.setSortArgument(oQuery);
	
	    var oRequest = {
	        oClass : v$
	        ,bIsThreadPanel : true
	        ,oQuery : oQuery
	        ,bSynchronous : true
	    };
	    v$.oDataStore.load(oRequest);
	    // return the position of the first entry if the query was successful
	    var tmpNodes = v$.oDataStore.getViewEntries(oRequest);
	    // show an error if the entry was not found
	    if (!tmpNodes || !v$.oDataStore.getLength(tmpNodes))
	        window.alert( this._smsgs[ "L_ENTRY_NOT_FOUND" ] );
	    else
	        sPos = v$.oDataStore.getAttribute(v$.oDataStore.getItem(tmpNodes, 0), 'position') + '';
	    return sPos;
	},
	getTopBorderEntry: function(){
	    var v$ = this;
	    var sPos = '';
	    var oQuery = v$.getBaseQuery();
	
	    if(v$.sStartKey)
	        oQuery.StartKey = encodeURIComponent(v$.sStartKey);
	    oQuery.count = 1;
	
	    v$.setSortArgument(oQuery);
	
	    var oRequest = {
	        oClass : v$
	        ,bIsThreadPanel : true
	        ,oQuery : oQuery
	        ,bSynchronous : true
	    };
	    v$.oDataStore.load(oRequest);
	    // return the position of the first entry if the query was successful
	    var tmpNodes = v$.oDataStore.getViewEntries(oRequest);
	    // show an error if the entry was not found
	    if (!tmpNodes || !v$.oDataStore.getLength(tmpNodes))
	        sPos = v$.oDataStore.getAttribute(v$.oDataStore.getItem(tmpNodes, 0), 'position') + '';
	    return sPos;
	},
	getBottomBorderEntry: function(){
	    var v$ = this;
	    var sPos = '';
	    var oQuery = v$.getBaseQuery();
	
	    if(v$.sUntilKey)
	        oQuery.StartKey = encodeURIComponent(v$.sUntilKey);
	    else
	        oQuery.EndView = 1;
	    
	    oQuery.count = 1;
	
	    v$.setSortArgument(oQuery);
	
	    var oRequest = {
	        oClass : v$
	        ,bIsThreadPanel : true
	        ,oQuery : oQuery
	        ,bSynchronous : true
	    };
	    v$.oDataStore.load(oRequest);
	    // return the position of the first entry if the query was successful
	    var tmpNodes = v$.oDataStore.getViewEntries(oRequest);
	    // show an error if the entry was not found
	    if (!tmpNodes || !v$.oDataStore.getLength(tmpNodes))
	        sPos = v$.oDataStore.getAttribute(v$.oDataStore.getItem(tmpNodes, 0), 'position') + '';
	    return sPos;
	},
	loadUnidList: function(start, count, reverse){
	    // ------------------------------------------
	    // retrieve unid info for entries in the view, returns true if succesful
	    // ------------------------------------------
	    var v$ = this;
	    var oQuery = v$.getBaseQuery();
	    oQuery.start = start;
	    oQuery.count = count;
        if( reverse ) oQuery.NavigateReverse = '1';
	    oQuery.NoEntryData = 1;
	    v$.setSortArgument(oQuery);
	
	    var oRequest = {
	        oClass : v$
	        ,bIsThreadPanel : true
	        ,oQuery : oQuery
            ,start : ''+start
            ,i_count : count
	        ,bSynchronous : true
	    };
        if( reverse ) oRequest.bIsReversed = true;
	    v$.oDataStore.load(oRequest);
	    return oRequest;
	},
	simpleSearch: function(sDefault){
	    // ------------------------------------------
	    // prompts for search text, if a function callback is defined
	    // ------------------------------------------
	    var v$ = this;
	    if (!v$.fnSearch) return;
	
	    var sOrgStartsWith = v$.sStartsWith;
	    
	    if(sDefault)
	        v$.sStartsWith += sDefault;
	
	    
	    if (sOrgStartsWith)
	        return;
	
	    
	    if(v$.fnStartsWith)
	        v$.fnStartsWith({
	            fnGetStartsWith:function(){return v$.sStartsWith;},
	            fnClearStartsWith:function(){v$.sStartsWith = '';v$.focus();},
	            fnFocus:function(){v$.focus();}
	        });
	},
	getDD: function(){
	    return dwa.lv.dragdropControl.prototype.getDragDropControlStatic(this.sDragDropControlId);
	},
	releaseDD: function(){
	    if(this.sDragDropControlId)
	        dwa.lv.dragdropControl.prototype.releaseDragDropControlStatic(this.sDragDropControlId);
	},
	doResize: function( bForceNow ){
	    // ------------------------------------------
	    // resize the vList after a brief timeout
	    //  this minimizes the number of resize events processed
	    // ------------------------------------------
	    
	
	    var v$=this;
	    if(v$.bIsPrintMode||v$.bResizeInProgress) return;
	    
	    if(!v$.tbl || !v$.tblContainer) return; // for initializing slider
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	    // revert ListContainer width to 100% on Mozilla
	    v$.tblContainer.parentNode.style.width = '100%';
	 } // end - GS
	    
	    if(v$.bNarrowMode != v$.needToBeNarrow()) {
	        v$.changeNarrowMode(!v$.bNarrowMode);
	        
	    }
	
	    v$.bResizeInProgress = true;
	    var bForce = (v$.getListHeight() <= v$.iAvgRowHeight);
	
	    v$.bCachedCalcTopRowIsInvalid = true;
	    v$.bCachedCalcBottomRowIsInvalid = true;
	
	    // reset MaxRowsPerPage so it will be bumped up again if necessary
	    v$.iMaxRowsPerPage = 20;
	    v$.adjustCount();
	
	    // Set just the last column's CSS
	    v$.syncColSS(1,0,v$.getHeightDiff(),v$.getWidthDiff());
	
	    if( dojo.isIE == 6 && dojo.doc.compatMode == "CSS1Compat" && !dojo._isBodyLtr() ){
		v$.deleteRows(-1);
		v$.adjustTable();
	    }

	    // done
	    v$.nIdTimerResize = null;
	    v$.bResizeInProgress = false;
	    if(v$.oRoot.getChildLoaded())
	        v$.adjustTable();
	
	    this.adjustFocusElements();
	    this.adjustElementSizes();
	    
	},
	adjustCount: function(){
	    if(this.bIsPrintMode)
	    {
	        if(this.oRoot.getChildLoaded() && this.getTotalEntriesInView())
	            this.iMaxRowsPerPage = this.getTotalEntriesInView();
	    }
	    else
	        this.iMaxRowsPerPage = Math.ceil(this.oScrollBar.getHeight() / this.iAvgRowHeight);
	},
	expandEntry: function(bExpand, oTreeViewEntry){
	    var v$ = this;
	    if( !this.bAllowCategoryAction /*|| this.hasResponseColumn()*/ )
	        return;
	    if(!oTreeViewEntry)
	    {
	        oTreeViewEntry = this.oRoot.getEntryByTumbler(this.sSelectedTumbler, true);
	        if(!oTreeViewEntry || !oTreeViewEntry.hasChildren())
	        {
	            v$.resetFlags();
	            return;
	        }
	    }
	    if(typeof bExpand != 'undefined')
	    {
	        if(bExpand && oTreeViewEntry.isExpanded())
	            return;
	        if(!bExpand && !oTreeViewEntry.isExpanded())
	            return;
	    }
	
	    this.showWaitBox(true);
	    setTimeout(function(){v$.expandEntryTimeout(bExpand, oTreeViewEntry)}, 0);
	},
	expandEntryTimeout: function(bExpand, oTreeViewEntry){
	    var v$=this;
	    if(!oTreeViewEntry)
	    {
	        oTreeViewEntry = v$.oRoot.getEntryByTumbler(v$.sSelectedTumbler, true);
	        if(!oTreeViewEntry || !oTreeViewEntry.hasChildren())
	        {
	            v$.resetFlags();
	            return;
	        }
	    }
	    oTreeViewEntry.expand(bExpand);
	    oTreeViewEntry.update();
	    v$.bCachedViewPosIsInvalid = true;
	
	    var oSelected = v$.oRoot.getEntryByTumbler(v$.sSelectedTumbler, true);
	    if(!oSelected.isVisible())
	    {
	        oSelected = oSelected.getVisibleParent();
	        v$.sSelectedTumbler = oSelected.getTumbler();
	        v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	        v$.deselectEntries();
	        v$.selectEntry(v$.getRowByIndex(v$.getSelectedRowIndex()));
	    }
	    else
	    {
	        v$.sSelectedTumbler = oSelected.getTumbler();
	        v$.iSelectedPosition = v$.oRoot.getIndexByTumbler(v$.sSelectedTumbler);
	    }
	    v$.deleteRows(3, v$.oRoot.getIndexByTumbler(oTreeViewEntry.getTumbler()) - v$.viewPos(0));
        if( !oTreeViewEntry.isExpanded() ){
    	    v$.jumpTo(void 0, true, v$.sSelectedTumbler);
        }else{
            v$.jumpTo(void 0, true);
        }
	},
	invalidateForcedCaretMoving: function(){
	    this.nMoveCaretAfterScroll = 0;
	},
	postScroll: function(nEvent, bShift){
	    var v$ = this;
	    this.invalidateForcedCaretMoving();
	    switch(nEvent)
	    {
	        case 'UP':
	        case 'PAGEUP':
	        case 'TOP':
	            this.nMoveCaretAfterScroll = bShift ? 3 : 1;
	            break;
	        case 'DOWN':
	        case 'PAGEDOWN':
	        case 'BOTTOM':
	            this.nMoveCaretAfterScroll = bShift ? 4 : 2;
	            break;
	    }
	    
	    var nOldScrollPos = this.oScrollBar.getOldPos();
	    this.oScrollBar.doScroll(nEvent);
	    var nErrorStatus = this.doScroll(nEvent);
	    if(nErrorStatus == 2) {
	        setTimeout(function(){v$.revertScrollPos(nOldScrollPos)}, 0);
	    }
	    else {
	        setTimeout(function(){v$.moveCaretAfterScroll(nErrorStatus == 1)}, 1);
	    }
	},
	moveCaretAfterScroll: function(bKeepCaret){
	    var v$=this;
	    if(v$.nMoveCaretAfterScroll)
	    {
	        switch(v$.nMoveCaretAfterScroll)
	        {
	            case 1:
	                v$.adjustSelection(true, false, true);
	                break;
	            case 2:
	                v$.adjustSelection(false, false, true);
	                break;
	            case 3:
	                v$.adjustSelection(true, true, true);
	                break;
	            case 4:
	                v$.adjustSelection(false, true, true);
	                break;
	        }
	        
	        // do focus only when v$.nMoveCaretAfterScroll is preset to not scroll body unexpectedly
	//        v$.focus();
	    }
	    v$.focus();
	    v$.resetFlags(bKeepCaret);
	},
	setListMode: function(oRoot){
	    this.setClientMode(true, oRoot);
	
	    this.bShowTwistieButton = false;
	    this.bAllowCategoryAction = false;
	    this.bAllowStoreColumnCookie = false;
	},
	setClientMode: function( bVirtual, oRoot ){
	    if(bVirtual) {
	        this.oReplacedRoot = oRoot;
	        this.oRoot = oRoot;
	    } else {
	        this.oReplacedRoot = null;
	        this.oRoot = this.getNewRoot();
	    }
	},
	getListMode: function(){
	    return !!this.oReplacedRoot;
	},
	getNewRoot: function(){
	    if( this.getListMode())
	        return this.oReplacedRoot;
	    else
	        return new dwa.lv.treeViewEntry(void 0, void 0, this.oDataStore);
	},
	destroyRoot: function(){
	    if( !this.oReplacedRoot && this.oRoot)
	    {
	        this.oRoot.destroy();
	        this.oRoot = null;
	    }
	},
	getEntriesByNoteId: function(p1){
	    return this.oRoot? this.oRoot.getEntriesByNoteId(p1):[];
	},
	getEntriesByUnid: function(p1){
	    return this.oRoot? this.oRoot.getEntriesByUnid(p1):[];
	},
	getMaxHeight: function(){
	    return this.iOneRowHeight;
	},
	getXmlData: function(){
	    return this.oXmlData;
	},
	showScrollHint: function(bShow){
	    var v$ = this;
	    if(!v$.scrollHintAllowed())
	        return;
	
	    var bAlreadyShown = !!v$.hScrollHintTimer;
	    if(v$.hScrollHintTimer)
	        clearTimeout(v$.hScrollHintTimer);
	    v$.hScrollHintTimer = null;
	    
	    var nNewPos = v$.oScrollBar.getCurrentPos();
	    var nPosDiff = Math.abs(v$.oScrollBar.getOldPos() - nNewPos);
	
	    var nTime = (new Date()).getTime();
	    if(nTime - v$.nScrollHintLastTime > 50)
	        v$.nScrollHintCumulativeDiffs = nPosDiff;
	    else
	        v$.nScrollHintCumulativeDiffs += nPosDiff;
	    v$.nScrollHintLastTime = nTime;
	
	    // just return if DiffOfScrollPos < NumRowsByPageDown to not show hint on page up/down/arrows.
	    // not return if hint is already being displayed to update text
	    if(!bAlreadyShown && v$.nScrollHintCumulativeDiffs < v$.iMaxRowsPerPage/2)
	        return;
	
	    if(bShow) {
	        v$.hScrollHintTimer = setTimeout(function(){v$.showScrollHint(false)}, 800);
	
	            switch(this.nScrollHintStatus) {
	                case 0:
	                    this.loadPercentileInfo();
	                    return;
	                case 1:
	                    return;
	                case 2:
	                    break;
	            }
	        // return when no scrollhint data is available.
	        if(!(v$.aScrollHints || v$.oDataStore.getLength(v$.aScrollHints)))
	            return;
	
	        var nIndex = Math.ceil(v$.oScrollBar.getCurrentPos() * (v$.oDataStore.getLength(v$.aScrollHints)-1) / v$.getTotalEntriesInView());
	        var aEntryData = v$.oDataStore.getEntryDatas(v$.oDataStore.getItem(v$.aScrollHints, nIndex));
	        var oArgs = {
	            sInner : ''
	            ,sHtml : ''
	            ,sSTInner : ''
	            ,sClass : ''
	            ,"v$" : v$
	            ,oColInfo : v$.aColInfo[v$.nSortBy != -1 ? v$.nSortBy : v$.nDefaultSortBy]
	            ,sUnid : ''
	            ,oDataItem : v$.oDataStore.getItem(aEntryData, v$.nSortBy != -1 && v$.nSortBy > v$.nDefaultSortBy && v$.oDataStore.getLength(aEntryData)>1 /* for NIFGetCollectionData case */ ? 1 : 0)
	            ,oXmlEntry : v$.oDataStore.getItem(v$.aScrollHints, nIndex)
	            ,oAttr : null
	            ,bConsolidate : true
	            ,bHasIMColInRow : false
	            ,bEmptyEntry : false
	        };
	        var sCellHtml = v$.generateCellHTML(oArgs).sInner;
	        if(bAlreadyShown && sCellHtml == v$.sScrollHintLastHtml)
	            return;
	        v$.sScrollHintLastHtml = sCellHtml;
	
	 if( dojo.isMozilla ){
	        var bCrop = sCellHtml.indexOf('<img')!=0 && sCellHtml.indexOf('<table')!=0 && sCellHtml.indexOf('<span')!=0 && sCellHtml.indexOf('<div')!=0;
	        var oHint = bCrop ? v$.hintTextBinding : v$.hintText;
	        var oHintHide = bCrop ? v$.hintText : v$.hintTextBinding;
	        oHintHide.style.display = 'none';
	 }else{ // G
	        var oHint = v$.hintText;
	 } // end - IS
	        oHint.style.display = 'block';
	        oHint.style.overflow = '';
	        oHint.style.width = 'auto';
	 if( dojo.isMozilla ){
	        // b-croplabel does not support html in value
	        if(bCrop) {
	            oHint.setAttribute('value', sCellHtml.replace(/&amp;/g, '&').replace(/&quot;/g, '\"').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&nbsp;/g, ' '));
	            oHint.setAttribute('com_ibm_dwa_ui_misc_croplabel_width', oHint.parentNode.offsetWidth/2);
	            oHint.style.MozBinding = 'url(' + dwa.common.utils.formatMessage(window.parent.sBindingUrl, 'l_Bindings', 'b-croplabel') + ')';
	        }
	        else {
	            oHint.innerHTML = sCellHtml;
	            oHint.style.overflow = 'hidden';
	            oHint.style.width = oHint.parentNode.offsetWidth/2 + 'px';
	        }
	 }else{ // G
	        oHint.innerHTML = sCellHtml;
	        oHint.style.overflow = 'hidden';
	        oHint.style.textOverflow = 'ellipsis';
	        oHint.style.width = oHint.parentNode.offsetWidth/2 + 'px';
	 } // end - IS
	        oHint.style[ (dojo._isBodyLtr() ? "left" : "right") ] = (oHint.parentNode.offsetWidth - oHint.offsetWidth) / 2 + 'px';
	        oHint.style.top = (oHint.parentNode.offsetHeight - oHint.offsetHeight) / 2 + 'px';
	        oHint.style.visibility = '';
	
	        v$.hintBg.style.display = 'block';
	 if( dojo.isMozilla || dojo.isWebKit ){
	        v$.hintBg.style[ (dojo._isBodyLtr() ? "left" : "right") ] = ((oHint.parentNode.offsetWidth - oHint.offsetWidth) / 2 - 20) + 'px';
	        v$.hintBg.style.top = ((oHint.parentNode.offsetHeight - oHint.offsetHeight) / 2 - 10) + 'px';
	        v$.hintBg.style.width = (oHint.offsetWidth + 20 * 2) + 'px';
	        v$.hintBg.style.height = (oHint.offsetHeight + 10 * 2) + 'px';
	
	        v$.hintBg.setAttribute('width', v$.hintBg.offsetWidth);
	        v$.hintBg.setAttribute('height', v$.hintBg.offsetHeight);
	        dwa.common.graphics.drawRoundRect(v$.hintBg, "black");
	 }else{ // GS
	//#define VL_SCROLLHINT_BG_PADDING    3
	        v$.hintBg.style[ (dojo._isBodyLtr() ? "left" : "right") ] = ((oHint.parentNode.offsetWidth - oHint.offsetWidth) / 2 - 20 - VL_SCROLLHINT_BG_PADDING) + 'px';
	        v$.hintBg.style.top = ((oHint.parentNode.offsetHeight - oHint.offsetHeight) / 2 - 10 - VL_SCROLLHINT_BG_PADDING) + 'px';
	        v$.hintBg.style.width = (oHint.offsetWidth + 20 * 2 + VL_SCROLLHINT_BG_PADDING * 2) + 'px';
	        v$.hintBg.style.height = (oHint.offsetHeight + 10 * 2 + VL_SCROLLHINT_BG_PADDING * 2) + 'px';
	
	        v$.hintBg.innerHTML = '<v:roundrect stroked="false" arc="20%" fillcolor= ' + "black" + ' style="width:' + (oHint.offsetWidth + 20 * 2) + 'px;height:' + (oHint.offsetHeight + 10 * 2) + 'px"></v:roundrect>';
	 } // end - I
	        v$.hintBg.style.visibility = '';
	    }
	    else {
	        v$.hScrollHintTimer = null;
	        v$.nScrollHintCumulativeDiffs = 0;;
	        v$.nScrollHintLastTime = 0;
	        v$.sScrollHintLastHtml = '';
	        v$.hintText.style.visibility = 'hidden';
	        v$.hintText.style.display = 'none';
	 if( dojo.isMozilla ){
	        v$.hintTextBinding.style.visibility = 'hidden';
	        v$.hintTextBinding.style.display = 'none';
	 } // end - G
	        v$.hintText.innerHTML = '';
	        v$.hintBg.style.visibility = 'hidden';
	        v$.hintBg.style.display = 'none';
	    }
	},
	expandEntryByEventStatic: function(ev, v$, bExpand, oTreeViewEntry){
	    if(!v$ && ev)
	    {
	        v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	        oTreeViewEntry = v$.getTreeViewEntry(dwa.lv.benriFuncs.eventGetTarget(ev));
	    }
	    v$.expandEntry(bExpand, oTreeViewEntry);
	},
	onMouseDownStatic: function(ev){
	    // ------------------------------------------
	    // the "onmousedown" handler
	    // ------------------------------------------
	
	    // get the VList
	    var eSrc=dwa.lv.benriFuncs.eventGetTarget(ev);
	    var v$=dwa.lv.virtualList.prototype.getVLStatic(eSrc);
	    if(!v$)return;
	
	    if(eSrc.getAttribute('isfocus')) {
	        var eRow = v$.getFocusedRow();
	        if(v$.handleExpand(ev, eSrc, eRow))
       	            return dwa.lv.benriFuncs.eventCancel(ev);
	        eSrc = eRow;
	    }
	
	    var bCtrl = dwa.lv.benriFuncs.eventIsShortcutKeyPressed(ev);
	    var bShift= ev.shiftKey;
	    
	    // store ctrl shift status to use this in mouseup handler.
	    // user sometimes loses all selection despite shift+clicked to select many docs.
	    // sinse ctrl or shift may be unpressed before mouseup and it invokes 1 entry selection.
	    v$.bCtrlShiftOnMousedown = bCtrl || bShift;
	    
	    var row = dwa.lv.benriFuncs.elGetAttr(eSrc,'tumbler',1);
	    if( row ){
	        // mousedown event comes even with right-mouse menu and preventing default event handler stops context menu event to be fired
	        // for Firefox 3 on Mac
	        // SPR #YHAO7HFCH9 - asudoh 8/20/2008
	        if (ev.button != 2)
	            dwa.lv.benriFuncs.eventPreventDefault(ev);
	
	        // Allow CTRL + Click to select more than one row
	        var aItems = v$.getDD().getSelected(false, v$);
	        if( aItems ){
	            if( bCtrl ){
	                if( v$.getDD().isSelected(row, v$, row.tumbler) ){ v$.deselectEntries([row]); row=null; }
	            }
	            else if( bShift ){
	                if(!v$.lastTumbler)v$.lastTumbler=dwa.lv.benriFuncs.elGetAttr(v$.getDD().getLastSelected(v$),"tumbler");
	                
	                // deselectEntries clears the lastTumbler, so reset it
	                var t1=v$.lastTumbler;
	                v$.deselectEntries();
	                v$.lastTumbler=t1;
	                
	                var nLastPos = v$.oRoot.getIndexByTumbler(v$.lastTumbler);
	                var nPos = v$.oRoot.getIndexByTumbler(row.getAttribute('tumbler'));
	                v$.selectMultiEntries(nLastPos, nPos, true);
	                return dwa.lv.benriFuncs.eventCancel(ev);
	            }
	            else{
	                // If a mouse-down event occurs one of the rows already selected, then do not de-select the other rows.
	                if( !v$.getDD().isSelected(row, v$, row.getAttribute('tumbler')) ) v$.deselectEntries();
	            }
	        }
	        // always append the selected entry
	        // YHAO7CP9PS : don't pass true to 3rd param to not cause drag gesture on right mouse down
	        // YRYR7J4BQ3 : Mac Safari doesn't set 2 to event.button for Ctrl + click (Gecko does) so look for ctrlKey instead.
	 if( dojo.isWebKit ){
	        v$.selectEntry(row,true,((navigator.userAgent.match(/Mac|iPhone/i)) ? !ev.ctrlKey : ((dojo.isIE ? (1) : (dojo.isMozilla ? (0) : (0) ) )==ev.button)),false,2);
	 }else{ // S
	        v$.selectEntry(row,true,(dojo.isIE ? (1) : (dojo.isMozilla ? (0) : (0) ) )==ev.button,false,2);
	 } // end - IG
	        v$.lastTumbler=dwa.lv.benriFuncs.elGetAttr(row,"tumbler");
	    }
	    else{
	        v$.deselectEntries();
	    }
	    v$.unfocusColumn();
	    v$.focus();
	    if(v$.bVcursor || (v$.bSupportScreenReader && !v$.isCategorizedView())) // nakakura
	        v$.nPreviousColIndexForScreenReader = v$.nColIndexForScreenReader = -1;
	    return dwa.lv.benriFuncs.eventCancel(ev);
	},
	handleExpand: function(ev, oFocus, oRow){
	    // TODO: check the behaiviour when the scrollbar is shown in body element..
	    if(dojo.isIE) return;
	    var oMainDoc = dojo.doc;
	    var aImgs = oRow.getElementsByTagName('img');
	    var oFocusPos = {
	        x:this.oFocusOuter.offsetLeft + dwa.lv.benriFuncs.elGetCurrentStyle(this.oFocusOuter, 'marginLeft', true) + ev.layerX,
	        y:this.oFocusOuter.offsetTop + dwa.lv.benriFuncs.elGetCurrentStyle(this.oFocusOuter, 'marginTop', true) + ev.layerY
	    };

	    for(var i=0; i<aImgs.length; i++) {
	        if(aImgs[i] && aImgs[i].getAttribute('twistie')=='1') {
	            var oPos = dwa.lv.benriFuncs.getAbsPos(aImgs[i]);
	            if(oPos.x<=oFocusPos.x && oFocusPos.x<oPos.x+aImgs[i].offsetWidth &&
	                oPos.y<=oFocusPos.y && oFocusPos.y<oPos.y+aImgs[i].offsetHeight) {
	                this.expandEntry(void 0, this.getTreeViewEntry(oRow));
	                return true;
	            }
	        }
	    }
	    return false;
	},
	onContextMenuStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    if(v$.fnContextMenu)
	        return v$.fnContextMenu(ev);
	},
	onHeaderContextMenuStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    if(v$.fnHeaderContextMenu)
	        return v$.fnHeaderContextMenu(ev);
	},
	onClickStatic: function(ev){
	    // ------------------------------------------
	    // This is really the "onmouseup" handler
	    // ------------------------------------------
	
	    // get the VList
	    var eSrc=dwa.lv.benriFuncs.eventGetTarget(ev);
	    var v$=dwa.lv.virtualList.prototype.getVLStatic(eSrc);
	    if(!v$)return;
	
	    if(eSrc.getAttribute('isfocus'))
	        eSrc = v$.getFocusedRow();
	
	    // CTRL and SHIFT clicks are handled in the "onmousedown" handler
	    if(v$.bAllowMultipleSelection && v$.bCtrlShiftOnMousedown) return;
	
	    // return if this is an RMM click and the row clicked is part of the current selection
	    if(2==ev.button && v$.isSelected(eSrc)) return;
	
	
	    // Process the click
	    var row = v$.getDD().getLastSelected(v$);
	    v$.deselectEntries();
	    v$.selectEntry(row,false,false,false,2);
	
	    // establish focus on the row
	    v$.unfocusColumn();
	    v$.focus();
	},
	onDblClickStatic: function(ev,bEnter){
	    // get the VList
	    var eSrc=dwa.lv.benriFuncs.eventGetTarget(ev);
	    var v$=dwa.lv.virtualList.prototype.getVLStatic(eSrc);
	
	    if(eSrc.getAttribute('isfocus')) {
	        eSrc = v$.getFocusedRow();
	
	        // to not invoke event on tbl
	        dwa.lv.benriFuncs.eventStopPropagation(ev)
	    }
	
	    // ------------------------------------------
	    // trap double-click event
	    // ------------------------------------------
	    if (!bEnter) {
	        // SPR # HFUA5B49ZD
	        //  deselect all other entries first
	        v$.deselectEntries(); 
	        // select the entry
	        v$.selectEntry(eSrc,false,false,false,2);
	    }
	    
	    // Expand the category entry
	    var oTreeViewEntry = v$.getTreeViewEntry(eSrc);
	    if (oTreeViewEntry && oTreeViewEntry.hasChildren() && !oTreeViewEntry.getUnid()){
	        // open the selected category
	        v$.expandEntry();
	        return;
	    }
	
	    // Else, open the entry
	    var aUnids = v$.getSelectedData();
	    if(aUnids){
	        if(v$.fnReadDoc)
	            v$.fnReadDoc( aUnids );
	    }
	    else{
	        if(v$.fnCreateDoc)
	            v$.fnCreateDoc();
	    }
	},
	onMouseWheelStatic: function(ev){
	    // get the VList
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	
	 if( dojo.isMozilla ){
	    var bIsUp = ev.detail < 0;
	 }else{ // G
	    var bIsUp = ev.wheelDelta > 0;
	 } // end - IS
	    var nCommand = bIsUp ? 'UP' : 'DOWN';
	    var bScrolled = false;
	    for(var i=0;i<3;i++)
	    {
	        bScrolled |= (v$.oScrollBar.doScroll(nCommand) != 0);
	 if( dojo.isIE ){
	        // draw screen for each scrolling on IE
	        v$.doScroll(nCommand);
	 } // end - I
	    }
	 if( dojo.isMozilla || dojo.isWebKit ){
	    // draw screen after scrolling on Mozilla for performance
	    v$.doScroll(nCommand);
	 } // end - GS
	    
	    if(bScrolled)
	        return dwa.lv.benriFuncs.eventCancel(ev);
	},
	onKeyDownStatic: function(ev){
	    // get the VList
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	
	    // ------------------------------------------
	    // trap Page-Up, Page-Down, Arrow-Up, etc keys
	    // ------------------------------------------
	    var event_type        = '';
	    var bContinueSelection = false;
	
	    // process this event
	    var keyPressed    = ev.keyCode;
	    var keyShift      = ev.shiftKey;
	    var keyCtrl       = dwa.lv.benriFuncs.eventIsShortcutKeyPressed(ev); 
	    var keyAlt        = ev.altKey; 
	    var srcElement    = dwa.lv.benriFuncs.eventGetTarget(ev);
	
	    var nCol = v$.getFocusedColumn();
	    if(nCol != -1) {
	        var bDragging = !!dwa.lv.virtualList.prototype.oDragInfoStatic.bar;
	        switch (keyPressed) {
	        case 32:
	        case 13:
	            if(v$.aColInfo[nCol].isSortable(v$.bIsPrintMode,v$.sThreadRoot,v$.bShowDefaultSort) && !bDragging)
	                v$.resortByColumn(parseInt(nCol,10), void 0, true);
	            return dwa.lv.benriFuncs.eventCancel(ev);
	
	        case 91:
	            if(!(navigator.userAgent.match(/Mac|iPhone/i))) return;
	        case 16:
	        case 17:
	            if(keyShift && keyCtrl && v$.aColInfo[nCol].isMoveable(v$.bIsPrintMode) && !v$.aColInfo[nCol].bIsExtend && !bDragging)
	                v$.startKeyDrag(nCol);
	            return dwa.lv.benriFuncs.eventCancel(ev);
	
	        case 39:
	        case 37:
	            var bReverse = keyPressed == (!dojo._isBodyLtr() ? 39 : 37);
	            if(keyShift && keyCtrl && bDragging) {
	                v$.doKeyDrag(bReverse ? -5 : 5);
	            }
	            else if(!keyShift && !keyCtrl&& !bDragging) {
	                var nNewCol = v$.findNextColumn(nCol, bReverse);
	                if(nNewCol != -1)
	                    v$.focusColumn(nNewCol);
	            }
	            return dwa.lv.benriFuncs.eventCancel(ev);
	
	        case 9:
	            if(bDragging)
	                return dwa.lv.benriFuncs.eventCancel(ev);
	            if(keyShift)
	                return;
	        case 27:
	            v$.unfocusColumn();
	            v$.focus();
	            dwa.lv.benriFuncs.eventCancel(ev);
	            break;
	        default:
	            return;
	        }
	        
	        return dwa.lv.benriFuncs.eventPreventDefault(ev);
	    }
	
        if( keyShift && keyPressed === 13 ){
            return v$.checkEnteringEditMode(ev);
        }

	    // SHIFT + PAGE  keys = selection
	    // SHIFT + ARROW keys = selection
	    // CTRL  + ARROW keys = move without breaking selection
	    // CTRL  + SPACE key  = de/select entry
	    // SHIFT + CTRL + END or HOME = select all to end or beginning
	
	    if( keyShift ){
	      var scrolls = { 36: 'TOP', 35: 'BOTTOM', 34: 'PAGEDOWN', 33: 'PAGEUP'};
              var scr = scrolls[keyPressed];
	      if( scr ){
	        if(!v$.lastTumbler)v$.lastTumbler=dwa.lv.benriFuncs.elGetAttr(v$.getDD().getLastSelected(v$),"tumbler");

	        // deselectEntries clears the lastTumbler, so reset it
	        var t1=v$.lastTumbler;
	        v$.deselectEntries();
	        v$.lastTumbler=t1;

		//v$.postScroll(scr, keyShift);
		v$.oScrollBar.doScroll(scr);
		v$.doScroll(scr);

		//v$.invalidateForcedCaretMoving();
		//v$.nMoveCaretAfterScroll = 3;
		//v$.moveCaretAfterScroll();
		v$.adjustSelection( /*isUP*/ (keyPressed&2) == 0, true, true, true);

	        var nLastPos = v$.oRoot.getIndexByTumbler(v$.lastTumbler);
	        v$.selectMultiEntries(nLastPos, v$.iSelectedPosition, true);

	    	// do scrolling operation
	    	return dwa.lv.benriFuncs.eventPreventDefault(ev);
              }
            }

	    // nakakura
	    if (v$.bVcursor || v$.bSupportScreenReader)
	            v$.handleKeyDownForScreenReader(keyPressed, keyShift);
	    switch (keyPressed) {
	    case 36:        // SHIFT + CTRL + HOME
	        // goto top position
		keyShift = keyCtrl = false;
	
	        // if SHIFT + CTRL, then select all 
	        // from last selection to top 
	        if (keyShift && keyCtrl) {
	            bContinueSelection = true;
	        }
	
	        // else, kill selection
	        else {
	            v$.deselectEntries();
	        }
	        v$.postScroll('TOP', keyShift);
	        break;
	
	    case 35:        // SHIFT + CTRL + END
	        // goto bottom position
		keyShift = keyCtrl = false;
	
	        // if SHIFT + CTRL, then select all 
	        // from last selection to bottom 
	        if (keyShift && keyCtrl) {
	            bContinueSelection = true;
	        }
	
	        // else, kill selection
	        else {
	            v$.deselectEntries();
	        }
	        v$.postScroll('BOTTOM', keyShift);
	        break;
	
	    case 32:
	        // win behavior is to reverse selection if CTRL is also on
	        //  we should reverse selection whether or not CTRL is on
	        dwa.lv.benriFuncs.eventPreventDefault(ev);
	        return;
	
	    case 34:
		keyShift = keyCtrl = false;
	        if (keyShift) {
	            bContinueSelection = true;
	        }
	        else if (!keyCtrl) {
	            v$.deselectEntries();
	        }
	        v$.postScroll('PAGEDOWN', keyShift);
	        break;
	
	    case 33:
		keyShift = keyCtrl = false;
	        if (keyShift) {
	            bContinueSelection = true;
	        }
	        else if (!keyCtrl) {
	            v$.deselectEntries();
	        }
	        v$.postScroll('PAGEUP', keyShift);
	        break;
	
	    case 40:
	    case 38:
	        if (!keyShift) {
	            v$.deselectEntries();
	        }
	        dwa.lv.benriFuncs.eventCancel(ev);
	        v$.invalidateForcedCaretMoving();
	        if(v$.doSelection((keyPressed==38), keyShift)) v$.postScroll('UP',keyShift);
	        return;
	
	    case 13:
	        // treat as double click on an entry
	        dwa.lv.virtualList.prototype.onDblClickStatic(ev,true);
	        break;
	
	    case 45:
	        // treat as a new document
	        if (v$.fnCreateDoc){
	            dwa.lv.benriFuncs.eventCancel(ev);
	            if ("string" == typeof(v$.fnCreateDoc)){
	                eval(v$.fnCreateDoc + "()");
	            }
	            else{
	                v$.fnCreateDoc();
	            }
	        }
	        return;
	
	    case 46:
	        if (v$.fnDelete) {
	            dwa.lv.benriFuncs.eventCancel(ev);
	            v$.fnDelete();
	        }
	        return;
	
	    case 39:
	    case 187:
	        v$.expandEntry(true);
	        break;
	    case 37:
	    case 189:
	        v$.expandEntry(false);
	        break;
	
	    case 120:
	        if(!v$.sThreadRoot)v$.refresh(true);
	        // must not call bodykeyhandler to refresh view again by F9.
	        return dwa.lv.benriFuncs.eventCancel(ev);
	
	    case 9:
	        if(!keyShift || !v$.hasResortableColumn())
	            return;
	        var nSortBy = v$.nSortBy != -1 ? v$.nSortBy : v$.nDefaultSortBy != -1 ? v$.nDefaultSortBy : 0;
	        v$.focusColumn(nSortBy);
	        dwa.lv.benriFuncs.eventCancel(ev);
	        break;
	
	    default:
	        // Is Character Numeric or Alphabetic?
	        //  all A ~ Z chars are upper-case values ... look for shift key if you are
	        //  trying to determine whether the user typed a lower or upper case char
	        //  keyboard letters:         "A" == 65, "Z" == 90
	        //  top of keyboard numbers:  "0" == 48, "9" == 57
	        //  num-lock keypad numbers:  "0" == 96, "9" == 105
	        //   we need to shift the keypad code down to the keyboard range so that
	        //   String.fromCharCode recognizes the character
	        keyPressed = (keyPressed > 95 && keyPressed < 106)? (keyPressed - 48) : keyPressed;
	        var isAlphaNumeric = (keyPressed >= 48 && keyPressed <= 57) 
	            || (keyPressed >= 65 && keyPressed <= 90);
	
	        // Non-alpha-numeric Character
	        if (!isAlphaNumeric || keyAlt || keyCtrl) {
	            if(keyPressed == 65 && keyCtrl) {
	                v$.selectAllEntries();
	                return dwa.lv.benriFuncs.eventCancel(ev);
	            }
	            return;
	        }
	
	        return;
	    }
	    // do scrolling operation
	    return dwa.lv.benriFuncs.eventPreventDefault(ev);
	},
	onKeyUpStatic: function(ev){
	    // get the VList
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	
	    // process this event
	    var keyReleased    = ev.keyCode;
	    var keyShift      = ev.shiftKey;
	    var keyCtrl       = dwa.lv.benriFuncs.eventIsShortcutKeyPressed(ev); 
	    var keyAlt        = ev.altKey; 
	
	    switch (keyReleased) {
	    case 91:
	        if(!(navigator.userAgent.match(/Mac|iPhone/i))) return;
	    case 16:
	    case 17:
	        var nCol = v$.getFocusedColumn();
	        if(nCol != -1 && v$.aColInfo[nCol].isMoveable(v$.bIsPrintMode) && !!dwa.lv.virtualList.prototype.oDragInfoStatic.bar && dwa.lv.virtualList.prototype.oDragInfoStatic.bByKey) {
	            v$.endDrag();
	//            v$.showColumnHover(v$.aColHdr[nCol], true);
	            return dwa.lv.benriFuncs.eventCancel(ev);
	        }
	    }
	},
	onKeyPressStatic: function(ev){
	    // Mozilla : cancel browser's default event handler
	
	    // process this event
	 if( dojo.isMozilla || dojo.isWebKit ){
	    var keyPressed = ev.charCode;
	 }else{ // GS
	    var keyPressed = ev.keyCode;
	 } // end - I
	    var keyShift   = ev.shiftKey;
	    var keyCtrl    = dwa.lv.benriFuncs.eventIsShortcutKeyPressed(ev); 
	    var keyAlt     = ev.altKey; 
	    // Additional fix for MHEG7JZK56 : need to return if this event is called unexpectedly
	    if(!keyPressed)
	        return;
	    // just return for some shortcut keys
	    if (keyAlt || keyCtrl)
	        return;
	
	    switch (keyPressed) {
	        case 36:
	        case 35:
	        case 32:
	        case 34:
	        case 33:
	        case 40:
	        case 38:
	        case 13:
	        case 45:
	        case 46:
	        case 39:
	        case 187:
	        case 37:
	        case 189:
	        case 120:
	            dwa.lv.benriFuncs.eventCancel(ev);
	            break;
	        case 9:
	        case 27:
	            // just through to close tab
	            break;
	        default:
	            //Moved over from VLEvent_onKeyDown since keydown returns the same value for both lowercase/uppercase keys
	            //, while keypress returns different values that we can use.
	            // Alpha-numeric Character
	            var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	            if (!v$) return;
	            v$.simpleSearch(String.fromCharCode(keyPressed));
	            dwa.lv.benriFuncs.eventPreventDefault(ev);
	    }
	    return false;
	},
	onPosChanged: function(event_type, nNewIndex){
	    // scroll to the position of the scroll-button
	    var v$ = this;
	
	    // revert scroll pos for reauthentication : BCOE6NTN7G
	    if( v$.doScroll(event_type) == 2 )
	        setTimeout(function(){v$.revertScrollPos(v$.nOldScrollPos)}, 0);
	    v$.resetFlags();
	},
	onScrollHint: function(){
	    var v$=this;
	    v$.showScrollHint(true);
	},
	onBeforeScroll: function( sEvent, nNewPos ){
	    var v$=this;
	
	    // remember scroll pos to revert for reauthentication : BCOE6NTN7G
	    v$.nOldScrollPos = v$.oScrollBar.getOldPos();
	
	    if(typeof nNewPos == 'undefined')
	        nNewPos = v$.iCurrentPosition;
	    
	    if(nNewPos < 0)
	        nNewPos = 0;
	    if(nNewPos > v$.getLastEntryPosInView())
	        nNewPos = v$.getLastEntryPosInView();
	
	    var oInfo = v$.calcInvalidateInfo(nNewPos);
	
	    var bNeedLoad = false;
	    if(v$.oRoot)
	    {
	        for(var i=oInfo.iStart;i<oInfo.iStart+oInfo.nCount;i++)
	            if(!v$.oRoot.getEntryByIndex(i, true))
	            {
	                bNeedLoad = true;
	                break;
	            }
	    }
	    else
	    {
	        bNeedLoad = true;
	    }
	    
	    var bNeedInsertRow = false;
	    if(oInfo.nCount > 1)
	        bNeedInsertRow = true;
	    
	    if(bNeedLoad)
	        v$.showWaitBox(true, oInfo.bAddToTop ? -1 : 1);
	    else
	    if(bNeedInsertRow)
	        v$.showWaitBox(true, void 0);
	},
	onColumnHoverStatic: function(ev){
	    // get the VList
	    var eSrc = dwa.lv.benriFuncs.eventGetTarget(ev);
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(eSrc);
	    if(!v$)return;
	    dwa.lv.benriFuncs.eventCancel(ev);
	
	    var nFocused = v$.getFocusedColumn();
	    if(nFocused!=-1) {
	        if(eSrc.getAttribute('isfocus'))
	            eSrc = v$.aColHdr[nFocused];
	        var iCol = dwa.lv.benriFuncs.findAttr("i_col",eSrc);
	        if(v$.bNarrowMode || nFocused==parseInt(iCol))
	            return;
	    }
	
	    switch(ev.type){
	        case 'mouseover':
	            {
	                var oFrom = dwa.lv.benriFuncs.eventGetFromTarget(ev);
	                var idSrc = dwa.lv.benriFuncs.findAttr("columnid",eSrc);
	                var idFrom = dwa.lv.benriFuncs.findAttr("columnid",oFrom);
	                if(!(idSrc && idFrom && idSrc==idFrom)) {
	                    var oCol = dwa.lv.benriFuncs.findAttr("columnid",eSrc,true);
	                    oCol.setAttribute('hovered', '1');
	                    if(oCol && oCol.getAttribute('resortable') /*&& oCol.className.indexOf('-resorted')==-1*/ && oCol.className.indexOf('-hover')==-1) {
	                        v$.unfocusColumn();
	                        v$.showColumnHover(oCol, true);
	                    }
	                }
	            }
	        break;
	        case 'mouseout':
	            {
	                var oTo = dwa.lv.benriFuncs.eventGetToTarget(ev);
	                var idSrc = dwa.lv.benriFuncs.findAttr("columnid",eSrc);
	                var idTo = dwa.lv.benriFuncs.findAttr("columnid",oTo);
	                if(!(idSrc && idTo && idSrc==idTo)) {
	                    var oCol = dwa.lv.benriFuncs.findAttr("columnid",eSrc,true);
	                    oCol.setAttribute('hovered', '');
	                    if(oCol && oCol.getAttribute('resortable') && oCol.className.indexOf('-hover')!=-1)
	                        v$.showColumnHover(oCol, false);
	                }
	            }
	        break;
	    }
	},
	onColumnClickStatic: function(ev){
	    // get the VList
	    var eSrc = dwa.lv.benriFuncs.eventGetTarget(ev);
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(eSrc);
	    if(!v$)return;
	    dwa.lv.benriFuncs.eventCancel(ev);
	
	    if(eSrc.getAttribute('isfocus')) {
	        ;
	        eSrc = v$.aColHdr[v$.getFocusedColumn()];
	        
	        // to not invoke event on tbl
	        dwa.lv.benriFuncs.eventStopPropagation(ev)
	    } else {
	        ;
	    }
	    // ------------------------------------------
	    // trap column twisty click, trigger resort
	    // ------------------------------------------
	    var nCol = dwa.lv.benriFuncs.findAttr("i_col", eSrc);
	    if ("undefined" != typeof(nCol)) {
	        v$.showColumnHover(dwa.lv.benriFuncs.findAttr("i_col", eSrc, true), false);
	        v$.resortByColumn(parseInt(nCol,10), void 0, true);
	    }
	},
	resortByColumnStatic: function(idVL, nCol){
	    var v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[idVL];
	    if (!v$) {return;}
	    
	    v$.resortByColumn(nCol, void 0, true);
	},
	onResortMenuStatic: function(ev){
	    // get the VList
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    if(!v$)return;
	    dwa.lv.virtualList.prototype.onHeaderContextMenuStatic(ev);
	    dwa.lv.benriFuncs.eventCancel(ev);
	},
	onResize: function( ev, bNoCancel){
	    
	    
	    var v$ = this;
	 if( dojo.isIE ){
	    if(!bNoCancel)
	        dwa.lv.benriFuncs.eventCancel(ev);
	 } // end - I
	
	    // call the re-size handler after a timeout, to avoid handling many resize events
	    if (v$.bResizeInProgress) {return;}
	    if (v$.nIdTimerResize) clearTimeout( v$.nIdTimerResize );
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	    // specify temporary width to ListContainer. Will be recalced in VL_doResize with '100%'. SPR:XMXL68Y8X3
	    if (v$.tblContainer)
	         v$.tblContainer.parentNode.style.width = v$.oContainer.clientWidth - v$.getScrollBarWidth() + 'px';
	 } // end - GS
	
	    v$.nIdTimerResize = setTimeout(function(){v$.doResize(true)}, 200);
	
	    v$.adjustElementSizes();
	    
	},
	onFocusStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    dwa.lv.benriFuncs.eventCancel(ev);
	    v$.focus();
	},
	onBlurStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    v$.blur(ev);
	},
	onRowBlurStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    v$.onRowBlur();
	},
	onColumnBlurStatic: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
	    v$.onColumnBlur();
	},
	onGotoClick: function(ev){
	    var v$ = this;
	    dwa.lv.benriFuncs.eventCancel(ev);
	    v$.simpleSearch();
	},
	getVLStatic: function(oElement){
	    // ------------------------------------------
	    // return the VirtualList object
	    // ------------------------------------------
	    var idVL;
	    for( var obj=oElement; obj; obj=obj.parentNode ){
	        if( obj.getAttribute ){
	            idVL=obj.getAttribute("idvl");
	            if( idVL ) break;
	        }
	    }
	    return( idVL ? dwa.lv.listViewBase.prototype.aComponentsStatic[idVL] : null );
	},
	onColumnResetStatic: function(arg){
	    var v$;
	    if("string"==typeof(arg))
	        v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[arg];
	    else
	        v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(arg));
	    if(v$.bNonDefaultWidths) {
	        // Custom widths in effect
	        // Should we ask whether to reset
	        // alert("Reset column width?");
	        
	        // replace special characters in the view name with an underscore char
	        var fldName = "VL"  + (v$.sThreadRoot? 'Thread':'') + v$.profileModifier + v$.sFolderName.replace(/[  $\(\)]/g,"_");
	
	        dwa.lv.miscs.getNotesListViewProfileCache().set(fldName, '', true, v$.sId);
	        v$.bNonDefaultWidths = false;
	        v$.resetColumnWidths();
	        v$.changeNarrowMode(v$.needToBeNarrow());
	    }
	},
	oDragInfoStatic: {bar:null,idVL:null,k:0,x:0,X:0,c:0,m:0,w:0,o:0,n:0},
	onColumnResizeStatic: function(ev, oDrag){
	    // ------------------------------------------
	    // drag start event
	    // ------------------------------------------
	    var bByKey = !!oDrag;
	    oDrag = oDrag || dwa.lv.benriFuncs.findAttr('i_col', dwa.lv.benriFuncs.eventGetTarget(ev), true);
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(oDrag);
	    if(!v$)return;
	    var p=dwa.lv.benriFuncs.getAbsPos(oDrag,true);
	    oBar = dojo.doc.createElement("DIV");
	    oBar.setAttribute('idvl', v$.sId);
	    with( oBar.style ){
	        position="absolute";
	        top     =p.y;
	        left    =p.x;
	        width   ="1px";
	        height  =(dojo.doc.body.clientHeight - p.y) + "px";
	        display ="block";
	        zIndex  =999;
	        backgroundColor="#000000";
	    }
	    
	    with( dwa.lv.virtualList.prototype.oDragInfoStatic ){
	        bar = dojo.doc.body.appendChild( oBar );
	        x = ev ? ev.clientX : p.x;
	        X = ev ? ev.clientX : p.x;
	        idVL = v$.sId;
	    }
	    dwa.lv.virtualList.prototype.oDragInfoStatic.c = parseInt(oDrag.getAttribute("i_col"),10);
	    dwa.lv.virtualList.prototype.oDragInfoStatic.maxWidth = v$.getColumnMaxWidth(dwa.lv.virtualList.prototype.oDragInfoStatic.c);
	    dwa.lv.virtualList.prototype.oDragInfoStatic.width = v$.aColInfo[dwa.lv.virtualList.prototype.oDragInfoStatic.c].nWidth;
	    dwa.lv.virtualList.prototype.oDragInfoStatic.orgLeft = p.x;
	    dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth = dwa.lv.virtualList.prototype.oDragInfoStatic.width;
	    dwa.lv.virtualList.prototype.oDragInfoStatic.bByKey = bByKey;
	
	    if(!bByKey) {
	        try {
	 if( dojo.isMozilla || dojo.isWebKit ){
	            dojo.doc.addEventListener("mousemove", dwa.lv.virtualList.prototype.doDrag2Static, true);
	            dojo.doc.addEventListener("mouseup", dwa.lv.virtualList.prototype.endDrag2Static, true);
	 }else{ // GS
	            dwa.lv.virtualList.prototype.oDragInfoStatic.bar.attachEvent("onmousemove", dwa.lv.virtualList.prototype.doDrag2Static);
	            dwa.lv.virtualList.prototype.oDragInfoStatic.bar.attachEvent("onmouseup", dwa.lv.virtualList.prototype.endDrag2Static);
	            dwa.lv.virtualList.prototype.oDragInfoStatic.bar.setCapture();
	 } // end - I
	        }
	        catch (er) {
	        }
	    }
	},
	doDrag2Static: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev)) || dwa.lv.listViewBase.prototype.aComponentsStatic[dwa.lv.virtualList.prototype.oDragInfoStatic.idVL];
	    return v$.doDrag(ev);
	},
	doDrag: function(ev){
	    // ------------------------------------------
	    // called during a drag event
	    // ------------------------------------------
	    var v$ = this;
	    var mouseX = ev.clientX;
	    var dx = mouseX - dwa.lv.virtualList.prototype.oDragInfoStatic.X;
	    if (0==dx){return;}
	
	    if (!dojo._isBodyLtr()) {
	       dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth = Math.min(Math.max(dwa.lv.virtualList.prototype.oDragInfoStatic.width - dx, v$.getMinColumnWidth(dwa.lv.virtualList.prototype.oDragInfoStatic.c)), dwa.lv.virtualList.prototype.oDragInfoStatic.maxWidth);
	       dwa.lv.virtualList.prototype.oDragInfoStatic.bar.style.left = dwa.lv.virtualList.prototype.oDragInfoStatic.orgLeft - (dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth - dwa.lv.virtualList.prototype.oDragInfoStatic.width) + 'px';
	    }
	    else {
	       dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth = Math.min(Math.max(dwa.lv.virtualList.prototype.oDragInfoStatic.width + dx, v$.getMinColumnWidth(dwa.lv.virtualList.prototype.oDragInfoStatic.c)), dwa.lv.virtualList.prototype.oDragInfoStatic.maxWidth);
	       dwa.lv.virtualList.prototype.oDragInfoStatic.bar.style.left = dwa.lv.virtualList.prototype.oDragInfoStatic.orgLeft + (dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth - dwa.lv.virtualList.prototype.oDragInfoStatic.width) + 'px';
	    }
	    dwa.lv.virtualList.prototype.oDragInfoStatic.x = mouseX;
	},
	endDrag2Static: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev)) || dwa.lv.listViewBase.prototype.aComponentsStatic[dwa.lv.virtualList.prototype.oDragInfoStatic.idVL];
	    return v$.endDrag(ev);
	},
	endDrag: function(){
	    // ------------------------------------------
	    // release capture of mouse
	    // ------------------------------------------
	    var v$ = this;
	    dwa.lv.virtualList.prototype.oDragInfoStatic.bar.style.display = "none";
	    if(!dwa.lv.virtualList.prototype.oDragInfoStatic.bByKey) {
	 if( dojo.isMozilla || dojo.isWebKit ){
	        dojo.doc.removeEventListener("mousemove", dwa.lv.virtualList.prototype.doDrag2Static, true);
	        dojo.doc.removeEventListener("mouseup", dwa.lv.virtualList.prototype.endDrag2Static, true);
	 }else{ // GS
	        dwa.lv.virtualList.prototype.oDragInfoStatic.bar.detachEvent("onmousemove", dwa.lv.virtualList.prototype.doDrag2Static);
	        dwa.lv.virtualList.prototype.oDragInfoStatic.bar.detachEvent("onmouseup", dwa.lv.virtualList.prototype.endDrag2Static);
	        dojo.doc.releaseCapture();
	 } // end - I
	    }
	
	    if (dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth != dwa.lv.virtualList.prototype.oDragInfoStatic.width){
	        v$.aColInfo[dwa.lv.virtualList.prototype.oDragInfoStatic.c].nOrgWidth = v$.aColInfo[dwa.lv.virtualList.prototype.oDragInfoStatic.c].nWidth = dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth;
	        v$.syncColSS(0,1,0);
	        v$.changeNarrowMode(v$.needToBeNarrow());
	    }
	
	    dojo.doc.body.removeChild( dwa.lv.virtualList.prototype.oDragInfoStatic.bar );
	    dwa.lv.virtualList.prototype.oDragInfoStatic.bar=null;
	},
	startKeyDrag: function(nCol){
	    dwa.lv.virtualList.prototype.onColumnResizeStatic(null, this.aDragBar[nCol]);
	},
	doKeyDrag: function(dx){
	    var v$ = this;
	    dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth += dx;
	    if (!dojo._isBodyLtr()) {
	       dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth = Math.min(Math.max(dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth, v$.getMinColumnWidth(dwa.lv.virtualList.prototype.oDragInfoStatic.c)), dwa.lv.virtualList.prototype.oDragInfoStatic.maxWidth);
	       dwa.lv.virtualList.prototype.oDragInfoStatic.bar.style.left = dwa.lv.virtualList.prototype.oDragInfoStatic.orgLeft - (dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth - dwa.lv.virtualList.prototype.oDragInfoStatic.width) + 'px';
	    }
	    else {
	       dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth = Math.min(Math.max(dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth, v$.getMinColumnWidth(dwa.lv.virtualList.prototype.oDragInfoStatic.c)), dwa.lv.virtualList.prototype.oDragInfoStatic.maxWidth);
	       dwa.lv.virtualList.prototype.oDragInfoStatic.bar.style.left = dwa.lv.virtualList.prototype.oDragInfoStatic.orgLeft + (dwa.lv.virtualList.prototype.oDragInfoStatic.newWidth - dwa.lv.virtualList.prototype.oDragInfoStatic.width) + 'px';
	    }
	},
	ExpandStatic: function(idVL){
	    var v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[idVL];
	    v$.expandEntry(true);
	    v$.focus();
	},
	ExpandAllStatic: function(idVL){
	    var v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[idVL];
	    v$.refresh(false, false, true, false);
	},
	CollapseStatic: function(idVL){
	    var v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[idVL];
	    v$.expandEntry(false);
	    v$.focus();
	},
	CollapseAllStatic: function(idVL){
	    var v$ = dwa.lv.listViewBase.prototype.aComponentsStatic[idVL];
	    v$.refresh(false, false, false, true);
	},
	getTreeViewEntry: function(oElement){
	    // ------------------------------------------
	    // return the TreeViewEntry object
	    // ------------------------------------------
	    var v$ = this;
	    var oTr = v$.getRowElement(oElement);
	    if( oTr ){
	        return v$.oRoot.getEntryByTumbler(oTr.getAttribute("tumbler"), true);
	    }
	},
	getRowElement: function( p1 ){
	    // return the row element from the childNode
	    var id;
	    for( var oTr=p1,i=0; oTr && i<50; oTr=oTr.parentNode,i++ ){
	        if( oTr.getAttribute ){
	            id=oTr.getAttribute("tumbler");
	            if( id ) return( oTr );
	        }
	    }
	    return null;
	},
	getColumnInfo: function( p1 ){
	    // ----------------------------------------------------
	    // Translate new ColumnInfo data to old format
	    // ----------------------------------------------------
	    if( !p1 ) return null;
	    
	    var aInfo=[];
	    for( var i=0,v$=this; i<v$.nColumnSpan; i++ ){
	        // blank columns will be removed
	        aInfo[i] = {sTitle:v$.aColInfo[i].sText, nColFmt:v$.aColInfo[i].nColFmt, bResortable:(!!v$.aColInfo[i].sText && v$.aColInfo[i].isSortable(false,false,v$.bShowDefaultSort))};
	    }
	    return aInfo;
	},
	doColorChange: function(){
	    // ----------------------------------------------------
	    // change row color on timeout to avoid Windows' repaint problems
	    // ----------------------------------------------------
	    var v$=this,eRow,i=1;
	    if( v$.$tumblers ){
	        for( var t1 in v$.$tumblers ){
	            eRow=v$.getRowByIndex(v$.oRoot.getIndexByTumbler(t1) - v$.viewPos(0));
	            if(eRow){
	                var bSelect = !!v$.$tumblers[t1];
	                var sClass = v$.getClassSelected();
	                if (bSelect ^ (dwa.common.utils.indexOf(eRow.className.split(' '), sClass) >= 0)) {
	                    dwa.common.utils.cssEditClassExistence(eRow, sClass, bSelect);
	                    v$.enumlateCellsInRow(eRow, bSelect ? v$.setSelected : v$.setUnselected);
	                }
	            }
	            delete v$.$tumblers[t1];
	            // only do 25 rows at a time
	            if( 25==i++ ){
	                return setTimeout(function(){v$.doColorChange()},0);
	            }
	        }
	    }
	    v$.$doColor=false;
	    v$.$tumblers=null;
	},
	revertScrollPos: function(nOldPos){
	    var v$=this;
	    v$.oScrollBar.setPos(v$.nOldScrollPos, true);
	    v$.adjustTable(true);
	},
	adjustElementSizes: function(){
	    if((dojo.isIE || dojo.isMozilla) && dojo.doc.compatMode == "CSS1Compat")
	    	try {
	    		// TJT: wrapped the following line in a try-catch, because this.listTd kept returning undefined
	    		// this was causing any other dijits on the same page to fail to render
	    		this.listTd.style.height = (this.getContainerHeight() - (this.bShowHeader ? this.header.offsetHeight : 0)) + 'px';
	    	} catch (e) {
	    		console.log("adjustElementSizes: " + e.toString());
	    	}
	}
});
