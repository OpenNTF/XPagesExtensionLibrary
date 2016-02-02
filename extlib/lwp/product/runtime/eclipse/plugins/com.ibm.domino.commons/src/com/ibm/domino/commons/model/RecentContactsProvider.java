/*
 * © Copyright IBM Corp. 2014
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

package com.ibm.domino.commons.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Directory;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;

import com.ibm.domino.commons.util.BackendUtil;

public class RecentContactsProvider implements IRecentContactsProvider {
    
   /**
    * Provides a service for collecting a list of internet addresses and names of those that have been in most recent
    * contact with a user.
    * 
    * This data is derived from the Sent and All Documents views of a user's mailfile.
    */
   private static final String              VIEW_ALL_DOCUMENTS   = "($All)"; //$NON-NLS-1$
   private static final String              VIEW_SENT            = "($SENT)"; //$NON-NLS-1$

   /**
    * Returns a List of PersonRecords representing a user's most recent contacts based on latest emails either sent or
    * received.
    * 
    * @param mailFile
    *           DB to open for scanning email data
    * @param isSent
    *           <p>
    *           true - recent contacts are based on recipients of user's emails<br />
    *           false - recent contacts are based on senders to user's mailfile
    *           </p>
    * @param count
    *           Maximum number of records to return
    * @param scanLimit
    *           Maximum number of documents to scan
    * @return
    */
  public RecentContactsResult getRecentContacts(Database database, final boolean isSent, 
                                  final int scanStart, final int scanLimit) throws ModelException {
      final String collectionString = isSent ? VIEW_SENT : VIEW_ALL_DOCUMENTS;
      final Map<String, RecentContact> savedEmails = new LinkedHashMap<String, RecentContact>();
      final List<RecentContact> returnList = new LinkedList<RecentContact>();

      // This is for debug - currently not printed anywhere
      @SuppressWarnings("unused")//$NON-NLS-1$
      int badNames = 0;
      int docsScanned = 0;

      Directory nDirectory = null;
      View collection = null;
      
      try
      {
         Session session = database.getParent();
         nDirectory = session.getDirectory();

         collection = database.getView(collectionString);
         collection.setAutoUpdate(false);
         collection.resortView("Date", false); //$NON-NLS-1$

         ViewNavigator nav = collection.createViewNav();
         ViewEntry entry = nav.getNth(scanStart + 1);

         while (entry != null && docsScanned < scanLimit)
         {
            final Document doc = entry.getDocument();
            
            if ( doc == null || (!isSent && !doc.hasItem("DeliveredDate")) ) { //$NON-NLS-1$
                // Ignore sent and draft documents in the all docs view
            }
            else {

                // Get a list of recent contacts from the note
                final List<RecentContact> recentContacts = isSent ? parseSentToFromNote(doc) : parseReceivedFromNote(doc);
    
                // Clean up and filter results
                for (final RecentContact person : recentContacts)
                {
                   person.DisplayName = person.DisplayName.trim().replaceAll("\"|'", ""); //$NON-NLS-1$
                   person.InternetAddress = person.InternetAddress.trim().replaceAll("\"|'", ""); //$NON-NLS-1$
    
                   final RecentContact previousPersonRecord = savedEmails.get(person.InternetAddress.toLowerCase());
    
                   if (previousPersonRecord != null)
                   {
                      ++previousPersonRecord.frequency;
    
                      // If the current record has a real display name and the old one doesn't - update it
                      if (previousPersonRecord.displayNameEqualsEmail && !person.displayNameEqualsEmail)
                      {
                         previousPersonRecord.DisplayName = person.DisplayName;
                      }
                   }
                   else
                   {
                      try
                      {
                         final boolean invalidEmailAddress = (person.InternetAddress.startsWith("CN=") //$NON-NLS-1$
                               || !person.InternetAddress.contains("@") || person.InternetAddress.contains("/")
                               || person.DisplayName.length() == 0 || person.InternetAddress.length() == 0
                               || person.InternetAddress.indexOf('@') != person.InternetAddress.lastIndexOf('@'));
    
                         if (nDirectory == null && invalidEmailAddress)
                         {
                            // Log some error here, maybe?
                         }
                         else if (invalidEmailAddress)
                         {
                            final String oldInet = person.InternetAddress;
                            final String notesName = oldInet.replaceAll("CN=|OU=|O=|@.*", ""); //$NON-NLS-1$
                            final Vector address_information = nDirectory.getMailInfo(notesName);
    
                            // Inet address is second to last item
                            final String inet = (String) address_information.get(address_information.size() - 2);
    
                            person.InternetAddress = inet;
                            person.DisplayName = notesName;
    
                            // We do this to avoid processing so that the initial hashcheck will increment
                            // the person record without coming down into this logic
                            savedEmails.put(oldInet, person);
    
                            if (savedEmails.containsKey(inet.toLowerCase()))
                            {
                               final RecentContact storedPerson = savedEmails.get(inet.toLowerCase());
                               ++storedPerson.frequency;
                               continue;
                            }
                         }
    
                         returnList.add(person);
                         savedEmails.put(person.InternetAddress.toLowerCase(), person);
                      }
                      catch (final NotesException e)
                      {
                         // Name wasn't found - logging for debug
                         ++badNames;
                      }
                   }
                }
            }
            
            ViewEntry next = nav.getNext();
            BackendUtil.safeRecycle(doc);
            entry.recycle();
            entry = next;
            
            ++docsScanned;
         }

      }
      catch (final NotesException e) {
          throw new ModelException("Unexpected error in recent contacts provider", e); // $NLX-RecentContactsProvider.Unexpectederrorinrecentcontactspr-1$
      }
      finally {
          BackendUtil.safeRecycle(collection);
          BackendUtil.safeRecycle(nDirectory);
      }

      return new RecentContactsResult(returnList, docsScanned);
   }

   /**
    * Given a note that was sent to other users - parse display names and internet addresses. List of PersonRecords will
    * be returned for data parsed from TO, CC, and BCC fields.
    * 
    * @param doc
    *           Email note to parse for recipient data
    * @return
    * @throws NotesException
    */
   private List<RecentContact> parseSentToFromNote(final Document doc) throws NotesException
   {
      final List<RecentContact> recentContacts = new LinkedList<RecentContact>();

      final Vector dateVec = doc.getItemValue("PostedDate"); //$NON-NLS-1$
      final Date emailDate = getDateFromVector(dateVec);

      final String[][] itemPairsToRead = { { "InetSendTo", "SendTo" }, //$NON-NLS-1$ //$NON-NLS-2$ 
                          { "InetCopyTo", "CopyTo" }, //$NON-NLS-1$ //$NON-NLS-2$
                          { "InetBlindCopyTo", "BlindCopyTo" } }; //$NON-NLS-1$ //$NON-NLS-2$

      for (int i = 0; i < itemPairsToRead.length; ++i)
      {
         final Vector inetVector = doc.getItemValue(itemPairsToRead[i][0]);
         final Vector nameVector = doc.getItemValue(itemPairsToRead[i][1]);

         if (nameVector == null || nameVector.size() == 0)
         {
            continue;
         }

         final boolean hasInet = (inetVector != null && inetVector.size() > 0);

         for (int addrIndex = 0; addrIndex < nameVector.size(); ++addrIndex)
         {
            final RecentContact person = new RecentContact();

            if (hasInet && !(person.InternetAddress = (String) inetVector.get(addrIndex)).contains("<"))
            {
               person.DisplayName = (String) nameVector.get(addrIndex);
               person.DisplayName = person.DisplayName.replaceAll("CN=|OU=|O=|@.*", ""); //$NON-NLS-1$
            }
            else
            {
               // This is not a notes recipient then - address in SendTo
               processComboInet(person, (String) nameVector.get(addrIndex));
            }

            person.lastContacted = emailDate;
            recentContacts.add(person);
         }
      }

      return recentContacts;
   }

   /**
    * Given a note received from another user, parse the from display name and internet address.
    * 
    * @param doc
    *           Email note to parse for sender data
    * @return
    * @throws NotesException
    */
   private List<RecentContact> parseReceivedFromNote(final Document doc) throws NotesException
   {
      // Currently only getting from address - may get items like others that were on the email
      // in the future.
      final List<RecentContact> recentContacts = new ArrayList<RecentContact>(1);
      final RecentContact sender = new RecentContact();
      recentContacts.add(sender);

      final Vector dateVec = doc.getItemValue("DeliveredDate"); //$NON-NLS-1$
      if (doc.hasItem("INetFrom")) //$NON-NLS-1$
      {
         sender.InternetAddress = doc.getItemValueString("INetFrom"); //$NON-NLS-1$

         if (sender.InternetAddress.contains("<"))
         {
            processComboInet(sender, sender.InternetAddress);
         }
         else
         {
            sender.DisplayName = doc.getItemValueString("From"); //$NON-NLS-1$
            sender.DisplayName = sender.DisplayName.replaceAll("CN=|OU=|O=|@.*", ""); //$NON-NLS-1$
         }
      }
      else
      {
         processComboInet(sender, doc.getItemValueString("From")); //$NON-NLS-1$
      }

      sender.lastContacted = getDateFromVector(dateVec);

      return recentContacts;
   }

   /**
    * Parse display name and internet address from a string in the format of
    * 
    * DisplayName <Interent@Addr.ess>
    * 
    * @param person
    *           PersonRecord to modify InternetAddress and DisplayName
    * @param comboInet
    *           String which potentially contains an address in the format of DisplayName <Interent@Addr.ess>
    */
   private void processComboInet(final RecentContact person, final String comboInet)
   {
      final int startOfBracket = comboInet.indexOf("<");
      if (startOfBracket < 0)
      {
         // We have no display name - just email
         person.InternetAddress = comboInet.replaceAll("<|>", ""); //$NON-NLS-1$
         person.DisplayName = person.InternetAddress;
         person.displayNameEqualsEmail = true;
      }
      else
      {
         // Extract the display and internet addresses
         person.DisplayName = comboInet.substring(0, startOfBracket);
         person.InternetAddress = comboInet.substring(startOfBracket + 1, comboInet.length() - 1);
      }
   }

   /**
    * Get Java Date from Notes item value (vector)
    * 
    * @param dateVec
    *           Vector to process into date
    * @return
    */
   private Date getDateFromVector(final Vector dateVec)
   {
      if (dateVec.size() == 0)
      {
         return new Date();
      }
      else
      {
         final DateTime dateTime = (DateTime) dateVec.get(0);
         final Date returnDate = safeToJavaDate(dateTime);
         return (returnDate != null) ? returnDate : new Date();
      }
   }

   /**
    * Converts a Notes DateTime to a Java Date. We have problems with date fields with values of FFFFFFFF:FFFFFFFFF
    * since they cause toJavaDate() to choke; we'll catch it here and just return a null.
    * 
    * Borrowed/Adapted - from Traveler
    */
   private Date safeToJavaDate(final DateTime date)
   {
      Date value = null;
      try
      {
         final String time = date.getTimeOnly();
         if ((time == null) || time.equals(""))
         {
            DateTime workingDate = date.getParent().createDateTime(date.getDateOnly() + " 00:00:00 GMT"); //$NON-NLS-1$
            value = workingDate.toJavaDate();
            workingDate.recycle();
            workingDate = null;
         }
         else
         {
            value = date.toJavaDate();
         }
      }
      catch (final NotesException notesEx)
      {
         // If there was an error we'll just return a null
      }

      return value;
   }
}
