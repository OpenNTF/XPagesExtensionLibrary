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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomCheckBox;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.controls.custom.CustomTree;
import com.ibm.commons.swt.controls.custom.CustomTreeColumn;
import com.ibm.commons.swt.data.controls.DCCheckbox;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.product.ProductUtil;
import com.ibm.xsp.extlib.designer.xspprops.xsplibs.XPageLibraryCheckStateListener;
import com.ibm.xsp.extlib.designer.xspprops.xsplibs.XPageLibraryContentProvider;
import com.ibm.xsp.extlib.designer.xspprops.xsplibs.XPageLibraryLabelProvider;
import com.ibm.xsp.library.LibraryWrapper;
import com.ibm.xsp.library.MissingLibraryWrapper;

/**
 * @author mgl
 *
 * Project: IBM Lotus Domino Designer
 * Unit XSPPage
 */
public class XSPGenPage extends DCPanel implements XSPAllPropertyConstants {
    private FormToolkit toolkit = null;
    private String[] gzipCodes = {PAGE_COMPRESS_NONEVAL, PAGE_COMPRESS_DEFVAL, PAGE_COMPRESS_ZIPNOLENVAL};
    private String[] gzipLabels = {"None", "GZip, set content length", "GZip"}; // $NLX-XSPGenPage.None-1$ $NLX-XSPGenPage.GZipsetcontentlength-2$ $NLX-XSPGenPage.GZip-3$
    private static final String SERVER_DEFAULT = "Server default"; // $NLX-XSPGenPage.Serverdefault-1$
    private DCPanel leftComposite = null;
    private static final String LINK_SAVE_PROP = "linkFormat";          // $NON-NLS-1$
    public static final String DOCTYPE_HTML_STRICT  = "HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\""; // $NON-NLS-1$
    public static final String DOCTYPE_HTML_TRANS   = "HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"";  // $NON-NLS-1$
    
    public static final String DOCTYPE_XHTML_STRICT = "html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\""; // $NON-NLS-1$
    public static final String DOCTYPE_XHTML_TRANS  = "html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"";  // $NON-NLS-1$
    public static final String DOCTYPE_HTML5        = "html"; // $NON-NLS-1$
    
    private String[] acfCodes = {"", "acf", "identity", "empty", "striptags"};  // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$  $NON-NLS-5$
    private String[] acfLabels = {SERVER_DEFAULT, "Use the ACF library", "No filtering", "Remove all", "Remove all tags, leave only text"}; //    $NLX-XSPGenPage.Removealltagsleaveonlytext-4$ $NLX-XSPGenPage.UsetheACFlibrary-1$ $NLX-XSPGenPage.Nofiltering-2$ $NLX-XSPGenPage.Removeall-3$
    
