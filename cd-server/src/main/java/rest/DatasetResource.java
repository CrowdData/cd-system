package rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.JSONP;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import core.CreateHandler;
import core.RDFSerializer;
import core.NGHandler;
import core.Repository;
import core.ResourceProvider;
import core.SchemaProvider;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("datasets")
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
	String jsonld=RDFSerializer.getRDFStringFromModel(m,"JSON-LD");
	
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").entity(jsonld).build();
      
    }
	/*
	@Path("getDataSchema")
    @GET
    @Produces({"application/ld+json"})
    public Response getDatasetSchema() {
		String URI="http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl";
		String jsonld=SchemaProvider.getSchema(URI);
		
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").entity(jsonld).build();
      
    }*/
	
	@POST
	@Path("{datasetid}")
	@JSONP(queryParam="callback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getTemplate(@QueryParam("callback") String callback,@PathParam("datasetid") String ds, String jsonData ){
		if(ds==null){
			throw new NullPointerException("Provide target dataset ID 'ds");
		}
		else{
		JSONObject requestData=new JSONObject(jsonData);
		if(!requestData.has("resourceType")){
		throw new NullPointerException("Provide resourceType in object");
		}
		JSONObject response=new CreateHandler().handleCreate(ds, requestData.getString("resourceType"));
		return Response.ok().entity(response.toString()).build();	
		}
		
		
		
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
    public Response getDataset(@QueryParam("ds") String dsID) {
		
	String ds= NGHandler.getMetadataString(dsID);
	if(Repository.exists(dsID)){
	//	Repository.getQuery(query)
	}
	
		
		
		
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/get").entity("Dataset retrieved").build();
      
    }
	
	
}
