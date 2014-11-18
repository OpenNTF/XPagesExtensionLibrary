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

import com.ibm.commons.util.FastStringBuffer;
import com.ibm.commons.util.StringUtil;


/**
 * @author mblout
 *
 */
public class XSPPropertiesUtils {
    final FastStringBuffer fsb = new FastStringBuffer(); 
    
    public String setPropertiesAsString(String docContents, String keyName, String value, java.util.Properties jProps) {
        int dcLen = docContents.length();
        boolean bRemove = !jProps.containsKey(keyName);
        String keyNameEquals = keyName + "="; //$NON-NLS-1$
        fsb.clear();
        // need to make the value charset safe, etc.
        if (!bRemove) {
        	value = formatValue(value, true);
        	fsb.clear();
        }
        int loc = StringUtil.indexOfIgnoreCase(docContents, keyNameEquals);
        // need to add the property
        if (loc == -1 && !bRemove) {
            fsb.append(docContents);
            if (!docContents.endsWith("\n")) // $NON-NLS-1$
                fsb.append("\r\n"); // $NON-NLS-1$
            fsb.append(keyNameEquals);
            fsb.append(value);
            fsb.append("\r\n"); // $NON-NLS-1$
        }
        else if (bRemove) {
        	// shouldn't happen, but handle being told to remove something that's not there
        	if (loc == -1)
        		fsb.append(docContents.substring(0));
        	else {
	            int nextLF = StringUtil.indexOfIgnoreCase(docContents, "\r", loc); // $NON-NLS-1$
	            // always take up to this property
	            fsb.append(docContents.substring(0, loc)); 
	            // if we're at the end, we're done, otherwise, get everything *after* this prop
	            if (nextLF != -1) {
	                if ((nextLF+2) < dcLen)
	                    fsb.append(docContents.substring(nextLF+2)); 
	            }
        	}
        }
        else {  // need to modify the property
            loc += keyNameEquals.length();
            // property starts after the equals...
            // need to find out where it ends (next line feed)
            int nextLF = StringUtil.indexOfIgnoreCase(docContents, "\r", loc); // $NON-NLS-1$
            String rest = null;
            if (nextLF != -1)
                rest = docContents.substring(nextLF);
            fsb.append(docContents.substring(0, loc));
            fsb.append(value);
            if (rest != null)
                fsb.append(rest);
        }
        return fsb.toString();
    }
    
    private String formatValue(String s, boolean isKey) {
        int len = s.length();
        for(int i=0; i<len; i++) {
            char c = s.charAt(i);
            switch(c) {
				case ' ': {
				    if(i == 0 || isKey) { 
				    	fsb.append('\\');
				    }
				    fsb.append(' ');
				} break;
                case '\\': 
                case '\t': 
                case '\n': 
                case '\r': 
                case '\f': 
                case '#': 
                case '=': 
                case ':': {
                	fsb.append('\\');
                	fsb.append(c);
				} break;
                default: {
                    if ((c < 0x0020) || (c > 0x007e)) {
                    	fsb.append('\\');
                    	fsb.append('u');
                    	fsb.append(StringUtil.toUnsignedHex(c,4));
                    } else {
                        fsb.append(c);
                    }
				} break;
            }
        }
        return fsb.toString();
    }
    
//    public static class PropertyReadError extends Error {
//        private static final long serialVersionUID = 1L;
//        
//        public PropertyReadError() {
//            super();
//        }
//        public PropertyReadError(Throwable t) {
//            super(t);
//        }
//    }
//    
    
    final private static XSPPropertiesUtils instance = new XSPPropertiesUtils();
    
    private XSPPropertiesUtils() {
    };
    
    /**
     * Singleton, private constructor 
     * @return
     */
    public static XSPPropertiesUtils instance() {
        return instance;
    }
//
//    /**
//     * 
//     * @param dproject
//     * @param name
//     * @return
//     */
//    public String getProperty(DesignerProject dproject, String name) throws PropertyReadError {
//        String val = null;
//        Properties props = readProperties(dproject);
//        if (null != props) {
//            val = props.getProperty(name);
//        }
//        else {
//            throw new PropertyReadError();
//        }
//        return val;
//    }
//    
//    
//    /**
//     * 
//     * @param name
//     * @param value
//     */
//    public boolean setPropertyIFFDoesNotExist(DesignerProject dproject, String name, String value) {
//        try {
//            String test = getProperty(dproject, name);
//            if (null != test) {
//                return false;
//            }
//        }
//        catch(Exception e) {
//            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn(e, "error setting xsp.properties " + name); //$NLX-XSPPropertiesUtils.errorsettingxspproperties-1$
//            return false;
//        }
//        
//        IFile file = getFile(dproject);
//        InputStream in = null;
//        try {
//            in = file.getContents();
//            
//            FastStringBuffer fsb = new FastStringBuffer(2000);
//            byte[] b = new byte[4096];
//            for (int n; (n = in.read(b)) != -1;) {
//                fsb.append(new String(b, 0, n));
//            }
//            String contents = fsb.toString();
//
//            if (!contents.endsWith("\n")) //$NON-NLS-1$
//                fsb.append("\r\n"); //$NON-NLS-1$
//            
//            fsb.append(name);
//            fsb.append(value);
//            fsb.append("\r\n"); //$NON-NLS-1$
//        }
//        catch(Exception e) {
//            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.warn(e, "error eading xsp.properties to set " + name); //$NLX-XSPPropertiesUtils.errorsettingxspproperties-1$
//            return false;
//        }
//        finally {
//            if (null != in) {
//                try {in.close();}
//                catch(Exception ine) {
//                    ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(ine, "error closing xsp.properties stream"); //$NLX-XSPPropertiesUtils.errorclosingpropertiesstream-1$
//                }
//            }
//        }
//        
//        return true;
//    }
//    
//    
//    private IFile getFile(DesignerProject dproject) {
//        return dproject.getProject().getFile("/WebContent/WEB-INF/xsp.properties"); //$NON-NLS-1$        
//    }
//    
//    
//    /**
//     * 
//     * @param dproject
//     * @return
//     */
//    public Properties readProperties(DesignerProject dproject) {
//        
//        Properties props = null;
//        
//        if (null == dproject || null == dproject.getProject()) {
//            return props;
//        }
//       
//        IFile file = getFile(dproject);
//        
//        if (null == file || !file.exists()) {
//            return props;
//        }
//        
//        InputStream propIS = null;
//        
//        try {
//            propIS = file.getContents();
//        } catch (CoreException e) {
//        }
//
//        props = new Properties();
//        if (propIS !=null) {
//            // Read the current file if it exists
//            try {
//                props.load(propIS);
//            } 
//            catch (IOException e) {
//                props = null;
//                ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, "error reading xsp.properties"); //$NLX-XSPPropertiesUtils.errorreadingxspproperties-1$
//            }
//        }
//        
//        return props;
//    }

}