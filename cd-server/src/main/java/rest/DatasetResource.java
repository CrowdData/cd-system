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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import core.JSONLDSerializer;
import core.SchemaHandler;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("dataset")
public class DatasetResource {
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@Path("getvoid")
    @GET
    @Produces({"application/ld+json"})
    public Response getVoid() {
	Model m=ModelFactory.createDefaultModel();
	m.read("http://vocab.deri.ie/void");
	String jsonld=JSONLDSerializer.getJSONLD(m);
	
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").entity(jsonld).build();
      
    }
	@Path("getDataSchema")
    @GET
    @Produces({"application/ld+json"})
    public Response getDatasetSchema() {
		String URI="http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl";
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
    public Response getDataset() {
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/get").entity("Dataset retrieved").build();
      
    }
	
	
}
