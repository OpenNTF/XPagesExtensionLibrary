/*
 * © Copyright IBM Corp. 2010
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
package com.ibm.xsp.extlib.javascript;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Session;

import com.ibm.commons.Platform;
import com.ibm.commons.util.StringUtil;
import com.ibm.jscript.InterpretException;
import com.ibm.jscript.JSContext;
import com.ibm.jscript.JavaScriptException;
import com.ibm.jscript.engine.IExecutionContext;
import com.ibm.jscript.types.BuiltinFunction;
import com.ibm.jscript.types.FBSDefaultObject;
import com.ibm.jscript.types.FBSGlobalObject;
import com.ibm.jscript.types.FBSNull;
import com.ibm.jscript.types.FBSObject;
import com.ibm.jscript.types.FBSUndefined;
import com.ibm.jscript.types.FBSUtility;
import com.ibm.jscript.types.FBSValue;
import com.ibm.jscript.types.FBSValueVector;
import com.ibm.xsp.FacesExceptionEx;
import com.ibm.xsp.component.UIViewRootEx;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DatabaseConstants;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import com.ibm.xsp.renderkit.html_extended.RenderUtil;
import com.ibm.xsp.util.FacesUtil;
import com.ibm.xsp.webapp.FacesResourceServlet;

/**
 * Extended Notes/Domino formula language.
 * <p>
 * This class implements a set of new functions available to the JavaScript interpreter. They become available to Domino
 * Designer in the category "@NotesFunctionEx".
 * </p>
 */
public class NotesFunctionsEx extends FBSDefaultObject {

    // Functions IDs
    private static final int FCT_TOPPARENTID = 1;
    private static final int FCT_TOPPARENTUNID = 2;

    private static final int FCT_FULLURL = 3;
    private static final int FCT_ABSOLUTEURL = 4;
    private static final int FCT_ENCODEURL = 5;
    private static final int FCT_ISABSOLUTEURL = 6;

    private static final int FCT_ERRORMESSAGE = 7;
    private static final int FCT_WARNINGMESSAGE = 8;
    private static final int FCT_INFORMATIONMESSAGE = 9;

    private static final int FCT_VIEWICONURL = 10;

    private static final int FCT_NORMALIZESUBJECT = 11;

    public static final String  FULLURL_DEFAULT_FORMAT = "DEFAULT_URL_FORMAT"; // $NON-NLS-1$
    public static final String  FULLURL_NOTES_FORMAT   = "NOTES_URL_FORMAT"; // $NON-NLS-1$
    
    // ============================= CODE COMPLETION ==========================
    //
    // Even though JavaScript is an untyped language, the XPages JavaScript
    // interpreter can make use of symbolic information defining the
    // objects/functions exposed. This is particularly used by Domino Designer
    // to provide the code completion facility and help the user writing code.
    //
    // Each function expose by a library can then have one or multiple
    // "prototypes", defining its parameters and the returned value type. To
    // make this definition as efficient as possible, the parameter definition
    // is compacted within a string, where all the parameters are defined
    // within parenthesis followed by the returned value type.
    // A parameter is defined by its name, followed by a colon and its type.
    // Generally, the type is defined by a single character (see bellow) or a
    // full Java class name. The returned type is defined right after the
    // closing parameter parenthesis.
    //
    // Here is, for example, the definition of the "@Date" function which can
    // take 3 different set of parameters:
    // "(time:Y):Y",
    // "(years:Imonths:Idays:I):Y",
    // "(years:Imonths:Idays:Ihours:Iminutes:Iseconds:I):Y");
    //
    // List of types
    // V void
    // C char
    // B byte
    // S short
    // I int
    // J long
    // F float
    // D double
    // Z boolean
    // T string
    // Y date/time
    // W any (variant)
    // N multiple (...)
    // L<name>; object
    // ex:
    // (entries:[Lcom.ibm.xsp.extlib.MyClass;):V
    //
    // =========================================================================

