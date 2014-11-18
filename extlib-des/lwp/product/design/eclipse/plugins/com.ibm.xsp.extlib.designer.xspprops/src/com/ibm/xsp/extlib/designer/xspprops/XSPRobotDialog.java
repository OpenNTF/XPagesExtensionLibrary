/*
 * © Copyright IBM Corp. 2013
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
package com.ibm.xsp.extlib.designer.xspprops;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.ibm.commons.swt.data.dialog.SimpleDialog;

public class XSPRobotDialog extends SimpleDialog {

    private String userAgents;
    private XSPRobotComposite dialogRobotComp;
    
    public XSPRobotDialog(Shell shell, String agents) {
        super(shell);
        userAgents = agents;
    }
    
    public String getMessage() {
        return "Add, remove, or edit search engine robot user agent keywords.";  // $NLX-XSPRobotDialog.Addremoveoreditsearchenginerobotu-1$
    }

    protected void fillClientArea(Composite parent) {
        parent.setLayout(new FillLayout());    
        dialogRobotComp = new XSPRobotComposite(parent, false, userAgents);
    }
    
    protected String getDialogTitle() {
        return "User Defined Search Engine Robot User Agents";  // $NLX-XSPRobotDialog.UserDefinedSearchEngineRobotUserA-1$
    }

    protected boolean performDialogOperation(IProgressMonitor progressMonitor) {
        return true;
    }
    
    public String getUserAgents() {
        return dialogRobotComp.getUserAgents();
    }    
}