package com.ibm.xsp.teamroom.timezone;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import javax.faces.model.SelectItem;

public class TimeZoneUtil {
	private static String[][] codeToNonNLSLabel_unsorted = new String[][]{
		{ "Z=12$DO=0$ZX=154$ZN=Dateline",	 "(GMT-12:00) International Date Line West"},
		{ "Z=11$DO=1$DL=9 -1 7 1 1 6$ZX=203$ZN=Samoa",	 "(GMT-11:00) Samoa"},
		{ "Z=11$DO=0$ZX=219$ZN=UTC-11",	 "(GMT-11:00) Coordinated Universal Time-11"},
		{ "Z=10$DO=0$ZX=169$ZN=Hawaiian",	 "(GMT-10:00) Hawaii"},
		{ "Z=9$DO=1$DL=3 2 1 11 1 1$ZX=129$ZN=Alaskan",	 "(GMT-09:00) Alaska"},
		{ "Z=8$DO=1$DL=3 2 1 11 1 1$ZX=194$ZN=Pacific",	 "(GMT-08:00) Pacific Time (US & Canada)"},
		{ "Z=8$DO=1$DL=4 1 1 10 -1 1$ZX=195$ZN=Pacific Standard Time (Mexico)",	 "(GMT-08:00) Baja California"},
		{ "Z=7$DO=1$DL=4 1 1 10 -1 1$ZX=178$ZN=Mexico Standard Time 2",	 "(GMT-07:00) Chihuahua, La Paz, Mazatlan - Old"},
		{ "Z=7$DO=1$DL=3 2 1 11 1 1$ZX=183$ZN=Mountain",	 "(GMT-07:00) Mountain Time (US & Canada)"},
		{ "Z=7$DO=1$DL=4 1 1 10 -1 1$ZX=184$ZN=Mountain Standard Time (Mexico)",	 "(GMT-07:00) Chihuahua, La Paz, Mazatlan - New"},
		{ "Z=7$DO=0$ZX=215$ZN=US Mountain",	 "(GMT-07:00) Arizona"},
		{ "Z=6$DO=0$ZX=141$ZN=Canada Central",	 "(GMT-06:00) Saskatchewan"},
		{ "Z=6$DO=0$ZX=145$ZN=Central America",	 "(GMT-06:00) Central America"},
		{ "Z=6$DO=1$DL=3 2 1 11 1 1$ZX=151$ZN=Central",	 "(GMT-06:00) Central Time (US & Canada)"},
		{ "Z=6$DO=1$DL=4 1 1 10 -1 1$ZX=152$ZN=Central Standard Time (Mexico)",	 "(GMT-06:00) Guadalajara, Mexico City, Monterrey - New"},
		{ "Z=6$DO=1$DL=4 1 1 10 -1 1$ZX=177$ZN=Mexico",	 "(GMT-06:00) Guadalajara, Mexico City, Monterrey - Old"},
		{ "Z=5$DO=1$DL=3 2 1 11 1 1$ZX=159$ZN=Eastern",	 "(GMT-05:00) Eastern Time (US & Canada)"},
		{ "Z=5$DO=0$ZX=201$ZN=SA Pacific",	 "(GMT-05:00) Bogota, Lima, Quito"},
		{ "Z=5$DO=1$DL=3 2 1 11 1 1$ZX=214$ZN=US Eastern",	 "(GMT-05:00) Indiana (East)"},
		{ "Z=3004$DO=0$ZX=220$ZN=Venezuela",	 "(GMT-04:30) Caracas"},
		{ "Z=4$DO=1$DL=3 2 1 11 1 1$ZX=135$ZN=Atlantic",	 "(GMT-04:00) Atlantic Time (Canada)"},
		{ "Z=4$DO=1$DL=10 3 7 2 3 7$ZX=147$ZN=Central Brazilian",	 "(GMT-04:00) Cuiaba"},
		{ "Z=4$DO=1$DL=10 2 7 4 1 7$ZX=193$ZN=Pacific SA",	 "(GMT-04:00) Santiago"},
		{ "Z=4$DO=1$DL=10 1 7 4 2 7$ZX=197$ZN=Paraguay",	 "(GMT-04:00) Asuncion"},
		{ "Z=4$DO=0$ZX=202$ZN=SA Western",	 "(GMT-04:00) Georgetown, La Paz, Manaus, San Juan"},
		{ "Z=3003$DO=1$DL=3 2 1 11 1 1$ZX=190$ZN=Newfoundland",	 "(GMT-03:30) Newfoundland"},
		{ "Z=3$DO=0$ZX=133$ZN=Argentina",	 "(GMT-03:00) Buenos Aires"},
		{ "Z=3$DO=1$DL=10 3 7 2 3 7$ZX=158$ZN=E. South America",	 "(GMT-03:00) Brasilia"},
		{ "Z=3$DO=1$DL=3 -1 7 10 -1 7$ZX=166$ZN=Greenland",	 "(GMT-03:00) Greenland"},
		{ "Z=3$DO=1$DL=10 1 1 3 2 1$ZX=181$ZN=Montevideo",	 "(GMT-03:00) Montevideo"},
		{ "Z=3$DO=0$ZX=200$ZN=SA Eastern",	 "(GMT-03:00) Cayenne, Fortaleza"},
		{ "Z=2$DO=1$DL=3 -1 1 9 -1 1$ZX=179$ZN=Mid-Atlantic",	 "(GMT-02:00) Mid-Atlantic"},
		{ "Z=2$DO=0$ZX=218$ZN=UTC-02",	 "(GMT-02:00) Coordinated Universal Time-02"},
		{ "Z=1$DO=1$DL=3 -1 1 10 -1 1$ZX=139$ZN=Azores",	 "(GMT-01:00) Azores"},
		{ "Z=1$DO=0$ZX=142$ZN=Cape Verde",	 "(GMT-01:00) Cape Verde Is."},
		{ "Z=0$DO=1$DL=3 -1 1 10 -1 1$ZX=165$ZN=GMT",	 "(GMT) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London"},
		{ "Z=0$DO=0$ZX=167$ZN=Greenwich",	 "(GMT) Monrovia, Reykjavik"},
		{ "Z=0$DO=1$DL=5 1 7 8 1 7$ZX=182$ZN=Morocco",	 "(GMT) Casablanca"},
		{ "Z=0$DO=0$ZX=216$ZN=UTC",	 "(GMT) Coordinated Universal Time"},
		{ "Z=-1$DO=1$DL=3 -1 1 10 -1 1$ZX=148$ZN=Central Europe",	 "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"},
		{ "Z=-1$DO=1$DL=3 -1 1 10 -1 1$ZX=149$ZN=Central European",	 "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb"},
		{ "Z=-1$DO=1$DL=3 -1 1 10 -1 1$ZX=198$ZN=Romance",	 "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris"},
		{ "Z=-1$DO=0$ZX=223$ZN=W. Central Africa",	 "(GMT+01:00) West Central Africa"},
		{ "Z=-1$DO=1$DL=3 -1 1 10 -1 1$ZX=224$ZN=W. Europe",	 "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"},
		{ "Z=-2$DO=1$DL=3 -1 1 10 -1 1$ZX=157$ZN=E. Europe",	 "(GMT+02:00) Minsk"},
		{ "Z=-2$DO=1$DL=4 -1 5 9 -1 5$ZX=160$ZN=Egypt",	 "(GMT+02:00) Cairo"},
		{ "Z=-2$DO=1$DL=3 -1 1 10 -1 1$ZX=163$ZN=FLE",	 "(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius"},
		{ "Z=-2$DO=1$DL=3 -1 1 10 -1 1$ZX=168$ZN=GTB",	 "(GMT+02:00) Athens, Bucharest, Istanbul"},
		{ "Z=-2$DO=1$DL=3 -1 6 9 2 1$ZX=172$ZN=Israel",	 "(GMT+02:00) Jerusalem"},
		{ "Z=-2$DO=1$DL=3 -1 5 10 -1 6$ZX=173$ZN=Jordan",	 "(GMT+02:00) Amman"},
		{ "Z=-2$DO=1$DL=3 -1 7 10 -1 7$ZX=180$ZN=Middle East",	 "(GMT+02:00) Beirut"},
		{ "Z=-2$DO=1$DL=4 1 1 9 1 1$ZX=187$ZN=Namibia",	 "(GMT+01:00) Windhoek"},
		{ "Z=-2$DO=0$ZX=206$ZN=South Africa",	 "(GMT+02:00) Harare, Pretoria"},
		{ "Z=-2$DO=1$DL=4 1 5 10 -1 5$ZX=208$ZN=Syria",	 "(GMT+02:00) Damascus"},
		{ "Z=-3$DO=0$ZX=130$ZN=Arab",	 "(GMT+03:00) Kuwait, Riyadh"},
		{ "Z=-3$DO=0$ZX=132$ZN=Arabic",	 "(GMT+03:00) Baghdad"},
		{ "Z=-3$DO=0$ZX=155$ZN=E. Africa",	 "(GMT+03:00) Nairobi"},
		{ "Z=-3$DO=1$DL=3 -1 1 10 -1 1$ZX=199$ZN=Russian",	 "(GMT+03:00) Moscow, St. Petersburg, Volgograd"},
		{ "Z=-3003$DO=1$DL=3 3 7 9 3 2$ZX=171$ZN=Iran",	 "(GMT+03:30) Tehran"},
		{ "Z=-4$DO=0$ZX=131$ZN=Arabian",	 "(GMT+04:00) Abu Dhabi, Muscat"},
		{ "Z=-4$DO=1$DL=3 -1 1 10 -1 1$ZX=134$ZN=Armenian",	 "(GMT+04:00) Yerevan"},
		{ "Z=-4$DO=1$DL=3 -1 1 10 -1 1$ZX=138$ZN=Azerbaijan",	 "(GMT+04:00) Baku"},
		{ "Z=-4$DO=0$ZX=143$ZN=Caucasus",	 "(GMT+04:00) Caucasus Standard Time"},
		{ "Z=-4$DO=0$ZX=164$ZN=Georgian",	 "(GMT+04:00) Tbilisi"},
		{ "Z=-4$DO=0$ZX=176$ZN=Mauritius",	 "(GMT+04:00) Port Louis"},
		{ "Z=-3004$DO=0$ZX=128$ZN=Afghanistan",	 "(GMT+04:30) Kabul"},
		{ "Z=-5$DO=1$DL=3 -1 1 10 -1 1$ZX=161$ZN=Ekaterinburg",	 "(GMT+05:00) Ekaterinburg"},
		{ "Z=-5$DO=0$ZX=196$ZN=Pakistan",	 "(GMT+05:00) Islamabad, Karachi"},
		{ "Z=-5$DO=0$ZX=225$ZN=West Asia",	 "(GMT+05:00) Tashkent"},
		{ "Z=-3005$DO=0$ZX=170$ZN=India",	 "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi"},
		{ "Z=-3005$DO=0$ZX=207$ZN=Sri Lanka",	 "(GMT+05:30) Sri Jayawardenepura"},
		{ "Z=-4505$DO=0$ZX=188$ZN=Nepal",	 "(GMT+05:45) Kathmandu"},
		{ "Z=-6$DO=0$ZX=140$ZN=Bangladesh",	 "(GMT+06:00) Dhaka"},
		{ "Z=-6$DO=0$ZX=146$ZN=Central Asia",	 "(GMT+06:00) Astana"},
		{ "Z=-6$DO=1$DL=3 -1 1 10 -1 1$ZX=186$ZN=N. Central Asia",	 "(GMT+06:00) Novosibirsk"},
		{ "Z=-3006$DO=0$ZX=185$ZN=Myanmar",	 "(GMT+06:30) Yangon (Rangoon)"},
		{ "Z=-7$DO=1$DL=3 -1 1 10 -1 1$ZX=192$ZN=North Asia",	 "(GMT+07:00) Krasnoyarsk"},
		{ "Z=-7$DO=0$ZX=204$ZN=SE Asia",	 "(GMT+07:00) Bangkok, Hanoi, Jakarta"},
		{ "Z=-8$DO=0$ZX=153$ZN=China",	 "(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi"},
		{ "Z=-8$DO=1$DL=3 -1 1 10 -1 1$ZX=191$ZN=North Asia East",	 "(GMT+08:00) Irkutsk"},
		{ "Z=-8$DO=0$ZX=205$ZN=Singapore",	 "(GMT+08:00) Kuala Lumpur, Singapore"},
		{ "Z=-8$DO=0$ZX=209$ZN=Taipei",	 "(GMT+08:00) Taipei"},
		{ "Z=-8$DO=0$ZX=213$ZN=Ulaanbaatar",	 "(GMT+08:00) Ulaanbaatar"},
		{ "Z=-8$DO=0$ZX=222$ZN=W. Australia",	 "(GMT+08:00) Perth"},
		{ "Z=-9$DO=0$ZX=175$ZN=Korea",	 "(GMT+09:00) Seoul"},
		{ "Z=-9$DO=0$ZX=211$ZN=Tokyo",	 "(GMT+09:00) Osaka, Sapporo, Tokyo"},
		{ "Z=-9$DO=1$DL=3 -1 1 10 -1 1$ZX=227$ZN=Yakutsk",	 "(GMT+09:00) Yakutsk"},
		{ "Z=-3009$DO=0$ZX=136$ZN=AUS Central",	 "(GMT+09:30) Darwin"},
		{ "Z=-3009$DO=1$DL=10 1 1 4 1 1$ZX=144$ZN=Cen. Australia",	 "(GMT+09:30) Adelaide"},
		{ "Z=-10$DO=1$DL=10 1 1 4 1 1$ZX=137$ZN=AUS Eastern",	 "(GMT+10:00) Canberra, Melbourne, Sydney"},
		{ "Z=-10$DO=0$ZX=156$ZN=E. Australia",	 "(GMT+10:00) Brisbane"},
		{ "Z=-10$DO=1$DL=10 1 1 4 1 1$ZX=210$ZN=Tasmania",	 "(GMT+10:00) Hobart"},
		{ "Z=-10$DO=1$DL=3 -1 1 10 -1 1$ZX=221$ZN=Vladivostok",	 "(GMT+10:00) Vladivostok"},
		{ "Z=-10$DO=0$ZX=226$ZN=West Pacific",	 "(GMT+10:00) Guam, Port Moresby"},
		{ "Z=-11$DO=0$ZX=150$ZN=Central Pacific",	 "(GMT+11:00) Magadan, Solomon Is., New Caledonia"},
		{ "Z=-12$DO=1$DL=10 4 1 3 -1 1$ZX=162$ZN=Fiji",	 "(GMT+12:00) Fiji"},
		{ "Z=-12$DO=1$DL=3 -1 1 10 -1 1$ZX=174$ZN=Kamchatka",	 "(GMT+12:00) Petropavlovsk-Kamchatsky - Old"},
		{ "Z=-12$DO=1$DL=9 -1 1 4 1 1$ZX=189$ZN=New Zealand",	 "(GMT+12:00) Auckland, Wellington"},
		{ "Z=-12$DO=0$ZX=217$ZN=UTC+12",	 "(GMT+12:00) Coordinated Universal Time+12"},
		{ "Z=-13$DO=0$ZX=212$ZN=Tonga",	 "(GMT+13:00) Nuku'alofa"},
	};

