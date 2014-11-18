/*
 * © Copyright IBM Corp. 2010, 2013
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
dojo.provide("extlib.dijit.Mobile");

dojo.require("dojo.hash");
dojo.require("dojo.NodeList-traverse");
dojo.require("dojox.mobile");
dojo.require("dojox.mobile.bookmarkable");
dojo.require("dojox.mobile.TabBar");
dojo.require('dijit._Widget');
dojo.require('dojox.mobile.View');
dojo.require("dojox.mobile.TransitionEvent");
dojo.requireIf(!dojo.isWebKit, "dojox.mobile.compat");

dojo.declare(
		"extlib.dijit.mobile.View",
		dojox.mobile.View,
	{
		// summary:
		//		An extlib widget which wraps the native dojox/mobile/View module

		resetContent: false,
		preload: false,
		loaded: false,
		applicationId: "",
		performTransition: function(/*String*/moveTo, /*Number*/transitionDir, /*String*/transition,
				/*Object|null*/context, /*String|Function*/method /*...*/){
			
			// Note before 9.0.1, the partial update to load the content was done
			// during onBeforeTransitionIn, but in 9.0.1 the xe:appPage is now supporting
			// onBefore/AfterTransitionIn, which is not compatible with loading content
			// during onBeforeTransitionIn, due to possible random ordering of 
			// the events occurring vs the AJAX response returning from the server.
			
			var moveToStr = null;
			// normalize the arguments to find the moveTo String value
			if( moveTo && typeof(moveTo) === "object" ){
				var detail = moveTo;
				moveToStr = detail.moveTo;
			}else if( typeof(moveTo) === "string" ){
				moveToStr = moveTo;
			}
			var targetView;
			if( moveToStr ){
				moveToStr.match(/#?(\w+)(.*)/);
				var targetViewId = RegExp.$1;
				targetView = dijit.byId(targetViewId);
			}
			
			if( targetView && targetView._isNeedsLoadContent && targetView._isNeedsLoadContent(moveToStr) ){
				//a function that calls: this.inherited(arguments);
				var inheritedPerformTransitionFn = dojo.hitch(this, this.inherited, arguments);
				targetView._loadContent(moveToStr, /*function callback*/inheritedPerformTransitionFn);
			}else{ // do the transition now
				this.inherited(arguments);
			}
		},
		_isNeedsLoadContent: function(/*String*/moveTo){
			if( !this.loaded || this.resetContent ){
				return true;
			}
			moveTo.match(/#?(\w+)(.*)/);
			var paramsString = RegExp.$2; 
			var query = paramsString.substring(paramsString.indexOf("?") + 1, paramsString.length);
			var params = dojo.queryToObject(query);
			if(params.resetContent=="true") {
				return true;
			}
			return false;
		},
		_loadContent: function(/*String*/moveTo, /*function*/callback){
			var domNode = dojo.byId(this.id);
			var container = (!domNode)? null : domNode.firstChild;
			if( container && container.nodeType == Node.TEXT_NODE/*3*/ ){
				container = container.nextSibling; // skip the \n newline
			}
			
			var params;
			moveTo.match(/#(\w+)(.*)/);
			var paramsString = RegExp.$2; 
			var query = paramsString.substring(paramsString.indexOf("?") + 1, paramsString.length);
			params = dojo.queryToObject(query); 
			
			params.pageTransition = "true";
			
			var axOptions = {
				"params": params, 
				"onComplete":callback
			};
			XSP.partialRefreshGet(container.id,axOptions);
			this.loaded = true; // TODO move this to the onComplete callback
		},
		buildRendering: function(){
			var appQuery = dojo.query(".singlePageApp").forEach(dojo.hitch(this, function(node){
                this.applicationId = node.id;
                var app = dijit.byId(this.applicationId);                
                this.selected = this.id == app.selectedAppPage;
            }));

			var hash = dojo.hash();
			
			//dojo.hash returns everything after hash, split into page name and additional parameters
			var hashComponents = hash.match(/([^&]*)(&.*)/);
			
			if(hashComponents != null && hashComponents.length > 1){
				hash = hashComponents[1];	
			}
			var params = {};
			if(hashComponents != null && hashComponents.length > 1){			
				params = dojo.queryToObject(hashComponents[2]);
			}
			if ( this.id == hash && !this.selected ) {
				var container = dojo.byId(hash).firstChild;
				if( container && container.nodeType == Node.TEXT_NODE/*3*/ ){
					container = container.nextSibling; // skip the \n newline
				}
				
				params.pageTransition = "true";
				var axOptions = {
						"params": params
					}
				
				XSP.allowSubmit();
				XSP.partialRefreshGet(container.id,axOptions);
				this.loaded = true;
				dojo.hash(hash, false);
			}

			this.inherited(arguments);
			
			// ensure resizing occurs...
			dojo.connect(this ,"onBeforeTransitionIn", this, this.resize);
		}
		
});

dojo.declare(
		"extlib.dijit.mobile.Application",
		dijit._Widget,
		{
			id: "",
			queue: [],
			transitioning: false,
			selectedAppPage: "",
			startup: function () 
			{
				this.loadSelectedView();
				
				dojo.connect(window,"onresize", null, dojo.hitch(this, this._doOnResize));
				dojo.connect(window,"onorientationchange", null, dojo.hitch(this, this._doOrientationChange));
			},
			loadSelectedView: function ( ) {
				if(!this.selectedAppPage)
					return;
				var appPage = dijit.byId(this.selectedAppPage);
                    
                if(appPage){
                    var container = appPage.containerNode.firstChild;
                    if( container && container.nodeType == Node.TEXT_NODE/*3*/ ){
                        container = container.nextSibling; // skip the \n newline
                    }
                    var params = {pageTransition: "true"};
                    var axOptions = { "params": params }
                    XSP.allowSubmit();
                    XSP.partialRefreshGet(container.id, axOptions);
                    appPage.selected = true;
                    appPage.loaded = true;
                }
			},
			_doOnResize: function(event){
				return this.onResize.apply(this, arguments);
			},
			onResize: function(event){
			},
			_doOrientationChange: function(event){
				return this.onOrientationChange.apply(this, arguments);
			},
			onOrientationChange: function(event){
			}
		}
);

dojo.declare(
		"extlib.dijit.mobile.Heading",
		dojox.mobile.Heading,
	{
			buildRendering: function(){
            this.inherited(arguments);
			if(this.label){
				var query, facet;
				query = dojo.query('.actionFacet',this.domNode);
				if(query.length==1) {
					facet = query[0];
					dojo.place(facet,this.domNode,"last");
				} 
			}
		},
		goTo: function(moveTo, href){
            this.inherited(arguments);
		},
		setLabel: function(label){
			if(label != this.label){
				this.label = label;
				this.domNode.firstChild.nodeValue = label;
			}
			var s = this.domNode.style;
			if(this.label.length > 50){
				// create a clone to calculate the arrow button width correctly
				// even when the heading is in the invisible state.
				var h = this.domNode.cloneNode(true);
				h.style.visibility = "hidden";
				dojo.body().appendChild(h);
				var b = h.childNodes[2];
				s.paddingLeft = b.offsetWidth + 30 + "px";
				s.textAlign = "left";
				dojo.body().removeChild(h);
				h = null;
			}else{
				s.paddingLeft = "";
				s.textAlign = "";
			}
		}
	}
);

XSP.inputFocus = function ksk ( element ) {
	result = dojo.query("input[type='text']",element);
	if ( result.length == 1 )
		result[0].focus();
}

XSP.centerNode = function ( id ) {
	node = dojo.byId(id);
	offsetX = "-"+((dojo.style(node,'width')+dojo.style(node,'border-left-width')+dojo.style(node,'border-right-width')+dojo.style(node,'padding-left')+dojo.style(node,'padding-right'))/2)+"px";
	offsetY = "-"+((dojo.style(node,'height')+dojo.style(node,'border-top-width')+dojo.style(node,'border-bottom-width')+dojo.style(node,'padding-top')+dojo.style(node,'padding-bottom'))/2)+"px";
	dojo.style(node,{"margin-top":offsetY,"margin-left":offsetX});
}

XSP.hideMobileFormTableError = function ( id, shadeId ) {
	node = dojo.byId(id);
	shadeNode = dojo.byId(shadeId);
	dojo.style(node,{"display":"none"});
	dojo.style(shadeNode,{"display":"none"});
}

/**
 * Performs a transition to another extlib.dijit.mobile.View. Updates the hash of the page
 * with the given parameters. The parameters can be passed in either as a string or as
 * an object. 
 * @param view		The currently selected view.
 * @param moveTo	The id of the view to be moved to.
 * @param dir		The direction of the transition.
 * @param transition	The type of transition to perform.
 * @param params	Parameters for the transition. Can be given either as a string (&k=v)
 * 					or as an object of key/value pairs { "foo":"bar", "k":"v" }
 * @returns
 */
XSP.moveToMPage = function sxy_rxy ( view, moveTo, dir, transition, params ) {
//    dojox.mobile._params = [];
	if ( view == null )
		return;
	var paramString = "";
	//check to see that if we have an object that it is a vanilla object
	if ( params == null ) { //nothing passed in for this arg
	   	params = "";
	} else if ( params instanceof Object && (params.constructor+"").match(/^function Object/) ) {
        for ( var k in params )
            paramString += "&"+k+"="+params[k];
        params = paramString;
    } else if ( (params.constructor+"").match(/^function String/) ) {
        if (params.length > 0 && params[0]!="&") //if we have a string make sure it starts right
            params = "&"+params;
    } else {
    	return;
    }
    if (params != "" && !params.match(/^(&(\w+)=(\w+))+/)){ 
	    	return;
	}
	
    // This is using a TransitionEvent instead of directly 
    // calling view.performTransition, so that the ViewController
    // will initialize viewRegistry.initialView, and the browser
    // back button can be used to transition back to the initial page
    // This code is similar to dojox.mobile._ItemBase.transitionTo:
    var transitionEventOptions = {
        moveTo: moveTo+params,
        transition: transition,
        transitionDir: dir
    };
    new dojox.mobile.TransitionEvent(view.domNode, transitionEventOptions).dispatch();
}

//call XSP.resizeForm for each form loaded
XSP.resizeForms = function resizeForms() {
	dojo.query('.mobileFormContainer').forEach(function(node) {
		XSP.resizeForm(node.id);
	});
}

//resize the form to fit the screen
XSP.resizeForm = function resizeForm(id) {
	var formTable = dojo.byId(id);
	var rowWidth;
	var formRows = dojo.query('.formRow:first-child', formTable);
	if( formRows.length == 0 ){
		return; // no rows
	}
	rowWidth = Math.max(90,
				formRows[0].offsetWidth 
				- dojo.style(formRows[0], "padding-left") 
				- dojo.style(formRows[0], "padding-right")
			);
	
	var maxLabelWidth = 0;
	var labelPositionAboveExists = false;
	var labels = dojo.query('.labelCell', formTable);
	labels.forEach(function(node) {
		var dataNode = null;
		if(node.childNodes.length == 2){
			if(node.childNodes[1].className == "dataCell"){
				if(!labelPositionAboveExists){
					labelPositionAboveExists = true;
				}
			}
		}
		if(!dojo.hasClass(node, 'textareaLabelCell') && !labelPositionAboveExists) {
			maxLabelWidth = Math.max(node.offsetWidth, maxLabelWidth);
		}else if(labelPositionAboveExists){
			maxLabelWidth = rowWidth;
		}
	});
	
	if( !labelPositionAboveExists && maxLabelWidth > 0 ){
		// the label cells have calculated their offsetWidth's
		var newLabelColumnWidth = Math.max(maxLabelWidth, 90);
		labels.forEach(function(node) {
			if(!dojo.hasClass(node, 'textareaLabelCell')) {
				node.style.width = newLabelColumnWidth + 'px';
			}
		});
		var margin = 15;
		var newDataColumnWidth = rowWidth - newLabelColumnWidth - 25;
		var datas = dojo.query('.dataCell', formTable);
		datas.forEach(function(node) {
			if(dojo.hasClass(node, 'textareaDataCell')) {
				node.style.width = (rowWidth - margin) + 'px';
			}else{
				node.style.width = newDataColumnWidth + 'px';
			}
		});
		// resize completed successfully, done.
		return;
	}
	else{
		labels.forEach(function(node) {
			var dataNode = null;
			var labelPositionAboveInstance = false;
			if(node.childNodes.length == 2){
				if(node.childNodes[1].className == "dataCell"){
					labelPositionAboveInstance = true;
				}
			}
			if(!dojo.hasClass(node, 'textareaLabelCell') && !labelPositionAboveInstance) {
				node.style.width = Math.max(maxLabelWidth, 90) + "px";
			}else{
				node.style.width = Math.max(maxLabelWidth, rowWidth) + "px";
			}
		});
		
		var datas = dojo.query('.dataCell', formTable);
		var margin = 28;
		datas.forEach(function(node) {
			var labelNode = null;
			var labelPositionAboveInstance = false;
			if(node.parentNode.className == "labelCell"){
				labelPositionAboveInstance = true;
			}
			if(!dojo.hasClass(node, 'textareaDataCell') && !labelPositionAboveInstance) {
				node.style.width = (rowWidth - node.parentNode.style.width - margin) + "px";
			}else{
				node.style.width = (rowWidth - margin) + "px";
			}
			if(null != node.childNodes[2] && dojo.hasClass(node.childNodes[2], "xspInputFieldRichText")){
				node.childNodes[2].style.width = (rowWidth - margin) + "px";
			}
		});
	}
	
	// check to see if this xe:formTable is in 
	// an xe:appPage that is not currently visible, in which case
	// we'll have to re-resize when it becomes visible later.
	var ancestorView = null; // a dojox.mobile.View
	for( var ancestorNode = dojo.byId(id); null != ancestorNode; ancestorNode = ancestorNode.parentNode){
		var ancestorDijit = dijit.byNode(ancestorNode);
		if( null != ancestorDijit && 'undefined' != typeof(ancestorDijit.onAfterTransitionIn) ){
			ancestorView = ancestorDijit;
			break;
		}
	}
	if( null == ancestorView || ancestorView === ancestorView.getShowingView() ){
		// the reason for the zero widths is not 
		// because of being in a not-shown appPage
		return;
	}
	var delayedResizer = {
			formTableId: id,
			handleToDisconnect: null,
			delayedResizeFn: function x_rfcb(){
				if( this.handleToDisconnect ){
					// disconnect the callback to prevent subsequent calls 
					// of this callback and to prevent multiple partial updates 
					// from building up multiple callbacks,
					// each getting triggered on transitions.
					dojo.disconnect(this.handleToDisconnect);
				}
				XSP.resizeForm(this.formTableId);
			}
		};
	delayedResizer.handleToDisconnect = dojo.connect(ancestorView, 'onAfterTransitionIn', 
			delayedResizer, delayedResizer.delayedResizeFn);
}

//rotation listeners (must come after function declarations)
if(window.onorientationchange !== undefined) {
	dojo.connect(dojo.global, "onorientationchange", XSP, XSP.resizeForms);
}

//hold a reference to the base impl as it
//will be used in the overriden version
//when not running under iOS...
XSP.__validationError = XSP.validationError;

//now override the base impl...
XSP.validationError = function validationError(clientId, message){
	// check if running iOS...
	dojo.require("dojox.mobile.app");
	var iOS = dojox.mobile.app.isIPhone || dojox.mobile.app.isIPad || dojox.mobile.app.isIPod;
	if(iOS){
		// if iOS, then spawn the error alert in a separate
		// UI thread to prevent the dialog freezing, this occurs
		// with (onchange+input[type=file]+alert) in iOS safari...
		setTimeout(function(){
			XSP.error(message);
		}, 0);
		var e = XSP.getElementById(clientId);
	    if(XSP.hasDijit()){
	        var widget = dijit.byId(clientId);
	        if(widget) e = widget;
	    }
	    if(e){
	    	if(e.select) e.select();
	    	if(e.focus) e.focus();
	    }
	}else{
		// if not iOS, the simply run the base impl...
		XSP.__validationError(clientId, message);
	}
};

dojo.declare(
		"extlib.dijit.mobile.ListItem",
		dojox.mobile.ListItem,
	{
        postCreate: function(){
        }
    });

dojo.declare("extlib.dijit.mobile.TabBar", 
		dojox.mobile.TabBar,{
            onResize: function(){
                this.inherited(arguments);
                if(dojo.query('body.android').length){
                    dojo.style(this.domNode, {width: '100%'});
                }
            }
        });
