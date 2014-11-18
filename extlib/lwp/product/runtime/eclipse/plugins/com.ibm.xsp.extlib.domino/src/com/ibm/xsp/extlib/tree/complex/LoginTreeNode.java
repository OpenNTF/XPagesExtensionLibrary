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

package com.ibm.xsp.extlib.tree.complex;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.tree.ITreeNode;



/**
 * Leaf Tree node that lets the user login/logout.
 * 
 * @author Philippe Riand
 */
public class LoginTreeNode extends BasicComplexTreeNode {

    private static final long serialVersionUID = 1L;

    public LoginTreeNode() {
    }

    public int getType() {
        return ITreeNode.NODE_LEAF;
    }
    
    // It is rendered uniquely when running on the server
    @Override
    public boolean isRendered() {
        if(!super.isRendered()) {
            return false;
        }
        if(NotesContext.isClient()) {
            return false;
        }
        if(isLoggedIn()) {
            if(!canLogout()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String getLabel() {
        String label = super.getLabel();
        if(StringUtil.isNotEmpty(label)) {
            return label;
        }
        boolean logged = isLoggedIn();
        return logged ? "Logout" : "Login"; // $NLS-LoginTreeNode.Logout-1$ $NLS-LoginTreeNode.Login-2$
    }
    
    @Override
    public String getHref() {
        String href = super.getHref();
        if(StringUtil.isNotEmpty(href)) {
            return href;
        }
        boolean logged = isLoggedIn();
        String command = "";
        FacesContext ctx = FacesContext.getCurrentInstance();
        
        if(logged && StringUtil.equals(ctx.getExternalContext().getAuthType(), "Domino")) //$NON-NLS-1$
        {
        	command = "?logout"; // $NON-NLS-1$
        }
        else
        {
        	command = /*logged ? "/?logout" :*/ "/?opendatabase&login"; // $NON-NLS-1$
        }
        href = ctx.getExternalContext().getRequestContextPath() + command;
        return href;
    }
    
    protected boolean isLoggedIn() {
        XSPContext ctx = XSPContext.getXSPContext(FacesContext.getCurrentInstance());
        if(ctx!=null) {
            com.ibm.designer.runtime.directory.DirectoryUser user = ctx.getUser();
            return !user.isAnonymous();
        }
        return false;
    }
    
    private static final String CANLOGOUT_KEY = "extlib.user.canlogout"; // $NON-NLS-1$
    protected boolean canLogout() {
        FacesContext context = FacesContext.getCurrentInstance();
        Boolean canLogout = (Boolean)context.getExternalContext().getSessionMap().get(CANLOGOUT_KEY);
        
        if(canLogout==null) 
        {
            canLogout = discoverCanLogLogout(context);
            context.getExternalContext().getSessionMap().put(CANLOGOUT_KEY, canLogout);
        }
        return canLogout;
    }
    protected Boolean discoverCanLogLogout(FacesContext context) 
    {
        // If authenticated from a WAS proxy, logout is not available
        String was_header = ((HttpServletRequest)context.getExternalContext().getRequest()).getHeader("HTTP_$WSAT"); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(was_header)) {
            return false;
        }
        
        // Find the authentication type
        String authType = (String)context.getExternalContext().getRequestMap().get("VAR_AUTH_TYPE"); // $NON-NLS-1$
        if(StringUtil.isNotEmpty(authType)) {
            // Basic has no logout
            if(StringUtil.equalsIgnoreCase(authType, "Basic")) { // $NON-NLS-1$
                return false;
            }
            // Client neither
            if(StringUtil.equalsIgnoreCase(authType, "Client")) { // $NON-NLS-1$
                return false;
            }
            // Say ok for the other modes...
            return true;
        }
        
	        // Check for session based auth
	        Map<Object, Object> headerMap = context.getExternalContext().getRequestHeaderMap();//.values().toString();
	        String cookie = (String) headerMap.get("Cookie"); //$NON-NLS-1$
	        
	        //If they have a domino session id they will be able to logout
	        if(null != cookie && cookie.contains("DomAuthSessId")) // $NON-NLS-1$
	        {
	        	return true;
	        }
        
        // Ok, we don't know so we disable it
        return false;
    }
}