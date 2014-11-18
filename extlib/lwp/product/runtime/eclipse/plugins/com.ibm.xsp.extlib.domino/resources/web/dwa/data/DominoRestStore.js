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

dojo.provide("dwa.data.DominoRestStore");

dojo.require("dwa.data._DominoStoreBase");

dojo.declare("dwa.data.DominoRestStore", dwa.data._DominoStoreBase, {

	database: null,
	view: null,
	url: "",
	type: "json",
	FolderName: "",
	page: null,
	
	getUrl: function(request){
		var url = null;
		if (this.view)
			url = this.view.getURL();
		return url;
	},
	
	_fetchItems: function(request){

		if (!this.database)
			this.database = new lotus_domino_Database(this.url);
	
		var sFolder = request.query.FolderName;
		if (this.FolderName!=sFolder){
			this.view = this.database.getView(sFolder,true);
			this.FolderName=sFolder;
		}

		var iCount = request.query && request.query.count ? request.query.count : request.count ? request.count : null;
		var iStart = request.query && request.query.start ? request.query.start : request.start ? request.start : null;
		
		if (iCount)
			this.view.setPageSize(iCount);
		
		if (iStart)
			this.view.setPos(iStart);
		
		this.view.getPage(dojo.hitch(this,"_onPageRetrieval",request),dojo.hitch(this,"_onFailure"));
	},
	_onFailure: function(e){
		throw new Error(e);
	},
	
	
	
	_onPageRetrieval: function(oRequest,oPage){
		this.page=oPage;

		var oItems = this.page.getEntries();
		if (oItems){
			var nRows = oItems.length;
			
			var aItems = this.format(oItems);
			//this.aItems = this.aItems ? this.aItems.concat(aItems) : aItems;
			
			this.fetchHandler(aItems,oRequest,149);
		}
	},
	
	format: function(aData){
		var aItems = [];
		for(var i=0;i<aData.length;i++)
			aItems.push(this.formatEntry(aData[i]));
		
		return aItems;
	},
	
	formatEntry: function(viewEntry){
		var item = {};

		var oColumns = viewEntry.getView().getColumns();
		var oEntryCols = viewEntry.getColumns();
		
		for(var x=0;x<oColumns.length;x++){
			var oColumn = oColumns[x];
			
			var sProgName = oColumn.getProgName();

			var oEntryCol = oEntryCols[oColumn.getID()];
				

			var oValue = viewEntry.getValue(oColumn);
			
			//item[sProgName]=oValue;
			item[sProgName] = 
			{
					"@columnnumber": parseInt(oColumn.getID().replace("col",""))-2,
					"@name": sProgName+"",
					"@type": oEntryCol ? oEntryCol.type : "text",
					"_isColumn":true,
					"_S": this,
					"@value": oValue ? oValue : ""
			};

		}
		
		item["@children"]=0;
		item["@descendents"]=0;
//		item["@siblings"]=5;
		item["@noteid"]=viewEntry.getUNID();
		item["@position"]=viewEntry.getTumbler();
		item["@unid"]=viewEntry.getUNID();
		item["@unread"]=false;	
		
		return item;
	}
	
	
});
