package core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.ResourceUtils;

/**
 * Class holding various convenience methods used throughout the whole web
 * application. It also holds methods that have not been moved to their own
 * packages
 * 
 * @author Stanislav Beran
 * 
 */
public class Tools {

	public static Model getModel(String url) {
		Model m = ModelFactory.createDefaultModel();
		m.read(url);
		return m;
		// String jsonld=serialize.getJSONLD(m);
	}

	/**
	 * Generates randomID (shorther than Java's UUID)
	 * 
	 * @return
	 */
	public static String generateID() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
		return Base64.encodeBase64URLSafeString(buffer.array());
	}

	/**
	 * 
	 * @param m
	 *            - model containing triples with blank nodes to be renamed
	 *            (used when client creates,updates new resource)
	 */
	public static void renameBlankNodes(Model m) {
		ResIterator r = m.listSubjects();
		while (r.hasNext()) {
			Resource res = r.next();
			if (res.isAnon()) {
				ResourceUtils.renameResource(
						res,
						"http://crowddata.abdn.ac.uk/resources/"
								+ Tools.generateID());
				System.out.println("Renamed blank node");
			}
		}

	}

	// recursive done check execution time server
	static long time;

	/**
	 * Method used to retrieve(based on Describe) root resource and it's
	 * subsequent children as well If resource contains objects it searches the
	 * whole repository for triples not only in the particular named graph. This
	 * is required since we are renaming blanknodes into IRI's, therefore
	 * describe query on parent resource only is not sufficient.
	 * 
	 * @param m
	 *            - resource represented as a model 
	 */
	public static void buildChildren(Model m) {
		NodeIterator it = m.listObjects();
		while (it.hasNext()) {
			RDFNode r = it.next();
			if (r.isURIResource()) {
				long start = System.currentTimeMillis();
				Model sibling = Queries.getDescribe(r.asResource().getURI(),
						null);
				long end = System.currentTimeMillis();
				time += (end - start);
				buildChildren(sibling);
				m.add(sibling);

			}

		}
	}

	/**
	 * Method checks if choice items of RDF template have associated any dynamic
	 * choices retrieved from elsewhere.
	 * 
	 * @param template
	 *            - RDForm template json representation
	 */
	public static void checkForChoices(JSONObject template) {
		JSONArray a = template.getJSONArray("templates");
		for (int i = 0; i < a.length(); i++) {
			JSONObject j = a.getJSONObject(i);
			if (j.get("type").equals("choice")) {
				checkForChoice(j);
				System.out.println("Is choice");
			}
		}

	}

	/**
	 * TODO rewrite to have rdftemplate id items unique and retrieve sparql
	 * query to get choices
	 * 
	 * @param j
	 */
	private static void checkForChoice(JSONObject j) {
		String id = j.getString("id");
		System.out.println("ID choice " + id);
		if (id.equals("related_incident")) {
			System.out.println("Related incident stuff");
			JSONArray a = Choices.getRelatedIncidents();
			if (a.length() > 0) {
				System.out.println("Related incidentds NOT ZERO");
				// remove invisible always first in styles
				j.getJSONArray("styles").remove(0);
				j.put("choices", a);
			}

		} else if (id.equals("related_event")) {
			System.out.println("Related event");
			JSONArray a = Choices.getRelatedEvents();
			if (a.length() > 0) {
				System.out.println("Related events NOT ZERO");
				// remove invisible always first in styles
				j.getJSONArray("styles").remove(0);
				j.put("choices", a);
			}

		}

	}

	/**
	 * Toggle between CREATE and UPDATE of RDForm template. It is required to
	 * change nodetype to appropriate one BLANK when creating resources URI when
	 * updating resources
	 * 
	 * @param template
	 *            -RDForm template representation
	 * @param type
	 *            - String BLANK or URI
	 */
	public static void changeRootTypes(JSONObject template, String type) {
		JSONArray a = template.getJSONArray("templates");
		for (int i = 0; i < a.length(); i++) {
			JSONObject j = a.getJSONObject(i);
			if (j.getString("type").equals("group")) {
				j.put("nodetype", type);
			}
		}
	}

	/**
	 * From IRP
	 * 
	 * @param uri
	 * @return
	 */
	public static String getNamespace(String uri) {
		if (uri.contains("#")) {
			return uri.substring(0, uri.lastIndexOf("#"));
		}
		if (uri.contains("/")) {
			return uri.substring(0, uri.lastIndexOf("/") + 1);
		}
		return uri;
	}

	/**
	 * Custom defined method to update current version of a resource, it relies
	 * for a resource to be ending with a decimal number, if it appends /1 to
	 * the uri
	 * 
	 * @param uri
	 *            - IRI to increment version of the resource
	 * @return new IRI with incremented version
	 */
	public static String getNewVersion(String uri) {
		if (uri.matches(".*\\d")) {
			String version = uri.substring(uri.lastIndexOf("/") + 1,
					uri.length());
			String base = uri.substring(0, uri.lastIndexOf("/") + 1);
			int ver = Integer.parseInt(version);
			ver++;
			return base + ver;

		}
		// is new resource not being updated
		return uri + "/1";
	}

	public static String getResourceID(String resourceURI) {
		return resourceURI.substring(resourceURI.lastIndexOf("/") + 1,
				resourceURI.length());
	}

	/**
	 * Convenience method to create bindings for SPARQL templates
	 * 
	 * @param params
	 *            - bindings objects @see Parameter
	 * @return
	 */
	public static ArrayList<Parameter> getParameters(Parameter... params) {
		ArrayList<Parameter> paraArray = new ArrayList<Parameter>();
		for (Parameter p : params) {
			paraArray.add(p);
		}

		return paraArray;
	}

	/*
	 * public static String getDataSetSchema(String prefix,String resource,
	 * String vocabulary){ Model m=Tools.getModel(vocabulary); Model
	 * resourceDesc
	 * =Tools.getResourceDescription(Prefixes.prefixes.get(prefix)+resource, m);
	 * 
	 * return JSONLDSerializer.getJSONLD(resourceDesc);
	 * 
	 * 
	 * 
	 * } public static String getDataSetSchema(String resource, String
	 * vocabulary){ Model m=Tools.getModel(vocabulary); Model
	 * resourceDesc=Tools.getResourceDescription(resource, m);
	 * 
	 * return JSONLDSerializer.getJSONLD(resourceDesc);
	 * 
	 * 
	 * 
	 * }
	 *//*
		 * returns- Model, collection of triples of properties, which has
		 * resourceURI as their rdfs:domain
		 * 
		 * public static Model getDomainResources(String resourceURI,Model m){
		 * Resource res=ResourceFactory.createResource(resourceURI);
		 * System.out.println(resourceURI); Model
		 * out=ModelFactory.createDefaultModel();
		 * 
		 * ParameterizedSparqlString query=new ParameterizedSparqlString();
		 * query.setCommandText( "" + "SELECT ?property " + "WHERE {" +
		 * " ?property rdfs:domain ?resource ." + "   }");
		 * query.setParam("resource",res);
		 * query.setNsPrefixes(Prefixes.prefixes);
		 * 
		 * System.out.println(query.asQuery().toString()); QueryExecution
		 * qExec=QueryExecutionFactory.create(query.asQuery(),m);
		 * 
		 * ResultSet rs = qExec.execSelect() ; try { while(rs.hasNext()){
		 * 
		 * QuerySolution cap=rs.next(); String
		 * resource=cap.getResource("property").toString();
		 * System.out.println(resource);
		 * out.add(getResourceDescription(resource,m)); }
		 * 
		 * }
		 * 
		 * catch(Exception e){ e.printStackTrace();
		 * 
		 * } finally { qExec.close();} return out;
		 * 
		 * 
		 * 
		 * }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * public static void main (String args[]){
		 * System.out.println(Tools.getNamespace
		 * ("http://xmlns.com/foaf/0.1/Person")); //
		 * System.out.println(Tools.getDomainResources
		 * ("http, "http://purl.org/dc/terms/")); //
		 * System.out.println(Tools.getDataSetSchema
		 * ("http://purl.org/dc/elements/1.1/description"
		 * ,"http://purl.org/dc/elements/1.1/")); }
		 */

	public static void main(String[] args) {
		System.out.println(Tools
				.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf/1"));

		System.out.println(Tools
				.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf"));

		System.out.println(Tools
				.getNewVersion("http://asdfa.com/asdfasdf/djfkwwlf/1cd"));

	}

}
