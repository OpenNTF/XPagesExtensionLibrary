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
* Date: 25 Feb 2011
* SerializeStreamUtil.java
* This was refactored out from xsp.application.SerializationUtil
* which was created 2006.
*/
package com.ibm.xsp.test.framework.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ibm.xsp.FacesExceptionEx;

/**
 * Util for saving/restoring an object to a stream - does not handle JSF/control
 * serialization, (e.g. it doesn't restore UIComponent reference to reference
 * the corresponding control in the restored tree, it can't restore
 * ValueBindings, etc.).  
 */
public class SerializeStreamUtil {

	/**
	 * Util for saving/restoring an object to a stream - does not handle
	 * JSF/control serialization, (e.g. it doesn't restore UIComponent reference
	 * to reference the corresponding control in the restored tree, it can't
	 * restore ValueBindings, etc.).
	 * 
	 * @param o
	 * @return
	 */
	public static Object streamSaveAndRestore(Object o) {
		try {
			return streamSaveAndRestoreInternal(o);
		} catch (Exception e) {
			traceData(o);
			throw new FacesExceptionEx(e);
		}
	}

	private static Object streamSaveAndRestoreInternal(Object o)
			throws Exception {
		final String padding = "PADDING"; // verifies serialization tests
											// aren't reading too much
	
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objout = new ObjectOutputStream(out);
		objout.writeObject(padding); // write before padding
		objout.writeObject(o);
		objout.writeObject(padding); // write after padding
		objout.flush();
	
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		ObjectInputStream objin = new ObjectInputStream(in);
		Object afterPaddingRetrieved = objin.readObject();
		if( ! padding.equals(afterPaddingRetrieved) ){ // should be padding
			throw new RuntimeException("Problem in stream serialization");
		}
		o = objin.readObject();
		Object beforePaddingRetrieved = objin.readObject();
		if( ! padding.equals(beforePaddingRetrieved) ){ // should be padding
			throw new RuntimeException("Problem in stream serialization");
		}
		return o;
	}

	private static void traceData(Object o) {
		traceData(0, 0, o);
	}

	private static void traceData(int depth, int n, Object o) {
		//String indent = "";
		//for (int i = 0; i < depth; i++) {
		//	indent += ' ';
		//}
	
		if (o instanceof Object[]) {
			//System.err.println(indent + depth + "[" + n + "]> [ARRAY]");
			//indent += "  ";
			depth++;
			Object[] arr = (Object[]) o;
			for (int i = 0; i < arr.length; i++) {
				traceData(depth, i, arr[i]);
			}
		} else {
//			String desc;
			if (o == null) {
//				desc = "null\t(OK)";
			} else {
//				desc = o.toString();
//				desc += "\t";
				try {
					streamSaveAndRestoreInternal(o);
//					desc += "(OK) ";
//					desc += o.getClass().getName();
				} catch (Exception e) {
//					desc += "(!!ERROR!!) ";
//					desc += e;
//					desc += "\t";
//					desc += o.getClass().getName();
				}
			}
			//System.err.println(indent + depth + "[" + n + "] " + desc);
		}
	}

}
