/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.common.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.extension.ExtensionManager;
import com.ibm.commons.iloader.node.lookups.api.AbstractLookup;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.LookupListener;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.xsp.stylekit.StyleKitFactory;
import com.ibm.xsp.stylekit.StyleKitListFactory;

/**
 * @author mleland
 *
 */
public class AppThemeLookup extends AbstractLookup implements LookupListener {
    
    // Hardcoded Theme Labels and Codes
    public final static String[]    theme_Standard_Ids      = {"webstandard", "oneui", "oneuiv2", "oneuiv2.1", "oneuiv3.0.2"};    // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$ $NON-NLS-5$
    public final static String[]    theme_Standard_Labels   = {"webstandard", "OneUI", "OneUI V2", "OneUI V2.1", "OneUI V3.0.2"}; // $NLX-AppThemeLookup.webstandard_sys-1$ $NLX-AppThemeLookup.OneUI.1_sys-2$ $NLX-AppThemeLookup.OneUIV2_sys-3$ $NLX-AppThemeLookup.OneUIV21_sys.1-4$ $NLX-AppThemeLookup.OneUIV302_sys-5$
    
    public final static String[]    theme_Mobile_Ids        = {"oneui_idx_v1.3"}; // $NON-NLS-1$
    public final static String[]    theme_Mobile_Labels     = {"OneUI IDX V1.3"}; // $NLX-AppThemeLookup.OneUIIDXV13-1$
    
    public final static String[]    theme_IPhone_Ids        = {"oneui_idx_v1.3", "iphone"}; // $NON-NLS-1$ $NON-NLS-2$
    public final static String[]    theme_IPhone_Labels     = {"OneUI IDX V1.3", "iPhone"}; // $NLX-AppThemeLookup.OneUIIDXV13-1$ $NLX-AppThemeLookup.iPhone-2$
    
    public final static String[]    theme_Android_Ids       = {"oneui_idx_v1.3", "android"}; // $NON-NLS-1$ $NON-NLS-2$
    public final static String[]    theme_Android_Labels    = {"OneUI IDX V1.3", "Android"}; // $NLX-AppThemeLookup.OneUIIDXV13-1$ $NLX-AppThemeLookup.Android-2$

    // StyleKitList Factories to suppress
    private final static String[]   SUPPRESS_FACTORIES      = {"com.ibm.xsp.extlib.oneui.themes.OneUIStyleKitFactory", // $NON-NLS-1$
                                                               "com.ibm.xsp.extlib.mobile.themes.MobileStyleKitFactory", // $NON-NLS-1$
                                                               "com.ibm.xsp.theme.oneui_idx.ThemeStyleKitFactory", // $NON-NLS-1$
                                                               "com.ibm.xsp.theme.oneuiv302.ThemeStyleKitFactory"}; // $NON-NLS-1$
    
    // Extension sometimes used with themes
    private final static String     THEME_EXT               = ".theme"; // $NON-NLS-1$
    
    // Private Fields
    private final String            _defCode;
    private final String            _defLabel;
    private final DesignerDELookup  _themeLookup;
    private final String[]          _hardCodedLabels;
    private final String[]          _hardCodedIds;
    private       List<Theme>       _themeList;
    
    /*
     * Theme Sources
     */
    private enum AppThemeSource {
        THEME_SRC_HARDCODED,
        THEME_SRC_APPLICATION,
        THEME_SRC_CONTRIBUTED
    };
    
    /*
     * Utilty Class for representing a theme
     */
    private class Theme {
        private       String         _label;
        private final String         _id;
        private final AppThemeSource _src;
        
        /*
         * Contructor
         */
        public Theme(final String label, final String id, final AppThemeSource src) {
            _id = id;
            _src = src;
            if (src == AppThemeSource.THEME_SRC_APPLICATION) {
                _label = StringUtil.format("{0} (Application)", label);   // $NLX-AppThemeLookup.0Application-1$
            } else {
                _label = label;
            }
        }
        
        /*
         * Gets the display label
         */
        public String getLabel() {
            return _label != null ? _label : _id;
        }
        
        /*
         * Gets the ID
         */
        public String getId() {
            return _id;
        }
        
        /*
         * Gets the theme source
         */
        public AppThemeSource getSrc() {
            return _src;
        }
        
        /*
         * Forces a theme label
         */
        public void setLabel(String newLabel) {
            _label = newLabel;
        }
    }
  
