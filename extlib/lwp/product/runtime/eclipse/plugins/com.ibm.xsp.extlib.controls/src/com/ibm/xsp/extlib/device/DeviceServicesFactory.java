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

package com.ibm.xsp.extlib.device;

import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.runtime.Application;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.extlib.device.impl.DeviceDataProvider;
import com.ibm.xsp.extlib.device.impl.DeviceServiceImpl;


public class DeviceServicesFactory {
    

    private static final String DEVICE_SERVICE_FACTORY = "com.ibm.xsp.extlib.device.DeviceServicesFactory";  // $NON-NLS-1$
    private static final String DEVICE_SERVICE_KEY     = "extlib.device.serviceFactory";  // $NON-NLS-1$
    
    private static final String DEVICE_DATAPROVIDER_SERVICE = "com.ibm.xsp.extlib.device.DeviceDataProvider";  // $NON-NLS-1$
    
    private static final String PREF_PROVIDER = "extlib.device.provider"; //$NON-NLS-1$
    
    
//    public static DeviceServicesFactory getInstance(FacesContext context) {
//        return getInstance();
//    }
    public static DeviceServicesFactory getInstance() {
        // Use the Designer application as it is cleared-out after a deployment
        // So we read back the new class, if any
        DeviceServicesFactory f = (DeviceServicesFactory)Application.get().getObject(DEVICE_SERVICE_KEY);
        if(f==null) {
            synchronized(DeviceServicesFactory.class) {
                f = (DeviceServicesFactory)Application.get().getObject(DEVICE_SERVICE_KEY);
                if(f==null) {
                    List<DeviceServicesFactory> l = AccessController.doPrivileged(new PrivilegedAction<List<DeviceServicesFactory>>() {
                        public List<DeviceServicesFactory> run() {
                            List<DeviceServicesFactory> l = ApplicationEx.getInstance().findServices(DEVICE_SERVICE_FACTORY);
                            return l;
                        }
                    });
                    if(!l.isEmpty()) {
                        f = l.get(0);
                    } else {
                        f = new DeviceServicesFactory();
                    }
                    Application.get().putObject(DEVICE_SERVICE_KEY,f);
                }
            }
        }
        return f;
    }
    
    private DeviceService deviceService;

    public DeviceServicesFactory() {
    }

    
    ///// Device service
    public DeviceService getDeviceService() {
        if(deviceService==null) {
            // Execute everything in a privileged block as it accesses class loaders and read extension points
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    //
                    // Find the device providers
                    //

                    // Read the authorized providers
                    String providersProp = ApplicationEx.getInstance().getApplicationProperty(PREF_PROVIDER, null);
                    String[] providersList = StringUtil.splitString(providersProp, ';'); 
                    
                    List<DeviceDataProvider> allDataProviders = ApplicationEx.getInstance().findServices(DEVICE_DATAPROVIDER_SERVICE);
                    List<DeviceDataProvider> dataProviders = new ArrayList<DeviceDataProvider>(allDataProviders.size());
                    for(int i=0; i<allDataProviders.size(); i++) {
                        DeviceDataProvider p = allDataProviders.get(i);
                        if(acceptDeviceDataProvider(providersList,p)) {
                            dataProviders.add(p);
                        }
                    }
                    DeviceDataProvider[] deviceDataProviders = dataProviders.toArray(new DeviceDataProvider[dataProviders.size()]);
                    sortDataProviders(providersList,deviceDataProviders);
                    
                   
                    // Create the service
                    deviceService = createDeviceService(deviceDataProviders);
                    
                    return null;
                }
            });
        }
        return deviceService;
    }

    protected DeviceService createDeviceService(DeviceDataProvider[] providers) {
        return new DeviceServiceImpl(providers);
    }
    
    protected boolean acceptDeviceDataProvider(String[] providersList, DeviceDataProvider provider) {
        if(provider.isDefaultProvider()) {
            return true;
        }
        if(indexOf(providersList, provider.getName())>=0) {
            return true;
        }
        return false;
    }

   
    
    protected void sortDataProviders(final String[] providerList, DeviceDataProvider[] providers) {
        (new QuickSort.ObjectArray(providers) {
            @Override
            public int compare(Object o1, Object o2) {
                DeviceDataProvider p1 = (DeviceDataProvider)o1; 
                DeviceDataProvider p2 = (DeviceDataProvider)o2;
                // Compare their position in the list
                int p = indexOf(providerList, p1.getName())-indexOf(providerList, p2.getName());
                if(p!=0) {
                    return p;
                }
                // Then compare their respective weight
                int w1 = ((DeviceDataProvider)o1).getWeight();
                int w2 = ((DeviceDataProvider)o2).getWeight();
                return w1-w2;
            }
        }).sort();
    }
    
    private static int indexOf(String[] array, String s) {
        if(array!=null) {
            for(int i=0; i<array.length; i++) {
                if(array[i].equalsIgnoreCase(s)) {
                    return i;
                }
            }
        }
        return -1;
    }
}