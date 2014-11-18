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
package com.ibm.xsp.extlib.designer.tooling.panels.redirect;


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_LABEL;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.DataNodeAdapter;
import com.ibm.commons.iloader.node.DataNodeListener;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ICollection;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.controls.custom.CustomButton;
import com.ibm.commons.swt.controls.custom.CustomComposite;
import com.ibm.commons.swt.controls.custom.CustomTree;
import com.ibm.commons.swt.controls.custom.CustomTreeColumn;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.layouts.PropLayout2;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.panels.IPanelDataReciever;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesEditorUtils;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.domino.xsp.utils.PropertyPanelTooltipUtil;
import com.ibm.xsp.extlib.component.misc.AbstractRedirectRule;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.commands.RemoveNodeCommand;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.panels.AbstractTreeNodePanel;
import com.ibm.xsp.extlib.designer.tooling.panels.actions.RedoAction;
import com.ibm.xsp.extlib.designer.tooling.panels.actions.UndoAction;
import com.ibm.xsp.extlib.designer.tooling.panels.model.LinkContentProvider;
import com.ibm.xsp.extlib.designer.tooling.panels.model.NodeLabelProvider;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.XSPNodeUtil;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * @author doconnor
 *
 */
public class RedirectRulesPanel extends PropLayout2 implements IPanelDataReciever {
    
    private ComplexPanelComposite cmplxComposite = null; //the composite that will house the UI for the selected node
    private IPanelExtraData       data             = null; //data access to the xpage
    private Composite             leftChild        = null; //the left composite - needed so we can retrieve the DataNode as needed
    private Menu                  linkMenu         = null; //the menu that will show "Add Link" items
    private TreeViewer            ruleViewer       = null; //the viewer that will show all of the links for the current tag
    private CustomButton          addLink          = null; // the button for adding link tree nodes to tree nodes 
    private CustomButton          removeLink       = null; // the button for deleting link tree nodes 
    
    private CustomButton          moveUp           = null;
    private CustomButton          moveDown         = null;
    
    private DataNodeListener dataNodeListener = new DataNodeAdapter() {
        public void onValueChanged2( DataNode source, int record, Object object, IMember member ) {
            RedirectRulesPanel.this.ruleViewer.refresh();
        }
    };
    
    
    private KeyListener delKeyListener = new KeyAdapter() {
        public void keyPressed(KeyEvent event) {
            switch(event.keyCode) {
                case SWT.DEL: {
                    RedirectRulesPanel.this.removeSelected();
                    break;
                }
            };
        }
    };

    /**
     * Constructs a new instance of this class
     * @param parent
     * @param style
     */
    public RedirectRulesPanel(Composite parent,int style) {
        super(parent, style);
    }
    
    

