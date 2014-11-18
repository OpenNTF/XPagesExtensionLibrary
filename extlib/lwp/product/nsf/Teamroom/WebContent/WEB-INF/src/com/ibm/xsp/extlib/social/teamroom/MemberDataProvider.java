package com.ibm.xsp.extlib.social.teamroom;

import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;

import com.ibm.commons.util.SystemCache;
import com.ibm.xsp.application.ApplicationEx;
import com.ibm.xsp.extlib.social.Person;
import com.ibm.xsp.extlib.social.impl.AbstractPeopleDataProvider;
import com.ibm.xsp.extlib.social.impl.PersonImpl;
import com.ibm.xsp.model.domino.DominoUtils;

/**
 * @author Tony McGuckin, IBM
 * 
 *         TeamRoom Member Data Provider.
 * 
 *         This data provider provides information for a set of members for this
 *         teamroom database.
 */
public class MemberDataProvider extends AbstractPeopleDataProvider {

	public static final String FIELD_INTERNETEMAIL = "internetEmail";

	private String NO_IMAGE_URL;
	private String IMAGE_URL;

	private static final String _viewName = "PeopleLookup";

	private static class MemberData extends PersonImpl.Properties {
		String thumbnailUrl;
		String internetEmail;
	}

	public MemberDataProvider() {
		ApplicationEx app = ApplicationEx.getInstance();
		NO_IMAGE_URL = app.getApplicationProperty("thumbnailUrl.noImageUrl", "/thumbNoImage.png");
		try {
			IMAGE_URL = "/.ibmmodres/";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getValue(PersonImpl person, Object prop) {
		if (prop.equals(Person.FIELD_THUMBNAIL_URL)) {
			return getData(person).thumbnailUrl;
		}
		if (prop.equals(FIELD_INTERNETEMAIL)) {
			return getData(person).internetEmail;
		}
		return null;
	}

	public Class<?> getType(PersonImpl person, Object prop) {
		if (prop.equals(Person.FIELD_THUMBNAIL_URL)) {
			return String.class;
		}
		if (prop.equals(FIELD_INTERNETEMAIL)) {
			return String.class;
		}
		return null;
	}

	public void readValues(PersonImpl[] persons) {
		for (int i = 0; i < persons.length; i++) {
			getData(persons[i]);
		}
	}

	private MemberData getData(PersonImpl person) {
		String id = person.getId();
		MemberData data = (MemberData) getProperties(id, MemberData.class);
		if (data == null) {
			synchronized (getSyncObject()) {
				data = (MemberData) getProperties(id, MemberData.class);
				if (null == data) {
					data = new MemberData();
					try {
						View vw = getView();
						if (null != vw) {
							ViewEntry vwe = vw.getEntryByKey(person.getId());
							if (null != vwe) {
								Vector<?> cvs = vwe.getColumnValues();
								if (!cvs.isEmpty()) {
									// get the thumbnameUrl column value...
									String thumbnailUrl = (String) cvs.get(5);
									if (!thumbnailUrl.equals("")) {
										// get the doc unid...
										String docUnid = (String) cvs.get(4);
										data.thumbnailUrl = IMAGE_URL + docUnid + "/$file/" + thumbnailUrl;
									} else {
										data.thumbnailUrl = NO_IMAGE_URL;
									}

									// get the email column value...
									String internetEmail = (String) cvs.get(1);
									if (!internetEmail.equals("")) {
										data.internetEmail = internetEmail;
									} else {
										// default to the abbreviated name column value...
										data.internetEmail = (String) cvs.get(0);
									}
								}
							}else{
								data.thumbnailUrl = NO_IMAGE_URL;
							}
						}
					} catch (NotesException e) {
						e.printStackTrace();
					}
					addProperties(id, data);
				}
			}
		}
		return data;
	}

	// People Lookup utilities

	protected View getView() throws NotesException {
		Database db = DominoUtils.getCurrentDatabase();
		View view = db.getView(_viewName);
		view.setAutoUpdate(false);
		view.refresh();
		return view;
	}

	public void clear(String id) {
		Map<?,?> map = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
		if(null != map && null != (SystemCache)map.get(CACHE_KEY)) {
			synchronized(map) {
				map.remove(CACHE_KEY);
			}
		}
	}
}
