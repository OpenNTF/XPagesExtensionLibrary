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
package com.ibm.xsp.test.framework.registry.parse;

import com.ibm.xsp.registry.parse.ConfigParserHandler;
import com.ibm.xsp.registry.parse.property.IPropertyDetails;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 * 4 May 2007
 * Unit: ConfigParserFactory.java
 */ 
public final class StrictParserHandler extends ConfigParserHandler {
    
    private boolean ignoreUnpublishedExtensions = false;
    @Override
    public void handle() {
        // the name of the method that invoked this method
        StackTraceElement[] arr = Thread.currentThread().getStackTrace();
        String msg = arr[3].getClassName()+"."+arr[3].getMethodName()+"(..)";
        throw new StrictParserException(msg);
    }
    @Override
    public void handle(Throwable cause) {
        throw new StrictParserException(cause);
    }
    @Override
    protected void handled() {
        // the name of the method that invoked this method
        StackTraceElement[] arr = Thread.currentThread().getStackTrace();
        String msg = arr[3].getClassName()+"."+arr[3].getMethodName()+"(..)";
        throw new StrictParserException(msg);
    }
    /* (non-Javadoc)
     * @see com.ibm.xsp.registry.parse.ConfigParserHandler#handlePropertyExtensionNotPublished(java.lang.String, java.lang.String)
     */
    @Override
    public void handlePropertyExtensionNotPublished(String propertyName,
            String elementName) {
        if( !ignoreUnpublishedExtensions ){
            super.handlePropertyExtensionNotPublished(propertyName, elementName);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.registry.parse.ConfigParserHandler#handleCollectionNoPropertyAddMethod(com.ibm.xsp.registry.parse.property.IPropertyDetails)
     */
    @Override
    public void handleCollectionNoPropertyAddMethod(IPropertyDetails meta) {
        if( !ignoreUnpublishedExtensions ){
            super.handleCollectionNoPropertyAddMethod(meta);
        }
    }
    /**
     * @return the ignoreUnpublishedExtensions
     */
    public boolean isIgnoreUnpublishedExtensions() {
        return ignoreUnpublishedExtensions;
    }
    /**
     * @param ignoreUnpublishedExtensions the ignoreUnpublishedExtensions to set
     */
    public boolean setIgnoreUnpublishedExtensions(boolean ignoreUnpublishedExtensions) {
        boolean oldValue = this.ignoreUnpublishedExtensions;
        this.ignoreUnpublishedExtensions = ignoreUnpublishedExtensions;
        return oldValue;
    }
    
}