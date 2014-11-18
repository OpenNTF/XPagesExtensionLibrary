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

dojo.provide("dwa.lv.textCellEditor");

dojo.declare(
	"dwa.lv.textCellEditor",
	null,
{
    constructor: function(cell, value, afterCellEdit){
        this.cell = cell;
        this.value = value;
        this.afterCellEdit = afterCellEdit;
    },
    show: function(){
        var cell = this.cell;

        cell.style.padding = "1px 0px";

        cell.innerHTML = '<input onblur="dwa.lv.textCellEditor.prototype._onBlur(event)"'
            + ' onkeydown="dwa.lv.textCellEditor.prototype._onKeyDown(event)" '
            + (dojo.isIE ? 'onkeypress="event.cancelBubble = true;"': '')
            + ' style="padding:0px;border:0px;"'
            + ' type="text" value="' + this.value + '" />';

        //cell.innerHTML = '<input type="text" onkeydown="event.cancelBubble = true;" onkeyupx="event.cancelBubble = true;" onkeypress="event.cancelBubble = true;" value="' + this.value + '" />';

        cell.firstChild.focus();
    },
    _onKeyDown: function(ev){
        if( dojo.isIE ){ ev.cancelBubble = true; }

        var keyCode = ev.keyCode;
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));
        var cell = v$.cellEditor;
        if( !cell ) return true;

        switch( keyCode ){
        case dojo.keys.ENTER:
            cell._exitEditMode(ev );
            return false;
        case dojo.keys.ESCAPE:
            cell._exitEditMode(ev, true );
            return false;
        }

        return true;
    },
    _onBlur: function(ev){
	    var v$ = dwa.lv.virtualList.prototype.getVLStatic(dwa.lv.benriFuncs.eventGetTarget(ev));

        var cell = v$.cellEditor;
        if( cell ){
            cell._exitEditMode(ev );
        }
    },
    _exitEditMode: function(ev, isCancel){
        var cell = this.cell;

        cell.style.padding = '';
        var newValue = cell.firstChild.value;

        if( this.afterCellEdit ){ this.afterCellEdit(newValue, cell, isCancel); }
    }
});
