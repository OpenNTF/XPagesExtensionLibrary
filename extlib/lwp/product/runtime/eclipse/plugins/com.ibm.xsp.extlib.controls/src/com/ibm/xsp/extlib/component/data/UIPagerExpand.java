/*
 * © Copyright IBM Corp. 2010, 2011
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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.model.DataModel;

import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.model.domino.DominoViewDataContainer;
import com.ibm.xsp.model.domino.DominoViewDataModel;
import com.ibm.xsp.model.domino.ViewNavigatorEx;
import com.ibm.xsp.model.domino.viewnavigator.NOIViewNavigatorEx;



/**
 * Pager that let a user define collapse/expand all the rows.
 */
public class UIPagerExpand extends AbstractPager {
	
	// Maximum items in a single request
	public static final int ALL_MAX = 1024;
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.PagerExpand"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.PagerExpand"; //$NON-NLS-1$

	// To be reintegrated in the PagerEvent
    /**
     * The action type identifier int for the collapse all action, see {@link PagerEvent} for the other identifiers.
     */
    public static final int ACTION_COLLAPSEALL 	= 20;
    /**
     * The action type identifier int for the expand all action, see {@link PagerEvent} for the other identifiers.
     */
    public static final int ACTION_EXPANDALL 	= 21;
	
	private String collapseText;
	private String expandText;
	
	public UIPagerExpand() {
		setRendererType(RENDERER_TYPE);
	}
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PAGER_EXPAND;
	}

	public String getCollapseText() {
		if(collapseText!=null) {
			return collapseText;
		}
		ValueBinding vb = getValueBinding("collapseText"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setCollapseText(String collapseText) {
		this.collapseText = collapseText;
	}

	public String getExpandText() {
		if(expandText!=null) {
			return expandText;
		}
		ValueBinding vb = getValueBinding("expandText"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setExpandText(String expandText) {
		this.expandText = expandText;
	}
    
    public void expandAll() {
    	ViewNavigatorEx nav = getNavigator();
    	if(nav!=null) {
    	    resetNavigatorBug(nav);
    		nav.expandAll();
        	FacesDataIterator dataIterator = findDataIterator();
        	if(dataIterator!=null) {
        		dataIterator.setFirst(0);
        	}
    	}
    }
    
    public boolean isExpandAll() {
    	ViewNavigatorEx nav = getNavigator();
    	if(nav!=null) {
    		return nav.getExpandLevel()==Integer.MAX_VALUE && !nav.hasCollapsedPaths();
    	}
    	return false;
    }
    
    public void collapseAll() {
    	ViewNavigatorEx nav = getNavigator();
    	if(nav!=null) {
            resetNavigatorBug(nav);
    		nav.collapseAll();
        	FacesDataIterator dataIterator = findDataIterator();
        	if(dataIterator!=null) {
        		dataIterator.setFirst(0);
        	}
    	}
    }
    
    public boolean isCollapseAll() {
    	ViewNavigatorEx nav = getNavigator();
    	if(nav!=null) {
    		return nav.getExpandLevel()<=1  && !nav.hasExpandedPaths();
    	}
    	return false;
    }

    //
    // Horrible fix for the Domino view navigator
    // 853 forgets to reset one of its private variable here, so we force it the hard way
    // from this pager.
    private static Field navigateSiblings;
    private void resetNavigatorBug(ViewNavigatorEx nav) {
        if(ExtLibUtil.isXPages853()) {
            // PHIL: 853 fix
            if(nav instanceof NOIViewNavigatorEx) {
                try {
                    if(navigateSiblings==null) {
                        navigateSiblings = AccessController.doPrivileged(
                           new PrivilegedAction<Field>() {
                               public Field run() {
                                   Class<?> c = NOIViewNavigatorEx.class;
                                   Field ff[] = c.getDeclaredFields();
                                   for(int i=0; i<ff.length; i++) {
                                       if(ff[i].getName().equals("navigateSiblings")) { // $NON-NLS-1$
                                           Field f = ff[i];
                                           f.setAccessible(true); // private member!
                                           return f;
                                       }
                                   }
                                   return null;
                               }
                           }
                        );
                    }
                    if(navigateSiblings!=null) {
                        navigateSiblings.setBoolean(nav, false);
                    }
                } catch(Throwable t) {
                    // Ignore every exception here. We just try to fix a bug.
                    // If it fails, then the bug just stays...
                }
            }
        }
    }
    
	protected ViewNavigatorEx getNavigator() {
    	FacesDataIterator dataIterator = findDataIterator();
    	if(dataIterator!=null) {
			DataModel dm = dataIterator.getDataModel();
			if(dm instanceof DominoViewDataModel) {
				DominoViewDataModel ddm = (DominoViewDataModel)dm;
				DominoViewDataContainer dc = ddm.getDominoViewDataContainer();
				if(dc!=null) {
					return ddm.getDominoViewDataContainer().getNavigator();
				}
			}
    	}
		return null;
	}
    
    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PagerEvent) {
            PagerEvent pe = (PagerEvent) event;
            switch (pe.getAction()) {
            	case ACTION_EXPANDALL: {
            		expandAll();
            	} break;
            	case ACTION_COLLAPSEALL: {
            		collapseAll();
            	} break;
            }
        }
    }
    
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		this.expandText = (String)values[1];
		this.collapseText = (String)values[2];
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[3];
		values[0] = super.saveState(context);
	    values[1] = expandText;
	    values[2] = collapseText;
		return values;
	}
}
