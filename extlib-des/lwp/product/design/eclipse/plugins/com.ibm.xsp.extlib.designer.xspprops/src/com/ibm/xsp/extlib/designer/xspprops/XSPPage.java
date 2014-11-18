/*
 * © Copyright IBM Corp. 2011, 2012
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
package com.ibm.xsp.extlib.designer.xspprops;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNode.ComputedField;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.data.controls.DCCheckbox;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCText;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;
import com.ibm.designer.domino.ui.commons.extensions.DesignerResource;
import com.ibm.xsp.extlib.designer.common.properties.AppThemeLookup;

/**
 * @author mgl
 *
 * Project: IBM Lotus Domino Designer
 * Unit XSPPage
 */
public class XSPPage extends DCPanel implements XSPAllPropertyConstants {
    private FormToolkit toolkit = null;
    private XSPParentEditor ourEditor = null;
    private DCCheckbox defError = null;
    private DCComboBox errPage = null;
    private Label epLabel = null;
    private String[] versionCodes = {"9.0.1","9.0.0","8.5.3","8.5.2", "8.5.1", "3.0"};
    private String[] versionLabels = {"Release 9.0.1", "Release 9.0.0", "Release 8.5.3","Release 8.5.2", "Release 8.5.1", "Release 8.5"};   // $NLX-XSPPage.Release901-1$ $NLX-XSPPage.Release854-2$ $NLX-XSPPage.Release853-3$ $NLX-XSPPage.Release852-4$ $NLX-XSPPage.Release851-5$ $NLX-XSPPage.Release85-6$
    private static final String SERVER_DEFAULT = "Server default";  // $NLX-XSPPage.Serverdefault-1$
    private static final String APP_DEFAULT = "Application default"; // $NLX-XSPPage.Applicationdefault-1$
    private static final String PLATFORM_DEFAULT = "Platform default"; // $NLX-XSPPage.Platformdefault-1$
    private static final String MOBILE_DEFAULT = "Mobile default";     // $NLX-XSPPage.Mobiledefault-1$
    private CustomCheckBox overrideWeb = null;
    private CustomCheckBox overrideNotes = null;
    private CustomCheckBox overrideIOS = null;
    private CustomCheckBox overrideAndroid = null;
    private CustomCheckBox debugUserAgent = null;
    private DCComboBox themeNotesCombo = null;
    private DCComboBox themeWebCombo = null;
    private DCComboBox mobileCombo = null;
    private DCComboBox themeIOSCombo = null;
    private DCComboBox themeAndroidCombo = null;
    private DCComboBox debugUserAgentCombo = null;
    private DCPanel leftComposite = null;
    private static final String LINK_DEF_PROP = "defaultLinkTarget";    // $NON-NLS-1$
    
    private String[] debugUserAgentStarterCodes = {"iOS", "Android"};  // $NON-NLS-1$ $NON-NLS-2$
    private String[] debugUserAgentStarterLabels = {"iOS", "Android"};  // $NLX-XSPPage.iOS-1$ $NLX-XSPPage.Android-2$
     
    private CustomCheckBox mobilePrefixCheckbox;
    private DCText mobilePrefixText;
    
    private XSPAllProperties props = null;

    private AppThemeLookup atl = null;
    private AppThemeLookup atl2 = null;
    private AppThemeLookup matl = null;
    private AppThemeLookup matlAndroid = null;
    private AppThemeLookup matlIPhone = null;
    private DCPanel xspDesignPanel;
    
