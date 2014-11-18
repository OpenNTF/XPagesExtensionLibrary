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
dojo.provide("extlib.dijit.Loading");
dojo.requireLocalization("dijit", "loading");

XSP.startAjaxLoading = function xe_ldsl(message) {
	dojo.require("dijit.Dialog");
	dojo.addOnLoad(function() {
		if (!message) {
			message = "Please wait...";
		}
		var ct = "<span class='dijitContentPaneLoading'>"+message+"</span>";
		XSP._axdlg = new dijit.Dialog({ title: "", content: ct });
		XSP._axdlg.titleBar.style.display='none';
		XSP._axdlg.show();
	});
}

XSP.endAjaxLoading = function xe_ldel() {
	if(XSP._axdlg) {
		XSP._axdlg.hide();
		XSP._axdlg = null;
	}
}

XSP.animateLoading = function xe_ldal(id,message) {
	var messages = dojo.i18n.getLocalization("dijit", "loading");
	message = message || messages["loadingState"];
	var he = "<span class='dijitContentPaneLoading'>"+message+"</span>";
	dojo.place(he,id,"first");
}
