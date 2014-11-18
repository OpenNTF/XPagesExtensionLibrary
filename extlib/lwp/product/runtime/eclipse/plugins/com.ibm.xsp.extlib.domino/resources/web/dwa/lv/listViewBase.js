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

dojo.provide("dwa.lv.listViewBase");

dojo.require("dojo.i18n");
dojo.requireLocalization("dwa.lv", "listview_s");
dojo.requireLocalization("dwa.lv", "listview_c");
dojo.require("dwa.date.dateFormatter");
dojo.require("dwa.lv.autoConsolidatedImageListener");
dojo.require("dwa.lv.autoConsolidatedImageListenerA11y"); // nakakura
dojo.require("dwa.common.utils");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.benriFuncs");

dojo.declare(
	"dwa.lv.listViewBase",
	null,
{
	constructor: function(){
		this._smsgs = dojo.i18n.getLocalization("dwa.lv", "listview_s", this.lang);
		this._cmsgs = dojo.i18n.getLocalization("dwa.lv", "listview_c", this.lang);
	},
	LV$Static: {
     dt:null
    ,iso:null
    ,st:null        // start tag
    ,t1:null        // start tag
    ,t2:null        // end tag

    ,init           : function(p1){
        this.st=p1;
        this.t1='<'+p1;
        this.t2='</'+p1+'>';
    }
    
    ,getStartTag    : function(p1){
        if( p1!=this.st ) this.init(p1);
        return this.t1;
    }
    ,getEndTag      : function(){
        return this.t2;
    }
    ,getUnreadFmt   : function(p1){
        // p1 == bShowUnread
        if(!this.u1){this.u1='vl-font-b';}
        return p1? this.u1:'';
    }
    ,isPrintMode    : false
},
	init: function(sId, nCol, bAltRowClr, nColHdrOffset, sColHdrId, bContainerHasAbsHeight, sTargetTumbler, bShowUnread){
	    this.sId = sId; // must be synched with name of stylesheet
	    this.sMenuId = sId + 'Menu';
	    this.sNarrowMenuId = sId + 'NarrowMenu';
	    
	    this.sContainerId = sId;
	    this.oContainer = dojo.doc.getElementById(sId);
	    if(this.oContainer && (dojo.isIE || dojo.isMozilla) && dojo.doc.compatMode == "CSS1Compat")
	        this.oContainer.style.overflow = 'hidden';
	    
	    this.oActionBar = null;
	    this.bShowHeader = false;
	    this.bHeaderInActionBar = false;
	    if (!sColHdrId || sId == sColHdrId) {
	        this.oActionBar = this.oContainer;
	        this.bHeaderInActionBar = false;
	        this.bShowHeader = true;
	    }
	    else if (sColHdrId && sColHdrId.toLowerCase() != "none") {
	        this.oActionBar = dojo.doc.getElementById(sColHdrId);
	        this.bHeaderInActionBar = true;
	        this.bShowHeader = true;
	    }
	
	    this.fnReadDoc = null;
	    this.fnCreateDoc = null;
	    this.fnFormatRow = null;
	    this.fnClassRow = null;
	    this.fnSortChanged = null;
	    this.fnSearch = null;
	    this.fnDelete = null;
	    this.fnSelect = null;
	    this.fnStartsWith = null;
	    this.fnContextMenu = null;
	    this.fnCanOpenDocument = null;
	    this.fnBeforeRefresh = null;
	    this.fnAfterRefresh = null;
	    this.fnSweepEntries = null;
	    this.fnLoadedData = null;
	    this.fnAfterGenerateContainer = null;
	    this.fnNarrowStyleChanged = null;
	
	    this.iWidth = 0;
	    this.iHeight = 0;
	
	    this.iAvgRowHeight = 15;
	    this.iOneRowHeight = 15;
	    this.iCharWidth    = 10;
	    this.iMaxRowsPerPage = 1;
	    
	    this.sTextlistSeparator = ', ';
	    this.bAlternateRows = bAltRowClr ? true : false;
	    this.bAllowMultipleSelection = true;
	
	    this.sColumnClassName = this.sId.replace(/[:,.]/g,"-") + "-Col";
	    this.sCellTagName = "SPAN";
	    this.sRowTagName  = "DIV";
	
	    this.bUseStartKey = false;
	
	    this.oKeyTrapAnchor = null;
	
	    this.oColHdr = null;
	    this.oNarrowHdr = null;
	
	    this.bIsPrintMode = false;
	    // for namepicker support -- this.sReadViewEntriesForm = "s_ReadViewEntries";
	    this.nDirIndex = '';
	    this.sFolderName = "(iNotes_Contacts)";
	    this.bAllowScrollHint = false;
	    this.hScrollHintTimer = null;
	    this.aScrollHints = [];
	
	    this.sThreadRoot = sTargetTumbler;
	
	    this.bUseStartKeyOnly = false;
	    this.bIncludeUntilKey = false;
	    this.sKeyType = '';
	    this.sSearchKey = '';
	    this.sStartKey = '';
	    this.sUntilKey = '';
	    this.nSortBy = -1;
	    this.sSortType = "";
	    this.nSortCol = -1;
	    this.nDefaultSortBy  = -1;
	    this.oPresetFields   = {};
	    this.sCategory  = "";
	    this.nLimit     = 0;
	
	    this.sRowFmt = "vl-row";
	    this.sCellFmt = (this.sId.indexOf('Component_') > 0) ? "vl-cell-xx" : "vl-cell";
	    this.sUnreadFmt  = dwa.lv.listViewBase.prototype.LV$Static.getUnreadFmt(bShowUnread);
	    this.bUseRangeEntries = false;
	    this.bAllowCategoryAction = this.sThreadRoot ? false : true;
	    this.bAllowStoreColumnCookie = true;
	    this.bShowTwistieButton = true;
	    this.bAlwaysSendStartKey = false;
	    this.bShowDefaultSort = false;
	
	    this.bCanBeNarrowMode = false;
	    this.bNarrowMode = false;
	    this.aLayoutInfo = null;
	    this.bUseHCSortIcon = false;
	    this.bAdjustParent = bContainerHasAbsHeight? false:true;
	
	    this.oSS        = null;
	    this.oSSSpan    = null;
	
	    this.nFocusedColumn = -1;
	    this.oFocusedRow = null;
	    this.oColumnFocus = null;
	    this.oColumnFocusOuter = null;
	    this.oFocus = null;
	    this.oFocusRight = null;
	    this.oFocusTop = null;
	    this.oFocusBottom = null;
	    this.oFocusOuter = null;
	    this.hFocus = null;
	    this.hBlur = null;
	    this.nFocusMode = 0;
	    this.bHasFocus = false;
	
	    this.anStyleWidths = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10];
	    this.anStyleWidthsSpan = [10, 10, 10, 10, 10, 10, 10, 10, 10, 10];
	
	    this.nScrollBarWidth = 0;
	
	    this.sStartsWith = '';
	    this.oDateFormatter = new dwa.date.dateFormatter;
	    this.oTimeFormatter = new dwa.date.dateFormatter(101);
	    this.oDateTimeFormatter = new dwa.date.dateFormatter(8);
	
	    // to update indicator for sparkle
	    this.sTabId = '';
	    this.nXmlRequest = 0;
	    
	    this.oDataStore = null;
	    
	    dwa.lv.listViewBase.prototype.aComponentsStatic[this.sId] = this;
	},
	aComponentsStatic: {},
	gSortStatic: {
	0 : ""
	,1 : "resortdescending"
	,2 : "resortascending"
	,3 : "resortdescending"
	,4 : "resortascending"
},
	getBaseQuery: function(){
		var oQuery = {
			FolderName : encodeURIComponent(this.sFolderName)
		};
		for(var sName in this.oPresetFields)
			oQuery[sName] = this.oPresetFields[sName];
	    if(this.sHiddenColumns)
	        oQuery.hc = this.sHiddenColumns;
		return oQuery;
	},
	getCellTagName: function(){
	    return this.sCellTagName;
	},
	getRowTagName: function(){
	    return this.sRowTagName;
	},
	getCellFmt: function(){
	    return this.sCellFmt;
	},
	getClassSelected: function(){
	    return this.bNarrowMode ? "vl-row-selected-narrow" : "vl-row-selected";
	},
	setSS: function(fSpan){
	    var asText = [];
	    for (var anWidths = this[!fSpan ? 'anStyleWidths' : 'anStyleWidthsSpan'], i = 0; i < anWidths.length; i++)
	        asText[i] = '.' + this.sColumnClassName + i + (fSpan ? 'span' : '') + ' { width: ' + anWidths[i] + 'px; }';
	
	    var sStyleSheet = !fSpan ? 'oSS' : 'oSSSpan';
	
	 if( dojo.isMozilla || dojo.isWebKit ){
	    var oMainDoc = dojo.doc;
	
	    if (this[sStyleSheet])
	        this[sStyleSheet].parentNode.removeChild(this[sStyleSheet]);
	    else
	        this[sStyleSheet] = oMainDoc.createElement('style');
	
	    for (var i = this[sStyleSheet].childNodes.length - 1; i >= 0; i--)
	        this[sStyleSheet].removeChild(this[sStyleSheet].childNodes[i]);
	
	    this[sStyleSheet].appendChild(oMainDoc.createTextNode(asText.join(' ')));
	    oMainDoc.getElementsByTagName('head')[0].appendChild(this[sStyleSheet]);
	 }else{ // GS
	    (this[sStyleSheet] = this[sStyleSheet] ? this[sStyleSheet] :
	     dojo.doc.createStyleSheet()).cssText = asText.join(' ');
	 } // end - I
	},
	syncCSS: function(aColWidths){
	    var nAvailWidth = this.getListWidth();
	    for(var row=0;row<2;row++) {
	        var aRowInfo = this.getSingleRowInfo(this.getLayoutInfo(), !!row);
	        if(!aRowInfo || !aRowInfo.length)
	            continue;
	
	        var nTotal = nAvailWidth;
	        for(var i=0;i<aRowInfo.length;i++) {
	            var nIndex = aRowInfo[i].nRealIndex;
	            this.anStyleWidths[nIndex] = aColWidths[nIndex];
	            this.anStyleWidthsSpan[nIndex] = Math.max(nTotal, 20);
	            nTotal -= aColWidths[nIndex];
	        }
	    }
	
	    this.setSS();
	    this.setSS(true);
	},
	formatNarrowHeader: function(sLabelHtml, bResorted, bHover){
	    return '<table class="vl-column-table" style="border:0px;" cellspacing=0 cellpadding=0 height=100% align=' + (dojo._isBodyLtr() ? "right" : "left") + '><tr>'
	        + '<td valign=center nowrap>'
	        + sLabelHtml
	        + '</td>'
	        + (!false && bResorted ?
	            '<td>&nbsp;</td>'
	            + '<td valign=center>'
	            + (dojo.hasClass(dojo.body(), 'dijit_a11y') ? // nakakura
	                '<span class="vl-column-resort-outer"' + (this.bUseHCSortIcon ? dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(12, 8), new dwa.common.utils.pos(24, 8), dwa.lv.globals.get().buildResourcesUrl("sorthcicons.gif"), true) : dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(9, 8), new dwa.common.utils.pos(18, 8), dwa.lv.globals.get().buildResourcesUrl("sorticons.gif"), true)) + ' class="vl-column-resort" vspace="0"/></span></td>' :
	                '<img class="vl-column-resort" vspace="0" ' + (this.bUseHCSortIcon ? dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(12, 8), new dwa.common.utils.pos(24, 8), dwa.lv.globals.get().buildResourcesUrl("sorthcicons.gif"), true, "visibility:inherit") : dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(9, 8), new dwa.common.utils.pos(18, 8), dwa.lv.globals.get().buildResourcesUrl("sorticons.gif"), true, "visibility:inherit")) + '/></td>') :
	            '')
	        + '<td>&nbsp;</td>'
	        + '</tr></table>';
	},
	formatColumnHeader: function(sLabelHtml, bResortable){
	    return '<table class="vl-column-table" style="border:0px;" cellspacing=0 cellpadding=0 height=100%><tr>'
	        + '<td valign=center nowrap>'
	        + sLabelHtml
	        + '</td>'
	        + (!false && bResortable ?
	            '<td>&nbsp;</td>'
	            + '<td valign=center>' // nakakura
				+ (dojo.hasClass(dojo.body(), 'dijit_a11y') ?
	                '<span class="vl-column-resort-outer"><img class="vl-column-resort" style="display:none" sortarrow="1" alt="sort"/></span>' :
				    '<img class="vl-column-resort" style="display:none" sortarrow="1" alt="sort"/>')
				+ '</td>' :
	            '')
	        + '</tr></table>';
	},
	hasResponseColumn: function(){
	    for(var i=0;i<this.nColumnSpan;i++)
	        if( this.aColInfo[i].bIsResponse)
	            return true;
	    return false;
	},
	hasResortableColumn: function(){
	    for(var i=0;i<this.nColumnSpan;i++)
	        if( this.aColInfo[i].bResortable)
	            return true;
	    return false;
	},
	getLayoutInfo: function(){
	    if(!this.aLayoutInfo)
	        this.aLayoutInfo = this.generateLayoutInfo(this.aColInfo, this.nColumnSpan, this.bNarrowMode);
	    if( ( (this.bSupportScreenReader && !this.isCategorizedView()) || this.bVcursor )  && !this.aLayoutInfoForScreenReader) // nakakura
	        this.aLayoutInfoForScreenReader = (this.bNarrowMode ? this.generateLayoutInfo(this.aColInfo, this.nColumnSpan, false) : this.aLayoutInfo);
	    return this.aLayoutInfo;
	},
	scrollHintAllowed: function(){
	    return this.bAllowScrollHint && !false && !this.isCategorizedView() && !this.isQuickSearchMode() && !this.oPresetFields.UnreadOnly && !this.oPresetFields.SearchString;
	},
	requireScrollHint: function(){
	    return this.scrollHintAllowed() && (!this.aScrollHints || !(dojo.isIE ? (this.aScrollHints.length) : (dojo.isMozilla ? (this.aScrollHints.snapshotLength) : (this.aScrollHints.snapshotLength) ) ));
	},
	getMinColumnWidth: function(nCol){
	    return (this.aColInfo[nCol].bIsThin && !this.bNarrowMode) ?
	        6 :
	        (this.aColInfo[nCol].bUnhideWhenWrapped && !this.bNarrowMode) ?
	            0 :
	            20;
	},
	getFixedColumnWidth: function(nCol){
	    return (this.aColInfo[nCol].bIsThin && !this.bNarrowMode) ?
	        6 :
	        (this.aColInfo[nCol].bUnhideWhenWrapped && !this.bNarrowMode) ?
	            0 :
	            void 0;
	},
	getMinColumnWidth: function(nCol){
	    return (this.aColInfo[nCol].bIsThin && !this.bNarrowMode) ?
	        6 :
	        (this.aColInfo[nCol].bUnhideWhenWrapped && !this.bNarrowMode) ?
	            0 :
	            20;
	},
	getFixedColumnWidth: function(nCol){
	    return (this.aColInfo[nCol].bIsThin && !this.bNarrowMode) ?
	        6 :
	        (this.aColInfo[nCol].bUnhideWhenWrapped && !this.bNarrowMode) ?
	            0 :
	            void 0;
	},
	getSametimeColumn: function(aCols){
	    if( !aCols.sIMColName ){
	        for( var i=0,iMax=aCols.length; i<iMax; i++ ){
	            if( aCols[i].sIMColName ){
	                aCols.sIMColName=aCols[i].sIMColName;
	                break;
	            }
	        }
	    }
	    return aCols.sIMColName;
	},
	getIMLink: function(oViewEntry, sIMColName){
	    return buildIMLink(this.getText(oViewEntry, sIMColName)) + '&nbsp;';
	},
	getHtmlInIconCell: function(sImgSrc, sWidthClass, bHasIMColInRow){
	 if( dojo.isMozilla ){
	    return this.getHtmlInTextCell('<table cellspacing=0 cellpadding=0 style="font-family:inherit;font-size:inherit;font-weight:inherit;border:0px"><tr>'
	         + '<td>' + sImgSrc + '</td><td>&nbsp;</td>'
	         + (bHasIMColInRow ? '<td><img src="' + dwa.lv.globals.get().buildResourcesUrl('transparent.gif') + '" width="13" height="13" style="visibility:hidden;border:0px;"/>&nbsp;</td>' : '')
	         + '</tr></table>', sWidthClass, false);
	 }else if( dojo.isWebKit ){ // G
	    return '<table cellspacing=0 cellpadding=0 style="font-family:inherit;font-size:inherit;font-weight:inherit;border:0px"><tr>'
	     + '<td>' + sImgSrc + '</td><td>&nbsp;</td>'
	     + (bHasIMColInRow ? '<td><img src="' + dwa.lv.globals.get().buildResourcesUrl('transparent.gif') + '" width="13" height="13" style="visibility:hidden;border:0px"/>&nbsp;</td>' : '')
	     + '</tr></table>';
	 }else{ // S
	    return sImgSrc;
	 } // end - IG
	},
	formatRow: function(sUnid,oTreeViewEntry,oXmlEntry,sCellClass,sWidthClass,cellTag,oListView,aCols,iStart,a1,n,aTitle){
	    
	
	    var v$ = this;
	    // row data
	    var bEmptyEntry = !oTreeViewEntry;
	    if (!oXmlEntry && !bEmptyEntry) oXmlEntry = oTreeViewEntry.getViewEntry();
	
	    // "total" row
	    var bTotal = sUnid || bEmptyEntry ? 0 : v$.oDataStore.getAttributeBoolean(oXmlEntry, "categorytotal");
	
	    // keep in mind that the number of columns of data may be different
	    //  than actual number of columns in view.
	    //  twistie's for example don't have a corresponding column
	    //  also, categories appear by themselves on a single row
	
	    // loop thru each column
	    var bTwistieShown = (v$.bShowTwistieButton == false) ? true : false;
	    var bExitLoop = false;
	    var iEnd = aCols.length;
	    var iLastExist = iStart;
	    var bHasSpecialResponseColumn = false;
	    var bConsolidate = !dwa.lv.listViewBase.prototype.LV$Static.isPrintMode;
	
	    // oDataItem shall be the node corresponding to the specified column number
	    var oDataItem, aDataItems=[], sName, sClass;
	
	    
	    for (var i=iStart; i < iEnd; i++){
	        aDataItems[i] = (!bEmptyEntry && !this.needHidden(v$.oDataStore, oXmlEntry, aCols[i], i>0 ? aCols[i-1] : null)) ? v$.oDataStore.getEntryDataByName(oXmlEntry, aCols[i].sName) : null;
	
	        // set flag if previous column is response
	        if (!aDataItems[i] && !aCols[i].bResponse && i>0 && aCols[i-1] && aCols[i-1].bResponse)
	            bHasSpecialResponseColumn=true;
	
	        if (aDataItems[i] || bEmptyEntry) iLastExist = i;
	    }
	    
	    
	
	    if(iLastExist+1 == iEnd)
	        iLastExist = iEnd;
	
	    var bFirstRowHasIMCol = false, bSecondRowHasIMCol = false;
	    var aRowInfo = this.getSingleRowInfo(v$.aLayoutInfo, false);
	    for(var i=0;i<aRowInfo.length;i++) {
	        var oColInfo = v$.aColInfo[aRowInfo[i].nRealIndex];
	        if(oColInfo.sIMColName)
	            bFirstRowHasIMCol = true;
	    }
	    if(v$.bNarrowMode) {
	        var aRowInfo = this.getSingleRowInfo(v$.aLayoutInfo, true);
	        for(var i=0;i<aRowInfo.length;i++) {
	            var oColInfo = v$.aColInfo[aRowInfo[i].nRealIndex];
	            if(oColInfo.sIMColName)
	                bSecondRowHasIMCol = true;
	        }
	    }
	
	    var bIsSecondRow = false;
	    for (var ix=iStart; ix < v$.aLayoutInfo.length && !bExitLoop; ix++){
	        var oLayoutInfo = v$.aLayoutInfo[ix];
	        var i = oLayoutInfo.nRealIndex;
	        oDataItem = aDataItems[i];
	
	        // skip blank response column to show next column
	        if(aCols[i].bResponse && !oDataItem)
	            continue;
	
	        // in Notebook view, the "Modified" column is chopped off because of this "bHasResponseData" and "bExitLoop"
	        var bHasResponseData = bHasSpecialResponseColumn && (i>0 && oDataItem && aCols[i-1].bResponse);
	
	        var bIsLastData = i==iLastExist || bHasResponseData;
	        var oArgs = {
	            sInner : ''
	            ,sHtml : ''
	            ,sSTInner : ''
	            ,sTitle : ''
	            ,sExtAttr : ' iscell="1" i_col="' + aCols[i].nXmlCol + '"'
	            ,sClass : ''
	            ,sWidthClass : sWidthClass + oLayoutInfo.nRealIndex + (bIsLastData ? 'span' : '')
	            ,"v$" : v$
	            ,oColInfo : aCols[i]
	            ,sUnid : sUnid
	            ,oDataItem : oDataItem
	            ,oXmlEntry : oXmlEntry
	            ,bConsolidate : bConsolidate
	            ,bHasIMColInRow : (bIsSecondRow ? bSecondRowHasIMCol : bFirstRowHasIMCol)
	            ,bEmptyEntry : bEmptyEntry
	            ,oTreeViewEntry : oTreeViewEntry
	        };
	        oArgs.sClass = sCellClass + (this.isShowGrayColumn(aCols[i], oLayoutInfo) ? ' ' + sCellClass + '-gray' : '') + " " + oArgs.sWidthClass;
	        
	        if(this.isCategory(oArgs))
	            oArgs.sClass += ' vl-font-c';
	
	        if(bIsLastData) bExitLoop = true;
	
	        if(oArgs.oDataItem)
	            oArgs.sHtml += this.getIndentHtml(oArgs);
	
	        if(!bTwistieShown && aCols[i].bTwistie && oArgs.oDataItem /* to not show extra twisty icon */&& !oArgs.bEmptyEntry && oTreeViewEntry.hasChildren()) {
	            oArgs.sHtml += this.getTwistieHtml(oTreeViewEntry, dwa.lv.globals.get().oSettings.bUseLiteList);
	            bTwistieShown = true;
	        }
	
	        if(bHasResponseData)
	        {
	            oArgs.sExtAttr += ' expand="1"';
	            bExitLoop = true;
	        }
	
	        
	        if(oArgs.bHasIMColInRow)
	            oArgs.sExtAttr += ' hasimcolinrow="1"';
	
	        oArgs.sExtAttr += ' widthclass="' + oArgs.sWidthClass + '"';
	
	        oArgs = this.generateCellHTML(oArgs);
	
	        a1[n++]= dwa.lv.listViewBase.prototype.LV$Static.getStartTag(cellTag);
	        a1[n++]= ' class="';
	        a1[n++]= oArgs.sClass;
	        a1[n++]= '"' + oArgs.sExtAttr + '>';
	        if(!oLayoutInfo.bSpacer)
	            a1[n++]= oArgs.sSTInner + oArgs.sInner;
	        a1[n++]= dwa.lv.listViewBase.prototype.LV$Static.getEndTag();
	
	        if(oLayoutInfo.bWrapAfter) {
	            a1[n++]= '<br>';
	            bIsSecondRow = true;
	        }
	        
	        if(!v$.bNarrowMode && v$.bSupportScreenReader)
	            aTitle[i] = oArgs.sTitle || '';
	    }
	    
	    // nakakura
	    if(v$.bNarrowMode && v$.bSupportScreenReader) {
	        for (var ix=iStart; ix < v$.aLayoutInfoForScreenReader.length && !bExitLoop; ix++){
	            var oLayoutInfo = v$.aLayoutInfoForScreenReader[ix];
	            var i = oLayoutInfo.nRealIndex;
	            oDataItem = aDataItems[i];
	            
	            // skip blank response column to show next column
	            if(aCols[i].bResponse && !oDataItem)
	                continue;
	            
	            // in Notebook view, the "Modified" column is chopped off because of this "bHasResponseData" and "bExitLoop"
	            var bHasResponseData = bHasSpecialResponseColumn && (i>0 && oDataItem && aCols[i-1].bResponse);
	            
	            var bIsLastData = i==iLastExist || bHasResponseData;
	            var oArgs = {
	                sTitle : ''
	                ,"v$" : v$
	                ,oColInfo : aCols[i]
	                ,oDataItem : oDataItem
	                ,oXmlEntry : oXmlEntry
	            };
	            
	            if(bIsLastData || bHasResponseData) 
	                bExitLoop = true;
	            
	            aTitle[i] = this.generateCellTitleForScreenReader(oArgs) || '';;
	        }
	    }
	
	    // Add the row to the VList table
	    return n;
	},
	needHidden:function(store, entry, colinfo, prevcolinfo){
		return false;
	},
	isCategory: function(oArgs){
		return (oArgs.oDataItem && (oArgs.v$.oDataStore.getAttribute(oArgs.oDataItem, 'category')=='true'));
	},
	generateCellHTML: function(oArgs){
	    // You can get column's type and value with getValue().
	    var sEmptyIconHtml = '<img src="' + dwa.lv.globals.get().buildResourcesUrl('transparent.gif') + '"'
	     + ' width="' + 13 + '" height="' + 11 + '" style="visibility:hidden;border:0px" alt="">';
	    var sTumbler = oArgs.v$.bShowTumbler && oArgs.oXmlEntry && oArgs.v$.oDataStore.getAttributeString(oArgs.oXmlEntry, 'position') ? (oArgs.v$.oDataStore.getAttributeString(oArgs.oXmlEntry, 'position') + ':') : '';
	    var sType = oArgs.v$.oDataStore.getType(oArgs.oDataItem);

	    var asHtml = [];
	    var asTitles = [];
	    var fUseIconCell;
	    var fReverseCell;
	
	    var vValue = oArgs.v$.oDataStore.getValue(oArgs.oDataItem);
	    var avValue = vValue instanceof Array ? vValue : [vValue];
	    var vFirstValue = avValue[0];
	
	    switch (sType) {
	    case 'datetimelist':
	    case 'datetime':
	        var aoCalendar = [], aoCalendarTitle = [];
	        for(var i=0;i<avValue.length;i++) {
	            vValue = avValue[i];

		    if( vValue instanceof Array ){
		        var aoCalendar2 = [], aoCalendarTitle2 = [];

			for( var j = 0; j < vValue.length; j++ ){

		              var oCalendar = vValue[j] instanceof dwa.date.calendar ?
		                vValue[j] : (new dwa.date.calendar).setDate(vValue[j], oArgs.v$.oZoneInfo);
	
		              if (oArgs.oColInfo.bTimeOnly) {
		                aoCalendar2.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
		                aoCalendarTitle2.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
		              } else {
		                var oDateFormatter = new dwa.date.dateFormatter(oArgs.oColInfo.nDTFmt || 0);
		                // convert to 2year to show datetime completely in date column width.
		                oDateFormatter.sFormat = oDateFormatter.sFormat.replace(/-yyyy/, '-yy').replace(/yyyy-/, 'yy-').replace(/yyyy/, 'yy');
		                oCalendar.sync();
		                aoCalendar2.push(oDateFormatter.format(oCalendar));
	
		                var oTitleDateFormatter = new dwa.date.dateFormatter(5);
		                aoCalendarTitle2.push(oTitleDateFormatter.format(oCalendar));
			      }
			}
			aoCalendar.push( aoCalendar2.join('/') );
			aoCalendarTitle.push( aoCalendarTitle2.join('/') );
		    }else{

	              var oCalendar = vValue instanceof dwa.date.calendar ?
	                vValue : (new dwa.date.calendar).setDate(vValue, oArgs.v$.oZoneInfo);
	
	              if (oArgs.oColInfo.bTimeOnly) {
	                aoCalendar.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
	                aoCalendarTitle.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
	              } else {
	                var oDateFormatter = new dwa.date.dateFormatter(oArgs.oColInfo.nDTFmt || 0);
	                // convert to 2year to show datetime completely in date column width.
	                oDateFormatter.sFormat = oDateFormatter.sFormat.replace(/-yyyy/, '-yy').replace(/yyyy-/, 'yy-').replace(/yyyy/, 'yy');
	                oCalendar.sync();
	                aoCalendar.push(oDateFormatter.format(oCalendar));
	
	                var oTitleDateFormatter = new dwa.date.dateFormatter(5);
	                aoCalendarTitle.push(oTitleDateFormatter.format(oCalendar));
		      }
		    }
	        }
	        
	        asHtml.push(sTumbler + aoCalendar.join(this.sTextlistSeparator));
	        asTitles.push(aoCalendarTitle.join(this.sTextlistSeparator));
	
	        break;
	    case 'number':
	    case 'numberlist':
	        fUseIconCell = oArgs.oColInfo.bIsIcon;
	        oArgs.sClass += !oArgs.oColInfo.bIsIcon ? ' s-number' : '';
	
	        if (oArgs.oColInfo.bIsIcon) {
	            var fUnreadIcon;
	
	            for (var i = avValue.length - 1; i >= 0; i--)
	                avValue.splice(i, avValue[i] <= 0 || avValue[i] >= 999 ? 1 : 0);
	
	            if (avValue.length == 0)
	                avValue = [vFirstValue];
	
	            for (var i = 0; i < avValue.length; i++) {
	                var nValue = avValue[i];
	                if (nValue > 0 && nValue < 999) {
	                    if (dwa.common.utils.indexOf([129, 130, 188, 211], nValue) >= 0) {
	                        nValue = (oArgs.v$.oDataStore.getAttributeBoolean(oArgs.oXmlEntry, 'unread') ? '188' : '211') - 0;
	                        fUnreadIcon = true;
	                    }
	                    asHtml.push(dwa.lv.benriFuncs.generateIconsImgURLString('' + nValue, oArgs.bConsolidate));
	                    asTitles.push(dwa.lv.benriFuncs.generateIconsImgTitleString(nValue));
	                } else {
	                    asHtml.push(sEmptyIconHtml);
	                }
	            }
	
	            if (fUnreadIcon)
	                oArgs.sExtAttr += ' isreadicon="1"';
	        } else if (oArgs.oColInfo.bBytes) {
	            var oUnit = {k: 1024, m: 1048576, g: 1073741824, t: 1099511627776};
	            var oFormat = {k: this._cmsgs[ "D_KILOBYTES" ], m: this._cmsgs[ "D_MEGABYTES" ], g: this._cmsgs[ "D_GIGABYTES" ], t: this._cmsgs[ "D_TERABYTES" ]};
	            var oTitleFormat = {k: this._smsgs[ "L_JAWS_KILOBYTES" ], m: this._smsgs[ "L_JAWS_MEGABYTES" ], g: this._smsgs[ "L_JAWS_GIGABYTES" ], t: this._smsgs[ "L_JAWS_TERABYTES" ]};
	            var sThousandsSep = dwa.lv.globals.get().oSettings.sThousandsSep || this._cmsgs[ "D_NUMFMT_THOUSANDSSEPARATOR" ];
	            var sDecimalSymbol = dwa.lv.globals.get().oSettings.sDecimalSymbol || this._cmsgs[ "D_NUMFMT_DECIMALSYMBOL" ];
	
	            var aoNumber = [], aoNumberTitle = [];
	            for(var i=0;i<avValue.length;i++) {
	                vValue = avValue[i];
	                vValue = vValue < oUnit.k ? oUnit.k : vValue;
	                var sUnit = vValue < oUnit.m ? 'k' : vValue < oUnit.g ? 'm' : vValue < oUnit.t ? 'g' : 't';
	                var asParts = (vValue / oUnit[sUnit]).toFixed(sUnit != 'k' ? 1 : 0).split('.');
	                var nMod = asParts[0].length % 3;
	                var asMatch = asParts[0].substr(nMod).match(/\d{3}/g);
	                asParts[0] = asParts[0].substr(0, nMod)
	                 + (asParts[0].length >= 3 && nMod ? ',' : '') + (asMatch ? asMatch.join(sThousandsSep) : '');
	
	                aoNumber.push(dwa.common.utils.formatMessage(oFormat[sUnit], asParts.join(sDecimalSymbol)));
	                aoNumberTitle.push(dwa.common.utils.formatMessage(oTitleFormat[sUnit], asParts.join(sDecimalSymbol)));
	            }
	
		    fReverseCell = true;
	            asHtml.push(sTumbler + aoNumber.join(this.sTextlistSeparator));
	            asTitles.push(aoNumberTitle.join(this.sTextlistSeparator));
	        } else {
	            var aoNumber = [], aoNumberTitle = [];
	            for(var i=0;i<avValue.length;i++) {
	                vValue = avValue[i];
	                aoNumber.push(this.formatNumber(vValue, oArgs.oColInfo.nDigits));
	                aoNumberTitle.push(this.formatNumber(vValue, oArgs.oColInfo.nDigits));
	            }
		    fReverseCell = true;
	            asHtml.push(sTumbler + aoNumber.join(this.sTextlistSeparator));
	            asTitles.push(aoNumberTitle.join(this.sTextlistSeparator));
	        }
	
	        break;
	    default:
	        // Create sametime awareness link
	        if (oArgs.oColInfo.sIMColName)
	            oArgs.sSTInner = this.getIMLink(oArgs.oXmlEntry, oArgs.oColInfo.sIMColName);
	
	        var sText = this.formatText(vValue);
	        if (sText && oArgs.oColInfo.bIsIcon && oArgs.sUnid){
	            // get icon image (either full html img tag or textual name of image in current db design--mail file)
	            // use same icon width and height we are using for icons in dwa.lv.benriFuncs.generateIconsImgURLString
	            if ((/^<img/i).test(sText)) {
	                fUseIconCell = true;
	                asHtml.push(sText);
	            } else {
	                // SPR DGUY7LYJN3 - don't use GIFs within mail file, use those in Forms file (Rule_On.gif, Rule_Off.gif)
	                var sSrc = (/^rule/i).test(sText) ? dwa.lv.globals.get().buildResourcesUrl(sText) : ("../../" + sText);
	                fUseIconCell = true;
	                asHtml.push('<img src="' + sSrc + '" style="border:0px" width="' + 13 + '" height="' + 11 + '">');
	            }
	        } else if (oArgs.bEmptyEntry && oArgs.oColInfo.bIsIcon) {
	            fUseIconCell = true;
	            asHtml.push(sEmptyIconHtml);
	        } else {
	            if (oArgs.v$.fnDisplayName)
	                sText = oArgs.v$.fnDisplayName(sText, oArgs.oDataItem, oArgs.oTreeViewEntry);
	            asHtml.push(sTumbler + (sText ? dwa.lv.benriFuncs.escapeHtmlKeywords(sText, 0) : '&nbsp;'));
	            asTitles.push(sText ? sText : '');
	            if (sText && oArgs.v$.bShowTooltipForText)
	                oArgs.sExtAttr += ' title="' + dwa.lv.benriFuncs.escapeHtmlKeywords(sText, 0) + '"';
	        }
	
	        break;
	    }

	
	    oArgs.sInner = fUseIconCell ? this.getHtmlInIconCell(oArgs.sHtml + asHtml.join(''), oArgs.sWidthClass, oArgs.bHasIMColInRow) :
	        this.getHtmlInTextCell(oArgs.sHtml + asHtml.join(''), oArgs.sWidthClass, fReverseCell);
	    oArgs.sTitle = asTitles.join(' ');
	
	    return oArgs;
	},
	// nakakura
	generateCellTitleForScreenReader: function(oArgs){
	    var sType = oArgs.v$.oDataStore.getType(oArgs.oDataItem);
	    var asTitles = [];
	    var vValue = oArgs.v$.oDataStore.getValue(oArgs.oDataItem);
	    var avValue = vValue instanceof Array ? vValue : [vValue];
	    var vFirstValue = avValue[0];
	    
	    switch (sType) {
	        case 'datetimelist':
	        case 'datetime':
	            aoCalendarTitle = [];
	            for(var i=0;i<avValue.length;i++) {
	                vValue = avValue[i];
	                
	                if(vValue instanceof Array){
	                    aoCalendarTitle2 = [];
	                    
	                    for(var j = 0; j < vValue.length; j++ ){
	                        var oCalendar = vValue[j] instanceof dwa.date.calendar ?
	                            vValue[j] : (new dwa.date.calendar).setDate(vValue[j], oArgs.v$.oZoneInfo);
	                        
	                        if (oArgs.oColInfo.bTimeOnly) {
	                            aoCalendarTitle2.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
	                        } else {
	                            var oTitleDateFormatter = new dwa.date.dateFormatter(5);
	                            aoCalendarTitle2.push(oTitleDateFormatter.format(oCalendar));
	                        }
	                    }
	                    aoCalendarTitle.push( aoCalendarTitle2.join('/') );
	                } else {
	                    var oCalendar = vValue instanceof dwa.date.calendar ?
	                        vValue : (new dwa.date.calendar).setDate(vValue, oArgs.v$.oZoneInfo);
	                    
	                    if (oArgs.oColInfo.bTimeOnly) {
	                        aoCalendarTitle.push(oArgs.v$.oTimeFormatter.formatTime(oCalendar));
	                    } else {
	                        var oTitleDateFormatter = new dwa.date.dateFormatter(5);
	                        aoCalendarTitle.push(oTitleDateFormatter.format(oCalendar));
	                    }
	                }
	            }
	            asTitles.push(aoCalendarTitle.join(this.sTextlistSeparator));
	            break;
	        case 'number':
	        case 'numberlist':
	            if (oArgs.oColInfo.bIsIcon) {
	                var fUnreadIcon;
	                
	                for (var i = avValue.length - 1; i >= 0; i--)
	                    avValue.splice(i, avValue[i] <= 0 || avValue[i] >= 999 ? 1 : 0);
	                
	                if (avValue.length == 0)
	                    avValue = [vFirstValue];
	                
	                for (var i = 0; i < avValue.length; i++) {
	                    var nValue = avValue[i];
	                    if (nValue > 0 && nValue < 999) {
	                        if (dwa.common.utils.indexOf([129, 130, 188, 211], nValue) >= 0) {
	                            nValue = (oArgs.v$.oDataStore.getAttributeBoolean(oArgs.oXmlEntry, 'unread') ? '188' : '211') - 0;
	                            fUnreadIcon = true;
	                        }
	                        asTitles.push(dwa.lv.benriFuncs.generateIconsImgTitleString(nValue));
	                    }
	                }
	            } else if (oArgs.oColInfo.bBytes) {
	                var oUnit = {k: 1024, m: 1048576, g: 1073741824, t: 1099511627776};
	                var oTitleFormat = {k: this._smsgs[ "L_JAWS_KILOBYTES" ], m: this._smsgs[ "L_JAWS_MEGABYTES" ], g: this._smsgs[ "L_JAWS_GIGABYTES" ], t: this._smsgs[ "L_JAWS_TERABYTES" ]};
	                var sThousandsSep = dwa.lv.globals.get().oSettings.sThousandsSep || this._cmsgs[ "D_NUMFMT_THOUSANDSSEPARATOR" ];
	                var sDecimalSymbol = dwa.lv.globals.get().oSettings.sDecimalSymbol || this._cmsgs[ "D_NUMFMT_DECIMALSYMBOL" ];
	                var aoNumber = [], aoNumberTitle = [];
	                for(var i=0;i<avValue.length;i++) {
	                    vValue = avValue[i];
	                    vValue = vValue < oUnit.k ? oUnit.k : vValue;
	                    var sUnit = vValue < oUnit.m ? 'k' : vValue < oUnit.g ? 'm' : vValue < oUnit.t ? 'g' : 't';
	                    var asParts = (vValue / oUnit[sUnit]).toFixed(sUnit != 'k' ? 1 : 0).split('.');
	                    var nMod = asParts[0].length % 3;
	                    var asMatch = asParts[0].substr(nMod).match(/\d{3}/g);
	                    asParts[0] = asParts[0].substr(0, nMod)
	                     + (asParts[0].length >= 3 && nMod ? ',' : '') + (asMatch ? asMatch.join(sThousandsSep) : '');
	                    
	                    aoNumberTitle.push(dwa.common.utils.formatMessage(oTitleFormat[sUnit], asParts.join(sDecimalSymbol)));
	                }
	                asTitles.push(aoNumberTitle.join(this.sTextlistSeparator));
	            } else {
	                var aoNumberTitle = [];
	                for(var i=0;i<avValue.length;i++) {
	                    vValue = avValue[i];
	                    aoNumberTitle.push(this.formatNumber(vValue, oArgs.oColInfo.nDigits));
	                }
	                asTitles.push(aoNumberTitle.join(this.sTextlistSeparator));
	            }
	            break;
	        default:
	            var sText = this.formatText(vValue);
	            if (!sText || !oArgs.oColInfo.bIsIcon || oArgs.sUnid){
	                if (oArgs.v$.fnDisplayName)
	                    sText = oArgs.v$.fnDisplayName(sText, oArgs.oDataItem, oArgs.oTreeViewEntry);
	                asTitles.push(sText ? sText : '');
	            }
	    }
	    return asTitles.join(' ');
	},
	formatText: function(vValue){
		if( vValue == null ) return '';

		switch (typeof(vValue)) {
		case 'object':
			if (vValue instanceof Array) {
				var asList = [];

				for (var i = 0; i < vValue.length; i++) {
					if (vValue[i] instanceof Array && vValue[i].length == 2
						&& (vValue[i][0] instanceof Date || vValue[i][0] instanceof dwa.date.calendar)
						&& (vValue[i][1] instanceof Date || vValue[i][1] instanceof dwa.date.calendar))
						var aoValues = vValue[i];
					else
						var aoValues = [vValue[i]];

					var asPair = [];

					for (var j = 0; j < aoValues.length; j++) {
						asPair[j] = this.formatText(aoValues[j]);
					}

					asList[i] = asPair.join('/');
				}

				return asList.join(this.sTextlistSeparator);
			} else if (vValue instanceof Date || vValue instanceof dwa.date.calendar) {
				var oCalendar = vValue instanceof dwa.date.calendar ?
					vValue : (new dwa.date.calendar).setDate(vValue, this.oZoneInfo);
				var oFormatter
					= oCalendar.fDateOnly ? this.oDateFormatter : oCalendar.fTimeOnly ? this.oTimeFormatter : this.oDateTimeFormatter;
				return oFormatter.format(oCalendar);
			}

			return '' + vValue;
		case 'undefined':
			return '';
		}

		return '' + vValue;
	},
	getHtmlInTextCell: function(sText, sWidthClass, bIsReverse){
	 if( dojo.isMozilla ){
	    if(((!!dojo._isBodyLtr() && bIsReverse) || (!dojo._isBodyLtr() && !bIsReverse)) && dwa.lv.globals.get().nIntBrowserVer >= 1.09) {
	        // FireFox3 right align cell
	        return '<div' + (sWidthClass ? ' class="' + sWidthClass + '"' : '') + ' style="text-align:right;">' + sText + '</div>';
	    } else
	    if(((!!dojo._isBodyLtr() && !bIsReverse) || (!dojo._isBodyLtr() && bIsReverse)) && dwa.lv.globals.get().nIntBrowserVer < 1.09) {
	        // FireFox2 left align cell
	        return '<div' + (sWidthClass ? ' class="' + sWidthClass + '"' : '') + ' style="text-align:left;">' + sText + '</div>';
	    } else {
	        return sText;
	    }
	 }else{ // G
	    return sText;
	 } // end - IS
	},
	changeIconToRead: function(oColInfo, oCell){
	    if(!oColInfo.bIsIcon || oCell.getAttribute('isreadicon')!='1')
	        return;
	    
	 if( dojo.isMozilla ){
	    // CCDL7DTBZM : stop to mark as read again.
	    if(oCell.getAttribute('markedasread') == '1')
	        return;
	    oCell.setAttribute('markedasread', '1');
	//    oCell.style.height = '100%';
	 } // end - G
	    
	    oCell.innerHTML = this.getHtmlInIconCell(dwa.lv.benriFuncs.generateIconsImgURLString('211', true), oCell.getAttribute('widthclass'), oCell.getAttribute('hasimcolinrow')=='1');
	},
	changeIconToUnread: function(oColInfo, oCell){
	    if(!oColInfo.bIsIcon || oCell.getAttribute('isreadicon')!='1')
	        return;
	    
	 if( dojo.isMozilla ){
	    // CCDL7DTBZM : stop to mark as unread again.
	    if(oCell.getAttribute('markedasread') == '0')
	        return;
	    oCell.setAttribute('markedasread', '0');
	//    oCell.style.height = '100%';
	 } // end - G
	    
	    oCell.innerHTML = this.getHtmlInIconCell(dwa.lv.benriFuncs.generateIconsImgURLString('188', true), oCell.getAttribute('widthclass'), oCell.getAttribute('hasimcolinrow')=='1');
	},
	changeTrashIconToShown: function( oCell ){
	    if(oCell.id.indexOf("ct")!=0 || oCell.getAttribute('istrash')=='1' || oCell.className.indexOf(" s-trash")!=-1)
	        return;
	    
	    oCell.className += " s-trash";
	    oCell.setAttribute('istrash', '1');
	},
	changeTrashIconToHidden: function( oCell ){
	    if(oCell.id.indexOf("ct")!=0 || oCell.getAttribute('istrash')!='1' || oCell.className.indexOf(" s-trash")==-1)
	        return;
	    
	    oCell.className = oCell.className.replace(" s-trash", '');
	    oCell.setAttribute('istrash', '');
	},
	isMarkedRead: function( row ){
	    var v$ = this;
	    if(v$.bIsLiteList) {
	        return !row.bUnread;
	    } else {
	        var t$=v$.getTreeViewEntry(row);
	        if( !t$ || !t$.getUnid() )return false;
	        var x$=t$.getViewEntry();
	        if( x$ )
	            return this.oDataStore.getAttribute(x$, "unread") != "true";
	    }
	    return false;
	},
	showTrashIcon: function( row, bShow ){
	    var v$ = this;
	    if(v$.bIsLiteList) {
	        // LiteList will refresh view
	    } else {
	        var t$=v4.getTreeViewEntry(row);
	        if( !t$ || !t$.getUnid() )return;
	        var x$=t$.getViewEntry();
	        if( x$ ){
	            x$.setAttribute("markedfordel",(bShow?"true":"false"));
	        }
	    }
	
	    this.enumlateCellsInRow(row, bShow ? this.changeTrashIconToShown : this.changeTrashIconToHidden);
	},
	setSelected: function(oColInfo, oCell){
	    if(oCell.className.indexOf('vl-font-c')==-1)
	        return;
	    oCell.className += ' vl-cell-selected';
	},
	setUnselected: function(oColInfo, oCell){
	    if(oCell.className.indexOf('vl-cell-selected')==-1)
	        return;
	    oCell.className = oCell.className.replace(' vl-cell-selected', '');
	},
	formatNumber: function( p1, p2 ){
	    // p1                   :number to format
	    // p2                   :desired decimal digits
	    // dwa.lv.globals.get().oSettings.sDecimalSep    :desired decimal character
	
	    // don't format the number if the decimal point digits vary
	    if( p2<0  ){ return p1; }
	    
	    // return quickly if zero decimal digits are required
	    if( p2==0 ){ return Math.round(p1); }
	    
	    // Round off the number by moving the decimal point into the last "integer" slot
	    //  then move the number back
	    var p0 = Math.pow(10,p2);
	    var s1 = ((Math.round(p1 * p0))/p0).toString();
	    
	    // replace the decimal separator with the desired one
	    var p3 = dwa.lv.globals.get().oSettings.sDecimalSep;
	    if( s1.indexOf('.')>=0 ){
	        return s1.replace(/\./, p3);
	    }else{
	        // and provide a fixed number of digits
	        var p4 = "0";
	        while(p4.length < p2) p4+="0";
	        return s1 + p3 + p4;
	    }
	},
	getTwistieHtml: function(oTreeViewEntry, bUseLiteView){
	    var a=[], n=0;
	
	    if (bUseLiteView) {
	        a[n++] = '<img';
	        a[n++] = LiteList.isExpanded(oTreeViewEntry) ? dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos(82, 0), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false) : dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos((this._cmsgs[ "D_DIR_DEFAULT" ]=="rtl") ? 51:41, 16), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false);
	        a[n++] = ' onmousedown="' + 'com_ibm_dwa_globals.oScript.LiteList.expandEntryByEvent(event)' + '" twistie="1">';
	    } else {
	        // nakakura
	        if(dojo.hasClass(dojo.body(), 'dijit_a11y')) {
	            a[n++] = '<span';
	            a[n++] = (oTreeViewEntry.isExpanded() ? dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos(82, 0), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false) : dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos((this._cmsgs[ "D_DIR_DEFAULT" ]=="rtl") ? 51:41, 16), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false));
	            a[n++] =  ' style="border:0px" twistie="1" alt="collapse-expand"></span>';
	        } else {
	            a[n++] = '<img';
	            a[n++] = oTreeViewEntry.isExpanded() ? dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos(82, 0), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false) : dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(new dwa.common.utils.pos(10, 9), new dwa.common.utils.pos((this._cmsgs[ "D_DIR_DEFAULT" ]=="rtl") ? 51:41, 16), dwa.lv.globals.get().buildResourcesUrl("sceneicons.gif"), false);
	            a[n++] = ' onmousedown="' + 'dwa.lv.virtualList.prototype.expandEntryByEventStatic(event)' + '" twistie="1" alt="collapse-expand">';
	        }
	    }
	
	    return a.join('');
	},
	getIndent: function(oArgs){
		return parseInt( this.oDataStore.getAttribute(oArgs.oDataItem, 'indent'),10 );
	},
	getIndentHtml: function(oArgs){
	    var a=[],n=0;
		var jMax = this.getIndent(oArgs);
	    if(typeof jMax != 'number') jMax=0;
	
	    for(var j=0;j<jMax;j++)
	        a[n++] = '&nbsp;&nbsp;&nbsp;&nbsp;';
	
	    return a.join('');
	},
	getText: function(p1     ,p2     ){
	    sRet = '';
	    if(p1&&p2){
	        var o=this.oDataStore.getEntryDataByName(p1, p2);
	        if(o)sRet=this.oDataStore.getText(o);
	    }
	    return sRet;
	},
	getSingleRowInfo: function(aLayoutInfo, bWrapRow){
	    var aRow = [];
	    for(var i=0;i<aLayoutInfo.length;i++)
	        if((!aLayoutInfo[i].bWrap && !bWrapRow) || (aLayoutInfo[i].bWrap && bWrapRow))
	            aRow.push(aLayoutInfo[i]);
	    return aRow;
	},
	getExtendColumn: function( aRowInfo ){
	    for(var i=0;i<aRowInfo.length;i++)
	    {
	        if( aRowInfo[i].bExtend)
	            return i;
	    }
	    return aRowInfo.length - 1;
	},
	generateLayoutInfo: function(aColInfo, nColumnSpan, bNarrow){
	    var aLayoutInfo = [];
	    if(!nColumnSpan)
	        nColumnSpan = aColInfo.length;
	
	    if(!bNarrow) {
	        for(var i=0;i<nColumnSpan;i++) {
	            if(!aColInfo[i].bUnhideWhenWrapped)
	                aLayoutInfo.push({nRealIndex:i,bExtend:aColInfo[i].bIsExtend});
	        }
	        return aLayoutInfo;
	    }
	
	    var aSpacers = [], bFoundWrapUnder = false;
	    var nCol = -1, nWidth = 0, nLastCol = -1;
	
	    for(var i=0;i<nColumnSpan;i++) {
	        if(aColInfo[i].sNarrowDisp == 'top') {
	            nLastCol = aLayoutInfo.length;
	            aLayoutInfo.push({nRealIndex:i});
	            if(aColInfo[i].bWrapUnder && !bFoundWrapUnder) {
	                bFoundWrapUnder = true;
	            }
	            else if (!bFoundWrapUnder) {
	                aSpacers.push({nRealIndex:i,bWrap:true,bSpacer:true});
	            }
	            if(aColInfo[i].nOrgWidth > nWidth) {
	                nCol = nLastCol;
	                nWidth = aColInfo[i].nOrgWidth;
	            }
	        }
	    }
	    if(nCol!=-1) {
	        aLayoutInfo[nCol].bExtend = true;
	    }
	    if(nLastCol!=-1) {
	        aLayoutInfo[nLastCol].bLastCol = true;
	    }
	    
	    var bSpacerAvailable = (bFoundWrapUnder && aSpacers.length);
	    var nWrapCol = -1, aWrapInfo = [], nWidth = 0, nWrapLastCol = -1;
	    for(var i=0;i<nColumnSpan;i++) {
	        if(aColInfo[i].sNarrowDisp == 'wrap') {
	            aWrapInfo.push({nRealIndex:i,bWrap:true,nSeqNum:aColInfo[i].nSeqNum});
	        }
	    }
	    aWrapInfo.sort(this.sortBySequenceNumber);
	    for(var i=0;i<aWrapInfo.length;i++) {
	        nWrapLastCol = i;
	        var oColInfo = aColInfo[aWrapInfo[i].nRealIndex];
	        if(oColInfo.nOrgWidth > nWidth) {
	            nWrapCol = nWrapLastCol;
	            nWidth = oColInfo.nOrgWidth;
	        }
	    }
	    if(nWrapCol!=-1) {
	        aWrapInfo[nWrapCol].bExtend = true;
	    }
	    if(nWrapLastCol!=-1) {
	        aWrapInfo[nWrapLastCol].bLastCol = true;
	    }
	    if(nLastCol!=-1 && nWrapCol!=-1) {
	        aLayoutInfo[nLastCol].bWrapAfter = true;
	        if(bSpacerAvailable)
	            aLayoutInfo = aLayoutInfo.concat(aSpacers);
	        aLayoutInfo = aLayoutInfo.concat(aWrapInfo);
	    }
	    return aLayoutInfo;
	},
	sortBySequenceNumber: function(oSrc, oDst){
	    if(oSrc.nSeqNum < oDst.nSeqNum)
	        return -1;
	    if(oSrc.nSeqNum > oDst.nSeqNum)
	        return 1;
	    if(oSrc.nRealIndex < oDst.nRealIndex)
	        return -1;
	    if(oSrc.nRealIndex > oDst.nRealIndex)
	        return 1;
	    return 0;
	},
	isShowGrayColumn: function(oColInfo, oLayoutInfo){
	    return (oColInfo.bShowGradientColor || (oLayoutInfo && oLayoutInfo.bWrap && oColInfo.bAlignGradientColor));
	},
	getSortGifInfo: function(iType,iSort,bHC){
	    switch (iSort){
	        case dwa.lv.listViewBase.prototype.gSortStatic[2]:
	            return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(24, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos(18, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	        case dwa.lv.listViewBase.prototype.gSortStatic[1]:
	            return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(36, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos(18, 8), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	    }
	    return null;
	},
	getHoverGifInfo: function(iType,iSort,bHC){
	    switch (iType){
	        case 5: // Dual Sort
	            switch (iSort){
	                case dwa.lv.listViewBase.prototype.gSortStatic[2]:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(48, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9,16), oOffset: new dwa.common.utils.pos(27, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	                case dwa.lv.listViewBase.prototype.gSortStatic[1]:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(60, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9,16), oOffset: new dwa.common.utils.pos(36, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	                default:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(12, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9,16), oOffset: new dwa.common.utils.pos( 9, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	            }
	            break;
	        case 2:
	        case 4:
	            switch (iSort){
	                case dwa.lv.listViewBase.prototype.gSortStatic[2]:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(24, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos(18, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	                default:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12, 8), oOffset: new dwa.common.utils.pos( 0, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos( 0, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	            }
	            break;
	        case 1:
	        case 3:
	            switch (iSort){
	                case dwa.lv.listViewBase.prototype.gSortStatic[1]:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12,16), oOffset: new dwa.common.utils.pos(36, 0), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos(18, 8), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	                default:
	                    return (bHC ? {oSize: new dwa.common.utils.pos(12, 8), oOffset: new dwa.common.utils.pos( 0, 8), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorthcicons.gif')} : {oSize: new dwa.common.utils.pos( 9, 8), oOffset: new dwa.common.utils.pos( 0, 8), sUrl: dwa.lv.globals.get().buildResourcesUrl('sorticons.gif')});
	            }
	            break;
	    }
	    return null;
	},
	getSortImage: function( oCol ){
	    var aImgs = oCol.getElementsByTagName('IMG');
	    for(var i=0;i<aImgs.length;i++) {
	        if(!!aImgs[i].getAttribute('sortarrow'))
	            return aImgs[i];
	    }
	    return null;
	},
 	focusElementRow: function(oElem, oFocus, oFocusOuter, bWidth100, bFocusRow){
        this.focusElement(oElem, oFocus, oFocusOuter, bWidth100, bFocusRow);
    },
	focusElement: function(oElem, oFocus, oFocusOuter, bWidth100, bFocusRow){
		;
		var oPos = dwa.lv.benriFuncs.getAbsPos(oElem);
		with(oFocusOuter.style) {
			left = oPos.x + 'px';
			top = oPos.y + 'px';
			width = bWidth100 ? '100%' : oElem.offsetWidth + 'px';
			height = (oElem.offsetHeight - (bFocusRow ? 1 : 0)) + 'px';
			visibility = '';
		}
		// in the case focus fails on the element background
		oFocus.tabIndex = 0;
		;
		try {
			oFocus.focus();
		} catch(e){}
		this.clearBlurTimeout();
		this.setFocusTimeout();
	},
	hasFocusTimeout: function(){
		return !!this.hFocus;
	},
	setFocusTimeout: function(){
		this.clearFocusTimeout();
		var oThis = this;
		this.hFocus = setTimeout(function(){oThis.clearFocusTimeout();}, 1);
	},
	clearFocusTimeout: function(){
		if(this.hFocus)
			clearTimeout(this.hFocus);
		this.hFocus = null;
		this.bHasFocus = true;
	},
	onBlur: function(ev){
		;
		this.setBlurTimeout();
	},
	blurByTimeout: function(){
		this.unfocusColumn();
		this.unfocusRow();
	    this.oKeyTrapAnchor.tabIndex = 0;
		this.hBlur = null;
		this.nFocusMode = 0;
		this.bHasFocus = false;
		;
	},
	setBlurTimeout: function(){
		this.clearBlurTimeout();
		if(this.hasFocusTimeout()) {
			;
			return;
		}
		var oThis = this;
		this.hBlur = setTimeout(function(){oThis.blurByTimeout()}, 1);
	},
	clearBlurTimeout: function(){
		if(this.hBlur)
			clearTimeout(this.hBlur);
		this.hBlur = null;
	},
	onColumnBlur: function(ev){
		;
		if(this.nFocusMode != 1)
			this.unfocusColumn();
		this.setBlurTimeout();
	},
	onRowBlur: function(ev){
		;
		if(this.nFocusMode != 2)
			this.unfocusRow();
		this.setBlurTimeout();
	},
	focusColumn: function(nCol){
		;
		this.unfocusColumn(true);
		this.unfocusRow();
		this.showColumnHover(this.aColHdr[nCol], true);
		this.oColumnFocusOuter.style.zIndex = '';
		this.nFocusMode = 1;
		this.nFocusedColumn = nCol;
	
		if(this.bSupportScreenReader) {
			this.oKeyTrapAnchor.tabIndex = 0;
			this.updateTextForJAWS();
			this.oKeyTrapAnchor.focus();
		}
		this.focusElement(this.bNarrowMode ? this.oNarrowHdr : this.aColHdr[nCol], this.oColumnFocus, this.oColumnFocusOuter, this.bNarrowMode);
	
		this.oKeyTrapAnchor.tabIndex = -1;
		;
	},
	unfocusColumn: function(bOnlyHover){
		;
		if(this.nFocusedColumn == -1)
			return;
	
		this.showColumnHover(this.aColHdr[this.nFocusedColumn], false);
		if(!bOnlyHover) {
			this.oColumnFocus.tabIndex = -1;
			this.oColumnFocusOuter.style.zIndex = -100;
		}
		;
		this.nFocusedColumn = -1;
	},
	getFocusedColumn: function(){
		return this.nFocusedColumn;
	},
	findNextColumn: function(nCol, bPrev){
		nCol = nCol + (bPrev ? -1 : 1);
		if(nCol<0 || nCol>=this.nColumnSpan)
			return -1;
		if(!this.aColInfo[nCol].isSortable() && !this.aColInfo[nCol].isMoveable())
			return this.findNextColumn(nCol, bPrev);
		return nCol;
	},
	adjustFocusElements: function(){
		if(this.nFocusMode == 1 && this.getFocusedColumn() != -1)
			this.focusColumn(this.getFocusedColumn());
	},
	updateFocus: function(){
		;
		if(this.nFocusMode == 1 && this.getFocusedColumn() != -1)
			this.focusColumn(this.getFocusedColumn());
		else
			this.updateRowFocus();
		return;
	},
	updateRowFocus: function(){
		var nIndex = this.getSelectedRowIndex();
		var oRow = isNaN(nIndex) ? null : this.getRowByIndex(nIndex);
		;
		if(oRow && this.isRowFullyDisplayed(oRow)) {
			this.focusRow(oRow);
		} else if( this.oKeyTrapAnchor ) {
			this.unfocusRow();
			if(this.bSupportScreenReader)
				this.updateTextForJAWS();
			this.oKeyTrapAnchor.focus();
			this.clearBlurTimeout();
			this.setFocusTimeout();
			this.nFocusMode = 0;
		}
		return;
	},
	focusRow: function(oRow){
		;
	
		this.oFocusOuter.style.zIndex = '';
		this.nFocusMode = 2;
		this.oFocusedRow = oRow;
		this.oFocus.setAttribute('com_ibm_dwa_ui_draggable_redirect', oRow.id);
	
		if(this.bSupportScreenReader) {
			this.oKeyTrapAnchor.tabIndex = 0;
			this.oKeyTrapAnchor.focus();
			this.updateTextForJAWS();
		}
		this.focusElementRow(oRow, this.oFocus, this.oFocusOuter, true, true);
	
	    this.oKeyTrapAnchor.tabIndex = -1;
		;
	},
	unfocusRow: function(){
		;
		if(!this.oFocusedRow)
			return;
	
		this.oFocus.tabIndex = -1;
		this.oFocusOuter.style.zIndex = -100;
		;
		this.oFocusedRow = null;
	},
	getFocusedRow: function(){
		return this.oFocusedRow;
	},
    getFocusedCell: function(oRow){
        if( !oRow ){
            oRow = this.oFocusedRow;
            if( !oRow ){ return null; }
        }


        var childInMaxCol = null;
        var max_icol = -1;

        var iFocusedColumn = this.nColIndexForScreenReader;
        if( iFocusedColumn != -1 && typeof(iFocusedColumn) != 'undefined' ){
            var children = oRow.childNodes;
            if( children ){
                var nearest_icol;
                var nearestChild = null;
                var n = children.length;
                for( var childIndex = 0; childIndex < n; childIndex++ ){
                    var child = children.item(childIndex);
                    if( child ){
                        var icol = child.getAttribute("i_col");
                        if( typeof(icol) == 'undefined' ) continue;
                        if( icol == iFocusedColumn ){
                            return child;
                        }
                        if( icol > iFocusedColumn ){
                            if( !nearestChild || nearest_icol > icol ){
                                nearest_icol = icol;
                                nearestChild = child;
                            }
                        }else if( max_icol < icol && !nearestChild ){
                            max_icol = icol;
                            childInMaxCol = child;
                        }
                    }
                }
            }
        }

        return nearestChild || childInMaxCol;
    },
    checkEnteringEditMode: function(ev){
            return dwa.lv.benriFuncs.eventPreventDefault(ev);
    },
    handleKeyDownInEditMode: function(ev, oCell){
            return dwa.lv.benriFuncs.eventPreventDefault(ev);
    },
	updateTextForJAWS: function(){
		if(!this.bSupportScreenReader)
			return;
		var sApplication = !this.bHasFocus && this.sApplicationNameForReader ? this.sApplicationNameForReader : '';
	
		var nColumn = this.getFocusedColumn();
		var sSorted = '';
		var sTitle = '';
		
		if(nColumn!=-1 && this.nSortBy==nColumn) {
			switch(this.sSortType) {
				case "":
					sSorted = this._smsgs[ "L_JAWS_LISTVIEW_NOSORTED" ];
					break;
				case "resortascending":
					sSorted = this._smsgs[ "L_JAWS_LISTVIEW_ASCENDINGSORTED" ];
					break;
				case "resortdescending":
					sSorted = this._smsgs[ "L_JAWS_LISTVIEW_DESCENDINGSORTED" ];
					break;
			}
			
		}
		if(nColumn!=-1)
			sTitle = this.aColInfo[nColumn].sTitle || this.aColInfo[nColumn].sText || this.aColInfo[nColumn].sHiddenTitle;
	
		var oSortInfo = {};
		this.getNewSortInfo(nColumn, oSortInfo);
		var sResort = '';
		if(nColumn!=-1 && this.aColInfo[nColumn].bResortable) {
			switch(oSortInfo.sSortType) {
				case "":
					sResort = this._smsgs[ "L_JAWS_LISTVIEW_UNDORESORT" ];
					break;
				case "resortascending":
					sResort = this._smsgs[ "L_JAWS_LISTVIEW_RESORTASCENDING" ];
					break;
				case "resortdescending":
					sResort = this._smsgs[ "L_JAWS_LISTVIEW_RESORTDESCENDING" ];
					break;
			}
		}
		
		// nakakura
		// use wai-aria attribute
		if(3 <= dojo.isMozilla || 8 <= dojo.isIE) {
			var sColumnId = this.sId + '-label-column';
			dojo.byId(sColumnId).innerHTML = (dwa.lv.benriFuncs.escapeHtmlKeywords(dwa.common.utils.formatMessage(this._smsgs[ "L_JAWS_LISTVIEW_COLUMN" ], sSorted, sTitle, sResort), 1|2|4|8)).replace(/^\s\s*/, '').replace(/\s*\s$/, '');
			dijit.setWaiState(this.oColumnFocus, "labelledby", sColumnId);			
		// use title attribute
		} else
			this.oColumnFocus.title = (dwa.lv.benriFuncs.escapeHtmlKeywords(dwa.common.utils.formatMessage(this._smsgs[ "L_JAWS_LISTVIEW_COLUMN" ], sSorted, sTitle, sResort), 1|2|4|8)).replace(/^\s\s*/, '').replace(/\s*\s$/, '');
	
		var oRow = this.getRowByIndex(this.getSelectedRowIndex());
		var sAttrName = this.nColIndexForScreenReader < 0 ? 'aria_label_row' : 'aria_label_cell_' + this.nColIndexForScreenReader;
		var sTitle = oRow && oRow.getAttribute(sAttrName) ? oRow.getAttribute(sAttrName) : '';
		var sAddText = '';
		if(this.nColIndexForScreenReader != this.nPreviousColIndexForScreenReader) {
			if (this.nColIndexForScreenReader != -1) {
				sAddText = dwa.common.utils.formatMessage(this._smsgs["L_JAWS_LISTVIEW_WITHCOLNAME"], this.aColInfo[this.nColIndexForScreenReader].sTitle || this.aColInfo[this.nColIndexForScreenReader].sText || this.aColInfo[this.nColIndexForScreenReader].sHiddenTitle);
			}
		}
		sTitle = (sAddText ? sAddText + ' ' : '') + sTitle;
		var sWithIndex = sTitle;
		if(this.nColIndexForScreenReader == -1)
			sWithIndex = dwa.common.utils.formatMessage(this._smsgs["L_JAWS_LISTVIEW_WITHPAGENUM"], sTitle, this.iSelectedPosition + 1 , this.getTotalEntriesInView());
		var sTextForScreenReader = (dwa.lv.benriFuncs.escapeHtmlKeywords(dwa.common.utils.formatMessage(this._smsgs[ "L_JAWS_LISTVIEW_WITHAPPLICATION" ], sApplication, sWithIndex), 1|2|4|8)).replace(/^\s\s*/, '').replace(/\s*\s$/, '');
		var nNbsp = ((this.sTextForScreenReader || "").match(/&nbsp;/g) || []).length;
		this.sTextForScreenReader = (sTextForScreenReader == (this.sTextForScreenReader || "").replace(/&nbsp;/g, "") ? (2 <= nNbsp ? this.sTextForScreenReader.replace(/&nbsp;/g, "") : this.sTextForScreenReader + "&nbsp;") : sTextForScreenReader);
		if(3 <= dojo.isMozilla || 8 <= dojo.isIE) {
			var sKeyTrapId = this.sId + '-label-keyTrap';
			var sFocusId = this.sId + '-label-focus';
			dojo.byId(sKeyTrapId).innerHTML = (dwa.lv.benriFuncs.escapeHtmlKeywords(dwa.common.utils.formatMessage(sApplication, this._smsgs[ "L_JAWS_LISTVIEW_NOENTRY" ]), 1|2|4|8)).replace(/^\s\s*/, '').replace(/\s*\s$/, '');
			dijit.setWaiState(this.oKeyTrapAnchor, "labelledby", sKeyTrapId);
			dojo.byId(sFocusId).innerHTML =  this.sTextForScreenReader;
			dijit.setWaiState(dojo.doc.activeElement, "labelledby", sFocusId);
		} else {
			this.oKeyTrapAnchor.title = (dwa.lv.benriFuncs.escapeHtmlKeywords(dwa.common.utils.formatMessage(sApplication, this._smsgs[ "L_JAWS_LISTVIEW_NOENTRY" ]), 1|2|4|8)).replace(/^\s\s*/, '').replace(/\s*\s$/, '');
			dojo.doc.activeElement.title = this.sTextForScreenReader.replace(/&nbsp;/g, " ");
		}
	},
	// nakakura
	handleKeyDownForScreenReader: function(keyPressed, keyShift) {
		var v$ = this;
		switch(keyPressed) {
			case 33:
			case 34:
			case 35:
			case 36:
			case 38:
			case 40:
				if (v$.bVcursor || !v$.isCategorizedView())
					v$.nPreviousColIndexForScreenReader = v$.nColIndexForScreenReader;
				break;
			case 37:
			case 39:
				if ((v$.bVcursor || !v$.isCategorizedView()) && keyShift) {
					v$.nPreviousColIndexForScreenReader = v$.nColIndexForScreenReader;
					var nNewColIndex = Math.max(Math.min(v$.nColIndexForScreenReader + (keyPressed == 39 ? 1 : -1) * (dojo._isBodyLtr() ? 1 : -1), v$.aLayoutInfoForScreenReader.length - 1), -1);
					if (nNewColIndex != v$.nColIndexForScreenReader) {
						v$.nColIndexForScreenReader = nNewColIndex;
						v$.focus();
					}
				}
				break;
		}
	}
});
