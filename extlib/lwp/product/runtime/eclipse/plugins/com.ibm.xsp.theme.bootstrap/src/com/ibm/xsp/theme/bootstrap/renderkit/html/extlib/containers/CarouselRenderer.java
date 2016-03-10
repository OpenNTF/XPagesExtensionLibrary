/*
 * © Copyright IBM Corp. 2014, 2015
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
* Date: 21 Dec 2014
* UICarousel.java
*/
package com.ibm.xsp.theme.bootstrap.renderkit.html.extlib.containers;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.extlib.renderkit.html_extended.FacesRendererEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.renderkit.html_basic.HtmlRendererUtil;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.theme.bootstrap.components.responsive.UICarousel;
import com.ibm.xsp.theme.bootstrap.components.responsive.SlideNode;
import com.ibm.xsp.util.HtmlUtil;
import com.ibm.xsp.util.JSUtil;

/**
 *
 * @author Brian Gleeson (brian.gleeson@ie.ibm.com)
 */
public class CarouselRenderer extends FacesRendererEx {
    
    // Container
    protected static final int PROP_CONTAINER_CLASS                 = 1;
    protected static final int PROP_CONTAINER_STYLE                 = 2;
    protected static final int PROP_INNER_CLASS                     = 3;
    protected static final int PROP_CONTAINER_HEIGHT_LARGE          = 4;
    protected static final int PROP_CONTAINER_HEIGHT_MEDIUM         = 5;
    protected static final int PROP_CONTAINER_HEIGHT_SMALL          = 6;
    protected static final int PROP_CONTAINER_HEIGHT_EXTRASMALL     = 7;
    // Indicators 
    protected static final int PROP_INDICATOR_CLASS                 = 10;
    // Controls 
    protected static final int PROP_CONTROLS_LEFT_CLASS             = 11;
    protected static final int PROP_CONTROLS_RIGHT_CLASS            = 12;
    protected static final int PROP_CONTROLS_STYLE                  = 13;
    protected static final int PROP_CONTROLS_GLYPH_RIGHT            = 14;
    protected static final int PROP_CONTROLS_GLYPH_LEFT             = 15;
    // Slides
    protected static final int PROP_SLIDE_CONTAINER_TAG             = 20;
    protected static final int PROP_SLIDE_CONTAINER_CLASS           = 21;
    protected static final int PROP_SLIDE_CONTAINER_STYLE           = 22;
    protected static final int PROP_SLIDE_HEADING_TAG               = 23;
    protected static final int PROP_SLIDE_HEADING_STYLE             = 24;
    protected static final int PROP_SLIDE_HEADING_CLASS             = 25;
    protected static final int PROP_SLIDE_BGIMAGE_SRC               = 26;
    protected static final int PROP_SLIDE_BGIMAGE_STYLE             = 27;
    protected static final int PROP_SLIDE_BGIMAGE_CLASS             = 28;
    protected static final int PROP_SLIDE_BGCOLOR                   = 29;
    protected static final int PROP_SLIDE_DESCR_TAG                 = 30;
    protected static final int PROP_SLIDE_DESCR_STYLE               = 31;
    protected static final int PROP_SLIDE_DESCR_CLASS               = 32;
    protected static final int PROP_SLIDE_CAPTION_TAG               = 33;
    protected static final int PROP_SLIDE_CAPTION_STYLE             = 34;
    protected static final int PROP_SLIDE_CAPTION_CLASS             = 35;
    protected static final int PROP_SLIDE_CAPTION_CONTAINER_CLASS   = 36;
    protected static final int PROP_SLIDE_BTN_TAG                   = 37;
    protected static final int PROP_SLIDE_BTN_ROLE                  = 38;
    protected static final int PROP_SLIDE_BTN_CONTAINER_TAG         = 39;
    protected static final int PROP_SLIDE_BTN_CONTAINER_CLASS       = 40;
    protected static final int PROP_SLIDE_BTN_STYLE                 = 41;
    protected static final int PROP_SLIDE_BTN_CLASS                 = 42;
    // Event params
    protected static final int PROP_SLIDE_INTERVAL                  = 50;
    
