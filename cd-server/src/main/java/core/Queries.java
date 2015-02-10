package core;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.ResultSet;

public class Queries {

	
	
	String SELE_TEMPLATE_QUERY="DESCRIBE ?concept FROM NAMED ?graph WHERE {?s ?p ?o}";
	
	
	String DESCRIBE_USER_QUERY="DESCRIBE ?user FROM NAMED ?graph WHERE {?user dc:identifier ?id}";
	
	String SELECT_EVERYTING="SELECT * FROM NAMED ?graph";
	
	
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


}
