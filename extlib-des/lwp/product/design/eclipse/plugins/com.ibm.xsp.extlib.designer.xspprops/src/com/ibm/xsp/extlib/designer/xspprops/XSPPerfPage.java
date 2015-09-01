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
package com.ibm.xsp.extlib.designer.xspprops;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.commons.iloader.node.lookups.api.StringLookup;
import com.ibm.commons.iloader.node.validators.IntegerValidator;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.data.controls.*;
import com.ibm.commons.util.StringUtil;

/**
 * @author mgl
 *
 * Project: IBM Lotus Domino Designer
 * Unit XSPPage
 */
public class XSPPerfPage extends DCPanel implements XSPAllPropertyConstants {
    private FormToolkit toolkit = null;
    private static final String SERVER_DEFAULT = "Server default"; // $NLX-XSPPerfPage.Serverdefault-1$
    private DCPanel leftComposite = null;
    private DCText maxPages = null;
    private Label mpLabel = null;
    private DCText maxPagesDisk = null;
    private Label mpdLabel = null;
    private DCComboBox persistCombo = null;
    private DCCheckbox gzPersisted = null;
    private DCCheckbox asyncPersisted = null;
    private Label ppModeLabel = null;
    private DCComboBox ppMode = null;
    private Label ifSmallerLabel = null;
    private DCText ifSmallerDoMemory = null;
    
