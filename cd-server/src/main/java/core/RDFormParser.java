package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import pojo.Description;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;
/**
 * This class handles generating RDForm templates from ontology. Main addition from NewFormParser is attempt to dynamicaly retrieve
 * choices for RDF templates. 
 * 
 * @author Stanislav Beran
 *
 */
public class RDFormParser {

	
	public JSONArray templates=new JSONArray();
	//check for first root call
	boolean first=true;
	boolean domains=true;
	OntModel model;
	
	String datasetNS;
	String rootUri;
	//prop used when root contains object property with defined range or restriction
	OntClass previousClass=null;
	
	public RDFormParser(String uri,String ds,boolean domains){
		datasetNS=ds;
		rootUri=uri;
		this.domains=domains;
		
		
		
	}
	/**
	 *  Entry point to Ralg 
	 * @param uri - IRI of a resource to be described (must be contained in the ontology associated with the dataset)
	 * @param ds   - dataset id to retrieve schema and KA from
	 * @param domains - boolean value to enable/disable domain processing as well.
	 * @return RDForm template in JSON object
	 */
	public static JSONObject getTemplates(String uri,String ds,boolean domains){
		
		JSONObject root=new JSONObject();
		root.put("root", uri);
		
		
		RDFormParser parser=new RDFormParser(uri,ds,domains);
		
		parser.initModel();
		
		//recursive
		parser.convertClass(uri, ds, null, null);	
		
		
		System.out.println(parser.getJSONTemplate().toString(5));
		JSONArray templateArray=parser.getJSONTemplate();
		//add automatic sort for each
		for(int i=0;i<templateArray.length();i++){
			templateArray.getJSONObject(i).put("automatic", "true");
		}
		root.put("templates", templateArray);
		return root;
		
	}
	
	public JSONArray getJSONTemplate(){
		return templates;	
	}
	
	public void initModel(){		
		String schemaDS=NGHandler.getSchemaString(datasetNS);
		String schemaKA=NGHandler.getKAString(datasetNS);
		Model intermediate=ModelFactory.createDefaultModel();
		if(Repository.exists(schemaDS)){
			intermediate.add(Repository.getModel(schemaDS));
			//intermediate.read("http://crowddata.abdn.ac.uk/genform/demand-schema-example.ttl");
			//intermediate.read("http://crowddata.abdn.ac.uk/genform/demand-ka-example.ttl");
		}
		else{
			throw new IllegalArgumentException(String.format("Schema for %s dataset doesn't exist",datasetNS));
		}
		if(Repository.exists(schemaKA)){
			intermediate.add(Repository.getModel(schemaKA));
		}
		//imports are handled by making inference graph, therefore any external imports must be defined in schema.
		
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, intermediate);
		model.writeAll(System.out,"TTL");
		
		
	}
	public boolean isSame(JSONObject o ){
		return o.getString("value").equals(o.getString("label"));
	}

	public void convertClass(String uri,String ds,String id,OntProperty prop){
		JSONObject template=new JSONObject();
		OntClass c=null;
		c=model.getOntClass(uri);
	    if(c==null){
	    	throw new NullPointerException("The class doesn't exist");
	    }
		if(first){
			template.put("id", uri);
			previousClass=c;     //point has cardinality
			first=false;
		}
		else{
			template.put("id", id);
			System.out.println("Check card for class"+c.getURI()+" and prop: "+prop.getURI());
			JSONObject cardinality=getCardinality(previousClass,prop);
			if(cardinality.has("min")||cardinality.has("max")||cardinality.has("pref")){
				template.put("cardinality", cardinality);
			}
			//if no cardinality constraints default is 0 with prefered 1
			else{
			 
			}
			template.put("property", prop.getURI());
		}
		
	    
		
		
		template.put("type", "group");
		template.put("nodetype", "RESOURCE");
		JSONArray choices=getOneOf(c);
		if(choices.length()!=0){
			template.put("type", "choice");
			template.put("choices", choices);
			if(isSame(choices.getJSONObject(0))){
				template.put("nodetype","ONLY_LITERAL");
			}
		}
		
		
		template.put("label", getClassDescription(c,ds).label);
		template.put("description", getClassDescription(c,ds).description);
		JSONObject constraints=new JSONObject();
		constraints.put("rdfs:type", uri);
		template.put("constraints",constraints);
		
		if(template.getString("type").equals("group")){
		JSONArray items=new JSONArray();
		for(OntProperty p : getProperties(c)){
			items.put(createID(p,c,ds));
			convertProperty(p,c,ds);
			
		}
		template.put("items", items);
		}
		templates.put(template);
			
		
	}
	
	public JSONObject getCardinality(OntClass c, OntProperty p){
		JSONObject j=new JSONObject();
		
		ExtendedIterator<OntClass> superclasses=c.listSuperClasses();
		while(superclasses.hasNext()){
			OntClass superclass=superclasses.next();
			if(superclass.isRestriction()){
				if(superclass.asRestriction().getOnProperty().equals(p)){
					if(superclass.asRestriction().isCardinalityRestriction()){
						int card=superclass.asRestriction().asCardinalityRestriction().getCardinality();			
						j.put("min", card);
						j.put("max", card);
						j.put("pref", card);
					}
					if(superclass.asRestriction().isMaxCardinalityRestriction()){
						int max=superclass.asRestriction().asMaxCardinalityRestriction().getMaxCardinality();
						j.put("max", max);
					}
					if(superclass.asRestriction().isMinCardinalityRestriction()){
						int min=superclass.asRestriction().asMinCardinalityRestriction().getMinCardinality();
						j.put("min", min);
					}
					//default cardinality
					if(!(j.has("max")||j.has("min"))){
						j.put("min", 0);
						j.put("pref", 1);
					}
			}
			}
		}
		return j;
				
				
						
			
				
	}
	
	public String createID(OntProperty p,OntClass c, String ds){
		
		return p.getLabel(null)+"_"+c.getLabel(null)+"_"+ds;
	}
	
	public HashSet<OntProperty> getProperties(OntClass c){
		HashSet<OntProperty> properties=new HashSet<OntProperty>();
		ExtendedIterator<OntClass> superclasses=c.listSuperClasses();
		while(superclasses.hasNext()){
			OntClass superclass=superclasses.next();
			if(superclass.isRestriction()){
				if(superclass.asRestriction().isAllValuesFromRestriction()){
					properties.add(superclass.asRestriction().getOnProperty());
				}
			}
			
			
		}
		if(domains){
			
		ExtendedIterator<OntProperty> allproperties=model.listOntProperties();
		while(allproperties.hasNext()){
			OntProperty prop=allproperties.next();
			if(prop.getDomain()!=null && prop.getDomain().equals(c)){
				properties.add(prop);
				System.out.println("Domain found");
			}
			
			
		}
		}

		return properties;
	}
	
	
