/*
 * © Copyright IBM Corp. 2014
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
package com.ibm.xsp.extlib.designer.tooling.annotation;

import java.net.URL;

public class ExtLibLayoutExtension {

    private boolean _responsive = false;
    private URL     _image      = null;
    private URL     _sampleURL  = null;

    public boolean isResponsive() {
        return _responsive;
    }

    public void setResponsive(final boolean responsive) {
        this._responsive = responsive;
    }

    public URL getImage() {
        return _image;
    }

    public void setImage(final URL image) {
        this._image = image;
    }

    public URL getSampleURL() {
        return _sampleURL;
    }

    public void setSampleURL(final URL sampleURL) {
        this._sampleURL = sampleURL;
    }
    
}
