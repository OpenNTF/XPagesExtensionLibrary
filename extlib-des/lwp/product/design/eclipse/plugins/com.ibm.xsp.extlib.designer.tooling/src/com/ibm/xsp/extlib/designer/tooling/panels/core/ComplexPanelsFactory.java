/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.tooling.panels.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Composite;

import com.ibm.designer.domino.xsp.api.panels.complex.IComplexPanel;
import com.ibm.designer.domino.xsp.api.panels.complex.IComplexPanelFactory;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.xsp.extlib.designer.tooling.ExtLibToolingPlugin;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

/**
 * This factory is contributed via the com.ibm.designer.domino.xsp.editor.panels
 * extension point. This factory will be supplied with a class name and will be 
 * expected to generate a new IComplexPanel based on that class name.
 * 
 * @author doconnor
 *
 */
public class ComplexPanelsFactory implements IComplexPanelFactory {

    private static final Class<?>[] PARAM_TYPES = new Class<?>[] {Composite.class};
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.panels.complex.IComplexPanelFactory#createPanel(java.lang.String, com.ibm.designer.domino.xsp.api.util.XPagesKey, java.lang.String, org.eclipse.swt.widgets.Composite)
     */
    public IComplexPanel createPanel(String contextId, XPagesKey key, String className, Composite parent) {
        //Called from the from the ComplexComposite class. The className provided is contributed via the 
        //com.ibm.designer.domino.xsp.editor.panels extension point.
        //This factory must take the class name and load a new class based on the name.
        //The new panel will then be added as a child of the ComplexComposite
        IComplexPanel panel = null;
        if (className != null) {
            Class<? extends IComplexPanel> clazz = loadClass(className);
            if (clazz != null) {
                panel = loadPanel(clazz, parent);
            }
        }
        
        return panel;
    }
    
    private IComplexPanel loadPanel(Class<? extends IComplexPanel> clazz, Composite parent) {
        IComplexPanel panel = null;
        try {
            Constructor<? extends IComplexPanel> constructor = clazz.getConstructor(PARAM_TYPES);
            panel = constructor.newInstance(new Object[] {parent});
        } catch (NoSuchMethodException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadPanel", e, "Failed to find the specified constructor.");  // $NON-NLS-1$ $NLE-ComplexPanelsFactory.Failedtofindthespecifiedconstruct-2$
            }  
        } catch (IllegalArgumentException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadPanel", e, "Illegal arguments provided to panel constructor.");  // $NON-NLS-1$ $NLE-ComplexPanelsFactory.Illegalargumentsprovidedtopanelco-2$
            } 
        } catch (InstantiationException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadPanel", e, "Error occurred while creating a new instance of the complex panel"); // $NON-NLS-1$ $NLE-ComplexPanelsFactory.Erroroccurredwhilecreatinganewins-2$
            } 
        } catch (IllegalAccessException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadPanel", e, "Illegal access error occurred while creating new complex panel"); // $NON-NLS-1$ $NLE-ComplexPanelsFactory.Illegalaccessserroroccurredwhilec-2$
            } 
        } catch (InvocationTargetException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadPanel", e, "");  // $NON-NLS-1$
            } 
        }
        
        return panel;
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    private Class<? extends IComplexPanel> loadClass(String className) {
        Class<? extends IComplexPanel> clazz = null;
        try {
            //The class is in this plugin, load it using this plugin's class loader
            clazz = ExtLibToolingPlugin.getDefault().getBundle().loadClass(className);
        } catch (ClassNotFoundException e) {
            if(ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.isErrorEnabled()){
                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.errorp(this, "loadClass", e, "Could not find the provided class {0}. Please check the class name as defined in plugin.xml");  // $NON-NLS-1$ $NLE-ComplexPanelsFactory.Couldnotfindtheprovidedclass0Plea-2$
            } 
        }
        return clazz;
    }

}