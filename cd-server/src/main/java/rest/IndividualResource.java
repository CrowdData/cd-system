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
import com.hp.hpl.jena.rdf.model.Property;
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
import core.UpdateHandler;
import core.XSDDateTime;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("resources")
public class IndividualResource {
  
	@POST
	@JSONP(queryParam="callback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getResourceTemplate(@QueryParam("callback") String callback,String json){
	JSONObject j=new JSONObject(json);
		JSONObject response=new UpdateHandler().handleUpdate(j.getString("resourceid"));
		return Response.ok().entity(response.toString()).build();	
		}
		
		
	@POST
	@Path("update")
	@JSONP(queryParam="callback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateResource(@QueryParam("callback") String callback,String jsonBody){
		JSONObject update=new JSONObject(jsonBody);
		Model m=	RDFSerializer.inputToRDFType(update.getJSONObject("graph").toString(), "RDF/JSON");
		
		//createProvenance() wasDerivedFrom remove derived from from previous resource
		Resource r=m.getResource(update.getString("resourceURI"));		
		Property wasDerivedFrom=ResourceFactory.createProperty("http://www.w3.org/ns/prov#wasDerivedFrom");
		r.removeAll(wasDerivedFrom);
		r.addProperty(wasDerivedFrom, ResourceFactory.createResource(update.getString("previousURI")));
		//set new user making update
		Calendar sent=Calendar.getInstance();
		sent.setTimeInMillis(update.getLong("timeSent"));
		r.removeAll(ResourceFactory.createProperty("http://rdfs.org/sioc/ns#created_at"));
		r.addProperty(ResourceFactory.createProperty("http://rdfs.org/sioc/ns#created_at"),XSDDateTime.getDateTime(sent),XSDDatatype.XSDdateTime);
		
		
		if(update.has("userid")){
		r.removeAll(ResourceFactory.createProperty("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/userid"));
		r.addProperty(ResourceFactory.createProperty("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/userid"), update.getString("userid"));			
		}
		Repository.addModel(m,update.getString("datasetPath"));
		
		return Response.ok().entity("{\"updated\":\"OK\"}").build();	
		}
		
	 
	
	
	
	
}
