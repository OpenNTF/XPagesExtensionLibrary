/*
 * © Copyright IBM Corp. 2010, 2013
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

package com.ibm.xsp.extlib.component.dialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.ContextCallback;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.component.UIEventHandler;
import com.ibm.xsp.component.UIFormEx;
import com.ibm.xsp.component.xp.XspForm;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoContentPane;
import com.ibm.xsp.extlib.component.dynamiccontent.UIDynamicControl;
import com.ibm.xsp.extlib.component.util.DynamicUIUtil;
import com.ibm.xsp.extlib.stylekit.StyleKitExtLibDefault;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.stylekit.ThemeControl;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.JavaScriptUtil;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;


/**
 * Dialog container.
 * <p>
 * Defines a modal dialog using the dijit.Dialog class. The dialog can be displayed from either
 * a piece of client side or server side script. 
 * </p>
 * 
 * This is a complex component as it is not generated as part of the page when rendered.
 * This is because Dojo moves a dialog from where it had been generated, directly to the
 * body tag for display reasons. From an XPages standpoint, this has several consequences:
 *   - The dialog is moved out of the main form, which means that the dialog should use
 *     its own form. It is created dynamically as a child of the popup content.
 *   - When doing partial refresh of a piece of markup containing a dialog, the dialog widget
 *     as to be destroyed. This is done by a wrapper component that stays where the dialog is
 *     generated, and find/destroy the dialog when it is itself destroyed. He will then be
 *     recreated.
 *   - The content of the dialog is created dynamically the first time the dialog is displayed.
 *     This is done by a partial refresh request on the dialog itself.
 *   - The initial markup dialog generates the destroy wrapper and a simple tag without the dojoType
 *     assigned. When the dialog is to be displayed, the tag is moved to the body and the dialog is
 *     partial refreshed.
 *   - The dialog can have events assigned to it (onHide, OnShow...). As these events are generally
 *     handled though child components, the handlers are only created when the content of the dialog
 *     is created. See the code which ensures that the events are assigned back to the dialog and
 *     not to the form, as the other chidren.
 * 
 */
public class UIDialog extends UIDojoContentPane implements NamingContainer, FacesComponent, FacesDojoComponent, ThemeControl {

    public static boolean DIALOG_NEXT = false;
    
    public static final String DIALOGACTION_PARAM = "$$action"; // $NON-NLS-1$
    
    
    public static final int ACTION_NONE         = 0;
    public static final int ACTION_SHOW_DIALOG  = 1;
    public static final int ACTION_HIDE_DIALOG  = 2;

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.dialog.Dialog"; // $NON-NLS-1$
    public static final String COMPONENT_FAMILY = "com.ibm.xsp.extlib.Dialog"; //$NON-NLS-1$
    public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dialog.Dialog"; //$NON-NLS-1$

    // Dialog attributes
    private Boolean keepComponents;

    // Server side events events
    private MethodBinding beforeContentLoad;
    private MethodBinding afterContentLoad;

