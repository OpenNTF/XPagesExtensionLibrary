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

dojo.provide("dwa.common.listeners");

dojo.declare(
	"dwa.common.responseListener",
	null,
{
	constructor: function(sKey){
		if (sKey == 'VOID')
			return;
	
		sKey = sKey ? sKey : ('LISTENER' + dwa.common.responseListener.prototype.nAutoGenKey++);
	
		for (var sOrigKey = sKey; this.oListeners[sKey]; dwa.common.responseListener.prototype.nAutoGenKey++)
			sKey = sOrigKey + dwa.common.responseListener.prototype.nAutoGenKey;
	
		this.oListeners[sKey] = this;
		this.sKey = sKey;
	},
	oListeners: {},
	nAutoGenKey: 0,
	track: function(sActivityHandler, nTimeoutDuration){
	},
	release: function(){
		var sKey = this.sKey;
		for (var s in this)
			delete this[s];
		delete this.oListeners[sKey];
	},
	onError: function(e){
		if (this.oListener && this.oListener.onError)
			this.oListener.onError(e);
	},
	onDataAvailable: function(){
	},
	onDatasetComplete: function(){
	},
	onReadyStateChange: function(){
	}
});


dojo.declare(
	"dwa.common.xmlListener",
	dwa.common.responseListener,
{
	empty: function(){
		if (!this.oHttpRequest)
			return;
		if(dojo.isMozilla){
			this.oHttpRequest.onload = null;
			this.oHttpRequest.onerror = null;
		}else if(dojo.isWebKit){
			this.oHttpRequest.onreadystatechange = this.onDatasetComplete;
		}else{
			this.oHttpRequest.onreadystatechange = this.onDatasetComplete;
		}
	},
	load: function(sUrl, sData, oHeaders){
		// Keep fnResume/fnCancel for re-launching request upon checkHttpStatus()
		this.fnResume = dojo.hitch(this, "load", sUrl, sData, oHeaders);
		this.fnCancel = dojo.hitch(this, "release");
	
		var sLog = "Loading URL: " + sUrl + '... (Key: ' + this.sKey + ')';
		console.debug(sLog);
	
		this.empty();
		this.oHttpRequest = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject('Microsoft.XMLHttp');
		if(dojo.isMozilla){
			this.oHttpRequest.onload = dojo.hitch(this, "onDatasetComplete");
			this.oHttpRequest.onerror = dojo.hitch(this, "onError");
		}else{
			this.oHttpRequest.onreadystatechange = dojo.hitch(this, "onReadyStateChange");
		}
		// Some code that uses the proxy server needs to be able to issue HEAD requests.
		this.oHttpRequest.open(this.sMethod ? this.sMethod : !sData ? 'GET' : 'POST', sUrl, true);
		if (oHeaders ) {
			for(var h in oHeaders) {
				this.oHttpRequest.setRequestHeader(h, oHeaders[h]);
			}
		}
		// begin: nonce
		//if (sData) {
		//	this.oHttpRequest.setRequestHeader('X-IBM-INOTES-NONCE', com_ibm_dwa_globals.sNonce);
		//}
		// end: nonce
		this.oHttpRequest.send(sData ? sData : null);
	},
	checkHttpStatus: function(vContentType){
		var sResponseContentType = this.oHttpRequest.getResponseHeader('Content-Type');
		var nStatus = this.oHttpRequest.status;
		var bSuccess = (2 == Math.floor(nStatus/100));	// Any 2xx code is valid
	
		for (var asContentType = vContentType instanceof Array ? vContentType : [vContentType], i = 0;
			 bSuccess && i < asContentType.length;
			 i++) 
		{
			if (-1 != sResponseContentType.indexOf(asContentType[i]))
				return true;
		}
	
		var fChecked = this.fChecked;
		this.fChecked = true;
	
		if (!bSuccess)
			throw new Error(dwa.common.utils.formatMessage("Bad HTTP status: %1 (%2)", nStatus, this.oHttpRequest.statusText));
		else
			throw new Error(dwa.common.utils.formatMessage("Bad content type: %1", sResponseContentType));
	},
	release: function(){
		this.empty();
		if(dojo.isMozilla){
			this.inherited(arguments);
		}else if(dojo.isWebKit){
			this.inherited(arguments);
		}else{
			// there is crash in IE6 when we release the oHttpRequest object inside the onDatasetComplete 
			// handler, so use a setTimeout to workaround the problem.
			var _arguments = arguments;
			setTimeout(dojo.hitch(this, function(d){
				this.inherited(_arguments);
				return d;
			}), 10);
		}
	}
});


