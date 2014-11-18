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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

import com.ibm.commons.util.QuickSort;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.registry.ComponentDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DefinitionDesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.designer.domino.xsp.registry.PropertyDesignerExtension;
import com.ibm.xsp.eclipse.tools.constants.ElementConstants;
import com.ibm.xsp.eclipse.tools.doc.ComplexType;
import com.ibm.xsp.eclipse.tools.doc.Component;
import com.ibm.xsp.eclipse.tools.doc.CompositeComponent;
import com.ibm.xsp.eclipse.tools.doc.Definition;
import com.ibm.xsp.eclipse.tools.doc.Namespace;
import com.ibm.xsp.eclipse.tools.doc.Registry;
import com.ibm.xsp.eclipse.tools.doc.TextWriter;
import com.ibm.xsp.registry.FacesContainerProperty;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesSimpleProperty;
import com.ibm.xsp.registry.RegistryUtil;
import com.ibm.xsp.registry.types.FacesSimpleTypes;



/**
 * 
 */
public class HTMLGenerator extends MarkupGenerator {

	// List formatting
    int ncol = 4;
    int colw = 240;
	
    public HTMLGenerator(Registry registry, File baseDirectory, Options options) {
        super(registry,baseDirectory,options);
    }
    
    protected String getFileExtension() {
        return "html";
    }
    
