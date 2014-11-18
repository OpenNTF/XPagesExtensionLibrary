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
package com.ibm.xsp.extlib.designer.tooling.visualizations.mobile;

/**
 * @author doconnor
 *
 */
public class ToolBarButtonVisualizer extends TabBarButtonVisualizer {
    private static final String TOOL_BAR_BUTTON_LABEL = "Tool Bar Button";  // $NLX-ToolBarButtonVisualizer.ToolBarButton-1$
    /**
     * 
     */
    public ToolBarButtonVisualizer() {
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.designer.tooling.visualizations.mobile.TabBarButtonVisualizer#getLabelDefaultValue()
     */
    @Override
    protected String getLabelDefaultValue() {
        return TOOL_BAR_BUTTON_LABEL;
    }
}