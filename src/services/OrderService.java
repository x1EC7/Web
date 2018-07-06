package services;

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

import beans.Order;
import beans.User;
import dao.OrderDAO;
import enums.Role;
import util.OrderUtil;

@Path("/order")
public class OrderService {

	@Context
	ServletContext ctx;

	private String contextPath;

	@PostConstruct
	private void init() {
		if (ctx.getAttribute("orderDAO") == null) {
			contextPath = ctx.getRealPath("");
			ctx.setAttribute("orderDAO", new OrderDAO(contextPath));

		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveOrder(@Context HttpServletRequest request) {
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user == null) {
			return Response.status(403).build();
		}
	
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		Order order = orderDAO.getMyOrder(user.getId());
		
		return Response.status(order == null ? 400 : 200).entity(order).build();
	}
	
	@POST
	@Path("/{id}/{amount}")
	public Response addToCart(@PathParam(value = "id") Integer id, @PathParam(value = "amount") Integer amount, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		if(user == null) {
			return Response.status(403).build();			
		}
		
		Order myOrder = orderDAO.addArticle(user.getId(), id, amount);
		
		return Response.status(myOrder == null ? 400 : 200).build();
	}
	
	@POST
	@Path("/order")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response order(OrderUtil util, @Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		Order order = orderDAO.orderCart(user.getId(), util);
		
		return Response.status(order == null ? 400 : 200).entity(order).build();
		
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrder(Order order, @Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		Order created = orderDAO.createOrder(order);
		return Response.status(created == null ? 400 : 200).build();
		
	}
	
	@GET
	@Path("/history")
	@Produces(MediaType.APPLICATION_JSON)
	public Response myHistory(@Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		Integer id = ((User) request.getSession().getAttribute("user")).getId();
		return Response.status(200).entity(orderDAO.getMyOrders(id)).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrder(@PathParam(value = "id") Integer id, @Context HttpServletRequest request){
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		return Response.status(200).entity(orderDAO.findById(id)).build();
	}
	
	@POST
	@Path("/remove/{id}")
	public Response removeArticle(@PathParam(value = "id") Integer id, @Context HttpServletRequest request){
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		Boolean removed = orderDAO.removeArticleFromCart(user.getId(), id);
		
		return Response.status(removed ? 200 : 400).build();
		
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editOrder(Order order, @Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		orderDAO.editOrder(order);
		return Response.status(200).build();
	}
	
	@GET
	@Path("/work")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkOrders(@Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		List<Order> orders = orderDAO.getWorkOrders(user.getId());
		
		return Response.status(200).entity(orders).build();
	}

	@POST
	@Path("/change/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeStatus(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(Status.FORBIDDEN).build();					
		}

		Order success = orderDAO.changeStatus(id, user.getId());
		return Response.status(success != null ? 200 : 403).entity(success).build();
	}
	
	@POST
	@Path("/cancel/{id}")
	public Response cancelOrder(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.DELIVERY) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		Order o = orderDAO.cancel(id, user.getId());
		
		return Response.status(o == null ? 403 : 200).build();
	}
	
	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request) {
		
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		
		return Response.status(200).entity(orderDAO.getAll()).build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteOrder(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		OrderDAO orderDAO = (OrderDAO) ctx.getAttribute("orderDAO");
		
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();					
		}
		orderDAO.delete(id);
		return Response.status(200).build();
	}
}
