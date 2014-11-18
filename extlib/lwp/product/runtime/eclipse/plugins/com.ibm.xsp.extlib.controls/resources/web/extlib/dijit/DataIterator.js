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
dojo.provide("extlib.dijit.DataIterator");

XSP.appendRows = function xe_apr(options) {
	// options are:
	// id: String [required] - clientId of the DataIterator control
	// url: String [required] - URL to an AJAX request for the current XPage, 
	//         targeting the DataIterator control instance 
	//         and with a pathInfo suffix of "/getrows". 
	// first: int [required] - the index of the first row 
	//         of the DataIterator control to be included in the AJAX response
	// count: int [required] - the number of rows of the DataIterator control
	//         to be included in the AJAX response.
	// state: boolean [required] - Indicate if the state, after getting
	//         the AJAX request, should be saved on the server.
	// linkId: String [optional] - The clientId of the control to hide or disable
	//         when no more rows are available. This will usually point to a 
	//         link control with text like "Show More".
	// linkDisabledFormat: String[optional] - One of null, "text", "link", "hide"
	//         indicating the state of the linkId control once the last row in the 
	//         DataIterator control has been reached. Defaults to "text". 
	var node=dojo.byId(options.id);
	
	var dataViewId = dojo.getAttr(node, "dataViewId");
	
	var wrapperNode=dataViewId?node:null;
	
	node=dataViewId?dojo.byId(dataViewId):node;
		
	var fa=node.getAttribute("xsp_first")
	var f = fa?parseInt(fa):options.first
			
	f=dataViewId?options.first:f;
	
	var url = options.url + '&first='+f.toFixed()+'&rows='+options.count.toFixed()
	if(!options.state) url += '&state=false'
	// TODO shouldn't that depend on options.state, and shouldn't it be cumulative? so count+first, instead of just count. 
	node.setAttribute("xsp_first",(f+options.count).toFixed())
	node = ((node.tagName.toLowerCase()=="table")?node.tBodies[0]:null) || node;
	var standByNode;
	
	if(wrapperNode)
	{
		dojo.require("dojox.widget.Standby");
		standByNode = new dojox.widget.Standby({target: wrapperNode.id});
		document.body.appendChild(standByNode.domNode);
		standByNode.startup();
		standByNode.show();
	}
	
	dojo.xhrGet({
		url: url,
		load: function(response, ioArgs) {
			this._addContent(node,response);
			return response;
		},
		// This should be simplified in 853 as a lot of code is common with partial refresh
		_addContent: function (node, content) {
			var lastNode = node.lastChild;
			var extract = function(markStart, markEnd) {
				var startIndex = content.indexOf(markStart);
				if(startIndex >= 0) {
					var endIndex = content.lastIndexOf(markEnd);
					if(endIndex >= 0) {
						var script = content.substring(startIndex + markStart.length, endIndex);
						content = content.substring(0, startIndex) + content.substring(endIndex + markEnd.length);
						return script;
					}
				}
			};
			var header = extract("<!-- XSP_UPDATE_HEADER_START -->\n", "<!-- XSP_UPDATE_HEADER_END -->\n");
			if(header) {
				this._execHeader(header);
			}
			var scriptContent = extract("<!-- XSP_UPDATE_SCRIPT_START -->", "<!-- XSP_UPDATE_SCRIPT_END -->\n");
			var oct = this._count(node);
			if(content) {
				if(content.indexOf("dojox.mobile.ListItem") != -1) {
					dojo.place("<div class=\"newAccordion menuHidden\">\n" + content + "\n</div>", node, "last");
				}
				else {
					dojo.place(content, node, "last")
				}
			}
			if(scriptContent) {
				this._execHeader(scriptContent);
			}
			
			
				
			
			// We need to process the newly added tags
			// We start from the node next to the previously last one, until the end of the list...
			for(var nn = lastNode ? lastNode.nextSibling : node.firstNode; nn; nn=nn.nextSibling) {
				if(nn.nodeType == 1) {
					this._execScript(nn);
					this._parseDojo(nn); // Ensure we parse the just added node
				}
			}
			
			// Calculate the #of tags being loaded (= #of rows)
			if(options.linkId) {
				var nr = this._count(node) - oct;
				if(nr < options.count) {
					var link = dojo.byId(options.linkId);
					if(link){ 
						var linkDisabledFormat = options.linkDisabledFormat || "text";
						if( "hide" == linkDisabledFormat ){
							dojo.style(link, "display", "none");
						}else if( "link" == linkDisabledFormat ){
							// append style class "disabled"
							dojo.addClass(link, "disabled");
						}else{ // "text"
							// Convert from:
							// <a id="aaa" href="bbb" style="ccc" class="ddd">eee</a>
							// to
							// <span id="aaa" style="ccc" class="ddd">eee</span>
							var spanAttrs = {id: dojo.attr(link, "id") };
							var existingStyle = dojo.attr(link, "style");
							if( existingStyle ){
								spanAttrs.style = existingStyle;
							}
							var existingClass = dojo.attr(link, "class");
							if( existingClass ){
								spanAttrs['class'] = existingClass;
							}
							var existingContent = dojo.attr(link, "innerHTML");
							if( existingContent ){
								spanAttrs.innerHTML = existingContent;
							}
							var span = dojo.create("span", spanAttrs, link, "replace");
							// append style class "disabled"
							dojo.addClass(span, "disabled");
							// no longer attempt to modify the link
							options.linkId = null;
						}
					}
				}
			}
			
			if(wrapperNode) {
				var nr = this._count(node) - oct;
				if(nr < options.count) {
					// setting this attribute to false will prevent the infinite scroll to perform other requests when no more data is available..
					 dojo.setAttr(wrapperNode,"moreData", false);
				}else {
					dojo.setAttr(wrapperNode,"moreData", true);
				}
			}
			
			//display the new entries with accordion opening
			if(content) {
				dojo.query('.newAccordion.menuHidden', node).forEach(function(newNode) {
					//set height explicitly on the node (needed for height transition)
					dojo.style(newNode, 'height', newNode.offsetHeight + 'px');
					
					//toggle the classes, causing the transition
					dojo.removeClass(newNode, "menuHidden");
					dojo.addClass(newNode, "menuShown");
					
					//in 1 second (transitions last .75 seconds), move the new rows
					//out of the temporary parent and destroy the parent div
					setTimeout(function() {
							//get all old nodes
							var allNodes = dojo.query(".newAccordion", node).siblings(".mblListItemWrapper");
							
							//get our just-loaded nodes
							var newNodes = dojo.query(".mblListItemWrapper", newNode);
							
							if(newNodes) {
								//var initialization
								var prevID = false, prevNum = 0, curNum, nextNum = parseInt(newNodes[0].id.split(":")[4]);
								
								//find the id of the closest node to our first new node
								allNodes.forEach(function(aNode) {
									curNum = parseInt(aNode.id.split(":")[4]);
									if(curNum > prevNum && curNum < nextNum) {
										prevNum = curNum;
										prevID = aNode.id;
									}
								});
								
								//place the new nodes into the correct spots
								newNodes.forEach(function(wrapNode) {
									if(!prevID) {
										//in case we had an empty dataview
										prevID = newNode;
									}
									dojo.place(wrapNode, prevID, "after");
									prevID = wrapNode.id;
								});
							}
							
							//destroy the now-empty newAccordion container
							dojo.destroy(newNode);
						}, 1000
					);
				});
			}
			
			
			XSP._loaded();
		},
		handle: function() {
			if(standByNode) {
				standByNode.hide();
				setTimeout(function() {
					dojo.destroy(standByNode.id);
				}, 500
			);
				
			}
		},
		_count: function(node) {
			var c = 0;
			dojo.query(">", node).forEach(function(n){if(n.nodeType==1 && n.tagName.toLowerCase()!="script")c++});
			return c;
		},
	    _execHeader: function(content) {
			var _n = dojo.place("<div>&shy;" + content + "</div>", dojo.body());
			this._execScript(_n);
			dojo.body().removeChild(_n);
		},
		_execScript: function(node) {
			var scriptNodes = node.nodeName.toLowerCase() == "script" ? [node] : dojo.query("script", node);
			dojo.forEach(scriptNodes, function xrnfe2_fe(nd){
				if(nd.type == "text/javascript"){
					dojo.eval(nd.innerHTML);
				}
			});
        },
	    _parseDojo: function(node) {
			if(node && dojo.parser) {
				if(node.getAttribute('dojoType')) {
					dojo.parser.instantiate([node]);
				}
				dojo.parser.parse(node);
			}
        }
	});
}