    @Override
    protected Object getProperty(int prop) {
        switch(prop) {
            // Container div
            case PROP_CONTAINER_CLASS:                return "carousel slide xspCarousel"; //$NON-NLS-1$
            case PROP_CONTAINER_STYLE:                return ""; //$NON-NLS-1$
            case PROP_INNER_CLASS:                    return "carousel-inner"; //$NON-NLS-1$
            case PROP_CONTAINER_HEIGHT_LARGE:         return "500px;"; //$NON-NLS-1$
            case PROP_CONTAINER_HEIGHT_MEDIUM:        return "400px;"; //$NON-NLS-1$
            case PROP_CONTAINER_HEIGHT_SMALL:         return "300px;"; //$NON-NLS-1$
            case PROP_CONTAINER_HEIGHT_EXTRASMALL:    return "300px;"; //$NON-NLS-1$
            // Indicators
            case PROP_INDICATOR_CLASS:                return "carousel-indicators"; //$NON-NLS-1$
            // Controls
            case PROP_CONTROLS_LEFT_CLASS:            return "carousel-control left prev-slide"; //$NON-NLS-1$
            case PROP_CONTROLS_RIGHT_CLASS:           return "carousel-control right next-slide"; //$NON-NLS-1$
            case PROP_CONTROLS_STYLE:                 return ""; //$NON-NLS-1$
            case PROP_CONTROLS_GLYPH_LEFT:            return "glyphicon-chevron-left"; //$NON-NLS-1$
            case PROP_CONTROLS_GLYPH_RIGHT:           return "glyphicon-chevron-right"; //$NON-NLS-1$
            // Slides
            case PROP_SLIDE_CONTAINER_TAG:            return "div"; //$NON-NLS-1$
            case PROP_SLIDE_CONTAINER_CLASS:          return "item xspCarouselItem"; //$NON-NLS-1$
            case PROP_SLIDE_CONTAINER_STYLE:          return ""; //$NON-NLS-1$
            // Slide Heading
            case PROP_SLIDE_HEADING_TAG:              return "h1"; // $NON-NLS-1$
            case PROP_SLIDE_HEADING_STYLE:            return ""; //$NON-NLS-1$
            case PROP_SLIDE_HEADING_CLASS:            return ""; //$NON-NLS-1$
            // Slide Background
            case PROP_SLIDE_BGIMAGE_SRC:              return ""; // $NON-NLS-1$
            case PROP_SLIDE_BGIMAGE_STYLE:            return "width:100%"; //$NON-NLS-1$
            case PROP_SLIDE_BGIMAGE_CLASS:            return ""; //$NON-NLS-1$
            case PROP_SLIDE_BGCOLOR:                  return "background-color: #cccccc"; //$NON-NLS-1$
            // Slide Description
            case PROP_SLIDE_DESCR_TAG:                return "div"; //$NON-NLS-1$
            case PROP_SLIDE_DESCR_STYLE:              return ""; //$NON-NLS-1$
            case PROP_SLIDE_DESCR_CLASS:              return "slideDescr"; //$NON-NLS-1$
            // Slide Caption
            case PROP_SLIDE_CAPTION_TAG:              return "h2"; // $NON-NLS-1$
            case PROP_SLIDE_CAPTION_STYLE:            return ""; //$NON-NLS-1$
            case PROP_SLIDE_CAPTION_CLASS:            return ""; //$NON-NLS-1$
            case PROP_SLIDE_CAPTION_CONTAINER_CLASS:  return "carousel-caption"; //$NON-NLS-1$
            // Slide Heading
            case PROP_SLIDE_BTN_TAG:                  return "a"; // $NON-NLS-1$
            case PROP_SLIDE_BTN_CONTAINER_TAG:        return "div"; // $NON-NLS-1$
            case PROP_SLIDE_BTN_CONTAINER_CLASS:      return "slideBtnContainer"; // $NON-NLS-1$
            case PROP_SLIDE_BTN_ROLE:                 return "button"; // $NON-NLS-1$
            case PROP_SLIDE_BTN_STYLE:                return ""; //$NON-NLS-1$
            case PROP_SLIDE_BTN_CLASS:                return "btn btn-default"; //$NON-NLS-1$
            // Carousel event params
            case PROP_SLIDE_INTERVAL:                 return 5000; //$NON-NLS-1$
        }
        return super.getProperty(prop);
    }

