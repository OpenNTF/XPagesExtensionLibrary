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

package com.ibm.xsp.extlib.beans;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.device.Device;
import com.ibm.xsp.extlib.device.DeviceService;
import com.ibm.xsp.extlib.device.DeviceServicesFactory;
import com.ibm.xsp.model.DataObject;

public class DeviceBean implements DataObject,
        Serializable  {
    

    private static final long serialVersionUID = -1542986760516721516L;
    public static final String BEAN_DATA = "extlib.device.deviceBeanData"; //$NON-NLS-1$
    private static final String SERVER_UA = "Server_UA"; // $NON-NLS-1$
    
    public Object getValue(Object key) {

        return ((DataObject)getDevice()).getValue(key); 
    }

    public Class<?> getType(Object key) {
        return ((DataObject)getDevice()).getType(key); 
    }

    public boolean isReadOnly(Object key) {
        return ((DataObject)getDevice()).isReadOnly(key);
    }

    public void setValue(Object key, Object value) {
        ((DataObject)getDevice()).setValue(key, value);
    }

    // ======================================================================
    // Access to the bean properties
    // ======================================================================
    
    public Device getDevice(FacesContext context) {
        Device device = (Device) context.getExternalContext()
                .getRequestMap().get(BEAN_DATA);
        if (device == null) {
            HttpServletRequest request= (HttpServletRequest)context.getExternalContext().getRequest();
            String userAgent = request.getHeader("user-agent");//$NON-NLS-1$
            if(StringUtil.isEmpty(userAgent))
            {
                userAgent = SERVER_UA;
            }
            DeviceServicesFactory factory = DeviceServicesFactory.getInstance();
            DeviceService dvc = factory.getDeviceService();
            device = dvc.getDevice(userAgent);
            context.getExternalContext().getRequestMap().put(BEAN_DATA, device);
        }
        return device;
    }

    public Device getDevice() {
        return getDevice(FacesContext.getCurrentInstance());
    }


    public Object getValue(String key) {
        Object retObj = getValue((Object)key);
        
        return retObj;
    }

    public String getVersion(String key)
    {
        Map versions = (Map)getValue("version");//$NON-NLS-1$
        if(versions != null)
        {
            return (String)versions.get(key);
        }
        return null;
    }
    
    public boolean isMobile() {
       return (Boolean)getValue("mobile");//$NON-NLS-1$
    }
    
    public boolean isTablet() {
        return (Boolean)getValue("tablet");//$NON-NLS-1$
    }

    // Apple flavours
    public boolean isIphone() {
        return (Boolean)getValue("iphone");//$NON-NLS-1$
    }

    public boolean isIpad() {
        return (Boolean)getValue("ipad");//$NON-NLS-1$
    }

    public boolean isIpod() {
        return (Boolean)getValue("ipod");//$NON-NLS-1$
    }
    
    // Operating System

    public boolean isAndroid() {
        return (Boolean)getValue("android");//$NON-NLS-1$
    }
    public boolean isApple() {
        return (Boolean)getValue("apple");//$NON-NLS-1$
    }
    
    public boolean isWindows() {
        return (Boolean)getValue("windows");//$NON-NLS-1$
    }
    
    public boolean isBlackberry() {
        return (Boolean)getValue("blackberry");//$NON-NLS-1$
    }

}