    protected void copyResources() throws IOException {
        copyResource("images","html/class.png");
        copyResource("images","html/bigclass.png");
        copyResource("images","html/interface.png");
        copyResource("images","html/biginterface.png");
        copyResource("images","html/xpages.png");
        copyResource("images","html/inherit.png");
        copyResource("css","html/registry.css");
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Index page
    protected void generateIndexPage(TextWriter w) throws IOException {
        w.prtln("<html>");
        w.prtln("<head>");
        w.prtln("<Title>XPages Controls Documentation</Title>");
        w.prtln("</head>");
        w.prtln("<body>");
        w.prtln("<META HTTP-EQUIV=\"Refresh\" CONTENT=\"0;URL=./Controls.html\" >");
        w.prtln("</body>");
        w.prtln("</html>");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Component List page
    protected void generateComponentListPage(TextWriter w) throws IOException {
        htmlHeader(w);
        //w.prtln("<div class='title'>Hierarchy</div>");
        w.prtln("<h1>Controls</h1>");
        

        if(getOptions().includeStandard) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_CORE); 
        	if(n!=null) {
        		generateList(w,n);
        	}
        }
        if(getOptions().includeExtLib) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_COREEX); 
        	if(n!=null) {
        		generateList(w,n);
        	}
        }
        if(getOptions().includeCustom) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_CUSTOM); 
        	if(n!=null) {
        		generateList(w,n);
        	}
        }
        if(getOptions().includeOthers) {
        	ArrayList<Namespace> nsList = getRegistry().getNamespaces();
        	for(int i=0; i<nsList.size(); i++) {
        		Namespace n = nsList.get(i);
        		if(!n.isStandard()) {
                	generateList(w,n);
        		}
        	}
        }
        
        htmlFooter(w);
    }
    private void generateList(TextWriter w, Namespace ns) throws IOException {
    	final HashMap<String,List<Component>> componentList = new HashMap<String,List<Component>>(); 

loop:   for (Definition def : getRegistry().getDefinitions()) {
            if(def instanceof Component ) {
                if(!def.isAbstract() && isGenerateDefinition(def)) {
                    if(!StringUtil.equals(def.getNamespace().getUri(),ns.getUri())) {
                        continue loop;
                    }
                    ComponentDesignerExtension compDef = (ComponentDesignerExtension)def.getFacesDefinition().getExtension(ElementConstants.DESIGNER_EXTENSION);
                    String cat = compDef.getCategoryId();
    	            if(StringUtil.isEmpty(cat)) {
    	            	cat = "Uncategorized";
    	            }
                    List<Component> l = componentList.get(cat);
                    if(l==null) {
                    	l = new ArrayList<Component>();
                    	componentList.put(cat,l);
                    }
                    l.add((Component)def);
                }
            }
        }
        if(componentList.size()>0) {
        	// List of categories and sort them
        	String[] cats = componentList.keySet().toArray(new String[componentList.size()]);
        	(new QuickSort.StringArray(cats,true)).sort();

            w.prtln("<table width='{0}px'>", Integer.toString(ncol*colw));
            w.prtln("<tr class='TableHeader' width='100%'>");
            w.prtln("<td colspan={0} width='100%' class='TableCellHeader'>",Integer.toString(ncol));
            w.prtln("{0}",w.encode(ns.getDescription()));
            w.prtln("</td>");
            w.prtln("</tr>");
        	for(int cc=0; cc<cats.length; cc++) {
                List<Component> list = componentList.get(cats[cc]);

	            int nr=list.size()/ncol;
	            //below is to get around a rounding bug
	            if ((list.size() % ncol) > 0){
	                nr++;
	            }

	            w.prtln("<tr class='TableHeader' width='100%'>");
	            w.prtln("<td colspan={0} width='100%' class='TableCategoryCell'>",Integer.toString(ncol));
	            String cat = cats[cc];
	            w.prtln("{0}",cat);
	            w.prtln("</td>");
	            w.prtln("</tr>");
	            
	            int idx = 0;
	            for( int i=0; i<nr; i++ ) {
	                w.prtln("<tr width='100%'>");
	                for( int j=0; j<ncol; j++ ) {
	                    w.prtln("<td width='{0}%' class='TableCell'>", Integer.toString(100/ncol));
	                    if(idx<list.size()) {
	                        if( genHierDefinition(w,0,list.get(idx)) ) {
	                            idx++;
	                        }
	                    }
	                    w.prtln("</td>");
	                }
	                w.prtln("</tr>");
	            }
	        }
        	
            w.prtln("</table>");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Complex Properties page
    protected void generateComplexListPage(TextWriter w) throws IOException {
        htmlHeader(w);
        //w.prtln("<div class='title'>Hierarchy</div>");
        w.prtln("<h1>Complex Properties</h1>");
        

        if(getOptions().includeStandard) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_CORE); 
        	if(n!=null) {
        		generateComplexList(w,n);
        	}
        }
        if(getOptions().includeExtLib) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_COREEX); 
        	if(n!=null) {
        		generateComplexList(w,n);
        	}
        }
        if(getOptions().includeCustom) {
        	Namespace n = getRegistry().getNamespace(Namespace.LIB_CUSTOM); 
        	if(n!=null) {
        		generateComplexList(w,n);
        	}
        }
        if(getOptions().includeOthers) {
        	ArrayList<Namespace> nsList = getRegistry().getNamespaces();
        	for(int i=0; i<nsList.size(); i++) {
        		Namespace n = nsList.get(i);
        		if(!n.isStandard()) {
        			generateComplexList(w,n);
        		}
        	}
        }
        
        htmlFooter(w);
    }
    private void generateComplexList(TextWriter w, Namespace ns) throws IOException {
    	final IdentityHashMap<ComplexType.Category,List<ComplexType>> complexList = new IdentityHashMap<ComplexType.Category,List<ComplexType>>(); 

loop:   for (Definition def : getRegistry().getDefinitions()) {
            if(def instanceof ComplexType ) {
                if(!def.isAbstract() && isGenerateDefinition(def)) {
                    if(!StringUtil.equals(def.getNamespace().getUri(),ns.getUri())) {
                        continue loop;
                    }
                    ComplexType.Category cat = ((ComplexType)def).getCategory();
                    List<ComplexType> l = complexList.get(cat);
                    if(l==null) {
                    	l = new ArrayList<ComplexType>();
                    	complexList.put(cat,l);
                    }
                    l.add((ComplexType)def);
                }
            }
        }

        if(complexList.size()>0) {
            w.prtln("<table width='{0}px'>", Integer.toString(ncol*colw));
            w.prtln("<tr class='TableHeader' width='100%'>");
            w.prtln("<td colspan={0} width='100%' class='TableCellHeader'>",Integer.toString(ncol));
            w.prtln("{0}",w.encode(ns.getDescription()));
            w.prtln("</td>");
            w.prtln("</tr>");
        	for(int cc=0; cc<ComplexType.categories.length; cc++) {
        		// Ignore if nothing inside
        		if(!complexList.containsKey(ComplexType.categories[cc])) {
        			continue;
        		}
                List<ComplexType> list = complexList.get(ComplexType.categories[cc]);

	            int nr=list.size()/ncol;
	            //below is to get around a rounding bug
	            if ((list.size() % ncol) > 0){
	                nr++;
	            }

	            w.prtln("<tr class='TableHeader' width='100%'>");
	            w.prtln("<td colspan={0} width='100%' class='TableCategoryCell'>",Integer.toString(ncol));
	            String cat = ComplexType.categories[cc].title;
	            w.prtln("{0}",cat);
	            w.prtln("</td>");
	            w.prtln("</tr>");
	            
	            int idx = 0;
	            for( int i=0; i<nr; i++ ) {
	                w.prtln("<tr width='100%'>");
	                for( int j=0; j<ncol; j++ ) {
	                    w.prtln("<td width='{0}%' class='TableCell'>", Integer.toString(100/ncol));
	                    if(idx<list.size()) {
	                        if( genHierDefinition(w,0,list.get(idx)) ) {
	                            idx++;
	                        }
	                    }
	                    w.prtln("</td>");
	                }
	                w.prtln("</tr>");
	            }
	        }
        	
            w.prtln("</table>");
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Hierarchy page
    protected void generateHierarchyPage(TextWriter w, boolean all) throws IOException {
        htmlHeader(w);
        //w.prtln("<div class='title'>Hierarchy</div>");
        w.prtln("<h1>Hierarchies</h1>");

        w.prtln("<table>");

        w.prtln("<tr class='TableHeader'>");
        w.prtln("<td class='TableCellHeader'>");
          w.prtln("<h2>Controls</h2>");
        w.prtln("</td>");
        w.prtln("<td class='TableCellHeader'>");
          w.prtln("<h2>Complex Types</h2>");
        w.prtln("</td>");
        if(getOptions().includeCustom) {
        	w.prtln("<td class='TableCellHeader'>");
        	  w.prtln("<h2>Custom controls</h2>");
        	w.prtln("</td>");
        }
        w.prtln("</tr>");
        
        w.prtln("<tr>");
        w.prtln("<td class='TableCell'>");
        for (Definition def : getRegistry().getHierarchicalDefinitions()) {
            if(def instanceof Component ) {
                genHierEntry(w,0,def,all,null,null);
            }
        }
        w.prtln("</td>");
        w.prtln("<td class='TableCell'>");
            for (Definition def : getRegistry().getHierarchicalDefinitions()) {
                if(def instanceof ComplexType ) {
                    genHierEntry(w,0,def,all);
                }
            }
        w.prtln("</td>");
        if(getOptions().includeCustom) {
	        w.prtln("<td  class='TableCell'>");
	            w.prtln("<b>Composite Controls</b><br>");
	            for (Definition def : getRegistry().getHierarchicalDefinitions()) {
	                if(def instanceof CompositeComponent ) {
	                    genHierEntry(w,0,def,all);
	                }
	            }
	        w.prtln("</td>");
        }
        w.prtln("</tr>");
        
        htmlFooter(w);
    }
    private void genHierEntry(TextWriter w, int level, Definition def, boolean all) throws IOException {
        genHierEntry(w,level,def,all,null,null);
    }
    private void genHierEntry(TextWriter w, int level, Definition def, boolean all, String[] uris, String ns) throws IOException {
        // If we are only displaying specific URIs, return if it is not the current one 
        if( level>0 && uris!=null ) {
            if(!isShowDefinition(def,uris,ns)) {
                return;
            }
        }

        // Stop if the definition is not meant to be shown
        if(!isGenerateDefinition(def)) {
            return;
        }
        
        if(all || !def.isAbstract()) {
        	genHierDefinition(w,level,def);
        	w.prtln("<br>");
        	level++;
        }
        for( Iterator<Definition> it=def.getChildren(); it.hasNext(); ) {
            Definition child = it.next();
            genHierEntry(w,level,child,all,uris,ns);
        }
    }
    private boolean isShowDefinition(Definition def, String[] uris, String ns) {
        // If there is at least a descendant to show, we have to show it...
        for( Iterator<Definition> it=def.getChildren(); it.hasNext(); ) {
            Definition child = it.next();
            if(isShowDefinition(child,uris,ns)) {
                return true;
            }
        }
        
        // Else, look if we have to show it...
        if(StringUtil.isEmpty(ns)) {
            for( int i=0; i<uris.length; i++ ) {
                if(StringUtil.equals(def.getNamespace().getUri(),uris[i])) {
                    return false;
                }
            }
        } else {
            if(!StringUtil.equals(def.getNamespace().getUri(),ns)) {
                return false;
            }
        }
        return true;
    }
    private boolean genHierDefinition(TextWriter w, int level, Definition def) throws IOException {
        if(isGenerateDefinition(def)) {
            for(int i=0; i<level; i++ ) {
                w.prt("&nbsp;&nbsp;&nbsp;");
            }
            if(level>0) {
                w.prt("<img src='images/inherit.png'/>");
            }
            w.prt("<img src='{0}'/>", def.isAbstract() ? "images/interface.png" : "images/class.png");
            genHierDefinitionLink(w,def); 
            
            return true;
        }
        return false;
    }
    private void genHierDefinitionLink(TextWriter w, Definition def) throws IOException {
        w.prtln("<a href={0}>{1}</a>", def.getFileName()+".html", def.getDisplayName());
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Component page
    protected void generateComponent(TextWriter w, Component c) throws IOException {
        htmlHeader(w);
        genTitle(w,"Control",c);
        genHierarchy(w,c);
        genDefinition(w,c);
        genPropertyList(w,c);
        genJava(w,c);
        genPropertyDetails(w,c);
        htmlFooter(w);
    }

    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Complex Type page
    protected void generateComplexType(TextWriter w, ComplexType c) throws IOException {
        htmlHeader(w);
        genTitle(w,"Complex Type",c);
        genHierarchy(w,c);
        genDefinition(w,c);
        genPropertyList(w,c);
        genJava(w,c);
        genPropertyDetails(w,c);
        htmlFooter(w);
    }
    

    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// Composite control page
    protected void generateComposite(TextWriter w, CompositeComponent c) throws IOException {
        htmlHeader(w);
        genTitle(w,"Custom Control",c);
        genHierarchy(w,c);
        genDefinition(w,c);
        genPropertyList(w,c);
        genJava(w,c);
        genPropertyDetails(w,c);
        htmlFooter(w);
    }


    private void genTitle(TextWriter w, String title, Definition def) throws IOException {
        w.prt("<p>");
        w.prt("<img src='{0}'/>", def.isAbstract() ? "images/biginterface.png" : "images/bigclass.png");
        w.prt("<span class='DefinitionTitle'>{0}:&nbsp;</span>", title );
        w.prt("<span class='DefinitionTitle-1'>{0}</span>", def.getDisplayName() );
        w.prtln("</p>");
    }

    private void genHierarchy(TextWriter w, Definition def) throws IOException {
        ArrayList<Definition> list = new ArrayList<Definition>();
        for( Definition d=def; d!=null; d=d.getParent() ) {
            list.add(d);
        }
        int level = 0;
        for(int i=list.size()-1; i>=0; i-- ) {
            if( genHierDefinition(w,level,list.get(i)) ) {
                w.prtln("<br>");
                level++;
            }
        }
        for( Iterator<Definition> it=def.getChildren(); it.hasNext(); ) {
            Definition child=it.next();
            genHierEntry(w,level,child,false);
        }
    }

    private void genDefinition(TextWriter w, Definition def) throws IOException {
        DefinitionDesignerExtension ext = (DefinitionDesignerExtension) def.getFacesDefinition().getExtension(ElementConstants.DESIGNER_EXTENSION);
        if(ext!=null) {
            w.prtln("<h2>Description</h2>" );
            String desc = ext.getDescription();
            if(StringUtil.isNotEmpty(desc)) {
                w.prtln("<p>{0}</p>", w.encode(desc) );
            }
        }
    }
    
    private void genPropertyList(TextWriter w, Definition def) throws IOException {
        FacesDefinition fd = def.getFacesDefinition();
        
        ArrayList<String> catList = new ArrayList<String>();
        HashMap<String, ArrayList<FacesProperty>> categories = new HashMap<String, ArrayList<FacesProperty>>();
        for (FacesProperty prop : RegistryUtil.getProperties(fd)) {
            
            String cat = DesignerExtensionUtil.getPropertyCategoryId(fd, prop);
            if(cat==null) {
                cat = "<none>";
            }
            
            ArrayList<FacesProperty> list = categories.get(cat);
            if(list==null) {
                list = new ArrayList<FacesProperty>();
                categories.put(cat,list);
                catList.add(cat);
            }
            list.add(prop);
        }

        (new QuickSort.JavaList(catList){
            public int compare(Object o1, Object o2) {
                String c1 = (String)o1;
                String c2 = (String)o2;
                return c1.compareToIgnoreCase(c2);
            }            
        }).sort();
        
        w.prtln("<h2>Properties</h2>" );
                
        w.prtln("<ul>" );
        if(!def.isAbstract()) {
            w.prtln("<li class='ListAttributeClass'><span class='Prop-Tag'>&lt;<b>{0}:{1}</b></span></li>", def.getNamespace().getPrefix(), fd.getTagName() );
        }

        for (String cat : catList) {
            ArrayList<FacesProperty> list = categories.get(cat);
            (new QuickSort.JavaList(list){
                public int compare(Object o1, Object o2) {
                    FacesProperty p1 = (FacesProperty)o1;
                    FacesProperty p2 = (FacesProperty)o2;
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }            
            }).sort();
            if(catList.size()>1 || !cat.equals("zzz")) {
                String catLabel = cat.equals("zzz") ? "&lt;not categorized&gt;" : cat;
                w.prtln("<li class='ListAttributeCategory'><span class='Prop-Category'>{0}</span></li>",catLabel);
            }
            
            for(int i=0; i<list.size(); i++ ) {
                FacesProperty prop = list.get(i);
                w.prtln("<li class='ListAttribute'><span class='Prop-Attribute'><a href='#prop_{0}'>{1}</a></span></li>", prop.getName(), prop.getName());
            }
        }
        if(!def.isAbstract()) {
            w.prtln("<li class='ListAttributeClass'>    /&gt</li>" );
        }
        w.prtln("</ul>" );
    }
    private void genPropertyDetails(TextWriter w, Definition def) throws IOException {
        FacesDefinition fd = def.getFacesDefinition();
        
        ArrayList<String> catList = new ArrayList<String>();
        HashMap<String, ArrayList<FacesProperty>> categories = new HashMap<String, ArrayList<FacesProperty>>();
        for (FacesProperty prop : RegistryUtil.getProperties(fd)) {
            
            String cat = DesignerExtensionUtil.getPropertyCategoryId(fd, prop);
            if(cat==null) {
                cat = "<none>";
            }
            
            ArrayList<FacesProperty> list = categories.get(cat);
            if(list==null) {
                list = new ArrayList<FacesProperty>();
                categories.put(cat,list);
                catList.add(cat);
            }
            list.add(prop);
        }

        (new QuickSort.JavaList(catList){
            public int compare(Object o1, Object o2) {
                String c1 = (String)o1;
                String c2 = (String)o2;
                return c1.compareToIgnoreCase(c2);
            }            
        }).sort();
        
        
        w.prtln("<p>");
        w.prtln("<table width='900px'>");
        for( Iterator<String> it=catList.iterator(); it.hasNext(); ) {
            String cat = it.next();
            ArrayList<FacesProperty> list = categories.get(cat);
            
            w.prtln("<tr width='100%'>");
            w.prtln("<td>");
            String catLabel = cat.equals("zzz") ? "&lt;not categorized&gt;" : cat;
            w.prtln("<br><h2>{0}</h2>", w.encode(catLabel) );
            w.prtln("</td>");
            w.prtln("</tr>");

            for(Iterator<FacesProperty> it2=list.iterator(); it2.hasNext(); ) {
                FacesProperty prop = it2.next();
                DesignerExtension ext = (DesignerExtension)prop.getExtension(ElementConstants.DESIGNER_EXTENSION);
                
                w.prtln("<tr>");
                w.prtln("<td>");
    
                w.prtln("<a name='prop_{0}'></a>", prop.getName());
                w.prtln("<table width='100%' class='TableHeader'>");
                // Title row
                w.prtln("  <tr>");
                w.prtln("  <td colspan=2 class='TableCellHeader'>");
                w.prt("{0}, ",prop.getName());
                genPropertyType(w,prop);
                if( isInherited(fd,prop) ) {
                    w.prt("<span class='Inherited'>&nbsp;&nbsp;[Inherited]</span>",prop.getName());
                }
                
                w.prtln("  </td>");
                w.prtln("  </tr>");
                // Text row
                w.prtln("  <tr>");
                w.prtln("  <td width='60%' class='TableCell'>");
                if(ext!=null) {
                	String disp = ext.getDisplayName();
                	if(StringUtil.isNotEmpty(disp)) {
                		w.prtln("<b>{0}</b><br>",ext.getDisplayName());
                	}
                	String desc = ext.getDescription();
                	if(StringUtil.isNotEmpty(desc)) {
                		w.prtln("{0}",w.encode(ext.getDescription()));
                	}
                }
                if (ext instanceof PropertyDesignerExtension) { 
                	PropertyDesignerExtension pde = (PropertyDesignerExtension) ext; 
                	String editorParams = pde.getEditorParameter(); 
                	if (StringUtil.isNotEmpty(editorParams)) { 
                		w.prtln("<br/><i>Legal Values: </i>{0}", StringUtil.removeLineBreaks(w.encode(editorParams))); 
                	} 
                } 
                w.prtln("  </td>");

                w.prtln("  <td width='40%' style='vertical-align: top'>");
                w.prtln("<table width='100%' class='TableProp'>");
                addProperty(w,"Required Attribute",prop.isRequired());
                if( prop instanceof FacesSimpleProperty ) {
                    FacesSimpleProperty fs = (FacesSimpleProperty)prop;
                    addProperty(w,"Localizable",fs.isLocalizable());
                    addProperty(w,"Allow Runtime Binding",fs.isAllowRunTimeBinding());
                    addProperty(w,"Allow Load Time Binding",fs.isAllowLoadTimeBinding());
                    addProperty(w,"Allow Non Binding",fs.isAllowNonBinding());
                }
                
                w.prtln("</table>");
                w.prtln("</td>");
                
                w.prtln("</tr>");
                w.prtln("</table>");
            }
        }
        w.prtln("</table>");
        w.prtln("</p>");
    }
    private void addProperty(TextWriter w, String title, boolean value) throws IOException {
        addProperty(w,title,value?"true":"false");
    }
    private void addProperty(TextWriter w, String title, String value) throws IOException {
        w.prtln("    <tr>");
        w.prtln("    <td class='TablePropCell' width='70%'>");
        w.prtln("        <i>{0}</i>", w.encode(title));
        w.prtln("    </td>");
        w.prtln("    <td  class='TablePropCell' width='30%'>");
        w.prtln("        {0}", w.encode(value));
        w.prtln("    </td>");
        w.prtln("    </tr>");
    }
    
    private void genPropertyType(TextWriter w, FacesProperty p) throws IOException {
        if( p instanceof FacesSimpleProperty ) {
            FacesSimpleProperty sp = (FacesSimpleProperty)p;
            w.prt("<span class='Type'>");
            switch(sp.getType()) {
                case FacesSimpleTypes.TYPE_CHAR:     w.prt("char");      break;
                case FacesSimpleTypes.TYPE_BYTE:     w.prt("byte");      break;
                case FacesSimpleTypes.TYPE_SHORT:    w.prt("short");     break;
                case FacesSimpleTypes.TYPE_INT:      w.prt("int");       break;
                case FacesSimpleTypes.TYPE_LONG:     w.prt("long");      break;
                case FacesSimpleTypes.TYPE_FLOAT:    w.prt("float");     break;
                case FacesSimpleTypes.TYPE_DOUBLE:   w.prt("double");    break;
                case FacesSimpleTypes.TYPE_BOOLEAN:  w.prt("boolean");   break;
                case FacesSimpleTypes.TYPE_STRING:   w.prt("string");    break;

                case FacesSimpleTypes.TYPE_GENERIC:  w.prt("generic");    break;
                
                case FacesSimpleTypes.TYPE_OBJECT: {
                    Definition def = getRegistry().find(sp.getTypeDefinition());
                    if(def!=null) {
                        genHierDefinitionLink(w,def);
                    } else {
                        w.prt("object");
                    }
                    Class<?> c = p.getJavaClass();
                    if(c!=null) {
                        w.prt(" - {0}", w.encode(c.getName()));
                    }
                }
            }
            w.prt("</span>");
        } if( p instanceof FacesContainerProperty ) {
            w.prt("Collection of ");
            FacesProperty ip = ((FacesContainerProperty)p).getItemProperty();
            genPropertyType(w,ip);
        }
    }

    private void genJava(TextWriter w, Definition def) throws IOException {
        FacesDefinition fd = def.getFacesDefinition();
        Class<?> c = fd.getJavaClass();
        if(c!=null) {
        	w.prtln("<h2>Java</h2>" );
        	w.prtln("<p>Java Class: <code>{0}</code></p>", w.encode(c.getName()) );
        }
    }
    
    private boolean isInherited(FacesDefinition def, FacesProperty prop) {
        return ! def.isDefinedProperty(prop.getName()); 
    }
    
    
    // Helpers
    private void htmlHeader(TextWriter w) throws IOException {
        w.prtln("<html>");
        w.prtln("<link rel='stylesheet' href='css/registry.css' type='text/css'>");
        
        w.prtln("<div class='MainTitle'>");
        w.prtln("<img src='images/xpages.png'>");
        w.prtln("<span class='MainTitleText'> Controls Documentation</span>");
        w.prtln("</div>");

        w.prtln("<p>");
        w.prtln("<a href='Controls.html'>Controls</a>&nbsp;");
        w.prtln("<a href='Complex.html'>Complex Types</a>&nbsp;");
        w.prtln("<a href='Hierarchy.html'>Show Hierarchies</a>&nbsp;");
        w.prtln("<a href='Hierarchy.all.html'>Show Full Hierarchies</a>&nbsp;");
        w.prtln("</p>");
    }
    private void htmlFooter(TextWriter w) throws IOException {
        w.prtln("</html>");
    }
}
