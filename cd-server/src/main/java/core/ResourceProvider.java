package core;

import java.util.ArrayList;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class ResourceProvider {

	/*
	 * This method describes resource only within current graph's data
	 * 
	 * @param uri - Resource uri
	 */

	public Model getResource(String uri, String dsID) {
		String dataNG = NGHandler.getDataString(dsID);
		
		return ResultSetParser.parseDescribe(Queries.describeQuery(uri, dataNG),uri);  //the uri is passed as a hack to identify resource

	}

	/*
	 * This method searches for a resource in the whole dataset
	 * deprecated using Queries getDescribe now
	 * 
	 * @param uri - Resource uri
	 */
	public Model getResource(String uri) {
		/*
		ParameterizedSparqlString query = new ParameterizedSparqlString();
		query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", uri);

		System.out.println(query.toString());
		// query fuseki server
		ResultSet result = Repository.selectQuery(query.asQuery().toString());

		return ResultSetParser.parseDescribe(result, uri);*/
		
	return	Queries.getDescribe(uri, null);
		
		
	}
	
	
	
	public static Resource generateResource(String datasetID) {
	return ResourceFactory.createResource(generateResourceString(datasetID));
		
	}
	public static String generateResourceString(String ds){
		
	//	String data=NGHandler.getDataString(ds);
		String resource="nores";
		boolean exist=true;
		
		while(exist){			
		resource=NGHandler.getResourceString(ds, Tools.generateID());			
			ArrayList<Parameter> binds=new ArrayList<Parameter>();
			binds.add(new Parameter("resource",resource,null));
			exist=Queries.ask(null,Queries.ASK_EXISTS,binds);
		}
		return resource;
	}
	
	public static void main(String args[]){
		ResourceProvider.generateResourceString("datast");
	}
	}
	


