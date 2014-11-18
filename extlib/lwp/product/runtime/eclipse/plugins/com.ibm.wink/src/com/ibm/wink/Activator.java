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

package com.ibm.wink;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.wink.common.internal.runtime.RuntimeDelegateImpl;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		// Ensure that the service loader uses the right class
		// See: http://www.mail-archive.com/discuss@restlet.tigris.org/msg07539.html
		ClassLoader oldcl = Thread.currentThread().getContextClassLoader();
		ClassLoader newcl = RuntimeDelegate.class.getClassLoader();
		
		Thread.currentThread().setContextClassLoader(newcl);
		try {
			RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		} finally {
			Thread.currentThread().setContextClassLoader(oldcl);
		}
		super.start(context);
	}
}
