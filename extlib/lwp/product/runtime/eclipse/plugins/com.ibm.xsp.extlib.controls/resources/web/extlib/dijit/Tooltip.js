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

dojo.provide("extlib.dijit.Tooltip");
	
dojo.require("dijit.Tooltip");

dojo.declare(
		"extlib.dijit.Tooltip",
		dijit.Tooltip,
		{
			ajaxParams: "",
			postCreate: function(){
				this.inherited(arguments);
				this.ctid = this.attr("id")+":_content"
				this.domNode.innerHTML = "<div id='"+this.ctid+"'></div>"
			}, 
			open: function(/*DomNode*/ target){
				// Delay the opening after partial refresh had been processed
				var _this = this; var _args = arguments;
				var options = {
					"params": this.ajaxParams,
					"formId": this.attr("id"),
					onComplete: function() {
						_this.inherited(_args);
					}
				};
				XSP.partialRefreshGet(this.ctid,options);
			}
		}
	);
