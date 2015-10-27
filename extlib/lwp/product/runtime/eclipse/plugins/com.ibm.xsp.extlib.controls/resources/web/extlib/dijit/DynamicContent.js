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
dojo.provide("extlib.dijit.DynamicContent");

XSP.showContent = function xe_sct(panelid,content,params) {
	params = dojo.mixin(params, dojo.queryToObject(dojo.hash()));
	
	var mixParams = {};
	var elem = dojo.byId(panelid);
	var contentParam = "content";
	if(elem != null) {
		var elemContentParam = elem.getAttribute("data-param");
		if(elemContentParam) {
			contentParam = elemContentParam;
		}
	}
	mixParams[contentParam] = content;
	
	params = dojo.mixin(params,mixParams)
	// If the param is set up for useHash, update the hash as well.
	// This doesn't check the id property in order to maintain consistency with
	// previous behavior.
	if(XSP._hashContent && XSP._hashContent[contentParam]) {
		XSP.updateHash(dojo.objectToQuery(params))
		XSP.partialRefreshGet(panelid,{params:XSP._hash})
	} else {
		XSP.partialRefreshGet(panelid,{params:params})
	}
}

XSP.registerHash = function xe_rhs(panelid) {
	if(!XSP._hashContent) {
		XSP._hashContent = {};
	}
	
	var elem = dojo.byId(panelid);
	if(!elem) { return; }
	var contentParam = elem.getAttribute("data-param");
	
	if(!XSP._hashContent[contentParam]) {
		XSP._hashContent[contentParam] = {
			id: panelid,
			value: ""
		};
	}
	
	if(!XSP._hashCallback) {
		dojo.require("dojo.hash");
		XSP._hashCallback = function(hash) {
			var hashObj = dojo.queryToObject(hash);
			var refreshIds = [];
			for(var paramName in hashObj) {
				// Only refresh if the hash is different from the last one for that panel
				if(XSP._hashContent[paramName] && XSP._hashContent[paramName].value != hashObj[paramName]) {
					refreshIds.push(XSP._hashContent[paramName].id);
					XSP._hashContent[paramName].value = hashObj[paramName];
				}
			}
			
			// Cycle through IDs needing an update in sequence
			if(refreshIds.length > 0) {
				XSP._hash=hash;
				
				var refresh = function() {
					var id = refreshIds.pop();
					if(id) {
						XSP.partialRefreshGet(id, {
							params: hash,
							onComplete: refresh
						});
					}
				}
				refresh();
			}
		}
		dojo.addOnLoad( function() {
			dojo.subscribe("/dojo/hashchange", XSP._hashCallback);
			if(dojo.hash()) {
				XSP._hashCallback(dojo.hash())
			}
		})
	}
}

// Update the copy of the hash while preventing the event processing
XSP.updateHash = function xs_uhs(h) {
	// Split it apart to update only the specific part of the hash
	var updateObj = dojo.queryToObject(h);
	var hashObj = dojo.queryToObject(dojo.hash());
	
	for(var updateKey in updateObj) {
		hashObj[updateKey] = updateObj[updateKey];
	}
	
	h = dojo.objectToQuery(hashObj);
	
	XSP._hash=h
	dojo.hash(h)
}
