package beans;

import java.util.Map;

import enums.Status;

public class Order {

	private Integer id;
	private String dateOfOrder;
	private Integer deliveryID;
	private Integer clientID;
	private Status status;
	private String note;
	private Double price;
	private Integer points;
	private Boolean deleted;
	private Map<Integer, Integer> articles; //article id, amount

	public Order() {

	}
	
	public Order(Integer id, String dateOfOrder, Integer deliveryID, Integer clientID, Status status, String note, Double price, Integer points,
			Boolean deleted, Map<Integer, Integer> articles) {
		super();
		this.id = id;
		this.dateOfOrder = dateOfOrder;
		this.deliveryID = deliveryID;
		this.clientID = clientID;
		this.status = status;
		this.note = note;
		this.points = points;
		this.price = price;
		this.articles = articles;
		this.deleted = deleted;
	}

	public final Double getPrice() {
		return price;
	}

	public final void setPrice(Double price) {
		this.price = price;
	}

	public final Boolean getDeleted() {
		return deleted;
	}

	public final void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String toFile() {
		String art = "";
		for(Map.Entry<Integer, Integer> entry : articles.entrySet()) {
			art += ";" + entry.getKey() + "--" + entry.getValue();
		}
		return System.getProperty("line.separator") + id + ";" + dateOfOrder + ";" + deliveryID + ";" + clientID + ";" + status + ";" + note + ";" + price + ";" + points + ";" + deleted + art;
	}

	public final Map<Integer, Integer> getArticles() {
		return articles;
	}

	public final void setArticles(Map<Integer, Integer> articles) {
		this.articles = articles;
	}
	
	public final Integer getId() {
		return id;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final String getDateOfOrder() {
		return dateOfOrder;
	}

	public final void setDateOfOrder(String dateOfOrder) {
		this.dateOfOrder = dateOfOrder;
	}

	public final Status getStatus() {
		return status;
	}

	public final void setStatus(Status status) {
		this.status = status;
	}

	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	public final Integer getDeliveryID() {
		return deliveryID;
	}

	public final void setDeliveryID(Integer deliveryID) {
		this.deliveryID = deliveryID;
	}

	public final Integer getClientID() {
		return clientID;
	}

	public final void setClientID(Integer clientID) {
		this.clientID = clientID;
	}

	public final Integer getPoints() {
		return points;
	}

	public final void setPoints(Integer points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", dateOfOrder=" + dateOfOrder + ", deliveryID=" + deliveryID + ", clientID="
				+ clientID + ", status=" + status + ", note=" + note + ", price=" + price + ", points=" + points
				+ ", articles=" + articles + "]";
	}
	
}
