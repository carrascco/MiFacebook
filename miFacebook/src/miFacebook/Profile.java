package miFacebook;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Profile {
	private User user;
	private Message userMessage;
	private int Fnum;
	private MessageList friendsMessages;
	
	public Profile() {
		
	}
	
	public Profile(User user, Message userMessage, int Fnum, MessageList friendsMessages) {
		this.user = user;
		this.userMessage = userMessage;
		this.Fnum = Fnum;
		this.friendsMessages = friendsMessages;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Message getUserMessage() {
		return userMessage;
	}
	public void setUserMessage(Message userMessage) {
		this.userMessage = userMessage;
	}
	public int getFnum() {
		return Fnum;
	}
	public void setFnum(int fnum) {
		Fnum = fnum;
	}
	public MessageList getFriendsMessages() {
		return friendsMessages;
	}
	public void setFriendsMessages(MessageList friendsMessage) {
		this.friendsMessages = friendsMessage;
	}
	public String toString() {
		String perfil="----------------------------------------------------------------------";
		perfil+=user+"\n\n";
		if(userMessage==null) {
			perfil+="No ha publicado mensajes todavía.\n\n";
		}else {
			perfil+="Último mensaje: "+userMessage+"\n\n";
		}
		perfil+="Numero de amigos: " + Fnum +"\n\n" ;
		if(friendsMessages==null) {
			perfil+="Sus amigos no han publicado mensajes. \n";
		}else {
			perfil+="Mensajes de amigos:" +friendsMessages + "\n";
		}
		perfil+="----------------------------------------------------------------------\n";
		return perfil;
	}
	
}
