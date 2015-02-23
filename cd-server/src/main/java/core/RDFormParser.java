package core;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RDFormParser {

	
	

	
	
	/*
	 * Resource must be root?
	 */
	public static void initParser(String ds,String resource){
		
		//Create json object with root resource
		JSONObject main=getSkeleton(resource);
		// Model get KA and schema ontologies for ds
		Model base=loadSchemas(ds);
		
		
		
			
		
		
		
	}
	
	/*
	 * Creates skeleton for RDFTemplate
	 */
	private static JSONObject getSkeleton(String root){
		JSONObject response=new JSONObject();
		response.put("root", root);
		response.put("templates", new JSONArray());
		return response;
	}
	
	
		
		
public static Model loadSchemas(String ds){
		
		Model base=ModelFactory.createDefaultModel();
		base.read("http://localhost/ontologies/user-ka.ttl");
		base.read("http://localhost/ontologies/user-schema.ttl");
		base.write(System.out,"TTL");
		
		
		
		
		return base;
		
	}
	
	
	
	public static ResultSet selectQueryModel(Model m,String queryTemplate,ArrayList<Triplet<String,String,RDFDatatype>>params){
		ParameterizedSparqlString query=new ParameterizedSparqlString();
	 	query.setCommandText(queryTemplate);	 	
	 	
	 	for(Triplet<String,String,RDFDatatype> triplet: params){
	 	//datatype exist assume it's literal
	 		if(triplet.getDatatype()!=null){
	 		query.setLiteral(triplet.getBind(),triplet.getObject(),triplet.getDatatype());
	 	}
	 		//otherwise its a resource or uri
	 		else{	
	 		query.setIri(triplet.getBind(), triplet.getObject());	 		
	 	}
	 	}
	 	 
	 	 query.setNsPrefixes(Prefixes.prefixes);
	 	//if no model query goes to FUSEKI TDB endpoint
	 	
	 	 
	 	 if(m==null){
	 		return Repository.selectQuery(query.asQuery().toString());
	 	}
	 	 else{
	 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
	 	return qExec.execSelect();
	 	 }
	
	}
	public static void main (String args[]){

		

	}
	
	
	
	
	
}
