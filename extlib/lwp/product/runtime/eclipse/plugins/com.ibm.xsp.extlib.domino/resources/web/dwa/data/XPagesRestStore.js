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

dojo.provide("dwa.data.XPagesRestStore");
dojo.require("dwa.data._DominoStoreBase");
dojo.require("dwa.common.notesValue");

dojo.declare("dwa.data.XPagesRestStore",
			 dwa.data._DominoStoreBase,
{
	//	summary:
	//		A data store for XPages REST service

	type: "json",
	notesValue: new dwa.common.notesValue,

	_getCellValue: function(){
		//	summary:
		//		Get cell value from a cell object.
		//	description:
		//		Cell object is an attribute value of an item, which represents a row.
		//		It can be any type of data structure, which may include various meta data.
		//		This method will become a toString() for the cell object.
		//		'this' points to the cell object.
		var value = this["@value"];
		return ( typeof(value) == "undefined" ) ? "" : value.toString();
	},

	format: function(data){
		if(!data || !data.items){ return data; }
		var items = data.items;
		this._numRows = data["@topLevelEntries"];
		return items;
	}
});
