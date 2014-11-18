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

dojo.provide("dwa.lv.msgs");

dojo.require("dojo.i18n");
dojo.requireLocalization("dwa.lv", "listview_c");
dojo.requireLocalization("dwa.lv", "listview_s");

dwa.lv.msgs = {
  listview_cmsgs: 0,
  listview_smsgs: 0
};

dwa.lv.msgs.checkLang = function(lang){
  if( !dwa.lv.msgs.msglang ){
    dwa.lv.msgs.msglang = lang;
  }else if( lang && dwa.lv.msgs.msglang != lang ){
    dwa.lv.msgs.msglang = lang;
    // flush cached msgdata
    dwa.lv.msgs.listview_cmsgs = 0;
    dwa.lv.msgs.listview_smsgs = 0;
  }
}

dwa.lv.msgs.getListViewCMsg = function(key,lang){
  dwa.lv.msgs.checkLang(lang);
  if( dwa.lv.msgs.listview_cmsgs == 0){
    dwa.lv.msgs.listview_cmsgs = dojo.i18n.getLocalization("dwa.lv", "listview_c", lang);
  }
  var msg = dwa.lv.msgs.listview_cmsgs[key];

  //if( !msg ){
  //  alert('undefined key=' + key + 'cmsg=' + dwa.lv.msgs.listview_cmsgs);
  //}

  return msg;
};

dwa.lv.msgs.getListViewSMsg = function(key,lang){
  dwa.lv.msgs.checkLang(lang);
  if( dwa.lv.msgs.listview_smsgs == 0){
    dwa.lv.msgs.listview_smsgs = dojo.i18n.getLocalization("dwa.lv", "listview_s", lang);
  }

  //var msg = dwa.lv.msgs.listview_smsgs[key];
  //if( !msg ){
  //  alert('undefined key=' + key + 'smsg=' + dwa.lv.msgs.listview_smsgs );
  //}

  return dwa.lv.msgs.listview_smsgs[key];
};
