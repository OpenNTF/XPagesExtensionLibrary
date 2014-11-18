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

dojo.provide("dwa.lv.benriFuncs");

dojo.require("dwa.lv.msgs");
dojo.require("dwa.common.utils");
dojo.require("dwa.lv.globals");
dojo.require("dwa.lv.autoConsolidatedImageListener");
dojo.require("dwa.lv.autoConsolidatedImageListenerA11y"); // nakakura

dwa.lv.benriFuncs.getScrollBarWidth = function(){
	    if(!dwa.lv.benriFuncs.nScrollBarWidth) {
	        var oDiv = dojo.doc.createElement("DIV");
	        with (oDiv.style) {
	            position = "absolute";
	            left = "-100px";
	            top = "0px";
	            width = "100px";
	            height = "100px";
	            overflow = "scroll";
	            visibility = "hidden";
	 if( dojo.isMozilla ){
	            MozBoxSizing = 'border-box';
	 }else if( dojo.isWebKit ){ // G
	            WebkitBoxSizing = 'border-box';
	 } // end - S
	        }
	        dojo.doc.body.appendChild(oDiv);
	        dwa.lv.benriFuncs.nScrollBarWidth = oDiv.offsetWidth - oDiv.clientWidth;
	        dojo.doc.body.removeChild(oDiv);
	    }
	    return dwa.lv.benriFuncs.nScrollBarWidth;
};

dwa.lv.benriFuncs.findAttr = function(sName, oElem, bReturnElem, nMaxDepth){
	    var sValue,nDepth=0;
	    if(!nMaxDepth) nMaxDepth=500;
	    while( oElem && !(sValue=oElem.getAttribute(sName)) && nDepth<nMaxDepth ){
	        oElem = oElem.parentNode;
	        if("#document"==oElem.nodeName || "HTML"==oElem.nodeName) return;
	        nDepth++;
	    }
	    if( nDepth>=nMaxDepth ) return null;
	    return bReturnElem ? oElem : sValue;
};

dwa.lv.benriFuncs.eventCancel = function(ev){
	    if(!ev) return;
	    (dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );
	    (dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );
};

dwa.lv.benriFuncs.eventPreventDefault = function(ev){
	    if(!ev) return;
	    (dojo.isIE ? (ev.returnValue = false) : (dojo.isMozilla ? (ev.preventDefault()) : (ev.preventDefault()) ) );
};

dwa.lv.benriFuncs.eventStopPropagation = function(ev){
	    if(!ev) return;
	    (dojo.isIE ? (ev.cancelBubble = true) : (dojo.isMozilla ? (ev.stopPropagation()) : (ev.stopPropagation()) ) );
};

dwa.lv.benriFuncs.eventGetTarget = function(ev){
	 if( dojo.isMozilla ){
	    if(ev.target)
	    {
	        var t1 = ev.target;
	        try {
	            while(t1 && t1.nodeName=='#text')
	                t1 = t1.parentNode;
	            return t1;
	        } catch(e) {
	        }
	    }
	    return null;
	 }else{ // G
	    return ev.srcElement;
	 } // end - IS
};

dwa.lv.benriFuncs.eventGetFromTarget = function(ev){
	 if( dojo.isMozilla ){
	    if(ev.relatedTarget)
	    {
	        var t1 = ev.relatedTarget;
	        try {
	            while(t1 && t1.nodeName=='#text')
	                t1 = t1.parentNode;
	            return t1;
	        } catch(e) {
	        }
	    }
	    return null;
	 }else{ // G
	    return ev.fromElement;
	 } // end - IS
};

dwa.lv.benriFuncs.eventGetToTarget = function(ev){
	 if( dojo.isMozilla ){
	    if(ev.relatedTarget)
	    {
	        var t1 = ev.relatedTarget;
	        try {
	            while(t1 && t1.nodeName=='#text')
	                t1 = t1.parentNode;
	            return t1;
	        } catch(e) {
	        }
	    }
	    return null;
	 }else{ // G
	    return ev.toElement;
	 } // end - IS
};

