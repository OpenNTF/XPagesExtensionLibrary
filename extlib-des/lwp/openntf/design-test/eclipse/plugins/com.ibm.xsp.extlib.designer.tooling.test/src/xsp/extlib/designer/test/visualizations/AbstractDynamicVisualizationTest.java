/*
 * Copyright IBM Corp. 2011
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
package xsp.extlib.designer.test.visualizations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Element;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.xsp.registry.FacesDefinition;

public abstract class AbstractDynamicVisualizationTest extends AbstractVisualizationTest {
		
	/**
	 * This method should return the processed xsp-markup that is the expected result of calling 
	 * getXSPMarkupForControl() of the visualization factory after the attributes from
	 * getAttributesToSet() have been set on the node to visualize. 
	 * @return String representing the processed xsp-markup expected to be returned by the visualization factory.
	 */
	public abstract String getExpectedProcessedFullXSPMarkupAfterSettingAttributes();
	
	/**
	 * This method should return the processed xsp-markup that is the expected result of calling 
	 * getXSPMarkupForControl() of the visualization factory after the attributes from
	 * getAttributesToSet() have been set on the node to visualize. 
	 * @return String representing the processed xsp-markup expected to be returned by the visualization factory.
	 */
	public abstract String getExpectedProcessedFullXSPMarkupAfterSettingComputedAttributes();
	
	/**
	 * This method is used to determine what attributes the test should set on the
	 * node to visualize to test the dynamic nature of the visualization.
	 * This method should return a HashMap with the attribute names as the keys and
	 * the values to set into the attributes as the values of the HashMap.
	 */
	public abstract HashMap<String,String> getAttributesToSet();
	
	/**
	 * This method is used to determine what computed attributes the test should set on the
	 * node to visualize to test the dynamic nature of the visualization.
	 * This method should return a HashMap with the attribute names as the keys and
	 * the values to set into the attributes as the values of the HashMap.
	 */
	public abstract HashMap<String,String> getComputedAttributesToSet();
	
	public static final String TEST_JAVASCRIPT = "<![CDATA[#{javascript:var a = \"aString\";\r\nvar b = \"anotherString\";\r\nreturn a + b;}]]>";
	
	/**
	 * This method test that the processed markup returned by getFullXSPMarkupForControl in the control visualization factory matches
	 * the expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes
	 * @throws Exception
	 */
	public void testGetProcessedFullXSPMarkupForControlAfterAttributesSet() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				HashMap<String,String> attributesToSet = getAttributesToSet();
				Set<String> attributeNames = attributesToSet.keySet();
				Iterator<String> attrIter = attributeNames.iterator();
				while(attrIter.hasNext()){
					String attributeName = attrIter.next();
					if(StringUtil.isNotEmpty(attributeName)){
						String attributeValue = attributesToSet.get(attributeName);
						if(StringUtil.isEmpty(attributeValue)){
							attributeValue = "";
						}
						XPagesDOMUtil.setAttribute((Element)_nodeToVisualize, attributeName, attributeValue);
					}
				}
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					//run the markup through the preprocessor
					returnedMarkup = processMarkup(returnedMarkup);
					String expectedMarkup = getExpectedProcessedFullXSPMarkupAfterSettingAttributes();
		        	if(StringUtil.isNotEmpty(expectedMarkup)){
		        		boolean equal = compareProcessedMarkup(returnedMarkup, expectedMarkup);
		        		if(!equal){
			        		fail("FAIL - returned processed markup from getFullXSPMarkupForControl did not match expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes");
			        	}
			        	else{
			        		System.out.println("SUCCESS - returned processed markup from getFullXSPMarkupForControl matched expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes");
			        		return;
			        	}
		        	}
		        	else{
		        		fail("FAIL - markup returned by getExpectedProcessedFullXSPMarkupAfterSettingAttributes was empty or null");
		        	}
				}
				else{
					fail("FAIL - markup returned by getFullXSPMarkupForControl was empty or null");
				}
			}
		}
		fail("FAIL - unknown error occurred while executing testGetProcessedFullXSPMarkupForControl");
	}
	
	/**
	 * This method test that the processed markup returned by getFullXSPMarkupForControl in the control visualization factory matches
	 * the expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes
	 * @throws Exception
	 */
	public void testGetProcessedFullXSPMarkupForControlAfterComputedAttributesSet() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				HashMap<String,String> attributesToSet = getComputedAttributesToSet();
				Set<String> attributeNames = attributesToSet.keySet();
				Iterator<String> attrIter = attributeNames.iterator();
				while(attrIter.hasNext()){
					String attributeName = attrIter.next();
					if(StringUtil.isNotEmpty(attributeName)){
						String attributeValue = attributesToSet.get(attributeName);
						if(StringUtil.isEmpty(attributeValue)){
							attributeValue = "";
						}
						XPagesDOMUtil.setAttribute((Element)_nodeToVisualize, attributeName, attributeValue);
					}
				}
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					//run the markup through the preprocessor
					returnedMarkup = processMarkup(returnedMarkup);
					String expectedMarkup = getExpectedProcessedFullXSPMarkupAfterSettingComputedAttributes();
		        	if(StringUtil.isNotEmpty(expectedMarkup)){
		        		boolean equal = compareProcessedMarkup(returnedMarkup, expectedMarkup);
		        		if(!equal){
			        		fail("FAIL - returned processed markup from getFullXSPMarkupForControl did not match expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes");
			        	}
			        	else{
			        		System.out.println("SUCCESS - returned processed markup from getFullXSPMarkupForControl matched expected markup from getExpectedProcessedFullXSPMarkupAfterSettingAttributes");
			        		return;
			        	}
		        	}
		        	else{
		        		fail("FAIL - markup returned by getExpectedProcessedFullXSPMarkupAfterSettingComputedAttributes was empty or null");
		        	}
				}
				else{
					fail("FAIL - markup returned by getFullXSPMarkupForControl was empty or null");
				}
			}
		}
		fail("FAIL - unknown error occurred while executing testGetProcessedFullXSPMarkupForControl");
	}
	
	/**
	 * This method test that the processed markup returned by getFullXSPMarkupForControl in the control visualization is valid
	 * @throws Exception
	 */
	public void testIsFullXSPMarkupForControlValidAfterAttributesSet() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				HashMap<String,String> attributesToSet = getAttributesToSet();
				Set<String> attributeNames = attributesToSet.keySet();
				Iterator<String> attrIter = attributeNames.iterator();
				while(attrIter.hasNext()){
					String attributeName = attrIter.next();
					if(StringUtil.isNotEmpty(attributeName)){
						String attributeValue = attributesToSet.get(attributeName);
						if(StringUtil.isEmpty(attributeValue)){
							attributeValue = "";
						}
						XPagesDOMUtil.setAttribute((Element)_nodeToVisualize, attributeName, attributeValue);
					}
				}
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					boolean valid = isReturnedMarkupValid(returnedMarkup);
					if(valid){
		        		System.out.println("SUCCESS - Processed markup from getFullXSPMarkupForControl is valid");
			        	return;
		        	}
		        	else{
		        		fail("FAIL - Processed markup returned by getFullXSPMarkupForControl is not valid XSP Markup");
		        	}
				}
				else{
					fail("FAIL - markup returned by getFullXSPMarkupForControl was empty or null");
				}
			}
		}
		fail("FAIL - unknown error occurred while executing testGetFullXSPMarkupForControl");
	}
	
	/**
	 * This method test that the processed markup returned by getFullXSPMarkupForControl in the control visualization is valid
	 * @throws Exception
	 */
	public void testIsFullXSPMarkupForControlValidAfterComputedAttributesSet() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				HashMap<String,String> attributesToSet = getComputedAttributesToSet();
				Set<String> attributeNames = attributesToSet.keySet();
				Iterator<String> attrIter = attributeNames.iterator();
				while(attrIter.hasNext()){
					String attributeName = attrIter.next();
					if(StringUtil.isNotEmpty(attributeName)){
						String attributeValue = attributesToSet.get(attributeName);
						if(StringUtil.isEmpty(attributeValue)){
							attributeValue = "";
						}
						XPagesDOMUtil.setAttribute((Element)_nodeToVisualize, attributeName, attributeValue);
					}
				}
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					boolean valid = isReturnedMarkupValid(returnedMarkup);
					if(valid){
		        		System.out.println("SUCCESS - Processed markup from getFullXSPMarkupForControl is valid");
			        	return;
		        	}
		        	else{
		        		fail("FAIL - Processed markup returned by getFullXSPMarkupForControl is not valid XSP Markup");
		        	}
				}
				else{
					fail("FAIL - markup returned by getFullXSPMarkupForControl was empty or null");
				}
			}
		}
		fail("FAIL - unknown error occurred while executing testGetFullXSPMarkupForControl");
	}
	
	/**
	 * This method will test if the attributes that are being set on the tag/control
	 * to test the dynamic nature of the visualizations exist for the control.
	 */
	public void testDoControlAttributesExist() {
		FacesDefinition def = _facesRegistry.findDef(getNamespaceURI(),getTagName());
		if(null == def){
			fail("Fail - the tag that this factory is trying to visualize does not exist");
		}
		else{
			Collection<String> realPropNames = def.getPropertyNames();
			if(null == realPropNames || realPropNames.isEmpty()){
				fail("Fail - this control returned that it has no attributes");
			}
			HashMap<String,String> attributesToSet = getAttributesToSet();
			if(null == attributesToSet || attributesToSet.isEmpty()){
				fail("Fail - this test returned no attributes to set on the tag");
			}
			Set<String> attributeNamesToSet = attributesToSet.keySet();
			if(null==attributeNamesToSet || attributeNamesToSet.isEmpty()){
				fail("Fail - failed to get the attribute names to set on the node we are trying to visualize");
			}
			Iterator<String> attrNamesIter = attributeNamesToSet.iterator();
			while(attrNamesIter.hasNext()){
				String attributeName = attrNamesIter.next();
				if(!realPropNames.contains(attributeName)){
					fail("Fail - this control does not have the property: " + attributeName);
				}
			}
		}
	}
	
}
