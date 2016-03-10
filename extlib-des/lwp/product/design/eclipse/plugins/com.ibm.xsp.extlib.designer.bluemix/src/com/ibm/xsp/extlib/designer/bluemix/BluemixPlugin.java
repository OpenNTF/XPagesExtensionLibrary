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

package com.ibm.xsp.extlib.designer.bluemix;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class BluemixPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.ibm.xsp.extlib.designer.bluemix"; // $NON-NLS-1$

    // The shared instance
    private static BluemixPlugin _plugin;
    
    /**
     * The constructor
     */
    public BluemixPlugin() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        _plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        _plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static BluemixPlugin getDefault() {
        return _plugin;
    }
    
    public static Image getImage(String location) {
        Image ret = getDefault().getImageRegistry().get( location );
        if (ret == null){
            ImageDescriptor imgDesc = getImageDescriptor( location );
            if (imgDesc != null) {
                ret = imgDesc.createImage();
                getDefault().getImageRegistry().put( location, ret );
            }
        }
        return ret;
    }
    
    public static ImageDescriptor getImageDescriptor(String location) {
        String iconPath = "images/"; // $NON-NLS-1$
        URL imageUrl = getDefault().getBundle().getEntry(iconPath + location);
        if (imageUrl != null) {
            return ImageDescriptor.createFromURL(imageUrl);
        }
        return null;
    }   

}