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

package com.ibm.xsp.extlib.designer.tooling.palette.singlepageapp;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;

/**
 * @author Gary Marjoram
 *
 */
public class WizardPageCombo {
    private   Combo              combo;
    private   WizardDataSub      pageData;
    private   boolean            addNone;
    private   ArrayList<String>  pageNames;
    private   ArrayList<Integer> pageIndexes;
    private   int                filter;
    
    public WizardPageCombo(Composite parent, int span, WizardDataSub pd, boolean addnone, int fltr) {
        combo = WizardUtils.createCombo(parent, span, null);
        pageData = pd;
        addNone = addnone;
        filter = fltr;
        pageNames = new ArrayList<String>();
        pageIndexes = new ArrayList<Integer>();
    }
    
    public void refresh() {
        int idx = getSelectedIndex();
        
        pageNames.clear();
        pageIndexes.clear();
        
        if (addNone) {
            pageNames.add(""); 
            pageIndexes.add(-1);
        }
        
        String[] names = WizardData.getInstance().getPageNameList();
        boolean addEntry;
        for (int i=0; i<names.length; i++) {
            addEntry = true;
            
            // Don't add this page
            if (pageData != null) {
                if (i == pageData.index) {
                    addEntry = false;
                }
            }           
            
            // Check is a filter in place
            if (addEntry && (filter != WizardData.PAGE_TYPE_INVALID)) {
                if (filter != WizardData.getInstance().pageList.get(i).type) {
                    addEntry = false;
                }
            }
            
            if (addEntry) {
                pageNames.add(names[i]);
                pageIndexes.add(i);
            }
        }
        combo.setItems(pageNames.toArray(new String[pageNames.size()]));
        
        setSelectedIndex(idx);
    }
    
    public int getSelectedIndex() {
        int idx = WizardUtils.getComboIndex(combo, -1); 
        if (idx >= 0) {
            return pageIndexes.get(idx);
        }
        
        return -1;
    }
    
    public void setSelectedIndex(final int idx) {
        for (int i=0; i<pageIndexes.size(); i++) {
            if (pageIndexes.get(i) == idx) {
                combo.select(i);
                return;
            }
        }
        
        combo.select(-1);
    }    
    
    public void pageDeleted(final int idx) {
        int comboIdx = getSelectedIndex();
        refresh();
        if (comboIdx == idx) {
            setSelectedIndex(-1);
        }
        else if (comboIdx > idx) {
            setSelectedIndex(comboIdx - 1);
        }
    }
    
    public int getOptionCount() {
        return pageNames.size(); 
    }
    
    public void setSelectedItem(final int item) {
        combo.select(item);
    }
}
 