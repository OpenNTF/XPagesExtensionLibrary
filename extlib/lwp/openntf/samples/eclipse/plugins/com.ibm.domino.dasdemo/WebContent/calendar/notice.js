// Page globals

var g_noticeId = null; // This is the ID specified in the notice.html URL

// Parse the event ID

var searchString = document.location.search;

searchString = searchString.substring(1); //strip off the leading '?'

var nvPairs = searchString.split("&");

for (i = 0; i < nvPairs.length; i++) {
    var nvPair = nvPairs[i].split("=");
    var name = nvPair[0];
    var value = nvPair[1];

    if (name == "id") {
        g_noticeId = value;
    }
}

// Script to execute when the page has loaded

dojo.ready(function() {
    initCommonGlobals(function(){
        var header = document.getElementById("header");
        header.innerHTML = g_userName;
        
        setFields(g_noticeId);
    });
});

// ----- Start of Functions -----

function setFields(id) {
    
    // TODO: Don't assume we know the notice URL format.
    // Get it from the service when that is supported.
    
    var noticeUrl = g_eventFeedUrl + "/" + id;
    noticeUrl = noticeUrl.replace("events", "notices");
    
    var xhrArgs = {
        url : noticeUrl,
        timeout : 10000,
        handleAs : "json",
        load : function(result) {
            if (result.events == null || result.events[0] == null) {
                return;
            }

            var event = result.events[0];
            var widget;

            g_actualId = event.id;

            if (event.summary != null) {
                widget = dijit.byId("summary");
                widget.setValue(event.summary);
            }

            if (event.location != null) {
                widget = dijit.byId("location");
                widget.setValue(event.location);
            }

            if (event.start != null) {
                widget = dijit.byId("startDate");				
                widget.setValue(parseDate(event.start.date,false));
                widget = dijit.byId("startTime");
                widget.setValue(parseTime(event.start.time));
            }

            if (event.end != null) {
                widget = dijit.byId("endDate");
                widget.setValue(parseDate(event.end.date,false));
                widget = dijit.byId("endTime");
                widget.setValue(parseTime(event.end.time));
            }
			
			if (event.recurrenceRule != null){
				var rule = event.recurrenceRule;
				if (rule.indexOf("DAILY") != -1) {
					widget = dijit.byId("daily");
					widget.set("checked",true);
				}
				else if (rule.indexOf("WEEKLY") != -1) {
					widget = dijit.byId("weekly");
					widget.set("checked",true);
				}
				else if (rule.indexOf("MONTHLY") != -1) {
					widget = dijit.byId("monthly");
					widget.set("checked",true);
				}
				var countstr = "COUNT=";
				var inx = rule.indexOf(countstr);
				if (inx != -1) {
					widget = dijit.byId("count");
					widget.setValue (rule.substring(inx+countstr.length,rule.length));
				}					
			}
			else {
				widget = dijit.byId("none");
				widget.set("checked",true);
			}

            if (event.description != null) {
                widget = dijit.byId("description");
                widget.setValue(event.description);
            }
            
            var showParticipantActions = false;
            if (event.organizer == null ) {
                removeElementById("organizerDiv");
                removeElementById("attendeesDiv");
            }
            else {
                var organizerName = event.organizer.displayName;
                
                // Make the divs visible
                var section = document.getElementById("organizerDiv");
                section.style.visibility = "visible";
                section = document.getElementById("attendeesDiv");
                section.style.visibility = "visible";
                
                // Set organizer 
                widget = dijit.byId("organizer");
                widget.setValue(organizerName);
                
                // Set attendees
                if ( event.attendees != null ) {
                    var attendeeNames = "";
                    var i = 0;
                    for ( i = 0; i < event.attendees.length; i++ ) {
                        if ( event.attendees[i].displayName == organizerName ) {
                            continue;
                        }
                        
                        if ( attendeeNames.length > 0 ) {
                            attendeeNames += ", ";
                        }
                        
                        attendeeNames += event.attendees[i].email;
                    }

                    widget = dijit.byId("attendees");
                    widget.setValue(attendeeNames);
                }
            }
        }
    };

    if (id == null) {
        alert("A valid notice ID is required.");
        removeWidgetById("accept");
        removeWidgetById("decline");
        removeWidgetById("delegate");
        removeWidgetById("counter");
    } else {
        dojo.xhrGet(xhrArgs);
    }
}

function accept(id) {

    var actionUrl = g_eventFeedUrl + "/" + id + "/action?type=accept";
    actionUrl = actionUrl.replace("events", "notices");
    
    var xhrArgs = {
        url : actionUrl,
        load : function(data) {
            document.location = "calendar.html";
        },
        error : function(error) {
            alert(error);
        }
    };

    dojo.xhrPut(xhrArgs);
}

function decline(id) {

    var actionUrl = g_eventFeedUrl + "/" + id + "/action?type=decline";
    actionUrl = actionUrl.replace("events", "notices");
    
    var xhrArgs = {
        url : actionUrl,
        load : function(data) {
            document.location = "calendar.html";
        },
        error : function(error) {
            alert(error);
        }
    };

    dojo.xhrPut(xhrArgs);
}

function delegate(id, delegateTo) {

    var actionUrl = g_eventFeedUrl + "/" + id + "/action?type=delegate";
    actionUrl = actionUrl.replace("events", "notices");
    
    var request = {};
    request.delegateTo = delegateTo;

    var xhrArgs = {
        url : actionUrl,
        headers : {
            "Content-Type" : "application/json"
        },
        postData : dojo.toJson(request),
        load : function(data) {
            document.location = "calendar.html";
        },
        error : function(error) {
            alert(error);
        }
    };

    dojo.xhrPut(xhrArgs);
}

function counter(id, counterStartDate, counterStartTime,counterEndDate,counterEndTime) {

    var actionUrl = g_eventFeedUrl + "/" + id + "/action?type=counter";
    actionUrl = actionUrl.replace("events", "notices");
    
    var request = {};
    request.counterStart = formatDate(counterStartDate)+"T"+formatTime(counterStartTime)+"Z";
    request.counterEnd = formatDate(counterEndDate)+"T"+formatTime(counterEndTime)+"Z";;

    var xhrArgs = {
        url : actionUrl,
        headers : {
            "Content-Type" : "application/json"
        },
        postData : dojo.toJson(request),
        load : function(data) {
            document.location = "calendar.html";
        },
        error : function(error) {
            alert(error);
        }
    };

    dojo.xhrPut(xhrArgs);
}

// TODO:  Move these methods to common.js

function removeWidgetById(id) {
    var widget = dijit.byId(id);
    widget.destroy();
}

function removeElementById(id) {
    var element = document.getElementById(id);
    element.parentNode.removeChild(element);
}

function trim(stringToTrim) {
    return stringToTrim.replace(/^\s+|\s+$/g,"");
}


