package com.ibm.xsp.extlib.renderkit.html_extended.mobile;

import com.ibm.xsp.extlib.tree.ITreeRenderer;

public class MobileNavigatorAccordionRenderer extends MobileNavigatorRenderer implements ITreeRenderer {

    private static final long serialVersionUID = 1L;

    public MobileNavigatorAccordionRenderer(){
        super();
    }

    @Override
    protected String getDojoType(){
        return "extlib.dijit.outline.accordion"; // $NON-NLS-1$
    }   
}