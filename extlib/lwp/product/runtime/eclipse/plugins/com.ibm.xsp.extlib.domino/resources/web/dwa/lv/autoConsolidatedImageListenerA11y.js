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

dojo.provide("dwa.lv.autoConsolidatedImageListenerA11y");

dojo.require("dwa.common.listeners");
dojo.require("dwa.lv.globals");

dojo.declare(
	"dwa.lv.autoConsolidatedImageListenerA11y",
	dwa.common.consolidatedImageListener,
{
	constructor: function(asIds, sSrc, sKey){
	},
	onDatasetComplete: function(){
	  return this.onReadyStateChange(arguments);
	},
	onReadyStateChange: function(sReadyState){
		if(!dojo.isMozilla && !dojo.isWebKit){
			if (sReadyState != 'complete')
				return;
		}
		for (var i = 0; i < this.asIds.length; i++) {
			var oContainer = dojo.doc.getElementById(this.asIds[i]);
			// check if the oContainer is still available.
			if (oContainer) {
				for (var aoImages = (/^img$/i).test(oContainer.tagName) ? [oContainer] : oContainer.getElementsByTagName('img'),
				 j = 0; j < aoImages.length; j++) {
					var oElem = aoImages[j];
					if (!oElem.getAttribute('xoffset') || !oElem.getAttribute('yoffset') || oElem.style.backgroundImage
					 || oElem.getAttribute('consolidatedImage') && oElem.getAttribute('consolidatedImage') != this.sHref)
						continue;
					oElem.style.top = (-oElem.getAttribute('yoffset')) +'px';
					oElem.style.left = (-oElem.getAttribute('xoffset')) +'px';
					oElem.style.position = 'absolute';
					oElem.src = this.sHref;
					oElem.setAttribute('xoffset', '');
					oElem.setAttribute('yoffset', '');
				}
			}
		}
		this.fToBeReleased = true;
		dwa.lv.autoConsolidatedImageListenerA11y.prototype.oLanded[this.sHref] = true;
	},
	onload: function(ev){
		var oImg = ev.currentTarget || (dojo.isIE ? (ev.srcElement) : (dojo.isMozilla ? (ev.target) : (ev.target)));
		if (oImg) {
			if (dwa.lv.autoConsolidatedImageListenerA11y.prototype.aoTarget.length == 0)
				setTimeout(dwa.lv.autoConsolidatedImageListenerA11y.prototype.onTimeout, 100);
			dwa.lv.autoConsolidatedImageListenerA11y.prototype.aoTarget.push(oImg);
		}
	},
	onTimeout: function(){
		var oHref = {};
		for (var aoTarget = dwa.lv.autoConsolidatedImageListenerA11y.prototype.aoTarget, i = 0; i < aoTarget.length; i++) {
			var sImg = aoTarget[i] && aoTarget[i].getAttribute('consolidatedImage');
			if (sImg) {
				aoTarget[i].id = aoTarget[i].id || ('CONSOLIDATED' + dwa.common.responseListener.prototype.nAutoGenKey++);
				(oHref[sImg] = oHref[sImg] || []).push(aoTarget[i].id);
			}
		}
		for (var s in oHref)
			new dwa.lv.autoConsolidatedImageListenerA11y(oHref[s], s);
		dwa.lv.autoConsolidatedImageListenerA11y.prototype.aoTarget = [];
	},
	oLanded: {},
	aoTarget: [],
	getConsolidatedImageAttrsByPosStatic: function(oSize, oOffset, sUrl, bNoDirect, sStyles ){
		if(oSize && oOffset) {
			var s = ' style="display:inline-block;position:relative;width:'+oSize.x+'px;height:'+oSize.y+'px;overflow:hidden"><img';
				// check if conimg is already cached, return style attr directly to not shrink image
				s+= (dwa.lv.autoConsolidatedImageListenerA11y.prototype.oLanded[sUrl] && !bNoDirect ?
					(' src="' + sUrl + '" style="position:absolute;left:-' + oOffset.x + 'px;top:-' + oOffset.y + 'px;' + (sStyles || '') + '"')
					:
					(' src="' + dwa.lv.globals.get().buildResourcesUrl("transparent.gif") + '"'
					 + (oOffset ? ' ' + 'xoffset' + '="' + oOffset.x + '" ' + 'yoffset' + '="' + oOffset.y + '"' : '')
					 + (' ' + 'consolidatedImage' + '="' + sUrl + '" onload="' + 'dwa.lv.autoConsolidatedImageListenerA11y.prototype.onload(event)' + '"' + (sStyles ? 'style="' + sStyles + '"': ''))
					)
				);
				return s;
		}
		return '';
	},
	applyConsolidatedImageAttrsByPosStatic: function(oElem, oSize, oOffset, sUrl, bNoDirect){
		if(oElem && oSize && oOffset) {
			if(dwa.lv.autoConsolidatedImageListenerA11y.prototype.oLanded[sUrl] && !bNoDirect) {
				oElem.src = sUrl;
				oElem.style.position = 'absolute';
				oElem.style.left = (-oOffset.x) + 'px';
				oElem.style.top = (-oOffset.y) + 'px';
				oElem.parentNode.style.width = oSize.x + 'px';
				oElem.parentNode.style.height = oSize.y + 'px';
			}
			else{
				oElem.setAttribute('xoffset', oOffset.x+'');
				oElem.setAttribute('yoffset', oOffset.y+'');
				oElem.setAttribute('consolidatedImage', sUrl);
	 if( dojo.isMozilla || dojo.isWebKit ){
				oElem.addEventListener('load', dwa.lv.autoConsolidatedImageListenerA11y.prototype.onload, false);
	 }else{ // GS
				oElem.attachEvent('onload', dwa.lv.autoConsolidatedImageListenerA11y.prototype.onload);
	 } // end - I
				oElem.src = dwa.lv.globals.get().buildResourcesUrl("transparent.gif");
				oElem.parentNode.style.width = oSize.x + 'px';
				oElem.parentNode.style.height = oSize.y + 'px';
			}
		}
	}
});
