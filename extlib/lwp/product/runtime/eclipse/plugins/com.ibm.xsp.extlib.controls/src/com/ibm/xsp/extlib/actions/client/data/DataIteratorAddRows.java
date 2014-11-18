/*
 * © Copyright IBM Corp. 2010, 2012
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
/*
 * DataIteratorAddRows.java was added 2011-Sep-21.
 */
package com.ibm.xsp.extlib.actions.client.data;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.actions.client.AbstractClientSimpleAction;
import com.ibm.xsp.ajax.AjaxUtil;
import com.ibm.xsp.component.FacesDataIterator;
import com.ibm.xsp.component.UIDataEx;
import com.ibm.xsp.context.FacesContextEx;
import com.ibm.xsp.extlib.component.data.FacesDataIteratorAjax;
import com.ibm.xsp.extlib.resources.ExtLibResources;
import com.ibm.xsp.util.FacesUtil;

public class DataIteratorAddRows extends AbstractClientSimpleAction {
// Note, to keep with the naming convention this class should 
// be named DataIteratorAddRowsAction (i.e. with the suffix ..Action)
// But it has already been shipped in 8.5.3UpgradePack1
// with the current name, so we cannot change it as it would break
// existing applications.

    public static final String DISABLED_FORMAT_HIDE = "hide"; //$NON-NLS-1$
    public static final String DISABLED_FORMAT_LINK = "link"; //$NON-NLS-1$
    public static final String DISABLED_FORMAT_TEXT = "text"; //$NON-NLS-1$
    public static final String DISABLED_FORMAT_AUTO = "auto"; //$NON-NLS-1$
    
    private String _for;
    private Integer _rowCount; 
    private String _disableId;
    private Boolean _state;
    private String _disabledFormat;
    
    
    public DataIteratorAddRows() {
        super();
    }
    
