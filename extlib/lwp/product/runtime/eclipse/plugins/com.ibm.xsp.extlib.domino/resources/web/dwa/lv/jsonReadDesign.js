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

dojo.provide("dwa.lv.jsonReadDesign");

dojo.require("dwa.lv.readJsonListener");

dojo.declare(
	"dwa.lv.jsonReadDesign",
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
		var oListener = new dwa.lv.readJsonListener;
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
			oListener.oCallback = function(oJsonRoot, sError){
				if(!oJsonRoot && sError)
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
		if(!oRequest.oJsonRoot)
			return null;
		if(oRequest.oQuery.Form == 's_ReadDesignEx_JSON')
			return this._selectSingleNode(oRequest.oJsonRoot, 'design');
		return oRequest.oJsonRoot;
	},
	getDesignExRoot: function(oRequest){
		if(!oRequest.oJsonRoot)
			return null;
		if(oRequest.oQuery.Form == 's_ReadDesignEx_JSON')
			return this._selectSingleNode(oRequest.oJsonRoot, 'designex');
		return null;
	},
	getUrl: function(oQuery){
		if(this.url.indexOf("://") == -1){
			oQuery.Form = oQuery.FormName + "_JSON";
			return this.url
		}
		if(!this.dwa)
			return this.url + '/' + oQuery.FolderName + '?ReadDesign&TZType=UTC&OutputFormat=Json';
		else {
			oQuery.Form = oQuery.FormName + "_JSON";
			return this.url + '/iNotes/Proxy?OpenDocument&Form=' + oQuery.Form
		            + "&PresetFields=FolderName;" + encodeURIComponent(oQuery.FolderName)
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
		return aNodes.length;
	},
	getItem: function(aNodes, nIndex){
		return aNodes[nIndex];
	},
	getText: function(oNode, sJoinChar){
		if( !oNode ) return '';
		return oNode.text;
	},
	getAttribute: function(oNode, sName){
		return oNode['@' + sName];
	},
	getAttributeBoolean: function(oNode, sName){
		return (!/^(false|no|0|)$/.test(this.getAttribute(oNode, sName) || ''));
	},
	getAttributeInt: function(oNode, sName){
		return (this.getAttribute(oNode, sName) - 0);
	},
	getAttributeString: function(oNode, sName){
		return (this.getAttribute(oNode, sName) || '');
	},
	_selectNodes: function(oNode, sElement){
		return oNode[sElement];
	},
	_selectSingleNode: function(oNode, sElement){
		return oNode[sElement] ? (oNode[sElement].constructor == Array ? oNode[sElement][0] : oNode[sElement]) : null;
	}
});
