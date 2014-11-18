dojo.provide("extlib.theme.OneUIA11Y");

XSP.addOnLoad(function() {
        var bodyElem = document.getElementsByTagName("body")[0];
        if (dojo.hasClass(bodyElem, "dijit_a11y")) {            
            dojo.addClass(bodyElem, "lotusImagesOff");
        }
});
