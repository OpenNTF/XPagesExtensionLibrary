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

dojo.provide("dwa.common.notesValue");

dojo.require("dwa.date.calendar");

dojo.declare(
	"dwa.common.notesValue",
	null,
{
	constructor: function(vValue, oZoneInfo){
		this.vValue = vValue; this.oZoneInfo = oZoneInfo;
		this.toString = this.getString;

		if( dojo.isIE ){
			this.D = {
				selectSingleNode: function(oNode, sExpression){
					return oNode.selectSingleNode(sExpression);
				},
				selectNodes: function(oNode, sExpression){
					return oNode.selectNodes(sExpression);
				},
				snapshotLength:  function(oNode){
					return oNode.length;
				},
				snapshotItem: function(oNode, i){
					return oNode[i];
				},
				snapshotItemText: function(oNode, i){
					return oNode[i].text;
				},
				nodeText: function(oNode){
					return oNode.text;
				}
			};
		}else{
			this.D = {
				oXPathEvaluator: new XPathEvaluator(),
				selectSingleNode: function(oNode, sExpression){
					return this.oXPathEvaluator.evaluate(sExpression, oNode, this.oXPathEvaluator.createNSResolver(oNode), XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue
				},
				selectNodes: function(oNode, sExpression){
					return this.oXPathEvaluator.evaluate(sExpression, oNode, this.oXPathEvaluator.createNSResolver(oNode), XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
				},
				snapshotLength:  function(oNode){
					return oNode.snapshotLength;
				},
				snapshotItem: function(oNode, i){
					return oNode.snapshotItem(i);
				},
				snapshotItemText: function(oNode, i){
					return oNode.snapshotItem(i).textContent;
				},
				nodeText: function(oNode){
					return oNode.textContent;
				}
			};
		}

	},
	DATA_TYPES: { number:1, text:1, datetime:1, datetimelist:1, numberlist:1, textlist:1 },
	FIRST_VALUE_NODE_EXP:	"./*[name()!=\'#text\']",
	VALID_TEXT_VALUES:	"./" + "/text()[.!=\'\n\']",

	// =====================================
	// Outputs:
	//   (routine) - The type of Notes value
	// =====================================
	getType: function(){
		if( this.sType ) return this.sType;

		switch (typeof(this.vValue)) {
		case 'object':
			if (this.vValue instanceof Array) {
				if (this.vValue.length == 2
				 && (this.vValue[0] instanceof Date || this.vValue[0] instanceof dwa.date.calendar)
				 && (this.vValue[1] instanceof Date || this.vValue[1] instanceof dwa.date.calendar))
					return 'datetimepair';
				else
					return (new dwa.common.notesValue(this.vValue[0])).getType().replace(/pair$/, '') + 'list';
			} else if (this.vValue instanceof Date || this.vValue instanceof dwa.date.calendar) {
				return 'datetime';
			}
	
			return 'text';
		case 'number':
		case 'boolean':
			return 'number';
		}
	
		return 'text';
	},
	getString: function(){
		switch (typeof(this.vValue)) {
		case 'object':
			if (this.vValue instanceof Array) {
				var asList = [];
	
				for (var i = 0; i < this.vValue.length; i++) {
					if (this.vValue[i] instanceof Array && this.vValue[i].length == 2
					 && (this.vValue[i][0] instanceof Date || this.vValue[i][0] instanceof dwa.date.calendar)
					 && (this.vValue[i][1] instanceof Date || this.vValue[i][1] instanceof dwa.date.calendar)) {
						var asPair = [];
	
						for (var j = 0; j < this.vValue[i].length; j++)
							asPair[j] = (new dwa.common.notesValue(this.vValue[i][j], this.oZoneInfo)).getString();
	
						asList[i] = asPair.join('/');
					} else {
						asList[i] = (new dwa.common.notesValue(this.vValue[i], this.oZoneInfo)).getString();
					}
				}
	
				return asList.join(';');
			} else if (this.vValue instanceof Date) {
				return (new dwa.date.calendar).setDate(this.vValue, this.oZoneInfo).getISO8601String();
			} else if (this.vValue instanceof dwa.date.calendar) {
				return this.vValue.getISO8601String();
			}
	
			return '' + this.vValue;
		case 'boolean':
			return '' + (this.vValue - 0);
		case 'undefined':
			return '';
		}
	
		return '' + this.vValue;
	},
	setJsonNode: function(oNode){
		if (!oNode) {
			this.vValue = void 0;
			return this;
		}
	
		var sName, oValue;
	
		//for (var s in oNode) {
//bug			if (s[0] == '@')
		//	if (s.charAt(0) == '@')
		//		continue;
		//	oValue = oNode[s];
		//	sName = s;
		//}
		var types = this.DATA_TYPES;
		for(var s in types){
			if(oNode[s]){
				sName = s;
				oValue = oNode[s];
				break;
			}
		}

		if( typeof(sName) == "undefined" ){
			this.sType = "text";
			this.vValue = "";
			return this;
		}else{
			this.sType = sName;
		}
	
		switch (sName) {
		case 'datetimelist':
			var oResultPair = oValue.datetimepair;
			var oResultItem = oValue.datetime;
			var aoDateList = [];
	
			// range entries
			if (oResultPair) {
				for (var i = 0; i < oResultPair.length; i++) {
					var aoDatePair = [];
					var oResultPairItem = oResultPair[i].datetime;
					for (var j = 0; j < oResultPairItem.length; j++) {
						var oCalendar = (new dwa.date.calendar).setISO8601String(oResultPairItem[j][0]);
						aoDatePair[j] = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
					}
					aoDateList[aoDateList.length] = aoDatePair;
				}
			}
	
			// list entries
			if (oResultItem) {
				for (var i = 0; i < oResultItem.length; i++) {
					var oCalendar = (new dwa.date.calendar).setISO8601String(oResultItem[i][0]);
					aoDateList[aoDateList.length] = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
				}
			}
	
			this.vValue = aoDateList;
			return this;
		case 'datetime':
			var oCalendar = (new dwa.date.calendar).setISO8601String(oValue[0]);
			this.vValue = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
			return this;
		case 'numberlist':
			var oResult = oValue.number;
			var anValue = [];
	
			if (oResult) {
				for (var i = 0; i < oResult.length; i++)
					anValue[i] = oResult[i][0] - 0;
			}
	
			this.vValue = anValue;
			return this;
		case 'number':
			this.vValue = oValue[0] - 0;
			return this;
		case 'textlist':
			var oResult = oValue.text;
			var asValue = [];
	
			if (oResult) {
				for (var i = 0; i < oResult.length; i++)
					asValue[i] = oResult[i][0];
			}
	
			this.vValue = asValue;
			return this;
		}
	
		this.vValue = oValue[0];
		return this;
	},

	setXmlNode: function(oNode, sExpression){
		var D = this.D;

		if (sExpression)
			oNode = D.selectSingleNode(oNode, sExpression);

		if (!oNode)
			return;

		if( dojo.isIE ){
			var oFirstValueNode = oNode.firstChild;
		}else{ // I
			var oFirstValueNode = D.selectSingleNode(oNode, this.FIRST_VALUE_NODE_EXP);
		} // end - GS

		this.sType = oFirstValueNode ? oFirstValueNode.nodeName : 'text';

		switch ( this.sType ) {
		case 'datetimelist':
			var oResultPair = D.selectNodes(oFirstValueNode, './datetimepair');
			var oResultItem = D.selectNodes(oFirstValueNode, './datetime');
			var aoDateList = [];

			// range entries
			if (oResultPair) {
				for (var i = 0; i < D.snapshotLength(oResultPair); i++) {
					var aoDatePair = [];
					var oResultPairItem = D.selectNodes( D.snapshotItem(oResultPair, i) /*DOIDOI* oResultPair[i] */, './datetime');
					for (var j = 0; j < D.snapshotLength(oResultPairItem); j++) {
						var oCalendar = (new dwa.date.calendar).setISO8601String(D.snapshotItemText(oResultPairItem, j));
						aoDatePair[j] = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
					}
					aoDateList[aoDateList.length] = aoDatePair;
				}
			}

			// list entries
			if (oResultItem) {
				for (var i = 0; i < D.snapshotLength(oResultItem); i++) {
					var oCalendar = (new dwa.date.calendar).setISO8601String(D.snapshotItemText(oResultItem, i));
					aoDateList[aoDateList.length] = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
				}
			}

			this.vValue = aoDateList;
			return this;
		case 'datetime':
			var oCalendar = (new dwa.date.calendar).setISO8601String(D.nodeText(oFirstValueNode));
			this.vValue = !oCalendar.fDateOnly && !oCalendar.fTimeOnly ? oCalendar.getDate() : oCalendar;
			return this;
		case 'numberlist':
			var oResult = D.selectNodes(oFirstValueNode, this.VALID_TEXT_VALUES);
			var anValue = [];

			if (oResult) {
				for (var i = 0, n = 0; i <  D.snapshotLength(oResult); i++)
					anValue[n++] = D.snapshotItemText(oResult, i) - 0;
			}

			this.vValue = anValue;
			return this;
		case 'number':
			this.vValue = D.nodeText(oFirstValueNode) - 0;
			return this;
		case 'textlist':
			var oResult = D.selectNodes(oFirstValueNode, this.VALID_TEXT_VALUES);
			var asValue = [];

			if (oResult) {
				for (var i = 0, n = 0; i < D.snapshotLength(oResult); i++)
					asValue[n++] = D.snapshotItemText(oResult, i);
			}

			this.vValue = asValue;
			return this;
		}

		this.vValue = (oFirstValueNode ? D.nodeText(oFirstValueNode) : '');

		return this;
	},
	setNode: function(oNode, sExpression){
		if(!oNode)
			return null;
		if(oNode.nodeName)
			return this.setXmlNode(oNode, sExpression);
		else
			return this.setJsonNode(oNode, sExpression);
	}
});
