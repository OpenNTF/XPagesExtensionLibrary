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
dojo.provide("extlib.responsive.dijit.xsp.bootstrap.AccordionPane");

dojo.require("dijit.layout.AccordionPane");

dojo.declare("extlib.responsive.dijit.xsp.bootstrap.AccordionPane",
		dijit.layout.AccordionPane, {
		_setContent: function(cont) {
			if(typeof(cont) == "string") {
				var extract = function(markStart, markEnd) {
					var startIndex = cont.indexOf(markStart);
					if( startIndex >= 0 ){
						var endIndex = cont.lastIndexOf(markEnd);
						if( endIndex >= 0 ) {
							var script = cont.substring(startIndex + markStart.length, endIndex);
							cont = cont.substring(0, startIndex) + cont.substring(endIndex + markEnd.length);
							return script;
						}
					}
				};
				var header = extract("<!-- XSP_UPDATE_HEADER_START -->\n","<!-- XSP_UPDATE_HEADER_END -->\n");
				if(header) {
					XSP.execScripts(XSP.processScripts(header,true));
				}
				this.inherited("_setContent", arguments);
			} else {
				this.inherited("_setContent", arguments);
			}
		}
	}
);