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

dojo.provide("dwa.lv.colpos");

dwa.lv.colpos.getParent = function(sTumbler){
	    var nPos = sTumbler.lastIndexOf('.');
	    if(nPos == -1)
	        return '';
	    
	    return sTumbler.substring(0,nPos);
};

dwa.lv.colpos.getLeafIndex = function(sTumbler){
	    var nPos = sTumbler.lastIndexOf('.');
	    if(nPos == -1)
	        return parseInt(sTumbler,10);
	
	    var sIndex = sTumbler.substring(nPos+1, sTumbler.length);
	    return parseInt(sIndex,10);
};

dwa.lv.colpos.getLeafIndexWithParent = function(sParent, sTumbler){
	    if(!dwa.lv.colpos.contain(sTumbler, sParent) || dwa.lv.colpos.compare(sParent, sTumbler)==0)
	        return null;
	    
	    var sLeafTumbler = sTumbler.substring(sParent ? sParent.length + 1 : 0, sTumbler.length);
	    var nPos = sLeafTumbler.indexOf('.');
	    if(nPos != -1)
	        sLeafTumbler = sLeafTumbler.substring(0, nPos);
	    
	    return parseInt(sLeafTumbler,10);
};

dwa.lv.colpos.getLeaf = function(sParentTumbler, nIndex){
	    return sParentTumbler ? sParentTumbler + '.' + nIndex : '' + nIndex;
};

dwa.lv.colpos.getNext = function(sTumbler){
	    var sParentTumbler = dwa.lv.colpos.getParent(sTumbler);
	    return (sParentTumbler ? sParentTumbler + '.' : '') + (dwa.lv.colpos.getLeafIndex(sTumbler) + 1);
};

dwa.lv.colpos.getDepth = function(sTumbler){
	    var nCount = 0;
	    var nPos = -1;
	    while(-1 != (nPos = sTumbler.indexOf('.', nPos+1)))
	        nCount ++;
	
	    return nCount;
};

dwa.lv.colpos.getNDepthIndex = function(sTumbler, nLevel /* 0 base */){
	    var sTemp = sTumbler;
	    
	    for(var i=0;i<nLevel;i++)
	    {
	        var nPos = sTemp.indexOf('.');
	        if(nPos == -1)
	            return (void 0);
	        
	        sTemp = sTemp.substring(nPos+1);
	    }
	    
	    var nPos = sTemp.indexOf('.');
	    sTemp = (nPos == -1) ? sTemp : sTemp.substring(0, nPos);
	    
	    return sTemp ? parseInt(sTemp,10) : void 0;
};

dwa.lv.colpos.contain = function(sThisTumbler, sTumbler){
	    while(sThisTumbler = dwa.lv.colpos.getParent(sThisTumbler))
	        if(sTumbler == sThisTumbler) return true;
	    
	    if(sTumbler == sThisTumbler) return true;
	    return false;
};

dwa.lv.colpos.compare = function(sTumb1, sTumb2){
	    var nLevel = 0;
	
	    if(sTumb1 == sTumb2)
	        return 0;
	    if(sTumb1 == 'start')
	        return 1;
	    if(sTumb2 == 'last')
	        return 1;
	
	    while(1)
	    {
	        var nIndex1 = dwa.lv.colpos.getNDepthIndex(sTumb1, nLevel);
	        var nIndex2 = dwa.lv.colpos.getNDepthIndex(sTumb2, nLevel);
	        
	        if(typeof nIndex1 == 'undefined' && typeof nIndex2 == 'undefined')
	            return 0;
	        if(typeof nIndex1 == 'undefined' || nIndex1 < nIndex2)
	            return 1;
	        if(typeof nIndex2 == 'undefined' || nIndex1 > nIndex2)
	            return -1;
	        nLevel ++;
	    }
};

dwa.lv.colpos.getTopLevel = function(sTumbler){
	    while(sTumbler && dwa.lv.colpos.getParent(sTumbler))
	        sTumbler = dwa.lv.colpos.getParent(sTumbler);
	
	    return sTumbler;
};

dwa.lv.colpos.getCommonParent = function(sTumbler1, sTumbler2){
	    while(sTumbler1)
	    {
	        if(dwa.lv.colpos.contain(sTumbler2, sTumbler1))
	            return sTumbler2;
	        
	        sTumbler1 = dwa.lv.colpos.getParent(sTumbler1);
	    }
	    return '';
};

dwa.lv.colpos.getNewOneByDeletion = function(sTumbler, sDeletedTumbler){
	    // return if target entry exists above deleted entry
	    if(dwa.lv.colpos.compare(sTumbler, sDeletedTumbler) == 1)
	        return sTumbler;
	    
	    // we should delete if target entry is child entry of deleted entry
	    if(dwa.lv.colpos.contain(sTumbler, sDeletedTumbler))
	        return '';
	    
	    // recalc new tumbler if target entry exists below deleted entry
	    var nDepth = dwa.lv.colpos.getDepth(dwa.lv.colpos.getCommonParent(sTumbler, sDeletedTumbler));
	    var aLeafs = sTumbler.split('.');
	    if(nDepth != dwa.lv.colpos.getDepth(sDeletedTumbler))
	        return sTumbler;
	
	    if(nDepth < aLeafs.length)
	        aLeafs[nDepth] = parseInt(aLeafs[nDepth],10) - 1;
	
	    return aLeafs.join('.');
};

