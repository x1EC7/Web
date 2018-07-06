package dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import beans.Article;
import enums.Type;
import util.FileUtil;

public class ArticleDAO {

	Map<Integer, Article> articles = new HashMap<>();
	
	private final String ARTICLES = "/articles.txt";
	
	private String path;
	
	private FileUtil fileUtil;
	
	public ArticleDAO(String contextPath) {
		this.path = contextPath;
		fileUtil = new FileUtil();
		loadArticles();
		savePeriodically();
	}
	
	public Map<Integer, Article> findAll(){
		return articles;
	}
	
	public Article addArticle(Article article) {
		
		article.setId(articles.size());
		article.setPopularity(0);
		article.setDeleted(false);

		articles.put(article.getId(), article);
		return article;		
	}

	private void loadArticles() {
		
		List<String> lines = fileUtil.read(path + ARTICLES);
		for(String line : lines) {
			StringTokenizer st = new StringTokenizer(line, ";");
			if(line.equals("") || line == null) continue;

			while(st.hasMoreTokens()) {
				Integer id = Integer.parseInt(st.nextToken().trim());
				Integer restaurantId = Integer.parseInt(st.nextToken().trim());
				Integer popularity = Integer.parseInt(st.nextToken().trim());
				String name = st.nextToken().trim();
				Double price = Double.parseDouble(st.nextToken().trim());
				String description = st.nextToken().trim();
				Integer amount = Integer.parseInt(st.nextToken().trim());
				Type type = Type.valueOf(st.nextToken().trim());
				Boolean deleted = st.nextToken().trim().equalsIgnoreCase("true") ? true : false;
				articles.put(id, new Article(id, restaurantId, popularity, name, price, description, amount, type, deleted));
			}
		}
		
	}

	public void deleteArticle(Integer id) {

		Article article = findById(id);

		article.setDeleted(!article.getDeleted());
		
	}

	public Article findById(Integer id) {

		return articles.get(id);
	}

	public void deleteRestaurant(Integer id) {

		articles.values().stream()
		.filter(a -> a.getRestaurantID() == id)
		.forEach(a -> a.setDeleted(true));
	}

	public void unDeleteRestaurant(Integer id) {

		articles.values().stream()
		.filter(a -> a.getRestaurantID() == id)
		.forEach(a -> a.setDeleted(false));
	}
	
	private void savePeriodically() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			fileUtil.saveAll(path + ARTICLES, allToFile());
			//System.out.println("articles saved");
		}, 5, 5, TimeUnit.SECONDS);
	}

	private List<String> allToFile() {
		List<String> lines = new ArrayList<>();
		
		articles.values().forEach(a -> lines.add(a.toFile()));

		return lines;
	}

	public List<Article> getTopTen() {

		List<Article> topTen = new ArrayList<>();

		splitByType(topTen, Type.FOOD);
		splitByType(topTen, Type.DRINK);
		
		return topTen;
	}

	public void splitByType(List<Article> topTen, Type type) {
		articles.values().stream()
		.filter(a -> a.getType() == type)
		.filter(a -> !a.getDeleted())
		.sorted(Comparator.comparing(Article::getPopularity).reversed())
		.limit(10)
		.forEach(topTen::add);
	}

	public Double getMaxPrice() {
		Article article = articles.values().stream()
		.sorted(Comparator.comparing(Article::getPrice).reversed())
		.findFirst().orElse(null);
		return article.getPrice();
	}

	public List<Article> getAll() {
		return articles.values().stream().collect(Collectors.toList());
	}

	public void increasePopularity(Integer id) {
		articles.get(id).increasePopularity();
	}

	public Double calculatePrice(HashMap<Integer, Integer> map) {
		Double price = 0D;
		
		for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
			price += articles.get(entry.getKey()).getPrice() * entry.getValue();
		}
		
		return price;
	}

	public List<Article> findByRestaurantId(Integer id) {
		return articles.values().stream()
				.filter(a -> a.getRestaurantID() == id)
				.filter(a -> !a.getDeleted())
				.collect(Collectors.toList());
	}

	public void editArticle(Article article) {
		
		Article a = findById(article.getId());
		
		a.setName(article.getName());
		a.setDescription(article.getDescription());
		a.setPrice(article.getPrice());
		a.setAmount(article.getAmount());
		a.setRestaurantID(article.getRestaurantID());
		a.setType(article.getType());
		
	}
	
}
