package core;

import com.hp.hpl.jena.datatypes.RDFDatatype;

public class Parameter {

	String bind;
	String value;
	RDFDatatype type;
	
	public Parameter(String bind,String value, RDFDatatype type){
		this.bind=bind;
		this.value=value;
		this.type=type;
		
		
	}
}
