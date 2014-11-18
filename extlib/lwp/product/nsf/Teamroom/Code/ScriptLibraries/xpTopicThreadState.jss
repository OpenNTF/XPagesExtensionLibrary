function TopicThreadState(){
	this.init = function init(){
		viewScope.TopicThread_state = new java.util.HashMap();
		// modifyPosition: String 'CURRENT' or rowEntry.getPosition()
		viewScope.TopicThread_state.modifyPosition = null; 
		// modifyEditOrNewReply: 'edit' or 'newReply'
		viewScope.TopicThread_state.modifyEditOrNewReply = null;
		// showBodyMap:
		// keys are: String 'CURRENT' or rowEntry.getPosition()
		// values are Boolean.FALSE(when key is 'CURRENT') 
		// or Boolean.TRUE (when key is a position)
		viewScope.TopicThread_state.showBodyMap = new java.util.HashMap(); 
	}
	this.initNewReply = function initNewReply(){
		// initially creating a new reply.
		// 1) hide the current doc, so it doesn't take up too much screen space
		// and you don't have to scroll down so far to the reply
		viewScope.TopicThread_state.showBodyMap['CURRENT'] = false;
		// 2) open the New Reply area for the current doc
		this._setModifyPosition('CURRENT', 'newReply');
	}
	this._reload = function _reload(dynamicContentArea){
		//<xe:changeDynamicContentAction for="currentDocArea">
		//</xe:changeDynamicContentAction>
		var action:com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction 
			= new com.ibm.xsp.extlib.actions.server.ChangeDynamicContentAction();
		action.setComponent(view);
		action.setFor(dynamicContentArea);
		action.invoke(facesContext, null);
	};
	this._setModifyPosition = function _setModifyPosition(position, editOrNewReply){
		viewScope.TopicThread_state.modifyPosition = position;
		viewScope.TopicThread_state.modifyEditOrNewReply = editOrNewReply;
	}
	this.changeCurrentHideBody = function changeCurrentHideBody(hide){
		if( hide ){
			viewScope.TopicThread_state.showBodyMap['CURRENT'] = false;
		}else{
			viewScope.TopicThread_state.showBodyMap.remove('CURRENT');
		}
		this._reload('currentDetailArea');
	};
	this.showCurrentNewReply = function changeCurrentNewReply(){
		
		var oldPosition = viewScope.TopicThread_state.modifyPosition;
		var oldEditOrNewReply = viewScope.TopicThread_state.modifyEditOrNewReply;
		
		this._setModifyPosition('CURRENT', 'newReply');
		this._reload('newReplyToCurrentArea');
		this._hideOldModifyArea(oldPosition, oldEditOrNewReply, 'CURRENT', 'newReply');
	};
	this.isPromptChangeEditArea = function isPromptChangeEditArea(){
		return null != viewScope.TopicThread_state.modifyPosition;
	};
	this.clearEditArea = function clearEditArea(){
		var oldPosition = viewScope.TopicThread_state.modifyPosition;
		var oldEditOrNewReply = viewScope.TopicThread_state.modifyEditOrNewReply;
		this._setModifyPosition(null, null);
		this._hideOldModifyArea(oldPosition, oldEditOrNewReply, null, null);
	};
	this._hideOldModifyArea = function _hideOldModifyArea(oldPosition, oldEditOrNewReply,
				newPosition, newEditOrNewReply){
		if( !oldPosition ){
			return;
		}
		// hide previously edited area
		if( 'CURRENT'== oldPosition ){
			if( 'edit' == oldEditOrNewReply ){
				this._reload("currentDocArea");
			}else{ // 'newReply'
				this._reload("newReplyToCurrentArea");
			}
		}else{ // position is a viewEntry position
			if( 'edit' == oldEditOrNewReply ){
				if( 'edit' == newEditOrNewReply ){
					// continue to display rowEditArea
				}else{ 
					// reload to show rowEditArea
					this._reload("rowEditArea");
				}
				// reload the show body area, so the
				// updated body value is reloaded.
				this._reload("rowDetailArea");
			}else{ // 'newReply'
				if( 'newReply' == newEditOrNewReply ){
					// continue to display rowReplyArea
				}else{
					// reload to show rowReplyArea
					this._reload("rowReplyArea");
				}
			}
		}
	}
	this.showCurrentEdit = function showCurrentEdit(){
		var oldPosition = viewScope.TopicThread_state.modifyPosition;
		var oldEditOrNewReply = viewScope.TopicThread_state.modifyEditOrNewReply;
		this._setModifyPosition('CURRENT', 'edit');
		this._reload("currentDocArea");
		this._hideOldModifyArea(oldPosition, oldEditOrNewReply, 'CURRENT', 'edit');
	};
	this._isShowSomeBodyRow = function _isShowSomeBodyRow (){
		var map = viewScope.TopicThread_state.showBodyMap; 
		if(map.isEmpty() || map.size() == 1 && map.containsKey('CURRENT') ){
				return false;
		}
		return true;
	}
	this.changeRowShowBody = function changeRowShowBody(show, row){
		if( show ){
			var oldShowSomeRow = this._isShowSomeBodyRow();
			
			viewScope.TopicThread_state.showBodyMap[row] = true;
			
			if( ! oldShowSomeRow ){
				this._reload("rowDetailArea");
			}
		}else{ //hide
			var removed = viewScope.TopicThread_state.showBodyMap.remove(row);
			if( removed ){
				var newShowSomeRow = this._isShowSomeBodyRow();
				if( !newShowSomeRow ){
					this._reload("rowDetailArea");
				}
			}// was not shown
		}
	};
	this.deleteRow = function deleteRow(row){
		this.changeRowShowBody(false, row);
	}
	this.showRowNewReply = function changeRowNewReply(row, showRowBody){
		// May be doing 2 actions: 
		// A) create new reply 
		// B) show current row body
		
		// A
		var oldPosition = viewScope.TopicThread_state.modifyPosition;
		var oldEditOrNewReply = viewScope.TopicThread_state.modifyEditOrNewReply;
		this._setModifyPosition(row, 'newReply');
		
		// B
		var oldShowSomeRow = this._isShowSomeBodyRow();
		viewScope.TopicThread_state.showBodyMap[row] = true;
		
		// A - show rowReplyArea if needed
		if( oldEditOrNewReply == 'newReply' && 'CURRENT' != oldPosition ){
			// already shown
		}else{
			this._reload("rowReplyArea");
		} 
		// A - hide previously edited area
		this._hideOldModifyArea(oldPosition, oldEditOrNewReply, row, 'newReply');
		// B - show current row if needed
		if( ! oldShowSomeRow ){
			this._reload("rowDetailArea");
		}
	};
	this.showRowEdit = function showRowEdit(row){
		var oldPosition = viewScope.TopicThread_state.modifyPosition;
		var oldEditOrNewReply = viewScope.TopicThread_state.modifyEditOrNewReply;
		this._setModifyPosition(row, 'edit');
		
		// reload rowEditArea
		// [Note if( oldEditRow && 'CURRENT' != oldEditRow )
		// then the row edit area will already be visible,
		// but reload it anyway to re-evaluate the load-time 
		// bindings - specifically to ensure the formName
		// is recalculated.]
		this._reload("rowEditArea");
		
		this._hideOldModifyArea(oldPosition, oldEditOrNewReply, row, 'edit');
	};
	this.isCurrentHideBody = function isCurrentHideBody(){
		return viewScope.TopicThread_state.showBodyMap.containsKey('CURRENT');
	};
	this.isCurrentNewReply = function isCurrentNewReply(){
		return 'CURRENT' == viewScope.TopicThread_state.modifyPosition
			&& 'newReply' == viewScope.TopicThread_state.modifyEditOrNewReply;
	};
	this.isCurrentEdit = function isCurrentEdit(){
		return 'CURRENT' == viewScope.TopicThread_state.modifyPosition
			&& 'edit' == viewScope.TopicThread_state.modifyEditOrNewReply;
	};
	this.isRowEditAny = function isRowEditAny(){
		return 'edit' == viewScope.TopicThread_state.modifyEditOrNewReply
			&& 'CURRENT' != viewScope.TopicThread_state.modifyPosition;
	};
	this.isRowShowBodyAny = function isRowShowBodyAny(){
		return this._isShowSomeBodyRow();
	};
	this.isRowShowBody = function isRowShowBody(row){
		return viewScope.TopicThread_state.showBodyMap.containsKey(row);
	};
	this.isRowNewReplyAny = function isRowNewReplyAny(){
		return 'newReply' == viewScope.TopicThread_state.modifyEditOrNewReply
			&& 'CURRENT' != viewScope.TopicThread_state.modifyPosition;
	};
	this.isRowNewReply = function isRowNewReply(row){
		return 'newReply' == viewScope.TopicThread_state.modifyEditOrNewReply
			&& row == viewScope.TopicThread_state.modifyPosition;
	}
	this.isRowEdit = function isRowEdit(row){
		return 'edit' == viewScope.TopicThread_state.modifyEditOrNewReply
			&& row == viewScope.TopicThread_state.modifyPosition;
	}
}
TopicThreadState = new TopicThreadState();