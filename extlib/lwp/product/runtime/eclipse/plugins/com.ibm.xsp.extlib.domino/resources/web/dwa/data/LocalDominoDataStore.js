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

dojo.provide("dwa.data.LocalDominoDataStore");
dojo.require("dwa.data.DominoDataStore");

dojo.declare("dwa.data.LocalDominoDataStore",
	dwa.data.DominoDataStore,
{
	//	summary:
	//		A data store that generates and returns data without I/O
	//		according to start, count, and sort parameters.
	//		This is only for test and demo.

	total: 5000,

	constructor: function(/*Object*/args){
		if(this.total > 10000){ this.total = 10000; }
		this._skip = Math.round(10000/this.total-0.5)*10 - 1;
		this.getUrl = undefined;
	},

	// bijection (one to one mapping generator)
	_translate: function(n, seed, size, inv){
		var p = seed+1;
		if(n == 0){ return n; }
		for(var i = 0; i < p; i++){
			if(n % p == i){
				var t = Math.round((size-1)/p-.5)*p; // max multiple
				var x = t+i>size-1?t-p+i:t+i; // adjust
				var u = Math.round(size/p/(p+1)*(i+1))*p; // middle
				var m = (u>=x)?u-p:u; // adjust
				return inv ? ((n-m)<=0?(n-m)+x+((i==0)?0:p)-i:n-m)
					: ((n+m)>x?(n+m)%x-((i==0)?0:p)+i:n+m);
			}
		}
	},

	 _getFullName: function(index){ // 0 <= index < 10000
		index *= this._skip;
		return firstnames[Math.round(index/100-.5)]+" "+surnames[index%100];
	},

	_toRecordIndex: function(index, sort, column){
		if(sort == 1){ // ascending
			return this._translate(index, column, this.total, true);
		}else if(sort == -1){ // descending
			return this._translate(this.total-index-1, column, this.total, true);
		}
		return index;
	},

	_generateEntry: function(index, pos){
		// 1:importance, 2:name, 3:subject, 4:date, 5:size, 6:attach, 7:flag
		var idx = [];
		for(var i = 1; i <= 7; i++){
			idx[i] = this._translate(index, i, this.total);
		}
		var time = ("000"+idx[4]%2400).slice(-4);
		var date = ("0"+Math.round(idx[4]/2400-.5)).slice(-2);
		var name = this._getFullName(Math.round(idx[2]/10-.5));
		var subject = idx[3];
		var size = idx[5]*777+7;
		var unid = "E2B97445E2FEBFFF4925760C001"+("0000"+pos).slice(-5);
		var flag = 0;
		var i = idx[7];
		if(i > this.total*0.999){
			flag = 182;
		}else if(i > this.total*0.990){
			flag = 180;
		}else if(i > this.total*0.900){
			flag = 179;
		}else if(i > this.total*0.550){
			flag = 178;
		}
		var unread = flag == 0 && index % 5 == 0;
		var attach = idx[6]<this.total*0.05?5:9999;
		var importance = idx[1]>this.total*0.990?204:0;

		return '\t{\n'
		+ '\t\t"@position": "'+pos+'",\n'
		+ '\t\t"@unid": "'+unid+'",\n'
		+ '\t\t"@noteid": "1106",\n'
		+ '\t\t"@siblings": "13",\n'
		+ (unread?'\t\t"@unread": "true",\n':'')
		+ '\t\t"entrydata": [\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "0",\n'
		+ '\t\t\t\t"@name": "$86",\n'
		+ '\t\t\t\t"number": { "0": "211" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "1",\n'
		+ '\t\t\t\t"@name": "$Importance",\n'
		+ '\t\t\t\t"number": { "0": "'+importance+'" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "2",\n'
		+ '\t\t\t\t"@name": "$93",\n'
		+ '\t\t\t\t"text": { "0": "'+name+'" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "3",\n'
		+ '\t\t\t\t"@name": "$73",\n'
		+ '\t\t\t\t"text": { "0": "Submission status changed - Accepted #'+subject+'" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "4",\n'
		+ '\t\t\t\t"@name": "$70",\n'
		+ '\t\t\t\t"datetime": { "0": "200908'+date+'T'+time+'11,49Z" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "5",\n'
		+ '\t\t\t\t"@name": "$106",\n'
		+ '\t\t\t\t"number": { "0": "'+size+'" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "6",\n'
		+ '\t\t\t\t"@name": "$97",\n'
		+ '\t\t\t\t"number": { "0": "'+attach+'" }\n'
		+ '\t\t\t},\n'
		+ '\t\t\t{\n'
		+ '\t\t\t\t"@columnnumber": "7",\n'
		+ '\t\t\t\t"@name": "$109",\n'
		+ '\t\t\t\t"number": { "0": "'+flag+'" }\n'
		+ '\t\t\t}\n'
		+ '\t\t]\n'
		+ '\t}\n'
	},

	_fetchItems: function(request){
		//console.log("_fetchItems: start="+request.start+", count="+request.count);
		var json = '{entries:{\n'
			+ '\t"@timestamp": "20090921T053923,58Z",\n'
			+ '\t"@toplevelentries": "'+this.total+'",\n'
			+ '\t"viewentry": [\n';
		var sort = 0, column;
		if(request.request.oQuery.resortascending){
			sort = 1;
			column = request.request.oQuery.resortascending;
		}else if(request.request.oQuery.resortdescending){
			sort = -1;
			column = request.request.oQuery.resortdescending;
		}
		for(var i = request.start; i < request.start+request.count && i < this.total; i++){
			if(i != request.start){ json += ",\n"; }
			var index = this._toRecordIndex(i, sort, column);
			json += this._generateEntry(index, request.request.bIsReversed ? request.start*2+request.count-i : i+1);
		}
		json += "	]}}\n";
		var _this = this;
		setTimeout(function(){
			_this.items = _this.format(dojo.fromJson(json));
			_this.fetchHandler(_this.items, request, _this._numRows);
		}, 0);
	}
});


