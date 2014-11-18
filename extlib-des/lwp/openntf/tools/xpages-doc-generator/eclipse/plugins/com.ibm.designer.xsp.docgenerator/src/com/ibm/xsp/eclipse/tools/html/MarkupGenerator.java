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

package com.ibm.xsp.eclipse.tools.html;

import java.io.*;

import com.ibm.commons.util.io.StreamUtil;
import com.ibm.xsp.eclipse.tools.doc.ComplexType;
import com.ibm.xsp.eclipse.tools.doc.Component;
import com.ibm.xsp.eclipse.tools.doc.CompositeComponent;
import com.ibm.xsp.eclipse.tools.doc.Definition;
import com.ibm.xsp.eclipse.tools.doc.Registry;
import com.ibm.xsp.eclipse.tools.doc.TextWriter;



/**
 * 
 */
public abstract class MarkupGenerator {
	
	public static class Options {
		public boolean includeStandard;
		public boolean includeCustom;
		public boolean includeExtLib;
		public boolean includeOthers;
	}

    private static final boolean GENERATE_UNPUBLISHED = false;
    
    private Registry registry;
    private Options options;
    private File baseDirectory;
    
    protected MarkupGenerator(Registry registry, File baseDirectory, Options options) {
        this.registry = registry;
        this.baseDirectory = baseDirectory;
        this.options = options;
    }
    
    public Registry getRegistry() {
        return registry;
    }
    
    public Options getOptions() {
        return options;
    }
    
    public boolean isGenerateDefinition(Definition def) {
        return GENERATE_UNPUBLISHED || def.isGenerateDocumentation();
    }
        
    public void generate() throws IOException {
        createDirectory();
        
        copyResources();
        
        TextWriter idx = new TextWriter(new File(baseDirectory,"index."+getFileExtension()));
        try {
            generateIndexPage(idx);
        } finally {
        	idx.close();
        }
        
        TextWriter ctrl = new TextWriter(new File(baseDirectory,"Controls."+getFileExtension()));
        try {
            generateComponentListPage(ctrl);
        } finally {
        	ctrl.close();
        }
        
        TextWriter cpx = new TextWriter(new File(baseDirectory,"Complex."+getFileExtension()));
        try {
            generateComplexListPage(cpx);
        } finally {
        	cpx.close();
        }

        TextWriter hie = new TextWriter(new File(baseDirectory,"Hierarchy."+getFileExtension()));
        try {
            generateHierarchyPage(hie,false);
        } finally {
        	hie.close();
        }

        TextWriter hall = new TextWriter(new File(baseDirectory,"Hierarchy.all."+getFileExtension()));
        try {
            generateHierarchyPage(hall,true);
        } finally {
        	hall.close();
        }
        
        for (Definition def : registry.getHierarchicalDefinitions()) {
            generateDefinition(def);
        }
    }
    
    private void generateDefinition(Definition def) throws IOException  {
        String fileName = def.getFileName()+"."+getFileExtension();
        TextWriter w = new TextWriter(new File(baseDirectory,fileName));
        try {
            if( def instanceof Component ) {
                generateComponent(w,(Component)def);
            } else if( def instanceof ComplexType ) {
                generateComplexType(w,(ComplexType)def);
            } else if( def instanceof CompositeComponent ) {
                generateComposite(w,(CompositeComponent)def);
            }
        } finally {
            w.close();
        }
        
        for (Definition child : def.getChildrenList()) {
            generateDefinition(child);
        }
    }
    
    protected abstract String getFileExtension(); 
    protected abstract void copyResources() throws IOException;
    protected abstract void generateIndexPage(TextWriter w) throws IOException; 
    protected abstract void generateHierarchyPage(TextWriter w, boolean all) throws IOException; 
    protected abstract void generateComponentListPage(TextWriter w) throws IOException; 
    protected abstract void generateComplexListPage(TextWriter w) throws IOException; 
    protected abstract void generateComponent(TextWriter w, Component c) throws IOException; 
    protected abstract void generateComplexType(TextWriter w, ComplexType c) throws IOException;
    protected abstract void generateComposite(TextWriter w, CompositeComponent c) throws IOException;

    
    private void createDirectory() {
        if(baseDirectory.exists()) {
            prune(baseDirectory);
        }
        baseDirectory.mkdirs();
    }
    
    private static void prune( File directory ) {
        File[] f = directory.listFiles();
        if( f!=null ) {
            for( int i=0; i<f.length; i++ ) {
                if( f[i].isDirectory() ) {
                    prune( f[i] );
                } else {
                    f[i].delete();
                }
            }
        }
        // Delete the directory
        directory.delete();
    }
    
    public void copyResource(String dir, String resourceName) throws IOException {
        String res = "com/ibm/xsp/eclipse/tools/resources/"+resourceName;
        InputStream is = getClass().getClassLoader().getResourceAsStream(res);
        if(is!=null) {
            try {
                File d = new File(baseDirectory,dir);
                d.mkdirs();
                int pos = resourceName.lastIndexOf('/');
                if(pos>=0) {
                    resourceName = resourceName.substring(pos+1);
                }
                OutputStream os = new FileOutputStream(new File(d,resourceName));
                try {
                    StreamUtil.copyStream(is,os);
                } finally {
                    os.close();
                }
            } 
            finally {
                is.close();
            }
        } 
    }
}
