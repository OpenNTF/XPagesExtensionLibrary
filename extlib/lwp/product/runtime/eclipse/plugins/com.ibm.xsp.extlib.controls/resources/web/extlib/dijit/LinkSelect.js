/*
* © Copyright IBM Corp. 2010, 2015
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
dojo.provide("extlib.dijit.LinkSelect");

dojo.require("dojo.cache")
dojo.require("dijit.form._FormWidget")
dojo.require("extlib.dijit.ExtLib")
dojo.require("dijit.form._FormValueWidget")
dojo.require("dojo.NodeList-manipulate")

dojo.declare(
	'extlib.dijit.LinkSelect',
	dijit.form._FormValueWidget,
	{
		listStyle: '',
		listClass: '',
		itemStyle: '',
		itemClass: '',
		firstItemStyle: '',
		firstItemClass: '',
		lastItemStyle: '',
		lastItemClass: '',
		enabledLinkStyle: '',
		enabledLinkClass: '',
		disabledLinkStyle: '',
		disabledLinkClass: '',
		tabindex: '',
		controlDisabled: '',
		valueList: {},
		templateString: dojo.cache("extlib.dijit", "templates/LinkSelect.html"),
		postCreate: function() {
			this.inherited(arguments)
			for(var i=0; i<this.valueList.length; i++) {
				var val = this.valueList[i].v;
				var st = this.itemStyle;
				var cl = this.itemClass;
				if(i==0) {st=XSP.concatStyle(st,this.firstItemStyle);cl=XSP.concatClass(cl,this.firstItemClass);} 
				if(i==this.valueList.length-1) {st=XSP.concatStyle(st,this.lastItemStyle);cl=XSP.concatClass(cl,this.lastItemClass);}
				var c = dojo.create("li", {}, this.list);
				if(cl) dojo.attr(c,"className",cl);
				if(st) c.style.cssText = st;
				var a = dojo.create("a", {
					href: 'javascript:;',
					val:val,
					role:'button'
				}, c);
				if(!this.readOnly) {
					a.onclick = dojo.hitch(this,this._setCurrent,i,a);
				}
				a.appendChild(dojo.doc.createTextNode(this.valueList[i].l||val))
			}
			this._setValueAttr(this.value);
		},
		_setValueAttr: function(value, priorityChange){
			this.textbox.value = this.value = value;
			this.inherited(arguments);
			var index = 0;
			dojo.query("a",this.list).forEach(function(n) {
				var dis = dojo.attr(n,"val")==value;
				//pull out item label if it exists, else item value
				var nVal = this.valueList[index].l||this.valueList[index].v;
				index++;
				
				n.style.cssText = dis?this.disabledLinkStyle:this.enabledLinkStyle;
				dojo.attr(n,"class",dis?this.disabledLinkClass:this.enabledLinkClass);
				//Mark list node (the anchor's parent) as selected/unselected
				dojo.attr(n,"aria-pressed",dis?"true":"false");
				dojo.attr(n,"aria-disabled",dis?"true":"false");
				dojo.attr(n,"tabindex",dis?"-1":this.tabindex);
			},this);
		},
		_setCurrent: function(idx,a){
			if(this.disabled){ return; }
			this._handleOnChange(this.valueList[idx].v);
			this._setValueAttr(idx>=0?this.valueList[idx].v:'');
			a.focus();
		},
		_onFocus: function(/*String*/ by){
			if(this.disabled){ return; }
			this.inherited(arguments);
		},
		_onTextChange: function(e){
			this._setValueAttr(this.textbox.value)
		},
		_onMouse: function(e){ // WHY THIS?? where is the issue?
			this.inherited(arguments)
		}
	}
);
