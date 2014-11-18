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

package com.ibm.xsp.extlib.component.dojoext.form;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ThemeUtil;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Dojo component used to input a values based on a series of images. 
 * <p>
 * </p>
 * @author Philippe Riand
 */
public class UIDojoExtImageSelect extends AbstractDojoExtImageSelect {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dojoext.form.ImageSelect"; // $NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dojoext.form.ImageSelect"; //$NON-NLS-1$
    
    private List<SelectImage> imageValues;
    
    public UIDojoExtImageSelect() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DOJO_FORM_IMAGESELECT;
    }

    public List<SelectImage> getImageValues() {
        return imageValues;
    }
    
    public void addImageValue(SelectImage value) {
        if(imageValues==null) {
            imageValues = new ArrayList<SelectImage>();
        }
        imageValues.add(value);
    }

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.imageValues = StateHolderUtil.restoreList(_context, this, _values[1]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[2];
        _values[0] = super.saveState(_context);
        _values[1] = StateHolderUtil.saveList(_context, imageValues);
        return _values;
    }
    
    
    @Override
    public int getImageCount() {
        return imageValues!=null ? imageValues.size() : 0;
    }
    
    @Override
    public ISelectImage getImage(int index) {
        return imageValues.get(index);
    }   
}