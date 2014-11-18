/** Author: Tony McGuckin, IBM **/

DISPLAY_ALL_DOCUMENTS		= 0;
DISPLAY_BY_MOST_RECENT 		= 1;
DISPLAY_BY_AUTHOR			= 2;
DISPLAY_BY_TAG				= 3;
DISPLAY_MY_DOCUMENTS		= 4;
DISPLAY_AUTHOR_PROFILE 		= 5;
DISPLAY_TOPIC_THREAD 		= 6;

DEFAULT_ROW_COUNT			= 25;

//-----------

function init() {
	// Needs to be done only ONCE, so if we're already initialized, get out.	
	if (applicationScope.initCustomBranding === true) {
		return;
	}

	var COLUMN_IMAGENAME	= 0;
	var COLUMN_IMAGETEXT	= 1;
	var COLUMN_FOOTERTEXT	= 2;
				
	try {
		// get the first entry in the view
		var db:NotesDatabase				= database;
		var view:NotesView					= db.getView("xpConfigProfile");
		var vecoll:NotesViewEntryCollection	= view.getAllEntries();
		var entry:NotesViewEntry			= vecoll.getFirstEntry();

		if (entry != null) {
			// skim the entry data off the view columns
			var cols		= entry.getColumnValues();
			var sImageName	= cols[COLUMN_IMAGENAME];
			var sImageText	= cols[COLUMN_IMAGETEXT];
			var sFooterText	= cols[COLUMN_FOOTERTEXT];
			var sImageURL	= (sImageName.length() > 0) ? (entry.getUniversalID() + "/$file/" + sImageName) : null;
			
			// and store them as properties in the custom control
			applicationScope.customImageURL		= sImageURL;
			applicationScope.customImageText	= sImageText;			
			applicationScope.customFooterText	= sFooterText;
		}
		// remember that we're initialized
		applicationScope.initCustomBranding = true;
	} catch (e) {
		print (e);
	}
}

//---------------------

function setPageHistory(pageTitle, pageName) {
	// remember the current page name and title for the "back to XZY" link
	sessionScope.historyPageName	= pageName;
	sessionScope.historyPageTitle	= pageTitle;
}

//---------------------

function setDisplayFormType(displayFormType) {
	// new topic or response? possible values 1 || 2
	sessionScope.displayFormType = displayFormType;
}

//---------------------

function getDisplayFormType(){
	// return the curent form type 1==new topic, 2==response
	return sessionScope.displayFormType;
}
