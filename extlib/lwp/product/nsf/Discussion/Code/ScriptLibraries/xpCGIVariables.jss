/*
	This server side script library implements a class named "CGIVariables" which allows for easy access
	to most CGI variables in XPages via javascript.
	
	For example, to dump the remote users name, IP address and browser string to the server console, use:
	Note: CGI variable names are case insensitive, so both "REMOTE_USER" or "Remote_User" etc. will work.
	
	var cgi = new CGIVariables();
	print ("Username: " + cgi.get("REMOTE_USER"));
	print ("Address : " + cgi.get("REMOTE_ADDR"));
	print ("Browser : " + cgi.get("HTTP_USER_AGENT"));
	
	@author		Thomas Gumz, IBM
 	@created	7/01/2008
  	@modified	6/26/2009
 	@version	2.0
*/

var CGIVariables = function () {
	
	function _get(name) {
		
		switch (name.toUpperCase()) {
		
		case "AUTH_TYPE":				return facesContext.getExternalContext().getRequest().getAuthType();
		case "CONTENT_LENGTH":			return facesContext.getExternalContext().getRequest().getContentLength();
		case "CONTENT_TYPE":			return facesContext.getExternalContext().getRequest().getContentType();
		case "CONTEXT_PATH": 			return facesContext.getExternalContext().getRequest().getContextPath();
		case "GATEWAY_INTERFACE":		return "CGI/1.1";
		case "HTTPS":					return facesContext.getExternalContext().getRequest().isSecure() ? "ON" : "OFF";
		case "PATH_INFO":				return facesContext.getExternalContext().getRequest().getPathInfo();
		case "PATH_TRANSLATED":			return facesContext.getExternalContext().getRequest().getPathTranslated();
		case "QUERY_STRING":			return facesContext.getExternalContext().getRequest().getQueryString();
		case "REMOTE_ADDR":				return facesContext.getExternalContext().getRequest().getRemoteAddr();
		case "REMOTE_HOST":				return facesContext.getExternalContext().getRequest().getRemoteHost();
		case "REMOTE_USER":				return facesContext.getExternalContext().getRequest().getRemoteUser();
		case "REQUEST_METHOD":			return facesContext.getExternalContext().getRequest().getMethod();
		case "REQUEST_SCHEME":			return facesContext.getExternalContext().getRequest().getScheme();
		case "REQUEST_URI":				return facesContext.getExternalContext().getRequest().getRequestURI();
		case "SCRIPT_NAME":				return facesContext.getExternalContext().getRequest().getServletPath();
		case "SERVER_NAME":				return facesContext.getExternalContext().getRequest().getServerName();
		case "SERVER_PORT":				return facesContext.getExternalContext().getRequest().getServerPort();
		case "SERVER_PROTOCOL":			return facesContext.getExternalContext().getRequest().getProtocol();
		case "SERVER_SOFTWARE":			return facesContext.getExternalContext().getContext().getServerInfo();
		
		// these are not really CGI variables, but useful, so lets just add them for convenience

		case "HTTP_ACCEPT":				return facesContext.getExternalContext().getRequest().getHeader("Accept");
		case "HTTP_ACCEPT_ENCODING":	return facesContext.getExternalContext().getRequest().getHeader("Accept-Encoding");
		case "HTTP_ACCEPT_LANGUAGE":	return facesContext.getExternalContext().getRequest().getHeader("Accept-Language");
		case "HTTP_CONNECTION":			return facesContext.getExternalContext().getRequest().getHeader("Connection");
		case "HTTP_COOKIE":				return facesContext.getExternalContext().getRequest().getHeader("Cookie");
		case "HTTP_HOST":				return facesContext.getExternalContext().getRequest().getHeader("Host");
		case "HTTP_REFERER":			return facesContext.getExternalContext().getRequest().getHeader("Referer");
		case "HTTP_USER_AGENT":			return facesContext.getExternalContext().getRequest().getHeader("User-Agent");
		}
	}
	
	// ==========================================
	// expose the public interface of this module
	// ==========================================

	return {
		get: _get
	}
}
