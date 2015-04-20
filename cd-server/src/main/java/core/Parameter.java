package core;

import com.hp.hpl.jena.datatypes.RDFDatatype;
/**
 * Used with SPARQL templates
 * @author stan
 *
 */
public class Parameter {

	String bind;
	String value;
	RDFDatatype type;
	/**
	 * Constructor to create new parameter if RDFDatatype is null system assumes value is an IRI
	 * @param bind - SPARQL template variable
	 * @param value - value of the sparql template variable
	 * @param type - type of the variable if literal
	 */
	public Parameter(String bind,String value, RDFDatatype type){
		this.bind=bind;
		this.value=value;
		this.type=type;
		
		
	}
}
