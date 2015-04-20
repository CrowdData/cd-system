package pojo;
import javax.xml.bind.annotation.XmlRootElement;
/**
 * Pojo class for message handling between server and client side
 * @author Stanislav Beran
 *
 */
@XmlRootElement
public class Message {
	
	int status;
	String message;
	String additional;
public Message(){}

public Message(int status, String message){
	this.message =message;
	this.status=status;
}
	public String getAdditional(){
		return additional;
	}
	public void setAdditional(String ad){
		additional=ad;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
