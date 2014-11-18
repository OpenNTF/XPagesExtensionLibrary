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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 15 Jul 2011
* ExtlibTestControlInitializer.java
* (was previously named XspExtlibRenderUtil.java)
*/
package xsp.extlib.test.render;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.xp.XspDataIterator;
import com.ibm.xsp.extlib.component.data.AbstractPager;
import com.ibm.xsp.extlib.component.image.IconEntry;
import com.ibm.xsp.extlib.component.image.UIMultiGraphic;
import com.ibm.xsp.extlib.component.layout.OneUIApplicationConfiguration;
import com.ibm.xsp.extlib.component.layout.UIApplicationLayout;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.render.TestControlInitializer;
import com.ibm.xsp.util.TypedUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibTestControlInitializer implements TestControlInitializer{
    public void initControl(AbstractXspTest test, UIComponent control, FacesContext context) {
        
        if( control instanceof AbstractPager ){
            AbstractPager pager = (AbstractPager) control;
            
            pager.setFor("repeat1");
            
            XspDataIterator repeat1 = new XspDataIterator();
            repeat1.setId("repeat1");
            repeat1.setRendered(false);
            TypedUtil.getChildren(pager.getParent()).add(repeat1);
            return;
        }
        if( control instanceof UIMultiGraphic ){ 
//    <xe:multiImage id="multiImage1" value='read'>
//        <xe:this.icons>
//            <xe:iconEntry url="xpPostRead.gif" selectedValue="read"/>
//            <xe:iconEntry url="xpPostUnread.gif" selectedValue="unread"/>
//        </xe:this.icons>
//    </xe:multiImage>
            UIMultiGraphic multiImage = (UIMultiGraphic) control;
            multiImage.setValue("read");
            // TODO multiImage should have a defaultValue property and inherit from UIOutput
            
            IconEntry readIcon = new IconEntry();
            readIcon.setComponent(multiImage);
            readIcon.setSelectedValue("read");
            readIcon.setUrl("/xpPostRead.gif");
            multiImage.addIcon(readIcon);
            
            IconEntry unreadIcon = new IconEntry();
            unreadIcon.setComponent(multiImage);
            unreadIcon.setSelectedValue("unread");
            unreadIcon.setUrl("/xpPostUnread.gif");
            multiImage.addIcon(unreadIcon);
        }
        if( control instanceof UIApplicationLayout ){
            UIApplicationLayout appLayout = (UIApplicationLayout) control;
            OneUIApplicationConfiguration layoutConfig = new OneUIApplicationConfiguration();
            appLayout.setConfiguration(layoutConfig);
        }
    }


}
