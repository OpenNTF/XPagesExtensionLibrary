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

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xsp.extlib.designer.junit.util.TestDesignerProject;

import com.ibm.commons.iloader.node.IAttribute;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ICollection;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.IObjectCollection;
import com.ibm.commons.iloader.node.IValueCollection;
import com.ibm.commons.iloader.node.NodeException;
import com.ibm.commons.util.StringUtil;
import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLException;
import com.ibm.designer.domino.xsp.api.util.XPagesDOMUtil;
import com.ibm.designer.domino.xsp.editpart.ExtendedDesignTimeJSContext;
import com.ibm.designer.domino.xsp.internal.loaders.XFacesDOMLoader;
import com.ibm.designer.domino.xsp.internal.loaders.XFacesMultiObjDOMLoader;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.macro.MacroProcessor;
import com.ibm.jscript.types.FBSCustomObject;
import com.ibm.jscript.types.FBSNumber;
import com.ibm.jscript.types.FBSObject;
import com.ibm.jscript.types.FBSUtility;
import com.ibm.jscript.types.FBSValue;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;

public abstract class AbstractVisualizationTest extends TestCase {
	
	boolean debug = false;
	
	public static String EXT_LIB_NAMESPACE_URI = "http://www.ibm.com/xsp/coreex";
	public static String EXT_LIB_PREFIX = "xe";
	public static String LINE_DELIMITER = "\r\n";
	public static final String START_SCRIPLET_TAG = "<%";
	public static final String START_SCRIPLET_VALUE_TAG = "<%=";
	public static final String END_SCRIPLET_TAG = "%>";
	
	public AbstractCommonControlVisualizer _visualizationFactory;
	public Node _nodeToVisualize;
	
	/**
	 * This method should return whether or not the markup of the visualization should be static
	 * @return boolean indicating if the markup of the control is static. 
	 */
	public abstract boolean isRenderMarkupStatic();
	
	/**
	 * This method should return the xsp-markup that is the expected result of calling 
	 * getFullXSPMarkupForControl() of the visualization factory. 
	 * @return String representing the full xsp-markup expected to be returned by the visualization factory.
	 */
	public abstract String getExpectedFullXSPMarkup();
	
	/**
	 * This method should return the xsp-markup that is the expected result of calling 
	 * getFullXSPMarkupForControl() of the visualization factory and then putting that markup through
	 * the javascript preprocessor. Basically this should return the source that is actually rendered
	 * by the XPage.  
	 * @return String representing the processed full xsp-markup expected to be returned by the visualization factory.
	 */
	public abstract String getExpectedProcessedFullXSPMarkup();
	
	/**
	 * This method should return the xsp-markup that is the expected result of calling 
	 * getXSPMarkupForControl() of the visualization factory. 
	 * @return String representing the xsp-markup expected to be returned by the visualization factory.
	 */
	public abstract String getExpectedXSPMarkup();
	
	/**
	 * This method is used to determine what tag the factory is visualizing. This is used to create the 
	 * node passed into the visualization factory when calling getFullXSPMarkup and getXSPMarkup.
	 * @return the tagName of the tag this factory is visualizing.
	 */
	public abstract String getTagName();
	
	/**
	 * This method is used to determine what tag the factory is visualizing. This is used to create the 
	 * node passed into the visualization factory when calling getFullXSPMarkup and getXSPMarkup.
	 * @return the namespaceURI of the tag this factory is visualizing.
	 */
	public abstract String getNamespaceURI();
	
	
	
	//These are the objects we use to create the visualizations and run the macro processor.
	public FacesSharableRegistry _facesRegistry;
	public XFacesMultiObjDOMLoader _loader;
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//set up the variables we need for the tests
		_facesRegistry = StandardRegistryMaintainer.getStandardRegistry();
		_loader = new XFacesMultiObjDOMLoader(new TestDesignerProject(_facesRegistry));
		String namespaceURI = getNamespaceURI();
		String tagName = getTagName();
		String prefix = getPrefixForNamespaceURI(namespaceURI);
		String docContents = getBlankXPageContents();
        if(StringUtil.isNotEmpty(docContents)){
			Document doc = createDocument(docContents);
	        if(null != doc){
	        	if(StringUtil.isNotEmpty(namespaceURI) && StringUtil.isNotEmpty(prefix)){
	        		_nodeToVisualize = doc.createElementNS(namespaceURI, XPagesDOMUtil.createQualifiedTagName(prefix, tagName));
	        	}
	        }
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		_visualizationFactory = null;
	}

