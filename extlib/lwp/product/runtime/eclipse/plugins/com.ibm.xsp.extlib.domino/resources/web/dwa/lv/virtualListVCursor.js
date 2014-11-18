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

dojo.provide( "dwa.lv.virtualListVCursor" );
dojo.require( "dwa.lv.virtualList" );

dojo.require( "dwa.lv.benriFuncs" );

dojo.declare(
    "dwa.lv.virtualListVCursor",
    dwa.lv.virtualList,
{
    inPlaceEdit: false,

    init: function(){
        this.bVcursor = true;
     	this.inherited(arguments);
    },
    checkEnteringEditMode: function(ev){
        if( !this.inPlaceEdit ){ return true; }

        var cell = this.getFocusedCell();
        if( cell && !this.bEditingCell ){
            this.bEditingCell = this.cellEdit( cell );
        }
        return dwa.lv.benriFuncs.eventPreventDefault(ev);
    },
    cellEdit: function( cell ){
        var ds = this.oDataStore;
        if( !ds.isEditableStore() ) return false;

        var row = cell.parentNode;
        var tumbler = row.getAttribute("tumbler");
        var entry = this.oRoot.getEntryByTumbler(tumbler, true );
        var item = entry.getViewEntry();
        var cols = ds.getEntryDatas(item)
        var colInfo = this.aColInfo[ cell.getAttribute('i_col') ];
        var val = ds.getValue(cols, colInfo.nXmlCol);

        this.cellEditor = this.createCellEditorByType( ds.getType(val), cell, val, dojo.hitch( this, this.afterCellEdit ) );

        if( this.cellEditor ){
            this._savedInnerHTML = cell.innerHTML;
            this.cellEditor.show();
        }

        return !!this.cellEditor;
    },
    createCellEditorByType: function(type, cell, value, afterCellEdit ){
        if( type === "text" ){
            dojo.require( "dwa.lv.textCellEditor" );
            return new dwa.lv.textCellEditor( cell, value, afterCellEdit );
        }else{
            return undefined;
        }
    },
    afterCellEdit: function( newValue, cell, isCancel ){
        if( this.bEditingCell ){
            this.bEditingCell = false;

            if( !isCancel ){
                // update the view

                var val = newValue;

                var ds = this.oDataStore;
                var row = cell.parentNode;
                var tumbler = row.getAttribute("tumbler");
                var entry = this.oRoot.getEntryByTumbler(tumbler, true );
                var item = entry.getViewEntry();
                var cols = ds.getEntryDatas(item)
                var colInfo = this.aColInfo[ cell.getAttribute('i_col') ];
                var valOnDS = ds.getValue(cols, colInfo.nXmlCol);
            }

            // restore readmode innerhtml
            cell.innerHTML = this._savedInnerHTML;
            this._savedInnerHTML = undefined;

            if( !isCancel && ds.getValue(valOnDS) !== val ){
                // update through datastore
                ds.setValue( valOnDS, val );
                // --- need to upate server
                if( this.fnSetDocColumn ){ this.fnSetDocColumn( item, valOnDS, colInfo ); }
                // refresh the view --- this.refresh();

                cell.innerHTML = val;
            }

            this.focus();
            if( dojo.isIE ){ this.focus(); }

            delete this.cellEditor;
        }
    },
    focusRow: function(oRow){
		this.oFocusOuter.style.zIndex = '';
		this.oFocusRight.style.zIndex = '';
		this.oFocusTop.style.zIndex = '';
		this.oFocusBottom.style.zIndex = '';
		this.nFocusMode = 2;
		this.oFocusedRow = oRow;
		this.oFocus.setAttribute('com_ibm_dwa_ui_draggable_redirect', oRow.id);
	
		if(this.bSupportScreenReader) {
			this.oKeyTrapAnchor.tabIndex = 0;
			this.oKeyTrapAnchor.focus();
			this.updateTextForJAWS();
		}
		this.focusElementRow(oRow, this.oFocus, this.oFocusOuter, true, true);
	
	    this.oKeyTrapAnchor.tabIndex = -1;
		;
        if( this.oKeyTrapAnchor ) {
            //this.unfocusRow();

			if(this.bSupportScreenReader)
				this.updateTextForJAWS();
			this.oKeyTrapAnchor.focus();
			this.clearBlurTimeout();
			this.setFocusTimeout();
			this.nFocusMode = 0;
		}
    },

	unfocusRow: function(){
		;
		if(!this.oFocusedRow)
			return;
	
		this.oFocus.tabIndex = -1;
		this.oFocusOuter.style.zIndex = -100;
		this.oFocusRight.style.zIndex = -100;
		this.oFocusTop.style.zIndex = -100;
		this.oFocusBottom.style.zIndex = -100;
		;
		this.oFocusedRow = null;
	},
    focusElementRow: function(oElem, oFocus, oFocusOuter, bWidth100, bFocusRow){
		var oPos = dwa.lv.benriFuncs.getAbsPos(oElem);
        var t= oPos.y + 'px';
        var h = oElem.offsetHeight - 1;  // decrement bottom border width for a focused row

        var oCell = this.getFocusedCell(oElem);
        if( oCell ){
            oElem = oCell;
        }

		oPos = dwa.lv.benriFuncs.getAbsPos(oElem);
        var l = oPos.x + 'px';
        var w = oElem.offsetWidth;

        if( this.bNarrowMode && oCell ){
            t= oPos.y + 'px';
            h = oElem.offsetHeight;
        }

        var vfocusClassName = (this.bEditingCell ? "vl-vfocus-edit" : "vl-vfocus" );
        this.oFocusRight.className = oFocus.className = vfocusClassName + " vl-vfocus-vertical";
        this.oFocusTop.className = this.oFocusBottom.className = vfocusClassName + " vl-vfocus-horizontal";

		with(oFocus.style) {
			left = oCell ? l : '0px';
			top = t;
			width = '0px';
			height = h + 'px';
		}
        with(this.oFocusRight.style){
			left = (oCell ? (oPos.x+w-1) : (w-1)) + 'px';
			top = t;
			width = '0px';
			height = h + 'px';
        }
        with(this.oFocusTop.style){
			left = oCell ? l : '0px';
			top = t;
			width = oCell ? w + 'px' : '100%';
			height = '0px';
        }
        with(this.oFocusBottom.style){
			left = oCell ? l : '0px';
			top = (oPos.y + h-1) + 'px';
			width = oCell ? w + 'px' : '100%';
			height = '0px';
        }
		//try {
		//	oFocus.focus();
		//} catch(e){}
		//this.clearBlurTimeout();
		//this.setFocusTimeout();
    },

    generateFocusElement: function(){
        var v$ = this; // 'this' is a virtual list object

        var s = '<div id="' + v$.sId + '-focus" class="vl-vfocus vl-vfocus-vertical">' + '</div>'
            + '<div id="' + v$.sId + '-focusRight" class="vl-vfocus vl-vfocus-vertical"></div>'
            + '<div id="' + v$.sId + '-focusTop" class="vl-vfocus vl-vfocus-horizontal"></div>'
            + '<div id="' + v$.sId + '-focusBottom" class="vl-vfocus vl-vfocus-horizontal"></div>';

        return s;
    }
});

