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

package com.ibm.xsp.extlib.component.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.model.domino.DominoViewDataContainer;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.model.domino.ViewNavigatorEx;
import com.ibm.xsp.util.TypedUtil;


/**
 * Class that manages the state of iterator.
 * <p>
 * </p>
 */
public class FacesDataIteratorStateManager {
	
	private static final FacesDataIteratorStateManager instance = new FacesDataIteratorStateManager();
	public static FacesDataIteratorStateManager get() {
		return instance;
	}
	
	private static final String STATE_KEY = "_xsp.extlib.viewstate.states"; //$NON-NLS-1$
	private static final String GLOBALROWS_KEY = "_xsp.extlib.viewstate.rows";  //$NON-NLS-1$

	protected static class StateMap extends HashMap<String, State> {
		private static final long serialVersionUID = 1L;

		public StateMap() {
		}
	}
	
	public static interface State {
		public void saveState(FacesContext context, FacesDataIterator dataIterator);
		public void restoreState(FacesContext context, FacesDataIterator dataIterator, boolean fullState);
	}

	public static interface Options {
		public boolean isGlobalRows();
	}
	
	public static class BasicState implements State {

		private int first=-1;
		private int rows=-1;
		private int expandLevel;
		private Set<String> collapsedPaths;
		private Set<String> expandedPaths;
		private String sortColumn;
		private int resortState;

		public BasicState(Options options) {
		}

		public int getFirst() {
			return first;
		}

		public void setFirst(int first) {
			this.first = first;
		}

		public int getRows() {
			return rows;
		}

		public void setRows(int rows) {
			this.rows = rows;
		}

		public int getExpandLevel() {
			return expandLevel;
		}

		public void setExpandLevel(int expandLevel) {
			this.expandLevel = expandLevel;
		}

		public Set<String> getCollapsedPaths() {
			return collapsedPaths;
		}

		public void setCollapsedPaths(Set<String> collapsedPaths) {
			this.collapsedPaths = collapsedPaths;
		}

		public Set<String> getExpandedPaths() {
			return expandedPaths;
		}

		public void setExpandedPaths(Set<String> expandedPaths) {
			this.expandedPaths = expandedPaths;
		}

		public String getSortColumn() {
			return sortColumn;
		}

		public void setSortColumn(String sortColumn) {
			this.sortColumn = sortColumn;
		}

		public int getResortState() {
			return resortState;
		}

		public void setResortState(int resortState) {
			this.resortState = resortState;
		}

		public void saveState(FacesContext context, FacesDataIterator dataIterator) {
			// Save the view panel state
			setFirst(dataIterator.getFirst());
			setRows(dataIterator.getRows());
			
			TabularDataModel tm = getTabularDataModel(dataIterator);
			if(tm!=null) {
				String colName = tm.getResortColumn();
				setSortColumn(colName);
				setResortState(tm.getResortState(colName));
			}
			
			// Save the navigator state
			ViewNavigatorEx nav = getDominoViewNavigator(dataIterator);
			if(nav!=null) {
				setExpandLevel(nav.getExpandLevel());
				setCollapsedPaths(nav.getCollapsedPaths());
				setExpandedPaths(nav.getExpandedPaths());
				//setSortColumn(nav.getSortColumn());
				//setSortOrder(nav.getSortOrder());
			}
		}
		public void restoreState(FacesContext context, FacesDataIterator dataIterator, boolean fullState) {
			// Always restore the rows
			int rows = getRows();
			if(rows>=0) {
				dataIterator.setRows(rows);
			}

			// Then restore the full state
			if(fullState) {
				// init the view panel state
				int first = getFirst();
				if(first>=0) {
					dataIterator.setFirst(first);
				}
				
				// Init the resort order of the view
				// This cannot be done at the view navigator level, but at the data container one
				TabularDataModel tm = getTabularDataModel(dataIterator);
				if(tm!=null) {
					tm.setResortOrder(getSortColumn(), codeToParameter(getResortState()));
				}
				
				// init the navigator state
				ViewNavigatorEx nav = getDominoViewNavigator(dataIterator);
				if(nav!=null) {
					nav.setExpandLevel(getExpandLevel());
					nav.setCollapsedPaths(getCollapsedPaths());
					nav.setExpandedPaths(getExpandedPaths());
				}
			}
		}
		
