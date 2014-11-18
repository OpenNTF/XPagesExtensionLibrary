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
dojo.provide("extlib.dijit.TabContainer");
	
dojo.require("extlib.dijit.ExtLib");
dojo.require("dijit.layout.TabContainer");

dojo.declare("extlib.dijit.TabContainer",dijit.layout.TabContainer, {
	createTab: function(params) {
		if(params && params.tabUniqueKey) {
			var t
			dojo.forEach(this.getChildren(), function(item) {
				if(item.tabUniqueKey==params.tabUniqueKey) {t = item}
			})
			if(t){this.selectChild(t); return;}
		}
		var url = XSP.axGetRequestUrl(this.id,dojo.mixin({_action:"createTab"},params))
		if(url) {
			var _this = this;
			dojo.xhrGet({
				url: url, 
				handleAs: "json",
				load: function(resp, ioArgs) {
					dojo.place("<div id='"+resp.id+"'></div>",_this.containerNode,"last")
					var axOptions = {
						onComplete: function() {
							var dj = dijit.byId(resp.id)
							_this.addChild(dj)
							_this.selectChild(dj)
						},
						params: {
							"$$ajaxinner": "frame"
						}
					}
					XSP.partialRefreshGet(resp.id,axOptions)
	        	},
	        	error: function(err) {
	        		console.log(err)
	        	}
			});			
		}
	},
	selectChild: function(page) {
		this.inherited(arguments)
		var form = XSP.findForm(this.id)
		if(form&&form[this.id+"_sel"]) {
			form[this.id+"_sel"].value = page ? page.id.substring(page.id.lastIndexOf(':')+1) : ""
		}
	},
	_removeTab: function(id,refreshId,params) {
		var pane = dijit.byId(id)
		if(pane) {
			// As removeTab can be called from partial refresh, we need to delay
			// this after partial refresh id completed
			setTimeout(dojo.hitch(this,function(){
				this.removeChild(pane)
				pane.destroyRecursive();
				if(refreshId) {
					var axOptions = {"params": params}
					XSP.partialRefreshGet(refreshId,axOptions)
				}
			}),0);
		}
	},
	closeChild: function(/*dijit._Widget*/ page){ 
		// The url must be calculated before the tab is removed
		var url = XSP.axGetRequestUrl(page.id,{_action:'closeTab'})
		this.inherited(arguments);
		if(page._destroyed) { // No official way to check that...
			if(url) {
				var _this = page;
				dojo.xhrGet({
					url: url, 
					handleAs: "text",
					load: function(resp, ioArgs) {
	        		},
	        		error: function(err) {
	        			console.log(err)
	        		}
				});
				return true;
			}
		}
	}
});
