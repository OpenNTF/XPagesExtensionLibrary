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

package com.ibm.xsp.extlib.actions.client;

import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.actions.client.AbstractClientSimpleAction;
import com.ibm.xsp.util.JavaScriptUtil;

/**
 * Basic client action that displays an alert in the browser. 
 * 
 * @author Philippe Riand
 * @designer.public
 */
public class AlertClientAction extends AbstractClientSimpleAction {
    
	private String _text;
	
	/**
	 * @return Returns the text to be displayed
	 */
	public String getText() {
        if (_text == null) {
            ValueBinding vb = getValueBinding("text"); //$NON-NLS-1$
            if (vb != null) {
                return (String) vb.getValue(FacesContext.getCurrentInstance());
            }
        }
        return _text;
	}

	/**
	 * @param text the text to be displayed
	 */
	public void setText(String text) {
		_text = text;
	}

    @Override
	public Object saveState(FacesContext context) {
        Object[] state = new Object[2];
        state[0] = super.saveState(context);
        state[1] = _text;
        return state;
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.binding.MethodBindingEx#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    @Override
	public void restoreState(FacesContext context, Object value) {
        Object[] values = (Object[])value;
        super.restoreState(context, values[0]);
        _text = (String)values[1];
    }
    
    @Override
    public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {
		StringBuilder b = new StringBuilder(256);
		
		String text = getText();
		if(text==null) {
			text = ""; //$NON-NLS-1$
		}
		b.append("alert("); //$NON-NLS-1$
		JavaScriptUtil.addString(b, text);
		b.append(");\n"); //$NON-NLS-1$
		
        return b.toString();
	}
    
}
