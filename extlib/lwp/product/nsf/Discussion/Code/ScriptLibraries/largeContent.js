dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dojo.parser");
dojo.require("dojox.mobile");
dojo.require("dojo.hash");
dojo.requireIf(!dojo.isWebKit, "dojox.mobile.compat");

dojo.declare("xsp.largeContent", [ dijit._Widget, dijit._Templated], {
	
	content : "", 	//content to be displayed - doesn't matter the size
	url: "",		//url to page that will display content on its own
	from: "",		//the name of the view that will be leaving (i.e. the mPage id, e.g. "document")
	
	
	templateString : "<div dojoAttachPoint='containerNode'>" +
						"<div dojoAttachPoint='contentDiv'></div>" +
						"<button dojoType='dijit.form.Button' type='button'" +	
							"dojoAttachPoint='viewContentButton' dojoAttachEvent='onclick: openPage'" +
							"style='width:100px; height:25px; font-size:12px;'>" +
							"View Content" +
						"</button> </div>",
	
	preamble : function() {
		//this.templateString = dojo.cache("", "../../../" + this.path + "/largeContent.html");
	},
	
	postCreate: function()
	{
		//console.log('postCreate');
	},

	startup : function() 
	{
		var control = this.containerNode;
		var div = this.contentDiv;
		var button = this.viewContentButton;
		
		//console.log(this.url);
		
		div.innerHTML = this.content;
		
		//var con = dojo.query('.labelContent')[0];
		div.style.width = '290px';
		//div.style.overflow = 'hidden';

		//div.innerHTML = con.innerHTML;

		if (div.scrollWidth > 290) 
		{
			//console.log("bigger");
			button.style.display = 'inline';
			
		}
		else
		{
			//console.log("smaller");
			button.style.display = 'none';
		}
	},
	
	openPage: function()
	{
		/*dojo.attr('displayContentDiv', 
		{
          //tabIndex: 1,
          //name: "nameAtt",
          innerHTML: "New Content"
        });*/
		
		//var displayCon = dijit.byId('displayContentDiv');
		//displayCon.content = this.content;
		
		//console.log('pressed button');
		
		
		var view = dijit.byId(this.from);
		view.performTransition(this.url, 1, 'slide');
		
		//document.body.style.background = '#ffffff';
		
		//var view = dijit.byNode(this.domNode.parentNode);
		//XSP.moveToMPage(this.tempDiv, "#largeContent", 1, "slide", null);
		//view.performTransition("largeContent", -1, null);
	}
});
