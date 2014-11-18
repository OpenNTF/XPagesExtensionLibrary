//---------------------
function getCurrentTimeZone() {
//---------------------
	var scope = applicationScope;
	if( scope.xpTimeZone_currentTimeZone ){
		return scope.xpTimeZone_currentTimeZone;
	}
	var currentTimeZone = session.evaluate("@GetCurrentTimeZone");
	if( currentTimeZone && !currentTimeZone.isEmpty() && currentTimeZone.get(0) ){
		currentTimeZone = currentTimeZone.get(0);
	}else{
		currentTimeZone = "";
	}
	scope.xpTimeZone_currentTimeZone = currentTimeZone;
	return currentTimeZone;
} // end getCurrentTimeZone

//---------------------
function xpTimeZone_TimeMerge_values(date, time, timeZone) {
//---------------------
	if( ! date || ! time || !timeZone 
		|| date.isEmpty() 
		|| !date.get(0)
		|| time.isEmpty() 
		|| !time.get(0)
		|| timeZone.isEmpty() 
		|| !timeZone.get(0) ){
		return "";
	}
	var doc:NotesDocument = database.getProfileDocument("TempVars","");
	doc.replaceItemValue("MtgDate", date);
	doc.replaceItemValue("MtgTime", time);
	doc.replaceItemValue("StartTImeZone", timeZone);
	var merged = _inner_TimeMerge(doc);
	doc.removeItem('MtgDate');
	doc.removeItem('MtgTime');
	doc.removeItem('StartTImeZone');
	return merged;
} // end xpTimeZone_TimeMerge

//---------------------
function xpTimeZone_TimeMerge_doc(doc) {
//---------------------
	var merged = _inner_TimeMerge(doc);
	return merged;
} // end xpTimeZone_TimeMerge

function _inner_TimeMerge(doc) {
//---------------------
	var merged = session.evaluate(
		"@TimeMerge("
			+"@Date(@Year(MtgDate); @Month(MtgDate); @Day(MtgDate));"
			+"@Time(@Hour(MtgTime); @Minute(MtgTime); @Second(MtgTime)); "
			+"StartTImeZone)", doc);
	return merged;
} // end TimeMerge
