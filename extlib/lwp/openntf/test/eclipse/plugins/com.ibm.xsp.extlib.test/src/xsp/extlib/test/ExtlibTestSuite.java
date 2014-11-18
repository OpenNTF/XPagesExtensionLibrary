/*
 * © Copyright IBM Corp. 2010, 2014
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
* Date: 6 May 2010
*/
package xsp.extlib.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import xsp.extlib.test.application.ExtlibReflectionSerializeTest;
import xsp.extlib.test.application.ExtlibRegisteredSerializationTest;
import xsp.extlib.test.control.ApplicationConfigurationDefaultsTest;
import xsp.extlib.test.control.ChangeDynamicContentTest;
import xsp.extlib.test.control.DataViewDetailsOnClientTest;
import xsp.extlib.test.control.DojoCheckBoxDefaultValueDisabledTest;
import xsp.extlib.test.control.MobileAppPageEventTest;
import xsp.extlib.test.control.MobileControlTooltipTest;
import xsp.extlib.test.control.NavigatorContainerNodeTest;
import xsp.extlib.test.page.translator.ExtlibGeneratePagesTest;
import xsp.extlib.test.registry.ExtlibBooleanPropertyDefaultTest;
import xsp.extlib.test.registry.ExtlibEventPropsHaveSubCategoryTest;
import xsp.extlib.test.registry.ExtlibExtlibGroupReuseTest;
import xsp.extlib.test.registry.ExtlibKnownPropertyRedefinitionTest;
import xsp.extlib.test.registry.ExtlibLabelsLocalizableTest;
import xsp.extlib.test.registry.ExtlibNamingConventionErrorTest;
import xsp.extlib.test.registry.ExtlibNamingConventionTest;
import xsp.extlib.test.registry.ExtlibNoRunTimeBindingsTest;
import xsp.extlib.test.registry.ExtlibPropertiesHaveSettersTest;
import xsp.extlib.test.registry.ExtlibPropertyDefaultValueTest;
import xsp.extlib.test.registry.annotate.BaseExtlibControlCategoryKnownTest;
import xsp.extlib.test.registry.annotate.ExtlibDisplayNameDuplicateTest;
import xsp.extlib.test.registry.annotate.ExtlibInputAccessibilityTest;
import xsp.extlib.test.registry.annotate.ExtlibInputDefaultValueDisabledTest;
import xsp.extlib.test.registry.annotate.ExtlibMergeWarningsTest;
import xsp.extlib.test.registry.annotate.ExtlibPropertiesHaveCategoriesTest;
import xsp.extlib.test.registry.annotate.ExtlibRoleAccessibilityTest;
import xsp.extlib.test.registry.annotate.ExtlibSpellCheckTest;
import xsp.extlib.test.registry.annotate.ExtlibTranslatableStringsTest;
import xsp.extlib.test.registry.annotate.MobilePageEditorTest;
import xsp.extlib.test.render.ExtlibDojoTypeTest;
import xsp.extlib.test.render.ExtlibRenderBooleanPropertyTest;
import xsp.extlib.test.render.ExtlibRenderDojoPropertyTest;
import xsp.extlib.test.render.ExtlibRenderIdTest;
import xsp.extlib.test.render.ExtlibRenderPageTest;
import xsp.extlib.test.render.ExtlibRenderThemeControlTest;
import xsp.extlib.test.render.MobileRenderThemeControlTest;
import xsp.extlib.test.serialize.ExtlibViewSerializeTest;
import xsp.extlib.test.setup.BootstrapJunitableTest;
import xsp.extlib.test.setup.ExtlibSuiteSetupTest;
import xsp.extlib.test.setup.OneUIJunitableTest;
import xsp.extlib.test.version.ExtlibSinceVersionsSetTest;

