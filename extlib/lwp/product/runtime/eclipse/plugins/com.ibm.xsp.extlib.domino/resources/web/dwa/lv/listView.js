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

dojo.provide("dwa.lv.listView");

dojo.require("dijit._Widget");
dojo.require("dwa.common.commonProperty");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.virtualList");
dojo.require("dwa.lv.jsonReadViewEntries");
dojo.require("dwa.lv.widgetListener");
dojo.require("dwa.lv.eventQueue");
dojo.require("dwa.common.utils");
dojo.require("dwa.lv.listViewBase");
dojo.require("dwa.lv.miscs");
dojo.require("dwa.lv.sortMenuControl");


dojo.declare(
	"dwa.lv.listView",
	dijit._Widget,
{
	store: null,
	actions: null,
	query: null,
	queryOptions: null,
	autoRender: true,
	structure: null, // grid structure (ReadDesign)

	viewColumns: "p-e-listview-columns",
	viewName: "p-e-listview-name",
	viewSortChanged: "p-e-listview-sortchanged",
	selectedDocument: "p-selecteddocument",
	viewDeleteDocument: "p-e-listview-deletedocument",
	pageHeight: "p-e-viewpanel-height",
	pageWidth: "p-e-viewpanel-width",
	canBeNarrowMode: true,
	showDefaultSort: false,
	hiddenColumns: "",
	textListSeparator: ", ",
	quickSearch: false,
	showUnread: true,
	sortByFirstDateColumn: true,
	useLiteList: false,
	dropdownMenu: false,
	softdeletion: true,
	allowScrollHint: false,
	showTumbler: false,
	checkUnidForRefresh: false,
	readDesignEx: true,
	singleSelect: false,
	tabid: "",
	showAwareness: false,
	allowStoreSortByProfile: false,
	showHiddenColumns: "",
	getUnreadCount: false,
	collectionPosition: "",
	hideColumn: false,
	previewMode: false,
	noAutoFocus: false,
	supportScreenReader: false,
	applicationNameForReader: "List view widget",
	dragdropControlId: "",
	fnFormatRow: "",
	tabPanelId: "",
	rangeEntries: "",
	top: "",
	pageIncrement: "",
	application: "",
	_stores: null,
	_actionsObjs: null,
	contextMenuInfo: null,
    alternateRows: false,
    inPlaceEdit: false,
    hookedEvents: [],
    isDebug: false,

	_defaultView: "($inbox)",

	postMixInProperties: function(){
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
		var sId = this.sId = this.id;
		dojo.addClass(this.domNode, "s-listview");

		if(this.structure && typeof(this.structure) == "object"){
			if(typeof(this.structure.fetch) == "function"){
				this.structure.fetch(this); // setup the view design
			}else if(dojo.isArray(this.structure) && this.structure[0].cells){
				var cells = this.structure[0].cells;
				for(var i = 0; i < cells.length; i++){
					if(cells[i].nWidth == undefined){ cells[i].nWidth = 15; }
					if( !cells[i].bIsIcon ){
						if(cells[i].bChars == undefined){ cells[i].bChars = true; }
						if(cells[i].nDigits == undefined){ cells[i].nDigits = 0; }
						if(cells[i].sTitle == undefined){ cells[i].sTitle = cells[i].sName; }
					}
				}
			}
		}

	    var sPropertiesPageHeight = this.pageHeight;
	    for (var asPropertiesPageHeight = sPropertiesPageHeight ? sPropertiesPageHeight.split(' ') : [],
	     i = 0; i < asPropertiesPageHeight.length; i++) {
	        var oPropertyPageHeight = dwa.common.commonProperty.get(asPropertiesPageHeight[i]);
	        oPropertyPageHeight.attach(this);
	    }
	    var sPropertiesPageWidth = this.pageWidth;
	    for (var asPropertiesPageWidth = sPropertiesPageWidth ? sPropertiesPageWidth.split(' ') : [],
	     i = 0; i < asPropertiesPageWidth.length; i++) {
	        var oPropertyPageWidth = dwa.common.commonProperty.get(asPropertiesPageWidth[i]);
	        oPropertyPageWidth.attach(this);
	    }

	    dwa.common.commonProperty.get(this.viewColumns).attach(this);

	    var oClass = this;

	    this.bShowUnread = this.showUnread;
	    this.bAllowMultipleSelection = !this.singleSelect;
	    this.bCanBeNarrowMode = this.canBeNarrowMode;
	    this.bShowDefaultSort = this.showDefaultSort;
	    this.sHiddenColumnsOrg = this.hiddenColumns;
	    this.sHiddenColumns = '';
	    this.sTextlistSeparator = this.textListSeparator;
	    this.sTabId = this.tabid+ ':com_ibm_dwa_ui_tab';
	    this.bUseLiteList = this.useLiteList;
	    this.bShowAwareness = this.showAwareness;
	    this.bStoreSortByProfile = this.allowStoreSortByProfile;
	    var sShowHiddenColNames = this.showHiddenColumns;
	    this.aShowHiddenColNames = sShowHiddenColNames ? sShowHiddenColNames.split('|') : [];
	    this.bSoftDeleteDisabled = !this.softdeletion;
	    this.bGetUnreadCount = this.getUnreadCount;
	    this.bAllowScrollHint = this.allowScrollHint;
	    this.sCollectionPosition = this.collectionPosition;
	    this.fHideColumn = this.hideColumn;
	    this.fPreviewMode = this.previewMode;
	    this.fCheckUnidForRefresh = this.checkUnidForRefresh;
	    this.bShowTumbler = this.showTumbler;
	    this.bNoAutoFocus = this.noAutoFocus;
	    this.bSupportScreenReader = this.supportScreenReader;
	    this.sApplicationNameForReader = this.applicationNameForReader;

	    this.sDragDropControlId = this.dragdropControlId;
	    var sFormatRow = this.fnFormatRow;
	    if(sFormatRow)
	        this.fnFormatRow = eval(sFormatRow);
	    else
	        this.fnFormatRow = function(sUnid, oTreeViewEntry, oXmlEntry, sCellClass, sWidthClass, sCellTagName, sId, aStr, n, aTitle){return dwa.lv.listView.prototype.formatRow.call(oClass, sUnid, oTreeViewEntry, oXmlEntry, sCellClass, sWidthClass, sCellTagName, sId, aStr, n, aTitle)};
	    this.fnReadDoc = function(){oClass.openEntryAction(oClass.getSelectedData())};
	    this.fnCreateDoc = function() {oClass.newEntryAction()};
	    this.hSelect = null;
	    this.sLastUnids = "";
	    this.fnSelect = function(nSelectionMode) {
	        if(oClass.hSelect){
	            clearTimeout(oClass.hSelect);
	        }
	        oClass.hSelect = setTimeout(
	            function(){
	                var asUnids = oClass.oVL.getSelectedData();
	                var sUnids = asUnids ? asUnids.join(',') : '';
	                if(sUnids != oClass.sLastUnids) {
	                    oClass.selectEntryAction(oClass.getSelectedData(), (nSelectionMode || 0) );
	                }
	                oClass.sLastUnids = sUnids;
	                oClass.hSelect = null;
	            }
	        ,1);
	    };
	    this.fnDelete = function() {oClass.deleteEntryAction(oClass.getSelectedData());};
	    this.fnLoadedData = function(oRoot) {oClass.loadedData();dwa.lv.globals.get().fFirstDataAvailable = true; dwa.lv.globals.get().fJesterReady = true;};
	    this.fnSortChanged = function(nCol, bByUser) {oClass.oCols.nSortBy = nCol; oClass.sortChangedAction(nCol, bByUser);};
	    this.fnSearch = function(sTxt, nCol, sKeyType, nXmlCol) {oClass.simpleSearch(sTxt, nCol, sKeyType, nXmlCol);};
	    this.sortMenuControl = dwa.lv.sortMenuControl.get();
	    this.fnStartsWith = function(oContext) {
	        var oNewContext = {
	            sortInfo: oClass.sortMenuControl.generateSortInfo(oClass.oVL),
	            getBufferedSearchString: function() {
	                var sSearch = oContext.fnGetStartsWith();
	                oContext.fnClearStartsWith();
	                return sSearch;
	            },
	            processSearch: function(nNewSortBy, sSearchKey) {
	                var oColSort = oClass.oCols[nNewSortBy];
	                if(!oColSort)
	                    return false;
	                oClass.oCols.sSearchKey = sSearchKey;
	                oClass.oCols.nSortBy = nNewSortBy;
	                oClass.oCols.sKeyType = oColSort.nFormat == 1 || oColSort.nFormat == 3 ? 'time' : oColSort.bBytes ? 'number' : 'text';
	                oContext.fnClearStartsWith();
	                oContext.fnFocus();
	                var oProperty = dwa.common.commonProperty.get(oClass.viewColumns);
	                if (oProperty)
	                    oProperty.setValue(oClass.oCols);
	                return true;
	            },
	            cancelSearch : function() {
	                oContext.fnClearStartsWith();
	                oContext.fnFocus();
	            }
	        };
	        oClass.searchEntryAction(oNewContext);
	    };
	    this.fnContextMenu = function(ev) {oClass.handleContextMenu(ev, oClass.getSelectedData());return dwa.lv.benriFuncs.eventCancel(ev);};
	    this.fnHeaderContextMenu = function(ev) {
	        var lvid = oClass.oVL.sId;
	        var menuInfo = oClass.sortMenuControl.generateMenuInfo(lvid);
	        if(menuInfo) {
	            oClass.handleNarrowMenu(dojo.fixEvent(ev), menuInfo, oClass.oVL.nSortCol);
	        }
	        return dwa.lv.benriFuncs.eventCancel(ev);
	    };
	    this.fnAfterGenerateContainer = function() {
            if( oClass.dropdownMenu ){ oClass.createContextMenu(); }
            if( oClass.oVL && oClass.oVL.appEventMgr ){
                oClass.oVL.appEventMgr.generateAppEventHooksAfterHTMLGeneration();
            }
        };
        this.fnNarrowStyleChanged = function(){
            if( oClass.oVL && oClass.oVL.appEventMgr ){
                oClass.oVL.appEventMgr.generateAppEventHooksAfterHTMLGeneration();
            }
        }
		this.fnAfterRefresh = function() {
            oClass.afterRefresh();
        };

		this.initVirtualList();

	    if (this.tabPanelId)
	        setTimeout(this.initActivation.dojo_misc_setContext(this), 1);

	    this.oSearch = {};
	    this.sFTSearch = '';
	    this.bShowUnreadOnly = false;
	    this.aoEntries = [];
	    this.oCols = [];
	    this.sFolder = '';
	    this.sStartKey = '';
	    this.sUntilKey = '';
	    this.bIncludeUntilKey = false;
	    this.fForceReload = false;
	    this.fShowResultMessage = false;
	},

    createVirtualList : function(){

        if( this.bUseLiteList ){
            return new LiteList;
        }else{
            var evs = this.hookedEvents;

            if( (!evs || evs.length <= 0) && !this.inPlaceEdit ){
                return new dwa.lv.virtualList;
            }else{
                dojo.require("dwa.lv.virtualListVCursor");

                var mgr = undefined;
                if( evs && evs.length > 0){
                    dojo.require("dwa.lv.appEventManager");

                    mgr = new dwa.lv.appEventManager({
                        hookedEvents: evs,
                        isDebug: this.isDebug
                    });

                    mgr.registerListView( this.sId, this );
                }

                var vl = new dwa.lv.virtualListVCursor;
                vl.appEventMgr = mgr;
                vl.inPlaceEdit = this.inPlaceEdit;

                return vl;
            }
        }
    },

	 initVirtualList : function(){

		if (!this.oVL)
			this.oVL = this.createVirtualList();

	    this.oVL.init(this.sId, 10, this.alternateRows, 0, (this.fHideColumn ? 'none' : false), false, (this.sCollectionPosition ? this.sCollectionPosition : ''), this.bShowUnread);

	    this.oVL.bAllowMultipleSelection = this.bAllowMultipleSelection;
	    this.oVL.bCanBeNarrowMode = this.bCanBeNarrowMode;
	    this.oVL.bShowDefaultSort = this.bShowDefaultSort;
	    this.oVL.sDragDropControlId = this.sDragDropControlId;

	    if(this.sTextlistSeparator)
	        this.oVL.sTextlistSeparator = this.sTextlistSeparator;

	    this.oVL.sTabId = this.sTabId;
	    this.oVL.bAllowScrollHint = this.bAllowScrollHint;
	    this.oVL.bCheckUnidForRefresh = this.fCheckUnidForRefresh;
	    this.oVL.bShowTumbler = this.bShowTumbler;
	    this.oVL.bSupportScreenReader = this.bSupportScreenReader;
	    this.oVL.sApplicationNameForReader = this.sApplicationNameForReader;
	    if(this.bSupportScreenReader || this.oVL.bVcursor )
	        this.oVL.nColIndexForScreenReader = this.nPreviousColIndexForScreenReader = -1; // nakakura

	    this.oVL.fnFormatRow = this.fnFormatRow;
	    this.oVL.fnReadDoc = this.fnReadDoc;
        this.oVL.fnSetDocColumn = dojo.hitch( this, this.setEntryColumnAction );
	    this.oVL.fnCreateDoc = this.fnCreateDoc;
	    if(this.selectedDocument)
	        this.oVL.fnSelect = this.fnSelect;
	    if(this.viewDeleteDocument)
	        this.oVL.fnDelete = this.fnDelete;
	    this.oVL.fnLoadedData = this.fnLoadedData;
	    this.oVL.fnSortChanged = this.fnSortChanged;
	    if(this.quickSearch) {
	        this.oVL.fnSearch = this.fnSearch;
	        this.oVL.fnStartsWith = this.fnStartsWith;
	    }
	    this.oVL.fnContextMenu = this.fnContextMenu;
	    this.oVL.fnHeaderContextMenu = this.fnHeaderContextMenu;
	    if (this.dropdownMenu || this.oVL.appEventMgr )
	        if(this.bUseLiteList)
	            this.fnAfterGenerateContainer();
	        else
	            this.oVL.fnAfterGenerateContainer = this.fnAfterGenerateContainer;
        this.oVL.fnNarrowStyleChanged = this.fnNarrowStyleChanged;
		this.oVL.fnAfterRefresh = this.fnAfterRefresh;
	    this.oVL.bAllowStoreColumnCookie = true;

	    this.oVL.oDataStore = new dwa.lv.jsonReadViewEntries({
			store: this.store,
			query: this.query,
			queryOptions: this.queryOptions
		}); //kami
	},
	startup: function(){
		if(this._started){return;}
		this._started = true;
		if(this.autoRender){
			this.render();
		}
	},
	resize: function(changeSize, resultSize){
		if(changeSize || resultSize){
			dojo.marginBox(this.domNode, changeSize||resultSize);
		}
		this.oVL.onResize();
	},
	render: function(){
		if(this.structure && typeof(this.structure) == "object"){
			if(typeof(this.structure.fetch) == "function"){
				var view = this.query && this.query.FolderName || this.store && this.store.folderName || this._defaultView;
				if(!this.structure.url && this.store && this.store.url){
					this.structure.url = this.store && this.store.url;
				}
				dwa.common.commonProperty.get(this.viewName).setValue(view); // kami
			}else if(this.structure[0].cells){
				var structure = this.structure[0].cells;
				structure.sFolder = structure.sFolder || this.store && this.store.folderName || this._defaultView;
				for(var i in this.structure[0]){
					if(i == "cells"){ continue; }
					structure[i] = this.structure[0][i];
				}
				dwa.common.commonProperty.get(this.viewColumns).setValue(structure);
			}
		}
	},
	release: function(){


	    var sPropertiesPageHeight = this.pageHeight;
	    for (var asPropertiesPageHeight = sPropertiesPageHeight ? sPropertiesPageHeight.split(' ') : [],
	     i = 0; i < asPropertiesPageHeight.length; i++) {
	        var oPropertyPageHeight = dwa.common.commonProperty.get(asPropertiesPageHeight[i]);
	        oPropertyPageHeight.detach(this);
	    }
	    var sPropertiesPageWidth = this.pageWidth;
	    for (var asPropertiesPageWidth = sPropertiesPageWidth ? sPropertiesPageWidth.split(' ') : [],
	     i = 0; i < asPropertiesPageWidth.length; i++) {
	        var oPropertyPageWidth = dwa.common.commonProperty.get(asPropertiesPageWidth[i]);
	        oPropertyPageWidth.detach(this);
	    }
	    if(this.oVL)
	        this.oVL.destroy();

	    dwa.common.commonProperty.get(this.viewColumns).detach(this);
	},
	destroy: function(){
	    if (this.narrowMenu)
	        this.narrowMenu.destroy();
	    if (this.searchDialog)
	        this.searchDialog.destroy();

	    this.release();
	    this.inherited(arguments);
	},
	initActivation: function(){

	    if (!this.tabPanelId)
	        return;
	    var oClass = this;
	    dwa.lv.widgetListener.prototype.oWidgets[this.tabPanelId+ ':com_ibm_dwa_ui_tabpanel'].onActivated = function() {oClass.onActivated();};
	    dwa.lv.widgetListener.prototype.oWidgets[this.tabPanelId+ ':com_ibm_dwa_ui_tabpanel'].onDeactivated = function() {oClass.onDeactivated();};
	},
	onActivated: function(){
	    this.oVL.focus();
	},
	onDeactivated: function(){
	},
	observeColumns: function(oProperty){
	    if(!oProperty.vValue)
	        return;


	    var aCols = oProperty.vValue;

	    var bFolder = this.sFolder != aCols.sFolder;
	    var bSort = this.oVL.nSortBy != aCols.nSortBy;
	    var bSearchKey = aCols.sSearchKey;
	    var bStartKey = this.sStartKey != aCols.sStartKey;
	    var bUntilKey = this.sUntilKey != aCols.sUntilKey;
	    var bIncludeUntilKey = this.bIncludeUntilKey != aCols.bIncludeUntilKey;
	    var bFTSearch = this.sFTSearch != aCols.sFTSearch;
	    var bShowUnreadOnly = this.bShowUnreadOnly != aCols.bShowUnreadOnly;

	    if(!bFolder && !bSort && !bSearchKey && !bStartKey && !bUntilKey && !bIncludeUntilKey && !bFTSearch && !bShowUnreadOnly && !this.fForceReload)
	        return;

	    ;

	    if(bFolder)
	        this.applyViewDesign(oProperty.vValue);

	    if(bSearchKey) {
	        this.oVL.sSearchKey = aCols.sSearchKey + '';
	        this.oVL.sKeyType = aCols.sKeyType;
	        this.oVL.bProcessingSearch = true;
	        aCols.sSearchKey = '';
	        aCols.sKeyType = '';
	    }

	    this.sStartKey = this.oVL.sStartKey = aCols.sStartKey;
	    this.sStartKey = this.oVL.sUntilKey = aCols.sUntilKey;
	    this.bIncludeUntilKey = this.oVL.bIncludeUntilKey = aCols.bIncludeUntilKey;
	    this.oVL.nDefaultSortBy = aCols.nDefaultSortBy;

	    var oPresets = {};
	    this.sFTSearch = aCols.sFTSearch;
	    if(this.sFTSearch)
	        oPresets.SearchString = encodeURIcomponent(this.sFTSearch);

	    this.bShowUnreadOnly = aCols.bShowUnreadOnly;
	    if(this.bShowUnreadOnly)
	        oPresets.UnreadOnly = 1;

	    //get unread count if it is a folder and bGetUnreadCount is set to true
	    if (this.bGetUnreadCount && aCols.bIsFolder)
	        oPresets.UnreadCountInfo = 1;

	    this.oVL.oPresetFields = oPresets;

	    this.fShowResultMessage = !!this.sFTSearch;

	    if(bFolder || bSort) {
	        this.resortByColumn(aCols.nSortBy);

	        // clear the preview pane
	        if(this.selectedDocument)
	            dwa.common.commonProperty.get(this.selectedDocument).setValue('');
	    } else
	        this.refresh(true, this.fForceReload ? false: true);

	    if(bSearchKey)
	        this.oVL.bProcessingSearch = false;

	    this.oVL.sSearchKey = '';
	    this.oVL.sKeyType = '';
	    this.fForceReload = false;
	},
	loadedData: function(){

	    var sProp = this.rangeEntries;
	    if(this.fShowResultMessage && sProp)
	        dwa.common.commonProperty.get(sProp).setValue(this.getTotalEntries());
	    this.fShowResultMessage = false;
	},
	getTotalEntries: function(){
	    if(this.bUseLiteList)
	        return this.oVL.bIsLastPage ? this.oVL.nLoadedCount : (this.oVL.nLoadedCount+1);
	    else
	        return this.oVL.oRoot.getTotalEntries();
	},
	getMaxRowsPerPage: function(){
	    if(this.bUseLiteList)
	        return this.oVL.getCount();
	    else
	        return this.oVL.iMaxRowsPerPage;
	},
	applyViewDesign: function(oCols){
	    if(!oCols)
	        return;

	    this.oCols = oCols;
	    this.sFolder = oCols.sFolder;
	    this.oVL.sFolderName = this.sFolder || "*";


	    this.oVL.resetColumnDesign(this.oCols.length);
	    for(var i=0,iMax=(this.oCols?this.oCols.length:0); i < iMax; i++ ){
	        var oInfo = this.oCols[i];
	        this.oVL.setColumnWidth (i, oInfo.nWidth, oInfo.bFixed, oInfo.bChars, oInfo.bTwistie, oInfo.bResponse, oInfo.bExtend, oInfo.sNarrowDisplay, oInfo.nSequenceNumber, oInfo.bBeginWrapUnder, oInfo.sIMColName, !!oInfo.bIsIcon, !!oInfo.bThinColumn, !!oInfo.bShowGradientColor, !!oInfo.bAlignGradientColor, !!oInfo.bUnhideWhenWrapped);
	        if( typeof(oInfo.nXmlCol) == 'undefined' ){ oInfo.nXmlCol = i; }
	        this.oVL.bindColumnData (i, oInfo.nXmlCol, undefined, oInfo.sName);
	        this.oVL.setColumnTitle (i, oInfo.sTitle, oInfo.bSort, oInfo.iViewSort, oInfo.sHiddenTitle, oInfo.nHeaderIcon);
	        this.oVL.setColumnFormat(i, oInfo.nFormat, oInfo.bBytes, oInfo.nDigits, oInfo.bOmitThisYear);
	    }
	    this.oVL.updateColumnDesign();

	    ;
	},
	load: function(){
	    this.refresh(true);
	},
	refresh: function(fLoad, fClearSelection){
	    if(this.bUseLiteList)
	        this.oVL.refresh(fLoad);
	    else
	        this.oVL.refresh(fLoad, fClearSelection, void 0, void 0, !this.bNoAutoFocus);
	},
	update: function(sFolder, asUnids, bShowTrash){
	    if(this.sFolder != sFolder || this.bUseLiteList) {
	        this.refresh(true);
	        return true; // don't need to update preview pane
	    } else if(dwa.lv.globals.get().oSettings.bSoftDeleteEnabled) {
	        if (this.bGetUnreadCount && this.oCols.bIsFolder) {
	            //check the unread state to update the unread count
	            var nTotalUnreadChanged = 0;
	            for(var i=0;i<asUnids.length;i++) {
	                var aRows = this.oVL.getRowsByUnid(asUnids[i]);
	                if (aRows && aRows.length && !this.oVL.isMarkedRead(aRows[0]))
	                    nTotalUnreadChanged--;
	            }
	            //waiho
	            //update unread count
	            //raise event property
	            if (nTotalUnreadChanged != 0  && dwa.lv.globals.get().oUnreadCountManager) {
	                dwa.lv.globals.get().oUnreadCountManager.updateFolderWithDelta(this.sFolder, nTotalUnreadChanged);
	            }
	        }

	        this.oVL.update(false, asUnids, 3);
	        return false; // need to update preview pane
	    } else {
	        for(var i=0;i<asUnids.length;i++) {
	            var aRows = this.oVL.getRowsByUnid(asUnids[i]);
	            for(var j=0;aRows && j<aRows.length;j++)
	                this.oVL.showTrashIcon(aRows[0], bShowTrash);
	        }
	        return false;
	    }
	},
	resortByColumn: function(nCol){
	    this.oVL.resortByColumn(nCol, void 0, void 0, !this.bNoAutoFocus);
	},
	createContextMenu: function(){

	    var oContainer = this.oVL.getRMMContainer();
	    this.eventQueueTarget = oContainer.id;
	    dwa.lv.eventQueue.get().attach(this, 'contextmenu', true);
	},
	afterRefresh: function(){

		var aUnids = this.oVL.getSelectedData();
		var oProperty = dwa.common.commonProperty.get(this.selectedDocument);
		// VSEN7TGHZB : When user clicks refresh, if we do move the selection then we should blank out the preview pane.
		if(oProperty.vValue != (aUnids && aUnids.length ? this.sFolder + '|' + aUnids[0] : ''))
			oProperty.setValue('');
	},
	observe: function(oProperty){
	    if (!oProperty.isLatest())
	        return;



	    var sNamesHeight = this.pageHeight;
	    var sNamesWidth = this.pageWidth;

	    if (oProperty.sName == this.top)
	        this.observeTop(oProperty);
	    else if (oProperty.sName == this.pageIncrement)
	        this.observePageIncrement(oProperty);
	    else if (dwa.common.utils.indexOf(sNamesHeight ? sNamesHeight.split(' ') : [], oProperty.sName) >= 0)
	        this.observeContainerSize(oProperty);
	    else if (dwa.common.utils.indexOf(sNamesWidth ? sNamesWidth.split(' ') : [], oProperty.sName) >= 0)
	        this.observeContainerSize(oProperty);
	    else if (oProperty.sName == this.viewColumns)
	        this.observeColumns(oProperty);
	},
	updateNavigateSelection: function(sUnid){
	    var aRows = this.oVL.getRowsByUnid(sUnid);
	    if (aRows && aRows.length){
	        this.oVL.deselectEntries();
	        this.oVL.selectEntry(aRows[0], false, null, null);
	    }
	},
	simpleSearch: function(){
	    // clear preview pane after quicksearch

	    var sProperty = this.selectedDocument;
	    if(sProperty)
	        dwa.common.commonProperty.get(sProperty).setValue('');
	},
	observeContainerSize: function(oProperty){
	    if(this.bUseLiteList)
	        this.oVL.doResize();
	    else
	        this.oVL.onResize();
	},
	getSelectedData: function(sColumn, bAdd, bSort /*=false*/){
	    if( !sColumn ){ sColumn = "ENTRYDATA"; }

	    var aRet = this.oVL.getSelectedData(sColumn, bAdd, bSort);
	    return aRet && aRet.length ? aRet : [];
	},
	formatRow: function(sUnid, oTreeViewEntry, oXmlEntry, sCellClass, sWidthClass, sCellTagName, sId, aStr, n, aTitle){
	    var iStart = 0;
	    if (this.bSoftDeleteDisabled) {
	        iStart = 1;
	        var bIsTrash = oXmlEntry && this.oDataStore.getAttributeBoolean(oXmlEntry, "markedfordel");

	        // insert a cell for displaying trash icon
	        aStr[n++] = dwa.lv.listViewBase.prototype.LV$Static.getStartTag(sCellTagName)
	                +' class="'+ sCellClass + ' ' + sWidthClass + '0' + (bIsTrash ? " s-trash" : '')
	                +'" iscell="1"';
	        if( bIsTrash ){
	            aStr[n++] = ' istrash="1"';
	        }

	        if( sUnid ){
	            aStr[n++] = ' id="'+"ct" + sUnid+'"';
	        }

	        // close HTML
	        aStr[n++] = '>' + dwa.lv.listViewBase.prototype.LV$Static.getEndTag();
	    }
	    return this.oVL.formatRow(sUnid, oTreeViewEntry, oXmlEntry, sCellClass, sWidthClass, sCellTagName, sId, this.oCols, iStart, aStr, n, aTitle);
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
	openEntryAction: function(items){
		var actions = this.findActions(items[0]);
		if(actions){
			actions.openEntryAction(items);
		}
	},
    setEntryColumnAction: function(entryItem, updatedColumnItem, colInfo){
		var actions = this.findActions(entryItem);
		if(actions && actions.setEntryColumnAction){
			actions.setEntryColumnAction(entryItem, updatedColumnItem, colInfo);
		}
    },
	newEntryAction: function(){
		if(this._actionsObjs.length == 1){
			this._actionsObjs[0].newEntryAction();
		}
	},
	selectEntryAction: function(items, selectionMode){
		// selectionMode - 1: selected by keyboard, 2: selected by mouse, 0: selected automatically by widget initialize, refresh, etc.
		var actions = this.findActions(items[0]);
		if(actions){
			actions.selectEntryAction(items, selectionMode);
		}
	},
	deleteEntryAction: function(items){
		var actions = this.findActions(items[0]);
		if(actions){
			actions.deleteEntryAction(items);
		}
	},
	sortChangedAction: function(col, isByUser){
	},
	searchEntryAction: function(context){
		dojo["require"]("dwa.lv.searchDialog");
		var _this = this;
		dojo.addOnLoad(function() {
			if (!_this.searchDialog) {
				_this.searchDialog = new dwa.lv.searchDialog({
					id: _this.sId + "-startswith"
				});
			}
			_this.searchDialog.show(context);
		});
	},
	handleNarrowMenu: function(ev, menuInfo, sortColIndex){
		dojo["require"]("dwa.common.contextMenu");
		if(!this.narrowMenuInfo){
			this.narrowMenuInfo = menuInfo;
		}
		for(var i = 0; i < this.narrowMenuInfo.length; i++){
			this.narrowMenuInfo[i].isChecked = (this.narrowMenuInfo[i].realIndex == sortColIndex);
		}
		var _this = this;
		var _ev = dojo.isIE ? dojo.mixin({}, ev) : ev;
		dojo.addOnLoad(function(){
			if(!_this.narrowMenu){
				_this.narrowMenu = new dwa.common.contextMenu({
					menuInfo: _this.narrowMenuInfo,
					focusMethod: dojo.hitch(_this, function(){_this.oVL.focus()}),
					id: _this.sId + "-narrowmenu"
				});
			}
			_this.narrowMenu.show(_ev);
		});
	},
    handleContextMenu: function(ev, items){
	},
	updateUnreadState: function(fSelected, fRead, asUnids, fFromPreview){
	    var aUnids = [];
	    if(fSelected)
	        aUnids = this.oVL.getSelectedData();
	    if(!fSelected && asUnids)
	        aUnids = asUnids;

	    var bRefreshAll = (this.bShowUnreadOnly && !fFromPreview) || aUnids.length==0;
	    if(bRefreshAll) {
	        // to update screen and cached xml data
	        this.refresh(true);

	        //when mark all document read/unread, update unread count of all folders
	        if (this.bGetUnreadCount && !fSelected && dwa.lv.globals.get().oUnreadCountManager) {
	            //mark all read, means empty all unread count
	            dwa.lv.globals.get().oUnreadCountManager.refreshAll(fRead);
	/*
	            var oProperty = dwa.common.commonProperty.get('p-e-unread-count-refreshall');
	            var oVal ={};
	            oVal.bEmptyAll = fRead; //mark all read, so empty all unread count
	            oProperty.setValue(oVal);
	*/
	        }
	    }
	    else {
	        if(!aUnids || !aUnids.length)
	            return;
	        var nTotalUnreadChanged = 0;
	        for(var i=0;i<aUnids.length;i++) {
	            var aRows = this.oVL.getRowsByUnid(aUnids[i]);
	            var fIsRead = false;
	            if (aRows && aRows.length) {
	                if (this.bGetUnreadCount && this.oCols.bIsFolder)
	                    fIsRead = this.oVL.isMarkedRead(aRows[0]);

	                if (this.bShowUnread) {
	                    var x$;

	                    // change the row "unread" status
	                    if (this.oVL.bIsLiteList) {
	                        aRows[0].bUnread = !fRead;
	                    } else {
	                        var t$ = this.oVL.getTreeViewEntry(aRows[0]);
	                        x$ = t$ && t$.getUnid() && t$.getViewEntry();
	                        x$ && this.oVL.oDataStore.setAttribute(x$, "unread", '' + !fRead);
	                    }

	                    if (this.oVL.bIsLiteList || x$) {
	                        var i$ = aRows[0].className.indexOf('vl-font');
	                        if (i$ > -1)
	                            aRows[0].className = aRows[0].className.substr(0, i$)
	                             + (!fRead ? dwa.lv.listViewBase.prototype.LV$Static.getUnreadFmt(this.bShowUnread) : "vl-font-n")
	                             + aRows[0].className.substr(i$ + 9);
	                    }

	                    var v$ = this.oVL;
	                    this.oVL.enumlateCellsInRow(aRows[0], !fRead ? function(oColInfo, oCell){v$.changeIconToUnread(oColInfo, oCell)} : function(oColInfo, oCell){v$.changeIconToRead(oColInfo, oCell)});
	                }

	                if (this.bGetUnreadCount && this.oCols.bIsFolder && (fRead != fIsRead))
	                    nTotalUnreadChanged++;
	            }
	            else if (this.bGetUnreadCount && this.oCols.bIsFolder){
	                //get the unread status from the xml data
	                var aEntries = this.oVL.getEntriesByUnid(aUnids[i]);
	                if (aEntries && aEntries.length){
	                    var oNode = aEntries[0].getViewEntry();
	                    if (oNode) {
	                        fIsRead = this.oVL.oDataStore.getAttribute(oNode, "unread") != "true";
	                        this.oVL.oDataStore.setAttribute(oNode, "unread", (fRead ?"false":"true"));
	                        if (fRead != fIsRead)
	                            nTotalUnreadChanged++;
	                    }
	                }
	            }

	        }


	        //update unread count
	        //raise event property
	        if (this.bGetUnreadCount && this.oCols.bIsFolder && nTotalUnreadChanged  && dwa.lv.globals.get().oUnreadCountManager) {
	            //SPR #PTHN7M2RF4:  take care the case of selecting all documents
	            if (aUnids.length == this.getTotalEntries()) {
	                var oUnreadObj = {};
	                oUnreadObj[this.sFolder] = fRead ? 0 : aUnids.length;
	                dwa.lv.globals.get().oUnreadCountManager.updateUnreadCount(oUnreadObj);
	            }
	            else
	                dwa.lv.globals.get().oUnreadCountManager.updateFolderWithDelta(this.sFolder, (fRead ? -1 : 1) * nTotalUnreadChanged);
	        }

	    }
	},

    // the following methods may be dojo.connect-ed by applications

    onMouseOver: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onMouseOver" );}
    },
    onCellMouseOver: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellMouseOver" );}
    },

    onMouseMove: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onMouseMove" );}
    },
    onCellMouseMove: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellMouseMove" );}
    },

    onMouseOut: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onMouseOut" );}
    },
    onCellMouseOut: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellMouseOut" );}
    },

    onMouseDown: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onMouseDown" );}
    },
    onCellMouseDown: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellMouseDown" );}
    },

    onMouseUp: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onMouseUp" );}
    },
    onCellMouseUp: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellMouseUp" );}
    },

    onClick: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onClick" );}
    },
    onCellClick: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellClick" );}
    },

    onDblClick: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onDblClick" );}
    },
    onCellDblClick: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellDblClick" );}
    },

    onContextMenu: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onContextMenu" );}
    },
    onCellContextMenu: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellContextMenu" );}
    },

    onFocus: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onFocus" );}
    },
    onCellFocus: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellFocus" );}
    },

    onBlur: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onBlur" );}
    },
    onCellBlur: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellBlur" );}
    },

    onKeyDown: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onKeyDown" );}
    },
    onCellKeyDown: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellKeyDown" );}
    },

    onKeyUp: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onKeyUp" );}
    },
    onCellKeyUp: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellKeyUp" );}
    },

    onKeyPress: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onKeyPress" );}
    },
    onCellKeyPress: function( ev, ext ){
        if( this.isDebug ){ console.log( "listview: - onCellKeyPress" );}
    }
});
