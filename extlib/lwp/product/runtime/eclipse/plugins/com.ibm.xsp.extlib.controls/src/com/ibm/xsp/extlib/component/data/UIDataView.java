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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.DataModel;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.model.TabularDataModel;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Custom data iterator that renders the content of a collection (view).
 * <p>
 * This iterator provides some predefined parts used to render the final 
 * markup.
 * </p>
 */
public class UIDataView extends AbstractDataView {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.DataView"; //$NON-NLS-1$
	public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.data.OneUICustomView"; //$NON-NLS-1$
    
    public static final String FACET_ICON				= "icon"; //$NON-NLS-1$
    public static final String FACET_CATEGORY_N			= "categoryRow"; //$NON-NLS-1$
    public static final String FACET_EXTRA_N			= "extra"; // With an index //$NON-NLS-1$

    // Accessibility
    private String role;
    private String summary;
    private String ariaLabel;

    // Options
    private Boolean columnTitles;
    private Boolean collapsibleRows;
    private Boolean collapsibleCategory;
    private Integer multiColumnCount;
    
	// High level columns
    private Boolean showCheckbox;    
    private Boolean showHeaderCheckbox;    
    private IconColumn iconColumn;
    private List<CategoryColumn> categoryColumn;
    private List<ExtraColumn> extraColumns;
    private String infiniteScroll;
    
	/**
	 * Note, the facet names based on {@link #FACET_EXTRA_N} and {@link #FACET_CATEGORY_N} are also row-based facets.
	 */
	private static final String[] ROW_FACET_NAMES = {
	    // Note, the superclass only has summary and detail, this adds icon and categoryRow.
			FACET_ICON, 
			FACET_SUMMARY, 
			FACET_DETAIL, 
	}; 
    
	public UIDataView() {
		// The data iterator implements the FacesInstanceClass which means that
		// an instance of the component is created at design time, to get the actual
		// class to generate. At that time, there isn't any FacesContext object so
		// a call to ThemeUtil will fail -> we have to catch the exception....
		//setRendererType("com.ibm.xsp.extlib.data.OneUIDataView");
	    if( null != FacesContextEx.getCurrentInstance() ){
	        setRendererType(RENDERER_TYPE);
	    }
	}
	
	@Override
	public String getStyleKitFamily() {
		return StyleKitExtLibDefault.DATAITERATOR_DATAVIEW;
	}

