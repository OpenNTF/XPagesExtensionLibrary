/*
* © Copyright IBM Corp. 2014
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
dojo.provide("extlib.responsive.dijit.xsp.bootstrap.ListTextBox");

dojo.require("extlib.dijit.ListTextBox")

dojo.declare(
	'extlib.responsive.dijit.xsp.bootstrap.ListTextBox',
	extlib.dijit.ListTextBox,
	{
		listClass: 'xspFilters xspInlineList',
		linkClass: 'xspFilter',
		linkRole: 'button',
		closeClass: 'glyphicon glyphicon-remove xspClose',
		templateString: dojo.cache("extlib.dijit", "templates/ListTextBox.html"),
		displayLabel:false,
		labels: {},
		
		_extractLabel: function(s){
			return this.labels && this.displayLabel ? this.labels[s]||s : s; 
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
					"aria-label": this.msgs.ListBoxAria_Remove
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
		}
	}
);
