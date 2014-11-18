// Page globals

var g_calType = "M";
var g_calDate = new Date();

// Parse the URL parameters

var searchString = document.location.search;

searchString = searchString.substring(1); //strip off the leading '?'

var nvPairs = searchString.split("&");

for (i = 0; i < nvPairs.length; i++) {
    var nvPair = nvPairs[i].split("=");
    var name = nvPair[0];
    var value = nvPair[1];

    if (name == "type") {
        g_calType = value;
    }
    else if (name == "date") {
        var date = parseDate(value, false);
        if ( date != null )
            g_calDate = date;
    }
}

// Script to execute when the page has loaded

dojo.ready(function(){
    initCommonGlobals(function(){

        console.log("Initializing the calendar store");
		
        var store = new dwa.data.iCalReadStore({
            url: g_eventFeedUrl + "?format=icalendar",
            fontColorMeeting: '000000',
            bgColorMeeting: 'ADDFFF',
            borderColorMeeting: '5495D5'
        });
		
		store.getUrl = function(request){
			var dp = "yyyyMMdd";
			var since = formatDate(dojo.date.locale.parse(request.query.startDate, {datePattern: dp, selector: "date"}) ) + "T00:00:00Z";
			var before = formatDate(dojo.date.locale.parse(request.query.endDate, {datePattern: dp, selector: "date"}) )+ "T00:00:00Z";
			var url = this.url + "&since="+ since + "&before=" + before;
			return url;
		};
        
        var dateString = formatDate(g_calDate, true);

        var widget = new dwa.cv.calendarView({
                type:g_calType,
                summarize:false,
                date:dateString,
                store:[store]
            }, dojo.doc.getElementById("cv"));

        widget.drawCalendar(g_calType, false);
        dojo.connect(widget, "openEntryAction", openEntryAction);

        var header = document.getElementById("header");
        header.innerHTML = g_userName;
        
        // Get new invitations
        getNewInvitations();
    });
});

//----- Start of Functions -----

function setFormat(fmt) {
    var widget = dijit.byId("cv");
    widget.drawCalendar(fmt, false);
}

function openEntryAction(items){
    //alert("openEntryAction! "+items[0].unid);
    document.location = "event.html?id=" + items[0].unid;
}

function newInvitationsCallback(result) {
    
    var html = "";

    if (result == null || result.notices == null || result.notices.length == 0) {
        html = "<i>No invitations found</i>";
    }
    else {
        var notices = result.notices;
        for ( var i = 0; i < notices.length; i++ ) {
            var notice = notices[i];
            
            var text = notice.summary;
            var lastSlash = notice.href.lastIndexOf("/");
            var id = notice.href.substring(lastSlash+1);
    
            html += "<a href=\"notice.html?id=" + id + "\">" + text + "</a><br>";
        }
    }
    
    var section = document.getElementById("miniView");
    section.innerHTML = html;
}

function getNewInvitations() {
    var xhrArgs = {
        url : g_inviteFeedUrl,
        timeout : 60000,
        handleAs : "json",
        load : newInvitationsCallback
    };

    dojo.xhrGet(xhrArgs);
}




