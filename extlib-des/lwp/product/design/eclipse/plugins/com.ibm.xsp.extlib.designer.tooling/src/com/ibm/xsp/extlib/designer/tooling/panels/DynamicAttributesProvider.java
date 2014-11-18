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
package com.ibm.xsp.extlib.designer.tooling.panels;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.w3c.dom.Node;

import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.panels.XPagesAVFolder;
import com.ibm.designer.domino.xsp.api.panels.XPagesAVPage;
import com.ibm.designer.domino.xsp.api.panels.XPagesAttributesProvider;
import com.ibm.designer.domino.xsp.api.util.XPagesKey;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;

@SuppressWarnings("restriction") // $NON-NLS-1$
abstract public class DynamicAttributesProvider extends XPagesAttributesProvider {
    
    protected boolean _needToRebuild = false;
    protected Set<INodeNotifier> _nodesListenedTo = new HashSet<INodeNotifier>();
    protected static String DATA_CATEGORY_OBJECT = "propprovcat"; //$NON-NLS-1$
    
    
    INodeAdapter _listener = new INodeAdapter() {
        public boolean isAdapterForType(Object type) {
            return type instanceof DynamicAttributesProvider;
        }

        /**
         * INodeNotifier.ADD, REMOVE, CHANGE, STRUCTURE_CHANGED, CONTENT_CHANGED
         */
        public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue,
                int pos) {
            if (shouldRebuildTabs(notifier, eventType, changedFeature, oldValue, newValue, pos)) {
                  setRebuild();
            }
        }
    };
    
   
    abstract protected boolean shouldRebuildTabs(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos);
    abstract protected String getExtensionTagName(XPagesKey key); 
    abstract protected Set<INodeNotifier> getNodeNotifiers(Node node); 
    
    private void setRebuild() {
        Node node = getNode();
        if (null != node) {
            node.setUserData(DATA_CATEGORY_OBJECT, new Object(), null);
        }
        _needToRebuild = true;
    }

    /* (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.panels.XPagesAttributesProvider#getCategoryID()
     */
    @Override
    public String getCategoryID() {
        Node node = getNode();
        if (null != node) {
            Object o = node.getUserData(DATA_CATEGORY_OBJECT);
            if (null != o)
                return String.valueOf(o.hashCode());
        }
        if (_needToRebuild) {
            removeNodeListeners();
            _needToRebuild = false;
        }
        return super.getCategoryID();
    }
    
    /* (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.panels.XPagesAttributesProvider#doGetAVPagesForTag(com.ibm.designer.domino.xsp.api.util.XPagesKey, com.ibm.designer.domino.xsp.api.panels.XPagesAVFolder)
     */
    @Override
    protected XPagesAVPage[] doGetAVPagesForTag(XPagesKey key, XPagesAVFolder folder) {
        
        setupNodeAdapters();
        
        XPagesAVPage[] av = null;
        try {
            String keyname = getExtensionTagName(key);
            if (StringUtil.isNotEmpty(keyname)) {
                XPagesKey otherKey = new XPagesKey(key.getNamespaceUri(), keyname);
                av = super.doGetAVPagesForTag(otherKey, folder);
            }
        }
        catch(Exception e) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
        }
          
        if (av == null || av.length == 0)             
            av = super.doGetAVPagesForTag(key, folder);
          
         return av;
    }
    
    
    private void setupNodeAdapters() {
        Set<INodeNotifier> set = getNodeNotifiers(getNode());
        if (null == set) 
            return;
        
        for (Iterator<INodeNotifier> it = set.iterator(); it.hasNext();) {
            addNodeAdapter(it.next());
        }
        
    }
    
    
    protected void addNodeAdapter(INodeNotifier nn) {
        
        INodeAdapter na = nn.getAdapterFor(this);
        if (na != null) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.traceDebugp(this.getClass(), "addNodeAdapter", "already has a noe listsner"); //$NON-NLS-1$ //$NON-NLS-2$
            nn.removeAdapter(na);
        }
        
        nn.addAdapter(_listener);
        _nodesListenedTo.add(nn);
    }
    
    
    private void removeNodeListeners() {
        
        for (Iterator<INodeNotifier> it = _nodesListenedTo.iterator(); it.hasNext();) {
            INodeNotifier nn = it.next();
            nn.removeAdapter(_listener);
        }
        _nodesListenedTo.clear();
    }
}