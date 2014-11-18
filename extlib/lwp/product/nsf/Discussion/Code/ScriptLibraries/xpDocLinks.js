function insertLinks(){
	var links=document.getElementsByTagName("a");
	var numLinks=links.length;
	for (var i=0;i<numLinks; i++) {
		var link = links[i];
		if(link!=null){
			var classname = link.getAttribute("class");
			var href=link.getAttribute("href");
			if(isDominoLink(classname)){
				var newanchor = document.createElement("a");
				newanchor.setAttribute("href", "notes://"+link.getAttribute("href"));
				newanchor.setAttribute("class","xpageTempLink");
				var sometext = document.createTextNode("Notes Link");
				var seperator=document.createTextNode(" | ");
				newanchor.appendChild(sometext);
				try{
					insertAfter(newanchor, link);
					insertAfter(seperator,link);
					numLinks=numLinks+1;		
				}catch(e){console.log(e);}
			 }else{
				if(isXPagesNotesLink(href,classname)){
					var newanchor = document.createElement("a");
					newanchor.setAttribute("href", link.getAttribute("href").replace("notes://",""));
					var sometext = document.createTextNode("Web Link");
					var seperator=document.createTextNode(" | ");
					newanchor.appendChild(sometext);
					try{
						insertAfter(newanchor, link);
						insertAfter(seperator,link);
						numLinks=numLinks+1;
					}catch(e){console.log(e);}
					if(link.firstChild!=null){
						var textElement=link.firstChild;
						if(textElement.firstChild!=null){
							var underlineTag=textElement.firstChild;
							if(underlineTag.tagName=="U"){
								underlineTag.innerHTML="Notes Link";
							}
						}
					}
				}
			}
		}
	}
}

function isDominoLink(classname){
	if(classname==null)
		return false;
	var firstPart=classname.substring(12,0);
	if(firstPart==("domino-link-")){
		return true;
	}
	return false;	
}

function isXPagesNotesLink(notesLink,classname){
	if(notesLink==null)
		return false;
	if(classname=="xpageTempLink")
		return false;
	var firstPart= /notes:\/\/\//.test(notesLink);
	if(firstPart==false)
		return false;
	var secondPart=/\.nsf/.test(notesLink);
	if(secondPart==false)
		return false;
	var thirdPart=/\?Redirect/.test(notesLink);
	if(thirdPart==false)
		return false;
	if(firstPart && secondPart &&thirdPart)
		return true;

	return false;
}

function insertAfter(newElement,targetElement) {
	var parent = targetElement.parentNode;
	if(parent.lastchild == targetElement) {	
		parent.appendChild(newElement);
	} else {
		parent.insertBefore(newElement, targetElement.nextSibling);
	}
}