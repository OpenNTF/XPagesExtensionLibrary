/*
 * © Copyright IBM Corp. 2010, 2015
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
package xsp.extlib.test.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ibm.xsp.component.UIDataIterator;
import com.ibm.xsp.extlib.component.picker.data.CollectionValuePickerData;
import com.ibm.xsp.extlib.component.picker.data.MapValuePickerData;
import com.ibm.xsp.extlib.tree.complex.LoginTreeNode;
import com.ibm.xsp.extlib.tree.complex.UserTreeNode;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.serialize.BaseRegisteredSerializationTest;

/**
 * Note, this is unusual in that it does not use reflection to compare the
 * UIComponents & StateHolders in the control tree, it only looks at the getters
 * corresponding to xsp-config property declarations. See
 * {@link ExtlibReflectionSerializeTest} for comparing using reflection.
 * 
 * @author mkehoe@ie.ibm.com
 */
public class ExtlibRegisteredSerializationTest extends BaseRegisteredSerializationTest {
    public static Object[][] extlibAllowNoComplexForProperty = new Object[][]{
			// this doesn't serialize the isRepeatControls property - it's transient, only used at page load time
			new Object[]{UIDataIterator.class, "repeatControls", boolean.class},
			// this doesn't serialize the isRemoveRepeat property - it's transient, only used at page load time
			new Object[]{UIDataIterator.class, "removeRepeat", boolean.class},
    		getPropertySkip_MapValuePickerData(),
    		getPropertySkip_CollectionValuePickerData(),
    };
	
    public static Object[][] extlibSkipNoGetter = new Object[][]{
    		// skip due to 
    		// java.lang.NoClassDefFoundError: com/ibm/domino/napi/NException
    		//  at com.ibm.xsp.extlib.tree.complex.UserTreeNode.isRendered(UserTreeNode.java:51)
    		new Object[]{UserTreeNode.class, "rendered"},
    		new Object[]{LoginTreeNode.class, "rendered"},
    };
    private Object[][] allNoGetter;
    
    @Override
    protected Object[][] getSkippedNoGetter() {
        if (allNoGetter == null) {
            allNoGetter = XspTestUtil.concat(super.getSkippedNoGetter(),
                    extlibSkipNoGetter);
        }
        return allNoGetter;
    }
    @Override
	protected Object[][] getSkipProperty(FacesSharableRegistry reg) {
        Object[][] arr = super.getSkipProperty(reg);
        arr = XspTestUtil.concat(arr, extlibAllowNoComplexForProperty);
        return arr;
    }
	@Override
	protected int getDebugIndex() {
		return -1;
	}
	private static Object[] getPropertySkip_MapValuePickerData(){
		LinkedHashMap<String, String> orderedMap = new LinkedHashMap<String, String>();
		orderedMap.put("Cat", "CAT");
		orderedMap.put("Dog", "DOG");
		orderedMap.put("Fish", "FISH");
		return new Object[]{MapValuePickerData.class, "options", Map.class, orderedMap};
	}
	private static Object[] getPropertySkip_CollectionValuePickerData(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("Cat");
		list.add("Dog");
		list.add("Fish");
		return new Object[]{CollectionValuePickerData.class, "collection", Collection.class, list};
	}
}
