/* 
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
 *
 * The first version of this file in this source control project 
 * was contributed through
 * https://github.com/OpenNTF/XPagesExtensionLibrary/pull/16
 * by Cameron Gregor (https://github.com/camac)
 */

package com.ibm.xsp.extlib.actions.server;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.extlib.component.dynamiccontent.UIInPlaceForm;
import com.ibm.xsp.util.FacesUtil;

public class InPlaceFormAction extends AbstractServerSimpleAction {

    private String _for;
    private String _formAction;
 
    private static final String ACTION_SHOW = "show"; // $NON-NLS-1$
    private static final String ACTION_HIDE = "hide"; // $NON-NLS-1$

    @Override
    public Object invoke(FacesContext context, Object[] params) throws EvaluationException, MethodNotFoundException {

        String forId = getFor();
        UIInPlaceForm inPlaceForm = findNonNullInPlaceForm(forId);

        String action = getFormAction();

        if (StringUtil.equals(action, ACTION_SHOW)) {
                inPlaceForm.show();
        } else if (StringUtil.equals(action, ACTION_HIDE)) {
            	inPlaceForm.hide();
        }
        else {
        		inPlaceForm.toggle();
        }

        return null; // do not move to a different page
    }

    /**
     * @param forId
     * @return
     */
    private UIInPlaceForm findNonNullInPlaceForm(String forId) {
        UIComponent forControl = null;
        // If there is an explicit id, then use it
        if (StringUtil.isNotEmpty(forId)) {
            forControl = FacesUtil.getComponentFor(getComponent(), forId);
            
            if (null != forControl && !(forControl instanceof UIInPlaceForm)) {
                String msg = "The In Place Form action cannot change the control {0} because it is not an In Place Form"; // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotchanget-1$
                msg = StringUtil.format(msg, forId);
                throw new FacesExceptionEx(msg);
            }
            if (forControl == null) {
                String msg = "The In Place Form action cannot find a control with the ID {0}."; // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotfindaco-1$
                msg = StringUtil.format(msg, forId);
                throw new FacesExceptionEx(msg);
            }
            return (UIInPlaceForm)forControl;
        } else {
            for (UIComponent c = getComponent(); c != null; c = c.getParent()) {
                if (c instanceof UIInPlaceForm) {
                    forControl = c;
                    break;
                }
            }
            if(forControl == null){
                String msg = "The In Place Form action cannot find an ancestor In Place Form control";  // $NLS-InPlaceFormAction.TheInPlaceFormactioncannotfindana-1$
                throw new FacesExceptionEx(msg);
            }
            return (UIInPlaceForm)forControl;               
        }
    }

    /**
     * The ID of the {@link UIInPlaceForm} control whose state will be updated
     * by this action. (Note, this is the control's ID, not the clientId.) + *
     * 
     * @return the _for
     */
    public String getFor() {
        if (null != _for) {
            return _for;
        }
        ValueBinding vb = getValueBinding("for"); //$NON-NLS-1$
        if (null != vb) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * + * @param _for the ID of the {@link UIInPlaceForm} control.
     */
    public void setFor(String _for) {
        this._for = _for;
    }

    /**
     * The action to perform on the InPlaceForm
     * 
     * @return
     */
    public String getFormAction() {
        if (null != _formAction) {
            return _formAction;
        }
        ValueBinding vb = getValueBinding("formAction"); //$NON-NLS-1$
        if (null != vb) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;

    }

    public void setFormAction(String _formAction) {
        this._formAction = _formAction;
    }

    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[]) value;
        super.restoreState(context, state[0]);
        _for = (String) state[1];
        _formAction = (String) state[2];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[3];
        state[0] = super.saveState(context);
        state[1] = _for;
        state[2] = _formAction;
        return state;
    }

}