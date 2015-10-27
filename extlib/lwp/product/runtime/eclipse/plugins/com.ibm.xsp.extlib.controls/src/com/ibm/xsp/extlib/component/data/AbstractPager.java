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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.component.UIDataEx;
import com.ibm.xsp.event.PagerEvent;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.HtmlUtil;



/**
 * Abstract base class for developing pagers.
 * <p>
 * Pagers are used to present options that drive a data iterator (views...), for example
 * to change the current page or the number of rows being displayed.<br>
 * This class contains convenient method that help writting pagers. 
 * </p>
 */
public class AbstractPager extends UIPanel implements FacesComponent, ThemeControl {
	
	public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.AbstractPager"; //$NON-NLS-1$
	public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.data.Pager"; //$NON-NLS-1$
    
    public static final String PAGER_ADDROWS_DEFAULT_ROWCOUNT_PROPERTY = "xsp.pager.addrows.defaultRowCount"; //$NON-NLS-1$
    public static final String PAGER_ADDROWS_DEFAULT_ROWCOUNT_DEFVAL = String.valueOf(UIDataEx.DEFAULT_ROWS_PER_PAGE); //$NON-NLS-1$

    protected int defaultRowCount;
    
	private String _for;
	private Boolean partialExecute;
	private Boolean partialRefresh;
	private String refreshId;
    private String style;
    private String styleClass;
    private String title;
    private String ariaLabel;

	private transient FacesDataIterator dataIterator;
	
	public AbstractPager() {
	    super();
        
        ApplicationEx app = ApplicationEx.getInstance();
        defaultRowCount = Integer.parseInt(app.getApplicationProperty(PAGER_ADDROWS_DEFAULT_ROWCOUNT_PROPERTY, PAGER_ADDROWS_DEFAULT_ROWCOUNT_DEFVAL));
	}

	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.PAGER;
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getFor() {
		if(_for!=null) {
			return _for;
		}
		ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setFor(String _for) {
		this._for = _for;
	}

	public boolean isPartialExecute() {
		if(partialExecute!=null) {
			return partialExecute;
		}
		ValueBinding vb = getValueBinding("partialExecute"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return true;
	}
	
	public void setPartialExecute(boolean partialExecute) {
		this.partialExecute = partialExecute;
	}

	public boolean isPartialRefresh() {
		if(partialRefresh!=null) {
			return partialRefresh;
		}
		ValueBinding vb = getValueBinding("partialRefresh"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return true;
	}
	
	public void setPartialRefresh(boolean partialRefresh) {
		this.partialRefresh = partialRefresh;
	}

	public String getRefreshId() {
		if(refreshId!=null) {
			return refreshId;
		}
		ValueBinding vb = getValueBinding("refreshId"); //$NON-NLS-1$
		if(vb!=null) {
			return (String)vb.getValue(getFacesContext());
		}
		return null;
	}

	public void setRefreshId(String partialRefreshId) {
		this.refreshId = partialRefreshId;
	}

	public String getStyle() {
		if (null != this.style) {
			return this.style;
		}
		ValueBinding vb = getValueBinding("style"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleClass() {
		if (null != this.styleClass) {
			return this.styleClass;
		}
		ValueBinding vb = getValueBinding("styleClass"); //$NON-NLS-1$
		if (vb != null) {
			return (String) vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	public java.lang.String getAriaLabel() {
		if (null != this.ariaLabel) {
			return this.ariaLabel;
		}
		ValueBinding _vb = getValueBinding("ariaLabel"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(getFacesContext());
		} else {
			return null;
		}
	}

	public String getTitle() {
		if (null != this.title) {
			return this.title;
		}
		ValueBinding _vb = getValueBinding("title"); //$NON-NLS-1$
		if (_vb != null) {
			return (java.lang.String) _vb.getValue(FacesContext.getCurrentInstance());
		} else {
			return null;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public void setAriaLabel(java.lang.String ariaLabel) {
		this.ariaLabel = ariaLabel;
	}
	
	// Should this be factor out to another class?
    public FacesDataIterator findDataIterator() {
        if (dataIterator != null) {
            return dataIterator;
        }

        String dataId = (String) getAttributes().get("for"); //$NON-NLS-1$
        if (dataId != null) {
            UIComponent data = findComponent(dataId);
            if (null == data) {
                // Scenario where a pager inside a custom control is 
                // attached to a data iterator outside that control
                data = FacesUtil.getComponentFor(this, dataId);
            }
            if (data instanceof FacesDataIterator) {
                dataIterator = (FacesDataIterator) data;
                return dataIterator;
            }
            return null;
        }

        dataIterator = findDataParent(getParent());
        return dataIterator;
    }
    private FacesDataIterator findDataParent(UIComponent component) {
        if (component == null) {
            return null;
        }
        if (component instanceof FacesDataIterator) {
            return (FacesDataIterator) component;
        }
        return findDataParent(component.getParent());
    }
    public UIComponent findSharedDataPagerParent() {
        String dataId = getFor();
        if (dataId != null) {
            UIComponent data = findComponent(dataId);
            if (data instanceof FacesDataIterator) {
                UIComponent dataParent = data;
                while (dataParent != null) {
                    UIComponent pagerParent = getParent();
                    while (pagerParent != null) {
                        if (pagerParent == dataParent) {
                            // Need to check that the shared parent has a user set Id as components
                            // with autogenerated Id's are not guaranteed to generate output
                            // that is capable of being partially refreshed.
                            while (dataParent!=null) {
                            	if(HtmlUtil.isUserId(dataParent.getId())) {
                            		break;
                            	}
                            	if(dataParent instanceof UIForm) {
                            		// Form always generate an ID, even when it is auto-generated.
                            		break;
                            	}
                                dataParent = dataParent.getParent();
                            }
                            return dataParent;
                        }
                        pagerParent = pagerParent.getParent();
                    }
                    dataParent = dataParent.getParent();
                }
            }
        }
        return null;
    }
    
    @Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PagerEvent) {
            // Tell JSF to switch to render response, like regular commands
            FacesContext context = getFacesContext();
            context.renderResponse();
        }
    }

    @Override
	public void queueEvent(FacesEvent e) {
        if (e instanceof PagerEvent) {
            if (isPartialExecute()) {
                e.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else {
                e.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
        }
        super.queueEvent(e);
    }
    
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this._for = (String)values[1];
		this.partialExecute = (Boolean)values[2];
		this.partialRefresh = (Boolean)values[3];
		this.refreshId = (String)values[4];
		this.style = (String)values[5];
		this.styleClass = (String)values[6];
		this.ariaLabel = (String)values[7];
		this.title = (String)values[8];	
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[9];
		values[0] = super.saveState(context);
	    values[1] = _for;
	    values[2] = partialExecute;
	    values[3] = partialRefresh;
	    values[4] = refreshId;
	    values[5] = style;
	    values[6] = styleClass;
	    values[7] = ariaLabel;
	    values[8] = title;
	    
		return values;
	}
	

    
    // ===================================================================
    // Faces Component Methods
    // ===================================================================

    public void initBeforeContents(FacesContext context) throws FacesException {
        // Nothing
    }

    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        // Standard stuff
        builder.buildAll(context, this, true);
    }

    public void initAfterContents(FacesContext context) throws FacesException {
        //Do nothing, the default implementation
    }
}