	/**
	 * This method will test if the isStaticMarkup() method of the visualization factory returns the correct result. 
	 * Child classes must implement isRenderMarkupStatic() which is used to determine if the markup generated by 
	 * visualization factory should be static or dynamic.
	 */
	public void testIsStaticMarkup() {
		boolean isStaticInVisualizer = _visualizationFactory.isStaticMarkup();
		boolean shouldBeStatic = isRenderMarkupStatic();
		if(!shouldBeStatic == isStaticInVisualizer){
			StringBuilder message = new StringBuilder();
			message.append("FAIL - Render markup for visualization is ");
			if(!shouldBeStatic){
				message.append("not ");
			}
			message.append("static but the visualizer returned that it is ");
			if(!isStaticInVisualizer){
				message.append("not ");
			}
			message.append("static");
			fail(message.toString());
		}
		else{
			StringBuilder message = new StringBuilder();
			message.append("SUCCESS - Render markup for visualization is ");
			if(!shouldBeStatic){
				message.append("not ");
			}
			message.append("static and the visualizer returned that it is ");
			if(!isStaticInVisualizer){
				message.append("not ");
			}
			message.append("static");
			System.out.println(message.toString());
		}
	}
	
	/**
	 * This method will test if the control/tag that the visualization factory is providing a 
	 * visualization for exists in the registry
	 */
	public void testDoesControlExist() {
		FacesDefinition def = _facesRegistry.findDef(getNamespaceURI(),getTagName());
		if(null == def){
			fail("Fail - the tag that this factory is trying to visualize does not exist");
		}
	}
	
