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
* Date: 3 Jun 2009
* SinceVersionsSetTest.java
*/

package com.ibm.xsp.test.framework.version;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesLibrary;
import com.ibm.xsp.registry.FacesProperty;
import com.ibm.xsp.registry.FacesRegistry;
import com.ibm.xsp.registry.FacesSharableRegistry;
import com.ibm.xsp.test.framework.AbstractXspTest;
import com.ibm.xsp.test.framework.TestProject;
import com.ibm.xsp.test.framework.XspTestUtil;
import com.ibm.xsp.test.framework.setup.SkipFileContent;

/**
 * @author Maire Kehoe (mkehoe@ie.ibm.com)
 *
 */
public class SinceVersionsSetTest extends AbstractXspTest {

    @Override
    public String getDescription() {
        return "to prevent "
                + XspTestUtil.getShortClass(NoClassDefFoundError.class)
                + " and "
                + XspTestUtil.getShortClass(NoSuchMethodError.class)
                + " problems when running new apps on old servers, "
                + "new propertys and tags should have a <since> element indicating when they were added.";
    }

    public void testSinceVersionsSet() throws Exception {
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        Map<String, FacesLibrary> prefixToLibrary = PrintTagNamesAndProps.getPrefixToNamespace(reg);
        List<SinceVersionList> versionListArr = getSinceVersionLists();
        if( null == versionListArr || versionListArr.isEmpty() ){
            versionListArr = new ArrayList<SinceVersionList>();
            versionListArr.add(new EmptySinceVersionList());
        }
        {
            String[] skips = getExtraSkips();
            skips = SkipFileContent.concatSkips(skips, this, "testSinceVersionsSet");
            if( skips.length > 0 ){
                int mostRecentIndex = versionListArr.size() - 1;
                SinceVersionList sinceList = versionListArr.get(mostRecentIndex);
                SinceVersionList wrapper = new SinceVersionListExtraSkipsWrapper(sinceList, getExtraSkips());
                versionListArr.set(mostRecentIndex, wrapper);
            }
        }
        
        String recentSinceVersion = getCurrentVersion(versionListArr);
        
        String fails = "";
        for (SinceVersionList versionInfo : versionListArr) {
            fails += checkSinceVersion(reg, prefixToLibrary, versionInfo.tagsAndProps(), versionInfo.sinceVersion());
        }
        
        // when new props are added, in the 8.5.3 stream, without a <since> version,
        // that aren't yet listed in SinceVersion853List, should complain: 
        //  xp:something.newProp bad since version. Expected <since>8.5.3(probably)<, was <since>null<
        // where the "(probably)" bit will be present until SinceVersion853List is re-generated. 
        List<Object[]> extraSinceList = findNewUnlistedTagsAndProps(reg, versionListArr);
        String dependsLibraryIdFilter = getDependsLibraryIdFilter();
        if( null != dependsLibraryIdFilter ){
            extraSinceList = PrintTagNamesAndProps.filterToDependsLibrary(reg, dependsLibraryIdFilter, extraSinceList);
        }
        Object[][] extraSinceArr = extraSinceList.toArray(new Object[extraSinceList.size()][]);
        fails += checkSinceVersion(reg, prefixToLibrary, extraSinceArr, recentSinceVersion+"(probably)");
        
        String[] skips = concatSkips(versionListArr);
        for (String skip : skips) {
            int oldLen = fails.length();
            fails = removeSkip(fails,skip);
            if( fails.length() == oldLen ){
                String proposedSkip = "Unused skip: "+skip; 
                fails = removeSkip(fails,skip);
                if( fails.length() == oldLen ){
                    fails += proposedSkip+"\n";
                }
            }
        }
        
        if( fails.length() >  0 ){
            fail(XspTestUtil.getMultilineFailMessage(fails, "<since> version mismatch"));
        }
    }

    /**
     * Available to override in subclasses. Defaults to the version of the last in the list.
     * @param versionListArr
     * @return
     */
    protected String getCurrentVersion(List<SinceVersionList> versionListArr) {
        SinceVersionList recentObj = getMostRecent(versionListArr);
        String recentSinceVersion = recentObj.sinceVersion();
        return recentSinceVersion;
    }

