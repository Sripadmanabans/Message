package message.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import message.constant.ConstantHolder;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class MessageListServlet extends HttpServlet implements ConstantHolder
{
	private static final Logger log = Logger.getLogger(MessageListServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		int i = 0; 
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		NamespaceManager.set(NAMESPACE);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Query query = new Query(DATASTORE_REGISTER);
		
		PreparedQuery preparedQuery = datastore.prepare(query);
		
		JsonObjectBuilder profiles = Json.createObjectBuilder();
		JsonArrayBuilder list = Json.createArrayBuilder();
		
		for(Entity result : preparedQuery.asIterable())
		{
			JsonObjectBuilder temp = Json.createObjectBuilder();
			temp.add(FIRST_NAME, result.getProperty(FIRST_NAME).toString());
			temp.add(LAST_NAME, result.getProperty(LAST_NAME).toString());
			temp.add(EMAIL, result.getProperty(EMAIL).toString());
			
			list.add(temp);
			i++;
			
		}
		
		profiles.add(PROFILE, list);
		
		JsonObject json = profiles.build();
		
		log.setLevel(Level.INFO);
		log.info(json.toString() + "  " + i);
		
		out.write(json.toString());
		out.close();
		
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		doGet(request, response);
	}
}
