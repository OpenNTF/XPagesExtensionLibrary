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

dojo.provide("dwa.lv.autoConsolidatedImageListener");

dojo.require("dwa.common.listeners");
dojo.require("dwa.lv.globals");

dojo.declare(
	"dwa.lv.autoConsolidatedImageListener",
	dwa.common.consolidatedImageListener,
{
	constructor: function(asIds, sSrc, sKey){
	},
	onDatasetComplete: function(){
	  return this.onReadyStateChange(arguments);
	},
	onReadyStateChange: function(sReadyState){
		dwa.common.consolidatedImageListener.prototype.onDatasetComplete.call(this, sReadyState);
		if (this.fToBeReleased)
			dwa.lv.autoConsolidatedImageListener.prototype.oLanded[this.sHref] = true;
	},
	onload: function(ev){
		var oImg = ev.currentTarget || (dojo.isIE ? (ev.srcElement) : (dojo.isMozilla ? (ev.target) : (ev.target) ) );
		if (oImg) {
			if (dwa.lv.autoConsolidatedImageListener.prototype.aoTarget.length == 0)
				setTimeout(dwa.lv.autoConsolidatedImageListener.prototype.onTimeout, 100);
			dwa.lv.autoConsolidatedImageListener.prototype.aoTarget.push(oImg);
		}
	},
	onTimeout: function(){
	
		var oHref = {};
	
		for (var aoTarget = dwa.lv.autoConsolidatedImageListener.prototype.aoTarget, i = 0; i < aoTarget.length; i++) {
			var sImg = aoTarget[i] && aoTarget[i].getAttribute('consolidatedImage');
			if (sImg) {
				aoTarget[i].id = aoTarget[i].id || ('CONSOLIDATED' + dwa.common.responseListener.prototype.nAutoGenKey++);
				(oHref[sImg] = oHref[sImg] || []).push(aoTarget[i].id);
			}
		}
	
		for (var s in oHref)
			new dwa.lv.autoConsolidatedImageListener(oHref[s], s);
	
		dwa.lv.autoConsolidatedImageListener.prototype.aoTarget = [];
	},
	oLanded: {},
	aoTarget: [],
	getConsolidatedImageAttrsByPosStatic: function(oSize, oOffset, sUrl, bNoDirect, sStyles ){
		if(oSize && oOffset) {
			 return ' width="' + oSize.x + '" height="' + oSize.y + '" src="' + dwa.lv.globals.get().buildResourcesUrl("transparent.gif") + '"'
				// check if conimg is already cached, return style attr directly to not shrink image
				+ (dwa.lv.autoConsolidatedImageListener.prototype.oLanded[sUrl] && !bNoDirect ?
					(' style="background-image:url(' +sUrl + ');background-position:-' + oOffset.x + 'px -' + oOffset.y + 'px;max-width:' + oSize.x + 'px;max-height:' + oSize.y + 'px')
					:
					((oOffset ? ' ' + 'xoffset' + '="' + oOffset.x + '" ' + 'yoffset' + '="' + oOffset.y + '"' : '')
					 + (' ' + 'consolidatedImage' + '="' + sUrl + '" onload="' + 'dwa.lv.autoConsolidatedImageListener.prototype.onload(event)' + '"')
					 + ' style="max-width:' + oSize.x + 'px;max-height:' + oSize.y + 'px')
				) + (sStyles ? ';' + sStyles : '') + '"';
		}
		return '';
	},
	applyConsolidatedImageAttrsByPosStatic: function(oElem, oSize, oOffset, sUrl, bNoDirect){
		if(oElem && oSize && oOffset) {
			oElem.setAttribute('width', oSize.x);
			oElem.setAttribute('height', oSize.y);
			if(dwa.lv.autoConsolidatedImageListener.prototype.oLanded[sUrl] && !bNoDirect) {
				oElem.style.backgroundImage = 'url(' +sUrl + ')';
				oElem.style.backgroundPosition = '-' + oOffset.x + 'px -' + oOffset.y + 'px';
			}
			else{
				oElem.setAttribute('xoffset', oOffset.x+'');
				oElem.setAttribute('yoffset', oOffset.y+'');
				oElem.setAttribute('consolidatedImage', sUrl);
	 if( dojo.isMozilla || dojo.isWebKit ){
				oElem.addEventListener('load', dwa.lv.autoConsolidatedImageListener.prototype.onload, false);
	 }else{ // GS
				oElem.attachEvent('onload', dwa.lv.autoConsolidatedImageListener.prototype.onload);
	 } // end - I
			}
			oElem.src= dwa.lv.globals.get().buildResourcesUrl("transparent.gif");
		}
	}
});
