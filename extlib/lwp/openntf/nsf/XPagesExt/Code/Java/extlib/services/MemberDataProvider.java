package extlib.services;

import com.ibm.xsp.extlib.social.Person;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;

/**
 * Member Data Provider.
 * 
 * This data provider provides information for a set of user in the database.
 */
public class MemberDataProvider extends AbstractPeopleDataProvider {

	private static class MemberData extends PersonImpl.Properties {
		String thumbnailUrl;
	}
	
	public MemberDataProvider() {
	}

	public String getName() {
		return "Demo App Provider";
	}
	
	public Object getValue(PersonImpl person, Object prop) {
		if(prop.equals(Person.FIELD_THUMBNAIL_URL)) {
			return getData(person).thumbnailUrl;
		}
		return null;
	}

	public Class<?> getType(PersonImpl person, Object prop) {
		if(prop.equals(Person.FIELD_THUMBNAIL_URL)) {
			return String.class;
		}
		return null;
	}

	public void readValues(PersonImpl[] persons) {
		for(int i=0; i<persons.length; i++) {
			getData(persons[i]);
		}
	}
	
	private MemberData getData(PersonImpl person) {
		String id = person.getId();
		MemberData data = (MemberData)getProperties(id,MemberData.class);
		if(data==null) {
			synchronized(getSyncObject()) {
				data = (MemberData)getProperties(id,MemberData.class);
				if(data==null) {
					data = new MemberData(); 

					// Read the thumbnail URL
					if(id.equals("CN=Frank Adams/O=renovations")) {
						data.thumbnailUrl = "/FrankAdams.png";
					} else if(id.equals("CN=Betty Zechman/O=renovations")) {
						data.thumbnailUrl = "/BettyZechman.png";
					} else if(id.equals("CN=Ted Amado/O=renovations")) {
						data.thumbnailUrl = "/TedAmado.png";
					} else {
						// We don't have a picture... let's return an unknown one
						data.thumbnailUrl = "/thumbNoPhoto.png";
					}
					
					addProperties(id,data);
				}
			}
		}
		return data;
	}
}
