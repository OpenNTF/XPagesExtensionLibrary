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
package xsp.extlib.designer.junit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.ibm.commons.util.StringUtil;

public class XMLCompareUtils {

    /**
     * This method will compare the attributes of a given src element with the attributes of a given dest element.
     * Both must contain the same attributes, but you can specify a String array of attribute names whose values
     * are ignored. Both elements must have the ignore attributes, their contents can be different and they will
     * still be considered equal.
     * @param src - the src element whose attributes are to be compared
     * @param dest - the dest element whose attributes are to be compared
     * @param ignore - the string array of attributes whose values are to be ignored during the compare.
     * @return true if the attributes of both nodes meet the criteria of being equal, false otherwise.
     */
    private static boolean compareAttributes (Element src, Element dest, String[] ignore) {
        NamedNodeMap srcAttrs = src.getAttributes();
        
        if (srcAttrs.getLength() != dest.getAttributes().getLength())
            return false;
        
        for (int ctr=0; ctr<srcAttrs.getLength(); ctr++) {
            Node srcAttr = srcAttrs.item(ctr);
            
            String name = srcAttr.getNodeName();
            if (Arrays.binarySearch(ignore, name) < 0) {
                Node destAttr = dest.getAttributeNode(name);
                if (destAttr == null || !srcAttr.isEqualNode(destAttr)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
	/**
     * More exhaustive comparison of two elements. Does not assume child node order is the same in src and dest.
     * @param src
     * @param dest
     * @param ignoreContentsOfNodes - an array of nodes that only need to be present. Their children do not take part in the comparison.
     * @param ignoreExistenceOfNodes - an array of nodes that will be ignored. If they are present in one element and not in the other, the two elements are still considered equal
     * @param ignoreAttrs - an array of attributes to ignore. The "value" of these attributes will be ignored when comparing any node or child node.  
     * @return
     */
    private static boolean deepCompareElements (Node src, Node dest, String[] ignoreContentsOfNodes, String[] ignoreExistenceOfNodes, String[] ignoreAttrs) {
    	//first check to see if the basics of the two nodes are equal
    	if(areIndividualNodeEqual(src, dest, ignoreContentsOfNodes, ignoreAttrs)){
    		if (Arrays.binarySearch(ignoreContentsOfNodes, src.getNodeName()) >=0) {
    			//if this is a node we are ignoring, then no need to check its children
    			return true;
    		}
    		//we need the security of using an iterator when removing child nodes, so instead of using
    		//getChildNodes and dealing with a NodeList which does not implement Iterator, we use a helper
    		//method to get the children in an ArrayList<Node>. 
    		ArrayList<Node> srcNodes = getChildNodes(src);
	    	ArrayList<Node> destNodes = getChildNodes(dest);
	    	if(srcNodes.isEmpty() && destNodes.isEmpty()){
	    		//no children and all the other node info matches, so these are equal
	    		return true;
	    	}
	        if(!srcNodes.isEmpty() && !destNodes.isEmpty()){
	        	Iterator<Node> srcNodeIterator = srcNodes.iterator();
	        	while(srcNodeIterator.hasNext()){
	        		Node srcNode = srcNodeIterator.next();
	        		boolean found = findAndRemoveMatchingNode(srcNode, destNodes, ignoreContentsOfNodes, ignoreExistenceOfNodes, ignoreAttrs);
	        		if(found){
	        			//we found a matching node in the destNodes array. It will have been removed from destNodes 
	        			//so remove from srcNodes as well now
	        			srcNodeIterator.remove();
	        		}
	        		else{
	        			//we failed to find a match so either they not the same, or this is an ignoreExistenceOfNode
	        			//if it is an ignoreExistneceOfNode then we can just remove it and consider it found.
	        			if(Arrays.binarySearch(ignoreExistenceOfNodes, srcNode.getNodeName()) >= 0){
	        				srcNodeIterator.remove();
	        			}
	        			else{
	        				//failed to find a match and not an ignoreExistencOf node, so return false.
	        				return false;
	        			}
	        		}
	        	}
	        	//at this point we should have gone through all the nodes in both maps. 
	        	//If both maps are empty, then we have found all nodes in both and they are equal.
	        	if(srcNodes.isEmpty() && destNodes.isEmpty()){
	        		return true;
	        	}
	        	else{
	        		return false;
	        	}
	        }
    	}
    	//basic node properties are not equal, so not the same.
    	return false;
    }
    
    /**
     * This method will get the child nodes of a given node and return them in an ArrayList
     * instead of a NodeList. NodeList does not implement Iterator, which is needed to make
     * the compare code safer.
     * @param parentNode
     * @return an ArrayList of childNodes if the parentNode has childNodes. If there are no children an empty ArrayList is returned.
     */
    private static ArrayList<Node> getChildNodes(Node parentNode){
    	ArrayList<Node> childNodesArray = new ArrayList<Node>();
    	if(null != parentNode){
    		NodeList childNodes = parentNode.getChildNodes();
    		if(null != childNodes && childNodes.getLength()>0){
    			for(int i=0; i<childNodes.getLength(); i++){
    				Node child = childNodes.item(i);
    				if(null != child){
    					childNodesArray.add(child);
    				}
    			}
    		}
    	}
    	return childNodesArray;
    }
    
    /**
     * This method will search through the destNodes to try find a matching node to srcNode. 
     * The matched node will have to have identical children to N levels to be considered a match.
     * Once a match is found, the node is removed from destNodes. 
     * @param srcNode
     * @param destNodes
     * @return true if match was found and removed, false otherwise. 
     */
    private static boolean findAndRemoveMatchingNode(Node srcNode, ArrayList<Node> destNodes, String[] ignoreContentsOfNodes,String[] ignoreExistenceOfNodes, String[] ignoreAttrs){
    	Iterator<Node> destNodesIterator = destNodes.iterator();
    	while(destNodesIterator.hasNext()){
    		Node destNode = destNodesIterator.next();
    		if(deepCompareElements(srcNode, destNode, ignoreContentsOfNodes, ignoreExistenceOfNodes, ignoreAttrs)){
    			//we have found a matching node, remove it from destNodes
    			destNodesIterator.remove();
    			return true;
    		}
    		else{
    			//we failed to find a match. Check if this is an ignoreExistenceOfNode, in which case we can just ignore it not having a match.
    			if(Arrays.binarySearch(ignoreExistenceOfNodes, destNode.getNodeName()) >= 0){
    				destNodesIterator.remove();
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * This method will check that two nodes are equal. 
     * For nodes to be considered equal.
     * 		The nodes must be of the same type
	 * 		The following node properties must be equal: 
	 *			nodeName, 
	 *			localName, 
	 *			namespaceURI, 
	 *			prefix, 
	 *			nodeValue
	 *		The attributes NamedNodeMaps must be equal (attributes in ignoreAttrs are ignored)
	 *		The childNodes NodeLists are equal. i.e. both nodes have exactly the same children, but in any order
	 *			Any child nodes that are in the ignoreContentsOfNodes array will be considered equal as long they are present in both. 
	 *			The contents of nodes in ignoreNodes can be different
     * @param srcNode
     * @param destNode
     * @param ignoreContentsOfNodes
     * @param ignoreAttrs
     * @return
     */
    private static boolean areIndividualNodeEqual(Node srcNode, Node destNode, String[] ignoreContentsOfNodes, String[] ignoreAttrs){
    	if(null != srcNode && null !=destNode){
	    	//try normal node compare first. Takes all the effort out if this check passes, but we will fail this
	    	//test a lot of the time.
	    	if(srcNode.isEqualNode(destNode)){
	    		return true;
	    	}
	    
	    	//verify that the node types are the same
	    	if(srcNode.getNodeType() == destNode.getNodeType()){
	    	
		    	String srcNodeName = srcNode.getNodeName();
		    	String srcLocalName = srcNode.getLocalName();
		    	String srcNamespaceURI = srcNode.getNamespaceURI();
		    	String srcPrefix = srcNode.getPrefix();
		    	String srcNodeValue = srcNode.getNodeValue();
		    	
		    	String destNodeName = destNode.getNodeName();
		    	String destLocalName = destNode.getLocalName();
		    	String destNamespaceURI = destNode.getNamespaceURI();
		    	String destPrefix = destNode.getPrefix();
		    	String destNodeValue = destNode.getNodeValue();
	    	
		    	//verify that the node properties are all the same
		    	if(StringUtil.equals(srcNodeName, destNodeName)
		    			&& StringUtil.equals(srcLocalName, destLocalName)
		    			&& StringUtil.equals(srcNamespaceURI, destNamespaceURI)
		    			&& StringUtil.equals(srcPrefix, destPrefix)
		    			&& StringUtil.equals(srcNodeValue, destNodeValue)
		    			){
		    		//if these are element Nodes check their attributes (only element nodes have attributes). 
		    		if(srcNode instanceof Element && destNode instanceof Element){
		    			//if this is one of our ignore nodes we pass automatically. Otherwise we pass to compare attributes
		    			//to see if the attributes match given our array of ignore attributes
			    		if((Arrays.binarySearch(ignoreContentsOfNodes, srcNodeName) >= 0 
		    				|| compareAttributes((Element)srcNode, (Element)destNode, ignoreAttrs))){
			    			//we have passed the attributes test. 
			    			return true;
			    		}
		    		}
		    		else{
		    			//if the nodes are not elements, then they do not have attributes to check, so they must be equal
		    			return true;
		    		}
		    		
		    	}
	    	}
    	}
    	return false;
    }
    
    /**
     * This method takes a node and removes any empty text node children from it. 
     * Empty text nodes are defined as text nodes containing a only spaces,
     * \n characters, \r characters and \t characters. 
     * You should normalize the node before calling this method on the off chance
     * it will trim important spaces from text. 
     * @param node
     */
    private static void removeEmptyTextNodes(Node node) {
        NodeList nodeList = node.getChildNodes();
        int length = nodeList.getLength();
        for(int i=length-1; i>=0; i--) {
            Node child = nodeList.item(i);
            if(child.getNodeType()==Node.TEXT_NODE) {
                Text txt = (Text) child;
                String data = txt.getData();
                if(StringUtil.isSpace(data)) {
                    node.removeChild(child);
                }
            } else {
                removeEmptyTextNodes(child);
            }
        }
    }
    
    /**
     * Exhaustive comparison of two documents. Does not assume child node order is the same in sourceDocument and destinationDocument.
     * Ignores formatting text nodes (carriage returns, whitespace, tabs etc...)
     * @param sourceDocument
     * @param destinationDocument  
     * @return true if the contents of the two documents are considered equal, false otherwise
     */
    public static boolean compare(Document sourceDocument, Document destinationDocument){
    	if(null != sourceDocument && null != destinationDocument){
    		Element sourceDocElement = sourceDocument.getDocumentElement();
    		Element destinationDocElement = destinationDocument.getDocumentElement();
    		if(null != sourceDocElement && null != destinationDocElement){
    			//before we do any comparing, normalize the nodes to remove any formatting text nodes, that might affect the compare
    			sourceDocElement.normalize();
    			removeEmptyTextNodes(sourceDocElement);
    			destinationDocElement.normalize();
    			removeEmptyTextNodes(destinationDocElement);
    			deepCompareElements(sourceDocElement, destinationDocElement, null, null, null);
    		}
    	}
    	return false;
    }
    
    /**
     * Exhaustive comparison of two documents. Does not assume child node order is the same in sourceDocument and destinationDocument.
     * Ignores formatting text nodes (carriage returns, whitespace, tabs etc...) sourceDocument and destinationDocument are required.
     * Optionally, you can also provide an array of nodes to ignore, an array of nodes whose contents should be ignored and/or an
     * array of attributes whose values should be ignored. Any or all of the optional arguments can be provided. 
     * @param sourceDocument
     * @param destinationDocument
     * @param ignoreContentsOfNodes - an array of nodes that only need to be present. Their children do not take part in the comparison.
     * @param ignoreExistenceOfNodes - an array of nodes that will be ignored. If they are present in one element and not in the other, the two elements are still considered equal
     * @param ignoreAttributes - an array of attributes to ignore. The "value" of these attributes will be ignored when comparing any node or child node.  
     * @return
     */
    public static boolean compare(Document sourceDocument, Document destinationDocument, String[] ignoreContentsOfNodes, String[] ignoreExistenceOfNodes, String[] ignoreAttributes){
    	if(null != sourceDocument && null != destinationDocument){
    		Element sourceDocElement = sourceDocument.getDocumentElement();
    		Element destinationDocElement = destinationDocument.getDocumentElement();
    		if(null != sourceDocElement && null != destinationDocElement){
    			//before we do any comparing, normalize the nodes to remove any formatting text nodes, that might affect the compare
    			sourceDocElement.normalize();
    			removeEmptyTextNodes(sourceDocElement);
    			destinationDocElement.normalize();
    			removeEmptyTextNodes(destinationDocElement);
    			deepCompareElements(sourceDocElement, destinationDocElement, ignoreContentsOfNodes, ignoreExistenceOfNodes, ignoreAttributes);
    		}
    	}
    	return false;
    }
    
    /**
     * Exhaustive comparison of two elements. Does not assume child node order is the same in sourceElement and destinationElement.
     * Ignores formatting text nodes (carriage returns, whitespace, tabs etc...)
     * @param sourceElement
     * @param destinationElement  
     * @return true if the contents of the two elements are considered equal, false otherwise
     */
    public static boolean compare(Element sourceElement, Element destinationElement){
    	if(null != sourceElement && null != destinationElement){
			//before we do any comparing, normalize the nodes to remove any formatting text nodes, that might affect the compare
    		sourceElement.normalize();
			removeEmptyTextNodes(sourceElement);
			destinationElement.normalize();
			removeEmptyTextNodes(destinationElement);
			deepCompareElements(sourceElement, destinationElement, null, null, null);
		}
    	return false;
    }
    
    /**
     * Exhaustive comparison of two elements. Does not assume child node order is the same in sourceElement and destinationElement.
     * Ignores formatting text nodes (carriage returns, whitespace, tabs etc...) sourceElement and destinationElement are required.
     * Optionally, you can also provide an array of nodes to ignore, an array of nodes whose contents should be ignored and/or an
     * array of attributes whose values should be ignored. Any or all of the optional arguments can be provided. 
     * @param sourceElement
     * @param destinationElement
     * @param ignoreContentsOfNodes - an array of nodes that only need to be present. Their children do not take part in the comparison.
     * @param ignoreExistenceOfNodes - an array of nodes that will be ignored. If they are present in one element and not in the other, the two elements are still considered equal
     * @param ignoreAttributes - an array of attributes to ignore. The "value" of these attributes will be ignored when comparing any node or child node.  
     * @return
     */
    public static boolean compare(Element sourceElement, Element destinationElement, String[] ignoreContentsOfNodes, String[] ignoreExistenceOfNodes, String[] ignoreAttributes){
    	if(null != sourceElement && null != destinationElement){
			//before we do any comparing, normalize the nodes to remove any formatting text nodes, that might affect the compare
			sourceElement.normalize();
			removeEmptyTextNodes(sourceElement);
			destinationElement.normalize();
			removeEmptyTextNodes(destinationElement);
			deepCompareElements(sourceElement, destinationElement, ignoreContentsOfNodes, ignoreExistenceOfNodes, ignoreAttributes);
		}
    	return false;
    }
	
	
}
