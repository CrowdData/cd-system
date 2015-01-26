package core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;



public class DatasetOperations {

	
	
	
	
	public void update(JSONObject response){
		
		 String inputdata=response.getString("jsonld");
		InputStream stream = new ByteArrayInputStream(inputdata.getBytes(StandardCharsets.UTF_8));		   
		   Model temp=ModelFactory.createDefaultModel();
		   temp.read(stream,null,"JSON-LD");
		
		
		
	}
	
	public static String getDataSetSchema(){
		Model m=Tools.getModel("http://vocab.deri.ie/void");
		Model resourceDesc=Tools.getResourceDescription(Prefixes.prefixes.get("void")+"Dataset", m);
		
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
		
		
	}

		
	
	

	
	
}