dwa.lv.benriFuncs.eventIsShortcutKeyPressed = function(ev){
		return (navigator.userAgent.match(/Mac|iPhone/i)) ? (ev.metaKey || ev.altKey) : ev.ctrlKey;
};

dwa.lv.benriFuncs.elGetAttr = function(el, sAttribute, bElement){
	    while( el ){
	        var u=(el.getAttribute? el.getAttribute(sAttribute):null);
	        if( u ) return (bElement? el:u);
	        el = el.parentNode;
	    }
	    return null;
};

dwa.lv.benriFuncs.elFromPoint = function( oBox, x, y, doc ){
	    // ----------------------------------------------------------------
	    // return the element if the x,y coordinates are inside the specified box
	    // ----------------------------------------------------------------
	    if( !doc ) doc=dojo.doc;
	    if( 'string'==typeof(oBox) ) oBox=doc.getElementById(oBox);
	    if( !oBox ) oBox = doc.body;
	 if( dojo.isIE || dojo.isWebKit ){
	    // IE
	    var el=doc.elementFromPoint(x,y);
	    if( oBox.contains(el) ) return el;
	    return null;
	 }else{ // IS
	    // Mozilla
	    //  Use a recursive scheme to look inside the specified box.
	    //  This could be VERY slow.  To speed up, pass a small area as oBox.
	    var posLP = dwa.lv.benriFuncs.getAbsPos(oBox, true);
	    var maxX  = posLP.x + oBox.offsetWidth;
	    var maxY  = posLP.y + oBox.offsetHeight;
	    if( x < posLP.x ) return null;
	    if( y < posLP.y ) return null;
	    if( x > maxX ) return null;
	    if( y > maxY ) return null;
	    for( var i=0, imax=oBox.childNodes.length; i<imax; i++ ){
	        var el = dwa.lv.benriFuncs.elFromPoint( oBox.childNodes[i], x, y, doc );
	        if( el ) return el;
	    }
	    return oBox;
	 } // end - G
};

dwa.lv.benriFuncs.elGetCurrentStyle = function(el, sProp, bByInteger, bPreferOffset){
	    var oValue = null;
	 if( dojo.isMozilla || dojo.isWebKit ){
	    var oStyle = dwa.lv.benriFuncs.elGetOwnerDoc(el).defaultView.getComputedStyle(el,null);
	    // SPR DYHG76K4XM : in Safari oStyle evaluates to null on second opening of namepicker
	    // since it is in an iFrame in sparkle. Need to reference its parent window's document instead.
	    if (oStyle == null)
	        oStyle = this.window.parent.document.defaultView.getComputedStyle(el,null);
	    oValue = oStyle.getPropertyValue(sProp.replace(/[A-Z]/g, function(str){return '-'+str.toLowerCase()}));
	 }else{ // GS
	    oValue = el.currentStyle[sProp];
	 } // end - I
	    switch(sProp)
	    {
	        case 'width':
	 if( dojo.isMozilla || dojo.isWebKit ){
	            if(oValue == 'auto' || oValue.indexOf('%') != -1)
	                return el.offsetWidth;
	 }else{ // GS
	            if(oValue == 'auto' || oValue.indexOf('%') != -1 || bPreferOffset)
	                return el.offsetWidth;
	 } // end - I
	        case 'height':
	 if( dojo.isMozilla || dojo.isWebKit ){
	            if(oValue == 'auto' || oValue.indexOf('%') != -1)
	                return el.offsetHeight;
	 }else{ // GS
	            if(oValue == 'auto' || oValue.indexOf('%') != -1 || bPreferOffset)
	                return el.offsetHeight;
	 } // end - I
	    }
	    if(bByInteger)
	    {
	        oValue = parseInt(oValue);
	        if(isNaN(oValue))
	            oValue = 0;
	    }
	    return oValue;
};

dwa.lv.benriFuncs.elGetOwnerDoc = function(el){
	    return el.ownerDocument ? el.ownerDocument : el.document;
};

