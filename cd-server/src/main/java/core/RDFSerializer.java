package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFSerializer {

	
	
	
	
	public static String getJSONLD (Model m,String type){
		
		ByteArrayOutputStream output = new ByteArrayOutputStream ();
		m.write(output,type);
		try {
			String out=output.toString("UTF8");
			System.out.println(out);
			return out;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(String.format("Encoding %s is not supported",type));
		}
		
	}
	
	
	public static Model inputToJSONLD(String input, String type){
		
		Model m=ModelFactory.createDefaultModel();
		m.read(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),null,type);
		m.write(System.out,"JSON-LD");
		return m;
	}
	
	
	public static void main(String args[]){
		
		Model m=ModelFactory.createDefaultModel();
		m.read("http://www.w3.org/ns/prov");
		
		
		
		
		
	}
	
}
