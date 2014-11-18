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

dojo.provide("dwa.common.dialog");

dojo.require("dijit._Widget");
dojo.require("dwa.common.graphics");

var D_COLOR_DIVDIALOG_SHADOW = "#000000";
var D_COLOR_DIVDIALOG_BACKGROUND = "rgb(239,235,247)";
var D_COLOR_DIVDIALOG_BORDER = "rgb(123,158,189)";
var D_COLOR_DIVDIALOG_TITLE = "rgb(222,227,239)";
var D_COLOR_DIVDIALOG_TITLE_BORDER = "rgb(123,158,189)";

var D_ZINDEX_DIALOG = 100;
var D_DivDialog_Z = 601;

var D_ALIGN_DEFAULT = "left";
var D_ALIGN_REVERSE = "right";

dojo.declare(
	"dwa.common.dialog",
	dijit._Widget,
{
	label: "",
	content: "",
	width: 300,
	height: 150,
	minWidth: 0,
	minHeight: 0,
	bodyBackgroundColor: "#FFFFFF",
	resizeTypes: ['w', 'e', 'n', 's', 'nw', 'ne', 'sw', 'se'],
	focusElementId: "",
	
	isModeless: false,
	resizable: true,
	draggable: true,
	autoPosition: false,
	restoreSize: false,
	autoRender: true,
	
	isRTL: false,
	_isMoved: false,
	_started: false,
	_rendered: false,
	
	postMixInProperties: function() {
		if (!dojo._isBodyLtr()) {
			this.isRTL = true;
			D_ALIGN_DEFAULT = "right";
			D_ALIGN_REVERSE = "left";
		}
	},
	
	postCreate: function() {
		this.sId = this.id;
		if(this.content) {
			this.domNode.innerHTML = this.content;
		} else {
			this.content = this.domNode.innerHTML;
		}
		this.width = parseInt(this.width);
		this.height = parseInt(this.height);
		this.minWidth = parseInt(this.minWidth);
		this.minHeight = parseInt(this.minHeight);
		if (this.minWidth <= 0 || this.width < this.minWidth) {
			this.minWidth = this.width;
		}
		if (this.minHeight <= 0 || this.height < this.minHeight) {
			this.minHeight = this.height;
		}
		
		dojo.addClass(this.domNode, "s-stack");
		this.domNode.style.top = "-9999px";
		dijit.setWaiRole(this.domNode, "dialog");
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
	
	render: function() {
		dojo.body().appendChild(this.domNode);
		this._generateDialogHtml();
		this.connect(dojo.byId(this.sId + "-close-icon"), "onclick", "hide");
		this.connect(this.domNode, "onkeydown", "onKeyDown");
		if (this.draggable) {
			this.connect(dojo.byId(this.sId + "-title"), "onmousedown", "_handleDrag");
		}
		if (this.resizable) {
			for (var i = 0; i < this.resizeTypes.length; i++) {
				this.connect(dojo.byId(this.sId + "-resizearea-" + this.resizeTypes[i]), "onmousedown", "_handleResize");
			}
		}
		this.connect(dojo.global, "onresize", function() {
			if (!this.isModeless) {
				var oCover = dojo.byId(this.sId + '-cover');
				this.domNode.style.height = oCover.style.height = dojo.doc.documentElement.scrollHeight + "px";
				this.domNode.style.width = oCover.style.width = dojo.doc.documentElement.scrollWidth + "px";
			}
		});
		if (this.autoPosition) {
			this.connect(dojo.global, "onresize", function() {
				if (!this._isMoved) {
					this.setPosition();
				}
			});
		}
		this._rendered = true;
	},
	
	destroy: function() {
		this.inherited(arguments);
	},
	
	show: function() {
		if (this.restoreSize) {
			this.setSize(this.width, this.height);
		}
		this.setPosition();
		this.domNode.style.visibility = "visible";
		var focusElem = dojo.byId(this.focusElementId);
		if (focusElem) {
			try {
				focusElem.focus();
			} catch(e) {
			}
		}
	},
	
	hide: function() {
		this._isMoved = false;
		this.domNode.style.visibility = "hidden";
		this.domNode.style.top = "-9999px";
	},
	
	setPosition: function() {
		var node = this.isModeless ? this.domNode : dojo.byId(this.sId + '-container');
		var viewport = dijit.getViewport();
		var marginBox = dojo.marginBox(node);
		var top = Math.floor(Math.max(0, viewport.t + ((viewport.h - marginBox.h) / 2)));
		var left = Math.floor(Math.max(0, Math.abs(viewport.l) + ((viewport.w - marginBox.w) / 2)));
		node.style.top = top + "px";
		node.style[D_ALIGN_DEFAULT] = left + "px";
		if (!this.isModeless) {
			for (var i = 1; i <= 4; i++) {
				var shadowElem = dojo.byId(this.sId + '-shadow-' + i);
				shadowElem.style.top = top + i + "px";
				shadowElem.style[D_ALIGN_DEFAULT] = left + i + "px";
			}
			this.domNode.style.top = "0px";
			this.domNode.style.left = "0px";
		}
	},
	
	setSize: function(nWidth, nHeight, nTop, nLeft) {
		var oContainer = this.isModeless ? this.domNode : dojo.byId(this.sId + "-container");
		var nDiffWidth = nWidth - oContainer.clientWidth;
		var nDiffHeight = nHeight - oContainer.clientHeight;
		var nDiffTop = isNaN(nTop) ? 0 : nTop - oContainer.offsetTop;
		var nDiffLeft = isNaN(nLeft) ? 0 : nLeft - oContainer.offsetLeft;
		var nDiffRight = (nDiffLeft == 0) ? -nDiffWidth : 0;
		var asResizeIds = [this.sId + "-container", this.sId + "-shadow-1", this.sId + "-shadow-2", this.sId + "-shadow-3", this.sId + "-shadow-4", this.sId + "-corner", this.sId + "-inner-container", this.sId + "-under-title"];
		if (this.isModeless)
			asResizeIds.push(this.sId);
		for (var i = 0; i < asResizeIds.length; i++) {
			var oElem = dojo.byId(asResizeIds[i]);
			var sId = asResizeIds[i];
			if (!oElem)
				continue;
			oElem.style.width = Math.max(0, parseInt(oElem.style.width) + nDiffWidth) + "px";
			if (asResizeIds[i].indexOf("-under-title") != -1)
				continue;
			oElem.style.height = Math.max(0, parseInt(oElem.style.height) + nDiffHeight) + "px";
			if (asResizeIds[i].indexOf("-inner-container") != -1 || asResizeIds[i].indexOf("-corner") != -1
			 || (this.isModeless && asResizeIds[i].indexOf("-container") != -1))
				continue;
			oElem.style.top = (oElem.offsetTop + nDiffTop) + "px";
			
			if (!this.isRTL && nDiffLeft != 0) {
				oElem.style[D_ALIGN_DEFAULT] = oElem.offsetLeft + nDiffLeft + "px";
			}
			if (this.isRTL && nDiffRight != 0) {
				var oBox = dojo.marginBox(oElem);
				var oViewPort = dijit.getViewport();
				oElem.style[D_ALIGN_DEFAULT] = parseInt(dojo.getComputedStyle(oElem).right) + nDiffRight + "px";
			}
		}
		this._resizeVectorGraphics(nDiffWidth, nDiffHeight);
	},
	
	onKeyDown: function(ev) {
		switch (ev.keyCode) {
			case 27: // Esc
				dojo.stopEvent(ev);
				return this.hide();
		}
	},
	
	_handleDrag: function(ev) {
		var oCurrentPos = {x: ev.clientX, y: ev.clientY};
		var oStartPos = this._oStartPos;
		var oDialogElem = this.isModeless ? this.domNode : dojo.byId(this.sId + '-container');
		switch (ev.type) {
		case 'mousedown':
			if(ev.button && ev.button == 2) {
				break;
			}
			this._oStartPos = {x: ev.clientX, y: ev.clientY};
			this.connect(dojo.doc, "onmousemove", "_handleDrag");
			this.connect(dojo.doc, "onmouseup", "_handleDrag");
			dojo.setSelectable(dojo.doc.body, false);
			this._bInDrag = true;
			this._createiFrameGuard();
			break;
		case 'mousemove':
			if (!this._bInDrag) {
				break;
			}
			this._isMoved = true;
			
			if (!this._oDragElem) {
				this._oDragElem = dojo.doc.body.appendChild(dojo.doc.createElement('div'));
				this._oDragElem.noWrap = true;
				dojo.style(this._oDragElem, {
					backgroundColor: "transparent",
					position: "absolute",
					top: oDialogElem.style.top,
					width: oDialogElem.offsetWidth + "px",
					height: oDialogElem.offsetHeight + "px",
					zIndex: "999",
					border: "solid #999999 1px",
					color: "#999999"
				});
				this._oDragElem.style[D_ALIGN_DEFAULT] = oDialogElem.style[D_ALIGN_DEFAULT];
				dojo.addClass(this._oDragElem, "s-label-light");
				this._oDragElem.innerHTML = "&nbsp;" + (this.label || "&nbsp;");
			}
			var x = parseInt(oDialogElem.style[D_ALIGN_DEFAULT]);
			this._oDragElem.style[D_ALIGN_DEFAULT] = Math.round((this.isRTL ? -1 : 1)*(oCurrentPos.x - oStartPos.x) + x) + "px";
			var y = parseInt(oDialogElem.style.top);
			this._oDragElem.style.top = Math.round(oCurrentPos.y - oStartPos.y + y) + "px";
			break;
		case 'mouseup':
			if (!this._bInDrag) {
				break;
			}
			this._bInDrag = false;
			this.disconnect(dojo.doc, "onmousemove", "_handleDrag");
			this.disconnect(dojo.doc, "onmouseup", "_handleDrag");
			dojo.setSelectable(dojo.doc.body, true);
			if (this._oDragElem) {
				this._oDragElem.parentNode.removeChild(this._oDragElem);
				this._oDragElem = null;
			}
			this._destroyiFrameGuard();
			
			for (var i = 0; i < this._asDragIds.length; i++) {
				var x = parseInt(dojo.byId(this._asDragIds[i]).style[D_ALIGN_DEFAULT]);
				dojo.byId(this._asDragIds[i]).style[D_ALIGN_DEFAULT] = Math.round((this.isRTL ? -1 : 1)*(oCurrentPos.x - oStartPos.x) + x) + "px";
				var y = parseInt(dojo.byId(this._asDragIds[i]).style.top);
				dojo.byId(this._asDragIds[i]).style.top = Math.round(oCurrentPos.y - oStartPos.y + y) + "px";
			}
		}
	},
	
	_handleResize: function(ev) {
		var oCurrentPos = {
			x: ev.clientX,
			y: ev.clientY
		};
		
		var oContainer = this.isModeless ? this.domNode : dojo.byId(this.sId + "-container");
		switch (ev.type) {
			case 'mousedown':
				if(ev.button && ev.button == 2) {
					break;
				}
				var asIds = (ev.target || ev.srcElement).id.split('-');
				this._sResizeType = asIds[asIds.length - 1];
				this.connect(dojo.doc, "onmousemove", "_handleResize");
				this.connect(dojo.doc, "onmouseup", "_handleResize");
				this._bInResize = true;
				this._createiFrameGuard();
				dojo.setSelectable(dojo.doc.body, false);
				break;
			case 'mousemove':
			case 'mouseup':
				if (!this._bInResize || !this._sResizeType || (ev.type == "mouseup" && !this._oResizeElem))
					return;
				
				var nTop = oContainer.offsetTop;
				var nLeft = oContainer.offsetLeft;
				var nWidth = oContainer.clientWidth;
				var nHeight = oContainer.clientHeight;
				if (this._sResizeType.indexOf('w') != -1) {
					nWidth = Math.max(this.minWidth, oContainer.offsetLeft + oContainer.clientWidth - oCurrentPos.x);
					nLeft = Math.min(oCurrentPos.x, oContainer.offsetLeft + oContainer.clientWidth - this.minWidth);
				}
				if (this._sResizeType.indexOf('e') != -1) {
					nWidth = Math.max(this.minWidth, oCurrentPos.x - oContainer.offsetLeft);
				}
				if (this._sResizeType.indexOf('n') != -1) {
					nTop = Math.min(oCurrentPos.y, oContainer.offsetTop + oContainer.clientHeight - this.minHeight);
					nHeight = Math.max(this.minHeight, oContainer.offsetTop + oContainer.clientHeight - oCurrentPos.y);
				}
				if (this._sResizeType.indexOf('s') != -1) {
					nHeight = Math.max(this.minHeight, oCurrentPos.y - oContainer.offsetTop);
				}
				
				if (ev.type == "mousemove") {
					if (!this._oResizeElem) {
						this._oResizeElem = dojo.doc.createElement("div");
						this._oResizeElem.setAttribute("id", this.sId + "-resize-div");
						this._oResizeElem.setAttribute("noWrap", "true");
						this._oResizeElem.style.cssText = "background-color:transparent;position:absolute;z-index:999;border:solid #999999 1px;border-style:dashed;";
						dojo.doc.body.appendChild(this._oResizeElem);
					}
					this._oResizeElem.style.top = nTop + "px";
					this._oResizeElem.style[D_ALIGN_DEFAULT]= (this.isRTL ? dojo.doc.body.clientWidth - nWidth - nLeft : nLeft) + "px";
					this._oResizeElem.style.width = nWidth + "px";
					this._oResizeElem.style.height = nHeight + "px";
					
				} else if (ev.type == "mouseup") {
					this._bInResize = false;
					this.disconnect(dojo.doc, "onmousemove", "_handleResize");
					this.disconnect(dojo.doc, "onmouseup", "_handleResize");
					dojo.doc.body.removeChild(this._oResizeElem);
					this._oResizeElem = null;
					this._destroyiFrameGuard();
					this._sResizeType = "";
					dojo.setSelectable(dojo.doc.body, true);
					this.setSize(nWidth, nHeight, nTop, nLeft);
				}
		}
	},
	
	// com_ibm_dwa_ui_dialog()
	_generateDialogHtml: function() {
		var oElem = this.domNode;
		var asCloseIndicator = ['7', '7', '40', '20'];
		var sBasicIconsGif = dojo.moduleUrl("dwa.common", "images/basicicons.gif");
		var sTransparentGif = dojo.moduleUrl("dwa.common", "images/transparent.gif");
		var sWidth = this.width + "px";
		var sHeight = this.height + "px";
		var sIFrameSrc = 'about:blank';
		var asShadowHtml = [];
		var fModeless = this.isModeless;
		
		if (!fModeless) {
			oElem.style.width = dojo.doc.documentElement.scrollWidth + "px";
			oElem.style.height = dojo.doc.documentElement.scrollHeight + "px";
			// Strangely enough, the currentStyle (in IE) of oShadow becomes "none" until oShadow has "s-hidden" class,
			// which happens with Quickr support on tearaway window of mail edit form.
			// So avoid setting "s-hidden" here - asudoh 5/15/2008
			for (var i = 0; i < 4; i++) {
				if (dojo.isMozilla || dojo.isWebKit) {
					var sHtml = '<canvas id="' + this.sId + '-shadow-' + (4 - i) + '" class="s-balloon-container"'
					 + ' style="width:' + sWidth + ';height:' + sHeight + ';opacity:' + (0.1 * (i + 1)) + ';">'
					 + '</canvas>';
				} else {
					var sOpacity = 'filter:alpha(opacity=' + (10 * (i + 1)) + ');opacity:' + (0.1 * (i + 1)) + ';';
					var sHtml = '<div id="' + this.sId + '-shadow-' + (4 - i) + '" class="s-balloon-container"'
					 + ' style="width:' + sWidth + ';height:' + sHeight + ';padding:0px 1px 1px 0px;' + sOpacity + '">'
					 + '<v:roundrect style="width:' + sWidth + ';height:' + sHeight + ';" arcsize="2%" fillcolor="' + D_COLOR_DIVDIALOG_SHADOW + '">'
					 + '<v:stroke on="false" />'
					 + '</v:roundrect>'
					 + '</div>';
				}
				asShadowHtml.push(sHtml);
			}
		}
		
		var sTitle = this.label;
		var sDivBgColor = this.bodyBackgroundColor;
		
		if (!fModeless)
			this._asDragIds = [
				this.sId + '-container',
				this.sId + '-shadow-1',
				this.sId + '-shadow-2',
				this.sId + '-shadow-3',
				this.sId + '-shadow-4'
			];
		else
			this._asDragIds = [this.sId];
		
		// Strangely enough, the currentStyle (in IE) of oShadow becomes "none" until oShadow has "s-hidden" class,
		// which happens with Quickr support on tearaway window of mail edit form.
		// So avoid setting "s-hidden" to "container" element - asudoh 5/15/2008
		var sHtml = '<iframe id="' + this.sId + '-cover" src="' + sIFrameSrc + '"'
		 + ' class="s-stack s-hidden ' + (!fModeless ? 's-dialog-body-overlay' : 's-modeless-body-overlay') + '" style="border-width:0px;">'
		 + '</iframe>'
		 + asShadowHtml.join('')
		 + '<div id="' + this.sId + '-container" class="s-balloon-container"'
		 + ' style="width:' + sWidth + ';height:' + sHeight + ';">';
		if (dojo.hasClass(dojo.doc.body, "dijit_a11y")) {
			var nWidth = this.width - 2;
			var nHeight = this.height - 2;
			sHtml += '<div id="' + this.sId + '-corner" style="width:' + nWidth + 'px;height:' + nHeight + 'px;overflow:hidden;background-color:black;border:1px solid black;"></div>';
		} else if (!dojo.isMozilla && !dojo.isWebKit) {
			var nWidth = this.width - (dojo.doc.compatMode == "CSS1Compat" ? 0 : 3);
			var nHeight = this.height - (dojo.doc.compatMode == "CSS1Compat" ? 0 : 1);
			sHtml += '<div class="" style="width:' + (this.width - (!this.isRTL && this.isModeless ? 1 : 0)) + 'px;height:100%;">'
			 + '<v:roundrect id="' + this.sId + '-corner" style="width:' + nWidth + 'px;height:'+ nHeight + 'px;"'
			 + ' arcsize="2%" strokecolor="' + D_COLOR_DIVDIALOG_BORDER + '" fillcolor="' + D_COLOR_DIVDIALOG_BACKGROUND + '"></v:roundrect>'
			 + '</div>';
		} else {
			sHtml += '<canvas id="' + this.sId + '-corner" class="s-stack"></canvas>';
		}
		if (!dojo.isMozilla && !dojo.isWebKit && dojo.doc.compatMode == "CSS1Compat") {
			var nHeight = this.height;
			var nWidth = this.width;
			nWidth = Math.max(0, nWidth - 8 - 8);
			nHeight = Math.max(0, nHeight - 20 - 8 - 4);
			sHtml += '<div id="' + this.sId + '-inner-container" class="s-stack" style="width:' + nWidth + 'px;height:' + nHeight + 'px;padding:20px 8px 8px 8px;">';
		} else {
			sHtml += '<div class="s-stack" style="padding:1.5em 8px 8px 8px;">';
		}
		var nWidth = this.width;
		nWidth = Math.max(0, nWidth - 2);
		nWidth += (dojo.isIE && dojo.doc.compatMode != "CSS1Compat" ? 2 : 0);
		sHtml += '<div class="s-basicpanel" style="padding-top:4px;' + (sDivBgColor ? ('background-color:' + sDivBgColor + ';') : '') + '">'
		 + '<div class="s-basicpanel">'
		 + this.content
		 + '</div>'
		 + '</div>'
		 + '</div>'
		 + '<div id="' + this.sId + '-under-title" '
		 + ' style="position:absolute;overflow:hidden;top:0.75em;height:0.75em;width:' + nWidth + 'px;' + (dojo.isIE && !dojo.hasClass(dojo.doc.body, "dijit_a11y") && !this.isRTL ? 'left:1px;' : '') + 'border:solid ' + D_COLOR_DIVDIALOG_TITLE_BORDER + ' 1px;border-top-width:0;background-color:' + D_COLOR_DIVDIALOG_TITLE + ';'
		 + '">'
		 + '</div>'
		 + '<div id="' + this.sId + '-titlebar" class="s-toppanel s-noselecttext" style="height:1.5em;"'
		 + ' unselectable="on"'
		 + '><table class="s-basicpanel" border="0" cellspacing="0" cellpadding="6">'
		 + '<tbody>'
		 + '<tr>'
		 + '<td id="' + this.sId + '-title" width="100%" class="s-label-light" style="' + (dojo.hasClass(dojo.doc.body, "dijit_a11y") ? "background:transparent;" : "") + 'font-weight:bold;' + (this.draggable ? "cursor:move;" : "") + '"'
		 + '>'
		 + (sTitle || '')
		 + '</td>'
		 + '<td id="' + this.sId + '-closebutton-container">'
		 + '<div id="' + this.sId + '-close-icon" class="s-handcursor" style="position:relative;width:' + asCloseIndicator[0] + 'px;height:' + asCloseIndicator[1] + 'px;overflow:hidden">'
		 + '<img alt="close" src="' + sBasicIconsGif + '" style="display:block;top:-' + asCloseIndicator[3] + 'px;left:-' + asCloseIndicator[2] + 'px;position:absolute;border-width:0px;"/>'
		 + '</div>'
		 + '</td>'
		 + '</tr>'
		 + '</tbody>'
		 + '</table>'
		 + '</div>';
		if (this.resizable) {
			for (var i = 0; i < this.resizeTypes.length; i++) {
				var type = this.resizeTypes[i];
				sHtml += '<div id="' + this.sId + '-resizearea-' + type + '"'
				 + ' style="overflow:hidden;position:absolute;'
				 + 'z-index:' + (type.length == 1 ? '1000' : '1001') + ';'
				 + 'cursor:' + type + '-resize;'
				 + (type.indexOf('w') != -1 ? 'left:0px;' : 'right:0px;')
				 + (type.indexOf('n') != -1 ? 'top:0px;' : 'bottom:0px;')
				 + (type.length == 2 ? 'width:7px;height:7px;' : (type.indexOf('w') != -1 || type.indexOf('e') != -1 ? 'width:4px;height:100%;' : 'width:100%;height:4px;'))
				 + '"></div>';
			}
		}
		this.domNode.innerHTML = sHtml + '</div>';
		
		if (oElem.style.zIndex == '')
			oElem.style.zIndex = D_ZINDEX_DIALOG;
		
		if (fModeless) {
			oElem.style.width = sWidth;
			oElem.style.height = sHeight;
		}
		
		var oContainer = dojo.byId(this.sId + '-container');
		if (!fModeless) {
			//SPR SANR78RSNF, we want to let user to see the whole dialog without doing any extra calculation of the size
			// so use overflow:auto to the div dialog container element.
			// the scrollbar will be shown around the <body>
			if (oElem.style.overflow == '')
				oElem.style.overflow = "auto";
			
			var oCover = dojo.byId(this.sId + '-cover');
			oCover.style.width = dojo.doc.documentElement.scrollWidth + "px";
			oCover.style.height = dojo.doc.documentElement.scrollHeight + "px";
			if (oContainer.style.zIndex == '')
				oContainer.style.zIndex = D_DivDialog_Z;
		}
		dojo.removeClass(oContainer, 's-hidden');
		
		this._resizeVectorGraphics(-1, -3);
		dojo.removeClass(dojo.byId(this.sId + '-cover'), 's-hidden');
	},
	
	_resizeVectorGraphics: function(nDiffWidth, nDiffHeight) {
		if (!this.isModeless) {
			for (var i = 1; i <= 4; i++) {
				var oElem = dojo.byId(this.sId + "-shadow-" + i);
				if (!dojo.isMozilla && !dojo.isWebKit) {
					oElem.firstChild.style.width = Math.max(0, parseInt(oElem.firstChild.style.width) + nDiffWidth) + "px";
					oElem.firstChild.style.height = Math.max(0, parseInt(oElem.firstChild.style.height) + nDiffHeight) + "px";
				} else {
					oElem.setAttribute('width', oElem.offsetWidth);
					oElem.setAttribute('height', oElem.offsetHeight);
					dwa.common.graphics.drawRoundRect(oElem, D_COLOR_DIVDIALOG_SHADOW);
				}
			}
		}
		
		if (!dojo.hasClass(dojo.doc.body, "dijit_a11y")) {
			var oElem = dojo.byId(this.sId + '-corner');
			if (!dojo.isMozilla && !dojo.isWebKit) {
				if (!this._rendered) {
					oElem.style.width = oElem.parentNode.offsetWidth + nDiffWidth + 'px';
					oElem.style.height = oElem.parentNode.offsetHeight + nDiffHeight + 'px';
				}
			} else {
				oElem.setAttribute('width', oElem.offsetWidth);
				oElem.setAttribute('height', oElem.offsetHeight);
				dwa.common.graphics.drawRoundRect(oElem, D_COLOR_DIVDIALOG_BACKGROUND, D_COLOR_DIVDIALOG_BORDER);
			}
		}
	},
	
	_createiFrameGuard: function() {
		if (!this._iFrameGuard) {
			this._iFrameGuard = dojo.doc.createElement("div");
			this._iFrameGuard.setAttribute("id", this.sId + "-iframeguard");
			var oViewport = dijit.getViewport();
			this._iFrameGuard.style.cssText = "z-index:998;left:0px;top:0px;position:absolute;width:" + oViewport.w + "px;height:" + oViewport.h + "px;";
			dojo.doc.body.appendChild(this._iFrameGuard);
		}
	},
	
	_destroyiFrameGuard: function() {
		if (this._iFrameGuard) {
			dojo.doc.body.removeChild(this._iFrameGuard);
			this._iFrameGuard = null;
		}
	}
});
