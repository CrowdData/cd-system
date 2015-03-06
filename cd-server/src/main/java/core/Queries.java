package core;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.RDFDatatype;
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
	//to get bindings for root element of parsing
	static String[]TEMPLATE_SELECT_RESTRICTIONS_BINDINGS={"resource","resourceRestriction","restrictionProperty","exactly","min","max"};
	static String[] TEMPLATE_SELECT_COMMENTS={"description", "label"};
	//classType,resource
	static String TEMPLATE_SELECT_LABEL_COMMENT="SELECT * \r\n" + 
			"WHERE{\r\n" + 
			"?b0 a ?classType .\r\n" + 
			"?b0 void:class ?resource .\r\n" + 
			"?b0 rdfs:label ?label .\r\n" + 
			"?b0 dcterms:description ?description .\r\n" + 
			"OPTIONAL{?b0 void:property ?property .}\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"";
	static String TEMPLATE_SELECT_LABEL_COMMENT_PROPERTY="SELECT * \r\n" + 
			"WHERE{\r\n" + 
			"?b0 a ?classType .\r\n" + 
			"?b0 void:class ?resource .\r\n" + 
			"?b0 rdfs:label ?label .\r\n" + 
			"?b0 dcterms:description ?description .\r\n" + 
			"?b0 void:property ?property .\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"";
	
	static String TEMPLATE_ASK_CHOICE="ASK \r\n" + 
			"WHERE{\r\n" + 
			"?b0 a ?classType .\r\n" + 
			"?b0 void:class ?resource .\r\n" + 
			"?b0 void:property ?property .\r\n" + 
			"?b0 cd:isChoice ?choice .\r\n" + 
			"OPTIONAL{?b0 cd:sparqlQuery ?query .} \r\n" + 
			"OPTIONAL{?b0 cd:sparqlEndpoint ?endpoint .} \r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"";
	
	static String TEMPLATE_SELECT_QUERY_ENDPOINT="SELECT ?query ?endpoint \r\n" + 
			"WHERE{\r\n" + 
			"?b0 void:class ?resource .\r\n" + 
			"?b0 void:property ?property .\r\n" + 
			"?b0 cd:sparqlQuery ?query .} \r\n" + 
			"?b0 cd:sparqlEndpoint ?endpoint .} \r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"";
	
	
	
	static String SELECT_EVERYTHING="SELECT * \r\n" + 
			"WHERE{\r\n" + 
			"?s ?p ?o .\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"";
	static String TEMPLATE_SELECT_RESTRICTIONS="SELECT * \r\n" + 
			"WHERE{\r\n" + 
			"?resource rdfs:subClassOf ?restriction .\r\n" + 
			"?restriction owl:onProperty ?restrictionProperty .\r\n" + 
			"OPTIONAL{?restriction owl:cardinality ?exactly .}\r\n" + 
			"OPTIONAL{?restriction owl:minCardinality ?min .}\r\n" + 
			"OPTIONAL{?restriction owl:maxCardinality ?max .}\r\n" + 
			"OPTIONAL{?restriction owl:allValuesFrom ?resourceRestriction .}" +
			"}\r\n" + 
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
	
	static String ASK_EXISTS="ASK {?resource ?p ?o .}";
	
	
	static String getBuses="PREFIX dc: <http://purl.org/dc/elements/1.1/> "+
            "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/> "+
			"SELECT ?label ?value " + 
			"               WHERE " + 
			"               { " + 
			"               ?value a naptan:BusStop ; " + 
			"               dc:identifier ?label; " + 
			"               }";




/*
 * Query fuseki endpoint for specific resource only triples 
 * which specifically contain resource as subject or object are returned.
 */
public static ResultSet describeQuery(String resource, String DS){
	ParameterizedSparqlString query = new ParameterizedSparqlString();
	
	query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", resource);				//tied to SELECT DESCRIBE QUERY
		query.setIri("graph", DS);   			//tied to SELECT DESCRIBE QUERY

		System.out.println(query.toString());
	return Repository.selectQuery(query.asQuery().toString());
	
	
	
}


/*
 * Original describe query, describing resource from the Model 
 */
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
public static boolean ask(Model m,String queryString,ArrayList<Parameter>params){
	ParameterizedSparqlString query=new ParameterizedSparqlString();
 	query.setCommandText(queryString);
 	populateQuery(query,params);
 	if(m!=null){
 		QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
 	 	return qExec.execAsk();
 	}
 	
 	return Repository.askQuery(query.asQuery().toString());
}

public static void populateQuery(ParameterizedSparqlString query,ArrayList<Parameter> params){
	for(Parameter triplet: params){
	 	//datatype exist assume it's literal
	 		if(triplet.type!=null){
	 		query.setLiteral(triplet.bind,triplet.value,triplet.type);
	 	}
	 		//otherwise its a resource or uri
	 		else{	
	 		query.setIri(triplet.bind, triplet.value);	 		
	 	}
	 	}
	 	 
	 	 query.setNsPrefixes(Prefixes.prefixes);
}
public static ResultSet selectQueryModel(Model m,String queryTemplate,ArrayList<Parameter>params){
	ParameterizedSparqlString query=new ParameterizedSparqlString();
 	query.setCommandText(queryTemplate);	 	
 	
 	populateQuery(query,params);
 	//if no model query goes to FUSEKI TDB endpoint
 	 if(m==null){
 		return Repository.selectQuery(query.asQuery().toString());
 	}
 	 //query model
 	 else{
 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
 	return qExec.execSelect();
 	 }

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
