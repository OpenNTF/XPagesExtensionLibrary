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
dojo.provide("extlib.dijit.Menu");

dojo.require("dijit.Menu");

XSP.openMenu = function xe_opnme(evt,menuCtor) {
	var menu=menuCtor();
	evt=dojo.fixEvent(evt);
	function closeAndRestoreFocus(){
		try {dijit.focus(evt.target);} catch(exception){}
		dijit.popup.close(menu);
		menu.destroy();
	}
	dijit.popup.open({popup:menu,around:evt.target/*,orient:dojo._isBodyLtr()...*/,onExecute:closeAndRestoreFocus,onCancel:closeAndRestoreFocus});
	menu.focus();
	dojo.connect(menu,"_onBlur",function(){
		dijit.popup.close(menu);
		menu.destroy();
	});
	dojo.stopEvent(evt);	
};