package miFacebookClient;

import java.io.BufferedInputStream;
import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import miFacebookClient.model.*;

public class Test {
	static int clientId;
	static int messageNum;
	static int friendNum;
	
	public static void poblarDatosIniciales() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());
		User user;
		user = new User("Pepe", "Botella");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Fernando", "Alonso");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Gabriel", "Marquez");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Ruben", "Bobalicon");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Lucia", "Belindo");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Maria", "Raya");
			target.path("api").path("users").request().post(Entity.xml(user));
		user = new User("Teresa", "Calcuta");
			target.path("api").path("users").request().post(Entity.xml(user));
	}
	
	public static void postUser(WebTarget target, User user) {
		Response r =target.path("api").path("users").request().post(Entity.xml(user));
		System.out.println("Response: "+r);
		User u=r.readEntity(User.class);
		clientId=u.getId();
	}
	
	public static void getUser(WebTarget target, int user_id) {
		System.out.println(target.path("api").path("users").path("" + user_id).request().accept(MediaType.APPLICATION_XML).get(User.class));
	}
	
	public static void updateUser(WebTarget target, int user_id, String nombre, String apellidos) {
		User user=target.path("api").path("users").path("" + user_id).request().accept(MediaType.APPLICATION_XML).get(User.class);
		if(nombre!=null)
			user.setNombre(nombre);
		if(apellidos!=null)
			user.setApellidos(apellidos);
		
		System.out.println(target.path("api").path("users").path(""+user_id).request().put(Entity.xml(user)));
	}
	
	public static void deleteUser(WebTarget target) {
		
		System.out.println(target.path("api").path("users").path("" + clientId).request().delete());
	}
	
	public static void getUserList(WebTarget target, String patron, int limit, int offset) {
		if(patron!=null) {
			System.out.println(target.path("api").path("users").queryParam("patron", patron).queryParam("offset", offset).queryParam("limit", limit).request().accept(MediaType.APPLICATION_XML).get(UsersList.class));
			return;
		}
		System.out.println(target.path("api").path("users").queryParam("offset", offset).queryParam("limit", limit).request().accept(MediaType.APPLICATION_XML).get(UsersList.class));
	}
	
	public static void postMessage(WebTarget target, String content) {
		Message message = new Message(clientId, content);
		System.out.println(target.path("api").path("users").path(""+clientId).path("messages").request().post(Entity.xml(message)));
		
	}
	
	public static void updateMessage(WebTarget target, String messageId, String content) {
		Message m=target.path("api").path("users").path("messages").path(messageId).request().accept(MediaType.APPLICATION_XML).get(Message.class);
		if(m.getUserId()==clientId) {
			System.out.println(target.path("api").path("users").path(""+clientId).path("messages").path(messageId)
					.queryParam("userId", clientId).request().put(Entity.text(content)));
			return;
		}
		System.out.println("El mensaje correspondiente al id que ha introducido, no le pertenece. No puede modificarlo.");
		
	}
	
	public static void deleteMessage(WebTarget target, String messageId) {
		Message m=target.path("api").path("users").path("messages").path(messageId).
				request().accept(MediaType.APPLICATION_XML).get(Message.class);
		if(m.getUserId()==clientId) {
			System.out.println(target.path("api").path("users").path(""+m.getUserId()).path("messages").path(messageId).queryParam("userId", clientId).request().delete());
			return;
		}
		System.out.println("El mensaje correspondiente al id que ha introducido, no le pertenece. No puede modificarlo.");
	}
	
	public static void getMessageByUser(WebTarget target, int userId, int limit, int offset, String fechaIni, String fechaFin) {
		System.out.println(target.path("api").path("users").path(""+userId).path("messages").queryParam("limit", limit).queryParam("offset", offset)
				.queryParam("fechaIni",fechaIni).queryParam("fechaFin", fechaFin)
				.request().accept(MediaType.APPLICATION_XML).get(MessageList.class));
	}
	
	public static void privateMessage(WebTarget target, int receiver, String content) {
		Message m= new Message(clientId, content);
		System.out.println(target.path("api").path("users").path(""+receiver).path("private_messages").request().post(Entity.xml(m)));
	}
	
	public static void getFriendsMessage(WebTarget target, int limit, int offset, String fechaIni, String fechaFin, String pattern) {
		System.out.println(target.path("api").path("users").path(""+clientId).path("friends").path("messages")
				.queryParam("limit", limit).queryParam("offset", offset).queryParam("fechaIni", fechaIni).queryParam("fechaFin", fechaFin).queryParam("patron", pattern)
				.request().accept(MediaType.APPLICATION_XML).get(MessageList.class));
	}
	
	public static void addFriend(WebTarget target, String idAmigo) {
		System.out.println(target.path("api").path("users").path(""+clientId).path("friends").queryParam("userId", idAmigo).request().post(null));
	}

	public static void deleteFriend(WebTarget target, int friendId) {
		System.out.println(target.path("api").path("users").path(""+clientId).path("friends").queryParam("user_id", clientId)
																		.queryParam("friend_id", friendId).request().delete());
	}
	
	public static void getFriendList(WebTarget target,String pattern, int offset, int limit) {
		System.out.println(target.path("api").path("users").path(""+clientId).path("friends")
							.queryParam("limit", limit).queryParam("offset", offset).queryParam("patron", pattern)
							.request().accept(MediaType.APPLICATION_XML).get(UsersList.class));
	}
	
	public static void getUserInfo(WebTarget target) {
		System.out.println(target.path("api").path("users").path(""+clientId).path("info").request().accept(MediaType.APPLICATION_XML).get(Profile.class));
	}
	
	private static void mostrarErrores(WebTarget target) {
		User u;
		u = new User();
		u.setId(1);

		Message m;
		m = new Message();
		m.setUserId(1);

		String content;
		content = "aaa";
		// postUser
		System.out.println("Posibles errores de postUser");
		System.out.println("Peticion: target.path(\"api\").path(\"users\").request().post(Entity.xml(user))");
		System.out.println(
				"El único error que se puede dar aquí sería el 500, Internal Server Error, por como esta hecha la implementación no se debería dar este error\n");

		// getUser
		System.out.println("Posibles errores de getUser");
		System.out.println(
				"Petición: target.path(\"api\").path(\"users\").path(\"\" + user_id).request().accept(MediaType.APPLICATION_XML).get(User.class)");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + 1000000).request()
				.accept(MediaType.APPLICATION_XML).get(Response.class));
		System.out.println("Bad Request: " + target.path("api").path("users").path("" + "a").request()
				.accept(MediaType.APPLICATION_XML).get(Response.class) + "\n");

		// updateUser
		System.out.println("Posibles errores de updateUser");
		System.out.println(
				"Petición: target.path(\"api\").path(\"users\").path(\"\"+user_id).request().put(Entity.xml(user))");
		System.out
				.println("Unauthorized: " + target.path("api").path("users").path("" + 2).request().put(Entity.xml(u)));
		System.out.println(
				"Bad Request: " + target.path("api").path("users").path("" + "a").request().put(Entity.xml(u)) + "\n");

		// deleteUser
		System.out.println("Posibles errores de postUser");
		System.out.println("Peticion: target.path(\"api\").path(\"users\").path(\"\" + clientId).request().delete()");
		System.out
				.println("Not Found: " + target.path("api").path("users").path("" + 1000000).request().delete() + "\n");

		// getUSerList
		System.out.println("Posibles errores de getUserList");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").queryParam(\"patron\", patron).queryParam(\"offset\", offset).queryParam(\"limit\", limit).request().accept(MediaType.APPLICATION_XML).get(UsersList.class)");
		System.out.println("Not Found: "
				+ target.path("api").path("users").queryParam("patron", "zzzzz").queryParam("offset", 0)
						.queryParam("limit", 1).request().accept(MediaType.APPLICATION_XML).get(Response.class)
				+ "\n");

		// postMessage
		System.out.println("Posibles errores de postMessage");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\"+clientId).path(\"messages\").request().post(Entity.xml(message))");
		System.out.println("Unauthrized: "
				+ target.path("api").path("users").path("" + 2).path("messages").request().post(Entity.xml(m)));
		System.out.println("Bad Request: "
				+ target.path("api").path("users").path("" + "a").path("messages").request().post(Entity.xml(m))
				+ "\n");

		// updateMessage
		System.out.println("Posibles errores de updateMessage");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + clientId).path(\"messages\").path(messageId).queryParam(\"userId\", clientId).request().put(Entity.text(content))");
		System.out.println("Unauthrized: " + target.path("api").path("users").path("" + 1).path("messages").path("1")
				.queryParam("userId", 2).request().put(Entity.text(content)));
		System.out.println("Bad Request: "
				+ target.path("api").path("users").path("" + "a").path("messages").request().post(Entity.xml(m))
				+ "\n");

		// deleteMessage
		System.out.println("Posibles errores de deleteMessage");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + m.getUserId()).path(\"messages\").path(messageId).queryParam(\"id\", clientId).request().delete()");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + clientId).path("messages").path("1000000").queryParam("userId", clientId).request().delete());
		System.out.println("Unauthrized: " + target.path("api").path("users").path("" + m.getUserId()).path("messages").path("1").queryParam("userId", 2).request().delete());
		System.out.println("Bad Request: " + target.path("api").path("users").path("" + m.getUserId()).path("messages")
				.path("a").queryParam("userId", 1).request().delete() + "\n");

		// getMessageByUser
		System.out.println("Posibles errores de getMessageByUser");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + userId).path(\"messages\").queryParam(\"limit\", limit).queryParam(\"offset\", offset).queryParam(\"fechaIni\", fechaIni).queryParam(\"fechaFin\", fechaFin).request().accept(MediaType.APPLICATION_XML).get(MessageList.class)");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + 1000000).path("messages")
				.queryParam("limit", 5).queryParam("offset", 0).queryParam("fechaIni", "2004-2-4")
				.queryParam("fechaFin", "2040-2-4").request().accept(MediaType.APPLICATION_XML).get(Response.class)
				+ "\n");

		// privateMessage
		System.out.println("Posibles errores de privateMessage");
		System.out.println(
				"Petición: target.path(\"api\".path(\"users\").path(\"\" + receiver).path(\"private_messages\").request().post(Entity.xml(m))");
		;
		System.out.println("Bad Request: "
				+ target.path("api").path("users").path("" + "a").path("private_messages").request().post(Entity.xml(m))
				+ "\n");

		// getFriendsMessage
		System.out.println("Posibles errores de getMessageByUser");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + userId).path(\"messages\").queryParam(\"limit\", limit).queryParam(\"offset\", offset).queryParam(\"fechaIni\", fechaIni).queryParam(\"fechaFin\", fechaFin).request().accept(MediaType.APPLICATION_XML).get(MessageList.class)");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + 1000000).path("messages")
				.queryParam("limit", 5).queryParam("offset", 0).queryParam("fechaIni", "2004-2-4")
				.queryParam("fechaFin", "2040-2-4").request().accept(MediaType.APPLICATION_XML).get(Response.class)
				+ "\n");

		// addFriend
		System.out.println("Posibles errores de addFriend");
		System.out.println(
				"Petición: target.path(\"api\").path(\"users\").path(\"\" + clientId).path(\"friends\").request().put(Entity.text(idAmigo))");
		;
		System.out.println("Bad Request: "
				+ target.path("api").path("users").path("a").path("friends").request().post(null)
				+ "\n");

		// deleteFriend
		System.out.println("Posibles errores de deleteFriend");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + clientId).path(\"friends\").queryParam(\"user_id\", clientId).queryParam(\"friend_id\", friendId).request().delete()");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + 1).path("friends")
				.queryParam("user_id", 1).queryParam("friend_id", 1000000).request().delete());
		System.out.println("Unauthrized: " + target.path("api").path("users").path("" + 1).path("friends")
				.queryParam("user_id", 2).queryParam("friend_id", 1).request().delete());
		System.out.println("Bad Request: " + target.path("api").path("users").path("" + "a").path("friends")
				.queryParam("user_id", 1).queryParam("friend_id", 1).request().delete() + "\n");

		// getFriendList
		System.out.println("Posibles errores de getFriendList");
		System.out.println(
				"Peticion: target.path(\"api\").path(\"users\").path(\"\" + clientId).path(\"friends\").queryParam(\"limit\", limit).queryParam(\"offset\", offset).queryParam(\"patron\", pattern).request().accept(MediaType.APPLICATION_XML).get(UsersList.class)");
		System.out.println("Not Found: " + target.path("api").path("users").path("" + 10000000).path("friends")
				.queryParam("limit", 5).queryParam("offset", 0).queryParam("patron", "").request()
				.accept(MediaType.APPLICATION_XML).get(Response.class));
		System.out.println("Bad Request: " + target.path("api").path("users").path("a").path("friends")
				.queryParam("limit", 5).queryParam("offset", 0).queryParam("patron", "").request()
				.accept(MediaType.APPLICATION_XML).get(Response.class) + "\n");

		// getUserInfo
		System.out.println("Posibles errores de getUserInfo");
		System.out.println(
				"Petición: target.path(\"api\").path(\"users\").path(\"\" + clientId).path(\"info\").request().accept(MediaType.APPLICATION_XML).get(Profile.class)");
		;
		System.out.println("Not Found: " + target.path("api").path("users").path("1000000").path("info").request()
				.accept(MediaType.APPLICATION_XML).get(Response.class));
		System.out.println("Bad Request: " + target.path("api").path("users").path("" + "a").path("info").request()
				.accept(MediaType.APPLICATION_XML).get(Response.class));
		
	}
	
