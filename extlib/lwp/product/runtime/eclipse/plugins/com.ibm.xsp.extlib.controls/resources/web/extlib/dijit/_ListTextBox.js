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
dojo.provide("extlib.dijit._ListTextBox");

dojo.require("dojo.cache")
dojo.require("dijit.form._FormWidget")
dojo.require("extlib.dijit.ExtLib")
dojo.require("dijit.form._FormValueWidget")
dojo.require("dojo.i18n")
dojo.require("dojo.string")
dojo.requireLocalization("extlib.dijit","listbox")

dojo.declare(
	'extlib.dijit._ListTextBox',
	dijit.form._FormValueWidget,
	{
		listClass: 'lotusFilters lotusInlineList',
		linkClass: 'lotusFilter',
        linkRole: 'button',
		closeClass: 'lotusClose',
		closable:true,
        tabIndex: -1, // We don't want to focus the container element
		readOnly:false,
		msep: ',',
		msgs: dojo.i18n.getLocalization("extlib.dijit","listbox",this.lang),
		templateString: dojo.cache("extlib.dijit", "templates/ListTextBox.html"),
		_extractLabel: function(s){
			return s; 
		},
		_setValueAttr: function(/*Number*/ value, /*Boolean, optional*/ priorityChange){
			this.textbox.value = this.value = value;
			this.inherited(arguments);
			this._clear();
			var inp = this.value
			var v = this.msep ? inp.split(this.msep) : [inp]
			var pos = 0;
            for(var i in v) {
            	if(v[i]) this._createChoiceItem(v[i],pos++)
            }
		},
		_clear: function(){
			var c = this.list; 
			while(c.hasChildNodes()) {c.removeChild(c.lastChild);}
		},
		_createChoiceItem: function(val, pos) {
			var lbl = this._extractLabel(val);
			// Bidi Mirroring
			var _pos = this.isLeftToRight()? 'last': 'first';

			var choiceItem = dojo.create("span", {
				val: val,
				style: pos>0?'margin-left: 5px':'',
                tabIndex: -1  // User actions dont happen on this node
			}, this.list, _pos);
			var a = dojo.create("a", {
				href: 'javascript:;',
                tabIndex: this.tabIndex != -1 ? this.tabIndex : "0", 
                "role": this.linkRole,
				"class": this.linkClass
			}, choiceItem);
			a.appendChild(dojo.doc.createTextNode(lbl))
			if(this.closable && !this.readOnly) {
                var removeChoiceItem = dojo.hitch(this, function(e) {
                    if(!this.disabled) {
                        this.list.removeChild(choiceItem);this._saveValue()
                    }
                });
				dojo.query(choiceItem).onclick(removeChoiceItem).onkeydown(dojo.hitch(this, function(ev){
                    if(ev.keyCode == dojo.keys.ENTER || ev.keyCode == dojo.keys.SPACE){
                    	dojo.stopEvent(ev);
                        removeChoiceItem();
                    }
                }));
				var a2 = dojo.create("span", {
					"class": this.closeClass,
					"aria-label": this.msgs.ListBoxAria_Remove,
					innerHTML: "x"
				}, a, _pos);
			}
			if(this.readOnly) {
				var ariaReadOnly = dojo.string.substitute(
						this.msgs.ListBoxAria_ReadOnly,
						[val]
						);
				dojo.attr(a, "aria-label", ariaReadOnly)
				dojo.attr(a, "aria-disabled", "true")
			}
			if(this.disabled) {
				dojo.attr(a, "aria-disabled", "true")
			}
		},
		_saveValue: function() {
			var val = ""
			var _sel = dojo.query(">", this.list);
			if(_sel&&_sel.length) {
				if(this.msep) {
					var sel = [];
					dojo.forEach(_sel, function(node){sel.push(dojo.attr(node,"val"));});
					val = sel.join(this.msep)
				} else {
					val = dojo.attr(_sel[0],"val")
				}
			}
			this._setValueAttr(val,true)
		},
		_onTextChange: function(e){
			this._setValueAttr(this.textbox.value,false)
		},
		_onMouse: function(e){ // WHY THIS?? where is the issue?
			this.inherited(arguments)
		}
	}
);