    public void writeCarousel(FacesContext context, ResponseWriter w, UICarousel c) throws IOException{
        String containerStyle      = "";
        String containerStyleClass = "";
        String carouselTitle       = "";
        String carouselHeightLG    = "height:" + (String)getProperty(PROP_CONTAINER_HEIGHT_LARGE); // $NON-NLS-1$
        String carouselHeightMD    = "height:" + (String)getProperty(PROP_CONTAINER_HEIGHT_MEDIUM); // $NON-NLS-1$
        String carouselHeightSM    = "height:" + (String)getProperty(PROP_CONTAINER_HEIGHT_SMALL); // $NON-NLS-1$
        String carouselHeightXS    = "height:" + (String)getProperty(PROP_CONTAINER_HEIGHT_EXTRASMALL); // $NON-NLS-1$
        
        if(c!=null) {
            containerStyle      = c.getStyle();
            containerStyleClass = c.getStyleClass();
            carouselTitle       = c.getTitle();
            carouselHeightLG    = StringUtil.isNotEmpty(c.getHeightLarge()) ?  "height:" + c.getHeightLarge() : carouselHeightLG; // $NON-NLS-1$
            carouselHeightMD    = StringUtil.isNotEmpty(c.getHeightMedium()) ?  "height:" + c.getHeightMedium() : carouselHeightMD; // $NON-NLS-1$
            carouselHeightSM    = StringUtil.isNotEmpty(c.getHeightSmall()) ?  "height:" + c.getHeightSmall() : carouselHeightSM; // $NON-NLS-1$
            carouselHeightXS    = StringUtil.isNotEmpty(c.getHeightExtraSmall()) ?  "height:" + c.getHeightExtraSmall() : carouselHeightXS; // $NON-NLS-1$
        }
        //Get the list of slides
        List<SlideNode> slides = c.getSlideNodes();
        
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
        if(StringUtil.isNotEmpty(carouselTitle)) {
            w.writeText(carouselTitle, null);
        }else{
            w.writeAttribute("title", "Carousel", null); // $NON-NLS-1$ $NLS-CarouselRenderer.carousel-2$
        }
        String role = "presentation"; // $NON-NLS-1$
        w.writeAttribute("role", role, null); // $NON-NLS-1$
        
        // Write CSS styles
        writeCssStyles(context, w, c, carouselHeightLG, carouselHeightMD, carouselHeightSM, carouselHeightXS);
        // Write Javscript methods
        writeJSScript(context, w, c);
        
        int slideCount = (slides != null ? slides.size() : 0);
        if(slideCount > 0) {
            writeCarouselIndicators(context, w, c, slideCount);
            
            //write inner div
            w.startElement("div", c); // $NON-NLS-1$
            String innerClazz = (String)getProperty(PROP_INNER_CLASS);
            if(StringUtil.isNotEmpty(innerClazz)) {
                w.writeAttribute("class", innerClazz, null); // $NON-NLS-1$
            }
            
            //write slides
            int size = slides.size();
            for(int i = 0; i < size; i++) {
                boolean isFirst = (i == 0);
                SlideNode slide = slides.get(i);
                if(slide != null) {
                    writeSlide(context, w, c, slide, isFirst);
                }
            }
            
            //end inner div
            w.endElement("div"); // $NON-NLS-1$
            
            //write the controls for the carousel
            writeCarouselControls(context, w, c, slideCount);
        }
        
        //end container div
        w.endElement("div"); // $NON-NLS-1$
    }
    