    public NotesFunctionsEx(JSContext jsContext) {

        super(jsContext, null, false);

        // Document helpers
        addFunction(FCT_TOPPARENTID, "@TopParentID", "(doc:W):T"); // $NON-NLS-1$ $NON-NLS-2$
        addFunction(FCT_TOPPARENTUNID, "@TopParentUNID", "(doc:W):T"); // $NON-NLS-1$ $NON-NLS-2$

        // URL handling
        addFunction(FCT_FULLURL, "@FullUrl", "(str:T):T", "(str:T, form:T):T"); // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
        addFunction(FCT_ABSOLUTEURL, "@AbsoluteUrl", "(str:T):T"); // $NON-NLS-1$ $NON-NLS-2$
        addFunction(FCT_ENCODEURL, "@EncodeUrl", "(str:T):T"); // $NON-NLS-1$ $NON-NLS-2$
        addFunction(FCT_ISABSOLUTEURL, "@IsAbsoluteUrl", "(str:T):T"); // $NON-NLS-1$ $NON-NLS-2$

        // XPages helpers
        addFunction(FCT_ERRORMESSAGE, "@ErrorMessage", "(str:Tcomp:W):V"); // $NON-NLS-1$ $NON-NLS-2$
        addFunction(FCT_WARNINGMESSAGE, "@WarningMessage", "(str:Tcomp:W):V"); // $NON-NLS-1$ $NON-NLS-2$
        addFunction(FCT_INFORMATIONMESSAGE, "@InfoMessage", "(str:Tcomp:W):V"); // $NON-NLS-1$ $NON-NLS-2$

        // Domino View
        addFunction(FCT_VIEWICONURL, "@ViewIconUrl", "(icon:I):T"); // $NON-NLS-1$ $NON-NLS-2$

        addFunction(FCT_NORMALIZESUBJECT, "@NormalizeSubject", "(subject:T):T", "(subject:T, maxlength:I):T"); // $NON-NLS-1$ // $NON-NLS-2$ // $NON-NLS-3$
    }

    private void addFunction(int index, String functionName, String... params) {
        createMethod(functionName, FBSObject.P_NODELETE | FBSObject.P_READONLY, new NotesFunction(getJSContext(),
                index, functionName, params));
    }

    @Override
    public boolean hasInstance(FBSValue v) {
        return v instanceof FBSGlobalObject;
    }

    @Override
    public boolean isJavaNative() {
        return false;
    }

    // =================================================================================
    // Functions implementation
    // For optimization reasons, there is one NotesFunction instance per function,
    // instead of one class (this avoids loading to many classes). To then distinguish
    // the actual function, it uses an index member.
    // =================================================================================

    public static class NotesFunction extends BuiltinFunction {

        private String functionName;
        private int index;
        private String[] params;

        NotesFunction(JSContext jsContext, int index, String functionName, String[] params) {

            super(jsContext);
            this.functionName = functionName;
            this.index = index;
            this.params = params;
        }

        /**
         * Index of the function.
         * <p>
         * There must be one instanceof this class per index.
         * </p>
         */
        public int getIndex() {
            return this.index;
        }

        /**
         * Return the list of the function parameters.
         * <p>
         * Note that this list is not used at runtime, at least for now, but consumed by Designer code completion.<br>
         * A function can expose multiple parameter sets.
         * </p>
         */
        @Override
        protected String[] getCallParameters() {
            return this.params;
        }

        /**
         * Function name, as exposed by Designer and use at runtime.
         * <p>
         * This function is exposed in the JavaScript global namespace, so you should be careful to avoid any name
         * conflict.
         * </p>
         */
        @Override
        public String getFunctionName() {
            return this.functionName;
        }

