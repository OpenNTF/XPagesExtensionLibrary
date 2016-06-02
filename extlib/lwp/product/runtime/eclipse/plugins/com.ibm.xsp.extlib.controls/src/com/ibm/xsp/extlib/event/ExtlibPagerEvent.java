/*
 * © Copyright IBM Corp. 2016
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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 4 Mar 2016
* ExtlibPagerEvent.java
*/
package com.ibm.xsp.extlib.event;

import javax.faces.component.UIComponent;

import com.ibm.xsp.event.PagerEvent;

/**
 *
 */
public class ExtlibPagerEvent extends PagerEvent {
    private static final long serialVersionUID = 1L;
    // This class added as part of:
    // Defect#200395 XLIB A11Y Mob | AVT3 | Focus issues due to Refresh issues on Mobile
    // In the next release post-9.0.1, the parent PagerEvent will probably 
    // have get/setClientId methods, so it will be possible to remove them here.
    
    private String clientId;
    
    public ExtlibPagerEvent(UIComponent component) {
        super(component);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
