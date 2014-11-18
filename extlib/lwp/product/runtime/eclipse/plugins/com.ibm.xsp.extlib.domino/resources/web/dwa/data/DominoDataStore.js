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

dojo.provide("dwa.data.DominoDataStore");
dojo.require("dwa.data._DominoStoreBase");
dojo.require("dwa.common.notesValue");

dojo.declare("dwa.data.DominoDataStore",
			 dwa.data._DominoStoreBase,
{
	//	summary:
	//		A data store for Domino data source

	dwa: true,
	notesValue: new dwa.common.notesValue,

	getUrl: function(request){
		if(this.url.indexOf("://") == -1){ return this.url }
		var oQuery = request.query;
		var oQueryOptions = request.queryOptions;
		if(this.dwa){
			if(!oQuery.Form)
				oQuery.Form = 's_ReadViewEntries'+(this.type=="json"?'_JSON':'');
		}
	
		var oArguments = {};
		var oPresets = {};
		var bPresets = false;
		for(var sParam in oQuery) {
			switch(sParam) {
				// url argument stuff
				case 'Form':
				case 'start':
				case 'count':
				case 'StartKey':
				case 'UseStartKeyOnly':
				case 'UntilKey':
				case 'IncludeUntilKey':
				case 'KeyType':
				case 'Navigate':
				case 'EndView':
				case 'NavigateReverse':
				case 'ExpandView':
				case 'CollapseView':
				case 'resortascending':
				case 'resortdescending':
					oArguments[sParam] = oQuery[sParam];
					break;
				// preset field stuff
				case 'FolderName':
				case 'UnreadCountInfo':
				case 'DBQuotaInfo':
				case 'PercentileInfo':
				case 'hc':
				case 'DirIndex':
				case 'NoEntryData':
				case 'PercentileEntries':
					oPresets[sParam] = oQuery[sParam];
					bPresets = true;
					break;
				default:
	//				alert('unknown parameter:' + sParam + '=' + oQuery[sParam]);
					break;
			}
		}
		if(oQueryOptions.bUsingHttps) {
			oPresets['s_UsingHttps'] = '1';
			bPresets = true;
		}
		if(this.type == "xml"){
			oPresets['noPI'] = '1';
			bPresets = true;
		}

		if(bPresets) {
			var sPresets = '';
			for(var sName in oPresets)
				sPresets += (sPresets ? ',' : '') + sName + ';' + oPresets[sName];
			oArguments['presetfields'] = sPresets;
		}
	
		var sArguments = '';
		for(var sName in oArguments)
			sArguments += (sArguments ? '&' : '') + sName + '=' + oArguments[sName];
		
		return this.dwa ?
			this.url + '/iNotes/Proxy'
				+ '?OpenDocument' + (sArguments ? '&' : '') + sArguments + '&TZType=UTC' :
			this.url + '/' + oQuery.FolderName
				+ '?ReadViewEntries' + (sArguments ? '&' : '') + sArguments + '&TZType=UTC' + (this.type=="json"?'&OutputFormat=Json':'');
	},

    setValue: function(item, attribute, value){
        if( attribute in item ){
            item[attribute] = value;
        }
    },

	_getCellValue: function(){
		//	summary:
		//		Get cell value from a cell object.
		//	description:
		//		Cell object is an attribute value of an item, which represents a row.
		//		It can be any type of data structure, which may include various meta data.
		//		This method will become a toString() for the cell object.
		//		'this' points to the cell object.
		//return typeof(this["@value"]) == "string" ? this["@value"] : "[object]";
		var valueType = this["@type"] || "text";
		var value = this["@value"];
		
		return (valueType.indexOf("list") == -1) ? value.toString() : value.toString();
	},

	format: function(data){
		//	summary:
		//		Format raw data to an array of items.
		//	data:
		//		Root of json object or the document object of XML dom tree.
		var entries;
		if(this.type == "json"){
			entries = data.entries ? data.entries : data;
			this._numRows = entries["@toplevelentries"] - 0;
			var items = entries.viewentry;
			for(var i = 0; i < items.length; i++){
				var item = items[i];
				item[this._storeRef] = this;
				var cols = item.entrydata;
				for(var j = 0; j < cols.length; j++){
					var col = cols[j];

					this.notesValue.setJsonNode( col );
					col['@type'] = this.notesValue.sType;
					col['@value'] = this.notesValue.vValue;
					item[col["@name"]] = col;
					col.toString = this._getCellValue;
					col[this._storeRef] = this;
					col._isColumn = true;
				}
			}
			for(var key in entries){
				if(key.charAt(0) == "@"){
					items[key] = entries[key];
				}
			}
		}else/* if(this.type == "xml")*/{
			var root = data;
			for(var i = 0; i < root.childNodes.length; i++){
				var child = root.childNodes[i];
				if(child.nodeType == 1 && child.nodeName == "readviewentries"){
					root = child;
					break;
				}
			}
			for(var i = 0; i < root.childNodes.length; i++){
				var child = root.childNodes[i];
				if(child.nodeType == 1 && child.nodeName == "viewentries"){
					this._numRows = child.getAttribute("toplevelentries") - 0;
					root = child;
					break;
				}
			}

			var items = [];
			for(var i = 0; i < root.childNodes.length; i++){
				var xmlitem = root.childNodes[i];
				if(xmlitem.nodeType == 1 && xmlitem.nodeName == "viewentry"){
					var item = {};
					item[this._storeRef] = this;

					var cols = [];
					for(var j = 0; j < xmlitem.childNodes.length; j++){
						var child = xmlitem.childNodes[j];
						if(child.nodeType == 1 && child.nodeName == "entrydata"){
							this.notesValue.setXmlNode( child );
							var col = {};
							col['@type'] = this.notesValue.sType;
							col['@value'] = this.notesValue.vValue;
							col.toString = this._getCellValue;
							col[this._storeRef] = this;
							col._isColumn = true;

							this.setAttrs( col, child );
							var name = col[ "@name" ];
							item[ name ] = col;
						}
					}

					this.setAttrs( item, xmlitem );
					items.push( item );
				}
			}

		}

		return items;
	},

	setAttrs: function( item, node ){
		var attrs = node.attributes || [];
		for (var i = 0, len = attrs.length; i < len; i++) {
			var attr = attrs[i];
			item["@"+attr.nodeName] = attr.nodeValue;
		}
	}
});
