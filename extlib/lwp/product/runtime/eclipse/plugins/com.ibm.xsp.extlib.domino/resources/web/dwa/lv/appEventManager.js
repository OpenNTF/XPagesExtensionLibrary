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

dojo.provide("dwa.lv.appEventManager");


dojo.declare(
	"dwa.lv.appEventManager",
	null,
{
    isDebug: false,

    //hookedEvents: [ 'mouseover','mouseout','mousemove','mousedown','mouseup',
    //                'click','dblclick','contextmenu',
    //                'focus','blur','keydown','keypress','keyup' ],
    hookedEvents: [],

    _id: null,
    _lvObj: null,

    // static objects
    _objectMap: {},

	constructor: function(/*Object*/args){
		if(args){
			dojo.mixin(this, args);
		}

        var evs = this.hookedEvents;
        if( !evs ){
            this.hookedEvents = [];
        }else if( typeof(evs) === 'string' ){
            this.hookedEvents = [ evs ];
        }
    },

    registerListView: function( id, lvObj ){
        dwa.lv.appEventManager.prototype._objectMap [ id ] = this;

        this._id = id;
        this._lvObj = lvObj;
    },

    _getObjFromEvent: function(ev, ext){
        var id = ext.id;
        if( !id ) return;
        var obj = dwa.lv.appEventManager.prototype._objectMap[ id ];

        return obj;
    },

    _eventTagMap: {
        click:  "tblContainerParent",
        dblclick:   "tblContainerParent",
        mousedown:  "tblContainerParent",
        mouseup:    "tblContainerParent",
        mouseover:  "tblContainerParent",
        mousemove:  "tblContainerParent",
        mouseout:   "tblContainerParent",
        contextmenu:    "tblContainer",
        focus:      "keytrap",
        blur:       "keytrap",
        keypress:   "keytrap",
        keydown:    "keytrap",
        keyup:      "keytrap"
    },

    _replaceHandler: function( eventName, orgHandler ){
            var id = this._id;

            if( orgHandler ){
                return function(event){
                    var rc = orgHandler(event);
                    dwa.lv.appEventManager.prototype.dispatchEvent(event,{id: id });
                    return rc;
                };
            }else{
                return function(event){
                    return dwa.lv.appEventManager.prototype.dispatchEvent(event,{id: id });
                };
            }
    },

    generateAppEventHooksAfterHTMLGeneration: function(){
        var vl = this._lvObj.oVL;

        var elements = {};
        elements.tblContainer = vl.tblContainer;
        elements.tblContainerParent = elements.tblContainer.parentNode;
        elements.keytrap = vl.oKeyTrapAnchor

        for( var index in this.hookedEvents ){
            var eventName = this.hookedEvents[index];
            if( !eventName ) continue;

            var tagLabel = this._eventTagMap[ eventName ];
            if( !tagLabel ) continue;

            var elm = elements[ tagLabel ];
            if( !elm ) continue;

            var propName = 'on' + eventName;

            elm[propName] = this._replaceHandler( eventName, elm[ propName ] );
        }
    },

    _keyboardEvents: {
        keypress:   1,
        keydown:    1,
        keyup:      1,
        focus:      1,
        blur:       1
    },

    _setExtInfo: function( ev, ext ){
        var vl = this._lvObj.oVL;
        var cell = dwa.lv.benriFuncs.eventGetTarget(ev);

	    for( ; cell; cell = cell.parentNode ){
	        if( cell.getAttribute && cell.getAttribute('iscell')){
                break;
	        }
	    }
        if( !cell ){
            if( dwa.lv.appEventManager.prototype._keyboardEvents[ ev.type ] ){
                var row = vl.getFocusedRow();
                if( row ){
                    var pos = row.getAttribute('tumbler');
                    var cell = vl.getFocusedCell(row);
                }
            }
        }else{
            for( var row = cell; row; row = row.parentNode ){
    	        if( row.getAttribute ){
                    var pos = row.getAttribute('tumbler');
                    if( pos ){
                        break;
                    }
    	        }
            }
        }

        if( row ){
            ext.row = row;
            ext.tumbler = pos;
            var entry = vl.oRoot.getEntryByTumbler(pos);
            var item = entry ? entry.getViewEntry() : null;
            ext.item = item;
        }

        if( cell ){
            var i_col = (cell.getAttribute('i_col') || 0);
            ext.cell = cell;
            ext.columnNumber = i_col;
            ext.colInfo = vl.aColInfo[ i_col ];
            if( item ){
                //ext.cellValue = vl.oDataStore.getEntryDataByNumber( item, i_col );
                ext.cellValue = vl.oDataStore.getEntryDataByName( item, ext.colInfo.sName );
            }
        }

        return !!cell;
    },

    dispatchEvent: function(ev, ext){
        if( !ev ){ ev = window.event; }

        var obj = dwa.lv.appEventManager.prototype._getObjFromEvent(ev, ext);
        if( !obj ) return;

        // dojo.hitch() code is not used,
        //   because onXXX() handlers may be dojo.connected by applications
        //
        // NG -- var f = dojo.hitch( obj, obj.handlerTable[ev.type] ); f(ev,ext);

        var cellOk = obj._setExtInfo(ev, ext);

        obj = obj._lvObj; // obj is changed to point to the listview object.

        if( cellOk ){
            if( ev.type === 'mouseover' ){
                obj.onCellMouseOver( ev, ext );
            }else if( ev.type === 'mouseout' ){
                obj.onCellMouseOut( ev, ext );
            }else if( ev.type === 'mousemove' ){
                obj.onCellMouseMove( ev, ext );
            }else if( ev.type === 'mousedown' ){
                obj.onCellMouseDown( ev, ext );
            }else if( ev.type === 'mouseup' ){
                obj.onCellMouseUp( ev, ext );
            }else if( ev.type === 'click' ){
                obj.onCellClick( ev, ext );
            }else if( ev.type === 'dblclick' ){
                obj.onCellDblClick( ev, ext );
            }else if( ev.type === 'contextmenu' ){
                obj.onCellContextMenu( ev, ext );
            }else if( ev.type === 'focus' ){
                obj.onCellFocus( ev, ext );
            }else if( ev.type === 'blur' ){
                obj.onCellBlur( ev, ext );
            }else if( ev.type === 'keydown' ){
                obj.onCellKeyDown( ev, ext );
            }else if( ev.type === 'keypress' ){
                obj.onCellKeyPress( ev, ext );
            }else if( ev.type === 'keyup' ){
                obj.onCellKeyUp( ev, ext );
            }
        }

        if( ev.type === 'mouseover' ){
            obj.onMouseOver( ev, ext );
        }else if( ev.type === 'mouseout' ){
            obj.onMouseOut( ev, ext );
        }else if( ev.type === 'mousemove' ){
            obj.onMouseMove( ev, ext );
        }else if( ev.type === 'mousedown' ){
            obj.onMouseDown( ev, ext );
        }else if( ev.type === 'mouseup' ){
            obj.onMouseUp( ev, ext );
        }else if( ev.type === 'click' ){
            obj.onClick( ev, ext );
        }else if( ev.type === 'dblclick' ){
            obj.onDblClick( ev, ext );
        }else if( ev.type === 'contextmenu' ){
            obj.onContextMenu( ev, ext );
        }else if( ev.type === 'focus' ){
            obj.onFocus( ev, ext );
        }else if( ev.type === 'blur' ){
            obj.onBlur( ev, ext );
        }else if( ev.type === 'keydown' ){
            obj.onKeyDown( ev, ext );
        }else if( ev.type === 'keypress' ){
            obj.onKeyPress( ev, ext );
        }else if( ev.type === 'keyup' ){
            obj.onKeyUp( ev, ext );
        }
        return true;
    }
} );

