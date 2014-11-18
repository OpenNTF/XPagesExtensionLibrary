/*
 * © Copyright IBM Corp. 2011
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

package com.ibm.domino.services;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_GONE;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_NOT_IMPLEMENTED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public enum ResponseCode {
    
    UNINITIALIZED(SC_INTERNAL_SERVER_ERROR, "Internal Error"), // $NLX-ResponseCode.InternalError-1$
    BAD_REQUEST(SC_BAD_REQUEST, "Bad Request"), // $NLX-ResponseCode.BadRequest-1$
    CONFLICT(SC_CONFLICT, "Conflict"), // $NLX-ResponseCode.Conflict-1$
    DEPRECATED_URI(SC_MOVED_PERMANENTLY, "Deprecated URI"), // $NLX-ResponseCode.DeprecatedURI-1$
    METHOD_NOT_ALLOWED(SC_METHOD_NOT_ALLOWED, "Method not allowed"), // $NLX-ResponseCode.Methodnotallowed-1$
    INTERNAL_ERROR(SC_INTERNAL_SERVER_ERROR, "Internal Error"), // $NLX-ResponseCode.InternalError.1-1$
    NOT_IMPLEMENTED(SC_NOT_IMPLEMENTED, "Not implemented"), // $NLX-ResponseCode.Notimplemented-1$
    RSRC_GONE(SC_GONE, "Gone"), // $NLX-ResponseCode.Gone-1$
    RSRC_NOT_FOUND(SC_NOT_FOUND, "Not found"), // $NLX-ResponseCode.Notfound-1$
    RSRC_UNCHANGED(SC_NOT_MODIFIED, "Not modified"), // $NLX-ResponseCode.Notmodified-1$
    FORBIDDEN(SC_FORBIDDEN, "Forbidden"), // $NLX-ResponseCode.Forbidden-1$
    UNAUTHORIZED(SC_UNAUTHORIZED, "Not Authorized"), // $NLX-ResponseCode.NotAuthorized-1$
    RSRC_CREATED(SC_CREATED, "Created"), // $NLX-ResponseCode.Created-1$
    OK(SC_OK, "OK"); // $NLX-ResponseCode.OK-1$
    
    public final int httpStatusCode;
    public final String httpStatusText;
    
    ResponseCode(final int httpStatusCode, final String httpStatusText) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatusText = httpStatusText;
    }
}