 /*
 * © Copyright IBM Corp. 2011, 2014
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
* Date: 2 Jan 2007
* ToolsUtil.java
*/


package com.ibm.xsp.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLException;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 2 Jan 2007
 * 
 * Project: IBM Lotus Component Designer.
 * Unit: ToolsUtil.java
 */
public class ToolsUtil {

    public static void writeOut(File file, String str) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            if( ! file.canWrite() ){
            	throw new RuntimeException("The file is not writable: "+file.getAbsolutePath());
            }
    
            FileWriter out = new FileWriter(file);
            out.write(str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String convertToString(Document document)
            throws XMLException {
        Node node = document.getDocumentElement();
        
        return nodeToString(node) ;
    }
    
    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
          Transformer t = TransformerFactory.newInstance().newTransformer();
          t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
          t.setOutputProperty(OutputKeys.INDENT, "yes");
          t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
        	throw new RuntimeException(te);
        }
        return sw.toString();
      }
    public static Document readInFile(File file) throws Exception {
    
            String inUrl = file.toURL().toString();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setExpandEntityReferences(true);
            factory.setValidating(false);
            
            // Note the DomUtil parser doesn't expand entities correctly.
    //        DocumentBuilder parser = DomUtil.getParser();
    
            // TODO (mkehoe) this is expanding &quot; in the description - it should
            // leave that the same        
            DocumentBuilder parser = factory.newDocumentBuilder();
            final List<String> errors = new ArrayList<String>();
            parser.setErrorHandler(new ErrorHandler(){
                public void fatalError(SAXParseException ex) throws SAXException {
                    throw ex;
                }
                public void error(SAXParseException ex) throws SAXException {
                    String msg = "Error: Line="+ex.getLineNumber()+": "+ex.getMessage();
                    System.err.println(msg);
                    errors.add(msg);
//                    throw ex;
                }
                public void warning(SAXParseException ex) throws SAXException {
                    System.err.println("Warning: Line="+ex.getLineNumber()+": "+ex.getMessage());
//                    throw ex;
                }
            });
            Document document = parser.parse(inUrl);
            if( !errors.isEmpty() ){
                String allMsgs = "";
                for (String msg : errors) {
                    allMsgs += msg+", ";
                }
                allMsgs = allMsgs.substring(0,  allMsgs.length()-2);
                throw new RuntimeException("Problem(s) parsing XML: "+allMsgs);
            }
            return document;
        }

    public static Map<String, String> processArgs(String[] args){
            Map<String, String> argsMap = new HashMap<String, String>(args.length / 2);
            for(int i = 0; i < args.length; i += 2){
                if(args[i].startsWith("--") && i + 1 <= args.length)
                    argsMap.put(args[i], args[i + 1]);
            }
            // debug to check what args were taken in
    //        for (Iterator iter = _argsMap.keySet().iterator(); iter.hasNext();) {
    //            String key = (String) iter.next();
    //            System.out.println("key: " + key + ", value: " + _argsMap.get(key));
    //        }
            return argsMap;
        }

}