    public void writeCssStyles(FacesContext context, ResponseWriter w, UICarousel c, String heightLarge, String heightMedium, String heightSmall, String heightExtraSmall) throws IOException {
        //CSS required for responsiveness
        w.startElement("style", c); // $NON-NLS-1$
        w.writeAttribute("type", "text/css", null); // $NON-NLS-1$ $NON-NLS-2$
        w.writeText(".carousel.xspCarousel {"+ heightLarge + "}", null); // $NON-NLS-1$
        w.writeText(".carousel .xspCarouselItem {"+ heightLarge + "}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 1200px) {.carousel.xspCarousel{"+ heightMedium + "}}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 1200px) {.xspCarousel .xspCarouselItem{"+ heightMedium + "}}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 992px) {.carousel.xspCarousel{"+ heightSmall + "}}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 992px) {.xspCarousel .xspCarouselItem{"+ heightSmall + "}}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 768px) {.carousel.xspCarousel{"+ heightExtraSmall + "}}", null); // $NON-NLS-1$
        w.writeText("@media (max-width: 768px) {.xspCarousel .xspCarouselItem{"+ heightExtraSmall + "}}", null); // $NON-NLS-1$
        w.endElement("style"); // $NON-NLS-1$
        newLine(w);
    }
    
    public void writeJSScript(FacesContext context, ResponseWriter w, UICarousel c) throws IOException {
        // Write the javascript method that starts the carousel
        
        // Get carousel id
        String userId  = c.getId();
        boolean isId   = HtmlUtil.isUserId(userId);
        
        if(isId) {
            // Write carousel activation method "carousel.carousel()" with given parameters
            String clientId = c.getClientId(context);

            // Get the carousel parameters
            // Get isAutoCycle property
            boolean isAuto = c.isAutoCycle();
            // Get slide interval value in milliseconds
            int interval    = c.getSlideInterval();
            String slideInt    = interval == 0 ? "" : Integer.toString(interval); //"" + (interval != 0 ? interval : (java.lang.Integer)getProperty(PROP_SLIDE_INTERVAL));
            // Get whether carousel is wrapped, paused on hover
            boolean wrapped = c.isWrapped();
            String pause = c.getPause();
            String isPause   = StringUtil.isEmpty(pause) ? "" : 
                               (StringUtil.equals(pause, "hover")  || // $NON-NLS-1$
                                StringUtil.equals(pause, "true")  // $NON-NLS-1$
                                   ? "hover"  // $NON-NLS-1$
                                   : pause);
            
            // write the script element
            w.startElement("script", c); // $NON-NLS-1$
            w.writeAttribute(TYPE, "text/javascript", null); // $NON-NLS-1$
            
            // Build the carousel function
            StringBuilder b = new StringBuilder();
            // Find the carousel element
            b.append("$(document).ready(function(){var carousel = x$("); // $NON-NLS-1$
            JSUtil.addSingleQuoteString(b, clientId);
            b.append(");");
            // Call the carousel method from bootstrap.js
            b.append("carousel.carousel({"); // $NON-NLS-1$
            if(isAuto) {
                if(StringUtil.isNotEmpty(slideInt)) {
                    b.append("interval:"); // $NON-NLS-1$
                    JSUtil.addString(b, slideInt);
                    b.append(",");
                }
            }else{
                b.append("interval:"); // $NON-NLS-1$
                JSUtil.addBoolean(b, false);
                b.append(",");
            }
            if(!wrapped){
                b.append("wrap:"); // $NON-NLS-1$
                JSUtil.addBoolean(b, wrapped);
                b.append(",");
            }
            if(StringUtil.isNotEmpty(isPause)) {
                b.append("pause:"); // $NON-NLS-1$
                JSUtil.addSingleQuoteString(b, isPause);
            }
            b.append("});});");
            w.write(b.toString());
            w.endElement("script"); // $NON-NLS-1$
        }
    }
    
