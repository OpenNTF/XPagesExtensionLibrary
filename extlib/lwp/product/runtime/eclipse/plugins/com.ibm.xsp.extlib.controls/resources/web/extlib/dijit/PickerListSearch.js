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
			trim: false,
			maxRowCount: 30,
			listWidth: "230px",
			listHeight: "18em",
			PickerListSearch_Search: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Search ,
            PickerListSearch_OK: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_OK ,
            PickerListSearch_Cancel: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerListSearch_Cancel ,
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
				var li = dojo.create("li", {
					val: val,
					tabIndex: 0,
					"class": "xspPickerItem",
					title: val,
					unselectable: "on",
					innerHTML: lbl
				}, this.containerNode);
			},
			_onComplete: function(){
				this.listScrollTo(this.bodyWrapper,null);
				setTimeout(dojo.hitch(this,function(){(this.currentNode||this.containerNode).focus()}),100);
			},
			_select: function(node){
				if(node && node.nodeName.toLowerCase() == "li") {
					dojo.query("li.xspPickerItemSelected",this.containerNode).removeClass("xspPickerItemSelected");
					dojo.toggleClass(node,"xspPickerItemSelected");
				}
			},
			_addNode: function(node){
				if(node && node.nodeName.toLowerCase() == "li") {
					var v = this.inputList.attr("value");
					var a = v ? (this.msep ? v.split(this.msep) : [v]) : [];
                    if(this.trim) a = dojo.map(a,dojo.trim)
					var val=dojo.attr(node,"val");
					if(dojo.indexOf(a,val)<0) {
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
			_handleKeyPress: function(e){
				if(e.charOrCode){
					if(!this.currentNode) return;
					var dk = dojo.keys;
					if(e.charOrCode === dk.UP_ARROW){
						this._mv(e,1,true)
					} else if(e.charOrCode === dk.DOWN_ARROW){
						this._mv(e,1,false)
					} else if(e.charOrCode === dk.PAGE_UP){
						this._mv(e,Math.floor(this.bodyWrapper.offsetHeight/this.currentNode.offsetHeight),true)
					} else if(e.charOrCode === dk.PAGE_DOWN){
						this._mv(e,Math.floor(this.bodyWrapper.offsetHeight/this.currentNode.offsetHeight),false)
					} else if(e.charOrCode === dk.HOME){
						this._mv(e,99999,true)
					} else if(e.charOrCode === dk.END){
						this._mv(e,99999,false)
					} else if(e.charOrCode === dk.ENTER || e.charOrCode === ' '){
						dojo.stopEvent(e)
						this._addNode(this.currentNode)
						if(!this.msep) {
							this.ok();
						}
					}
				}
			},	
			_handleOnFocus: function(e){
				//console.log("_handleOnFocus");
				if(this.currentNode) {
					this.currentNode.focus()
				} else {
					var node = this.listFirstItem(this.containerNode,this.currentNode)
					if(node) { 
						this._setCurrent(node);
						this._select(node)
						node.focus();
					}
				}
			},
			_mv: function(e,n,d) {
				var nc = this.currentNode
				for(;n;n--) {
					var p = d ? nc.previousSibling : nc.nextSibling
					if(p) nc=p; else break;
				}
				if(nc!==this.currentNode) {
					this._setCurrent(nc)
					this._select(nc)
					if(this.currentNode) this.currentNode.focus()
				}
				dojo.stopEvent(e)
			},
			_getResult: function(){
				return this.inputList.attr("value");
			}
		}
	);
