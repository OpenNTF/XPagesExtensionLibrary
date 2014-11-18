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

dojo.provide("extlib.dojo.data.FileStore");
dojo.require("dojo.date.stamp");

dojo.declare("extlib.dojo.data.FileStore", null, {

	constructor: function(/* Object */ keywordParameters){
		//	summary: constructor
		//	keywordParameters: {url: String}
		this._features = {
			'dojo.data.api.Read':true, 
			'dojo.data.api.Write':true,
			'dojo.data.api.Identity':true, 
			'dojo.data.api.Notification':true
		};
		this.url = keywordParameters.url;
		this._identity = '@unid';
	},
	
	url: "",
	_items:[],
	_byIdentity:{},
	_topLevelEntries:0,
	_start:0,
	
	// Pending changes
	_pendings:{},
	_created: 0,

	
	// =========================================================================
	// dojo.data.api.Read	

	fetch: function(request) {
		var originalAbortFunction = request.abort || null,
		    aborted = false, self = this;

		request = request || {};
		
		var handleError = function(error){
			if (aborted) return;
			if(request.onError){
				var scope = request.scope || dojo.global;
				request.onError.call(scope, error, request);
			}
		};
		
		var handleData = function(data) {
			if (aborted) return;
			try{
				self._processResponse(request, data);
			}catch(e){
				handleError(e, request);
			}
		};		
		
		request.abort = function(){
			aborted = true;
			if(originalAbortFunction){
				originalAbortFunction.call(request);
			}
		};
		var idx = self.url.indexOf('?');
		var url = [self.url, (idx == -1 ? "?" : "&"), "start=",
				      request.start, "&count=", request.count];
		if (request.sort && request.sort.length) {
			url = url.concat(["&sortcolumn=", request.sort[0].attribute, 
			                  "&sortorder=", request.sort[0].descending?"descending":"ascending"]);
		}
		var getArgs = {
				url: url.join(''),
				handleAs: "json",
				preventCache: true
			};
		var getHandler = dojo.xhrGet(getArgs);
		
		getHandler.addCallback(function (data){
			handleData(data);
		});
		
		getHandler.addErrback(function(error) {
			handleError(error);
		});
		return request;
	},
	
	_processResponse: function(requestObject, data) {
		this.close();
		this._start = requestObject.start;
		
		//TODO: clear identity?		
		dojo.forEach(data.items, function(entry, idx) {
			var item = {storeRef:this, attributes:entry};
			var id = item.attributes[this._identity]
			var pending = this._pendings[id]
			if(pending) {
				for (var s in pending.modAttrs[s]) {
					this.item.attributes[s] = pending.modAttrs[s]
				}
			}
			this._byIdentity[id] = item;
			this._items.push(item);
		}, this);

		this._topLevelEntries = data['@toplevelentries'];
		this.onData(requestObject, data);
		this._finishResponse(requestObject);
	},
	
	_finishResponse: function(requestObject) {
		var scope = requestObject.scope || dojo.global;
		if(requestObject.onBegin){
			requestObject.onBegin.call(scope, this._topLevelEntries, requestObject);
		}
		if(requestObject.onItem){
			dojo.forEach(this._items, function(item) {
				requestObject.onItem.call(scope, item, requestObject);
			});
		}
		if(requestObject.onComplete && this._items.length){
			requestObject.onComplete.call(scope, requestObject.onItem ? null : this._items, requestObject);   
		}
	},
	
	onData: function(requestObject, data) {
	},
	
	getFeatures: function(){
		return this._features;
	},
	
	getValue: function(	/* item */ item, 
						/* attribute-name-string */ attribute, 
						/* value? */ defaultValue){
		this._assertHasAttribute(item, attribute);
		var value = item.attributes[attribute];
		if (dojo.isArray(value)) {
			value = (value.length > 0) ? value[0] : defaultValue;
		}
		return value;
	},

	getValues: function(/* item */ item,
						/* attribute-name-string */ attribute){
		this._assertHasAttribute(item, attribute);
		var value = item.attributes[attribute];
		if (!dojo.isArray(value)) {
			value = [value];
		}
		return value;
	},

	getAttributes: function(/* item */ item){
		this._assertIsItem(item);
		var array = [];
		for (var s in item.attributes) {
			array.push(s);
		}
		return array; // array
	},

	hasAttribute: function(	/* item */ item,
							/* attribute-name-string */ attribute){
		this._assertIsItem(item);
		return typeof item.attributes[attribute] != 'undefined';
	},

	containsValue: function(/* item */ item,
							/* attribute-name-string */ attribute, 
							/* anything */ value){
		this._assertHasAttribute(item, attribute);
		return item.attributes[attribute] == value;
	},

	isItem: function(/* anything */ something){
		return (something && something.storeRef === this);
	},

	_assertIsItem: function(/* anything */ something){ 
		if (!this.isItem(something)) {
			throw new Error("TableStore: invalid parameter");
		}
	},

	_assertHasAttribute: function(/* item */ item,
	                              /* attribute-name-string */ attribute) {
	    if (!this.hasAttribute(item, attribute)) {
			throw new Error("TableStore: invalid parameter");
		}
	},
	
	isItemLoaded: function(/* anything */ something) {
		console.log("isItemLoaded="+something.toString());
		return this.isItem(something);
	},

	loadItem: function(/* object */ keywordArgs){
		console.log("loadItem");
		if (!this.isItemLoaded(keywordArgs.item)) {
			throw new Error('Unimplemented API: TableStore.loadItem');
		}
	},

	close: function(/*dojo.data.api.Request || keywordArgs || null */ request){
		for (var s in this._byIdentity) {
			delete this._byIdentity[s];
		}
		this._items.splice(0, this._items.length);
	},

	getLabel: function(/* item */ item){
		console.log("getLabel="+item.toString());
		return undefined;
	},

	getLabelAttributes: function(/* item */ item){
		console.log("getLabelAttributes="+item.toString());
		return null;
	},

	
	// =========================================================================
	// dojo.data.api.Identity	
	
	getIdentity: function(/* item */ item){
		this._assertIsItem(item);
		return item.attributes[this._identity]; // string
	},

	getIdentityAttributes: function(/* item */ item){
		return [this._identity];
	},

	fetchItemByIdentity: function(/* object */ keywordArgs){
		var item = this._byIdentity[keywordArgs.identity];
		if (item && keywordArgs.onItem) {
			keywordArgs.onItem(item);
		} else if (!item && keywordArgs.onError) {
			kewordArgs.onError(new Error("Item not available"));
		}
	},

	
	// =========================================================================
	// dojo.data.api.Write
	
	newItem: function(/* Object? */ keywordArgs, /*Object?*/ parentInfo){
		throw new Error('Unimplemented API: dojo.data.api.Write.newItem');
	},

	deleteItem: function(/* item */ item){
		this._assertIsItem(item);

		var id = this.getIdentity(item)
		var pending = this._pendings[id]
		if(!pending) {
			pending = {op:2, origAttrs: item.attributes, modAttrs:{}}
			this._pendings[id] = pending
		} else {
			if(pending.op==2) { // Already Deleted, do nothing
				return false;
			}
			// Created or modified, just continue
		}
		pending.op=2; 
		pending.modAttrs={}; 
		this.onDelete(item);
		return true;
	},

	setValue: function(	/* item */ item, 
						/* string */ attribute,
						/* almost anything */ value){
		this._assertIsItem(item);
		this._assertHasAttribute(item, attribute);
		if (typeof value == 'undefined' || value === null)
			throw new Error("TableStore.setValue: invalid parameter");

		// Don't allow changing the item's identity
		if(attribute == this._identity){
			throw new Error("TableStore does not support changing the value of an item's identifier.");
		}
		
		var id = this.getIdentity(item)
		var pending = this._pendings[id]
		if(!pending) {
			pending = {op:1, origAttrs: item.attributes, modAttrs:{}}
			this._pendings[id] = pending
		} else {
			if(pending.op==2) { // Deleted, do nothing
				return false;
			}
			// Created or modified, just continue
		}
			
		var oldValue = item.attributes[attribute];
		item.attributes[attribute] = pending.modAttrs[attribute] = value;
		this.onSet(item, attribute, oldValue, value);
		return true; // boolean
	},

	
	setValues: function(/* item */ item,
						/* string */ attribute, 
						/* array */ values){
		if (!values.length)
			throw new Error('TableStore does not support unsetting attributes');
		
		return this.setValue(item, attribute, value); // boolean
	},

	unsetAttribute: function(	/* item */ item, 
								/* string */ attribute){
		throw new Error('Unimplemented API: dojo.data.api.Write.clear');
	},

	save: function(/* object */ keywordArgs){
		if (this._saveInProgress)
			throw new Error('TableStore: Save in progress!');
		
		// Compose the message to send to the server
		var postData = {
			created: dojo.date.stamp.toISOString(new Date()),
			rows: []
		}
		for(var s in this._pendings) {
			var p =  this._pendings[s]
			var o = {op:p.op,id:s,items:p.modAttrs}
			postData.rows.push(o)
		}
		
		var self = this;
		var loadFct = function(data){
			self._saveInProgress = false;
			self._pendings = {};
			self.created = 0;
			if(keywordArgs && keywordArgs.onComplete){
				var scope = keywordArgs.scope || dojo.global;
				keywordArgs.onComplete.call(scope);
			}
		};
		var errorFct = function(err){
			self._saveInProgress = false;
			if(keywordArgs && keywordArgs.onError){
				var scope = keywordArgs.scope || dojo.global;
				keywordArgs.onError.call(scope, err);
			}
		};
		var args = {
				url: this.url,
				handleAs: "json",
				postData: dojo.toJson(postData),
				headers: {"Content-Type":"application/json"},
				timeout: XSP.submitLatency,
				load: loadFct,
				error: errorFct
		};

		this._saveInProgress = true;
		handler = dojo.xhrPost(args);		
	},

	revert: function(){
		for(var s in this._pendings) {
			var p = this._pendings[s]
			if(op==1) {
				var o = this._byIdentity[s];
				if(o) {
					for( var i in p.modAttrs) {
						o[i] = p.modAttrs[i]
					}
				}
			}
		}
		this.created = 0;
		return true;
	},

	isDirty: function(/* item? */ item){
		var id = this.getIdentity(item);
		if(this._pendings[id]) {
			return true;
		}
		return false;
	},
	
	
	// =========================================================================
	// dojo.data.api.Notification

	onSet: function(/* item */ item, 
					/*attribute-name-string*/ attribute, 
					/*object | array*/ oldValue,
					/*object | array*/ newValue){
	},

	onNew: function(/* item */ newItem, /*object?*/ parentInfo){
	},

	onDelete: function(/* item */ deletedItem){
	} // TJT: removed trailing comma

});