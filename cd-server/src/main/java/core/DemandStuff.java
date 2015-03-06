package core;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class DemandStuff {

	
	
	
	
	
	
	public JSONArray getBusChoices(){
		
		String d2r="http://crowddata.abdn.ac.uk:8080/d2rq/sparql";
		JSONArray choices=new JSONArray();
		
		ResultSet r=Repository.selectQuery(Queries.getBuses, d2r);
		
		while(r.hasNext()){
			QuerySolution s=r.next();
			JSONObject obj=new JSONObject();
			obj.put("value", s.getResource("value").getURI());
			obj.put("label", s.getLiteral("label").getString());
			choices.put(obj);
		}
		return choices;
		
		
		
		
	}
	
	public static void main (String args[]){
		JSONArray a=new DemandStuff().getBusChoices();
	//	for(int i=0; i<a.length();i++ ){
	//		System.out.println(a.getJSONObject(i).toString(5));
			
	//	}
	System.out.println(	a.toString(5));
		
	}
	
	
	
	
	
	
}





