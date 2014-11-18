// Page globals

var g_eventId = null; // This is the ID specified in the event.html URL
var g_actualId = null; // This is the UID retrieved from the REST service

// Parse the event ID

var searchString = document.location.search;

searchString = searchString.substring(1); //strip off the leading '?'

var nvPairs = searchString.split("&");

for (i = 0; i < nvPairs.length; i++) {
    var nvPair = nvPairs[i].split("=");
    var name = nvPair[0];
    var value = nvPair[1];

    if (name == "id") {
        g_eventId = value;
    }
}

// Script to execute when the page has loaded

dojo.ready(function() {
    initCommonGlobals(function(){
        var header = document.getElementById("header");
        header.innerHTML = g_userName;
        
        setFields(g_eventId);
    });
});

// ----- Start of Functions -----

function setFields(id) {
    var xhrArgs = {
        url : g_eventFeedUrl + "/" + id,
        timeout : 60000,
        handleAs : "json",
        load : function(result) {
            if (result.events == null || result.events[0] == null) {
                return;
            }

            var event = result.events[0];
            var widget;

            g_actualId = event.id;

            var test = event["id"];

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
                
                // Set the text for the delete button
                var deleteButton = document.getElementById("delete");
                if ( organizerName == g_userName ) {
                    deleteButton.innerHTML = "Cancel Meeting";
                }
                else {
                    deleteButton.innerHTML = "Decline";
                    
                    widget = dijit.byId("save");
                    widget.setDisabled(true);

                    showParticipantActions = true;
                }
                
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
            
            if ( !showParticipantActions ) {
                removeWidgetById("delegate");
            }
        }
    };

    if (id == null) {
        var today = new Date();
        removeWidgetById("delete");
        removeWidgetById("delegate");

        widget = dijit.byId("startDate");
        widget.setValue(today);
        widget = dijit.byId("startTime");
        widget.setValue(today);

        widget = dijit.byId("endDate");
        widget.setValue(today);
        widget = dijit.byId("endTime");
        widget.setValue(today);
        
        // Hide the organizer field, but show the attendees field
        removeElementById("organizerDiv");
        var section = document.getElementById("attendeesDiv");
        section.style.visibility = "visible";
    } else {
        dojo.xhrGet(xhrArgs);
    }
}

function saveEvent(id) {
    var event = {};
    var widget;

    widget = dijit.byId("summary");
    event.summary = widget.getValue();

    widget = dijit.byId("location");
    event.location = widget.getValue();

    widget = dijit.byId("description");
    event.description = widget.getValue();

    var dtStart = {};
    widget = dijit.byId("startDate");
    dtStart.date = formatDate(widget.getValue(),false);
    widget = dijit.byId("startTime");
    dtStart.time = formatTime(widget.getValue());
    // dtStart.utc = true;
    event.start = dtStart;

    var dtEnd = {};
    widget = dijit.byId("endDate");
    dtEnd.date = formatDate(widget.getValue(),false);
    widget = dijit.byId("endTime");
    dtEnd.time = formatTime(widget.getValue());
    // dtEnd.utc = true;
    event.end = dtEnd;
    
    widget = dijit.byId("attendees");
    var value = widget.getValue();
    if ( value != null && value.length > 0 ) {
        var addresses = value.split(",");

        var attendees = [ ];
        for ( i = 0; i < addresses.length; i++ ) {
            attendees[i] = {};
            attendees[i].email = trim(addresses[i]);
        }
        
        event.attendees = attendees;
        
        event.sequence = 0;
        
        var organizer = {};
        organizer.email = g_userEmail;
        event.organizer = organizer;
    }
	
	widget = dijit.byId("none");
	if (!widget.checked) {
		var count = dijit.byId("count").getValue();
		var rule = "FREQ=";
		if (dijit.byId("daily").checked) 
			rule += "DAILY";
		else if (dijit.byId("weekly").checked)
			rule += "WEEKLY";
		else if (dijit.byId("monthly").checked)
			rule += "MONTHLY";
		rule = rule + ";" + "COUNT=" + count;
		event.recurrenceRule = rule;
	}
	
    var events = [ event ];
    var request = {};
    request.events = events;

    if (id != null) {
		event.id = id;
	    var xhrArgs = {
		        url : g_eventFeedUrl+'/'+ id,
		        putData : dojo.toJson(request),
		        handleAs : "json",
		        headers : {
		            "Content-Type" : "application/json"
		        },
		        load : function(data) {
		            document.location = "calendar.html";
		        },
		        error : function(error) {
		            alert(error);
		        }
		    };
		
		    dojo.xhrPut(xhrArgs);
    }
    else {
	    var xhrArgs = {
	        url : g_eventFeedUrl,
	        postData : dojo.toJson(request),
	        handleAs : "json",
	        headers : {
	            "Content-Type" : "application/json"
	        },
	        load : function(data) {
	            document.location = "calendar.html";
	        },
	        error : function(error) {
	            alert(error);
	        }
	    };
	
	    dojo.xhrPost(xhrArgs);
    }

}

function deleteEvent(id) {

    var xhrArgs = {
        url : g_eventFeedUrl + "/" + id,
        load : function(data) {
            document.location = "calendar.html";
        },
        error : function(error) {
            alert(error);
        }
    };

    dojo.xhrDelete(xhrArgs);
}

function delegate(id, delegateTo) {

    if (id != null) {
		var actionUrl = g_eventFeedUrl + "/" + id + "/action?type=delegate";
		
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
}

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


