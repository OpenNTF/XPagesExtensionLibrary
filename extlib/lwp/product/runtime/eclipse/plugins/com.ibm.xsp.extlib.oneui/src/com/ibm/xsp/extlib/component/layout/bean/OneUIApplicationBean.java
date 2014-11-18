/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.component.layout.bean;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.faces.context.FacesContext;

import com.ibm.commons.Platform;
import com.ibm.commons.util.io.json.JsonParser;
import com.ibm.xsp.extlib.component.layout.OneUIApplicationConfiguration;
import com.ibm.xsp.extlib.component.layout.bean.config.OneUIJSONFactory;


/**
 * OneUI Layout Bean
 */
public class OneUIApplicationBean implements ApplicationBean {

    public static final String DEFAULT_CONFIGURATION_FILE = "/WEB-INF/OneUIApplication.json"; // $NON-NLS-1$
    
    private static final long serialVersionUID = 1L;
    
    private OneUIApplicationConfiguration configuration;
    
    public OneUIApplicationBean() {
    }
    
    
    //////////////////////////////////////////////////////////////////////////////
    // Manage search
    //////////////////////////////////////////////////////////////////////////////

    
    //////////////////////////////////////////////////////////////////////////////
    // Access the configuration
    //////////////////////////////////////////////////////////////////////////////

    public OneUIApplicationConfiguration getConfiguration() {
        if(configuration==null) {
            synchronized (this) {
                if(configuration==null) {
                    this.configuration = parseConfiguration(DEFAULT_CONFIGURATION_FILE);
                    if(configuration==null) {
                        configuration = createDefaultConfiguration();
                    }
                }
            }
        }
        return configuration;
    }
    
    protected OneUIApplicationConfiguration createDefaultConfiguration() {
        OneUIApplicationConfiguration conf = new OneUIApplicationConfiguration();
        return conf;
    }
    
    public synchronized void setConfigurationFile(String path) {
        this.configuration = parseConfiguration(path);
        if(configuration==null) {
            configuration = createDefaultConfiguration();
        }
    }
    
    protected static OneUIApplicationConfiguration parseConfiguration(String path) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        InputStream is = ctx.getExternalContext().getResourceAsStream(path);
        if(is!=null) {
            try {
                try {
                    Reader reader = new InputStreamReader(is,"UTF-8"); // $NON-NLS-1$
                    OneUIJSONFactory factory = new OneUIJSONFactory();
                    return (OneUIApplicationConfiguration)JsonParser.fromJson(factory, reader);
                } finally {
                    is.close();
                }
            } catch(Exception ex) {
                Platform.getInstance().log(ex);
            }
        }
        return null;
    }
}