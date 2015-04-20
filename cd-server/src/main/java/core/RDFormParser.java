package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
/**
 * 
 * @author Stanislav Beran
 * @deprecated 
 * Old class to handle generation of RDForms templates based on the defined schema
 * @see NewFormParser.java
 *
 */
public class RDFormParser {

	
	/*
	 * ------------------------------------------
	 * Rewritten see NewFormParser.java
	 * ------------------------------------------
	 */

	
	
	/*
	 * Resource must be root?
	 */
	public static void initParser(String ds,String resource){
		
		//Create json object with root resource
		JSONObject mainJson=getSkeleton(resource);
		// Model get KA and schema ontologies for ds
		//TODO workout how to get dataset
		Model base0=loadSchemas(ds);
		OntModel base=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF, base0);
		
	
		//GET Root restrictions for initial template
		ArrayList<HashMap<String,RDFNode>> root= getRoot(resource,base);
		ArrayList<HashMap<String,RDFNode>> rootComments=getComments(base, resource, true, null);//null if no property restriction
		//ConstructRootTemplate
		JSONObject rootJson=getRootTemplate(resource,root,rootComments);
		mainJson.getJSONArray("templates").put(rootJson);
		
		
		System.out.println(mainJson.toString(5));
		//processRoot(root,main,resource);
		
		//test comments
		getComments(base,resource,false,"http://xmlns.com/foaf/0.1/name");
		
		
		OntProperty p=base.getOntProperty("someProp");
		
		
		
		
	}
	
	
	
	
	
	
	public static JSONObject getRootTemplate(String resource,ArrayList<HashMap<String,RDFNode>> root,ArrayList<HashMap<String,RDFNode>> rootComments){
		//derived properties to be constructed from who know where. 
		HashSet<String> properties=new HashSet<String>();
		JSONObject rootTemplate=new JSONObject();
		rootTemplate.put("id", resource);		//id as a resource for convention
		rootTemplate.put("type", "group");		//all roots mustbe group
		JSONObject cardinality=new JSONObject();
		cardinality.put("min", 1);        //default minimum one
		rootTemplate.put("cardinality",cardinality);
		
		if(rootComments.size()==1){
			rootTemplate.put("description", rootComments.get(0).get("description").asLiteral().getString());
			rootTemplate.put("label", rootComments.get(0).get("label").asLiteral().getString());
		}
		else{
			throw new IllegalArgumentException("Check root comments, size is bigger than one or 0");
		}
		
		

		//internal properties 
		for(HashMap<String,RDFNode> bindings: root){
			
		if (bindings.get("restrictionProperty")!=null){
			properties.add(bindings.get("restrictionProperty").asResource().getURI());
	
		}
			
		}
		//domain properties
		
		
		
		
		
		JSONArray items=new JSONArray();
		for(String item: properties){
		  items.put(item);	
		}
		rootTemplate.put("items", resource+items);
		
		return rootTemplate;
	}
	public static ArrayList<HashMap<String,RDFNode>> getRoot (String root,Model base){
		ArrayList<Parameter> bindings=new ArrayList<Parameter>();
		bindings.add(new Parameter("resource",root,null));
		
		ResultSet r=Queries.selectQueryModel(base, Queries.TEMPLATE_SELECT_RESTRICTIONS, bindings);
		ArrayList<HashMap<String,RDFNode>> rootResults=ResultSetParser.getBindings(r, Queries.TEMPLATE_SELECT_RESTRICTIONS_BINDINGS);
		System.out.println(rootResults.size());
		return rootResults;
		
	}
	
	
	/*
	 * Should work for both, property and class descriptions
	 */
	public static ArrayList<HashMap<String,RDFNode>> getComments(Model m,String resource,boolean parent,String property){
	ArrayList<Parameter> bindings=new ArrayList<Parameter>();
	ResultSet r;
	bindings.add(new Parameter("resource",resource,null));
	if(!parent){
	bindings.add(new Parameter("classType","http://crowddata.abdn.ac.uk/ontologies/ka/form-requirements#PropertyDescription",null));
	bindings.add(new Parameter("property",property,null));
	 r=Queries.selectQueryModel(m, Queries.TEMPLATE_SELECT_LABEL_COMMENT_PROPERTY, bindings);
	}
	else{
		bindings.add(new Parameter("classType","http://crowddata.abdn.ac.uk/ontologies/ka/form-requirements#ClassDescription",null));	
		 r=Queries.selectQueryModel(m, Queries.TEMPLATE_SELECT_LABEL_COMMENT, bindings);
	}
	
	
	return ResultSetParser.getBindings(r, Queries.TEMPLATE_SELECT_COMMENTS);	
 
		
		
		
		
	}
	
	/*
	 * Creates skeleton for RDFTemplate
	 */
	private static JSONObject getSkeleton(String root){
		JSONObject response=new JSONObject();
		response.put("root", root);
		response.put("templates", new JSONArray());
		return response;
	}
	
private static void processRoot(ArrayList<HashMap<String,RDFNode>> results,JSONObject template, String resource){
	
	JSONObject rootTemplate=new JSONObject();
	rootTemplate.put("id", resource);		//id as a resource for convention
	rootTemplate.put("type", "group");		//all roots mustbe group
	JSONObject cardinality=new JSONObject();
	cardinality.put("min", 1);        //default minimum one
	rootTemplate.put("cardinality",cardinality);
	
	
	ArrayList<JSONObject> intermidiate=new ArrayList<JSONObject>();
	for(HashMap<String,RDFNode> bindings : results){
		JSONObject j=new JSONObject();
		
		String id=bindings.get("restrictionProperty").asResource().getURI();
		j.put("id", id);
		RDFNode resRest=bindings.get("resourceRestriction");
		if(resRest!=null && resRest.isLiteral()){
			j.put("type","text");
			j.put("nodetype", "DATATYPE_LITERAL");
			j.put("datatype", resRest.asLiteral().getDatatypeURI());
				
		}
		else if (resRest!=null && resRest.isURIResource()){
			j.put("type", "group");
			
		}
		
		if(bindings.get("exactly")!=null){
			j.put("min",bindings.get("exactly"));
			j.put("max",bindings.get("exactly"));
			j.put("pref",bindings.get("exactly"));
		}
		if(bindings.get("min")!=null){
			j.put("min",bindings.get("min"));
			
		}
		if(bindings.get("max")!=null){
			j.put("max",bindings.get("max"));
			
		}
		
		
		
		
	}
	
	
	
	
}
	

	
	


		
public static Model loadSchemas(String ds){
		
		Model base=ModelFactory.createDefaultModel();
		base.read("http://localhost/ontologies/user-ka.ttl");
		base.read("http://localhost/ontologies/user-schema.ttl");
		base.write(System.out,"TTL");
		
		
		
		
		return base;
		
	}
	
	
	
	
	public static void main (String args[]){

		RDFormParser.initParser("ds","http://xmlns.com/foaf/0.1/Agent");

	}
	
	
	
	
	
}