    public static class Action {
    	private UIDialog dialog;
        private int action;
        private String refreshId;
        private Map<String,String> refreshParams;
        Action(FacesContext context, UIDialog dialog, int action, Map<String,String> refreshParams) {
        	this.dialog = dialog;
            this.action = action;
            this.refreshParams = refreshParams;
        }
        Action(FacesContext context, UIDialog dialog, int action, String refreshId, Map<String,String> refreshParams) {
        	this.dialog = dialog;
            this.action = action;
            this.refreshId = refreshId;
            this.refreshParams = refreshParams;
        }
        public String generateClientScript() {
        	FacesContext context = FacesContext.getCurrentInstance();
            switch (action) {
            	case UIDialog.ACTION_SHOW_DIALOG: {
            		String method = "openDialog"; // $NON-NLS-1$
            		if(dialog instanceof UITooltipDialog) {
            			method = "openTooltipDialog"; // $NON-NLS-1$
            		}
            		// Generate the piece of code that shows the dialog
            		StringBuilder b = new StringBuilder();
            		// setTimeOut(xxx,0) will ensure it will be executed the partial refresh completed
            		b.append("setTimeout(function(){XSP."); // $NON-NLS-1$
            		b.append(method);
            		b.append("(");
            		JavaScriptUtil.addString(b, dialog.getClientId(context));
            		b.append(",null"); // $NON-NLS-1$
            		Object params = refreshParams;
            		if(params!=null) {
            			try {
            				String json = JsonGenerator.toJson(JsonJavaFactory.instance,params,true);
            				b.append(",");
            				b.append(json);
            			} catch(Exception ex) {
            				throw new FacesExceptionEx(ex);
            			}
            		}
            		b.append("),0});");
            		String script = b.toString();
            		return script;
            	}
            	case UIDialog.ACTION_HIDE_DIALOG: {
            		String method = "closeDialog"; // $NON-NLS-1$
            		if(dialog instanceof UITooltipDialog) {
            			method = "closeTooltipDialog"; // $NON-NLS-1$
            		}
            		// Generate the piece of code that closes the dialog
            		StringBuilder b = new StringBuilder();
            		//b.append("setTimeout(function(){XSP.");
            		b.append("XSP."); // $NON-NLS-1$
            		b.append(method);
            		b.append("(");
            		JavaScriptUtil.addString(b, dialog.getClientId(context));
            		String rid=ExtLibUtil.getClientId(context,dialog,refreshId,true);
            		if(StringUtil.isNotEmpty(rid)) {
            			b.append(",");
            			JavaScriptUtil.addString(b, rid);
            			Object params = refreshParams;
            			if(params!=null) {
            				try {
            					String json = JsonGenerator.toJson(JsonJavaFactory.instance,params,true);
            					b.append(",");
            					b.append(json);
            				} catch(Exception ex) {
            					throw new FacesExceptionEx(ex);
            				}
            			}
            		}
            		b.append(");");
                
            		String script = b.toString();
            		return script;
            	}
            }
            return null;
        }
    }
    
    public static class PopupContent extends UIDynamicControl {

        public static final String RENDERER_TYPE = "com.ibm.xsp.extlib.dialog.DialogPopup"; //$NON-NLS-1$
        
        public PopupContent() {
            setRendererType(RENDERER_TYPE);
        }

        @Override
        public String getFamily() {
            return UIDialog.COMPONENT_FAMILY;
        }

        @Override
        protected boolean isValidInContext(FacesContext context) {
            FacesContextEx ctx = (FacesContextEx)context;
            return getDialog().isDialogRequest(ctx);
        }

        @Override
        public boolean getRendersChildren() {
            return false;
        }

        public UIDialog getDialog() {
            return (UIDialog)getParent();
        }

        @Override
        public void encodeBegin(FacesContext context) throws IOException {
            if(!DIALOG_NEXT) {
                // If the original dialog mode, the content is created by the popup panel
                //if(getDialog().pendingAction==null) {
                    FacesContextEx ctx = (FacesContextEx)context;
                    if(getDialog().isDialogCreateRequest(ctx)) {
                        // If the content is already created, then we first delete it
                        // and then recreate it
                        createContent(ctx);
                    } else {
                        // Else, we delete the content if the request is out of the dialog
                        if(isContentCreated() && !getDialog().isKeepComponents()) {
                            if(!getDialog().isDialogRequest(ctx)) {
                                deleteContent(ctx);
                            }
                        }
                    }
                //}
            }
            super.encodeBegin(context);
        }

        @Override
        public void createChildren(FacesContextEx context) {
            PopupForm form = new PopupForm();
            form.setAutoForm(true);
            form.setId("form1"); // TODO allow user-specified formId $NON-NLS-1$
            TypedUtil.getChildren(this).add(form);
            super.createChildren(context);
        }
        
        @Override
        protected UIFormEx getRootComponent() {
            if(getChildCount()>0) {
                return (UIFormEx)getChildren().get(0); //must be UIFormEx
            }
            return null;
        }

        @Override
        protected String getCreateId() {
            return getDialog().getId();
        }

        @Override
        protected MethodBinding getBeforeContentLoad() {
            return getDialog().getBeforeContentLoad();
        }

        @Override
        protected MethodBinding getAfterContentLoad() {
            return getDialog().getAfterContentLoad();
        }
        
