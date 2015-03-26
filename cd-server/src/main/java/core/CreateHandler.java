package core;

import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class CreateHandler {

	
	OntModel resourceGraph=ModelFactory.createOntologyModel();
	Property inDataset=ResourceFactory.createProperty("http://rdfs.org/ns/void#inDataset");
	Property wasDerivedFrom=ResourceFactory.createProperty("http://www.w3.org/ns/prov#wasDerivedFrom");
	
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
		
		//output model as rdfjsonString and assing it to jsonResponse
		JSONObject graph=new JSONObject(RDFSerializer.getRDFStringFromModel(resourceGraph, "RDF/JSON"));
		jsonResponse.put("graph", graph );
		
		
		//load template from file based on resourceType
												//should this be reosurcetype? questions and answers are same resource but different templates
		JSONObject template=RDFormTemplateProvider.getInstance().getTemplate(resourceType);
		jsonResponse.put("template", template);
		
		
	
		//assing to json object if fine
		
		System.out.println(jsonResponse.toString(5));
		return jsonResponse;
		
		
	}
	
	public static void main (String [] args){
		
		new CreateHandler().handleCreate("testdataset", "http://this.is.resource.type");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