    private String[] commonEncodeVals = {SERVER_DEFAULT, "utf-8", "utf-16", "iso-8859-1", "iso-8859-2", // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
                                         "iso-8859-4", "iso-8859-5", "iso-8859-6","iso-8859-7", // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
                                         "iso-8859-8", "iso-8859-9", "big5", "GB2312",  // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
                                         "shift_jis", "iso-2022-jp",  // $NON-NLS-1$ $NON-NLS-2$
                                         "euc-jp", "euc-kr", "euc-cn", "us-ascii" };  // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    private String[] commonEncodeCodes = {"", "utf-8", "utf-16", "iso-8859-1", "iso-8859-2", // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            "iso-8859-4", "iso-8859-5", "iso-8859-6","iso-8859-7", // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            "iso-8859-8", "iso-8859-9", "big5", "GB2312",  // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
            "shift_jis", "iso-2022-jp",  // $NON-NLS-1$ $NON-NLS-2$
            "euc-jp", "euc-kr", "euc-cn", "us-ascii" };  // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    private String[] doctypeLabels = {SERVER_DEFAULT,
                                      "HTML Strict", "HTML Transitional", // $NLX-XSPGenPage.HTMLStrict-1$ $NLX-XSPGenPage.HTMLTransitional-2$
                                      "XHTML Strict", "XHTML Transitional", "HTML5"}; // $NLX-XSPGenPage.XHTMLStrict-1$ $NLX-XSPGenPage.XHTMLTransitional-2$ $NLX-XSPGenPage.HTML5-3$
    private String[] doctypeCodes = {"", DOCTYPE_HTML_STRICT, DOCTYPE_HTML_TRANS, DOCTYPE_XHTML_STRICT, DOCTYPE_XHTML_TRANS, 
                                     DOCTYPE_HTML5};
    
    private Composite treeComposite;
    private CheckboxTreeViewer _xpageLibraries;
    private Label missingLibrariesTxt;
    private Label missingLibrariesImg;
    
    DCPanel rtPanel;
    private CustomCheckBox robotOverride;
    private CustomCheckBox robotWellKnown;
    private Button editRobots;
    private XSPRobotComposite robotComp;
    private XSPRobotUserAgents userAgents = new XSPRobotUserAgents("");
    
    public XSPGenPage(Composite parent, FormToolkit ourToolkit, XSPParentEditor dpe) {
        super(parent, SWT.NONE);
        toolkit = ourToolkit;
        initialize();
    }
    
    private ScrolledForm initialize() {
        setParentPropertyName("xspProperties"); // $NON-NLS-1$
        GridLayout ourLayout = new GridLayout(1, false);
        ourLayout.marginHeight = 0;
        ourLayout.marginWidth = 0;
        setLayout(ourLayout);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        ScrolledForm scrolledForm = toolkit.createScrolledForm(this);
        scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite formatComposite = XSPEditorUtil.createFormComposite(scrolledForm);

        XSPEditorUtil.createCLabel(formatComposite, "Page Generation Properties", 2); // $NLX-XSPGenPage.PageGenerationProperties-1$
        
        createLeftSide(formatComposite);
        createRightSide(formatComposite);
        return scrolledForm;
    }

    private void createLeftSide(Composite formatComposite) {
        leftComposite = XSPEditorUtil.createDCPanel(formatComposite, 1, "xspProperties", "leftComp"); // $NON-NLS-1$ $NON-NLS-2$
        leftComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        
        createGeneralArea(leftComposite);
    }
        
    private void createRightSide(Composite formatComposite) {
        rtPanel = XSPEditorUtil.createDCPanel(formatComposite, 1, -1, 20, "xspProperties", "rtComp"); // $NON-NLS-1$ $NON-NLS-2$
        rtPanel.setParentPropertyName("xspProperties"); // $NON-NLS-1$
        rtPanel.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        
        createBrowserOptions(rtPanel);
        
        createXPageLibraries(rtPanel);
        
        createRobotUserAgents(rtPanel);
    }
    
    private void createGeneralArea(Composite parent) {
        Section webSide = XSPEditorUtil.createSection(toolkit, parent, "HTML Generation", 1, 1); // $NLX-XSPGenPage.HTMLGeneration-1$
        Composite webContainer = XSPEditorUtil.createSectionChild(webSide, 2);
        webContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, 1, 1));
        
        Label cvLabel = XSPEditorUtil.createLabel(webContainer, "Client validation:", 1); // $NLX-XSPGenPage.Clientvalidation-1$
        cvLabel.setToolTipText("Enable client side validation; the server default value is true unless it has been overridden."); // $NLX-XSPGenPage.Enabletheclientsidevalidationdefa-1$
        CustomComposite cvComp = XSPEditorUtil.createZeroMarginComposite(webContainer, 3, 1, 10, "clientValComp"); // $NON-NLS-1$
        XSPEditorUtil.createRadio(cvComp, "clientValidate", SERVER_DEFAULT, XSPAllProperties.SERVER_DEFVAL, "cvServer", 1);  // $NON-NLS-2$ $NON-NLS-1$
        XSPEditorUtil.createRadio(cvComp, "clientValidate", "On", "true", "cvOn", 1); // $NON-NLS-1$ $NON-NLS-4$ $NON-NLS-3$ $NLX-XSPGenPage.On-2$
        XSPEditorUtil.createRadio(cvComp, "clientValidate", "Off", "false", "cvoff", 1); // $NON-NLS-1$ $NON-NLS-4$ $NON-NLS-3$ $NLX-XSPGenPage.Off-2$
        
        Label compLabel = XSPEditorUtil.createLabel(webContainer, "Compression:", 1); // $NLX-XSPGenPage.Compression-1$
        compLabel.setToolTipText("This defines the compression mode used when a page is rendered to the client.  The compression \nis effectively enabled when the client supports it, as specified in the HTTP header of the request."); // $NLX-XSPGenPage.iseffectivelyenabledwhentheclient-1$
        DCComboBox gzipCombo = XSPEditorUtil.createDCCombo(webContainer, "pageCompress", 1, true, false); // $NON-NLS-1$
        gzipCombo.setFirstBlankLine(true);
        gzipCombo.setFirstLineTitle(SERVER_DEFAULT); 
        gzipCombo.setLookup(new StringLookup(gzipCodes, gzipLabels));
        
        Label enLabel = XSPEditorUtil.createLabel(webContainer, "Encoding:", 1);  // $NLX-XSPGenPage.Encoding-1$
        enLabel.setToolTipText("Defines the character set returned for the page."); // $NLX-XSPGenPage.Definesthecharactersetreturnedfor-1$
        DCComboBox encode = new DCComboBox(webContainer, SWT.DROP_DOWN, "encoding"); // $NON-NLS-1$
        encode.setAttributeName("pageEncoding"); // $NON-NLS-1$
        encode.setEditableLabels(true);
        encode.setLookup(new StringLookup(commonEncodeCodes, commonEncodeVals));
        encode.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
        
        Label dtLabel = XSPEditorUtil.createLabel(webContainer, "HTML doctype:", 1);  // $NLX-XSPGenPage.HTMLdoctype-1$
        dtLabel.setToolTipText("Defines the document doctype generated by the engine."); // $NLX-XSPGenPage.Definesthedocumentdoctypegenerate-1$
        DCComboBox docType = XSPEditorUtil.createDCCombo(webContainer, "docType", 1, false, true);//new DCComboBox(webContainer, SWT.DROP_DOWN, "docType"); // $NON-NLS-1$
        docType.setEditableLabels(true);
        docType.setLookup(new StringLookup(doctypeCodes, doctypeLabels));
        
        DCCheckbox writeMeta = XSPEditorUtil.createCheckboxTF(webContainer, "Generate meta tags in the HTML header", "writeMetaContent", 2); // $NON-NLS-2$ $NLX-XSPGenPage.GeneratemetatagsintheHTMLheader-1$
        writeMeta.setToolTipText("Generate a <meta> tag in the HTML header defining the content type, and the optional character set.  This meta tag is the first tag appearing after the <head> one."); // $NLX-XSPGenPage.GenerateametatagintheHTMLheaderde-1$
        
        DCCheckbox pcOption = XSPEditorUtil.createCheckboxTF(webContainer, "Force content type to application/xhtml+xml", "htmlPrefContentType", 2); // $NON-NLS-2$ $NLX-XSPGenPage.Forcecontenttypetoapplicationxhtm-1$
        pcOption.setToolTipText("Force the content type to be application/xhtml+xml if the user agent supports it.  This has to be used very \ncarefully as some features won't work with an XML content type.  For example, the innerHTML JavaScript property \nis read only and then breaks Dojo or XPages partial refresh.  Moreover, the RichText fields converted to MIME are not \nXHTML compatible.  This option should only be used in very particular cases."); // $NLX-XSPGenPage.XHTMLcompatibleThisoptionshouldon-1$
        
        DCCheckbox prOption = XSPEditorUtil.createCheckboxTF(webContainer, "Page redirect mode", "pageRedirectMode", 2); // $NON-NLS-2$ $NLX-XSPGenPage.Pageredirectmode-1$
        prOption.setToolTipText("Page redirect mode - This happens when the runtime redirects to a new page (navigation rules, \nAPI, simple action).  When this property is true, the runtime emits an HTTP 302 code to the ask \nclient to redirect to the new page. This ensures that the client has the right URL in its address \nbar, at the cost of an extra client/server roundtrip. Else, the redirection is purely done on the \nserver, without any notification to the browser (the URL doesn't change)"); // $NLX-XSPGenPage.serverwithoutanynotificationtothe-1$
        
        DCCheckbox allowZero = XSPEditorUtil.createCheckboxTF(webContainer, "Allow zero rows in repeat controls", "allowRepeatZero", 2); // $NON-NLS-2$ $NLX-XSPGenPage.Allowzerorowsinrepeatcontrols-1$
        allowZero.setToolTipText("Defines the behavior in repeating controls when the rows property evaluates to 0. By default, \nin that situation 30 rows are displayed per page. With this option set to true, no rows would \nbe displayed.  This may be useful when computing the number of rows to display, as there may be \nsituations when no rows should be displayed but the control should still be rendered."); // $NLX-XSPGenPage.situationswhennorowsshouldbedispl-1$
        
        
        Section acfSection = XSPEditorUtil.createSection(toolkit, parent, "Active Content Filtering", 1, 1); // $NLX-XSPGenPage.ActiveContentFiltering-1$
        if(acfSection.getLayoutData() instanceof GridData){
            ((GridData)acfSection.getLayoutData()).verticalIndent = 15;
        }
        Composite acfComp = XSPEditorUtil.createSectionChild(acfSection, 2);
        Label acfLabel = XSPEditorUtil.createLabel(acfComp, "Display filter type:", 1); // $NLX-XSPGenPage.Displayfiltertype-1$
        acfLabel.setToolTipText("Defines which filter should be used by default when displaying content for some controls (richtext)."); // $NLX-XSPGenPage.Defineswhichfiltershouldbeusedbyd-1$
        DCComboBox acfCombo = XSPEditorUtil.createDCCombo(acfComp, "rtHTMLFilter", 1, true, false); // $NON-NLS-1$
        acfCombo.setLookup(new StringLookup(acfCodes, acfLabels));

        Label acfInLabel = XSPEditorUtil.createLabel(acfComp, "Save filter type:", 1); // $NLX-XSPGenPage.Savefiltertype-1$
        acfInLabel.setToolTipText("Defines which filter should be used by default when saving data for some controls (richtext)."); // $NLX-XSPGenPage.Defineswhichfiltershouldbeusedbyd.1-1$
        DCComboBox acfInCombo = XSPEditorUtil.createDCCombo(acfComp, "rtHTMLFilterIn", 1, true, false); // $NON-NLS-1$
        acfInCombo.setLookup(new StringLookup(acfCodes, acfLabels));
        
        Label acfConfigLabel = XSPEditorUtil.createLabel(acfComp, "Config file:", 1); // $NLX-XSPGenPage.Configfile-1$
        acfConfigLabel.setToolTipText("Defines the acf library config file to use.  If empty, it uses \nthe default config file provided by the library; else, it looks for \na file locating in data/properties. For example, acf-config.xml"); // $NLX-XSPGenPage.afilelocatingindatapropertiesFore-1$
        XSPEditorUtil.createText(acfComp, "htmlFilterACFConfig", 1, 0, 0); // $NON-NLS-1$
        
        
        webSide.setClient(webContainer);
        acfSection.setClient(acfComp);
    }
    
    private void createBrowserOptions(Composite parent) {
        Section rtSection = XSPEditorUtil.createSection(toolkit, parent, "Rich Text Options", 1, 1); // $NLX-XSPGenPage.RichTextOptions-1$
        Composite rtDCP = XSPEditorUtil.createSectionChild(rtSection, 2);
        rtDCP.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, 1, 1));
        Label lfLabel = XSPEditorUtil.createLabel(rtDCP, "Save links in:", 1); // $NLX-XSPGenPage.Savelinksin-1$
        lfLabel.setToolTipText("Defines how links should be saved in a Domino document"); // $NLX-XSPGenPage.DefineshowlinksshouldbesavedinaDo-1$
        Composite buttonHolder = new Composite(rtDCP, SWT.NONE);
        buttonHolder.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        GridLayout gl = SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2);
        buttonHolder.setLayout(gl);
        buttonHolder.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, false, 1, 1));
        XSPEditorUtil.createRadio(buttonHolder, LINK_SAVE_PROP, "Notes format", XSP_SAVE_USE_NOTES, "notesLinks", 1); //  $NON-NLS-2$ $NLX-XSPGenPage.Notesformat-1$
        XSPEditorUtil.createRadio(buttonHolder, LINK_SAVE_PROP, "Web format", XSP_SAVE_USE_WEB, "webLinks", 1); //  $NON-NLS-2$ $NLX-XSPGenPage.Webformat-1$

        rtSection.setClient(rtDCP);   
    }
    
    private void createXPageLibraries(Composite parent){
        Section advancedSection = XSPEditorUtil.createSection(toolkit, parent, "XPage Libraries", 1, 1);  // $NLX-XSPGenPage.XPageLibraries-1$
        treeComposite = new Composite(advancedSection, SWT.NONE);
        treeComposite.setLayoutData(SWTLayoutUtils.createGDFillNoGrab());
        treeComposite.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        treeComposite.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        
        new Label(treeComposite, SWT.NONE).setText("Select the libraries of extended XPage controls to use\nin this application.");  // $NLX-XSPGenPage.SelectthelibrariesofextendedXPage-1$
        
        CustomTree tree = new CustomTree(treeComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, "xsp.libraries"); // $NON-NLS-1$
        tree.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setRows(4);
        CustomTreeColumn col = new CustomTreeColumn(tree, SWT.NONE, "lib.id.col"); // $NON-NLS-1$
        col.setText("Library ID"); // $NLX-XSPGenPage.LibraryID-1$
        col.setWidthUnit(CustomTreeColumn.UNIT_REMAINDER);
        /*
         * Create a checkbox viewer that allows the user to select which xsp
         * libraries this application will depend upon
         */
        _xpageLibraries = new CheckboxTreeViewer(tree);
        _xpageLibraries.setLabelProvider(new XPageLibraryLabelProvider());
        _xpageLibraries.setContentProvider(new XPageLibraryContentProvider());
        _xpageLibraries.setColumnProperties(new String[]{"ID"}); //For future reference - when editing - we need to check against the ids set here! $NON-NLS-1$
            
        XSPEditorUtil.createLabel(treeComposite, "When running on the Web, the libraries must be available on the\nserver. When running on the Notes client, the library plug-ins must\nbe installed on the client.", 1); // $NLX-XSPGenPage.WhenrunningontheWebthelibrariesmu-1$
        
        Composite twoCols = new Composite(treeComposite, SWT.NONE);
        twoCols.setLayout(SWTLayoutUtils.createLayoutNoMarginNoSpacing(2));
        twoCols.setLayoutData(SWTLayoutUtils.createGDFillHorizontalNoGrab());
        
        missingLibrariesImg = new Label(twoCols, SWT.NONE);
        GridData data = new GridData();
        data.verticalAlignment = SWT.BEGINNING;
        data.verticalIndent = 3;
        missingLibrariesImg.setLayoutData(data);
        
        missingLibrariesTxt = new Label(twoCols, SWT.NONE);
        data = GridDataFactory.copyData(data);
        data.horizontalIndent = 5;
        data.verticalIndent = 0;
        missingLibrariesTxt.setLayoutData(data);
        
        advancedSection.setClient(treeComposite);
        
        SWTUtils.setBackgroundColor(treeComposite);
    }
    
    private void createRobotUserAgents(Composite parent) {
        Section rtSection = XSPEditorUtil.createSection(toolkit, parent, "Search Engine Robot User Agents", 1, 1);  // $NLX-XSPGenPage.SearchEngineRobotUserAgents-1$
        Composite rtDCP = new Composite(rtSection, SWT.NONE);
        rtDCP.setLayoutData(SWTLayoutUtils.createGDFillNoGrab());
        rtDCP.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        rtDCP.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));   
        Label lbl = new Label(rtDCP, SWT.NONE);
        lbl.setText("Options for producing robot-friendly URLs.");  // $NLX-XSPGenPage.OptionsforproducingrobotfriendlyU-1$
        lbl.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                
        robotOverride = XSPEditorUtil.createIndentedCheck(rtDCP, "Override platform default", "robotOverride", 0);  // $NON-NLS-2$ $NLX-XSPGenPage.Overrideplatformdefault-1$
        robotOverride.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if(robotOverride.getSelection()) {
                    userAgents.set(XSPRobotUserAgents.ROBOT_UA_AUTO);
                } else {
                    userAgents.reset();
                }
                saveRobotUserAgents();
                enableOptions();
                rtPanel.getDataNode().setModelModified(true);
                rtPanel.getDataNode().notifyInvalidate(null);
            }
        });

        String wellKnownStr = StringUtil.format("Support well-known robots ({0})", "Google, Yahoo, Bing"); // $NON-NLS-2$ $NLX-XSPGenPage.Supportwellknownrobots0-1$      
        robotWellKnown = XSPEditorUtil.createIndentedCheck(rtDCP, wellKnownStr, "robotWellKnown", 18);  // $NON-NLS-1$
        robotWellKnown.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                if(robotWellKnown.getSelection()) {
                    userAgents.remove(XSPRobotUserAgents.ROBOT_UA_EMPTY);
                    userAgents.add(XSPRobotUserAgents.ROBOT_UA_AUTO);
                } else {
                    userAgents.remove(XSPRobotUserAgents.ROBOT_UA_AUTO);
                    if(userAgents.getUserDefinedCount() == 0) {
                        userAgents.add(XSPRobotUserAgents.ROBOT_UA_EMPTY);
                    }
                }
                saveRobotUserAgents();
                enableOptions();
                rtPanel.getDataNode().setModelModified(true);
                rtPanel.getDataNode().notifyInvalidate(null);
            }
        });
          
        editRobots = new Button(rtDCP, SWT.PUSH);
        editRobots.setText("User defined robots...");  // $NLX-XSPGenPage.Userdefinedrobots-1$
        GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
        gd.horizontalIndent = 18;
        editRobots.setLayoutData(gd);        
        editRobots.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                XSPRobotDialog dialog = new XSPRobotDialog(getShell(), userAgents.get());
                if(dialog.open() == Dialog.OK) {
                    userAgents.set(dialog.getUserAgents());
                    saveRobotUserAgents();
                    enableOptions();                    
                }   
            }
        });
                
        robotComp = new XSPRobotComposite(rtDCP, true, "");
        
        rtSection.setClient(rtDCP);   
    }
    
    public void enableOptions() {
        readRobotUserAgents();
        if(userAgents.isDefault()) {
            // Default Position
            robotOverride.setSelection(false);
            robotWellKnown.setSelection(false);
            robotWellKnown.setEnabled(false);     
            editRobots.setEnabled(false);
            robotComp.setVisible(false);
        } else { 
            robotOverride.setSelection(true);
            robotWellKnown.setEnabled(true);
            editRobots.setEnabled(true);   
            robotWellKnown.setSelection(userAgents.getAuto());
            robotComp.setUserAgents(userAgents.get());            
            robotComp.setVisible(userAgents.getUserDefinedCount() > 0);
        }
    }

    public void initProject() {
        getDataNode().notifyInvalidate(null);
        enableOptions();
        
        if(treeComposite != null){
            final DataNode root = getDataNode().getRootNode();
            if(root != null){
                //get our bean reference to xsp.properties object
                IMember member = root.getMember("xspProperties"); // $NON-NLS-1$
                if(member instanceof IAttribute){
                    //Jackpot!
                    try {
                        Object xsp = root.getObject(root.getCurrentObject(), (IAttribute)member);
                        if(xsp instanceof XSPAllProperties){
                            _xpageLibraries.addCheckStateListener(new XPageLibraryCheckStateListener(getDataNode(), (XSPAllProperties)xsp));
                            _xpageLibraries.setInput(xsp);
                            String dep = ((XSPAllProperties) xsp).getDependencies();
                            if(StringUtil.isNotEmpty(dep)){
                                String[] ids = dep.split(",");
                                Object[] elements = ((ITreeContentProvider)_xpageLibraries.getContentProvider()).getElements(xsp);
                                List<LibraryWrapper> libs = new ArrayList<LibraryWrapper>();
                                if(elements != null && elements.length > 0){
                                    for(Object o : elements){
                                        if(o instanceof LibraryWrapper){
                                            libs.add((LibraryWrapper)o);
                                        }
                                    }
                                }
                                List<String>libIds = new ArrayList<String>();
                                if(!libs.isEmpty()){
                                    List<LibraryWrapper>selected = new ArrayList<LibraryWrapper>();
                                    for(LibraryWrapper lib : libs){
                                        if(Arrays.asList(ids).contains(lib.getLibraryId())){
                                            selected.add(lib);
                                        }
                                        if(!(lib instanceof MissingLibraryWrapper)){
                                            libIds.add(lib.getLibraryId());
                                        }
                                    }
                                    _xpageLibraries.setCheckedElements(selected.toArray(new LibraryWrapper[0]));
                                    List<String> missingLibs = new ArrayList<String>();
                                    for(String id : ids){
                                        if(!libIds.contains(id)){
                                            missingLibs.add(id);
                                        }
                                    }
                                    if(!missingLibs.isEmpty()){
                                        String warn = "The current application depends on the libraries listed below.\nThese libraries are not installed in {0}. Including\nthese libraries and controls from these libraries in your XPages will\ncause errors in your application at runtime.\n\nMissing Libraries:\n{1}";  // $NLX-XSPGenPage.Thecurrentapplicationdependsonthe-1$
                                        StringBuffer missingBuff = new StringBuffer();
                                        for(String libId : missingLibs){
                                            missingBuff.append("  -" + libId + "\n"); // $NON-NLS-1$ // $NON-NLS-2$
                                        }
                                        warn = StringUtil.format(warn, ProductUtil.getProductNameWithoutIBM(), missingBuff.toString());
                                        ImageDescriptor desc = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/warning_overlay.gif"); // $NON-NLS-1$
                                        if(desc != null){
                                            final Image warningImg = desc.createImage(); // $NON-NLS-1$
                                            missingLibrariesImg.setImage(warningImg);
                                            missingLibrariesImg.addDisposeListener(new DisposeListener() {
                                                /*
                                                 * (non-Javadoc)
                                                 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
                                                 */
                                                public void widgetDisposed(DisposeEvent arg0) {
                                                    if(warningImg != null && !warningImg.isDisposed()){
                                                        warningImg.dispose();
                                                    }
                                                }
                                            });
                                        }
                                        missingLibrariesTxt.setText(warn);
                                    }
                                }
                            }
                            
                        }
                    } catch (NodeException e) {
                        
                    }
                }
            }
        }
    }
    
    private void readRobotUserAgents() {
        IAttribute robotUserAgents = (IAttribute) rtPanel.getDataNode().getMember("robotUserAgents"); // $NON-NLS-1$
        try {
            userAgents.set(StringUtil.getNonNullString(rtPanel.getDataNode().getValue(robotUserAgents)));
        } catch (NodeException e) {
            userAgents.reset();
            e.printStackTrace();
        }     
    }
    
    private void saveRobotUserAgents() {
        IAttribute robotUserAgents = (IAttribute) rtPanel.getDataNode().getMember("robotUserAgents"); // $NON-NLS-1$
        try {
            rtPanel.getDataNode().setValue(robotUserAgents, userAgents.get(), null);                                    
        } catch (NodeException e) {
            e.printStackTrace();
        }     
    }    
}
