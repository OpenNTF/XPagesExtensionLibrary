/*
 * © Copyright IBM Corp. 2015
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

package com.ibm.xsp.extlib.designer.bluemix.action;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.bluemix.BluemixLogger;
import com.ibm.xsp.extlib.designer.bluemix.config.BluemixConfig;
import com.ibm.xsp.extlib.designer.bluemix.config.ConfigManager;
import com.ibm.xsp.extlib.designer.bluemix.util.BluemixUtil;

/**
 * @author Gary Marjoram
 *
 */
public class OpenAction extends Action implements IHandler {

    @Override
    public String getText() {
        return "&Open in Default System Browser"; // $NLX-OpenAction.OpeninDefaultSystemBrowser-1$
    }

    @Override
    public void run() {
        openInBrowser();
    }

     @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        openInBrowser();
        return null;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }
    
    private void openInBrowser() {
        if (ToolbarAction.project != null) {
            // Check for a valid configuration
            BluemixConfig config = ConfigManager.getInstance().getConfig(ToolbarAction.project);
            if (config.isValid(true)) {
                if (StringUtil.isNotEmpty(config.uri)) {
                    try {
                        // Check is the user lanching an XPage
                        if (StringUtil.isNotEmpty(ToolbarAction.xpage)) {
                            // Yes - Adjust the URL
                            BluemixUtil.openUrlInDefaultBrowser(new URL(config.uri + "/" + ToolbarAction.xpage));                            
                        } else {
                            // No - Open the base URL
                            BluemixUtil.openUrlInDefaultBrowser(new URL(config.uri));
                        }
                    } catch (MalformedURLException e) {
                        if(BluemixLogger.BLUEMIX_LOGGER.isErrorEnabled()){
                            BluemixLogger.BLUEMIX_LOGGER.errorp(this, "openInBrowser", e, "URL is Malformed"); // $NON-NLS-1$ $NLE-OpenAction.URLisMalformed-2$
                        }
                    }                    
                } else {
                    MessageDialog.openWarning(null, "Open in Default System Browser", "This application has not been deployed or there is no route to it."); // $NLX-OpenAction.OpeninDefaultSystemBrowser.1-1$ $NLX-OpenAction.Thisapplicationhasnotbeendeployed-2$
                }
            } else {
                MessageDialog.openWarning(null, "Open in Default System Browser", "This application is not configured for deployment."); // $NLX-OpenAction.OpeninDefaultSystemBrowser.1-1$ $NLX-OpenAction.Thisapplicationisnotconfiguredfor-2$
            }
        } else {
            MessageDialog.openError(null, "Open in Default System Browser", "No application has been selected or the selected application is not open."); // $NLX-OpenAction.OpeninDefaultSystemBrowser.1-1$ $NLX-OpenAction.Noapplicationhasbeenselectedorthe-2$
        }
    }
}