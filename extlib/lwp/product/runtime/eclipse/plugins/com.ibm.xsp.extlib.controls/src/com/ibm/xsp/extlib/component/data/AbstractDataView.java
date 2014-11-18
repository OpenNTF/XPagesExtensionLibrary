/*
 * © Copyright IBM Corp. 2010, 2011
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

package com.ibm.xsp.extlib.component.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;
import javax.faces.render.Renderer;

import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.DataPublisher.ShadowedObject;
import com.ibm.xsp.util.StateHolderUtil;
import com.ibm.xsp.util.TypedUtil;

/**
 * Custom data iterator that renders the content of a collection (view).
 * <p>
 * This iterator provides some predefined parts used to render the final markup.
 * </p>
 */
public class AbstractDataView extends UIDataSourceIterator {

    public static final String COMPONENT_TYPE = "com.ibm.xsp.extlib.data.AbstractDataView"; //$NON-NLS-1$

    public static final String AJAX_ID_SUFFIX = "_ajax"; // $NON-NLS-1$

    public static final String FACET_HEADER = "header"; // $NON-NLS-1$

    public static final String FACET_FOOTER = "footer"; // $NON-NLS-1$

    public static final String FACET_PAGERTOP = "pagerTop"; // $NON-NLS-1$

    public static final String FACET_PAGERTOPLEFT = "pagerTopLeft"; // $NON-NLS-1$

    public static final String FACET_PAGERTOPRIGHT = "pagerTopRight"; // $NON-NLS-1$

    public static final String FACET_PAGERBOTTOM = "pagerBottom"; // $NON-NLS-1$

    public static final String FACET_PAGERBOTTOMLEFT = "pagerBottomLeft"; // $NON-NLS-1$

    public static final String FACET_PAGERBOTTOMRIGHT = "pagerBottomRight"; // $NON-NLS-1$

    public static final String FACET_SUMMARY = "summary"; // $NON-NLS-1$

    public static final String FACET_DETAIL = "detail"; // $NON-NLS-1$

    public static final String FACET_NOROWS = "noRows"; // $NON-NLS-1$

    /**
     * The default id for the inner auto-generated RowComponent control, is
     * added as a container for the row contents in the data-processing phases
     * of the lifecycle.
     */
    public static final String ROW_ID = "_row"; // $NON-NLS-1$

    private static final boolean ENABLE_PARTIAL_REFRESH_ROW = true;

    // Flobal options
    private Boolean showItemsFlat;

    // High level columns
    private SummaryColumn summaryColumn;

    // Detail
    private Boolean collapsibleDetail;

    private Boolean detailsOnClient;

    private Boolean disableHideRow;

    // Page to open when the link is clicked
    private String pageName;

    private Boolean openDocAsReadonly;

    // Styling
    private String style;

    private String styleClass;

    private String rowStyle;

    private String rowStyleClass;

    

    // disableGetFacets used to prevent an infinite loop related to MKEE89RPXF
    private transient boolean disableGetFacets;

    private transient PhaseId processFacetsForPhase; // part of the workaround
                                                     // for SPR#MKEE8BFEDR

    private static final String[] CONTAINER_FACET_NAMES = { FACET_HEADER,
            FACET_PAGERTOP, FACET_PAGERTOPLEFT, FACET_PAGERTOPRIGHT,
            FACET_PAGERBOTTOM, FACET_PAGERBOTTOMLEFT, FACET_PAGERBOTTOMRIGHT,
            FACET_FOOTER, FACET_NOROWS, };

    /**
     * Note, the facet names based on {@link #FACET_EXTRA_N} are also row-based
     * facets.
     */
    private static final String[] ROW_FACET_NAMES = { FACET_SUMMARY,
            FACET_DETAIL, };

    // Internal component that renders a row
    // This component is used to render a single row, for a partial refresh
    public static class RowComponent extends UIComponentBase {
        @Override
        public String getFamily() {
            return AbstractDataView.COMPONENT_FAMILY;
        }

