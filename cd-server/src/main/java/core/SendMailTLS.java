package core;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * Class responsible for sending emails to registered users 
 * @author stan
 *
 */
public class SendMailTLS {

	
	 public static void sendMail(String recipient,String title, String body){
		 
		 
			HttpClient httpClient = new DefaultHttpClient();
		 try {

			//   System.getProperties().put("http.proxyHost","proxy.abdn.ac.uk");
			//   System.getProperties().put("http.proxyPort",8080);
			    String mailrequest="http://homepages.abdn.ac.uk/s.beran/pages/mail.php";
			    HttpPost request = new HttpPost(mailrequest);
			  //  StringEntity params = new StringEntity(body);
			    String to=recipient;
			    String subject=title;
			    String message=body;
    
			    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			      nameValuePairs.add(new BasicNameValuePair("to", to));
			      nameValuePairs
			          .add(new BasicNameValuePair("subject", subject));
			      nameValuePairs.add(new BasicNameValuePair("message",message));
			     

			      request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			   HttpResponse resp= httpClient.execute(request);
			  System.out.println("StatusCode: "+ resp.getStatusLine().getStatusCode()+resp.getEntity().toString());
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 
		 
		 
		 
		
	 }
	
	
	public static void main(String args[]){
		
		new SendMailTLS().sendMail("contact@stanberan.org","Title ","Hello from server.<a href=http://crowddata.abdn.ac.uk/dashboard/verify.php?email=someone@email.com&token=345-353gg4g56g-5g>Verify</a>");
	
		
	}
}