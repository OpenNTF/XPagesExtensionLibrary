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
package com.ibm.xsp.extlib.designer.tooling.panels.complex.configuration;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_BANNER;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_FOOTER;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_PLACE_BAR;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_TITLE_BAR;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.DESIGNER_EXTENSION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_ONEUI_CONFIGURATION;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.swt.SWTUtils;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCPanel;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.panels.complex.DynamicPanel;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.xsp.extlib.designer.common.properties.AppThemeLookup;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties;
import com.ibm.xsp.extlib.designer.common.properties.PropertiesLoader;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.ConfigurationLegal;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

public class ApplicationLayoutOneUIPanel extends DynamicPanel {

  private static final String PLATFORM_DEFAULT = "Platform default";  // $NLX-ApplicationLayoutOneUIPanel.Platformdefault-1$
  private AppThemeLookup lookup;

  public ApplicationLayoutOneUIPanel(Composite parent, int style) {
      super(parent, style);
      
  }

  public ApplicationLayoutOneUIPanel(Composite parent) {
      super(parent);
  }
  
  @Override
  protected void createContents(Composite parent) {
      
      initDataNode(parent);

      parent.setLayout(createChildLayout(1));
    
      Composite topComp = new Composite(parent, SWT.NONE);
      topComp.setLayout(createChildLayout(2));
      Label imageLabel = new Label(topComp, SWT.NONE);      
      Image image = ExtLibToolingPlugin.getImage("AppLayout_DialogPic2.png"); //$NON-NLS-1$
      imageLabel.setImage(image);
      GridData gd = new GridData();
      imageLabel.setLayoutData(gd);

      Text descLabel = new Text(topComp, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
      descLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      
      GridData gdesc = new GridData(GridData.FILL_BOTH);
      gdesc.grabExcessVerticalSpace = true;
      gdesc.grabExcessHorizontalSpace = true;
      gdesc.widthHint = 210;
      descLabel.setLayoutData(gdesc);
      String desc = ""; //$NON-NLS-1$

      FacesRegistry registry = getExtraData().getDesignerProject().getFacesRegistry();
      FacesDefinition def = registry.findDef(EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_ONEUI_CONFIGURATION);
      if (null != def) {
          DesignerExtension ext = (DesignerExtension)def.getExtension(DESIGNER_EXTENSION);
          if (ext != null) {
              desc = ext.getDescription();
          }
      }
      descLabel.setText(desc);
      descLabel.update();
      
      // null for checked value because runtime default if "on" (true)
      ExtLibRegistryUtil.Default defBanner = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_BANNER, String.valueOf(true));
      createDCCheckBox(EXT_LIB_ATTR_BANNER, defBanner.trueValue(), defBanner.falseValue(), "&Banner -- Area at the top of the page  for branding and global links", null); // $NLX-ApplicationLayoutOneUIPanel.BannerAreaatthetopofthepageforbra-1$
      
      ExtLibRegistryUtil.Default defTitleBar = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_TITLE_BAR, String.valueOf(true));
      createDCCheckBox(EXT_LIB_ATTR_TITLE_BAR,  defTitleBar.trueValue(), defTitleBar.falseValue(), "&Title bar -- Area for application title and search controls", null); // $NLX-ApplicationLayoutOneUIPanel.TitlebarAreaforapplicationtitlea-1$

