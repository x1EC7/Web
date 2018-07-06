package beans;

import java.util.List;

import enums.Category;

public class Restaurant {

	private Integer id;
	private String name;
	private String address;
	private Category category;
	private Boolean deleted;
	private List<Article> articles;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address = address;
	}

	public final Category getCategory() {
		return category;
	}

	public final void setCategory(Category category) {
		this.category = category;
	}

	public final List<Article> getArticles() {
		return articles;
	}

	public final void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public Restaurant(Integer id, String name, String address, Category category, List<Article> articles,
			Boolean deleted) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.category = category;
		this.articles = articles;
		this.deleted = deleted;
	}

	public final Integer getId() {
		return id;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final Boolean getDeleted() {
		return deleted;
	}

	public final void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Restaurant() {

	}
	
	public void addArticle(Article article) {
		this.articles.add(article);
	}
	
	public void removeArticle(Article article) {
		this.articles.remove(article);
	}

	public String toFile() {
		return System.getProperty("line.separator") + id + ";" + name + ";" + address + ";" + category + ";" + deleted;
	}
}
