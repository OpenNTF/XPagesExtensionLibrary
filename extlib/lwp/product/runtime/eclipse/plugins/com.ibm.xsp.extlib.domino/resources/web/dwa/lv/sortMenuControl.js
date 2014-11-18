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

dojo.provide("dwa.lv.sortMenuControl");

dojo.declare(
    "dwa.lv.sortMenuControl",
    null,
{
    constructor: function( props ){
        dojo.mixin( this, props );
    },

    generateSortInfo: function( vl, isMenuInfo ){
        var defSortIdx = vl.nSortBy;
        if( !isMenuInfo ){
            if( defSortIdx < 0 ) defSortIdx = vl.aColInfo.nSortBy;
            if( defSortIdx < 0 ) defSortIdx = vl.aColInfo.nDefaultSortBy;
        }

        var sortColIdx = -1;
        var sortCols = [];
        for(var i=0;i<vl.nColumnSpan;i++){
            var col = vl.aColInfo[i];
            if(col.isSortable(false, vl.sThreadRoot, vl.bShowDefaultSort)){
                var v = {
                    index: (col.nSortCol || col.nColSort),
                    label: (col.sText || col.sHiddenTitle ||
                                vl._smsgs[ "L_NARROW_NOTITLE" ] || "(No title)")
                };

                if( !isMenuInfo ){
                    if( col.bIsIcon ) continue;

                    var fmt = col.nFormat || col.nColFmt;

                    if( fmt == 1 || fmt == 3 ){
                        v.type = "date";
                    }else if( col.bBytes ){
                        v.type = "number";
                    }else{
                        v.type = "text";
                    }
                }

                sortCols.push(v);

                if( defSortIdx == v.index ){
                    sortColIdx = v.index;
                }
            }
        }

        var sc = {
            sortColIdx: sortColIdx,
            sortCols: sortCols
        };

        if( isMenuInfo && sortCols.length > 0 && vl.bNonDefaultWidths ){
            sc.msgRestoreWidth = vl._smsgs[ "L_RESTORE_WIDTHS" ] || "Restore Width";
        }
        return sc;
    },

    _convertSortInfoToMenuInfo: function( sortInfo, lvid, vl ){
        var sortColIdx = sortInfo.sortColIdx;
        var sortCols = sortInfo.sortCols;
        var msgRestoreWidth = sortInfo.msgRestoreWidth;

        var n = sortCols.length;
        if( n <= 0 ) return null;

        var menuInfo = [];

        for( var i = 0; i < n; i++ ){
            var sortCol = sortCols[i];
            var idx = sortCol.index;

            var mi = {
                label: sortCol.label,
                action: dojo.hitch( null, vl.resortByColumnStatic, lvid, idx ),
                realIndex: idx
            };

            if( idx == sortColIdx ){
                mi.checked = true;
            }

            menuInfo.push( mi );
        }

        if( msgRestoreWidth ){
            menuInfo.push( {isSeparator: true} );
            menuInfo.push( {
                label: msgRestoreWidth,
                action: dojo.hitch( null, vl.onColumnResetStatic, lvid )
            } );
        }

        return menuInfo;
    },

    generateMenuInfo: function(lvid ){
        var lvWidget = dijit.byId( lvid );
        if( !lvWidget ) return;

        var v$ = lvWidget.oVL;
        if(!v$.bNarrowMode || v$.sThreadRoot) return null;

        var sc = this.generateSortInfo( v$, true );
        var menuInfo = this._convertSortInfoToMenuInfo( sc, lvid, v$ );
        if( !menuInfo ) return null;

        return menuInfo;
    }
});

dwa.lv.sortMenuControl.get = function( props ){
    var o = dwa.lv.sortMenuControl.obj;
    if( !o ){ dwa.lv.sortMenuControl.obj = o = new dwa.lv.sortMenuControl(props);}
    
    return o;
};
