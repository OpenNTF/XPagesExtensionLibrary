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
dojo.provide("extlib.responsive.dijit.xsp.bootstrap.PickerListSearch");

dojo.require("extlib.responsive.dijit.xsp.bootstrap.Dialog")
dojo.require("extlib.dijit.PickerListSearch")
dojo.require("extlib.responsive.dijit.xsp.bootstrap.ListTextBox")

dojo.declare(
	'extlib.responsive.dijit.xsp.bootstrap.PickerListSearch',
	[extlib.dijit.PickerListSearch],
	{
        listWidth: "100%",
		templateString: dojo.cache("extlib.responsive.dijit.xsp.bootstrap", "templates/PickerListSearch.html")
	}
);