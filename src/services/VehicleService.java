package services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import beans.User;
import beans.Vehicle;
import dao.VehicleDAO;
import enums.Role;

@Path("/vehicle")
public class VehicleService {

	@Context
	ServletContext ctx;
	
	private String contextPath;
		
	@PostConstruct
	public void init() {
		if(ctx.getAttribute("vehicleDAO") == null) {
			contextPath = ctx.getRealPath("");
			ctx.setAttribute("vehicleDAO", new VehicleDAO(contextPath));
		}
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.ADMIN) {
			return Response.status(403).build();
		}
		
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		
		return Response.status(200).entity(vehicleDAO.getAll()).build();
	}
	
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllVehicles() {
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		
		List<Vehicle> vehicles = new ArrayList<>();
		
		vehicleDAO.findAll().stream()
		.filter(p -> !p.getDeleted())
		.forEach(vehicles::add);
		
		return Response.status(200).entity(vehicles).build();
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteVehicle(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		vehicleDAO.deleteVehicle(id);
		
		return Response.status(200).build();		
		
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticle(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		
		return Response.status(200).entity(vehicleDAO.findById(id)).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addVehicle(Vehicle vehicle, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if(user.getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		vehicleDAO.addVehicle(vehicle);
		
		return Response.status(200).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editVehicle(Vehicle vehicle, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if(user.getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");

		vehicleDAO.editVehicle(vehicle);
		
		return Response.status(200).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/available")
	public Response availableVehicles(@Context HttpServletRequest request) {

		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		Vehicle v = vehicleDAO.getMyVehicle(user.getId());
		List<Vehicle> vehicles = new ArrayList<>();
		
		if(v != null) {
			vehicles.add(v);
			return Response.status(200).entity(vehicles).build();
		}
		
		vehicles = vehicleDAO.getAvailable(user.getId());
		return Response.status(200).entity(vehicles).build();
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/take/{id}")
	public Response takeVehicle(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		
		VehicleDAO vehicleDAO = (VehicleDAO) ctx.getAttribute("vehicleDAO");
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		if(vehicleDAO.getMyVehicle(user.getId()) != null && vehicleDAO.getMyVehicle(user.getId()).getId() != id) {
			return Response.status(403).build();			
		}
		
		
		Vehicle v = vehicleDAO.takeOrReturn(user.getId(), id);
		
		return Response.status(200).entity(v).build();
	}
	
	
}
