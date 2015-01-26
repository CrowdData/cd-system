package rest;

import java.net.URLDecoder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import core.JSONLDSerializer;
import core.Prefixes;
import core.Tools;
@Path("tools")
public class ToolsResources {

	
	
	
	
	@Path("getjsonld")
    @GET
    @Produces({"application/ld+json"})
    public Response getVoid(@QueryParam("url") String url) {
		String s=URLDecoder.decode(url);
		System.out.println(s);
		
		
		
		
	Model m=ModelFactory.createDefaultModel();
	m.read(s);
	String jsonld=JSONLDSerializer.getJSONLD(m);
	
	return	Response.ok().header("Target","http://crowddata.abdn.ac.uk:8080/crowddata/dataset/getDataSchema").header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT").entity("JSONLD:"+jsonld).build();
      
    }
	
	public String getDataSetSchema(String prefix,String resource, String vocabulary){
		Model m=Tools.getModel(vocabulary);
		Model resourceDesc=Tools.getResourceDescription(Prefixes.prefixes.get(prefix)+resource, m);
		
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
	}	
	
}
