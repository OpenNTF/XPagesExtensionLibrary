package com.ibm.domino.services.util;

import static com.ibm.domino.services.HttpServiceConstants.HEADER_CONTENT_RANGE_ITEMS;

public class ContentUtil {
	
    /**
     * Get header Content-Range header value to indicate how many items are being 
     * returned and how many total items exist:
     * 
     * Content-Range: items 0-24/66
     * 
     * @param start
     * @param last
     * @param count
     * @return String with Content-Range header value (i.e. items 0-24/66).
     */
    public static String getContentRangeHeaderString(int start, int last, int count) {
        StringBuilder hb = new StringBuilder(32);
        hb.append(HEADER_CONTENT_RANGE_ITEMS);
        hb.append(Integer.toString(start));
        hb.append("-");  // $NON-NLS-1$ 
        hb.append(Integer.toString(last));
        hb.append("/");  // $NON-NLS-1$ 
        hb.append(Integer.toString(count));
        
        return hb.toString();
   	}
}
