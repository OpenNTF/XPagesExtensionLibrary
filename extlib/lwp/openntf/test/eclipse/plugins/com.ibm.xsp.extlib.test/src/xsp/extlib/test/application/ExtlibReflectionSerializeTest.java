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
/*
* Author: Maire Kehoe (mkehoe@ie.ibm.com)
* Date: 25 Feb 2011
* ExtlibReflectionSerializeTest.java
*/
package xsp.extlib.test.application;

import com.ibm.designer.runtime.DesignerRuntime;
import com.ibm.xsp.extlib.renderkit.html_extended.outline.tree.AbstractTreeRenderer;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.script.WrapperXSP;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.serialize.BaseReflectionSerializeTest;

/**
 * A test that serializes JSF control trees containing each control in the
 * registry, and uses the Java reflection APIs to compare the contents of the
 * old control tree to the restored control tree.
 * 
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class ExtlibReflectionSerializeTest extends BaseReflectionSerializeTest {
	
    @Override
	public String getDescription() {
		return "creates and serializes control trees for each tag in the registry, " +
				"then uses Java reflection to compare the old and restore trees.";
	}
	public static Object[][] getCompareSkips_AbstractTreeRenderer() {
		Object[][] skips = new Object[][]{
				// Add skip to prevent StackOverflowError
				// TODO rename AbstractTreeRenderer.getTreeNodeDefault
				new Object[]{"getTreeNodeDefault", AbstractTreeRenderer.class, false},
		};
		return skips ;
	}
    @Override
    protected Object[][] getReflectionCompareSkips(FacesSharableRegistry reg) {
        Object[][] skips = super.getReflectionCompareSkips(reg);
        skips = XspTestUtil.concat(skips, getCompareSkips_AbstractTreeRenderer());
        return skips;
    }

    @Override
    protected Object[][] getSkippedNoGetter() {
        Object[][] skips = super.getSkippedNoGetter();
        skips = XspTestUtil.concat(skips, ExtlibRegisteredSerializationTest.extlibSkipNoGetter);
        return skips;
    }
    @Override
    protected Object[][] getSkipProperty(FacesSharableRegistry reg) {
        Object[][] skips = super.getSkipProperty(reg);
        skips = XspTestUtil.concat(skips, ExtlibRegisteredSerializationTest.extlibAllowNoComplexForProperty);
        return skips;
    }
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// prevent early JavaScriptUtil initialization with wrong JSContext type.
		WrapperXSP.register(DesignerRuntime.getJSContext());
	}

}
