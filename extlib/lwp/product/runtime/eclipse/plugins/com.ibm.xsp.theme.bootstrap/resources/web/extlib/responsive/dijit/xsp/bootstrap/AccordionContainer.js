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
dojo.provide("extlib.responsive.dijit.xsp.bootstrap.AccordionContainer");

dojo.require("extlib.dijit.AccordionContainer");

dojo.declare("extlib.responsive.dijit.xsp.bootstrap.AccordionContainer",
	extlib.dijit.AccordionContainer, {
		postCreate: function(){
			this.inherited(arguments)
			
			dojo.query(".dijitAccordionContainer", this.domNode).forEach(function(n){dojo.attr(n,"class","accordion")});
			dojo.query(".dijitAccordionInnerContainer", this.domNode).forEach(function(n){dojo.attr(n,"class","accordion-group")});
			dojo.query(".dijitAccordionTitle", this.domNode).forEach(function(n){dojo.attr(n,"class","accordion-heading")});
			dojo.query(".dijitAccordionTitleFocus", this.domNode).forEach(function(n){dojo.attr(n,"class","accordion-toggle")});
			dojo.query(".dijitAccordionChildWrapper", this.domNode).forEach(function(n){dojo.attr(n,"class","accordion-body collapse")});
	    }
	}
);