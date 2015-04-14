package core;

import java.util.HashMap;

import org.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class UserHandler {
static Model m=ModelFactory.createDefaultModel();	
//public static HashMap<String,Resource> awaitingUsers=new HashMap<String,Resource>();
static String awaitinguserDS=NGHandler.getDataString("users");
static Property token=ResourceFactory.createProperty("http://crowddata.abdn.ac.uk/def/user/token");	
static Property name=ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/name");		
static Property email=ResourceFactory.createProperty("http://xmlns.com/foaf/0.1/email");
static Resource PersonClass=ResourceFactory.createResource("http://xmlns.com/foaf/0.1/Person");
static Resource AgentClass=ResourceFactory.createResource( "http://xmlns.com/foaf/0.1/Agent");
static Resource EntityClass=ResourceFactory.createResource( "http://xmlns.com/foaf/0.1/Entity");
static Resource provAgentClass=ResourceFactory.createResource( "http://www.w3.org/ns/prov#Agent");	

	public static String createUser(String nameStr, String emailStr){
		
		Model user=getUser(emailStr);
		if(user.isEmpty()){
			
			
		
		String tokenID=Tools.generateID();
		
		Model userModel=ModelFactory.createDefaultModel();
		Resource newUser=userModel.createResource(NGHandler.getResourceString("users", tokenID));
		
		newUser.addProperty(RDF.type,PersonClass );
		newUser.addProperty(RDF.type, AgentClass);
		newUser.addProperty(RDF.type, EntityClass);
		newUser.addProperty(RDF.type, provAgentClass);
		
		newUser.addProperty(name, nameStr, XSDDatatype.XSDstring);
		newUser.addProperty(email,ResourceFactory.createResource("mailto:"+emailStr));
		
	
		userModel.write(System.out,"TTL");
		
		Repository.addModel(userModel, awaitinguserDS);
		sendMail(nameStr,tokenID,emailStr);
		return "Your account has been created, please check your email and paste the activation code in to the activation form below.";
	}
		//only one
		String userID=getUserID(user);
		
		sendMail(nameStr,userID,emailStr);
		return "Your activation code has been sent to your email.";
	}
	
	public static String getUserID(Model user){
		ResIterator it=user.listSubjects();
		String userURI=it.next().getURI();
		String userID=Tools.getResourceID(userURI);
		return userID;
	}
	
	
	
	public static void sendMail(String nameStr,String token,String emailStr){
		
		String body="Dear " +nameStr+",<br/><br/> Please paste the following activation code into the form on the IITB Life app to gain access to the system. <br/>"+
		"<br/><br/> <b>Your activation code</b> : "+token;
				
		
		SendMailTLS.sendMail(emailStr, "IITB Life Activation Code", body);
		
		
	}
	
	public static Model getUser(String email){
		
		String query="DESCRIBE  ?user FROM <http://crowddata.abdn.ac.uk/datasets/users/data/> WHERE {?user <http://xmlns.com/foaf/0.1/email> <mailto:"+email+"> . }";
		Model m=Repository.describeQuery(query);
		return m;
	}
	
	
	public static void main(String[] args){
		System.out.println(createUser("user","s.beran@abdn.ac.uk"));
	}
	
	
}