        @Override
        public Renderer getRenderer(FacesContext context) {
            return getParent().getRenderer(context);
        }

    }

    public AbstractDataView() {
    }

    public boolean isShowItemsFlat() {
        if (showItemsFlat != null) {
            return showItemsFlat;
        }
        ValueBinding vb = getValueBinding("showItemsFlat"); // $NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return false;
    }

    public void setShowItemsFlat(boolean showItemsFlat) {
        this.showItemsFlat = showItemsFlat;
    }

    public boolean isRowRefresh(FacesContext context) {
        if (ENABLE_PARTIAL_REFRESH_ROW) {
            // This mode is only supported if a partial tree is rendered
            if (((FacesContextEx) context).isAjaxWholeTreeRendered()) {
                return false;
            }
            // There is an issue with IE refreshing a single row
            if (/* ExtLibCompUtil.isXPages852() && */XSPContext
                    .getXSPContext(context).getUserAgent().isIE(0, 8)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public SummaryColumn getSummaryColumn() {
        return this.summaryColumn;
    }

    public void setSummaryColumn(SummaryColumn summaryColumn) {
        this.summaryColumn = summaryColumn;
    }

    public boolean isCollapsibleDetail() {
        if (collapsibleDetail != null) {
            return collapsibleDetail;
        }
        ValueBinding vb = getValueBinding("collapsibleDetail"); // $NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return false;
    }

    public void setCollapsibleDetail(boolean collapsibleDetail) {
        this.collapsibleDetail = collapsibleDetail;
    }

    

    public boolean isDetailsOnClient() {
        if (detailsOnClient != null) {
            return detailsOnClient;
        }
        ValueBinding vb = getValueBinding("detailsOnClient"); // $NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return false;
    }

    public void setDetailsOnClient(boolean detailsOnClient) {
        this.detailsOnClient = detailsOnClient;
    }

    public boolean isDisableHideRow() {
        if (disableHideRow != null) {
            return disableHideRow;
        }
        ValueBinding vb = getValueBinding("disableHideRow"); // $NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return false;
    }

    public void setDisableHideRow(boolean disableHideRow) {
        this.disableHideRow = disableHideRow;
    }

    /**
     * Returns the name of the page that should be opened when a link is clicked
     * on this view.
     * 
     * @return the pageName
     */
    public String getPageName() {
        if (pageName != null) {
            return pageName;
        }
        ValueBinding binding = getValueBinding("pageName"); // $NON-NLS-1$
        if (binding != null) {
            return (String) binding.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * Sets the name of the page to be opened when the link on a row is clicked.
     * 
     * @param name
     *            the pageName to set
     */
    public void setPageName(String name) {
        pageName = name;
    }

    public boolean isOpenDocAsReadonly() {
        if (openDocAsReadonly != null) {
            return openDocAsReadonly;
        }
        ValueBinding vb = getValueBinding("openDocAsReadonly"); // $NON-NLS-1$
        if (vb != null) {
            Boolean b = (Boolean) vb.getValue(getFacesContext());
            if (b != null) {
                return b;
            }
        }
        return false;
    }

    public void setOpenDocAsReadonly(boolean openDocAsReadonly) {
        this.openDocAsReadonly = openDocAsReadonly;
    }

    public String getStyle() {
        if (null != this.style) {
            return this.style;
        }
        ValueBinding vb = getValueBinding("style"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        else {
            return null;
        }
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        if (null != this.styleClass) {
            return this.styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        else {
            return null;
        }
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getRowStyle() {
        if (null != this.rowStyle) {
            return this.rowStyle;
        }
        ValueBinding vb = getValueBinding("rowStyle"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        else {
            return null;
        }
    }

    public void setRowStyle(String rowStyle) {
        this.rowStyle = rowStyle;
    }

    public String getRowStyleClass() {
        if (null != this.rowStyleClass) {
            return this.rowStyleClass;
        }
        ValueBinding vb = getValueBinding("rowStyleClass"); //$NON-NLS-1$
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        else {
            return null;
        }
    }

    public void setRowStyleClass(String rowStyleClass) {
        this.rowStyleClass = rowStyleClass;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        super.restoreState(context, values[0]);
        showItemsFlat = (Boolean) values[1];
        summaryColumn = (SummaryColumn) StateHolderUtil.restoreObjectState(
                context, this, values[2]);
        collapsibleDetail = (Boolean) values[3];
        detailsOnClient = (Boolean) values[4];
        disableHideRow = (Boolean) values[5];
        pageName = (String) values[6];
        openDocAsReadonly = (Boolean) values[7];
        style = (String) values[8];
        styleClass = (String) values[9];
        rowStyle = (String) values[10];
        rowStyleClass = (String) values[11];
        
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] values = new Object[12];
        values[0] = super.saveState(context);
        values[1] = showItemsFlat;
        values[2] = StateHolderUtil.saveObjectState(context, summaryColumn);
        values[3] = collapsibleDetail;
        values[4] = detailsOnClient;
        values[5] = disableHideRow;
        values[6] = pageName;
        values[7] = openDocAsReadonly;
        values[8] = style;
        values[9] = styleClass;
        values[10] = rowStyle;
        values[11] = rowStyleClass;
       
        return values;
    }

    @Override
    public void buildContents(FacesContext context,
            FacesComponentBuilder builder) throws FacesException {
        // TODO update the UIDataView to add header/footer facets and allow
        // prevent outerDiv/table
        // like the UIDataIterator parent.

        // the container facets are not repeated for each row
        for (String facetName : getContainerFacetNames()) {
            if (builder.isFacetAvailable(context, this, facetName)) {
                builder.buildFacet(context, this, facetName);
            }
        }

        RowComponent row = new RowComponent();
        // TODO this ID is not sufficiently unique - should prefix it with the
        // control id or allow it to auto-generate
        // an ID or something. As it is, if you have 2 sibling xe:dataView or
        // xe:forumViews on a page,
        // they will both have the same clientID for the inner row container.
        row.setId(ROW_ID);
        TypedUtil.getChildren(this).add(row);

        // the view children are repeated for each row
        builder.buildChildren(context, row);

        // the row facets are repeated for each row (they are decoded /
        // re-rendered for each row)
        for (String facetName : getRowFacetNames()) {
            if (builder.isFacetAvailable(context, row, facetName)) {
                builder.buildFacet(context, row, facetName);
            }
        }
    }

    /**
     * The names of the facets that should be created in the child row control,
     * and should be repeated for each row. The {@link #FACET_EXTRA_N} facet
     * names are also repeated for each row, though they are not included in
     * this array.
     * 
     * @return
     */
    protected String[] getRowFacetNames() {
        return ROW_FACET_NAMES;
    }

    /**
     * The names of the facets that should be created in the data view container
     * control, and not repeated for each row.
     * 
     * @return
     */
    protected String[] getContainerFacetNames() {
        return CONTAINER_FACET_NAMES;
    }

    // Ajax support...
    @Override
    public String getAjaxContainerClientId(FacesContext context) {
        String id = getClientId(context);
        return id + AJAX_ID_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.component.UIDataIterator#restoreValueHolderState(javax.faces
     * .context.FacesContext, javax.faces.component.UIComponent)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    @Override
    protected void restoreValueHolderState(FacesContext context,
            UIComponent component) {
        super.restoreValueHolderState(context, component);

        // start workaround for MKEE89RPXF
        if (ExtLibUtil.isXPages852()) {
            if (component.getFacetCount() > 0) {
                Map<String, UIComponent> facets;
                if (disableGetFacets && component == this) {
                    facets = super.getFacets();
                }
                else {
                    facets = TypedUtil.getFacets(component);
                }
                for (UIComponent c : facets.values()) {
                    if (c != null) {
                        restoreValueHolderState(context, c);
                    }
                }
            }
        }
        // end workaround for MKEE89RPXF
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.xsp.component.UIDataIterator#setRowIndex(int)
     */
    @Override
    public void setRowIndex(int index) {
        // disableGetFacets used to prevent an infinite loop when there
        // are both facets on the data view control, and some facet on the
        // rowComponent or any descendant control, related to MKEE89RPXF.
        if (ExtLibUtil.isXPages852()) {
            disableGetFacets = true;
        }
        try {
            super.setRowIndex(index);
        }
        finally {
            disableGetFacets = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponentBase#getFacets()
     */
    @SuppressWarnings("rawtypes")//$NON-NLS-1$
    @Override
    public Map getFacets() {
        if (disableGetFacets) {
            return Collections.emptyMap();
        }
        return super.getFacets();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.component.UIDataIterator#saveValueHolderState(javax.faces
     * .context.FacesContext, javax.faces.component.UIComponent)
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    @Override
    protected void saveValueHolderState(FacesContext context,
            UIComponent component) {
        super.saveValueHolderState(context, component);

        // start workaround for MKEE89RPXF
        if (ExtLibUtil.isXPages852()) {
            if (component.getFacetCount() > 0) {
                Map<String, UIComponent> facets;
                if (disableGetFacets && component == this) {
                    facets = super.getFacets();
                }
                else {
                    facets = TypedUtil.getFacets(component);
                }
                for (UIComponent c : facets.values()) {
                    if (c != null) {
                        saveValueHolderState(context, c);
                    }
                }
            }
        }
        // end workaround for MKEE89RPXF
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.extlib.component.data.UIDataSourceIterator#processDecodes
     * (javax.faces.context.FacesContext)
     */
    @Override
    public void processDecodes(FacesContext context) {
        if (ExtLibUtil.isXPages852()) {
            processFacetsForPhase = PhaseId.APPLY_REQUEST_VALUES;
        }
        try {
            super.processDecodes(context);
        }
        finally {
            processFacetsForPhase = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.extlib.component.data.UIDataSourceIterator#processUpdates
     * (javax.faces.context.FacesContext)
     */
    @Override
    public void processUpdates(FacesContext context) {
        if (ExtLibUtil.isXPages852()) {
            processFacetsForPhase = PhaseId.UPDATE_MODEL_VALUES;
        }
        try {
            super.processUpdates(context);
        }
        finally {
            processFacetsForPhase = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.extlib.component.data.UIDataSourceIterator#processValidators
     * (javax.faces.context.FacesContext)
     */
    @Override
    public void processValidators(FacesContext context) {
        if (ExtLibUtil.isXPages852()) {
            processFacetsForPhase = PhaseId.PROCESS_VALIDATIONS;
        }
        try {
            super.processValidators(context);
        }
        finally {
            processFacetsForPhase = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ibm.xsp.component.UIDataIterator#revokeControlData(java.util.List,
     * javax.faces.context.FacesContext)
     */
    @Override
    protected void revokeControlData(List<ShadowedObject> shadowedObjects,
            FacesContext context) {
        if (null != processFacetsForPhase && ExtLibUtil.isXPages852()) {
            try {
                // workaround for SPR#MKEE8BFEDR: Repeat, input values in header
                // are not processed when submitted.
                if (getFacetCount() > 0) {
                    int phaseOrdinal = processFacetsForPhase.getOrdinal();
                    for (UIComponent facet : TypedUtil.getFacets(this).values()) {
                        if (!facet.isRendered()) {
                            continue;
                        }
                        if (phaseOrdinal == PhaseId.APPLY_REQUEST_VALUES
                                .getOrdinal()) {
                            facet.processDecodes(context);
                            continue;
                        }
                        if (phaseOrdinal == PhaseId.UPDATE_MODEL_VALUES
                                .getOrdinal()) {
                            facet.processUpdates(context);
                            continue;
                        }
                        if (phaseOrdinal == PhaseId.PROCESS_VALIDATIONS
                                .getOrdinal()) {
                            facet.processValidators(context);
                            continue;
                        }
                    }
                }
            }
            finally {
                processFacetsForPhase = null;
            }
        }
        super.revokeControlData(shadowedObjects, context);
    }

}