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

dojo.provide("dwa.lv.NarrowModeMenu");

dojo.require("dijit._Widget");

dojo.declare(
	"dwa.lv.NarrowModeMenu",
	dijit._Widget,
{
	lvid: null,
	vl: null,
	checkedMenuItem: null,
	sortBy: -1,
	designRead: false,
	bindState: false,

	postCreate: function(){
		this.inherited(arguments);

		// set listview widget reference
		var lvWidget = dijit.byId( this.lvid );
		if( !lvWidget ) return;
		this.vl = lvWidget.oVL;
	},

	updateDesign: function(){
		this.updateState();
	},
	updateState: function(){
	},

	_onChange: function( checked, index, menuItem, vl, id ){
	},
	_resetColWidth: function(vl, id){
	}
});

dwa.lv.NarrowModeMenu.create = function( lvWidgetId ){
	var menu = new dwa.lv.NarrowModeMenu( { lvid: lvWidgetId } );

	//menu.updateDesign();
	return menu;
};
