/*
 * © Copyright IBM Corp. 2011
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

dojo.provide("extlib.dojo.helper.IFrameAdjuster");

dojo.ready(function() {
	var target = document.getElementsByTagName('iframe'),
	    regex = /loadFirebugConsole/,
	    i;
	for(i = 0; i < target.length; i++) {
		// Don't remove, but adjust any occasional Firebug iframe inserted by dojox.html.metrics
		if(regex.test(target[i].src)) {
			//target[i].parentNode.removeChild(target[i]);
			if (!target[i].id) {target[i].id = "firebug" + i}
			if (!target[i].title) {target[i].title = "firebug" + i}
			target[i].setAttribute("role", "presentation");
		}
		// Add title if needed to an iframe created by dojo.hash
		if (target[i].id && target[i].id == "dojo-hash-iframe" && !target[i].title) {
			target[i].title = "dojo-hash-iframe" + i;
			target[i].setAttribute("role", "presentation");
		}
	}
});


