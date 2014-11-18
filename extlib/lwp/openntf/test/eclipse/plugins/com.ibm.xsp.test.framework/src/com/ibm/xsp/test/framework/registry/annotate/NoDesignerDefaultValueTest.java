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
 * Date: 23 Feb 2011
 * NoStringDefaultsTest.java
 */

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.registry.annotate.DesignerExtensionSubsetAnnotater;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class NoDesignerDefaultValueTest extends AbstractXspTest {
	
	@Override
	public String getDescription() {
		return "that designer default-values should not usually be present";
	}
	public void testPropertyDesignerExtensionDefaultValue() throws Exception {
		FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(
				this, new DesignerExtensionPropertyDefaultValueAnnotater());
		
		List<Object[]> inTestSkips = getSkips();
		
		String fails = "";
		for (FacesDefinition def : TestProject.getLibDefinitions(reg, this)) {
			Collection<String> propNames = def.isTag()?  def.getPropertyNames() : def.getDefinedPropertyNames();
			for (FacesProperty prop : RegistryUtil.getProperties(def, propNames)) {
				String designerDefaultValue = (String) prop.getExtension("default-value");
				if( null != designerDefaultValue ){
					
	                int skipIndex = isSkipped(inTestSkips, def, prop, designerDefaultValue); 
	                if( -1 == skipIndex ){
	                    fails += ref(def, prop)+ " <designer-extension><default-value>"
	                        + designerDefaultValue + "</>, likely to break theme property values\n";
	                    continue;
	                }else{ // skipped
	                    Object[] skip = inTestSkips.get(skipIndex);
	                    String skipExpectedDefault = (String) skip[2];
	                    if( !skipExpectedDefault.equals(designerDefaultValue) ){
	                        fails += ref(def, prop)+ " actual <designer default-value>"
	                            + designerDefaultValue + "< != skip value >"+skipExpectedDefault+"<\n";
	                    }
	                }
				}
			}
		}
        for (Object[] skip : inTestSkips) {
			if( skip.length == 3 ){
				fails += "unused skip "+skip[1]+"."+skip[0]+" <designer-extension><default-value>"+skip[2]+"</>\n";
			}
		}
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(null, this, "testPropertyDesignerExtensionDefaultValue"));
		if( fails.length() > 0 ){
			fail(XspTestUtil.getMultilineFailMessage(fails));
		}
	}
	private String ref(FacesDefinition def, FacesProperty prop) {
		return def.getFile().getFilePath()+" "+ParseUtil.getTagRef(def)+" "+prop.getName();
	}
	
	private int isSkipped(List<Object[]> inTestSkips, FacesDefinition def, FacesProperty prop,
			String designerDefaultValue) {
		
		int skipIndex = -1;
		String propName = prop.getName();
		Class <?> defClass = def.getJavaClass();
		String tagRef = def.isTag() ? 
				(def.getFirstDefaultPrefix() + ":" + def.getTagName()) 
				: (def.getFirstDefaultPrefix() + "-" + def.getId());
		int i = 0;
		for (Object[] skip : inTestSkips) {
			if( skip[0].equals(propName)){
				if( skip[1].equals(defClass) || skip[1].equals(tagRef) ){
					skipIndex = i;
					break;
				}
			}
			i++;
		}
		if( -1 != skipIndex ){
			// mark the skip as used.
			Object[] skip = inTestSkips.get(skipIndex);
			if( skip.length > 3 ){
				throw new RuntimeException("Skip already used");
			}
			skip = XspTestUtil.concat(skip, new Object[]{true});
			inTestSkips.set(skipIndex, skip);
		}
		return skipIndex;
	}
	/**
	 * propName, classOrDefTagRef, actualValue, skipUsed
	 * 
	 * @return
	 */
	protected List<Object[]> getSkips() {
		List<Object[]> skips = new ArrayList<Object[]>();
		return skips;
	}
	private class DesignerExtensionPropertyDefaultValueAnnotater extends DesignerExtensionSubsetAnnotater{
		@Override
		protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
			return (parsed instanceof FacesProperty);
		}
		@Override
		protected String[] createExtNameArr() {
			String[] arr = new String[]{
				"default-value",
			};
			return arr;
		}
		
	}
}
