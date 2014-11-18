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
* Date: 2014-Feb-04
* IsoDateConverter.js
*/
define([
    "dojo/_base/declare", // declare
    "dojo/date/stamp", // stamp
    "ibm/xsp/widget/layout/xspClientDojo",
	"dojo/_base/lang" // mixin(), isArray(), etc.
], function(declare, stamp, xspClientDojo, lang){

	// module:
	//		extlib.date.IsoDateConverter

	return declare("extlib.date.IsoDateConverter", /*superclass*/[], {
		// summary:
		//		ISO date converter
		//
		// description:
		//		This is an XSP Converter, which is an object with function:
		//		convert(String clientId, String value): Object
		
		// message: String
		//		Text displayed in the pop-up dialog if date conversion fails.
		//		Required.
		message: "",

		/*=====
		constructor: function(params){
			// summary:
			//		Create the converter.
			// params: Object|null
			//		Hash of initialization parameters for widget.
		 	//		The hash can contain any of the widget's properties, excluding read-only properties.
		 },
		=====*/
		postscript: function(/*Object?*/params){
			// summary:
			//		Kicks off converter instantiation, function invoked by the AMD declare function.
			// tags:
			//		private
			// mix in our passed parameters
			if(params){
				lang.mixin(this, params);
			}
		},
		
		convert: function(/*String*/ clientId, /*String*/value){
			// summary:
			//		called to convert the string value to a Date object
			// clientId: String
			//		ID of the control where the value was entered.
			// value: String
			//		non-null & non-empty String value to be converted to a Fate.
			// returns: Date
			//		null if some conversion problem occurred and was reported through XSP.validationError,
			//		or the Date value created from the String value. 
			// description:
			//		Convert the value to a Date, using the ISO date format, 
			//		reporting any conversion problems to XSP.validationError
			
			var endDateIndex = value.length;
			if( endDateIndex - value.indexOf('-') < 6 ){
				// in Android
				// We have something like
				//	 2013-5-5
				// which the server-side converter will accept. 
				// But stamp.fromISOString will not accept it.
				// We need to mutate it to: 
				//	 2013-05-05
				var firstDash = value.indexOf('-');
				var secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
				if( -1 != firstDash && -1 != secondDash && 2 == (secondDash - firstDash) ){
					// insert 0 after 1st '-'
					value = value.substring(0,firstDash+1)+'0'+value.substring(firstDash+1);
					// recompute since the string changed:
					secondDash = (-1 == firstDash)? -1 : value.indexOf('-', firstDash+1);
					endDateIndex = value.length;
				}
				if( -1 != secondDash && -1 != endDateIndex && 2 == (endDateIndex - secondDash) ){
					// insert 0 after 2nd '-'
					value = value.substring(0,secondDash+1)+'0'+value.substring(secondDash+1);
				}
			}

			
			var dateObj = stamp.fromISOString(/*formattedString*/value);
			if( null != dateObj ){
				// fromISOString accepts the formats:
				//    yyyy
				//    yyyy-MM
				//    yyyy-MM-dd
				// but this converter requires year, month and day-of-month
				if( value.length < 10 ){
					// verify 2 '-'s
					var firstColon = value.indexOf('-');
					var valid = (-1 != firstColon);
					var secondColon = (!valid)?-1: value.indexOf('-', firstColon+1);
					valid = valid && (-1 != secondColon);
					if( ! valid ){
						dateObj = null;
					}
				}
				if( -1 != value.indexOf('T') ){
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
