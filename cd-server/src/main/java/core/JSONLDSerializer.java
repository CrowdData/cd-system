package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class JSONLDSerializer {

	
	
	
	
	public static String getJSONLD (Model m){
		
		ByteArrayOutputStream output = new ByteArrayOutputStream ();
		m.write(output,"JSON-LD");
		try {
			String out=output.toString("UTF8");
			System.out.println(out);
			return out;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "error";
		}
		
	}
	
	
	public void test(String input){
		
		Model m=ModelFactory.createDefaultModel();
		m.read(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),null,"JSON-LD");
		m.write(System.out,"JSON-LD");
		
	}
	
	
	public static void main(String args[]){
		
		Model m=ModelFactory.createDefaultModel();
		m.read("http://www.w3.org/ns/prov");
		
		JSONLDSerializer serializer=new JSONLDSerializer();
		String result=JSONLDSerializer.getJSONLD(m);
		serializer.test(result);
		
		
		
	}
	
}
