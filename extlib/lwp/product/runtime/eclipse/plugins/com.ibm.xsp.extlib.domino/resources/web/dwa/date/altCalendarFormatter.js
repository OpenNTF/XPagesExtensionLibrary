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

dojo.provide("dwa.date.altCalendarFormatter");

dojo.requireLocalization("dwa.date", "calendar");

var D_GERESH="&#x05f3";
var D_GERSHAIM="&#x05f4";
var D_DIG="&#x05d0;|&#x05d1;|&#x05d2;|&#x05d3;|&#x05d4;|&#x05d5;|&#x05d6;|&#x05d7;|&#x05d8";
var D_TEN="&#x05d9;|&#x05db;|&#x05dc;|&#x05de;|&#x05e0;|&#x05e1;|&#x05e2;|&#x05e4;|&#x05e6";
var D_HUN="&#x05e7;|&#x05e8;|&#x05e9;|&#x05ea";
var D_REP="&#x05d9;&#x05d4;|&#x05d9;&#x05d5;|&#x05d8;&#x05d5;|&#x05d8;&#x05d6";
var MILLISECS_IN_DAY = 86400000; // (HOURS_IN_DAY * MINS_IN_HOUR * SECS_IN_MIN * MILLISECS_IN_SEC)

dojo.declare(
	"dwa.date.altCalendarFormatter",
	null,
{
	constructor: function(nType){
		this._msgs = dojo.i18n.getLocalization("dwa.date", "calendar", this.lang);
		this.nType = nType;
		this.asCache = {};
		this.asJapaneseSixDay = this._msgs["L_ALTCAL_SIXDAY_LIST"].split('|');
	},
	format: function(oDate){
		// read from data cache
		var sDate = oDate.getISO8601String();
		if (this.asCache[sDate])
			return this.asCache[sDate];
	
		// get alternate calendar
		var sDay = '';
		switch (this.nType) {
			case 1:
				sDay = this.formatHijri(oDate);
				break;
			case 2:
				sDay = this.formatHebrew(oDate);
				break;
			case 3:
				sDay = this.formatJapaneseSixDay(oDate);
				break;
		}
	
		// write to data cache
		if (sDay)
			this.asCache[sDate] = sDay;
	
		return sDay;
	},
	lunarlookup: [
		/*   GregYear, GregMonth, GregDay, LunarMonth */
		[1989, 12, 28, 12], [1990,  1, 27,  1], [1990,  2, 25,  2], [1990,  3, 27,  3], [1990,  4, 25,  4], [1990,  5, 24,  5], [1990,  6, 23,  5], [1990,  7, 22,  6], [1990,  8, 20,  7], [1990,  9, 19,  8],
		[1990, 10, 19,  9], [1990, 11, 17, 10], [1990, 12, 17, 11], [1991,  1, 16, 12], [1991,  2, 15,  1], [1991,  3, 16,  2], [1991,  4, 15,  3], [1991,  5, 14,  4], [1991,  6, 12,  5], [1991,  7, 12,  6],
		[1991,  8, 10,  7], [1991,  9,  8,  8], [1991, 10,  8,  9], [1991, 11,  6, 10], [1991, 12,  6, 11], [1992,  1,  5, 12], [1992,  2,  4,  1], [1992,  3,  4,  2], [1992,  4,  3,  3], [1992,  5,  3,  4],
		[1992,  6,  1,  5], [1992,  6, 30,  6], [1992,  7, 30,  7], [1992,  8, 28,  8], [1992,  9, 26,  9], [1992, 10, 26, 10], [1992, 11, 24, 11], [1992, 12, 24, 12], [1993,  1, 23,  1], [1993,  2, 21,  2],
		[1993,  3, 23,  3], [1993,  4, 22,  3], [1993,  5, 21,  4], [1993,  6, 20,  5], [1993,  7, 19,  6], [1993,  8, 18,  7], [1993,  9, 16,  8], [1993, 10, 15,  9], [1993, 11, 14, 10], [1993, 12, 13, 11],
		[1994,  1, 12, 12], [1994,  2, 10,  1], [1994,  3, 12,  2], [1994,  4, 11,  3], [1994,  5, 11,  4], [1994,  6,  9,  5], [1994,  7,  9,  6], [1994,  8,  7,  7], [1994,  9,  6,  8], [1994, 10,  5,  9],
		[1994, 11,  3, 10], [1994, 12,  3, 11], [1995,  1,  1, 12], [1995,  1, 31,  1], [1995,  3,  1,  2], [1995,  3, 31,  3], [1995,  4, 30,  4], [1995,  5, 29,  5], [1995,  6, 28,  6], [1995,  7, 28,  7],
		[1995,  8, 26,  8], [1995,  9, 25,  8], [1995, 10, 24,  9], [1995, 11, 23, 10], [1995, 12, 22, 11], [1996,  1, 20, 12], [1996,  2, 19,  1], [1996,  3, 19,  2], [1996,  4, 18,  3], [1996,  5, 17,  4],
		[1996,  6, 16,  5], [1996,  7, 16,  6], [1996,  8, 14,  7], [1996,  9, 13,  8], [1996, 10, 12,  9], [1996, 11, 11, 10], [1996, 12, 11, 11], [1997,  1,  9, 12], [1997,  2,  8,  1], [1997,  3,  9,  2],
		[1997,  4,  7,  3], [1997,  5,  7,  4], [1997,  6,  5,  5], [1997,  7,  5,  6], [1997,  8,  3,  7], [1997,  9,  2,  8], [1997, 10,  2,  9], [1997, 10, 31, 10], [1997, 11, 30, 11], [1997, 12, 30, 12],
		[1998,  1, 28,  1], [1998,  2, 27,  2], [1998,  3, 28,  3], [1998,  4, 26,  4], [1998,  5, 26,  5], [1998,  6, 24,  5], [1998,  7, 23,  6], [1998,  8, 22,  7], [1998,  9, 21,  8], [1998, 10, 20,  9],
		[1998, 11, 19, 10], [1998, 12, 19, 11], [1999,  1, 18, 12], [1999,  2, 16,  1], [1999,  3, 18,  2], [1999,  4, 16,  3], [1999,  5, 15,  4], [1999,  6, 14,  5], [1999,  7, 13,  6], [1999,  8, 11,  7],
		[1999,  9, 10,  8], [1999, 10,  9,  9], [1999, 11,  8, 10], [1999, 12,  8, 11], [2000,  1,  7, 12], [2000,  2,  5,  1], [2000,  3,  6,  2], [2000,  4,  5,  3], [2000,  5,  4,  4], [2000,  6,  2,  5],
		[2000,  7,  2,  6], [2000,  7, 31,  7], [2000,  8, 29,  8], [2000,  9, 28,  9], [2000, 10, 27, 10], [2000, 11, 26, 11], [2000, 12, 26, 12], [2001,  1, 24,  1], [2001,  2, 23,  2], [2001,  3, 25,  3],
		[2001,  4, 24,  4], [2001,  5, 23,  4], [2001,  6, 21,  5], [2001,  7, 21,  6], [2001,  8, 19,  7], [2001,  9, 17,  8], [2001, 10, 17,  9], [2001, 11, 15, 10], [2001, 12, 15, 11], [2002,  1, 13, 12],
		[2002,  2, 12,  1], [2002,  3, 14,  2], [2002,  4, 13,  3], [2002,  5, 12,  4], [2002,  6, 11,  5], [2002,  7, 10,  6], [2002,  8,  9,  7], [2002,  9,  7,  8], [2002, 10,  6,  9], [2002, 11,  5, 10],
		[2002, 12,  4, 11], [2003,  1,  3, 12], [2003,  2,  1,  1], [2003,  3,  3,  2], [2003,  4,  2,  3], [2003,  5,  1,  4], [2003,  5, 31,  5], [2003,  6, 30,  6], [2003,  7, 29,  7], [2003,  8, 28,  8],
		[2003,  9, 26,  9], [2003, 10, 25, 10], [2003, 11, 24, 11], [2003, 12, 23, 12], [2004,  1, 22,  1], [2004,  2, 20,  2], [2004,  3, 21,  2], [2004,  4, 19,  3], [2004,  5, 19,  4], [2004,  6, 18,  5],
		[2004,  7, 17,  6], [2004,  8, 16,  7], [2004,  9, 14,  8], [2004, 10, 14,  9], [2004, 11, 12, 10], [2004, 12, 12, 11], [2005,  1, 10, 12], [2005,  2,  9,  1], [2005,  3, 10,  2], [2005,  4,  9,  3],
		[2005,  5,  8,  4], [2005,  6,  7,  5], [2005,  7,  6,  6], [2005,  8,  5,  7], [2005,  9,  4,  8], [2005, 10,  3,  9], [2005, 11,  2, 10], [2005, 12,  1, 11], [2005, 12, 31, 12], [2006,  1, 29,  1],
		[2006,  2, 28,  2], [2006,  3, 29,  3], [2006,  4, 28,  4], [2006,  5, 27,  5], [2006,  6, 26,  6], [2006,  7, 25,  7], [2006,  8, 24,  7], [2006,  9, 22,  8], [2006, 10, 22,  9], [2006, 11, 21, 10],
		[2006, 12, 20, 11], [2007,  1, 19, 12], [2007,  2, 18,  1], [2007,  3, 19,  2], [2007,  4, 17,  3], [2007,  5, 17,  4], [2007,  6, 15,  5], [2007,  7, 14,  6], [2007,  8, 13,  7], [2007,  9, 11,  8],
		[2007, 10, 11,  9], [2007, 11, 10, 10], [2007, 12, 10, 11], [2008,  1,  8, 12], [2008,  2,  7,  1], [2008,  3,  8,  2], [2008,  4,  6,  3], [2008,  5,  5,  4], [2008,  6,  4,  5], [2008,  7,  3,  6],
		[2008,  8,  1,  7], [2008,  8, 31,  8], [2008,  9, 29,  9], [2008, 10, 29, 10], [2008, 11, 28, 11], [2008, 12, 27, 12], [2009,  1, 26,  1], [2009,  2, 25,  2], [2009,  3, 27,  3], [2009,  4, 25,  4],
		[2009,  5, 24,  5], [2009,  6, 23,  5], [2009,  7, 22,  6], [2009,  8, 20,  7], [2009,  9, 19,  8], [2009, 10, 18,  9], [2009, 11, 17, 10], [2009, 12, 16, 11], [2010,  1, 15, 12], [2010,  2, 14,  1],
		[2010,  3, 16,  2], [2010,  4, 14,  3], [2010,  5, 14,  4], [2010,  6, 12,  5], [2010,  7, 12,  6], [2010,  8, 10,  7], [2010,  9,  8,  8], [2010, 10,  8,  9], [2010, 11,  6, 10], [2010, 12,  6, 11],
		[2011,  1,  4, 12], [2011,  2,  3,  1], [2011,  3,  5,  2], [2011,  4,  3,  3], [2011,  5,  3,  4], [2011,  6,  2,  5], [2011,  7,  1,  6], [2011,  7, 31,  7], [2011,  8, 29,  8], [2011,  9, 27,  9],
		[2011, 10, 27, 10], [2011, 11, 25, 11], [2011, 12, 25, 12], [2012,  1, 23,  1], [2012,  2, 22,  2], [2012,  3, 22,  3], [2012,  4, 21,  4], [2012,  5, 21,  4], [2012,  6, 19,  5], [2012,  7, 19,  6],
		[2012,  8, 17,  7], [2012,  9, 16,  8], [2012, 10, 15,  9], [2012, 11, 14, 10], [2012, 12, 13, 11], [2013,  1, 12, 12], [2013,  2, 10,  1], [2013,  3, 12,  2], [2013,  4, 10,  3], [2013,  5, 10,  4],
		[2013,  6,  9,  5], [2013,  7,  8,  6], [2013,  8,  7,  7], [2013,  9,  5,  8], [2013, 10,  5,  9], [2013, 11,  3, 10], [2013, 12,  3, 11], [2014,  1,  1, 12], [2014,  1, 31,  1], [2014,  3,  1,  2],
		[2014,  3, 31,  3], [2014,  4, 29,  4], [2014,  5, 29,  5], [2014,  6, 27,  6], [2014,  7, 27,  7], [2014,  8, 25,  8], [2014,  9, 24,  9], [2014, 10, 24,  9], [2014, 11, 22, 10], [2014, 12, 22, 11],
		[2015,  1, 20, 12], [2015,  2, 19,  1], [2015,  3, 20,  2], [2015,  4, 19,  3], [2015,  5, 18,  4], [2015,  6, 16,  5], [2015,  7, 16,  6], [2015,  8, 14,  7], [2015,  9, 13,  8], [2015, 10, 13,  9],
		[2015, 11, 12, 10], [2015, 12, 11, 11], [2016,  1, 10, 12], [2016,  2,  8,  1], [2016,  3,  9,  2], [2016,  4,  7,  3], [2016,  5,  7,  4], [2016,  6,  5,  5], [2016,  7,  4,  6], [2016,  8,  3,  7],
		[2016,  9,  1,  8], [2016, 10,  1,  9], [2016, 10, 31, 10], [2016, 11, 29, 11], [2016, 12, 29, 12], [2017,  1, 28,  1], [2017,  2, 26,  2], [2017,  3, 28,  3], [2017,  4, 26,  4], [2017,  5, 26,  5],
		[2017,  6, 24,  6], [2017,  7, 23,  6], [2017,  8, 22,  7], [2017,  9, 20,  8], [2017, 10, 20,  9], [2017, 11, 18, 10], [2017, 12, 18, 11], [2018,  1, 17, 12], [2018,  2, 16,  1], [2018,  3, 17,  2],
		[2018,  4, 16,  3], [2018,  5, 15,  4], [2018,  6, 14,  5], [2018,  7, 13,  6], [2018,  8, 11,  7], [2018,  9, 10,  8], [2018, 10,  9,  9], [2018, 11,  8, 10], [2018, 12,  7, 11], [2019,  1,  6, 12]
	],
	formatJapaneseSixDay: function(oCalendar){
		if (!oCalendar)
			return '';
	
		if (oCalendar.nYear < 1990 || 2018 < oCalendar.nYear)
			return '';
	
		for (var j=this.lunarlookup.length-1; j>=0; j--) {
			var oDate = new Date(this.lunarlookup[j][0], this.lunarlookup[j][1]-1, this.lunarlookup[j][2]);
			var nDays = Math.floor((oCalendar.getDate().getTime() - oDate.getTime()) / MILLISECS_IN_DAY);
			if (nDays >= 0) {
				var nLunerMonth = this.lunarlookup[j][3];
				var nLunerDate = nDays + 1;
				var nSixday = (nLunerMonth + nLunerDate - 2) % 6;
				return this.asJapaneseSixDay[nSixday];
			}
		}
		return '';
	},
	formatHebrew: function(oCalendar){
		if (!oCalendar)
			return '';
	
		var asHebMonthsNames = (this._msgs["L_ALTCAL_JEWISH_MONTH_LIST2"] + '|*|' + this._msgs["L_ALTCAL_JEWISH_MONTH_LIST"]).split('|');
		var anDaysFromYearStart = [0,31,59,90,120,151,181,212,243,273,304,334];
		var anDaysInMonth = [
			[[30, 30, 30 ], [   0,   0,   0 ], [   0,   0,   0 ]], //Tishri
			[[29, 29, 30 ], [  30,  30,  30 ], [  30,  30,  30 ]], //Heshvan
			[[29, 30, 30 ], [  59,  59,  60 ], [  59,  59,  60 ]], //Kislev
			[[29, 29, 29 ], [  88,  89,  90 ], [  88,  89,  90 ]], //Tevet
			[[30, 30, 30 ], [ 117, 118, 119 ], [ 117, 118, 119 ]], //Shvat
			[[30, 30, 30 ], [ 147, 148, 149 ], [ 147, 148, 149 ]], //Adar (Alef in leap years)
			[[29, 29, 29 ], [ 147, 148, 149 ], [ 177, 178, 179 ]], //Adar (Bet in leap years)
			[[30, 30, 30 ], [ 176, 177, 178 ], [ 206, 207, 208 ]], //Nisan
			[[29, 29, 29 ], [ 206, 207, 208 ], [ 236, 237, 238 ]], //Iyar
			[[30, 30, 30 ], [ 235, 236, 237 ], [ 265, 266, 267 ]], //Sivan
			[[29, 29, 29 ], [ 265, 266, 267 ], [ 295, 296, 297 ]], //Tammuz
			[[30, 30, 30 ], [ 294, 295, 296 ], [ 324, 325, 326 ]], //Av
			[[29, 29, 29 ], [ 324, 325, 326 ], [ 354, 355, 356 ]], //Elul
			[[ 0,  0,  0 ], [ 353, 354, 355 ], [ 383, 384, 385 ]]
		];
	
		var bIsLeap = !(oCalendar.nYear%4) && (oCalendar.nYear%100 || !(oCalendar.nYear%400));
		var npYears = oCalendar.nYear - 1;
		var nlDays = Math.floor(npYears/4) - Math.floor(npYears/100) + Math.floor(npYears/400);
		var njDate = (365 * npYears + nlDays) + (anDaysFromYearStart[oCalendar.nMonth] + (bIsLeap && oCalendar.nMonth > 1? 1 : 0)) + oCalendar.nDate + 1721426 - 1;
	
		var nhDate = njDate - 347997;
		var nhMonth = Math.floor((nhDate * 24*1080) / (29*24*1080 + 12*1080 + 793));
		var nhYear = Math.floor((19 * nhMonth + 234) / 235) + 1;
	
		var nDaysInHebYear = nhDate - this.startOfHebYear(nhYear);
		while (nDaysInHebYear < 1) {
			nhYear--;
			nDaysInHebYear = nhDate - this.startOfHebYear(nhYear);
		}
	
		nhMonth = 0;
		var nInd1 = this.isHebLeap(nhYear)? 2 : 1;
		var nhYearLength = this.startOfHebYear(nhYear + 1) - this.startOfHebYear(nhYear);
		var nInd2 = (nhYearLength > 380? nhYearLength-30 : nhYearLength) - 353;  /* 0 - 2 */
		while (nDaysInHebYear > anDaysInMonth[nhMonth][nInd1][nInd2])
			nhMonth++;
	
		nhMonth--;
		var nhDay = nDaysInHebYear - anDaysInMonth[nhMonth][nInd1][nInd2];
		var sHebMonthName = asHebMonthsNames[this.isHebLeap(nhYear) && nhMonth == 5? 6 : nhMonth];
		if (this.isHebLeap(nhYear) && nhMonth == 5)
			sHebMonthName += ' ' + this._msgs["D_ADAR_ALEF_NUMBER"];
		else if (this.isHebLeap(nhYear) && nhMonth == 6)
			sHebMonthName += ' ' + this._msgs["D_ADAR_BET_NUMBER"];
	
		var bHebLang = this.lang == 'he';
		return (bHebLang? this.hebYearNumerals(nhYear) : nhYear) + '|' + sHebMonthName + '|' + (bHebLang? this.hebDayNumerals(nhDay) : nhDay);
	},
	startOfHebYear: function(nYear){
		var nMonths = Math.floor((235 * nYear - 234) / 19);
		var nPartsOfDay = nMonths * (12*1080 + 793) + 11*1080 + 204;
		var nDays  = nMonths * 29 + Math.floor(nPartsOfDay / (24*1080));
		var nTimeOfDay = nPartsOfDay % 24*1080;
		var nDayOfWeek = nDays % 7;
		if (nDayOfWeek == 2 || nDayOfWeek == 4 || nDayOfWeek == 6) {
			nDays += 1;
			nDayOfWeek = nDays % 7;
		}
		if (nDayOfWeek == 1 && nTimeOfDay > 15 * 1080 + 204 && !this.isHebLeap(nYear))
		   nDays += 2;
		else if (nDayOfWeek == 0 && nTimeOfDay > 21 * 1080 + 589 && this.isHebLeap(nYear-1))
		   nDays += 1;
	
		return nDays;
	},
	isHebLeap: function(nYear){
		var nY = (nYear*12 + 17) % 19;
		return nY >= ((nY < 0) ? -7 : 12);
	},
	hebYearNumerals: function(nHebYear){
		var asDIG = this.getHebNumerals('DIG'), asTEN = this.getHebNumerals('TEN'), asHUN = this.getHebNumerals('HUN'), asREP = this.getHebNumerals('REP');
		var str  = '', str2 = '', sGeresh=D_GERESH, sQuot=D_GERSHAIM;
		var i = 0, n = 4, j = 9;
		var nYear = nHebYear%1000;
	
		while (nYear) {
			if (nYear >= n*100) {
				str=str.concat(asHUN[n-1]);
				nYear -= n*100;
				continue;
			} else if (n > 1) {
				n--;
				continue;
			} else if (nYear >= j*10) {
				str=str.concat(asTEN[j-1]);
				nYear -= j*10;
			} else if (j >1) {
				j--;
				continue;
			} else if (nYear > 0) {
				str=str.concat(asDIG[nYear-1]);
				nYear=0;
			}
		}
	
		var str1 = '', nInd = str.indexOf(asREP[0]);
		if (nInd > -1)
			str = str1.concat(str.substr(0, nInd), asREP[2], str.substr(nInd+asREP[0].length, str.length-nInd-asREP[0].length));
		else if ((nInd=str.indexOf(asREP[1])) > -1)
			str = str1.concat(str.substr(0, nInd), asREP[3], str.substr(nInd+asREP[1].length, str.length-nInd-asREP[1].length));
	
		if (str.length > 1) {
			var nLast = str.lastIndexOf('&');
			str = str2.concat(str.substr(0, nLast), sQuot, str.substr(nLast));
		} else {
			str = str.concat(sGeresh);
		}
		return str;
	},
	hebDayNumerals: function(nHebDay){
		var asDIG = this.getHebNumerals('DIG'), asTEN = this.getHebNumerals('TEN'), asHUN = this.getHebNumerals('HUN'), asREP = this.getHebNumerals('REP');
		var str = '', j = 3, sGeresh=D_GERESH, bNogrsh = false, sQuot=D_GERSHAIM;
		var nDay = nHebDay;
		while (nDay) {
			if (nDay >= j*10) {
				str=str.concat(asTEN[j-1]);
				nDay -= j*10;
			} else if (j >1) {
				j--;
				continue;
			} else if (nDay > 0) {
				str=str.concat(asDIG[nDay-1]);
				nDay = 0;
			}
		}
	
		var str1 = '', nInd = str.indexOf(asREP[0]);
		if (nInd > -1)
		   str = str1.concat(str.substr(0, nInd), asREP[2], str.substr(nInd+asREP[0].length, str.length-nInd-asREP[0].length));
		else if ((nInd=str.indexOf(asREP[1])) > -1)
		   str = str1.concat(str.substr(0, nInd), asREP[3], str.substr(nInd+asREP[1].length, str.length-nInd-asREP[1].length));
	
		if (!bNogrsh) {
			var str2 = '';
			if (str.indexOf('&') != str.lastIndexOf('&')) {
				var nLast = str.lastIndexOf('&');
				str = str2.concat(str.substr(0, nLast), sQuot, str.substr(nLast));
			} else {
				str = str.concat(sGeresh);
			}
		}
		return str;
	},
	getHebNumerals: function(nType){
		if (nType == 'DIG')
			return D_DIG.split('|');
		else if (nType == 'TEN')
			return D_TEN.split('|');
		else if (nType == 'HUN')
			return D_HUN.split('|');
		else if (nType == 'REP')
			return D_REP.split('|');
		else
			return '';
	}
});
