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

package com.ibm.domino.commons.model;

import java.util.Date;

public interface IStatisticsProvider {
    
    public void UpdateInt(String facility, String statName, boolean additive, int value);

    /**
     * DEPRECATED: This method truncates the 64-bit long value to 32 bits.
     */
    @Deprecated
    public void UpdateLong(String facility, String statName, boolean additive, long value);
    
    public void UpdateText(String facility, String statName, String value);
    public void UpdateTimeDate(String facility, String statName, Date date);
    public void UpdateNumber(String facility, String statName, double value);
    public void Delete(String facility, String statName);

}
