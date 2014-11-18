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
/*
* Date: 10 Feb 2014
* IsoTimeConverter.java
*/
define([
    "dojo/_base/declare", // declare
    "dojo/date/stamp", // stamp
    "ibm/xsp/widget/layout/xspClientDojo",
	"dojo/_base/lang" // mixin(), isArray(), etc.
], function(declare, stamp, xspClientDojo, lang){
	return declare("extlib.date.IsoDateTimeConverter",[], {
		// message: String
		//		Text displayed in the pop-up dialog if date conversion fails.
		//		Required.
		message: "",
		postscript: function(/*Object?*/params){
			// mix in our passed parameters
			if(params){
				lang.mixin(this, params);
			}
		},
		
		convert: function(/*String*/ clientId, /*String*/value){
			// fromISOString always expects an initial T, but this converter does not.
			value = "T"+value;
			
			var dateObj = stamp.fromISOString(/*formattedString*/value);
			// This doesn't accept values containing a date like:
			//    yyyy
			//    yyyy-MM
			//    yyyy-MM-dd
			// We only want:
			// THH:mm
			var element = XSP.getElementById(clientId); // get hidden input field
			if( null == dateObj  ){
				// conversion failed.
				if( null != element ){
					element.setAttribute('aria-invalid', 'true');
				}
				XSP.validationError(clientId, this.message);
				return null;
			}
			// pass.
			if( null != element ){
				element.setAttribute('aria-invalid', 'false');
			}
			return dateObj;
		}// end convert
	});
});
