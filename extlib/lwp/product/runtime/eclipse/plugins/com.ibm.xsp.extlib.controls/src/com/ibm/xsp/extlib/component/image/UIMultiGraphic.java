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

package com.ibm.xsp.extlib.component.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.xp.XspGraphicImage;
import com.ibm.xsp.util.StateHolderUtil;


/**
 * Graphic class that can display an image selected from a list.
 */
public class UIMultiGraphic extends XspGraphicImage {

    // same as the superclass rendererType
    public static final String RENDERER_TYPE = "com.ibm.xsp.ImageEx"; //$NON-NLS-1$
    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.MultiImage"; //$NON-NLS-1$
    
    private List<IconEntry> icons;
    
    private transient boolean entry_set;
    private transient IconEntry entry;
	
	public UIMultiGraphic() {
	    // TODO UIMultiGraphic should possibly extend from UIOutput, and should have a defaultValue property.
		setRendererType(RENDERER_TYPE);
	}

    @Override
    public void _xspCleanTransientData() {
        super._xspCleanTransientData();
        if( null != icons && icons instanceof ArrayList){
            ((ArrayList<IconEntry>)icons).trimToSize();
        }
        this.entry = null;
        this.entry_set = false;
    }

	public List<IconEntry> getIcons() {
		return icons;
	}
	
	public void addIcon(IconEntry icon) {
		if(icons==null) {
			icons = new ArrayList<IconEntry>();
		}
		icons.add(icon);
	}

	@Override
    public String getAlt() {
		if(entry!=null) {
		    String alt = entry.getAlt();
		    if(null != alt){
		        return alt;
		    }
		}
    	return super.getAlt();
    }

    @Override
    public String getTitle() {
        // Check various places for the title - comparing
        // to null rather than to the empty string, because
        // for titles and alt's the empty string has a 
        // specific meaning and should be passed through
        
        // first check the xe:iconEntry for a title
        if(entry!=null) {
            // <xe:multiImage>
            //   <xe:this.icons>
            //     <xe:iconEntry title="foo" url="/foo.gif" />
            String iconEntryTitle = entry.getTitle();
            if( null != iconEntryTitle ){
                return iconEntryTitle;
            }
        }
        // then check the xe:multiImage for a title
        String multiImageTitle = super.getTitle();
        if( null != multiImageTitle ){
            // <xe:multiImage title="foo"/>
            return multiImageTitle;
        }
        // then check for an alt (on both xe:iconEntry and xe:multiImage)
        String anyAlt = getAlt();
        if( null != anyAlt ){
            return anyAlt;
        }
        return null;
    }

	@Override
    public Object getValue() {
		if(entry_set) {
			if(entry!=null) {
				return entry.getUrl();
			}
			return null;
		}
    	return super.getValue();
    }

	@Override
    public String getStyle() {
		if(entry!=null) {
			String iconEntryStyle = entry.getStyle();
			if( null != iconEntryStyle ){
			    return iconEntryStyle;
			}
		}
    	return super.getStyle();
    }

	@Override
    public String getStyleClass() {
		if(entry!=null) {
			String iconEntryStyleClass = entry.getStyleClass();
			if( null != iconEntryStyleClass ){
			    return iconEntryStyleClass;
			}
		}
    	return super.getStyleClass();
    }

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		// The icon entry is temporarily set when the component is rendered
		if(isRendered()) {
			this.entry = findIcon(context);
			this.entry_set = true;
		}
		super.encodeBegin(context);
	}

	@Override
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);
		this.entry = null;
		this.entry_set = false;
	}

	protected IconEntry findIcon(FacesContext context) {
		List<IconEntry> icons = getIcons(); 
		if(icons!=null && !icons.isEmpty()) {
			// Get the value
			Object value = getValue();
			for(IconEntry icon: icons) {
				// Check if the value is equal
				if(StringUtil.equals(value, icon.getSelectedValue())) {
					return icon;
				}
				// Else, check if it is selected
				if(icon.isSelected()) {
					return icon;
				}
			}
		}
		return null;
	}
	
	@Override
	public void restoreState(FacesContext context, Object state) {
		Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
        icons = StateHolderUtil.restoreList(context, this, values[1]);
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] values = new Object[2];
		values[0] = super.saveState(context);
        values[1] = StateHolderUtil.saveList(context, icons);
		return values;
	}
}
