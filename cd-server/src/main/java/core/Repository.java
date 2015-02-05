package core;

import org.apache.jena.fuseki.EmbeddedFusekiServer;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

public class Repository {
	
	static DatasetAccessor DS;
	
	static{
		//Connect with the repository
		createFusekiAccessor();
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
	
	
	public static void createFusekiAccessor(){
		System.out.println("Creating accessor...");
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
	
	
	
	
	
	public static void main (String args[]){
	
		Repository.addModel(ModelFactory.createDefaultModel(), NGHandler.getDataString("deman"));
		System.out.println(Repository.exists(NGHandler.getDataString("deman")));
		
	}

}