    private class MobilePrefixField extends ComputedField{
        private IAttribute mobileTheme;
        /**
         * @param name
         * @param type
         */
        public MobilePrefixField() {
            super("mobilePrefixComputed", ComputedField.TYPE_STRING); // $NON-NLS-1$
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#isReadOnly()
         */
        @Override
        public boolean isReadOnly() {
            return false;
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#getValue(java.lang.Object)
         */
        @Override
        public String getValue(Object instance) throws NodeException {
            if(instance instanceof XSPAllProperties){
                if(mobileTheme == null){
                    DataNode dn = leftComposite.getDataNode();
                    if(dn != null){
                        IMember member = dn.getMember("mobilePrefix"); // $NON-NLS-1$
                        if(member instanceof IAttribute){
                            mobileTheme = (IAttribute)member;
                        }
                    }
                }
                String prefix = leftComposite.getDataNode().getValue(mobileTheme);
                if(StringUtil.isEmpty(prefix)){
                    return "m_"; // $NON-NLS-1$
                }
                else{
                    return StringUtil.getNonNullString(prefix);
                }
            }
            return null;
        }

        /* (non-Javadoc)
         * @see com.ibm.commons.iloader.node.DataNode.ComputedField#setValue(java.lang.Object, java.lang.String, com.ibm.commons.iloader.node.DataChangeNotifier)
         */
        @Override
        public void setValue(Object instance, String value, DataChangeNotifier notifier) throws NodeException {
            if(instance instanceof XSPAllProperties){
                if(mobileTheme == null){
                    DataNode dn = leftComposite.getDataNode();
                    if(dn != null){
                        IMember member = dn.getMember("mobilePrefix"); // $NON-NLS-1$
                        if(member instanceof IAttribute){
                            mobileTheme = (IAttribute)member;
                        }
                    }
                }
                leftComposite.getDataNode().setValue(mobileTheme, value, null);
            }
        }
    }
    
    public XSPPage(Composite parent, FormToolkit ourToolkit, XSPParentEditor dpe) {
        super(parent, SWT.NONE);
        toolkit = ourToolkit;
        ourEditor = dpe;
        // use this to populate the list of themes in the other lookups
        atl = new AppThemeLookup(ourEditor.getDominoDesignerProject(), "", PLATFORM_DEFAULT, AppThemeLookup.theme_Standard_Ids, AppThemeLookup.theme_Standard_Labels);
        atl2 = new AppThemeLookup(ourEditor.getDominoDesignerProject(), "", APP_DEFAULT, AppThemeLookup.theme_Standard_Ids, AppThemeLookup.theme_Standard_Labels);
        matl = new AppThemeLookup(ourEditor.getDominoDesignerProject(), "", MOBILE_DEFAULT, AppThemeLookup.theme_Mobile_Ids, AppThemeLookup.theme_Mobile_Labels);
        matlIPhone = new AppThemeLookup(ourEditor.getDominoDesignerProject(), "", APP_DEFAULT, AppThemeLookup.theme_IPhone_Ids, AppThemeLookup.theme_IPhone_Labels);
        matlAndroid = new AppThemeLookup(ourEditor.getDominoDesignerProject(), "", APP_DEFAULT, AppThemeLookup.theme_Android_Ids, AppThemeLookup.theme_Android_Labels);
        
        this.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent arg0) {
                atl.dispose();  // free up the theme listeners
                atl2.dispose();
                matl.dispose();
                matlIPhone.dispose();
                matlAndroid.dispose();
            }
        });
        
