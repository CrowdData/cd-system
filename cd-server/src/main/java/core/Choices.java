package core;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Choices {

	
	public JSONArray getBusChoices(){
		
		String d2r="http://crowddata.abdn.ac.uk:8080/d2rq/sparql";
		
		ResultSet r=Repository.selectQuery(Queries.getBuses, d2r);
		return getChoices(r);
	
	}
	public static JSONArray getChoices(ResultSet r){
		JSONArray choices=new JSONArray();
		while(r.hasNext()){
			QuerySolution s=r.next();
			JSONObject obj=new JSONObject();
			if(s.getResource("value").isAnon()){
				obj.put("value", s.getResource("value").asNode().getBlankNodeId());
			}
			else{
			obj.put("value", s.getResource("value").getURI());
			}
			obj.put("label", s.getLiteral("label").getString());
			choices.put(obj);
		}
		return choices;
	}
	
	public JSONArray getIncidentTypeChoices()
	{
		Model m=ModelFactory.createDefaultModel();
		m.read("http://crowddata.abdn.ac.uk/ontologies/incident.ttl");
		
		 String getTypeChoiceQeury=	"SELECT ?label ?value " +
				"               WHERE " + 
				"               { " + 
				"               ?value a inc:Report; " + 
				"               rdfs:label ?label; " + 
				"               }";

		
		ResultSet r=Queries.selectQueryModel(m, getTypeChoiceQeury, null);
		return getChoices(r);
		
		
		
	}
	
	public static JSONArray getRelatedIncidents()
	{
		
		String location=Strings.FUSEKI_QUERY_URI;
	
		 String getTypeChoiceQeury=	"SELECT ?label ?value FROM <http://crowddata.abdn.ac.uk/datasets/incidents/data/> " +
				"               WHERE " + 
				"               { " + 
				"               ?value a inc:Incident; " + 
				"               dcterms:title ?label; . " + 
				"               }";	
		 	
		 	String query=Queries.populateQuery(getTypeChoiceQeury,null,false);
		
		ResultSet r=Repository.selectQuery(query,location);
		return getChoices(r);
		
		
		
	}
	
	
	public JSONArray getIncidentStatusChoices()
	{
		Model m=ModelFactory.createDefaultModel();
		m.read("http://crowddata.abdn.ac.uk/ontologies/event.n3");
		
		 String getTypeChoiceQeury=	"SELECT ?label ?value " +
				"               WHERE " + 
				"               { " + 
				"               ?value a ev:Department; " + 
				"               rdfs:label ?label; " + 
				"               }";

		
		ResultSet r=Queries.selectQueryModel(m, getTypeChoiceQeury, null);
		return getChoices(r);
		
		
		
	}
	public static JSONArray getRelatedEvents()
	{
		String location=Strings.FUSEKI_QUERY_URI;
		
		 String getTypeChoiceQeury=	"SELECT ?label ?value FROM <http://crowddata.abdn.ac.uk/datasets/eventsv2/data/>" +
				"               WHERE " + 
				"               { " + 
				"               ?value a ev:IITBEvent; " + 
				"               dcterms:title ?label; . " + 
				"               }";

		
		 	String query=Queries.populateQuery(getTypeChoiceQeury,null,false);
		
		ResultSet r=Repository.selectQuery(query,location);
		return getChoices(r);
		
		
	}
	
	
	
	
	
	public static void main (String args[]){
		JSONArray a=new Choices().getRelatedEvents();
		JSONArray b=new Choices().getIncidentStatusChoices();
	//	for(int i=0; i<a.length();i++ ){
	//		System.out.println(a.getJSONObject(i).toString(5));
			
	//	}
	System.out.println(	a.toString(5) +"\n\n\n\nTYPE\n\n\n"+b.toString(5) );
		
	}
	
	
	
	
	
	
}





