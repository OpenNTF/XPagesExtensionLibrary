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
package com.ibm.xsp.extlib.designer.xspprops;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.ibm.commons.util.StringUtil;
import com.ibm.commons.util.io.ByteStreamCache;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;


/**
 * @author mleland
 *
 */
public class XSPAllProperties implements XSPAllPropertyConstants {
    private Properties ourProps = null;
    private IFile ourPropsFile = null;
    public final String FALSE_DEFVAL = "false"; // $NON-NLS-1$
    public final String TRUE_DEFVAL = "true"; // $NON-NLS-1$
    public final String FILE_ASYNC_DEFVAL = TRUE_DEFVAL;
    public final String DISCARD_JS_DEFVAL = TRUE_DEFVAL;
    public final String PAGE_REDIRECT_DEFVAL = TRUE_DEFVAL;
    public final String ALLOWZERO_DEFVAL = FALSE_DEFVAL;
    public final String FILTER_ACF = "acf"; // $NON-NLS-1$
    public final String FILTER_IDENTITY = "identity"; // $NON-NLS-1$
    public final String FILTER_STRIPTAGS = "striptags"; // $NON-NLS-1$
    public final String FILTER_EMPTY = "empty"; // $NON-NLS-1$
    public final String TRANSIENT_SESSION_DEFVAL = FALSE_DEFVAL;
    public final String OLDCDSTYLE_DEFVAL = FALSE_DEFVAL;
    public final String USER_TZRT_DEFVAL = TRUE_DEFVAL;
    public final String GZIP_PERSIST_DEFVAL = FALSE_DEFVAL;
    public final static String SERVER_DEFVAL = "";
    public final static String APP_DEFVAL = "";
    public final static String MOBILE_DEFVAL = "";
    public final static String ROBOT_USER_AGENTS_DEFVAL = "";    
    private static final HashMap<String, String> keyAttrMap = new HashMap<String, String>(40);
    
    public static final QualifiedName XSP_DEPENDENCIES_PROP_INIT = new QualifiedName(XSP_LIBRARY_DEPENDENCIES, ".init"); // $NON-NLS-1$
    public static final QualifiedName XSP_DEPENDENCIES_PROP_FINAL = new QualifiedName(XSP_LIBRARY_DEPENDENCIES, ".final"); // $NON-NLS-1$