    public AppThemeLookup(final DesignerProject prj, final String defCode, final String defLabel, 
                          final String[] hardCodedIds, final String[] hardCodedLabels) {
        _defCode = defCode;
        _defLabel = defLabel;
        _hardCodedIds = hardCodedIds;
        _hardCodedLabels = hardCodedLabels;
        
        // Create the theme list
        _themeList = new ArrayList<Theme>();
        
        // Get the application theme lookup
        _themeLookup = DesignerDELookup.getThemesLookup(prj);
        _themeLookup.addLookupListener(this);
        
        // Now load the themes
        loadThemes();
    }
    
    /*
     * Loads all the themes from scratch
     */
    private void loadThemes() {
        _themeList.clear();
        
        // Add the application themes
        loadApplicationThemes();
        
        // Add contributed themes
        loadContributedThemes();
        
        // Add the hard-coded themes
        loadHardCodedThemes();
        
        // Handle duplicate labels
        fixDuplicateLabels();

        // Sort - must do this last
        sortThemes();   
    }
    
    /*
     * Loads Application themes
     */
    private void loadApplicationThemes() {
        // Add in the application themes
        for (int i=0; i < _themeLookup.size(); i++) {
            addTheme(new Theme(_themeLookup.getLabel(i), _themeLookup.getLabel(i).concat(THEME_EXT), AppThemeSource.THEME_SRC_APPLICATION));
        }
    }
    
    /*
     * Loads themes contributed through the extension point
     */
    private void loadContributedThemes() {    
        // Get the styleKitFactories
        List<StyleKitFactory> styleKitFactories = ExtensionManager.findServices(
                null, 
                this.getClass().getClassLoader(), 
                StyleKitFactory.STYLEKIT_FACTORY_SERVICE, 
                StyleKitFactory.class);
        
        // Remove suppressed factories
        Iterator<StyleKitFactory> iterator = styleKitFactories.iterator();
        while (iterator.hasNext()) {
            String factoryName = iterator.next().getClass().getCanonicalName();
            for (String suppress: SUPPRESS_FACTORIES) {
                if (StringUtil.equalsIgnoreCase(suppress, factoryName)) {
                    iterator.remove();
                    break;
                }
            }
        }        
        
        // Look for instances of StyleKitListFactory
        for (StyleKitFactory styleKitFactory : styleKitFactories) {
            if( styleKitFactory instanceof StyleKitListFactory ){
                StyleKitListFactory listFactory = (StyleKitListFactory) styleKitFactory;
                String[] themeIds = listFactory.getThemeIds();
                if (themeIds != null) {
                    for (String themeId:themeIds) {
                        addTheme(new Theme(themeId, themeId, AppThemeSource.THEME_SRC_CONTRIBUTED));                    
                    }
                }
            }
        }
    }
    
    /*
     * Loads the hard-coded themes
     */
    private void loadHardCodedThemes() {
        // Add the hardcoded themes
        for (int i=0; i < _hardCodedLabels.length; i++) {
            addTheme(new Theme(_hardCodedLabels[i], _hardCodedIds[i], AppThemeSource.THEME_SRC_HARDCODED));
        }        
    }
        
    /*
     * Adds a theme to the theme list whilst enforcing theme precedence
     * Returns true if theme was added
     */
    private boolean addTheme(final Theme theme) {
        // Assume success
        boolean added = true;
        
        // Check for an existing theme with this ID
        Theme existingTheme = getThemeFromId(theme.getId());
        if (existingTheme == null) {
            // Not on list already - add it
            _themeList.add(theme);
        } else {
            // There's an existing theme with this ID on the list
            switch (theme.getSrc()) {
                case THEME_SRC_APPLICATION:
                    // Application themes trump all others
                    _themeList.remove(existingTheme);                    
                    _themeList.add(theme);
                    break;
                    
                case THEME_SRC_CONTRIBUTED:
                    if (existingTheme.getSrc() == AppThemeSource.THEME_SRC_HARDCODED) {
                       // Contributed themes trump hard-coded ones 
                        _themeList.remove(existingTheme);
                        _themeList.add(theme);
                    } else if (existingTheme.getSrc() == AppThemeSource.THEME_SRC_CONTRIBUTED) {
                        // Multiple contributed themes with this ID - use the ID as the label
                        existingTheme.setLabel(null);
                        added = false;
                    } else {
                        added = false;
                    }
                    break;
                    
                case THEME_SRC_HARDCODED:
                    // Hardcoded themes do not trump any others
                    added = false;
                    break;
            }
        }
        
        return added;
    }
            
