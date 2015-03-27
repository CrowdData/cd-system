package core;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class CreateHandler {

	
	OntModel resourceGraph=ModelFactory.createOntologyModel();
	Property inDataset=ResourceFactory.createProperty("http://rdfs.org/ns/void#inDataset");
	Property wasDerivedFrom=ResourceFactory.createProperty("http://www.w3.org/ns/prov#wasDerivedFrom");
	Property rdfTemplate=ResourceFactory.createProperty("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/rdfTemplate");
	public JSONObject handleCreate(String datasetID, String resourceType){
		JSONObject jsonResponse=new JSONObject();
		
		String newResource=ResourceProvider.generateResourceString(datasetID);
		String fullDatasetID=NGHandler.getDataString(datasetID);
		
		
		jsonResponse.put("resourceURI", newResource);
		jsonResponse.put("datasetPath", fullDatasetID);
		jsonResponse.put("datasetID", datasetID);
		
		//add resource to graph
		OntResource individual=resourceGraph.createOntResource(newResource);
		//set type from resourceType
		individual.setRDFType(ResourceFactory.createResource(resourceType));
		//set prov entity type
		individual.addRDFType(ResourceFactory.createResource("http://www.w3.org/ns/prov#Entity"));
		
		
		//set version to be 1?
		individual.setVersionInfo("1");
		individual.addProperty(inDataset, ResourceFactory.createResource(fullDatasetID));
		//add absolute location of rdfTemplate that is going to be used to create resource
		individual.addProperty(rdfTemplate,RDFormTemplateProvider.getInstance().getURLLocation(resourceType));
		
		
		
		//load template from file based on resourceType
												//should this be reosurcetype? questions and answers are same resource but different templates
		JSONObject template=RDFormTemplateProvider.getInstance().getTemplate(resourceType);
		jsonResponse.put("template", template);
		
		//add specific template constraints from root if exist
		addTemplateConstraints(individual,template);
		

		//output model as rdfjsonString and assing it to jsonResponse
		JSONObject graph=new JSONObject(RDFSerializer.getRDFStringFromModel(resourceGraph, "RDF/JSON"));
		jsonResponse.put("graph", graph );
		
		
		
		System.out.println(jsonResponse.toString(5));
		return jsonResponse;
		
		
	}
	
	public static void main (String [] args){
		new CreateHandler().handleCreate("testdataset", "http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Question");
	//	new CreateHandler().handleCreate("testdataset", "http://this.is.resource.type");
		
	}
	
	
	
	
	private void addTemplateConstraints(OntResource individual, JSONObject template){
		JSONObject root=findRootTemp(template);
		if(!root.has("constraints")){
			return;
		}
	
		JSONObject constraints=root.getJSONObject("constraints");
		Iterator<?> keys = constraints.keys();
		while(keys.hasNext()){			
			String prop=(String)keys.next();
			Property p=ResourceFactory.createProperty(prop);
			//only uri's allowed
			individual.addProperty(p,ResourceFactory.createResource(constraints.getString(prop)));
		}
		
		
		
	}
	private JSONObject findRootTemp(JSONObject template){
		String rootID=template.getString("root");
		JSONArray a=template.getJSONArray("templates");
		for(int i=0; i<a.length();i++){
			if(a.getJSONObject(i).getString("id").equals(rootID)){
				return a.getJSONObject(i);
			}
			
		}
		throw new NullPointerException("MISSING ROOT TEMPLATE");
	}
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

