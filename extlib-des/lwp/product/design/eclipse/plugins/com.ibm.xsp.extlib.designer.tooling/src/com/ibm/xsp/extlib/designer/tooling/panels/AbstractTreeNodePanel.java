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
package com.ibm.xsp.extlib.designer.tooling.panels;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
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
import org.eclipse.swt.widgets.Label;
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
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.commands.RemoveNodeCommand;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames;
import com.ibm.xsp.extlib.designer.tooling.panels.actions.RedoAction;
import com.ibm.xsp.extlib.designer.tooling.panels.actions.UndoAction;
import com.ibm.xsp.extlib.designer.tooling.panels.model.LinkContentProvider;
import com.ibm.xsp.extlib.designer.tooling.panels.model.NodeLabelProvider;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.XSPNodeUtil;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This abstract panel encapsulates all of the functionality needed to create a "Links" panel for the
 * applicationLayout tag. The panel has two main sections, the left column and right column.
 * The left column will display a TreeViewer. The TreeViewer will display the link node children
 * of the attribute provided via the abstract method {@link #getLinkAttributeName()}.<br/>
 * When items are selected in the Tree the right column will be updated. This works similar
 * to the Data Panel on an XPage. An item is selected in the TreeViewer and as a result a corresponding
 * properties panel is displayed in the right hand column.
 * 
 * 
 * @author doconnor
 *
 */
public abstract class AbstractTreeNodePanel extends PropLayout2 implements IPanelDataReciever {
    
    
    final private TreeNodePanelDescriptor  _descriptor;
    
    private Menu                  _childLinkMenu    = null; //the menu that will show "Add Child Link" items
    private ComplexPanelComposite _complexComposite = null; //the composite that will house the UI for the selected node
    private IPanelExtraData       _data             = null; //data access to the xpage
    private Composite             _leftChild        = null; //the left composite - needed so we can retrieve the DataNode as needed
    private Menu                  _linkMenu         = null; //the menu that will show "Add Link" items
    private TreeViewer            _linkViewer       = null; //the viewer that will show all of the links for the current tag
    private CustomButton          _addLink          = null; // the button for adding link tree nodes to tree nodes 
    private CustomButton          _addChildLink     = null; // the button for adding link tree nodes to tree nodes that can have children
    private CustomButton          _removeLink       = null; // the button for deleting link tree nodes 
    
    private CustomButton          _moveUp           = null;
    private CustomButton          _moveDown         = null;
    
    private DataNodeListener dataNodeListener = new DataNodeAdapter() {
        public void onValueChanged2( DataNode source, int record, Object object, IMember member ) {
            AbstractTreeNodePanel.this._linkViewer.refresh();
        }
    };
    
    
    private KeyListener delKeyListener = new KeyAdapter() {
        public void keyPressed(KeyEvent event) {
            switch(event.keyCode) {
                case SWT.DEL: {
                    AbstractTreeNodePanel.this.removeSelected();
                    break;
                }
            };
        }
    };

    
    
    protected abstract String getLinkAttributeDescription();
    
 
    /**
     * Constructs a new instance of this class
     * @param parent
     * @param style
     */
    public AbstractTreeNodePanel(Composite parent, TreeNodePanelDescriptor descriptor, int style) {
        super(parent, style);
        _descriptor = descriptor;
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
    protected void addLinkMenuItem(Menu menu, final String label, final FacesDefinition def, final boolean createChild) {
        MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(label);
        
        item.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                Object newObject = null;
                Object parentObj = null;
                DataNode dn = DCUtils.findDataNode(_leftChild, true);
                IMember member = dn.getMember(_descriptor.propertyName);
                ILoader loader = dn.getLoader();
                IClassDef classDef;
                try {
                    classDef = loader.loadClass(def.getNamespaceUri(), def.getTagName());
                    parentObj = dn.getCurrentObject();

                    newObject = classDef.newInstance(parentObj);

                    // if we're creating a child node, get the parent from the tree selection
                    if(createChild){
                        // this DN is set when selection changes in the tree
                        DataNode dnParent = DCUtils.findDataNode(_complexComposite, true);
                        // find the first member of type "ITreeNode"
                        IMember parentMember = AbstractTreeNodePanel.this.findTreeNodeMember(dnParent.getCurrentObject(), loader, null);
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
                            String var = getLinkDisplayName(def, false);
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
                                _descriptor.propertyName);
                    }
                }
                if(newObject != null){
                    _linkViewer.refresh();
                    if (null != parentObj){
                        _linkViewer.setExpandedState(parentObj, true);
                    }
                    _linkViewer.setSelection(new StructuredSelection(newObject));
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
        
        FacesRegistry registry = _data.getDesignerProject().getFacesRegistry();
        
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
     * Creates a new menu and adds a menu item for each TreeNode returned by {@link #findTreeNodes()}.
     * 
     * @param parent
     *          the shell parent of the menu
     * @param createChild
     *          a flag which specifies whether the menu item should deal with children of a TreeNode or add
     *          root TreeNodes
     *          
     * @return
     *          the menu containing menu items returned by {@link #findTreeNodes()}
     */
    private Menu addLinkTypes(Shell parent, boolean createChild) {
        Menu menu = new Menu(parent);
        SWTUtils.setControlId(menu, "menu.links.add"); //$NON-NLS-1$
        List<FacesDefinition> treeNodes = findTreeNodes();
        if (treeNodes != null) {
            for (final FacesDefinition def : treeNodes) {
                // @todo: do we want to look for required properties here?
                
                String label = getLinkDisplayName(def, true);
                addLinkMenuItem(menu, label, def, createChild);
            }
        }
        return menu;
    }
    

    /**
     * 
     * @param def
     * @return
     */
    protected String getLinkDisplayName(FacesDefinition def, boolean forMenu) {
        if(!forMenu && StringUtil.equals(def.getTagName(), IExtLibTagNames.EXT_LIB_TAG_BASIC_LEAF_NODE)){
            return "Link"; // $NLX-AbstractTreeNodePanel.Link-1$
        }
        DesignerExtension ext = DesignerExtensionUtil.getExtension(def);
        if (ext != null) {
            return ext.getDisplayName();
        }
        return def.getTagName();
    }

    
    /**
     * setup the DataNode for the panel using the member (attribute) name provided by {@link #getLinksContainingTypeName()}  
     * @param leftChild
     * @param listener
     */
    protected void setupLinkPanelDataNode(Composite leftChild, DataNodeListener listener) {
        ExtLibPanelUtil.initDataNode(leftChild, listener, _descriptor.complexName); 
    }
    
    
    protected void removeLinkPanelDataNodeListener(DataNodeListener listener) {
        if(_leftChild != null){
            DataNode dataNode = DCUtils.findDataNode(_leftChild, false);
            if (dataNode != null) {
                dataNode.removeDataNodeListener(AbstractTreeNodePanel.this.dataNodeListener);
                AbstractTreeNodePanel.this.dataNodeListener = null;
            }
        }
    }
    
    /**
     * create new unique text for a "label" attribute, if present, by looking at all existing. 
     * @param displayName
     * @return
     */
    protected String generateNewLabel(FacesDefinition def) {
        if(StringUtil.equals(def.getTagName(), IExtLibTagNames.EXT_LIB_TAG_DOMINO_VIEW_LIST)){
            return null; //for the dominoViewListTreeNode tag we do not want to set the label!
        }
        String label = "Label"; // $NLX-AbstractTreeNodePanel.Label-1$
        String displayName = getLinkDisplayName(def, false);
        if (null == displayName)
            return label;
        
        Object input = _linkViewer.getInput();
        
        ITreeContentProvider cp = (ITreeContentProvider)_linkViewer.getContentProvider();
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
        
        _leftChild = leftChild;
        
        // set up the data node, add listener to refresh tree in case "children" is edited 
        // using right tree.
        
        setupLinkPanelDataNode(leftChild, this.dataNodeListener);
       
        _linkMenu = addLinkTypes(leftChild.getShell(), false);
        _childLinkMenu = addLinkTypes(leftChild.getShell(), true);

        CustomTree tree = new CustomTree(leftChild, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER, "tree.id"); // $NON-NLS-1$
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        tree.setCols(55);
        GridData treeData = SWTLayoutUtils.createGDFill();
        treeData.heightHint = 200;
        treeData.horizontalIndent = getControlIndentAmt();
        tree.setLayoutData(treeData);
        CustomTreeColumn links = new CustomTreeColumn(tree, SWT.NONE, "links.id"); // $NON-NLS-1$
        links.setText("Links");  // $NLX-AbstractTreeNodePanel.Links-1$
        links.setWidthUnit(CustomTreeColumn.UNIT_PERCENT);
        links.setColWidth(100);
        
        tree.addKeyListener(delKeyListener);

        _linkViewer = new TreeViewer(tree);
        NodeLabelProvider labelProvider = new NodeLabelProvider(_data.getDesignerProject().getFacesRegistry());
        _linkViewer.setLabelProvider(labelProvider);
        LinkContentProvider contentPvdr = new LinkContentProvider(_descriptor.propertyName, _data.getDesignerProject().getFacesRegistry());
        
        _linkViewer.setContentProvider(contentPvdr);

        _linkViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = event.getSelection();
                
                boolean enableRemove   = false;
                boolean enableAddChild = false;
                
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection structured = (IStructuredSelection) sel;
                    Object element = structured.getFirstElement();
                    if (element instanceof Element) {
                        Element e = (Element) element;
                        refreshMoveButtons(e);
                        DataNode dn = DCUtils.findDataNode(_complexComposite, true);
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
                            // enablement for "Add Child"...
                            IMember mTreeNode = AbstractTreeNodePanel.this.findTreeNodeMember(e, dn.getLoader(), null);
                            enableAddChild = mTreeNode != null;
                        }
                        else {
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warnp(AbstractTreeNodePanel.class, "selectionChanged", "No loader for current data node"); //$NON-NLS-1$ // $NLW-AbstractTreeNodePanel.Noloaderforcurrentdatanode-2$
                        }

                        // show the properties on the right
                        _complexComposite.updatePanel(e.getNamespaceURI(), e.getLocalName());
                        // re-layout parents until we get to a scrolled composite
                        getDisplay().asyncExec(new Runnable() {
                            /*
                             * (non-Javadoc)
                             * @see java.lang.Runnable#run()
                             */
                            public void run() {
                                if (AbstractTreeNodePanel.this.isDisposed()) { 
                                    return;
                                }
                                Composite parent = _complexComposite;
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
                                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warnp(AbstractTreeNodePanel.this,
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
                        if (null == element && null != _complexComposite) {
                            _complexComposite.updatePanel(null, null);
                        }
                    }
                }
                _removeLink.setEnabled(enableRemove);
                _addChildLink.setEnabled(enableAddChild);
            }
        });
        
        _linkViewer.setInput(getDataNode());
        _leftChild.addDisposeListener(new DisposeListener() {
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
        _moveUp = new CustomButton(buttonParent, SWT.NONE, "move.up.id");//$NON-NLS-1$
        _moveUp.setToolTipText("Moves the currently selected item up"); // $NLX-AbstractTreeNodePanel.Movesthecurrentlyselecteditemup-1$
        _moveUp.setImage(getDisabledImage(true));
        _moveUp.addSelectionListener(new SelectionAdapter() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                ISelection sel = _linkViewer.getSelection();
                if(sel instanceof IStructuredSelection){
                    Object o = ((IStructuredSelection)sel).getFirstElement();
                    if(o instanceof Element){
                        moveUp((Element)o);
                    }
                }
            }
            
        });
        
        _moveDown = new CustomButton(buttonParent, SWT.NONE, "move.down.id");//$NON-NLS-1$
        _moveDown.setToolTipText("Moves the currently selected item down"); // $NLX-AbstractTreeNodePanel.Movesthecurrentlyselecteditemdown-1$
        _moveDown.setImage(getDisabledImage(false));
        _moveDown.addSelectionListener(new SelectionAdapter() {

            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent event) {
                super.widgetSelected(event);
                ISelection sel = _linkViewer.getSelection();
                if(sel instanceof IStructuredSelection){
                    Object o = ((IStructuredSelection)sel).getFirstElement();
                    if(o instanceof Element){
                        moveDown((Element)o);
                    }
                }
            }
        });
        if(_linkViewer.getSelection() instanceof IStructuredSelection){
            Object o = ((IStructuredSelection)_linkViewer.getSelection()).getFirstElement();
            if(o instanceof Element){
                refreshMoveButtons((Element)o);
            }
        }
    }

    @Override
    protected void createRightContents(Composite rightChild) {
        ((GridData)rightChild.getLayoutData()).horizontalAlignment = SWT.FILL;
        _complexComposite = new ComplexPanelComposite(rightChild, SWT.NONE);
        initComplexDataNode(_complexComposite);
        _complexComposite.updatePanelData(_data);
        _complexComposite.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(1));
        _complexComposite.setLayoutData(SWTLayoutUtils.createGDFill());
        
        _complexComposite.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        
        TreeItem[] items = _linkViewer.getTree().getItems();
        if(items != null && items.length > 0){
            Object firstItem = items[0].getData();
            _linkViewer.setSelection(new StructuredSelection(firstItem));
        }
    }

    private List<FacesDefinition> findTreeNodes() {
        if (this._data != null && this._data.getDesignerProject() != null) {
            FacesRegistry registry = this._data.getDesignerProject().getFacesRegistry();
            if (registry != null) {
                return ExtLibRegistryUtil.getNodes(registry, _descriptor.complexType, _descriptor.propertyName);
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
        Tree tree = _linkViewer.getTree();
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
        DataNode dnSelected = DCUtils.findDataNode(_complexComposite, true);
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
            	ITreeContentProvider cp = (ITreeContentProvider)_linkViewer.getContentProvider();
	        	Object[] childrenFromProvider = cp.getChildren(selected);
	        	if (childrenFromProvider == null || childrenFromProvider.length == 0) {
	        		hasChildElement = false;
	        	}
            }
            
            if (hasChildElement) {
            	
                // can this have children? if so, get the member that contains the children
                IMember childrenMember = findTreeNodeMember(selected, loader, null);
    
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
                IMember containerMember = findTreeNodeMember(grandparentNode, loader, memberName);
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
                        if (null != cpost && cpost.size() == 0 && parent != null) {
                        	if (containerMember.getName().endsWith("children")) { //$NON-NLS-1$
                        		removeAndUpdateUndoActions(parent, undo, redo, stack, bars);
                        	}
                        }
                    }
                }
                // try to select the previous (or next) one
                if (selectedIndex >= 0) {
                    TreeItem[] items = tree.getItems();
                    if (selectedIndex < items.length)
                        tree.select(items[selectedIndex]);
                }
                else {
                    _complexComposite.updatePanel(null, null);
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
        RemoveNodeCommand cmd = new RemoveNodeCommand(n);
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

    private IMember findTreeNodeMember(Object element, ILoader loader, String nameToFind) {
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
                                    if(fp.getJavaClass().isAssignableFrom(ITreeNode.class)){
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
        this._data = data;
    }
    
    
    protected IPanelExtraData getExtraData() {
        return _data;
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
                    AbstractTreeNodePanel.this.dispose();
                }
            }
        });
        initLayout();
        setCurrentParent(this);
        
        ExtLibPanelUtil.initDataNode(this, null, EXT_LIB_ATTR_CONFIGURATION);
        
        createTopSection();
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

    
    
    private Composite _desc;
    
    protected Composite getDescriptionPanel() {
        if (null == _desc) {
            _desc = createPanel(this, 1);
        }
        return _desc;
    }
    
    
    
    protected void createTopSection() {
        String desc = getLinkAttributeDescription();
        if(StringUtil.isNotEmpty(desc)){
            Composite c = getDescriptionPanel();
            Label l = new Label(c, SWT.NONE);
            GridData gd = new GridData();
            gd.horizontalIndent = getControlIndentAmt();
            l.setText(desc);
            l.setLayoutData(gd);
        }
    }
    
    
    protected void clearLinkSelection() {

        if (null != _linkViewer) {
            _linkViewer.setSelection(new StructuredSelection());
        }
    }
    
    
    protected void enableButtons(boolean enabled) {
        if (null != _addLink)       _addLink.setEnabled(enabled);
        if (null != _addChildLink)  _addChildLink.setEnabled(false); /// selection should enable this when appropriate
    }
    
    protected void enableDelete(boolean enabled){
        if(enabled && _linkViewer != null){
            ISelection selection = _linkViewer.getSelection();
            if(selection.isEmpty()){
                enabled = false; //can't enable delete if there is nothing selected in the table
            }
        }
        if (null != _removeLink){    
            _removeLink.setEnabled(enabled);
        }
    }
    
    protected void updateViewers() {
        if (null != _linkViewer) {
            _linkViewer.refresh();
        }
        if (null != _complexComposite) {
            _complexComposite.update();
        }
    }
    
    protected void createLinkButtons() {
        CustomComposite buttonParent = new CustomComposite(this, SWT.NONE, "button.parent.id"); // $NON-NLS-1$
        GridLayout gl = SWTLayoutUtils.createLayoutDefaultSpacing(3);
        gl.marginWidth = 0;
        buttonParent.setLayout(gl);
        GridData gd = SWTLayoutUtils.createGDFillHorizontal();
        gd.horizontalSpan = 2;
        gd.horizontalIndent = getControlIndentAmt();
        buttonParent.setLayoutData(gd);

        _addLink = new CustomButton(buttonParent, SWT.PUSH, "add.link.id"); // $NON-NLS-1$
        _addLink.setText("Add Item");  // $NLX-AbstractTreeNodePanel.AddItem-1$
        _addLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Button item = (Button) event.widget;
                Rectangle rect = item.getBounds();
                Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
                _linkMenu.setLocation(pt.x, pt.y + rect.height);

                _linkMenu.setVisible(true);
                while (!_linkMenu.isDisposed() && _linkMenu.isVisible()) {
                    if (!getDisplay().readAndDispatch()){
                        getDisplay().sleep();
                    }
                }
            }
        });

        _addChildLink = new CustomButton(buttonParent, SWT.PUSH, "add.child.id"); // $NON-NLS-1$
        _addChildLink.setText("Add Child");  // $NLX-AbstractTreeNodePanel.AddChild-1$
        _addChildLink.setEnabled(false); // will enable on appropriate link selection
        _addChildLink.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent event) {
                Button item = (Button) event.widget;
                Rectangle rect = item.getBounds();
                Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
                _childLinkMenu.setLocation(pt.x, pt.y + rect.height);

                _childLinkMenu.setVisible(true);
                while (!_childLinkMenu.isDisposed() && _childLinkMenu.isVisible()) {
                    if (!getDisplay().readAndDispatch()){
                        getDisplay().sleep();
                    }
                }
            }
        });
        _removeLink = new CustomButton(buttonParent, SWT.PUSH, "remove.id"); // $NON-NLS-1$
        _removeLink.setText("Remove");  // $NLX-AbstractTreeNodePanel.Remove-1$
        _removeLink.setEnabled(false); // will enable on appropriate link selection
        _removeLink.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                AbstractTreeNodePanel.this.removeSelected();
            }
        });
    }
    
    protected void refreshMoveButtons(Element e){
        if(_moveUp != null && !_moveUp.isDisposed()){
            boolean enabled = canMoveUp(e);
            if(enabled){
                _moveUp.setImage(getEnabledImage(true));
            }
            else{
                _moveUp.setImage(getDisabledImage(true));
            }
            _moveUp.setEnabled(enabled);
        }
        if(_moveDown != null && !_moveDown.isDisposed()){
            boolean enabled = canMoveDown(e);
            if(enabled){
                _moveDown.setImage(getEnabledImage(false));
            }
            else{
                _moveDown.setImage(getDisabledImage(false));
            }
            _moveDown.setEnabled(enabled);
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
            _linkViewer.setSelection(new StructuredSelection(e));
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
            _linkViewer.setSelection(new StructuredSelection(e));
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
        return _linkViewer;
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