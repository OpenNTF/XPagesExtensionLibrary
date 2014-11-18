/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.xsp.extlib.component.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.component.FacesComponent;
import com.ibm.xsp.page.FacesComponentBuilder;
import com.ibm.xsp.util.StateHolderUtil;

/**
 * Redirect control to assist mostly with mobile page redirection. By default is based on request header's "User-Agent" information.
 * 
 * @author Andrejus Chaliapinas
 *
 */
public class UIRedirect extends UIComponentBase implements FacesComponent 
{
	public static final String	RENDERER_TYPE = "com.ibm.xsp.extlib.misc.Redirect";	//$NON-NLS-1$
	public static final String	COMPONENT_TYPE = "com.ibm.xsp.extlib.misc.Redirect";	//$NON-NLS-1$
	public static final String	COMPONENT_FAMILY = "com.ibm.xsp.extlib.misc.Redirect";	//$NON-NLS-1$

	private List<AbstractRedirectRule> rules;

	public UIRedirect() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public List<AbstractRedirectRule> getRules() {
		return rules;
	}

	public void addRule(AbstractRedirectRule redirectRule) {
		if (rules == null) {
			rules = new ArrayList<AbstractRedirectRule>();
		}
		rules.add(redirectRule);
	}

	@Override
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);

		this.rules = StateHolderUtil.restoreList(context, this, values[1]);
	}

	@Override
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);

		values[1] = StateHolderUtil.saveList(context, rules);
		return values;
	}


	// UIComponent building where actual redirect will happen
	public void initBeforeContents(FacesContext context) throws FacesException {
		processRedirect(context);
	}

	protected void processRedirect(FacesContext context) {

		// TODO unused: RedirectRule.isDisableRequestParams (really, go check)
		// TODO have a conversation with the Designer team around naming of the property disableRequestParams

		// Use cases to get here from Traveler:
		// Use case #1 will be translated by NSF application into use case #2
		// when particular NSF form will have an XPage associated.
		// URL will stay the same #1 format in browser address field.
		// 1.
		// http://www.acme.com/CCC579050049E718/0E5E7B7972EE57F1802577C2003DF433/4544B55217EB99038025799F0054C209?OpenDocument
		// 2.
		// http://www.acme.com/teamrm8xl.nsf/topicThread.xsp?action=openDocument&documentId=4544B55217EB99038025799F0054C209

		String redirectURL = "";

		List<AbstractRedirectRule> rules = getRules();
		
		if (rules != null && rules.size() > 0) 
		{
			for (int i = 0; i < rules.size(); i++) 
			{
				AbstractRedirectRule redirectRule = rules.get(i);
				if (null == redirectRule) 
				{
					continue;
				}
				
				redirectURL = redirectRule.getRedirectURL(context);
				
				if(!StringUtil.isEmpty(redirectURL)) 
				{
					try 
					{
						context.getExternalContext().redirect(redirectURL);
					} 
					catch (IOException ioe) 
					{
						String msg = "Problem applying redirect rule to open {0}"; // $NLX-UIRedirect_ProblemDoingRedirect-1$
						msg = StringUtil.format(msg, redirectURL);
						throw new FacesException(msg, ioe);
					}
					break;
				}
			}
		}
	}

	public void buildContents(FacesContext context, FacesComponentBuilder builder) throws FacesException {
		// default behavior
		builder.buildAll(context, this, /* facets */true);
	}

	public void initAfterContents(FacesContext context) throws FacesException {
	}
}