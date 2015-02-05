package core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
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
		String URI="http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl"
		
		/*
		Model m=Tools.getModel("http://vocab.deri.ie/void");
		
		System.out.println("datasetDesc");
		
		//OntModel ontM=ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM_RDFS_INF, m);
		Model resourceDesc=Tools.getDomainResources(Prefixes.prefixes.get("void")+"Dataset", m);
		Model resourceDesc1=Tools.getResourceDescription(Prefixes.prefixes.get("void")+"Dataset", m);
		resourceDesc.add(resourceDesc1);
		//get dataset class description and add it to the model
		Model s=Tools.getModel("http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl");
		resourceDesc.add(s);
		*/
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
		
		
	}
	public static void main(String args[]){
		System.out.println(DatasetOperations.getDataSetSchema());
	}

		
	
	

	
	
}
