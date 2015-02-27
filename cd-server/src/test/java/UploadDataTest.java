import java.io.BufferedReader;
import java.io.InputStreamReader;



import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import core.NGHandler;
import core.Repository;
import core.ResourceProvider;

import rest.ToolsResources;


public class UploadDataTest {

	String resource="http://crowddata.abdn.ac.uk/datasets/test/resource/1";
	
	
	
@Test	
public void testRDF(){

	//String empty=getString("/tests/notexist.rdf");
//	Assert.assertNull(empty);
	
	String rdf=getString("/tests/test1.rdf");	
	ToolsResources.addData("testrdf", rdf, "RDF/XML");
	
	
	Model m1=	new ResourceProvider().getResource(
			resource, "testrdf");
Assert.assertTrue(m1.containsResource(ResourceFactory.createResource(resource)));
Assert.assertTrue(m1.contains(null, null, "test1"));

 testRemoveGraph("testrdf");
}
@Test
public void testJSONLD(){

	String jsonld=getString("/tests/test1.jsonld");
	ToolsResources.addData("testjsonld", jsonld,"JSON-LD");
	
	
	Model m1=	new ResourceProvider().getResource(
			resource, "testjsonld");
Assert.assertTrue(m1.containsResource(ResourceFactory.createResource(resource)));
Assert.assertTrue(m1.contains(null, null, "test1"));

	testRemoveGraph("testjsonld");
	
	
}@Test
public void testTTL(){

	String ttl=getString("/tests/test1.ttl");
	ToolsResources.addData("testttl", ttl,"TTL");
	
	
	Model m1=	new ResourceProvider().getResource(
			resource, "testttl");
Assert.assertTrue(m1.containsResource(ResourceFactory.createResource(resource)));
Assert.assertTrue(m1.contains(null, null, "test1"));

	testRemoveGraph("testttl");
	
	
}
@Test
public void testRDFJSON(){

	String rdfjson=getString("/tests/test1.json");
	ToolsResources.addData("testrdfjson", rdfjson,"RDF/JSON");
	
	
	Model m1=	new ResourceProvider().getResource(
			resource, "testrdfjson");
Assert.assertTrue(m1.containsResource(ResourceFactory.createResource(resource)));
Assert.assertTrue(m1.contains(null, null, "test1"));

	testRemoveGraph("testrdfjson");
	
	
}

public void testRemoveGraph(String graph){
	
	String graphData=NGHandler.getDataString(graph);
	Model m=Repository.getModel(graphData);
	Assert.assertFalse(m.isEmpty());
	
	Repository.deleteModel(NGHandler.getDataString(graph));
	m=Repository.getModel(graphData);
	Assert.assertNull(m);
	

	
	
}



public String getString(String filepath){
	
	BufferedReader filestr=new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filepath)));
	StringBuilder message=new StringBuilder();
	String line;
	try{
	while((line = filestr.readLine()) != null){
	    message.append(line);
	}
	
	}catch(Exception e){
		e.printStackTrace();
	}
	if("".equals(message.toString())){
		return null;
	}
	else {
		return message.toString();
	}
	
}
	
	
	
	
	
	
}
