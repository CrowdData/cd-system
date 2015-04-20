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
import com.hp.hpl.jena.update.UpdateAction;
/**
 * This class holds SPARQL Queries and methods required for creating,updating and retrieving data from Jena Models and our datastore.
 * @author Stanislav Beran
 *
 */
public class Queries {
	//to get bindings for root element of parsing
	/**
	 * Used by RDFormParser
	 * @deprecated
	 * 
	 */
	static String[]TEMPLATE_SELECT_RESTRICTIONS_BINDINGS={"resource","resourceRestriction","restrictionProperty","exactly","min","max"};
	/**
	 * Used by NewFormParser.java
	 */
	static String[] TEMPLATE_SELECT_COMMENTS={"description", "label"};
	/**
	 * Query used by NewFormParser/CopyOfNewFormParser to get descripiton and label of the class
	 * for RDForm templates.
	 */
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
	/**
	 * Query used by NewFormParser/CopyOfNewFormParser to get descripiton and label of the property associated with a class
	 * for RDForm templates.
	 */
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
	/**
	 * Not tested
	 * Query to ask if choice exist for a particular property 
	 */
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
	/**
	 * Query to get SPARQL query and endpoint where (label,value) choices are extracted from
	 */
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
	/**
	 * @deprecated
	 * Used with old RDFormParser to determine restriction of a resource
	 */
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
	
	/**
	 * Trivial imitiation of DESCRIBE QUERY selects triples where resource is either subject or object
	 */
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
	/**
	 * Not used 
	 */
	static String ASK_EXISTS="ASK {?resource ?p ?o .}";
	
	/**
	 * Query to get choice label and uri of buses
	 */
	static String getBuses="PREFIX dc: <http://purl.org/dc/elements/1.1/> "+
            "PREFIX naptan: <http://transport.data.gov.uk/def/naptan/> "+
			"SELECT ?label ?value " + 
			"               WHERE " + 
			"               { " + 
			"               ?value a naptan:BusStop ; " + 
			"               dc:identifier ?label; " + 
			"               }";




/**
 *  Query datastore for specific resource only triples 
 * which specifically contain resource as subject or object are returned.
 * @param resource - IRI
 * @param DS   - named graph of the dataset to be queried
 * @return - ResultSet 
 */
public static ResultSet describeQuery(String resource, String DS){
	ParameterizedSparqlString query = new ParameterizedSparqlString();
	
	query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", resource);				//tied to SELECT DESCRIBE QUERY
		query.setIri("graph", DS);   			//tied to SELECT DESCRIBE QUERY

		System.out.println(query.toString());
	return Repository.selectQuery(query.asQuery().toString());
	
	
	
}


/**
 * This method returns Model of a resourceURI, if Model m is null it queries the whole datastore.(all named graphs)
 * @param resourceURI - IRI to be described
 * @param m	- Model to be queried
 * @return - Model representation of the described IRI
 */
public static Model getDescribe(String resourceURI, Model m){
	Resource res=ResourceFactory.createResource(resourceURI);
	Model out=ModelFactory.createDefaultModel();
	ParameterizedSparqlString query=new ParameterizedSparqlString();
 	query.setCommandText(Queries.DESCRIBE);
 	 query.setParam("resource",res);
 	 
 //	 query.setNsPrefixes(Prefixes.prefixes);
 	 if(m!=null){
 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
 	qExec.execDescribe(out);
 	 }
 	 else{
 		 out=Repository.describeQuery(query.asQuery().toString());
 	 }
 	return out;
	
}
/**
 * Method handles ask queries
 * @param m - model to be queried if null datastore is queried instead
 * @param queryString - SPARQL Query template
 * @param params	- Parameters to populate query
 * @return boolean true or false
 */
public static boolean ask(Model m,String queryString,ArrayList<Parameter>params){
 	String populatedQuery=populateQuery(queryString,params,false);
 	if(m!=null){
 		QueryExecution qExec=QueryExecutionFactory.create(populatedQuery,m);	
 	 	return qExec.execAsk();
 	}
 	
 	return Repository.askQuery(populatedQuery);
}
/**
 * Populate query with custom bindings and add default system prefixes to it. 
 * @param queryString - SPARQL query template
 * @param params     - parameters(custom bindings) for SPARQL template
 * @param update 	- SPARQL UPDATE query ?
 * @return populated query String with system defined prefixes
 */
public static String populateQuery(String queryString,ArrayList<Parameter> params,boolean update){
	ParameterizedSparqlString query=new ParameterizedSparqlString();
 	query.setCommandText(queryString);
	if(params!=null){
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
	}
	 	 
	 	 query.setNsPrefixes(Prefixes.prefixes);
	 	 if(update){
	 	 return query.asUpdate().toString();
	 	 }
	 	 return query.asQuery().toString();
}
/**
 * Method return ResultSet of a select query if Model m is not provided it queries datastore
 * @param m - model to be queried
 * @param queryTemplate  - SPARQL query template
 * @param params	- custom bindings @see Parameter
 * @return
 */
public static ResultSet selectQueryModel(Model m,String queryTemplate,ArrayList<Parameter>params){

 	String populatedQuery= populateQuery(queryTemplate,params,false);
 	//if no model query goes to FUSEKI TDB endpoint
 	 if(m==null){
 		return Repository.selectQuery(populatedQuery);
 	}
 	 //query model
 	 else{
 	QueryExecution qExec=QueryExecutionFactory.create(populatedQuery,m);	
 	return qExec.execSelect();
 	 }

}
/**
 * This method handles update queries, if Model m not provided update datastore
 * @param m
 * @param queryTemplate
 * @param params
 */
public static void updateModel(Model m,String queryTemplate,ArrayList<Parameter>params){ 
 	String populatedQuery=populateQuery(queryTemplate,params,true);
 	if(m==null){
 		Repository.updateQuery(populatedQuery);
 		return;
 	}
 	UpdateAction.parseExecute(populatedQuery, m);
 	
	
	
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
public static void main(String[] args){
	Model m=ModelFactory.createDefaultModel();
	String query="insert data { <a> <b> ?test . }";
	Queries.updateModel(m, query,Tools.getParameters(new Parameter("test","uri",null)));
	m.write(System.out,"TTL");
	
	
	
	
	
	
	
}
}
