package services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import beans.Restaurant;
import beans.User;
import dao.RestaurantDAO;
import enums.Role;
import util.RestaurantFilter;

@Path("/restaurant")
public class RestaurantService {

	@Context
	ServletContext ctx;

	private String contextPath;

	@PostConstruct
	public void init() {
		if (ctx.getAttribute("restaurantDAO") == null) {
			contextPath = ctx.getRealPath("");
			ctx.setAttribute("restaurantDAO", new RestaurantDAO(contextPath));
		}
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRestaurant(@PathParam(value="id") Integer id, @Context HttpServletRequest request) {
		
		RestaurantDAO restaurantDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		return Response.status(200).entity(restaurantDAO.findById(id)).build();
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.ADMIN) {
			return Response.status(403).build();
		}
		
		RestaurantDAO restaurantDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		return Response.status(200).entity(restaurantDAO.getAll()).build();
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRestaurants(@Context HttpServletRequest request) {
		RestaurantDAO rDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		List<Restaurant> restaurants = new ArrayList<>();
				
		rDAO.getAllRestaurants().values().stream()
		.filter(r -> !r.getDeleted()).forEach(restaurants::add);
		
		
		return Response.status(200).entity(restaurants).build();
	}
	
	@POST
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterRestaurants(RestaurantFilter filter, @Context HttpServletRequest request) {
		RestaurantDAO rDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		List<Restaurant> restaurants = new ArrayList<>();
		
		rDAO.getAllRestaurants().values().stream()
		.filter(r -> r.getName().toLowerCase().contains(filter.getName().toLowerCase()))
		.filter(r -> r.getCategory().toString().toLowerCase().contains(filter.getCategory().toLowerCase()))
		.filter(r -> r.getAddress().toLowerCase().contains(filter.getAddress().toLowerCase()))
		.filter(r -> !r.getDeleted())
		.forEach(restaurants::add);
		
		return Response.status(200).entity(restaurants).build();
	}
		
	@POST
	@Path("/favourites/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addFavouriteRestaurant(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		RestaurantDAO rDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user == null){
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		return Response.status(200).entity(rDAO.addFavourite(user.getId(), id)).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addRestaurant(Restaurant restaurant, @Context HttpServletRequest request) {
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}

		RestaurantDAO r = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		restaurant = r.addRestaurant(restaurant);

		return Response.status(200).entity(restaurant).build();
	}
	
	@GET
	@Path("/favourites")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMyFavs(@Context HttpServletRequest request) {
		
		RestaurantDAO restaurantDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		User user = (User) request.getSession().getAttribute("user");
		
		if(user == null) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		
		List<Restaurant> favs = restaurantDAO.getMyFavourites(user.getId());
		
		return Response.status(200).entity(favs).build();
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRestaurant(Boolean flag, @PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		RestaurantDAO restaurantDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		restaurantDAO.deleteRestaurant(id);
			
		return Response.status(200).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editRestaurant(Restaurant restaurant, @Context HttpServletRequest request) {
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		RestaurantDAO restaurantDAO = (RestaurantDAO) ctx.getAttribute("restaurantDAO");
		
		restaurantDAO.editRestaurant(restaurant);
		
		return Response.status(200).build();
	}
}
