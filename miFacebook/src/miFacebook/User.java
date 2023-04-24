package miFacebook;

import java.net.URL;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class User {
	private int id;
	private String nombre;
	private String apellidos;
	private URL href;
	
	public User(String nombre, String apellidos) {
		this.nombre = nombre;
		this.apellidos = apellidos;
	}
	public User() {
		
	}

	@XmlAttribute(required=false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public URL getHref() {
		return href;
	}
	public void setHref(URL href) {
		this.href = href;
	}
	
	public String toString() {
		return "\n{\n\tId: " + id + ", Nombre: " + nombre + ", Apellidos: " + apellidos + "\n\t" + "Href: " + href + "\n}\n";
		
	}
	
}
