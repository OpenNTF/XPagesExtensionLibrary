/*
 * © Copyright IBM Corp. 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

dojo.provide("dwa.lv.cookie");


dojo.declare(
	"dwa.lv.cookie",
	null,
{
	constructor: function(document, sName, nHours, sPath, sDomain, bSecure ){
		this.$doc = document;
		this.$sName = sName;
		if (nHours)
		{
			var oCurrDate = new Date();
			oCurrDate.setTime( oCurrDate.getTime() + nHours*3600000 );
			this.$oExpiresDate = oCurrDate;
		}
		else
			this.$oExpiresDate = null;
		this.$sPath = sPath ? sPath : null;
		this.$sDomain = sDomain ? sDomain : null;
		this.$bSecure = bSecure ? bSecure : false;
		this.$oLastStored = {};
		this.$oLastLoaded = {};
	},
	getValue: function(){
		// NOTE: Older codebase checked HTTP_COOKIE field value as well (in addition to document.cookie),
		//       but such code was removed at some point of time
		var asMatch = (this.$doc.cookie || '').match(new RegExp(this.$sName + '=([^;]+)'));
		return asMatch && asMatch[1] || null;
	},
	store: function(sCookieValue, bOverwriteCheck){
		var fUpdated = false;
		if( !sCookieValue )
		{
			// Cookie may have been updated due to actions in a separate window...before
			//  updating a cookie with outdated values...load the latest info
			this.appendNewValue();
			this.$oLastStored = {};
	
			var a=[],n=0;
			for (var prop in this)
			{
				if ((prop.charAt(0) == '$') || ((typeof this[prop]) == 'function'))
					continue;
				a[n++] = prop + ':' + encodeURIComponent(this[prop]+'');
				this.$oLastStored[prop] = this[prop];
				if(this.$oLastLoaded[prop] == 'undefined' || this.$oLastLoaded[prop] != encodeURIComponent(this[prop]+''))
					fUpdated = true;
			}
			if(bOverwriteCheck && !fUpdated)
				for(var prop in this.$oLastLoaded)
					if(this[prop] == 'undefined' || this.$oLastLoaded[prop] != encodeURIComponent(this[prop]+''))
						fUpdated = true;
	
			var sCookieValue = a.join('&');
		}
	
		//Improvement in performance for IE
		if(bOverwriteCheck && !fUpdated)
			return;
	
		// can't use setCookie h_utils routine as it will escape sCookieValue a 2nd time
		this.$doc.cookie = this.$sName + "=" + sCookieValue +
			(this.$oExpiresDate ? "; expires=" + this.$oExpiresDate.toGMTString() : "") +
			(this.$sPath ? "; path=" + this.$sPath : "") +
			(this.$sDomain ? "; domain=" + this.$sDomain : "") +
			(this.$bSecure ? "; secure" : "");
	},
	appendNewValue: function(){
		// append cookie value that was stored by another window.
		var sCookieValue = this.getValue();
		if (!sCookieValue)
			return false;
		
		var a = sCookieValue.split('&');
		for (var i = 0; i<a.length; i++)
			a[i] = a[i].split(':');
			
		for (var i = 0; i<a.length; i++)
		{
			if((typeof(this[a[i][0]]) == 'undefined' && typeof(this.$oLastStored[a[i][0]]) == 'undefined') ||
				(this[a[i][0]] == this.$oLastStored[a[i][0]]))
			{
				this[a[i][0]] = decodeURIComponent(a[i][1]);
			}
		}
		return true;
	},
	load: function(){
		var sCookieValue = this.getValue();
		if (!sCookieValue)
			return false;
		
		var a = sCookieValue.split('&');
		for (var i = 0; i<a.length; i++)
			a[i] = a[i].split(':');
			
		this.$oLastStored = {};
		this.$oLastLoaded = {};
		for (var i = 0; i<a.length; i++)
		{
			this[a[i][0]] = decodeURIComponent(a[i][1]);
			this.$oLastStored[a[i][0]] = a[i][1];
			this.$oLastLoaded[a[i][0]] = a[i][1];
		}
		return true;
	},
	remove: function(){
		this.$doc.cookie = this.$sName + "=" +
			(this.$sPath ? "; path=" + this.$sPath : "") +
			(this.$sDomain ? "; domain=" + this.$sDomain : "") +
			"; expires=Thu, 01-Jan-1970 00:00:01 GMT";
	}
});
