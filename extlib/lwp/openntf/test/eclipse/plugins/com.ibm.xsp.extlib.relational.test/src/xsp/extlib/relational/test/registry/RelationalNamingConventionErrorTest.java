/*
 * © Copyright IBM Corp. 2012
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 2 Feb 2012
* RelationalNamingConventionErrorTest.java
*/
package xsp.extlib.relational.test.registry;

/**
 * 
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class RelationalNamingConventionErrorTest extends RelationalNamingConventionTest {
	 @Override
    public String getDescription() {
        return super.getDescription() +" [ERROR severity only]";
    }

    @Override
    protected int getRuleSeverityLevelCutoff() {
        // only show Errors, not Warning and Info
        return SEV_ERROR;
    }
	
	private String[] skips = new String[]{
    };
    @Override
    protected String[] getSkips() {
        String[] arr = super.getSkips();
        // This is checking a different set of controls than the superclass
        // so none of the superclass skips apply
        arr = skips;
        return arr;
    }
}