        initialize();
    }
    
    private ScrolledForm initialize() {
        //setParentPropertyName("xspProperties"); // $NON-NLS-1$
        GridLayout ourLayout = new GridLayout(1, false);
        ourLayout.marginHeight = 0;
        ourLayout.marginWidth = 0;
        setLayout(ourLayout);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        ScrolledForm scrolledForm = toolkit.createScrolledForm(this);
        scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite formatComposite = XSPEditorUtil.createFormComposite(scrolledForm);

        XSPEditorUtil.createCLabel(formatComposite, "XPage Properties", 2); // $NLX-XSPPage.XPageProperties-1$
        
        createLeftSide(formatComposite);
        createRightSide(formatComposite);
        return scrolledForm;
    }

    private void createLeftSide(Composite formatComposite) {
        leftComposite = new DCPanel(formatComposite, SWT.NONE);
        leftComposite.setParentPropertyName("xspProperties"); // $NON-NLS-1$
        leftComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        leftComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        leftComposite.setLayout(gridLayout);
        
        createGeneralArea(leftComposite);
    }
        
    private void createRightSide(Composite formatComposite) {
        Composite rtComposite = new Composite(formatComposite, SWT.NONE);
        rtComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        rtComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 20;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        rtComposite.setLayout(gridLayout);
        
        createBrowserOptions(rtComposite);
    }
    
    private void createGeneralArea(Composite parent) {
        Section themeSide = XSPEditorUtil.createSection(toolkit, parent, "Theme Defaults", 1, 1); // $NLX-XSPPage.ThemeDefaults-1$
        Composite themeContainer = XSPEditorUtil.createSectionChild(themeSide, 2);
        
        Label tLabel = XSPEditorUtil.createLabel(themeContainer, "Application theme:", 1); // $NLX-XSPPage.Applicationtheme-1$
        tLabel.setToolTipText("The theme to be used in this application."); // $NLX-XSPPage.Thethemetobeusedinthisapplication-1$
        DCComboBox themeCombo = XSPEditorUtil.createDCCombo(themeContainer, "theme", 1, false, false); // $NON-NLS-1$
        themeCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
        
        themeCombo.setLookup(atl);
        themeCombo.setEditableLabels(true);
        
        overrideWeb = XSPEditorUtil.createIndentedCheck(themeContainer, "Override on Web:", "overrideweb", 18); // $NON-NLS-2$ $NLX-XSPPage.OverrideonWeb-1$
        overrideWeb.setToolTipText("If set, this theme is used on the web instead of what's specified as the application theme."); // $NLX-XSPPage.Ifsetthisthemeisusedonthewebinste-1$
        overrideWeb.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!overrideWeb.getSelection()) {
                    themeWebCombo.select(0);
                    leftComposite.getDataNode().setModelModified(true);
                    leftComposite.getDataNode().notifyInvalidate(null);
                }
                themeWebCombo.setEnabled(overrideWeb.getSelection());
            }
        });
        themeWebCombo = XSPEditorUtil.createDCCombo(themeContainer, "themeWeb", 1, false, false); // $NON-NLS-1$
        themeWebCombo.setLookup(atl2);
        themeWebCombo.setEditableLabels(true);
  
        overrideNotes = XSPEditorUtil.createIndentedCheck(themeContainer, "Override on Notes:", "overridenotes", 18); // $NON-NLS-2$ $NLX-XSPPage.OverrideonNotes-1$
        overrideNotes.setToolTipText("If set, this theme is used in the Notes client instead of what's specified as the application theme."); // $NLX-XSPPage.IfsetthisthemeisusedintheNotescli-1$
        overrideNotes.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!overrideNotes.getSelection()) {
                    themeNotesCombo.select(0);
                    leftComposite.getDataNode().setModelModified(true);
                    leftComposite.getDataNode().notifyInvalidate(null);
                }
                themeNotesCombo.setEnabled(overrideNotes.getSelection());
            }
        });
        themeNotesCombo = XSPEditorUtil.createDCCombo(themeContainer, "themeNotes", 1, false, false); // $NON-NLS-1$
        themeNotesCombo.setLookup(atl2);
        themeNotesCombo.setEditableLabels(true);
        
        Composite mobileParent = new Composite(themeContainer, SWT.NONE);
        mobileParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        GridData mobileGd = SWTLayoutUtils.createGDFillHorizontal();
        mobileGd.horizontalSpan = 2;
        mobileParent.setLayoutData(mobileGd);
        mobilePrefixCheckbox = new CustomCheckBox(mobileParent, SWT.CHECK, "");
        mobilePrefixCheckbox.setText("Use mobile theme for XPages with the prefix:"); // $NLX-XSPPage.UsemobilethemeforXPageswiththepre-1$
        mobilePrefixCheckbox.setToolTipText("If set, the XPages Runtime will apply the default mobile theme or\nthe override theme when a request URL contains the page prefix."); // $NLX-XSPPage.IfsettheXPagesRuntimewillapplythe-1$
        mobilePrefixCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean selected = ((CustomCheckBox)event.widget).getSelection();
                IAttribute mobileTheme = (IAttribute) leftComposite.getDataNode().getMember("mobilePrefix"); // $NON-NLS-1$
                try {
                    if(selected){
                        String val = mobilePrefixText.getText();
                        leftComposite.getDataNode().setValue(mobileTheme, val, null);
                        mobilePrefixText.setEnabled(true);
                        setMobileControlsState(true, false);
                    }
                    else{
                        leftComposite.getDataNode().setValue(mobileTheme, null, null);                        
                        mobilePrefixText.setEnabled(false);
                        setMobileControlsState(false, false);
                    }
                } catch (NodeException e) {
                    e.printStackTrace();
                }
                leftComposite.getDataNode().setModelModified(true);
                leftComposite.getDataNode().notifyInvalidate(null);
            }
        });
        SWTUtils.setBackgroundColor(mobileParent, themeContainer.getBackground());
        mobilePrefixText = XSPEditorUtil.createText(mobileParent, "mobilePrefixComputed", 1, 1, 7); // $NON-NLS-1$
        
        leftComposite.getDataNode().addComputedField(new MobilePrefixField());
        
        Label mLabel = XSPEditorUtil.createIndentedLabel(themeContainer, "Mobile theme:", 1, 18); // $NLX-XSPPage.Mobiletheme-1$
        mLabel.setToolTipText("The mobile theme to be used by this application.");  // $NLX-XSPPage.Themobilethemetobeusedbythisappli-1$
        mobileCombo = XSPEditorUtil.createDCCombo(themeContainer, "mobileTheme", 1, false, false); // $NON-NLS-1$
        mobileCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));            
        mobileCombo.setLookup(matl);
        mobileCombo.setEditableLabels(true);
             
        overrideIOS = XSPEditorUtil.createIndentedCheck(themeContainer, "Override on iOS:", "overrideIOS", 36);  //  $NON-NLS-2$ $NLX-XSPPage.OverrideoniOS-1$
        overrideIOS.setToolTipText("If set, this theme is used instead of the Mobile default theme for iOS.");  // $NLX-XSPPage.IfsetthisthemeisusedinsteadoftheM-1$
        overrideIOS.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!overrideIOS.getSelection()) {
                    themeIOSCombo.select(0);
                    leftComposite.getDataNode().setModelModified(true);
                    leftComposite.getDataNode().notifyInvalidate(null);
                }
                themeIOSCombo.setEnabled(overrideIOS.getSelection());
            }
        });
        themeIOSCombo = XSPEditorUtil.createDCCombo(themeContainer, "themeIOS", 1, false, false); // $NON-NLS-1$
        themeIOSCombo.setLookup(matlIPhone);
        themeIOSCombo.setEditableLabels(true);
        
        overrideAndroid = XSPEditorUtil.createIndentedCheck(themeContainer, "Override on Android:", "overrideAndroid", 36);  //  $NON-NLS-2$ $NLX-XSPPage.OverrideonAndroid-1$
        overrideAndroid.setToolTipText("If set, this theme is used instead of the Mobile default theme for Android.");  // $NLX-XSPPage.IfsetthisthemeisusedinsteadoftheM.1-1$
        overrideAndroid.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!overrideAndroid.getSelection()) {
                    themeAndroidCombo.select(0);
                    leftComposite.getDataNode().setModelModified(true);
                    leftComposite.getDataNode().notifyInvalidate(null);
                }
                themeAndroidCombo.setEnabled(overrideAndroid.getSelection());
            }
        });
        themeAndroidCombo = XSPEditorUtil.createDCCombo(themeContainer, "themeAndroid", 1, false, false); // $NON-NLS-1$
        themeAndroidCombo.setLookup(matlAndroid);
        themeAndroidCombo.setEditableLabels(true);
        
        debugUserAgent = XSPEditorUtil.createIndentedCheck(themeContainer, "Debug user agent:", "debugUserAgent", 18);  //  $NON-NLS-2$ $NLX-XSPPage.Debuguseragent-1$
        String toolTip = "Use this setting to view this application under the context of the selected user agent within a standard browser.\n" + // $NLX-XSPPage.Whenresettingthisoptionaserverres-1$
                         "This setting should only be used for development purposes and disabled for production deployment."; // $NLX-XSPPage.Whenresettingthisoptionaserverres.1-1$
        debugUserAgent.setToolTipText(toolTip); 
        debugUserAgent.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!debugUserAgent.getSelection()) {
                    debugUserAgentCombo.deselectAll();
                    leftComposite.getDataNode().setModelModified(true);
                    leftComposite.getDataNode().notifyInvalidate(null);
                } else {
                    debugUserAgentCombo.select(0);                    
                }
                debugUserAgentCombo.setEnabled(debugUserAgent.getSelection());
            }
        });
        debugUserAgentCombo = XSPEditorUtil.createDCCombo(themeContainer, "themeDebugUserAgent", 1, true, false); // $NON-NLS-1$
        debugUserAgentCombo.setLookup(new StringLookup(debugUserAgentStarterCodes, debugUserAgentStarterLabels));
        debugUserAgentCombo.setEditableLabels(true);
        
        Section errorSection = XSPEditorUtil.createSection(toolkit, parent, "Error Handling", 1, 1); // $NLX-XSPPage.ErrorHandling-1$
        Composite errContainer = XSPEditorUtil.createSectionChild(errorSection, 2);
        
        defError = XSPEditorUtil.createCheckboxTF(errContainer, "Display XPage runtime error page", "defaultErrorPage", 2); // $NON-NLS-2$ $NLX-XSPPage.DisplayXPageruntimeerrorpage-1$
        defError.setToolTipText("When set, the default error page is displayed by the XSP layer.  This is very useful\nin development as it provides extra information on the error"); // $NLX-XSPPage.indevelopmentasitprovidesextrainf-1$
        defError.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                enableErrorOptions();
            }
        });
        
        epLabel = XSPEditorUtil.createIndentedLabel(errContainer, "Error page:", 1, 18); // $NLX-XSPPage.Errorpage-1$
        epLabel.setToolTipText("Defines an XSP specific error page.  When not defined, it displays a default error page"); // $NLX-XSPPage.DefinesanXSPspecificerrorpageWhen-1$
        errPage = XSPEditorUtil.createDCCombo(errContainer, "errorPage", 1, true, true); // $NON-NLS-1$
        errPage.setFirstBlankLine(true);
        errPage.setFirstLineTitle("Standard server error page"); // $NLX-XSPPage.Standardservererrorpage-1$
        DesignerDELookup xpageLookup = new DesignerDELookup(ourEditor.getDominoDesignerProject(), DesignerResource.TYPE_XPAGE, false) {
            public String getCode(int index) {
                String xpLabel = getLabel(index);
                return (xpLabel + ".xsp"); // $NON-NLS-1$
            }
        };
        errPage.setLookup(xpageLookup);
        
        Section timeouts = XSPEditorUtil.createSection(toolkit, parent, "Timeouts", 1, 1); // $NLX-XSPPage.Timeouts-1$
        Composite toContainer = XSPEditorUtil.createSectionChild(timeouts, 2);
        
        Label atLabel = XSPEditorUtil.createLabel(toContainer, "Application timeout:", 1); // $NLX-XSPPage.Applicationtimeout-1$
        atLabel.setToolTipText("Defines when an application is discarded from memory after a period of inactivity.  When not set here or at the \nserver level, the default timeout is 30 minutes.");  // $NLX-XSPPage.serverlevelthedefaulttimeoutis30m.1-1$
        CustomComposite atComp = XSPEditorUtil.createZeroMarginComposite(toContainer, 2, 1, 10, "appTimeoutComp"); // $NON-NLS-1$
        DCText appTimeout = XSPEditorUtil.createText(atComp, "appTimeout", 1, 0, 6); // $NON-NLS-1$
        appTimeout.setValidator(IntegerValidator.positiveInstance);
        XSPEditorUtil.createLabel(atComp, "(minutes)", 1); // $NLX-XSPPage.minutes-1$
        
        Label stLabel = XSPEditorUtil.createLabel(toContainer, "Session timeout:", 1); // $NLX-XSPPage.Sessiontimeout-1$
        stLabel.setToolTipText("Defines when a user session is discarded from memory after a period of inactivity.  When not set here or at the \nserver level, the default timeout is 30 minutes.");  // $NLX-XSPPage.serverlevelthedefaulttimeoutis30m-1$
        CustomComposite stComp = XSPEditorUtil.createZeroMarginComposite(toContainer, 2, 1, 10, "sessTimeoutComp"); // $NON-NLS-1$
        DCText sessTimeout = XSPEditorUtil.createText(stComp, "sessionTimeout", 1, 0, 6); // $NON-NLS-1$
        sessTimeout.setValidator(IntegerValidator.positiveInstance);
        XSPEditorUtil.createLabel(stComp, "(minutes)", 1); // $NLX-XSPPage.minutes.1-1$
        
        Label partialUpdateTimeoutLabel = XSPEditorUtil.createLabel(toContainer, "Partial update timeout:", 1); // $NLX-XSPPage.Partialupdatetimeout-1$
        partialUpdateTimeoutLabel.setToolTipText("Allows you to configure the partial update timeout.  When not set here or at the \nserver level, the default value is 20 seconds.");  // $NLX-XSPPage.serverlevelthedefaultvalueis20sec-1$
        CustomComposite putComp = XSPEditorUtil.createZeroMarginComposite(toContainer, 2, 1, 10, "putTimeoutComp"); // $NON-NLS-1$
        DCText partialUpdateTimeout = XSPEditorUtil.createTextNoFill(putComp, "partialUpdateTimeout", 1, 0, 6); // $NON-NLS-1$
        partialUpdateTimeout.setValidator(IntegerValidator.positiveInstance);
        XSPEditorUtil.createLabel(putComp, "(seconds)", 1); // $NLX-XSPPage.seconds-1$

        // have positive logic, so reverse true/false
        DCCheckbox trSessions = XSPEditorUtil.createCheckbox(toContainer, "Persist sessions between requests", "sessionTransient", 2, Boolean.FALSE.toString(), Boolean.TRUE.toString()); // $NON-NLS-2$ $NLX-XSPPage.Persistsessionsbetweenrequests-1$
        trSessions.setToolTipText("When false, sessions are transient, and are not saved between requests (nor are pages)."); // $NLX-XSPPage.Whenfalsesessionsaretransientanda-1$
        
        DCCheckbox designChange = XSPEditorUtil.createCheckboxTF(toContainer, "Refresh entire application when design changes", "forceFullRefresh", 2); // $NON-NLS-2$ $NLX-XSPPage.Refreshentireapplicationwhendesig-1$
        designChange.setToolTipText("When this property is set to true, a full application refresh is requested when the design of a class changes (means that all the data are discarded in scopes)."); // $NLX-XSPPage.Whenthispropertyissettotrueafulla-1$
        
        Section fileUploadSection = XSPEditorUtil.createSection(toolkit, parent, "File Upload Options", 1, 1); // $NLX-XSPPage.FileUploadOptions-1$
        Composite flContainer = XSPEditorUtil.createSectionChild(fileUploadSection, 2);

        Label upLabel = XSPEditorUtil.createLabel(flContainer, "Maximum size:", 1); // $NLX-XSPPage.Maximumsize-1$
        upLabel.setToolTipText("Controls the maximum size of a file being uploaded as an attachment"); // $NLX-XSPPage.Controlsthemaximumsizeofafilebein-1$
        CustomComposite maxComp = XSPEditorUtil.createZeroMarginComposite(flContainer, 2, 1, 10, "maxContainer"); // $NON-NLS-1$
        DCText uploadMax = XSPEditorUtil.createText(maxComp, "uploadMax", 1, 0, 10); // $NON-NLS-1$
        uploadMax.setValidator(IntegerValidator.positiveInstance);
        XSPEditorUtil.createLabel(maxComp, "(KB)", 1); // $NLX-XSPPage.KB-1$
        
        Label upDirLabel = XSPEditorUtil.createLabel(flContainer, "Directory:", 1); // $NLX-XSPPage.Directory-1$
        upDirLabel.setToolTipText("Directory used to temporarily store the uploaded attachment. Defaults to a temporary directory returned by the OS"); // $NLX-XSPPage.Directoryusedtotemporarilystoreth-1$
        XSPEditorUtil.createText(flContainer, "uploadDir", 1, 0, 0); // $NON-NLS-1$
        
        themeSide.setClient(themeContainer);
        timeouts.setClient(toContainer);
        errorSection.setClient(errContainer);
        fileUploadSection.setClient(flContainer);
    }
    
    private void createBrowserOptions(Composite parent) {
        
        Section windowSection = XSPEditorUtil.createSection(toolkit, parent, "Window Behavior for Navigation and Links", 1, 1); // $NLX-XSPPage.WindowBehaviorforNavigationandLin-1$
        DCPanel windowDCP = XSPEditorUtil.createDCPanel(windowSection, 1, "xspProperties", "windowPanel"); // $NON-NLS-1$ $NON-NLS-2$
        windowDCP.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, 1, 1));
        Label navLabel = XSPEditorUtil.createLabel(windowDCP, "(Notes client only)", 1); // $NLX-XSPPage.Notesclientonly-1$
        navLabel.setToolTipText("Link behavior when not specified on the link itself."); // $NLX-XSPPage.Linkbehaviorwhennotspecifiedonthe-1$
        XSPEditorUtil.createRadio(windowDCP, LINK_DEF_PROP, SERVER_DEFAULT, XSPAllProperties.SERVER_DEFVAL, "windowServer", 1); // $NON-NLS-1$ $NON-NLS-2$
        XSPEditorUtil.createRadio(windowDCP, LINK_DEF_PROP, "Open in same window and tab", XSP_LINK_TARGET_SAME_VAL, "windowSame", 1);  //  $NON-NLS-2$ $NLX-XSPPage.Openinsamewindowandtab-1$
        XSPEditorUtil.createRadio(windowDCP, LINK_DEF_PROP, "Open in new window or tab (per client preference)", XSP_LINK_TARGET_NEW_WINDOW, "windowNew", 1); //  $NON-NLS-2$ $NLX-XSPPage.Openinnewwindowortabperclientpref-1$

        Section versionSection = XSPEditorUtil.createSection(toolkit, parent, "Minimum Supported Release ", 1, 1); // $NLX-XSPPage.MinimumSupportedRelease-1$
        Composite c = toolkit.createComposite(versionSection);
        c.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        c.setLayoutData(SWTLayoutUtils.createGDFill());
        DCPanel versionDCP = XSPEditorUtil.createDCPanel(c, 2, "xspProperties", "versionPanel"); // $NON-NLS-1$ $NON-NLS-2$
        versionDCP .setLayoutData(new GridData(SWT.BACKGROUND, SWT.FILL, false, false, 2, 1));
        Label minVerLabel = XSPEditorUtil.createLabel(versionDCP, "Compile this application to run on:", 2); // $NLX-XSPPage.Compilethisapplicationtorunon-1$
        minVerLabel.setToolTipText("If set to a specific version, design time errors are generated when features are \nused that are not available in the specified release."); // $NLX-XSPPage.usedthatarenotavailableinthespeci-1$
        DCComboBox versionBox = XSPEditorUtil.createDCCombo(versionDCP, "minVersionLevel", 2, true, false); // $NON-NLS-1$
        versionBox.setLookup(new StringLookup(versionCodes, versionLabels));
        versionBox.setFirstBlankLine(true);
        versionBox.setFirstLineTitle("Minimum release required by the XPage features used"); // $NLX-XSPPage.MinimumreleaserequiredbytheXPagef-1$
        
        DCCheckbox old = XSPEditorUtil.createCheckboxTF(versionDCP, "Use 8.5.0 style and styleClass property", "oldCDStyle", 2); // $NON-NLS-2$ $NLX-XSPPage.Use850styleandstyleClassproperty-1$
        old.setToolTipText("In 8.5.0 style and styleClass attributes were set as a base property but in 8.5.1 and beyond they are set \nas compositeData properties and referred to in the inner custom control as compositeData.style/styleClass. \nSet to true to revert to 8.5 behavior, default is false."); // $NLX-XSPPage.Settotruetorevertto85behaviordefa-1$
        
        xspDesignPanel = XSPEditorUtil.createDCPanel(c, 1, "xspDesignProps", "xspDesignProps"); // $NON-NLS-1$ $NON-NLS-2$
        XSPEditorUtil.createCheckboxTF(xspDesignPanel, "Ignore errors for unknown namespace URIs", "allowNamespaceMarkupTags", 1); // $NON-NLS-2$ $NLX-XSPPage.IgnoreerrorsforunknownnamespaceUR-1$
        Section dojoSection = XSPEditorUtil.createSection(toolkit, parent, "Dojo", 1, 1); // $NLX-XSPPage.Dojo-1$
        DCPanel dojoComp = XSPEditorUtil.createDCPanel(dojoSection, 2, "xspProperties", "dojoSection"); // $NON-NLS-2$ $NON-NLS-1$
        Label djvLabel = XSPEditorUtil.createLabel(dojoComp, "Dojo version:", 1); // $NLX-XSPPage.Dojoversion-1$
        djvLabel.setToolTipText("The version of the Dojo Toolkit to use. By default the Dojo version is detected \nby examining the folder Data/domino/js/ for subfolders with names like dojo-<version>, \nand using the latest version available.  Change this setting if you are installing \ndifferent versions of Dojo in that folder and you need XPages to use a specific version. \nNote, using XPages with a Dojo version other than the default is unsupported; if you do \nso you will need to test for compatibility problems."); // $NLX-XSPPage.soyouwillneedtotestforcompatibili-1$
        XSPEditorUtil.createText(dojoComp, "dojoVersion", 1, 0, 0); // $NON-NLS-1$
        Label djcLabel = XSPEditorUtil.createLabel(dojoComp, "Dojo parameters:", 1); // $NLX-XSPPage.Dojoparameters-1$
        djcLabel.setToolTipText("Add parameters to the djConfig attribute of Dojo.  Useful to switch Dojo to debug, \nusing for example: isDebug:true"); // $NLX-XSPPage.usingforexampleisDebugtrue-1$
        XSPEditorUtil.createText(dojoComp, "dojoConfig", 1, 0, 0); // $NON-NLS-1$
        
        Section tzValSection = XSPEditorUtil.createSection(toolkit, parent, "Time Zone", 1, 1); // $NLX-XSPPage.TimeZone-1$
        DCPanel tzComp = XSPEditorUtil.createDCPanel(tzValSection, 4, "xspProperties", "tzSection"); // $NON-NLS-1$ $NON-NLS-2$
        tzComp.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, 1, 1));
        
        Label tzLabel = XSPEditorUtil.createLabel(tzComp, "Time zone:", 1); // $NLX-XSPPage.Timezone-1$
        tzLabel.setToolTipText("Defines the timezone to use.  When not specified, it uses the server timezone."); // $NLX-XSPPage.DefinesthetimezonetouseWhennotspe-1$
        XSPEditorUtil.createRadio(tzComp, "timeZone", SERVER_DEFAULT, XSPAllProperties.SERVER_DEFVAL, "tzServer", 1); // $NON-NLS-1$ $NON-NLS-2$
        XSPEditorUtil.createRadio(tzComp, "timeZone", "Browser", "true", "tzOn", 1);  // $NON-NLS-1$ $NON-NLS-3$ $NON-NLS-4$ $NLX-XSPPage.Browser-2$
        XSPEditorUtil.createRadio(tzComp, "timeZone", "Server", "false", "tzoff", 1); // $NON-NLS-1$ $NON-NLS-3$ $NON-NLS-4$ $NLX-XSPPage.Server-2$
        
        DCCheckbox tzrt = XSPEditorUtil.createCheckboxTF(tzComp, "Round trip time zone", "roundTripTZ", 4); // $NON-NLS-2$ $NLX-XSPPage.Roundtriptimezone-1$
        tzrt.setToolTipText("Round trip time zone handling"); // $NLX-XSPPage.Roundtriptimezonehandling-1$
        
        windowSection.setClient(windowDCP);
        versionSection.setClient(c);
        tzValSection.setClient(tzComp);
        dojoSection.setClient(dojoComp);
    }
    
    public void enableErrorOptions() {
        boolean bEnable = !defError.getSelection();
        epLabel.setEnabled(bEnable);
        errPage.setEnabled(bEnable);
    }
    
    public void enableOptions() {
        themeNotesCombo.setEnabled(overrideNotes.getSelection());
        themeWebCombo.setEnabled(overrideWeb.getSelection());
    }

    public void initProject() {
        props = (XSPAllProperties)leftComposite.getDataNode().getCurrentObject();
        overrideNotes.setSelection((props.getThemeNotes().length() > 0));
        overrideWeb.setSelection((props.getThemeWeb().length() > 0));
        themeNotesCombo.setEnabled(overrideNotes.getSelection());
        themeWebCombo.setEnabled(overrideWeb.getSelection());
        String mobilePrefix = props.getMobilePrefix();
        if(StringUtil.isEmpty(mobilePrefix)){
            setMobileControlsState(false, true);
        } else {
            setMobileControlsState(true, true);            
        }
        
        getDataNode().notifyInvalidate(null);
        enableOptions();
        enableErrorOptions();
    }
    
    private void setMobileControlsState(boolean enabled, boolean initialLoad) {
        mobilePrefixCheckbox.setSelection(enabled);
        mobilePrefixText.setEnabled(enabled);
        
        // Enable/Disable the mobile combo and all the checkboxes
        mobileCombo.setEnabled(enabled);
        overrideIOS.setEnabled(enabled);
        overrideAndroid.setEnabled(enabled);
        debugUserAgent.setEnabled(enabled);
        
        if(!enabled) {
            // Default all mobile controls
            if(!initialLoad) {
                mobileCombo.select(0);                                
                themeIOSCombo.select(0);
                themeAndroidCombo.select(0);
                debugUserAgentCombo.deselectAll();
            }
            overrideIOS.setSelection(false);
            overrideAndroid.setSelection(false);
            debugUserAgent.setSelection(false);
            
            // Disable the mobile sub Combos
            themeIOSCombo.setEnabled(false);
            themeAndroidCombo.setEnabled(false);
            debugUserAgentCombo.setEnabled(false);
        } else {
            // Enable the sub combos if the value is set
            themeIOSCombo.setEnabled(!StringUtil.isEmpty(props.getThemeIOS()));
            themeAndroidCombo.setEnabled(!StringUtil.isEmpty(props.getThemeAndroid()));
            debugUserAgentCombo.setEnabled(!StringUtil.isEmpty(props.getThemeDebugUserAgent()));
            
            // check the checkboxes if the value is set
            overrideIOS.setSelection(!StringUtil.isEmpty(props.getThemeIOS()));
            overrideAndroid.setSelection(!StringUtil.isEmpty(props.getThemeAndroid()));
            debugUserAgent.setSelection(!StringUtil.isEmpty(props.getThemeDebugUserAgent()));
        }
    }
    
    public void setDesignPropsClassDef(IClassDef classDef){
        if(this.xspDesignPanel != null){
            xspDesignPanel.getDataNode().setClassDef(classDef);
        }
    }
    
    public void setDesignPropsProvider(IObjectCollection dataProvider){
        if(this.xspDesignPanel != null){
            xspDesignPanel.getDataNode().setDataProvider(dataProvider);
        }
    }
}