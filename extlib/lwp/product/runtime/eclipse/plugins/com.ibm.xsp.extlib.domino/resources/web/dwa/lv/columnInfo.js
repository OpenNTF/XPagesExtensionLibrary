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

dojo.provide("dwa.lv.columnInfo");


dojo.declare(
	"dwa.lv.columnInfo",
	null,
{
	constructor: function(p1 ){
	    // --------------------------------------------------------------
	    // column info class
	    //  members:
	    //  .nXmlCol        data-source column number
	    //  .nColSort       data-source column number used for sorting
	    //  .nColFmt        type of data in the column ... 1==datetime
	    //  .sFmt           additional style added to the row
	    //  .sTitle         column mouse-over text
	    //  .sHiddenTitle   column mouse-over text
	    //  .nWidth         column width
	    //  .bIsResponse    response column
	    //  .bIsFixed       fixed width
	    //  .sLabel         column label (plus img)
	    //  .sText          column label text
	    //  .bResortable    is resortable
	    //  .iViewSort      type of sort used by the view's primary sort
	    //  .bIsExtend      extend column width
	    // --------------------------------------------------------------
	    this.nXmlCol = p1;
	    this.nColSort = p1;
	    this.nColFmt = 0;
	    this.sFmt = '';
	    this.bIsResponse = false;
	    this.bIsTwistie = false;
	    this.bIsFixed = false;
	    this.bIsExtend = false;
	    this.nOrgWidth = 0;
	    this.nWidth = 20;
	    this.nChars = 0;
	    this.sLabel = '';
	    this.sTitle = '';
	    this.sHiddenTitle = '';
	    this.sText = '';
	    this.bResortable = 0;
	    this.iViewSort = 0;
	    this.sNarrowDisp = '';
	    this.nSeqNum = 1;
	    this.bWrapUnder = false;
	    this.sIMColName = '';
	    this.bIsIcon = false;
	    this.bIsThin = false;
	    this.nHeaderIcon = 0;
	    this.bShowGradientColor = false;
	    this.bAlignGradientColor = false;
	    this.bUnhideWhenWrapped = false;
	},
	isSortable: function(p1,p2,p3){
	    // p1 == v$.isPrintMode
	    // p2 == v$.targetTumbler
	    // p3 == v$.bShowDefaultSort
	    if( p1 || p2 )return 0;
	    if( 0!=this.bResortable || (p3 && 0!=this.iViewSort) )return 1;
	    return 0;
	},
	isMoveable: function(p1,p2 ){
	    // p1 == v$.isPrintMode
	    // p2 == v$.targetTumbler
	    // Fixed problem that column width is not inherited in print mode. (SPR SDOY6TEJJJ)
	    if( p2 )return 0;
	    if( 0==this.bIsFixed )return 1;
	    return 0;
	}
});
