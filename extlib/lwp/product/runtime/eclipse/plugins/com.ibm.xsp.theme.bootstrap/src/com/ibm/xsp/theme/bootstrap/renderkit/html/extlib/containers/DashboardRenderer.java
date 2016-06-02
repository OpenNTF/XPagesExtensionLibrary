/*
 * © Copyright IBM Corp. 2014, 2015, 2016
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
* Author: Brian Gleeson (brian.gleeson@ie.ibm.com)
* Date: 29 Sep 2014
* DashboardRenderer.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibRenderUtil;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.theme.bootstrap.components.responsive.DashNode;
import com.ibm.xsp.theme.bootstrap.components.responsive.UIDashboard;
import com.ibm.xsp.util.HtmlUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class DashboardRenderer extends FacesRendererEx {
    
    //column class prefixes
    public static final String COLUMN_TINY      = "col-xs-";  // $NON-NLS-1$
    public static final String COLUMN_SMALL     = "col-sm-";  // $NON-NLS-1$
    public static final String COLUMN_MEDIUM    = "col-md-";  // $NON-NLS-1$
    public static final String COLUMN_LARGE     = "col-lg-";  // $NON-NLS-1$
    
    // Container
    protected static final int PROP_CONTAINER_CLASS                = 1;
    protected static final int PROP_CONTAINER_STYLE                = 2;
    protected static final int PROP_INNER_CLASS                    = 3;
    
    // Heading
    protected static final int PROP_HEADING_TAG                    = 10;
    protected static final int PROP_HEADING_STYLE                  = 11;
    protected static final int PROP_HEADING_CLASS                  = 12;
    
    // Dash Nodes
    protected static final int PROP_TOTALCOLUMNS                   = 20;
    protected static final int PROP_NODE_CONTAINER_TAG             = 21;
    protected static final int PROP_NODE_CONTAINER_CLASS           = 22;
    protected static final int PROP_NODE_CONTAINER_STYLE           = 23;
    protected static final int PROP_NODE_TITLE_TAG                 = 24;
    protected static final int PROP_NODE_TITLE_STYLE               = 25;
    protected static final int PROP_NODE_TITLE_CLASS               = 26;
    protected static final int PROP_NODE_IMAGE_SRC                 = 27;
    protected static final int PROP_NODE_IMAGE_STYLE               = 28;
    protected static final int PROP_NODE_IMAGE_CLASS               = 29;
    protected static final int PROP_NODE_IMAGE_WIDTH_DEFAULT       = 30;
    protected static final int PROP_NODE_IMAGE_HEIGHT_DEFAULT      = 31;
    protected static final int PROP_NODE_DESCRIPTION_STYLECLASS    = 32;
    protected static final int PROP_NODE_DEFAULT_GLYPH_SIZE        = 33;
    protected static final int PROP_NODE_DEFAULT_GLYPH_TAG         = 34;
    protected static final int PROP_NODE_DEFAULT_GLYPH_CLASS       = 35;
    protected static final int PROP_NODE_DEFAULT_GLYPH_STYLE       = 36;
    protected static final int PROP_NODE_DEFAULT_BADGE_TAG         = 37;
    protected static final int PROP_NODE_DEFAULT_BADGE_STYLE       = 38;
    protected static final int PROP_NODE_DEFAULT_BADGE_CLASS       = 39;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // Container div
            case PROP_CONTAINER_CLASS:               return "row xspDashboard"; //$NON-NLS-1$
            case PROP_CONTAINER_STYLE:               return ""; //$NON-NLS-1$
            case PROP_INNER_CLASS:                   return "xspDashboardInner"; //$NON-NLS-1$
            // Heading
            case PROP_HEADING_TAG:                   return "h2"; // $NON-NLS-1$
            case PROP_HEADING_STYLE:                 return "border-bottom: 1px solid #DDDDDD;"; //$NON-NLS-1$
            case PROP_HEADING_CLASS:                 return ""; //$NON-NLS-1$
            // Dash Nodes
            case PROP_TOTALCOLUMNS:                  return 12;
            case PROP_NODE_CONTAINER_TAG:            return "div"; //$NON-NLS-1$
            case PROP_NODE_CONTAINER_CLASS:          return "xspDash"; //$NON-NLS-1$
            case PROP_NODE_CONTAINER_STYLE:          return ""; //$NON-NLS-1$
            // Node Title
            case PROP_NODE_TITLE_TAG:                return "h3"; // $NON-NLS-1$
            case PROP_NODE_TITLE_STYLE:              return ""; //$NON-NLS-1$
            case PROP_NODE_TITLE_CLASS:              return "xspDashTitle"; //$NON-NLS-1$
            // Node Image
            case PROP_NODE_IMAGE_SRC:                return ""; // $NON-NLS-1$
            case PROP_NODE_IMAGE_STYLE:              return "display:inline-block;"; //$NON-NLS-1$
            case PROP_NODE_IMAGE_CLASS:              return "img-responsive"; //$NON-NLS-1$
            case PROP_NODE_IMAGE_WIDTH_DEFAULT:      return "200px"; //$NON-NLS-1$
            case PROP_NODE_IMAGE_HEIGHT_DEFAULT:     return "200px"; //$NON-NLS-1$
            //Node Description
            case PROP_NODE_DESCRIPTION_STYLECLASS:   return "text-muted xspNodeDescription"; //$NON-NLS-1$
            // Node Glyphicon
            case PROP_NODE_DEFAULT_GLYPH_TAG:        return "div"; // $NON-NLS-1$
            case PROP_NODE_DEFAULT_GLYPH_SIZE:       return ""; //$NON-NLS-1$
            case PROP_NODE_DEFAULT_GLYPH_CLASS:      return "glyphicon "; //$NON-NLS-1$
            case PROP_NODE_DEFAULT_GLYPH_STYLE:      return ""; //$NON-NLS-1$
            // Node Badge
            case PROP_NODE_DEFAULT_BADGE_TAG:        return "span"; // $NON-NLS-1$
            case PROP_NODE_DEFAULT_BADGE_CLASS:      return "badge dashBadge"; //$NON-NLS-1$
            case PROP_NODE_DEFAULT_BADGE_STYLE:      return ""; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }
    
    public void writeDashboard(FacesContext context, ResponseWriter w, UIDashboard c) throws IOException{
        String containerStyle      = "";
        String containerStyleClass = "";
        String headingText         = "";
        String headingStyle        = "";
        String headingStyleClass   = "";
        String boardTitle          = "";
        
        if(c != null) {
            containerStyle      = c.getStyle();
            containerStyleClass = c.getStyleClass();
            headingText         = c.getHeading();
            headingStyle        = c.getHeadingStyle();
            headingStyleClass   = c.getHeadingStyleClass();
            boardTitle          = c.getTitle();
        }
        //Get the list of dash nodes
        List<DashNode> nodes = c.getDashNodes();
        
        //write container div
        w.startElement("div", c); // $NON-NLS-1$
        
        if(HtmlUtil.isUserId(c.getId())) {
            String clientId = c.getClientId(context);
            w.writeAttribute("id", clientId, null); // $NON-NLS-1$ $NON-NLS-2$
        }
        
        String containerClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_CONTAINER_CLASS), containerStyleClass);
        if(StringUtil.isNotEmpty(containerClazz)) {
            w.writeAttribute("class", containerClazz, null); // $NON-NLS-1$
        }
        String containerMixinStyle = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_CONTAINER_STYLE), containerStyle);
        if(StringUtil.isNotEmpty(containerMixinStyle)) {
            w.writeAttribute("style", containerMixinStyle, null); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(boardTitle)) {
            w.writeText(boardTitle, null);
        }else{
            w.writeAttribute("title", "Dashboard", null); // $NON-NLS-1$ $NLS-DashboardRenderer.dashboard-2$
        }
        String role = "presentation"; // $NON-NLS-1$
        w.writeAttribute("role", role, null); // $NON-NLS-1$

        //write title div
        if(StringUtil.isNotEmpty(headingText)) {
            w.startElement((String)getProperty(PROP_HEADING_TAG), c);
            String titleClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_HEADING_CLASS), headingStyleClass);
            if(StringUtil.isNotEmpty(titleClazz)) {
                w.writeAttribute("class", titleClazz, null); // $NON-NLS-1$
            }
            String titleMixinStyle = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_HEADING_STYLE), headingStyle);
            if(StringUtil.isNotEmpty(titleMixinStyle)) {
                w.writeAttribute("style", titleMixinStyle, null); // $NON-NLS-1$
            }
            w.writeText(headingText, null);
            w.endElement((String)getProperty(PROP_HEADING_TAG));
        }
        
        if(nodes != null && nodes.size() > 0) {
            //write inner div
            w.startElement("div", c); // $NON-NLS-1$
            String innerClazz = (String)getProperty(PROP_INNER_CLASS);
            if(StringUtil.isNotEmpty(innerClazz)) {
                w.writeAttribute("class", innerClazz, null); // $NON-NLS-1$
            }
            
            //write dash nodes
            writeDashNodes(context, w, c);
            
            //end inner div
            w.endElement("div"); // $NON-NLS-1$
        }
            
        //end container div
        w.endElement("div"); // $NON-NLS-1$
    }
    
    public void writeDashNodes(FacesContext context, ResponseWriter w, UIDashboard c) throws IOException{
        //iterate through dash nodes, set their size and render them
        List<DashNode> nodes = c.getDashNodes();
        if(null != nodes) {
            int size = nodes.size();
            if(size > 0) {
                for(DashNode node : nodes) {
                    writeNode(context, w, c, node, size);
                }
            }
        }
    }
    
    public void writeNode(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node, int nodeCount) throws IOException{
        String containerStyle = node.getStyle();
        String containerStyleClass = node.getStyleClass();
        boolean useGlyph = node.isIconEnabled();
        
        //write node container div
        w.startElement((String)getProperty(PROP_NODE_CONTAINER_TAG), c);
        String clazz = ExtLibUtil.concatStyleClasses(getNodeContainerClass(node, nodeCount), containerStyleClass);

        if(StringUtil.isNotEmpty(clazz)) {
            w.writeAttribute("class", clazz, null); // $NON-NLS-1$
        }
        String containerMixinStyle = ExtLibUtil.concatStyles((String)getProperty(PROP_NODE_CONTAINER_STYLE), containerStyle);
        if(StringUtil.isNotEmpty(containerMixinStyle)) {
            w.writeAttribute("style", containerMixinStyle, null); // $NON-NLS-1$
        }
        
        //Start the enclosing link if a href exists
        String labelLink = node.getLabelHref();
        boolean isLink = StringUtil.isNotEmpty(labelLink);
        boolean isDisplayAsLink = node.isDisplayNodeAsLink();
        
        //Add link tag is href exists and node is set to be wrapped in a link
        if(isDisplayAsLink && isLink) {
            //add anchor tag for title link
            w.startElement("a", c);
            RenderUtil.writeLinkAttribute(context,w, labelLink);
        }
        
        //write the image or the glyphicon instead
        //write image + alt text + width + height
        if(useGlyph) {
            writeGlyphicon(context, w, c, node);
        }else{
            writeNodeImage(context, w, c, node);
        }
        //write title
        writeNodeLabel(context, w, c, node);
        
        //Close link
        if(isDisplayAsLink && isLink) {
            w.endElement("a");
        }
        
        //write description
        writeNodeDescription(context, w, c, node);
        
        //close container
        w.endElement((String)getProperty(PROP_NODE_CONTAINER_TAG));
    }
    
    public void writeNodeLabel(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node) throws IOException{
        String labelText = node.getLabelText();
        
        if(StringUtil.isNotEmpty(labelText)) {
            String labelLink = node.getLabelHref();
            String labelStyle = node.getLabelStyle();
            String labelClass = node.getLabelStyleClass();
            boolean badgeEnabled = node.isBadgeEnabled();
            boolean isLink = StringUtil.isNotEmpty(labelLink);
            boolean isDisplayAsLink = node.isDisplayNodeAsLink();
            
            //Add link tag if href is set and node is not wrapped in a link
            if(!isDisplayAsLink && isLink) {
                //add anchor tag for title link
                w.startElement("a", c);
                RenderUtil.writeLinkAttribute(context,w, labelLink);
            }
            w.startElement((String)getProperty(PROP_NODE_TITLE_TAG), c);
            String titleClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_NODE_TITLE_CLASS), labelClass);
            if(StringUtil.isNotEmpty(titleClazz)) {
                w.writeAttribute("class", titleClazz, null); // $NON-NLS-1$
            }
            String titleMixinStyle = ExtLibUtil.concatStyles((String)getProperty(PROP_NODE_TITLE_STYLE), labelStyle);
            if(StringUtil.isNotEmpty(titleMixinStyle)) {
                w.writeAttribute("style", titleMixinStyle, null); // $NON-NLS-1$
            }
            w.writeText(labelText, null);
            
            if(badgeEnabled){ 
                writeNodeBadge(context, w, c, node);
            }
            
            w.endElement((String)getProperty(PROP_NODE_TITLE_TAG));
            
            if(!isDisplayAsLink && isLink) {
                w.endElement("a");
            }
        }
    }
    
    public void writeNodeBadge(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node) throws IOException{
        String badgeLabel = node.getBadgeLabel();
        String badgeStyle = node.getBadgeStyle();
        String badgeClass = node.getBadgeStyleClass();
        
        w.startElement((String)getProperty(PROP_NODE_DEFAULT_BADGE_TAG), c);
        String badgeClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_NODE_DEFAULT_BADGE_CLASS), badgeClass);
        if(StringUtil.isNotEmpty(badgeClazz)) {
            w.writeAttribute("class", badgeClazz, null); // $NON-NLS-1$
        }
        String badgeMixinStyle = ExtLibUtil.concatStyles((String)getProperty(PROP_NODE_DEFAULT_BADGE_STYLE), badgeStyle);
        if(StringUtil.isNotEmpty(badgeMixinStyle)) {
            w.writeAttribute("style", badgeMixinStyle, null); // $NON-NLS-1$
        }
        if(StringUtil.isNotEmpty(badgeLabel)) {
            w.writeText(badgeLabel, null);
        }
        w.endElement((String)getProperty(PROP_NODE_DEFAULT_BADGE_TAG));
    }
    
    public void writeNodeDescription(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node) throws IOException{
        String descrText = node.getDescription();
        
        if(StringUtil.isNotEmpty(descrText)) {
            String descrStyle = node.getDescriptionStyle();
            String descrClass = node.getDescriptionStyleClass();
            
            w.startElement("span", c); // $NON-NLS-1$
            String descrClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_NODE_DESCRIPTION_STYLECLASS), descrClass);
            if(StringUtil.isNotEmpty(descrClazz)) {
                w.writeAttribute("class", descrClazz, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(descrStyle)) {
                w.writeAttribute("style", descrStyle, null); // $NON-NLS-1$
            }
            w.writeText(descrText, null);
            w.endElement("span"); // $NON-NLS-1$
        }
    }
    
    public void writeGlyphicon(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node) throws IOException{
        String glyphicon = node.getIcon();
        
        if(StringUtil.isNotEmpty(glyphicon)) {
            String glyphiconTag = node.getIconTag();
            String glyphSize = node.getIconSize();
            String glyphStyle = node.getIconStyle();
            String glyphTitle = node.getIconTitle();
            
            String tag = StringUtil.isNotEmpty(glyphiconTag) ? glyphiconTag : (String)getProperty(PROP_NODE_DEFAULT_GLYPH_TAG);
            String size = StringUtil.isNotEmpty(glyphSize) ? (glyphSize.contains("font-size:") ? glyphSize : "font-size:"+glyphSize) : (String)getProperty(PROP_NODE_DEFAULT_GLYPH_SIZE); // $NON-NLS-1$ $NON-NLS-2$
            
            w.startElement(tag, c);
            String glyphClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_NODE_DEFAULT_GLYPH_CLASS), glyphicon);
            if(StringUtil.isNotEmpty(glyphClazz)) {
                w.writeAttribute("class", glyphClazz, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(glyphTitle)) {
                w.writeAttribute("title", glyphTitle, null); // $NON-NLS-1$
            }
            String iconStyle = StringUtil.isNotEmpty(glyphStyle) ? ExtLibUtil.concatStyles(size, glyphStyle) : size;
            
            if(StringUtil.isNotEmpty(iconStyle)) {
                w.writeAttribute("style", iconStyle, null); // $NON-NLS-1$
            }
            w.endElement(tag);
        }
    }
    
    public void writeNodeImage(FacesContext context, ResponseWriter w, UIDashboard c, DashNode node) throws IOException{
        String imageSrc = node.getImageSrc();
        
        if(StringUtil.isNotEmpty(imageSrc)) {
            String imageWidth = node.getImageWidth();
            String imageHeight = node.getImageHeight();
            String imageStyle = node.getImageStyle();
            String imageClass = node.getImageStyleClass();
            String imageAlt = node.getImageAlt();
            
            w.startElement("img", c); // $NON-NLS-1$
            String imageClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_NODE_IMAGE_CLASS), imageClass);
            if(StringUtil.isNotEmpty(imageClazz)) {
                w.writeAttribute("class", imageClazz, null); // $NON-NLS-1$
            }
            String imageMixinStyle = ExtLibUtil.concatStyles((String)getProperty(PROP_NODE_IMAGE_STYLE), imageStyle);
            String setImageWidth = StringUtil.isNotEmpty(imageWidth) ? imageWidth : (String)getProperty(PROP_NODE_IMAGE_WIDTH_DEFAULT);
            if(StringUtil.isNotEmpty(setImageWidth)) {
                imageMixinStyle = ExtLibUtil.concatStyles(imageMixinStyle, "width:" + setImageWidth); // $NON-NLS-1$
            }
            String setImageHeight= StringUtil.isNotEmpty(imageHeight) ? imageHeight : (String)getProperty(PROP_NODE_IMAGE_HEIGHT_DEFAULT);
            if(StringUtil.isNotEmpty(setImageHeight)) {
                imageMixinStyle = ExtLibUtil.concatStyles(imageMixinStyle, "height:" + setImageHeight); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(imageMixinStyle)) {
                w.writeAttribute("style", imageMixinStyle, null); // $NON-NLS-1$
            }
            if(ExtLibRenderUtil.isAltPresent(imageAlt)) {
                w.writeAttribute("alt", imageAlt, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(imageSrc)) {
                w.writeAttribute("src", HtmlRendererUtil.getImageURL(context, imageSrc), null); // $NON-NLS-1$
            }
            w.endElement("img"); // $NON-NLS-1$
        }
    }
    
    /*
     * Generate the CSS classes of the node container div
     */
    protected String getNodeContainerClass(DashNode node, int nodeCount) {
        //Retrieve size properties from component
        int totalSize = (Integer)getProperty(PROP_TOTALCOLUMNS); //There are 12 available columns in bootstrap's grid system
        int largeSize = node.getSizeLarge();
        int mediumSize = node.getSizeMedium();
        int smallSize = node.getSizeSmall();
        int xsmallSize = node.getSizeExtraSmall();
        
        //Declare size variables
        int lgSize = 0;
        int mdSize = 0;
        int smSize = 0;
        int xsSize = 0;
        
        //Check if medium size, the most common size, has been set
        if(mediumSize > 0) {
            mdSize = mediumSize;
        }else{
            //Default medium size
            //Under 7 nodes = total-grid-size/number-of-nodes
            //Except 5 nodes = total-grid-size/6 = 2
            //Over 7 nodes = 1
            mdSize = (nodeCount == 5) ? totalSize/6 :
                     (nodeCount < 7) ? totalSize/nodeCount : 1;
        }
        //If large size has not been set, match it with medium size
        if(largeSize <= 0) {
            lgSize = mdSize;
        }else{
            lgSize = largeSize;
        }
        
        //Set default smSize & xsSize if they are not defined
        switch (mdSize) {
        case 1:
            smSize = (smallSize <= 0)  ? 4 : smallSize;
            xsSize = (xsmallSize <= 0) ? 4 : xsmallSize;
            break;
        case 2:
            smSize = (smallSize <= 0)  ? 4 : smallSize;
            xsSize = (xsmallSize <= 0) ? 6 : xsmallSize;
            break;
        case 3:
            smSize = (smallSize <= 0)  ? 3 : smallSize;
            xsSize = (xsmallSize <= 0) ? 6 : xsmallSize;
            break;
        case 4:
            smSize = (smallSize <= 0)  ? 4 : smallSize;
            xsSize = (xsmallSize <= 0) ? 4 : xsmallSize;
            break;
        case 5:
            smSize = (smallSize <= 0)  ? 4 : smallSize;
            xsSize = (xsmallSize <= 0) ? 6 : xsmallSize;;
            break;
        case 6:
            smSize = (smallSize <= 0)  ? 6  : smallSize;
            xsSize = (xsmallSize <= 0) ? 12 : xsmallSize;
            break;
        default:
            smSize = (smallSize <= 0)  ? 12 : smallSize;
            xsSize = (xsmallSize <= 0) ? 12 : xsmallSize;
            break;
        }
        
        String clazz = "";
        clazz = COLUMN_TINY + xsSize + " " + COLUMN_SMALL + smSize + " " + COLUMN_MEDIUM + mdSize + " " + COLUMN_LARGE + lgSize + " " + getProperty(PROP_NODE_CONTAINER_CLASS);
        return clazz;
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UIDashboard c = (UIDashboard) component;
        if (!c.isRendered()) {
            return;
        }
        writeDashboard(context, w, c);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        //Nothing to do here, all handled in write dashboard
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Forget about the children, the dash children are rendered elsewhere
    }

}