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

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonGenerator.Generator;
import com.ibm.commons.util.io.json.JsonGenerator.StringBuilderGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.domino.osgi.core.context.ContextInfo;
import com.ibm.domino.services.sample.json.JsonViewEntryAdapter;
import com.ibm.domino.services.sample.service.SampleService;

@Path("sample/contacts") // $NON-NLS-1$
public class ContactsListResource {
    
    private static final int MAX_ENTRIES = 100;

    /**
     * Gets a list of contacts.
     * 
     * @param uriInfo
     * @return
     */
    @GET
    public Response getContacts(@Context UriInfo uriInfo) {

        SAMPLE_SERVICE_LOGGER.traceEntry(this, "getContacts"); // $NON-NLS-1$
        
        Database database = ContextInfo.getUserDatabase();
        if ( database == null ) {
            throw new WebApplicationException(SampleService.createErrorResponse("No database context.", Response.Status.NOT_FOUND)); // $NLX-ContactsListResource.Nodatabasecontext-1$
        }
        
        String jsonEntity = null;
        View view = null;
        
        try {
            // Open the contacts view
            view = database.getView("AllContacts"); // $NON-NLS-1$
            ViewNavigator entries = view.createViewNav();
            
            // Get the base URL for each entry
            String baseUrl = uriInfo.getAbsolutePath().getPath();
            
            // Create and initialize a JSON generator
            StringBuilder sb = new StringBuilder();
            Generator generator = new StringBuilderGenerator(JsonJavaFactory.instanceEx, sb, false);
            generator.out("[");
            generator.nl();
            generator.incIndent();
            
            // TODO: Implement paging.  For now just stop after the
            // entry count reaches MAX_ENTRIES

            // Do for each entry
            int i = 0;
            ViewEntry entry = entries.getFirst();
            while ( entry != null && i < MAX_ENTRIES ) {
                if ( i > 0 ) {
                    generator.out(",");
                    generator.nl();
                }
                
                // Convert view entry to JSON format
                JsonViewEntryAdapter entryAdapter = new JsonViewEntryAdapter(entry, baseUrl);
                generator.toJson(entryAdapter);
                
                // Get the next entry
                ViewEntry next = entries.getNext();
                
                // Recycle the last entry and loop
                entry.recycle();
                entry = next;
                i++;
            }
            
            generator.nl();
            generator.decIndent();
            generator.out("]");
            
            jsonEntity = sb.toString();
        }
        catch (NotesException e) {
            throw new WebApplicationException(SampleService.createErrorResponse(e));
        }
        catch (IOException e) {
            throw new WebApplicationException(SampleService.createErrorResponse(e));
        }
        catch (JsonException e) {
            throw new WebApplicationException(SampleService.createErrorResponse(e));
        }
        finally {
            if ( view != null ) {
                try {
                    view.recycle();
                    view = null;
                } catch (NotesException e) {
                    // Ignore
                }
            }
        }
        
        ResponseBuilder builder = Response.ok();
        builder.type(MediaType.APPLICATION_JSON_TYPE).entity(jsonEntity);
        Response response = builder.build();
        SAMPLE_SERVICE_LOGGER.traceExit(this, "getContacts", response); // $NON-NLS-1$

        return response;
    }

}