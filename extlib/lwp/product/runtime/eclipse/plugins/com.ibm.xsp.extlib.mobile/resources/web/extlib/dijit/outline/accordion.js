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
dojo.provide("extlib.dijit.outline.accordion");
dojo.require("extlib.dijit.outline.base");

dojo.declare("extlib.dijit.outline.accordion",
[extlib.dijit.outline.base],{
	isRoot:true,
	createMenuObject:function(){
		var containerDomNode=new dojox.mobile.EdgeToEdgeList();
		
		if(!this.isRoot){
			dojo.addClass(containerDomNode.domNode,"menuHidden");
			dojo.addClass(containerDomNode.domNode,"menuCreated");
			dojo.removeClass(containerDomNode.domNode, "mblEdgeToEdgeList");
			dojo.addClass(containerDomNode.domNode,"mblRoundRectList");
		}
		else{
			this.isRoot=false;
		}
		
		return(containerDomNode);
	},
	
	addSubMenu:function(menuItem,menu,subMenuObj){
		var itemDomNode=new dojox.mobile.ListItem({
			moveTo:menuItem.href,
			label:menuItem.label,
			//Fix for SPR#BGLN9HYDW9 Part 1 - No icon image appearing for container nodes
			icon: menuItem.iconImg,
            // Fix for SPR#BGLN9J6K6U - Icon alt text not applied
            alt: menuItem.iconAlt
		});
		
		dojo.connect(itemDomNode.domNode,"onclick",dojo.hitch(this,"_toggleMenu",{menuItem:itemDomNode}));

		//Fix for SPR#BGLN9HYDW9 Part 2 - No icon image appearing for container nodes
		//Run startup function to create icon image
		itemDomNode.startup();
		if(menuItem.href) {
		    //But startup adds unwanted rightArrow to a sub menu when href is set, remove first child after startup
		    itemDomNode.removeChild(itemDomNode.domNode.firstChild);
		}
		
		//Add down arrow
		var arrow=dojo.create("div",null,itemDomNode.domNode,"first");
		dojo.addClass(arrow,"mblArrowDown");
		
		//append the new node to the menu
		itemDomNode.placeAt(menu);
		subMenuObj.placeAt(itemDomNode.domNode);
		
		//item styling
		dojo.addClass(itemDomNode.domNode,menuItem.containerClass);
        // Fix for SPR#BGLN9J6KLF - Custom style properties not applied
        var cssStyle = itemDomNode.domNode.style.cssText + menuItem.containerStyle;
        itemDomNode.domNode.style.cssText = cssStyle;
		
		// Fix for SPR#BGLN9HZH98 - nested container nodes dont work
		dojo.style(itemDomNode.domNode,"height","auto");
		
		//Fix for SPR#BGLN9HYDW9 Part 3 - No icon image appearing for container nodes
		if(menuItem.iconImg != null && menuItem.iconImg != "") {
		    if(itemDomNode.iconNode){
		        //Adjust image height if specified
		        if(menuItem.iconHeight != null && menuItem.iconHeight != "") {
		            itemDomNode.iconNode.style.height = menuItem.iconHeight;
		        }
		        //Adjust image width if specified
		        if(menuItem.iconWidth != null && menuItem.iconWidth != "") {
		            itemDomNode.iconNode.style.width = menuItem.iconWidth;
		        }
		        //Adjust the margin-top of the icon so it appears vertically aligned to center in the ListItem
		        //This code is immitating the code from layoutVariableHeight function of ListItem.js
			    var f = function(){
			        var t = Math.round((itemDomNode.domNode.offsetHeight - itemDomNode.iconNode.offsetHeight) / 2 - itemDomNode.domNode.style.paddingTop);
			        itemDomNode.iconNode.style.marginTop = t + "px";
			    }
			    if(itemDomNode.iconNode.offsetHeight === 0 && itemDomNode.iconNode.tagName === "IMG"){
			        itemDomNode.iconNode.onload = f;
			    }
			}
		}
	},
	
	_toggleMenu: function(args){
		dojo.stopEvent(window.event);
		var menuItem = args.menuItem;
		
		dojo.toggleClass(menuItem.domNode.firstChild, "mblArrowDown");
		dojo.toggleClass(menuItem.domNode.firstChild, "mblArrowUp");
		//I test the arrowUp because it has just changed
		if(dojo.hasClass(menuItem.domNode.firstChild, "mblArrowUp")) {
			var toggleNode = dojo.query('.menuHidden', menuItem.domNode)[0];
			// Height changes are handled in menuShown & menuHidden css classes
			//show the first submenu
			dojo.toggleClass(toggleNode, "menuShown");
			dojo.toggleClass(toggleNode, "menuHidden");
			if(dojo.hasClass(toggleNode, "menuCreated")) {
				dojo.removeClass(toggleNode, "menuCreated");
			}
		}
		else{
			//closing down open submenus
			dojo.forEach(dojo.query('.menuShown', menuItem.domNode),
					function(node) {
						var arrowContainer = dojo.query('.mblArrowUp', node)[0];
						if(arrowContainer) {
							dojo.toggleClass(arrowContainer, "mblArrowDown");
							dojo.toggleClass(arrowContainer, "mblArrowUp");
						}
						// Height changes are now handled in menuShown, menuHidden css classes
						dojo.toggleClass(node, "menuHidden");
						dojo.toggleClass(node, "menuShown");
					}
			);
		}
	},
	
	addMenuItem:function(menuItem,menu)
	{
		var itemDomNode=new dojox.mobile.ListItem({
			moveTo:menuItem.href,
			label:menuItem.label,
			// "id" support added for SPR#RDIM9FZFPN, for XPages automation.
			id: menuItem.id,
			// Fix for SPR#BGLN9HYDW9 Part 4 - No icon image appearing for leaf nodes
			icon: menuItem.iconImg,
            // Fix for SPR#BGLN9J6K6U - Icon alt text not applied
			alt: menuItem.iconAlt
		});
		dojo.addClass(itemDomNode.domNode,menuItem.itemClass);
		// Fix for SPR#BGLN9J6KLF - Custom style properties not applied
        var cssStyle = itemDomNode.domNode.style.cssText + menuItem.itemStyle;
		itemDomNode.domNode.style.cssText = cssStyle;
		
		//append the new node to the menu and call startup to create the control
		itemDomNode.placeAt(menu);
		itemDomNode.startup();
		
		// Fix for SPR#BGLN9HYDW9 Part 5 - No icon image appearing for leaf nodes
		if(menuItem.iconImg != null && menuItem.iconImg != "") {
            if(itemDomNode.iconNode){
                //Adjust image height if specified
                if(menuItem.iconHeight != null && menuItem.iconHeight != "") {
                    itemDomNode.iconNode.style.height = menuItem.iconHeight;
                }
                //Adjust image width if specified
                if(menuItem.iconWidth != null && menuItem.iconWidth != "") {
                    itemDomNode.iconNode.style.width = menuItem.iconWidth;
                }
                //Adjust the margin-top of the icon so it appears vertically aligned to center in the ListItem
                //This code is immitating the code from layoutVariableHeight function of ListItem.js
                var f = function(){
                    var t = Math.round((itemDomNode.domNode.offsetHeight - itemDomNode.iconNode.offsetHeight) / 2 - itemDomNode.domNode.style.paddingTop);
                    itemDomNode.iconNode.style.marginTop = t + "px";
                }
                if(itemDomNode.iconNode.offsetHeight === 0 && itemDomNode.iconNode.tagName === "IMG"){
                    itemDomNode.iconNode.onload = f;
                }
            }
        }
	},
	
	renderMenu:function(menu){
		//this.srcNodeRef.appendChild(menu);
		menu.placeAt(this.srcNodeRef);
	}
});