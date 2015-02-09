package com.ibm.xsp.extlib.actions.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.complex.Parameter;
import com.ibm.xsp.extlib.component.dynamiccontent.UIInPlaceForm;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.util.StateHolderUtil;

public class InPlaceFormAction extends AbstractServerSimpleAction {

	private String _for;
	private String _formAction;
    private List<Parameter> parameters;


	private static final String ACTION_TOGGLE = "toggle";
	private static final String ACTION_SHOW = "show";
	private static final String ACTION_HIDE = "hide";
	
	@Override
	public Object invoke(FacesContext context, Object[] params)
			throws EvaluationException, MethodNotFoundException {

		String forId = getFor();
		UIInPlaceForm inPlaceForm = findNonNullInPlaceForm(forId);
		
		Map<String,String> parameters = null;
        List<Parameter> nameValuePairs = getParameters();
        if( null != nameValuePairs && ! nameValuePairs.isEmpty() ){
            parameters = new HashMap<String, String>(nameValuePairs.size());
            // evaluate parameter value bindings
            for (Parameter pair : nameValuePairs) {
                String name = pair.getName();
                if( StringUtil.isNotEmpty(name) ){
                    String value = pair.getValue();
                    parameters.put(name, value);
                }
            }
            if( parameters.isEmpty() ){
                parameters = null;
            }
        }
		
        String action = getFormAction();
				
		// If nothing has been set then just toggle
		if (StringUtil.isEmpty(action)) action = ACTION_TOGGLE;

		if (StringUtil.equals(action, ACTION_TOGGLE)) {
			
			 if( null != parameters){
				 inPlaceForm.toggle(parameters);
			 } else {
				 inPlaceForm.toggle();
			 }			
			
		} else if (StringUtil.equals(action, ACTION_SHOW)) {
			
			if (null != parameters) {
				inPlaceForm.show(parameters);
			} else {
				inPlaceForm.show();
			}
			
		} else if (StringUtil.equals(action, ACTION_HIDE)) {
			inPlaceForm.hide();
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
		} else {
			for (UIComponent c = getComponent(); c != null; c = c.getParent()) {
				if (c instanceof UIInPlaceForm) {
					forControl = c;
					break;
				}
			}
		}

		if (forControl instanceof UIInPlaceForm) {
			return (UIInPlaceForm) forControl;
		}
		if (null != forControl) {
			String msg = "The In Place Form action cannot change the control {0} because it is not an In Place Form"; 
			msg = StringUtil.format(msg, forId);
			throw new FacesExceptionEx(msg);
		}
		// else if( null == forControl ){
		UIComponent ancestor = findHighestAncestor(getComponent());
		if (!(ancestor instanceof UIViewRoot)) {
			String msg = "The In Place Form action is no longer in the control tree, so it cannot find a control with the ID {0}."; 
			msg = StringUtil.format(msg, forId);
			throw new FacesExceptionEx(msg);
		}
		// }

		String msg = "The In Place Form action cannot find a control with the ID {0}."; 
		msg = StringUtil.format(msg, forId);
		throw new FacesExceptionEx(msg);
	}

	/**
	 * @param component
	 * @return
	 */
	private UIComponent findHighestAncestor(UIComponent ancestor) {
		UIComponent last = ancestor;
		while (null != ancestor) {
			last = ancestor;
			ancestor = ancestor.getParent();
		}
		return last;
	}

	/**
	 * The ID of the {@link UIInPlaceForm} control whose state will be
	 * updated by this action. (Note, this is the control's ID, not the
	 * clientId.)
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
	 * @param _for
	 *            the ID of the {@link UIInPlaceForm} control.
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

	 /**
     * Add a parameter
     * @param parameter
     */
    public void addParameter(Parameter parameter){
        if (parameters == null) {
            parameters = new ArrayList<Parameter>();
        }
        parameters.add(parameter);
    }

    /**
     * Return the list of parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
	
	@Override
	public void restoreState(FacesContext context, Object value) {
		Object[] state = (Object[]) value;
		super.restoreState(context, state[0]);
		_for = (String) state[1];
		_formAction = (String) state[2];
        parameters = StateHolderUtil.restoreList(context, getComponent(), state[3]);
	}

	@Override
	public Object saveState(FacesContext context) {
		Object[] state = new Object[4];
		state[0] = super.saveState(context);
		state[1] = _for;
		state[2] = _formAction;
		state[3] = StateHolderUtil.saveList(context, parameters);
		return state;
	}

}