    /**
     * Available to override in subclass, when null will test all tags in the
     * registry, including the XPages runtime tags.
     * 
     * @return
     */
    protected String getDependsLibraryIdFilter(){
        return null;
    }

    /**
     * Should be overridden in subclass. The last version in the list 
     * is considered the current library tag version. The older versions
     * will be treated as from older since versions,
     * and will not require a since>currentVersion</since tag.
     * 
     * @return
     */
    protected List<SinceVersionList> getSinceVersionLists(){
        List<SinceVersionList> list = new ArrayList<SinceVersionList>();
        return list;
    }
    private String[] concatSkips(List<SinceVersionList> versionListArr) {
        List<String[]> all = new ArrayList<String[]>(versionListArr.size());
        for (SinceVersionList versionInfo : versionListArr) {
            String[] skips = versionInfo.skips();
            all.add(skips);
        }
        return XspTestUtil.concatStringArrays(all.toArray(new String[versionListArr.size()][]));
    }
    private String removeSkip(String fails, String skip) {
        skip = skip+"\n";
        int index = fails.indexOf(skip);
        if( -1 == index ){
            return fails;
        }
        if( 0 != index ){
            // unused skips are nested, so need to check 
            // this is an actual match at the start of a line.
            index = fails.indexOf("\n"+skip);
            if( -1 == index ){
                return fails;
            }
            index++;
        }
        int endIndex = index + skip.length();
        return fails.substring(0, index)+ fails.substring(endIndex);
    }
    private String checkSinceVersion(FacesRegistry reg,
            Map<String, FacesLibrary> libByPrefix, Object[][] tagsAndProps, String expectedSince) {
        String fails = "";
        for (Object[] tagAndProp : tagsAndProps) {
            
            String prefixedTagName = getName(tagAndProp);
            FacesDefinition def = PrintTagNamesAndProps.getDef(libByPrefix, prefixedTagName);
            if( null == def ){
                fails+= prefixedTagName+" not found. Expected <since>"+expectedSince+"<\n";
                continue;
            }
            String actualDefSince = def.getSince();
            boolean isNewTag = PrintTagNamesAndProps.isNewTag(tagAndProp);
            if( isNewTag && !StringUtil.equals(expectedSince, actualDefSince) ){
                fails += XspTestUtil.loc(def) + " bad since version. Expected <since>"
                        + expectedSince + "<, was <since>" + actualDefSince
                        + "<\n";
                continue;
            }
            
            if( isNoProps(tagAndProp) ){
                continue;
            }
            String[] propNames = getProps(tagAndProp);
            for (String name : propNames) {
                FacesProperty prop = def.getProperty(name);
                if( null == prop ){
                    String msg = XspTestUtil.loc(def)+" " +name+" not found. Expected <since>"+expectedSince+"<";
                    fails+= msg + "\n";
                    continue;
                }
                String actualPropSince = prop.getSince();
                if( null == actualPropSince ){
                    actualPropSince = actualDefSince;
                }
                if( ! StringUtil.equals(expectedSince, actualPropSince) ){
                    fails += XspTestUtil.loc(def) +" " +name+ " bad since version. Expected <since>"
                            + expectedSince + "<, was <since>" + actualPropSince
                            + "<\n";
                    continue;
                }
            }
            
        }
        return fails;
    }

