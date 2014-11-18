/*
 * Copyright IBM Corp. 2014
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 17 Feb 2014
* TypeAheadCombo.js
*/
define([
    "dojo/_base/declare", // declare
    "dojox/mobile/ComboBox",
    "dojo/_base/lang" // mixin(), isArray(), etc.
], function(declare, ComboBox, lang){

	return declare("extlib.dijit.mobile.TypeAheadCombo", /*superclass*/[ComboBox], {
		constructor: function tars_ctor(/*Object*/options, /*Node*/node) {
		},
		destroy: function(preserveDom){
			if(this.dropDown && !this.dropDown._destroyed){
				this.dropDown.destroyRecursive(preserveDom);
				this.dropDown = null;
			}
			this.inherited(arguments);
		},
		buildRendering: function(){
			this.inherited(arguments);
			if( this.textbox ){
				// SPR#MKEE9L7E9B, setting autocomplete="off" in the initial HTML
				// was setting dojox.mobile.ComboBox autoComplete=true (boolean),
				// so instead setting autocomplete="off" on the modified HTML
				// to avoid changing that dijit autoComplete value.
				if( !dojo.hasAttr(this.textbox, 'autocomplete') ){
					dojo.attr(this.textbox, 'autocomplete','off');
				}
			}
		}
	});
});
