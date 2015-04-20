package rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.JSONP;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import core.CreateHandler;
import core.Parameter;
import core.Queries;
import core.RDFSerializer;
import core.NGHandler;
import core.Repository;
import core.ResourceProvider;
import core.SchemaProvider;
import core.Tools;
import core.XSDDateTime;

/**
 * dataset Resource class 
 */
@Path("datasets")
public class DatasetResource {
    /**
     * Method returning void ontology in JSON-LD format 
     * @return JSON-LD representation of Void ontology
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
	/**
	 * Method handling POST request to retrieve data for client to create resource
	 * @param callback - for jsonp handling
	 * @param ds - datasetid where the resource data from the client will go to
	 * @param jsonData - additional information "resourceType" is class associated with the requested RDFform template
	 * @return JSON representation containing datasetpath,datasetid,initialized rdf/json graph and resourceid
	 */
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
	/**
	 * Method handling creation of resource
	 * @param callback
	 * @param ds
	 * @param jsonData  
	 * @param uriInfo   - will be used for provenance data
	 * @param request   - will be used for provenance data
	 * @return
	 */
	@Path("{datasetid}/create")
    @POST
    @JSONP(queryParam="callback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response createResource(@QueryParam("callback") String callback,@PathParam("datasetid") String ds, String jsonData, @Context UriInfo uriInfo, @Context Request request) {
		JSONObject reqJSON=new JSONObject(jsonData);
		System.out.println(reqJSON.toString());
		JSONObject response=new JSONObject();
		response.put("absolutePath",uriInfo.getAbsolutePath().toString());
		response.put("request", reqJSON);
		URI loc;
		
		
		
		String graph=reqJSON.getJSONObject("graph").toString();	
		Model m=	RDFSerializer.inputToRDFType(graph, "RDF/JSON");
		if(m.isEmpty()){
			throw new IllegalArgumentException("Could not parse data{"+graph+"}");
		}
		
		
		
		
		String resourceURI=reqJSON.getString("resourceURI");
		Resource r= m.getResource(resourceURI);
		
		//generic for every resource
		Calendar sent=Calendar.getInstance();
		sent.setTimeInMillis(reqJSON.getLong("timeSent"));
		
		r.addProperty(ResourceFactory.createProperty("http://rdfs.org/sioc/ns#created_at"),XSDDateTime.getDateTime(sent),XSDDatatype.XSDdateTime);
		r.addProperty(ResourceFactory.createProperty("http://www.w3.org/ns/prov#wasAttributedTo"), ResourceFactory.createResource(NGHandler.getResourceString("users", reqJSON.getString("userid"))));
		
		
		//Process id
		
		
		
		String templateID=reqJSON.getString("templateID");
		//TODO: process add remove triples based on templateid, datasetid 
		if(reqJSON.has("questionID")){
			Resource question=m.getResource(reqJSON.getString("questionID"));
			question.addProperty(ResourceFactory.createProperty("http://rdfs.org/sioc/ns#has_reply"),r);				
			r.addProperty(ResourceFactory.createProperty("http://rdfs.org/sioc/ns#reply_of"),question);	
		}
		
		//renameBlankNodesIf exist to resourceURI
		Tools.renameBlankNodes(m);
		
		//add it to repository
		Repository.addModel(m,NGHandler.getDataString(ds));
		
		//m.listResourcesWithProperty(null);
		
		
		try {
			loc = new URI("http://crowddata.abdn.ac.uk/resources/"+reqJSON.getString("resourceURI"));
			return Response.created(loc).entity(response.toString()).build();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.notModified().entity(response).build();
		
      
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
