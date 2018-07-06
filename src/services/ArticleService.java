package services;

import java.util.ArrayList;
import java.util.HashMap;
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

import beans.Article;
import beans.User;
import dao.ArticleDAO;
import enums.Role;
import util.ArticleFilter;

@Path("/article")
public class ArticleService {

	@Context
	ServletContext ctx;

	private String contextPath;

	@PostConstruct
	public void init() {

		if (ctx.getAttribute("articleDAO") == null) {
			contextPath = ctx.getRealPath("");
			ctx.setAttribute("articleDAO", new ArticleDAO(contextPath));
		}
	}
	
	@POST
	@Path("/popularity/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response increasePopularity(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		ArticleDAO articleDAO = ((ArticleDAO) ctx.getAttribute("articleDAO"));
		
		articleDAO.increasePopularity(id);
		
		return Response.status(200).build();	
	}
	
	@POST
	@Path("/calcPrice")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response calcPrice(String vals, @Context HttpServletRequest request) {
		HashMap<Integer, Integer> map = new HashMap<>();
		for(String tmp : vals.split(";")) {
			String[] s = tmp.split(":");
			map.put(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
		}
		
		ArticleDAO articleDAO = ((ArticleDAO) ctx.getAttribute("articleDAO"));
		
		Double price = articleDAO.calculatePrice(map);
		
		return Response.status(200).entity(price).build();
	}
	
	@POST
	@Path("/filter")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterArticles(ArticleFilter articleFilter, @Context HttpServletRequest request) {
		
		ArticleDAO articleDAO = ((ArticleDAO) ctx.getAttribute("articleDAO"));
		
		List<Article> articles = new ArrayList<>();

		articleDAO.findAll().values().parallelStream()
		.filter(r -> articleFilter.getRestaurant() == r.getRestaurantID())
		.filter(r -> r.getName().toLowerCase().contains(articleFilter.getName().toLowerCase()))
		.filter(r -> articleFilter.getType().toLowerCase().equalsIgnoreCase(r.getType().toString().toLowerCase()))
		.forEach(articles::add);
		
		if(articles.isEmpty()) {
			return Response.status(200).entity(articles).build();			
		}
		//articles.stream().forEach(System.out::println);
		if(articleFilter.getMaxPrice() != 0 && !articles.isEmpty()) {
			for(int i = articles.size() ; i --> 0; ) {
				if(articles.get(i).getPrice() > articleFilter.getMaxPrice())
					articles.remove(i);
			}
		}

		//articles.stream().forEach(System.out::println);
		if(articles.isEmpty()) {
			return Response.status(200).build();			
		}
		
		if(articleFilter.getMinPrice() != 0 && !articles.isEmpty()) {
			for(int i = articles.size() ; i --> 0; ) {
				if(articles.get(i).getPrice() < articleFilter.getMaxPrice())
					articles.remove(i);
			}
		}
		//articles.stream().forEach(System.out::println);
		return Response.status(200).entity(articles).build();
		
	}

	@GET
	@Path("/admin")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		
		if(user.getRole() != Role.ADMIN) {
			return Response.status(403).build();
		}
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		List<Article> articles = articleDAO.getAll();
		
		return Response.status(200).entity(articles).build();
	}
	
	@GET
	@Path("/max")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMaxPrice(@Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		Double maxPrice = articleDAO.getMaxPrice();
		return Response.status(200).entity(maxPrice).build();
	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticles(@Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		List<Article> articles =  new ArrayList<>();
		
		articleDAO.findAll().values().stream()
		.filter(a -> !a.getDeleted())
		.forEach(articles::add);
		return Response.status(200).entity(articles).build();
	}
	
	@GET
	@Path("/top10")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopTen(@Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		List<Article> top10 = articleDAO.getTopTen();
		
		return Response.status(200).entity(top10).build();
	}
	
	@DELETE
	@Path("/restaurant/{id}")
	public Response deleteRestaurant(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
			articleDAO.deleteRestaurant(id);
		return Response.status(200).build();
	}
	
	@PUT
	@Path("/restaurant/{id}")
	public Response unDeleteRestaurant(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
			articleDAO.unDeleteRestaurant(id);
		return Response.status(200).build();
	}
	
	
	@DELETE
	@Path("/{id}")
	public Response deleteArticle(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		articleDAO.deleteArticle(id);
		return Response.status(200).build();
	}
		
	@GET
	@Path("/restaurant/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRestaurantArticles(@PathParam(value="id") Integer id, @Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		return Response.status(200).entity(articleDAO.findByRestaurantId(id)).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addArticle(Article article, @Context HttpServletRequest request){
		if (((User) request.getSession().getAttribute("user")).getRole() != Role.ADMIN) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		Article added = articleDAO.addArticle(article);
		
		return Response.status(added == null ? 400 : 200).entity(added).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getArticleById(@PathParam(value = "id") Integer id, @Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		return Response.status(200).entity(articleDAO.findById(id)).build();	
	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editArticle(Article article, @Context HttpServletRequest request) {
		ArticleDAO articleDAO = (ArticleDAO) ctx.getAttribute("articleDAO");
		
		articleDAO.editArticle(article);
		
		return Response.status(200).build();
	}
}
