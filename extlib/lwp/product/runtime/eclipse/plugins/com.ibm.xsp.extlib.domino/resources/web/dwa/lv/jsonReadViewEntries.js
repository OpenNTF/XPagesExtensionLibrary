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

dojo.provide("dwa.lv.jsonReadViewEntries");

dojo.declare(
	"dwa.lv.jsonReadViewEntries",
	null,
{
	store: null,
	query: null,
	queryOptions: null,
	dwa: true,
	url: "",

	_items: null, // set by this._callbackSetData

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}
	},
	load: function(oRequest){
		if(!this.store){ return; }
        // get the index value for non-Domino datastores
        var index = oRequest.oQuery.start ? oRequest.oClass.oRoot.getIndexByTumbler(oRequest.oQuery.start) : 0;
		var start = (oRequest.oQuery.EndView == "1")
			? (this._numRows || this.store._numRows || this._items.length) - oRequest.oQuery.count + 1
			: (oRequest.oQuery.NavigateReverse == "1"
			? index - oRequest.oQuery.count + 1
			: index);

        // creating sort array for non-Domino datastores
        var sort = [];
        var isAsc = typeof(oRequest.oQuery.resortascending) != 'undefined';
        var isDsc = typeof(oRequest.oQuery.resortdescending) != 'undefined';
        if( isAsc || isDsc ){
            var sortcol = (isAsc ? oRequest.oQuery.resortascending : oRequest.oQuery.resortdescending);
            var colInfo = oRequest.oClass.aColInfo[sortcol];

            sort.push( {
                attribute: colInfo.sName,
                descending: !!isDsc
            } );
        }

		var _this = this;
		var fetch = {
			// copy the this.query data to another storage
			query:  (this.query ? dojo.clone(this.query) : {}),
			queryOptions: this.queryOption || {
				bUsingHttps: dwa.lv.globals.get().oSettings.bUsingHttps
			},
			start: start, // for non-domino datastores
			count: oRequest.oQuery.count, // for non-domino datastores
			sort: sort, // for non-domino datastores
			request: oRequest,
			onBegin: function(size, requestObject){
				dojo.hitch(_this, "_callbackFetchStart")(size, requestObject);
			},
			onComplete: function(result, requestObject){
				dojo.hitch(_this, "_callbackSetData")(result, requestObject);
			},
			onError: function(errText){
				console.error('dwa.lv.virtualList: ' + errText);
			}
		};

		if( oRequest && oRequest.bSynchronous ){
			fetch.xhrArgs = {sync: true};
		}

		this.store.fetch(fetch);
	},
	getViewEntriesRoot: function(oRequest){
		return this._items;
	},
	loadPrecentileInfo: function(oRequest){
		return false;
	},
	getUrl: function(oQuery){
		if(!this.store){ return location.href; }
		if(!this.store.getUrl){
			var url = this.store.url || this._jsonFileUrl;
			return url + "?start=" + oQuery.start + "&count=" + oQuery.count;
		}
		var request = {
			query: oQuery,
			queryOptions: {
				bUsingHttps: dwa.lv.globals.get().oSettings.bUsingHttps
			}
		}
		return this.store.getUrl(request);
	},
	getViewEntries: function(oRequest){
		return this._items;
//		return this.getViewEntriesRoot(oRequest) ? this._selectNodes(this.getViewEntriesRoot(oRequest), "viewentry") : null;
	},
	getEntryDatas: function(oViewEntry){
		// Not ready for dojo.data
		return this._selectNodes(oViewEntry, "entrydata");
	},
	getEntryDataByName: function(oViewEntry, sName){
		return this.store ? this.store.getValue(oViewEntry, sName) : null;
//		return oViewEntry[sName];

//		var aNodes = this._selectNodes(oViewEntry, "entrydata");
//		for(var i=0;i<this.getLength(aNodes);i++)
//			if(this.getAttribute(aNodes[i], 'name') == sName)
//				return aNodes[i];
//		return null;
	},
	getEntryDataByNumber: function(oViewEntry, nCol){
		// Not ready for dojo.data
		var aNodes = this._selectNodes(oViewEntry, "entrydata");
		for(var i=0;i<this.getLength(aNodes);i++)
			if(this.getAttribute(aNodes[i], 'columnnumber') == nCol)
				return aNodes[i];
		return null;
	},
	getLength: function(aNodes){
		return aNodes.length;
	},
	getItem: function(aNodes, nIndex){
		return aNodes[nIndex];
	},
	removeChild: function(aNodes, nIndex){
		return aNodes.splice(nIndex, 1);
	},
	getText: function(oNode, sJoinChar){
		// Not ready for dojo.data
		if( !oNode ) return '';
		return oNode.text;
	},
	getAttribute: function(oNode, sName){
		if(sName == "toplevelentries" && oNode['@' + sName] == undefined){
			return this._numRows || this.store._numRows || this._items.length;
		}else if(sName == "position" && oNode['@' + sName] == undefined){
			// Experimental:
			// Retuns an array index (1 origin) as a position.
			// This is for datastores that has no positions such as ItemFileReadStore.
			// BUG: This cannot support partitioned loading.
			var pos = dojo.indexOf(this._items, oNode) + 1;
			return pos + "";
		}
		return oNode['@' + sName];
	},
	getType: function(oNode){
		return this.getValue(oNode, '@type');
	},
	getValue: function(oNode, sName){
		if(!oNode){ return null; }
		if(!sName) sName = '@value';
		if(typeof(oNode) == "object"){
			// oNode is an item (or child item)
			if(sName == '@value'){
                if( this.store.hasAttribute(oNode, sName )){
    				var aValues = this.store.getValues(oNode, sName);
	    			return aValues.length == 0 ? undefined : (aValues.length == 1 ? aValues[0] : aValues);
                }else{
                    return oNode;
                }
			}
			else {
				return this.store.getValue(oNode, sName);
			}
		}else{
			if(sName == "@type"){
				return (typeof(oNode) == "number") ? "number" : "text";
			}else /*if(sName == "@value")*/{
				return oNode;
			}
		}
	},
    isEditableStore: function(){
        return !!(this.store && this.store.setValue);
    },
    setValue: function(oNode, oValue ){
        if( oNode && typeof(oNode) === "object" ){
            this.store.setValue(oNode, '@value', oValue);
        }
    },
	getAttributeBoolean: function(oNode, sName){
		return (!/^(false|no|0|)$/.test(this.getAttribute(oNode, sName) || ''));
	},
	getAttributeInt: function(oNode, sName){
		return (this.getAttribute(oNode, sName) - 0);
	},
	getAttributeString: function(oNode, sName){
		return (this.getAttribute(oNode, sName) || '');
	},
	setAttribute: function(oNode, sName, oValue){
		return oNode['@' + sName] = oValue;
	},
	_selectNodes: function(oNode, sElement){
		if(sElement == "viewentry"){
			return oNode; // ==this._items;
		}
		return oNode[sElement] ? oNode[sElement] : [];
	},
	_selectSingleNode: function(oNode, sElement){
		return oNode[sElement] ? (oNode[sElement].constructor == Array ? oNode[sElement][0] : oNode[sElement]) : null;
	},

	_callbackFetchStart: function(/*int*/size, /*Object*/requestObject){
		this._numRows = size - 0;
	},

	_callbackSetData: function(/*Array*/results, /*Object*/requestObject){
		this._items = results;
		if(results && results[0] && results[0]["@position"] == undefined){
			// for non-domino datastores
			for(var i = 0; i < results.length; i++){
				results[i]["@position"] = requestObject.start + i + 1;
			}

			if( requestObject.request && requestObject.request.oQuery && requestObject.request.oQuery.NavigateReverse === "1" ){
				// reverse array elements for the reverse query

				for( var i = 0, j = results.length - 1; i < j; i++, j-- ){
					var o = results[i];
					results[i] = results[j];
					results[j] = o;
				}
			}
		}
		requestObject.request.oClass.loadedData(requestObject.request);
	}
});