    public void writeSlide(FacesContext context, ResponseWriter w, UICarousel c, SlideNode slide, boolean isFirst) throws IOException{
        String slideTag = (String)getProperty(PROP_SLIDE_CONTAINER_TAG);
        
        if(StringUtil.isNotEmpty(slideTag)) {
            String slideClass   = slide.getStyleClass();
            String slideStyle   = slide.getStyle();
            String bgColor      = slide.getBackgroundColor();
            String bgSrc        = slide.getBackgroundSrc();
            
            // start the slide div
            w.startElement(slideTag, c);
            String slideClassMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_CONTAINER_CLASS), slideClass);
            if(isFirst) {
                slideClassMixin = ExtLibUtil.concatStyleClasses(slideClassMixin, "active"); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(slideClassMixin)) {
                w.writeAttribute("class", slideClassMixin, null); // $NON-NLS-1$
            }
            String slideStyleMixin = "";
            if(StringUtil.isEmpty(bgColor)) {
                slideStyleMixin = ExtLibUtil.concatStyles((String)getProperty(PROP_SLIDE_BGCOLOR), (String)getProperty(PROP_SLIDE_CONTAINER_STYLE));
            }else{
                slideStyleMixin = ExtLibUtil.concatStyles((String)getProperty(PROP_SLIDE_CONTAINER_STYLE), "background-color:" + bgColor); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(bgSrc)) {
                String bgSrcHref = HtmlRendererUtil.getImageURL(context,bgSrc);
                slideStyleMixin = ExtLibUtil.concatStyles(slideStyleMixin, "background-image:url('" + bgSrcHref + "');background-size:cover;background-position:center center"); // $NON-NLS-1$ $NON-NLS-2$
            }
            if(StringUtil.isNotEmpty(slideStyle)) {
                slideStyleMixin = ExtLibUtil.concatStyles(slideStyleMixin, slideStyle);
            }
            if(StringUtil.isNotEmpty(slideStyleMixin)) {
                w.writeAttribute("style", slideStyleMixin, null); // $NON-NLS-1$
            }
            
            // write the slide heading
            writeSlideCaption(context, w, c, slide);
            