    public String getFor() {
        if (_for == null) {
            ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(getFacesContext());
            }
        }
        return _for;
    }

    public void setFor(String _for) {
        this._for = _for;
    }

    public int getRowCount() {
        if (null != this._rowCount) {
            return this._rowCount;
        }
        ValueBinding vb = getValueBinding("rowCount"); //$NON-NLS-1$
        if (vb != null) {
            Integer val = (Integer) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return 0;
    }

    public void setRowCount(int rows) {
        this._rowCount = rows;
    }
    
    public String getDisableId() {
        if (_disableId == null) {
            ValueBinding vb = getValueBinding("disableId"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(getFacesContext());
            }
        }
        return _disableId;
    }

    public void setDisableId(String disableId) {
        this._disableId = disableId;
    }

    /**
     * @return the disabledFormat
     */
    public String getDisabledFormat() {
        if(_disabledFormat!=null) {
            return _disabledFormat;
        }
        ValueBinding vb = getValueBinding("disabledFormat"); //$NON-NLS-1$
        if(vb!=null) {
            return (String)vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * @param disabledFormat the disabledFormat to set
     */
    public void setDisabledFormat(String disabledFormat) {
        this._disabledFormat = disabledFormat;
    }

    public boolean isState() {
        if (null != this._state) {
            return this._state;
        }
        ValueBinding vb = getValueBinding("state"); //$NON-NLS-1$
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(getFacesContext());
            if(val!=null) {
                return val;
            }
        } 
        return false;
    }

    public void setState(boolean state) {
        this._state = state;
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[6];
        state[0] = super.saveState(context);
        state[1] = _for;
        state[2] = _rowCount;
        state[3] = _disableId;
        state[4] = _disabledFormat;
        state[5] = _state;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _for = (String)values[1];
        _rowCount = (Integer)values[2];
        _disableId = (String)values[3];
        _disabledFormat = (String) values[4];
        _state = (Boolean)values[5];
    }

    @Override
    public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {
        FacesDataIterator dt = findDataIterator();
        String linkId = null;
        String id = getDisableId();
        if(StringUtil.isNotEmpty(id)) {
            UIComponent l = FacesUtil.getComponentFor(getComponent(), id);
            if(l!=null) {
                linkId = l.getClientId(context);
            }
        }
        String disabledFormat = getDisabledFormat();
        disabledFormat = computeDisabledFormat(context, disabledFormat, /*rendererDefaultFormat*/null);
        return generateJavaScript(context, dt, getRowCount(), isState(), linkId, disabledFormat);
    }

    protected FacesDataIterator findDataIterator() {
        String id = getFor();
        if(StringUtil.isNotEmpty(id)) {
            UIComponent c = FacesUtil.getComponentFor(getComponent(), id);
            if(c==null) {
                throw new FacesExceptionEx(null,"Component {0} does not exist",id); // $NLX-DataIteratorAddRows.Component0doesnotexist-1$
            }
            if(!(c instanceof FacesDataIterator)) {
                throw new FacesExceptionEx(null,"Component {0} is not a data iterator",id); // $NLX-DataIteratorAddRows.Component0isnotadataiterator-1$
            }
            return (FacesDataIterator)c;
        }
        for(UIComponent c=getComponent(); c!=null; c=c.getParent()) {
            if(c instanceof FacesDataIterator) {
                return (FacesDataIterator)c;
            }
        }
        throw new FacesExceptionEx(null,"The simple action cannot find a data iterator"); // $NLX-DataIteratorAddRows.Thesimpleactioncannotfindadataite-1$
    }
    /**
     * @param context
     * @param dt
     * @param rowCount
     * @param state
     * @param linkId
     * @return
     * @throws EvaluationException
     * @throws MethodNotFoundException
     * @deprecated use the other generate.. method instead, 
     * first calling {@link #computeDisabledFormat(FacesContext, String, String)} 
     * to compute the last parameter for 
     * {@link #generateJavaScript(FacesContext, FacesDataIterator, int, boolean, String, String)}.
     */
    public static String generateJavaScript(FacesContext context, FacesDataIterator dt, int rowCount, boolean state, String linkId) throws EvaluationException, MethodNotFoundException {
        String computedDisabledFormat = computeDisabledFormat(context, null, null);
        return generateJavaScript(context, dt, rowCount, state, linkId, computedDisabledFormat);
    }

    /**
     * 
     * @param context
     * @param dt
     * @param rowCount
     * @param state
     * @param linkId
     * @param computedDisabledFormat the result of a call to {@link #computeDisabledFormat(FacesContext, String, String)};
     * @return
     * @throws EvaluationException
     * @throws MethodNotFoundException
     */
    public static String generateJavaScript(FacesContext context, FacesDataIterator dt, int rowCount, boolean state, String linkId,
            String computedDisabledFormat) throws EvaluationException, MethodNotFoundException {
        StringBuilder b = new StringBuilder(256);
        
        UIComponent c = (UIComponent)dt;
        
        // Add the dojo module
        ExtLibResources.addEncodeResource(context, ExtLibResources.extlibDataIterator);
        
        // And generate the piece of script
        String id = (c instanceof FacesDataIteratorAjax) ? ((FacesDataIteratorAjax)c).getAjaxContainerClientId(context) : c.getClientId(context);
        String url = AjaxUtil.getAjaxUrl(context, c, UIDataEx.AJAX_GETROWS, c.getClientId(context));
        url = context.getExternalContext().encodeActionURL(url);

        int first = dt.getFirst()+dt.getRows();
        int count = rowCount;
        if(count<=0) {
            // For SPR#MKEE8MHELJ, default to 30 rows
            // instead of to dt.getRows(), to prevent duplicating
            // the number of rows displayed on every click.
            count = UIDataEx.DEFAULT_ROWS_PER_PAGE;
        }
        
        // partial workaround for SPR#LHEY8LNDZS, problem in the xpage runtime.
        // The UIDataEx and UIDataIterator classes can't handle it when 
        // the number of rows to be added is < the rows property - they 
        // return too few rows. The client-side XSP.appendRows method 
        // thinks that, since too few rows are present, all the rows 
        // in the data set have been displayed, so it removes
        // the "Show more" link, even though not all rows have been shown.
        count = Math.min(count, dt.getRows());
        
        try {
            b.append("XSP.appendRows("); //$NON-NLS-1$
            JsonJavaObject jo = new JsonJavaObject();
            jo.putString("id", id); //$NON-NLS-1$
            jo.putString("url", url); //$NON-NLS-1$
            jo.putInt("first", first); //$NON-NLS-1$
            jo.putInt("count", count); //$NON-NLS-1$
            jo.putBoolean("state", state); //$NON-NLS-1$
            if( null != linkId ){
                jo.putString("linkId", linkId); //$NON-NLS-1$
                
                if( !DISABLED_FORMAT_TEXT.equals(computedDisabledFormat) ){
                    jo.putString("linkDisabledFormat", computedDisabledFormat); //$NON-NLS-1$
                }
            }
            JsonGenerator.toJson(JsonJavaFactory.instance,b,jo,true);
            b.append(");"); //$NON-NLS-1$
        } catch(Exception e) {
            throw new FacesExceptionEx(e);
        }
        
        return b.toString();
    }
    /**
     * Returns an exact string that is one of {@link #DISABLED_FORMAT_HIDE}, 
     * {@link #DISABLED_FORMAT_LINK} or {@link #DISABLED_FORMAT_TEXT}, 
     * having consulted the xsp.properties options for the default value. 
     * @param context
     * @param disabledFormatInXPageSource
     * @param rendererDefaultFormat
     * @return
     */
    public static String computeDisabledFormat(FacesContext context, String disabledFormatInXPageSource, String rendererDefaultFormat ){
        
        String formatTypeAsString;
        if( StringUtil.isNotEmpty(disabledFormatInXPageSource) ){
            formatTypeAsString = disabledFormatInXPageSource;
        }else{
            // Note, if there are customer complaints about 
            // the change in default disabledFormat, then publicize
            // this xsp.properties option.
            String xspPropertiesOption = "extlib.addrows.disabledFormat"; //$NON-NLS-1$
            String applicationDefaultFormat = ((FacesContextEx)context).getProperty(xspPropertiesOption);
            if( StringUtil.isNotEmpty(applicationDefaultFormat) ){
                formatTypeAsString = applicationDefaultFormat;
            }else{
                if( StringUtil.isNotEmpty(rendererDefaultFormat) ){
                    formatTypeAsString = rendererDefaultFormat;
                }else{
                    formatTypeAsString = DISABLED_FORMAT_TEXT;
                }
            }
        }
        if( DISABLED_FORMAT_AUTO.equals(formatTypeAsString) ){
            formatTypeAsString = DISABLED_FORMAT_TEXT;
        }
        if (DISABLED_FORMAT_LINK.equals(formatTypeAsString) ) {
            return DISABLED_FORMAT_LINK;
        } else if(DISABLED_FORMAT_HIDE.equals(formatTypeAsString)){
            return DISABLED_FORMAT_HIDE;
        }else{
            return DISABLED_FORMAT_TEXT;
        }
    }
}