var firstnames = [
"Adam","Ahmed","Alex","Ali","Amanda","Amy","Andrea","Andrew",
"Andy","Angela","Anna","Anne","Anthony","Antonio","Ashley","Barbara",
"Ben","Bill","Bob","Brian","Carlos","Carol","Chris","Christian",
"Christine","Cindy","Claudia","Dan","Daniel","Dave","David","Debbie",
"Elizabeth","Eric","Gary","George","Heather","Jack","James","Jason",
"Jean","Jeff","Jennifer","Jessica","Jim","Joe","John","Jonathan",
"Jose","Juan","Julie","Karen","Kelly","Kevin","Kim","Laura",
"Linda","Lisa","Luis","Marco","Maria","Marie","Mark","Martin",
"Mary","Matt","Matthew","Melissa","Michael","Michelle","Mike","Mohamed",
"Monica","Nancy","Nick","Nicole","Patricia","Patrick","Paul","Peter",
"Rachel","Richard","Robert","Ryan","Sam","Sandra","Sara","Sarah",
"Scott","Sharon","Stephanie","Stephen","Steve","Steven","Susan","Thomas",
"Tim","Tom","Tony","William"
];
var surnames = [
"Bailey","Baker","Barnes","Bell","Bennett","Brams","Bray","Brent",
"Brooks","Brown","Bryant","Bush","Butler","Campbell","Carter","Clark",
"Coleman","Collins","Cook","Cooper","Cox","Davis","Diaz","Edwards",
"Evans","Flores","Foster","Garcia","Gonzales","Gonzalez","Gray","Green",
"Griffin","Hall","Harris","Hayes","Henderson","Hernandez","Hill","Howard",
"Hughes","Jackson","James","Jenkins","Johnson","Jones","Kelly","King",
"Lee","Lewis","Long","Lopez","Martin","Martinez","Miller","Mitchell",
"Moore","Morgan","Morris","Murphy","Nelson","Parker","Patterson","Perez",
"Perry","Peterson","Phillips","Powell","Price","Ramirez","Reed","Richardson",
"Rivera","Roberts","Robinson","Rodriguez","Rogers","Ross","Russell","Sanchez",
"Sanders","Scott","Simmons","Smith","Stewart","Taylor","Thomas","Thompson",
"Torres","Turner","Walker","Ward","Washington","Watson","White","Williams",
"Wilson","Wood","Wright","Young"
];