    public XSPAllProperties(DesignerProject project, IFile ourFile){
        ourPropsFile = ourFile;
        InputStream propIS = null;
        try{
            if (!ourPropsFile.exists()) {
                propIS = getDefaultContent();
            }
            else {
                try {
                    propIS = ourPropsFile.getContents();
                } catch (CoreException e) {
                }
            }
            ourProps = new Properties();
            if (propIS !=null) {
                // Read the current file if it exists
                try {
                    ourProps.load(propIS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }finally{
            if(propIS != null){
                try {
                    propIS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        initAttrKeyMap();
    }
    
    public InputStream getDefaultContent() {
        Properties defProps = new Properties();
        defProps.put(XSP_PERSISTENCE_MODE, XSP_PERSISTENCE_FILE);
        defProps.put(XSP_AJAX_WHOLE_TREE_RENDER, WHOLE_TREE_RENDER_DEFVAL);
        defProps.put(XSP_AGGREGATE_RESOURCES, XSP_AGGREGATE_RESOURCE_DEFNEWVAL);
        ByteStreamCache bsc = new ByteStreamCache();
        try {
            defProps.store(bsc.getOutputStream(), "");
        } catch (IOException e) {
            return null;
        }
        return bsc.getInputStream();
    }
    
    public String getErrorPage() {
        return ourProps.getProperty(ERROR_PAGE, SERVER_DEFVAL);
    }

    public void setErrorPage(String errorPage) {
        if (StringUtil.equals(SERVER_DEFVAL, errorPage))
            ourProps.remove(ERROR_PAGE);
        else
            ourProps.setProperty(ERROR_PAGE, errorPage);
    }
    
    public String getDefaultErrorPage() {
        return ourProps.getProperty(DEFAULT_ERROR_PAGE, DEFAULT_ERROR_PAGE_DEFVAL);
    }

    public void setDefaultErrorPage(String errorPage) {
        if (StringUtil.equals(errorPage, DEFAULT_ERROR_PAGE_DEFVAL))
            ourProps.remove(DEFAULT_ERROR_PAGE);
        else
            ourProps.setProperty(DEFAULT_ERROR_PAGE, errorPage);
    }
    
    public String getAppTimeout() {
        return ourProps.getProperty(APP_TIMEOUT, SERVER_DEFVAL);
    }
    
    public void setAppTimeout(String timeout) {
        if (StringUtil.equals(timeout, SERVER_DEFVAL))
            ourProps.remove(APP_TIMEOUT);
        else
            ourProps.setProperty(APP_TIMEOUT, timeout);
    }
    
    public String getSessionTimeout() {
        return ourProps.getProperty(SESSION_TIMEOUT, SERVER_DEFVAL);
    }
    
    public void setSessionTimeout(String timeout) {
        if (StringUtil.equals(timeout, SERVER_DEFVAL))
            ourProps.remove(SESSION_TIMEOUT);
        else
            ourProps.setProperty(SESSION_TIMEOUT, timeout);
    }
    
    public String getUploadMax() {
        return ourProps.getProperty(FILE_UPLOAD_MAXSIZE, SERVER_DEFVAL);
    }
    
    public void setUploadMax(String uploadMax) {
        if (StringUtil.equals(SERVER_DEFVAL, uploadMax))
            ourProps.remove(FILE_UPLOAD_MAXSIZE);
        else
            ourProps.setProperty(FILE_UPLOAD_MAXSIZE, uploadMax);
    }
    
    public String getUploadDir() {
        return ourProps.getProperty(FILE_UPLOAD_DIRECTORY);
    }
    
    public void setUploadDir(String uploadDir) {
        ourProps.setProperty(FILE_UPLOAD_DIRECTORY, uploadDir);
    }
    
    public String getPageEncoding() {
        return ourProps.getProperty(PAGE_ENCODING, SERVER_DEFVAL);
    }
    
    public void setPageEncoding(String encoding) {
        if (StringUtil.equals(encoding, SERVER_DEFVAL))
            ourProps.remove(PAGE_ENCODING);
        else
            ourProps.setProperty(PAGE_ENCODING, encoding);
    }
    
    public String getPageCompress() {
        return ourProps.getProperty(PAGE_COMPRESS, SERVER_DEFVAL);
    }
    
    public void setPageCompress(String compress) {
        if (StringUtil.equals(compress, SERVER_DEFVAL))
            ourProps.remove(PAGE_COMPRESS);
        else
            ourProps.setProperty(PAGE_COMPRESS, compress);
    }
    
    public String getClientValidate() {
        return ourProps.getProperty(CLIENT_VALIDATE, SERVER_DEFVAL);
    }
    
    public void setClientValidate(String doValidate) {
        if (StringUtil.equals(doValidate, SERVER_DEFVAL))
            ourProps.remove(CLIENT_VALIDATE);
        else
            ourProps.setProperty(CLIENT_VALIDATE, doValidate);
    }
    
    public String getJavaScriptCache() {
        return ourProps.getProperty(JSCRIPT_CACHESIZE, JSCRIPT_CACHESIZE_DEFVAL);
    }
    
    public void setJavaScriptCache(String cacheVal) {
        if (StringUtil.equals(JSCRIPT_CACHESIZE_DEFVAL, cacheVal) || StringUtil.isEmpty(cacheVal))
            ourProps.remove(JSCRIPT_CACHESIZE);
        else
            ourProps.setProperty(JSCRIPT_CACHESIZE, cacheVal);
    }
    
    public String getTimeZone() {
        return ourProps.getProperty(USER_TIMEZONE, SERVER_DEFVAL);     // $NON-NLS-1$
    }
    
    public void setTimeZone(String tz) {
        if (StringUtil.equals(SERVER_DEFVAL, tz)) // $NON-NLS-1$
            ourProps.remove(USER_TIMEZONE);
        else
            ourProps.setProperty(USER_TIMEZONE, tz);
    }
    
    public String getTheme() {
        return ourProps.getProperty(DB_THEME, SERVER_DEFVAL);
    }
    
    public void setTheme(String newTheme) {
        if (StringUtil.equals(SERVER_DEFVAL, newTheme))
            ourProps.remove(DB_THEME);
        else
            ourProps.setProperty(DB_THEME, newTheme);
    }
    
    public String getThemeNotes() {
        return ourProps.getProperty(DB_THEME_NOTES, APP_DEFVAL);
    }
    
    public void setThemeNotes(String nTheme) {
        if (StringUtil.equals(APP_DEFVAL, nTheme))
            ourProps.remove(DB_THEME_NOTES);
        else
            ourProps.setProperty(DB_THEME_NOTES, nTheme);
    }
    
    public String getThemeWeb() {
        return ourProps.getProperty(DB_THEME_WEB, APP_DEFVAL);
    }
    
    public void setThemeWeb(String wTheme) {
        if (StringUtil.equals(APP_DEFVAL, wTheme))
            ourProps.remove(DB_THEME_WEB);
        else
            ourProps.setProperty(DB_THEME_WEB, wTheme);
    }

    public String getMobileTheme() {
        return ourProps.getProperty(DB_MOBILE_THEME, MOBILE_DEFVAL);
    }
    
    public void setMobileTheme(String newTheme) {
        if (StringUtil.equals(MOBILE_DEFVAL, newTheme))
            ourProps.remove(DB_MOBILE_THEME);
        else
            ourProps.setProperty(DB_MOBILE_THEME, newTheme);
    }    
    
    public String getThemeIOS() {
        return ourProps.getProperty(DB_MOBILE_THEME_IOS, APP_DEFVAL);
    }
    
    public void setThemeIOS(String newTheme) {
        if (StringUtil.equals(APP_DEFVAL, newTheme))
            ourProps.remove(DB_MOBILE_THEME_IOS);
        else
            ourProps.setProperty(DB_MOBILE_THEME_IOS, newTheme);
    }    
    
    public String getThemeAndroid() {
        return ourProps.getProperty(DB_MOBILE_THEME_ANDROID, APP_DEFVAL);
    }
    
    public void setThemeAndroid(String newTheme) {
        if (StringUtil.equals(APP_DEFVAL, newTheme))
            ourProps.remove(DB_MOBILE_THEME_ANDROID);
        else
            ourProps.setProperty(DB_MOBILE_THEME_ANDROID, newTheme);
    }    
    
    public String getThemeDebugUserAgent() {
        return ourProps.getProperty(DB_MOBILE_DEBUG_USER_AGENT, APP_DEFVAL);
    }
    
    public void setThemeDebugUserAgent(String newTheme) {
        if (StringUtil.equals(APP_DEFVAL, newTheme))
            ourProps.remove(DB_MOBILE_DEBUG_USER_AGENT);
        else
            ourProps.setProperty(DB_MOBILE_DEBUG_USER_AGENT, newTheme);
    }      

    public String getPagePersistence() {
        return ourProps.getProperty(XSP_PERSISTENCE_MODE, SERVER_DEFVAL);
    }
    
    public void setPagePersistence(String pagePersist) {
        if (StringUtil.equals(SERVER_DEFVAL, pagePersist))
            ourProps.remove(XSP_PERSISTENCE_MODE);
        else
            ourProps.setProperty(XSP_PERSISTENCE_MODE, pagePersist);
    }
    
    public String getDefaultLinkTarget() {
        return ourProps.getProperty(XSP_APP_DEFAULT_LINK_TARGET, SERVER_DEFVAL);
    }
    
    public void setDefaultLinkTarget(String defLinkTarget) {
        if (StringUtil.equals(SERVER_DEFVAL, defLinkTarget))
            ourProps.remove(XSP_APP_DEFAULT_LINK_TARGET);
        else
            ourProps.setProperty(XSP_APP_DEFAULT_LINK_TARGET, defLinkTarget);
    }
    
    public String getAggregateResources() {
        // new apps will have this set to true, so read default is false
        return ourProps.getProperty(XSP_AGGREGATE_RESOURCES, XSP_AGGREGATE_RESOURCE_DEFVAL);
    }
    
    public void setAggregateResources(String doAggregate) {
        ourProps.setProperty(XSP_AGGREGATE_RESOURCES, doAggregate);
    }
    
    public String getDocType() {
        return ourProps.getProperty(XSP_HTML_DOCTYPE, SERVER_DEFVAL);
    }
    
    public void setDocType(String newDocType) {
        if (StringUtil.equals(SERVER_DEFVAL, newDocType))
            ourProps.remove(XSP_HTML_DOCTYPE);
        else
            ourProps.setProperty(XSP_HTML_DOCTYPE, newDocType);
    }
    
    public String getMinVersionLevel() {
        return ourProps.getProperty(XSP_MINIMUM_VERSION_LEVEL, null);
    }
    
    public void setMinVersionLevel(String newLevel) {
        if (newLevel == null || newLevel.length() == 0)
            ourProps.remove(XSP_MINIMUM_VERSION_LEVEL);
        else
            ourProps.setProperty(XSP_MINIMUM_VERSION_LEVEL, newLevel);
    }
    
    public void setDependencies(String dependencies){
        if(dependencies == null){
            ourProps.remove(XSP_LIBRARY_DEPENDENCIES);
            return;
        }
        ourProps.setProperty(XSP_LIBRARY_DEPENDENCIES, dependencies);
    }
    
    
    /**
     * 
     * @param append
     * @deprecated typo in method name, use {@link #appendDependencies(String)} instead.
     */
    public void appendDenendencies(String append){
        appendDependencies(append);
    }

    public void appendDependencies(String append){
        if(StringUtil.isEmpty(append)){
            return;
        }
        String libs = ourProps.getProperty(XSP_LIBRARY_DEPENDENCIES);
        if(StringUtil.isNotEmpty(libs)){
            libs = libs.trim();
            String[] parts = libs.split(",");
            if(Arrays.asList(parts).contains(append)){
                return;
            }
            if(!libs.endsWith(",")){
                libs += ",";
            }
            libs += append;
        }
        else{
            libs = append;
        }
        ourProps.setProperty(XSP_LIBRARY_DEPENDENCIES, libs);
    }
    
    public String getDependencies(){
        return ourProps.getProperty(XSP_LIBRARY_DEPENDENCIES);
    }
    
    public String getRenderWholeTree() {
        return ourProps.getProperty(XSP_AJAX_WHOLE_TREE_RENDER, WHOLE_TREE_RENDER_DEFVAL);
    }
    
    public void setRenderWholeTree(String newVal) {
        ourProps.setProperty(XSP_AJAX_WHOLE_TREE_RENDER, newVal);
    }
    
    public String getLinkFormat() {
        return ourProps.getProperty(XSP_SAVE_LINKS, XSP_SAVE_USE_NOTES);
    }
    
    public void setLinkFormat(String newVal) {
        if (newVal == null || newVal.equalsIgnoreCase(XSPAllPropertyConstants.XSP_SAVE_USE_NOTES))
            ourProps.remove(XSP_SAVE_LINKS);
        else
            ourProps.setProperty(XSP_SAVE_LINKS, newVal);
    }
    
    public Properties getPropertiesObj(){
        return ourProps;
    }

    public String getDojoVersion() {
        return ourProps.getProperty(XSP_CLIENT_DOJO_VSN, SERVER_DEFVAL);
    }
    
    public void setDojoVersion(String djVersion) {
        if (StringUtil.equals(djVersion, SERVER_DEFVAL))
            ourProps.remove(XSP_CLIENT_DOJO_VSN);
        else
            ourProps.setProperty(XSP_CLIENT_DOJO_VSN, djVersion);
    }
    
    public String getDojoConfig() {
        return ourProps.getProperty(XSP_DOJO_CONFIG, SERVER_DEFVAL);
    }
    
    public void setDojoConfig(String djConfig) {
        if (StringUtil.equals(djConfig, SERVER_DEFVAL)) {
            ourProps.remove(XSP_DOJO_CONFIG);
        }
        else
            ourProps.setProperty(XSP_DOJO_CONFIG, djConfig);
    }
    
    public String getExpiresGlobal() {
        return ourProps.getProperty(XSP_EXPIRES_GLOBAL, SERVER_DEFVAL); 
    }
    
    public void setExpiresGlobal(String expGlobal) {
        if (StringUtil.equals(expGlobal, SERVER_DEFVAL)) {
            ourProps.remove(XSP_EXPIRES_GLOBAL);
        }
        else
            ourProps.setProperty(XSP_EXPIRES_GLOBAL, expGlobal);
    }
    
    public String getHtmlPrefContentType() {
        return ourProps.getProperty(XSP_HTML_PREFERRED_CT, SERVER_DEFVAL);
    }
    
    public void setHtmlPrefContentType(String ct) {
        if (StringUtil.equals(ct, SERVER_DEFVAL)) {
            ourProps.remove(XSP_HTML_PREFERRED_CT);
        }
        else
            ourProps.setProperty(XSP_HTML_PREFERRED_CT, ct);
    }

    public String getHtmlFilterACFConfig() {
        return ourProps.getProperty(XSP_HTMLFILTER_ACF, SERVER_DEFVAL);
    }
    
    public void setHtmlFilterACFConfig(String acfConfig) {
        if (StringUtil.equals(acfConfig, SERVER_DEFVAL))
            ourProps.remove(XSP_HTMLFILTER_ACF);
        else
            ourProps.setProperty(XSP_HTMLFILTER_ACF, acfConfig);
    }
    
    public String getGzipPersistedFiles() {
        return ourProps.getProperty(XSP_PERSISTENCE_GZIP, GZIP_PERSIST_DEFVAL);
    }
    
    public void setGzipPersistedFiles(String gz) {
        if (StringUtil.equals(gz, GZIP_PERSIST_DEFVAL))
            ourProps.remove(XSP_PERSISTENCE_GZIP);
        else
            ourProps.setProperty(XSP_PERSISTENCE_GZIP, gz);
    }
    
    public String getAttachmentPersistDir() {
        return ourProps.getProperty(XSP_PERS_DIR_XSPPERS, SERVER_DEFVAL);
    }
    
    public void setAttachmentPersistDir(String attDir) {
        if (StringUtil.equals(attDir, SERVER_DEFVAL))
            ourProps.remove(XSP_PERS_DIR_XSPPERS);
        else
            ourProps.setProperty(XSP_PERS_DIR_XSPPERS, attDir);
    }
    
    public String getPagePersistDir() {
        return ourProps.getProperty(XSP_PERS_DIR_XSPSTATE, SERVER_DEFVAL);
    }
    
    public void setPagePersistDir(String ppDir) {
        if (StringUtil.equals(ppDir, SERVER_DEFVAL)) {
            ourProps.remove(XSP_PERS_DIR_XSPSTATE);
        }
        else
            ourProps.setProperty(XSP_PERS_DIR_XSPSTATE, ppDir);
    }
    
    public String getUploadPersistDir() {
        return ourProps.getProperty(XSP_PERS_DIR_XSPUPLOAD, SERVER_DEFVAL);
    }
    
    public void setUploadPersistDir(String ulDir) {
        if (StringUtil.equals(ulDir, SERVER_DEFVAL))
            ourProps.remove(XSP_PERS_DIR_XSPUPLOAD);
        else
            ourProps.setProperty(XSP_PERS_DIR_XSPUPLOAD, ulDir);
    }
    
    public String getDiscardJS() {
        return ourProps.getProperty(XSP_PERS_DISCARDJS, DISCARD_JS_DEFVAL);
    }
    
    public void setDiscardJS(String discardjs) {
        if (StringUtil.equals(discardjs, DISCARD_JS_DEFVAL))
            ourProps.remove(XSP_PERS_DISCARDJS);
        else
            ourProps.setProperty(XSP_PERS_DISCARDJS, discardjs);
    }
    
    public String getUncompressCssAndDojo() {
        return ourProps.getProperty("xsp.client.resources.uncompressed", FALSE_DEFVAL); // $NON-NLS-1$
    }
    
    public void setUncompressCssAndDojo(String uncompress) {
        if (StringUtil.equals(uncompress, FALSE_DEFVAL))
            ourProps.remove("xsp.client.resources.uncompressed"); // $NON-NLS-1$
        else
            ourProps.setProperty("xsp.client.resources.uncompressed", uncompress); // $NON-NLS-1$
    }
    
    public String getAsyncFilePersistence() {
        return ourProps.getProperty(XSP_PERS_FILE_ASYNC, FILE_ASYNC_DEFVAL);
    }
    
    public void setAsyncFilePersistence(String asyncVal) {
        if (StringUtil.equals(asyncVal, FILE_ASYNC_DEFVAL))
            ourProps.remove(XSP_PERS_FILE_ASYNC);
        else
            ourProps.setProperty(XSP_PERS_FILE_ASYNC, asyncVal);
    }

    public String getMaxSavedPages() {
        return ourProps.getProperty(XSP_PERS_FILE_MAXVIEWS, SERVER_DEFVAL);
    }
    
    public void setMaxSavedPages(String maxPages) {
        if (StringUtil.equals(maxPages, SERVER_DEFVAL))
            ourProps.remove(XSP_PERS_FILE_MAXVIEWS);
        else
            ourProps.setProperty(XSP_PERS_FILE_MAXVIEWS, maxPages);
    }

    public String getMaxSavedPagesMemory() {
        return ourProps.getProperty(XSP_PERS_TREE_MAXVIEWS, SERVER_DEFVAL);
    }
    
    public void setMaxSavedPagesMemory(String maxPages) {
        if (StringUtil.equals(maxPages, SERVER_DEFVAL))
            ourProps.remove(XSP_PERS_TREE_MAXVIEWS);
        else
            ourProps.setProperty(XSP_PERS_TREE_MAXVIEWS, maxPages);
    }

    public String getPagePersistenceViewState() {
        return ourProps.getProperty(XSP_PERS_VIEWSTATE, SERVER_DEFVAL);
    }
    
    public void setPagePersistenceViewState(String ppMode) {
        if (StringUtil.equals(ppMode, SERVER_DEFVAL))
            ourProps.remove(XSP_PERS_VIEWSTATE);
        else
            ourProps.setProperty(XSP_PERS_VIEWSTATE, ppMode);
    }
    
    public String getPagePersistenceThreshold() {
        return ourProps.getProperty(XSP_PERSIST_THRESHHOLD, "0");
    }
    
    public void setPagePersistenceThreshold(String th) {
        if (StringUtil.isEmpty(th) || StringUtil.equals(th, "0"))
            ourProps.remove(XSP_PERSIST_THRESHHOLD);
        else
            ourProps.setProperty(XSP_PERSIST_THRESHHOLD, th);
    }
    
    public String getPageRedirectMode() {
        return ourProps.getProperty(XSP_REDIRECT, PAGE_REDIRECT_DEFVAL);
    }
    
    public void setPageRedirectMode(String redir) {
        if (StringUtil.equals(redir, PAGE_REDIRECT_DEFVAL))
            ourProps.remove(XSP_REDIRECT);
        else
            ourProps.setProperty(XSP_REDIRECT, redir);
    }
    
    public String getAllowRepeatZero() {
        return ourProps.getProperty(XSP_REPEAT_ALLOWZERO, ALLOWZERO_DEFVAL);
    }
    
    public void setAllowRepeatZero(String allowZero) {
        if (StringUtil.equals(allowZero, ALLOWZERO_DEFVAL))
            ourProps.remove(XSP_REPEAT_ALLOWZERO);
        else
            ourProps.setProperty(XSP_REPEAT_ALLOWZERO, allowZero);
    }
    
    public String getRtHTMLFilter() {
        return ourProps.getProperty(XSP_RT_DEF_HTMLFILTER, SERVER_DEFVAL);
    }
    
    public void setRtHTMLFilter(String filter) {
        if (StringUtil.equals(filter, SERVER_DEFVAL))
            ourProps.remove(XSP_RT_DEF_HTMLFILTER);
        else
            ourProps.setProperty(XSP_RT_DEF_HTMLFILTER, filter);
    }

    public String getRtHTMLFilterIn() {
        return ourProps.getProperty(XSP_RT_DEF_HTMLFILTERIN, SERVER_DEFVAL);
    }
    
    public void setRtHTMLFilterIn(String filter) {
        if (StringUtil.equals(filter, SERVER_DEFVAL))
            ourProps.remove(XSP_RT_DEF_HTMLFILTERIN);
        else
            ourProps.setProperty(XSP_RT_DEF_HTMLFILTERIN, filter);
    }

    public String getSessionTransient() {
        return ourProps.getProperty(XSP_SESSION_TRANSIENT, TRANSIENT_SESSION_DEFVAL);
    }
    
    public void setSessionTransient(String isTransient) {
        if (StringUtil.equals(isTransient, TRANSIENT_SESSION_DEFVAL))
            ourProps.remove(XSP_SESSION_TRANSIENT);
        else
            ourProps.setProperty(XSP_SESSION_TRANSIENT, isTransient);
    }
    
    public String getOldCDStyle() {
        return ourProps.getProperty(XSP_THEME_NOCOMPDS, OLDCDSTYLE_DEFVAL);
    }

    public void setOldCDStyle(String useOld) {
        if (StringUtil.equals(useOld, OLDCDSTYLE_DEFVAL))
            ourProps.remove(XSP_THEME_NOCOMPDS);
        else
            ourProps.setProperty(XSP_THEME_NOCOMPDS, useOld);
    }
    
    public String getRoundTripTZ() {
        return ourProps.getProperty(XSP_USER_TZ_RT, USER_TZRT_DEFVAL);
    }

    public void setRoundTripTZ(String rtTZ) {
        if (StringUtil.equals(rtTZ, USER_TZRT_DEFVAL))
            ourProps.remove(XSP_USER_TZ_RT);
        else
            ourProps.setProperty(XSP_USER_TZ_RT, rtTZ);
    }
    
    public String getCacheXPath() {
        return ourProps.getProperty(XPATH_CACHESIZE, XPATH_CACHESIZE_DEFVAL);
    }
    
    public void setCacheXPath(String cacheVal) {
        if (StringUtil.isEmpty(cacheVal) || StringUtil.equals(XPATH_CACHESIZE_DEFVAL, cacheVal))
            ourProps.remove(XPATH_CACHESIZE);
        else
            ourProps.setProperty(XPATH_CACHESIZE, cacheVal);
    }
    
    public String getPartialUpdateTimeout() {
        return ourProps.getProperty(XSP_PARTIAL_UPDATE_TIMEOUT, SERVER_DEFVAL);
    }
    
    public void setPartialUpdateTimeout(String timeout) {
        if (StringUtil.equals(SERVER_DEFVAL, timeout))
            ourProps.remove(XSP_PARTIAL_UPDATE_TIMEOUT);
        else
            ourProps.setProperty(XSP_PARTIAL_UPDATE_TIMEOUT, timeout);
    }
    
    public String getForceFullRefresh() {
        return ourProps.getProperty(XSP_FORCE_FULLREFRESH, FALSE_DEFVAL);
    }
    
    public void setForceFullRefresh(String fullRefresh) {
        if (StringUtil.equals(FALSE_DEFVAL, fullRefresh))
            ourProps.remove(XSP_FORCE_FULLREFRESH);
        else
            ourProps.setProperty(XSP_FORCE_FULLREFRESH, fullRefresh);
    }
    
    public String getWriteMetaContent() {
        return ourProps.getProperty(XSP_HTML_METACONTENT, FALSE_DEFVAL);
    }
    
    public void setWriteMetaContent(String writeIt) {
        if (StringUtil.equals(writeIt, FALSE_DEFVAL))
            ourProps.remove(XSP_HTML_METACONTENT);
        else
            ourProps.setProperty(XSP_HTML_METACONTENT, writeIt);
    }
    
    public void setMobilePrefix(String prefix){
        if(StringUtil.isNotEmpty(prefix)){
            ourProps.setProperty(XSP_MOBILE_THEME, prefix);
        }
        else{
            ourProps.remove(XSP_MOBILE_THEME);
        }
    }
    
    public String getMobilePrefix(){
        return ourProps.getProperty(XSP_MOBILE_THEME);
    }
    
    
    public String getRobotUserAgents() {
        return ourProps.getProperty(XSP_SEARCH_BOT_ID_LIST, ROBOT_USER_AGENTS_DEFVAL);
    }
    
    public void setRobotUserAgents(String userAgents) {
        if (StringUtil.equals(ROBOT_USER_AGENTS_DEFVAL, userAgents))
            ourProps.remove(XSP_SEARCH_BOT_ID_LIST);
        else
            ourProps.setProperty(XSP_SEARCH_BOT_ID_LIST, userAgents);
    }
    
    private void initAttrKeyMap() {
        keyAttrMap.put(APP_TIMEOUT, "appTimeout");  // $NON-NLS-1$
        keyAttrMap.put(XSP_AGGREGATE_RESOURCES, "aggregateResources");  // $NON-NLS-1$
        keyAttrMap.put(ERROR_PAGE, "errorPage");    // $NON-NLS-1$
        keyAttrMap.put(DEFAULT_ERROR_PAGE, "defaultErrorPage"); // $NON-NLS-1$
        keyAttrMap.put(DB_THEME, "theme");  // $NON-NLS-1$
        keyAttrMap.put(DB_THEME_NOTES, "themeNotes"); // $NON-NLS-1$
        keyAttrMap.put(DB_THEME_WEB, "themeWeb");   // $NON-NLS-1$
        keyAttrMap.put(DB_MOBILE_THEME, "mobileTheme"); // $NON-NLS-1$
        keyAttrMap.put(DB_MOBILE_THEME_IOS, "themeIOS");     // $NON-NLS-1$
        keyAttrMap.put(DB_MOBILE_THEME_ANDROID, "themeAndroid");     // $NON-NLS-1$
        keyAttrMap.put(DB_MOBILE_DEBUG_USER_AGENT, "themeDebugUserAgent");     // $NON-NLS-1$
        
        keyAttrMap.put(SESSION_TIMEOUT, "sessionTimeout"); // $NON-NLS-1$
        keyAttrMap.put(FILE_UPLOAD_MAXSIZE , "uploadMax"); // $NON-NLS-1$
        keyAttrMap.put(FILE_UPLOAD_DIRECTORY, "uploadDir"); // $NON-NLS-1$
        keyAttrMap.put(PAGE_ENCODING, "pageEncoding"); // $NON-NLS-1$
        keyAttrMap.put(PAGE_COMPRESS, "pageCompress"); // $NON-NLS-1$
        keyAttrMap.put(CLIENT_VALIDATE, "clientValidate"); // $NON-NLS-1$
        keyAttrMap.put(USER_TIMEZONE, "timeZone"); // $NON-NLS-1$
        keyAttrMap.put(JSCRIPT_CACHESIZE, "javaScriptCache"); // $NON-NLS-1$
        keyAttrMap.put(XPATH_CACHESIZE, "cacheXPath"); // $NON-NLS-1$
        keyAttrMap.put(XSP_PERSISTENCE_MODE, "pagePersistence"); // $NON-NLS-1$
        keyAttrMap.put(XSP_PERSISTENCE_GZIP, "gzipPersistedFiles"); // $NON-NLS-1$
        keyAttrMap.put(XSP_APP_DEFAULT_LINK_TARGET, "defaultLinkTarget");   // $NON-NLS-1$
        keyAttrMap.put(XSP_SAVE_LINKS, "linkFormat");   // $NON-NLS-1$
        keyAttrMap.put(XSP_PARTIAL_UPDATE_TIMEOUT, "partialUpdateTimeout"); // $NON-NLS-1$
        keyAttrMap.put(XSP_LIBRARY_DEPENDENCIES, "dependencies");       // $NON-NLS-1$
        keyAttrMap.put(XSP_MINIMUM_VERSION_LEVEL, "minVersionLevel");   // $NON-NLS-1$
        
        keyAttrMap.put(XSP_AJAX_WHOLE_TREE_RENDER, "renderWholeTree");  // $NON-NLS-1$
        keyAttrMap.put(XSP_HTML_DOCTYPE, "docType");                    // $NON-NLS-1$
        
        keyAttrMap.put(XSP_DOJO_CONFIG, "dojoConfig");                      // $NON-NLS-1$
        keyAttrMap.put(XSP_CLIENT_DOJO_VSN, "dojoVersion");                 // $NON-NLS-1$
        keyAttrMap.put(XSP_EXPIRES_GLOBAL, "expiresGlobal");                // $NON-NLS-1$
        keyAttrMap.put(XSP_HTML_PREFERRED_CT, "htmlPrefContentType");       // $NON-NLS-1$
        keyAttrMap.put(XSP_HTMLFILTER_ACF, "htmlFilterACFConfig");          // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_DIR_XSPPERS, "attachmentPersistDir");       // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_DIR_XSPSTATE, "pagePersistDir");            // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_DIR_XSPUPLOAD, "uploadPersistDir");         // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_DISCARDJS, "discardJS");                    // $NON-NLS-1$
        keyAttrMap.put("xsp.client.resources.uncompressed", "uncompressCssAndDojo");                    // $NON-NLS-1$ $NON-NLS-2$
        keyAttrMap.put(XSP_PERS_FILE_ASYNC, "asyncFilePersistence");        // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_FILE_MAXVIEWS, "maxSavedPages");            // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_TREE_MAXVIEWS, "maxSavedPagesMemory");      // $NON-NLS-1$
        keyAttrMap.put(XSP_PERS_VIEWSTATE, "pagePersistenceViewState");     // $NON-NLS-1$
        keyAttrMap.put(XSP_REDIRECT, "pageRedirectMode");                   // $NON-NLS-1$
        keyAttrMap.put(XSP_REPEAT_ALLOWZERO, "allowRepeatZero");            // $NON-NLS-1$ 
        keyAttrMap.put(XSP_RT_DEF_HTMLFILTER, "rtHTMLFilter");              // $NON-NLS-1$
        keyAttrMap.put(XSP_RT_DEF_HTMLFILTERIN, "rtHTMLFilterIn");          // $NON-NLS-1$
        keyAttrMap.put(XSP_SESSION_TRANSIENT, "sessionTransient");          // $NON-NLS-1$
        keyAttrMap.put(XSP_THEME_NOCOMPDS, "oldCDStyle");                   // $NON-NLS-1$
        keyAttrMap.put(XSP_USER_TZ_RT, "roundTripTZ");                      // $NON-NLS-1$
        keyAttrMap.put(XSP_HTML_METACONTENT, "writeMetaContent");           // $NON-NLS-1$
        keyAttrMap.put(XSP_PERSIST_THRESHHOLD, "pagePersistenceThreshold"); // $NON-NLS-1$
        keyAttrMap.put(XSP_FORCE_FULLREFRESH, "forceFullRefresh");          // $NON-NLS-1$
        
        keyAttrMap.put(XSP_MOBILE_THEME, "mobilePrefix");                   // $NON-NLS-1$
        keyAttrMap.put(XSP_SEARCH_BOT_ID_LIST, "robotUserAgents");          // $NON-NLS-1$
    }
    
    // really clunky, but need a bean to do the binding!
    public String keyForAttr(String attrName) {
        Set<Entry<String, String>> entrySet = keyAttrMap.entrySet();
        Iterator<Entry<String, String>> it = entrySet.iterator();
        while(it.hasNext()) {
            Entry<String, String> one = it.next();
            if (StringUtil.equals(attrName, one.getValue()))
                return one.getKey();
        }
        return null;
    }
}