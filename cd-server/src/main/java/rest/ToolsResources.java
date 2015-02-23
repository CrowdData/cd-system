package rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import core.Prefixes;
import core.RDFSerializer;
import core.Repository;
import core.ResourceProvider;
import core.SchemaProvider;
import core.Tools;

@Path("tools")
public class ToolsResources {

	@Path("getschema")
	@GET
	@Produces({ "application/ld+json" })
	public Response getSchema(@QueryParam("url") String url,
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
		
		output=SchemaProvider.getSchema(s, dsID);
		String jsonld=RDFSerializer.getJSONLD(output, "JSON-LD");
		return Response
				.ok()
				.header("Target",
						"http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"GET, POST, DELETE, PUT").entity(jsonld).build();

	}

	@Path("get")
	@GET
	@Produces({"application/ld+json"})
	public Response getResource(@QueryParam("resource") String resourceURI,@QueryParam("ds") String dsID) {
		
		if(resourceURI==null){
		
			throw new 
			IllegalArgumentException("Resource URI must be provided");
		}
		
		try {
			URL urldec = new URL(resourceURI);
		}// throws Malformed exception if wrong
		catch (MalformedURLException e) {

			return Response.serverError()
					.entity("resource parameter must be a valid URI").build();
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
	
		@Path("add")
	    @POST
	    @Consumes({"text/turtle"})
		@Produces({"application/ld+json"})
	    public Response addTurtle(@QueryParam("ds") String ds,String data) {
		
		Model m=	RDFSerializer.inputToJSONLD(data, "TTL");
		return Response.ok().entity(RDFSerializer.getJSONLD(m, "JSON-LD")).build();	
	
	    	
	}
		@Path("add")
	    @POST
	    @Consumes({"application/ld+json"})
		@Produces({"application/ld+json"})
	    public Response addJson(@QueryParam("ds") String ds,String data) {
		
		Model m=	RDFSerializer.inputToJSONLD(data, "TTL");
		return Response.ok().entity(RDFSerializer.getJSONLD(m, "JSON-LD")).build();	
	
	    	
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
