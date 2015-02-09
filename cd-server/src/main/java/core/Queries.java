package core;

public class Queries {

	
	
	String SELE_TEMPLATE_QUERY="DESCRIBE ?concept FROM NAMED ?graph WHERE {?s ?p ?o}";
	
	
	String DESCRIBE_USER_QUERY="DESCRIBE ?user FROM NAMED ?graph WHERE {?user dc:identifier ?id}";
	
	String SELECT_EVERYTING="SELECT * FROM NAMED ?graph";
	
	
	static String SELECT_DESCRIBE_QUERY="SELECT ?p1 ?o1 ?s2 ?p2  WHERE {{ GRAPH ?graph {" + 
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
	
	
}
