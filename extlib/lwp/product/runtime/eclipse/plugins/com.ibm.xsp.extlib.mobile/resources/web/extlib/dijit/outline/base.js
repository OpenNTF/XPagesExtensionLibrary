dojo.provide("extlib.dijit.outline.base");

dojo.require("dojox.mobile");
dojo.requireIf(!dojo.isWebKit, "dojox.mobile.compat");

dojo.require("dijit._Widget");
dojo.require("dijit.Menu");


dojo.declare("extlib.dijit.outline.base",
[dijit._Widget],{
	jsonTree:"",
	_menu:null,
	
	postCreate:function(){
		this.jsonTree=dojo.fromJson(this.jsonTree.replace(/\\\"/g,"\""));
		this._renderTree();
		this.renderMenu(this._menu);
	},
	
	_renderSubTree:function(menuItem,menu){
		try{
			if(menuItem.items){
				var pSubMenu=this.createMenuObject();
				for(var i=0;i<menuItem.items.length;i++){
					this._renderSubTree(menuItem.items[i],pSubMenu);
				}
				
        		this.addSubMenu(menuItem,menu,pSubMenu);
	        	
			}
			else{
	        	this.addMenuItem(menuItem,menu);
			}
		}
		catch(e){
			console.debug(e);
		}
	},
	
	_renderTree:function(){
		this._menu=this.createMenuObject();
		for(var i=0;i<this.jsonTree.items.length;i++){
			this._renderSubTree(this.jsonTree.items[i],this._menu);
		}
	},
	
	//to be overridden by sub classes
	createMenuObject:function(){
	},
	
	addSubMenu:function(menuItem,menu,subMenuObj){
	},
	
	addMenuItem:function(menuItem,menu){
	},
	
	renderMenu:function(menu){
	}
	
});