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

dojo.provide("dwa.lv.DominoReadDesign");

dojo.require("dwa.common.commonProperty");
dojo.require("dwa.lv.jsonReadDesign");
dojo.require("dwa.lv.xmlReadDesign");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.miscs");
dojo.require("dwa.common.utils");

dojo.declare(
	"dwa.lv.DominoReadDesign",
	null,
{
	url: "",
	type: "json", // "json" or "xml"
	dwa: true,

	viewColumns: "",
	viewColumnsExt: "",
	viewName: "",
	readDesignEx: true,
	showUnreadOnly: "",
	sortByFirstDateColumn: false,
	searchDefaultSortColumn: false,
	showDefaultSort: false,
	updateUnreadOnlyMenu: "",

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}

	    this.oColCache = [];
	},
	_initStore: function(){
		if(this.oDataStore)
			return;
		if(this.type == "json"){
			this.oDataStore = new dwa.lv.jsonReadDesign({
				url: this.url,
				dwa: this.dwa
			});
		}else{
			this.oDataStore = new dwa.lv.xmlReadDesign({
				url: this.url,
				dwa: this.dwa
			});
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
	load: function(sFolder, nSortBy, sFTSearch, sFormName, sDirIndex, fnLoaded ){
	    if (!sFolder)
	        return;

		this._initStore();

		var oQuery = {
			FolderName : sFolder
            ,bReadDesignEx: this.readDesignEx
			,FormName : sFormName || ( this.readDesignEx ? "s_ReadDesignEx" : "s_ReadDesign")
		};
        if( typeof sDirIndex != 'undefined' ){ oQuery.sDirIndex = sDirIndex; }
		var oRequest = {
			oRequest : oRequest
			,sProperty : this.viewColumns + '-internal'
			,sFolder : sFolder
			,nSortBy : nSortBy
			,bSortByFirstDateColumn : this.sortByFirstDateColumn
			,bSearchDefaultSortColumn : this.searchDefaultSortColumn
			,sFTSearch : sFTSearch
			,oColCache : this.oColCache
			,bShowDefaultSort : this.showDefaultSort
			,oDataStore : this.oDataStore
			,oClass : this
			,fnCallback : fnLoaded || this.loaded
			,oQuery : oQuery
		};
		this.oDataStore.load(oRequest);
	},
	loaded: function(oRequest){
	    var oViewDesignRoot = this.oDataStore.getDesignRoot(oRequest);
	    if (!oViewDesignRoot ) {
	        dwa.lv.globals.get().oStatusManager.addEntry(3, '', dwa.lv.globals.get().oStrings.sLoadingDataComplete);
	        return;
	    }
	
	    var aAllCols = this.parseDesign(oRequest, oRequest.bSoftDeleteDisabled ? 1 : 0, '', oRequest.sWho, oRequest.sAltWho, oRequest.sFolder, oRequest.bShowDefaultSort, oRequest.aShowHiddenColNames);
	    var aCols=[];
	    for( var i=0,j=0,iMax=(aAllCols?aAllCols.length:0); i<iMax; i++ ){
	        if( !aAllCols[i].bHidden )
	            aCols[j++] = aAllCols[i];
	    }
	
	    aCols.sFolder = oRequest.sFolder;
	    aCols.nDefaultSortBy = oRequest.bSearchDefaultSortColumn ? this.getFirstSortColumn(aCols) : this.getFirstDateColumn(aCols);
	    var nDefaultSortBy = -1;
	    if(oRequest.bSortByFirstDateColumn)
	        nDefaultSortBy = aCols.nDefaultSortBy;
	    aCols.nSortBy = isNaN(oRequest.nSortBy) ? nDefaultSortBy : oRequest.nSortBy;
	    aCols.sFTSearch = oRequest.sFTSearch;
	    aCols.sStartKey = '';
	    aCols.sUntilKey = '';
	    aCols.bIncludeUntilKey = false;
	    aCols.sIMColName = aAllCols.sIMColName;
	
	    aCols.bShowUnreadOnly = false;
	    var oViewDesignExRoot = this.oDataStore.getDesignExRoot(oRequest);
	    if(oViewDesignExRoot) {
	        var sNameVLColInfo = this.oDataStore.getAttributeString(oViewDesignExRoot, 'vlcolinfo_name');
	        var sVLColInfo = this.oDataStore.getAttributeString(oViewDesignExRoot, 'vlcolinfo');
	        if(sNameVLColInfo)
	            dwa.lv.miscs.getNotesListViewProfileCache().set(sNameVLColInfo, sVLColInfo);
	        
	        var sNameMLSortBy = this.oDataStore.getAttributeString(oViewDesignExRoot, 'mlsortby_name');
	        var sMLSortBy = this.oDataStore.getAttributeString(oViewDesignExRoot, 'mlsortby');
	        if(sNameMLSortBy)
	            dwa.lv.miscs.getNotesListViewProfileCache().set(sNameMLSortBy, sMLSortBy);
	        aCols.nSortBy = sMLSortBy!='' ? parseInt(sMLSortBy) : aCols.nSortBy;
	        
	        var sNameMLOptions = this.oDataStore.getAttributeString(oViewDesignExRoot, 'mloptions_name');
	        var sMLOptions = this.oDataStore.getAttributeString(oViewDesignExRoot, 'mloptions');
	        if(sNameMLOptions)
	            dwa.lv.miscs.getNotesListViewProfileCache().set(sNameMLOptions, sMLOptions);
	        aCols.bShowUnreadOnly = sMLOptions.indexOf('U')!=-1;
	
	        aCols.bIsFolder = (this.oDataStore.getAttributeString(oViewDesignExRoot, 'isfolder') == '1');
	
	        if(oRequest.sPropertyToUpdateMenu)
	            dwa.common.commonProperty.get(oRequest.sPropertyToUpdateMenu).setValue(aCols.bShowUnreadOnly);
	    }
	
	    if (aCols.nSortBy==-1 || aCols.nSortBy==aCols.nDefaultSortBy)
	        aCols.nSortBy = nDefaultSortBy;
	
	    dwa.common.commonProperty.get(oRequest.sProperty).setValue(aCols);
	},
	parseDesign: function(oRequest,bTrash,sIMColName,sWho,sAlt,sViewName,bShowDefaultSort/*=true*/,aShowHiddenColNames){
	    
	    if (typeof(bShowDefaultSort)=='undefined'){ bShowDefaultSort=true;}
	
	    var d,k,i,iMax,oDesign,aNodes,aCols,bViewSort,nSTCol=-1;
	
	    // create an array to hold a design collection
	    aCols = [];
	
	    var oDesign = this.oDataStore.getDesignRoot(oRequest);
	
	    // get rowlines attr from design element.
	    var nRowLines = this.oDataStore.getAttributeInt(oDesign, 'rowlines');
	    var bAltWhoExists = false;
	
	    // get the collection of column elements
	    aNodes = this.oDataStore.getColumns(oRequest);
	    if (!aNodes){ return; }
	
	    // get the number of columns
	    // iMax = Math.max(0,oDesign.getAttribute("columns"));
	    iMax = this.oDataStore.getLength(aNodes);
	
	
	    // k represents physical column in view
	    k = 0;
	    // d represents the xml data column
	    d = 0;
	
	    // used for tracking the first sorted column in a view
	    bViewSort = false;
	
	    if (bTrash){
	        // Define column 0 object for displaying trash icon
	        aCols[k] = { nWidth:20, bChars:false, bFixed:true, bIsIcon:true, nXmlCol:-1, bIsTrash:true };
	        k++;
	    }
	    
	    var nColWho = -1;
	    var nColAlt = -1;
	    
	    for (i=0; i < iMax; i++){
	        var colDesign = this.oDataStore.getItem(aNodes, i);
	        var bSortA    = false;
	        var bSortD    = false;
	        var bFixed    = true;
	        var bIsIcon   = false;
	        var nWidth    = 0;
	        var bChars    = true;
	        var sTitle    = "";
	        var sHiddenTitle = "";
	        var nFormat   = 2;
	        var bTwistie  = false;
	        var iColSort  = 0;
	        var iViewSort = 0;
	
	        // resizeable?
	        bFixed = !this.oDataStore.getAttributeBoolean(colDesign, "resize");
	
	        // width?
	        nWidth = this.oDataStore.getAttributeInt(colDesign, "width");
	        // If col width is zero and column is fixed, treat it as hidden
	        if (0 == nWidth && bFixed){ 
	            d++; continue;
	        }
	        else {
	            // "Designer" allows the user to specify the number of characters
	            //  each view column should display.  this info is translated to
	            //  pixels by the Domino XML generator.  The code below is an attempt
	            //  to recreate the intended char width specified by the designer.
	            // XML design data seems to indicate a size of 8 pixels per character
	            nWidth = Math.ceil(nWidth/8);
	        }
	
	        // column title
	        sTitle = this.oDataStore.getAttributeString(colDesign, "title");
	        sHiddenTitle = this.oDataStore.getAttributeString(colDesign, "hiddentitle");
	
	        // icon?
	        bIsIcon = this.oDataStore.getAttributeBoolean(colDesign, "icon");
	        // icons are fixed width even if it says we can resize
	        //  -> unless the column has a title
	        if (bIsIcon && 0==sTitle.length) {
	            // #YHAO6MG627: it set icon column size when notes column design over D_IconColumnWidth.
	            bFixed=true;
	            nWidth=Math.max(this.oDataStore.getAttributeInt(colDesign, "width"), 20);
	            bChars=false;
	        }
	
	        // resortable?
	        bSortA = this.oDataStore.getAttributeBoolean(colDesign, "resortascending");
	        bSortD = this.oDataStore.getAttributeBoolean(colDesign, "resortdescending");
	        if (bSortA && bSortD) iColSort = 5;
	        else if (bSortA)      iColSort = 2;
	        else if (bSortD)      iColSort = 1;
	
	        // first sorted column in view
	        //  If the column can be resorted, make sure that the resort indication makes sense.
	        if (!bViewSort){
	            // is this column part of the view's primary sort?
	            bViewSort = this.oDataStore.getAttributeBoolean(colDesign, "sort");
	
	            // overwrite sort value for some special columns
	            // YCDL78VA6D:  draft does not sort the column "date" ascending as the preference set.
	            if(sViewName == "($Drafts)" && this.oDataStore.getAttributeString(colDesign, "name") == '$86')
	                bViewSort = false;
	
	            if (bViewSort){
	                // ascending or descending?
	                if (this.oDataStore.getAttributeBoolean(colDesign, "sortdescending"))
	                    iViewSort = 1;
	                else
	                    iViewSort = 2;
	                    
	                // do we need to fix up the "resort" indication?
	                if (iColSort==iViewSort){
	                    // some knucklehead set the view sorting to "ascending", 
	                    //  and then set the "click to resort" to "ascending" also.
	                    //  (this only matters for the first column)
	                    //  attempt to fix the mistake
	                    //  the designer really meant to resort descending ... opposite of the view's sort
	                    iColSort=2==iColSort?4:3;
	                }
	                else
	                // do we need to show a sort indication?
	                if (0==iColSort && bShowDefaultSort){
	                    iColSort=iViewSort;
	                }
	            }
	        }
	
	        // store attribs in visible & exposed hidden column array
	        aCols[k] = {
	            nWidth:nWidth
	            ,bChars:bChars
	            ,bFixed:bFixed
	            ,bIsIcon:bIsIcon
	            ,nXmlCol:parseInt(this.oDataStore.getAttributeString(colDesign, "columnnumber"))
	            ,bSort:iColSort
	            ,sTitle:sTitle
	            ,sHiddenTitle:sHiddenTitle
	            ,sName:this.oDataStore.getAttributeString(colDesign, "name")
	            ,iViewSort:iViewSort
	            ,nDigits:0
	            ,bIsName:this.oDataStore.getAttributeBoolean(colDesign, "isname")
	            ,bExtend:this.oDataStore.getAttributeBoolean(colDesign, "extendcolwidth")
	            ,sNumFormat:''
	            ,bBytes:false
	            ,bOmitThisYear:false
	
	            ,bHidden:this.oDataStore.getAttributeBoolean(colDesign, "hidden")
	            ,bTwistie:this.oDataStore.getAttributeBoolean(colDesign, "twistie")
	            ,nFormat:this.oDataStore.getAttributeInt(colDesign, "format")
	            ,bResponse:this.oDataStore.getAttributeBoolean(colDesign, "response")
	            ,sNarrowDisplay:this.oDataStore.getAttributeString(colDesign, "narrowdisplay")
	            ,nSequenceNumber:isNaN(parseInt(this.oDataStore.getAttributeString(colDesign, "sequence"))) ? 1 : parseInt(this.oDataStore.getAttributeString(colDesign, "sequence"))
	            ,bBeginWrapUnder:this.oDataStore.getAttributeBoolean(colDesign, "beginwrapunder")
	        };
	
	        if(aShowHiddenColNames && dwa.common.utils.indexOf(aShowHiddenColNames, aCols[k].sName)!=-1 && aCols[k].bHidden)
	            aCols[k].visible = true;
	
	        if(sWho == aCols[k].sName)
	            nColWho = k;
	        if(sAlt == aCols[k].sName)
	            nColAlt = k;
	
	        // overwrite hidden value for some special columns
	        if(sViewName == "$ThreadsEmbed" && aCols[k].sName == '$ThreadsEmbed')
	            aCols[k].bHidden = true;
	
	        // If name column, look for two other attributes
	        if( dwa.lv.globals.get().oSettings.bLiveNames){
	            // With DWA we show status presently only on leftmost column so as not to
	            //  interfere with selection
	            if( sIMColName && (sIMColName == aCols[k].sName || sIMColName == aCols[k].sTitle) ){
	                aCols[k].sIMColName = sIMColName;
	            }
	            else if( this.oDataStore.getAttributeBoolean(colDesign, "imstatus") ){
	                var s = this.oDataStore.getAttributeString(colDesign, "imcolumnname");
	                aCols[k].sIMColName = s ? s : aCols[k].sName;
	                sIMColName = aCols[k].sIMColName;
	            }
	        }
	
	        // overwrite format value for some special columns
	        var oMap = {
	            "($Follow-Up)": '$114',
	            "($SoftDeletions)": '$107',
	            "Threads": '$13'
	        };
	
	        for (var s in oMap) {
	            if (sViewName == s && aCols[k].sName == oMap[s]) {
	                aCols[k].nFormat = 3;
	                break;
	            }
	        }
	
	        if (aCols[k].nFormat){
	            // For date-time columns, retrieve the date-time format
	            // now always try to get the date-time format because the Time column has the format=2
	            // -waiho 5/1/03
	            var oDTFmtNode = this.oDataStore.getDatetimeFormat(colDesign);
	            var sDTFmt = this.oDataStore.getAttributeString(oDTFmtNode, "show");
	            aCols[k].nDTFmt = (sDTFmt == "datetime" ? 7 :0);
	            aCols[k].bTimeOnly = (sDTFmt == "time" ? true: false);
	            aCols[k].bOmitThisYear = dwa.lv.globals.get().oSettings.bOmitThisYear && this.oDataStore.getAttributeBoolean(oDTFmtNode, "omitthisyear");
	        }
	        
	        // append NUMBER format ... needed for SIZE column
	        var oFmtNode = this.oDataStore.getNumberFormat(colDesign);
	        var sFmt    = this.oDataStore.getAttributeString(oFmtNode, "digits");
	        var bVaries = this.oDataStore.getAttributeBoolean(oFmtNode, "varying");
	        if( bVaries )
	            aCols[k].nDigits = -1;
	        else
	            aCols[k].nDigits = parseInt(sFmt,10);
	        
	        aCols[k].sNumFormat = this.oDataStore.getAttributeString(oFmtNode, "format");
	        aCols[k].bBytes = this.oDataStore.getAttributeBoolean(oFmtNode, "bytes");
	
	        this.parseSpecialAttrs(aCols[k], colDesign);
	
	        // overwrite special attributes for some columns
	        if(sViewName == "Threads") {
	            switch(aCols[k].sName) {
	            case '$86':
	                aCols[k].nHeaderIcon = 130;
	                aCols[k].bShowGradientColor = true;
	                break;
	            case '$Importance':
	                aCols[k].nHeaderIcon = 150;
	                aCols[k].bAlignGradientColor = true;
	                aCols[k].bThinColumn = true;
	                aCols[k].bFixed = true;
	                aCols[k].nWidth = 6;
	                aCols[k].bChars = false;
	                break;
	            case '$ToStuff':
	                aCols[k].nHeaderIcon = 184;
	                break;
	            case '$32':
	                aCols[k].nHeaderIcon = 5;
	                break;
	            case '$109':
	                aCols[k].nHeaderIcon = 182;
	                break;
	            }
	        }
	
	        // change response column to normal hidden column to use Notebook view as flat view
	        if(sViewName == "($Journal)") {
	            switch(aCols[k].sName) {
	            case '$51':
	                aCols[k].hide = true;
	                aCols[k].bResponse = false;
	                aCols[k].bTwistie = false;
	                break;
	            case '$52':
	                aCols[k].bTwistie = false;
	                break;
	            }
	        }
	
	        // advance column counter
	        k++;
	    }
	
	    if(nColWho != -1 && nColAlt != -1)
	    {
	        aCols[nColWho].hide = true;
	        aCols[nColAlt].visible = true;
	        bAltWhoExists = true;
	    }
	
	    // Remove awareness flag from secondary awareness column for Junk Mail view
	    var bIsFirstIMCol = true;
	    for(var i=0;sIMColName && i<aCols.length;i++) {
	        if(aCols[i].sIMColName == sIMColName && aCols[i].sName != sIMColName && !aCols[i].bHidden) {
	            if(!bIsFirstIMCol)
	                aCols[i].sIMColName = '';
	            bIsFirstIMCol = false;
	        }
	    }
	
	    for( var i=0,nHidden=0,nIMCol=0,nHide=0,v=0,iMax=aCols.length; i<iMax; i++ ){
	        var nOrgHidden = aCols[i].bHidden;
	        if( ((!aCols[i].bHidden || aCols[i].visible) && !aCols[i].hide) || aCols[i].bUnhideWhenWrapped ){
	            aCols[i].bHidden = false;
	            if(nHidden) aCols[i].nXmlCol -= nHidden;
	            // nSortCol property to keeps track of the available XML columns for sorting purposes (Next/Previous navigation)
	            aCols[i].nSortCol = aCols[i].nXmlCol; // for Navigate
	            aCols[i].nSortColForPrint = aCols[i].nSortCol - nHide; // for Print
	            if(aCols[i].bUnhideWhenWrapped) {
	                nHide++;
	                nHidden++;
	            }
	        }
	        else
	        {
	            aCols[i].bHidden = true;
	            // We're eliminating this hidden column (need to fixup the nXmlCol value for
	            //  all following columns)
	            if( aCols[i].sName == sIMColName && dwa.lv.globals.get().oSettings.bLiveNames ) // for Navigation
	                // never used. just to not increment nHidden
	                nIMCol++;
	            else if(aCols[i].hide) // for Print
	                nHide++;
	            else
	                nHidden++;
	        }
	    }
	    aCols.nRowLines = nRowLines;
	    aCols.sIMColName = sIMColName;
	    aCols.bAltWhoExists = bAltWhoExists;

	    return aCols;
	},
	parseSpecialAttrs: function(oCol, oNode){
	    var sAttr = (this.oDataStore.getAttributeString(oNode, "attrs")).replace(/\s/g, '');
	    var asMatch = sAttr.match(/(^|,)\$TypeHeaderIcon=([^,]+)(,|$)/);
	    var sHeaderIcon = asMatch && asMatch[2];
	    if(sHeaderIcon) {
	        var aValues = sHeaderIcon.split(':');
	        var nNum = parseInt(aValues[2]);
	        if(aValues[1]=='colicon1' && !isNaN(nNum))
	            oCol.nHeaderIcon = nNum + 1; //convert 0 base to 1 base
	    }
	
	    var bThinColumn = !!sAttr.match(/(^|,)\$ThinColumn=true(,|$)/);
	    if (bThinColumn && 0==oCol.sTitle.length) {
	        oCol.bThinColumn = true;
	        oCol.bFixed = true;
	        oCol.nWidth = 6;
	        oCol.bChars = false;
	    }
	
	    oCol.bShowGradientColor = !!sAttr.match(/(^|,)\$ShowGradientColor=true(,|$)/i);
	    oCol.bAlignGradientColor = !!sAttr.match(/(^|,)\$AlignGradientColor=true(,|$)/);
	    oCol.bUnhideWhenWrapped = !!sAttr.match(/(^|,)\$UnhideWhenWrapped=true(,|$)/);
	},
	getFirstDateColumn: function(aCols){
	    // set a global var for the first date column
	    if(aCols){
	        if(window.s_ViewName=="Threads"){
	            for(var i=0,i$=aCols.length;i<i$;i++){
	                if(0!=aCols[i].iViewSort){
	                    aCols[i].nFormat = 3;
	                    return i;
	                }
	            }
	        }else{
	            for(var i=0,i$=aCols.length;i<i$;i++){
	                if(1==aCols[i].nFormat)return i;
	            }
	        }
	    }
	    return -1;
	},
	getFirstSortColumn: function(aCols){
	    // set a global var for the first sort column
	    if(aCols){
	        for(var i=0,i$=aCols.length;i<i$;i++)
	            if(aCols[i].bSort != 0)return i;
	    }
	    return -1;
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
	        if(this.readDesignEx && this.updateUnreadOnlyMenu)
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
	    this.oCols.bShowUnreadOnly = oProperty.vValue;
	    
	    var sFldName = "MLOptions" + this.oCols.sFolder.replace(/[  $\(\)]/g,'_');
	    var sValue = dwa.lv.miscs.getNotesListViewProfileCache().get(sFldName);
	    sValue = sValue.replace(/U/g, '') + (this.oCols.bShowUnreadOnly ? 'U' : '');
	    dwa.lv.miscs.getNotesListViewProfileCache().set(sFldName, sValue, true, this.sId);
	    
	    if(this.readDesignEx && this.updateUnreadOnlyMenu)
	        dwa.common.commonProperty.get(this.updateUnreadOnlyMenu).setValue(this.oCols.bShowUnreadOnly);
	    
	    if(this.viewColumnsExt)
	        dwa.common.commonProperty.get(this.viewColumnsExt).setValue(this.oCols);
	    else
	        dwa.common.commonProperty.get(this.viewColumns).setValue(this.oCols);
	}
});
