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

dojo.provide("dwa.lv.treeViewEntry");

dojo.require("dwa.lv.treeViewCollection");
dojo.require("dwa.lv.colpos");

dojo.declare(
	"dwa.lv.treeViewEntry",
	null,
{
	indexForDummy: true,

	constructor: function(oRoot, oXmlEntry, oDataStore, sTumbler){
	    if(!oRoot)
	    {
	        this.bIsRoot=1;
	        this.clearIndexTable();
	        this.clearIndexCache();
	        this.clearTumblerCache();
	        this.clearNoteIdCache();
	        this.clearUnidCache();
	        this.setAllExpanded(false);
	    }
	    else{
	        this.bIsRoot=0;
	        this.oRoot=oRoot;
	    }
	
	    this.aChilds = [];
	    this.aExpandedChilds = [];
	    this.oCollection = new dwa.lv.treeViewCollection();
	    this.oIgnoreCollection = new dwa.lv.treeViewCollection();
	    this.oDataStore = oDataStore;
	
	    this.setChildLoaded(false);
	    this.setXmlNode(oXmlEntry);
	
	    if(sTumbler)
	        this.sTumbler = sTumbler;
	
	    if(!this.isRoot())
	        this.appendToParent();
	
	    this.expand(this.getAllExpanded());
	},
	sEntryPrefixStatic: 'S',
	sIndexPrefixStatic: 'I',
	sTumblerPrefixStatic: 'T',
	sImgExpandStatic: 'twistyopenup.gif',
	// remove the value not to call dojo.isBodyLtr() toofast,, sImgCollapseStatic: (dojo._isBodyLtr() ? "twistycloseup.gif" : "twistycloseupBidi.gif"),
	getRoot: function(){
	    return (this.bIsRoot? this : this.oRoot);
	},
	isRoot: function(){
	    return this.bIsRoot;
	},
	setListMode: function(){
	    this.getRoot().bListMode = true;
	},
	getListMode: function(){
	    return !!this.getRoot().bListMode;
	},
	setAllExpanded: function(bAllExpanded){
	    this.getRoot().bAllExpanded = !!bAllExpanded;
	},
	getAllExpanded: function(){
	    return !!this.getRoot().bAllExpanded;
	},
	setChildLoaded: function(bIsChildLoaded){
	    this.bIsChildLoaded = bIsChildLoaded;
	},
	getChildLoaded: function(){
	    return this.bIsChildLoaded;
	},
	findEntry: function(nIndex){
	    return this.oCollection.find(nIndex);
	},
	hide: function(bHide){
	    if(typeof(bHide)=='undefined')
	        bHide = true;
	    var sParentTumbler = dwa.lv.colpos.getParent(this.getTumbler());
	    var oParent = this.getEntryByTumbler(sParentTumbler);
	    var nIndex = dwa.lv.colpos.getLeafIndex(this.getTumbler());
	    if(bHide)
	        oParent.oIgnoreCollection.add(nIndex);
	    else
	        oParent.oIgnoreCollection.remove(nIndex);
	},
	isHidden: function(){
	    var sParentTumbler = dwa.lv.colpos.getParent(this.getTumbler());
	    var oParent = this.getEntryByTumbler(sParentTumbler);
	    var nIndex = dwa.lv.colpos.getLeafIndex(this.getTumbler());
	    return oParent.oIgnoreCollection.find(nIndex);
	},
	applyViewEntries: function(){
	  return this.applyXmlEntries(arguments);
	},
	applyXmlEntries: function(sStartTumbler, nCount, oRequest, bUseStartKey, bIsReverse){
	    if(!this.isRoot())
	        return this.getRoot().applyXmlEntries(sStartTumbler, nCount, oRequest);
	
	    
	    var bIsFirstTime = !this.getChildLoaded();
	    var oViewEntries = this.oDataStore.getViewEntriesRoot(oRequest);
	    if(oRequest)
	    {
	        if(bIsFirstTime)
	        {
	            var sRangeEntries = this.oDataStore.getAttribute(oViewEntries, 'rangeentries') + '';
	            
	            // rangeentries should be handled if url have startKey.
	            // should ignore when full text search.
	            if(bUseStartKey && sRangeEntries && sRangeEntries != 'null' && sRangeEntries != 'undefined')
	            {
	                this.setNumChilds(parseInt(sRangeEntries,10));
	            }
	            else
	            {
	                var sTopLevelEntries = this.oDataStore.getAttribute(oViewEntries, 'toplevelentries') + '';
	                this.setNumChilds(parseInt(sTopLevelEntries,10));
	            }
	        }

            if( !sStartTumbler ){
                var sParentStartTumbler = "";
                var oStart = "start";
            }else{
    	        var sParentStartTumbler = dwa.lv.colpos.getParent(sStartTumbler);
    	        var oStart = (sStartTumbler == '1') ? 'start' : sStartTumbler;
            }
	        var oStartOrg = oStart;
	
	        var aEntries = this.oDataStore.getViewEntries(oRequest);

            imax = aEntries ? this.oDataStore.getLength(aEntries) : -1;

	        for(var i=0,j=imax-1;i<imax;j--,i++)
	        {
	            var oEntry = this.oDataStore.getItem(aEntries, bIsReverse?j:i);
	            var sTumbler = this.oDataStore.getAttribute(oEntry, 'position') + '';
	            var sParentTumbler = dwa.lv.colpos.getParent(sTumbler);
	
	            if(sParentTumbler != sParentStartTumbler && !dwa.lv.colpos.contain(sParentStartTumbler, sParentTumbler))
	                this.setAllExpanded(true);
	
	            if(sTumbler)
	            {
	                var oTreeViewEntry = null;
	                
	                if(!this.getEntryByTumbler(sTumbler))
	                {
	                    // KYOE7A3B7B : setStartIndex first sinse new dwa.lv.treeViewEntry() changes NumEntries if the entry is out of range unexpectedly.
	                    if(bUseStartKey && bIsFirstTime && i==0)
	                        this.setStartIndex(dwa.lv.colpos.getLeafIndex(sTumbler));
	                    oTreeViewEntry = new dwa.lv.treeViewEntry(this.getRoot(), oEntry, this.oDataStore);
	                }
	                else
	                {
	                    oTreeViewEntry = this.getEntryByTumbler(sTumbler);
	                    if(oTreeViewEntry.isDummy())
	                        oTreeViewEntry.setXmlNode(oEntry);
	                }
	                
	                if(i==0 && ((!bIsReverse && oStart=='last') || bIsReverse))
	                {
	                    if(this.oDataStore.getLength(aEntries) < nCount)
	                        this.ignoreEntriesRecursive('start', oTreeViewEntry);
	                }
	                else
	                {
	                    this.ignoreEntriesRecursive(oStart, oTreeViewEntry);
	                }
	
	                oStart = oTreeViewEntry;
	            }
	        }
	        if(bIsReverse) {
	            this.ignoreEntriesRecursive(oStart, oStartOrg);
	        }
	        else
	        {
	            if(imax < nCount || sStartTumbler=='last' /* to ignore entries for ftsearch */){
	                this.ignoreEntriesRecursive(oStart, 'last');
                }
	        }
	    }
	    
	},
	ignoreEntriesRecursive: function(oStartEntry, oEndEntry){
	    var bStartIsTop = oStartEntry == 'start';
	    var sStartTumbler = bStartIsTop ? 'start' : (oStartEntry.getTumbler ? oStartEntry.getTumbler() : oStartEntry);
	    var bEndIsLast = oEndEntry == 'last';
	    var sEndTumbler = bEndIsLast ? 'last' : (oEndEntry.getTumbler ? oEndEntry.getTumbler() : oEndEntry);
	
	    if(dwa.lv.colpos.compare(sStartTumbler, sEndTumbler) != 1)
	        return;
	    
	    if(dwa.lv.colpos.contain(sEndTumbler, sStartTumbler))
	    {
	        // goto child
	        var sParentTumbler = sStartTumbler;
	        var oParent = this.getEntryByTumbler(sParentTumbler);
	        var nStart = oParent.getStartIndex();
	        var nEnd = bEndIsLast ? oParent.getEndIndex() : dwa.lv.colpos.getLeafIndexWithParent(sParentTumbler, sEndTumbler) - 1;
	        var nCount = nEnd - nStart + 1;
	        if(nCount > 0)
	            oParent.oIgnoreCollection.add(nStart, nEnd);
	        if(!bEndIsLast){
                var oNewStartEntry = oParent.getChildEntry(dwa.lv.colpos.getLeafIndexWithParent(sParentTumbler, sEndTumbler));
                if( dwa.lv.colpos.getParent(sEndTumbler) != sParentTumbler ){
                    if( oNewStartEntry.isDummy() ) oNewStartEntry.setIgnoredDummy( true );
                }
	            this.ignoreEntriesRecursive(oNewStartEntry, oEndEntry);
            }
	    }
	    else
	    {
	        // goto parent or same level
	        var sParentTumbler = dwa.lv.colpos.getParent(sStartTumbler);
	        var oParent = this.getEntryByTumbler(sParentTumbler);
	        var nStart = bStartIsTop ? oParent.getStartIndex() : (dwa.lv.colpos.getLeafIndex(sStartTumbler) + (oStartEntry.getTumbler ? 1 : 0));
	        if(dwa.lv.colpos.contain(sEndTumbler, sParentTumbler))
	        {
	            // same level moving
	            var nEnd = bEndIsLast ? oParent.getEndIndex() : (dwa.lv.colpos.getLeafIndexWithParent(sParentTumbler, sEndTumbler) - (oEndEntry.getTumbler ? 1 : 0));
	            var nCount = nEnd - nStart + 1;
	            if(nCount > 0)
	                oParent.oIgnoreCollection.add(nStart, nEnd);
	            var oNewStartEntry = oParent.getChildEntry(dwa.lv.colpos.getLeafIndexWithParent(sParentTumbler, sEndTumbler));
	            if(!bEndIsLast && oNewStartEntry){
                    if( dwa.lv.colpos.getParent(sEndTumbler) != sParentTumbler ){
                        if( oNewStartEntry.isDummy() ) oNewStartEntry.setIgnoredDummy( true );
                    }

	                this.ignoreEntriesRecursive(oNewStartEntry, oEndEntry);
                }
	        }
	        else
	        {
	            // goto parent
	            if(!oParent)
	                return;
	            var nEnd = oParent.getEndIndex();
	            var nCount = nEnd - nStart + (sStartTumbler=='1' ? 0 : 1);
	            if(nCount > 0)
	                oParent.oIgnoreCollection.add(nStart, nEnd);
	            this.ignoreEntriesRecursive(oParent, oEndEntry);
	        }
	    }
	},
	ignoreEntries: function(sTumbler, nCount){
	    var oParent = this.getEntryByTumbler(dwa.lv.colpos.getParent(sTumbler));
	    if(!oParent) return;
	    var nStart = dwa.lv.colpos.getLeafIndex(sTumbler);
	    var nEnd = nStart + nCount - 1;
	    if(nStart < oParent.getStartIndex())
	        nStart = oParent.getStartIndex();
	    if(nEnd > oParent.getEndIndex())
	        nEnd = oParent.getEndIndex();
	    if(nEnd - nStart >= 0)
	        oParent.oIgnoreCollection.add(nStart, nStart + nCount - 1);
	},
	getNextIgnoreEntries: function(nIndex){
	    return this.oIgnoreCollection.getNext(nIndex);
	},
	getNextExpandedChild: function(nCurrIndex){
	    if(!this.aExpandedChilds)
	        this.aExpandedChilds = [];
	
	    this.aExpandedChilds.sort(function(a,b){return a-b});
	
	    var nIndex = void(0);
	    for(var i=0,imax=this.aExpandedChilds.length;i<imax;i++)
	        if(this.aExpandedChilds[i] >= nCurrIndex)
	        {
	            nIndex = this.aExpandedChilds[i];
	            break;
	        }
	
	    return nIndex;
	},
	setChildExpanded: function(oChild){
	    var oParent = oChild.getParent();
	    if(oParent != this)
	        return;
	
	    if(!this.aExpandedChilds)
	        this.aExpandedChilds = [];
	
	    var nIndex = dwa.lv.colpos.getLeafIndex(oChild.getTumbler());
	    for(var i=this.aExpandedChilds.length-1;i>=0;i--)
	        if(this.aExpandedChilds[i] == nIndex)
	            this.aExpandedChilds.splice(i, 1);
	
	    if(oChild.isExpanded())
	        this.aExpandedChilds.push(nIndex);
	},
	expand: function(bExpand){
	    if(!this.hasChildren() && !this.getListMode())
	        return;
	
	    this.bIsExpanded = typeof bExpand != 'undefined' ? bExpand : !this.isExpanded();
	
	    if(this.getParent())
	        this.getParent().setChildExpanded(this);
	},
	isExpanded: function(){
	    return (this.hasChildren() || this.getListMode()) && this.bIsExpanded;
	},
	getParent: function(){
	    return this.oParent;
	},
	setXmlNode: function(oEntry){
	    if(oEntry)
	    {
	        this.oXmlEntry  = oEntry;
	        this.sTumbler   = this.oDataStore.getAttribute(oEntry, 'position') + '';
	        this.sNoteId    = this.oDataStore.getAttribute(oEntry, 'noteid') + '';
	        if(this.sNoteId == 'null')
	            this.sNoteId = null;
	        if(this.sNoteId)
	            this.applyNoteIdCache(this.sNoteId, this);
	        this.sUnid      = this.oDataStore.getAttribute(oEntry, 'unid') + '';
	        if(this.sUnid == 'null' || this.sUnid == 'undefined')
	            this.sUnid = null;
	        if(this.sUnid)
	            this.applyUnidCache(this.sUnid, this);
	
	
	        var sChildren   = this.oDataStore.getAttribute(oEntry, 'children') + '';
	        var nChildren   = this.getRoot().getListMode() ?
	                            0 :
	                            ((sChildren && sChildren != 'null') ?
	                                ((isNaN(parseInt(sChildren,10)) || dwa.lv.colpos.getDepth(this.sTumbler)>=31) ?
	                                    0 :
	                                    parseInt(sChildren,10))
	                                :
	                                0
	                            );
	        this.setStartIndex(1);
	        this.setNumChilds(nChildren);
	        this.setNumVisibleDescendants(0);
	        
	        this.bHasChildren = nChildren ? true : false;
	        this.bIsDummy = false;
	    }
	    else
	    {
	        this.oXmlEntry = null;
	        this.sTumbler = '';
	        this.setStartIndex(1);
	        this.setNumChilds(0);
	        this.setNumVisibleDescendants(0);
	        this.bHasChildren = false;
	        this.bIsDummy = (this.isRoot() ? false : true);
	    }
	},
    setIgnoredDummy: function( bIsIgnoredDummy ){
        this.bIsIgnoredDummy = bIsIgnoredDummy;
    },
    isIgnoredDummy: function(){
        return !!this.bIsIgnoredDummy;
    },
	getXmlNode: function(oXmlEntry){
	    return this.oXmlEntry;
	},
	getViewEntry: function(oXmlEntry){
	    return this.getXmlNode(oXmlEntry);
	},
	getTumbler: function(){
	    return this.sTumbler;
	},
	getUnid: function(){
	    return this.sUnid;
	},
	getNoteId: function(){
	    return this.sNoteId;
	},
	hasChildren: function(){
	    return this.bHasChildren || this.isDummy();
	},
	appendToParent: function(){
	    if(this.isRoot())
	        return;
	
	    var sParentTumbler = dwa.lv.colpos.getParent(this.getTumbler());
	    var oParent = this.getEntryByTumbler(sParentTumbler);
	    
	    if(!oParent)
	    {
	        oParent = new dwa.lv.treeViewEntry(this.getRoot(), null, this.oDataStore, sParentTumbler);
	        oParent.sTumbler = sParentTumbler;
	        this.setAllExpanded(true);
	        oParent.setNumChilds(dwa.lv.colpos.getLeafIndex(this.getTumbler()));
	    }
	    
	    this.oParent=oParent;
	    this.applyTumblerCache(this.getTumbler(), this);
	
	    oParent.appendChild(dwa.lv.colpos.getLeafIndex(this.getTumbler()), this);
	    if(this.getAllExpanded() || oParent.isDummy())
	        oParent.expand(true);
	},
	appendChild: function(index, oEntry){
	    this.aChilds[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + index] = oEntry;
	    this.oCollection.add(index);
	//  if((this.isDummy() || this.getRoot().getListMode()) && index > this.getEndIndex())
	    if(index > this.getEndIndex())
	        this.setNumChilds(index - this.getStartIndex() + 1);
	    this.bHasChildren = true;
	    this.setChildLoaded(true);
	},
	showAll: function(){
		var nTotalEntries = this.getTotalEntries();

		this.oIgnoreCollection.showAll();

		for( var ii = 0; ii < nTotalEntries; ii++ ){
			var stb = this.getTumblerByIndex(ii);
			var ety = this.getEntryByIndex(ii);

			var state = (!ety ? 0 : (ety.isDummy() ? (ety.isIgnoredDummy() ? -2 : -1) : 1));

			console.log( "sTumbler[" + ii + "] = " + stb + ", state = " + state );
            if( ety ) ety.oIgnoreCollection.showAll();
		}


	},
	getRequestRangeEx: function( oInvalidateInfo, nChunkCount, bUp, sCollapsedTumbler){
		var nStart = oInvalidateInfo.iStart;
		var nEnd = oInvalidateInfo.iStart + oInvalidateInfo.nCount - 1;
		var nMax = this.getTotalEntries() - 1;
		if( nEnd > nMax ) nEnd = nMax;

		var oRet = {};

		console.log( "getRequestRangeEx: start=" + nStart + ", count=" + nChunkCount + ", up=" + bUp + ", coldpos=" + sCollapsedTumbler );
//this.showAll();

		for( var nLoadStart = nStart; nLoadStart <= nEnd; nLoadStart++ ){
			var entry = this.getEntryByIndex(nLoadStart, true);
			if( !entry ) break;
		}
		if( nLoadStart > nEnd ) return {};
		for( var nLoadEnd = nEnd; nLoadEnd > nLoadStart; nLoadEnd-- ){
			var entry = this.getEntryByIndex(nLoadEnd, true);
			if( !entry ) break;
		}

		console.log( "getRequestRangeEx: loadstart=" + nLoadStart + ", loadend=" + nLoadEnd );

		if( bUp ){
			if( nLoadEnd == nEnd && nLoadStart > nStart ){
				oRet.nStart = Math.max( nLoadStart - 1, 0);
			}else{
				oRet.nStart = Math.min(nLoadEnd + 1, nEnd);
				oRet.bNavigateReverse = true;
			}
		}else{
			if( nLoadStart == nStart && nLoadEnd < nEnd ){
				oRet.nStart = Math.min(nLoadEnd + 1, nEnd);
				oRet.bNavigateReverse = true;
			}else{
				oRet.nStart = Math.max( nLoadStart - 1, 0);
			}
		}

		oRet.nCount = Math.max( nLoadEnd - nLoadStart + 2, nChunkCount );

		var pos = this.getTumblerByIndex(oRet.nStart)
		if( !bUp && !oRet.bNavigateReverse ){
			// check collapsed entries to be skipped from the loading targets
			var entry = this.getEntryByTumbler( pos, true );
			if( entry && entry.hasChildren() ){
				var lastChildNumber = entry.oCollection.max();
				if( lastChildNumber ){
					// the entry is collapsed...
					pos = pos + '.' + (lastChildNumber + 1);
				}else /*if( !entry.getChildLoaded() )*/ {
					// check initially collapsed entries
					pos = pos + '.' + entry.getStartIndex();
				}
			}

            // move the starting position to the next sibling of the collapsed row
            if( sCollapsedTumbler && dwa.lv.colpos.contain( pos, sCollapsedTumbler ) ){
                pos = dwa.lv.colpos.getNext( sCollapsedTumbler );
            }
		}
		oRet.position = pos;

		console.dir( oRet );
		return oRet;
	},
	getRequestRange: function(nStart, nCount, bUp){
console.log( "getRequestRange: start=" + nStart + ", count=" + nCount );
	    var nEnd = nStart + nCount - 1;
	    var oRet = {nStart:nStart, nEnd:nEnd, bNavigateReverse:false};
	
	    for(var i=nStart;i<=nEnd;i++)
	    {
	        oRet.nStart = i;
	        if(!this.oCollection.find(i) || !this.getChildEntry(i) || this.getChildEntry(i).isDummy())
	            break;
	    }
	
	    if(nEnd > this.getEndIndex())
	        nEnd = this.getEndIndex();

	    for(var i=nEnd;i>=nStart;i--)
	    {
	        oRet.nEnd = i;
	        if(!this.oCollection.find(i) || !this.getChildEntry(i) || this.getChildEntry(i).isDummy())
	            break;
	    }
	
	    if(oRet.nStart < this.getStartIndex())
	        oRet.nStart = this.getStartIndex();
	    if(oRet.nEnd > this.getEndIndex())
	        oRet.nEnd = this.getEndIndex();
	
	    if(oRet.nStart > oRet.nEnd)
	        return null;
	
	    var nNewStart = oRet.nEnd - nCount + 1;
	    if(nNewStart < this.getStartIndex())
	        nNewStart = this.getStartIndex();
	
	    for(var i=nStart;i>=nNewStart;i--)
	    {
	        if(this.oCollection.find(i) && this.getChildEntry(i) && !this.getChildEntry(i).isDummy())
	            break;
	        oRet.nStart = i;
	    }
	
	    var bPrevExist = bNextExist = false;
	    if(oRet.nStart == this.getStartIndex() || this.oCollection.find(oRet.nStart-1) || this.oIgnoreCollection.find(oRet.nStart-1) /* found in ignore collection means there is previous entry */)
	        bPrevExist = true;
	    if(oRet.nEnd == this.getEndIndex() || this.oCollection.find(oRet.nEnd+1) || this.oIgnoreCollection.find(oRet.nEnd-1) /* found in ignore collection means there is next entry */)
	        bNextExist = true;
	    oRet.bNavigateReverse = (!bPrevExist && bNextExist) || (bUp && bPrevExist && bNextExist);

console.dir( oRet );
	    return oRet;
	},
	getChildEntry: function(index){
	    var oEntry = this.aChilds[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + index];
	    return oEntry ? oEntry : null;
	},
	setStartIndex: function(nStart){
	    this.nStart = nStart;
	},
	getStartIndex: function(){
	    return this.nStart;
	},
	getEndIndex: function(){
	    return this.getStartIndex() + this.getNumChilds() - 1;
	},
	setNumChilds: function(nTotal){
	    this.nTotal = nTotal;
	},
	getNumChilds: function(){
	    return this.nTotal;
	},
	setNumVisibleDescendants: function(nVisibleDescendants){
	    this.nVisibleDescendants = nVisibleDescendants;
	},
	getNumVisibleDescendants: function(){
	    return this.nVisibleDescendants;
	},
	getTotalEntries: function(){
	    return this.getNumVisibleDescendants();
	},
	setDepth: function(nDepth){
	    this.nDepth = nDepth;
	},
	getDepth: function(){
	    return this.nDepth;
	},
	clearIndexCache: function(){
	    this.getRoot().aIndexCache = {};
	},
	clearIndexTable: function(){
	    this.getRoot().aIndexTable = [];
	},
	applyIndexTable: function(nIndex, sParentTumbler, nChildIndex){
	    if(!this.isRoot())
	        return this.getRoot().applyIndexTable(nIndex, sParentTumbler, nChildIndex);
	
	    if(typeof nIndex == 'undefined' || typeof sParentTumbler == 'undefined' || typeof nChildIndex == 'undefined')
	        return;
	
	    this.aIndexTable.push({nIndex:nIndex, sParentTumbler:sParentTumbler, nChildIndex:nChildIndex});
	},
	update: function(){
	    if(!this.isRoot())
	        return this.getRoot().update();
	
	    this.updateIndexTable();
	},
	updateIndexTable: function(){
	    if(!this.isRoot())
	        return this.getRoot().updateIndexTable();
	
	    this.clearIndexCache();
	    this.clearIndexTable();
	
	    var oArg = {nIndex:0};
	    this.updateIndexTable2(oArg);

//console.dir( this.aIndexTable );
	    return;
	},
	updateIndexTable2: function(oArg){
	    var nCurrIndex = this.getStartIndex();
	    var nOrgIndex = oArg.nIndex;
		var nIncForDummy = (this.indexForDummy ? 1 : 0);
	    
	    while(1)
	    {
	        var nNextIndex = this.getNextExpandedChild(nCurrIndex);
	        var oNextIgnore = this.oIgnoreCollection.getNext(nCurrIndex);
	        
	        if(!nNextIndex && !oNextIgnore)
	            break;
	        
	        if(oNextIgnore && (!nNextIndex || (nNextIndex && oNextIgnore.nStart <= nNextIndex)))
	        {
	            if(nCurrIndex < oNextIgnore.nStart)
	            {
	                this.applyIndexTable(oArg.nIndex, this.getTumbler(), nCurrIndex);
	                oArg.nIndex += oNextIgnore.nStart - nCurrIndex;
	                nCurrIndex = oNextIgnore.nStart;
	            }
	            
	            nCurrIndex = oNextIgnore.nEnd + 1;
	        }
	        else if(nNextIndex)
	        {
	            this.applyIndexTable(oArg.nIndex, this.getTumbler(), nCurrIndex);
	            var oEntry = this.getChildEntry(nNextIndex);
	            oArg.nIndex += nNextIndex - nCurrIndex + ( oEntry.isDummy() ? (oEntry.isIgnoredDummy() ? 0 : nIncForDummy) : 1);
	            oEntry.updateIndexTable2(oArg);
	
	            nCurrIndex = nNextIndex + 1;
	        }
	    }
	
	    if(nCurrIndex <= this.getEndIndex())
	    {
	        this.applyIndexTable(oArg.nIndex, this.getTumbler(), nCurrIndex);
	        oArg.nIndex += this.getEndIndex() - nCurrIndex + 1;
	    }
	
	    this.setNumVisibleDescendants(oArg.nIndex - nOrgIndex);
	    return oArg;
	},
	getTumblerByIndex: function(nIndex){
	    if(!this.isRoot())
	        return this.getRoot().getTumblerByIndex(nIndex);
	
	    if(typeof nIndex == 'undefined')
	        return null;
	
	    if(this.aIndexCache[dwa.lv.treeViewEntry.prototype.sIndexPrefixStatic + nIndex])
	        return this.aIndexCache[dwa.lv.treeViewEntry.prototype.sIndexPrefixStatic + nIndex];
	
	    if(!(nIndex<this.getNumVisibleDescendants()))
	        return '';
	
	    var oObj = null;
	    for(var i=0;i<this.aIndexTable.length;i++)
	    {
	        var oTmpObj = this.aIndexTable[i];
	        if(nIndex < oTmpObj.nIndex)
	            break;
	
	        oObj = oTmpObj;
	    }
	
	    if(!oObj)
	        return '';
	
	    var sTumbler = oObj.sParentTumbler + (oObj.sParentTumbler ? '.' : '') + ((nIndex - oObj.nIndex) + oObj.nChildIndex);
	    this.aIndexCache[dwa.lv.treeViewEntry.prototype.sIndexPrefixStatic + nIndex] = sTumbler;
	    return sTumbler;
	},
	clearNoteIdCache: function(bDestroy){
	    this.getRoot().aNoteIdCache = bDestroy? null:{};
	},
	applyNoteIdCache: function(sNoteId, oNode){
	    if(!this.isRoot())
	        return this.getRoot().applyNoteIdCache(sNoteId, oNode);
	
	    if(typeof sNoteId == 'undefined' || typeof oNode == 'undefined')
	        return;
	
	    var oArray = this.aNoteIdCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sNoteId];
	    if(!oArray) this.aNoteIdCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sNoteId] = oArray = [];
	    oArray.push(oNode.getTumbler());
	},
	getEntriesByNoteId: function(sNoteId){
	    if(!this.isRoot())
	        return this.getRoot().getEntriesByNoteId(sNoteId);
	    
	    var a1=[],a2=this.aNoteIdCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sNoteId];
	    for( var i=0; a2 && i<a2.length; i++ )
	        a1[i]=this.getEntryByTumbler(a2[i]);
	    return a1;
	},
	clearUnidCache: function(bDestroy){
	    this.getRoot().aUnidCache = bDestroy? null:{};
	},
	applyUnidCache: function(sUnid, oNode){
	    if(!this.isRoot())
	        return this.getRoot().applyUnidCache(sUnid, oNode);
	
	    if(typeof sUnid == 'undefined' || typeof oNode == 'undefined')
	        return;
	
	    var oArray = this.aUnidCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sUnid];
	    if(!oArray) this.aUnidCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sUnid] = oArray = [];
	    oArray.push(oNode);
	},
	getEntriesByUnid: function(sUnid){
	    if(!this.isRoot())
	        return this.getRoot().getEntriesByUnid(sUnid);
	    
	    return this.aUnidCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sUnid];
	},
	clearTumblerCache: function(){
	    this.getRoot().aTumblerCache = {};
	},
	applyTumblerCache: function(sTumbler, oNode){
	    if(!sTumbler || typeof oNode == 'undefined')
	        return;
	
	    this.getRoot().aTumblerCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sTumbler] = oNode;
	},
	getEntryByTumbler: function(sTumbler, noDummy){
	    if(!sTumbler)
	        return this.getRoot();
	
		var entry = this.getRoot().aTumblerCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sTumbler];

		if( this.indexForDummy && noDummy && entry && entry.isDummy() ) return null;
		return entry;
	},
	getEntryByIndex: function(nIndex, noDummy){
		var sTumbler = this.getTumblerByIndex(nIndex);
		var oEntry = sTumbler ? this.getEntryByTumbler(sTumbler) : null;
		if( this.indexForDummy && noDummy && oEntry && oEntry.isDummy() ) return null;
		return oEntry;
	},
	getParentByTumbler: function(sTumbler){
	    if(!sTumbler)
	        return null;
	
	    var nPos = sTumbler.lastIndexOf('.');
	
	    if(nPos == -1)
	        return this.getRoot();
	
	    var sParent = sTumbler.substring(0, nPos);
	    return this.getEntryByTumbler(sParent);
	},
	isDummy: function(){
	    return !!this.bIsDummy;
	},
	isVisible: function(){
	    var oParent = this.getParent();
	    while(oParent != this.getRoot())
	    {
	        if(!oParent.isExpanded())
	            return false;
	        oParent = oParent.getParent();
	    }
	    return true;
	},
	getVisibleParent: function(){
	    if(this.isRoot())
	        return null;
	    
	    if(this.isVisible())
	        return this;
	    
	    return this.getParent().getVisibleParent();
	},
	getIndexByTumbler: function(sTumbler){
	    if(!this.isRoot())
	        return this.getRoot().getIndexByTumbler(sTumbler);
	    
	    if(!sTumbler)
	        return (void 0);
	
	    if(this.aIndexCache[dwa.lv.treeViewEntry.prototype.sTumblerPrefixStatic + sTumbler])
	        return this.aIndexCache[dwa.lv.treeViewEntry.prototype.sTumblerPrefixStatic + sTumbler];
	
	    var oEntry = this.getEntryByTumbler(sTumbler);
	    if(oEntry && !oEntry.isVisible())
	        return (void 0);
	    
	    var sParentTumbler = dwa.lv.colpos.getParent(sTumbler);
	    var nIndex = dwa.lv.colpos.getLeafIndex(sTumbler);
	    
	    for(var i=this.aIndexTable.length - 1;i>=0;i--)
	    {
	        var oObj = this.aIndexTable[i];
	        if(oObj.sParentTumbler == sParentTumbler && oObj.nChildIndex <= nIndex)
	        {
	            var nIndex = oObj.nIndex + (nIndex - oObj.nChildIndex);
	            this.aIndexCache[dwa.lv.treeViewEntry.prototype.sTumblerPrefixStatic + sTumbler] = nIndex;
	            return nIndex;
	        }
	    }
	    
	    return (void 0);
	},
	removeEntry: function(oEntry){
	    if(!this.isRoot())
	        return this.getRoot().removeEntry(oEntry);
	    
	    var sTumbler = oEntry.getTumbler();
	    var oParent = oEntry.getParent();
	    var nIndex = dwa.lv.colpos.getLeafIndex(sTumbler);
	    
	    // remove recursively the entries from cache 
	    oEntry.remove();
	    // remove from parent node
	    oParent.removeChild(nIndex);
	},
	remove: function(){
	    this.removeFromCache(this);
	    for(var i=this.getStartIndex(); i<=this.getEndIndex(); i++)
	    {
	        var oEntry = this.getChildEntry(i);
	        if(oEntry)
	            oEntry.remove();
	    }
	},
	removeChild: function(nIndex){
	    // rebuild aChilds array
	    var aNewChilds = [];
	    for(var name in this.aChilds)
	    {
	        var oNode = this.aChilds[name];
	        if(!oNode || typeof oNode == 'function')
	            continue;
	        if(name.indexOf(dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic) != 0)
	            continue;
	        var nIdx = parseInt(name.substring(dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic.length),10);
	        if(isNaN(nIdx))
	            continue;
	        if(nIdx == nIndex)
	            continue;
	        if(nIdx > nIndex)
	        {
	            aNewChilds[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + (nIdx - 1)] = oNode;
	            oNode.sTumbler = (oNode.getParent().getTumbler() ? oNode.getParent().getTumbler() + '.' : '') + (nIdx - 1);
	        }
	        else
	            aNewChilds[name] = oNode;
	    }
	    this.aChilds = aNewChilds;
	
	    // rebuild aExpandedChilds array
	    var aNewChilds = [];
	    for(var i=0;i<this.aExpandedChilds.length;i++)
	    {
	        var nIdx = this.aExpandedChilds[i];
	        if(nIdx == nIndex)
	            continue;
	        if(nIdx > nIndex)
	            aNewChilds.push(nIdx - 1);
	        else
	            aNewChilds.push(nIdx);
	    }
	    this.aExpandedChilds = aNewChilds;
	    
	    // remove index from collections
	    this.oCollection.removeAndShift(nIndex);
	    this.oIgnoreCollection.removeAndShift(nIndex);
	    
	    // decrease num of child nodes
	    this.setNumChilds(Math.max(0, this.getNumChilds() - 1));
	},
	removeFromCache: function(oEntry){
	    // remove entry from caches that root only have
	    if(!this.isRoot())
	        return this.getRoot().removeFromCache(oEntry);
	
	    if(!oEntry)
	        return;
	
	    // remove from NoteID cache
	    this.aNoteIdCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + oEntry.getNoteId()] = null;
	
	    // remove from Unid cache
	    this.aUnidCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + oEntry.getUnid()] = null;
	
	    // remove from Tumbler cache
	    var sDeletedTumbler = oEntry.getTumbler();
	    this.aTumblerCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sDeletedTumbler] = null;
	
	    // rebuild aTumblerCache array
	    var aCache={},nStart=dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic.length;
	    for(var p in this.aTumblerCache)
	    {
	        var oNode = this.aTumblerCache[p];
	        if( !oNode || typeof oNode == 'function' || p.indexOf(dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic)!=0 )
	            continue;
	        var sTumbler = p.substr(nStart);
	
	        var sNewTumbler = dwa.lv.colpos.getNewOneByDeletion(sTumbler, sDeletedTumbler);
	        if(sNewTumbler)
	        {
	            aCache[dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic + sNewTumbler] = oNode;
	            oNode.sTumbler = sNewTumbler;
	        }
	    }
	    this.aTumblerCache=aCache;
	},
	setAttribute: function(p1 ,p2 ){
	    var ve1=this.getViewEntry();
	    if( ve1 ){
	        this.oDataStore.setAttribute(ve1,p1,p2);
	    }
	},
	getAttribute: function(p1 ){
	    var ve1=this.getViewEntry();
	    if( ve1 ){
	        return this.oDataStore.getAttribute(ve1,p1);
	    }
	    return null;
	},
	destroy: function(){
	    if( this.isRoot() ){
	        this.clearNoteIdCache(1);
	        this.clearUnidCache(1);
	    }
	
	    for(var name in this.aChilds)
	    {
	        var oNode = this.aChilds[name];
	        if(!oNode || typeof oNode == 'function')
	            continue;
	        if(name.indexOf(dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic) != 0)
	            continue;
	        var nIdx = parseInt(name.substring(dwa.lv.treeViewEntry.prototype.sEntryPrefixStatic.length),10);
	        if(isNaN(nIdx))
	            continue;
	        oNode.destroy();
	    }
	    
	    this.aChilds = null;
	    this.aExpandedChilds = null;
	    if(this.oCollection) this.oCollection.destroy();
	    this.oCollection = null;
	    if(this.oIgnoreCollection) this.oIgnoreCollection.destroy();
	    this.oIgnoreCollection = null;
	}
});