dwa.lv.benriFuncs.generateIconsImgTitleString = function(iconNumber){
  if( !dwa.lv.benriFuncs.aIconTips )
    dwa.lv.benriFuncs.aIconTips = [ dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_0"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_1"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_2"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_3"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_4"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_5"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_6"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_7"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_8"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_9"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_10"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_11"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_12"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_13"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_14"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_15"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_16"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_17"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_18"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_19"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_20"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_21"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_22"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_23"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_24"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_25"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_26"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_27"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_28"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_29"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_30"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_31"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_32"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_33"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_34"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_35"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_36"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_37"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_38"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_39"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_40"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_41"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_42"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_43"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_44"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_45"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_46"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_47"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_48"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_49"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_50"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_51"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_52"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_53"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_54"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_55"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_56"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_57"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_58"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_59"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_60"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_61"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_62"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_63"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_64"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_65"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_66"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_67"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_68"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_69"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_70"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_71"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_72"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_73"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_74"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_75"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_76"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_77"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_78"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_79"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_80"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_81"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_82"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_83"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_84"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_85"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_86"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_87"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_88"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_89"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_90"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_91"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_92"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_93"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_94"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_95"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_96"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_97"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_98"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_99"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_100"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_101"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_102"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_103"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_104"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_105"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_106"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_107"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_108"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_109"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_110"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_111"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_112"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_113"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_114"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_115"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_116"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_117"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_118"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_119"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_120"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_121"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_122"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_123"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_124"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_125"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_126"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_127"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_128"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_129"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_130"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_131"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_132"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_133"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_134"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_135"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_136"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_137"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_138"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_139"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_140"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_141"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_142"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_143"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_144"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_145"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_146"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_147"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_148"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_149"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_150"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_151"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_152"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_153"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_154"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_155"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_156"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_157"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_158"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_159"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_160"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_161"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_162"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_163"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_164"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_165"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_166"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_167"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_168"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_169"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_170"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_171"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_172"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_173"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_174"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_175"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_176"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_177"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_178"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_179"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_180"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_181"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_182"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_183"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_184"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_185"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_186"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_187"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_188"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_189"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_190"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_191"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_192"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_193"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_194"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_195"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_196"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_197"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_198"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_199"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_200"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_201"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_202"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_203"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_204"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_205"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_206"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_207"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_208"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_209"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_210"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_211"), dwa.lv.msgs.getListViewSMsg( "L_VIEWICONTIPS_ELM_212") ];

		var sIconTips = dwa.lv.benriFuncs.aIconTips;
		var nIcon = iconNumber - 0;
		return sIconTips[nIcon] ? sIconTips[nIcon] : '';
};