    /**
     * Creates a link menu item. Adds a listener to the item, the listener
     * adds a child node to the applicationLayout. If <code>createChild</code> is
     * true then a child will be added to the currently selected node.
     * 
     * @param menu
     *          the parent menu item
     * @param label
     *          the label of the menu item
     * @param def
     *          the FacesDefinition of the TreeNode represented by the menu item
     * @param createChild
     *          a flag that determines whether or not the TreeNode being inserted should be inserted as a
     *          child of another TreeNode or as a child of the applicationLayout tag.
     */
    protected void addRulesMemuItem(Menu menu, final String label, final FacesDefinition def, final boolean createChild) {
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(label);
        
        item.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                Object newObject = null;
                Object parentObj = null;
                DataNode dn = DCUtils.findDataNode(leftChild, true);
                IMember member = dn.getMember("rules"); // $NON-NLS-1$
                ILoader loader = dn.getLoader();
                IClassDef classDef;
                try {
                    classDef = loader.loadClass(def.getNamespaceUri(), def.getTagName());
                    parentObj = dn.getCurrentObject();

                    newObject = classDef.newInstance(parentObj);

                    // if we're creating a child node, get the parent from the tree selection
                    if(createChild){
                        // this DN is set when selection changes in the tree
                        DataNode dnParent = DCUtils.findDataNode(cmplxComposite, true);
                        // find the first member of type "ITreeNode"
                        IMember parentMember = RedirectRulesPanel.this.findRulesNodeMember(dnParent.getCurrentObject(), loader, null);
                        // and change whose collection we'll add to 
                        if (parentMember instanceof ICollection) {
                            parentObj = dnParent.getCurrentObject();
                            member = parentMember;
                        }
                    }
                    IObjectCollection col = loader.getObjectCollection(parentObj, (ICollection) member);
                    col.add(col.size(), newObject, null);

                    // add a default label if appropriate
                    if(def.getProperty(EXT_LIB_ATTR_LABEL) != null && newObject instanceof Element){ 
                        //TODO DAN - Should use data node here.. should avoid instanceof Element if possible.
                        if(def.getProperty(IExtLibAttrNames.EXT_LIB_ATTR_VAR) != null){
                            String var = getLinkDisplayName(def);
                            if(StringUtil.isEmpty(var)){
                                var = "var"; // $NON-NLS-1$
                            }
                            else{
                                char[] cs = var.toCharArray();
                                if(cs != null){
                                    StringBuffer buff = new StringBuffer();
                                    for(char c : cs){
                                        if(!Character.isSpaceChar(c)){
                                            buff.append(c);
                                        }
                                    }
                                    var = buff.toString();
                                }
                            }
                            String[] vars = XPagesDOMUtil.getVars(((Element)newObject).getOwnerDocument(), null);
                            var = XPagesDOMUtil.generateUniqueVar(Arrays.asList(vars), (Element)newObject, var);
                            XPagesDOMUtil.setAttribute((Element)newObject, IExtLibAttrNames.EXT_LIB_ATTR_VAR, var);
                        }
                        String newLabel = generateNewLabel(def);
                        XPagesDOMUtil.setAttribute((Element)newObject, EXT_LIB_ATTR_LABEL, newLabel);
                    }
                } catch (NodeException e) {
                    if (ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()) {
                        ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "addLinkMenuItem", e, "Failed to create new {0}",  // $NON-NLS-1$ $NLE-AbstractTreeNodePanel.Failedtocreatenew0-2$
                                "rules"); // $NON-NLS-1$
                    }
                }
                if(newObject != null){
                    ruleViewer.refresh();
                    if (null != parentObj){
                        ruleViewer.setExpandedState(parentObj, true);
                    }
                    ruleViewer.setSelection(new StructuredSelection(newObject));
                }
            }
        });
    }

    /**
     * @param treenodeTypeName
     * @return the names of any propertoes of 'treenodeTypeName' that are required.
     */
    protected List<String> checkRequired(String treenodeTypeName) { 
        
        List<String> propnames = new ArrayList<String>();
        
        FacesRegistry registry = data.getDesignerProject().getFacesRegistry();
        
        FacesDefinition propDef = registry.findDef(EXT_LIB_NAMESPACE_URI, treenodeTypeName);
        for (String propname : propDef.getDefinedPropertyNames()) {
            boolean required = ExtLibRegistryUtil.getIsRequiredProperty(registry, propDef.getId(), propname);
            if (required) {
                propnames.add(propname);
            }
        }
        return propnames;
    }
    /**
     * Creates a new menu and adds a menu item for each TreeNode returned by {@link #findRulesNodes()}.
     * 
     * @param parent
     *          the shell parent of the menu
     * @param createChild
     *          a flag which specifies whether the menu item should deal with children of a TreeNode or add
     *          root TreeNodes
     *          
     * @return
     *          the menu containing menu items returned by {@link #findRulesNodes()}
     */
    private Menu addLinkTypes(Shell parent, boolean createChild) {
        Menu menu = new Menu(parent);
        SWTUtils.setControlId(menu, "menu.rules.add"); //$NON-NLS-1$
        List<FacesDefinition> treeNodes = findRulesNodes();
        if (treeNodes != null) {
            for (final FacesDefinition def : treeNodes) {
                String label = getLinkDisplayName(def);
                addRulesMemuItem(menu, label, def, createChild);
            }
        }
        return menu;
    }
    

    /**
     * 
     * @param def
     * @return
     */
    protected String getLinkDisplayName(FacesDefinition def) {
        DesignerExtension ext = DesignerExtensionUtil.getExtension(def);
        if (ext != null) {
            return ext.getDisplayName();
        }
        return def.getTagName();
    }
    
    protected void removeLinkPanelDataNodeListener(DataNodeListener listener) {
        if(leftChild != null){
            DataNode dataNode = DCUtils.findDataNode(leftChild, false);
            if (dataNode != null) {
                dataNode.removeDataNodeListener(RedirectRulesPanel.this.dataNodeListener);
                RedirectRulesPanel.this.dataNodeListener = null;
            }
        }
    }
    
    /**
     * create new unique text for a "label" attribute, if present, by looking at all existing. 
     * @param displayName
     * @return
     */
    protected String generateNewLabel(FacesDefinition def) {
        String label = "Label"; // $NLX-AbstractTreeNodePanel.Label-1$
        String displayName = getLinkDisplayName(def);
        if (null == displayName)
            return label;
        
        Object input = ruleViewer.getInput();
        
        ITreeContentProvider cp = (ITreeContentProvider)ruleViewer.getContentProvider();
        Object[] children = cp.getElements(input);
        
        Stack<Object> stack = new Stack<Object>(); // for non-recursive tree traversal
        stack.push(input);

        Set<String> set = new HashSet<String>(); // collects current names
        
        while (!stack.isEmpty()) {
            if (null != children) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i] instanceof Element) {
                        String l = XPagesDOMUtil.getAttribute((Element)children[i], EXT_LIB_ATTR_LABEL);
                        if (StringUtil.isNotEmpty(l))                
                             set.add(l);
                    }
                    stack.push(children[i]);
                }
            }
            children = cp.getChildren(stack.pop());
        }
        while (!stack.isEmpty());
        
        String prefix = displayName;
        String[] splits = displayName.split(" "); //$NON-NLS-1$
        if (null != splits && splits.length > 0) {
            prefix = splits[0];
            for (int i = 1; i < 100; i++) {
                String thistry = prefix + " " + String.valueOf(i); //$NON-NLS-1$
                if (!set.contains(thistry)) {
                    label = thistry;
                    break;
                }
            }
        }
        return label;
    }
    

    @Override
    protected void createLeftContents(Composite leftChild) {
        
        this.leftChild = leftChild;
        
        // set up the data node, add listener to refresh tree in case "children" is edited 
        // using right tree.
       
        linkMenu = addLinkTypes(leftChild.getShell(), false);

        CustomTree tree = new CustomTree(leftChild, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER, "tree.id"); // $NON-NLS-1$
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setCols(55);
        GridData treeData = SWTLayoutUtils.createGDFill();
        treeData.heightHint = 200;
        tree.setLayoutData(treeData);
        CustomTreeColumn rules = new CustomTreeColumn(tree, SWT.NONE, "rules.id"); // $NON-NLS-1$
        rules.setText("Rule Type");   // $NLS-RedirectRulesPanel.RuleType-1$
        rules.setWidthUnit(CustomTreeColumn.UNIT_PERCENT);
        rules.setColWidth(50);
        
        CustomTreeColumn url = new CustomTreeColumn(tree, SWT.NONE, "url.id"); // $NON-NLS-1$
        url.setText("URL");   // $NLS-RedirectRulesPanel.URL-1$
        url.setWidthUnit(CustomTreeColumn.UNIT_REMAINDER);
        
        tree.addKeyListener(delKeyListener);
        FacesDefinition def = data.getDesignerProject().getFacesRegistry().findDef(EXT_LIB_NAMESPACE_URI, "redirect"); // $NON-NLS-1$
        tree.setToolTipText(PropertyPanelTooltipUtil.getTooltipString(def, "rules")); // $NON-NLS-1$
        ruleViewer = new TreeViewer(tree);
        NodeLabelProvider labelProvider = new NodeLabelProvider(data.getDesignerProject().getFacesRegistry(), true);
        ruleViewer.setLabelProvider(labelProvider);
        LinkContentProvider contentPvdr = new LinkContentProvider("rules", data.getDesignerProject().getFacesRegistry()); // $NON-NLS-1$
        
        ruleViewer.setContentProvider(contentPvdr);

        ruleViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = event.getSelection();
                
                boolean enableRemove   = false;
                
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection structured = (IStructuredSelection) sel;
                    Object element = structured.getFirstElement();
                    if (element instanceof Element) {
                        Element e = (Element) element;
                        refreshMoveButtons(e);
                        DataNode dn = DCUtils.findDataNode(cmplxComposite, true);
                        ILoader loader = dn.getLoader();
                        
                        if (null == loader) {
                            loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(getExtraData().getDesignerProject());
                        }
                        
                        if (loader != null) {
                            dn.setDataProvider(new SingleCollection(e));
                            try {
                                dn.setClassDef(loader.getClassOf(e));
                            } catch (NodeException ne) {
                                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(ne, "Failed to get class def of element ", e.getTagName());  // $NLE-AbstractTreeNodePanel.Failedtogetclassdefofelement-1$
                                }
                            }
                            // enable "remove"
                            enableRemove = true;
                        }
                        else {
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warnp(AbstractTreeNodePanel.class, "selectionChanged", "No loader for current data node"); //$NON-NLS-1$ // $NLW-AbstractTreeNodePanel.Noloaderforcurrentdatanode-2$
                        }

                        // show the properties on the right
                        cmplxComposite.updatePanel(e.getNamespaceURI(), e.getLocalName());
                        // re-layout parents until we get to a scrolled composite
                        getDisplay().asyncExec(new Runnable() {
                            /*
                             * (non-Javadoc)
                             * @see java.lang.Runnable#run()
                             */
                            public void run() {
                                if (RedirectRulesPanel.this.isDisposed()) { 
                                    return;
                                }
                                Composite parent = cmplxComposite;
                                Composite prevParent = parent;
                                while (parent != null && !parent.isDisposed()) {
                                    try {
                                        if (parent instanceof ScrolledComposite) {
                                            ((ScrolledComposite) parent).setMinSize(prevParent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                                            break;
                                        }
                                        parent.layout();
                                    } catch (Throwable t) {
                                        if (ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isWarnEnabled()) {
                                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warnp(RedirectRulesPanel.this,
                                                    "selectionChanged", t, //$NON-NLS-1$
                                                    "Error encountered when refreshing applicationLayout properties panel"); // $NLW-AbstractTreeNodePanel.Errorencounteredwhenrefreshingapp-1$
                                        }
                                    }
                                    prevParent = parent;
                                    parent = parent.getParent();
                                }
                            }
                        });
                    } 
                    else { // (it's not instanceof Element, must be null
                        if (null == element && null != cmplxComposite) {
                            cmplxComposite.updatePanel(null, null);
                        }
                    }
                }
                removeLink.setEnabled(enableRemove);
            }
        });
        
        ruleViewer.setInput(getDataNode());
        leftChild.addDisposeListener(new DisposeListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            public void widgetDisposed(DisposeEvent event) {
                removeLinkPanelDataNodeListener(dataNodeListener);
            }
        });
        
        Composite buttonParent = new Composite(leftChild, SWT.NONE);
        buttonParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        GridData bpGD = SWTLayoutUtils.createGDFillVerticalNoGrab();
        bpGD.verticalAlignment = GridData.BEGINNING;
        buttonParent.setLayoutData(bpGD);
        moveUp = new CustomButton(buttonParent, SWT.NONE, "move.up.id");//$NON-NLS-1$
        moveUp.setToolTipText("Moves the currently selected item up"); // $NLX-AbstractTreeNodePanel.Movesthecurrentlyselecteditemup-1$
        moveUp.setImage(getDisabledImage(true));
        moveUp.addSelectionListener(new SelectionAdapter() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                ISelection sel = ruleViewer.getSelection();
                if(sel instanceof IStructuredSelection){
                    Object o = ((IStructuredSelection)sel).getFirstElement();
                    if(o instanceof Element){
                        moveUp((Element)o);
                    }
                }
            }
            
        });
        
        moveDown = new CustomButton(buttonParent, SWT.NONE, "move.down.id");//$NON-NLS-1$
        moveDown.setToolTipText("Moves the currently selected item down"); // $NLX-AbstractTreeNodePanel.Movesthecurrentlyselecteditemdown-1$
        moveDown.setImage(getDisabledImage(false));
        moveDown.addSelectionListener(new SelectionAdapter() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                ISelection sel = ruleViewer.getSelection();
                if(sel instanceof IStructuredSelection){
                    Object o = ((IStructuredSelection)sel).getFirstElement();
                    if(o instanceof Element){
                        moveDown((Element)o);
                    }
                }
            }
        });
        if(ruleViewer.getSelection() instanceof IStructuredSelection){
            Object o = ((IStructuredSelection)ruleViewer.getSelection()).getFirstElement();
            if(o instanceof Element){
                refreshMoveButtons((Element)o);
            }
        }
        
        DataNode dataNode = DCUtils.findDataNode(leftChild, false);
        dataNode.addDataNodeListener(dataNodeListener);
    }

    @Override
    protected void createRightContents(Composite rightChild) {
        ((GridData)rightChild.getLayoutData()).horizontalAlignment = SWT.FILL;
        cmplxComposite = new ComplexPanelComposite(rightChild, SWT.NONE);
        initComplexDataNode(cmplxComposite);
        cmplxComposite.updatePanelData(data);
        cmplxComposite.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        cmplxComposite.setLayoutData(SWTLayoutUtils.createGDFill());
        
        cmplxComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        TreeItem[] items = ruleViewer.getTree().getItems();
        if(items != null && items.length > 0){
            Object firstItem = items[0].getData();
            ruleViewer.setSelection(new StructuredSelection(firstItem));
        }
    }

    private List<FacesDefinition> findRulesNodes() {
        if (this.data != null && this.data.getDesignerProject() != null) {
            FacesRegistry registry = this.data.getDesignerProject().getFacesRegistry();
            if (registry != null) {
                return ExtLibRegistryUtil.getNodes(registry, "redirect", "rules"); // $NON-NLS-1$ $NON-NLS-2$
            }
        }
        return null;
    }

    @Override
    protected int getNumLeftColumns() {
        return 2;
    }

    @Override
    protected int getNumRightColumns() {
        return 1;
    }
    
    private void initComplexDataNode(ComplexPanelComposite parent) {
        DataNode dataNode = DCUtils.findDataNode(parent, false);
        if (dataNode != null) {
            DCUtils.initDataBinding(parent.getParent());
            DataNode newNode = DCUtils.findDataNode(parent, true);
            newNode.setClassDef(dataNode.getClassDef());
        }
    }

    /**
     * 
     */
    private void removeSelected() {
        
        // save the current table selection
        Tree tree = ruleViewer.getTree();
        TreeItem[] selectedItems = tree.getSelection();
        int selectedIndex = -1;
        if (null != selectedItems && selectedItems.length > 0) {
            TreeItem[] items = tree.getItems();
            if (null != items && items.length > 0) {
                for (int i = 0; i < items.length; i++) {
                    if (selectedItems[0].equals(items[i])) {
                        selectedIndex = i;
                        break;
                    }
                }
                if (selectedIndex == items.length -1)
                    selectedIndex--;
            }
        }
        else{
            //play the prompt sound!
            Display.getDefault().beep();
            return;
        }
        
        // the DN for the complextComposite will be the current selection
        DataNode dnSelected = DCUtils.findDataNode(cmplxComposite, true);
        ILoader loader = dnSelected.getLoader();
        Element selected = (Element)dnSelected.getCurrentObject();
        
        if (null == selected) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn("\"Delete\" links menu item pressed with no selected element"); //$NON-NLS-1$
            return;
        }
        
        boolean doIt = true;
        
        // need to check if we want to warn that they are deleting a container with child elements 
        
        NodeList childrenNodes = selected.getChildNodes();
        
        if (null != childrenNodes && childrenNodes.getLength() > 0) {
            // does it have any non-text children?
            boolean hasChildElement = false;
            for (int i = 0; i < childrenNodes.getLength() && !hasChildElement; i++) {
                Node node = childrenNodes.item(i);
                hasChildElement = (node.getNodeType() == Node.ELEMENT_NODE);
            }
            
            // GGRD8UAP4U check if this -really- has children, or just child element nodes (like an empty <this.children>)
            if (hasChildElement) {
                ITreeContentProvider cp = (ITreeContentProvider)ruleViewer.getContentProvider();
                Object[] childrenFromProvider = cp.getChildren(selected);
                if (childrenFromProvider == null || childrenFromProvider.length == 0) {
                    hasChildElement = false;
                }
            }
            
            if (hasChildElement) {
                
                // can this have children? if so, get the member that contains the children
                IMember childrenMember = findRulesNodeMember(selected, loader, null);
    
                // if selected has child DOM Nodes and could have child tree nodes 
                // (has a member of type iTreeNode), warn...
                if (childrenMember != null) {
                    doIt = MessageDialog.openConfirm(getShell(), 
                            "Application Layout", // $NLX-AbstractTreeNodePanel.ApplicationLayout-1$
                    "This Node has children. Are you sure you want to remove it, and all of its children?"); // $NLX-AbstractTreeNodePanel.ThisNodehaschildrenAreyousureyouw-1$
                }
            }
        }

        Node parent = selected.getParentNode();        
        if (doIt && null != parent) {
            String memberName = parent.getLocalName();
            try {
                if (memberName.startsWith(IExtLibTagNames.TAG_THIS_PREFIX)){
                    memberName = memberName.substring(IExtLibTagNames.TAG_THIS_PREFIX.length());
                }

                Node grandparentNode = selected.getParentNode().getParentNode();
                IMember containerMember = findRulesNodeMember(grandparentNode, loader, memberName);
                if (containerMember instanceof ICollection) {
                    IObjectCollection c  = loader.getObjectCollection(grandparentNode, (ICollection)containerMember);
                    if (null != c) {
                        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
                        IAction undo = null;
                        IAction redo = null;
                        CommandStack stack = null;
                        IActionBars bars = null;
                        if(part instanceof IViewPart){
                            bars = ((IViewPart)part).getViewSite().getActionBars();
                            if(bars != null){
                                undo = bars.getGlobalActionHandler(ActionFactory.UNDO.getId());
                                redo = bars.getGlobalActionHandler(ActionFactory.REDO.getId());
                                
                                if(redo == null){
                                    stack = XPagesEditorUtils.getCommandStack(null);
                                    redo = new RedoAction(stack);
                                }
                                if(undo == null){
                                    if(stack == null){
                                        stack = XPagesEditorUtils.getCommandStack(null);
                                    }
                                    undo = new UndoAction(stack, redo);
                                    if(redo instanceof RedoAction){
                                        ((RedoAction)redo).setUndoAction((UndoAction)undo);
                                    }
                                }
                                bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undo);
                                bars.setGlobalActionHandler(ActionFactory.REDO.getId(), redo);
                            }
                        }
                        for (int i=0; i < c.size(); i++){
                            // remove the member of the collection that is selected
                            if (c.get(i).equals(selected)) {
                                Node n = (Node)c.get(i);
                                removeAndUpdateUndoActions(n, undo, redo, stack, bars);
                                break;
                            }
                        }

                        // GGRD8UAP4U get the children again.... if 0, remove the "this.children"
                        IObjectCollection cpost  = loader.getObjectCollection(grandparentNode, (ICollection)containerMember);
                        if (null != cpost && cpost.size() == 0) {
                            if (containerMember.getName().endsWith("children")) { //$NON-NLS-1$
                                removeAndUpdateUndoActions(parent, undo, redo, stack, bars);
                            }
                        }
                    }
                }
                // try to select the previous (or next) one
                if (selectedIndex >= 0) {
                    TreeItem[] items = tree.getItems();
                    if (selectedIndex < items.length){
                        ruleViewer.refresh();
                        tree.select(items[selectedIndex]);
                    }
                }
                else {
                    ruleViewer.refresh();
                    cmplxComposite.updatePanel(null, null);
                }
            }
            catch(NodeException e) {
                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, "Failed to get children for Data Node ", memberName);  // $NLE-AbstractTreeNodePanel.FailedtogetchildrenforDataNode-1$
                }
            }
        }
    }
    
    
    /*
     * 
     */
    
    private void removeAndUpdateUndoActions(Node n, IAction undo, IAction redo, CommandStack stack, IActionBars bars) {
        RemoveNodeCommand cmd = new RemoveNodeCommand(n, true);
        XPagesEditorUtils.executeCommand(cmd, (IEditorPart)null);
        if(stack != null){
            if(undo != null){
                undo.setEnabled(stack.canUndo());
            }
            if(redo != null){
                redo.setEnabled(stack.canRedo());
            }
            if(bars != null){
                bars.updateActionBars();
            }
        }
    }

    private IMember findRulesNodeMember(Object element, ILoader loader, String nameToFind) {
        IMember member = null;
        if (null != element){
            try {
                IClassDef classDef = loader.getClassOf(element);
                if (null != classDef) {
                    Object obj = classDef.getNativeClass(); //@TODO: is getNativeClass() OK here?
                    if (obj instanceof FacesDefinition) {
                        FacesDefinition fdef = (FacesDefinition)obj;
                        Collection<String> names = fdef.getPropertyNames();
                        for(String name : names){
                            FacesProperty prop = fdef.getProperty(name);
                            if(prop instanceof FacesContainerProperty){
                                FacesContainerProperty cp = (FacesContainerProperty)prop;
                                if(cp.getItemProperty() != null){
                                    FacesProperty fp = cp.getItemProperty();
                                    if(fp.getJavaClass().isAssignableFrom(AbstractRedirectRule.class)){
                                        if (null == nameToFind || nameToFind.equals(name)) {
                                            member = classDef.getMember(name);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch(NodeException e) {
                if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, StringUtil.format("Failed to get class definition or native class for {0}", ((Element)element).getTagName())); // $NLE-AbstractTreeNodePanel.Failedtogetclassdefinitionornativ-1$
                }
            }
        }
        return member;
    }
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.panels.IPanelDataReciever#setExtraData(com.ibm.designer.domino.xsp.api.panels.IPanelExtraData)
     */
    public void setExtraData(IPanelExtraData data) {
        this.data = data;
    }
    
    
    protected IPanelExtraData getExtraData() {
        return data;
    }

    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.panels.IPanelDataReciever#setExtraData(com.ibm.designer.domino.xsp.api.panels.IPanelExtraData)
     */
    @Override
    protected void createContents() {
        this.getParent().addDisposeListener(new DisposeListener() {
            /*
             * (non-Javadoc)
             * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
             */
            public void widgetDisposed(DisposeEvent e) {
                if(!isDisposed()){
                    RedirectRulesPanel.this.dispose();
                }
            }
        });
        initLayout();
        setCurrentParent(this);
        
        createLinkButtons();
        super.createContents();
    }
    
    protected Composite createPanel(Composite parent, int columns) {

        Composite c = new Composite(this, SWT.NONE);
        GridLayout gl = SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(columns);
        gl.marginWidth = gl.marginLeft = 0;
        c.setLayout(gl);
        GridData data = SWTLayoutUtils.createGDFillHorizontalNoGrab();
        data.horizontalIndent = 0;
        data.horizontalSpan = 2;
        c.setLayoutData(data);
        return c;
    }

    protected void clearLinkSelection() {

        if (null != ruleViewer) {
            ruleViewer.setSelection(new StructuredSelection());
        }
    }
    
    
    protected void enableButtons(boolean enabled) {
        if (null != addLink)       addLink.setEnabled(enabled);
    }
    
    protected void enableDelete(boolean enabled){
        if(enabled && ruleViewer != null){
            ISelection selection = ruleViewer.getSelection();
            if(selection.isEmpty()){
                enabled = false; //can't enable delete if there is nothing selected in the table
            }
        }
        if (null != removeLink){    
            removeLink.setEnabled(enabled);
        }
    }
    
    protected void updateViewers() {
        if (null != ruleViewer) {
            ruleViewer.refresh();
        }
        if (null != cmplxComposite) {
            cmplxComposite.update();
        }
    }
    
    protected void createLinkButtons() {
        
        createHeading("Redirection Rules", 2);  // $NLX-RedirectRulesPanel.RedirectionRules-1$
        
        createLabel("Redirection rules list, the order of the rules is significant as the first matching rule determines the redirect page.",   // $NLX-RedirectRulesPanel.Redirectionruleslisttheorderofthe-1$
                createSpanGD(2));
        
        CustomComposite buttonParent = new CustomComposite(this, SWT.NONE, "button.parent.id"); // $NON-NLS-1$
        GridLayout gl = SWTLayoutUtils.createLayoutDefaultSpacing(2);
        gl.marginWidth = 0;
        buttonParent.setLayout(gl);
        GridData gd = SWTLayoutUtils.createGDFillHorizontal();
        gd.horizontalSpan = 2;
        buttonParent.setLayoutData(gd);

        addLink = new CustomButton(buttonParent, SWT.PUSH, "add.link.id"); // $NON-NLS-1$
        addLink.setText("Add");   // $NLX-RedirectRulesPanel.Add-1$
        addLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Button item = (Button) event.widget;
                Rectangle rect = item.getBounds();
                Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
                linkMenu.setLocation(pt.x, pt.y + rect.height);

                linkMenu.setVisible(true);
                while (!linkMenu.isDisposed() && linkMenu.isVisible()) {
                    if (!getDisplay().readAndDispatch()){
                        getDisplay().sleep();
                    }
                }
            }
        });

        removeLink = new CustomButton(buttonParent, SWT.PUSH, "remove.id"); // $NON-NLS-1$
        removeLink.setText("Remove"); // $NLX-RedirectRulesPanel.Remove-1$
        removeLink.setEnabled(false); // will enable on appropriate link selection
        removeLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                RedirectRulesPanel.this.removeSelected();
            }
        });
    }
    
    protected void refreshMoveButtons(Element e){
        if(moveUp != null && !moveUp.isDisposed()){
            boolean enabled = canMoveUp(e);
            if(enabled){
                moveUp.setImage(getEnabledImage(true));
            }
            else{
                moveUp.setImage(getDisabledImage(true));
            }
            moveUp.setEnabled(enabled);
        }
        if(moveDown != null && !moveDown.isDisposed()){
            boolean enabled = canMoveDown(e);
            if(enabled){
                moveDown.setImage(getEnabledImage(false));
            }
            else{
                moveDown.setImage(getDisabledImage(false));
            }
            moveDown.setEnabled(enabled);
        }
    }

    /**
     * Move the element down in the underlying model
     */
    protected void moveDown(Element e) {
        Node nextSibling = XSPNodeUtil.getNextSibling(e);
        if (nextSibling != null) {
            nextSibling = XSPNodeUtil.getNextSibling(nextSibling);
            if(nextSibling != null){
                e.getParentNode().insertBefore(e, nextSibling);
            }
            else{
                e.getParentNode().appendChild(e);
            }
            XPagesDOMUtil.formatNode(e.getParentNode(), null);
            ruleViewer.refresh();
            ruleViewer.setSelection(new StructuredSelection(e));
        }
    }

    /**
     * Move the element up in the underlying model.
     */
    protected void moveUp(Element e) {
        Node prevSibling = XSPNodeUtil.getPreviousSibling(e);
        if (prevSibling != null) {
            e.getParentNode().insertBefore(e, prevSibling);
            XPagesDOMUtil.formatNode(e.getParentNode(), null);
            ruleViewer.refresh();
            ruleViewer.setSelection(new StructuredSelection(e));
        }
    }
    
    protected boolean canMoveUp(Element e){
        return XSPNodeUtil.getPreviousSibling(e) != null;
    }
    
    protected boolean canMoveDown(Element e){
        return XSPNodeUtil.getNextSibling(e) != null;
    }
    
    private Image getDisabledImage(boolean upImage){
        if(upImage){
            return ExtLibToolingPlugin.getImage("moveUpDisabled.gif"); // $NON-NLS-1$
        }else{
            return ExtLibToolingPlugin.getImage("moveDownDisabled.gif"); // $NON-NLS-1$
        }
    }
    
    private Image getEnabledImage(boolean upImage){
        if(upImage){
            return ExtLibToolingPlugin.getImage("moveUp.gif"); // $NON-NLS-1$
        }else{
            return ExtLibToolingPlugin.getImage("moveDown.gif"); // $NON-NLS-1$
        }
    }
    
    protected TreeViewer getLinkTreeViewer(){
        return ruleViewer;
    }


    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.layouts.PropLayout#dispose()
     */
    @Override
    public void dispose() {
        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        IActionBars bars = null;
        if(part instanceof IViewPart){
            bars = ((IViewPart)part).getViewSite().getActionBars();
            if(bars != null){
                bars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
                bars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
            }
        }
        if(!isDisposed()){
            super.dispose();
        }
    } 
    
}