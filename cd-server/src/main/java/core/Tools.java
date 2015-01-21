package core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class Tools {

	
	
	public static Model getModel(String url){
		Model m=ModelFactory.createDefaultModel();
		m.read(url);
		return m;
		//String jsonld=serialize.getJSONLD(m);
	}
	
	
	
	public static Model getResourceDescription(String resourceURI,Model m){
		Resource res=ResourceFactory.createResource(resourceURI);
		System.out.println(resourceURI);
		Model out=ModelFactory.createDefaultModel();
		
	 		ParameterizedSparqlString query=new ParameterizedSparqlString();
	 	query.setCommandText( ""
	 				+ "SELECT ?p ?o "
	 				+ "WHERE {"
	 				+ " ?resource ?p ?o ."
	 				+ "   }");
	 	 query.setParam("resource",res);
	 		 query.setNsPrefixes(Prefixes.prefixes);
	 		
	 		System.out.println(query.asQuery().toString());
	 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
	 		   
	 	ResultSet rs = qExec.execSelect()	 			;
	 		     try {
	 		        while(rs.hasNext()){
	 		      
	 		        	QuerySolution cap=rs.next();
	 		        Statement s=	ResourceFactory.createStatement(res, ResourceFactory.createProperty(cap.get("p").toString()), cap.get("o"));
	 		        	out.add(s);
	 		        	
	 		        }
	 		       
	 		     }
	 		  
	 		        catch(Exception e){
	 		        	e.printStackTrace();
	 		        
	 		        }		     
	 		      finally { qExec.close();}
	 		    return out;

		
	
}
		
		
		
		
		
		
		
		
	}
	
	
