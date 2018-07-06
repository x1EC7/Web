package util;

public class OrderUtil {

	Double price;
	Integer points;
	String note;

	
	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	public OrderUtil() {
		super();
	}

	public final Double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "OrderUtil [price=" + price + ", points=" + points + "]";
	}

	public final void setPrice(Double price) {
		this.price = price;
	}

	public final Integer getPoints() {
		return points;
	}

	public final void setPoints(Integer points) {
		this.points = points;
	}

}
