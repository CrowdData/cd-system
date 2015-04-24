package core;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * 
 * @author stan
 * This class handles retrieval of RDForms templates from default location
 *
 */
public class RDFormTemplateProvider {
	static RDFormTemplateProvider provider=null;
	HashMap<String,String> templateMappings;
	
	static {
		init();
	}
	/**
	 * 
	 * @return RDFormTemplateProvider singleton instance
	 */
	public static RDFormTemplateProvider getInstance(){
		return provider;
	}
	/**
	 * Initializes this class when called for first time. TODO Retrieve templates from dedicated dataset.
	 */
	public static void init(){
		
		//TODO :get mappings from the fuseki 
		provider=new RDFormTemplateProvider();
		
		provider.templateMappings=new HashMap<String,String>();
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Demand",Strings.templateLocation+"demand-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Event",Strings.templateLocation+"event-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Feedback",Strings.templateLocation+"feedback-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Response",Strings.templateLocation+"response-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Question",Strings.templateLocation+"question-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Disruption",Strings.templateLocation+"disruption-template.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/def/incidents/Report",Strings.templateLocation+"disruption-template-new.json");
		provider.templateMappings.put("http://crowddata.abdn.ac.uk/def/events/IITBEvent",Strings.templateLocation+"event-template-new.json");
	}
	/**
	 * This method returns RDForm template based on the requested type and checks if multiple choices are existent for any of the templates
	 * item.  
	 * @param type - type of the resource 
	 * @return JSONObject RDForm template
	 */
	public JSONObject getTemplate(String type){
		

		String urlLocation=getURLLocation(type);
		String templateJson=URLReader.readURL(urlLocation);
		JSONObject template=new JSONObject(templateJson);
		Tools.checkForChoices(template);
		return template;
		
	}
	
	
	
	
	/**
	 *  Return absolute path of the location where RDForm is located
	 * @param resourceType - type of the resource
	 * @return String absolute path of the RDForm template 
	 */
	public String getURLLocation(String resourceType){
		
		if(!templateMappings.containsKey(resourceType)){
			throw new NullPointerException("Requested template doesn't exist...");
		}
		return templateMappings.get(resourceType);
		
	}
	
	public static void main (String[] args){
		System.out.println(RDFormTemplateProvider.getInstance().getTemplate("http://crowddata.abdn.ac.uk/ontologies/cd/0.1/Disruption").toString(5));
		
		
	}
	
	
	
	
	
	
	
}
