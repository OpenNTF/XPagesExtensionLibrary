var path = "";
var sUnid = items[0].unid;
if(dojo.isMozilla || dojo.isWebKit) {
    path = #{javascript:"\"" + @FullUrl('/') + "\""};
    path = path + "%TARGET-XPAGE%?documentId=" + sUnid + "&action=openDocument";
} else {
    path = "%TARGET-XPAGE%?documentId=" + sUnid + "&action=openDocument";
}
document.location.href = path;