package rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.JSONP;
import org.json.JSONException;
import org.json.JSONObject;

import pojo.Message;

import com.hp.hpl.jena.rdf.model.Model;

import core.SchemaProvider;
import core.SendMailTLS;
import core.Tools;
import core.UserHandler;
@Path("user")
public class PersonResource {

	
	@Path("getDataSchema")
    @GET
    @Produces({"application/ld+json"})
    public Response getDatasetSchema() {
		//check for cache
		// return getDescriptor(descriptor uri)
		//return model getOntology PropertiesClassess( Model descriptor)
		//
	
		
		
		
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").entity("").build();
      
    }
	
	
	@Path("generateid")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateId(){
		
		JSONObject response=new JSONObject();
		response.put("id", Tools.generateID());
		
		return Response.ok(response.toString()).build();
		
	}

	@Path("create")
	@JSONP(queryParam="callback")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createUser(@FormParam("name")String name,@FormParam("email") String email) {
		
		String response=	UserHandler.createUser(name, email);
		return	Response.ok().entity(response).build();	
		
      
    }
	@Path("validate")
	@JSONP(queryParam="callback")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response activate(@FormParam("code")String code,@FormParam("email2") String email) {
		
		Model user=UserHandler.getUser(email);
		if(user.isEmpty()){
			return Response.serverError().entity(new Message(1,"It appears you are not yet registered for IITB Life access, please use the <a href=#register>registration form</a> above to register.")).build();
		}
		String id=UserHandler.getUserID(user);
		if(id.equals(code)){
			Message m=new Message(2,"Thank you, your account has been verified. You may now access the IITB Life.");
			m.setAdditional(code);
			return Response.ok(m).build();
		}
		return Response.serverError().entity(new Message(3,"This appears to be an incorrect activation code, please use the <a href=#register>registration form</a> above and we will send your activation code again.")).build();
		
      
    }
	@Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDataset() {
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/update").entity("Dataset updated").build();
      
    }
	@Path("remove")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response remove() {
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/remove").entity("Dataset removed").build();
      
    }
	
	@Path("get")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getDataset(String json) {
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/get").entity("Dataset retrieved").build();
      
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
