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
dojo.provide("extlib.dijit.PickerList");

dojo.require("dojo.cache")
dojo.require("extlib.dijit.TemplateDialog")
dojo.require("dojo.i18n")
dojo.require("dojo.string")
dojo.requireLocalization("extlib.dijit","pickers")

dojo.declare(
	'extlib.dijit.PickerList',
	[extlib.dijit.TemplateDialog],
	{
		msep: "",
		allowMultiple: false,
		trim: false,
		listWidth: "270px",
		listHeight: "25em",
		PickerList_OK: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerList_OK,
        PickerList_Cancel: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerList_Cancel,
        PickerName_unSelectedListLabel:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_unSelectedListLabel,
		templateString: dojo.cache("extlib.dijit","templates/PickerList.html"),
		controlValues: "",
		firstNode: null,
		currentNode: null,
		postCreate: function() {
			this.inherited(arguments)
			this._fixStyles(this.templateString)
			var controlValue=this.getControlValue(this.control);
			this.controlValues = controlValue ? (this.msep ? controlValue.split(this.msep) : [controlValue]) : [];
			if(this.trim) this.controlValues = dojo.map(this.controlValues,dojo.trim)
			dojo.setAttr(this.containerNode, "aria-label", this.dlgTitle);
			this._fetchData()
		},
		ok: function(){
			var res = this._getResult()
			this.updateControl(this.control,res,this.msep)
            this.popup.destroyRecursive();
            this.destroyRecursive();
		},
		cancel: function(){
            this.popup.destroyRecursive();
            this.destroyRecursive();
		},
        show: function(){
			this.inherited(arguments)
			this.popup.onCancel = function(){
	            this.destroyRecursive();
				return 1;
			}
        },
		_fetchData: function() {
			this.firstNode = null;
			var _this = this;
			dojo.xhrGet({
		        url: this.url, 
		        preventCache: this.preventCache,
		        handleAs: "json",
		        preventCache: this.preventCache,
		        load: function(resp, ioArgs) {
					dojo.forEach(resp.items, function(item) {
						_this._onItem(item)
					})
					_this._onComplete()
		        },
		        error: function(err,ioArgs) {
		        	if(ioArgs.xhr.status!=401) { // Ignore unauthorized
		        		alert(err)
		        	}
		        }
			});			
		},
		_onItem: function(item){
			var val = item["@value"]
			var lbl = item["@label"] || val
			var itemIsSelected = false
			if(val&&dojo.indexOf(this.controlValues,val)>=0) {
				itemIsSelected = true
			}
			var li = dojo.create("li", {
				val: val,
				tabIndex: -1,
				role:"option",
				"aria-selected": itemIsSelected ? "true" : "false",
				"class": "xspPickerItem",
				title: val,
				unselectable: "on",
				innerHTML: lbl
			}, this.containerNode);
			if(itemIsSelected) {
				dojo.addClass(li,"xspPickerItemSelected")
				if(!this.firstNode) this.firstNode=li
			}
		},
		_onComplete: function(){
			if(!this.firstNode) {
				this.firstNode = this.containerNode.firstChild 
			}
			this._setCurrent(this.firstNode)
			this._updateResult()
			setTimeout(dojo.hitch(this,function(){(this.firstNode||this.containerNode).focus()}),100);
		},
		_setCurrent: function(node) {
			this.currentNode = node
			if(node) {
				this.listScrollTo(this.bodyWrapper,node)
			}
		},
		_select: function(node){
			if(node && node.nodeName.toLowerCase() == "li") {
				if(!this.msep) {
					dojo.query("li.xspPickerItemSelected",this.containerNode).removeClass("xspPickerItemSelected");
				}
				dojo.toggleClass(node,"xspPickerItemSelected");
				this._toggleAttribute(node, "aria-selected", "true", "false");
				this._updateResult()
			}
		},
		_toggleAttribute:function(node,attribute,val1,val2)
		{
			val = dojo.getAttr(node,attribute);
			if(val == val1)
				{
				dojo.setAttr(node, attribute, val2);
				}
			else
				{
				dojo.setAttr(node, attribute, val1);
				}
		},
		_handleClick: function(e){
			// Key presses on arrows can trigger click events
			// In such cases xspPickerBody is the target element class. If that element
			// is set as the current node it breaks keyboard navigation
			// Ignoring such events fixes the problem
			if(e.target && null != e.target.className && e.target.className != "xspPickerBody" && e.target.className != "xspPickerBodyWrapper") {
				this._select(e.target)
				this._setCurrent(e.target)
			}
			e.target.focus();
		},
		_handleDblClick: function(e) {
			this._handleClick(e)
			this.ok();
		},
		_handleKeyPress: function(evt){
			var key = evt.keyCode ? evt.keyCode : evt.which
			if(key){
				if(!this.currentNode) return;
				var dk = dojo.keys;
				if(key === dk.UP_ARROW){
					this._moveFocus(evt,1,true)
				} else if(key === dk.DOWN_ARROW){
					this._moveFocus(evt,1,false)
				} else if(key === dk.PAGE_UP){
					this._moveFocus(evt,Math.floor(this.bodyWrapper.offsetHeight/this.currentNode.offsetHeight),true)
				} else if(key === dk.PAGE_DOWN){
					this._moveFocus(evt,Math.floor(this.bodyWrapper.offsetHeight/this.currentNode.offsetHeight),false)
				} else if(key === dk.HOME){
					this._moveFocus(evt,99999,true)
				} else if(key === dk.END){
					this._moveFocus(evt,99999,false)
				} else if(key === dk.ENTER || key === dk.SPACE || key === " "){
					if(this.msep) {
						this._select(this.currentNode)
					} else {
						this.ok();
					}
					evt.preventDefault()
					evt.stopPropagation()
					dojo.stopEvent(evt)
				} else if(key === dk.ESCAPE){
					this.cancel();
					evt.preventDefault()
					evt.stopPropagation()
					dojo.stopEvent(evt)
				}
			}
		},
		_handleFocus: function(list){
			if(this.currentNode) {
				this.currentNode.focus()
			} else {
				var node = this.listFirstItem(list.container)
				if(node) { 
					this._setCurrent(node);
					if(!this.msep) {
						this._select(node)
					}
					node.focus();
				}
			}
		},
		_moveFocus: function(evt, start, prevDir) {
			var curNode = this.currentNode
			for(; start > 0 ;start--) {
				var newNode = prevDir ? curNode.previousSibling : curNode.nextSibling
				if(null != newNode) curNode = newNode; else break;
			}
			if(curNode !== this.currentNode) {
				if(!this.msep) {
					dojo.setAttr(this.currentNode, "aria-selected", "false");
				}
				this._setCurrent(curNode)
				if(!this.msep) {
					this._select(curNode)
				}
				if(this.currentNode) {
					this.currentNode.focus()
				}
			}
			dojo.stopEvent(evt)
		},
		_getResult: function(){
			var _selected = dojo.query("li.xspPickerItemSelected", this.containerNode);
			if(_selected&&_selected.length) {
				if(this.msep) {
					var val = []; var lbl = [];
					dojo.forEach(_selected, function(node){
						var v=dojo.attr(node,"val");
						val.push(v);
						lbl.push(node.innerHTML||v);
					});
					return {values:val, labels:lbl};
				} else {
					return {values:[dojo.attr(_selected[0],"val")], labels:[_selected[0].innerHTML]};
				}
			}
			return {values:[], labels:[]};
		},
		_updateResult: function() {
			/*this.result.innerHTML = this._getResult().labels.join(',')*/
		}
	}
);
