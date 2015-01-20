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
	
	
}
