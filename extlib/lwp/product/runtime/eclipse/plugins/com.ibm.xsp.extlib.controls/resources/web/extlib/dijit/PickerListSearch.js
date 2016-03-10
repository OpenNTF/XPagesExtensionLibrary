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
dojo.provide("extlib.dijit.PickerListSearch");

dojo.require("dojo.cache")
dojo.require("extlib.dijit.TemplateDialog")
dojo.require("extlib.dijit.ListTextBox")
dojo.require("dojo.i18n")
dojo.require("dojo.string")
dojo.requireLocalization("extlib.dijit","pickers")

dojo.declare(
		'extlib.dijit.PickerListSearch',
		[extlib.dijit.TemplateDialog],
		{
			msep: "",
			allowMultiple: false,
			trim: false,
			maxRowCount: 30,
			listWidth: "230px",
			listHeight: "18em",
			PickerListSearch_Search: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Search ,
			PickerListSearch_SearchFor: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_SearchFor,
            PickerListSearch_OK: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_OK ,
            PickerListSearch_Cancel: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Cancel ,
            PickerName_unSelectedListLabel:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_unSelectedListLabel,
			templateString: dojo.cache("extlib.dijit","templates/PickerListSearch.html"),
			currentNode: null,
			labelSingle: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Value ,
			labelMultiple: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Values ,
			postCreate: function() {
				this.inherited(arguments)
				this._fixStyles(this.templateString)
				this.bodyWrapper.scrollTop = 0;
				var controlValue=this.getControlValue(this.control);
	        	if(this.msep) {
	        		this.inputList.msep = this.msep;
	        	}
				var dj = dijit.byId(this.control);
				if(dj) {
					this.djControl = dj;
					this.inputList._extractLabel = function(s) { 
						return dj._extractLabel(s); 
					}
				}
				this.inputList.attr("value",controlValue);
	        	dojo.connect(this.btSearch,"onclick",this,'_onSearch')
	        	dojo.connect(this.edSearch,"onkeypress",this,'_searchKeyPress')
	        	this.inputText.innerHTML = this.msep ? this.labelMultiple : this.labelSingle;
				this._fetchData()
			},
			ok: function(){
				var v = this.inputList.attr("value")
				var a = v ? (this.msep ? v.split(this.msep) : [v]) : [];
                if(this.trim) a = dojo.map(a,dojo.trim)
				this.updateControl(this.control,{values:a},this.msep)
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
			_setCurrent: function(node) {
				this.currentNode = node
				if(node) {
					this.listScrollTo(this.bodyWrapper,node);
				}
			},
			_select: function(node){
				if(node && node.nodeName.toLowerCase() == "li") {
					if(!this.msep) {
						dojo.query("li.xspPickerItemSelected",this.containerNode).removeClass("xspPickerItemSelected");
					}
					dojo.toggleClass(node,"xspPickerItemSelected");
				}
			},
			_searchKeyPress: function(e){
				if(e.charOrCode===dojo.keys.ENTER){
					this._onSearch()
				}
			},
			_onSearch: function(){
				this._fetchData(this.edSearch.value)
			},
			_fetchData: function(start) {
				var _this = this;
				var count = this.maxRowCount ? "&count="+this.maxRowCount : ""
				var startKey = start ? "&startkeys="+encodeURIComponent(start) : ""
				this.listClear(this.containerNode)
				dojo.xhrGet({
			        url: this.url+count+startKey, 
			        preventCache: this.preventCache,
			        handleAs: "json",
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
					"class": "xspPickerItem",
					"aria-selected": itemIsSelected ? "true" : "false",
					title: val,
					unselectable: "on",
					innerHTML: lbl
				}, this.containerNode);
			},
			_onComplete: function(){
				this.listScrollTo(this.bodyWrapper,null);
				setTimeout(dojo.hitch(this,function(){this.edSearch.focus()}),100);
			},
			_addNode: function(node){
				if(node && node.nodeName.toLowerCase() == "li") {
					var v = this.inputList.attr("value");
					var a = v ? (this.msep ? v.split(this.msep) : [v]) : [];
                    if(this.trim) a = dojo.map(a,dojo.trim)
					var val=dojo.attr(node,"val");
                    
                    var index = dojo.indexOf(a,val)
					if(index < 0) {
						if(this.msep) {
							a.push(val)
						} else {
							a = [val]
						}
						var dj = this.djControl;
						if(dj && dj.labels && !dj.labels[val]) {
							dj.labels[val] = node.textContent || node.innerText;  
						}
					}
					this.inputList.attr("value",a.join(this.msep?this.msep:""));
				}
			},
			_handleClick: function(e){
				this._select(e.target)
				this.currentNode = e.target // Do not scroll! (this._setCurrent(e.target))
				if(!this.msep) { // Select it when not multiple
					this._addNode(e.target)
				}
				e.target.focus();
			},
			_handleDblClick: function(e) {
				this._handleClick(e)
				this._addNode(e.target)
				if(!this.msep) {
					this.ok();
				}
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
						}
						this._addNode(this.currentNode)
						if(!this.msep) {
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
			_handleOnFocus: function(e){
				if(this.currentNode) {
					this.currentNode.focus()
				} else {
					var node = this.listFirstItem(this.containerNode)
					if(node) { 
						this._setCurrent(node);
						node.focus()
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
				return this.inputList.attr("value");
			}
		}
	);
