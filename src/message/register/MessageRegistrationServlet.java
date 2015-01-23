package message.register;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import message.constant.ConstantHolder;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@SuppressWarnings("serial")
public class MessageRegistrationServlet extends HttpServlet implements ConstantHolder
{
	private static final Logger log = Logger.getLogger(MessageRegistrationServlet.class.getName());
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException 
	{
		response.setContentType("text/plain");
		
		PrintWriter out = response.getWriter();
		
		InputStream input = request.getInputStream();
		JsonReader reader = Json.createReader(input);
		
		JsonObject jsonObject = reader.readObject();
		
		reader.close();
		input.close();
		
		log.setLevel(Level.INFO);
		log.info (jsonObject.toString());
		
        String firstName = jsonObject.getString(FIRST_NAME);
        String lastName = jsonObject.getString(LAST_NAME);
        String email = jsonObject.getString(EMAIL);
        String regId = jsonObject.getString(REGISTRATION_ID);
		
		log.info(firstName + " : " + lastName + " : " + email);
		
		NamespaceManager.set(NAMESPACE);
		
		DatastoreService dataStore = DatastoreServiceFactory.getDatastoreService();
		
		Entity register = new Entity(DATASTORE_REGISTER, email);
		
		register.setProperty(FIRST_NAME, firstName);
		register.setProperty(LAST_NAME, lastName);
		register.setProperty(EMAIL, email);
		register.setProperty(REGISTRATION_ID, regId);
		
		dataStore.put(register);
		
		out.write("Sucess");
		out.flush();
		out.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		doGet(request, response);
	}
}
