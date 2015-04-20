package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * This class handles encoding and decoding of various serializations of RDF by
 * use of JENA API.
 * 
 * @author Stanislav Beran
 * 
 */
public class RDFSerializer {

	/**
	 * 
	 * @param m
	 *            - Jena Model to be transformed to String
	 * @param type
	 *            - type of the serialization
	 * @return String output of serialized Model
	 */
	public static String getRDFStringFromModel(Model m, String type) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		m.write(output, type);
		try {
			String out = output.toString("UTF8");
			System.out.println(out);
			return out;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(String.format(
					"Encoding %s is not supported", type));
		}

	}

	/**
	 * 
	 * @param input
	 *            - RDF input in supported format
	 * @param type
	 *            - Type of serialization
	 * @return - Model object of the input
	 */
	public static Model inputToRDFType(String input, String type) {

		Model m = ModelFactory.createDefaultModel();
		m.read(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
				null, type);
		m.write(System.out, "JSON-LD");

		return m;
	}

}
