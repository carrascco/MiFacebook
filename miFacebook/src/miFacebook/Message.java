package miFacebook;

import java.net.URL;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
public class Message {
	private int id;
	private int user_id;
	private String content;
	private String fecha;
	private URL href;
	
	public Message(int user_id, String content, String fecha) {
		this.user_id = user_id;
		this.content = content;
		this.fecha = fecha;
	}
	
	public Message(int user_id, String content) {
		this.user_id = user_id;
		this.content = content;
	}
	public Message() {
		
	}

	@XmlAttribute(required=false)
	public int getUserId() {
		return user_id;
	}

	public void setUserId(int user_id) {
		this.user_id = user_id;
	}
	
	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public URL getHref() {
		return href;
	}
	public void setHref(URL href) {
		this.href = href;
	}
	
	public String toString() {
		return "\n{\n\tMessageId: " + id + ", User_Id: " + user_id + ", Content: \"" + content + "\", Fecha: " + fecha + "\n\t" + "Href: " + href + "\n}\n";
	}

	
}
