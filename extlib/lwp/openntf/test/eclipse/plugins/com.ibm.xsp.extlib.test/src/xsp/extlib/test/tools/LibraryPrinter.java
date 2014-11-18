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
* Date: 10 Oct 2011
* LibraryPrinter.java
*/
package xsp.extlib.test.tools;

import com.ibm.xsp.library.StandardRegistryMaintainer;
import com.ibm.xsp.registry.FacesComponentDefinition;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibraryFragment;
import com.ibm.xsp.registry.FacesProject;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;

/**
 *
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 */
public class LibraryPrinter {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FacesSharableRegistry reg = StandardRegistryMaintainer.getStandardRegistry();
        String libraryId = "com.ibm.xsp.extlib.library";
        
        System.out.println("LibraryPrinter.main()");
        
        String indent = "\t";
        for( FacesSharableRegistry subset : reg.getDepends() ) {
            String subsetId = subset.getId();
            if( !libraryId.equals(subsetId) ){
                continue;
            }
            
            for (FacesProject proj : subset.getLocalProjectList()) {
                for (FacesLibraryFragment file : proj.getFiles()) {
                    
                    System.out.println(indent+"file:{");
                    indent = indent +"\t";
                    System.out.println(indent+"file-path:"+file.getFilePath());
                    System.out.println(indent+"short-file-path:"+file.getFilePath().substring(file.getFilePath().lastIndexOf('/')+1));
                    System.out.println();
                    
                    String defaultPrefix = subset.getLibrary(file.getNamespaceUri()).getFirstDefaultPrefix();
                    
                    for (FacesDefinition def : file.getDefs()) {
                        System.out.println(indent+"def:{");
                        indent = indent +"\t";
                        
                        String qualifiedTagName = !def.isTag()? null : (defaultPrefix +":" +def.getTagName());
                        System.out.println(indent+"tag-name:"+qualifiedTagName);
                        System.out.println(indent+"reference-id:"+def.getReferenceId());
                        System.out.println(indent+"def-java-class:"+def.getJavaClass().getName());
                        System.out.println(indent+"def-short-class:"+shortName(def.getJavaClass()));
                        String parentRef;
                        FacesDefinition parent = def.getParent();
                        parentRef = (parent == null)? null : parent.getReferenceId(); 
                        System.out.println(indent+"parent-ref:"+parentRef);
                        String type = def.getClass().getSimpleName();
                        System.out.println(indent+"type:"+type);
                        if( null != def.getSince() ){
                            System.out.println(indent+"def-since:"+def.getSince());
                        }
                        if( def instanceof FacesComponentDefinition ){
                            FacesComponentDefinition comp = (FacesComponentDefinition) def;
                            System.out.println(indent+"component-family:"+comp.getComponentFamily());
                            System.out.println(indent+"renderer-type:"+comp.getRendererType());
                        }
                        
                        for (FacesProperty prop : RegistryUtil.getProperties(def, def.getPropertyNames()) ) {
                            
                            System.out.println(indent+"prop:{");
                            indent = indent +"\t";
                            System.out.println(indent+"property-name:"+prop.getName());
                            System.out.println(indent+"prop-java-class:"+prop.getJavaClass().getName());
                            System.out.println(indent+"prop-short-class:"+shortName(prop.getJavaClass()));
                            if( prop.isAttribute() ){
                                System.out.println(indent+"is-attribute:"+prop.isAttribute());
                            }
                            if( prop.isRequired() ){
                                System.out.println(indent+"required:"+prop.isRequired());
                            }
                            if( null != prop.getSince() ){
                                System.out.println(indent+"prop-since:"+prop.getSince());
                            }
                            
                            FacesContainerProperty container = null;
                            FacesProperty item = null;
                            if( prop instanceof FacesContainerProperty ){
                                container = (FacesContainerProperty) prop;
                                item = container.getItemProperty();
                            }else{
                                item = prop;
                            }
                            String setter;
                            if( null != container ){
                                setter = container.getCollectionAddMethod();
                            }else{
                                setter = prop.getName();
                                setter = "set"+Character.toUpperCase(setter.charAt(0))+setter.substring(1);
                            }
                            setter += "(" + item.getJavaClass().getName()+")";
                            System.out.println(indent+"setter:"+setter);
                            
                            if( item instanceof FacesSimpleProperty ){
                                FacesSimpleProperty simple = (FacesSimpleProperty) item;
                                if( !simple.isAllowRunTimeBinding() ){
                                    System.out.println(indent+"allow-run-time-binding:false");
                                }
                                if( ! simple.isAllowLoadTimeBinding() ){
                                    System.out.println(indent+"allow-load-time-binding:false");
                                }
                                if( ! simple.isAllowNonBinding() ){
                                    System.out.println(indent+"allow-non-binding:false");
                                }
                            }
                            
                            indent = indent.substring(1);
                            System.out.println(indent+"}//end prop");
                            
                        }
                        
                        indent = indent.substring(1);
                        System.out.println(indent+"}//end def");
                    }
                    indent = indent.substring(1);
                    System.out.println(indent+"}//end file");
                } // end file
            } // end proj
        } // end library
        
        
        
    }

    /**
     * @param javaClass
     * @return
     */
    private static String shortName(Class<?> javaClass) {
        String name = javaClass.getName();
        return name.substring(name.lastIndexOf('.')+1);
    }
}
