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

		ParameterizedSparqlString query = new ParameterizedSparqlString();
		query.setCommandText(Queries.SELECT_DESCRIBE_QUERY);

		query.setIri("resource", uri);
		query.setIri("graph", dataNG);

		System.out.println(query.toString());
		ResultSet result = Repository.selectQuery(query.asQuery().toString());

		return parseDescribe(result, uri);

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

		return parseDescribe(result, uri);
	}

	private Model parseDescribe(ResultSet result, String resourceURI) {

		Model resourceDescription = ModelFactory.createDefaultModel();
		while (result.hasNext()) {
			QuerySolution sol = result.next();
			// if first union graph
			if (sol.contains("p1")) {
				Statement s = ResourceFactory.createStatement(ResourceFactory
						.createResource(resourceURI), ResourceFactory
						.createProperty(sol.get("p1").toString()), sol
						.get("o1"));
				resourceDescription.add(s);
			}
			// if second union graph
			if (sol.contains("s2")) {
				Statement s = ResourceFactory.createStatement(sol
						.getResource("s2"), ResourceFactory.createProperty(sol
						.get("p2").toString()), ResourceFactory
						.createResource(resourceURI));
				resourceDescription.add(s);
			}
		}

		return resourceDescription;

	}

	public static void main(String[] args) {
		new ResourceProvider().getResource(
				"http://crowddata.abdn.ac.uk/data/datasets/user/1", "user");
		new ResourceProvider()
				.getResource("http://crowddata.abdn.ac.uk/data/datasets/user/1");
	}

}