        @Override
        protected void deleteContent(FacesContextEx context) {
            if(!getDialog().isKeepComponents()) {
                // Remove the EventHandlers add to the dialog
                List<UIComponent> l = TypedUtil.getChildren(getDialog());
                for(int i=0; i<l.size(); ) {
                    UIComponent c = l.get(i);
                    if(c instanceof UIEventHandler) {
                        l.remove(i);
                    } else {
                        i++;
                    }
                }
                // Then delete the content of the panel
                super.deleteContent(context);
            }
        }
    }
    
    // The embedded form ensures that the event handlers are assigned 
    public static class PopupForm extends XspForm implements DynamicUIUtil.IDynamicContainer {
        public PopupForm() {
            // We completely prevent the form submission as everything should
            // be done using partial refresh. This also prevent the default form
            // submission when the user presses 'enter' 
            setOnsubmit("return false;"); // $NON-NLS-1$
        }
        public void addDynamicChild(UIComponent c) {
            if(c instanceof UIEventHandler) {
                UIDialog dlg = (UIDialog)getParent().getParent();
                TypedUtil.getChildren(dlg).add(c);
            } else {
                TypedUtil.getChildren(this).add(c);
            }
        }
    }
    
    /**
     * 
     */
    public UIDialog() {
        setRendererType(RENDERER_TYPE);
    }

    @Override
    public String getStyleKitFamily() {
        return StyleKitExtLibDefault.DIALOG_MODAL;
    }

    public boolean isVisible() {
        PopupContent popup = getPopupContent();
        if(popup!=null && popup.isContentCreated()) {
            return popup.isValidInContext(FacesContext.getCurrentInstance());
        }
        return false;
    }
    
    public void show() {
        show(null);
    }
    