dwa.lv.benriFuncs.generateIconsImgURLString = function(iconNumber, fConsolidate, bNoAltText, bGrayScale){
		if ( typeof(iconNumber) == "undefined" )
			return '';
	
		var nIcon = iconNumber - 0;
		var sIconTip = dwa.lv.benriFuncs.generateIconsImgTitleString(nIcon);
		// nakakura
		if(dojo.hasClass(dojo.body(), "dijit_a11y"))
			bGrayScale = true;
		if (fConsolidate) {
			var oSize = new dwa.common.utils.pos(nIcon == 150 || nIcon == 204 ? 5 : 13, 11);
			var oOffset = new dwa.common.utils.pos(((nIcon - 1) % 10) * 13, Math.floor((nIcon - 1) / 10) * 11);
			// nakakura
			var sUrl = bGrayScale ? dwa.lv.globals.get().buildResourcesUrl('colicon1-gray.gif') : (dojo.isMozilla || dojo.isWebKit ? ((nIcon in { 2: void 0, 3: void 0, 4: void 0, 5: void 0, 10: void 0, 11: void 0, 12: void 0, 33: void 0, 54: void 0, 58: void 0, 69: void 0, 81: void 0, 82: void 0, 83: void 0, 84: void 0, 122: void 0, 128: void 0, 129: void 0, 130: void 0, 133: void 0, 143: void 0, 150: void 0, 158: void 0, 160: void 0, 168: void 0, 178: void 0, 179: void 0, 180: void 0, 181: void 0, 182: void 0, 183: void 0, 184: void 0, 185: void 0, 186: void 0, 188: void 0, 196: void 0, 197: void 0, 200: void 0, 203: void 0, 204: void 0, 210: void 0, 211: void 0, 212: void 0 }) ? dwa.lv.globals.get().buildResourcesUrl('vwicns.gif') : dwa.lv.globals.get().buildResourcesUrl('colicon1.gif')) : ((nIcon in { 2: void 0, 3: void 0, 4: void 0, 5: void 0, 10: void 0, 11: void 0, 12: void 0, 33: void 0, 54: void 0, 58: void 0, 69: void 0, 81: void 0, 82: void 0, 83: void 0, 84: void 0, 122: void 0, 128: void 0, 129: void 0, 130: void 0, 133: void 0, 143: void 0, 150: void 0, 158: void 0, 160: void 0, 168: void 0, 178: void 0, 179: void 0, 180: void 0, 181: void 0, 182: void 0, 183: void 0, 184: void 0, 185: void 0, 186: void 0, 188: void 0, 196: void 0, 197: void 0, 200: void 0, 203: void 0, 204: void 0, 210: void 0, 211: void 0, 212: void 0 }) ? dwa.lv.globals.get().buildResourcesUrl('vwicns.gif') : dwa.lv.globals.get().buildResourcesUrl('colicon1.gif')));
			if (dojo.hasClass(dojo.body(), "dijit_a11y")) {
				return '<span' + dwa.lv.autoConsolidatedImageListenerA11y.prototype.getConsolidatedImageAttrsByPosStatic(oSize, oOffset, sUrl, false, "border:0px;")
					+ ((!bNoAltText && sIconTip) ? ' alt="' + sIconTip + '" title="' + sIconTip + '"' : ' alt=""') + '></span>';
			}
	 if( dojo.isMozilla || dojo.isWebKit ){
			return '<img ' + dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(oSize, oOffset, sUrl, false, "border:0px;")
			 + ((!bNoAltText && sIconTip) ? ' alt="' + sIconTip + '" title="' + sIconTip + '"' : ' alt=""') + '/>';
	 }else{ // GS
			var sImg = '<img ' + dwa.lv.autoConsolidatedImageListener.prototype.getConsolidatedImageAttrsByPosStatic(oSize, oOffset, sUrl, false, "border:0px;")
			 + ((!bNoAltText && sIconTip) ? ' alt="' + sIconTip + '" title="' + sIconTip + '"' : ' alt=""');
			return (bGrayScale ? sImg.indexOf(' style="')!=-1 ? sImg.replace(' style="', ' style="filter:gray;') : sImg + ' style="filter:gray"' : sImg)
			 + ' />';
	 } // end - I
		} else {
			var sImg = "00"+iconNumber;
			return '<img src="' + dwa.lv.globals.get().buildResourcesUrl('vwicn' + sImg.substr(sImg.length - 3, 3) + (bGrayScale ? 'g' : '') + '.gif') + '" style="border:0px;"'
			 + ' width="' + 13 + '" height="' + 11 + '"' + ((!bNoAltText && sIconTip) ? ' alt="' + sIconTip + '" title="' + sIconTip + '"' : '') + '/>';
		}
};

dwa.lv.benriFuncs.ST$ = {
	sText:''
	,sComp:''
	,hTimer:null
};

dwa.lv.benriFuncs.setStatusText = function(sText, sComp, nOrder){
		// we may need to control msg in statusbar if one or more controls use status bar in one page in the future.
		dwa.lv.benriFuncs.ST$.sText = sText;
		dwa.lv.benriFuncs.ST$.sComp = sComp;
		if(dwa.lv.benriFuncs.ST$.hTimer)
			clearTimeout(dwa.lv.benriFuncs.ST$.hTimer);
		dwa.lv.benriFuncs.ST$.hTimer = setTimeout(dwa.lv.benriFuncs.setStatusTextByTimeout, 1);
};