    public void testCurrentSinceListCorrect() throws Exception {
        List<SinceVersionList> versionListArr = getSinceVersionLists();
        
        FacesSharableRegistry reg = TestProject.createRegistry(this);
        List<Object[]> extraSinceList = findNewUnlistedTagsAndProps(reg, versionListArr);
        String dependsLibraryIdFilter = getDependsLibraryIdFilter();
        if( null != dependsLibraryIdFilter ){
            extraSinceList = PrintTagNamesAndProps.filterToDependsLibrary(reg, dependsLibraryIdFilter, extraSinceList);
        }
        
        String fails ="";
        for (Object[] extraTag : extraSinceList) {
            if( isNoProps(extraTag) ){
                fails += "new tag "+getName(extraTag)+"\n";
                continue;
            }
            
            fails += "tag "+getName(extraTag)+" has new props: " +toString(getProps(extraTag))+"\n";
        }
        fails = XspTestUtil.removeMultilineFailSkips(fails, 
                SkipFileContent.concatSkips(getCurrentSkips(), this, "testCurrentSinceListCorrect"));
        if( fails.length() > 0 ){
            SinceVersionList mostRecent = getMostRecent(versionListArr);
            String msg;
            if( null != mostRecent ){
                msg = XspTestUtil.getShortClass(mostRecent) + " is out of date. Please regenerate that class. ";
            }else{
                msg = "Known tags list not available. Please override getSinceVersionLists() and list these tags.";
            }
            fail(XspTestUtil.getMultilineFailMessage(fails, msg));
        }
    }
    /**
     * This should only be used in Green tests, used by testCurrentSinceListCorrect
     * @return
     */
    protected String[] getCurrentSkips(){
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    
    /**
     * This should only be used in Green tests, used by testSinceVersionsSet.
     * These skips should not be added from this method 
     * but should be supplied through {@link #getSinceVersionLists()}, 
     * as values in {@link SinceVersionList#skips()}.
     * @return
     */
    protected String[] getExtraSkips() {
        return StringUtil.EMPTY_STRING_ARRAY;
    }
    private List<Object[]> findNewUnlistedTagsAndProps(FacesSharableRegistry registry,
            List<SinceVersionList> versionListArr) {
        List<Object[]> expectedSinceList = new ArrayList<Object[]>();
        for (SinceVersionList versionInfo : versionListArr) {
            PrintTagNamesAndProps.addAll(expectedSinceList, versionInfo.tagsAndProps());
        }
        
        List<Object[]> actualSinceList = PrintTagNamesAndProps.getTagsAndProps(registry);
        
        List<Object[]> extraSinceList = actualSinceList;
        PrintTagNamesAndProps.removeAll(actualSinceList, expectedSinceList.toArray(new Object[expectedSinceList.size()][]), true);
        return extraSinceList;
    }
    public SinceVersionList getMostRecent(List<SinceVersionList> versionListArr){
        if( null == versionListArr || versionListArr.isEmpty() ){
            return null;
        }
    	return versionListArr.get(versionListArr.size()-1);
    }

    private String[] getProps(Object[] extraTag) {
        return PrintTagNamesAndProps.getProps(extraTag);
    }

    private String getName(Object[] extraTag) {
        return PrintTagNamesAndProps.getName(extraTag);
    }

    private boolean isNoProps(Object[] extraTag) {
        return PrintTagNamesAndProps.isNoProps(extraTag);
    }
    private static String toString(String[] arr) {
        if( null == arr ){
            arr = StringUtil.EMPTY_STRING_ARRAY;
        }
        return StringUtil.toStringArray(arr, -1, Integer.MAX_VALUE);
    }
    private static final class EmptySinceVersionList implements SinceVersionList {
        public Object[][] tagsAndProps() {
            return new Object[0][];
        }

        public String[] skips() {
            return StringUtil.EMPTY_STRING_ARRAY;
        }

        public String sinceVersion() {
            // null is the first version.
            return null;
        }
    }
    /**
     * Note, this is provided for building Green* subclasses,
     * which skip all known fails (rather than skipping issues
     * known to be not-a-problem). In that case, the skip 
     * method would be like so:
     * @Override
     * protected List<SinceVersionList> getSinceVersionLists() {
     *     List<SinceVersionList> list = super.getSinceVersionLists();
     *     int mostRecentIndex = list.size() - 1;
     *     SinceVersionList sinceList = list.get(mostRecentIndex);
     *     SinceVersionList wrapper = new SinceVersionListExtraSkipsWrapper(sinceList, skips);
     *     list.set(mostRecentIndex, wrapper);
     *     return list;
     * }
     * @author Maire Kehoe (mkehoe@ie.ibm.com)
     */
    public static class SinceVersionListExtraSkipsWrapper implements SinceVersionList{
        private SinceVersionList delegate;
        private String[] extraSkips;
        public SinceVersionListExtraSkipsWrapper(SinceVersionList delegate,
                String[] extraSkips) {
            super();
            this.delegate = delegate;
            this.extraSkips = extraSkips;
        }
        public Object[][] tagsAndProps() {
            return delegate.tagsAndProps();
        }
        public String sinceVersion() {
            return delegate.sinceVersion();
        }
        public String[] skips() {
            return XspTestUtil.concat(delegate.skips(),extraSkips);
        }
    }
}
