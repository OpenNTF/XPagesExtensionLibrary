<?xml version="1.0" encoding="UTF-8"?>
<xp:view xmlns:xp="http://www.ibm.com/xsp/core" disableTheme="true">
    <xp:label loaded="${javascript:compositeData.displayLabel}" value="${javascript:compositeData.labelText}"
        styleClass="xvwDisplayChoice" style="padding:0px 3px 0px 3px;" id="label2"></xp:label>
    <xp:span id="${javascript:compositeData.actionGroupName + '_actionManager'}" styleClass="xvwDisplayChoice">
        <xp:repeat id="actionManagerRepeat" rows="30"
            repeatControls="false" disableTheme="true" style="float: left; padding 1em;"
            first="0" var="action" indexVar="i" removeRepeat="true">
            <xp:this.value><![CDATA[${javascript:compositeData.actions}]]></xp:this.value>
            <xp:link id="link1">
                <xp:this.style><![CDATA[${javascript:if(compositeData.padActions){
    return "padding:4px";
}else{
    return "padding:0px";
}}]]></xp:this.style>
                <xp:image id="image1" alt="#{javascript:action.imageAlt}" title="#{javascript:action.imageAlt}">
                    <xp:this.url><![CDATA[#{javascript:var selectedId = sessionScope.get(compositeData.actionGroupName + "_selectedId");
if(selectedId == getClientId(this.getParent().getId())){
    if(null != action.params){
        for(var param in action.params){
            if(null != param.name){
                sessionScope.put(param.name, param.value);
            }
        }
    }
    if(null != compositeData.dynamicContentId){
        var c = getComponent(compositeData.dynamicContentId);
        if(null != c){
            c.show(action.selectedValue);
        }
    }
    return action.selectedImage;
}else{
    if(selectedId == null || selectedId == ""){
        if(compositeData.defaultSelectedValue == action.selectedValue ){
            sessionScope.put(compositeData.actionGroupName + "_selectedId", getClientId(this.getParent().getId()));
            sessionScope.put(compositeData.actionGroupName + "_selectedValue", action.selectedValue);
            return action.selectedImage;
        }else{
            return action.deselectedImage;
        }
    }else{
        return action.deselectedImage;
    }
}}]]></xp:this.url>
                </xp:image>
                <xp:eventHandler event="onclick" submit="true"
                    refreshMode="partial" refreshId="${compositeData.refreshId}">
                    <xp:this.action><![CDATA[#{javascript:sessionScope.put(compositeData.actionGroupName + "_selectedId", getClientId(this.getParent().getId()));
sessionScope.put(compositeData.actionGroupName + "_selectedValue", action.selectedValue);
if(null != action.params){
    for(var param in action.params){
        if(null != param.name){
            sessionScope.put(param.name, param.value);
        }
    }
}
/*
if(null != compositeData.dynamicContentId){
    var c = getComponent(compositeData.dynamicContentId);
    if(null != c){
        c.show(action.selectedValue);
    }
}
*/
}]]></xp:this.action>
                </xp:eventHandler>
            </xp:link>
        </xp:repeat>
    </xp:span>
</xp:view>
