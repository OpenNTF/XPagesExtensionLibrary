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

package com.ibm.xsp.extlib.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lotus.domino.Database;
import lotus.domino.NotesException;

import com.ibm.commons.util.StringUtil;
import com.ibm.xsp.context.FacesContextEx;


/**
 * Replace ${...} sequence of characters.
 * @author priand
 */
public class StringReplacer {
    
    public static final String EXPR_START   = "${"; //$NON-NLS-1$
    public static final String EXPR_END     = "}"; //$NON-NLS-1$
    
    private Pattern pattern;

    public StringReplacer(){
        this.pattern = Pattern.compile(Pattern.quote(getExprStart())+"(.*?)"+Pattern.quote(getExprEnd())); //$NON-NLS-1$
    }
    
    protected String getExprStart() {
        return EXPR_START;
    }
    
    protected String getExprEnd() {
        return EXPR_END;
    }
    
    protected String getReplacement(String s) {
        FacesContextEx ctx = FacesContextEx.getCurrentInstance();
        if(ctx!=null) {
            // Try some contextual replacement
            if(StringUtil.equals(s, "database")) { //$NON-NLS-1$
                Database db = ExtLibUtil.getCurrentDatabase(ctx);
                if(db!=null) {
                    try {
                        return db.getFileName();
                    } catch(NotesException ex) {
                        // Ignore and return empty
                    }
                }
                return null;
            }
            if(StringUtil.equals(s, "databasePath")) { //$NON-NLS-1$
                Database db = ExtLibUtil.getCurrentDatabase(ctx);
                if(db!=null) {
                    try {
                        return db.getFilePath();
                    } catch(NotesException ex) {
                        // Ignore and return empty
                    }
                }
                return null;
            }
            // Then JSF properties
//            if(ctx!=null) {
                String rep = ctx.getProperty(s);
                if(rep!=null) {
                    return rep;
                }
//            }
        }
        
        // And finally System properties
        String rep = System.getProperty(s);
        return rep;
    }
    
    public String replace(CharSequence s){
        if(s!=null) {
            Matcher matcher = pattern.matcher(s);
            StringBuilder b = null; int ptr = 0;
            while(ptr<s.length() && matcher.find(ptr)){
                if(b==null) {
                    b = new StringBuilder();
                }
                b.append(s.subSequence(ptr, matcher.start()));
                String rep = getReplacement(matcher.group(1));
                b.append(rep!=null ? rep : matcher.group());
                ptr = matcher.end();
            }
            return b!=null ? b.append(s.subSequence(ptr, s.length())).toString() : s.toString();
        }
        return null;
    }
    
    public static void main(String[] args){
        System.out.println(new StringReplacer().replace("The java dir is ${java.home}, and the compiler is ${java.compiler}.")); // $NON-NLS-1$
    }
}