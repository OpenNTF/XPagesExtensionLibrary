/*
 * © Copyright IBM Corp. 2014
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
$(document).ready(function() {
	$('ul.dropdown-menu [data-toggle=dropdown]').on('click', function(event) {
    	event.preventDefault(); event.stopPropagation(); 
    	// 	opening the one you clicked on
    	$(this).parent().addClass('open');
    	var menu = $(this).parent().find("ul");
    	var menupos = menu.offset();
    	if ((menupos.left + menu.width()) + 30 > $(window).width()) {
        	var newpos = - menu.width();      
    	} else {
        	var newpos = $(this).parent().width();
    	}
    	menu.css({ left:newpos, width: menu.width() });
	});
});

var XTB = XTB || {};
XTB.initCollapsibleMenu = function(label, collapseTo) {
	
	var left = $(".applayout-column-left");
	var ul = left.find("ul").first();
	
	if (ul.length>0) {
		
		var div = $('<div/>').addClass('visible-xs visible-sm dropdown xspCollapsibleMenu');
		var btn = $('<button class="btn btn-default btn-left-col-menu dropdown-toggle" data-toggle="dropdown">' + label + ' <span class="caret"></span></button>');
		var clone = ul.clone().addClass('dropdown-menu');		//clone of the menu
	
		div.append( btn );
		div.append( clone );
		
		//append menu button to target element
		if (collapseTo.indexOf(".")==-1 && collapseTo.indexOf("#")==-1) {
			collapseTo = "." + collapseTo;
		}
		var $tgt = $(collapseTo);
		if ($tgt.length==0) {
			$tgt = left;
		}
		
		$tgt.after(div);
	}
	
}

//jQuery selector that works with XPages IDs
//See - http://openntf.org/XSnippets.nsf/snippet.xsp?id=x-jquery-selector-for-xpages
function x$(idTag, param){
	idTag = idTag.replace(/:/gi, "\\:") + (param ? param : "");
	return($("#" + idTag));
}

XSP.xbtShowHideDetails = function(id,idx,pos,summaryOrDetail,iconDown,iconUp, titleShow, titleHide){
	var span=id+":"+idx+"_shChevron";
	var sum=id+":"+idx+"_sum";
	var det=id+":"+idx+"_detail";
	var inp=dojo.byId(id+"_shfld");
	var a=inp.value?inp.value.split(","):[];
	var ia=dojo.indexOf(a,pos);
	if(ia>=0) a.splice(ia,1); else a.push(pos); inp.value=a.toString();
	var vis=dojo.style(det,"display")!="none";
	
	var iconSpan = dojo.byId(span);
	iconSpan.className =vis?""+iconDown:""+iconUp;
	var toggleTitle = vis?""+titleShow:""+titleHide;
	dojo.query(iconSpan.parentNode).attr("aria-label", toggleTitle).attr("title", toggleTitle);
	dojo.query('.sr-only', iconSpan)[0].innerHTML = toggleTitle
	
	if(vis){
		dojo.style(det,"display","none");
		if(summaryOrDetail) {
			dojo.style(sum,"display",""); 
		}
	}else{
		dojo.style(det,{opacity: "0",display:"block"});
		dojo.fadeIn({node:dojo.byId(det),duration:400}).play();
		if(summaryOrDetail) {
			dojo.style(sum,"display","none");
		}
	}
};