      ExtLibRegistryUtil.Default defPlaceBar = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_PLACE_BAR, String.valueOf(true));
      createDCCheckBox(EXT_LIB_ATTR_PLACE_BAR,  defPlaceBar.trueValue(), defPlaceBar.falseValue(), "&Place bar -- Area for secondary title and action buttons", null); // $NLX-ApplicationLayoutOneUIPanel.PlacebarAreaforsecondarytitleanda-1$

      ExtLibRegistryUtil.Default defFooter = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_FOOTER, String.valueOf(true));
      createDCCheckBox(EXT_LIB_ATTR_FOOTER,  defFooter.trueValue(), defFooter.falseValue(), "&Footer -- Area under the content area for links and text", null); // $NLX-ApplicationLayoutOneUIPanel.FooterAreaunderthecontentareaforl-1$
      
      ExtLibRegistryUtil.Default defLegal = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_FOOTER, String.valueOf(true));      
      createDCCheckBox(ConfigurationLegal.ATTR_LEGAL, defLegal.trueValue(), defLegal.falseValue(), "&Legal -- Area at the bottom of the page for the legal information", null); // $NLX-ApplicationLayoutOneUIPanel.LegalAreaatthebottomofthepagefort-1$
      
      createThemeCombo();
  }
  
  
  private void createThemeCombo() {
      
      // the ComplexPanelComposite is the Parent of the ApplicationLayoutOneUIPanel (DynamicPanel)
      Object data = getParent().getData("pprops"); //$NON-NLS-1$
      if (null == data){
          return;
      }
      Group group = new Group(getCurrentParent(), SWT.NONE);
      group.setText("&Application theme"); // $NLX-ApplicationLayoutOneUIPanel.ApplicationThemeGroup-1$
      group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      GridLayout gl = createChildLayout(1);
      gl.marginLeft = gl.marginRight = getControlIndentAmt();
      gl.marginTop = gl.marginBottom = 4;
      
      group.setLayout(gl);
      
      Composite current = getCurrentParent();
      setCurrentParent(group);
      
      Label themedesc = new Label(getCurrentParent(), SWT.WRAP);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.widthHint = 200;
      themedesc.setLayoutData(gd);
      
      String msg = "The application layout control works best in conjunction with an application theme. You can set a theme for the application here now, or do it later in Application Properties."; // $NLX-ApplicationLayoutOneUIPanel.Applicationthemedescriptionopen-1$
          
      themedesc.setText(msg); 
      
      PreservingProperties pp = (PreservingProperties)data;
      
      DCPanel propsPanel = new DCPanel(getCurrentParent(), SWT.NONE);
      initPropertiesNode(propsPanel, pp);
      setCurrentParent(propsPanel);
      GridData gdDC = createControlGDFill(1);
      gdDC.horizontalIndent = 0;
      propsPanel.setLayoutData(gdDC);
      GridLayout layout = createChildLayout(2);
      layout.marginLeft = 0;
      propsPanel.setLayout(layout);
      
      Label label = new Label(getCurrentParent(), SWT.NONE); 
      label.setText("&Application theme:");// $NLX-ApplicationLayoutOneUIPanel.ApplicationThemeCombo-1$
      label.setLayoutData(new GridData());
      
      lookup = new AppThemeLookup(getExtraData().getDesignerProject(), "", PLATFORM_DEFAULT, AppThemeLookup.theme_Standard_Ids, AppThemeLookup.theme_Standard_Labels); 
      this.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent arg0) {
              lookup.dispose();  // free up the theme listeners
          }
      });
      
      GridData gdCombo = createControlGDFill(1);
      gdCombo.grabExcessHorizontalSpace = true;
      DCComboBox c = createCombo("xsp.theme", lookup, gdCombo, false /*blank line*/); //$NON-NLS-1$
      SWTUtils.setControlId(c, "applayout.dialog.theme"); //$NON-NLS-1$
      
      // if it's not set, default to the last standard theme, currently OneUI V3.0.2
      Object currentvalue = getParent().getData("ppropstheme"); //$NON-NLS-1$
      if (null == currentvalue || (currentvalue instanceof String && StringUtil.isEmpty((String)currentvalue))) {
          if (c.getItemCount() > 0) {
              // Get the last standard theme
              String target = AppThemeLookup.theme_Standard_Ids[AppThemeLookup.theme_Standard_Ids.length - 1];
              for (int i=0; i < lookup.size(); i++) {
                  if (StringUtil.equals(target, lookup.getCode(i))) {
                      c.select(i);
                      break;
                  }
              }
          }
      }
      
      setCurrentParent(current);
  }
  
  
  private DataNode initPropertiesNode(DCPanel panel, PreservingProperties pp) {
      ILoader loader = new PropertiesLoader(null);
      
      DataNode dn = panel.getDataNode();
      
      try {
          IClassDef classDef = loader.getClassOf(pp);
          dn.setClassDef(classDef);
          dn.setDataProvider(new SingleCollection(pp));
      }
      catch(NodeException e) {
          ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "initPropertiesNode", e, "NodeException trying to initialize the  properties node");  // $NON-NLS-1$ $NLE-ApplicationLayoutOneUIPanel.NodeExceptiontryingtoinitializeth-2$
      }
      
      return dn;
  }
  
  
  private void initDataNode(Composite parent) {
      // parent dn is the app layout 
      DataNode dnAppLayout = DCUtils.findDataNode(parent, false);
      
      // thisis the config value
      DCUtils.initDataBinding(this);
      
      if (dnAppLayout != null) {
          ILoader loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(getExtraData().getDesignerProject());
          
          IClassDef def = ExtLibRegistryUtil.getClassDef(loader, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_ONEUI_CONFIGURATION);
          
          if (def != null) {
              try {
                  Object  object = dnAppLayout.getCurrentObject();
                  IMember configMember = dnAppLayout.getMember(EXT_LIB_ATTR_CONFIGURATION);
                  
                  if (null != object && null != configMember) {
                      Object child = dnAppLayout.getObject(object, (IAttribute) configMember);
                      if (child == null) {
                          child = create(def, loader, (IAttribute)configMember, (Node)object);
                      }
                      
                      if (null != child) {
                          DataNode dnConfig = DCUtils.findDataNode(this, false);
                          if (null != dnConfig) {
                              dnConfig.setDataProvider(new SingleCollection(child));
                              dnConfig.setClassDef(def);
                          }
                      }
                  }
                  else {
                      ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "initDataNode", "unable to set classDef for configuration");  // $NON-NLS-1$ $NLE-ApplicationLayoutOneUIPanel.unabletosetclassDefforconfigurati-2$
                  }
              } catch (NodeException e) {
                  ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "initDataNode", e, "NodeException generated when trying to access the \'configuration\' attribute");  // $NON-NLS-1$ $NLE-ApplicationLayoutOneUIPanel.NodeExceptiongeneratedwhentryingt-2$
              }
          }
      }
  }
          
  static private Object create(IClassDef def, ILoader loader, IAttribute attr, Node node) {
      Object o = null;
      try {
          o = def.newInstance(node);
          loader.setObject(node, attr, o, null);
      }
      catch(NodeException e) {
          ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
      }
      return o;
  }
    
}