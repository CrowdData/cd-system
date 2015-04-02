package core;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DemandStuff {

	
	
	
	
	
	
	public JSONArray getBusChoices(){
		
		String d2r="http://crowddata.abdn.ac.uk:8080/d2rq/sparql";
		JSONArray choices=new JSONArray();
		
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
		
		String location="http://crowddata.abdn.ac.uk/query/sparql";
	
		 String getTypeChoiceQeury=	"SELECT ?label ?value " +
				"               WHERE " + 
				"               { " + 
				"               ?value a inc:Incident; " + 
				"               dcterms:title ?label; . " + 
				"               }";

		 ParameterizedSparqlString query=new ParameterizedSparqlString();
		 	query.setCommandText(getTypeChoiceQeury);	 	
		 	
		 	Queries.populateQuery(query,null);
		
		ResultSet r=Repository.selectQuery(query.asQuery().toString(),location);
		return getChoices(r);
		
		
		
	}
	
	
	public JSONArray getIncidentStatusChoices()
	{
		Model m=ModelFactory.createDefaultModel();
		m.read("http://crowddata.abdn.ac.uk/ontologies/incident.ttl");
		
		 String getTypeChoiceQeury=	"SELECT ?label ?value " +
				"               WHERE " + 
				"               { " + 
				"               ?value a inc:IncidentStatus; " + 
				"               rdfs:label ?label; " + 
				"               }";

		
		ResultSet r=Queries.selectQueryModel(m, getTypeChoiceQeury, null);
		return getChoices(r);
		
		
		
	}
	
	
	
	public static void main (String args[]){
		JSONArray a=new DemandStuff().getRelatedIncidents();
		JSONArray b=new DemandStuff().getIncidentTypeChoices();
	//	for(int i=0; i<a.length();i++ ){
	//		System.out.println(a.getJSONObject(i).toString(5));
			
	//	}
	System.out.println(	a.toString(5) +"\n\n\n\nTYPE\n\n\n"+b.toString(5) );
		
	}
	
	
	
	
	
	
}





