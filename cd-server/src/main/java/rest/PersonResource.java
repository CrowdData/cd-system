package rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import core.SchemaHandler;
import core.Tools;
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
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDataset(String jsonObject) {
		try{
		JSONObject obj=new JSONObject(jsonObject);
		// TODO: get key id from json;
		// TODO: get key jsonld
		// TODO: parse jsonld to model
		// TODO: store user in user dedicated repository?
		
		}
		catch(JSONException e){
			return Response.serverError().entity(new JSONObject("{\"error:\" \"Check JSON Syntax could not be parsed\"}").toString()).build();
		}
		return	Response.ok().entity("{\"user_uri\":\"uri/to/user/profile\"").build();
      
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