/*
 * If there is no range or restriction imposed on the property treated as literal(even when it's object property)!	
 */
public void convertProperty(OntProperty p,OntClass c, String ds){
	JSONObject template=new JSONObject();
	template.put("id", createID(p,c,ds));
	template.put("property", p.getURI());
	Description propDesc=getPropertyDescription(p,c,ds);
	template.put("label", propDesc.label);
	template.put("description", propDesc.description);
	JSONObject cardinality=getCardinality(c,p);
	if(cardinality.has("max")||cardinality.has("min")){
		template.put("cardinality", cardinality);
	}
	
	if(isChoice(p.getURI(), c.getURI())){
		System.out.println("Property is choice.");
		JSONArray array=getChoices(p.getURI(), c.getURI());
		template.put("choices", array);
		template.put("type","choice");
		template.put("nodetype", "RESOURCE");
		if(isSame(array.getJSONObject(0))){
			template.put("nodetype", "ONLY_LITERAL");
		}
		templates.put(template);
		return;	
	}
	
	
	
	Resource range=getRange(c,p);
	if(range==null){
		//throw new NullPointerException("Range is neither overriden nor provided by the property.");
	}
	System.out.println("Checking if "+p.getURI()+" is datatypeproperty");
	if(range!=null && (p.isDatatypeProperty() ||isDatatypeResource(range))){
		
	template.put("type", "text");
	template.put("nodetype", "DATATYPE_LITERAL");
	template.put("datatype", range.getURI() );	
		
		
	}
	// blacklist to generic types
	else if(range!=null && p.isObjectProperty()&&!Prefixes.isBlacklisted(range.getURI())){
		//If range of the class is the class itself the parser goes to infinite loop
		//If so create TEXT item with URI 
		if(range.getURI().equals(c.getURI())){
			template.put("type", "text");
			template.put("nodetype", "URI");
			templates.put(template);
			return;
		}
		convertClass(range.getURI(),ds,createID(p,c,ds),p);
		return;
		
		
	}
	
	else{
		template.put("type", "text");
		template.put("nodetype", "ONLY_LITERAL");
	}
	
	templates.put(template);
		
}

public boolean isDatatypeResource(Resource range){
	return range.getURI().contains(XSD.getURI());
}
public JSONArray getOneOf(OntClass c){
	JSONArray a=new JSONArray();
	ExtendedIterator<OntClass> superclasses=c.listEquivalentClasses();
	while(superclasses.hasNext()){
		OntClass superclass=superclasses.next();
		if(superclass.isEnumeratedClass()){
			RDFList l=superclass.asEnumeratedClass().getOneOf();
			for(int i=0;i<l.size();i++){
				JSONObject j=new JSONObject();
				if(l.get(i).isURIResource()){
					
					j.put("value", l.get(i).asResource().getURI());
				String label=	model.getOntResource(l.get(i).asResource().getURI()).getLabel(null);
					System.out.println("LABEL is:"+label);
					j.put("label", label);
				}
				else if(l.get(i).isLiteral()){
					j.put("value", l.get(i).asLiteral().getString());
					j.put("label", l.get(i).asLiteral().getString());
				}
				else{
					throw new NullPointerException("This shouuld not happen GET one off");
				}
				
				a.put(j);
				
			}
		}
	
	
}
	return a;
}