    public void show(Map<String,String> parameters) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        Map<String,String> p = new HashMap<String, String>();
        if(parameters!=null) {
            p.putAll(parameters);
        }
        p.put("$$createdialog", "false"); // $NON-NLS-1$ $NON-NLS-2$
        Action pendingAction = new Action(context,this,ACTION_SHOW_DIALOG,p);
        ExtLibUtil.postScript(context, pendingAction.generateClientScript());
        // Force the content to be recreated here so the JS code can access the dialog
        // components right after it is closed
        // Note that we should then prevent the dialog from being recreated during
        // the rendering phase of the popup panel, else the values will be lost
        PopupContent popup = getPopupContent();
        popup.createContent(context);
    }
    
    public void hide() {
        hide(null,null);
    }
    public void hide(String refreshId) {
        hide(refreshId,null);
    }
    public void hide(String refreshId, Map<String,String> refreshParams) {
        FacesContextEx context = FacesContextEx.getCurrentInstance();
        PopupContent popup = getPopupContent();
        if(popup.isContentCreated()) {
            // Remove the content
            popup.deleteContent(context);
            Action pendingAction = new Action(context,this,ACTION_HIDE_DIALOG,refreshId,refreshParams);
            ExtLibUtil.postScript(context, pendingAction.generateClientScript());
        }
    }
    
    public PopupContent getPopupContent() {
        if(getChildCount()>0) {
            return (PopupContent)getChildren().get(0);
        }
        return null;
    }
    
    public UIFormEx getRootComponent() {
        PopupContent ct = getPopupContent();
        if(ct!=null) {
            return ct.getRootComponent();
        }
        return null;
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public boolean isKeepComponents() {
        if (null != this.keepComponents) {
            return this.keepComponents;
        }
        ValueBinding _vb = getValueBinding("keepComponents"); //$NON-NLS-1$
        if (_vb != null) {
            Boolean val = (java.lang.Boolean) _vb.getValue(FacesContext.getCurrentInstance());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setKeepComponents(boolean keepComponents) {
        this.keepComponents = keepComponents;
    }
    
    public MethodBinding getBeforeContentLoad() {
        return beforeContentLoad;
    }
    public void setBeforeContentLoad(MethodBinding beforeContentLoad) {
        this.beforeContentLoad = beforeContentLoad;
    }
    
    public MethodBinding getAfterContentLoad() {
        return afterContentLoad;
    }
    public void setAfterContentLoad(MethodBinding afterContentLoad) {
        this.afterContentLoad = afterContentLoad;
    }
    
    public boolean isDialogRequest(FacesContextEx context) {
        // The current panel is the current one if the request is
        // a partial refresh, where the panel is the target component
        if(context.isAjaxPartialRefresh()) {
            String id = context.getPartialRefreshId();
            if(DIALOG_NEXT) {
                if(FacesUtil.isClientIdChildOf(context, this, id)) {
                    return true;
                }
            } else {
                if(FacesUtil.isClientIdChildOf(context, getPopupContent(), id)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    public boolean isDialogCreateRequest(FacesContextEx context) {
        // The current panel is the current one if the request is
        // a partial refresh, where the panel is the target component
        // The request must also include a $$showdialog=true param in the URL
        Map<String, String> params = TypedUtil.getRequestParameterMap(context.getExternalContext());
        if(StringUtil.equals(params.get("$$showdialog"),"true")) { // $NON-NLS-1$ $NON-NLS-2$
            // If the creation had been prohibited, then do not create it
            if(StringUtil.equals(params.get("$$createdialog"),"false")) { // $NON-NLS-1$ $NON-NLS-2$
                return false;
            }
            if(context.isAjaxPartialRefresh()) {
                String id = context.getPartialRefreshId();
                if(DIALOG_NEXT) {
                    if(StringUtil.equals(getPopupContent(), id)) {
                        return true;
                    }
                } else {
                    if(StringUtil.equals(getPopupContent().getClientId(context), id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if(DIALOG_NEXT) {
            // In the dialog next implementation, the content is created by the dialog itself
            //if(pendingAction==null) {
                FacesContextEx ctx = (FacesContextEx)context;
                if(isDialogCreateRequest(ctx)) {
                    getPopupContent().createContent(ctx);
                } else {
                    // Else, we delete the content if the request is out of the dialog
                    if(getPopupContent().isContentCreated() && !isKeepComponents()) {
                        if(!isDialogRequest(ctx)) {
                            getPopupContent().deleteContent(ctx);
                        }
                    }
                }
            //}
        }
        super.encodeBegin(context);
    }

    
    //
    // The dialog never renders its children in 852 mode.
    // If we are here, it is because the page is refreshing and the dialog is
    // not the current context.
    //
    @Override
    public boolean getRendersChildren() {
        if(!DIALOG_NEXT) {
            // In the original dialog, it does not render it children
            if(isDialogRequest(FacesContextEx.getCurrentInstance())) {
                return false;
            }
            return true;
        }
        return super.getRendersChildren();
    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {
        if(DIALOG_NEXT) {
            super.encodeChildren(context);
        } else {
            // If we are here,it is because we should not render the children...
        }
    }
    
    //
    // Implementation of com.ibm.xsp.component.FacesComponent
    //
    public void initBeforeContents(FacesContext context) throws FacesException {
    }
    public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
        buildContents(context, builder, this);
    }
    protected void buildContents(FacesContext context, FacesComponentBuilder builder, UIComponent parent) throws FacesException {
        if(DynamicUIUtil.isDynamicallyConstructing(context)) {
            // We should reset the dynamically constructing flag as we don't want it to be
            // propagated to its children (ex: nested dialogs...)
            UIComponent c = DynamicUIUtil.getDynamicallyConstructedComponent(context); 
            DynamicUIUtil.setDynamicallyConstructing(context, null);
            try {
                builder.buildAll(context, this, false);
                return;
            } finally {
                DynamicUIUtil.setDynamicallyConstructing(context, c);
            }
        } else {
            PopupContent content = new PopupContent();
            content.setId("_content"); // $NON-NLS-1$
            TypedUtil.getChildren(parent).add(content);
            // Set the source page name for creating the inner content
            content.setSourcePageName(DynamicUIUtil.getSourcePageName(builder));
        }
    }
    
    public void initAfterContents(FacesContext context) throws FacesException {
    }
    
    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        return super.invokeOnComponent(context, clientId, callback);
    }
   
    //
    // State handling
    //

    @Override
    public void restoreState(FacesContext _context, Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.keepComponents = (Boolean) _values[1];
        this.beforeContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[2]);
        this.afterContentLoad = StateHolderUtil.restoreMethodBinding(_context, this, _values[3]);
    }

    @Override
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[4];
        _values[0] = super.saveState(_context);
        _values[1] = keepComponents;
        _values[2] = StateHolderUtil.saveMethodBinding(_context, beforeContentLoad);
        _values[3] = StateHolderUtil.saveMethodBinding(_context, afterContentLoad);
        return _values;
    }
}