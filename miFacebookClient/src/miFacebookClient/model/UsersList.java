package miFacebookClient.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UsersList {
	  private List<User> l;
	  
	  public UsersList(){
	    
	  }
	  public UsersList (List l){
	    this.l = l;
	  }
	  public List<User> getL() {
	    return l;
	  }
	  public void setL(List<User> l) {
	    this.l = l;
	  }
	  public String toString() {
		  return l.toString();
	  }
}