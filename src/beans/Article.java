package beans;

import enums.Type;

public class Article {

	private Integer id;
	private Integer restaurantID;
	private Integer popularity;
	private String name;
	private Double price;
	private String description;
	private Integer amount; // g and ml
	private Boolean deleted;
	private Type type;
	
	public Article(Integer id, Integer restaurantID, Integer popularity, String name, Double price, String description, Integer amount, Type type, Boolean deleted) {
		this.id = id;
		this.restaurantID = restaurantID;
		this.popularity = popularity;
		this.name = name;
		this.price = price;
		this.description = description;
		this.amount = amount;
		this.deleted = deleted;
		this.type = type;
	}

	public Article() {

	}

	public final Integer getPopularity() {
		return popularity;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final Double getPrice() {
		return price;
	}

	public final void setPrice(Double price) {
		this.price = price;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final Integer getAmount() {
		return amount;
	}

	public final void setAmount(Integer amount) {
		this.amount = amount;
	}

	public final Boolean getDeleted() {
		return deleted;
	}

	public final void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public final Integer getId() {
		return id;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final Type getType() {
		return type;
	}

	public final void setType(Type type) {
		this.type = type;
	}
	
	public final Integer getRestaurantID() {
		return restaurantID;
	}
	
	public final void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}

	public final void setRestaurantID(Integer restaurantID) {
		this.restaurantID = restaurantID;
	}

	public String toFile() {
		return System.getProperty("line.separator") + id + ";"+ restaurantID + ";" + popularity + ";" + name + ";" + price + ";" + description + ";" + amount + ";"+ type + ";" + deleted;
	}

	public void increasePopularity() {
		++this.popularity;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", restaurantID=" + restaurantID + ", popularity=" + popularity + ", name=" + name
				+ ", price=" + price + ", description=" + description + ", amount=" + amount + ", deleted=" + deleted
				+ ", type=" + type + "]";
	}

}
