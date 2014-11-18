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
package com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.navigator.NavigatorPlugin;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.annotation.ExtLibLayoutExtension;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.WizardUtils;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesDefinition;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.*;

/**
 * @author Gary Marjoram
 *
 */
public class AlwStartPage extends WizardPage implements ControlListener, Listener, SelectionListener {

    private static final int    MARGIN       = 10;
    private static final int    SPACER       = 3;
    private static final int    DEFAULT_ROWS = 3;
    private static final String SAMPLE_TEXT  = "Sample"; // $NLX-AlwStartPage.Sample-1$
    private int                 _textMargin  = 0;
    private List<LayoutConfig>  _configList  = new ArrayList<LayoutConfig>();
    private Table               _table;
    private Button              _allRadio;
    private Button              _responsiveRadio;
    private Button              _nonResponsiveRadio;
    private final Image         _defImage;
    private final Image         _responsiveImage;
    private final Cursor        _handCursor;
    private final Font          _titleFont;
    private final Color         _hyperlinkColor;
    private final boolean       _showResponsiveIcon = false;

    /*
     * Enum for different configuration types
     */
    private static enum ConfigurationType {
        NON_RESPONSIVE,
        RESPONSIVE,
        ALL
    };
    
    /*
     * Constructor
     */
    protected AlwStartPage() {
        super("");
        setTitle("Application Layout");             // $NLX-AlwStartPage.ApplicationLayout-1$
        setMessage("Choose the configuration for this control.", IMessageProvider.INFORMATION); // $NLX-AlwStartPage.Choosetheconfigurationforthiscont-1$

        // Setup the title font
        _titleFont = JFaceResources.getBannerFont();
        
        // Setup hand cursor
        _handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
        
        // Load images - Do not have to be disposed later - plugin maintains a list
        _defImage = ExtLibToolingPlugin.getImage("app_layout.jpg"); // $NON-NLS-1$
        if (_showResponsiveIcon) {
            _responsiveImage = NavigatorPlugin.getImage("navigatorIcons/navigatorChild.png"); // $NON-NLS-1$
        } else {
            _responsiveImage = null;
        }
        
        // Setup hyperlink color
        _hyperlinkColor = new Color(Display.getCurrent(), 0, 0, 255);
    }
    
    /*
     * Creates the UI for this wizard page
     */
    @Override
    public void createControl(final Composite parent) {
        // Setup the initial data
        populateConfigurationList();

        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = WizardUtils.createGridLayout(1, 5);
        container.setLayout(layout);

        // Create the radio group
        Group group = WizardUtils.createGroup(container, 1, 3);
        (_allRadio = WizardUtils.createRadio(group, "&All", 1, this)).setSelection(true); // $NLX-AlwStartPage.All-1$
        _responsiveRadio = WizardUtils.createRadio(group, "&Responsive", 1, this); // $NLX-AlwStartPage.Responsive-1$
        _nonResponsiveRadio = WizardUtils.createRadio(group, "N&on-Responsive", 1, this); // $NLX-AlwStartPage.NonResponsive-1$
        
        // Create the table
        _table = new Table(container, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.NO_SCROLL);
        _table.setHeaderVisible(false);
        _table.setLinesVisible(false);
        GridData gd = new GridData(SWT.DEFAULT);
        gd.horizontalSpan = 1;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
        // Set the preferred height (3 rows)
        gd.heightHint = (_defImage.getBounds().height + (MARGIN * 2)) * DEFAULT_ROWS; 
        _table.setLayoutData(gd);

        // Add one column
        new TableColumn(_table, SWT.NONE);
        
        // Add the rows
        refreshTable(ConfigurationType.ALL);

        setControl(container);
        setPageComplete(false);
    }
    
