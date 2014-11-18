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

package com.ibm.xsp.extlib.device.impl;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.FacesExceptionEx;

import com.ibm.xsp.designer.context.XSPUserAgent;
import com.ibm.xsp.extlib.device.Device;
import com.ibm.xsp.extlib.device.DeviceService;
import com.ibm.xsp.extlib.social.impl.ResourceImpl;


public class DeviceImpl extends ResourceImpl implements Device {

    private static final long serialVersionUID = 1L;
    
    public static final int PHONE = 1;
    public static final int TABLET = 2;
    public static final int OTHER = 4; //for example an iPod
    
    public static final int GOOGLE = 8;
    public static final int APPLE = 16;
    public static final int MICROSOFT = 32;
    public static final int BLACKBERRY = 64;
    
    private int m_type = 0;
    private Method[] methods = this.getClass().getMethods();
    private static String[] PREFIXES = {"is","get"}; // $NON-NLS-1$ $NON-NLS-2$
    private Map m_versions;
    private XSPUserAgent _xspUserAgent;
    


    public DeviceImpl(DeviceService service, String userAgent) {
        super(service,userAgent);
        userAgent = StringUtil.getNonNullString(userAgent);
        parseBrowser(userAgent.toLowerCase());

        
    }

    @Override
    public DeviceService getService() {
        return (DeviceService)super.getService();
    }
    
  
    private Method retrieveMethod(String name)
    {
        for(String prefix:PREFIXES)
        {
            for(Method method:methods)
            {
                if(method.getName().equalsIgnoreCase(prefix+(String)name))
                {
                    Class[] parameters = method.getParameterTypes();
                    // this avoids to call mehod with same name but with parameters, at the moment we don't support parameters
                    if(parameters.length == 0)
                    {
                        Object[] params = new Object[] {};
                        return method;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public Object getField(String key) {
        
        // Look at the different providers
        DeviceDataProvider[] dataProviders = (DeviceDataProvider[])getService().getResourceDataProviders();
        for(int i=0; i<dataProviders.length; i++) {
            DeviceDataProvider pd = dataProviders[i];
            if(pd instanceof AbstractDeviceDataProvider) {
                Object value = ((AbstractDeviceDataProvider)pd).getValue(this, key);
                if(value!=null) {
                    return value;
                }
            }
        }
        Method met = retrieveMethod(key);
        if(met != null)
        {
            try {
                Object[] params = new Object[] {};
                return met.invoke(this, params);
            }
            catch (Exception e) {
                //Ignore exception
            }
        }
        return super.getField(key); 
    }
    
    /**
     * This is mostly for dump/debug capability
     * @param key
     * @return
     */
    @Override
    public Object getFieldByProvider(String provider, String key) {
        DeviceDataProvider[] dataProviders = (DeviceDataProvider[])getService().getResourceDataProviders();
        for(int i=0; i<dataProviders.length; i++) {
            DeviceDataProvider pd = dataProviders[i];
            if(StringUtil.equals(pd.getName(),provider)) {
                if(pd instanceof AbstractDeviceDataProvider) {
                    Object value = ((AbstractDeviceDataProvider)pd).getValue(this, key);
                    return value;
                }
            }
        }
        return null; 
    }

    @Override
    public void setField(String key, Object value) {
        throw new FacesExceptionEx(null,"The DeviceImpl object is read only. Use the sessionScope instead to store the user related data"); // $NLX-DeviceImpl.TheDeviceImplobjectisreadonlyUset-1$
    }
    
    @Override
    public Class<?> getType(Object key) {
        
        DeviceDataProvider[] dataProviders = (DeviceDataProvider[])getService().getResourceDataProviders();
        
        // Look at the different providers
        for(int i=0; i<dataProviders.length; i++) {
            DeviceDataProvider pd = dataProviders[i];
            if(pd instanceof AbstractDeviceDataProvider) {
                Class<?> type = ((AbstractDeviceDataProvider)pd).getType(this, key);
                if(type!=null) {
                    return type;
                }
            }
        }

        if(key instanceof String)
        {
            Method met = retrieveMethod((String)key);
            if(met != null)
            {
               return met.getReturnType();
            }
        }
        
        return super.getType(key);
    }
    
    public boolean isAndroid()
    {
        return (m_type & GOOGLE) == GOOGLE;
    }
    
    public boolean isApple()
    {
        return (m_type & APPLE) == APPLE;
    }

    public boolean isBlackberry()
    {
        return (m_type & BLACKBERRY) == BLACKBERRY;
    }
    
    public boolean isWindows()
    {
        return (m_type & MICROSOFT) == MICROSOFT;
    }

    public boolean isMobile()
    {
        return  (m_type & PHONE) == PHONE;
    }
    
    
    public boolean isTablet()
    {
        return (m_type & TABLET) == TABLET ;
    }
    
    public boolean isOther()
    {
        return (m_type & OTHER) == OTHER ;
    }
    
    public boolean isIphone()
    {
        return isApple() && isMobile() && !isOther();
    }
    
    public boolean isIpad()
    {
        return isApple() && isTablet();
    }
    
    public boolean isIpod()
    {
        return isApple() && isMobile() && isOther();
    }
    
    public Map getVersion()
    {
        return m_versions;
    }
    
    
    private String getVersion(String key)
    {
        if(_xspUserAgent == null)
        {
            _xspUserAgent = new XSPUserAgent(getId().toLowerCase());
        }
        return _xspUserAgent.getVersion(key);
    }
    
    
    protected void parseBrowser(String userAgentLC) {
        if(m_type == 0)
        {
           
            m_versions = new Hashtable();
            
            
            if(userAgentLC.contains("mobi")) // $NON-NLS-1$
            {
                m_type += PHONE;
            }
            if(userAgentLC.contains("tablet")) // $NON-NLS-1$
            {
                m_type += TABLET;
            }
            
            if(userAgentLC.contains("android")) // $NON-NLS-1$
            {
                m_type += GOOGLE;
                if((m_type & PHONE) == 0)
                {
                    m_type += TABLET;
                }
                m_versions.put("android", getVersion("android")); // $NON-NLS-1$ $NON-NLS-2$
                
            }
            else if(userAgentLC.contains("blackberry")) // $NON-NLS-1$
            {
                m_type += BLACKBERRY;
                if((m_type & TABLET) == 0)
                {
                    m_type += PHONE;
                }
                m_versions.put("blackberry",getVersion("blackberry")); // $NON-NLS-1$ $NON-NLS-2$
            }
            else if(userAgentLC.contains("windows")) // $NON-NLS-1$
            {
                
                String winVer = getVersion("windows phone"); // $NON-NLS-1$
                if(StringUtil.isEmpty(winVer))
                {
                    winVer = getVersion("windows phone os"); // $NON-NLS-1$
                }
                m_versions.put("windows",winVer); // $NON-NLS-1$
                if(!StringUtil.isEmpty(winVer))
                {
                    m_type += MICROSOFT;
                }
            }
            else if(userAgentLC.contains("mac os")) // $NON-NLS-1$
            {
                m_type += APPLE;
                String iDev = "iphone"; // $NON-NLS-1$
                if(userAgentLC.contains("ipad")) // $NON-NLS-1$
                {
                    m_type += TABLET;
                    if((m_type & PHONE) == PHONE)
                    {
                        m_type -= PHONE;
                    }
                    if((m_type & OTHER) == OTHER)
                    {
                        m_type -= OTHER;
                    }
                    iDev = "ipad"; // $NON-NLS-1$
                    
                }
                else if(userAgentLC.contains("ipod")) // $NON-NLS-1$
                {
                    if((m_type & PHONE) != PHONE)
                    {
                        m_type += PHONE;
                    }
                    if((m_type & OTHER) != OTHER)
                    {
                        m_type += OTHER;
                    }
                    iDev = "ipod"; // $NON-NLS-1$
                }
                String devVer = getVersion("iphone os");// $NON-NLS-1$
                if(StringUtil.isEmpty(devVer))
                {
                    devVer = getVersion("cpu os"); // $NON-NLS-1$
                }
                m_versions.put(iDev,devVer);
                
            }
        }
    }

}