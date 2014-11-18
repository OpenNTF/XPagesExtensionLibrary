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
dojo.provide("extlib.dijit.PickerName");

dojo.require("dojo.cache")
dojo.require("extlib.dijit.ExtLib")
dojo.require("extlib.dijit.TemplateDialog")
dojo.require("dojo.i18n")
dojo.require("dojo.string")
dojo.requireLocalization("extlib.dijit","pickers")

dojo.declare(
	'extlib.dijit.PickerName',
	[extlib.dijit.TemplateDialog],
	{
		msep: "",
		trim: false,
		sources: "",
		maxRowCount: 50,
		listWidth: "230px",
		listHeight: "22em",
		PickerName_Search: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_Search,
        PickerName_Add: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_Add,
        PickerName_Remove:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_Remove,
        PickerName_RemoveAll:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_RemoveAll,
        PickerName_OK:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_OK,
        PickerName_Cancel:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_Cancel,
        PickerName_unSelectedListLabel:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_unSelectedListLabel,
        PickerName_selectedListLabel:dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerName_selectedListLabel,
		templateString: dojo.cache("extlib.dijit","templates/PickerName.html"),
		list1:null,
		list2:null,
		postCreate: function() {
			this.inherited(arguments)
			this._fixStyles(this.templateString)
        	dojo.connect(this.btAdd,"onclick",this,'_onAdd')
        	dojo.connect(this.btRemove,"onclick",this,'_onRemove')
        	dojo.connect(this.btRemoveAll,"onclick",this,'_onRemoveAll')
        	dojo.connect(this.btSearch,"onclick",this,'_onSearch')
        	dojo.connect(this.edSearch,"onkeypress",this,'_searchKeyPress')
        	var nlsStrings=dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang);
            var searchIn=nlsStrings.PickerName_SearchIn;
            var searchFor=nlsStrings.PickerName_SearchFor;
            this.tdSearchIn.innerHTML=searchIn+":";
            this.tdSearchFor.innerHTML=searchFor+":";
            
            this.list1={body:this.list1Body,container:this.list1Container,current:null}
			this.list2={body:this.list2Body,container:this.list2Container,current:null}
			if(!this.msep) {
				dojo.style(this.btRemove,{display:'none'})
			}

			var controlValue=this.getControlValue(this.control);
			if(controlValue) {
				
				var controlValues = this.msep ? controlValue.split(this.msep) : [controlValue]
				for(var i in controlValues) {
					var val = this.trim?dojo.trim(controlValues[i]):controlValues[i]
					if(val) {
						this._createLi(this.list2,val,XSP.extractCN(val))
					}
				}
				this._resetTabIndexes();
			}else
			{
				this._resetTabIndexes();
			}
			if(this.sources) {
				dojo.forEach(this.sources,function(s) {
					var opt = dojo.doc.createElement('option');
					opt.text = s;         
     
                    if(dojo.isIE == 7){
                        this.cbSources.add(opt); // IE
                    }
                    else{
                        this.cbSources.add(opt,null); // Doesn't work in IE 7
                    }				
				},this);
			} else {
				dojo.style(this.pnSources,"display","none");
			}
			
			// GVT issue - SPR: #PDCW8T4LDV
			// Originally found on traveller template - "remove" and "remove all" buttons don't move to the left in RTL mode.
			// Get a handle on the remove button, set its parent div to float left if in RTL
			if(!dojo._isBodyLtr())
			{
				this.btRemove.parentNode.style.styleFloat = "left"; // IE
				this.btRemove.parentNode.style.cssFloat  = "left";  // non - IE
			}
			this._fetchData();
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
        _resetTabIndexes: function(){
        	if(this.list2.container.childNodes.length > 0)
        	{
        		dojo.setAttr(this.list2.container, "tabindex", 0);
        		dojo.removeClass(this.list2.container,"xspPickerBodyEmpty");
    			dojo.setAttr(this.btRemoveAll, "tabindex", 0);
    			dojo.setAttr(this.btRemove, "tabindex", 0);
        	}
        	else
        	{
        		dojo.setAttr(this.list2.container, "tabindex", -1);
        		dojo.addClass(this.list2.container,"xspPickerBodyEmpty");
    			dojo.setAttr(this.btRemoveAll, "tabindex", -1);
    			dojo.setAttr(this.btRemove, "tabindex", -1);	
        	}
        	
        },
        _onAdd: function(){
			var _selected = dojo.query("li.xspPickerItemSelected", this.list1.container);
			if(_selected&&_selected.length) {
				this._addNode(_selected[0])
			}
		},
		_onRemove: function(){
			var _sel = dojo.query("li.xspPickerItemSelected", this.list2.container);
			dojo.forEach(_sel, function(item) {
				this._removeNode(item);
			},this)
		},
		_onRemoveAll: function(){
			this._clear(this.list2);
			this._resetTabIndexes();
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
			var src = this.sources ? "&source="+this.cbSources.selectedIndex : ""
			var count = this.maxRowCount ? "&count="+this.maxRowCount : ""
			var startKey = start ? "&startkeys="+encodeURIComponent(start) : ""
			this._onStart()
			dojo.xhrGet({
		        url: this.url+count+startKey+src, 
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
			var val = dojo.trim(item["@value"])
			var lbl = item["@label"] || val
			var li = this._createLi(this.list1,val,lbl);
		},
		_onStart: function(){
			this._clear(this.list1)
		},
		_onComplete: function(){
			var firstNode = this.list1.firstChild
			if(firstNode) {
				this._select(this.list1,firstNode)
				this._setCurrent(this.list1,firstNode)
			}
			setTimeout(dojo.hitch(this,function(){this.edSearch.focus()}),100);
		},
		_addNode: function(node){
			if(node) {
				var val = dojo.attr(node,"val");
				firstNode = (this.list2.container.childNodes.length == 0);
				if(dojo.query("[val='"+val+"']",this.list2.container).length>0) return;
				if(!this.msep) {this._clear(this.list2);}
				this._createLi(this.list2,val,XSP.extractCN(val));
				if(firstNode)
					{
						this._resetTabIndexes();
					}
			}
		},
		_removeNode: function(node){
			if(node) {
				var n = node.previousSibling || node.nextSibling;
				this._setCurrent(this.list2,n);
				this.list2.container.removeChild(node);
				if(n == null)
				{
					 this._resetTabIndexes();
				}
			}
		},
		_getResult: function(){
			var _sel = dojo.query("li", this.list2.container);
			if(_sel&&_sel.length) {
				if(this.msep) {
					var val = []; var lbl = [];
					dojo.forEach(_sel, function(node){
						val.push(dojo.attr(node,"val"));
						lbl.push(node.innerHTML);
					});
					return {values:val, labels:lbl};
				} else {
					return {values:[dojo.attr(_sel[0],"val")], labels:[_sel[0].innerHTML]};
				}
			}
			return {values:[], labels:[]};
		},
		
		// Source combo
		_onSrcChange: function(v){
			this._fetchData()
		},
		
		// List1 event handlers
		_list1Click: function(e){
			this._listClick(this.list1,e)
		},
		_list1DblClick: function(e) {
			this._onAdd()
			if(!this.msep) {
				this.ok();
			}
		},
		_list1KeyPress: function(e){
			this._listKeyPress(this.list1,e)
			if(e.charOrCode === dojo.keys.ENTER || e.charOrCode === ' '){
				dojo.stopEvent(e)
				this._addNode(e.target)
			} else if(e.charOrCode === dojo.keys.TAB){
				(e.shiftKey ? this.btSearch : this.btAdd).focus();
				dojo.stopEvent(e)
			}
		},		
		_list1OnScroll: function(e){
			
		},
		_list1OnFocus: function(e){
			this._listFocus(this.list1)
		},
		_list1Set: function(e){
			this._set(this.list1,e)
		},
		_list1Unset: function(e){
			dojo.removeClass(e.target,"xspPickerItemHover");
		},

		// List2 event handlers
		_list2Click: function(e){
			this._listClick(this.list2,e)
		},
		_list2DblClick: function(e) {
			this._removeNode(e.target);
		},
		_list2KeyPress: function(e){
			this._listKeyPress(this.list2,e)
			if(e.charOrCode === dojo.keys.ENTER || e.charOrCode === ' '){
				this._removeNode(e.target);
				dojo.stopEvent(e)
			} else if(e.charOrCode === dojo.keys.TAB){
				var remButton = this.msep ? this.btRemove : this.btRemoveAll;
				(e.shiftKey ? this.btAdd : remButton).focus();
				dojo.stopEvent(e)
			}
		},
		_list2OnFocus: function(e){
			this._listFocus(this.list2)
		},
		_list2Set: function(e){
			this._set(this.list2,e)
		},
		_list2Unset: function(e){
			this._unset(this.list2,e)
		},
		
		// Listbox common implementation
		_select: function(list,node){
			if(node && node.nodeName.toLowerCase() == "li") {
				dojo.query("li.xspPickerItemSelected",list.container).removeClass("xspPickerItemSelected");
				dojo.toggleClass(node,"xspPickerItemSelected");
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
		_createLi: function(list,val,lbl) {
			if(!val) return;
			return dojo.create("li", {
				val: val,
				tabIndex: -1,
				role:"option",
				"aria-selected": "false",
				"class": "xspPickerItem",
				title: val,
				unselectable: "on",
				innerHTML: lbl
			}, list.container);
		},
		_setCurrent: function(list,node) {
			prevCurrent = list.current
			if(prevCurrent) {
				dojo.setAttr(prevCurrent, "tabindex", -1);
				this._toggleAttribute(prevCurrent, "aria-selected", "true", "false")
			}
			list.current = node
			if(node) {
				this.listScrollTo(list,list.current);
				dojo.setAttr(node, "tabindex", 0);
				this._toggleAttribute(node, "aria-selected", "true", "false")
				this._select(list,node);
				node.focus();
			}
		},
		_clear: function(list){
			this.listClear(list.container);
			list.current = null;
		},
		_listFocus: function(list){
			if(list.current) {
				list.current.focus()
			} else {
				var node = this.listFirstItem(list.container)
				if(node) { 
					this._setCurrent(list, node);
					node.focus();
					this._select(list,node);
				}
			}
		},
		_set: function(list,e){
			if(e.target!==list.body){
				dojo.addClass(e.target,"xspPickerItemHover");
			}
		},
		_unset: function(list,e){
			dojo.removeClass(e.target,"xspPickerItemHover");
		},
		_listClick: function(list,e){
			this._select(list,e.target)
			this._setCurrent(list,e.target)
		},
		_listKeyPress: function(list,e){
			if(e.charOrCode){
				if(!list.current) return;
				var dk = dojo.keys;
				if(e.charOrCode === dk.UP_ARROW){
					this._mv(list,e,1,true)
				} else if(e.charOrCode === dk.DOWN_ARROW){
					this._mv(list,e,1,false)
				} else if(e.charOrCode === dk.PAGE_UP){
					this._mv(list,e,Math.floor(list.body.offsetHeight/list.current.offsetHeight),true)
				} else if(e.charOrCode === dk.PAGE_DOWN){
					this._mv(list,e,Math.floor(list.body.offsetHeight/list.current.offsetHeight),false)
				} else if(e.charOrCode === dk.HOME){
					this._mv(list,e,99999,true)
				} else if(e.charOrCode === dk.END){
					this._mv(list,e,99999,false)
				}
			}
		},				
		_mv: function(list,e,n,d) {
			var nc = list.current
			for(;n;n--) {
				var p = d ? nc.previousSibling : nc.nextSibling
				if(p) nc=p; else break;
			}
			if(nc!==list.current) {
				
				this._setCurrent(list,nc)
				this._select(list,nc)
				if(list.current) list.current.focus()
			}
			dojo.stopEvent(e)
		}
	}
);
