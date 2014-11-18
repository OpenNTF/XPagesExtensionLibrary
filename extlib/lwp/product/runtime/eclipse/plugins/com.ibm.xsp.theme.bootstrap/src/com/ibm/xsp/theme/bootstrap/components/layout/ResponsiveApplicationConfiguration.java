/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.theme.bootstrap.components.layout;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.component.layout.impl.BasicApplicationConfigurationImpl;

public class ResponsiveApplicationConfiguration extends BasicApplicationConfigurationImpl {
	

	public static final String WIDTH_FULL = "full"; //$NON-NLS-1$
	public static final String WIDTH_FLUID = "fluid"; //$NON-NLS-1$
	public static final String WIDTH_FIXED = "fixed"; //$NON-NLS-1$
	
	public static final String NAVBAR_FIXED_TOP 			= "fixed-top"; //$NON-NLS-1$
	public static final String NAVBAR_FIXED_BOTTOM 			= "fixed-bottom"; //$NON-NLS-1$
	public static final String NAVBAR_UNFIXED_TOP 			= "unfixed-top"; //$NON-NLS-1$
	
	private Boolean invertedNavbar;
    private String fixedNavbar;
    private Boolean collapseLeftColumn;
    private String collapseLeftTarget;
    private String collapseLeftMenuLabel;
    private String pageWidth;
	
	public ResponsiveApplicationConfiguration() {}
	
	//TODO add getRendererType back in future release if extra responsive layouts added 
    //@Override 
	//public String getRendererType() {
    //	return "com.ibm.xsp.theme.bootstrap.layout.ResponsiveApplicationConfiguratoin";
    //}
    
    public boolean isInvertedNavbar() {
        if(invertedNavbar != null) {
            return invertedNavbar;
        }
        ValueBinding vb = getValueBinding("invertedNavbar"); // $NON-NLS-1$
        if(vb!=null) {
            Boolean b = (Boolean)vb.getValue(getFacesContext());
            if(b!=null) {
                return b;
            }
        }
        return false;
    }
    public void setInvertedNavbar(boolean invertedNavbar) {
        this.invertedNavbar = invertedNavbar;
    }
    
    public String getFixedNavbar() {
    	if(fixedNavbar != null) {
            return fixedNavbar;
        }
        ValueBinding vb = getValueBinding("fixedNavbar"); // $NON-NLS-1$
        if(vb!=null) {
            String s = (String)vb.getValue(getFacesContext());
            if(s!=null && (StringUtil.equals(s, NAVBAR_FIXED_TOP) || StringUtil.equals(s, NAVBAR_FIXED_BOTTOM) || StringUtil.equals(s, NAVBAR_UNFIXED_TOP))) {
                return s;
            }
        }
        return null;
    }
    public void setFixedNavbar(String fixedNavbar) {
        this.fixedNavbar = fixedNavbar;
    }
    
    
    public boolean isCollapseLeftColumn() {
    	 if(collapseLeftColumn!=null) {
             return collapseLeftColumn;
         }
         ValueBinding vb = getValueBinding("collapseLeftColumn"); // $NON-NLS-1$
         if(vb!=null) {
             Boolean b = (Boolean)vb.getValue(getFacesContext());
             if(b!=null) {
                 return b;
             }
         }
         return false;
    }
    public void setCollapseLeftColumn(boolean collapseLeftColumn) {
		this.collapseLeftColumn = collapseLeftColumn;
	}
    
    public String getCollapseLeftTarget() {
        if(collapseLeftTarget!=null) {
            return collapseLeftTarget;
        }
        ValueBinding vb = getValueBinding("collapseLeftTarget"); // $NON-NLS-1$
        if(vb!=null) {
        	String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
    	return null;
	}
    public void setCollapseLeftTarget(String collapseLeftTarget) {
		this.collapseLeftTarget = collapseLeftTarget;
	}
    
    public String getCollapseLeftMenuLabel() {
        if(collapseLeftMenuLabel!=null) {
            return collapseLeftMenuLabel;
        }
        ValueBinding vb = getValueBinding("collapseLeftMenuLabel"); // $NON-NLS-1$
        if(vb!=null) {
        	String s = (String)vb.getValue(getFacesContext());
            if(s!=null) {
                return s;
            }
        }
		return null;
	}
    public void setCollapseLeftMenuLabel(String collapseLeftMenuLabel) {
		this.collapseLeftMenuLabel = collapseLeftMenuLabel;
	}
    
	public String getPageWidth() {
        if(pageWidth!=null) {
            return pageWidth;
        }
        ValueBinding vb = getValueBinding("pageWidth"); // $NON-NLS-1$
        if(vb!=null) {
        	String s = (String)vb.getValue(getFacesContext());
            if(s!=null && (StringUtil.equals(s, WIDTH_FIXED) || StringUtil.equals(s, WIDTH_FLUID) || StringUtil.equals(s, WIDTH_FULL))) {
                return s;
            }
        }
		return null;
    }
    public void setPageWidth(String pageWidth) {
    	this.pageWidth = pageWidth;
    }
    
    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        this.invertedNavbar = (Boolean)values[1];
        this.collapseLeftColumn = (Boolean)values[2];
        this.pageWidth = (String)values[3];
        this.fixedNavbar = (String)values[4];
        this.collapseLeftTarget = (String)values[5];
        this.collapseLeftMenuLabel = (String)values[6];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[7];
        values[0] = super.saveState(context);
        values[1] = invertedNavbar;
        values[2] = collapseLeftColumn;
        values[3] = pageWidth;
        values[4] = fixedNavbar;
        values[5] = collapseLeftTarget;
        values[6] = collapseLeftMenuLabel;
        return values;
    }
}
