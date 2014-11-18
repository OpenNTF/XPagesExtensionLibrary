/**
 This server side script library is used by the "authorProfile" custom control.
 It obtains information about the author from the authors profile document (if found)
 and the number of documents authored. 
 
 The custom control just needs the FULLY QUALIFIED name of the person
 like "Thomas Gumz/Westford/IBM" or "CN=Thomas Gumz/OU=Westford/O=IBM"

 The name can be supplied to the control either by:
 - setting the "lookupName" property, or
 - including the url argument "&lookupName=" in the URL
 (both the URL argument and property name is defined in KEYWORD_TOKEN below)
*/

var IMG_PLACEHOLDER		= "xpPhotoPlaceholder.gif"
var VIEW_PROFILES		= "xpAuthorProfiles";
var VIEW_POSTS			= "xpAuthorPosts";

var COLUMN_FROM			= 0;
var COLUMN_CREATED		= 1;
var COLUMN_EMAIL		= 2;
var COLUMN_PHONE		= 3;
var COLUMN_ROLE			= 4;
var COLUMN_PHOTO		= 5;
var COLUMN_GOAL			= 6;

var KEYWORD_TOKEN		= "lookupName";

var FILTER_MAIN			= ":Main";
var FILTER_RESPONSES	= ":Resp";


function initAuthorProfile() {
	
	var sLookupName:string = null;

	// check if the name is supplied via a property	
	sLookupName = compositeData.getProperty(KEYWORD_TOKEN);

	// if not, check for the URL argument
	if (sLookupName == null) {
		if (param.containsKey(KEYWORD_TOKEN)) {
			sLookupName = param.get(KEYWORD_TOKEN);
		}
	}

	if (sLookupName != null) {
		
		var nNotesName:NotesName 				= session.createName(sLookupName);
		var sCanonicalName:String				= nNotesName.getCanonical();
								
		compositeData.nameAbbreviated			= nNotesName.getAbbreviated();								
		compositeData.categoryFilterMain		= sCanonicalName + FILTER_MAIN;
		compositeData.categoryFilterResponses	= sCanonicalName + FILTER_RESPONSES;
											
		getUserProfile(nNotesName);
		getUserStats(nNotesName);

	} else {
		compositeData.profileFound = false;
	}
		
}

function getUserProfile(name:NotesName) {

	// get the user profile information
	
	var db:NotesDatabase		= database;
	var view:NotesView			= db.getView(VIEW_PROFILES);
	view.setAutoUpdate(false);
	var entry:NotesViewEntry	= view.getEntryByKey(name.getCanonical(), true);

	if (entry != null) {

		var cols = entry.getColumnValues();
				
		compositeData.name				= name.getCommon();
		compositeData.nameAbbreviated	= name.getAbbreviated();
		compositeData.photoURL			= getPhotoURL(cols[COLUMN_PHOTO]);
		compositeData.created			= cols[COLUMN_CREATED];
		compositeData.email				= cols[COLUMN_EMAIL];
		compositeData.phone				= cols[COLUMN_PHONE];
		compositeData.role				= cols[COLUMN_ROLE];
		compositeData.goal				= cols[COLUMN_GOAL];
		compositeData.profileFound		= true;
	} else {
		compositeData.profileFound		= false;
	}
}


function getUserStats(name:NotesName) {

	// get the total number of main posts and response posts for the author
		
	var db:NotesDatabase		= database;
	var view:NotesView			= db.getView(VIEW_POSTS);
	view.setAutoUpdate(false);
	var nav:NotesViewNavigator;
		
	nav = view.createViewNavFromCategory(compositeData.categoryFilterMain);
	if (nav != null) {compositeData.postsMain = nav.getCount()}
		
	nav = view.createViewNavFromCategory(compositeData.categoryFilterResponses);
	if (nav != null) {compositeData.postsResponses = nav.getCount()}
}


function getPhotoURL(sFileName:string):string {

	// build the URL for the photo (or the placeholder)
	/*
	var imageName:String = IMG_PLACEHOLDER;
	if(!profileDoc.isNewNote()){
		var al:java.util.List = profileDoc.getAttachmentList("attachment");
		if(!al.isEmpty()){
			var eo:NotesEmbeddedObject = al.get(0);
			imageName = eo.getHref();
		}
	}
	return(imageName);
	*/
	
	var sPhotoURL:string;

	if (sFileName.length() > 0) {
		sPhotoURL = entry.getUniversalID() + "/$file/" + sFileName;
	} else {
		sPhotoURL = IMG_PLACEHOLDER;
	}
	return sPhotoURL;
}




