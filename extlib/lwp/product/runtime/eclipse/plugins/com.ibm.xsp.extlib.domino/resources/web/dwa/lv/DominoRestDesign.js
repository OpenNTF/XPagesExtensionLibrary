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

dojo.provide("dwa.lv.DominoRestDesign");

dojo.require("dwa.common.commonProperty");
dojo.require("dwa.lv.jsonReadDesign");
dojo.require("dwa.lv.xmlReadDesign");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.miscs");
dojo.require("dwa.common.utils");

dojo.declare("dwa.lv.DominoRestDesign",null,
{
	url: "",
	db: null,
	type: "xml",
	dwa: true,
	oColCache: {},
	folder: "",
	view: "",
	

	viewColumns: "",
	viewColumnsExt: "",
	viewName: "",
	readDesignEx: true,
	showUnreadOnly: "",
	sortByFirstDateColumn: false,
	searchDefaultSortColumn: false,
	showDefaultSort: false,
	updateUnreadOnlyMenu: "",

	error: function(e){
		console.error(e);
	},
	constructor: function(/*Object*/args){

		if (args.url){
			this.url=args.url;
			this.db = new lotus_domino_Database(this.url);
		}

	},
	fetch: function(listViewWidget){

		this.viewColumns = listViewWidget.viewColumns;
		this.viewName = listViewWidget.viewName;

	    var oPropertyViewName = dwa.common.commonProperty.get(this.viewName);
	    oPropertyViewName.attach(this);
	    dwa.common.commonProperty.get(this.viewColumns + '-internal').attach(this);
	    if(this.showUnreadOnly)
	        dwa.common.commonProperty.get(this.showUnreadOnly).attach(this);
	    this.observeName(oPropertyViewName);
	},
	load: function(sFolder, nSortBy, sFTSearch){
		
		if (!sFolder)
			return;
		
		this.view = this.db.getView(sFolder,true);
		this.view.setPageSize(0);
		
		//Open the first page of the view with size 0 to read design
		this.view.getPage(dojo.hitch(this,"loaded"),dojo.hitch(this,"error"));
		
	},
	loaded: function(oPage){
		
		var aAllCols = this.parseDesign(oPage.getView());
		
		var aCols = [];
	    for(var i=0;i<aAllCols.length;i++){
	        if(!aAllCols[i].bHidden)
	           aCols.push(aAllCols[i]);
	    }
		
	    aCols.sFolder=this.view.getTitle();
	    aCols.nDefaultSortBy = -1;
	    
	    aCols.nSortBy = -1;
	    aCols.sFTSearch = null;
	    aCols.sStartKey = '';
	    aCols.sUntilKey = '';
	    aCols.bIncludeUntilKey = false;
	    aCols.sIMColName = aAllCols.sIMColName;
	    aCols.bShowUnreadOnly = false;
	    
	    var sProperty = this.viewColumns + '-internal';
	    dwa.common.commonProperty.get(sProperty).setValue(aCols);
	},
	parseDesign: function(oView,bTrash,sIMColName,sWho,sAlt,sViewName,bShowDefaultSort/*=true*/,aShowHiddenColNames){
		var oColumns = oView.getColumns();

		var aCols = [];
		for(var k=0;k<oColumns.length;k++){
			
	        var bSortA    = false;
	        var bSortD    = false;
	        var bFixed    = false;
	        var bIsIcon   = false;
	        var nWidth    = 0;
	        var bChars    = true;
	        var sTitle    = "";
	        var sHiddenTitle = "";
	        var nFormat   = 2;
	        var bTwistie  = false;
	        var iColSort  = 0;
	        var iViewSort = 0;

			var oCol = oColumns[k];
	        aCols[k] = {
		            nWidth: oCol.getWidth(),
		            bFixed: bFixed,
		            bIsIcon: bIsIcon,
		            bChars: true,
		            nXmlCol: k,
		            bSort:iColSort,
		            sTitle: oCol.getTitle(),
		            sHiddenTitle:sHiddenTitle,
		            sName: oCol.getProgName()+"",
		            iViewSort:iViewSort,
		            nDigits:0,
		            bIsName: true,
		            bExtend: true,
		            sNumFormat: "general",
		            bBytes:false,
		            bOmitThisYear:false,
		            bHidden: oCol.isHidden(),
		            bTwistie: false,
		            nFormat: nFormat,
		            bResponse: false,
		            sNarrowDisplay: "top",
		            nSequenceNumber: 0,
		            bBeginWrapUnder: false
		        };
		} //End For Loop
		
			aCols.nRowLines = 5;
		    aCols.sIMColName = "";
		    aCols.bAltWhoExists = false;
		
		return aCols;
	},
	getFirstDateColumn: function(aCols){
		//alert('in first date column');
		debugger;
	},
	getFirstSortColumn: function(aCols){
	    //alert('in first sort col');
	    debugger;
	},
	observeLoadedDesign: function(oProperty){
	    this.oCols = oProperty.vValue;
	    this.oColCache[this.oCols.sFolder] = this.oCols;
	    if(this.viewColumnsExt)
	        dwa.common.commonProperty.get(this.viewColumnsExt).setValue(this.oCols);
	    else
	        dwa.common.commonProperty.get(this.viewColumns).setValue(this.oCols);

		
	},
	observe: function(oProperty){
	    if (!oProperty.isLatest())
	        return;
	
	    if (oProperty.sName == this.viewName)
	        this.observeName(oProperty);
	    else if (oProperty.sName == this.viewColumns + '-internal')
	        this.observeLoadedDesign(oProperty);
	    else if (oProperty.sName == this.showUnreadOnly)
	        this.observeShowUnreadOnly(oProperty);
	},
	observeName: function(oProperty){
	    if (!oProperty.vValue)
	        return;
	
	    var sFolder = oProperty.vValue.split('|')[0];
	    var nSortBy = parseInt(oProperty.vValue.split('|')[1]);
	    if(this.oColCache[sFolder]) {
	        this.oCols = this.oColCache[sFolder];
	        if(!isNaN(nSortBy))
	            this.oCols.nSortBy = nSortBy;
	        if(this.readDesignEx && updateUnreadOnlyMenu)
	            dwa.common.commonProperty.get(this.updateUnreadOnlyMenu).setValue(this.oCols.bShowUnreadOnly);
	        if(this.viewColumnsExt)
	            dwa.common.commonProperty.get(this.viewColumnsExt).setValue(this.oCols);
	        else
	            dwa.common.commonProperty.get(this.viewColumns).setValue(this.oCols);
	    }
	    else
	        this.load(sFolder, nSortBy);
	},
	observeShowUnreadOnly: function(oProperty){

	}
});
