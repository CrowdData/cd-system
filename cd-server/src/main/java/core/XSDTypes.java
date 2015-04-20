package core;

import java.util.ArrayList;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
/**
 * 
 * @author Stanislav Beran
 *
 */
public class XSDTypes {

	
	
	
	static String[] supportedTypes={"anyURI","boolean","byte","date","dateTime","decimal","double","duration","float",
	                         "int","integer","language","long","string"};
	
	
	public static ArrayList<String> getTypes(String o){
		
		ArrayList<String> validTypes=new ArrayList<String>();
		
		for(String type: supportedTypes){
			XSDDatatype xsdtype=new XSDDatatype(type);
			if(xsdtype.isValid(o)){
				validTypes.add(xsdtype.getURI());
				System.out.println("Value "+ o.toString()+" type: "+ xsdtype.getURI());
			}
			
			
		}
		return validTypes;
		
	}
		
		
		
	public static void main(String args[]){
		
		XSDTypes.getTypes("classic string");
		XSDTypes.getTypes("1.1");
		XSDTypes.getTypes("2014-01-01");
		XSDTypes.getTypes("-34.00493377");
	
		
	}
		
		
		
		
		
		
}
