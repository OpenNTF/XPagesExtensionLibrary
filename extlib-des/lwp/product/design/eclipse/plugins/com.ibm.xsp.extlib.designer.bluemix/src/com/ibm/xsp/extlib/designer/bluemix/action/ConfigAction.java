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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.action.Action;

import com.ibm.xsp.extlib.designer.bluemix.wizard.ConfigBluemixWizard;

/**
 * @author Gary Marjoram
 *
 */
public class ConfigAction extends Action implements IHandler {

    @Override
    public String getText() {
        return "&Configure For Deployment";  // $NLX-ConfigAction.ConfigureForDeployment-1$
    }

    @Override
    public void run() {
        ConfigBluemixWizard.launch();
    }

     @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ConfigBluemixWizard.launch();
        return null;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
    }
}