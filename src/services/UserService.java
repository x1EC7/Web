package services;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import dao.UserDAO;
import enums.Role;

@Path("/auth")
public class UserService {
	
	@Context
	ServletContext ctx;
	
	private String contextPath;
	
	@PostConstruct
	public void init() {
		if (ctx.getAttribute("userDAO") == null) {
	    	contextPath = ctx.getRealPath("");
			ctx.setAttribute("userDAO", new UserDAO(contextPath));
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editUser(User user, @Context HttpServletRequest request) {
		UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
		User admin = (User) request.getSession().getAttribute("user");
		if(admin.getRole() != Role.ADMIN) {
			return Response.status(403).build();
		}

		userDao.updateUser(user);

		return Response.status(200).build();
	}
	
	@POST
	@Path("/used/{points}")
	public Response login(@PathParam(value = "points") Integer points, @Context HttpServletRequest request) {
		UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
		User user = (User) request.getSession().getAttribute("user");
		userDao.updatePoints(user.getUsername(), points);
		
		return Response.status(200).build();
	}
	
	@POST
	@Path("/addpoint/{id}")
	public Response addPoint(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(403).build();			
		}
		userDao.addPoint(id);
		return Response.status(200).build();
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user, @Context HttpServletRequest request) {
			UserDAO userDao = (UserDAO) ctx.getAttribute("userDAO");
		User loggedUser = userDao.find(user.getUsername(), user.getPassword());
		if (loggedUser == null) {
			return Response.status(400).entity("Invalid username and/or password").build();
		}
		request.getSession().setAttribute("user", loggedUser);
		return Response.status(200).build();
	}
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(User user, @Context HttpServletRequest request) {
		UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
		if(!userDAO.takenUsername(user.getUsername()) && !userDAO.takenEmail(user.getEmail())) {
			User newUser = userDAO.register(user);
			return Response.status(200).entity(newUser).build();
		}
		return Response.status(400).build();
	}
	
	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void logout(@Context HttpServletRequest request) {
		request.getSession().invalidate();
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request) {
		UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		if(user.getRole() != Role.ADMIN) {
			return Response.status(403).build();
		}
		
		List<User> users = userDAO.getAll();
		
		return Response.status(200).entity(users).build();
	}
	
	@GET
	@Path("/currentUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User login(@Context HttpServletRequest request) {
		return (User) request.getSession().getAttribute("user");
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		UserDAO userDAO = (UserDAO) ctx.getAttribute("userDAO");
		
		return Response.status(200).entity(userDAO.findById(id)).build();
	}

}
