/*
 * © Copyright IBM Corp. 2013
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
dojo.provide("extlib.dijit.mobile.ScrollablePane");
dojo.require("dojox.mobile.ScrollablePane");
dojo.require("extlib.dijit.DataIterator");
dojo.require("dojo.json");

dojo.declare("extlib.dijit.mobile.ScrollablePane",dojox.mobile.ScrollablePane, 
	{
	dataRows: 0, // rows of data to be retrieved
	dataViewId: "TBD", // data view id passed from the renderer
	scrollToPos: "TBD", // position to scrollto
	moreData: true, // changed by the dataIterator depending on the availability of more data to be fetched
	servletPath: "",
	
	onTouchEnd: function(e) { // overriding the original method to add the setting of the value of the hidden input field that keeps track of the scrolled portion
		this.inherited(arguments);
		var hifNode = dojo.byId(this.id + "_hif");
		if(hifNode) {
				var pos = this.getPos();
				dojo.setAttr(hifNode,"value",JSON.stringify(pos));
		}
	},
	adjustDestination: function(to, pos) { //overriding original method to perform the infinite scrolling
		var dim = this.getDim();
		var ch = dim.c.h;
		var dh = dim.d.h;
		var toY = to.y - 72;
		var wrapId = this.id;
		
		var node=dojo.byId(this.id);
		
		// not sure why I can't find the attribute using the hasAttr method.. DataIterator is setting it using the setAttr
		if(node && (dojo.hasAttr(node,"moreData") || node.moreData != undefined) ) {
				this.moreData = dojo.hasAttr(node,"moreData")?dojo.getAttr(node,"moreData"):node.moreData;
		}
		
    	if(dh - ch > toY && this.moreData) {
    		var countVal = 30;
			if(this.dataRows > 0){
				countVal = this.dataRows ;
			}
			var firstVal = function() { // calculating the first element index in the subsequent request this needs to be associated with the actual dataViewId
				var count = 0;
				dojo.query(".mblListItemWrapper").forEach(function(element, index) {
					if (element.id.indexOf(wrapId) >= 0) {
						count++;
					}
				});
				return count;
			};
						
			var viewList = dojo.query("[name='$$viewid']");
			var viewId = dojo.attr(viewList[0], "value");
			
			var urlVal = function(servletPath) {
				var baseUrl = location.href;
				var returnUrl = baseUrl;
				if (baseUrl.indexOf("?") > -1) {
					returnUrl = baseUrl.substring(0,baseUrl.indexOf("?"));
				}else if (baseUrl.indexOf("#") > -1) {
					returnUrl = baseUrl.substring(0,baseUrl.indexOf("#"));
				}
				
				if(!~returnUrl.indexOf(servletPath)) {
					returnUrl = returnUrl + servletPath;
				}
					
				returnUrl = returnUrl + "/getrows?";
				var ajaxUrl = returnUrl + "$$axtarget=" + wrapId + "&$$viewid=" + viewId;
				
				return ajaxUrl;
			}
			var optionsVal = {
				"id": wrapId
				,"url": urlVal(this.servletPath)
				,"first": firstVal()
				,"count": countVal
				,"state": true
			};
			
			XSP.appendRows(optionsVal);
    	}
    	return true;
	},
	
	startup: function() {  //overriding original method to resize after init
		this.inherited(arguments);
		this.resize();
		// Scroll to the right position
		if(this.scrollToPos != "TBD")
			{
			var scrollPosObj = JSON.parse(this.scrollToPos);
			this.scrollTo(scrollPosObj);
			}
	}
}
);
