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
* TypeAheadStore.js
*/
define([
    "dojo/_base/declare", // declare
    "ibm/xsp/widget/layout/data/TypeAheadReadStore",
    "dojo/store/DataStore",
    "ibm/xsp/widget/layout/xspClientDojo",
    "dojo/_base/lang" // mixin(), isArray(), etc.
], function(declare, TypeAheadReadStore, DataStore, xspClientDojo, lang){
	// private internal class
	var _TypeAheadDataEx = declare("extlib.store.TypeAheadStore._TypeAheadDataEx", [TypeAheadReadStore], {
		constructor: function tade_ctor(/*Object*/options, /*Node*/typeAheadElement) {
			//console.log("debugging _TypeAheadDataEx constructor");
		},
		fetch: function fetch_override(/* Object */ args){
			// convert RegExp to String and move from .value to .name
			args.query.name = String(args.query.value);
			return this.inherited(arguments);
		},
		getValues: function getValues_override(item, attribute) {
			// override to change getValue to this.getValue
			return [this.getValue(item, attribute)];
		}
	});
	return declare("extlib.store.TypeAheadStore", /*superclass*/[], {
		_typeAheadDataWrapperStore: null,
		constructor: function tas_ctor(/*Object*/options, /*Node*/typeAheadElement) {
			var typeAheadData = new _TypeAheadDataEx(options, typeAheadElement);
			_typeAheadDataWrapperStore = new DataStore({store:typeAheadData});
		},
		query: function query(query, options){
			return _typeAheadDataWrapperStore.query(query, options);
		},
		get: function get(id){
			return _typeAheadDataWrapperStore.get(id);
		}
	});
});
