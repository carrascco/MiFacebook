package miFacebookClient.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MessageList {
  private List<Message> l;
  
  public MessageList(){
    
  }
  public MessageList (List<Message> l){
    this.l = l;
  }
  public List<Message> getL() {
    return l;
  }
  public void setL(List<Message> l) {
    this.l = l;
  }
  public String toString() {
	  return l.toString();
  }
}