// FIXME: TRATAR TODOS LOS ERRORES PARA QUE SI EL USUARIO METE ALGO QUE DEVUELVE ERROR, LA EXCEPCION NO CIERRE EL SISTEMA
	public static void main(String[] args) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());		
		try (Scanner sc = new Scanner(new BufferedInputStream(System.in,1024))){
			System.out.println("Bienvenido a MiFacebook. \n Introduce los datos para crear un nuevo usuario.\n Nombre: ");
			String nombre = sc.nextLine();
			System.out.println("Apellidos: ");
			String apellidos = sc.nextLine();
			User user = new User(nombre, apellidos);
			postUser(target, user);
			messageNum=0;
			friendNum=0;
			while(true) {
			int localOffset=0;
			System.out.println("------------------------------------------");
				System.out.println("Que acción quieres realizar?");
				System.out.println("1: Ver lista de usuarios.");
				System.out.println("2: Editar mis datos de usuario.");
				System.out.println("3: Ver mis mensajes publicados.");
				System.out.println("4: Publicar un mensaje.");
				System.out.println("5: Editar un mensaje. (Necesario ID de mensaje)");
				System.out.println("6: Borrar un mensaje. (Necesario ID de mensaje)");
				System.out.println("7: Enviar mensaje privado. (Necesario ID de usuario)");
				System.out.println("8: Ver mi lista de amigos.");
				System.out.println("9: Añadir un amigo. (Necesario ID de usuario)");
				System.out.println("10: Eliminar amigo. (Necesario ID de usuario)");
				System.out.println("11: Ver mensajes de mis amigos.");
				System.out.println("12: Ver mi perfil.");
				System.out.println("13: Eliminar mi usuario y salir del sistema.");
				System.out.println("14: Salir del sistema.");
				System.out.println("------------------------------------------");
				System.out.println("Introduce el numero correspondiente a la acción: ");
				System.out.print("\t");
				switch(sc.nextLine()) {
				case "1":
					//TERMINADO
					int limit=0;
					int offset=0;
					String patron=null;
					System.out.println("¿Desea filtrar por numero de usuarios mostrados? Escriba 'si' o 'no'.");
					String res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el limite");
						limit=sc.nextInt(); sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					
					System.out.println("¿Desea filtrar por patrón de nombre? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el patrón");
						patron=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					while(true) {
						if(limit==0) limit=5;
						String menu="1. Pasar a siguiente página";
						try {
							getUserList(target,patron,limit,localOffset);;
						}catch(Exception e) {
							menu="";
							localOffset-=limit;
							getUserList(target,patron,limit,localOffset);;
							System.out.println("\t\t-----------------------"); 
							System.out.println("\t\t| No hay más páginas. |"); 
							System.out.println("\t\t-----------------------");
						}
						
						System.out.println(menu);
						if(localOffset!=0) {
							System.out.println("2: Volver a anterior página.");
						}
						System.out.println("3: Dejar de ver.");
						res=sc.nextLine();
						if(res.equals("1")) {
							if(menu!="")
								localOffset+=limit;
						}else if(res.equals("2")) {
							if(localOffset!=0)
								localOffset-=limit;
						}else if(res.equals("3")) {
							break;
						}else {
							System.out.println("Error");
							break;							
						}}
					break;
	
				case "2":
					System.out.println("¿Que desea cambiar?");
					System.out.println("1: Nombre.");
					System.out.println("2: Apellidos.");
					System.out.println("3: Ambas.");
					System.out.println("4: Nada, volver atrás.");
					String opt=sc.nextLine();
					if(opt!="4") {
						if(opt.equals("1")) {
							System.out.println("Introduzca nombre: ");
							nombre=sc.nextLine();
							updateUser(target,clientId,nombre, null);
						}else if(opt.equals("2")) {
							System.out.println("Introduzca apellidos: ");
							apellidos=sc.nextLine();
							updateUser(target,clientId,null, apellidos);
							
						}else if(opt.equals("3")) {
							System.out.println("Introduzca nombre: ");
							nombre=sc.nextLine();
							System.out.println("Introduzca apellidos: ");
							apellidos=sc.nextLine();
							
							updateUser(target,clientId,nombre, apellidos);	
						}
					}
					
					break;
	
				case "3": //("3: Ver mis mensajes publicados.");getMessageByUser(WebTarget target, int userId, int limit, int offset, String fechaIni, String fechaFin) {
					if(messageNum==0) {
						System.out.println("Aún no ha publicado ningún mensaje.");
						break;
					}					
					limit=0;
					offset=0;
					String fechaIni=null;
					String fechaFin=null;
					System.out.println("¿Desea filtrar por numero de mensajes mostrados? Escriba 'si' o 'no'.");
					 res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el limite");
						limit=sc.nextInt(); sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
	
					System.out.println("¿Desea filtrar por fecha de inicio? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba la fecha en formato 'YYYY-MM-DD'");
						fechaIni=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					System.out.println("¿Desea filtrar por fecha de fin? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba la fecha en formato 'YYYY-MM-DD'");
						fechaFin=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					while(true) {
						if(limit==0) limit=5;
						String menu="1. Pasar a siguiente página";
						try {
							getMessageByUser(target, clientId, limit, localOffset, fechaIni, fechaFin);
						}catch(Exception e) {
							menu="";
							localOffset-=limit;
							getMessageByUser(target, clientId, limit, localOffset, fechaIni, fechaFin);
							System.out.println("\t\t-----------------------"); 
							System.out.println("\t\t| No hay más páginas. |"); 
							System.out.println("\t\t-----------------------");
							}
						
						System.out.println(menu);
						if(localOffset!=0) {
							System.out.println("2: Volver a anterior página.");
						}
						System.out.println("3: Dejar de ver.");
						res=sc.nextLine();
						if(res.equals("1")) {
							if(menu!="")
								localOffset+=limit;
						}else if(res.equals("2")) {
							if(localOffset!=0)
								localOffset-=limit;
						}else if(res.equals("3")) {
							break;
						}else {
							System.out.println("Error");
							break;							
						}
						
					}
					
					
					break;
	
				case "4"://("4: Publicar un mensaje.");
					System.out.println("Escriba el mensaje a publicar: ");
					String m=sc.nextLine();
					postMessage(target,m);
					messageNum++;
					
					break;
	
				case "5"://System.out.println("5: Editar un mensaje."); updateMessage(WebTarget target, String messageId, String content
					if(messageNum==0) {
						System.out.println("Aún no ha publicado ningún mensaje.");
						break;
					}	
					System.out.println("Escriba el id de mensaje que desea modificar:");
					String id=sc.nextLine();
					System.out.println("Escriba el nuevo contenido del mensaje:");
					//FIXME: MISMO PROBLEMA QUE EN EL CASE 4
					String content=sc.nextLine();
					updateMessage(target,id,content);
					
					break;
	
				case "6"://System.out.println("6: Borrar un mensaje.");deleteMessage(WebTarget target, String messageId)
					if(messageNum==0) {
						System.out.println("Aún no ha publicado ningún mensaje.");
						break;
					}	
					System.out.println("Escriba el id del mensaje que desea eliminar:");
					deleteMessage(target,sc.nextLine());	
					messageNum--;
					
					break;
	
				case "7"://System.out.println("7: Enviar mensaje privado.");  privateMessage(WebTarget target, int receiver, String content)
					System.out.println("Escriba el id del destinatario:");
					int destiny=sc.nextInt();sc.nextLine();
					if(destiny==clientId) {
						System.out.println("No puede escribir un mensaje a sí mismo.");
						break;
					}
					System.out.println("Escriba el contenido del mensaje privado.");
					content=sc.nextLine();
					privateMessage(target,destiny,content);
					
					break;
	
				case "8"://System.out.println("8: Ver mi lista de amigos.");
					if(friendNum==0) {
						System.out.println("Aún no ha añadido ningún amigo.");
						break;
					}
					patron=null;
					limit=5;
					offset=0;
					System.out.println("¿Desea filtrar por patrón de nombre? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el patrón");
						patron=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					System.out.println("¿Desea filtrar por numero de amigos mostrados? Escriba 'si' o 'no'.");
					 res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el limite");
						limit=sc.nextInt(); sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					
					while(true) {
						if(limit==0) limit=5;
						String menu="1: Pasar siguiente página.";
						try {
							getFriendList(target,patron,localOffset,limit);
						}catch(Exception e) {
							menu="";
							System.out.println("\t-----------------------"); 
							System.out.println("\t| No hay más páginas. |"); 
							System.out.println("\t-----------------------");
						}
						
						System.out.println(menu);
						System.out.println("2: Volver a anterior página.");
						System.out.println("3: Dejar de ver.");
						res=sc.nextLine();
						if(res.equals("1")) {
							if(menu!="")
								localOffset+=limit;
						}else if(res.equals("2")) {
							localOffset-=limit;
						}else if(res.equals("3")) {
							break;
						}else {
							System.out.println("Error");
							break;							
						}}
					
					
					break;
	
				case "9"://System.out.println("9: Añadir un amigo.");
					System.out.println("Introduzca el id del usuario que desea añadir como amigo:");
					String idAmigo=sc.nextLine();
					addFriend(target, idAmigo);
					friendNum++;				
					
					break;
	
				case "10"://System.out.println("10: Eliminar amigo.");
					System.out.println("Introduzca el id del usuario que desea añadir como amigo:");
					int idFriend=sc.nextInt();sc.nextLine();
					deleteFriend(target, idFriend);
					friendNum--;
					break;
	
				case "11"://System.out.println("11: Ver mensajes de mis amigos.");
					patron=null;
					if(friendNum==0) {
						System.out.println("Aún no ha añadido ningún amigo.");
						break;
					}					
					limit=0;
					offset=0;
					localOffset=0;
					 fechaIni=null;
					 fechaFin=null;
					System.out.println("¿Desea filtrar por numero de mensajes mostrados? Escriba 'si' o 'no'.");
					 res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba el limite");
						limit=sc.nextInt(); sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					
					System.out.println("¿Desea filtrar por fecha de inicio? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba la fecha en formato 'YYYY-MM-DD'");
						fechaIni=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					System.out.println("¿Desea filtrar por fecha de fin? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						System.out.println("Escriba la fecha en formato 'YYYY-MM-DD'");
						fechaFin=sc.nextLine();
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
						break;
					}
					while(true) {
						if(limit==0) limit=5;
						String menu="1. Pasar a siguiente página";
						
						try {
							getFriendsMessage(target, limit, localOffset, fechaIni, fechaFin, patron);
						}catch(Exception e) {
							menu="";
							localOffset-=limit;
							getFriendsMessage(target, limit, localOffset, fechaIni, fechaFin, patron);
							System.out.println("\t\t-----------------------"); 
							System.out.println("\t\t| No hay más páginas. |"); 
							System.out.println("\t\t-----------------------");
							}
						
						System.out.println(menu);
						if(localOffset!=0) {
							System.out.println("2: Volver a anterior página.");
						}
						System.out.println("3: Dejar de ver.");
						res=sc.nextLine();
						if(res.equals("1")) {
							if(menu!="")
								localOffset+=limit;
						}else if(res.equals("2")) {
							if(localOffset!=0)
								localOffset-=limit;
						}else if(res.equals("3")) {
							break;
						}else {
							System.out.println("Error");
							break;							
						}
						
					}
					
					
					break;
	
				case "12"://System.out.println("12: Ver mi perfil.");
					getUserInfo(target);
					
					break;
	
				case "13"://System.out.println("13: Eliminar mi usuario.");
					System.out.println("¿Está seguro de que desea eliminar su usuario? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						deleteUser(target);						
					}else if(res.equals("no")) {
						
					}else {
						System.out.println("Error");
					}
	
				case "14"://System.out.println("14: Salir del sistema.");
					System.out.println("¿Desea ver que errores devuelve el servidor para peticiones incorrectas antes de salir? Escriba 'si' o 'no'.");
					res=sc.nextLine();
					if(res.equals("si")) {
						mostrarErrores(target);
						System.out.println("\n\nMuchas gracias por utilizar MiFacebook.");
						System.exit(0);
					}else if(res.equals("no")){
						System.out.println("\n\nMuchas gracias por utilizar MiFacebook.");
						System.exit(0);
					}
					break;
				default:
					System.out.println("Introduce un número de la lista");
					break;
				}
			}
		}
		
		
		
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/miFacebook/").build();
	}
}