	private static String[][] codeToNonNLSLabel_sortByCode;
	
	private static final Comparator<Object> CODE_ARRAY_COMPARATOR = new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			String s1 = toCodeString(o1);
			String s2 = toCodeString(o2);
			return s1.compareTo(s2);
		}
	};
	private static final Comparator<Object> CODE_IGNORE_ZX_COMPARATOR = new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			String s1 = toCodeString(o1);
			String s2 = toCodeString(o2);
			s1 = removeZX(s1);
			s2 = removeZX(s2);
			return s1.compareTo(s2);
		}
	};
	
	private static final Comparator<Object> CODE_IGNORE_OPTIONAL_COMPARATOR = new Comparator<Object>(){
		public int compare(Object o1, Object o2) {
			String s1 = toCodeString(o1);
			String s2 = toCodeString(o2);
			s1 = removeOptional(s1);
			s2 = removeOptional(s2);
			return s1.compareTo(s2);
		}
	};
	public static String[][] getCodeToLabel(Locale labelTranslateLocale){
		return codeToNonNLSLabel_unsorted;
	}
	private static String[][] byCode(){
		if( null == codeToNonNLSLabel_sortByCode){
			String[][] copy = Arrays.copyOf(codeToNonNLSLabel_unsorted, codeToNonNLSLabel_unsorted.length);
			Arrays.sort(copy, CODE_ARRAY_COMPARATOR);
			codeToNonNLSLabel_sortByCode = copy;
		}
		return codeToNonNLSLabel_sortByCode;
	}
	public static SelectItem[] getComboOptions(Locale labelTranslateLocale){
		String[][] codeToLabel = getCodeToLabel(labelTranslateLocale);
		SelectItem[] items = new SelectItem[codeToLabel.length];
		int i = 0;
		for (String[] codeAndLabel : codeToLabel) {
			items[i] = new SelectItem(codeAndLabel[0], codeAndLabel[1]);
			i++;
		}
		return items;
	}

	/**
	 * Note, the indexes depend on the locale, because the index returned is the
	 * index in array sorted by the translated labels.
	 * 
	 * @param code
	 * @param labelTranslateLocale
	 * @return
	 */
	public static boolean containsCode(String code){
		int indexInCodeArr = Arrays.binarySearch(byCode(), code, CODE_ARRAY_COMPARATOR);
		return indexInCodeArr >= 0;
	}
	public static String label(String code, Locale labelTranslateLocale){
		code = resetOrdering(code);
		return findLabel(code, CODE_ARRAY_COMPARATOR);
	}
	private static String findLabel(String code, Comparator<Object> comparator) {
		String[][] byCode = byCode();
		int indexInCodeArr = Arrays.binarySearch(byCode, code, comparator);
		if( indexInCodeArr < 0 ){
			return null;
		}
		String[] codeAndLabel = byCode[indexInCodeArr];
		return codeAndLabel[1];
	}
	public static String labelAllowImperfectMatch(String code, Locale labelTranslateLocale){
		// It would be best it this is not used,
		// as there are multiple TimeZones with the same
		// optional values but which have different names.
		// It is present so it is possible to preserve 
		// some of the existing behavior.
		code = resetOrdering(code);
		code = removeZX(code);
		return findLabel(code, CODE_IGNORE_ZX_COMPARATOR);
	}
	public static String labelIgnoreOptional(String code, Locale labelTranslateLocale){
		// It would be best it this is not used,
		// as there are multiple TimeZones with the same
		// optional values but which have different names.
		// It is present so it is possible to preserve 
		// some of the existing behavior.
		code = removeOptional(code);
		return findLabel(code, CODE_IGNORE_OPTIONAL_COMPARATOR);
	}
	public static String getOffsetOnly(String code){
		// Z=5$DO=1$DL=3 2 1 11 1 1$ZN=US Eastern$ZX=214
		String z = code.substring(2, code.indexOf('$') );
		if( "0".equals(z)){
			return "GMT";
		}
		if( z.charAt(0) == '-' ){
			z = z.substring(1);
			return "GMT+"+z;
		}
		return "GMT-"+z;
	}
	private static String removeOptional(String code) {
		// remove trailing ZX and ZN
		int index = code.lastIndexOf("$ZX=");
		if( -1 != index ){
			code = code.substring(0, index);
		}
		index = code.lastIndexOf("$ZN");
		if( -1 != index ){
			code = code.substring(0, index);
		}
		return code;
	}
	private static String removeZX(String code) {
		// remove trailing ZX and ZN
		int index = code.lastIndexOf("$ZX=");
		if( -1 != index ){
			int endZXIndex = code.indexOf('$', index+1);
			if( -1 == endZXIndex ){
				return code.substring(0, index);
			}
			return code.substring(0, index)+code.substring(endZXIndex);
		}
		return code;
	}
	public static String containsCodeIgnoreOrdering(String code){
		code = resetOrdering(code);
		if( containsCode(code) ){
			// return the updated code
			return code;
		}
		return null;
	}
	private static String resetOrdering(String code) {
		// convert the optional parts to the ordering above,
		// with ZX before ZN, needed because the Web classic
		// TimeZone picker was using the wrong ordering.
		int ZNindex = code.indexOf("ZN=");
		int ZXindex = code.indexOf("ZX=");
		if( -1 != ZNindex && -1 != ZXindex && ZXindex > ZNindex ){
			String ZX = code.substring(ZXindex);
			String ZN = code.substring(ZNindex, ZXindex - 1);
			code = code.substring(0, ZNindex)+ZX+"$"+ZN;
		}
		return code;
	}

	private static String toCodeString(Object o1) {
		if( o1 instanceof String[] ){
			o1 = ((String[])o1)[0];
		}
		String s1 = (String)o1;
		return s1;
	}
}
