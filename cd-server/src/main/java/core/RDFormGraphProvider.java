package core;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFormGraphProvider {


	
	
private static Model getResourceData(String resource){
	
	return Queries.getDescribe(resource, null);
	
}

public static String getGraph(String resource){
	
	Model m=getResourceData(resource);
	if(m.isEmpty()){
		throw new NullPointerException(String.format("The following resource %s was not found",resource));
	}
	m.write(System.out,"RDF/JSON");
	return RDFSerializer.getRDFStringFromModel(m, "RDF/JSON");
		
	
}
public static void main(String[] args){
	RDFormGraphProvider.getGraph("http://crowddata.abdn.ac.uk/datasets/questions/resource/LU1MeJOYTf-_3QQSldWfvw");
	
}
	
	
	
	
	
}
