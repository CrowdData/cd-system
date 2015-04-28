package core;



import java.io.File;
import java.io.FileWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;

public class RepoDumps {
 
	String[] datasets={"disruption","events","feedback","questions","demand","questionsv2","responsesv2","incidents","demandv2","eventsv2","feedbackv2","users"};
	
	
	public void createDumps(){
		
		
		for(String dataset : datasets){
		Query query= QueryFactory.create("CONSTRUCT{?s ?p ?o.}FROM<http://crowddata.abdn.ac.uk/datasets/"+dataset+"/data/>WHERE{?s ?p ?o.}");	
		QueryExecution qexec=QueryExecutionFactory.sparqlService(Strings.FUSEKI_QUERY_URI,query);	
		Model m=qexec.execConstruct();
		System.out.println(query.toString());
		File f=new File("data/"+dataset+"-dump.ttl");	
		f.getParentFile().mkdirs();
		try{
		m.write(new FileWriter(f), "TTL");
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
			
			
		}
		
		
		
		
		
		
		
	}
	public static void main(String[] args){
		new RepoDumps().createDumps();
	}
	
	
	
	
}