        /**
         * Actual code execution.
         * <p>
         * The JS runtime calls this method when the method is executed within a JavaScript formula.
         * </p>
         * 
         * @param context
         *            the JavaScript execution context (global variables, function...)
         * @param args
         *            the arguments passed to the function
         * @params _this the "this" object when the method is called as a "this" member
         */
        @Override
        public FBSValue call(IExecutionContext context, FBSValueVector args, FBSObject _this)
                throws JavaScriptException {

            try {
                // Else execute the formulas
                switch (index) {

                    // ////////////////////////////////////////////////////////////////////////////////////////
                    // Document IDs
                    // ////////////////////////////////////////////////////////////////////////////////////////

                    case FCT_TOPPARENTID: {
                        if (args.size() <= 1) {
                            Document d = args.size() == 0 ? getCurrentDocument() : getDocument(args.get(0)
                                    .toJavaObject());
                            while (d != null) {
                                String unid = d.getParentDocumentUNID();
                                if (StringUtil.isEmpty(unid)) {
                                    return FBSUtility.wrap(d.getNoteID());
                                }
                                d = d.getParentDatabase().getDocumentByUNID(unid);
                            }
                            // we should never be here
                            return FBSNull.nullValue;
                        }
                    }
                        break;
                    case FCT_TOPPARENTUNID: {
                        if (args.size() <= 1) {
                            Document d = args.size() == 0 ? getCurrentDocument() : getDocument(args.get(0)
                                    .toJavaObject());
                            while (d != null) {
                                String unid = d.getParentDocumentUNID();
                                if (StringUtil.isEmpty(unid)) {
                                    return FBSUtility.wrap(d.getUniversalID());
                                }
                                d = d.getParentDatabase().getDocumentByUNID(unid);
                            }
                            // we should never be here
                            return FBSNull.nullValue;
                        }
                    }
                        break;

                    // ////////////////////////////////////////////////////////////////////////////////////////
                    // URL handling
                    // ////////////////////////////////////////////////////////////////////////////////////////

                    case FCT_FULLURL: {
                        if (args.size() == 1) {
                            String url = args.get(0).stringValue();
                            return FBSUtility.wrap(fullUrl(url));
                        } else if (args.size() == 2) {
                            String url = args.get(0).stringValue();
                            String urlFormat = args.get(1).stringValue();
                            return FBSUtility.wrap(fullUrl(url, urlFormat));
                        }
                    }
                        break;
                    case FCT_ABSOLUTEURL: {
                        if (args.size() == 1) {
                            String url = args.get(0).stringValue();
                            return FBSUtility.wrap(absoluteUrl(url));
                        }
                    }
                        break;
                    case FCT_ENCODEURL: {
                        if (args.size() == 1) {
                            String url = args.get(0).stringValue();
                            return FBSUtility.wrap(encodeUrl(url));
                        }
                    }
                        break;
                    case FCT_ISABSOLUTEURL: {
                        if (args.size() == 1) {
                            String url = args.get(0).stringValue();
                            return FBSUtility.wrap(FacesUtil.isAbsoluteUrl(url));
                        }
                    }
                        break;

                    // ////////////////////////////////////////////////////////////////////////////////////////
                    // XPages Helpers
                    // ////////////////////////////////////////////////////////////////////////////////////////

                    case FCT_ERRORMESSAGE: {
                        if (args.size() >= 1) {
                            FacesContext ctx = FacesContext.getCurrentInstance();
                            String msg = getErrorMessage(args.get(0));
                            UIComponent c = null;
                            if (args.size() >= 2) {
                                FBSValue v = args.get(1);
                                if (v.isString()) {
                                    Object _thisObject = context.getThis() != null ? context.getThis().toJavaObject()
                                            : null;
                                    UIComponent start = (_thisObject instanceof UIComponent) ? (UIComponent) _thisObject
                                            : ctx.getViewRoot();
                                    c = FacesUtil.getComponentFor(start, v.stringValue());
                                }
                            }
                            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
                            ctx.addMessage(c != null ? c.getClientId(ctx) : null, m);
                            return FBSUndefined.undefinedValue;
                        }
                    }
                        break;

                    case FCT_WARNINGMESSAGE: {
                        if (args.size() >= 1) {
                            FacesContext ctx = FacesContext.getCurrentInstance();
                            String msg = getErrorMessage(args.get(0));
                            UIComponent c = null;
                            if (args.size() >= 2) {
                                FBSValue v = args.get(1);
                                if (v.isString()) {
                                    Object _thisObject = context.getThis() != null ? context.getThis().toJavaObject()
                                            : null;
                                    UIComponent start = (_thisObject instanceof UIComponent) ? (UIComponent) _thisObject
                                            : ctx.getViewRoot();
                                    c = FacesUtil.getComponentFor(start, v.stringValue());
                                }
                            }
                            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, msg);
                            ctx.addMessage(c != null ? c.getClientId(ctx) : null, m);
                            return FBSUndefined.undefinedValue;
                        }
                    }
                        break;

                    case FCT_INFORMATIONMESSAGE: {
                        if (args.size() >= 1) {
                            FacesContext ctx = FacesContext.getCurrentInstance();
                            String msg = getErrorMessage(args.get(0));
                            UIComponent c = null;
                            if (args.size() >= 2) {
                                FBSValue v = args.get(1);
                                if (v.isString()) {
                                    Object _thisObject = context.getThis() != null ? context.getThis().toJavaObject()
                                            : null;
                                    UIComponent start = (_thisObject instanceof UIComponent) ? (UIComponent) _thisObject
                                            : ctx.getViewRoot();
                                    c = FacesUtil.getComponentFor(start, v.stringValue());
                                }
                            }
                            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
                            ctx.addMessage(c != null ? c.getClientId(ctx) : null, m);
                            return FBSUndefined.undefinedValue;
                        }
                    }
                        break;

                    // ////////////////////////////////////////////////////////////////////////////////////////
                    // Domino View
                    // ////////////////////////////////////////////////////////////////////////////////////////

                    case FCT_VIEWICONURL: {
                        if (args.size() >= 1) {
                            int icon = args.get(0).intValue();
                            if (icon >= 1 && icon <= 212) {
                                String idx = StringUtil.toString(icon, 3, '0');
                                String url = "/.ibmxspres/domino/icons/vwicn" + idx + ".gif"; // $NON-NLS-1$ $NON-NLS-2$
                                return FBSUtility.wrap(url);
                            }
                            return FBSUtility.wrap("/.ibmxspres/domino/icons/vwicn999.gif"); // $NON-NLS-1$
                        }
                    }
                        break;

                    case FCT_NORMALIZESUBJECT: {
                        if (args.size() == 1) {
                            String subject = args.get(0).stringValue();
                            if (null != subject) {
                                return FBSUtility.wrap(normalizeSubject(subject, 80));
                            }
                            return FBSUndefined.undefinedValue;
                        } else if (args.size() == 2) {
                            String subject = args.get(0).stringValue();
                            int maxlength = args.get(1).intValue();
                            if (null != subject && maxlength > 0) {
                                return FBSUtility.wrap(normalizeSubject(subject, maxlength));
                            } else if (null != subject) {
                                return FBSUtility.wrap(normalizeSubject(subject, 80));
                            }
                            return FBSUndefined.undefinedValue;
                        }
                    }
                        break;

                    default: {
                        throw new InterpretException(null, StringUtil.format(
                                "Internal error: unknown function \'{0}\'", functionName)); // $NLX-NotesFunctionEx_InternalErrorUnknownFunction-1$
                    }

                }

                // } catch (InterpretException e) {
                // throw e;
                // } catch (NotesException e) {
                // // This case covers where a call to session.evaluate() throws a NotesException
                // // We want to continue rendering the page but allow @IsError to pick up on this issue
                // // so we return @Error (NaN / FBSUndefined.undefinedValue)
                // return FBSUndefined.undefinedValue;
            } catch (Exception e) {
                throw new InterpretException(e, StringUtil.format("Error while executing function \'{0}\'", // $NLX-NotesFunctionEx_ErrorExecutingFunction-1$
                        functionName));
            }
            throw new InterpretException(null, StringUtil.format("Cannot evaluate function \'{0}\'", functionName)); // $NLX-NotesFunctionEx_CannotEvaluateFunction-1$
        }
    }

       
    /**
     * This methods calculates the full URL path, relative to the server.
     * 
     * @param url
     * @return
     */
    public static String fullUrl(String url) {
        return fullUrl(url, FULLURL_DEFAULT_FORMAT); 
    }
    
    /**
     * This methods calculates the full URL path, relative to the server.
     * 
     * @param url
     * @param urlFormat
     * @return
     */
    public static String fullUrl(String url, String urlFormat) {
        FacesContext context = FacesContext.getCurrentInstance();

        // If already absolute, leave...
        if (FacesUtil.isAbsoluteUrl(url)) {
            return url;
        }

        // SPR # PEDS8WXD6J - @FullUrl("foo.nsf") returns: /foo.nsf/foo.nsf
        // See here if the url passed contains the name of an NSF
        String nsfName = null;
        if (url.toLowerCase().contains(".nsf")) { // $NON-NLS-1$   
            // if so extract the name and check for duplicates later
            int eon = url.toLowerCase().lastIndexOf(".nsf");   // $NON-NLS-1$ 
            int son = eon;
            while (son >= 0) {
                char c = url.charAt(son);
                if (c == '!' || c == '/' || c == '\\') {
                    son++;
                    break;
                }
                son--;
            }
            if (son == -1) {
                son++;
            }
            nsfName = url.substring(son, eon+4);    
        }

        
        // If it is a global URL (/.ibmxspres/...)
        if (url.startsWith(FacesResourceServlet.RESOURCE_PREFIX)) {
            // It says as is and it will be server relative
            return url;
        }

        // If it is not an absolute URL, then make it absolute based on the current page
        if (!url.startsWith("/")) {  // $NON-NLS-1$ 
            UIViewRootEx vex = (UIViewRootEx) context.getViewRoot();
            String pageName = vex.getPageName();
            int idx = pageName.lastIndexOf("/");  // $NON-NLS-1$ 
            if (idx >= 0) {
                String path = pageName.substring(0, idx + 1);
                url = path + url;
            } else {
                url = "/" + url;  // $NON-NLS-1$ 
            }
        }

        // Then, make it relative to the current application
        url = context.getApplication().getViewHandler().getResourceURL(context, url);
        
        if(StringUtil.equalsIgnoreCase(urlFormat, FULLURL_NOTES_FORMAT) && Platform.getInstance().isPlatform("Notes")){   // $NON-NLS-1$
            // use a format that can be included in a notes://blah... URL
            // i.e. remove any server symbols (!!) and any /xsp prefixes 
            int posServerSymbol = url.indexOf("!!"); // $NON-NLS-1$ 
            if (posServerSymbol >= 0) {
                url = url.substring(posServerSymbol+2);
                if (!url.startsWith("/")) {  // $NON-NLS-1$ 
                    url = "/" + url;  // $NON-NLS-1$ 
                }
            } else if (url.startsWith("/xsp")) { // $NON-NLS-1$ 
                // Need to chop off any leading "/xsp" tokens  
                url = url.substring(4);
            }
        }

        // If the input URL is an NSF name, check that it is not duplicated in output
        if (StringUtil.isNotEmpty(nsfName)) {
            int first, last;
            do {
                first = url.toLowerCase().indexOf(nsfName.toLowerCase());
                last  = url.toLowerCase().lastIndexOf(nsfName.toLowerCase());
                if (first >= 0 && first != last ) {
                    url = url.replaceFirst(nsfName, "");
                }} while (first != last); 
            // be consistent with non-duplicated use case
            if (!url.endsWith("/")) {  // $NON-NLS-1$ 
                url = url + "/";   // $NON-NLS-1$ 
            }
        }
        
        // SPR# EGLN8DEN5U - if the NSF is in a subfolder then this can cause problems in XPiNC
        // ... where backslashes in the path name, e.g. subfolder\\foo.nsf cause get escaped in URL 
        // various other "funnies" can occur - e.g. empty slots, slashes after the server symbol etc 
        url = url.replaceAll("\\\\", "/");
        url = url.replaceAll("//", "/"); 
        url = url.replaceAll("!!/", "!!"); 
        //System.out.println(url);
        return url;
    }

    /**
     * This methods make a URL absolute, by prefix it with the protocol/server name.
     * 
     * @param url
     * @return
     */
    public static String absoluteUrl(String url) {
        return FacesUtil.makeUrlAbsolute(FacesContext.getCurrentInstance(), url);
    }

    /**
     * This methods encode the URL, but adding the necessary attributes when appropriate (session id...).
     * 
     * @param url
     * @return
     */
    public static String encodeUrl(String url) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (RenderUtil.isXspUrl(url)) {
            url = context.getExternalContext().encodeActionURL(url);
        } else {
            url = context.getExternalContext().encodeResourceURL(url);
        }
        return url;
    }

    /**
     * This method trims a subject to the specified maxlength if it exceeds it
     * 
     * @param subject
     * @param maxlength
     * @return normalized subject
     */
    public static String normalizeSubject(String subject, int maxlength) {
        if (subject.length() > maxlength) {
            subject = subject.substring(0, maxlength) + "..."; //$NON-NLS-1$
        } else if (subject.length() == 0) {
            subject = "Untitled"; // $NLS-NotesFunctionEx_SubjectUntitled-1$
        }
        return subject;
    }

    /**
     * Get an error message from a JavaScript value. Such a message is used to display an error message in JSF.
     * 
     * @return
     */
    public static String getErrorMessage(FBSValue v) throws InterpretException {
        if (v.isJavaObject()) {
            Object o = v.toJavaObject();
            if (o instanceof Throwable) {
                StringBuilder b = new StringBuilder();
                for (Throwable t = (Throwable) o; t != null; t = getCause(t)) {
                    if (b.length() > 0) {
                        b.append('\n');
                    }
                    b.append(t.toString());
                }
                return b.toString();
            }
        }
        return v.stringValue();
    }

    private static Throwable getCause(Throwable t) {
        Throwable c = t.getCause();
        return c != t ? c : null;
    }

    // =========================================================================
    // Some useful helpers
    // =========================================================================

    private static Session getCurrentSession() {
        return ExtLibUtil.getCurrentSession();
    }

    private static Database getCurrentDatabase() {
        return ExtLibUtil.getCurrentDatabase();
    }

    public static Document getCurrentDocument() {
        return getDocument(DatabaseConstants.CURRENT_DOCUMENT);
    }

    public static Document getDocument(Object var) {
        if (var instanceof Document) {
            return (Document) var;
        }
        if (var instanceof DominoDocument) {
            return ((DominoDocument) var).getDocument();
        }
        if (var instanceof String) {
            Object data = FacesUtil.resolveRequestMapVariable(FacesContext.getCurrentInstance(), (String) var);
            if (data instanceof DominoDocument) {
                DominoDocument dd = (DominoDocument) data;
                return (Document) dd.getDocument();
            }
            if (data instanceof Document) {
                return (Document) data;
            }
        }
        throw new FacesExceptionEx(null, "Cannot find document {0}", var); // $NLX-NotesFunctionEx_CannotFindNotesDocument-1$
    }

    private static DominoDocument getCurrentDominoDocument() {
        return getDominoDocument(DatabaseConstants.CURRENT_DOCUMENT);
    }

    private static DominoDocument getDominoDocument(Object var) {
        if (var instanceof DominoDocument) {
            return (DominoDocument) var;
        }
        if (var instanceof String) {
            Object data = FacesUtil.resolveRequestMapVariable(FacesContext.getCurrentInstance(), (String) var);
            if (data instanceof DominoDocument) {
                return (DominoDocument) data;
            }
        }
        throw new FacesExceptionEx(null, "Cannot find Domino document {0}", var); // $NLX-NotesFunctionEx_CannotFindXPagesDocumentObject-1$
    }
}