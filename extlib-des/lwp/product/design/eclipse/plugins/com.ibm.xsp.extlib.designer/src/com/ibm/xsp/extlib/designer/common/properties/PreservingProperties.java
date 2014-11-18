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
package com.ibm.xsp.extlib.designer.common.properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.ibm.commons.iloader.node.DataChangeNotifier;
import com.ibm.commons.util.FastStringBuffer;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.designer.DesignerExtensionLogger;


/**
 * @author mblout
 *
 */
public class PreservingProperties {
    
    String  propertiesAsString;
    
    final FastStringBuffer  fsb         = new FastStringBuffer();
    final Properties        javaioprops = new Properties();
    final ContentFacade     facade;
    final boolean           saveOnSet;  // if false, an explicit call to save() must be made to write 
    
    boolean rebuildjProps = false;
    boolean isDirty = false;
    
    String ENCODING = "ISO-8859-1"; //$NON-NLS-1$
    
   
    public interface ContentFacade {
        String      getName     ();
        InputStream getContents ();
        void        setContents (InputStream in);
        void        append      (InputStream in);
    }


    public PreservingProperties(ContentFacade provider, boolean saveOnSet) {
        this.facade = provider;
        this.saveOnSet = saveOnSet;
        fsb.clear();
        loadAsString();
    }
    
    public String getName() {
        return facade.getName();
    }

    
    
    
    /**
     * in theory, this should only load once at the beginning, and from then on will be
     * maintained by the class
     *  
     */
    public boolean loadAsString() {
        
        boolean ok = true;
        InputStream in = null;
        try {
            in = facade.getContents();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                fsb.append(new String(b, 0, n));
            }
        }
        catch(Exception e) {
            ok = false;
            fsb.clear();
            DesignerExtensionLogger.CORE_LOGGER.error(e, "exception reading properties from stream"); // $NLE-PropertiesUtils.exceptionreadingpropertiesfromstream-1$
        }

        rebuildjProps = true;
        propertiesAsString = fsb.toString();
        return ok;
    }
    
    
    public String set(String key, String value) {
        return set(key, value, null);
    }
    
    public String set(String key, String value, DataChangeNotifier notifier) {
        
        int len = propertiesAsString.length();
        
        getProperties();
        
        isDirty = true;
        
        if (null == value) {
            javaioprops.remove(key);
        }
        else {
            javaioprops.setProperty(key, value);
        }
        // @TODO: this is copied from XSPPropBeanLoader - need to refactor
        boolean bRemove = !javaioprops.containsKey(key);
        String keyNameEquals = key + "="; //$NON-NLS-1$
        fsb.clear();
        int loc = StringUtil.indexOfIgnoreCase(propertiesAsString, keyNameEquals);
        // need to add the property
        if (loc == -1 && !bRemove) {
            fsb.append(propertiesAsString);
            if (!propertiesAsString.endsWith("\n")) //$NON-NLS-1$
                fsb.append("\r\n"); //$NON-NLS-1$
            fsb.append(keyNameEquals);
            fsb.append(value);
            fsb.append("\r\n"); //$NON-NLS-1$
            //@todo: call notifier methods in PreservingProperties
//            if (null != notifier)
//                notifier.notifyItemInserted(this, member, position, exclude)
        }
        else if (bRemove) {
            int nextLF = StringUtil.indexOfIgnoreCase(propertiesAsString, "\r", loc); //$NON-NLS-1$
            // always take up to this property
            fsb.append(propertiesAsString.substring(0, loc)); 
            // if we're at the end, we're done, otherwise, get everything *after* this prop
            if (nextLF != -1) {
                if ((nextLF+2) < len)
                    fsb.append(propertiesAsString.substring(nextLF+2)); 
            }
        }
        else {  // need to modify the property
            loc += keyNameEquals.length();
            // property starts after the equals...
            // need to find out where it ends (next line feed)
            int nextLF = StringUtil.indexOfIgnoreCase(propertiesAsString, "\r", loc); //$NON-NLS-1$
            String rest = null;
            if (nextLF != -1)
                rest = propertiesAsString.substring(nextLF);
            fsb.append(propertiesAsString.substring(0, loc));
            fsb.append(value);
            if (rest != null)
                fsb.append(rest);
        }
        
        rebuildjProps = true;
        propertiesAsString = fsb.toString();
        
        // persist
        if (saveOnSet) {
            save();
        }
        
        return propertiesAsString;
    }
    
    
    /**
 
     * @return
     */
    
    public Properties getProperties() {
        if (rebuildjProps) { 
            
            ByteArrayInputStream bs = null;
            try{
                byte[] bytes = propertiesAsString.getBytes(ENCODING);
                bs = new ByteArrayInputStream(bytes);
                javaioprops.clear();
                try {
                    javaioprops.load(bs);
                } catch (IOException e) {
                    DesignerExtensionLogger.CORE_LOGGER.error(e, "exception reading properties"); // $NLE-PropertiesUtils.exceptionreadingproperties-1$
                }
            }
            catch(UnsupportedEncodingException e) {
                DesignerExtensionLogger.CORE_LOGGER.error(e, "exception reading properties"); // $NLE-PropertiesUtils.exceptionreadingproperties-1$
            }
            finally{
                if(bs != null){
                    try {
                        bs.close();
                    } catch (IOException e) {
                        DesignerExtensionLogger.CORE_LOGGER.error(e, "exception closing properties stream"); // $NLE-PropertiesUtils.exceptionclosingpropertiesstream-1$
                    }
                }
            }
        }
        rebuildjProps = false;
        return javaioprops;
    }
    
    /**
     * 
     * @return
     */
    public boolean isDirty() {
        return isDirty;
    }
    
    /**
     * 
     */
    public void save() {
        try {
            byte[] bytes = propertiesAsString.getBytes(ENCODING);
            InputStream in = new ByteArrayInputStream(bytes);
            facade.setContents(in);
            isDirty = false;
        }
        catch(UnsupportedEncodingException e) {
            DesignerExtensionLogger.CORE_LOGGER.error(e, "exception writing properties"); // $NLE-PropertiesUtils.exceptionwritingproperties-1$
        }
    }

}