            // end the slide div
            w.endElement(slideTag);
        }
    }
    
    public void writeCarouselIndicators(FacesContext context, ResponseWriter w, UICarousel c, int slideCount) throws IOException{
        if(slideCount > 1) {
            String indicatorClass = c.getIndicatorStyleClass();
            String indicatorStyle = c.getIndicatorStyle();
        
            w.startElement("ol", c); // $NON-NLS-1$
            String indicatorClazz = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_INDICATOR_CLASS), indicatorClass);
            if(StringUtil.isNotEmpty(indicatorClazz)) {
                w.writeAttribute("class", indicatorClazz, null); // $NON-NLS-1$
            }
            String indicatorMixinStyle = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_CONTAINER_STYLE), indicatorStyle);
            if(StringUtil.isNotEmpty(indicatorMixinStyle)) {
                w.writeAttribute("style", indicatorMixinStyle, null); // $NON-NLS-1$
            }
            
            for(int i = 0; i < slideCount; i++) {
                w.startElement("li", c); // $NON-NLS-1$
                w.writeAttribute("data-target", "div[id$=" + c.getId() + "]", null); // $NON-NLS-1$ $NON-NLS-2$
                w.writeAttribute("data-slide-to", i, null); // $NON-NLS-1$
                if(i == 0) {
                    w.writeAttribute("class", "active", null); // $NON-NLS-1$ $NON-NLS-2$
                }
                w.endElement("li"); // $NON-NLS-1$
            }
            //end the ol
            w.endElement("ol"); // $NON-NLS-1$
        }
    }
    
    public void writeCarouselControls(FacesContext context, ResponseWriter w, UICarousel c, int slideCount) throws IOException{
        if(slideCount > 1) {
            // write left arrow
            w.startElement("a", c);
            String leftControlClass = (String)getProperty(PROP_CONTROLS_LEFT_CLASS);
            if(StringUtil.isNotEmpty(leftControlClass)) {
                w.writeAttribute("class", leftControlClass, null); // $NON-NLS-1$
            }
            String controlsStyle = (String)getProperty(PROP_CONTROLS_STYLE);
            if(StringUtil.isNotEmpty(controlsStyle)) {
                w.writeAttribute("style", controlsStyle, null); // $NON-NLS-1$
            }
            w.writeAttribute("href", "div[id$=" + c.getId() + "]", null); // $NON-NLS-2$ $NON-NLS-1$
            w.writeAttribute("data-slide", "prev", null); // $NON-NLS-1$ $NON-NLS-2$
            // write glyphicon
            w.startElement("span", c); // $NON-NLS-1$
            String leftGlyph = ExtLibUtil.concatStyleClasses("glyphicon", (String)getProperty(PROP_CONTROLS_GLYPH_LEFT)); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(leftGlyph)) {
                w.writeAttribute("class", leftGlyph, null); // $NON-NLS-1$
            }
            //end glyphicon span
            w.endElement("span"); // $NON-NLS-1$
            // end left arrow
            w.endElement("a");
            
            //write right arrow
            w.startElement("a", c);
            String rightControlClass = (String)getProperty(PROP_CONTROLS_RIGHT_CLASS);
            if(StringUtil.isNotEmpty(rightControlClass)) {
                w.writeAttribute("class", rightControlClass, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(controlsStyle)) {
                w.writeAttribute("style", controlsStyle, null); // $NON-NLS-1$
            }
            w.writeAttribute("href", "div[id$=" + c.getId() + "]", null); // $NON-NLS-1$ $NON-NLS-2$
            w.writeAttribute("data-slide", "next", null); // $NON-NLS-1$ $NON-NLS-2$
            // write glyphicon
            w.startElement("span", c); // $NON-NLS-1$
            String rightGlyph = ExtLibUtil.concatStyleClasses("glyphicon", (String)getProperty(PROP_CONTROLS_GLYPH_RIGHT)); // $NON-NLS-1$
            if(StringUtil.isNotEmpty(rightGlyph)) {
                w.writeAttribute("class", rightGlyph, null); // $NON-NLS-1$
            }
            //end glyphicon span
            w.endElement("span"); // $NON-NLS-1$
            // end right arrow
            w.endElement("a");
        }
    }
    
    public void writeSlideHeading(FacesContext context, ResponseWriter w, UICarousel c, SlideNode slide, String headingText) throws IOException {
        if(StringUtil.isNotEmpty(headingText)) {
            String headingStyle  = slide.getHeadingStyle();
            String headingClass  = slide.getHeadingStyleClass();
            String headingTag    = slide.getHeadingTag();
            
            String tag = StringUtil.isNotEmpty(headingTag) ? headingTag : (String)getProperty(PROP_SLIDE_HEADING_TAG);
            w.startElement(tag, c);
            String classMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_HEADING_CLASS), headingClass);
            if(StringUtil.isNotEmpty(classMixin)) {
                w.writeAttribute("class", classMixin, null); // $NON-NLS-1$
            }
            String styleMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_HEADING_STYLE), headingStyle);
            if(StringUtil.isNotEmpty(styleMixin)) {
                w.writeAttribute("style", styleMixin, null); // $NON-NLS-1$
            }
            
            //write the heading text
            w.writeText(headingText, null);
            
            // end img tag
            w.endElement(tag);
        }
    }
    
    public void writeSlideCaption(FacesContext context, ResponseWriter w, UICarousel c, SlideNode slide) throws IOException {
        String captionText       = slide.getCaptionText();
        String descriptionText   = slide.getDescriptionText();
        String headingText       = slide.getHeadingText();
        String buttonLabel       = slide.getButtonLabel();
        
        if( StringUtil.isNotEmpty(captionText)     || StringUtil.isNotEmpty(headingText) ||
            StringUtil.isNotEmpty(descriptionText) || StringUtil.isNotEmpty(buttonLabel) ) {
            
            String captionStyle  = slide.getCaptionStyle();
            String captionClass  = slide.getCaptionStyleClass();
            String captionTag    = slide.getCaptionTag();
            
            // start caption container
            w.startElement("div", c); // $NON-NLS-1$
            String containerClass = (String)getProperty(PROP_SLIDE_CAPTION_CONTAINER_CLASS);
            if(StringUtil.isNotEmpty(containerClass)) {
                w.writeAttribute("class", containerClass, null); // $NON-NLS-1$
            }

            // write the slide heading
            writeSlideHeading(context, w, c, slide, headingText);
            
            // write caption text
            if(StringUtil.isNotEmpty(captionText)){
                String tag = StringUtil.isNotEmpty(captionTag) ? captionTag : (String)getProperty(PROP_SLIDE_CAPTION_TAG);
                w.startElement(tag, c);
                String classMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_CAPTION_CLASS), captionClass);
                if(StringUtil.isNotEmpty(classMixin)) {
                    w.writeAttribute("class", classMixin, null); // $NON-NLS-1$
                }
                String styleMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_CAPTION_STYLE), captionStyle);
                if(StringUtil.isNotEmpty(styleMixin)) {
                    w.writeAttribute("style", styleMixin, null); // $NON-NLS-1$
                }
            
                w.writeText(captionText, null);
                
                // end caption tag
                w.endElement(tag);
            }
            
            // write slide description
            writeSlideDescription(context, w, c, slide, descriptionText);

            // write slide button
            writeSlideButton(context, w, c, slide, buttonLabel);
            
            // end caption container
            w.endElement("div"); // $NON-NLS-1$
        }
    }
    
    public void writeSlideDescription(FacesContext context, ResponseWriter w, UICarousel c, SlideNode slide, String descrText) throws IOException {
        if(StringUtil.isNotEmpty(descrText)) {
            String descrStyle  = slide.getDescriptionStyle();
            String descrClass  = slide.getDescriptionStyleClass();
            
            String tag = (String)getProperty(PROP_SLIDE_DESCR_TAG);
            w.startElement(tag, c);
            String classMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_DESCR_CLASS), descrClass);
            if(StringUtil.isNotEmpty(classMixin)) {
                w.writeAttribute("class", classMixin, null); // $NON-NLS-1$
            }
            String styleMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_DESCR_STYLE), descrStyle);
            if(StringUtil.isNotEmpty(styleMixin)) {
                w.writeAttribute("style", styleMixin, null); // $NON-NLS-1$
            }
            
            // write description text
            w.writeText(descrText, null);
            
            // end description tag
            w.endElement(tag);
        }
    }
    
    public void writeSlideButton(FacesContext context, ResponseWriter w, UICarousel c, SlideNode slide, String buttonText) throws IOException {
        if(StringUtil.isNotEmpty(buttonText)) {
            String buttonHref  = slide.getButtonHref();
            String buttonStyle  = slide.getButtonStyle();
            String buttonClass  = slide.getButtonStyleClass();

            // write button container
            String contTag = (String)getProperty(PROP_SLIDE_BTN_CONTAINER_TAG);
            w.startElement(contTag, c);
            w.writeAttribute("class", (String)getProperty(PROP_SLIDE_BTN_CONTAINER_CLASS), null); // $NON-NLS-1$
            
            String tag = (String)getProperty(PROP_SLIDE_BTN_TAG);
            w.startElement(tag, c);

            String role = (String)getProperty(PROP_SLIDE_BTN_ROLE);
            w.writeAttribute("role", role, null); // $NON-NLS-1$
            
            String classMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_BTN_CLASS), buttonClass);
            if(StringUtil.isNotEmpty(classMixin)) {
                w.writeAttribute("class", classMixin, null); // $NON-NLS-1$
            }
            String styleMixin = ExtLibUtil.concatStyleClasses((String)getProperty(PROP_SLIDE_BTN_STYLE), buttonStyle);
            if(StringUtil.isNotEmpty(styleMixin)) {
                w.writeAttribute("style", styleMixin, null); // $NON-NLS-1$
            }
            if(StringUtil.isNotEmpty(buttonHref)) {
                RenderUtil.writeLinkAttribute(context,w,buttonHref);
            }
            
            // write button text
            w.writeText(buttonText, null);
            
            // end button tag
            w.endElement(tag);
            // end button container
            w.endElement(contTag);
        }
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        // Nothing to decode here...
    }
    
    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter w = context.getResponseWriter();
        UICarousel c = (UICarousel) component;
        if (!c.isRendered()) {
            return;
        }
        
        writeCarousel(context, w, c);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        // Nothing else to do here
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Forget about the children, the slides are rendered elsewhere
    }
}