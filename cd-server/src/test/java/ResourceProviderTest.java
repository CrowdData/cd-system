import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import core.ResourceProvider;


public class ResourceProviderTest {

	
	
	@Test
	public void testResourceProvider(){

	Model m1=	new ResourceProvider().getResource(
			"http://crowddata.abdn.ac.uk/data/datasets/user/nonexistinguser");
	Assert.assertFalse(m1.containsResource(ResourceFactory.createResource("http://crowddata.abdn.ac.uk/data/datasets/user/1")));
	Assert.assertFalse(m1.contains(null, null, "Test User 1"));
	
		
		
	}
	/*
	@Test
	public void test1(){
		
	Model m1=	new ResourceProvider().getResource(
				"http://crowddata.abdn.ac.uk/data/datasets/user/1", "user");
	Assert.assertTrue(m1.containsResource(ResourceFactory.createResource("http://crowddata.abdn.ac.uk/data/datasets/user/1")));
	Assert.assertTrue(m1.contains(null, null, "Test User 1"));
	
	}	
	
	@Test
	public void test2(){
		
		Model m1=	new ResourceProvider()
		.getResource("http://crowddata.abdn.ac.uk/data/datasets/user/1");
	Assert.assertTrue(m1.containsResource(ResourceFactory.createResource("http://crowddata.abdn.ac.uk/data/datasets/user/1")));
	Assert.assertTrue(m1.contains(null, null, "Test User 1"));
	
	}*/
	@Test
	public void test3(){
		
		Model m1=	new ResourceProvider().getResource(
				"http://crowddata.abdn.ac.uk/data/datasets/user/1", "notexistingnamedgraph");
	Assert.assertFalse(m1.containsResource(ResourceFactory.createResource("http://crowddata.abdn.ac.uk/data/datasets/user/1")));
	Assert.assertFalse(m1.contains(null, null, "Test User 1"));
	
	}
	
	
}
