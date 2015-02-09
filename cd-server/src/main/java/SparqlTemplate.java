import com.hp.hpl.jena.query.QuerySolutionMap;


public class SparqlTemplate {

	String type;
	String template="DESCRIBE ?concept FROM NAMED ?graph WHERE {?s ?p ?o}";
	
	
	
	
	
	QuerySolutionMap bindings;
	public SparqlTemplate(String type){
		this.type=type;
		bindings=new QuerySolutionMap();	
		
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public QuerySolutionMap getBindings() {
		return bindings;
	}
	public void setBindings(QuerySolutionMap bindings) {
		this.bindings = bindings;
	}
	

	
	
	
	
	
}
