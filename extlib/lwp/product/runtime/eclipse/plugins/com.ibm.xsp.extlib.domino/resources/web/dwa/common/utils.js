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

dojo.provide("dwa.common.utils");

dwa.common.utils.formatMessage = function(sFormat){
	var avArgs = arguments;
	return sFormat.replace(/%([1-9][0-9]*)/g, function(s0, s1){ return ((s1 - 0) > avArgs.length ? s0 : avArgs[s1 - 0]); });
};

dwa.common.utils.sign = function(n){
	return n != 0 ? (n / Math.abs(n)) : n;
};

dwa.common.utils.floorAbs = function(n){
	var i = dwa.common.utils.sign(n);
	var nAbs = Math.abs(n);
	return i * Math.floor(nAbs);
};

dwa.common.utils.compare = function(vElem1, vElem2){
	return vElem1 && typeof(vElem1.equals) == 'function' ? vElem1.equals(vElem2) : (vElem1 == vElem2);
};

dwa.common.utils.difference = function(vElem1, vElem2){
	return vElem1 - vElem2;
};

dwa.common.utils.indexOf = function(avArray, vSearch, fnCompare){
	fnCompare = fnCompare ? fnCompare : dwa.common.utils.compare;

	for (var i = 0; i < avArray.length; i++) {
		if (fnCompare(avArray[i], vSearch))
			return i;
	}

	return -1;
};

dwa.common.utils.cssEditClassExistence = function(oElem, sClass, fExist){
	if (!oElem)
		return;
	var asClass = oElem.className.split(' ');
	var bChanged = false;
	if (fExist) {
		if (dwa.common.utils.indexOf(asClass, sClass) < 0) {
			asClass[asClass.length] = sClass;
			bChanged = true;
		}
	} else {
		for (var nIndex = -1; (nIndex = dwa.common.utils.indexOf(asClass, sClass)) >= 0;) {
			asClass.splice(nIndex, 1);
			bChanged = true;
		}
	}
	if (bChanged)
		oElem.className = asClass.join(' ');
}

dwa.common.utils.elSetInnerText = function(oElem, sText){
	while (oElem.firstChild)
		oElem.removeChild(oElem.firstChild);

	if(!dojo.isMozilla && !dojo.isWebKit){
		oElem.appendChild(oElem.document.createTextNode(sText));
	}else{
		oElem.appendChild(oElem.ownerDocument.createTextNode(sText));
	}
};

dwa.common.utils.pos = function(x, y){
	this.x = x;
	this.y = y;
};

dwa.common.utils.getRangeAt = function(oDocument, nIndex){
	if(!dojo.isMozilla && !dojo.isWebKit){
		var oSelection = dojo.doc.selection;
		var oCollection;
		try {
			oCollection = oSelection.TextRange ? [oSelection.TextRange] :
			 oSelection.type == 'Control' ? [oSelection.createRange()] : oSelection.createRangeCollection();
		} catch (e) {
			oCollection = [];
		}
		return oCollection[nIndex];
	}else{
		var oSelection = oDocument.defaultView.getSelection();
		return oSelection.rangeCount > nIndex ? oSelection.getRangeAt(nIndex) : void 0;
	}
};
