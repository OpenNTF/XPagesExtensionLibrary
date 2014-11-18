function getFullPath() {
	var url = context.getUrl().toString();
	var regex = /(\/[^.]+)(\.xsp)?$/;
	var path = regex.exec(url);
	return path;
}

function getPath() {
	var path = getFullPath();
	return path[1];
}

