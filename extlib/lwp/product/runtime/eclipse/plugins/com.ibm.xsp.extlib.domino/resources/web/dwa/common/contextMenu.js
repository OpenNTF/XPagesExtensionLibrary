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

dojo.provide("dwa.common.contextMenu");

dojo.require("dwa.common.dropdownManager");
dojo.require("dwa.common.menu");

dojo.declare(
	"dwa.common.contextMenu",
	null,
{
	menuInfo: null,
	defaultImageModule: "dwa.common",
	defaultConsolidatedImage: "images/basicicons.gif",
	focusMethod: null,
	activeIndex: 0,
	id: "",
	
	_menu: null,
	_dropdownManager: null,
	_started: false,
	
	constructor: function(/*Object*/args) {
		if (args) {
			dojo.mixin(this, args);
		}
		this._dropdownManager = dwa.common.dropdownManager.get("root");
		
		var menuNode = dojo.doc.createElement("div");
		if (this.id) {
			menuNode.id = this.id;
		}
		dojo.addClass(menuNode, "s-hidden");
		dojo.doc.body.appendChild(menuNode);
		var props = {
			menuInfo: this.menuInfo,
			dropdownManagerId: "root",
			defaultImageModule: this.defaultImageModule,
			defaultConsolidatedImage: this.defaultConsolidatedImage,
			focusMethod: this.focusMethod,
			activeIndex: this.activeIndex
		};
		this._menu = new dwa.common.menu(props, menuNode);
	},
	
	show: function(/*Object*/ev) {
		dojo.stopEvent(ev);
		if (!this._started) {
			this._menu.startup();
			dojo.removeClass(this._menu.domNode, "s-hidden");
			this._started = true;
		} else {
			this._menu.refresh();
		}
		this._dropdownManager.nContainerWidth = this._dropdownManager.nContainerHeight = 0;
		var x = dojo._isBodyLtr() ? ev.clientX : dojo.doc.body.clientWidth - ev.clientX;
		var y = ev.clientY;
		this._dropdownManager.oPos = new dwa.common.utils.pos(x, y);
		this._dropdownManager.show(ev, this._menu.id);
	},
	
	hide: function() {
		this._menu.deactivate(true);
	},
	
	destroy: function() {
		this._menu.destroy();
	}
});
