package core;

import java.util.Vector;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class SchemaHandler {

	
	
	
	
	
	
	public static String getSchema(String dataModelString){
		Model schema=ModelFactory.createDefaultModel();
		
		Model dataModel=getDataModel(dataModelString);
		
		Vector<OntModel> imports=getImports(dataModel);
		
		//TODO: Add selection of resources and properties from imports ?
	
		schema.add(dataModel);
		for(OntModel o: imports){
			schema.add(o);
		}
		String output=JSONLDSerializer.getJSONLD(schema);
		
		return output;
		
		
		
		
		
	}
	
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
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM, m);
		ExtendedIterator<OntModel> it=model.listSubModels(true);
		while(it.hasNext()){
			imports.add(it.next());
		}
		
	return imports;
		
	}
	
	public static void main (String[] args){
		SchemaHandler.getDataModel("yaayy");
		
	}
	
}
