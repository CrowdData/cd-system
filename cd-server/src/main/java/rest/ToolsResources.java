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

import core.JSONLDSerializer;
import core.Prefixes;
import core.Repository;
import core.ResourceProvider;
import core.Tools;
@Path("tools")
public class ToolsResources {

	
	
	
	
	@Path("getjsonld")
    @GET
    @Produces({"application/ld+json"})
    public Response getVoid(@QueryParam("url") String url) {
		String s=URLDecoder.decode(url);
		System.out.println(s);
		
		Model output=ModelFactory.createDefaultModel();
		if(Prefixes.isBlacklisted(url)){
			return Response.noContent().entity("BlackListed").build();
		}
		
			
	String namespace=Tools.getNamespace(url);
	System.out.println("Namespace:"+namespace);
	
	Model schema=ModelFactory.createDefaultModel();
	schema.read(namespace);
	Model describeOutput=Tools.getDescribeFromModel(url, schema);
	//Check for additional schema?
	//Check for KA location

	
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").entity("JSONLD:").build();
      
    }
	
	
	@Path("get")
	@GET
	@Produces({"application/ld+json"})
	public Response getResource(@QueryParam("resource") String resourceURI,@QueryParam("ds") String dsID){
		
		if(resourceURI==null){
		
			throw new NullPointerException("Resource URI must be provided");
		}
		
		String encodedURI=URLDecoder.decode(resourceURI);
		try{
		URL url=new URL(encodedURI);
		}//throws Malformed exception if wrong
		catch(MalformedURLException e){
			
			return Response.serverError().entity("resource parameter must be a valid URI").build();
		}
		
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
		String jsonld=JSONLDSerializer.getJSONLD(m);
		return Response.ok(jsonld).build();
		
		
		
	}
	
	
	public String getDataSetSchema(String prefix,String resource, String vocabulary){
		Model m=Tools.getModel(vocabulary);
		Model resourceDesc=Tools.getResourceDescription(Prefixes.prefixes.get(prefix)+resource, m);
		
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
	}	
	
	
	
}
