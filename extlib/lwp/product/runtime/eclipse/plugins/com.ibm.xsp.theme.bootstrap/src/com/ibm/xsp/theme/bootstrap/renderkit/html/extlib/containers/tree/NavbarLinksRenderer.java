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
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers.tree;

import javax.faces.component.UIComponent;

import com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.util.NavRenderer;

public class NavbarLinksRenderer extends NavRenderer {
    
    private static final long serialVersionUID = 1L;
    
    protected static final int PROP_POSITION = 1;
    
    private String position;
    
    public static final String POSITION_LEFT     = "navbar-left"; //$NON-NLS-1$
    public static final String POSITION_RIGHT    = "navbar-right"; //$NON-NLS-1$
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            case PROP_POSITION:    return position; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    public NavbarLinksRenderer() {
        this(POSITION_RIGHT);
    }
    
    public NavbarLinksRenderer(UIComponent component) {
        super(component);
        setPosition(POSITION_RIGHT);
    }
    
    public NavbarLinksRenderer(String _position) {
        if(_position.equals(POSITION_LEFT) || _position.equals(POSITION_RIGHT)) {
            setPosition(_position);
        }else{
            setPosition(POSITION_RIGHT);
        }
    }

    @Override
    protected boolean makeSelectedActive(TreeContextImpl node) {
        return false;
    }

    @Override
    protected String getContainerStyleClass(TreeContextImpl node) {
        if(node.getDepth()==1) {
            return "nav navbar-nav " + getProperty(PROP_POSITION); // $NON-NLS-1$
        }
        return super.getContainerStyleClass(node);
    }
    
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}