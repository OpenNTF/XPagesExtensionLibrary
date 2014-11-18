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

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;

/**
 * @author mblout
 *
 */
public class DebugUtil {
    
    public static void dumpDefinitions(FacesSharableRegistry reg, AbstractXspTest test, PrintStream out) {
        
        out.println("--- dumping TestProject.getDefinitions for " + test.getName() + "----");
        out.println("----   ID [namespace, reference-ID] -----"); 
        
        List<FacesDefinition> defs = TestProject.getDefinitions(reg, test);
        for (Iterator<FacesDefinition> it = defs.iterator(); it.hasNext();) {
            FacesDefinition def = it.next();
            String msg = MessageFormat.format("   {0} [{1}, {2}]", def.getId(), def.getNamespaceUri(), def.getReferenceId());
            out.println(msg);
        }
    }

}