public Resource getRange(OntClass c, OntProperty p){
	
	ExtendedIterator<OntClass> superclasses=c.listSuperClasses();
	while(superclasses.hasNext()){
		OntClass superclass=superclasses.next();
		if(superclass.isRestriction()){
			if(superclass.asRestriction().isAllValuesFromRestriction()){
				if(superclass.asRestriction().getOnProperty().equals(p))
			return	superclass.asRestriction().asAllValuesFromRestriction().getAllValuesFrom();
			}
		}
		
		
	}
	//get associated range if no all valuesfrom restriction found
	return p.getRange();
	
}

public Description getPropertyDescription(OntProperty p, OntClass c, String ds){
	ArrayList<HashMap<String,RDFNode>> bindings=getComments(model,c.getURI(),false,p.getURI());
	return getDescription(bindings,p);
}
public Description getClassDescription(OntClass c, String ds){
	System.out.println("Getting class description for"+c.getURI());
	ArrayList<HashMap<String,RDFNode>> bindings=getComments(model,c.getURI(),true,null);
	return getDescription(bindings,c);
	
}
public Description getDescription(ArrayList<HashMap<String,RDFNode>> bindings,OntResource c){
	Description desc=new Description();
if(bindings.size()!=0){
		
		desc.setDescription(bindings.get(0).get("description").asLiteral().getString());
		desc.setLabel(bindings.get(0).get("label").asLiteral().getString());
	}
	else if(c.getComment(null)!=null && c.getLabel(null)!=null){
		desc.setDescription(c.getComment(null));
		desc.setLabel(c.getLabel(null));
	}
	else{
		desc.setDescription("No description found");
		desc.setLabel("No label found");
		//throw new NullPointerException("No description found");
		
	}
	return desc;
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
public boolean isChoice(String property, String resource){
	ArrayList<Parameter> param=new ArrayList<Parameter>();
	param.add(new Parameter("property",property,null));
	param.add(new Parameter("resource",resource,null));
	//param.add(new Parameter("choice","true",XSDDatatype.XSDboolean));
	return Queries.ask(model,Queries.TEMPLATE_ASK_CHOICE,param);
	//check query , check ontology boolean have no quotes. 
}

public JSONArray getChoices(String property, String resource){
	ArrayList<Parameter> param=new ArrayList<Parameter>();
	param.add(new Parameter("property",property,null));
	param.add(new Parameter("resource",resource,null));
	ResultSet r= Queries.selectQueryModel(model,Queries.TEMPLATE_SELECT_QUERY_ENDPOINT,param);
	if(r.hasNext()){		
	QuerySolution sol=r.next();
	String query=sol.get("query").asLiteral().getString();
	String endpoint=sol.get("endpoint").asResource().getURI();
	
	return getChoiceArray(query,endpoint);	
		
	}
	throw new NullPointerException ("Sparql Query and Endpoint Not Found for property"+property+" resource"+resource);
}


private JSONArray getChoiceArray(String query, String endpoint){
	
	JSONArray choices=new JSONArray();
	
	ResultSet r=Repository.selectQuery(query, endpoint);
	if(!r.hasNext()){
		throw new NullPointerException (String.format("No choice binding find in %s for query %s",endpoint,query));
	}
	
	while(r.hasNext()){
		QuerySolution s=r.next();
		JSONObject obj=new JSONObject();
		RDFNode value=s.get("value");
		RDFNode label=s.get("label");
		if(value==null || label==null){
			throw new NullPointerException("Label or Value is empty check you sparql query and ensure it contains label value bindings.");
		}
		if(value.isResource()){
			obj.put("value", value.asResource().getURI());
		}
		if(value.isLiteral()){
			obj.put("value",value.asLiteral().getLexicalForm());
			System.out.println("Choice Lexical Form of value"+obj.getString("value"));
		}
		if(label.isResource()){
			obj.put("label", label.asResource().getURI());
		}
		if(label.isLiteral()){
			obj.put("label",label.asLiteral().getLexicalForm());
			System.out.println("Choice Lexical Form of value"+obj.getString("value"));
		}
		
		choices.put(obj);
	}
	return choices;
	
	
	
	
}


public static void main (String[] args){
	
	System.out.println(RDFormParser.getTemplates("http://crowddata.abdn.ac.uk/datasets/testdemand/schema/Demand", "demandv2",true).toString(5));
	
	
}
	
	
	
	
	
	
	
}