    /*
     * Reads the registry for app layout configurations
     */
    private void populateConfigurationList() {
        // Find all the app layout configurations
        List<FacesDefinition> list = ExtLibRegistryUtil.getConfigNodes(StandardRegistryMaintainer.getStandardRegistry()); 
        for (FacesDefinition def:list) {
            // Create a new LayoutConfig
            LayoutConfig lc = new LayoutConfig();
            lc.tagName = def.getTagName();
            lc.facesDef = def;

            DesignerExtension de = DesignerExtensionUtil.getExtension(def);
            if (de != null) {
                // There's a <designer-extension>
                lc.title = de.getDisplayName();
                lc.description = de.getDescription();
            }
            
            ExtLibLayoutExtension le = (ExtLibLayoutExtension) def.getExtension(LAYOUT_EXTENSION);            
            if (le != null) {
                // There is a <layout-extension>
                lc.responsive = le.isResponsive();
                lc.sampleURL = le.getSampleURL();
                if (le.getImage() != null) {
                    // Get the image if any
                    ImageDescriptor id = ImageDescriptor.createFromURL(le.getImage());
                    lc.image = id.createImage();
                }
            }

            if (lc.image == null) {
                // If there's no image add a default one
                lc.image = _defImage;
            }
            
            if (StringUtil.isEmpty(lc.title)) {
                // Use the tagName if there's no title
                lc.title = lc.facesDef.getFirstDefaultPrefix() + ":" + lc.tagName;
            }
            
            // Add this configuration to the list
            _configList.add(lc);
        }
        
        // Sort the list
        Collections.sort(_configList, new Comparator<LayoutConfig>() {
            @Override
            public int compare(LayoutConfig lc1, LayoutConfig lc2) {
                return lc1.title.compareToIgnoreCase(lc2.title);
            }
        });   
    }
    
    /*
     * Refreshes the table rows for a particular configuration type
     */
    private void refreshTable(final ConfigurationType ct) {
        _table.setRedraw(false);
        _table.removeAll();
        for (LayoutConfig lc : _configList) {
            boolean addItem = true;
            switch (ct) {
                case RESPONSIVE:
                    addItem = lc.responsive;
                    break;
                    
                case NON_RESPONSIVE:
                    addItem = !lc.responsive;
                    break;
                    
                default:
                    break;
            }
            if (addItem) {
                new TableItem(_table, SWT.NONE).setData(lc);
                _textMargin = Math.max(_textMargin, getImageWidth(lc.image) + (MARGIN * 2));
            }
        }
        _table.setRedraw(true);
        setPageComplete(false);        
    }