import com.ibm.xsp.test.framework.SampleTestSuite;
import com.ibm.xsp.test.framework.TestClassList;
import com.ibm.xsp.test.framework.lifecycle.RegisteredDecodeTest;
import com.ibm.xsp.test.framework.registry.BaseComplexCheckTest;
import com.ibm.xsp.test.framework.registry.BaseComplexNotRunTimeBindingTest;
import com.ibm.xsp.test.framework.registry.BaseComponentRendererTest;
import com.ibm.xsp.test.framework.registry.BaseComponentTypeTest;
import com.ibm.xsp.test.framework.registry.BaseInheritRendererTypeGetterTest;
import com.ibm.xsp.test.framework.registry.BaseMultiValuePropsUseListTest;
import com.ibm.xsp.test.framework.registry.BasePropertyAllowsValueTest;
import com.ibm.xsp.test.framework.registry.BasePropertyNameCamelCaseTest;
import com.ibm.xsp.test.framework.registry.CollectionNotRunTimeBindingsTest;
import com.ibm.xsp.test.framework.registry.NoTransientPropertyTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseDefinitionsHaveDisplayNamesTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseForEditorTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseImageEditorTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseInputReadOnlyPropertyTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseInputReadOnlyRendererTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseLoadedPropertyTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseNoDesignerDefaultValueTest;
import com.ibm.xsp.test.framework.registry.annotate.BasePageEditorTest;
import com.ibm.xsp.test.framework.registry.annotate.BasePropertyCategoryKnownTest;
import com.ibm.xsp.test.framework.registry.annotate.BasePropertyStyleTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseSimpleActionCategoryTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseTableAccessibilityTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseTitleAccessibilityTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseTodoTaggedTest;
import com.ibm.xsp.test.framework.registry.annotate.BaseVarEditorTest;
import com.ibm.xsp.test.framework.registry.parse.StrictParserTest;
import com.ibm.xsp.test.framework.render.BaseInputSaveValueTest;
import com.ibm.xsp.test.framework.render.BaseRenderControlTest;
import com.ibm.xsp.test.framework.render.BaseRenderTitleTest;
import com.ibm.xsp.test.framework.serialize.BaseSerializeValueBindingTest;
import com.ibm.xsp.test.framework.setup.TestSetupTest;

/**
 * 
 */
public class ExtlibTestSuite extends TestSuite {
    
    /**
     * See {@link ExtlibSuiteSetupTest} and {@link SampleTestSuite}.
     */
    public static final long BASED_ON_SAMPLE_SUITE_VERSION = 41;
    
