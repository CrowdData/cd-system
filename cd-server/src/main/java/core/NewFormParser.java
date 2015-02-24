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
	static boolean first=true;
	
	OntModel model;
	
	public void initModel(){		
		loadSchemas("ds");
	}
	
	public void convertClass(String uri,String ds,String id,OntProperty prop){
		JSONObject template=new JSONObject();
		OntClass c=null;
		if(first){
			template.put("id", uri);
			first=false;
		}
		else{
			template.put("id", id);
		}
		
		
	    if(model.containsResource(ResourceFactory.createResource(uri))){
	    	System.out.println("Model contains resource"+uri);
	    	c=model.getOntClass(uri);
	    }
	    else{
	    	throw new NullPointerException("The class doesn't exist");
	    }
		
		
		template.put("type", "group");
		template.put("label", getClassDescription(c,ds).label);
		template.put("description", getClassDescription(c,ds).description);
		JSONObject constraints=new JSONObject();
		constraints.put("rdfs:type", uri);
		template.put("constraints",constraints);
		template.put("nodetype", "URI");
		
		JSONArray items=new JSONArray();
		for(OntProperty p : getProperties(c)){
			items.put(createID(p,c,ds));
			convertProperty(p,c,ds);
			
		}
		template.put("items", items);
		templates.put(template);
			
		
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
		
		ExtendedIterator<OntProperty> allproperties=model.listOntProperties();
		while(allproperties.hasNext()){
			OntProperty prop=allproperties.next();
			if(prop.getDomain()!=null && prop.getDomain().equals(c)){
				properties.add(prop);
				System.out.println("Domain found");
			}
			
			
		}

		return properties;
	}
	
	
/*
 * If there is no range or restriction imposed on the property treated as literal!	
 */
public void convertProperty(OntProperty p,OntClass c, String ds){
	JSONObject template=new JSONObject();
	template.put("id", createID(p,c,ds));
	template.put("property", p.getURI());
	Description propDesc=getPropertyDescription(p,c,ds);
	template.put("label", propDesc.label);
	template.put("description", propDesc.description);
	
	
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
	// blacklist to prevent infinite loop
	else if(range!=null && p.isObjectProperty()&&!Prefixes.isBlacklisted(range.getURI())){
		
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
	//get associated range if not overriden
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
	
	NewFormParser parser=new NewFormParser();
	parser.initModel();
	parser.convertClass("http://xmlns.com/foaf/0.1/Agent", "someds","root");
	System.out.println(parser.templates.toString(5));
	
}
	
	
	
	
	
	
	
}
