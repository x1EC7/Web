package util;

public class RestaurantFilter {

	private String name;
	private String category;
	private String address;

	public RestaurantFilter() {

	}

	public RestaurantFilter(String name, String category, String address) {
		this.name = name;
		this.category = category;
		this.address = address;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getCategory() {
		return category;
	}

	public final void setCategory(String category) {
		this.category = category;
	}

	public final String getAddress() {
		return address;
	}

	public final void setAddress(String address) {
		this.address = address;
	}

}
