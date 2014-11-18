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

dojo.provide("dwa.common.menu");

dojo.require("dijit._Widget");
dojo.require("dwa.common.dropdownManager");
dojo.require("dwa.common.dropdownBox");
dojo.require("dwa.common.utils");
dojo.require("dwa.common.listeners");

dojo.requireLocalization("dwa.common", "menu");

var D_ALIGN_DEFAULT = "left";
var D_ALIGN_REVERSE = "right";

dojo.declare(
	"dwa.common.menu",
	[dijit._Widget, dwa.common.dropdownBox],
{
	menuInfo: null,
	defaultImageModule: "dwa.common",
	defaultConsolidatedImage: "images/basicicons.gif",
	focusMethod: null,
	activeIndex: 0,
	autoRender: true,
	dropdownManagerId: "root",
	supportScreenReader: true,
	handleFocus: true,
	
	isRTL: false,
	_parentInfo: null,
	_previousFocusElem: null,
	_children: null,
	_selectedIndex: 0,
	_numOfSeparators: 0,
	_isSelectionExists: false,
	_disabledClass: "s-menu-text-disabled",
	_checkMarkIndexes: {},
	
	postMixInProperties: function() {
		if (!dojo._isBodyLtr()) {
			this.isRTL = true;
			D_ALIGN_DEFAULT = "right";
			D_ALIGN_REVERSE = "left";
		}
		this._msgs = dojo.i18n.getLocalization("dwa.common", "menu", this.lang);
		this._children = [];
	},
	
	postCreate: function() {
		this.sId = this.id;
		dojo.addClass(this.domNode, "s-popup");
		this.domNode.setAttribute("com_ibm_dwa_ui_dropdownMenu_resetActive", "false");
		this.domNode.style.padding = "4px";
		
		var numOfSeparators = 0;
		dojo.forEach(this.menuInfo, function(menu, i) {
			if (menu.isSeparator) {
				numOfSeparators++;
				return;
			}
			this._isSelectionExists = this._isSelectionExists || !!(menu.isChecked != undefined || menu.radioGroupId);
			var n = i - numOfSeparators;
			if (menu.radioGroupId) {
				if (this._checkMarkIndexes[menu.radioGroupId]) {
					this._checkMarkIndexes[menu.radioGroupId].push(n);
				} else {
					this._checkMarkIndexes[menu.radioGroupId] = [n];
				}
				if (menu.isChecked == undefined) {
					menu.isChecked = false;
				}
			}
		}, this);
		this._numOfSeparators = numOfSeparators;
		
		for (var radioGroupId in this._checkMarkIndexes) {
			var indexes = this._checkMarkIndexes[radioGroupId];
			var isCheckedIndexes = [];
			for (var i = 0; i < indexes.length; i++) {
				if (this.menuInfo[indexes[i]].isChecked) {
					isCheckedIndexes.push(indexes[i]);
				}
			}
			if (isCheckedIndexes.length == 0) {
				this.menuInfo[indexes[0]].isChecked = true;
			} else if (1 < isCheckedIndexes.length) {
				for (var j = 1; j < isCheckedIndexes.length; j++) {
					this.menuInfo[isCheckedIndexes[j]].isChecked = false;
				}
			}
		}
	},
	
	startup: function() {
		if (this._started) {
			return;
		}
		this._started = true;
		if (this.autoRender) {
			this.render();
		}
	},
	
	destroy: function() {
		for (var i = 0; i < this._children.length; i++) {
			this._children[i].destroy();
		}
		this.inherited(arguments);
	},
	
	render: function() {
		this.draw();
		if (!this._parentInfo) {
			this._previousFocusElem = document.activeElement;
		}
		this._initActive();
		this._connectEvents();
	},
	
	activate: function(oDropdownManager) {
		this.inherited(arguments);
		this._initActive();
	},
	
	handleKeyDown: function(ev) {
		switch (ev.keyCode) {
			case 9: // tab
				dojo.stopEvent(ev);
				return;
			case 13: // enter
			case 32: // space
				dojo.stopEvent(ev);
				var targetNode = this._getRow(this._selectedIndex);
				this._dispatchOnClickEvent(targetNode);
				if (targetNode.getAttribute("havechild") == "false" && !dojo.hasClass(targetNode, this._disabledClass)) {
					this._hide();
				}
				return;
			case 27: // esc
			case 37: // left
			case 39: // right
				dojo.stopEvent(ev);
				var targetNode = this._getRow(this._selectedIndex);
				// close menu
				if (!this._parentInfo && ev.keyCode == 27) {
					this._hide();
				// close sub menu
				} else if (ev.keyCode == 27 || this.isRTL && ev.keyCode == 39 || !this.isRTL && ev.keyCode == 37) {
					if (this._parentInfo) {
						this._updateFocus(this._parentInfo.menu.sId, this._parentInfo.childIndex);
						var oDropdownManager = dwa.common.dropdownManager.get(this.dropdownManagerId);
						oDropdownManager.hide(null, oDropdownManager.aoActiveDrops.length - 1);
					}
				// open
				} else if (targetNode.getAttribute("havechild") == "true" && !dojo.hasClass(targetNode, this._disabledClass)) {
					this.setActive(this._selectedIndex, true);
				}
				return;
			case 38: // up
				dojo.stopEvent(ev);
				this.setActive(this._selectedIndex == 0 ? this.menuInfo.length - this._numOfSeparators - 1 : this._selectedIndex - 1, false);
				return;
			case 40: // down
				dojo.stopEvent(ev);
				this.setActive(this._selectedIndex == this.menuInfo.length - this._numOfSeparators - 1 ? 0 : this._selectedIndex + 1, false);
				return;
		}
	},
	
	onBlur: function(ev) {
		if (!dwa.common.menu.prototype.isNextFocusOnMenu) {
			this.deactivate(true);
		}
	},
	
	refresh: function() {
		for (var i = 0; i < this._children; i++) {
			this._children[i].refresh();
		}
		this._resetLabels();
		this._resetCheckMarkIcons();
		this._resetDisabledRows();
	},
	
	getSelectedLabelText: function() {
		var td = dojo.byId(this.sId + "-td-" + this._selectedIndex);
		var imgTags = td.getElementsByTagName("img");
		if(0 < imgTags.length) {
			return dojo.attr(imgTags[0], "alt");
		} else {
			return dojo.trim(td[dojo.isIE ? "innerText" : "textContent"] || "");
		}
	},
	
	_getRow: function(nIndex) {
		return dojo.byId(this.sId + '-item-' + nIndex);
	},
	
	_connectEvents: function() {
		this.connect(this.domNode, "onkeydown", "handleKeyDown");
		this.connect(this.domNode, "oncontextmenu", function(ev) {
			dojo.stopEvent(ev);
		});
		var numOfSeparators = 0;
		dojo.forEach(this.menuInfo, function(menu, i) {
			if (menu.isSeparator) {
				numOfSeparators++;
				return;
			}
			var targetNode = this._getRow(i - numOfSeparators);
			var n = i - numOfSeparators;
			if (menu.subMenu) {
				this.connect(targetNode, "onclick", function(ev) {
					if (menu.isDisabled) {
						dojo.stopEvent(ev);
					} else {
						this.showChild(n, ev);
					}
				});
			} else {
				this.connect(targetNode, "onclick", function(ev) {
					if (menu.isDisabled) {
						dojo.stopEvent(ev);
					} else {
						if (menu.radioGroupId) {
							dojo.forEach(this._checkMarkIndexes[menu.radioGroupId], function(index) {
								if (index == n && !this._isCheckMarkIconShown(index) || index != n && this._isCheckMarkIconShown(index)) {
									this.menuInfo[index].isChecked = this._toggleCheckMark(index);
								}
							}, this);
						} else if (menu.isChecked != undefined) {
							menu.isChecked = this._toggleCheckMark(n);
						}
						if (menu.action) {
							var args = menu.args == undefined ? [] : menu.args;
							args = dojo.isArray(args) ? args : [args];
							if (menu.isChecked != undefined && !menu.radioGroupId) {
								args = args.concat([menu.isChecked]);
							}
							if (typeof(menu.action) == "function") {
								menu.action.apply(menu.context || null, args);
							} else {
								var _scope = menu.scope || dojo.global;
								_scope[menu.action].apply(menu.context || menu.scope || null, args);
							}
						}
						dojo.stopEvent(ev);
						this._hide();
					}
				});
			}
			this.connect(targetNode, "onmouseover", function(ev) {
				this.setActive(n, (menu.isDisabled || !menu.subMenu ? false : true));
			});
		}, this);
	},
	
	_updateFocus: function(sId, nIndex) {
		if(!this.handleFocus) {
			this.activeIndex = this._selectedIndex = nIndex;
			this.setHighlightRow(nIndex);
			return;
		}
		var nextFocusElem = dojo.byId(sId + '-td-' + nIndex);
		if (nextFocusElem) {
			dwa.common.menu.prototype.isNextFocusOnMenu = true;
			try {
				nextFocusElem.focus();
				this.activeIndex = this._selectedIndex = nIndex;
			} catch(e) {
			} finally {
				dwa.common.menu.prototype.isNextFocusOnMenu = false;
			}
		}
	},
	
	_initActive: function() {
		if (this.menuInfo[this.activeIndex] && !this.menuInfo[this.activeIndex].isSeparator) {
			this.setActive(this.activeIndex, false);
		} else {
			for (var i = 0; i < this.menuInfo.length; i++) {
				if (!this.menuInfo[i].isSeparator && !this.menuInfo[i].isDisabled) {
					this.setActive(i, false);
					break;
				}
			}
		}
	},
	
	_hide: function() {
		if (this.focusMethod) {
			this.focusMethod();
		} else if (this._previousFocusElem) {
			try {
				this._previousFocusElem.focus();
			} catch(e) {
			}
		}
		this.deactivate(true);
	},
	
	_dispatchOnClickEvent: function(oActive) {
		if (!dojo.isMozilla && !dojo.isWebKit) {
			oActive.fireEvent('onclick');
		} else {
			var evClick = dojo.doc.createEvent('MouseEvents');
			evClick.initMouseEvent('click', true, true, dojo.global, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
			oActive.dispatchEvent(evClick);
		}
	},
	
	_resetLabels: function() {
		for (var n = 0; n < this.menuInfo.length - this._numOfSeparators; n++) {
			var displayedLabel = dojo.byId(this.sId + '-td-' + n).innerHTML;
			var descriptorIndex = this._getRow(n).getAttribute("descriptorIndex");
			var menuInfoLabel = this.menuInfo[descriptorIndex].label;
			if (displayedLabel != menuInfoLabel) {
				dojo.byId(this.sId + '-td-' + n).innerHTML = menuInfoLabel;
			}
		}
	},
	
	_toggleCheckMark: function(n) {
		var transparentElem = dojo.byId(this.sId + '-check-' + n + '-transparent');
		var iconElem = dojo.byId(this.sId + '-check-' + n + '-icon');
		var isShown = this._isCheckMarkIconShown(n);
		dojo.removeClass(isShown ? transparentElem : iconElem, "s-nodisplay");
		dojo.addClass(isShown ? iconElem : transparentElem, "s-nodisplay");
		dojo.removeClass(isShown ? iconElem : transparentElem, "s-img-display");
		dojo.addClass(isShown ? transparentElem : iconElem, "s-img-display");
		return !isShown;
	},
	
	_resetCheckMarkIcons: function() {
		if (!this._isSelectionExists) {
			return;
		}
		for (var n = 0; n < this.menuInfo.length - this._numOfSeparators; n++) {
			var row = this._getRow(n);
			var descriptorIndex = row.getAttribute("descriptorIndex");
			if (this.menuInfo[descriptorIndex].isChecked == undefined) {
				continue;
			}
			var isShown = this._isCheckMarkIconShown(n);
			if (this.menuInfo[descriptorIndex].isChecked != undefined) {
				if (this.menuInfo[descriptorIndex].isChecked && !isShown || !this.menuInfo[descriptorIndex].isChecked && isShown) {
					this._toggleCheckMark(n);
				}
			}
		}
	},
	
	_isCheckMarkIconShown: function(n) {
		return dojo.hasClass(dojo.byId(this.sId + '-check-' + n + '-transparent'), "s-nodisplay");
	},
	
	_resetDisabledRows: function() {
		for (var n = 0; n < this.menuInfo.length - this._numOfSeparators; n++) {
			var row = this._getRow(n);
			var descriptorIndex = row.getAttribute("descriptorIndex");
			var isDisabled = !!this.menuInfo[descriptorIndex].isDisabled;
			if (dojo.hasClass(row, this._disabledClass) && !isDisabled) {
				dojo.removeClass(row, this._disabledClass);
			} else if (!dojo.hasClass(row, this._disabledClass) && isDisabled) {
				dojo.addClass(row, this._disabledClass);
			}
			var label = this.menuInfo[descriptorIndex].label;
			var ariaLabel = isDisabled ? dwa.common.utils.formatMessage(this._msgs["L_MENU_ITEM_DISABLED"], label) : (this.menuInfo[descriptorIndex].subMenu ? dwa.common.utils.formatMessage(this._msgs["L_MENU_ITEM_SUBMENU"], label) : label);
			if (8 <= dojo.isIE || 3 <= dojo.isMozilla) {
				dojo.byId(this.sId + '-item-' + n + '-label').innerHTML = ariaLabel;
			} else if (dojo.isIE <= 7 || dojo.isMozilla <= 2) {
				row.title = ariaLabel;
			}
		}
	},
	
	// l_Menu.h#com_ibm_dwa_ui_dropdownMenu.prototype.draw()
	draw: function() {
		var D_NoUserSelect = dojo.isMozilla ? '-moz-user-select:none;' : (dojo.isWebKit ? '-webkit-user-select:none;' : '');
		var oElem = this.domNode;
		var aoNodes = [];
		for (var n = 0; oElem.firstChild;) {
			var oNode = oElem.removeChild(oElem.firstChild);
			if (oNode.nodeName != '#text')
				aoNodes[n++] = oNode;
		}
		var asHtml = [];
		var asAriaLabel = [];
		var sTransparentGif = dojo.moduleUrl("dwa.common", "images/transparent.gif");
		var sHighlightClass = 's-list-selected s-list-text-selected';
		var fIconExists;
		var asConsolidatedImages = [];
		
		for (var i = 0; i < Math.max(aoNodes.length, this.menuInfo.length); i++) {
			asConsolidatedImages[i] = dojo.moduleUrl(this.menuInfo[i].imageModule || this.defaultImageModule, this.menuInfo[i].consolidatedImage || this.defaultConsolidatedImage);
			var sIcon = (this.menuInfo[i].iconInfo ? (dojo.isArray(this.menuInfo[i].iconInfo) ? this.menuInfo[i].iconInfo.join(' ') : this.menuInfo[i].iconInfo) : "");
			var sCaption = this.menuInfo[i].label + "";
			var asDescriptors = sIcon ? [sIcon, sCaption] : [sCaption];
			fIconExists = fIconExists || asDescriptors.length > 1;
		}
		for (var n = 0, i = 0; i < Math.max(aoNodes.length, this.menuInfo.length); i++, n += (!fSeparator - 0)) {
			var sIcon = (this.menuInfo[i].iconInfo ? (dojo.isArray(this.menuInfo[i].iconInfo) ? this.menuInfo[i].iconInfo.join(' ') : this.menuInfo[i].iconInfo) : "");
			var sCaption = this.menuInfo[i].label + "";
			var asDescriptors = sIcon ? [sIcon, sCaption] : [sCaption];
			var fChild = !!this.menuInfo[i].subMenu;
			var asControl = asDescriptors[asDescriptors.length - 1].match(/^([0-9\/]*)(#|\*|_)/);
			var fSeparator = !!this.menuInfo[i].isSeparator;
			var sClass = 's-menu-item s-outline-text';
			if (fSeparator) {
				asHtml[asHtml.length] = '<tr>'
				 + '<td colspan="3" onmousedown="return false;" unselectable="on" style="height:2px;overflow:hidden;' + D_NoUserSelect + '">'
				 + '<img alt="" class="s-img-display" width="1" height="1" src="' + sTransparentGif + '"/>'
				 + '</td>'
				 + '</tr>'
				 + '<tr>'
				 + '<td colspan="3" unselectable="on" onmousedown="return false;"'
				 + ' style="height:3px;overflow:hidden;border-top:solid #999999 1px;' + D_NoUserSelect + '">'
				 + '<img alt="" class="s-img-display" width="1" height="1" src="' + sTransparentGif + '"/>'
				 + '</td>'
				 + '</tr>';
			} else {
				var sImg = '<span style="position:relative;display:inline-block;width:3px;height:' + (!this.isRTL ? 5 : 7) + 'px;overflow:hidden">'
					+ '<img alt="'+this._msgs["L_MENU_SUBMENU"]+'" class="s-img-display" src="' + dojo.moduleUrl("dwa.common", "images/basicicons.gif") + '" style="top:-20px;left:-' + (!this.isRTL ? 50 : 120) + 'px;position:absolute;border-width:0px;"/>'
					+ '</span>';
				var sCheck;
				if (this.menuInfo[i].isChecked == undefined) {
					sCheck = '';
				} else {
					sCheck = '<img id="' + this.sId + '-check-' + n + '-transparent" src="' + sTransparentGif + '" width="7" height="7" class="' + (this.menuInfo[i].isChecked ? 's-nodisplay' : 's-img-display') + '"/>'
					+ '<span style="position:relative;width:7px;height:7px;overflow:hidden"'
					+ ' class="' + (!this.menuInfo[i].isChecked ? 's-nodisplay' : 's-img-display') + '"'
					+ 'id="' + this.sId + '-check-' + n + '-icon">'
					+ '<img alt="'+this._msgs["L_MENU_CHECK"]+'" align="center"  class="s-img-display" src="' + dojo.moduleUrl("dwa.common", "images/basicicons.gif")
					+ '" unselectable="on"' + ' style="left:-30px;top:-20px;position:absolute;border-width:0px;"/>'
					+ '</span>';
				}
				var sIcon = '';
				sLabel = asDescriptors[asDescriptors.length - 1].substr(asControl ? asControl[0].length : 0);
				if (asDescriptors.length > 1) {
					var asIconDescriptor = asDescriptors[0].split(' ');
					var sIconImage = asIconDescriptor.length > 3 ?
					 sTransparentGif : asIconDescriptor[2];
					// SPR# PTHN7NYLHX: if input control loses focus, menu is hidden and no choice is selected
					//		use  unselectable="on"  to avoid losing focus.
					 sIcon = '<span style="position:relative;display:block;width:' + asIconDescriptor[0] + 'px;height:' + asIconDescriptor[1] + 'px;overflow:hidden">'
					 + '<img class="s-img-display" align="center"  src="' + asConsolidatedImages[i] + '" unselectable="on" style="'
					 + (asIconDescriptor.length > 3 ? 'left:-' + asIconDescriptor[2] + 'px;top:-' + asIconDescriptor[3] + 'px;' : '')
					 + D_NoUserSelect + 'position:absolute;border-width:0px;"'
					 + ' alt="'+ asDescriptors[asDescriptors.length - 1].substr(asControl ? asControl[0].length : 0) + '"/>'
					 + '</span>';
				}
				if (this.supportScreenReader) {
					var sAriaLabel = this.menuInfo[i].ariaLabel || sLabel;
					sAriaLabel = this.menuInfo[i].isDisabled ?  dwa.common.utils.formatMessage(this._msgs["L_MENU_ITEM_DISABLED"], sAriaLabel) : fChild ?  dwa.common.utils.formatMessage(this._msgs["L_MENU_ITEM_SUBMENU"], sAriaLabel) : sAriaLabel;
				}
				asHtml[asHtml.length]
				 = '<tr id="' + this.sId + '-item-' + n + '" descriptorIndex="' + i + '" role="row"'
				 + ' class="' + (this.menuInfo[i].isDisabled ? this._disabledClass : '') + '" havechild="' + !!fChild + '" onmousedown="return false;"'
				 + '>'
				 + '<td id="' + this.sId + '-item-' + n + '-selection" class="' + sClass + (!this._isSelectionExists ? ' s-nodisplay' : '') +  '"'
				 + ' align="' + D_ALIGN_DEFAULT + '" unselectable="on" style="white-space:nowrap;overflow:hidden;' + D_NoUserSelect + '"'
				 + ' selectiontargets="' + (asControl && asControl[1] ? asControl[1] : '') + '">'
				 + sCheck
				 + '</td>'
				 + '<td class="' + sClass + (!fIconExists ? ' s-nodisplay' : '') +  '"'
				 + ' align="' + D_ALIGN_DEFAULT + '" unselectable="on" style="white-space:nowrap;overflow:hidden;' + D_NoUserSelect + '">'
				 + sIcon
				 + '</td>'
				 + '<td id="' + this.sId + '-td-' + n + '" role="gridcell" tabindex="' + (this.handleFocus ? 0 : -1) + '" class="' + sClass + '" align="' + D_ALIGN_DEFAULT + '" unselectable="on" style="white-space:nowrap;' + D_NoUserSelect
				 + (dojo.hasClass(dojo.body(), "dijit_a11y") ? '"' : 'outline-width:0px;" hidefocus="true"')
				 + (8 <= dojo.isIE || 3 <= dojo.isMozilla ? ' aria-labelledby="' + this.sId + '-item-' + n + '-label"' : (dojo.isIE <= 7 || dojo.isMozilla <= 2 ? ' title="' + (this.supportScreenReader ? sAriaLabel : "") + '"' : ''))
				 + '>'
				 + sLabel
				 + '</td>'
				 + '<td class="' + sClass + '" align="' + D_ALIGN_REVERSE + '" valign="middle" unselectable="on" style="' + D_NoUserSelect + '">'
				 + (fChild ? sImg : '')
				 + '</td>'
				 + '</tr>';
				 if (8 <= dojo.isIE || 3 <= dojo.isMozilla) {
					 asAriaLabel[asAriaLabel.length] = (this.supportScreenReader ? '<label id="' + this.sId + '-item-' + n + '-label" style="display:none">' + sAriaLabel + '</label>' : "");
				 }
			}
		}
		this.domNode.innerHTML = '<table role="presentation" onmousedown="return false;" cellspacing="0" cellpadding="0" unselectable="on" style="' + D_NoUserSelect + 'border-width:0px;"><tbody unselectable="on" role="grid" aria-label="'+ this._msgs["L_MENU_GRID"] +'" style="' + D_NoUserSelect + '">' + asHtml.join('') + '</tbody></table>' + asAriaLabel.join('');
		
		for (var i = 0; i < asConsolidatedImages.length; i++) {
			new dwa.common.consolidatedImageListener([this.sId], asConsolidatedImages[i]);
		}
	},
	
	// l_Menu.h#com_ibm_dwa_ui_dropdownMenu.prototype.setActive()
	setActive: function(nIndex, fShowChild) {
		this._updateFocus(this.sId, nIndex);
		
		this.setHighlightRow(nIndex);
		if (!dojo.hasClass(dojo.body(), "dijit_a11y")) {
			for (var n = 0; n < this.menuInfo.length - this._numOfSeparators; n++) {
				var checkMarkIcon = dojo.byId(this.sId + '-check-' + n + '-icon');
				if (checkMarkIcon && !dojo.hasClass(checkMarkIcon, "s-nodisplay")) {
					checkMarkIcon.firstChild.style.left = (/s\-list\-selected/.test((dojo.byId(this.sId + '-item-' + n)).className)) ? "-239px" : "-30px";
				}
			}
		}
		
		var oActive = this._getRow(nIndex);
		if (this.sChild) {
			var oDropdownManager = dwa.common.dropdownManager.get(this.dropdownManagerId);
			for (var i = oDropdownManager.aoActiveDrops.length - 1; 0 <= i; i--) {
				if (oDropdownManager.aoActiveDrops[i].id && oDropdownManager.aoActiveDrops[i].id.indexOf(this.sId) == 0 && this.sId.length < oDropdownManager.aoActiveDrops[i].id.length) {
					oDropdownManager.hide(null, i);
				}
			}
			this.sChild = '';
		}
		
		if (fShowChild) {
			this._dispatchOnClickEvent(oActive);
		}
	},
	
	setHighlightRow: function(nIndex) {
		if (!this.handleFocus && dojo.hasClass(dojo.body(), "dijit_a11y")) {
			for (var n = 0; n < this.menuInfo.length - this._numOfSeparators; n++) {
				dojo.toggleClass(dojo.byId(this.sId + '-td-' + n), "s-bold", n == nIndex);
			}
		} else {
			var asHighlightClass = ['s-list-selected', 's-list-text-selected'];
			for (var aoElems = this.domNode.getElementsByTagName('*'), i = 0; i < aoElems.length; i++) {
				var asMatch = aoElems[i].id.match(/\-item\-([0-9]+)$/);
				if (!asMatch)
					continue;
				for (j = 0; j < asHighlightClass.length; j++) {
					dwa.common.utils.cssEditClassExistence(aoElems[i], asHighlightClass[j], asMatch && asMatch[1] - 0 == nIndex);
				}
			}
		}
	},
	
	// l_Menu.h#com_ibm_dwa_ui_dropdownMenu.prototype.showChild()
	showChild: function(nIndex, ev) {
		var oDropdownManager = dwa.common.dropdownManager.get(this.dropdownManagerId);
		var oElem = this._getRow(nIndex);
		var descriptorIndex = oElem.getAttribute("descriptorIndex");
		var sChild = oElem.id + "-child";
		
		var subMenu = dijit.byId(sChild);
		if (!subMenu) {
			var subMenuNode = dojo.doc.createElement("div");
			subMenuNode.id = sChild;
			this.domNode.parentNode.appendChild(subMenuNode);
			var props = {
				menuInfo: this.menuInfo[descriptorIndex].subMenu,
				defaultImageModule: this.defaultImageModule,
				defaultConsolidatedImage: this.defaultConsolidatedImage,
				focusMethod: this.focusMethod,
				dropdownManagerId: this.dropdownManagerId,
				_parentInfo: {
					menu: this,
					childIndex: nIndex
				}
			};
			subMenu = new dwa.common.menu(props, subMenuNode);
			this._children.push(subMenu);
			subMenu.startup();
			oDropdownManager.oHtml[sChild] = subMenu.domNode.innerHTML;
		}
		var nOrient = oDropdownManager.nOrient;
		oDropdownManager.nOrient = 3;
		
		if (!dojo.isMozilla && !dojo.isWebKit && dojo.doc.compatMode == "CSS1Compat") {
			oDropdownManager.setPos(ev, oElem);
			var padding = this.domNode.style.padding.substr(0, this.domNode.style.padding.length - 2) - 0;
			oDropdownManager.oPos.x = !this.isRTL ? this.domNode.offsetLeft + this.domNode.clientLeft + padding : dojo.doc.body.clientWidth - this.domNode.offsetLeft - this.domNode.clientLeft - padding - oElem.offsetWidth;
			oDropdownManager.oPos.y = this.domNode.offsetTop + this.domNode.clientTop + padding + oElem.offsetTop;
		} else if (!dojo.isMozilla && !dojo.isWebKit) {
			oDropdownManager.setPos(ev, oElem);
			// The offsetY of the event is calculated against the entire table (instead of the row) for IE...
			for (var aoRows = dojo.byId(this.sId).firstChild.rows, i = 0; i < aoRows.length && aoRows[i] != oElem;
				oDropdownManager.oPos.y += aoRows[i].offsetHeight, i++);
		} else {
			oDropdownManager.fIgnoreLayer = true;
			oDropdownManager.setPos(ev, oElem);
		}
		oDropdownManager.nCurrentLevel++;
		oDropdownManager.show(ev, sChild);
		oDropdownManager.nOrient = nOrient;
		this.sChild = sChild;
		dojo.stopEvent(ev);
	}
});
