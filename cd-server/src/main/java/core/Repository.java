package core;


import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
/**
 * Class responsible for dealing with Fuseki datastore
 * @author stan
 *
 */
public class Repository {
	
	private static DatasetAccessor DS;
	
	static{
		//Connect with the repository
		createFusekiAccessors();
	}
	/*
	public static void createFuseki(){
		dataset= TDBFactory.createDataset(Strings.TDBLocation);
		dataset.begin(ReadWrite.WRITE);
		Model tdb= dataset.getDefaultModel();
		server=EmbeddedFusekiServer.create(4040, dataset.asDatasetGraph(), Strings.TDBLocation);
		server.start();
	}
	public static void stopServer(){
		server.stop();
	}
	*/
	
	/**
	 * Initialize when first called
	 */
	public static void createFusekiAccessors(){
		System.out.println("Creating accessor for model...");
		DS=DatasetAccessorFactory.createHTTP(Strings.FUSEKI_SERVICE_URI);
		System.out.println("Created...");
	
	}
	
	public static void addModel(Model m){
		DS.add(m);
	}
	public static void addModel(Model m, String namedGraph){
		DS.add(namedGraph, m);
	}
	public static void deleteModel(String namedGraph){
		DS.deleteModel(namedGraph);
	}
	
	public static boolean exists(String namedGraph){
		return DS.containsModel(namedGraph);
	}
	public static void deleteDefault(){
		DS.deleteDefault();
	}
	
	public static void putModel(Model m, String namedGraph){
		DS.putModel(namedGraph,m);
	}
	
	public static void updateQuery(String query){
	UpdateRequest update=UpdateFactory.create(query);
	UpdateExecutionFactory.createRemote(update, Strings.FUSEKI_UPDATE_URI).execute();
	
		
	}
	public static ResultSet selectQuery(String queryString){
	Query query=QueryFactory.create(queryString);
	QueryExecution qexec= QueryExecutionFactory.sparqlService(Strings.FUSEKI_QUERY_URI, query);
	return qexec.execSelect();	
		
	}
	public static boolean askQuery(String queryString){
		Query query=QueryFactory.create(queryString);
		QueryExecution qexec= QueryExecutionFactory.sparqlService(Strings.FUSEKI_QUERY_URI, query);
		return qexec.execAsk();	
			
		}
	public static Model describeQuery(String queryString){
		Query query=QueryFactory.create(queryString);
		QueryExecution qexec= QueryExecutionFactory.sparqlService(Strings.FUSEKI_QUERY_URI, query);
		return qexec.execDescribe();
			
		}
	
	public static Model getModel(String namedGraph){
		
		return DS.getModel(namedGraph);
		
		
	}
	public static ResultSet selectQuery(String queryString,String service){
		Query query=QueryFactory.create(queryString);
		QueryExecution qexec= QueryExecutionFactory.sparqlService(service, query);
		return qexec.execSelect();
	}
	

	
	

	public static void loadTest(){
		String schemaDS=NGHandler.getSchemaString("user");
		String schemaKA=NGHandler.getKAString("user");
		Model userds=ModelFactory.createDefaultModel();
		Model userka=ModelFactory.createDefaultModel();
		userds.read("http://localhost/ontologies/user-schema.ttl");
		userka.read("http://localhost/ontologies/user-ka.ttl");
		putModel(userka,schemaKA);
		putModel(userds,schemaDS);
		
		
		
	}
	
	public static void main (String args[]){
		//String query="INSERT INTO GRAPH <%s> {<%s> <http://crowddata.abdn.ac.uk/user> .}";
		//String s=buildRepetativeQuery(query,"http://newgraph.com");
		///System.out.println(s);
		Repository.loadTest();
		//Repository.updateQuery("CLEAR ALL");
		//Repository.addModel(Tools.getModel("http://crowddata.abdn.ac.uk/descriptions/datasetDescription.ttl"), "http://test");
		//System.out.println(Repository.exists("http://test"));
		
	}

}
