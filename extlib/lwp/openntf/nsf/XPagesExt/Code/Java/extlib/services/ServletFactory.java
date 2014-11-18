package extlib.services;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.domino.services.ServiceEngine;
import com.ibm.domino.services.ServiceException;
import com.ibm.domino.services.rest.das.view.RestViewColumn;
import com.ibm.domino.services.rest.das.view.RestViewEntry;
import com.ibm.domino.services.rest.das.view.RestViewService;
import com.ibm.domino.services.rest.das.view.RestViewXmlLegacyService;
import com.ibm.domino.services.rest.das.view.impl.DefaultViewColumn;
import com.ibm.domino.services.rest.das.view.impl.DefaultViewParameters;
import com.ibm.xsp.extlib.services.servlet.DefaultServiceFactory;
import com.ibm.xsp.extlib.services.servlet.DefaultServletFactory;
import com.ibm.xsp.extlib.services.servlet.ServiceFactory;

/**
 * Servlet Factory.
 * 
 * The servlet factory class is used to create the actual instances of the Servlets and
 * dispatch the requests to them.
 */
public class ServletFactory extends DefaultServletFactory {

	private static ServiceFactory createFactory() {
		DefaultServiceFactory factory = new DefaultServiceFactory();
		
		// All Contacts View
		final List<RestViewColumn> allContactsColumns = Arrays.asList( (RestViewColumn)
				new DefaultViewColumn("ComputedColumn") {
					@Override
					public Object evaluate(RestViewService service, RestViewEntry entry) throws ServiceException {
						String v = entry.getColumnValue("EMail").toString();
						return v.toUpperCase();
					}
				}
			);
		factory.addFactory("AllContacts", new ServiceFactory() {
			public ServiceEngine createEngine(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException {
				DefaultViewParameters p = new DefaultViewParameters();
				p.setViewName("AllContacts");
				p.setGlobalValues(DefaultViewParameters.GLOBAL_ALL);
				p.setSystemColumns(DefaultViewParameters.SYSCOL_ALL);
				p.setDefaultColumns(true);
				p.setColumns(allContactsColumns);
				// Set the default parameters
				p.setStart(0);
				p.setCount(4);
				return new RestViewXmlLegacyService(httpRequest,httpResponse,p);
			}
		});
		
		return factory;
	}
	
	public ServletFactory() {
		super("services","Extension Library Services Servlet",createFactory());
	}
}