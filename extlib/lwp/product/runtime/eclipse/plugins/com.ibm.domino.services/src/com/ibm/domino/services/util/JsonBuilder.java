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

package com.ibm.domino.services.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;

import lotus.domino.DateTime;
import lotus.domino.NotesException;

import com.ibm.commons.util.AbstractIOException;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.domino.services.rest.RestServiceConstants;



/**
 * Specialized JSON writer within a StringBuilder.
 * 
 * @author Philippe Riand
 */
public class JsonBuilder extends JsonGenerator.StringBuilderGenerator {
	
	private int objectLevels = 0;
	private boolean first[] = new boolean[32]; // max 32 for now...
	
	public JsonBuilder(StringBuilder b, boolean compact) {
		super(null,b,compact);
	}

	public void startObject() throws IOException {
		nl();
        indent();
        out('{');
        first[++objectLevels]=true;
		incIndent();
	}
	public void endObject() throws IOException {
		nl();
		decIndent();
        indent();
        out('}');
        first[--objectLevels]=false;
	}
	public void startArray() throws IOException {
		nl();
        indent();
        out('[');
        first[++objectLevels]=true;
		incIndent();
	}
	public void endArray() throws IOException {
		nl();
		decIndent();
        indent();
        out(']');
        first[--objectLevels]=false;
	}
	public void startArrayItem() throws IOException {
		if(!first[objectLevels]) {
            out(',');
		}
	}
	public void endArrayItem() throws IOException {
        first[objectLevels]=false;
	}
	public void startProperty(String propertyName) throws IOException {
		if(!first[objectLevels]) {
            out(',');
		} else {
			first[objectLevels]=false;
		}
		nl();
        incIndent();
        indent();
        outPropertyName(propertyName);
        out(':');
	}
	public void endProperty() throws IOException {
		decIndent();
	}

	// Should be moved to the core libs
    @Override
	public void outNumberLiteral(double d) throws IOException {
    	long l = (long)d;
    	if((double)l==d) {
    		String s = Long.toString(l);
            out(s);
    	} else {
    		String s = Double.toString(d);
            out(s);
    	}
    }
	
	// Should be moved to the core libs
	public void outDateLiteral(Date value) throws IOException {
        String s = dateToString(value);
        outStringLiteral(s);
    }

	public void outDateLiteral(DateTime value) throws IOException {
        String s = dateToString(value);
        out(s);
    }

	public void outDominoValue(Object value) throws IOException {
		try {
			if(value==null) {
				outNull();
				return;
			}
			if(value instanceof String) {
				outStringLiteral((String)value);
				return;
			}
			if(value instanceof Number) {
				outNumberLiteral(((Number)value).doubleValue());
				return;
			}
			if(value instanceof Boolean) {
				outBooleanLiteral(((Boolean)value).booleanValue());
				return;
			}
			if(value instanceof Date) {
				outStringLiteral(dateToString((Date)value));
				return;
			}
			if(value instanceof DateTime) {
				try {
					outStringLiteral(dateToString(((DateTime)value).toJavaDate()));
					return;
				} catch(NotesException ex) {
					throw new AbstractIOException(ex,"");
				}
			}			
			if (value instanceof Collection) {
				startArray();
				Collection v = (Collection) value;
				for (Object raw : v) {
					startArrayItem();
					outDominoValue(raw);
					endArrayItem();
				}
				endArray();
				return;
			}
			// Should not happen...
			outStringLiteral("???");
		} catch(JsonException ex) {
			throw new AbstractIOException(ex,"");
		}
	}	

    
	//TODO: What the TZ should be??
	private static SimpleDateFormat ISO8601 = new SimpleDateFormat(RestServiceConstants.TIME_FORMAT_B); //$NON-NLS-1$
	
    
	public String dateToString(Date value) throws IOException {
		return ISO8601.format(value);
    }
    
    public String dateToString(DateTime value) throws IOException {
		try {
			return ISO8601.format(value.toJavaDate());
		} catch(NotesException ex) {
			throw new AbstractIOException(ex,"");
		}
    }
    
    public Date toJavaDate(DateTime value) throws IOException {
		try {
			return value.toJavaDate();
		} catch(NotesException ex) {
			throw new AbstractIOException(ex,"");
		}
    }
}	
