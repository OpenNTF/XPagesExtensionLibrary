// Common globals (across pages)

var g_eventFeedUrl = null;
var g_inviteFeedUrl = null;
var g_userName = null;
var g_userEmail = null;

dojo.require("dojo.date.locale");


// Initialize the globals.
//
// This function makes an asynchronous request to get the server.
// The globals are not completely initialized until the server 
// responds.  Specify a callback if you want to be informed
// when everything is intialized.

function initCommonGlobals(callback) {
    var xhrArgs = {
        url : "/api/calendar",
        timeout : 60000,
        handleAs : "json",
        load : function(result) {
            console.log("Loading response from /api/calendar");
            if (result.calendars == null || result.calendars[0] == null) {
                return;
            }
        
            // Assumes the first calendar object is the authenticated
            // user's calendar.
            var calendar = result.calendars[0];
            g_userName = calendar.owner.distinguishedName;
            g_userEmail = calendar.owner.email;
            
            var links = calendar.links;
            for ( var i = 0; i < links.length; i++ ) {
                var link = links[i];
                if ( "events" == link.rel ) {
                    g_eventFeedUrl = link.href;
                }
                else if ( "invitations" == link.rel ) {
                    g_inviteFeedUrl = link.href;
                }
            }
            
            callback();
        }
    };
    
    console.log("Sending request to /api/calendar");
    dojo.xhrGet(xhrArgs);
}

function formatDate(date, useSlashes) {
    var dp = "yyyy-MM-dd";
    if ( useSlashes )
        dp = "yyyy/MM/dd";
    
    return dojo.date.locale.format(date, {datePattern: dp, selector: "date"});
}

function parseDate(dateString, useSlashes) {
    var dp = "yyyy-MM-dd";
    if ( useSlashes )
        dp = "yyyy/MM/dd";
    
    return dojo.date.locale.parse(dateString, {datePattern: dp, selector: "date"});
}
function formatTime(date, useSlashes) {
    var dp = "HH:mm:ss";   
    return dojo.date.locale.format(date, {timePattern: dp, selector: "time"});
}

function parseTime(dateString) {
    var dp = "HH:mm:ss";  
    return dojo.date.locale.parse(dateString, {timePattern: dp, selector: "time"});
}
