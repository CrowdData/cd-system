import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

import core.ResourceProvider;


public class ResourceProviderTest {

	
	
	@Test
	public void testResourceProvider(){
		
	Model m1=	new ResourceProvider().getResource(
				"http://crowddata.abdn.ac.uk/data/datasets/user/1", "user");
	Model m2=	new ResourceProvider()
				.getResource("http://crowddata.abdn.ac.uk/data/datasets/user/1");
	
	Model m3=	new ResourceProvider().getResource(
			"http://crowddata.abdn.ac.uk/data/datasets/user/1", "notexistingnamedgraph");

	Model m4=	new ResourceProvider().getResource(
			"http://crowddata.abdn.ac.uk/data/datasets/user/nonexistinguser");
	
	Assert.assertFalse(m1.isEmpty());
	Assert.assertFalse(m2.isEmpty());
	Assert.assertTrue(m3.isEmpty());
	Assert.assertTrue(m4.isEmpty());
	
		
		
	}
	
}
