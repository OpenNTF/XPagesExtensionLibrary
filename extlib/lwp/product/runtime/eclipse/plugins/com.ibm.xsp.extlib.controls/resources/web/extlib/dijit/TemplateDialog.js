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
dojo.provide("extlib.dijit.TemplateDialog");

dojo.require("dijit._Widget")
dojo.require("dijit._Templated")
dojo.require("extlib.dijit.Dialog")

XSP._templatesStyles=[]
                      
/*
 * This is dialog class that loads its content from an html template
 */
dojo.declare(
     'extlib.dijit.TemplateDialog',
     [dijit._Widget, dijit._Templated],
     {
          popup : null,
          widgetsInTemplate: true,
          dlgTitle: "",
          preventCache: false,

          postMixInProperties: function() {
              this.inherited(arguments);
          },
          postCreate: function() {
         	  dojo.connect(this.pkOk,"onclick",this,'ok')
         	  dojo.connect(this.pkCancel,"onclick",this,'cancel')
              this.inherited(arguments);
          },
          destroy: function(){
        	  this.inherited(arguments);
          },
          focusInputControl: function(){
        	  if(this.control) {
      			  var c = dojo.byId(this.control);
        		  setTimeout(function(){c.focus()});
        	  }
          },
          show: function(){
			  var dlgClass = dojo.getObject(XSP._dialog_type||"extlib.dijit.Dialog")
              this.popup = new dlgClass({title: this.dlgTitle});
              this.popup.attr("content", this.domNode);
              this.popup.startup();
              this.popup.show();
              // The dialog, and this widget should be cleaned-up from memory when the underlay is gone
              // this is unfortunately done after the fadeout effect is done, and there no other known event
              dojo.connect(this.popup._fadeOut,"onEnd",dojo.hitch(this,function(){
                  this.popup.destroyRecursive();
                  this.destroyRecursive();
              }));
          },
          hide: function(){
              this.popup.hide();
          },
          cancel: function(){
              this.hide();
          },
          ok: function() {},
          
          // val: object that contains 2 properties
          //		- values, contains the list of values
          //		- labels, contains the associated labels, if any
          // msep: separator when multiple values are returned
          updateControl: function(id,val,msep) {
  			  var c = dojo.byId(id)
  			  if(c) {
  				  var dj = dijit.byId(id);
  				  if(dj && dj.labels && val.labels) {
  					  for(var idx in val.values) {
  						  if(!dj.labels[val.values[idx]]) {
  							  dj.labels[val.values[idx]]=val.labels[idx];
  						  }
  					  }
  				  }
				  
				  var v = val.values.join(msep?msep:"");
				  if(dj && dj._setValueAttr) {
					dj._setValueAttr(v,true);
				  } else {
					c.value = v;
					if(dojo.isIE) {
						c.fireEvent('onchange')
					} else {
						var evObj = document.createEvent('Event');
						evObj.initEvent( 'change', true, false );
						c.dispatchEvent(evObj);
					}
				  }
  			  }
          },
          
          _fixStyles: function(s) {
        	  // IE ignores the style tags in a template
        	  if(dojo.isIE&&!XSP._templatesStyles[s]) {
        		  XSP._templatesStyles[s]=true
        		  var i1=s.indexOf("<style>"); var i2=s.indexOf("</style>");
        		  if(i1>=0&&i2>=0) {
            	      var st = dojo.doc.createElement('style');
            	      st.setAttribute("type", "text/css");
                	  st.styleSheet.cssText = s.substring(i1+7,i2);
                	  document.getElementsByTagName('head')[0].appendChild(st);          
        		  }
        	  }
          },
          //BGLN8NLMRU
          getControlValue: function(id) {
        	var controlValue=null;
        	var element = XSP.getElementById(id);
  			if(XSP.hasDijit()){
  				var djd = dijit.byId(id);
  				if(djd) {
  					controlValue = XSP.getDijitFieldValue(djd)
  				}
  			}
  			if(controlValue==null && element && !element.disabled) {
  				controlValue = XSP.getFieldValue(element);
  			}
  			return controlValue;
          },
          // Utility functions
          listScrollTo: function(list,node){
        	  if(node==null) {
      	  		list.scrollTop = 0
        	  } else {
        	  	var o = list.offsetTop
        	  	var t = list.scrollTop
        	  	var h = list.offsetHeight
        	  	if(node.offsetTop-o<t) {
        	  		list.scrollTop = node.offsetTop-o
        	  	} else {
        	  		if(node.offsetTop+node.offsetHeight-o>t+h) {
        	  			list.scrollTop = node.offsetTop+node.offsetHeight-h-o
        	  		}
        	  	}
        	  }
			},
			listClear: function(list){
				while(list.hasChildNodes()) {list.removeChild(list.lastChild);}
			},
			listFirstItem: function(list){
				var q = dojo.query("li:first-child", list);
				return q && q.length ? q[0] : null;
			}
      }
);
