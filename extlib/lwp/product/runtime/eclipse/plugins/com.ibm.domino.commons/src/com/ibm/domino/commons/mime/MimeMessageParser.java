/*
 * © Copyright IBM Corp. 2012
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

package com.ibm.domino.commons.mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.Stream;

import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.field.address.Address;
import org.apache.james.mime4j.field.address.AddressList;
import org.apache.james.mime4j.message.BinaryBody;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Entity;
import org.apache.james.mime4j.message.Header;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.TextBody;
import org.apache.james.mime4j.parser.Field;

import com.ibm.domino.commons.model.ModelException;

public class MimeMessageParser {
    
    private static final String ITEM_SUBJECT = "Subject"; //$NON-NLS-1$
    private static final String ITEM_FORM = "Form"; //$NON-NLS-1$
    private static final String ITEM_SEND_TO = "SendTo"; //$NON-NLS-1$
    private static final String ITEM_COPY_TO = "CopyTo"; //$NON-NLS-1$
    private static final String ITEM_BLINDCOPY_TO = "BlindCopyTo"; //$NON-NLS-1$
    private static final String ITEM_IN_REPLY_TO = "In_Reply_To"; //$NON-NLS-1$
    private static final String ITEM_RETURN_RECEIPT = "ReturnReceipt"; //$NON-NLS-1$
    private static final String ITEM_DISPOSITION_NOTIFICATION_TO = "Disposition_Notification_To"; //$NON-NLS-1$
    
    private static final String HEADER_IN_REPLY_TO = "In-Reply-To"; //$NON-NLS-1$
    private static final String HEADER_DISPOSITION_NOTIFICATION_TO = "Disposition-Notification-To"; //$NON-NLS-1$
    
    private InputStream _is;
    
    public MimeMessageParser(InputStream is) {
        _is = is;
    }
    
    public void fromMime(Document document) throws MimeIOException, IOException, ModelException {
        try {
            Message mimeMsg = new Message(_is);
            List<Field> fields = mimeMsg.getHeader().getFields();
            if ( fields == null || fields.size() == 0 ) {
                throw new ModelException("Message is empty", ModelException.ERR_INVALID_INPUT); // $NLX-MimeMessageParser.Messageisempty-1$
            }
        
            document.replaceItemValue(ITEM_FORM, "Memo"); //$NON-NLS-1$
            
            String subject = mimeMsg.getSubject();
            document.replaceItemValue(ITEM_SUBJECT, subject);
            
            AddressList to = mimeMsg.getTo();
            writeAddresses(document, ITEM_SEND_TO, to);
    
            AddressList cc = mimeMsg.getCc();
            writeAddresses(document, ITEM_COPY_TO, cc);
            
            AddressList bcc = mimeMsg.getBcc();
            writeAddresses(document, ITEM_BLINDCOPY_TO, bcc);
            
            Header header = mimeMsg.getHeader();
            Field inReplyTo = header.getField(HEADER_IN_REPLY_TO);
            if ( inReplyTo != null ) {
                String value = inReplyTo.getBody();
                document.replaceItemValue(ITEM_IN_REPLY_TO, value);
            }
            
            Field receiptTo = header.getField(HEADER_DISPOSITION_NOTIFICATION_TO);
            if ( receiptTo != null ) {
                String value = receiptTo.getBody();
                document.replaceItemValue(ITEM_DISPOSITION_NOTIFICATION_TO, value);
                document.replaceItemValue(ITEM_RETURN_RECEIPT, "1");
            }
            
            writeEntity(document, null, mimeMsg);
        }
        catch (NotesException e) {
            throw new ModelException("Notes document error.", e); // $NLX-MimeMessageParser.Notesdocumenterror-1$
        }
    }
    
    private static void writeEntity(Document document, MIMEEntity parent, Entity entity) throws IOException, NotesException {  
        MIMEEntity notesEntity = null;
        if ( parent == null ) {
            notesEntity = document.createMIMEEntity();
        }
        else {
            notesEntity = parent.createChildEntity();
        }
        
        String mediaType = entity.getMimeType();
        
        Body body = entity.getBody();
        if ( body instanceof Multipart ) {

            // Set the content type
            
            MIMEHeader notesHeader = notesEntity.createHeader("Content-Type"); //$NON-NLS-1$
            notesHeader.setHeaderVal(mediaType);
            
            Multipart multipart = (Multipart)body;
            for (BodyPart part : multipart.getBodyParts()) {  
                writeEntity(document, notesEntity, part);  
            }
        }
        else {
            Header header = entity.getHeader();
            
            // Handle content disposition
            String filename = entity.getFilename();
            String disposition = entity.getDispositionType();
            if ( disposition != null ) {
                MIMEHeader notesHeader = notesEntity.createHeader("Content-Disposition"); //$NON-NLS-1$
                String value = disposition;
                if ( filename != null ) {
                    value += "; filename=\"" + filename + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                }
                notesHeader.setHeaderVal(value);
            }
            
            // Handle content ID
            Field id = header.getField("Content-ID"); //$NON-NLS-1$
            if ( id != null ) {
                MIMEHeader notesHeader = notesEntity.createHeader("Content-ID"); //$NON-NLS-1$
                notesHeader.setHeaderVal(id.getBody());
            }

            // Write a simple part's content
            
            Session session = document.getParentDatabase().getParent();
            
            if ( body instanceof TextBody) {
                Reader reader = ((TextBody)body).getReader();
                Stream stream = session.createStream();
                readerToNotesStream(stream, reader);
                
                // TODO: Handle character set
                
                notesEntity.setContentFromText(stream, mediaType, MIMEEntity.ENC_NONE);
            }
            else if ( body instanceof BinaryBody ) {
                InputStream is = ((BinaryBody)body).getInputStream();
                Stream stream = session.createStream();
                inputStreamToNotesStream(stream, is);

                notesEntity.setContentFromBytes(stream, mediaType, MIMEEntity.ENC_NONE);
            }
            else {
                // TODO: Is this an error condition?
            }
        }
    }
    
    private static void writeAddresses(Document document, String itemName, AddressList addresses) throws NotesException {
        
        if ( addresses != null && addresses.size() > 0 ) {
            Vector<String> values = new Vector<String>();
            for ( int i = 0; i < addresses.size(); i++ ) {
                Address address = addresses.get(i);
                values.add(address.toString());
            }
            
            document.replaceItemValue(itemName, values);
        }
    }

    private static void readerToNotesStream(Stream stream, Reader reader) throws IOException, NotesException {
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        while ( line != null ) {
            stream.writeText(line);
            stream.writeText("\r\n"); //$NON-NLS-1$
            line = br.readLine();
        }
    }
    
    private static void inputStreamToNotesStream(Stream stream, InputStream is) throws IOException, NotesException {
        
        int ch = is.read();
        while ( ch != -1 ) {
            byte b[] = new byte[1];
            b[0] = (byte)ch;
            stream.write(b);
            ch = is.read();
        }
    }
}