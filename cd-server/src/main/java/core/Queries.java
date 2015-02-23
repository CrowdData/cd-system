package core;

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

public class Queries {

	
	static String TEMPLATE_SELECT_RESTRICTIONS="SELECT ?restrictionProperty ?exactly ?min ?max \r\n" + 
			"WHERE{\r\n" + 
			"?resource rdfs:subClassOf ?restriction .\r\n" + 
			"?restriction owl:onProperty ?restrictionProperty .\r\n" + 
			"OPTIONAL{?restriction owl:cardinality ?exactly .}\r\n" + 
			"OPTIONAL{?restriction owl:cardinality ?min .}\r\n" + 
			"OPTIONAL{?restriction owl:cardinality ?max .}\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"";
	
	static String DESCRIBE=""
				+ "DESCRIBE ?resource "
				+ "   ";
	
	static String SELECT_RESOURCE=
			"SELECT ?res\r\n" + 
			"WHERE{\r\n" + 
			"GRAPH ?graph{\r\n" + 
			"?res ?prop ?obj .\r\n" + 
			"}}\r\n" + 
			"";
	
	
	static String SELECT_DESCRIBE_QUERY=
	"SELECT ?p1 ?o1 ?s2 ?p2  WHERE {{ GRAPH ?graph {" + 
			" " + 
			"?resource ?p1 ?o1 .} " + 
			"} " + 
			"UNION " + 
			"{GRAPH ?graph { " + 
			"" + 
			"?s2 ?p2 ?resource .}" + 
			"}}" + 
			"" + 
			"";
	
	





public static ResultSet describeQuery(String resource, String DS){
	ParameterizedSparqlString query = new ParameterizedSparqlString();
	query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", resource);				//tied to SELECT DESCRIBE QUERY
		query.setIri("graph", DS);   			//tied to SELECT DESCRIBE QUERY

		System.out.println(query.toString());
	return Repository.selectQuery(query.asQuery().toString());
	
	
	
}


public static Model getDescribeFromModel(String resourceURI, Model m){
	Resource res=ResourceFactory.createResource(resourceURI);
	Model out=ModelFactory.createDefaultModel();
	ParameterizedSparqlString query=new ParameterizedSparqlString();
 	query.setCommandText(Queries.DESCRIBE);
 	 query.setParam("resource",res);
 	 
 //	 query.setNsPrefixes(Prefixes.prefixes);
 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
 	qExec.execDescribe(out);
 	return out;
	
}



/*
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

	

}*/

}
