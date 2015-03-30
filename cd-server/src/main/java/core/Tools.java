package core;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;


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
	
	
	public static String generateID(){
		UUID uuid=UUID.randomUUID();
	      ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
          buffer.putLong(uuid.getMostSignificantBits());
          buffer.putLong(uuid.getLeastSignificantBits());
          return Base64.encodeBase64URLSafeString(buffer.array());
	}

	
	public static String getNamespace(String uri) {	 
		 if (uri.contains("#")) {
			 return uri.substring(0,uri.lastIndexOf("#"));
		 }
		 if (uri.contains("/")) {
			 return uri.substring(0,uri.lastIndexOf("/")+1);
		 }
		 return uri;
	}
	
	public static String getNewVersion(String uri) {	 
		 if (uri.matches(".*\\d")) {
			String version= uri.substring(uri.lastIndexOf("/")+1,uri.length());
			String base= uri.substring(0,uri.lastIndexOf("/")+1);
			int ver=Integer.parseInt(version);
			ver++;
			return base+ver;
			
		 }
		 //is new resource not being updated
		 return uri+"/1";
	}
	

	public static ArrayList<Parameter> getParameters(Parameter... params){
		ArrayList<Parameter> paraArray=new ArrayList<Parameter>();
		for(Parameter p: params){
			paraArray.add(p);
		}
		
		return paraArray;
	}
	
	/*
	public static String getDataSetSchema(String prefix,String resource, String vocabulary){
		Model m=Tools.getModel(vocabulary);
		Model resourceDesc=Tools.getResourceDescription(Prefixes.prefixes.get(prefix)+resource, m);
		
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
	}
	public static String getDataSetSchema(String resource, String vocabulary){
		Model m=Tools.getModel(vocabulary);
		Model resourceDesc=Tools.getResourceDescription(resource, m);
		
		return JSONLDSerializer.getJSONLD(resourceDesc);
		
		
		
	} */    /*
		returns- Model, collection of triples of properties, which has resourceURI as their rdfs:domain
	 
	public static Model getDomainResources(String resourceURI,Model m){
		Resource res=ResourceFactory.createResource(resourceURI);
		System.out.println(resourceURI);
		Model out=ModelFactory.createDefaultModel();
		
	 		ParameterizedSparqlString query=new ParameterizedSparqlString();
	 	query.setCommandText( ""
	 				+ "SELECT ?property "
	 				+ "WHERE {"
	 				+ " ?property rdfs:domain ?resource ."
	 				+ "   }");
	 	 query.setParam("resource",res);
	 		 query.setNsPrefixes(Prefixes.prefixes);
	 		
	 		System.out.println(query.asQuery().toString());
	 	QueryExecution qExec=QueryExecutionFactory.create(query.asQuery(),m);	
	 		   
	 	ResultSet rs = qExec.execSelect()	 			;
	 		     try {
	 		        while(rs.hasNext()){
	 		      
	 		        	QuerySolution cap=rs.next();
	 		        String resource=cap.getResource("property").toString();
	 		        System.out.println(resource);
	 		        out.add(getResourceDescription(resource,m));	
	 		        }
	 		       
	 		     }
	 		  
	 		        catch(Exception e){
	 		        	e.printStackTrace();
	 		        
	 		        }		     
	 		      finally { qExec.close();}
	 		    return out;

		
	
}
	

	
	
		

		
		public static void main (String args[]){
			System.out.println(Tools.getNamespace("http://xmlns.com/foaf/0.1/Person"));
		//	System.out.println(Tools.getDomainResources("http, "http://purl.org/dc/terms/"));
		//	System.out.println(Tools.getDataSetSchema("http://purl.org/dc/elements/1.1/description","http://purl.org/dc/elements/1.1/"));
		}
		
		
		
		*/
	
public static void main(String[]args){
	System.out.println(
Tools.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf/1"));
	
	System.out.println(
Tools.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf"));
	
	System.out.println(
Tools.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf/1cd"));
	
	
	
}
	
}
	
