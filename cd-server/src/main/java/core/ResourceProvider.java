package core;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class ResourceProvider {

	/*
	 * This method describes resource only within current graph
	 * 
	 * @param uri - Resource uri
	 */

	public Model getResource(String uri, String dsID) {
		String dataNG = NGHandler.getDataString(dsID);
		
		return ResultSetParser.parseDescribe(Queries.describeQuery(uri, dataNG),uri);  //the uri is passed as a hack to identify resource

	}

	/*
	 * This method searches for a resource in the whole model
	 * 
	 * @param uri - Resource uri
	 */
	public Model getResource(String uri) {
		ParameterizedSparqlString query = new ParameterizedSparqlString();
		query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", uri);

		System.out.println(query.toString());
		// query fuseki server
		ResultSet result = Repository.selectQuery(query.asQuery().toString());

		return ResultSetParser.parseDescribe(result, uri);
	}
	

	public static void main(String[] args) {
		new ResourceProvider().getResource(
				"http://crowddata.abdn.ac.uk/data/datasets/user/1", "user");
		new ResourceProvider()
				.getResource("http://crowddata.abdn.ac.uk/data/datasets/user/1");
	}

}