    /*
     * Function invoked when the page is about to be displayed or hidden
     */
    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        if (visible) {
            // Add the listeners
            _table.addControlListener(this);
            _table.addListener(SWT.MeasureItem, this);
            _table.addListener(SWT.PaintItem, this);
            _table.addListener(SWT.EraseItem, this);
            _table.addListener(SWT.MouseMove, this);
            _table.addListener(SWT.MouseUp, this);
            _table.addSelectionListener(this);
        }
        else {
            // Remove the listeners
            _table.removeSelectionListener(this);
            _table.removeListener(SWT.MouseUp, this);
            _table.removeListener(SWT.MouseMove, this);
            _table.removeListener(SWT.EraseItem, this);
            _table.removeListener(SWT.PaintItem, this);
            _table.removeListener(SWT.MeasureItem, this);
            _table.removeControlListener(this);
        }
    }

    @Override
    public void controlMoved(final ControlEvent event) {
    }

    /*
     * Function invoked when the dialog is being resized 
     */
    @Override
    public void controlResized(final ControlEvent event) {
        // Make the colum width the full width of the table
        _table.getColumn(0).setWidth(_table.getClientArea().width);
    }

    /*
     * Handles specified table events
     */
    @Override
    public void handleEvent(final Event event) {
        LayoutConfig lc;
        Point pt;
        TableItem item;
        int fullWidth = _table.getClientArea().width;
          
        switch (event.type) {
            case SWT.MeasureItem:
                lc = (LayoutConfig) event.item.getData();
                // Height is the max of image and text height
                event.height = Math.max(getTextAreaHeight(event.gc, fullWidth, lc), getImageHeight(lc.image)) + (MARGIN * 2);
                break;

            case SWT.EraseItem:
                event.detail &= ~SWT.FOREGROUND;
                // Ensure that our only column is dimensioned correctly ->
                // Have to do this here or the table will not display correctly
                // when the wizard is first opened
                if (_table.getColumn(0).getWidth() != _table.getClientArea().width) {
                    _table.getColumn(0).setWidth(_table.getClientArea().width);
                }
                break;

            case SWT.PaintItem:
                lc = (LayoutConfig) event.item.getData();
                int textHeight = getTextAreaHeight(event.gc, fullWidth, lc);
                
                // Draw the image
                if (lc.image != null) {
                    int imageOffset = (_textMargin - getImageWidth(lc.image)) / 2; 
                    event.gc.drawImage(lc.image, imageOffset, event.y + ((event.height - getImageHeight(lc.image)) / 2));
                    Rectangle rect = lc.image.getBounds(); 
                    rect.x = imageOffset;
                    rect.y = event.y + ((event.height - getImageHeight(lc.image)) / 2);
                    event.gc.drawRectangle(rect);
                }
                    
                // Draw the title
                Font origFont = event.gc.getFont();
                event.gc.setFont(_titleFont);
                int y = event.y + ((event.height - textHeight) / 2);
                event.gc.drawText(lc.title, _textMargin, y, true);

                // Draw the rssponsive thumbnail
                if (lc.responsive && _showResponsiveIcon) {
                    event.gc.drawImage(_responsiveImage, _textMargin + getTextWidth(event.gc, lc.title) + SPACER, y);
                    y += Math.max(getTextHeight(event.gc, "X") + SPACER, getImageHeight(_responsiveImage) + SPACER);
                } else {
                    y += getTextHeight(event.gc, "X") + SPACER;
                }
                
                // Draw the description
                event.gc.setFont(origFont);
                for (String line : getLines(event.gc, lc.description, fullWidth - _textMargin - MARGIN)) {
                    event.gc.drawText(line, _textMargin, y, true);
                    y += getTextHeight(event.gc, "X");
                }
                y += SPACER;
                int x = _textMargin;
                
                // Draw the sample-url
                if (lc.sampleURL != null) {
                    Color origColor = _table.getForeground();
                    event.gc.setForeground(_hyperlinkColor);
                    Point extent = event.gc.textExtent(SAMPLE_TEXT);
                    event.gc.drawText(SAMPLE_TEXT, x, y, true);
                    event.gc.drawLine(x, y + extent.y - 1, x + extent.x, y + extent.y - 1);
                    event.gc.setForeground(origColor);
                    lc.hyperlinkRect = new Rectangle(x, y, extent.x, extent.y);
                }
                break;
                
            case SWT.MouseMove:
                pt = new Point(event.x, event.y);
                item = _table.getItem(pt);
                if (item != null) {
                    lc = (LayoutConfig) item.getData();
                    
                    // Check is mouse over a hyperlink 
                    if (lc.hyperlinkRect != null) {
                        if (lc.hyperlinkRect.contains(pt)) {
                            // Change the cursor
                            _table.setCursor(_handCursor);
                            return;
                        }
                    }
                }
                
                // Not over hyperlink, reset the cursor if required
                if (_table.getCursor() == _handCursor) {
                    _table.setCursor(null);
                }
                break;
                
            case SWT.MouseUp:
                // If the cursor is over a hyperlink then launch the browser
                if (_table.getCursor() == _handCursor) {
                    pt = new Point(event.x, event.y);                    
                    item = _table.getItem(pt);
                    if (item != null) {
                        lc = (LayoutConfig) item.getData();
                        openSampleUrl(lc.sampleURL);
                    }
                }
                break;
        }
    }
    
    /*
     * Calculates the height of the text are for each table row
     */
    protected int getTextAreaHeight(final GC gc, final int fullWidth, final LayoutConfig lc) {
        Font origFont = gc.getFont();
        
        // Add the title height
        gc.setFont(_titleFont);
        int textHeight = getTextHeight(gc, "X") + SPACER;
        gc.setFont(origFont);
        if (lc.responsive && _showResponsiveIcon) {
            textHeight = Math.max(textHeight, getImageHeight(_responsiveImage) + SPACER);
        }
        
        // Add the description height
        int lineCount = getLines(gc, lc.description, fullWidth - _textMargin - MARGIN).length;
        textHeight += (lineCount * getTextHeight(gc, "X"));
        
        // Add in the footer height
        if (lc.sampleURL != null) {
            textHeight += getTextHeight(gc, "X") + SPACER;
        }
        
        return textHeight;
    }
    
    /*
     * Utility function to get text width
     */
    public static int getTextWidth(final GC gc, final String txt) {
        return gc.textExtent(txt).x;
    }

    /*
     * Utility function to get text height
     */
    public static int getTextHeight(final GC gc, final String txt) {
        return gc.textExtent(txt).y;
    }
    
    /*
     * Utility function to get and image width
     */
    public static int getImageWidth(final Image img) {
        if (img != null) {
            return img.getBounds().width;
        }
        return 0;
    }

    /*
     * Utility function to get an image height
     */
    public static int getImageHeight(final Image img) {
        if (img != null) {
            return img.getBounds().height;
        }
        return 0;
    }

    /*
     * Splits text into a number of lnes based on the specified width
     */
    protected static String[] getLines(final GC gc, final String text, final int width) {
        if (text == null) {
            return new String[]{};
        }
        
        char[] chars = text.toCharArray();
        List<String> lines = new ArrayList<String>();
        StringBuffer line = new StringBuffer();
        StringBuffer word = new StringBuffer();

        for (int i = 0; i < chars.length; i++) {
            // Handle the newline character
            if (chars[i] == '\n') {
                line.append(word);
                word.delete(0, word.length());
                lines.add(line.toString());
                line.delete(0, line.length());
                continue;
            }

            word.append(chars[i]);

            // Check for word boundary
            if (Character.isWhitespace(chars[i])) {
                // Do we need a new line for this word ?
                if (getTextWidth(gc, line.toString() + word.toString()) > width) {
                    // Yes
                    lines.add(line.toString());
                    line.delete(0, line.length());
                }
                line.append(word);
                word.delete(0, word.length());
            }
        }

        // Handle remaining word if any
        if (word.length() > 0) {
            // Do we need a new line for this word
            if (getTextWidth(gc, line.toString() + word.toString()) > width) {
                // Yes
                lines.add(line.toString());
                line.delete(0, line.length());
            }
            line.append(word);
        }

        // Handle remaining line if any
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines.toArray(new String[lines.size()]);
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent event) {
    }

    /*
     * Handles widget clicks
     */
    @Override
    public void widgetSelected(final SelectionEvent event) {
        if (event.widget == _allRadio) {
            refreshTable(ConfigurationType.ALL);            
        } else if (event.widget == _responsiveRadio) {
            refreshTable(ConfigurationType.RESPONSIVE);
        } else if (event.widget == _nonResponsiveRadio) {
            refreshTable(ConfigurationType.NON_RESPONSIVE);
        } else if (event.widget == _table) {
            // User has clicked on a table item, enable the next button
            setPageComplete(true);
        }
        getWizard().getContainer().updateButtons();            
    }
    
    /*
     * Returns the selected layout configuration if any
     */
    public LayoutConfig getSelectedLayoutConfig() {
        TableItem items[] = _table.getSelection();
        if (items.length > 0) {
            return (LayoutConfig) items[0].getData();
        }
        
        return null;
    }

    /*
     * Opens a URL in the default browser
     */
    public void openSampleUrl(URL url) {
        IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

        try {
            IWebBrowser browser = support.getExternalBrowser();
            browser.openURL(url);
        }
        catch (PartInitException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "openSampleUrl", e, "Failed to initialize browser part");  // $NON-NLS-1$ $NLE-AlwStartPage.Failedtoinitializebrowserpart-2$
            }
        } 
        catch (Exception e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "openSampleUrl", e, "Failed to launch browser");  // $NON-NLS-1$ $NLE-AlwStartPage.Failedtolaunchbrowser-2$
            }            
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        
        // Dispose all Images
        for (LayoutConfig lc:_configList) {
            if ((lc.image != null) && (!lc.image.isDisposed()) && (lc.image != _defImage)) {
                lc.image.dispose();
            }
        }     
    }

    /*
     * Utility class for holding layout configuration info
     */
    public class LayoutConfig {
        public Image           image           = null;
        public String          description     = "";
        public URL             sampleURL       = null;
        public String          title           = "";
        public String          tagName         = "";
        public boolean         responsive      = false;
        public FacesDefinition facesDef        = null;
        public Rectangle       hyperlinkRect   = null;
    }
}