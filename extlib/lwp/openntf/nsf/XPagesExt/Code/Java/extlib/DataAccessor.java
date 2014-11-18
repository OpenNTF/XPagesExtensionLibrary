package extlib;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class DataAccessor {

	private boolean firstContactIDRead;
	private String firstContactID;
	
	public DataAccessor() {
	}

	
	// ===================================================================
	// Access the first document in the contact view
	// ===================================================================

	public String getFirstContactID() throws NotesException {
		if(!firstContactIDRead) {
			Database db = ExtLibUtil.getCurrentDatabase();
			View view = db.getView("AllContacts");
			Document doc = view.getFirstDocument();
			if(doc!=null) {
				firstContactID = doc.getNoteID();
			}
		}
		return firstContactID;
	}
}
