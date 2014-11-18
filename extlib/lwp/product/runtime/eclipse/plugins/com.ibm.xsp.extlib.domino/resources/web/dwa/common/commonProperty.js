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

dojo.provide("dwa.common.commonProperty");


dojo.declare(
	"dwa.common.commonProperty",
	null,
{
	constructor: function(sName){
		dwa.common.commonProperty.oProperties[sName] = this;
		this.sName = sName;
		this.aoObservers = [];
		this.nSeq = 0;
		if(this.init)
			this.init();
	},
	setValue: function(vValue, fSync){
		// Create a snapshot of this object.
		// This is important, since this object can be overwritten while this function calls observer functions.
		var oCaller = arguments.callee.caller;
		var oProperty = new dwa.common.commonPropertySnapshot(this.sName, vValue, this.vValue, oCaller, ++this.nSeq);
	
		// Then update the property value
		this.vValue = vValue;
	
		// Log the property update
		if (dwa.common.commonProperty.prototype.fLogObserver) {
			var asMatch = oCaller ? oCaller.toString().match(/function *(\w*)/) : void 0;
			var sName = asMatch && asMatch[1] ? asMatch[1] : '';
			var sLog = 'Setting ' + vValue + ' to ' + this.sName + (sName ? (' from ' + sName) : '') + ' (Sequence: ' + this.nSeq + ')';
			console.debug(sLog);
		}
	
		// Call observer functions
		if (!fSync)
			setTimeout(dojo.hitch(oProperty, "callObservers"), 0);
		else
			oProperty.callObservers();
	
		// Finish - Return the new value specified (Not the latest property value)
		return oProperty.vValue;
	},
	attach: function(oObserver){
		return this.aoObservers[this.aoObservers.length] = oObserver;
	},
	detach: function(oObserver){
		for (var i = this.aoObservers.length - 1; i >= 0; i--) {
			if (this.aoObservers[i] == oObserver)
				this.aoObservers.splice(i, 1);
		}
	}
});

dwa.common.commonProperty.oProperties = {};
dwa.common.commonProperty.get = function(sName){
	if (dwa.common.commonProperty.oProperties[sName])
		return dwa.common.commonProperty.oProperties[sName];
	return new dwa.common.commonProperty(sName);
};

dojo.declare(
	"dwa.common.commonPropertySnapshot",
	null,
{
	constructor: function(sName, vValue, vPrevValue, oCaller, nSeq){
		this.sName = sName;
		this.vValue = vValue;
		this.vPrevValue = vPrevValue;
		this.oCaller = oCaller;
		this.nSeq = nSeq;
	},
	isLatest: function(){
		return this.nSeq >= dwa.common.commonProperty.get(this.sName).nSeq;
	},
	callObservers: function(){
		for (var aoObservers = dwa.common.commonProperty.get(this.sName).aoObservers, i = 0; i < aoObservers.length; i++) {
			if (dwa.common.commonProperty.prototype.fLogObserver) {
				var asMatch = aoObservers[i].constructor && aoObservers[i].constructor.toString().match(/function *(\w*)/);
				var sName = aoObservers[i].sClass ? aoObservers[i].sClass : (asMatch && asMatch[1] || '');
				var nSeq = dwa.common.commonProperty.get(this.sName).nSeq;
				var sLog = 'Calling observer' + (sName ? (' ' + sName) : '') + ' for ' + this.sName
				 + ' (Value: ' + this.vValue + ', Sequence: ' + this.nSeq + '/' + nSeq + ')';
				console.debug(sLog);
			}
			aoObservers[i].observe && aoObservers[i].observe(this);
		}
	}
});