		protected TabularDataModel getTabularDataModel(FacesDataIterator iterator) {
			DataModel dm = iterator.getDataModel();
			if(dm instanceof TabularDataModel) {
				return (TabularDataModel)dm;
			}
			return null;
		}
		protected ViewNavigatorEx getDominoViewNavigator(FacesDataIterator iterator) {
			DataModel dm = iterator.getDataModel();
			if(dm instanceof DominoViewDataModel) {
				DominoViewDataModel ddm = (DominoViewDataModel)dm;
				DominoViewDataContainer dc = ddm.getDominoViewDataContainer();
				if(dc!=null) {
					return dc.getNavigator();
				}
			}
			return null;
		}
		
	    protected String codeToParameter(int resort) {
	    	if (resort == TabularDataModel.RESORT_ASCENDING)
	    		return TabularDataModel.SORT_ASCENDING;
	    	if (resort == TabularDataModel.RESORT_DESCENDING)
	    		return TabularDataModel.SORT_DESCENDING;
	    	if (resort == TabularDataModel.RESORT_BOTH)
	    		return TabularDataModel.SORT_TOGGLE;
	    	return null;
	    }	}
	
	public FacesDataIteratorStateManager() {
	}
	
	public boolean restoreState(FacesContext context, UIComponent c, String key, boolean fullState) {
		if(StringUtil.isEmpty(key)) {
			return false;
		}
		Options options = null;
		FacesDataIterator dataIterator;
		if(c instanceof FacesDataIteratorStateHandler) {
			FacesDataIteratorStateHandler itx = (FacesDataIteratorStateHandler)c;
			dataIterator = itx.getFacesDataIterator(context);
			options = itx.getOptions();
		} else {
			dataIterator = (FacesDataIterator)c;
		}
		State state = findState(context, dataIterator, key);
		if(state!=null) {
			state.restoreState(context, dataIterator, fullState);
		}
		// Restore the global row #
		if(options!=null) {
			if(options.isGlobalRows()) {
				Map<String,Object> sessionMap = ExtLibUtil.getSessionScope();
				Integer rows = (Integer)sessionMap.get(GLOBALROWS_KEY);
				if(rows!=null) {
					dataIterator.setRows(rows);
				}
			}
		}
		return state!=null;
	}
	
	public boolean saveState(FacesContext context, UIComponent c, String key) {
		if(StringUtil.isEmpty(key)) {
			return false;
		}
		Options options = null;
		State state;
		FacesDataIterator dataIterator;
		if(c instanceof FacesDataIteratorStateHandler) {
			FacesDataIteratorStateHandler itx = (FacesDataIteratorStateHandler)c;
			state = itx.createDataIteratorState(context,null);
			dataIterator = itx.getFacesDataIterator(context);
			options = itx.getOptions();
		} else {
			state = new BasicState(null);
			dataIterator = (FacesDataIterator)c;
		}
		if(state!=null) {
			state.saveState(context, dataIterator);
			StateMap states = getIteratorStateMap(context, true);
			states.put(key,state);
		}
		// Save the global row #
		if(options!=null) {
			if(options.isGlobalRows()) {
				Map<String,Object> sessionMap = ExtLibUtil.getSessionScope();
				sessionMap.put(GLOBALROWS_KEY,dataIterator.getRows());
			}
		}
		return state!=null;
	}
	
	// Find the state object for an iterator
	protected State findState(FacesContext context, FacesDataIterator iterator, String key) {
		StateMap states = getIteratorStateMap(context, false);
		if(states!=null) {
			State state = states.get(key);
			return state;
		}
		return null;
	}
	
	private StateMap getIteratorStateMap(FacesContext context, boolean create) {
		Map<String, Object> sessionScope = TypedUtil.getSessionMap(context.getExternalContext());
		StateMap states = (StateMap)sessionScope.get(STATE_KEY);
		if(states==null && create) {
			states = new StateMap(); 
			sessionScope.put(STATE_KEY,states);
		}
		return states;
	}
}
