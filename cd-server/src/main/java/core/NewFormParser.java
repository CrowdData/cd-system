package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

public class NewFormParser {

	
	public JSONArray templates=new JSONArray();
	//check for first root call
	boolean first=true;
	boolean domains=true;
	OntModel model;
	
	String datasetNS;
	String rootUri;
	//prop used when root contains object property with define range or restriction
	OntClass previousClass=null;
	
	public NewFormParser(String uri,String ds,boolean domains){
		datasetNS=ds;
		rootUri=uri;
		this.domains=domains;
		
		
		
	}
	public static JSONObject getTemplates(String uri,String ds,boolean domains){
		
		JSONObject root=new JSONObject();
		root.put("root", uri);
		
		
		NewFormParser parser=new NewFormParser(uri,ds,domains);
		
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
			
		}
		else{
			throw new IllegalArgumentException(String.format("Schema for %s dataset doesn't exist",datasetNS));
		}
		if(Repository.exists(schemaKA)){
			intermediate.add(Repository.getModel(schemaKA));
		}
		//imports are handled by making inference graph
		
		model=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF, intermediate);
		model.writeAll(System.out,"TTL");
		
		
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
		}
		
	    
		
		
		template.put("type", "group");
		template.put("label", getClassDescription(c,ds).label);
		template.put("description", getClassDescription(c,ds).description);
		JSONObject constraints=new JSONObject();
		constraints.put("rdfs:type", uri);
		template.put("constraints",constraints);
		template.put("nodetype", "RESOURCE");
		
		JSONArray items=new JSONArray();
		for(OntProperty p : getProperties(c)){
			items.put(createID(p,c,ds));
			convertProperty(p,c,ds);
			
		}
		template.put("items", items);
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
	
	Resource range=getRange(c,p);
	if(range==null){
	//	throw new NullPointerException("Range is neither overriden nor provided by the property.");
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
	
	
public void loadSchemas(String ds){
	
	Model base=ModelFactory.createDefaultModel();
	base.read("http://localhost/ontologies/user-ka.ttl");
	base.read("http://localhost/ontologies/user-schema.ttl");
	
	model=ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, base);

	
}
	
/*
 * Should work for both, property and class descriptions
 */
public static ArrayList<HashMap<String,RDFNode>> getComments(Model m,String resource,boolean parent,String property){
ArrayList<Triplet<String,String,RDFDatatype>> bindings=new ArrayList<Triplet<String,String,RDFDatatype>>();
ResultSet r;
bindings.add(new Triplet<String,String,RDFDatatype>("resource",resource,null));
if(!parent){
bindings.add(new Triplet<String,String,RDFDatatype>("classType","http://crowddata.abdn.ac.uk/ontologies/ka/form-requirements#PropertyDescription",null));
bindings.add(new Triplet<String,String,RDFDatatype>("property",property,null));
 r=Queries.selectQueryModel(m, Queries.TEMPLATE_SELECT_LABEL_COMMENT_PROPERTY, bindings);
}
else{
	bindings.add(new Triplet<String,String,RDFDatatype>("classType","http://crowddata.abdn.ac.uk/ontologies/ka/form-requirements#ClassDescription",null));	
	 r=Queries.selectQueryModel(m, Queries.TEMPLATE_SELECT_LABEL_COMMENT, bindings);
}


return ResultSetParser.getBindings(r, Queries.TEMPLATE_SELECT_COMMENTS);	

	
	
	
	
}
public static void main (String[] args){
	
	NewFormParser.getTemplates("http://xmlns.com/foaf/0.1/Agent", "user",true);
	
	
}
	
	
	
	
	
	
	
}
