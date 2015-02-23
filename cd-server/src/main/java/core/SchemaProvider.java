package core;

import java.util.ArrayList;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class SchemaProvider {

	
public static void main(String args[]){
	
	new SchemaProvider().getSchema("http://xmlns.com/foaf/0.1/Agent", "user");
	
}

public static Model getSchema(String URI, String dsID){
	//get namespace to read ontology	
		String namespace=Tools.getNamespace(URI);
		System.out.println("Namespace:"+namespace);
		
		Model schema=ModelFactory.createDefaultModel();
		try{
		schema.read(namespace);
		}
		catch(Exception e){
			IllegalArgumentException ex=new IllegalArgumentException("Something went wrong when reading external ontology");
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
		System.out.println("Namespace to read schema:"+namespace);
		if(schema.isEmpty()){
			throw new NullPointerException("The ontology does not exist...");
		}
		
		Model describedClass=Queries.getDescribeFromModel(URI, schema);
		if(describedClass.isEmpty()){
			throw new NullPointerException("The class does not exist...");
		}
						
		//Check for additional schema
		Model schemaM=getSchemaDS(URI,dsID);
		//Check for KA data
		Model kaM=getKA(URI,dsID);
	
		
		//add custom defined schema + what resources for describing class in context of dataset
		describedClass.add(schemaM).add(kaM);
		describedClass.write(System.out,"TTL");
		
		return describedClass;

		
}

private static Model getSchemaDS(String URI, String dsID){
	//create schemas named graph URI
	String schemaNG = NGHandler.getSchemaString(dsID);
	
	/*
	return ResultSetParser.parseDescribe(Queries.describeQuery(URI, schemaNG), URI);
	*/
	System.out.println(String.format("Contains Model %s %s",schemaNG,Repository.exists(schemaNG)));
	
	Model schemaM=Repository.getModel(schemaNG);
	return Queries.getDescribeFromModel(URI, schemaM);
	//Consider return whole schema to avoid complex querying or return whole schema and use describe argument

}

private static Model getKA(String URI, String dsID){
	String kaNG=NGHandler.getKAString(dsID);
	
	ParameterizedSparqlString query = new ParameterizedSparqlString();
	query.setCommandText(Queries.SELECT_RESOURCE);
	
	query.setIri("graph", kaNG);
	query.setIri("prop", Prefixes.prefixes.get("void")+"class");   //may change for now, ugly workaround
	query.setIri("obj", URI);
	System.out.println(query.asQuery().toString());
	ResultSet result= Repository.selectQuery(query.asQuery().toString());
	ArrayList<String> resources=ResultSetParser.getResourceList(result, "res");
	
	Model m=ModelFactory.createDefaultModel();
	for(int i=0; i<resources.size();i++){
		m.add(ResultSetParser.parseDescribe(Queries.describeQuery(resources.get(i), kaNG),resources.get(i)));
	}
	
	
	return m;
	
}

}




	/*
	
	public static String getSchema(String URI){
		
		Model schema=ModelFactory.createDefaultModel();
		
		Model dataModel=getDataModel(URI);
		
		Vector<OntModel> imports=getImports(dataModel);
		
		//TODO: Add selection of resources and properties from imports ?
	
		schema.add(dataModel);
		for(OntModel o: imports){
			schema.add(o);
		}
		String output=JSONLDSerializer.getJSONLD(schema);
		
		return null;
		
	}
		*/
		
		
	
	
	
	
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
	