dojo.declare(
	"dwa.common.elementListener",
	dwa.common.responseListener,
{
	nElementListenerTimeout: 0, // com_ibm_dwa_globals.oSettings.nElementListenerTimeout
	constructor: function(sKey){
		if (sKey == 'VOID')
			return;
		this.aoListeners = [this];
	},
	load: function(sHref){
		this.sHref = sHref;
		if (!this.checkInFlight())
			return;
	
		if (this.nElementListenerTimeout > 0)
			setTimeout(dojo.hitch(this, "loadImpl", sHref), this.nElementListenerTimeout);
		else
			this.loadImpl(sHref);
	},
	loadImpl: function(sHref){
		this.createElement();
	
		var sLog = 'Loading element ' + this.sElementName + ' with ' + this.sHrefName + '=' + this.sHref + '... (Key: ' + this.sKey + ')';
		console.debug(sLog);
		this.oElement.setAttribute(this.sHrefName, sHref);
		this.oElement.ownerDocument.getElementsByTagName(this.sParent)[0].appendChild(this.oElement);
	},
	checkInFlight: function(){
		if (dwa.common.elementListener.prototype.oInFlight[this.sHref]
		 && dwa.common.elementListener.prototype.oInFlight[this.sHref] != this) {
			dwa.common.elementListener.prototype.oInFlight[this.sHref].aoListeners.push(this);
			return false;
		}
	
		dwa.common.elementListener.prototype.oInFlight[this.sHref] = this;
		return true;
	},
	createElement: function(){
		this.oElement = this.oDocument ? this.oDocument.createElement(this.sElementName) : document.createElement(this.sElementName);
	
		for (var s in this.oValues)
			this.oElement[s] = this.oValues[s];
	
		this.oElement.id = this.sKey;
		if(dojo.isMozilla || dojo.isWebKit){
			this.oElement.onload = dojo.hitch(this, "handleEvent", 'onDatasetComplete');
		}else{
			this.oElement.onreadystatechange = dojo.hitch(this, "handleEvent", 'onReadyStateChange');
		}
	},
	handleEvent: function(sEvent){
		var fCancel = sEvent == 'CANCEL';
		for (var i = 0; i < this.aoListeners.length; i++) {
			if (!fCancel)
				this.aoListeners[i][sEvent](this.oElement.readyState);
			if (this.aoListeners[i] != this && (this.aoListeners[i].fToBeReleased || fCancel))
				this.aoListeners[i].release();
		}
		dwa.common.elementListener.prototype.oInFlight[this.sHref] = void 0;
		if (this.fToBeReleased || fCancel)
			this.release();
	},
	release: function(){
		if(dojo.isMozilla || dojo.isWebKit){
			if (this.oElement)
				this.oElement.onload = null;
		}else{
			if (this.oElement)
				this.oElement.onreadystatechange = null;
		}
		delete this.oElement;
		this.inherited(arguments);
	},
	oInFlight: {}
});


dojo.declare(
	"dwa.common.scriptListener",
	dwa.common.elementListener,
{
	sHrefName: 'src',
	sElementName: 'script',
	sParent: 'head',
	oValues: {type: 'text/javascript'}
});


dojo.declare(
	"dwa.common.consolidatedImageListener",
	dwa.common.elementListener,
{
	constructor: function(asIds, sHref, sKey){
		if (sKey == 'VOID')
			return;
		this.asIds = asIds;
		this.sHref = sHref;
	
		if (!asIds || !this.checkInFlight())
			return;
	
		this.createElement();
		this.oElement.style.cssText = 'position:absolute;width:0px;height:0px;top:0px;left:0px;';
		//this.oElement.setAttribute(this.sHrefName, sHref);//_ak//this does not work for relative paths in IE8 real mode.
		this.oElement.src = sHref;
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
					oElem.style.backgroundImage = 'url(' + this.sHref + ')';
					oElem.style.backgroundPosition = '-' + oElem.getAttribute('xoffset') + 'px -' + oElem.getAttribute('yoffset') + 'px';
					oElem.setAttribute('xoffset', '');
					oElem.setAttribute('yoffset', '');
				}
			}
		}
	
		this.fToBeReleased = true;
	},
	sHrefName: 'src',
	sElementName: 'img',
	sParent: 'body',
	oValues: {}
});
dwa.common.consolidatedImageListener.prototype.onDatasetComplete = dwa.common.consolidatedImageListener.prototype.onReadyStateChange;
