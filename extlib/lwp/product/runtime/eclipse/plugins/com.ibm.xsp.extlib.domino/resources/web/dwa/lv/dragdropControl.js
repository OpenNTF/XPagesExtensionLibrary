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

dojo.provide("dwa.lv.dragdropControl");

dojo.require("dwa.lv.benriFuncs");
dojo.require("dwa.lv.globals");

dojo.declare(
	"dwa.lv.dragdropControl",
	null,
{
	constructor: function(sId){
	//public properties
		this.sId = sId;
		this.bEnabled = false;
		this.ddAttr = 'unid';// [string] 	the element attribute to find when storing drag&drop data
		this.ddUnselectCB = 'DDUnselected';
	// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
	// private properties
	// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
		this.$doc = dojo.doc;
		this.$bDrag = false;
		this.$iOffsetL = 0;
		this.$iOffsetT = 0;
		this.$aItems = null;
		this.$numItems = 0;
		this.$oIndexs = null;	// used in conjunction with list view tumbler to determine if a row is already selected
		this.$oSrc = null;	// DOM reference to the last selected object
		this.$idTarget = null;	// ondragover id reference
		this.$sData = null;	// usually a semi-colon list of unids
		this.$iMaxW = 0;
		this.$iMaxH = 0;
		this.$iMaxX = 0;
		this.$iMaxY = 0;
		this.$bSet = true;
		this.$aTargets = {};	 // [array]	  used to indicate a valid drop target
		this.$oOwner = null;
		this.$fnDragStart = null;	// function reference ... use setDragStartCB
		this.$fnDragEnd = null;	// function reference ... use setDragEndCB
	
		this.$bVisible = false;
		this.$orgX = 0;
		this.$orgY = 0;
		this.$fnMM = null;
		this.$fnMU = null;
	},
	isEnabled: function(){
		return this.bEnabled;
	},
	getData: function(p1){
		if(p1)
			this.$checkOwner(p1);
		return this.$sData;
	},
	getFirstSelected: function(p1){
		this.$checkOwner(p1);
		if(this.$aItems){
			for(var i=0,imax=this.$aItems.length;i<imax;i++)
				if(this.$aItems[i])
					return this.$aItems[i];
		}
	},
	getLastSelected: function(p1){
		this.$checkOwner(p1);
		if(this.$aItems){
			for(var i=this.$aItems.length-1;i>=0;i--)
				if(this.$aItems[i])
					return this.$aItems[i];
		}
	},
	getSelectedCount: function(p1){
		this.$checkOwner(p1);
		if(this.$aItems)
			return this.$numItems;
		else
			return 0;
	},
	getSelected: function(p1,p2,p3){
		this.$checkOwner(p2);
		if(p1&&1==this.getSelectedCount(p2)) {
			return this.getFirstSelected(p2);
		} else {
			var a=[];
			if(this.$aItems){
				for(var i=0,imax=this.$aItems.length;i<imax;i++)
					if(this.$aItems[i])
						a[a.length] = this.$aItems[i];
			}
			return a;
		}
	},
	isSelected: function(p1,p2,p3){
		this.$checkOwner(p2);
		if(p3 && this.$oIndexs && typeof(this.$oIndexs['pos:'+p3])=='number')
			return true;
		if(this.$aItems){
			for(var i=0,imax=this.$aItems.length;i<imax;i++)
				if(this.$aItems[i]==p1)
					return true;
		}
	},
	setSelected: function(p1,p2,doc,p4,p5,p6){
		if(!p5)
			this.$checkOwner(p4);
		if(!doc)
			doc=this.$doc;
		this.$clearFn1();
		if(p1){
			this.$aItems=[p1];
			this.$numItems=1;
			if(p6){
				this.$oIndexs={};
				this.$oIndexs['pos:'+p6]=0;
			}
			if(p2)
				this.$setFn(doc, p4);
		}
		else{
			if(this.$aItems)
				for(var i=0,imax=this.$aItems.length;i<imax;i++){
					if(this.$aItems[i]){
						this.$aItems[i].onmousemove=null;
						this.$notifyToOwner(this.$aItems[i]);
						this.$aItems[i]=null;
					}
				}
			this.$aItems=null;
			this.$numItems=0;
			this.$oIndexs=null;
		}
	},
	addSelected: function(p1,p2,doc,p4,p5){
		this.$checkOwner(p4);
		if(!doc)
			doc=this.$doc;
		if(!this.$aItems){
			this.setSelected(p1,p2,doc,p4,null,p5);
		}
		else{
			if(p5 && this.$oIndexs && typeof(this.$oIndexs['pos:'+p5])=='number'){
				var idx=this.$oIndexs['pos:'+p5];
				if(this.$aItems[idx]){
					this.$aItems[idx]=null;
					this.$numItems--;
				}
				this.$oIndexs['pos:'+p5]=this.$aItems.length;
				this.$aItems[this.$aItems.length]=p1;
				this.$numItems++;
			}
			else{
				this.removeSelected(p1,p2,doc,p4,p5);
				if(!this.$aItems){
					this.$aItems=[p1];
					this.$numItems=1;
					this.$oIndexs={};
					this.$oIndexs['pos:'+p5]=0;
				}
				else{
					if(this.getLastSelected(p4))
						this.getLastSelected(p4).onmousemove=null;
					if(p5){
						if(!this.$oIndexs)
							this.$oIndexs={};
						this.$oIndexs['pos:'+p5]=this.$aItems.length;
					}
					this.$aItems[this.$aItems.length]=p1;
					this.$numItems++;
				}
			}
			if(p2)
				this.$setFn(doc, p4);
		}
	},
	removeSelected: function(p1,p2,doc,p4,p5){
		this.$checkOwner(p4);
		if(!doc)
			doc=this.$doc;
		if(!this.$aItems || !this.$numItems)
			return;
		if(p5 && this.$oIndexs){
			var id=this.$oIndexs['pos:'+p5];
			if(typeof(id)!='number')
				return;
			this.$aItems[id].onmousemove=null;
			this.$notifyToOwner(this.$aItems[id]);
			this.$oIndexs['pos:'+p5]=null;
			this.$aItems[id]=null;
			this.$numItems--;
		}
		else{
			for(var i=0,imax=this.$aItems.length;i<imax;i++){
				if(p1==this.$aItems[i]){
					this.$aItems[i].onmousemove=null;
					this.$notifyToOwner(this.$aItems[i]);
					this.$aItems[i]=null;
					this.$numItems--;
					break;
				}
			}
		}
		this.$clearFn1();
		if(p2)
			this.$setFn(doc, p4);
	},
	setDropIcon: function(p1){
		if (this.oImg)
			this.oImg.src=this.$getDropIcon(p1);
	},
	clearTargets: function(){
		this.$aTargets=null;
		this.$bSet=true;
	},
	addDropTarget: function(p1	 ,p2	 ,p3	 ,p4	 ,p5	 ){
		if(!this.$aTargets)
			this.$aTargets={};
		this.$aTargets[p1]={ondrop:p2,ondragover:p3,ondragout:p4,ondragmove:p5};
		this.$bSet=true;
	},
	setDragStartCB: function(fn){
		this.$fnDragStart=fn;
	},
	setDragEndCB: function(fn){
		this.$fnDragEnd=fn;
	},
	$clearFn1: function(){
		// GSER75HMFN : now clearFn only remove event from selected elements. getSelected generates return array for performance
		if(this.$aItems){
			for(var i=0,imax=this.$aItems.length;i<imax;i++){
				if(this.$aItems[i])
					this.$aItems[i].onmousemove=null;
			}
		}
	},
	$clearFn2: function(doc){
		var oTmp = this;
		doc.body.onmousemove=function(ev){oTmp.$fnMM(ev);};
		doc.body.onmouseup=function(ev){oTmp.$fnMU(ev);};
		this.$fnMU=this.$fnMM=null;
	
		for( var idTarget in this.$aTargets ){
			var t1=pageGetElementById(idTarget,doc);
			if( t1 ){
				t1.onmouseover=null;
				t1.onmouseout=null;
			}
		}
	},
	$setFn: function(doc, p1){
		if(this.$aItems && this.bEnabled){
			var oTmp = this;
			this.getLastSelected(p1).onmousemove=function(ev){oTmp.$dragStart(ev);};
	//		if(this.$dragEnd!=doc.body.onmouseup){
				doc.body.onmouseup=function(ev){oTmp.$dragEnd(ev);};
	//		}
		}
	},
	$setData: function(p1){
		this.$checkOwner(p1);
		if(this.getSelectedCount(p1)){
			for(var i=0,a=[],imax=this.$aItems.length;i<imax;i++){
				a[i]=dwa.lv.benriFuncs.elGetAttr(this.$aItems[i],this.ddAttr);
			}
			this.$sData=a.join(';');
		}
		else{
			this.$sData="";
		}
	},
	$getDropIcon: function(p1){
		var oIcons = {"_i1": '041', "_i2": '022', "_i0": '080'};
		var sProp = p1 == 'move' ? '_i1' : p1 == 'copy' ? '_i2' : '_i0';
		if(!this[sProp]){
			this[sProp]=dwa.lv.globals.get().buildResourcesUrl('vwicn' + oIcons[sProp] + '.gif');
			return this[sProp];
		}
	},
	$getMinW: function(p1){
		var n=p1.offsetWidth,p2=p1.parentNode;
		while(p2&&p2.offsetWidth){
			n=Math.min(n,p2.offsetWidth);
			p2=p2.parentNode;
		}
		return n;
	},
	$checkOwner: function(p1){
		if(this.$oOwner==p1)
			return;
		this.setSelected(null,null,null,p1,true);
		this.$setOwner(p1);
	},
	$setOwner: function(p1){
		this.$oOwner=p1;
	},
	$notifyToOwner: function(p1){
		obj=this.$oOwner;
		if(!obj)
			return;
		if(obj[this.ddUnselectCB])
			obj[this.ddUnselectCB](p1);
	},
	$dragStart: function(ev){
		ev=eventGet(ev);
		var doc=dwa.lv.benriFuncs.elGetOwnerDoc(dwa.lv.benriFuncs.eventGetTarget(ev));
		if( !this.$bDrag ){
			this.$setData(this.$oOwner);
			this.$clearFn1();
			var oItem=this.getLastSelected(this.$oOwner);
			var hItem=Math.floor(oItem.offsetHeight/2);
			var wItem=this.$getMinW(oItem);  // problems with getting the item's width in the VirtualList code
			with( this ){
				$bDrag	  =true;
				$iOffsetT	=doc.body.scrollTop-hItem;
				$iOffsetL	=doc.body.scrollLeft+2;
				$iMaxW	  =17+wItem;  // status icon width + oItem width
				$iMaxH	  =oItem.offsetHeight;
				$iMaxX	  =doc.body.scrollLeft+Math.min(doc.body.offsetWidth,doc.body.clientWidth)-5;
				$iMaxY	  =doc.body.scrollTop+Math.min(doc.body.offsetHeight,doc.body.clientHeight)-5;
				$bVisible	=false;
				$orgX	   =ev.screenX;
				$orgY	   =ev.screenY;
			};
			// build a draggable row
			var oPos = getAbsPos(oItem,true);
			this.$oSrc = doc.getElementById('e-dragdropmanager-frame');
			if (!this.$oSrc) {
				var oRow = doc.createElement("DIV");
				oRow.id = "e-dragdropmanager-frame";
				with( oRow.style ){
					backgroundColor = 'transparent';
					cursor	  = 'default';
					display	 = 'none';
					height	  = 'auto';
					left		= '0px';
					overflow	= 'visible';
					position	= 'absolute';
					top		 = '0px';
					width	   = 'auto';
					zIndex	  = '999';
				}
				oRow.noWrap	 = true;
				oRow.innerHTML = '<div style="color:#999999;font-size:70%;border:solid #999999 1px;position:absolute;top:0px;left:0px;">&nbsp;</div><div class="s-noselecttext" onselectstart="return false;" unselectable="on" style="color:#999999;font-size:70%;border:solid #999999 1px;position:absolute;top:0px;left:0px;vertical-align:top;overflow:hidden;"></div>';
				this.$oSrc = doc.body.appendChild(oRow);
			}
	
			// adjust width and height
			this.$oSrc.lastChild.innerHTML = oItem.innerHTML;
			this.$oSrc.lastChild.style.height =this.$oSrc.firstChild.style.height=oItem.offsetHeight + 'px';
			this.$oSrc.lastChild.style.width  =this.$oSrc.firstChild.style.width =oItem.offsetWidth + 'px';
	
			if (this.getSelectedCount(this.$oOwner)>1) {
				// offset one of the inner DIVs to make it look like multiple elements are being dragged
				with( this.$oSrc.firstChild.style ){
					left = '4px';
					top  = '4px';
					borderWidth = '1px';
					if( gbIsGecko || gbIsSafari ){
						borderLeftColor= 'transparent';
						borderTopColor = 'transparent';
					}else{
						borderLeftWidth= '0px';
						borderTopWidth = '0px';
					}
				}
			}else{
				// make it look like one element is being dragged
				with( this.$oSrc.firstChild.style ){
					left = '0px';
					top  = '0px';
					borderWidth = '0px';
				}
			}
	
			this.$fnMM=doc.body.onmousemove;
			this.$fnMU=doc.body.onmouseup;
			var oTmp = this;
			doc.body.onmousemove=function(ev){oTmp.$dragMove(ev);};
			doc.body.onmouseup=function(ev){oTmp.$dragDrop(ev);};
	
			if( this.$bSet ){
				for( var idTarget in this.$aTargets ){
					var t1=pageGetElementById(idTarget,doc);
					if( t1 ){
						t1.onmouseover=function(ev){oTmp.$dragOver(ev);};
						t1.onmouseout=function(ev){oTmp.$dragOut(ev);};
						t1.setAttribute('com_ibm_dwa_ui_droppable',idTarget);
					}
				}
				this.$bSet=false;
			}
		}
	},
	$dragEnd: function(ev){
		ev=eventGet(ev);
		var doc=dwa.lv.benriFuncs.elGetOwnerDoc(dwa.lv.benriFuncs.eventGetTarget(ev));
		this.$clearFn2(doc);
		this.$clearFn1();
		if(this.$oSrc) this.$oSrc.style.display='none';
		var e1=doc.getElementById(this.$idTarget);
		if( e1 ){
			var c1=e1.className;
			if(c1){
				var c2=e1.getAttribute('com_ibm_dwa_ui_dragoverClass');
				if(!c2)c2="s-dragover-default";
				var n1=c1.indexOf(c2);
				if( n1>-1 )
					e1.className = c1.substr(0,n1);
			}
		}
		with(this){
			$bDrag=$bVisible=false;
			oImg=$oSrc=$idTarget=$sData=null;
		}
		if(this.$fnDragEnd){
			this.$fnDragEnd();
		}
	},
	$dragMove: function(ev){
		ev=eventGet(ev);
		if((!gbIsGecko&&(dojo.isIE ? (1) : (dojo.isMozilla ? (0) : (0) ) )!=ev.button)||ev.clientX<=2||ev.clientY<=2){
			return this.$dragEnd(ev);
		}
		var x=(ev.clientX+this.$iOffsetL);
		var y=(ev.clientY+this.$iOffsetT);
		try{
			with(this.$oSrc.style){
				left=x+'px';
				top=y+'px';
			}
		}
		catch(er){
			this.$dragEnd(ev);
		}
		if(!this.$bVisible && (Math.abs(this.$orgX - ev.screenX)>10 || Math.abs(this.$orgY - ev.screenY)>10)){
			this.$bVisible=true;
			this.$oSrc.style.display='block';
			if(this.$fnDragStart)
				this.$fnDragStart();
		}
		if(this.$bVisible){
			var nIcon;
			var t0=dwa.lv.benriFuncs.eventGetTarget(ev),doc=dwa.lv.benriFuncs.elGetOwnerDoc(t0);
			var e1=dwa.lv.benriFuncs.elGetAttr(t0,'com_ibm_dwa_ui_droppable',true);
			if(e1){
				this.$idTarget = e1.id;
				nIcon='move';
				if(!this.$fireEvent('dragmove',t0,eventIsShortcutKeyPressed(ev)))
					return;
			}
			this.setDropIcon(nIcon);
		}
	},
	$dragDrop: function(ev){
		ev=eventGet(ev);
		if(!this.$bDrag)
			return;
		var t0=dwa.lv.benriFuncs.eventGetTarget(ev),doc=dwa.lv.benriFuncs.elGetOwnerDoc(t0);
		this.$clearFn2(doc);
		this.$oSrc.style.display='none';
		if(this.$idTarget&&dwa.lv.benriFuncs.elFromPoint(this.$idTarget,ev.clientX,ev.clientY,doc)){
			this.$fireEvent('drop',t0,eventIsShortcutKeyPressed(ev));
		}
		this.$dragEnd(ev);
	},
	$dragOver: function(ev){
		ev=eventGet(ev);
		if(!this.$bDrag)
			return;
		var nIcon;
		var t0=dwa.lv.benriFuncs.eventGetTarget(ev),doc=dwa.lv.benriFuncs.elGetOwnerDoc(t0);
		var e1=dwa.lv.benriFuncs.elGetAttr(t0,'com_ibm_dwa_ui_droppable',true);
		if(e1){
			this.$idTarget = e1.id;
			var c1=e1.className;
			var c2=e1.getAttribute('com_ibm_dwa_ui_dragoverClass');
			if(!c2)c2="s-dragover-default";
			if(-1==c1.indexOf(c2)){
				e1.className += ' ' + c2;
			}
			nIcon='move';
			if(!this.$fireEvent('dragover',t0,eventIsShortcutKeyPressed(ev)))
				return;
		}
		this.setDropIcon(nIcon);
	},
	$dragOut: function(ev){
		ev=eventGet(ev);
		if(!this.$bDrag)
			return;
		var t0=dwa.lv.benriFuncs.eventGetTarget(ev),doc=dwa.lv.benriFuncs.elGetOwnerDoc(t0);
		var e1=dwa.lv.benriFuncs.elGetAttr(t0,'com_ibm_dwa_ui_droppable',true);
		if(e1 && e1.id==this.$idTarget){
			var c1=e1.className;
			if(c1){
				var c2=e1.getAttribute('com_ibm_dwa_ui_dragoverClass');
				if(!c2)c2="s-dragover-default";
				var n1=c1.indexOf(c2);
				if( n1>-1 )
					e1.className = c1.substr(0,n1);
			}
			this.setDropIcon();
			this.$fireEvent('dragout',t0);
		}
	},
	$fireEvent: function(p1,p2,p3){
		var t1=this.$aTargets[this.$idTarget],ev={type:p1,srcElement:p2,ctrlKey:p3,returnValue:true},s1='on'+p1;
		if(t1 && t1[s1]){
			t1[s1](ev);
		}
		return ev.returnValue;
	},
	DD$Static: void 0,
	oInstancesStatic: {},
	getDragDropControlStatic: function(sId){
		if(!sId){
			if(!dwa.lv.dragdropControl.prototype.DD$Static)
				dwa.lv.dragdropControl.prototype.DD$Static = new dwa.lv.dragdropControl('com_ibm_dwa_misc_dragdropControl_reserved');
			return dwa.lv.dragdropControl.prototype.DD$Static;
		}else{
			if(!dwa.lv.dragdropControl.prototype.oInstancesStatic[sId])
				dwa.lv.dragdropControl.prototype.oInstancesStatic[sId] = new dwa.lv.dragdropControl(sId);
			return dwa.lv.dragdropControl.prototype.oInstancesStatic[sId];
		}
	},
	releaseDragDropControlStatic: function(sId){
		if('undefined' != typeof(dwa.lv.dragdropControl.prototype.oInstancesStatic[sId]))
			delete dwa.lv.dragdropControl.prototype.oInstancesStatic[sId];
	}
});
