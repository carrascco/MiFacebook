package miFacebook.bbdd;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import miFacebook.*;

@Path("/users")
public class MiFacebookResources {
	
	@Context
	private UriInfo uriInfo;
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response addUser(User user) {
		try {
			String sql = "INSERT INTO User(nombre, apellidos) " + "VALUES (?,?)" ;
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
			ps.setString(1, user.getNombre());
			ps.setString(2, user.getApellidos());
			ps.executeUpdate();
			ResultSet generatedID = ps.getGeneratedKeys();
			
			if (generatedID.next()) {
				user.setId(generatedID.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/" + user.getId();
				return Response.status(Response.Status.CREATED).entity(user).header("Location", location).build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario").build();
			
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.toString()).build();
		}
	}
	
	@GET
	@Path("{user_id}")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getUser(@PathParam("user_id") String id) {
		try {
			int userId = Integer.parseInt(id);
			String sql = "SELECT * FROM User where id=?;";
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User(rs.getString("nombre"), rs.getString("apellidos"));
				user.setId(rs.getInt("id"));
				user.setHref(new URL(uriInfo.getAbsolutePath() + ""));
				return Response.status(Response.Status.OK).entity(user).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
		
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Path("{user_id}")
	public Response updateUser(@PathParam("user_id") String id, User updateUser) {
		try {
			int userId = Integer.parseInt(id);
			if(userId==updateUser.getId()) {
				User user;
				String sql = "SELECT * FROM User where id=?;";
				PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
				ps.setInt(1, updateUser.getId());
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					user = new User(rs.getString("nombre"), rs.getString("apellidos"));
					user.setId(rs.getInt("id")); 
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
				user.setNombre(updateUser.getNombre());
				user.setApellidos(updateUser.getApellidos());
	
				sql = "UPDATE User SET nombre=?, apellidos=? WHERE id=?;";
				ps = AdministradorConexion.prepareStatement(sql);
				ps.setString(1, user.getNombre());
				ps.setString(2, user.getApellidos());
				ps.setInt(3, user.getId());
				ps.executeUpdate();
				String location = uriInfo.getAbsolutePath() + "/" + user.getId();
				return Response.status(Response.Status.OK).entity(user).header("Location", location).build();
			}else return Response.status(Response.Status.UNAUTHORIZED).entity("No autorizado a editar perfil de otro usuario.").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el usuario\n" + e.getStackTrace()).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}
	}
	
	@DELETE
	@Path("{user_id}")
	public Response deleteUser(@PathParam("user_id") String id) {
		try {
			int userId = Integer.parseInt(id);
			String sql = "DELETE FROM User WHERE id=?;";
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, userId);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).entity("Usuario eliminado.").build();
			else 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el usuario\n" + e.getStackTrace()).build();
		}
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getUserList(@QueryParam("patron") String pattern,@QueryParam("limit") int limit,@QueryParam("offset") int offset) {
		try {
			String sql;
			PreparedStatement ps;
			if(limit==0) {
				limit=5;
			}
			if(pattern==null) {
				sql = "SELECT * FROM User LIMIT "+limit+" OFFSET "+offset;
				ps = AdministradorConexion.prepareStatement(sql);
			}else {
				sql = "SELECT * FROM User WHERE nombre Like '%" + pattern + "%' LIMIT "+limit+" OFFSET "+offset;
				ps = AdministradorConexion.prepareStatement(sql);
//				ps.setInt(1, limit);
//				ps.setInt(2, offset);
				
			}
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList<User> list = new ArrayList<User>();
				rs.beforeFirst();
				User user;
				while (rs.next()) {
					user = new User(rs.getString("nombre"), rs.getString("apellidos"));
					user.setId(rs.getInt("id"));
					user.setHref(new URL(uriInfo.getAbsolutePath() + "/" + user.getId()));
					list.add(user);
				}
				UsersList users = new UsersList(list);
				return Response.status(Response.Status.OK).entity(users).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
	}
	
	@POST
	@Path("{user_id}/messages")
	@Consumes(MediaType.APPLICATION_XML)
	public Response postMessage(@PathParam("user_id") String id,Message message) {
		try {
			int userId = Integer.parseInt(id);
			if(userId==message.getUserId()) {
				String sql = "INSERT INTO Message(content, fecha) " + "VALUES (?,?)" ;
				PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
				ps.setString(1, message.getContent());
				ps.setDate(2, Date.valueOf(LocalDate.now()));
				ps.executeUpdate();
				ResultSet generatedID = ps.getGeneratedKeys();
				
				if (generatedID.next()) {
					message.setId(generatedID.getInt(1));
					String location = uriInfo.getAbsolutePath() + "/" + message.getId();
					sql = "INSERT INTO User_publish_Message(user_id,message_id) " + "VALUES (?,?)" ;
					ps = AdministradorConexion.prepareStatement(sql);
					ps.setInt(1, message.getUserId());
					ps.setInt(2, message.getId());
					ps.executeUpdate();
					return Response.status(Response.Status.CREATED).entity(message).header("Location", location).build();
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo postear el mensaje").build();
			}else return Response.status(Response.Status.UNAUTHORIZED).entity("No autorizado para publicar el mensaje en pagina personal de otro usuario.").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo postear el mensaje\n" + e.toString()).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}
		
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Path("messages/{message_id}")
	public Response getMessage(@PathParam("message_id") int messageId) {
		try {
			String sql="SELECT m.*,upm.user_id FROM Message m INNER JOIN User_publish_Message upm WHERE m.id=upm.message_id AND m.id="+messageId;
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
//			ps.setInt(1, messageId);		
			ResultSet rs = ps.executeQuery(sql);
			
			if (rs.next()) {
				ArrayList<Message> list = new ArrayList<Message>();
				rs.beforeFirst();
				Message m;
				while (rs.next()) {
					m = new Message(rs.getInt("user_id"),rs.getString("content"), rs.getString("fecha"));
					m.setId(rs.getInt("id"));
					m.setHref(new URL(""+uriInfo.getAbsolutePath()));
					list.add(m);
				}
				MessageList messages = new MessageList(list);
				Message mes=messages.getL().get(0);
				return Response.status(Response.Status.OK).entity(mes).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();	
			}
		}catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el mensaje\n" + e.toString()).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer\n"+e.toString()).build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		}
	}
	
	
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("{user_id}/messages/{message_id}")
	public Response updateMessage(@PathParam("user_id") String userid, @PathParam("message_id") String messageid,@QueryParam("userId") int userId,String updateMessage) {
		try {
			int user_Id = Integer.parseInt(userid);
			int messageId = Integer.parseInt(messageid);
			
			if(user_Id==userId) {
				Message message;
				String sql = "SELECT * FROM Message where id=?;";
				PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
				ps.setInt(1, messageId);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
				sql = "UPDATE Message SET content=? WHERE id=?;";
				ps = AdministradorConexion.prepareStatement(sql);
				ps.setString(1, updateMessage);
				ps.setInt(2, messageId);
				ps.executeUpdate();
				message = new Message(messageId, updateMessage);
				String location = uriInfo.getAbsolutePath() + "/" + messageId;
				return Response.status(Response.Status.OK).entity(message).header("Location", location).build();
			}else return Response.status(Response.Status.UNAUTHORIZED).entity("No autorizado a editar el mensaje de otro usuario.").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el mensaje\n" + e.getStackTrace()).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}
	}
	
	@DELETE
	@Path("{user_id}/messages/{message_id}")
	public Response deleteMessage(@PathParam("user_id") String user_id,@PathParam("message_id") String message_id,@QueryParam("userId") int id) {
		try{	
			int userId = Integer.parseInt(user_id);
			int messageId = Integer.parseInt(message_id);
			if(userId==id) {
				try {
					String sql = "DELETE FROM Message WHERE id=?;";
					PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
					ps.setInt(1, messageId);
					int affectedRows = ps.executeUpdate();
					if (affectedRows == 1)
						return Response.status(Response.Status.NO_CONTENT).build();
					else 
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el usuario\n" + e.getStackTrace()).build();
				}
			}else return Response.status(Response.Status.UNAUTHORIZED).entity("No autorizado para borrar el mensaje de la pagina personal de otro usuario.").build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}	
	}
	
	@GET
	@Path("{user_id}/messages")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getMessageByUser(@PathParam("user_id") int user,@QueryParam("limit") int limit,@QueryParam("offset") int offset,@QueryParam("fechaIni") String fechaIni,@QueryParam("fechaFin") String fechaFin) {
		try {
			String sql;
			if(limit==0) {
				limit=5;
			}
			if(fechaIni==null) {
				fechaIni="2004-2-4";
			}
			if(fechaFin==null) {
				fechaFin="2040-12-30";
			}
			PreparedStatement ps;
				sql = "SELECT m.*, upm.user_id FROM MiFacebook.Message m\n" + 
						"JOIN MiFacebook.User_publish_Message upm ON m.id=upm.message_id\n"+
						"WHERE upm.user_id=" + user +
						" AND m.fecha BETWEEN \"" + fechaIni + "\" AND \"" + fechaFin + "\" " 
						+ " ORDER BY fecha DESC, id DESC"
						+ " LIMIT " + limit 
						+ " OFFSET " + offset;
				ps = AdministradorConexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery(sql);
				
				if (rs.next()) {
					ArrayList<Message> list = new ArrayList<Message>();
					rs.beforeFirst();
					Message m;
					while (rs.next()) {
						m = new Message(rs.getInt("user_id"),rs.getString("content"), rs.getString("fecha"));
						m.setId(rs.getInt("id"));
						m.setHref(new URL(uriInfo.getAbsolutePath() + "/" + m.getId()));
						list.add(m);
					}
					MessageList messages = new MessageList(list);
					return Response.status(Response.Status.OK).entity(messages).build();
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
		}catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Perrooo").build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
	}
	
	@POST
	@Path("{user_id}/private_messages")
	@Consumes(MediaType.APPLICATION_XML)
	public Response privateMessage(@PathParam("user_id") String id,Message message) {
		try {
			int userId = Integer.parseInt(id);
			if(userId!=message.getUserId()) {
				String sql = "INSERT INTO Private_Message(content,fecha) " + "VALUES (?,?)" ;
				PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
				ps.setString(1, message.getContent());
				ps.setDate(2, Date.valueOf(LocalDate.now()));
				ps.executeUpdate();
				ResultSet generatedID = ps.getGeneratedKeys();
				
				if (generatedID.next()) {
					message.setId(generatedID.getInt(1));
					String location = uriInfo.getAbsolutePath() + "/" + message.getId();
					sql = "INSERT INTO User_sends_Message(id_sender,id_receiver,message_id) " + "VALUES (?,?,?)" ;
					ps = AdministradorConexion.prepareStatement(sql);
					ps.setInt(1, message.getUserId());
					ps.setInt(2, userId);
					ps.setInt(3, message.getId());
					ps.executeUpdate();
					return Response.status(Response.Status.CREATED).entity(message).header("Location", location).build();
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo postear el mensaje").build();
			}else return Response.status(Response.Status.BAD_REQUEST).entity("No puedes enviarte un mensaje privado a ti mismo.").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo postear el mensaje\n" + e.toString()).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}
	}
	
	@GET
	@Path("{user_id}/friends/messages")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getFriendsMessage(@PathParam("user_id") int user,@QueryParam("limit") int limit,@QueryParam("offset") int offset,
								@QueryParam("fechaIni") String fechaIni,@QueryParam("fechaFin") String fechaFin, @QueryParam("patron") String pattern) {
		try {
			String sql;
			if(limit==0) {
				limit=5;
			}
			if(fechaIni==null) {
				fechaIni="2004-2-4";
			}
			if(fechaFin==null) {
				fechaFin="2040-12-30";
			}
			String possiblePattern ="";
			if(pattern!=null) {
				possiblePattern = " AND content Like '%" + pattern + "%' ";
			}
			PreparedStatement ps;
				sql = "SELECT m.*, upm.user_id FROM User_publish_Message upm JOIN Is_Friend_Of fo ON (upm.user_id = fo.user_id2) \n" + 
						"JOIN Message m ON (upm.message_id = m.id) " +
						"WHERE fo.user_id1=" + user + possiblePattern +
						" AND m.fecha BETWEEN \"" + fechaIni + "\" AND \"" + fechaFin + "\" " 
						+ " ORDER BY fecha DESC, id DESC"
						+ " LIMIT " + limit 
						+ " OFFSET " + offset ;
				ps = AdministradorConexion.prepareStatement(sql);
				ResultSet rs = ps.executeQuery(sql);
				
				if (rs.next()) {
					ArrayList<Message> list = new ArrayList<Message>();
					rs.beforeFirst();
					Message m;
					while (rs.next()) {
						m = new Message(rs.getInt("user_id"),rs.getString("content"), rs.getString("fecha"));
						m.setId(rs.getInt("id"));
						m.setHref(new URL("http://localhost:8080/miFacebook/api/users/" + m.getUserId() + "/messages/" + m.getId()));
						list.add(m);
					}
					MessageList messages = new MessageList(list);
					return Response.status(Response.Status.OK).entity(messages).build();
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
		}catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
	}
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	@Path("{user_id}/friends")
	public Response addFriend(@PathParam("user_id") String id,@QueryParam("userId") String friend) {
		try {
			int userId = Integer.parseInt(id);
			int friendId = Integer.parseInt(friend);
			String sql = "INSERT INTO Is_Friend_Of(user_id1, user_id2) " + "VALUES (?,?)" ;
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, friendId);
			ps.setInt(2, userId);
			ps.executeUpdate();
			sql = "INSERT INTO Is_Friend_Of(user_id1, user_id2) " + "VALUES (?,?)" ;
			ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, friendId);
			int rs=ps.executeUpdate();
			if(rs==0)
				return Response.status(Response.Status.BAD_REQUEST).entity("El usuario ya es amigo suyo").build();
			String location = uriInfo.getAbsolutePath() + "";
			return Response.status(Response.Status.CREATED).header("Location", location).build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.toString()).build();
		}catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}
	}
	
	@DELETE
	@Path("{user_id}/friends")
	public Response deleteFriend(@PathParam("user_id") String id,@QueryParam("user_id") int userId, @QueryParam("friend_id") int friendId) {
		try{	
			int user_Id = Integer.parseInt(id);
			if(userId==user_Id) {
				try {
					String sql = "DELETE FROM Is_Friend_Of WHERE user_id1=? AND user_id2=?;";
					PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
					ps.setInt(1, friendId);
					ps.setInt(2, userId);
					int affectedRows = ps.executeUpdate();
					if (affectedRows == 1) {
						sql = "DELETE FROM Is_Friend_Of WHERE user_id1=? AND user_id2=?;";
						ps = AdministradorConexion.prepareStatement(sql);
						ps.setInt(1, userId);
						ps.setInt(2, friendId);
						affectedRows = ps.executeUpdate();
						return Response.status(Response.Status.NO_CONTENT).build();
					}else 
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();		
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el amigo\n" + e.getStackTrace()).build();
				}
			}else return Response.status(Response.Status.UNAUTHORIZED).entity("No autorizado para eliminar amigo de otro usuario.").build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		}	
	}
	
	@GET
	@Path("{user_id}/friends")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getFriendList(@PathParam("user_id") String userid ,@QueryParam("patron") String pattern, 
								@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
		try {
			int id=Integer.parseInt(userid);
			String sql;
			PreparedStatement ps;
			if(pattern==null) {
				sql = "SELECT u.* FROM User u INNER JOIN Is_Friend_Of f ON u.id=f.user_id2 WHERE f.user_id1="+id;
			}else {
				sql = "SELECT u.* FROM User u INNER JOIN Is_Friend_Of f ON u.id=f.user_id2 WHERE f.user_id1="+id+" AND u.nombre Like '%"+pattern+"%'";
			}
			if(limit==0) {
				limit=5;
			}
			
			sql+=" LIMIT "+limit+" OFFSET "+offset;
			ps = AdministradorConexion.prepareStatement(sql);
			
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ArrayList<User> list = new ArrayList<User>();
				rs.beforeFirst();
				User user;
				while (rs.next()) {
					user = new User(rs.getString("nombre"), rs.getString("apellidos"));
					user.setId(rs.getInt("id"));
					user.setHref(new URL("http://localhost:8080/miFacebook/api/users/" + user.getId()));
					list.add(user);
				}
				UsersList users = new UsersList(list);
				return Response.status(Response.Status.OK).entity(users).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
	}
	
	@GET
	@Path("{user_id}/info")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getUserInfo(@PathParam("user_id") String userid) {
		Profile p = new Profile();
		try {
			int id=Integer.parseInt(userid);
			String sql = "SELECT * FROM User where id=?;";
			PreparedStatement ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				User user = new User(rs.getString("nombre"), rs.getString("apellidos"));
				user.setId(rs.getInt("id"));
				user.setHref(new URL("http://localhost:8080/miFacebook/api/users/" + user.getId()));
				p.setUser(user);
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
			sql = "SELECT m.*, upm.user_id FROM MiFacebook.Message m\n" + 
					"JOIN MiFacebook.User_publish_Message upm ON m.id=upm.message_id\n"+
					"WHERE upm.user_id=" + id + " ORDER BY fecha DESC LIMIT 1";
			ps = AdministradorConexion.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			if (rs.next()) {
				Message m =new Message(rs.getInt("user_id"),rs.getString("content"), rs.getString("fecha"));
				m.setId(rs.getInt("id"));
				m.setHref(new URL("http://localhost:8080/miFacebook/api/users/" + m.getUserId() + "/messages/" + m.getId()));
				p.setUserMessage(m);
			} 
			sql = "SELECT u.* FROM User u INNER JOIN Is_Friend_Of f ON u.id=f.user_id2 WHERE f.user_id1=?";
			ps = AdministradorConexion.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			rs.last();
			int Fnum = rs.getRow();
			p.setFnum(Fnum);
			sql = "SELECT m.*, upm.user_id FROM User_publish_Message upm JOIN Is_Friend_Of fo ON (upm.user_id = fo.user_id2) \n" + 
					"JOIN Message m ON (upm.message_id = m.id) " +
					"WHERE fo.user_id1=" + id + " ORDER BY fecha DESC LIMIT 10";
			ps = AdministradorConexion.prepareStatement(sql);
			rs = ps.executeQuery(sql);
			if (rs.next()) {
				ArrayList<Message> list = new ArrayList<Message>();
				rs.beforeFirst();
				Message m;
				while (rs.next()) {
					m = new Message(rs.getInt("user_id"),rs.getString("content"), rs.getString("fecha"));
					m.setId(rs.getInt("id"));
					m.setHref(new URL("http://localhost:8080/miFacebook/api/users/" + m.getUserId() + "/messages/" + m.getId()));
					list.add(m);
				}
				p.setFriendsMessages(new MessageList(list));
			} 
			return Response.status(Response.Status.OK).entity(p).build();
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudo parsear el id a Integer").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		} catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fallo creación URL usuario").build();
		}
	}
}
