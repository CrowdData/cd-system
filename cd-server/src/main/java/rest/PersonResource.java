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

import core.DatasetOperations;
import core.SchemaHandler;
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
		String URI="http://crowddata.abdn.ac.uk/descriptions/person.ttl";
		
		String jsonld=SchemaHandler.getSchema(URI);
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").entity(jsonld).build();
      
    }

	@Path("create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDataset() {
	URI u=null;
		try {
	u=	new URI("http://crowddata.abdn.ac.uk:8080/crowddata/dataset");
	} catch (URISyntaxException e) {
		e.printStackTrace();
	}
		return	Response.created(u).header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/create").entity("Resource Created").build();
      
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
