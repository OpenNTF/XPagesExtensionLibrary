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
dojo.provide("extlib.dijit.AccordionContainer");

dojo.require("dijit.layout.AccordionContainer");

dojo.declare("extlib.dijit.AccordionContainer",dijit.layout.AccordionContainer, 
	{
		selectChild: function(page) {
			this.inherited(arguments)
			var form = XSP.findForm(this.id)
			if(form&&form[this.id+"_sel"]) {
				form[this.id+"_sel"].value = page ? page.id.substring(page.id.lastIndexOf(':')+1) : ""
			}
		}
	}
);
