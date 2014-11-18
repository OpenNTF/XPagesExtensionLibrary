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

dojo.provide("dwa.data._DominoStoreBase");

dojo.declare("dwa.data._DominoStoreBase", null,
{
	//	summary:
	//		A data store base class for Domino data source variants

	url: "",
	type: "json", // "json" or "xml"
	items: [],
	_storeRef: "_S",
	folderName: "",

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}
	},

	getFeatures: function(){
		return {
			'dojo.data.api.Read': true
		};
	},

	getValue: function(item, attribute, defaultValue){
		var values = this.getValues(item, attribute);
		if(values && values.length > 0){
			return values[0];
		}
		return defaultValue;
	},

	getValues: function(item, attribute){
		var v = item[attribute] || [];
		return dojo.isArray(v) ? v : [v];
	},

	getAttributes: function(item){
		var attributes = [];
		for(var i in item){
			if(i.charAt(0) == '_'){ continue; }
			attributes.push(i);
		}
		return attributes;
	},

	hasAttribute: function(item, attribute){
		if(this.getValue(item, attribute)){
			return true;
		}
		return false;
	},

	isItemLoaded: function(item){
		 return this.isItem(item);
	},

	loadItem: function(keywordArgs){
	},

	getLabel: function(item){
		return this.getValue(item, this.label);
	},
	
	getLabelAttributes: function(item){
		return [this.label];
	},

	containsValue: function(item, attribute, value){
		var values = this.getValues(item,attribute);
		for(var i = 0; i < values.length; i++){
			if(values[i] === value){
				return true;
			}
		}
		return false;
	},

	isItem: function(item){
		if(item && item[this._storeRef] === this){
			return true;
		}
		return false;
	},
	
	close: function(request){
	},

	errorHandler: function(errorData, requestObject){
		if(requestObject.onError){
			var scope = requestObject.scope || dojo.global;
			requestObject.onError.call(scope, errorData, requestObject);
		}
	},

	fetchHandler: function(items, requestObject, numRows){
		var scope = requestObject.scope || dojo.global;
		if(!requestObject.store){
			requestObject.store = this;
		}
		if(requestObject.onBegin){
			requestObject.onBegin.call(scope, numRows, requestObject);
		}
		if(requestObject.onItem){
			for(var i = 0; i < items.length; i++){
				var item = items[i];
				requestObject.onItem.call(scope, item, requestObject);
			}
		}
		if(requestObject.onComplete){
			requestObject.onComplete.call(scope, items, requestObject);   
		}
	},

	fetch: function(request){
		request = request || {};
		if(!request.store){
			request.store = this;
		}
		this._fetchItems(request);
		return request;
	},

	_fetchItems: function(request){
		if(!request.query){ request.query = {}; }
		if(!request.queryOptions){ request.queryOptions = {}; }
		if(request.request && request.request.oQuery){
			dojo.mixin(request.query, request.request.oQuery);
		}

		var url = this._requestUrl = this.getUrl(request);
		var getArgs = {
			url: url,
			handleAs: this.type
		};

		if( request.xhrArgs ){
			dojo.mixin( getArgs, request.xhrArgs );
		}

		var _this = this;
		var deferred = dojo.xhrGet(getArgs);
		deferred.addCallback(function(data){
			_this.items = _this.format(data);
			_this.fetchHandler(_this.items, request, _this._numRows);
		});
		deferred.addErrback(function(error){
			_this.errorHandler(error, request);
		});
	},

	format: function(data){
		// subclass should implement
		return data.items;
	},

	getUrl: function(request){
		// subclass should implement
		return this.url;
	},

	xmlToJson: function(/*DOMNode*/node){
		var types = { number:0, text:0, datetime:0, datetimepair:"datetime", datetimelist:"datetime", numberlist:"number", textlist:"text" };

		var item = {};
		var attrs = node.attributes || [];
		for (var i = 0, len = attrs.length; i < len; i++) {
			var attr = attrs[i];
			item["@"+attr.nodeName] = attr.nodeValue;
		}
		if(node.childNodes.length == 1 && node.firstChild.nodeType == 3){
			var varType = item["@type"];

			if(!varType){
				item = node.firstChild.nodeValue ;
			}else{
				// includes type attribute in the tag..
				if(typeof(types[varType]) == "undefined"){ varType = item["@type"] = "text"; }
				item[varType] = [node.firstChild.nodeValue];
			}

			//item["@value"] = node.firstChild.nodeValue;
		}
		for(var i = 0; i < node.childNodes.length; i++){
			var child = node.childNodes[i];
			if(child.nodeType != 1){ continue; }

			var childNodeName = child.nodeName;
			var childJson = this.xmlToJson(child);

			if(types[node.nodeName] == childNodeName){
				// basic element inside xxxlist or datetimepair
				childJson = [childJson];
			}

			if(childNodeName.indexOf("list") != -1){
				item[childNodeName] = childJson;
			}else{
				if(!item[childNodeName]){
					item[childNodeName] = [];
				}
				item[childNodeName].push(childJson);
			}
		}
		return item;
	}
});
