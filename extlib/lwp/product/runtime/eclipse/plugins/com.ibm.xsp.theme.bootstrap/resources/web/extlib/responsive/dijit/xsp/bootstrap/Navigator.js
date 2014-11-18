/*
 * © Copyright IBM Corp. 2014
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
dojo.provide("extlib.responsive.dijit.xsp.bootstrap.Navigator");

XSP.xbtMenuSwap = function(evt,effect,sfid,plusIcon,minusIcon) {
	var tg = (window.event) ? evt.srcElement : evt.target
	for(var ct = tg.parentNode.nextSibling; ct && ct.nodeType!=1; ) {
		ct = ct.nextSibling;
	}
	if(ct.style.display == "none") {
		if(effect == "wipe") {
			dojo.fx.wipeIn({node:ct}).play();
		} else {
			ct.style.display = "block";
		}		
		dojo.removeClass(tg, ""+plusIcon)
		dojo.addClass(tg, ""+minusIcon)
		if (sfid) {
			var field = XSP.getElementById(sfid);
			if (field) {
				field.value = 1;
			}
		}
	} else {
		if(effect == "wipe") {
			dojo.fx.wipeOut({node:ct}).play();
		} else {
			ct.style.display = "none";
		}		
		dojo.removeClass(tg, ""+minusIcon)
		dojo.addClass(tg, ""+plusIcon)
		if (sfid) {
			var field = XSP.getElementById(sfid);
			if (field) {
				field.value = 0;
			}
		}
	}
};