    public static List<Class<?>> getTestClassList() { 
        TestClassList suite = new TestClassList();

        // Put the test that generates .java files from .xsp files
        // at the start, as other tests depend on it to pass.
        // - BaseGeneratePagesTest
        suite.addTestSuite(ExtlibGeneratePagesTest.class);
        
        // .setup
        // - TestSetupTest
        suite.addTestSuite(TestSetupTest.class);
        // - BaseSuiteSetupTest
        suite.addTestSuite(ExtlibSuiteSetupTest.class);
        // (extlib test) OneUIJunitableTest
        suite.addTestSuite(OneUIJunitableTest.class);
        // (extlib test) BootstrapJunitableTest
        suite.addTestSuite(BootstrapJunitableTest.class);
        
        // .control
        suite.addTestSuite(ApplicationConfigurationDefaultsTest.class);
        suite.addTestSuite(ChangeDynamicContentTest.class);
        suite.addTestSuite(DataViewDetailsOnClientTest.class);
        suite.addTestSuite(DojoCheckBoxDefaultValueDisabledTest.class);
        suite.addTestSuite(MobileAppPageEventTest.class);
        suite.addTestSuite(MobileControlTooltipTest.class);
        suite.addTestSuite(NavigatorContainerNodeTest.class);
        
        // .lifecycle tests
        // - RegisteredDecodeTest
        suite.addTestSuite(RegisteredDecodeTest.class);
        
        // .registry
        // - BaseBooleanPropertyDefaultTest
        suite.addTestSuite(ExtlibBooleanPropertyDefaultTest.class);
        // - BaseComplexCheckTest
        suite.addTestSuite(BaseComplexCheckTest.class);
        // - BaseComponentRendererTest
        suite.addTestSuite(BaseComponentRendererTest.class);
        // - BaseComponentTypeTest
        suite.addTestSuite(BaseComponentTypeTest.class);
        // - BaseGroupReuseTest
        suite.addTestSuite(ExtlibExtlibGroupReuseTest.class);
        // - BaseInheritRendererTypeGetterTest
        suite.addTestSuite(BaseInheritRendererTypeGetterTest.class);
        // - BaseKnownPropertyRedefinitionTest
        suite.addTestSuite(ExtlibKnownPropertyRedefinitionTest.class);
        // - BaseLabelsLocalizableTest
        suite.addTestSuite(ExtlibLabelsLocalizableTest.class);
        // - BaseMultiValuePropsUseListTest
        suite.addTestSuite(BaseMultiValuePropsUseListTest.class);
        // - BaseNamingConventionErrorTest
        suite.addTestSuite(ExtlibNamingConventionErrorTest.class);
        // - BaseNamingConventionTest
        suite.addTestSuite(ExtlibNamingConventionTest.class);
        // - BaseNoRunTimeBindingsTest
        suite.addTestSuite(ExtlibNoRunTimeBindingsTest.class);
        // - BasePropertiesHaveSettersTest
        suite.addTestSuite(ExtlibPropertiesHaveSettersTest.class);
        // - BasePropertyAllowsValueTest
        suite.addTestSuite(BasePropertyAllowsValueTest.class);
        // - BasePropertyDefaultValueTest
        suite.addTestSuite(ExtlibPropertyDefaultValueTest.class);
        // - BasePropertyNameCamelCaseTest
        suite.addTestSuite(BasePropertyNameCamelCaseTest.class);
        // - CollectionNotRunTimeBindingsTest
        suite.addTestSuite(CollectionNotRunTimeBindingsTest.class);
        // - BaseComplexNotRunTimeBindingTest
        suite.addTestSuite(BaseComplexNotRunTimeBindingTest.class);
        // - NoTransientPropertyTest
        suite.addTestSuite(NoTransientPropertyTest.class);
        
        // .registry.annotate
        // - BaseControlCategoryKnownTest
        suite.addTestSuite(BaseExtlibControlCategoryKnownTest.class);
        // - BaseDefinitionsHaveDisplayNamesTest
        suite.addTestSuite(BaseDefinitionsHaveDisplayNamesTest.class);
        // - BaseDisplayNameDuplicateTest
        suite.addTestSuite(ExtlibDisplayNameDuplicateTest.class);
        // - BaseEventPropsHaveSubCategoryTest
        suite.addTestSuite(ExtlibEventPropsHaveSubCategoryTest.class);
        // - BaseForEditorTest
        suite.addTestSuite(BaseForEditorTest.class);
        // - BaseImageEditorTest
        suite.addTestSuite(BaseImageEditorTest.class);
        // - BaseLoadedPropertyTest
        suite.addTestSuite(BaseLoadedPropertyTest.class);
        // - BaseMergeWarningsTest
        suite.addTestSuite(ExtlibMergeWarningsTest.class);
        // - BaseNoDesignerDefaultValueTest
        suite.addTestSuite(BaseNoDesignerDefaultValueTest.class);
        // - BasePageEditorTest
        suite.addTestSuite(BasePageEditorTest.class);
        // - MobilePageEditorTest (only in extlib)
        suite.addTestSuite(MobilePageEditorTest.class);
        // - BasePropertiesHaveCategoriesTest
        suite.addTestSuite(ExtlibPropertiesHaveCategoriesTest.class);
        // - BasePropertyCategoryKnownTest
        suite.addTestSuite(BasePropertyCategoryKnownTest.class);
        // - BasePropertyStyleTest
        suite.addTestSuite(BasePropertyStyleTest.class);
        // - BaseSimpleActionCategoryTest
        suite.addTestSuite(BaseSimpleActionCategoryTest.class);
        // - BaseSpellCheckTest
        suite.addTestSuite(ExtlibSpellCheckTest.class);
        // - BaseTitleAccessibilityTest
        suite.addTestSuite(BaseTitleAccessibilityTest.class);
        // - BaseTableAccessibilityTest
        suite.addTestSuite(BaseTableAccessibilityTest.class);
        // - BaseInputAccessibilityTest
        suite.addTestSuite(ExtlibInputAccessibilityTest.class);
        // - BaseInputDefaultValueDisabledTest
        suite.addTestSuite(ExtlibInputDefaultValueDisabledTest.class);
        // - BaseInputReadOnlyPropertyTest
        suite.addTestSuite(BaseInputReadOnlyPropertyTest.class);
        // - BaseInputReadOnlyRendererTest
        suite.addTestSuite(BaseInputReadOnlyRendererTest.class);
        // - BaseRoleAccessibilityTest
        suite.addTestSuite(ExtlibRoleAccessibilityTest.class);
        // - BaseTodoTaggedTest
        suite.addTestSuite(BaseTodoTaggedTest.class);
        // - BaseTranslatableStringsTest
        suite.addTestSuite(ExtlibTranslatableStringsTest.class);
        // - BaseVarEditorTest
        suite.addTestSuite(BaseVarEditorTest.class);
        
        // .registry.parse
        // - StrictParserTest
        suite.addTestSuite(StrictParserTest.class);
        
        // .render
        // - BaseDojoTypeTest
        suite.addTestSuite(ExtlibDojoTypeTest.class);
        // - BaseInputSaveValueTest
        suite.addTestSuite(BaseInputSaveValueTest.class);
        // - BaseRenderBooleanPropertyTest
        suite.addTestSuite(ExtlibRenderBooleanPropertyTest.class);
        // - BaseRenderControlTest
        suite.addTestSuite(BaseRenderControlTest.class);
        // - BaseRenderDojoPropertyTest
        suite.addTestSuite(ExtlibRenderDojoPropertyTest.class);
        // - BaseRenderIdTest
        suite.addTestSuite(ExtlibRenderIdTest.class);
        // - BaseRenderPageTest
        suite.addTestSuite(ExtlibRenderPageTest.class);
        // - BaseRenderThemeControlTest
        suite.addTestSuite(ExtlibRenderThemeControlTest.class);
        suite.addTestSuite(MobileRenderThemeControlTest.class);
        // - BaseRenderTitleTest
        suite.addTestSuite(BaseRenderTitleTest.class);
        
        // .serialize
        // - BaseReflectionSerializeTest
        suite.addTestSuite(ExtlibReflectionSerializeTest.class);
        // - BaseRegisteredSerializationTest
        suite.addTestSuite(ExtlibRegisteredSerializationTest.class);
        // - BaseSerializeValueBindingTest
        suite.addTestSuite(BaseSerializeValueBindingTest.class);
        // - BaseViewSerializeTest
        suite.addTestSuite(ExtlibViewSerializeTest.class);
        
//        //xsp.editor.registry
//        suite.addTestSuite(StrictDesignerTest.class);
//        
//        // xsp.editor.warn
//        // these tests are not fatal and should not prevent delivering
//        // but they need to be fixed eventually.
//        suite.addTestSuite(AlignEditorTest.class); // TODO depends on non-..xsp.test test plugins
//        suite.addTestSuite(TabindexEditorTest.class); // TODO depends on non-..xsp.test test plugins
        
        // .version
        // - BaseSinceVersionsSetTest
        suite.addTestSuite(ExtlibSinceVersionsSetTest.class);
        
        return suite.getTests();
    }
    public static Test suite() { 
        TestSuite suite = new ExtlibTestSuite();
        TestClassList.addAll(suite, getTestClassList());
        return suite;
    }
    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }
}
