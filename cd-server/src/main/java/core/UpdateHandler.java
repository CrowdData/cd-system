package core;

import org.json.JSONObject;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.ResourceUtils;

public class UpdateHandler {

	Property inDataset = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#inDataset");
	Property wasDerivedFrom = ResourceFactory
			.createProperty("http://www.w3.org/ns/prov#wasDerivedFrom");
	Property rdfTemplate = ResourceFactory
			.createProperty("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/rdfTemplate");

	public JSONObject handleUpdate(String resourceID) {
		JSONObject jsonResponse = new JSONObject();

		Model data = Queries.getDescribe(resourceID, null);
		
		
		long start=System.currentTimeMillis();
		Tools.buildSiblings(data);
		long end=System.currentTimeMillis();
		System.out.println("Total execution time:"+(end-start));
		System.out.println("Queries execution time:"+Tools.time);
		
		Resource oldresource = data.getResource(resourceID);
		if (data.isEmpty()) {
			throw new NullPointerException(
					"Resource does not exist in our system");
		}
		
		String inds = data
				.getRequiredProperty(data.getResource(resourceID), inDataset)
				.getResource().getURI();
		String templateid = data.getRequiredProperty(
				data.getResource(resourceID), rdfTemplate).getString();
		String newid = Tools.getNewVersion(resourceID);
		
		ResourceUtils.renameResource(oldresource, newid);
		
		jsonResponse.put("resourceURI", Tools.getNewVersion(resourceID));
		jsonResponse.put("datasetPath", inds);
		jsonResponse.put("previousURI", resourceID);

		JSONObject graph = new JSONObject(RDFSerializer.getRDFStringFromModel(
				data, "RDF/JSON"));
		jsonResponse.put("graph", graph);
		
		JSONObject template = new JSONObject(URLReader.readURL(templateid));
		Tools.checkForChoices(template);
		
		//change group items to URI
		Tools.changeRootTypes(template, "URI");
		
		jsonResponse.put("template", template);
		
		System.out.println(jsonResponse.toString(5));
		return jsonResponse;

	}


	public static void main(String[] args) {
		new UpdateHandler()
				.handleUpdate("http://crowddata.abdn.ac.uk/datasets/testing/resource/yrIs6zEMSEaC5foyE-Bb7g");
		// new CreateHandler().handleCreate("testdataset",
		// "http://this.is.resource.type");

	}

}