	/**
	 * This method test that the markup returned by getXSPMarkupForControl in the control visualization factory matches
	 * the expected markup from getExpectedXSPMarkup
	 * @throws Exception
	 */
	public void testGetXSPMarkupForControl() throws Exception {
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				String returnedMarkup = _visualizationFactory.getXSPMarkupForControl(_nodeToVisualize, null, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					String expectedMarkup = getExpectedXSPMarkup();
		        	if(StringUtil.isNotEmpty(expectedMarkup)){
		        		boolean equal = compareMarkup(returnedMarkup, expectedMarkup);
		        		if(!equal){
			        		fail("FAIL - returned markup from getXSPMarkupForControl did not match expected markup from getExpectedXSPMarkup");
			        	}
			        	else{
			        		System.out.println("SUCCESS - returned markup from getXSPMarkupForControl matched expected markup from getExpectedXSPMarkup");
			        		return;
			        	}
		        	}
		        	else{
		        		fail("FAIL - markup returned by getExpectedXSPMarkup was empty or null");
		        	}
				}
				else{
					fail("FAIL - markup returned by getXSPMarkupForControl was empty or null");
				}
			}
		}
		fail("FAIL - unknown error occurred while executing testGetXSPMarkupForControl");
	}

	/**
	 * This method test that the markup returned by getFullXSPMarkupForControl in the control visualization factory matches
	 * the expected markup from getExpectedFullXSPMarkup
	 * @throws Exception
	 */
	public void testGetFullXSPMarkupForControl() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					String expectedMarkup = getExpectedFullXSPMarkup();
		        	if(StringUtil.isNotEmpty(expectedMarkup)){
		        		boolean equal = compareMarkup(returnedMarkup, expectedMarkup);
		        		if(!equal){
			        		fail("FAIL - returned markup from getFullXSPMarkupForControl did not match expected markup from getExpectedFullXSPMarkup");
			        	}
			        	else{
			        		System.out.println("SUCCESS - returned markup from getFullXSPMarkupForControl matched expected markup from getExpectedFullXSPMarkup");
			        		return;
			        	}
		        	}
		        	else{
		        		fail("FAIL - markup returned by getExpectedFullXSPMarkup was empty or null");
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
	 * This method test that the processed markup returned by getFullXSPMarkupForControl in the control visualization factory matches
	 * the expected markup from getExpectedProcessedFullXSPMarkup
	 * @throws Exception
	 */
	public void testGetProcessedFullXSPMarkupForControl() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
				String returnedMarkup = _visualizationFactory.getFullXSPMarkupForControl(_nodeToVisualize, _facesRegistry);
				if(StringUtil.isNotEmpty(returnedMarkup)){
					//run the markup through the preprocessor
					returnedMarkup = processMarkup(returnedMarkup);
					String expectedMarkup = getExpectedProcessedFullXSPMarkup();
		        	if(StringUtil.isNotEmpty(expectedMarkup)){
		        		boolean equal = compareProcessedMarkup(returnedMarkup, expectedMarkup);
		        		if(!equal){
			        		fail("FAIL - returned processed markup from getFullXSPMarkupForControl did not match expected markup from getExpectedProcessedFullXSPMarkup");
			        	}
			        	else{
			        		System.out.println("SUCCESS - returned processed markup from getFullXSPMarkupForControl matched expected markup from getExpectedProcessedFullXSPMarkup");
			        		return;
			        	}
		        	}
		        	else{
		        		fail("FAIL - markup returned by getExpectedProcessedFullXSPMarkup was empty or null");
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
	public void testIsFullXSPMarkupForControlValid() throws Exception{
		if(null == _nodeToVisualize){
			fail("Fail - Failed to generate the DOM node for the visualization factory to provide render-markup for");
		}
		else{
			if(null == _visualizationFactory){
				fail("Fail - Failed to create visualization factory");
			}
			else{
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
	 * Helper method to check if the given markup is valid. The markup is run through the macro processor 
	 * and the results returned and checked for validity. 
	 * @param markup
	 * @return true if the generated source is valid, false otherwise. 
	 */
	public boolean isReturnedMarkupValid(String markup){
		//process the markup
		 String processedMarkup = processMarkup(markup);
         //Check to see if the markup brought back by the preprocessor is valid
         boolean valid = false;
         //if we have processed markup
         if(StringUtil.isNotEmpty(processedMarkup)){
             //check to see if it is valid
             valid = isMarkupValid(processedMarkup);
         }
         return valid;
	}
	
	/**
	 * Private method to put the returned xsp markup through the javascript preprocessor. 
	 * @param markup
	 * @return the processed markup
	 */
	public String processMarkup(String markup){
		MacroProcessor macroProc = new MacroProcessor(ExtendedDesignTimeJSContext.getJSContext());
        String processedMarkup = "";
        try{
            FBSObject _this = new XPagesObject(_loader,_nodeToVisualize);
            macroProc.setThis(_this);
            processedMarkup = macroProc.process(markup);            
        }
        catch(Exception e){
        	//do nothing
        }
        return processedMarkup;
	}
	
	/**
     * We try to create a doc with the source. If we can then its valid. If we can't then its not valid
     * and return false. Return true otherwise. 
     * @param source
     * @return whether or not we can successfully create a document with the given markup.
     */
	public boolean isMarkupValid(String source){
        if(StringUtil.isNotEmpty(source)){
            //create a document from the source to pass into the xsp Validator
            Document xspDoc = null;
            try{
                xspDoc = DOMUtil.createDocument(source);
                if(null != xspDoc){
                    //if we managed to create the doc then the markup was valid.
                    return true;
                }
            }
            catch (XMLException xmlEx){
                return false;
            }
        }
        return false;
    }
	
	/**
	 * This is a helper method that will return the standard opening xp:view tag that is used by most of the
	 * visualization factories
	 * @return an opening xp:view tag with standard configuration
	 */
	public String getDefaultXPageHeader(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("<xp:view xmlns:xp=\"http://www.ibm.com/xsp/core\">");
		strBuilder.append(LINE_DELIMITER);
		return strBuilder.toString();
	}
	
	/**
	 * This is a helper method that will return the standard closing xp:view tag that is used by all of the
	 * visualization factories to this point.
	 * @return a standard closing xp:view tag
	 */
	public String getDefaultXPageFooter(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(LINE_DELIMITER);
		strBuilder.append("</xp:view>");
		strBuilder.append(LINE_DELIMITER);
		return strBuilder.toString();
	}
	
	/**
	 * Simple helper method to return the default contents of an XPage.
	 * We use this to generate a document we then use to create a child node
	 * to pass into the visualizer. This method will give us a property configured
	 * view node with the namespaces already configured. 
	 * @return
	 */
	public String getBlankXPageContents(){
		return getDefaultXPageHeader() + getDefaultXPageFooter();
	}
	
	/**
	 * Private method to return a matching prefix for a given namespaceURI.
	 * @param namespaceURI
	 * @return the matching prefix for that given namespaceURI.
	 */
	public String getPrefixForNamespaceURI(String namespaceURI){
		if(StringUtil.equals(namespaceURI, EXT_LIB_NAMESPACE_URI)){
			return EXT_LIB_PREFIX;
		}
		return null;
	}
	
	/**
	 * This method will compare the markup returned by the visualization factory to the 
	 * markup expected by the test case.
	 * @param returnedMarkup - the markup returned by the visualization factory
	 * @param expectedMarkup - the markup we expect to have been returned by the visualization factory
	 * @return true if the markup matches, false otherwise.
	 */
	public boolean compareMarkup(String returnedMarkup, String expectedMarkup){
		if(StringUtil.isNotEmpty(returnedMarkup) && StringUtil.isNotEmpty(expectedMarkup)){
			//handle the standard case first where the strings match out of the box.
			boolean equal = StringUtil.equals(returnedMarkup, expectedMarkup);
			if(equal){
				pringDebug(returnedMarkup, expectedMarkup, "compareMarkup unchanged markup before stipping line delims etc..");
				return true;
			}
			else{
				//if the visualization is static and does contain any scriplet tags, then line delimiters have no 
				//effect on whether or not the markup will render. So stip out line delims and try the compare again.
				//We use a case sensitive string compare on the off chance case is important in any labels used.
				boolean returnedMarkupContainsScript = containsScript(returnedMarkup);
				boolean expectedMarkupContainsScript = containsScript(expectedMarkup);
				if((returnedMarkupContainsScript && ! expectedMarkupContainsScript) ||
					(!returnedMarkupContainsScript && expectedMarkupContainsScript)){
					pringDebug(returnedMarkup, expectedMarkup, "compareMarkup one with script one without");
					//if one contains script and the other doesn't, then they are not the same.
					return false;
				}
				else if(!returnedMarkupContainsScript && !expectedMarkupContainsScript){
					//no script in either markup blobs. So strip out line delims. Leave spaces and tabs in case they are important. 
					returnedMarkup = stripMarkup(returnedMarkup);
					expectedMarkup = stripMarkup(expectedMarkup);
					pringDebug(returnedMarkup, expectedMarkup, "compareMarkup no script");
					return StringUtil.equals(returnedMarkup, expectedMarkup);
				}
				else{
					//both contain script, so we have to do a bit more work to figure out if they are equal. 
					//strip out line delims only where they are definitely not needed. (i.e. ouside of the script tags.)
					returnedMarkup = stripMarkupContainingScript(returnedMarkup);
					expectedMarkup = stripMarkupContainingScript(expectedMarkup);
					pringDebug(returnedMarkup, expectedMarkup, "compareMarkup with script");
					return StringUtil.equals(returnedMarkup, expectedMarkup);
				}
			}
		}
		return false;
	}
	
	/**
	 * This method will compare the markup returned by the visualization factory to the 
	 * markup expected by the test case.
	 * @param returnedMarkup - the processed markup returned by the visualization factory
	 * @param expectedMarkup - the processed markup we expect to have been returned by the visualization factory
	 * @return true if the markup matches, false otherwise.
	 */
	public boolean compareProcessedMarkup(String returnedMarkup, String expectedMarkup){
		if(StringUtil.isNotEmpty(returnedMarkup) && StringUtil.isNotEmpty(expectedMarkup)){
			//handle the standard case first where the strings match out of the box.
			boolean equal = StringUtil.equals(returnedMarkup, expectedMarkup);
			if(equal){
				return true;
			}
			else{
				//there should be no script in either markup blobs. So strip out line delims. 
				//Leave spaces and tabs in case they are important. 
				returnedMarkup = stripMarkup(returnedMarkup);
				expectedMarkup = stripMarkup(expectedMarkup);
				pringDebug(returnedMarkup, expectedMarkup, "compareProcessedMarkup");
				return StringUtil.equals(returnedMarkup, expectedMarkup);
			}
		}
		return false;
	}
	
	/**
	 * Helper method to create a Document from the supplied contents
	 * @param documentContents - the contents of the document
	 * @return a new Document containing the provided contents
	 * @throws Exception
	 */
	public Document createDocument(String documentContents) throws Exception{
		if(StringUtil.isNotEmpty(documentContents)){
			return DOMUtil.createDocument(documentContents);
		}
		return null;
	}
	
	/**
	 * Helper method to check to see if the provided markup contains any scriplet tags. 
	 * @param markup
	 * @return true if the markup contains any script tags, false otherwise. 
	 */
	public boolean containsScript(String markup){
		if(StringUtil.isNotEmpty(markup)){
			if(markup.indexOf(START_SCRIPLET_VALUE_TAG)!=-1){
				return true;
			}
			else if(markup.indexOf(START_SCRIPLET_TAG)!= -1){
				return true;
			}
			else if(markup.indexOf(END_SCRIPLET_TAG)!= -1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Helper method to remove all line delimiters from a string
	 * @param markup - the string to remove the line delimiters from 
	 * @return a string containing the markup without any line delims
	 */
	public String stripMarkup(String markup){
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i<markup.length(); i++){
			char tmpChr = markup.charAt(i);
			if(tmpChr!='\n' && tmpChr!='\r' ) {
	        	strBuilder.append(tmpChr);
			}
		}
        return strBuilder.toString();
	}
	
	/**
	 * Helper method to remove all line delimiters from a string
	 * @param markup - the string to remove the line delimiters from 
	 * @return a string containing the markup without any line delims
	 */
	public String stripMarkupContainingScript(String markup){
		int openScriptletPos = markup.indexOf(START_SCRIPLET_TAG);
		int endScriptletPos = markup.indexOf(END_SCRIPLET_TAG);
		if(openScriptletPos!=1 && endScriptletPos!=-1 && openScriptletPos<endScriptletPos){
			endScriptletPos = endScriptletPos+END_SCRIPLET_TAG.length();
			String start = markup.substring(0,openScriptletPos);
			String script = markup.substring(openScriptletPos,endScriptletPos);
			String end = markup.substring(endScriptletPos);
			return stripMarkup(start) + script + stripMarkupContainingScript(end);
		}
		else{
			return markup;
		}
	}
	
	/**
	 *Copied from CCCustomRenderingElementEditPart. Needed for makup processing
	 */
	public static class XPagesObject extends FBSCustomObject {
        XFacesDOMLoader     loader;
        IClassDef           classDef;
        Object              object;
        XPagesObject(XFacesDOMLoader loader, Object object) {
            super(ExtendedDesignTimeJSContext.getJSContext());
            this.loader = loader;
            try {
                this.classDef = object!=null ? loader.getClassOf(object) : null;
                this.object = object;
            } catch(NodeException ex) {
                // Just ignore - classdef will be null
            }
        }
        public boolean hasProperty(String name) {
            if(classDef!=null) {
                IMember m = classDef.getMember(name);
                if(m instanceof IAttribute) {
                    return true;
                }
                if(m instanceof ICollection) {
                    return true;
                }
            }
            return super.hasProperty(name);
        }
        public FBSValue get(String name) throws InterpretException {
            try {
                if(classDef!=null) {
                    IMember m = classDef.getMember(name);
                    if(m instanceof IAttribute) {
                        try {
                        	if(object instanceof Element){
                        		Element elem = (Element)object;
                        		IAttribute attr = (IAttribute)m;
                        		String attrVal = XPagesDOMUtil.getAttribute(elem, attr.getName());
                        		return FBSUtility.wrap(ExtendedDesignTimeJSContext.getJSContext(),attrVal);
                        	}
                        } catch(Exception ex) {
                            // Ignore, and let fall to a property access error
                        }
                    }
                    if(m instanceof ICollection) {
                        ICollection col = (ICollection)m;
                        if(col.getType()==ICollection.TYPE_OBJECT) {
                            IObjectCollection c = loader.getObjectCollection(object, col);
                            return new XPagesObjectCollection(loader,object,c);
                        } else {
                            IValueCollection c = loader.getValueCollection(object, col);
                            return new XPagesValueCollection(loader,object,c);
                        }
                    }
                }
            } catch(NodeException ex) {
                // Just ignore
            }
            return super.get(name);
        }
        public boolean canPut(String name) {
            return false; // read only
        }
    }
	
	/**
	 *Copied from CCCustomRenderingElementEditPart. Needed for makup processing
	 */ 
	public static class XPagesValueCollection extends FBSCustomObject {
        IValueCollection    collection;
        XPagesValueCollection(XFacesDOMLoader loader, Object object, IValueCollection collection) {
            super(ExtendedDesignTimeJSContext.getJSContext());
            this.collection = collection;
        }
        public boolean hasProperty(String name) {
            if(StringUtil.equals(name, "length")) { // $NON-NLS-1$
                return true;
            }
            return super.hasProperty(name);
        }
        public FBSValue get(String name) throws InterpretException {
            if(StringUtil.equals(name, "length")) { // $NON-NLS-1$
                return FBSNumber.get(getArrayLength());
            }
            return super.get(name);
        }
        public FBSValue get(int index) throws InterpretException {
            if(index>=0 && index<collection.size()) {
                return getArrayValue(index);
            }
            return super.get(index);
        }
        public boolean canPut(String name) {
            return false; // read only
        }
        public boolean isArray() {
            return true;
        }
        public int getArrayLength() {
            return collection.size();
        }
        public FBSValue getArrayValue(int index) throws InterpretException {
            return FBSUtility.wrap(ExtendedDesignTimeJSContext.getJSContext(),collection.get(index));
        }
    }
    
    /**
	 *Copied from CCCustomRenderingElementEditPart. Needed for makup processing
	 */
	public static class XPagesObjectCollection extends FBSCustomObject {
        XFacesDOMLoader     loader;
        IObjectCollection   collection;
        XPagesObjectCollection(XFacesDOMLoader loader, Object object, IObjectCollection collection) {
            super(ExtendedDesignTimeJSContext.getJSContext());
            this.loader = loader;
            this.collection = collection;
        }
        public boolean hasProperty(String name) {
            if(StringUtil.equals(name, "length")) { // $NON-NLS-1$
                return true;
            }
            return super.hasProperty(name);
        }
        public FBSValue get(String name) throws InterpretException {
            if(StringUtil.equals(name, "length")) { // $NON-NLS-1$
                return FBSNumber.get(getArrayLength());
            }
            return super.get(name);
        }
        public boolean hasIndexedProperty(){
            return true;
        }
        public FBSValue get(int index) throws InterpretException {
            if(index>=0 && index<collection.size()) {
                return getArrayValue(index);
            }
            return super.get(index);
        }
        public boolean canPut(String name) {
            return false; // read only
        }
        public boolean isArray() {
            return true;
        }
        public int getArrayLength() {
            return collection.size();
        }
        public FBSValue getArrayValue(int index) throws InterpretException {
            Object o = collection.get(index);
            return new XPagesObject(loader,o);
        }
    }
    
    /**
     * @return true if you want to print debug statements for a given test. 
     */
    public boolean getDebug(){
    	return debug;
    }
    
    /**
     * Helper method to show the markup that is being compared
     * @param returnedMarkup - markup to compare
     * @param expectedMarkup - markup to compare
     * @param label - descrition to appear in header of output
     */
    public void pringDebug(String returnedMarkup, String expectedMarkup, String label){
    	if(getDebug()){
	    	System.out.println();
	    	System.out.println("================= "+label+" =================");
			System.out.println("Returned Markup = ");
			System.out.println(returnedMarkup);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			System.out.println("Expected Markup = ");
			System.out.println(expectedMarkup);
			System.out.println("===============================================================================");
			System.out.println();
    	}
    }
	
}
