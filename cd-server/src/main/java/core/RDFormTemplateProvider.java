package core;

import java.util.HashMap;

import org.json.JSONObject;

public class RDFormTemplateProvider {
	static RDFormTemplateProvider provider=null;
	static String defaultLocation="http://crowddata.abdn.ac.uk/templates/";
	HashMap<String,String> templateMappings;
	
	static {
		init();
	}
	
	public static RDFormTemplateProvider getInstance(){
		return provider;
	}
	public static void init(){
		
		//TODO :get mappings from the fuseki 
		provider=new RDFormTemplateProvider();
		
		provider.templateMappings=new HashMap<String,String>();
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Demand",defaultLocation+"demand-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Event",defaultLocation+"event-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Feedback",defaultLocation+"feedback-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Response",defaultLocation+"response-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Question",defaultLocation+"question-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Disruption",defaultLocation+"disruption-template.json");
		
		
	}
	
	public JSONObject getTemplate(String type){
		
		if(!templateMappings.containsKey(type)){
			throw new NullPointerException("Requested template doesn't exist...");
		}
		
		String urlLocation=templateMappings.get(type);
		String templateJson=URLReader.readURL(urlLocation);
		return new JSONObject(templateJson);
		
	}
	
	public static void main (String[] args){
		System.out.println(RDFormTemplateProvider.getInstance().getTemplate("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Disruption").toString(5));
		
		
	}
	
	
	
	
	
	
	
}