    private String[] persistCodes = {"", "basic", "file", "fileex"};    // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    private String[] persistLabels = {SERVER_DEFAULT, 
                                      "Keep pages in memory", // $NLX-XSPPerfPage.Keeppagesinmemorybestperformance-1$
                                      "Keep pages on disk", // $NLX-XSPPerfPage.Keeppagesondiskbestscalability-1$
                                      "Keep only the current page in memory"}; // $NLX-XSPPerfPage.Keeponlythecurrentpageinmemorysca-1$
    private String[] persistStateCodes = {"","fullstate", "notree", "delta", "deltaex"}; // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$
    private String[] persistStateLabels = {SERVER_DEFAULT, "Entire page content", // $NLX-XSPPerfPage.Entirepagecontent-1$
                                           "None; good for read only pages", // $NLX-XSPPerfPage.Nonegoodforreadonlypages-1$
                                           "Only changes since the tree was constructed", // $NLX-XSPPerfPage.Onlychangessincethetreewasconstru-1$
                                           "Entire tree, then only changes since constructed"}; // $NLX-XSPPerfPage.Entiretreethenonlychangessincecon-1$
    
    public XSPPerfPage(Composite parent, FormToolkit ourToolkit, XSPParentEditor dpe) {
        super(parent, SWT.NONE);
        toolkit = ourToolkit;
        initialize();
    }
    
    private ScrolledForm initialize() {
        GridLayout ourLayout = new GridLayout(1, false);
        ourLayout.marginHeight = 0;
        ourLayout.marginWidth = 0;
        setLayout(ourLayout);
        setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        ScrolledForm scrolledForm = toolkit.createScrolledForm(this);
        scrolledForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite formatComposite = XSPEditorUtil.createFormComposite(scrolledForm);

        XSPEditorUtil.createCLabel(formatComposite, "Performance Properties", 2); // $NLX-XSPPerfPage.PerformanceProperties-1$
        
        createLeftSide(formatComposite);
        createRightSide(formatComposite);
        return scrolledForm;
    }

    private void createLeftSide(Composite formatComposite) {
        leftComposite = XSPEditorUtil.createDCPanel(formatComposite, 2, -1, 20, "xspProperties", "leftComp"); // $NON-NLS-1$ $NON-NLS-2$
        leftComposite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));
        createGeneralArea(leftComposite);
    }
    
    private void createRightSide(Composite parent) {
        DCPanel rightComp = XSPEditorUtil.createDCPanel(parent, 2, -1, 20, "xspProperties", "rightComp"); // $NON-NLS-1$ $NON-NLS-2$
        rightComp.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 4));

        Section optionsSection = XSPEditorUtil.createSection(toolkit, rightComp, "Options", 2, 1); // $NLX-XSPPerfPage.Options-1$
        Composite optComp = XSPEditorUtil.createSectionChild(optionsSection, 2);

        DCCheckbox wholeTree = XSPEditorUtil.createCheckboxTF(optComp, "Evaluate the entire page on partial refresh", "renderWholeTree", 2); //  $NON-NLS-2$ $NLX-XSPPerfPage.Evaluatetheentirepageonpartialref-1$
        wholeTree.setToolTipText("This property defines if the JSF tree should be completely processed during the render phase,\nincluding the components that are not rendered.  When set to false it gives better performance \nbut with potential side effects if some components are changing data during the render phase \n(which should be avoided anyway)."); // $NLX-XSPPerfPage.whichshouldbeavoidedanyway-1$
        
        DCCheckbox aggregateRes = XSPEditorUtil.createCheckboxTF(optComp, "Use runtime optimized JavaScript and CSS resources", "aggregateResources", 2); // $NON-NLS-2$ $NLX-XSPPerfPage.UseruntimeoptimizedJavaScriptandC-1$
        aggregateRes.setToolTipText("Define if the resources served to a page should be aggregated.  This option should be \nused to provide the best download time experience.  The option defaults to false when not set, \nbut new applications created in Designer 8.5.3 or later will contain an xsp.properties file \nwith this option value set to true."); // $NLX-XSPPerfPage.withthisoptionvaluesettotrue-1$
        
        DCCheckbox uncompressedResources = XSPEditorUtil.createCheckboxTF(optComp, "Use uncompressed resource files (CSS && Dojo)", "uncompressCssAndDojo", 2); // $NON-NLS-2$ $NLX-XSPPerfPage.UseuncompressedresourcefilesCSSDo-1$
        uncompressedResources.setToolTipText("Define if the resources served to the page are to be compressed.\nEnabling this option serves uncompressed resources to the page,\nwhich may be desirable for debugging purposes."); // $NLX-XSPPerfPage.Defineiftheresourcesservedtothepa-1$
        
        DCCheckbox discardJS = XSPEditorUtil.createCheckboxTF(optComp, "Discard JavaScript context after each page", "discardJS", 2); // $NON-NLS-2$ $NLX-XSPPerfPage.DiscardJavaScriptcontextaftereach-1$
        discardJS.setToolTipText("Discard the JavaScript context for a page after the page is processed. \nThis is a runtime optimization that is set to true by default but might be \nreverted to avoid compatibility issues (although it is *not* advised)."); // $NLX-XSPPerfPage.revertedtoavoidcompatibilityissue-1$
        
        Label greLabel = XSPEditorUtil.createLabel(optComp, "Global resource expiration:", 1); // $NLX-XSPPerfPage.Globalresourceexpiration-1$
        greLabel.setToolTipText("Defines the default expiration duration for global resources.  When not defined, \nit is 10 days"); // $NLX-XSPPerfPage.itis10days-1$
        CustomComposite greComp = XSPEditorUtil.createZeroMarginComposite(optComp, 2, 1, 10, "greDaysComp"); // $NON-NLS-1$
        DCText greDays = XSPEditorUtil.createTextNoFill(greComp, "expiresGlobal", 1, 0, 6); // $NON-NLS-1$
        greDays.setValidator(IntegerValidator.positiveInstance);
        XSPEditorUtil.createLabel(greComp, "(days)", 1); // $NLX-XSPPerfPage.days-1$
        
        optionsSection.setClient(optComp);
    }
        
    private void createGeneralArea(Composite parent) {
        Section persSection = XSPEditorUtil.createSection(toolkit, parent, "Persistence Options", 2, 1); // $NLX-XSPPerfPage.PersistenceOptions-1$
        Composite persComp = XSPEditorUtil.createSectionChild(persSection, 2);
        Label sppLabel = XSPEditorUtil.createLabel(persComp, "Server page persistence:", 1); // $NLX-XSPPerfPage.Serverpagepersistence-1$
        sppLabel.setToolTipText("Defines the persistence mode for the JSF pages (a.k.a. Views).  Keeping pages in \nmemory provides the best performance, while keeping pages on disk provides the \nbest scalability.  Keeping the current page in memory scales and performs well.");  // $NLX-XSPPerfPage.DefinesthepersistencemodefortheJS-1$
        persistCombo = XSPEditorUtil.createDCCombo(persComp, "pagePersistence", 1, true, false); // $NON-NLS-1$
        StringLookup persistLookup = new StringLookup(persistCodes, persistLabels);
        persistCombo.setLookup(persistLookup);
        persistCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e) {
                // allow max pages to be set for default and mem settings
                if (persistCombo.getSelectionIndex() > 1 && StringUtil.isNotEmpty(maxPages.getText())) {
                    maxPages.setText("");
                }
                else if (persistCombo.getSelectionIndex() < 2 && StringUtil.isNotEmpty(maxPagesDisk.getText())) {
                    maxPagesDisk.setText("");
                }
                enableOptions();
            }
        });
        
        mpLabel = XSPEditorUtil.createIndentedLabel(persComp, "Maximum pages in memory:", 1, 18); // $NLX-XSPPerfPage.Maximumpagesinmemory-1$
        mpLabel.setToolTipText("Defines the number of pages persisted when in memory (MRU algorithm)"); // $NLX-XSPPerfPage.Definesthenumberofpagespersistedw-1$
        maxPages = XSPEditorUtil.createTextNoFill(persComp, "maxSavedPagesMemory", 1, 0, 6); // $NON-NLS-1$
        maxPages.setValidator(IntegerValidator.positiveInstance);
        ppModeLabel = XSPEditorUtil.createIndentedLabel(persComp, "Page persistence mode:", 1, 18); // $NLX-XSPPerfPage.Pagepersistencemode-1$
        ppModeLabel.setToolTipText("Defines how much of the JSF pages are persisted."); // $NLX-XSPPerfPage.DefineshowmuchoftheJSFpagesareper-1$
        ppMode = XSPEditorUtil.createDCCombo(persComp, "pagePersistenceViewState", 1, true, false); // $NON-NLS-1$
        ppMode.setLookup(new StringLookup(persistStateCodes, persistStateLabels));
        
        mpdLabel = XSPEditorUtil.createIndentedLabel(persComp, "Maximum pages on disk:", 1, 18); // $NLX-XSPPerfPage.Maximumpagesondisk-1$
        mpdLabel.setToolTipText("Defines the number of pages persisted when on disk (MRU algorithm)"); // $NLX-XSPPerfPage.Definesthenumberofpagespersistedw.1-1$
        maxPagesDisk = XSPEditorUtil.createTextNoFill(persComp, "maxSavedPages", 1, 0, 6); // $NON-NLS-1$
        maxPagesDisk.setValidator(IntegerValidator.positiveInstance);
        gzPersisted = XSPEditorUtil.createIndentedCheckboxTF(persComp, "GZip persisted files", "gzipPersistedFiles", 2, 18); // $NON-NLS-2$ $NLX-XSPPerfPage.GZippersistedfiles-1$
        gzPersisted.setToolTipText("Defines if the persisted files should be GZIP'ed on disk (less disk space, more CPU processing)"); // $NLX-XSPPerfPage.Definesifthepersistedfilesshouldb-1$
        asyncPersisted = XSPEditorUtil.createIndentedCheckboxTF(persComp, "Persist files asynchronously", "asyncFilePersistence", 2, 18); // $NON-NLS-2$ $NLX-XSPPerfPage.Persistfilesasynchronously-1$
        asyncPersisted.setToolTipText("Defines if the page persistence to a file should be done\nasynchronously (best response time, creates extra threads on the server)."); // $NLX-XSPPerfPage.asynchronouslybestresponsetimecre-1$

        CustomComposite memInsteadComp = XSPEditorUtil.createZeroMarginComposite(persComp, 2, 2, 10, "memInsteadComp"); // $NON-NLS-1$
        ifSmallerLabel = XSPEditorUtil.createIndentedLabel(memInsteadComp, "Save to memory instead when less than (x) bytes:", 1, 18); // $NLX-XSPPerfPage.Savetomemoryinsteadwhenlessthanxb-1$
        ifSmallerLabel.setToolTipText("Defines if the pages should be serialized in memory, instead of files, when their size \nis less than a specific amount of bytes. 0, which is default, means that it \nis always serialized to disk."); // $NLX-XSPPerfPage.isalwaysserializedtodisk-1$
        ifSmallerDoMemory = XSPEditorUtil.createTextNoFill(memInsteadComp, "pagePersistenceThreshold", 1, 0, 6); // $NON-NLS-1$
        ifSmallerDoMemory.setValidator(IntegerValidator.positiveInstance);
        
        persSection.setClient(persComp);
    }
    
    public void enableOptions() {
        boolean bEnableMem = true;
        boolean bEnableDisk = true;
        if (persistCombo == null)
            return;
        if (persistCombo.getSelectionIndex() == 1) {
            bEnableDisk = false;
        }
        if (persistCombo.getSelectionIndex() > 1) {
            bEnableMem = false;
        }
        mpLabel.setEnabled(bEnableMem);
        maxPages.setEnabled(bEnableMem);
        ppModeLabel.setEnabled(bEnableDisk);
        ppMode.setEnabled(bEnableDisk);
        mpdLabel.setEnabled(bEnableDisk);
        maxPagesDisk.setEnabled(bEnableDisk);
        gzPersisted.setEnabled(bEnableDisk);
        asyncPersisted.setEnabled(bEnableDisk);
        ifSmallerLabel.setEnabled(bEnableDisk);
        ifSmallerDoMemory.setEnabled(bEnableDisk);
    }

    public void initProject() {
        getDataNode().notifyInvalidate(null);
        enableOptions();
    }
    
}