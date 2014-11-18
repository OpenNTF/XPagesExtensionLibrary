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

dojo.provide("dwa.date.dateFormatter");

dojo.require("dojo.i18n");
dojo.require("dwa.date.calendar");

dojo.requireLocalization("dwa.date", "calendar");

dojo.declare(
	"dwa.date.dateFormatter",
	null,
{
	constructor: function(nFormat){
		this._msgs = dojo.i18n.getLocalization("dwa.date", "calendar", this.lang);
		this.sAMSuffix = this._msgs["L_AM_SUFFIX"];
		this.sPMSuffix = this._msgs["L_PM_SUFFIX"];

		this.asMonths = [
			this._msgs["L_FULLMONTH_JAN"], this._msgs["L_FULLMONTH_FEB"], this._msgs["L_FULLMONTH_MAR"],
			this._msgs["L_FULLMONTH_APR"], this._msgs["L_FULLMONTH_MAY"], this._msgs["L_FULLMONTH_JUN"],
			this._msgs["L_FULLMONTH_JUL"], this._msgs["L_FULLMONTH_AUG"], this._msgs["L_FULLMONTH_SEP"],
			this._msgs["L_FULLMONTH_OCT"], this._msgs["L_FULLMONTH_NOV"], this._msgs["L_FULLMONTH_DEC"]
		];
		this.asShortMonths = [
			this._msgs["L_SHORTMONTH_JAN"], this._msgs["L_SHORTMONTH_FEB"], this._msgs["L_SHORTMONTH_MAR"],
			this._msgs["L_SHORTMONTH_APR"], this._msgs["L_SHORTMONTH_MAY"], this._msgs["L_SHORTMONTH_JUN"],
			this._msgs["L_SHORTMONTH_JUL"], this._msgs["L_SHORTMONTH_AUG"], this._msgs["L_SHORTMONTH_SEP"],
			this._msgs["L_SHORTMONTH_OCT"], this._msgs["L_SHORTMONTH_NOV"], this._msgs["L_SHORTMONTH_DEC"]
		];
		this.asNominativeMonths = (this._msgs["L_NOMINATIVEMONTH_JAN"] ? [
			this._msgs["L_NOMINATIVEMONTH_JAN"], this._msgs["L_NOMINATIVEMONTH_FEB"], this._msgs["L_NOMINATIVEMONTH_MAR"],
			this._msgs["L_NOMINATIVEMONTH_APR"], this._msgs["L_NOMINATIVEMONTH_MAY"], this._msgs["L_NOMINATIVEMONTH_JUN"],
			this._msgs["L_NOMINATIVEMONTH_JUL"], this._msgs["L_NOMINATIVEMONTH_AUG"], this._msgs["L_NOMINATIVEMONTH_SEP"],
			this._msgs["L_NOMINATIVEMONTH_OCT"], this._msgs["L_NOMINATIVEMONTH_NOV"], this._msgs["L_NOMINATIVEMONTH_DEC"]
		] : undefined);
		this.asDays = [
			this._msgs["L_FULLDAY_SUN"], this._msgs["L_FULLDAY_MON"], this._msgs["L_FULLDAY_TUE"], this._msgs["L_FULLDAY_WED"],
			this._msgs["L_FULLDAY_THU"], this._msgs["L_FULLDAY_FRI"], this._msgs["L_FULLDAY_SAT"]
		];
		this.asShortDays = [
			this._msgs["L_SHORTDAY_SUN"], this._msgs["L_SHORTDAY_MON"], this._msgs["L_SHORTDAY_TUE"], this._msgs["L_SHORTDAY_WED"],
			this._msgs["L_SHORTDAY_THU"], this._msgs["L_SHORTDAY_FRI"], this._msgs["L_SHORTDAY_SAT"]
		];

		this.oCalendarData.sDateFormat = this._msgs["D_DTFMT_DATE0"];
		this.oCalendarData.sDateSep = this._msgs["D_DTFMT_DATESEP0"];
		this.oCalendarData.sDateFormatLong = this._msgs["D_DTFMT_FULLDATE0"];
		this.oCalendarData.sTimeFormat = this._msgs["D_DTFMT_TIME0"];
		this.oCalendarData.sTimeSep = this._msgs["D_DTFMT_TIMESEP0"];

		if (typeof(nFormat) == 'undefined')
			nFormat = 3;
	
		if (typeof(nFormat) == 'string') {
			this.sFormat = nFormat;
			return this;
		}
	
		switch (nFormat) {
		case 100:
			this.sFormat = this.oCalendarData.sTimeFormat.replace(/\s*t/, '');
			this.fTimeOnly = true;
			break;
		case 102:
			this.sFormat = this.oCalendarData.f24Hour ? 'H' : 'h';
			this.sFormat += this.oCalendarData.f2DigitHour ? this.sFormat : '';
			this.fTimeOnly = true;
			break;
		case 101:
			this.sFormat = this.oCalendarData.sTimeFormat;
			this.fTimeOnly = true;
			break;
		case 0:
		case 7:
			this.sFormat = this.oCalendarData.sDateFormat;
			this.fDateOnly = nFormat != 7;
			break;
		case 11:
		case 12:
			this.sFormat = this.oCalendarData.sDateFormat.replace(/-yyyy/, '').replace(/yyyy-/, '').replace(/yyyy/, '')
			 .replace(/-yy/, '').replace(/yy-/, '').replace(/yy/, '')
			 .replace(/-GGGG/, '').replace(/GGGG-/, '').replace(/GGGG/, '').replace(/-GGG/, '').replace(/GGG-/, '').replace(/GGG/, '')
			 .replace(/-gg/, '').replace(/gg-/, '').replace(/gg/, '').replace(/-g/, '').replace(/g-/, '').replace(/g/, '');
			this.fDateOnly = nFormat != 12;
			break;
		case 13:
		case 14:
			this.sFormat = this.oCalendarData.sDateFormat;
			if (this.oCalendarData.f4DigitYear)
				this.sFormat = this.sFormat.replace(/yyyy/g, 'yy');
			this.fDateOnly = nFormat != 14;
			break;
		case 1:
			this.sFormat = this.oCalendarData.sDateFormat;
			if (!this.oCalendarData.f4DigitYear)
				this.sFormat = this.sFormat.replace(/yyyy/g, '@1').replace(/(yy|@1)/g, 'yyyy');
			this.fDateOnly = true;
			break;
		case 2:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_SHORTMONTH4YR"], this.oCalendarData.f2DigitDay ? 'dd' : 'd');
			this.fDateOnly = true;
			break;
		case 10:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_FULLMONTH4YR"], this.oCalendarData.f2DigitDay ? 'dd' : 'd');
			this.fDateOnly = true;
			break;
		case 4:
			this.fDateOnly = true;
		case 5:
			this.sFormat = this.oCalendarData.sDateFormatLong;
			break;
		case 6:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_DAYDATE"], this.oCalendarData.f2DigitDay ? 'dd' : 'd');
			this.fDateOnly = true;
			break;
		case 15:
			this.sFormat = this._msgs["D_DTFMT_WEEKDATE"];
			this.fDateOnly = true;
			break;
		case 9:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_DAYMONTHDATE"], this.oCalendarData.f2DigitDay ? 'dd' : 'd');
			this.fDateOnly = true;
			break;
		case 16:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_MONTH4YR"], this.oCalendarData.f2DigitDay ? 'dd' : 'd');
			this.fDateOnly = true;
			break;
		case 17:
			this.sFormat = this._msgs["D_DTFMT_MONTH4YR"].replace(/MMMM/g, 'MMM');
			this.fDateOnly = true;
			break;
		case 18:
			this.sFormat = this.oCalendarData.sDateFormatLong.replace(/dddd/g, 'ddd').replace(/MMMM/g, 'MMM');
			this.fDateOnly = true;
			break;
		case 3:
		case 8:
		default:
			this.sFormat = dwa.common.utils.formatMessage(this._msgs["D_DTFMT_MEDIUMDATE"], this.oCalendarData.sDateFormat);
			this.fDateOnly = nFormat != 8;
			break;
		}
	
		var oFormatWithTimeList = {
			7: void 0,
			12: void 0,
			14: void 0,
			8: void 0,
			5: void 0
		};
	
		if (nFormat in oFormatWithTimeList) {
			var sFormatDateWithTime = "%1 %2";
			this.sFormat = dwa.common.utils.formatMessage(sFormatDateWithTime, this.sFormat, this.oCalendarData.sTimeFormat);
		}
	
		if (this.fDateOnly)
			this.format = this.formatDate;
		if (this.fTimeOnly)
			this.format = this.formatTime;

	},
	format: function(oCalendar){
		if (oCalendar.isInvalid())
			return '';
	
		var nTwoDigitYear = oCalendar.nYear % 100;
		var n12Hr = oCalendar.nHours % 12 == 0 ? 12 : (oCalendar.nHours % 12);
		var sTwoDigitYear = (nTwoDigitYear < 10 ? '0' : '') + nTwoDigitYear;
		var sMonth = (oCalendar.nMonth < 9 ? '0' : '') + (oCalendar.nMonth + 1);
		var sDate = (oCalendar.nDate < 10 ? '0' : '') + oCalendar.nDate;
		var s12Hr = (n12Hr < 10 ? '0' : '') + n12Hr;
		var sHours = (oCalendar.nHours < 10 ? '0' : '') + oCalendar.nHours;
		var sMinutes = (oCalendar.nMinutes < 10 ? '0' : '') + oCalendar.nMinutes;
		var sSeconds = (oCalendar.nSeconds < 10 ? '0' : '') + oCalendar.nSeconds;
	
		// Do not change the order of following replacements
		return this.sFormat.replace(/MMMM/g, '@1').replace(/MMM/g, '@2').replace(/dddd/g, '@3').replace(/ddd/g, '@4').replace(/NNNN/g,"@5")
		 .replace(/yyyy/g, oCalendar.nYear).replace(/yy/g, sTwoDigitYear)
		 .replace(/MM/g, sMonth).replace(/M/g, oCalendar.nMonth + 1)
		 .replace(/dd/g, sDate).replace(/d\b/g, oCalendar.nDate)
		 .replace(/hh/g, s12Hr).replace(/h/g, n12Hr).replace(/HH/g, sHours).replace(/H/g, oCalendar.nHours)
		 .replace(/mm/g, sMinutes).replace(/m/g, oCalendar.nMinutes)
		 .replace(/ss/g, sSeconds).replace(/s/g, oCalendar.nSeconds)
		 .replace(/t/g, oCalendar.nHours < 12 ? this.sAMSuffix : this.sPMSuffix)
		 .replace(/-/g, this.oCalendarData.sDateSep)
		 .replace(/:/g, this.oCalendarData.sTimeSep)
		 .replace(/@1/g, this.asMonths[oCalendar.nMonth]).replace(/@2/g, this.asShortMonths[oCalendar.nMonth]).replace(/@5/g,(this.asNominativeMonths?this.asNominativeMonths[oCalendar.nMonth]:this.asMonths[oCalendar.nMonth]))
		 .replace(/@3/g, this.asDays[oCalendar.nDay]).replace(/@4/g, this.asShortDays[oCalendar.nDay]);
	},
	formatDate: function(oCalendar){
		if (oCalendar.isInvalid())
			return '';
	
		var nTwoDigitYear = oCalendar.nYear % 100;
		var sTwoDigitYear = (nTwoDigitYear < 10 ? '0' : '') + nTwoDigitYear;
		var sMonth = (oCalendar.nMonth < 9 ? '0' : '') + (oCalendar.nMonth + 1);
		var sDate = (oCalendar.nDate < 10 ? '0' : '') + oCalendar.nDate;
	
		// Do not change the order of following replacements
		return this.sFormat.replace(/MMMM/g, '@1').replace(/MMM/g, '@2').replace(/dddd/g, '@3').replace(/ddd/g, '@4').replace(/NNNN/g,"@5")
		 .replace(/yyyy/g, oCalendar.nYear).replace(/yy/g, sTwoDigitYear)
		 .replace(/MM/g, sMonth).replace(/M/g, oCalendar.nMonth + 1)
		 .replace(/dd/g, sDate).replace(/d\b/g, oCalendar.nDate)
		 .replace(/-/g, this.oCalendarData.sDateSep)
		 .replace(/@1/g, this.asMonths[oCalendar.nMonth]).replace(/@2/g, this.asShortMonths[oCalendar.nMonth]).replace(/@5/g,(this.asNominativeMonths?this.asNominativeMonths[oCalendar.nMonth]:this.asMonths[oCalendar.nMonth]))
		 .replace(/@3/g, this.asDays[oCalendar.nDay]).replace(/@4/g, this.asShortDays[oCalendar.nDay]);
	},
	formatTime: function(oCalendar){
		if (oCalendar.isInvalid())
			return '';
	
		var n12Hr = oCalendar.nHours % 12 == 0 ? 12 : (oCalendar.nHours % 12);
		var s12Hr = (n12Hr < 10 ? '0' : '') + n12Hr;
		var sHours = (oCalendar.nHours < 10 ? '0' : '') + oCalendar.nHours;
		var sMinutes = (oCalendar.nMinutes < 10 ? '0' : '') + oCalendar.nMinutes;
		var sSeconds = (oCalendar.nSeconds < 10 ? '0' : '') + oCalendar.nSeconds;
	
		// Do not change the order of following replacements
		return this.sFormat.replace(/hh/g, s12Hr).replace(/h/g, n12Hr).replace(/HH/g, sHours).replace(/H/g, oCalendar.nHours)
		 .replace(/mm/g, sMinutes).replace(/m/g, oCalendar.nMinutes)
		 .replace(/ss/g, sSeconds).replace(/s/g, oCalendar.nSeconds)
		 .replace(/t/g, oCalendar.nHours < 12 ? this.sAMSuffix : this.sPMSuffix)
		 .replace(/:/g, this.oCalendarData.sTimeSep);
	},
	omitYear: function(){
		this.sFormat = this.sFormat.replace(/-yyyy/, '').replace(/yyyy-/, '').replace(/yyyy/, '')
		 .replace(/-yy/, '').replace(/yy-/, '').replace(/yy/, '')
		 .replace(/-GGGG/, '').replace(/GGGG-/, '').replace(/GGGG/, '').replace(/-GGG/, '').replace(/GGG-/, '').replace(/GGG/, '')
		 .replace(/-gg/, '').replace(/gg-/, '').replace(/gg/, '').replace(/-g/, '').replace(/g-/, '').replace(/g/, '');
	},
	validateDate: function(sDate){
		if (!sDate)
			return (new dwa.date.calendar).setUTCDate(new Date(Date.UTC(NaN)));
	
		var asTokens = this.getRegExpValidateDate(sDate).exec(sDate);
	
		if (!asTokens) {
			var e = new Error('Bad date representation: ' + sDate);
			e.sBadArg = sTime;
			e.sBadArgFor = 'DateUIString';
			throw e;
		}
	
		if (!this.sDateValidateFormat) {
			var sFormat = this.sFormat.replace(/MMMM/g, '').replace(/MMM/g, '').replace(/dddd/g, '').replace(/ddd/g, '');
			var nYearPos = sFormat.indexOf('y');
			var nMonthPos = sFormat.search(/M{1,2}/);
			var nDatePos = sFormat.search(/d{1,2}/);
	
			this.sDateValidateFormat = '';
	
			for (var i = 0; i < sFormat.length; i++)
				this.sDateValidateFormat += i == nYearPos ? 'Y' : i == nMonthPos ? 'M' : i == nDatePos ? 'D' : '';
		}
	
		// If 4 digits are first, assume this is year followed by month & day
		var anTokenPoses = [2, 3, 5];
		this.sDateValidateFormat = asTokens[anTokenPoses[0]].length != 4 ? this.sDateValidateFormat : 'YMD';
	
		var nYear = void 0 - 0, nMonth = void 0 - 0, nDate = void 0 - 0;
	
		for (var i = 0; i < this.sDateValidateFormat.length; i++) {
			var n = anTokenPoses[i];
	
			switch (this.sDateValidateFormat.charAt(i)) {
			case 'Y':
				if (asTokens[n].length != 2 && asTokens[n].length != 4) {
					var e = new Error('Bad date representation: ' + sDate);
					e.sBadArg = sTime;
					e.sBadArgFor = 'DateUIString';
					throw e;
				}
	
				nYear = asTokens[n] - 0;
				if (asTokens[n].length <= 2)
					nYear += 1900 + ((nYear + 1900) < ((new dwa.date.calendar).setDate(new Date).nYear - 20) ? 100 : 0);
	
				break;
			case 'M':
				nMonth = asTokens[n] - 0;
				break;
			case 'D':
				nDate = asTokens[n] - 0;
				break;
			}
		}
	
		if (isNaN(nYear) || isNaN(nMonth) || isNaN(nDate)) {
			var e = new Error('Bad date representation: ' + sDate);
			e.sBadArg = sTime;
			e.sBadArgFor = 'DateUIString';
			throw e;
		}
	
		if (nMonth > 12 && nDate > 0 && nDate <= 12) {
			var nTmp = nMonth;
			nMonth = nDate;
			nDate = nTmp;
		}
	
		var oCalendar = new dwa.date.calendar(nYear, nMonth - 1, nDate, 0, 0, 0, 0, void 0, true, false);
	
		if (nMonth < 1 || nMonth > 12 || (nDate > 28 && nDate > oCalendar.getDaysInMonth())) {
			var e = new Error('Bad date representation: ' + sDate);
			e.sBadArg = sTime;
			e.sBadArgFor = 'DateUIString';
			throw e;
		}
	
		return oCalendar;
	},
	validateTime: function(sTime){
		if (!sTime)
			return (new dwa.date.calendar).setUTCDate(new Date(Date.UTC(NaN)));
	
		var asTokens = this.getRegExpValidateTime().exec(sTime);
	
		if (!asTokens) {
			var e = new Error('Bad time representation: ' + sTime);
			e.sBadArg = sTime;
			e.sBadArgFor = 'TimeUIString';
			throw e;
		}
	
		var fPM;
	
		// Determine if am/pm was specified
		if (asTokens[1] || asTokens[6]) {
			var sToken = asTokens[1] ? asTokens[1] : asTokens[6];
	
			if (this.sAMSuffix.toUpperCase() == sToken.toUpperCase()) {
				fPM = false;
			} else if (this.sPMSuffix.toUpperCase() == sToken.toUpperCase()) {
				fPM = true;
			} else {
				var e = new Error('Bad time representation: ' + sTime);
				e.sBadArg = sTime;
				e.sBadArgFor = 'TimeUIString';
				throw e;
			}
		}
	
		var nHours = asTokens[2] - 0;
		var nMinutes = asTokens[3] - 0;
		var nSeconds = (asTokens[5] ? asTokens[5] : '') - 0;
	
		if (isNaN(nHours) || isNaN(nMinutes) || isNaN(nSeconds)
		 || nHours >= 24 || nMinutes >= 60 || nSeconds >= 60) {
			var e = new Error('Bad time representation: ' + sTime);
			e.sBadArg = sTime;
			e.sBadArgFor = 'TimeUIString';
			throw e;
		}
	
		if (typeof(fPM) != 'undefined') {
			nHours = nHours != 12 ? nHours : 0; // adjust 12:** pm
			nHours += fPM ? 12 : 0;
		}
	
		return new dwa.date.calendar(1900, 0, 1, nHours, nMinutes, nSeconds, 0, void 0, false, true);
	},
	sRegExpMetaChars: '.*?+-/:{}()[],^$\\\'"|!',
	getRegExpValidateDate: function(sDate){
		var sDefaltSepChars = '-/.';
	
		var sSep = '\\-\\/\\.' + 
					((!!this.oCalendarData.sDateSep && sDefaltSepChars.indexOf(this.oCalendarData.sDateSep) == -1) ?
					 ((this.sRegExpMetaChars.indexOf(this.oCalendarData.sDateSep) != -1 ? '\\' : '') + this.oCalendarData.sDateSep) : '');
	
		var sPat = ( sDate.match( new RegExp('[' + sSep + ']' ))) ?
					'([^\\s\\d' + sSep + ']*)(\\d{1,4})[' + sSep + '](\\d{1,2})[' + sSep + ']([^\\s\\d' + sSep + ']*)(\\d{1,4})' :
					'([^\\s\\d' + sSep + ']*)(\\d{1,4})(\\d{1,2})([^\\s\\d' + sSep + ']*)(\\d{1,4})';
	
		return new RegExp(sPat);
	},
	getRegExpValidateTime: function(){
		var sDefaltSepChars = ':h.';
	
		var sSep = '\\:h\\.' + 
					((!!this.oCalendarData.sTimeSep && sDefaltSepChars.indexOf(this.oCalendarData.sTimeSep) == -1) ?
					 ((this.sRegExpMetaChars.indexOf(this.oCalendarData.sTimeSep) != -1 ? '\\' : '') + this.oCalendarData.sTimeSep) : '');
	
		var sPat = '([^\\s\\d]*)[\\s]?(\\d+)[' + sSep + '](\\d+)([\\:\\.](\\d+))?[\\s]?([^\\s\\d]*)';
	
	 	return new RegExp(sPat);
	},

	oCalendarData: {
		nDefaultDuration: 60, // D_CS_DEFAULT_DURATION,
		nFirstDayMonth: 0,
		nFirstDayFiveDay: 1,
		nFirstDayWeek: 1
	}
});
