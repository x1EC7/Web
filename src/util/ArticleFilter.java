package util;

public class ArticleFilter {

	private String type;
	private Integer minPrice;
	private Integer maxPrice;
	private String name;
	private Integer restaurantId;

	public ArticleFilter(String type, Integer minPrice, Integer maxPrice, String name, Integer restaurantId) {
		super();
		this.type = type;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.name = name;
		this.restaurantId = restaurantId;
	}

	public ArticleFilter() {

	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}

	public final Integer getMinPrice() {
		return minPrice;
	}

	public final void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public final Integer getMaxPrice() {
		return maxPrice;
	}

	public final void setMaxPrice(Integer maxPrice) {
		this.maxPrice = maxPrice;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final Integer getRestaurant() {
		return restaurantId;
	}

	public final void setRestaurant(Integer restaurant) {
		this.restaurantId = restaurant;
	}

	public final Integer getRestaurantId() {
		return restaurantId;
	}

	public final void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

}
