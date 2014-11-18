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
dojo.provide("extlib.dijit.DialogNext");

dojo.require("dijit.Dialog");

dojo.declare(
	"extlib.dijit.Dialog",
	[dijit.Dialog],
	{
		keepComponents: false
	}
);

dojo.declare(
	"extlib.dijit._DialogWrapper",
	[dijit._Widget],
	{
		dialogId: "",
		destroy: function(/*Boolean*/ preserveDom) {
			// Destroy the associated dialog
			var dlg=dijit.byId(this.dialogId);
			if(dlg) {
				dlg.destroy();
			}
			this.inherited(arguments);
        }
	}
);

XSP.openDialog = function xe_od(dialogId,options,params) {
	var dlg = dijit.byId(dialogId)
	if(dlg) {
		// If the dialog exists, then keep it if asked for
		if(dlg.keepComponents) {
			dlg.show();
			return;
		}
	} else {
		// Move the tag that will be partial refreshed to the body tag
		// Else, the form it contains won't be created as it will be within 
		// the main form
		//dojo.place(dojo.byId(dialogId),dojo.body());
	}
	var onComplete = function() {
		// The dialog content is not parsed properly, maybe because the node is moved?
		// To be investigated, but we force it to be parsed
		dojo.parser.parse(dojo.byId(dialogId));
		dijit.byId(dialogId).show();
	}
	var axOptions = {
		"params": dojo.mixin({'$$showdialog':true},params),
		"onComplete": onComplete,
		"formId": dialogId
	}
	XSP.partialRefreshGet(dialogId,axOptions)		
}

XSP.closeDialog = function xe_cd(dialogId,refreshId,params){
	var dlg = dijit.byId(dialogId);
	if(dlg){
		// As closeDialog can be called from partial refresh, we need to delay
		// this after partial refresh id completed
		setTimeout(dojo.hitch(this,function(){
			dlg.hide();
			if(refreshId) {
				var axOptions = {"params": params}
				XSP.partialRefreshGet(refreshId,axOptions)
			}
		}),0);
	}
}
