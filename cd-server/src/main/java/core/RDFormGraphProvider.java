package core;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFormGraphProvider {


	
	
private static Model getResourceData(String resource){
	
	return Queries.getDescribe(resource, null);
	
}

public static void getGraph(String resource){
	
	Model m=getResourceData(resource);
	m.write(System.out,"RDF/JSON");
		
	
}
public static void main(String[] args){
	RDFormGraphProvider.getGraph("http://crowddata.abdn.ac.uk/datasets/questions/resource/LU1MeJOYTf-_3QQSldWfvw");
	
}
	
	
	
	
	
}
