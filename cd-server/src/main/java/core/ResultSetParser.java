package core;

import java.util.ArrayList;

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
	
	
	
	
}