dwa.lv.benriFuncs.setStatusTextByTimeout = function(){
		// need to set text again because IE overwrites text when reloaded background image(ex. consolidate images).
		dwa.lv.benriFuncs.ST$.hTimer = null;
		// SPR# JFOR7QNMLE
		if (dwa.lv.globals.get().oStatusManager){
			var fIsStatusText = (dwa.lv.benriFuncs.ST$.sComp.indexOf('.showSelections') > 0);
			if (dwa.lv.benriFuncs.ST$.sText)
				dwa.lv.globals.get().oStatusManager.addEntry(fIsStatusText ? 2 : 3, '', dwa.lv.benriFuncs.ST$.sText);
			else if (dwa.lv.globals.get().oStatusManager.hideInfoContainer && fIsStatusText)
				dwa.lv.globals.get().oStatusManager.hideInfoContainer();
		}
};

dwa.lv.benriFuncs.escapeHtmlKeywords = function(sHtmlText, nFlag){
		if(!sHtmlText)
			return sHtmlText;
	
		if(!nFlag)
			nFlag = (1 | 2 | 4 | 8 | 16);
	
		return sHtmlText
				.replace(/&/g, (nFlag & 1 ? '&amp;'  : '&'))
				.replace(/&amp;#/g, '&#')
				.replace(/\"/g,(nFlag & 2 ? '&quot;' : '\"'))
				.replace(/</g, (nFlag & 4   ? '&lt;'   : '<'))
				.replace(/>/g, (nFlag & 8   ? '&gt;'   : '>'))
				.replace(/ /g, (nFlag & 16 ? '&nbsp;' : ' '));
};

dwa.lv.benriFuncs.getAbsPos = function( el, bAbs, bNoBody ){
	    // if bAbs==true, then really get the absolute position
	    //  otherwise, we normally want the offset from
	    //  this element's relatively positioned container
	
	    if( !el )
	        return;
	
	    var pos = new dwa.common.utils.pos(el.offsetLeft, el.offsetTop);
	    var elParent = typeof el.offsetParent == "object" ? el.offsetParent : null;
	    var s;
	
	    // Sometimes el.offsetWidth returns wrong value when style.width = 100% for bidi
	    // This is workaround code for that.
	    if (!dojo._isBodyLtr()) {
	       if (el.offsetLeft < 3 && elParent != null && elParent.childNodes.length == 1 && (elParent.offsetWidth - el.offsetWidth) > 0)
	          pos.x += elParent.offsetWidth - el.offsetWidth - 2 ; // -2 is the edge width
	    }
	
	    while( elParent != null && elParent.nodeName != "#document")
	    {
	        if (!bAbs)
	        {
	            s = dwa.lv.benriFuncs.elGetCurrentStyle(elParent, "position");
	            if ("relative" == s || "absolute" == s) break;
	        }
	        pos.x += elParent.offsetLeft - elParent.scrollLeft;
	        pos.y += elParent.offsetTop  - elParent.scrollTop;
	        elParent = elParent.offsetParent;
	    }
	    // add body scroll offset
	    if( !bNoBody ){
	        var b$=dwa.lv.benriFuncs.elGetOwnerDoc(el).body;
	        pos.x += b$.scrollLeft;
	        pos.y += b$.scrollTop;
	    }
	    return pos;
};

dwa.lv.benriFuncs.buildDataUrl = function(sForm, oArgs){
		if(!oArgs) oArgs={};
		return (oArgs.bAbsolute ? (dwa.lv.globals.get().sNsfPath + '/' + "iNotes" + '/' + "Proxy" + '/') : ("../../" + "iNotes" + '/' + "Proxy" +'/')) + "?OpenDocument&Form=" + sForm + 
			(oArgs.bCache&&oArgs.bLangDep ? "&l="+dojo.locale : "") +
			(oArgs.bCache ? ((dwa.lv.globals.get().oSettings.aCompressible && dwa.lv.globals.get().oSettings.aCompressible[4-1]=='1') ? "&gz" : "") + (oArgs.bNoCR ? '' : "&CR") + (oArgs.bNoMX ? '' : "&MX") + (oArgs.sTimeStamp?("&TS="+oArgs.sTimeStamp):(dwa.lv.globals.get().oSession.sFormsTLM ? "&TS="+dwa.lv.globals.get().oSession.sFormsTLM : "")) : "") + 
			(oArgs.bCache&&oArgs.bUserDep&&dwa.lv.globals.get().oSession.s_UNH ? "&UNH="+dwa.lv.globals.get().oSession.s_UNH : "");
};

