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
dojo.provide("extlib.dijit.PickerCheckbox");

dojo.require("dojo.cache")
dojo.require("extlib.dijit.TemplateDialog")
dojo.require("dojo.i18n")
dojo.require("dojo.string")
dojo.requireLocalization("extlib.dijit","pickers")

dojo.declare(
	'extlib.dijit.PickerCheckbox',
	[extlib.dijit.TemplateDialog],
	{
		msep: "",
		trim: false,
		listWidth: "270px",
		listHeight: "25em",
		PickerCheckBox_OK: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerCheckBox_OK,
		PickerCheckBox_Cancel: dojo.i18n.getLocalization("extlib.dijit","pickers",this.lang).PickerCheckBox_Cancel,
		templateString: dojo.cache("extlib.dijit","templates/PickerCheckbox.html"),
		controlValues: "",
		firstNode: null,
		postCreate: function() {
			this.inherited(arguments)
			this._fixStyles(this.templateString)
			var controlValue=this.getControlValue(this.control)
			this.controlValues = controlValue ? (this.msep ? controlValue.split(this.msep) : [controlValue]) : []
			if(this.trim) this.controlValues = dojo.map(this.controlValues,dojo.trim)
			this._fetchData()
		},
		ok: function(){
			var res = this._getResult()
			this.updateControl(this.control,res,this.msep)
            this.popup.destroyRecursive();
            this.destroyRecursive();
		},
		cancel: function(){
            this.popup.destroyRecursive();
            this.destroyRecursive();
		},
        show: function(){
			this.inherited(arguments)
			this.popup.onCancel = function(){
	            this.destroyRecursive();
				return 1;
			}
        },
		_fetchData: function() {
			this.firstNode = null;
			var _this = this;
			dojo.xhrGet({
		        url: this.url, 
		        preventCache: this.preventCache,
		        handleAs: "json",
		        load: function(resp, ioArgs) {
					var idx=0;
					dojo.forEach(resp.items, function(item) {
						_this._onItem(item,idx++)
					})
					_this._onComplete()
		        },
		        error: function(err,ioArgs) {
		        	if(ioArgs.xhr.status!=401) { // Ignore unauthorized
		        		alert(err)
		        	}
		        }
			});			
		},
		_onItem: function(item,idx){
			var id = "ck_"+idx
			var val = item["@value"]
			var lbl = item["@label"] || val
			var ck = dojo.create("input", {
				id: id,
				type: "checkbox",
				value: val,
				name: lbl,
				"class": "xspPickerInput"
			}, this.containerNode);
			dojo.create("label", {title: val, innerHTML: lbl, "class": "xspPickerSpan", "for": id}, this.containerNode);
			dojo.create("br", null, this.containerNode);
			if(val&&dojo.indexOf(this.controlValues,val)>=0) {
				ck.checked=true
				if(!this.firstNode) this.firstNode=ck
			}
		},
		_onComplete: function(){
			if(!this.firstNode) {
				this.firstNode = this.containerNode.firstChild 
			}
			this._updateResult()
			if(this.firstNode) setTimeout(dojo.hitch(this,function(){(this.firstNode||this.containerNode).focus()}),100);
		},
		_handleClick: function(e){
			if(!this.msep) {
				dojo.forEach(this.containerNode.getElementsByTagName("input"), function(ck){
					if(ck!=e.target) ck.checked=false;
				});
			}
			this._updateResult()
		},
		_getResult: function(){
			var val = []; var lbl = [];
			dojo.forEach(this.containerNode.getElementsByTagName("input"), function(ck){
				if(ck.checked) {
					val.push(ck.value)
					lbl.push(ck.nextSibling.innerHTML)
				}
			});
			return {values:val, labels:lbl};
		},
		_updateResult: function() {
			/*this.result.innerHTML = this._getResult().labels.join(',')*/
		}
	}
);
