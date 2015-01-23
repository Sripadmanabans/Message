package message.send;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import message.constant.ConstantHolder;

@SuppressWarnings("serial")
public class MessageSendServlet extends HttpServlet implements ConstantHolder
{
	private static final Logger log = Logger.getLogger(MessageSendServlet.class.getName());
	private HttpURLConnection connection;
	private String urlString = "https://android.googleapis.com/gcm/send";
	private String value;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		response.setContentType("text/plain");
		
		PrintWriter out = response.getWriter();
		
		InputStream input = request.getInputStream();
		JsonReader reader = Json.createReader(input);
		
		JsonObject object = reader.readObject();
		
		reader.close();
		input.close();
		
		log.setLevel(Level.INFO);
		log.info(object.toString());
		
		String email = object.getString(EMAIL);
		String message = object.getString(MESSAGE);
		
		NamespaceManager.set(NAMESPACE);
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Filter matchEmailFiler = new FilterPredicate(EMAIL, FilterOperator.EQUAL, email);
		
		Query query = new Query(DATASTORE_REGISTER).setFilter(matchEmailFiler);
		
		PreparedQuery pq = datastore.prepare(query);
		
		Entity result = pq.asSingleEntity();
		
		JsonObjectBuilder payload = Json.createObjectBuilder();
		JsonArrayBuilder register = Json.createArrayBuilder();
		JsonObjectBuilder data = Json.createObjectBuilder();
		
		data.add("message", message);
		data.add("email", email);
		register.add(result.getProperty(REGISTRATION_ID).toString());
		
		payload.add("registration_ids", register);
		payload.add("data", data);
		
		JsonObject json = payload.build();
		
		log.info(json.toString());
		
		try
		{
			URL url = new URL(urlString);
			URLConnection urlcon = url.openConnection();
			log.info(urlcon.getClass().getName());
			connection = (HttpURLConnection) urlcon;
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "key=AIzaSyAWijr7FRIH5LaOessE-RrIwKvsHadZzs4");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			
			OutputStream output = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
			
			writer.write(json.toString());
			writer.flush();
			writer.close();
			output.close();
			
			InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuffer resp = new StringBuffer();
            while((line = rd.readLine()) != null)
            {
                resp.append(line);
                resp.append('\n');
            }
            rd.close();
            
            value = resp.toString();
            
            log.info(value);
            

		}
		catch(IOException e)
		{
			log.info("Something wrong in connection part");
		}
		catch(ClassCastException e)
		{
			log.info("What's this??");
		}
		finally
		{
			if(connection != null)
			{
				connection.disconnect();
			}
		}
		

		out.write("Hello");
		out.close();
		
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException 
	{
		doGet(request, response);
	}
}
