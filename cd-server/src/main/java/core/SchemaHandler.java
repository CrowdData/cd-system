package core;

import java.util.HashMap;
import java.util.Vector;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SchemaHandler {

private static HashMap<String,String> dataModelCache;	
	
	static{
		dataModelCache=new HashMap<String,String>();
		dataModelCache.put("http://xmlns.com/foaf/0.1/Person", "http://crowddata.abdn.ac.uk/descriptions/person.ttl");
		dataModelCache.put("http://rdfs.org/ns/void#Dataset", "http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl");
	}
	
public void addDataModel(String uri, String location){
	dataModelCache.put(uri, location);
	
}
private String getDataModel(String URI){
	
	if(dataModelCache.containsKey(URI)){
		return dataModelCache.get(URI);
		
	}
	return null;
	
}



	
	
	public static String getSchema(String URI){
		/*
		Model schema=ModelFactory.createDefaultModel();
		
		Model dataModel=getDataModel(URI);
		
		Vector<OntModel> imports=getImports(dataModel);
		
		//TODO: Add selection of resources and properties from imports ?
	
		schema.add(dataModel);
		for(OntModel o: imports){
			schema.add(o);
		}
		String output=JSONLDSerializer.getJSONLD(schema);
		*/
		return null;
		
	}
		
		
		
	}
	
	
	
	/*
	private static Model getDataModel(String dataModel){
		
		Model m=Tools.getModel(dataModel);
		if(m.size()>0){
			return m;
		}
		throw new NullPointerException(String.format("%s data model does not exist",dataModel));
		
	}
	private static Vector<OntModel> getImports(Model m){
		Vector<OntModel> imports=new Vector<OntModel>();
		//create ontology from data model to get imports
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, m);
		model.write(System.out,"TTL");
		ExtendedIterator<OntModel> it=model.listSubModels(true);
		while(it.hasNext()){
			imports.add(it.next());
		}
		System.out.println("The imports size:"+imports.size());
	return imports;
		
	}
	
	public static void main (String[] args){
		SchemaHandler.getDataModel("yaayy");
		
	}*/
	
