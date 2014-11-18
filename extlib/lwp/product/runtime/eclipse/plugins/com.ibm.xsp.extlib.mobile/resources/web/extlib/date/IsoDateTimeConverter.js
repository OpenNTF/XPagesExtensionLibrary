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
* IsoDateTimeConverter.java
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
			if(params){
				lang.mixin(this, params);
			}
		},
		convert: function(/*String*/ clientId, /*String*/value){
			var endDateIndex = value.indexOf('T');
			if( (-1 != endDateIndex) && endDateIndex - value.indexOf('-') < 6 ){
				// in Android
				// We have something like
				//	 2013-5-5T09:30
				// which the server-side converter will accept. 
				// But stamp.fromISOString will not accept it.
				// We need to mutate it to: 
				//	 2013-05-05T09:30
				var firstDash = value.indexOf('-');
				var secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
				if( -1 != firstDash && -1 != secondDash && 2 == (secondDash - firstDash) ){
					// insert 0 after 1st '-'
					value = value.substring(0,firstDash+1)+'0'+value.substring(firstDash+1);
					// recompute since the string changed:
					secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
					endDateIndex = value.indexOf('T');
				}
				if( -1 != secondDash && -1 != endDateIndex && 2 == (endDateIndex - secondDash) ){
					// insert 0 after 2nd '-'
					value = value.substring(0,secondDash+1)+'0'+value.substring(secondDash+1);
				}
			}
			
			var dateObj = stamp.fromISOString(/*formattedString*/value);
			if( null != dateObj ){
				if( value.length < 16 ){
					// yyyy-MM-ddTHH:mm
					// Verify does contain all 3 parts of yyyy-MM-dd
					// as fromISOString accepts yyyy and yyyy-MM too.
					// Verify 2 '-'s
					var firstColon = value.indexOf('-');
					var valid = (-1 != firstColon);
					var secondColon = (!valid)?-1: value.indexOf('-', firstColon+1);
					valid = valid && (-1 != secondColon);
					if( ! valid ){
						dateObj = null;
					}
				}
				if( -1 == value.indexOf('T') ){
					// Verify does contain a T
					dateObj = null;
				}
			}
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
