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
	params = dojo.mixin(params,{content:content})
	if(XSP._hashContentId==panelid) {
		XSP.updateHash(dojo.objectToQuery(params))
		XSP.partialRefreshGet(panelid,{params:XSP._hash})
	} else {
		XSP.partialRefreshGet(panelid,{params:params})
	}
}

XSP.registerHash = function xe_rhs(panelid) {
	XSP._hashContentId=panelid
	if(!XSP._hashCallback) {
		dojo.require("dojo.hash");
		XSP._hashCallback = function(hash) {
			// Only refresh if the has is different from the last one
			if(XSP._hash!=hash) {
				XSP._hash=hash
				XSP.partialRefreshGet(XSP._hashContentId,{params:hash})
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
	XSP._hash=h
	dojo.hash(h)
}
