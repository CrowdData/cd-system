package core;

import java.util.ArrayList;
import java.util.HashMap;

public class Prefixes {

public static HashMap<String,String> prefixes=new HashMap<String,String>();
public static ArrayList<String> blacklist=new ArrayList<String>();
static{
		prefixes.put("ns", "http://www.w3.org/2006/vcard/ns#");
		prefixes.put("prov", "http://www.w3.org/ns/prov#");
		prefixes.put("foaf", "http://xmlns.com/foaf/0.1/");
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		prefixes.put("owl","http://www.w3.org/2002/07/owl#");
		prefixes.put("void", "http://rdfs.org/ns/void#");
		prefixes.put("dcterms", "http://purl.org/dc/terms/");
		prefixes.put("cd", "http://crowddata.abdn.ac.uk/ontologies/cd/0.1/");
		prefixes.put("afn", "http://jena.hpl.hp.com/ARQ/function#");
		//blacklist for class description retrieval
		blacklist.add("http://www.w3.org/2002/07/owl#");
		blacklist.add("http://www.w3.org/2000/01/rdf-schema#");
		blacklist.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		
}


public static boolean isBlacklisted(String uri){
	
	for(String s: blacklist){
		if(uri.contains(s)){
			return true;
		}
	}
	return false;
	
}


}


