package core;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class ResultSetParser {

	
	
	
	/*
	 * This method is tied to Query.SELECT_DESCRIBE_QUERY
	 */
	public static Model parseDescribe(ResultSet result, String resourceURI) {

		Model resourceDescription = ModelFactory.createDefaultModel();
		while (result.hasNext()) {
			QuerySolution sol = result.next();
			// if first union graph
			if (sol.contains("p1")) {
				Statement s = ResourceFactory.createStatement(ResourceFactory
						.createResource(resourceURI), ResourceFactory
						.createProperty(sol.get("p1").toString()), sol
						.get("o1"));
				resourceDescription.add(s);
			}
			// if second union graph
			if (sol.contains("s2")) {
				Statement s = ResourceFactory.createStatement(sol
						.getResource("s2"), ResourceFactory.createProperty(sol
						.get("p2").toString()), ResourceFactory
						.createResource(resourceURI));
				resourceDescription.add(s);
			}
		}

		return resourceDescription;

	}
	
	/*
	 * Given result set and parameter of the expected resource this method returns
	 * List of resources as string - The method does not handle anonymous resources
	 */
	public static ArrayList<String> getResourceList(ResultSet result, String param){
		ArrayList<String> resources=new ArrayList<String>();
		while(result.hasNext()){
			QuerySolution sol=result.next();
			if(sol.contains(param)){
				RDFNode n=sol.get(param);
				if(n.isURIResource()){
					resources.add(n.asResource().getURI());
					
				}
				else{
					throw new IllegalStateException("The returned resource was a BNODE label of it="+n.asResource().getId().getLabelString());
				}
				
			}
			
			
			
			
			
		}
		return resources;
		
		
	}
	/*
	 * Given result set and list of parameters of the expected resource this method returns
	 * List of Hashmaps of binded values as RDFNode - The method does not handle anonymous resources
	 */
	public static ArrayList<HashMap<String,RDFNode>> getBindings(ResultSet result, ArrayList<String> params){
		ArrayList<HashMap<String,RDFNode>> resources=new ArrayList<HashMap<String,RDFNode>>();
		
		while(result.hasNext()){
			QuerySolution sol=result.next();
			HashMap<String,RDFNode> values=new HashMap<String,RDFNode>();
			for(String param :params){
		
			if(sol.contains(param)){
			
				values.put(param,sol.get(param));
				
			}
			else{
				values.put(param, null);
			}
			
			}
			resources.add(values);
			
			
			
			
		}
		return resources;
		
		
	}
	
	
	
	
}
