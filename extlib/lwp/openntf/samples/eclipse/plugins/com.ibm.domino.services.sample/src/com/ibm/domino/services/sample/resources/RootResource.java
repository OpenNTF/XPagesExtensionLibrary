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

package com.ibm.domino.services.sample.resources;

import static com.ibm.domino.services.sample.json.JsonConstants.HREF_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.LINKS_PROP;
import static com.ibm.domino.services.sample.json.JsonConstants.REL_PROP;
import static com.ibm.domino.services.sample.service.SampleService.SAMPLE_SERVICE_LOGGER;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.services.sample.service.SampleService;

@Path("sample") // $NON-NLS-1$
public class RootResource {
    
    private static final String API_SAMPLE_PATH = "/api/sample"; // $NON-NLS-1$
    private static final String CONTACTS_PATH = "/xpagesext.nsf" + API_SAMPLE_PATH + "/contacts"; // $NON-NLS-1$ $NON-NLS-2$

    /**
     * Gets links.
     * 
     * @param uriInfo
     * @return
     */
    @GET
    public Response getLinks(@Context UriInfo uriInfo) {

        SAMPLE_SERVICE_LOGGER.traceEntry(this, "getLinks"); // $NON-NLS-1$
        
        String jsonEntity = null;
        
        try {
            jsonEntity = getResponseEntity();
        } 
        catch (IOException e) {
            throw new WebApplicationException(SampleService.createErrorResponse(e));
        } 
        catch (JsonException e) {
            throw new WebApplicationException(SampleService.createErrorResponse(e));
        }
        
        ResponseBuilder builder = Response.ok();
        builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        Response response = builder.build();
        SAMPLE_SERVICE_LOGGER.traceExit(this, "getLinks", response); // $NON_NLS-1$

        return response;
    }

    private String getResponseEntity() throws IOException, JsonException {
        
        String url = CONTACTS_PATH;
        
        StringBuilder sb = new StringBuilder();
        Generator g = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
        g.out("{");
        g.nl();
        g.incIndent();

        // Start array of links
        
        g.indent();
        g.outPropertyName(LINKS_PROP);
        g.out(":[");
        g.nl();
        g.incIndent();
        
        // Emit contacts link
        
        g.indent();
        g.out("{");
        g.nl();
        g.incIndent();
        
        g.indent();
        g.outPropertyName(REL_PROP);
        g.out(":");
        g.outLiteral("contacts"); // $NON-NLS-1$
        g.out(",");
        g.nl();
        
        g.indent();
        g.outPropertyName(HREF_PROP);
        g.out(":");
        g.outLiteral(url);
        g.nl();
        
        g.decIndent();
        g.indent();
        g.out("}");
        g.nl();
        
        // Close array of links
        
        g.decIndent();
        g.indent();
        g.out("]");
        
        g.decIndent();
        g.indent();
        g.nl();
        g.out("}");

        return sb.toString();
    }
}