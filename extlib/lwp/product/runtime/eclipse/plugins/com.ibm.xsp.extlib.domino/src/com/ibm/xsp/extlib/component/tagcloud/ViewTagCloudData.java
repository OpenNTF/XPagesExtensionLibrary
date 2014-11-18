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
package com.ibm.xsp.extlib.component.tagcloud;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.commons.util.ArrayIterator;
import com.ibm.commons.util.IteratorWrapper;
import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.complex.ValueBindingObjectImpl;
import com.ibm.xsp.domino.app.tagcloud.TagCloud;
import com.ibm.xsp.domino.app.tagcloud.TagCloudEntry;
import com.ibm.xsp.domino.app.tagcloud.TagCloudFactory;


/**
 * Tag Cloud Data coming from a Domino view.
 */
public class ViewTagCloudData extends ValueBindingObjectImpl implements ITagCloudData {

    private String viewName;
    private Integer categoryColumn;
    private String sortTags;
    private Integer maxTagLimit;
    private Integer tagThreshold;
    private Integer minEntryCount;

    private String cacheMode;
    private Integer cacheRefreshInterval;

    private String linkTargetPage;
    private String linkRequestParam;
    private String linkMetaSeparator;

    public ViewTagCloudData() {
    }
    
    public String getViewName() {
        if (null != this.viewName) {
            return this.viewName;
        }
        ValueBinding vb = getValueBinding("viewName"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
    
    public int getCategoryColumn() {
        if (null != this.categoryColumn) {
            return this.categoryColumn;
        }
        ValueBinding vb = getValueBinding("categoryColumn"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 0;
    }
    public void setCategoryColumn(int categoryColumn) {
        this.categoryColumn = categoryColumn;
    }
    
    public String getSortTags() {
        if (null != this.sortTags) {
            return this.sortTags;
        }
        ValueBinding vb = getValueBinding("sortTags"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }
    public void setSortTags(String sortTags) {
        this.sortTags = sortTags;
    }

    public int getMaxTagLimit() {
        if (null != this.maxTagLimit) {
            return this.maxTagLimit;
        }
        ValueBinding vb = getValueBinding("maxTagLimit"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 50;
    }
    public void setMaxTagLimit(int maxTagLimit) {
        this.maxTagLimit = maxTagLimit;
    }

    public int getTagThreshold() {
        if (null != this.tagThreshold) {
            return this.tagThreshold;
        }
        ValueBinding vb = getValueBinding("tagThreshold"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 5;
    }
    public void setTagThreshold(int tagThreshold) {
        this.tagThreshold = tagThreshold;
    }

    public int getMinEntryCount() {
        if (null != this.minEntryCount) {
            return this.minEntryCount;
        }
        ValueBinding vb = getValueBinding("minEntryCount"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return -1;
    }
    public void setMinEntryCount(int minEntryCount) {
        this.minEntryCount = minEntryCount;
    }

    public String getCacheMode() {
        if (null != this.cacheMode) {
            return this.cacheMode;
        }
        ValueBinding vb = getValueBinding("cacheMode"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }
    public String getEffectiveCacheMode(){
        String mode = getCacheMode();
        if( StringUtil.isEmpty(mode) ){
            mode = "auto"; //$NON-NLS-1$
        }
        return mode;
    }
    public void setCacheMode(String cacheMode) {
        this.cacheMode = cacheMode;
    }

    public int getCacheRefreshInterval() {
        if (null != this.cacheRefreshInterval) {
            return this.cacheRefreshInterval;
        }
        ValueBinding vb = getValueBinding("cacheRefreshInterval"); //$NON-NLS-1$
        if (vb != null) {
            Number val = (Number) vb.getValue(getFacesContext());
            if(val!=null) {
                return val.intValue();
            }
        } 
        return 120;
    }
    public void setCacheRefreshInterval(int cacheRefreshInterval) {
        this.cacheRefreshInterval = cacheRefreshInterval;
    }

    public String getLinkTargetPage() {
        if (null != this.linkTargetPage) {
            return this.linkTargetPage;
        }
        ValueBinding vb = getValueBinding("linkTargetPage"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }
    public void setLinkTargetPage(String linkTargetPage) {
        this.linkTargetPage = linkTargetPage;
    }

    public String getLinkRequestParam() {
        if (null != this.linkRequestParam) {
            return this.linkRequestParam;
        }
        ValueBinding vb = getValueBinding("linkRequestParam"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }
    public void setLinkRequestParam(String linkRequestParam) {
        this.linkRequestParam = linkRequestParam;
    }

    public String getLinkMetaSeparator() {
        if (null != this.linkMetaSeparator) {
            return this.linkMetaSeparator;
        }
        ValueBinding vb = getValueBinding("linkMetaSeparator"); //$NON-NLS-1$
        if (vb != null) {
            return (String)vb.getValue(getFacesContext());
        } 
        return null;
    }
    public void setLinkMetaSeparator(String linkMetaSeparator) {
        this.linkMetaSeparator = linkMetaSeparator;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[12];
        state[0] = super.saveState(context);
        state[1] = viewName;
        state[2] = categoryColumn;
        state[3] = sortTags;
        state[4] = maxTagLimit;
        state[5] = tagThreshold;
        state[6] = minEntryCount;
        state[7] = cacheMode;
        state[8] = cacheRefreshInterval;
        state[9] = linkTargetPage;
        state[10] = linkRequestParam;
        state[11] = linkMetaSeparator;
        return state;
    }
    
    @Override
    public void restoreState(FacesContext context, Object value) {
        Object[] state = (Object[])value;
        super.restoreState(context, state[0]);
        this.viewName = (String) state[1];
        this.categoryColumn = (Integer) state[2];
        this.sortTags = (String) state[3];
        this.maxTagLimit = (Integer) state[4];
        this.tagThreshold = (Integer) state[5];
        this.minEntryCount = (Integer) state[6];
        this.cacheMode = (String) state[7];
        this.cacheRefreshInterval = (Integer) state[8];
        this.linkTargetPage = (String) state[9];
        this.linkRequestParam = (String) state[10];
        this.linkMetaSeparator = (String) state[11];
    }
    
    @SuppressWarnings("unchecked") // $NON-NLS-1$
    private Object getPropertyMapWrapper() {
        return new Map() {
            public Object get(Object key) {
                switch(((String)key).charAt(0)) {
                    case 'c': {
                        if(key.equals("categoryColumn")) { // $NON-NLS-1$
                            return getCategoryColumn();
                        }
                        if(key.equals("cache")) { // $NON-NLS-1$
                            return this;
                        }
                    } break;
                    // External database access not supported as of 8.5.3!
                    // Core will throw an exception if this case is enabled
                    // Needs a secure and performant means to access external db
//                  case 'd': {
//                      if(key.equals("database")) {
//                          return getDatabase();
//                      }
//                  } break;
                    case 'l': {
                        if(key.equals("links")) { // $NON-NLS-1$
                            return this;
                        }
                    } break;
                    case 'm': {
                        if(key.equals("maxTagLimit")) { // $NON-NLS-1$
                            return getMaxTagLimit();
                        }
                        if(key.equals("minEntryCount")) { // $NON-NLS-1$
                            return getMinEntryCount();
                        }
                        if(key.equals("mode")) { // $NON-NLS-1$
                            return getEffectiveCacheMode();
                        }
                        if(key.equals("metaSeparator")) { // $NON-NLS-1$
                            return getLinkMetaSeparator();
                        }
                    } break;
                    case 'v': {
                        if(key.equals("viewName")) { // $NON-NLS-1$
                            return getViewName();
                        }
                    } break;
                    case 'r': {
                        if(key.equals("refreshInterval")) { // $NON-NLS-1$
                            return getCacheRefreshInterval();
                        }
                        if(key.equals("requestParam")) { // $NON-NLS-1$
                            return getLinkRequestParam();
                        }
                    } break;
                    case 's': {
                        if(key.equals("sortTags")) { // $NON-NLS-1$
                            return getSortTags();
                        }
                    } break;
                    case 't': {
                        if(key.equals("tagThreshold")) { // $NON-NLS-1$
                            return getTagThreshold();
                        }
                        if(key.equals("targetPage")) { // $NON-NLS-1$
                            return getLinkTargetPage();
                        }
                    } break;
                }
                return null;
            }

            // Dummy not implemented methods
            public void clear() {}
            public boolean containsKey(Object key) {return false;}
            public boolean containsValue(Object value) {return false;}
            public Set entrySet() {return null;}
            public boolean isEmpty() {return false;}
            public Set keySet() {return null;}
            public Object put(Object key, Object value) {return null;}
            public void putAll(Map t) {}
            public Object remove(Object key) {return null;}
            public int size() {return 0;}
            public Collection values() {return null;}
        };
    }
    
    protected TagCloud getTagCloud() {
        return TagCloudFactory.getTagCloud(getPropertyMapWrapper());
    }
    
    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.tagcloud.ITagCloudData#getEntryCount()
     */
    public int getEntryCount() {
        TagCloud tg = getTagCloud();
        if(tg!=null) {
            return tg.getEntryCount();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see com.ibm.xsp.extlib.component.tagcloud.ITagCloudData#getEntryCountAsString()
     */
    public String getEntryCountAsString() {
        TagCloud tg = getTagCloud();
        if(tg!=null) {
            return tg.getEntryCountAsString();
        }
        return "0";
    }

    public void refresh() {
    }
    
    public ITagCloudEntries getEntries() {
        TagCloud tg = getTagCloud();
        if(tg!=null) {
            //TagCloudFactory.getTagCloud(compositeData);
            TagCloudEntry[] entries = tg.getEntries();
            return new Entries(entries);
        }
        return null;
    }
    
    private static class Entries implements ITagCloudEntries {
        private TagCloudEntry[] entries;
        private Entries(TagCloudEntry[] entries) {
            this.entries = entries;
        }
        @SuppressWarnings("unchecked") // $NON-NLS-1$
        public Iterator<ITagCloudEntry> getEntries() {
            return new IteratorWrapper<ITagCloudEntry>(new ArrayIterator<TagCloudEntry>(entries)) {
                @Override
                protected ITagCloudEntry wrap(Object o) {
                    final TagCloudEntry te = (TagCloudEntry)o;
                    return new ITagCloudEntry() {
                        public int getCount() {
                            return te.getCount();
                        }
                        public String getLabel() {
                            return te.getName();
                        }
                        public String getMetaData() {
                            return te.getMetaData();
                        }
                        public String getUrl() {
                            return te.getUrl();
                        }
                        public int getWeight() {
                            return te.getWeight();
                        }
                    };
                }
            };
        }
    }
}