package core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/*
 * Modified example of url reader:
https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
*/
public class URLReader {
	
	
	
	public static String readURL(String url){
		String output="";
		try{
		URL oracle = new URL(url);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(oracle.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null){
            output+=inputLine;
        }
        in.close();
		return output;
		
		
	}
		catch(Exception e){
			throw new NullPointerException("Could not read url");
		}
		
		
}
}
	
	

