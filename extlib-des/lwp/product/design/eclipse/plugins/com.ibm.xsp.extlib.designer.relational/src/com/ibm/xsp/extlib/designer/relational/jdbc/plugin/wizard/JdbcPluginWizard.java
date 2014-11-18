/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.xsp.extlib.designer.relational.jdbc.plugin.wizard;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.ibm.xsp.extlib.designer.relational.utils.RelationalLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * @author Gary Marjoram
 *
 */
public class JdbcPluginWizard extends Wizard {
    public final static String    WINDOW_TITLE = "JDBC Driver Plug-in Wizard"; // $NLX-JdbcPluginWizard.JDBCDriverPluginWizard-1$
    private JdbcPluginWizardPage _page;

    //
    // Add the single Wizard page
    //
    @Override
    public void addPages() {
        // Set the Wizard Title
        setWindowTitle(WINDOW_TITLE);

        // Only one page in this Wizard
        _page = new JdbcPluginWizardPage();
        addPage(_page);
    }

    //
    // We're putting a progress bar on this page
    //
    @Override
    public boolean needsProgressMonitor() {
        return true;
    }

    //
    // Generates the output
    //
    @Override
    public boolean performFinish() {
        // Clear any previous errors
        _page.setErrorMessage(null);

        // Initialise the generator
        final JdbcPluginGenerator generator = new JdbcPluginGenerator(_page.getPluginName(), _page.getClassName(), _page.getJarList(),
                _page.getOutputDir(), _page.getUpdateSite(), _page.getDeleteProject());

        // Setup result container
        final Exception exception[] = new Exception[1];
        exception[0] = null;

        // Start the generation process
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) {
                    try {
                        generator.generateUpdateSite(monitor);
                    } catch (Exception e) {
                        exception[0] = e;
                    }
                }
            });
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check for errors
        if (exception[0] != null) {
            String msg = exception[0].getLocalizedMessage();
            if (msg == null) {
                msg = exception[0].getClass().toString();
            }
            Throwable cause = exception[0].getCause();
            if (cause != null) {
                msg += " : " + cause.getMessage();
            }
            _page.setErrorMessage(msg);
            
            // Log error
            if (RelationalLogger.EXT_LIB_RELATIONAL_LOGGER.isInfoEnabled()) {
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.info(exception[0], "performFinish : Error generating plugin / updateSite "); // $NLI-JdbcPluginWizard.performFinisherrorgeneratingplugi-1$
            }

            // Failure - keep the Wizard open
            return false;
        }

        // Success - exit the Wizard
        return true;
    }

}