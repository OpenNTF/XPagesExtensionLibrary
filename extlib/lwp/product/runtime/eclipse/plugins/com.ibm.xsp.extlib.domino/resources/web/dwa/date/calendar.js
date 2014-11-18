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

dojo.provide("dwa.date.calendar");

dojo.require("dwa.common.utils");

dojo.declare(
	"dwa.date.calendar",
	null,
{
	constructor: function(nYear, nMonth, nDate, nHours, nMinutes, nSeconds, nMilliseconds, oZoneInfo, fDateOnly, fTimeOnly){
		this.nYear = nYear ? nYear : 0;
		this.nEra = this.nYear < 0 ? 0 : 1;
		this.nMonth = nMonth ? nMonth : 0;
		this.nDate = nDate ? nDate : 0;
		this.nHours = nHours ? nHours : 0;
		this.nMinutes = nMinutes ? nMinutes : 0;
		this.nSeconds = nSeconds ? nSeconds : 0;
		this.nMilliseconds = nMilliseconds? nMilliseconds : 0;
		this.oZoneInfo = oZoneInfo;
		this.fDateOnly = fDateOnly;
		this.fTimeOnly = fTimeOnly;
		this.toString = this.getISO8601String;
	},
	clone: function(){
		var oCalendar
		 = new dwa.date.calendar(this.nYear, this.nMonth, this.nDate, this.nHours, this.nMinutes, this.nSeconds, this.nMilliseconds);
		oCalendar.nEra = this.nEra;
		oCalendar.nDay = this.nDay;
		oCalendar.oZoneInfo = this.oZoneInfo;
		oCalendar.fDateOnly = this.fDateOnly;
		oCalendar.fTimeOnly = this.fTimeOnly;
		return oCalendar;
	},
	isInvalid: function(){
		return isNaN(this.nYear + this.nMonth + this.nDate + this.nHours + this.nMinutes + this.nSeconds + this.nMilliseconds);
	},
	equals: function(oCalendar){
		var fEqual = true;
	
		if (!this.fTimeOnly) {
			fEqual = this.nYear != oCalendar.nYear ? false : fEqual;
			fEqual = this.nMonth != oCalendar.nMonth ? false : fEqual;
			fEqual = this.nDate != oCalendar.nDate ? false : fEqual;
		}
	
		if (!this.fDateOnly) {
			fEqual = this.nHours != oCalendar.nHours ? false : fEqual;
			fEqual = this.nMinutes != oCalendar.nMinutes ? false : fEqual;
			fEqual = this.nSeconds != oCalendar.nSeconds ? false : fEqual;
			fEqual = this.nMilliseconds != oCalendar.nMilliseconds ? false : fEqual;
		}
	
		return this.oZoneInfo && !this.oZoneInfo.equals(oCalendar.oZoneInfo) || !this.oZoneInfo && oCalendar.oZoneInfo ? false : fEqual;
	},
	sync: function(){
		this.setUTCDate(this.getUTCDate());
	},
	set: function(oCalendar){
		if (!oCalendar.fTimeOnly) {
			this.nYear = oCalendar.nYear;
			this.nEra = oCalendar.nEra;
			this.nMonth = oCalendar.nMonth;
			this.nDate = oCalendar.nDate;
			this.nDay = oCalendar.nDay;
		}
	
		if (!oCalendar.fDateOnly) {
			this.nHours = oCalendar.nHours;
			this.nMinutes = oCalendar.nMinutes;
			this.nSeconds = oCalendar.nSeconds;
			this.nMilliseconds = oCalendar.nMilliseconds;
		}
	
		this.oZoneInfo = oCalendar.oZoneInfo;
		this.fDateOnly = this.fDateOnly && oCalendar.fDateOnly;
		this.fTimeOnly = this.fTimeOnly && oCalendar.fTimeOnly;
		return this;
	},
	adjustDays: function(nYears, nMonths, nDays){
		// We apply the days difference first. This is important, as the month adjustment's result varies by the actual date.
		if (nDays) {
			var oDate = this.getUTCDate();
			oDate.setTime(oDate.getTime() + nDays * 86400000);
			this.setUTCDate(oDate);
		}
	
		// Apply the months and years differences
		if (nYears || nMonths) {
			this.nMonth += nMonths;
	
			var nAdjYears = this.nMonth < 0 ?
			 (-(Math.floor(-this.nMonth / this.nMonthsInYear)) - 1) : Math.floor(this.nMonth / this.nMonthsInYear);
			this.nMonth -= nAdjYears * this.nMonthsInYear;
			nYears += nAdjYears;
			this.nYear += nYears;
	
			var nMaxDay = this.getDaysInMonth();
			this.nDate = this.nDate <= nMaxDay ? this.nDate : nMaxDay;
		}
	
		return this;
	},
	compare: function(oSrc){
		var	nDiff = 0;
	
		for (var i = (!this.fTimeOnly && !oSrc.fTimeOnly ? 0 : 3); nDiff == 0 && i < (!this.fDateOnly && !oSrc.fDateOnly ? 6 : 3); i++) {
			switch (i) {
			case 0: nDiff = this.nYear - oSrc.nYear; break;
			case 1: nDiff = this.nMonth - oSrc.nMonth; break;
			case 2: nDiff = this.nDate - oSrc.nDate; break;
			case 3: nDiff = this.nHours - oSrc.nHours; break;
			case 4: nDiff = this.nMinutes - oSrc.nMinutes; break;
			case 5: nDiff = this.nSeconds - oSrc.nSeconds; break;
			}
		}
	
		return dwa.common.utils.sign(nDiff);
	},
	isLeapYear: function(){
		return this.nYear >= 0 ? (((0 == this.nYear % 4) && (0 != (this.nYear % 100))) || (0 == this.nYear % 400)) : (this.nYear % 4 == 0);
	},
	getDaysInMonth: function(){
		var anDaysInMonth = [31, void 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
		return anDaysInMonth[this.nMonth] ? anDaysInMonth[this.nMonth] : this.isLeapYear() ? 29 : 28;
	},
	getDate: function(){
		if ((!this.oZoneInfo && dwa.date.zoneInfo.prototype.fOSCurrentIsUsed) || (this.oZoneInfo && this.oZoneInfo.isOSCurrent()))
			return this.getOSDate();
	
		// For the timezone offset calculation, the actual timezone information is needed
		var oZoneInfo = !this.oZoneInfo ? dwa.date.zoneInfo.prototype.getCurrent() : this.oZoneInfo;
		var nOffset = oZoneInfo.nOffset;
		
		if (oZoneInfo.oDSTBoundaries) {
			var nDSTBegin = oZoneInfo.oDSTBoundaries.oBegin.compareWithDay(this);
			var nDSTEnd = oZoneInfo.oDSTBoundaries.oEnd.compareWithDay(this);
	
			if (oZoneInfo.oDSTBoundaries.oBegin.nMonth < oZoneInfo.oDSTBoundaries.oEnd.nMonth ?
			 (nDSTBegin > 0 && nDSTEnd < 0) : (nDSTBegin > 0 || nDSTEnd < 0)) {
				nOffset -= oZoneInfo.nDSTBias;
			} else if (nDSTBegin == 0 && this.nHours >= 2) {
				nOffset -= oZoneInfo.nDSTBias;
				// This hour technically does not exist
				this.nHours = this.nHours >= 3 ? this.nHours : 3;
			} else if (nDSTEnd == 0 && this.nHours < 1) {
				nOffset -= oZoneInfo.nDSTBias;
			}
		}
	
		var oDate = this.getUTCDate();
		oDate.setTime(oDate.getTime() + nOffset * 60000);
		return oDate;
	},
	getOSDate: function(){
		return new Date(this.nYear, this.nMonth, this.nDate, this.nHours, this.nMinutes, this.nSeconds, this.nMilliseconds);
	},
	getUTCDate: function(){
		return new Date(Date.UTC(this.nYear, this.nMonth, this.nDate, this.nHours, this.nMinutes, this.nSeconds, this.nMilliseconds));
	},
	setDate: function(oDate, oZoneInfo){
		this.oZoneInfo = oZoneInfo;
		return oZoneInfo || !dwa.date.zoneInfo.prototype.fOSCurrentIsUsed ?
		 this.setUTCDate((oZoneInfo ? oZoneInfo : dwa.date.zoneInfo.prototype.getCurrent()).getUTCEquiv(oDate)) :
		 this.setOSDate(oDate);
	},
	setOSDate: function(oDate){
		this.nYear = oDate.getFullYear();
		this.nEra = this.nYear < 0 ? 0 : 1;
		this.nMonth = oDate.getMonth();
		this.nDate = oDate.getDate();
		this.nDay = oDate.getDay();
		this.nHours = oDate.getHours();
		this.nMinutes = oDate.getMinutes();
		this.nSeconds = oDate.getSeconds();
		this.nMilliseconds = oDate.getMilliseconds();
		return this;
	},
	setUTCDate: function(oDate){
		this.nYear = oDate.getUTCFullYear();
		this.nEra = this.nYear < 0 ? 0 : 1;
		this.nMonth = oDate.getUTCMonth();
		this.nDate = oDate.getUTCDate();
		this.nDay = oDate.getUTCDay();
		this.nHours = oDate.getUTCHours();
		this.nMinutes = oDate.getUTCMinutes();
		this.nSeconds = oDate.getUTCSeconds();
		this.nMilliseconds = oDate.getUTCMilliseconds();
		return this;
	},
	getISO8601String: function(){
		if (this.isInvalid())
			return '';
	
		var asDate = [];
	
		if (!this.fTimeOnly)
			asDate[asDate.length]
			 = (this.nYear < 10 ? '0' : '') + (this.nYear < 100 ? '0' : '') + (this.nYear < 1000 ? '0' : '') + this.nYear
			 + (this.nMonth < 9 ? '0' : '') + (this.nMonth + 1)
			 + (this.nDate < 10 ? '0': '') + this.nDate;
	
		if (!this.fDateOnly) {
			var nMilliseconds = Math.floor(this.nMilliseconds / 10);
			asDate[asDate.length] = 'T'
			 + (this.nHours < 10 ? '0': '') + this.nHours
			 + (this.nMinutes < 10 ? '0' : '') + this.nMinutes
			 + (this.nSeconds < 10 ? '0': '') + this.nSeconds
			 + ','
			 + (nMilliseconds < 10 ? '0' : '') + nMilliseconds;
		}
	
		var oZoneInfo = this.oZoneInfo ? this.oZoneInfo : dwa.date.zoneInfo.prototype.getCurrent();
		var fAddZone = !this.fDateOnly && !this.fTimeOnly;
		return asDate.join('') + (fAddZone ? (!oZoneInfo.equals(oZoneInfo.oUTC) ? ('$' + oZoneInfo.getCanonical()) : 'Z') : '');
	},
	getISO8601BasicFormatString: function(){
		if (this.isInvalid())
			return '';
	
		var asDate = [];
	
		if (!this.fTimeOnly)
			asDate[asDate.length]
			 = (this.nYear < 10 ? '0' : '') + (this.nYear < 100 ? '0' : '') + (this.nYear < 1000 ? '0' : '') + this.nYear
			 + (this.nMonth < 9 ? '0' : '') + (this.nMonth + 1)
			 + (this.nDate < 10 ? '0': '') + this.nDate;
	
		if (!this.fDateOnly) {
			var nMilliseconds = Math.floor(this.nMilliseconds / 10);
			asDate[asDate.length] = 'T'
			 + (this.nHours < 10 ? '0': '') + this.nHours
			 + (this.nMinutes < 10 ? '0' : '') + this.nMinutes
			 + (this.nSeconds < 10 ? '0': '') + this.nSeconds;
		}
	
		var oZoneInfo = this.oZoneInfo ? this.oZoneInfo : dwa.date.zoneInfo.prototype.getCurrent();
		var fAddZone = !this.fDateOnly && !this.fTimeOnly;
		return asDate.join('') + (fAddZone ? (!oZoneInfo.equals(oZoneInfo.oUTC) ? '' : 'Z') : '');
	},
	setISO8601String: function(sISO8601String){
		if (!sISO8601String)
			return this.setOSDate(new Date(Date.UTC(NaN)));
	
		this.fDateOnly = sISO8601String.indexOf('T') == -1;
	
		if (this.fTimeOnly = sISO8601String.charAt(0) == 'T')
			sISO8601String = '19000101' + sISO8601String;
	
		var nIndex = sISO8601String.indexOf('$');
	
		if (nIndex != -1) {
			this.oZoneInfo = new dwa.date.zoneInfo(sISO8601String.substr(nIndex + 1));
			sISO8601String = sISO8601String.substr(0, nIndex);
		} else {
			this.oZoneInfo = void 0;
		}
	
		var asTokens = this.oRegExpR5DSTSpecifier.exec(sISO8601String);
		var sDSTSpecifier = '';
	
		if (asTokens) {
			sISO8601String = asTokens[1];
			sDSTSpecifier = asTokens[3];
		}
	
		if (!(asTokens = this.oRegExpISO8601.exec(sISO8601String))) {
			var e = new Error('Bad ISO8601 string: ' + sISO8601String);
			e.sBadArg = sISO8601String;
			e.sBadArgFor = 'ISO8601String';
			throw e;
		}
	
		this.nYear = asTokens[1] ? (asTokens[1] - 0) : 1900;
		this.nEra = this.nYear < 0 ? 0 : 1;
		this.nMonth = asTokens[2] ? (asTokens[2] - 1) : 0;
		this.nDate = asTokens[3] ? (asTokens[3] - 0) : 1;
		this.nHours = asTokens[5] ? (asTokens[5] - 0) : 0;
		this.nMinutes = asTokens[7] ? (asTokens[7] - 0) : 0;
		this.nSeconds = asTokens[9] ? (asTokens[9] - 0) : 0;
		this.nMilliseconds = asTokens[11] ? ((asTokens[11] - 0) * Math.pow(10, 3 - asTokens[11].length)) : 0;
		this.oZoneInfo = asTokens[13] == 'Z' ? dwa.date.zoneInfo.prototype.oUTC : this.oZoneInfo;
	
		if (asTokens[14] && asTokens[15]) {
			var nOffsetHours = asTokens[15] ? asTokens[15] - 0 : 0;
			var nOffsetMinutes = asTokens[17] ? asTokens[17] - 0 : 0;
			var nOffsetSeconds = asTokens[19] ? asTokens[19] - 0 : 0;
	
			var nOffset = (asTokens[14] == '+' ? -1 : 1) * (nOffsetHours * 3600 + nOffsetMinutes * 60 + nOffsetSeconds);
			nOffset -= sDSTSpecifier == 1 ? 3600 : 0;
			this.oZoneInfo = new dwa.date.zoneInfo(Math.floor(nOffset / 60), 0);
		}
		
		return this;
	},
	getDaysSinceEpoch: function(){
		return Math.floor(this.getUTCDate() / 86400000);
	},
	setDaysSinceEpoch: function(nDaysSinceEpoch){
		var oDate = this.getUTCDate();
		var nTime = oDate.getTime() % 86400000;
		oDate.setTime(nDaysSinceEpoch * 86400000 + nTime);
		return this.setUTCDate(oDate);
	},
	nEpoch: 719163,
	nMonthsInYear: 12,
	nDaysInWeek: 7,
	oRegExpISO8601: new RegExp('^(\\d{4})[\\-\\/\\.]?(\\d{1,2})[\\-\\/\\.]?(\\d{1,2})'
	 + '(T?(\\d{1,2})([\\-:\\.]?(\\d{1,2})([\\-:\\.]?(\\d{1,2})'
	 + '(\\,(\\d{1,3}))?)?)?)?'
	 + '((Z)|([\\+\\-])(\\d{1,2})([\\-:\\.]?(\\d{1,2})'
	 + '([\\-:\\.]?(\\d{1,2}))?)?)?$'),
	oRegExpR5DSTSpecifier: new RegExp('^(.*)(D([01N]))$')
});


dojo.declare(
	"dwa.date.DSTBoundary",
	null,
{
	constructor: function(nMonth, nWeek, nDay){
		this.nMonth = nMonth;
		this.nWeek = nWeek;
		this.nDay = nDay;
	},
	clone: function(){
		var oDSTBoundary = new dwa.date.DSTBoundary;
		oDSTBoundary.nMonth = this.nMonth;
		oDSTBoundary.nWeek = this.nWeek;
		oDSTBoundary.nDay = this.nDay;
		return oDSTBoundary;
	},
	equals: function(oDSTBoundary){
		return oDSTBoundary.nMonth == this.nMonth && oDSTBoundary.nWeek == this.nWeek && oDSTBoundary.nDay == this.nDay;
	},
	getBoundaryDay: function(oCalendar){
		// the first or last day of the month/year
		// year is provided by oCalendar, month is this DST boundary's one
		oCalendar.nMonth = this.nMonth - 1;
		oCalendar.nDate = this.nWeek >= 0 ? 1 : oCalendar.getDaysInMonth();
		oCalendar.nHours = 12;
		oCalendar.nMinutes = oCalendar.nSeconds = oCalendar.nMilliseconds = 0;
		var oDate = oCalendar.getUTCDate();
	
		// perform day shift until the day of the week matches this DST boundary's one
		while (oDate.getUTCDay() != this.nDay - 1)
			oDate.setTime(oDate.getTime() + dwa.common.utils.sign(this.nWeek) * 86400000);
	
		// perform week shift based on this DST boundary's #week
		oDate.setTime(oDate.getTime() + (this.nWeek - dwa.common.utils.sign(this.nWeek)) * 86400000 * 7);
	
		// set the result
		oCalendar.nMonth = oDate.getUTCMonth();
		oCalendar.nDate = oDate.getUTCDate();
	
		return oCalendar;
	},
	compareWithDay: function(oCalendar){
		// check for nMonths out of range
		if (oCalendar.nMonth < this.nMonth - 1)
			return -1;
		if (oCalendar.nMonth > this.nMonth - 1)
			return 1;
	
		// compute the rollover day either from the end of the oCalendar.nMonth
		// or from the beginning of the oCalendar.nMonth.
		var nDaysInMonth = oCalendar.getDaysInMonth();
	
		// evaluate oCalendar.nDay
		oCalendar = (new dwa.date.calendar).setUTCDate(oCalendar.getUTCDate());
	
		var nDaysUntilBoundaryDay = (this.nDay - 1) - oCalendar.nDay;
		if (nDaysUntilBoundaryDay < 0)
			nDaysUntilBoundaryDay += 7;
	
		var nNextBoundaryDay = oCalendar.nDate + nDaysUntilBoundaryDay;
		if (nNextBoundaryDay > nDaysInMonth)
			nNextBoundaryDay -= 7;
	
		var nBoundaryDay;
	
		if (this.nWeek < 0) {
			var nLastBoundaryDayOfMonth = nNextBoundaryDay + (dwa.common.utils.floorAbs((nDaysInMonth - nNextBoundaryDay) / 7) * 7);
			nBoundaryDay = nLastBoundaryDayOfMonth + (7 * (this.nWeek + 1));
		}else{
			var nFirstBoundaryDayOfMonth = ((nNextBoundaryDay - 1) % 7) + 1;
			nBoundaryDay = (7 * (this.nWeek - 1)) + nFirstBoundaryDayOfMonth;
		}
	
		// compare against the computed rollover day
		return dwa.common.utils.sign(oCalendar.nDate - nBoundaryDay);
	}
});


dojo.declare(
	"dwa.date.DSTBoundaries",
	null,
{
	constructor: function(oBegin, oEnd){
		if (arguments.length == 1) {
			this.setRuleStr(arguments[0]);
		} else {
			this.oBegin = oBegin;
			this.oEnd = oEnd;
		}
	},
	clone: function(){
		var oDSTBoundaries = new dwa.date.DSTBoundaries;
		oDSTBoundaries.oBegin = this.oBegin.clone();
		oDSTBoundaries.oEnd = this.oEnd.clone();
		return oDSTBoundaries;
	},
	equals: function(oDSTBoundaries){
		return oDSTBoundaries.oBegin.equals(this.oBegin) && oDSTBoundaries.oEnd.equals(this.oEnd);
	},
	getRuleStr: function(){
		return this.oBegin.nMonth + ' ' + this.oBegin.nWeek + ' ' + this.oBegin.nDay
		 + ' ' + this.oEnd.nMonth + ' ' + this.oEnd.nWeek + ' ' + this.oEnd.nDay;
	},
	setRuleStr: function(sRuleStr){
		var anDSTRule = [];
	
		for (var asDSTRule = sRuleStr.split(' '), i = 0; i < 6; i++)
			anDSTRule[i] = parseInt(asDSTRule[i]);
	
		this.oBegin = new dwa.date.DSTBoundary(anDSTRule[0], anDSTRule[1], anDSTRule[2]);
		this.oEnd = new dwa.date.DSTBoundary(anDSTRule[3], anDSTRule[4], anDSTRule[5]);
		return this;
	}
});


dojo.declare(
	"dwa.date.zoneInfo",
	null,
{
	constructor: function(nOffset, nDSTBias, oDSTBoundaries, sZoneKey, sZoneName){
		if (arguments.length <= 1)
			this.setCanonical(arguments[0]);
		else
			this.setRule(nOffset, nDSTBias, oDSTBoundaries, sZoneKey, sZoneName);
	},
	clone: function(){
		var oZoneInfo = new dwa.date.zoneInfo;
		oZoneInfo.nOffset = this.nOffset;
		oZoneInfo.nDSTBias = this.nDSTBias;
		oZoneInfo.oDSTBoundaries = this.oDSTBoundaries ? this.oDSTBoundaries.clone() : void 0;
		oZoneInfo.sZoneKey = this.sZoneKey;
		oZoneInfo.sZoneName = this.sZoneName;
		return oZoneInfo;
	},
	equals: function(oZoneInfo){
		return oZoneInfo && this.nOffset == oZoneInfo.nOffset && this.nDSTBias == oZoneInfo.nDSTBias
		 && (this.nDSTBias == 0 || this.oDSTBoundaries.equals(oZoneInfo.oDSTBoundaries));
	},
	getUTCEquiv: function(oDate){
		if (this.equals(this.oUTC))
			return oDate;
		return new Date(oDate.getTime() - this.getOffset(oDate) * 60000);
	},
	setRule: function(nOffset, nDSTBias, oDSTBoundaries, sZoneKey, sZoneName){
		var aoDefinedZoneInfo = dwa.date.zoneInfo.prototype.getDefined();

		this.nOffset = nOffset;
		this.nDSTBias = nDSTBias;
		this.oDSTBoundaries = oDSTBoundaries;

		for (var s in aoDefinedZoneInfo) {
			if (aoDefinedZoneInfo[s] && aoDefinedZoneInfo[s].equals(this)) {
				this.sZoneKey = aoDefinedZoneInfo[s].sZoneKey;
				this.sZoneName = aoDefinedZoneInfo[s].sZoneName;
				return this;
			}
		}

		// the rule is not found on the table. in this case, we add the rule to the table
		if (!dwa.date.zoneInfo.prototype.nUnknownZoneKeyCount)
			dwa.date.zoneInfo.prototype.nUnknownZoneKeyCount = 1;

		// ignore sZoneKey provided - risk with conflicting zone key
		this.sZoneKey = 'Unknown' + dwa.date.zoneInfo.prototype.nUnknownZoneKeyCount;
		this.sZoneName = sZoneName ? sZoneName : sZoneKey ? sZoneKey :
		 (this.sUnknownSymbol + dwa.date.zoneInfo.prototype.nUnknownZoneKeyCount);

		var nZoneID = dwa.common.utils.floorAbs(this.nOffset / MINS_IN_HOUR) + (this.nOffset % MINS_IN_HOUR) * 100;
		var sDSTRule = this.oDSTBoundaries ? this.oDSTBoundaries.getRuleStr() : void 0;
		var nOffsetHour = Math.floor(Math.abs(this.nOffset / MINS_IN_HOUR));
		var nOffsetMinutes = Math.abs(this.nOffset) % MINS_IN_HOUR;
		var sOffsetHour = (nOffsetHour < 10 ? '0' : '') + nOffsetHour;
		var sOffsetMinutes = (nOffsetMinutes < 10 ? '0' : '') + nOffsetMinutes;
		var sUIString = dwa.common.utils.formatMessage(this.sDisplayFormat,
		 dwa.common.utils.sign(this.nOffset) < 0 ? '+' : '-', sOffsetHour, sOffsetMinutes, this.sZoneName);

		dwa.date.zoneInfo.prototype.nUnknownZoneKeyCount++;

		this.aavTable[this.aavTable.length] = [this.sZoneKey, this.sZoneName, sUIString, nZoneID, !!this.nDSTBias, sDSTRule];

		var aoDefinedZoneInfo = dwa.date.zoneInfo.prototype.getDefined();
		aoDefinedZoneInfo[this.sZoneKey] = this.clone();

		return this;
	},
	setCanonical: function(sZoneCanonical){
		if (!sZoneCanonical)
			return;
	
		var asElements = sZoneCanonical.split('$');
	
		for (var i = 0; i < asElements.length; i++) {
			var asPair = asElements[i].split('=');
			switch (asPair[0]) {
			case 'Z':
				var nZoneID = parseInt(asPair[1]);
				this.nOffset = (nZoneID % 100) * 60 + dwa.common.utils.floorAbs(nZoneID / 100);
				break;
			case 'DO':
				this.nDSTBias = parseInt(asPair[1]) * 60;
				break;
			case 'DL':
				this.oDSTBoundaries = new dwa.date.DSTBoundaries(asPair[1]);
				break;
			case 'ZN':
				this.sZoneKey = asPair[1];
				break;
			}
		}
	
		// the timezone information should primary be identified by the ZN parameter.
		// should be identified by the rule if not found.
		try {
			this.setZoneKey(this.sZoneKey);
		} catch (e) {
			this.setRule(this.nOffset, this.nDSTBias, this.oDSTBoundaries, this.sZoneKey, this.sZoneName);
		}
	
		return this;
	},
	setZoneKey: function(sZoneKey){
		var oZoneInfo = dwa.date.zoneInfo.prototype.getDefined()[sZoneKey];
	
		if (!oZoneInfo) {
			var e = new Error('Bad timezone key: ' + sZoneKey);
			e.sBadArg = sZoneKey;
			e.sBadArgFor = 'Timezone';
			throw e;
		}
	
		this.nOffset = oZoneInfo.nOffset;
		this.nDSTBias = oZoneInfo.nDSTBias;
		this.oDSTBoundaries = oZoneInfo.oDSTBoundaries;
		this.sZoneKey = oZoneInfo.sZoneKey;
		this.sZoneName = oZoneInfo.sZoneName;
	
		return this;
	},
	getCanonical: function(){
		var sResult = '';
		sResult += 'Z=' + (dwa.common.utils.floorAbs(this.nOffset / 60) + (this.nOffset % 60) * 100);
		sResult += '$DO=' + dwa.common.utils.floorAbs(this.nDSTBias / 60);
		if (this.oDSTBoundaries)
			sResult += '$DL=' + this.oDSTBoundaries.getRuleStr();
		sResult += '$ZN=' + this.sZoneKey;
		return sResult;
	},
	getCurrent: function(fObtainDefault){
		// if a ZoneInfo is specified by user preference, set and return it
		if (this.sCurrentZoneInfo) {
			dwa.date.zoneInfo.prototype.oCurrentZoneInfo = new dwa.date.zoneInfo(this.sCurrentZoneInfo);
			if (dwa.date.zoneInfo.prototype.oCurrentZoneInfo.equals(this.getOSCurrent()))
				dwa.date.zoneInfo.prototype.fOSCurrentIsUsed = true;
			return dwa.date.zoneInfo.prototype.oCurrentZoneInfo;
		}
	
		dwa.date.zoneInfo.prototype.fOSCurrentIsUsed = true;
		return this.getOSCurrent(fObtainDefault);
	},
	getOSCurrent: function(fObtainDefault){
		if (typeof(fObtainDefault) == 'undefined')
			fObtainDefault = true;
	
		// if we already have the current ZoneInfo, return it
		if (dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo)
			return dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo.clone();
	
		// obtain the define ZoneInfo list
		var aoDefinedZoneInfo = dwa.date.zoneInfo.prototype.getDefined();
	
		// evaluate the current ZoneInfo
		for (var s in aoDefinedZoneInfo) {
			if (aoDefinedZoneInfo[s] && aoDefinedZoneInfo[s].isOSCurrent())
				return (dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo = aoDefinedZoneInfo[s]).clone();
		}
	
		// the current ZoneInfo was not found on the timezone table.
		// in this case, we look up the timezone table for the matching DST rule, and update the timezone table.
		var anOffsets = dwa.date.zoneInfo.prototype.getLocalOffsets();
	
		for (var s in aoDefinedZoneInfo) {
			if (aoDefinedZoneInfo[s]) {
				var oZoneInfo = new dwa.date.zoneInfo;
				oZoneInfo.nOffset = anOffsets[0];
				oZoneInfo.nDSTBias = aoDefinedZoneInfo[s].nDSTBias;
				oZoneInfo.oDSTBoundaries = aoDefinedZoneInfo[s].oDSTBoundaries;
	
				if (oZoneInfo.isOSCurrent()) {
					oZoneInfo.setRule(oZoneInfo.nOffset, oZoneInfo.nDSTBias, oZoneInfo.oDSTBoundaries);
					return dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo = oZoneInfo;
				}
			}
		}
	
		if (fObtainDefault)
			return new dwa.date.zoneInfo(0, 0, void 0, '');
	},
	getDefined: function(){
		// if we already have the defined ZoneInfo list, return it
		if (dwa.date.zoneInfo.prototype.aoDefinedZoneInfo)
			return dwa.date.zoneInfo.prototype.aoDefinedZoneInfo;
	
		dwa.date.zoneInfo.prototype.aoDefinedZoneInfo = {};
	
		// Customization - set additional timezones
		if( typeof( Custom_TimeZones_Lite ) == 'function' )
		{
			var aavCustomTable = [];
			Custom_TimeZones_Lite( aavCustomTable );
			for( var i = 0; i < aavCustomTable.length; i++ )
				aavCustomTable[ i ][ 6 ] = true;
			this.aavTable = aavCustomTable.concat( this.aavTable );
		}
	
		for (var aavTable = this.aavTable, i = 0; i < aavTable.length; i++){
			var oZoneInfo = new dwa.date.zoneInfo;
			oZoneInfo.nOffset = (aavTable[i][3] % 100) * 60 + dwa.common.utils.floorAbs(aavTable[i][3] / 100);
			oZoneInfo.nDSTBias = (aavTable[i][4] - 0) * 60;
			oZoneInfo.oDSTBoundaries = aavTable[i][5] ? new dwa.date.DSTBoundaries(aavTable[i][5]) : void 0;
			oZoneInfo.sZoneKey = aavTable[i][0];
			oZoneInfo.sZoneName = aavTable[i][1];
			dwa.date.zoneInfo.prototype.aoDefinedZoneInfo[oZoneInfo.sZoneKey] = oZoneInfo;
		}
		
		return dwa.date.zoneInfo.prototype.aoDefinedZoneInfo;
	},
	isOSCurrent: function(){
		if (dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo)
			return this.equals(dwa.date.zoneInfo.prototype.oOSCurrentZoneInfo);
	
		var oDate = new Date;
		var anOffsets = dwa.date.zoneInfo.prototype.getLocalOffsets();
		
		// avoid the day change due to DST shift
		oDate.setUTCHours(12);
	
		// compare time timezone offsets
		if (this.nDSTBias == 0 || !this.oDSTBoundaries)
			return anOffsets[0] == anOffsets[1] && oDate.getTimezoneOffset() == this.nOffset;
		else if (this.nOffset != anOffsets[0] || this.nOffset - this.nDSTBias != anOffsets[1])
			return false;
	
		for (var n = 1; n >= (/(Mac|Linux)/.test(navigator.platform) ? -1 : 1); n -= 2) {
			var oCalendar = (new dwa.date.calendar).setUTCDate(oDate);
			oCalendar.nHours = 12;
			oCalendar.nMinutes = oCalendar.nSeconds = oCalendar.nMilliseconds = 0;
	
			// check DST boundaries for 3 years
			for ( var i = 0; i < 3; i++, oCalendar.nYear += n) {
				// set the date to the DST beginning day
				// NOTE about this.setDateOrig(1):
				// Date.prototype.set\(UTC\|\)Month(1) (means Feb.) to Mar. 30, 2002 results in Mar. 1, 2002 (instead of Feb. 28, 2002)
				var oCalendarBoundaryBegin = this.oDSTBoundaries.oBegin.getBoundaryDay(oCalendar);
				oDate.setFullYear(oCalendarBoundaryBegin.nYear);
				oDate.setDate(1);
				oDate.setMonth(oCalendarBoundaryBegin.nMonth);
				oDate.setDate(oCalendarBoundaryBegin.nDate);
				oDate.setHours(23);
				oDate.setMinutes(0);
				oDate.setSeconds(0);
				oDate.setMilliseconds(0);
				oDate.setTime(oDate.getTime() + 3600000);
			
				// the timezone for the DST beginning day should be DST/on one
				if (oDate.getTimezoneOffset() != this.nOffset - this.nDSTBias)
					return false;
			
				// the timezone for the previous day of the DST beginning day should be DST/off one
				oDate.setTime(oDate.getTime() - 86400000 * 1.5);
				if (oDate.getTimezoneOffset() != this.nOffset)
					return false;
			
				// set the date to the DST end day
				// NOTE about this.setDateOrig(1):
				// Date.prototype.set\(UTC\|\)Month(1) (means Feb.) to Mar. 30, 2002 results in Mar. 1, 2002 (instead of Feb. 28, 2002)
				var oCalendarBoundaryEnd = this.oDSTBoundaries.oEnd.getBoundaryDay(oCalendar);
				oDate.setFullYear(oCalendarBoundaryEnd.nYear);
				oDate.setDate(1);
				oDate.setMonth(oCalendarBoundaryEnd.nMonth);
				oDate.setDate(oCalendarBoundaryEnd.nDate);
				oDate.setHours(23);
				oDate.setMinutes(0);
				oDate.setSeconds(0);
				oDate.setMilliseconds(0);
				oDate.setTime(oDate.getTime() + 3600000);
			
				// the timezone for the DST end day should be DST/off one
				if (oDate.getTimezoneOffset() != this.nOffset)
					return false;
	
				// the timezone for the previous day of the DST end day should be DST/on one
				oDate.setTime(oDate.getTime() - 86400000 * 1.5);
				if (oDate.getTimezoneOffset() != this.nOffset - this.nDSTBias)
					return false;
			}
		}
	
		return true;
	},
	getBoundaries: function(nYear){
		var oCalendarBegin = new dwa.date.calendar(nYear, 0, 1, 2, 0, 0, 0);
		var oCalendarEnd = new dwa.date.calendar(nYear, 0, 1, 1, 0, 0, 0);
	
		this.oDSTBoundaries.oBegin.getBoundaryDay(oCalendarBegin);
		this.oDSTBoundaries.oEnd.getBoundaryDay(oCalendarEnd);
	
		var oDateBegin = oCalendarBegin.getUTCDate();
		var oDateEnd = oCalendarEnd.getUTCDate();
	
		oDateBegin.setTime(oDateBegin.getTime() + this.nOffset * 60000);
		oDateEnd.setTime(oDateEnd.getTime() + this.nOffset * 60000);
	
		return {oDateBegin: oDateBegin, oDateEnd: oDateEnd};
	},
	addBoundaries: function(nYear){
		if (dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey][nYear])
			return;
	
		var anBoundaries = dwa.date.zoneInfo.prototype.oBoundaries[this.sZoneKey];
		var oBoundaries = this.getBoundaries(nYear);
	
		var nBegin = oBoundaries.oDateBegin.getTime();
		var nEnd = oBoundaries.oDateEnd.getTime();
	
		anBoundaries[anBoundaries.length] = nBegin;
		anBoundaries[anBoundaries.length] = nEnd;
	
		dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey][nYear] = true;
		dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey].fSouthern = nBegin > nEnd;
	},
	getOffset: function(oDate){
		if (!this.nDSTBias)
			return this.nOffset;
	
		if (!dwa.date.zoneInfo.prototype.oBoundaries[this.sZoneKey])
			dwa.date.zoneInfo.prototype.oBoundaries[this.sZoneKey] = [];
		if (!dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey])
			dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey] = {};
	
		var anBoundaries = dwa.date.zoneInfo.prototype.oBoundaries[this.sZoneKey];
		var nYear = oDate.getUTCFullYear();
		var nLength = anBoundaries.length;
	
		this.addBoundaries(nYear - 1);
		this.addBoundaries(nYear);
		this.addBoundaries(nYear + 1);
	
		if (nLength != anBoundaries.length)
			anBoundaries.sort(dwa.common.utils.difference);
	
		var nPos = anBoundaries.length;
	
		for (var i = 0; i < anBoundaries.length; i++) {
			if (anBoundaries[i] > oDate.getTime()) {
				nPos = i - 1;
				break;
			}
		}
	
		return (nPos % 2 == 0) ^ dwa.date.zoneInfo.prototype.oBoundaryMarks[this.sZoneKey].fSouthern ?
		 this.nOffset - this.nDSTBias : this.nOffset;
	},
	getLocalOffsets: function(){
		if (dwa.date.zoneInfo.anLocalOffsets)
			return dwa.date.zoneInfo.anLocalOffsets;
	
		var oDate = new Date;
		var nOffset = oDate.getTimezoneOffset();
	
		// Loop for at most 1 year
		for (var nInc = 8 * dwa.date.calendar.prototype.nDaysInWeek * 86400000, i = 0; i < 7; i++) {
			oDate.setTime(oDate.getTime() + nInc);
			if (oDate.getTimezoneOffset() != nOffset) {
				dwa.date.zoneInfo.anLocalOffsets
				 = [Math.max(nOffset, oDate.getTimezoneOffset()), Math.min(nOffset, oDate.getTimezoneOffset())];
				return dwa.date.zoneInfo.anLocalOffsets;
			}
		}
		
		return dwa.date.zoneInfo.anLocalOffsets = [nOffset, nOffset];
	},
	aavTable: [
		["Dateline", "", "", 12, false, ""],
		["Samoa", "", "", 11, false, ""],
		["Hawaiian", "", "", 10, false, ""],
		["Alaskan", "", "", 9, true, "3, 2, 1, 11, 1, 1"],
		["Alaskan-noDST", "", "", 9, false, ""],
		["Pacific", "", "", 8, true, "3, 2, 1, 11, 1, 1"],
		["Pacific-noDST", "", "", 8, false, ""],
		["Pacific (Mexico)", "", "", 8, true, "4, 1, 1, 10, -1, 1"],
		["Mountain", "", "", 7, true, "3, 2, 1, 11, 1, 1"],
		["Mountain (Mexico)", "", "", 7, true, "4, 1, 1, 10, -1, 1"],
		["US Mountain", "", "", 7, false, ""],
		["Central", "", "", 6, true, "3, 2, 1, 11, 1, 1"],
		["Central (Mexico)", "", "", 6, true, "4, 1, 1, 10, -1, 1"],
		["Central America/Canada Central", "", "", 6, false, ""],
		["Eastern", "", "", 5, true, "3, 2, 1, 11, 1, 1"],
		["US Eastern/SA Pacific", "", "", 5, false, ""],
		["SA Western (Venezuela)", "", "", 3004, false, ""],
		["Atlantic", "", "", 4, true, "3, 2, 1, 11, 1, 1"],
		["Pacific SA", "", "", 4, true, "10, 2, 7, 3, 2, 7"],
		["Central Brazilian", "", "", 4, true, "10, 3, 7, 2, 2, 7"],
		["SA Western", "", "", 4, false, ""],
		["Newfoundland", "", "", 3003, true, "3, 2, 1, 11, 1, 1"],
		["Newfoundland-noDST", "", "", 3003, false, ""],
		["E. South America", "", "", 3, true, "10, 3, 7, 2, 2, 7"],
		["Greenland", "", "", 3, true, "4, 1, 1, 10, -1, 1"],
		["Montevideo", "", "", 3, true, "10, 1, 1, 3, 2, 1"],
		["Argentina", "", "", 3, true, "10, 3, 7, 3, 2, 7"],
		["SA Eastern", "", "", 3, false, ""],
		["Mid-Atlantic", "", "", 2, true, "3, -1, 1, 9, -1, 1"],
		["Mid-Atlantic-noDST", "", "", 2, false, ""],
		["Azores", "", "", 1, true, "3, -1, 1, 10, -1, 1"],
		["Cape Verde", "", "", 1, false, ""],
		["GMT", "", "", 0, true, "3, -1, 1, 10, -1, 1"],
		["Greenwich", "", "", 0, false, ""],
		["Western/Central Europe", "", "", -1, true, "3, -1, 1, 10, -1, 1"],
		["W. Central Africa", "", "", -1, false, ""],
		["Namibia", "", "", -2, true, "4, 1, 1, 9, 1, 1"],
		["Israel", "", "", -2, true, "3, -1, 6, 9, 2, 1"],
		["Egypt", "", "", -2, true, "4, 4, 5, 9, -1, 5"],
		["Jordan", "", "", -2, true, "3, -1, 5, 10, -1, 6"],
		["GTB", "", "", -2, true, "3, -1, 1, 10, -1, 1"],
		["Middle East", "", "", -2, true, "3, -1, 1, 10, -1, 7"],
		["South Africa", "", "", -2, false, ""],
		["Russian", "", "", -3, true, "3, -1, 1, 10, -1, 1"],
		["Arab/E. Africa", "", "", -3, false, ""],
		["Iran", "", "", -3003, true, "3, 3, 7, 9, 3, 2"],
		["Iran-noDST", "", "", -3003, false, ""],
		["Caucasus", "", "", -4, true, "3, -1, 1, 10, -1, 1"],
		["Mauritius", "", "", -4, true, "10, -1, 1, 3, -1, 1"],
		["Arabian", "", "", -4, false, ""],
		["Afghanistan", "", "", -3004, false, ""],
		["Ekaterinburg", "", "", -5, true, "3, -1, 1, 10, -1, 1"],
		["West Asia", "", "", -5, false, ""],
		["India", "", "", -3005, false, ""],
		["Nepal", "", "", -4505, false, ""],
		["N. Central Asia", "", "", -6, true, "3, -1, 1, 10, -1, 1"],
		["Central Asia/Sri Lanka", "", "", -6, false, ""],
		["Myanmar", "", "", -3006, false, ""],
		["SE Asia", "", "", -7, false, ""],
		["North Asia", "", "", -7, true, "3, -1, 1, 10, -1, 1"],
		["China/Singapore/Taiwan/W. Australia", "", "", -8, false, ""],
		["North Asia East", "", "", -8, true, "3, -1, 1, 10, -1, 1"],
		["W. Australia", "", "", -8, true, "10, -1, 1, 3, -1, 1"],
		["Japan/Korea", "", "", -9, false, ""],
		["Yakutsk", "", "", -9, true, "3, -1, 1, 10, -1, 1"],
		["Cen. Australia", "", "", -3009, true, "10, 1, 1, 4, 1, 1"],
		["AUS Central", "", "", -3009, false, ""],
		["AUS Eastern/Tasmania", "", "", -10, true, "10, 1, 1, 4, 1, 1"],
		["Vladivostok", "", "", -10, true, "3, -1, 1, 10, -1, 1"],
		["E. Australia/West Pacific", "", "", -10, false, ""],
		["Central Pacific", "", "", -11, false, ""],
		["New Zealand", "", "", -12, true, "9, -1, 1, 4, 1, 1"],
		["Fiji", "", "", -12, false, ""],
		["Tonga", "", "", -13, false, ""]
	],
	sUnknownSymbol: 'Unknown',
	sDisplayFormat: '(GMT%1%2:%3) %4',
	oBoundaries: {},
	oBoundaryMarks: {}
});

dwa.date.zoneInfo.prototype.oUTC = (new dwa.date.zoneInfo).setZoneKey("Greenwich");
