/*
 * © Copyright IBM Corp. 2008, 2013
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
* Date: 3 Jan 2008
* SpellCheckTest.java
*/

package com.ibm.xsp.test.framework.registry.annotate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.page.translator.JavaUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesExtensibleNode;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesPropertyType;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesRenderKitFragment;
import com.ibm.xsp.registry.FacesRendererDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.parse.ElementUtil;
import com.ibm.xsp.registry.parse.ParseUtil;
import com.ibm.xsp.registry.parse.RegistryAnnotater;
import com.ibm.xsp.registry.parse.RegistryAnnotaterInfo;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 3 Jan 2008
 * Unit: SpellCheckTest.java
 */
public class SpellCheckTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "spell check description and display names for hard-coded common misspellings";
    }
    private List<String[]> toCheckFor;
    private List<String[]> asEntireWord;
    private List<String[]> preventExceptInUrls;
    private String[][]s_toCheckFor = new String[][]{
            // whitespace
            {"\n", "should not be present"}, 
            {"\t", "should not be present"}, 
            {"  ", "should not be present"},
            // Doc team said these are 2 words, not one word:
            {"readonly", "should be 'read only'"}, 
            {"checkbox", "should be 'check box'"}, 
            {"Checkbox", "should be 'Check box'"},
            // (9.0.0) a search of the Designer help indicates 
            // that "drop-down" is preferred to "drop down"
            {"drop down","should be 'drop-down'"},
            {"Drop Down","should be 'Drop-Down'"},
            {"Drop down","should be 'Drop-Down'"},
            // Some spelling we were making:
            {"Javascript", "should be JavaScript"}, 
            {"javascript", "should be JavaScript"}, 
            {"Specifes", "should be Specifies"}, 
            {"specifes", "should be specifies"},
            {"Specfies", "should be Specifies"},
            {"specfies", "should be specifies"},
            {"inital", "should be initial"},
            // Grammatical mistake the doc team complained about:
            {"Specified", "should not be at the start of a sentence"},
            // acronyms should be upper case. 
            {"Html", "should be HTML"},
            {"xhtml", "should be XHTML"},
            {"Xhtml", "should be XHTML"},
            {"XHtml", "should be XHTML"},
            {"xml", "should be XML"},
            {"Xml", "should be XML"},
            {"url", "should be URL"},
            {"Url", "should be URL"},
            {"ajax", "should be AJAX"},
            {"Ajax", "should be AJAX"},
            {"json", "should be JSON"},
            {"Json", "should be JSON"},
            {"JSon", "should be JSON"},
            // Updated 2012-05-29, this test 
            // used to say "should be XPATH", but
            // from http://www.w3.org/TR/xpath/
            // XPath is not an acronym, and is spelled XPath
            {"XPATH", "should be XPath"},
            {"Xpath", "should be XPath"},
            {"xpath", "should be XPath"},
            // Product names should be upper case
            {"domino", "should be Domino"},
            {"lotus", "should be Lotus"},
            {"dojo", "should be Dojo"},
            {"java", "should be Java"},
            // for user-friendly-ness
            {"component", "should be control"},
            {"Component", "should be Control"},
            // for USA-english vs International english
            {"'", "should use \" for quotes, and do not use contractions like \"don't\""},
            // use of technical terms:
            {"string","usually should be \"text\""},
            {"String","usually should be \"text\""},
            // it's two words:
            {"datasource", "should be 'data source'"},
            {"Datasource", "should be 'Data Source'"},
    };
    private String[][] s_preventExceptInUrls = new String[][]{
            // for USA-english vs International english
            {"/", "should not use \"/\" to mean and/or, as translators and non-USA english speakers don't understand"},
            {"html", "should be HTML"},
    };
    private String[][] s_asEntireWord = new String[][]{
            // Doc team say ID is upper case
            {"Id", "should be ID"},
            {"id", "should be ID"},
            // acronyms should be upper case. (uri is common within words) 
            {"uri", "should be URI"}, // entire word to prevent fail for "during"
            {"Uri", "should be URI"},
            // spelling mistake
            {"teh", "should be the"},
            {"bat", "should probably be bar"},
            // Product names should be upper case
            {"notes", "should be Notes"}, // entire word to prevent fail for "denotes"
            // miscellaneous
            {"ex", "should be e.g."},
    };
    
    public void testSpellCheck() throws Exception {
        
        String fails = "";
        toCheckFor = getToCheckFor();
        asEntireWord = getAsEntireWord();
        preventExceptInUrls = getPreventExceptInUrls();
        
        FacesSharableRegistry reg = TestProject.createRegistryWithAnnotater(this, new DescriptionDisplayNameAnnotater());
        
        // collect the non-file FacesExtensibleNodes
        List<NodeInfo> nodes = new ArrayList<NodeInfo>();
        for (FacesProject proj : TestProject.getLibProjects(reg, this)) {
            for (FacesLibraryFragment file : proj.getFiles()) {
                for (FacesDefinition def : file.getDefs()) {
                    nodes.add(new NodeInfo(def, nodePath(def, null)));
                    for (String propName : def.getDefinedInlinePropertyNames()) {
                        FacesProperty prop = def.getProperty(propName);
                        nodes.add(new NodeInfo(prop, nodePath(def, prop)));
                        if( prop instanceof FacesPropertyType ){
                            FacesPropertyType propType = (FacesPropertyType) prop;
                            addInnerProps(nodes, propType);
                        }
                    }
                }
                for (String kitId : file.getRenderKitIds()) {
                    FacesRenderKitFragment kitFrag = file.getRenderKitFragment(kitId);
                    nodes.add(new NodeInfo(kitFrag, file.getFilePath()+" "+kitFrag.getRenderKitId()));
                    for (FacesRendererDefinition def : kitFrag.getDefs()) {
                        nodes.add(new NodeInfo(def, nodePath(def, null)));
                        for (String propName : def.getDefinedInlinePropertyNames()) {
                            FacesProperty prop = def.getProperty(propName);
                            nodes.add(new NodeInfo(prop, nodePath(def, prop)));
                            if( prop instanceof FacesPropertyType ){
                                FacesPropertyType propType = (FacesPropertyType) prop;
                                addInnerProps(nodes, propType);
                            }
                        }
                    }
                }
            }
        }
        
        
        for (NodeInfo node : nodes) {
            
            String name = (String) node.node.getExtension("display-name");
            if( null != name ){
                fails += spellCheck(node, name);
            }
            String descr = (String) node.node.getExtension("description");
            if( null != descr ){
                fails += spellCheck(node, descr);
            }
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails,
                SkipFileContent.concatSkips(getSkips(), this, "testSpellCheck"));
        if( fails.length() > 0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails));
        }
    }
    protected class NodeInfo{
       public FacesExtensibleNode node;
       public String nodePath;
       public NodeInfo(FacesExtensibleNode node, String nodePath) {
           super();
           this.node = node;
           this.nodePath = nodePath;
       }
       
    }
    protected String[] getSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    /**
     * Strings whose presence will cause a fail.
     * <pre>
     * {badString, reason for fail},
     * {badString, reason for fail},
     * </pre>
     * Note, these are test configuration options, not skips.
     * @return
     */
    protected List<String[]> getToCheckFor(){
        List<String[]> list = new ArrayList<String[]>();
        list.addAll(Arrays.asList(s_toCheckFor));
        return list;
    }
    /**
     * Strings whose presence as a word (rather than a substring) will cause a fail.
     * Note, these are test configuration options, not skips.
     * @return
     */
    protected List<String[]> getAsEntireWord(){
        List<String[]> list = new ArrayList<String[]>();
        list.addAll(Arrays.asList(s_asEntireWord));
        return list;
    }
    /**
     * Strings whose presence outside of a URL will cause a fail.
     * The test's URL detection is not complicated and doesn't conform to any spec.
     * Note, these are test configuration options, not skips.
     */
    protected List<String[]> getPreventExceptInUrls() {
        List<String[]> list = new ArrayList<String[]>();
        list.addAll(Arrays.asList(s_preventExceptInUrls));
        return list;
    }
    private void addInnerProps(List<NodeInfo> nodes,FacesPropertyType def) {
        for (String propName : def.getDefinedInlinePropertyNames()) {
            FacesProperty prop = def.getProperty(propName);
            nodes.add(new NodeInfo(prop, nodePath(def, prop)));
            if( prop instanceof FacesPropertyType ){
                FacesPropertyType propType = (FacesPropertyType) prop;
                addInnerProps(nodes, propType);
            }
        }
    }
    protected String spellCheck(NodeInfo nodeInfo, String name) {
        
        String fails = "";
        for (String[] wordAndReason : toCheckFor) {
            String word = wordAndReason[0];
            if( -1 != name.indexOf(word) ){
                String reason = wordAndReason[1];
                String fail = location(nodeInfo)+" Bad word " + JavaUtil.toJavaString(word) 
                    + " (" +reason+"), in: " + fullStr(name);
                fails += fail + "\n";
            }
        }
        for (String[] wordAndReason : preventExceptInUrls) {
            String word = wordAndReason[0];
            if( -1 != name.indexOf(word) ){
                String nameWithoutUrls = removeUrls(name);
                if( -1 != nameWithoutUrls.indexOf(word) ){
                    String reason = wordAndReason[1];
                    String fail = location(nodeInfo)+" Bad word " + JavaUtil.toJavaString(word) 
                        + " (" +reason+"), in: " + fullStr(name);
                    fails += fail + "\n";
                }
            }
        }
        for (String[] wordAndReason : asEntireWord) {
            String word = wordAndReason[0];
            int index = name.indexOf(word);
            if( -1 == index ){
                continue;
            }
            // contains the word, but check it isn't just a substring of
            // another word
            // for each occurrance of the word in the string
            for (; -1 != index; index = name.indexOf(word, index+1)) {
                char beforeChar = (0 == index)? ' ' : name.charAt(index-1);
                boolean atStartOfWord = 0 == index
                        || Character.isWhitespace(beforeChar)
                        || '(' == beforeChar || '[' == beforeChar
                        || '-' == beforeChar;
                if( ! atStartOfWord ){
                    continue;
                }
                int afterIndex = index + word.length(); 
                boolean atEndOfWord = afterIndex == name.length();
                if( ! atEndOfWord ){
                    char afterChar = name.charAt(afterIndex);
                    atEndOfWord = Character.isWhitespace(afterChar)
                            || '.' == afterChar || ',' == afterChar
                            || ':' == afterChar || '-' == afterChar;
                }
                if( !atEndOfWord ){
                    continue;
                }
                String reason = wordAndReason[1];
                String fail = location(nodeInfo)+" Bad word " + JavaUtil.toJavaString(word) + " (" +reason+"), in: " + fullStr(name);
                fails += fail + "\n";
                break; // bad word found in this string, ignore other occurrances of the same word. 
            }
        }
        
        return fails;
    }
	/**
     * @param name
     * @return
     */
    private String removeUrls(String name) {
        String[] protocols = new String[]{
                "http://"
        };
        for (String protocol : protocols) {
            for (int i = name.indexOf(protocol); i != -1; i = name.indexOf(protocol, i+1)) {
                // for each occurance of the protocol in the name
                
                // yes it's weird but easier for debugging:
                StringBuilder url = new StringBuilder(protocol); 
                for (int urlCharIndex = i+protocol.length(); urlCharIndex < name.length(); urlCharIndex++) {
                    char nthChar = name.charAt(urlCharIndex);
                    if( (nthChar >= 'a'&& nthChar <='z') 
                            || (nthChar >= 'a'&& nthChar <='Z')
                            || (nthChar >= '0'&& nthChar <='p')
                            || nthChar == '/'
                                || nthChar == '-'
                                    || nthChar == '.' 
                                        || nthChar == '_'
                                            || nthChar == '#'){
                        url.append(nthChar);
                        continue;
                    }else{
                        break;
                    }
                }
                String replacement = "[removed URL]";
                name = name.replace(url, replacement);
            }
        }
        return name;
    }
    public String fullStr(String name) {
		String escaped = JavaUtil.toJavaString(name);
		return escaped.substring(1, escaped.length()-1);
	}

    /**
	 * @param node
	 * @return
	 */
	private String location(NodeInfo node) {
		return node.nodePath;
//		if( node instanceof FacesProperty ){
//			FacesProperty prop = (FacesProperty)node;
//			return "prop_" +prop.getName();
//		}
//		if( node instanceof FacesDefinition ){
//			FacesDefinition def = (FacesDefinition)node;
//			return "def_" +def.getId();
//		}
//		return "unknown_"+node;
	}
	private String nodePath(FacesDefinition def, FacesProperty prop){
		String path = def.getFile().getFilePath()+" "+ ParseUtil.getTagRef(def);
		if( null != prop ){
			path += " "+prop.getName();
		}
		return path;
	}
    public static class DescriptionDisplayNameAnnotater implements RegistryAnnotater{
        private String[] elemNames = {"description", "display-name"};
        private String[] elemValues = new String[elemNames.length];
        public void annotate(RegistryAnnotaterInfo info,
                FacesExtensibleNode parsed, Element elem) {
            
            if( isApplicableExtensibleNode(parsed) ){
                extractNonTrimValues(elem, elemNames, elemValues);
                int index = 0;
                for (String value : elemValues) {
                    if( null != value ){
                        value = toLocalized(info, value);
                        parsed.setExtension(elemNames[index], value);
                    }
                    index++;
                }
            }
        }
		/**
		 * @param elem
		 * @param names
		 * @param values
		 */
		private void extractNonTrimValues(Element elem, String[] names,
				String[] values) {
			// like ElementUtil.extractValues(elem, elemNames, elemValues);
			// except without calling trim on the values
			Arrays.fill(values, null);
			int numberUnSet = names.length;
			for (Element child : ElementUtil.getChildren(elem)) {
				int nameIndex = -1;
				int j = 0;
				for (String name : names) {
					if( child.getLocalName().equals(name) ){
						nameIndex = j;
						break;
					}
					j++;
				}
				if( -1 == nameIndex ){
					continue;
				}
				values[nameIndex] = getContents(child);
				if( --numberUnSet == 0 ){
					break;
				}
			}
		}
	    private String getContents(Element i) {
	        // should maybe serialze the contents of the element, instead of just
	        // assuming that it only contains text.
	        StringBuffer buffer = new StringBuffer();
	        // append all the text in the given element
	        appendChildren(i, buffer);   
	        return buffer.toString();
	    }
	    private void appendChildren(Element element, StringBuffer buffer) {
	        NodeList children = element.getChildNodes();
	        for (int i = 0; i < children.getLength(); i++) {
	            Node child = children.item(i);
	            
	            if( child instanceof CDATASection || child instanceof Text ){
	                buffer.append( ((CharacterData)child).getData() );
	            }
	            if( child instanceof Element ){
	                appendChildren((Element)child, buffer);
	            }
	        }
	    }		
		protected boolean isApplicableExtensibleNode(FacesExtensibleNode parsed) {
            // not reading the faces-config-extension description & display-name 
            return !(parsed instanceof FacesLibraryFragment);
        }
        private String toLocalized(RegistryAnnotaterInfo info, String value) {
            // descriptions & display-names may be translated like %key%, 
            // or like %/referencePath/[descr|name]%
            // See 
            // http://www-10.lotus.com/ldd/ddwiki.nsf/dx/XPages_configuration_file_format#base+designer-extension
            if( value.length() > 2 && value.charAt(0) == '%' && value.charAt(value.length()-1) == '%'){
                String keyOrPath = value.substring(1, value.length() - 1);
                if( keyOrPath.charAt(0) == '/' ){
                    // path
                    String[] segments = (keyOrPath.substring(1)).split("/");
                    if( segments.length == 2 || segments.length == 3 ){
                        String lastSegment = segments[segments.length - 1];
                        int segType = "name".equals(lastSegment)? 1 : "descr".equals(lastSegment)? 2 : 0;
                        if( 0 != segType ){
                            FacesRegistry reg = info.getRegistry();
                            FacesDefinition def = reg.findDef(segments[0]);
                            FacesExtensibleNode target;
                            if( segments.length == 2 ){
                                target = def;
                            }else{ // segments.length == 3
                                FacesProperty prop = null == def? null : def.getProperty(segments[1]);
                                target = prop;
                            }
                            if( null != target ){
                                String extensionName = segType == 1? "display-name" : "description";
                                String extensionValue = (String) target.getExtension(extensionName);
                                if( null != extensionValue ){
                                    return extensionValue;
                                }
                            }
                        }
                    }
                    // fall through if can't find def/prop
                    throw new RuntimeException("Cannot find value for path "+keyOrPath);
                }else{
                    // key
                    ResourceBundle bundle = info.getResourceBundle();
                    if( null != bundle ){
                        String bundleValue = bundle.getString(keyOrPath);
                        if( null != bundleValue ){
                            return bundleValue;
                        }
                    }
                    throw new RuntimeException("Cannot find value for key "+keyOrPath);
                }
            }
            // non-translated value
            return value;
        }
    }
}
