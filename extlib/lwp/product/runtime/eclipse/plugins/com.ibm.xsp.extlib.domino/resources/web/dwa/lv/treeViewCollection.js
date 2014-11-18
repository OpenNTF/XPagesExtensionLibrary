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

dojo.provide("dwa.lv.treeViewCollection");


dojo.declare(
	"dwa.lv.treeViewCollection",
	null,
{
	constructor: function(nChunk){
	    this.clear(nChunk);
	},
	showAll: function(){
		console.dir( this.aCollections );
	},
	clear: function(nChunk){
	    this.aCollections = [];
	    this.nChunk = nChunk ? nChunk : 0;
	},
	add: function(nStart, nEnd){
	    if(typeof nStart == 'undefined')
	        return;
	
	    if(typeof nEnd == 'undefined')
	        nEnd = nStart;
	
	    var oObj = {nStart:nStart, nEnd:nEnd};
	    this.aCollections.push(oObj);
	    this.sort();
	},
	addToTail: function(nStart){
	    if(typeof nStart == 'undefined')
	        return;
	
	    if(this.getNumChunks()) {
	        var oChunk = this.getChunk(this.getNumChunks() - 1);
	        if(oChunk.nEnd + 1 == nStart) {
	            if(this.nChunk) {
	                if(oChunk.nEnd - oChunk.nStart + 1 < this.nChunk) {
	                    oChunk.nEnd = nStart;
	                    return;
	                }
	            }
	            else {
	                oChunk.nEnd = nStart;
	                return;
	            }
	        }
	    }
	    
	    // don't sort in this func for performance and keep chunk.
	    var oObj = {nStart:nStart, nEnd:nStart};
	    this.aCollections.push(oObj);
	},
	find: function(nIndex){
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        if(!oObj)
	            continue;
	
	        if(oObj.nStart <= nIndex && nIndex <=oObj.nEnd)
	            return true;
	    }
	
	    return false;
	},
	getCount: function(){
	    var nCount = 0;
	    
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        if(!oObj)
	            continue;
	
	        nCount += (oObj.nEnd - oObj.nStart) + 1;
	    }
	
	    return nCount;
	},
	getNext: function(nIndex){
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        if(!oObj)
	            continue;
	
	        if(nIndex <= oObj.nStart)
	            return oObj;
	    }
	
	    return (void 0);
	},
	getNumChunks: function(){
	    return this.aCollections.length;
	},
	getChunk: function(nIndex){
	    return this.aCollections[nIndex];
	},
	max: function(){
		var maxValue;
		for(var i=this.aCollections.length - 1; i >= 0; i--)
		{
			var oObj = this.aCollections[i];

			if( !maxValue || maxValue < oObj.nEnd){
				maxValue = oObj.nEnd;
			}
		}
		return maxValue;
	},
	sort: function(){
	    this.aCollections.sort(dwa.lv.treeViewCollection.prototype.compareStatic);
	    
	    var aNewArray = [];
	    var nNewIndex = -1;
	    
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        
	        if(nNewIndex == -1)
	        {
	            nNewIndex = 0;
	            aNewArray[nNewIndex] = oObj;
	        }
	        
	        var oNewObj = aNewArray[nNewIndex];
	        
	        if(oNewObj.nEnd+1 >= oObj.nStart)
	        {
	            oNewObj.nEnd = Math.max(oNewObj.nEnd, oObj.nEnd);
	            continue;
	        }
	        else
	        {
	            nNewIndex ++;
	            aNewArray[nNewIndex] = oObj;
	        }
	    }
	    
	    this.aCollections = aNewArray;
	},
	destroy: function(){
	    this.aCollections = null;
	},
	compareStatic: function(oSrc, oDst){
	    if(oSrc && oDst)
	        return oSrc.nStart - oDst.nStart;
	
	    return 0;
	},
	remove: function(nIndex){
	    this.aCollections.sort(dwa.lv.treeViewCollection.prototype.compareStatic);
	    
	    var aNewArray = [];
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        if(oObj.nStart >= nIndex)
	        {
	            if(oObj.nStart < nIndex)
	                aNewArray.push({nStart:oObj.nStart, nEnd:nIndex-1});
	            if(nIndex < oObj.nEnd)
	                aNewArray.push({nStart:nIndex+1, nEnd:oObj.nEnd});
	        }
	        else
	            aNewArray.push(oObj);
	    }
	    
	    this.aCollections = aNewArray;
	},
	removeAndShift: function(nIndex){
	    this.aCollections.sort(dwa.lv.treeViewCollection.prototype.compareStatic);
	    
	    var aNewArray = [];
	    for(var i=0;i<this.aCollections.length;i++)
	    {
	        var oObj = this.aCollections[i];
	        if(oObj.nEnd >= nIndex)
	        {
	            if(oObj.nEnd == nIndex) {
	                if(oObj.nStart < nIndex)
	                    aNewArray.push({nStart:oObj.nStart, nEnd:oObj.nEnd-1});
	            }
	            else if(oObj.nStart < nIndex) {
	                aNewArray.push({nStart:oObj.nStart, nEnd:oObj.nEnd-1});
	            }
	            else if(oObj.nStart == nIndex) {
	                aNewArray.push({nStart:oObj.nStart, nEnd:oObj.nEnd-1});
	            }
	            else {
	                aNewArray.push({nStart:oObj.nStart-1, nEnd:oObj.nEnd-1});
	            }
	        }
	        else
	            aNewArray.push(oObj);
	    }
	    
	    this.aCollections = aNewArray;
	}
});