    /*
     * Gets a theme from the list using its ID
     */
    private Theme getThemeFromId(final String Id) {
        String compareId = removeThemeIdExtension(Id);
        for (Theme theme:_themeList) {
            if (StringUtil.equalsIgnoreCase(compareId, removeThemeIdExtension(theme.getId()))) {
                return theme;
            }
        }
        return null;
    }
    
    /*
     * Removes the ".theme" extension from an ID
     */
    private String removeThemeIdExtension(final String Id) {
        if (StringUtil.endsWithIgnoreCase(Id, THEME_EXT)) {
            return Id.substring(0, Id.length() - THEME_EXT.length());
        }
        return Id;
    }
    
    /*
     * Sorts the theme list alphabetically
     */
    private void sortThemes() {
        Collections.sort(_themeList, new Comparator<Theme>() {
            @Override
            public int compare(Theme t1, Theme t2) {
                return StringUtil.compareToIgnoreCase(t1.getLabel(), t2.getLabel());
            }
        });    
    }
    
    /*
     * Fixes any duplicate labels on the theme list
     */
    private void fixDuplicateLabels() {
        List<Theme> newList = new ArrayList<Theme>();
        
        // Loop until we've removed all items from the list
        while (_themeList.size() > 0) {
            // Remove the head of the list as the target
            Theme theme = _themeList.remove(0);
            
            // Iterate looking for duplicate labels
            boolean duplicates = false;
            Iterator<Theme> iterator = _themeList.iterator();
            while (iterator.hasNext()) {
                Theme listTheme = iterator.next();
                if (StringUtil.equalsIgnoreCase(listTheme.getLabel(), theme.getLabel())) {
                    // Found a dubplicate label - remove from existing list
                    iterator.remove();
                    
                    // Include the ID in the new label and add to the new list
                    listTheme.setLabel(StringUtil.format("{0} [{1}]", listTheme.getLabel(), listTheme.getId())); //$NLX-AppThemeLookup.ThemeLabelFormat-1$
                    newList.add(listTheme);
                    
                    // Record the fact we've found a duplicate
                    duplicates = true;
                }
            }       
            
            if (duplicates) {
                // Duplicate(s) were found - change the label of the target
                theme.setLabel(StringUtil.format("{0} [{1}]", theme.getLabel(), theme.getId())); //$NLX-AppThemeLookup.ThemeLabelFormat-1$
            }
            
            // Add the target to the new list
            newList.add(theme);
        }
        
        // Swap in the new list
        _themeList = newList;
    }
    
    /*
     * Removes the listener
     */
    public void dispose() {
        _themeLookup.removeLookupListener(this);
    }
    
    /*
     * Returns the size of the lookup
     */
    @Override
    public int size() {
        // +1 for the default theme
        return (_themeList.size() + 1);
    }
    
    /*
     * Returns the ID for a chosen index
     */
    @Override
    public String getCode(final int index) {
        if (index == 0) {
            return _defCode;
        }
        
        if ((index > 0) && (index <= _themeList.size())) {
            return _themeList.get(index-1).getId();
        }

        return "";
    }
    
    /*
     * Returns the label for a chosen index
     */
    @Override
    public String getLabel(final int index) {
        if (index == 0) {
            return _defLabel;
        }
        
        if ((index > 0) && (index <= _themeList.size())) {
            return _themeList.get(index-1).getLabel();
        }

        return "";
    }
    
    /*
     * Comparison function
     */
    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof AppThemeLookup) {
            if(((AppThemeLookup)obj).size() != this.size()) {
                return false;
            }
            for(int i = 0; i < size(); i++) {
                if(!StringUtil.equals(getCode(i), ((AppThemeLookup)obj).getCode(i))) {
                   return false; 
                }
                if(!StringUtil.equals(getLabel(i), ((AppThemeLookup)obj).getLabel(i))) {
                    return false; 
                 }
            }
            return true;
        }
        return super.equals(obj);
    }
    
    /*
     * Reloads everything
     */
    @Override
    public void lookupChanged(final ILookup lookup) {
        loadThemes();
        notifyLookupChanged();
    }    
    
}