package rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.JSONP;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import core.ErrorMessage;
import core.Message;
import core.NGHandler;
import core.Prefixes;
import core.RDFSerializer;
import core.Repository;
import core.ResourceProvider;
import core.SchemaProvider;
import core.Tools;

@Path("tools")
public class ToolsResources {
int ACCEPTED=Response.Status.ACCEPTED.getStatusCode();
int CREATED=Response.Status.CREATED.getStatusCode();
int OK=Response.Status.OK.getStatusCode();

	
	@Path("get/schema")
	@GET
	@Produces({ "application/ld+json" })
	public Response getSchema(@QueryParam("resource") String url,
			@QueryParam("ds") String dsID) {
		if (url == null) {
			throw new NullPointerException("Class URI must be provided");
		}
		if(dsID==null){
			throw new NullPointerException("Dataset ID must be provided");
		}
		try {
			URL urldec = new URL(url);
		}// throws Malformed exception if wrong
		catch (MalformedURLException e) {

			return Response.serverError()
					.entity("resource parameter must be a valid URI").build();
		}
		String s = URLDecoder.decode(url);
		System.out.println("This after decode :"+s);
		Model output = ModelFactory.createDefaultModel();
		if (Prefixes.isBlacklisted(s)) {
			return Response.serverError().entity("This Class is too generic")
					.build();
		}
		
		output=SchemaProvider.getResourceSchema(s, dsID);
		String jsonld=RDFSerializer.getJSONLD(output, "JSON-LD");
		return Response
				.ok()
				.header("Target",
						"http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").entity(jsonld).build();

	}

	@Path("get/resource")
	@GET
	@Produces({"application/ld+json"})
	public Response getResource(@QueryParam("resource") String resourceURI,@QueryParam("ds") String dsID) {
		
		if(resourceURI==null){
		
			throw new 
			IllegalArgumentException("Resource URI 'resource' must be provided");
		}
		
		try {
			URL urldec = new URL(resourceURI);
		}// throws Malformed exception if wrong
		catch (MalformedURLException e) {

			throw new IllegalArgumentException("Resource must be valid URI");
		}
		
		String encodedURI=URLDecoder.decode(resourceURI);
	
		
		
		Model m;
		if(dsID==null){
			m=new ResourceProvider().getResource(encodedURI);
		}
		else{
			m=new ResourceProvider().getResource(encodedURI,dsID);
		}
		if(m.isEmpty()){
			return Response.status(Response.Status.NOT_FOUND).entity(String.format("The resource %s was not found",encodedURI)).build();
		}
		String jsonld=RDFSerializer.getJSONLD(m,"JSON-LD");
		return Response.ok(jsonld).build();
		
	}
	
		@Path("upload.turtle")
	    @POST
	    @Consumes({"text/turtle"})
		@Produces(MediaType.APPLICATION_JSON)
	    public Response addTurtle(@QueryParam("ds") String ds,String data) {
		addData(ds,data,"TTL");
		return Response.ok().entity(new Message(ACCEPTED,"Data added to"+NGHandler.getDataString(ds))).build();	
	
	    	
	}
		@Path("upload.ldjson")
	    @POST
	    @Consumes({"application/ld+json"})
		@Produces(MediaType.APPLICATION_JSON)
	    public Response addJson(@QueryParam("ds") String ds,String data) {
		addData(ds,data,"JSON-LD");
		return Response.ok().entity(new Message(ACCEPTED,"Data added to"+NGHandler.getDataString(ds))).build();	
	
	    	
	}
		@Path("upload.rdfxml")
	    @POST
	    @Consumes({"application/rdf+xml"})
		@Produces(MediaType.APPLICATION_JSON)
	    public Response addRDFXML(@QueryParam("ds") String ds,String data) {
		addData(ds,data,"RDF/XML");
		return Response.ok().entity(new Message(ACCEPTED,"Data added to"+NGHandler.getDataString(ds))).build();	
	
	    	
	}
		@Path("upload.rdfjson")
	    @POST
	    @Consumes({"application/rdf+json"})
		@Produces(MediaType.APPLICATION_JSON)
	    public Response addJsonRDF(@QueryParam("ds") String ds,String data) {
		addData(ds,data,"RDF/JSON");
		return Response.ok().entity(new Message(ACCEPTED,"Data added to"+NGHandler.getDataString(ds))).build();	
	    	
	}
		@Path("generate/id")
	    @GET
	  //  @JSONP (queryParam="callback")
		@Produces(MediaType.APPLICATION_JSON)
	    public Response generateResource(@QueryParam("ds") String ds) {
			if(ds==null){
				throw new NullPointerException("Provide target dataset ID 'ds' for the resource to be generated from");
			}
			else{
			String genRes=	ResourceProvider.generateResourceString(ds);
			JSONObject o=new JSONObject();
			o.put("id", genRes);
			return Response.ok().entity(o.toString()).build();	
			}
		
	
	
	    	
	}
		@Path("get/namedgraph")
	    @GET
		@Produces(MediaType.APPLICATION_JSON)
	    public Response getNamed(@QueryParam("ds") String ds) {
			if(ds==null){
				throw new NullPointerException("Provide target dataset ID 'ds'");
			}
			else{
			String genRes=	NGHandler.getDataString(ds);
			if(Repository.exists(genRes)){
			JSONObject o=new JSONObject();
			o.put("graph", genRes);
			return Response.ok().entity(o.toString()).build();	
			}
			else{
				throw new NullPointerException("This dataset is empty");
			}
			}
		
	
	
	    	
	}
		public static void addData(String ds,String data, String format){
			if(ds==null){
				throw new IllegalArgumentException("Dataset ID parameter 'ds' must be provided");
			}
			Model m=	RDFSerializer.inputToJSONLD(data, format);
			if(m.isEmpty()){
				throw new IllegalArgumentException("Could not parse data{"+data+"}");
			}
			Repository.addModel(m,NGHandler.getDataString(ds));
		}
	
		
		
	
		
/*
	public String getDataSetSchema(String prefix, String resource,
			String vocabulary) {
		Model m = Tools.getModel(vocabulary);
		Model resourceDesc = Tools.getResourceDescription(
				Prefixes.prefixes.get(prefix) + resource, m);

		return JSONLDSerializer.getJSONLD(resourceDesc);

	}
*/
}
