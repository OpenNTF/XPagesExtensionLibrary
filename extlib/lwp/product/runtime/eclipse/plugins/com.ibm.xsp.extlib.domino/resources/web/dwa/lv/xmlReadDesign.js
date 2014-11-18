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

dojo.provide("dwa.lv.xmlReadDesign");

dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.readXmlListener");

dojo.declare(
	"dwa.lv.xmlReadDesign",
	null,
{
	dwa: true,
	url: "",

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}
	},
	load: function(oRequest){
		var oListener = new dwa.lv.readXmlListener;
		oListener.oRequest = oRequest;
	
		var sUrl = this.getUrl(oRequest.oQuery);
		dojo.publish("loadDesign", [{url:sUrl}]);
		if(oRequest.bSynchronous) {
			oListener.load(sUrl, void 0, void 0, true);
			oListener.processResponse();
			oListener.release();
		}
		else {
			var oDataStore = this;
			oListener.oRequest = oRequest;
			oListener.oClass = oRequest.oClass;
			oListener.oCallback = function(oXmlRoot, sError){
				if(!oXmlRoot && sError)
					alert(sError);
				oRequest.fnCallback.call(oListener.oClass, oListener.oRequest);
			};
			if(oRequest.sTabId)
				oListener.track(oRequest.sTabId);
			oListener.load(sUrl);
		}
		return true;
	},
	getDesignRoot: function(oRequest){
		if(!oRequest.oXmlRoot)
			return null;
		if(oRequest.oQuery.Form == 's_ReadDesignEx') {
			return this._selectSingleNode(oRequest.oXmlRoot, 'viewdesign');
		} else {
			return oRequest.oXmlRoot;
		}
	},
	getDesignExRoot: function(oRequest){
		if(!oRequest.oxmlRoot)
			return null;
		if(oRequest.oQuery.Form == 's_ReadDesignEx') {
			return this._selectSingleNode(oRequest.oXmlRoot, 'viewdesignex');
		} else {
			return null;
		}
	},
	getUrl: function(oQuery){
		if(this.url.indexOf("://") == -1){ return this.url }
		if(!this.dwa)
			return this.url + '/' + oQuery.FolderName + '?ReadDesign&TZType=UTC';
		else {
			oQuery.Form = oQuery.FormName;
			return this.url + '/iNotes/Proxy?OpenDocument&Form=' + oQuery.Form
		            + "&PresetFields=" + (oQuery.bReadDesignEx ? "s_ViewName" : "FolderName") + ";" + encodeURIComponent(oQuery.FolderName)
                    + (typeof oQuery.sDirIndex != 'undefined' ? ",DirIndex;" + oQuery.sDirIndex : '')
		            + ',noPI;1' + "&KIC";
		}
	},
	getColumns: function(oRequest){
		return this._selectNodes(this.getDesignRoot(oRequest), "column");
	},
	getDatetimeFormat: function(oColumn){
		return this._selectSingleNode(oColumn, "datetimeformat");
	},
	getNumberFormat: function(oColumn){
		return this._selectSingleNode(oColumn, "numberformat");
	},
	getLength: function(aNodes){
		return (dojo.isIE ? (aNodes.length) : (dojo.isMozilla ? (aNodes.snapshotLength) : (aNodes.snapshotLength) ) );
	},
	getItem: function(aNodes, nIndex){
		return (dojo.isIE ? (aNodes[nIndex]) : (dojo.isMozilla ? (aNodes.snapshotItem(nIndex)) : (aNodes.snapshotItem(nIndex)) ) );
	},
	getText: function(oNode, sJoinChar){
		if( !oNode ) return '';
	 if( dojo.isMozilla || dojo.isWebKit ){
		var a0=this._selectNodes(oNode, kXMLSelText); //"descendant::text()"  IE can't handle "descendant" format
		if( a0 ){
			for( var i=0,n=0,a1=[]; i<this.getLength(a0); i++ ){
				if('\n'!=this.getItem(a0, i).nodeValue) a1[n++]=this.getItem(a0, i).nodeValue;
			}
			return a1.join(sJoinChar?sJoinChar:'');
		}
		return '';
	 }else{ // GS
		return oNode.text;
	 } // end - I
	},
	getAttribute: function(oNode, sName){
		return oNode.getAttribute(sName);
	},
	getAttributeBoolean: function(oNode, sName){
		return (!/^(false|no|0|)$/.test(oNode.getAttribute(sName) || ''));
	},
	getAttributeInt: function(oNode, sName){
		return (oNode.getAttribute(sName) - 0);
	},
	getAttributeString: function(oNode, sName){
		return (oNode.getAttribute(sName) || '');
	},
	_selectNodes: function(oNode, sElement){
		return (dojo.isIE ? (oNode.selectNodes(sElement)) : (dojo.isMozilla ? (dwa.lv.globals.get().oXPathEvaluator.evaluate(sElement, oNode, dwa.lv.globals.get().oXPathEvaluator.createNSResolver(oNode), XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)) : (dwa.lv.globals.get().oXPathEvaluator.evaluate(sElement, oNode, dwa.lv.globals.get().oXPathEvaluator.createNSResolver(oNode), XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)) ) );
	},
	_selectSingleNode: function(oNode, sElement){
		return (dojo.isIE ? (oNode.selectSingleNode(sElement)) : (dojo.isMozilla ? (dwa.lv.globals.get().oXPathEvaluator.evaluate(sElement, oNode, dwa.lv.globals.get().oXPathEvaluator.createNSResolver(oNode), XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue) : (dwa.lv.globals.get().oXPathEvaluator.evaluate(sElement, oNode, dwa.lv.globals.get().oXPathEvaluator.createNSResolver(oNode), XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue) ) );
	}
});
