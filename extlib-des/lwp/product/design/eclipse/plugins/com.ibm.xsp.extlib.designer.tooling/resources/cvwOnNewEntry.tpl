var yyyymmdd = null;
var calDate = calendar.getDate();
// if we have a calendar date, format it as a yyyymmdd string
if (calDate != null) {
    var yyyy = new String(calDate.getFullYear());
    var month = calDate.getMonth() + 1;
    var mm = month < 10 ? new String('0' + month) : month; 
    var day = calDate.getDate();
    var dd = day < 10 ? new String('0' + day) : day;
    var yyyymmdd = yyyy + mm + dd;
}
    
var path = "";
if(dojo.isMozilla || dojo.isWebKit){
    path = #{javascript:"\"" + @FullUrl('/') + "\""};
}
        
// append the XPage to create a calendar entry
path += "%TARGET-XPAGE%";
    
// add a parameter value for the selected date if available
if (yyyymmdd != null) {
    path = path + "?date=" + yyyymmdd;  
}
    
//change the current URL
document.location.href=path;