/*
 * © Copyright IBM Corp. 2010
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

package com.ibm.xsp.extlib.services.rest.tree;

import com.ibm.domino.services.rest.RestServiceParameters;
import com.ibm.xsp.extlib.tree.ITree;


/**
 * Tree Parameters.
 */
public interface TreeParameters extends RestServiceParameters {

	/**
	 * Get the tree to render.
	 * @return
	 */
	public ITree getTree();
	
	/**
	 * Get the start element, as a path.
	 */
	public String getStart();
	
	/**
	 * Get the number of elements to render.
	 */
	public int getCount();
	
	/**
	 * Get the depth of the elements to render. 
	 */
	public int getDepth();
}
