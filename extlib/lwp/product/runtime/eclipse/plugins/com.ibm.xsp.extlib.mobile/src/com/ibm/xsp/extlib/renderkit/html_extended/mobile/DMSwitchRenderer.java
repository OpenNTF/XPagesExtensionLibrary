package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.xsp.dojo.FacesDojoComponent;
import com.ibm.xsp.extlib.component.dojo.UIDojoWidgetBase;
import com.ibm.xsp.extlib.component.mobile.UIDMSwitch;
import com.ibm.xsp.extlib.renderkit.dojo.DojoRendererUtil;
import com.ibm.xsp.extlib.renderkit.dojo.DojoWidgetRenderer;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.resource.DojoModuleResource;

public class DMSwitchRenderer extends DojoWidgetRenderer {

    @Override
    protected String getTagName() {
        return "div"; // $NON-NLS-1$
    }

    @Override
    protected String getDefaultDojoType(FacesContext context,
            FacesDojoComponent component) {
        return "dojox.mobile.Switch"; // $NON-NLS-1$
    }

    @Override
    protected DojoModuleResource getDefaultDojoModule(FacesContext context,
            FacesDojoComponent component) {
        return ExtLibResources.dojoxMobile;
    }

     @Override
    protected void initDojoAttributes(FacesContext context, FacesDojoComponent dojoComponent, Map<String,String> attrs) throws IOException {
        super.initDojoAttributes(context, dojoComponent, attrs);
        if(dojoComponent instanceof UIDMSwitch) {
            UIDMSwitch c = (UIDMSwitch)dojoComponent;           
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"leftLabel",c.getLeftLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"rightLabel",c.getRightLabel()); // $NON-NLS-1$
            DojoRendererUtil.addDojoHtmlAttributes(attrs,"value",c.getValue()); // $NON-NLS-1$
            
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchStart", c.getOnTouchStart());
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchEnd", c.getOnTouchEnd());
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onTouchMove", c.getOnTouchMove());
            encodeDojoEvents(context, (UIDojoWidgetBase)c, "onStateChanged", c.getOnStateChanged());
        }
    }
}