	public boolean isColumnTitles() {
		if(columnTitles!=null) {
			return columnTitles;
		}
		ValueBinding vb = getValueBinding("columnTitles"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setColumnTitles(boolean columnTitles) {
		this.columnTitles = columnTitles;
	}
	
	public boolean isCollapsibleRows() {
		if(collapsibleRows!=null) {
			return collapsibleRows;
		}
		ValueBinding vb = getValueBinding("collapsibleRows"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setCollapsibleRows(boolean collapsibleRows) {
		this.collapsibleRows = collapsibleRows;
	}
	
	public boolean isCollapsibleCategory() {
		if(collapsibleCategory!=null) {
			return collapsibleCategory;
		}
		ValueBinding vb = getValueBinding("collapsibleCategory"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return true; // non-default return value.
	}
	
	public void setCollapsibleCategory(boolean collapsibleCategory) {
		this.collapsibleCategory = collapsibleCategory;
	}

	public int getMultiColumnCount() {
		if(multiColumnCount!=null) {
			return multiColumnCount;
		}
		ValueBinding vb = getValueBinding("multiColumnCount"); //$NON-NLS-1$
		if(vb!=null) {
			Integer b = (Integer)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return 0;
	}
	
	public void setMultiColumnCount(int multiColumnCount) {
		this.multiColumnCount = multiColumnCount;
	}

	public boolean isShowHeaderCheckbox() {
		if(showHeaderCheckbox!=null) {
			return showHeaderCheckbox;
		}
		ValueBinding vb = getValueBinding("showHeaderCheckbox"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setShowHeaderCheckbox(boolean showHeaderCheckbox) {
		this.showHeaderCheckbox = showHeaderCheckbox;
	}

	public boolean isShowCheckbox() {
		if(showCheckbox!=null) {
			return showCheckbox;
		}
		ValueBinding vb = getValueBinding("showCheckbox"); //$NON-NLS-1$
		if(vb!=null) {
			Boolean b = (Boolean)vb.getValue(getFacesContext());
			if(b!=null) {
				return b;
			}
		}
		return false;
	}
	
	public void setShowCheckbox(boolean showCheckbox) {
		this.showCheckbox = showCheckbox;
	}
	
    public IconColumn getIconColumn() {
		return this.iconColumn;
	}

	public void setIconColumn(IconColumn iconColumn) {
		this.iconColumn = iconColumn;
	}
	
    public List<CategoryColumn> getCategoryColumn() {
        return categoryColumn;
    }
    
    public void addCategoryColumn(CategoryColumn column) {
        if(categoryColumn==null) {
            categoryColumn = new ArrayList<CategoryColumn>();
        }
        categoryColumn.add(column);
    }
	
	public List<ExtraColumn> getExtraColumns() {
		return extraColumns;
	}
	
	public void addExtraColumn(ExtraColumn column) {
		if(extraColumns==null) {
			extraColumns = new ArrayList<ExtraColumn>();
		}
		extraColumns.add(column);
	}
	
	public String getInfiniteScroll() {
        if (infiniteScroll != null) {
            return infiniteScroll;
        }
        ValueBinding vb = getValueBinding("infiniteScroll"); // $NON-NLS-1$
        if (vb != null) {
            String b = (String) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return null;
    }

    public void setInfiniteScroll(String infiniteScroll) {
        this.infiniteScroll = infiniteScroll;
    }
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
        columnTitles = (Boolean)values[1];
        collapsibleRows = (Boolean)values[2];
        collapsibleCategory = (Boolean)values[3];
        multiColumnCount = (Integer)values[4];
        showCheckbox = (Boolean) values[5];
        showHeaderCheckbox = (Boolean) values[6];
        iconColumn = (IconColumn)StateHolderUtil.restoreObjectState(context, this, values[7]);
        categoryColumn = StateHolderUtil.restoreList(context, this, values[8]);
        extraColumns = StateHolderUtil.restoreList(context, this, values[9]);
        role = (String) values[10];
        summary = (String) values[11];
        ariaLabel = (String) values[12];
        infiniteScroll = (String) values[13];
        
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[14];
		values[0] = super.saveState(context);
        values[1] = columnTitles;
        values[2] = collapsibleRows;
        values[3] = collapsibleCategory;
        values[4] = multiColumnCount;
        values[5] = showCheckbox;
        values[6] = showHeaderCheckbox;
        values[7] = StateHolderUtil.saveObjectState(context, iconColumn);
        values[8] = StateHolderUtil.saveList(context, categoryColumn);
        values[9] = StateHolderUtil.saveList(context, extraColumns);
        values[10] = role;
        values[11] = summary;
        values[12] = ariaLabel;
        values[13] = infiniteScroll;
		return values;
	}

	@Override
	public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		super.buildContents(context, builder);
		
        RowComponent row = (RowComponent)getChildren().get(0);
        
		List<ExtraColumn> extras = getExtraColumns();
        if( null != extras ){
    		for(int i = 0; i < extras.size(); i ++ ){
    		    if(i==0) {
                    if( builder.isFacetAvailable(context, row, FACET_EXTRA_N) ){
                        builder.buildFacet(context, row, FACET_EXTRA_N);
                        continue;
                    }
    		    }
    			String facetName = FACET_EXTRA_N+i;
    			if( builder.isFacetAvailable(context, row, facetName) ){
    				builder.buildFacet(context, row, facetName);
    			}
    		}
		}
        
        List<CategoryColumn> categoryList = getCategoryColumn();
        
        // to allow apps created before 2011-10-09 to continue working,
        // support the "categoryRow" facet even when categoryList is absent.
        if( builder.isFacetAvailable(context, row, FACET_CATEGORY_N) ){
            builder.buildFacet(context, row, FACET_CATEGORY_N);
            if( null == categoryList ){
                // make a pseudo-category-column.
                CategoryColumn categoryColumn = new CategoryColumn();
                addCategoryColumn(categoryColumn);
                categoryList = getCategoryColumn();
            }
        }
        
        if( null != categoryList ){
            for(int i = 0; i < categoryList.size(); i ++ ){
                if(i==0) {
                    // "categoryRow" has already been build,
                    // but may build "categoryRow0" here
                }
                String facetName = FACET_CATEGORY_N+i;
                if( builder.isFacetAvailable(context, row, facetName) ){
                    builder.buildFacet(context, row, facetName);
                }
            }
        }
    }

	/**
	 * The names of the facets that should be created in the child row control,
	 * and should be repeated for each row. The {@link #FACET_EXTRA_N} and {@link #FACET_CATEGORY_N} facet
	 * names are also repeated for each row, though they are not included in
	 * this array.
	 * 
	 * @return
	 */
	@Override
	protected String[] getRowFacetNames() {
		return ROW_FACET_NAMES;
	}
	
    /**
     * Return the ids as an array
     * @return
     */
    public String[] getSelectedIds() {
        // TODO this method is unused, why is it here? 
        TabularDataModel dataModel = getTabularDataModel();
        if (dataModel == null) {
            return NO_IDS;
        }
        List<String> ids = new ArrayList<String>();
        for (Iterator<?> i = dataModel.getSelectedIds(); i.hasNext(); ) {
            ids.add((String)i.next());
        }
        return StringUtil.toStringArray(ids);
    }
    private static final String[] NO_IDS = new String[0];
    
    /**
     * Convenience method for retrieving the <code>TabularDataModelEx</code> object
     * associated with this component.
     * @return the TabularDataModel
     */
    protected TabularDataModel getTabularDataModel() {
        DataModel dataModel = getDataModel();
        if (dataModel instanceof TabularDataModel) {
            return (TabularDataModel) dataModel;
        }
        return null;
    }

    /**
     * @return the role
     */
    public String getRole() {
        if (null != this.role) {
            return this.role;
        }
        ValueBinding vb = getValueBinding("role"); //$NON-NLS-1$
        if (vb != null) {
            return (java.lang.String) vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
	
    public String getSummary() {
        if (null != this.summary) {
            return this.summary;
        }
        ValueBinding vb = getValueBinding("summary"); //$NON-NLS-1$
        if (vb != null) {
            return (java.lang.String) vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAriaLabel() {
        if (null != this.ariaLabel) {
            return this.ariaLabel;
        }
        ValueBinding vb = getValueBinding("ariaLabel"); //$NON-NLS-1$
        if (vb != null) {
            return (java.lang.String) vb.getValue(FacesContext.getCurrentInstance());
        } else {
            return null;
        }
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }

}
