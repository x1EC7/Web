package dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import beans.Order;
import enums.Status;
import util.FileUtil;
import util.OrderUtil;

public class OrderDAO {

	private HashMap<Integer, Order> orders;

	private String path;

	private final String ORDERS = "/orders.txt";

	private FileUtil fileUtil;

	public OrderDAO() {

	}

	public OrderDAO(String path) {
		this.path = path;
		orders = new HashMap<Integer, Order>();
		fileUtil = new FileUtil();
		loadOrders();
		savePeriodically();
	}

	public Order getMyOrder(Integer id) {
		
		Order order = orders.values().stream().filter(o -> o.getClientID() == id).filter(o -> o.getStatus() == Status.CART)
				.findFirst().orElse(null);
		
		if(order == null) {
			order = new Order();
			order.setClientID(id);
			order.setDeliveryID(-1);
			order.setStatus(Status.CART);
			order.setId(orders.size());
			order.setPoints(0);
			order.setPrice(0D);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			order.setDateOfOrder(dtf.format(LocalDateTime.now()).toString());
			order.setNote("no note");
			order.setDeleted(false);
			order.setArticles(new HashMap<Integer, Integer>());
		}
		orders.put(order.getId(), order);
		return order;

	}
	
	public Order createOrder(Order order) {
		
		Order exists = orders.values().stream()
				.filter(o -> o.getClientID() == order.getClientID())
				.filter(o -> o.getStatus() == Status.CART)
				.findFirst().orElse(null);
		
		if (exists != null)
			return null;
		
		order.setStatus(Status.CART);
		order.setPoints(0);
		order.setDeliveryID(-1);
		order.setId(orders.size());
		order.setDeleted(false);
		order.setPrice(0D);
		order.setArticles(null);
		orders.put(order.getId(), order);
		
		return order;
	}
	
	public Order addArticle(Integer myId, Integer id, Integer amount) {
		Order myOrder =orders.values().stream()
		.filter(o -> o.getClientID() == myId)
		.filter(o -> o.getStatus() == Status.CART)
		.findFirst().orElse(null);
		
		if(myOrder == null) {
			myOrder = getMyOrder(myId);
		}
		
		myOrder.getArticles().put(id, amount);
		
		return myOrder;
	}
	
	public Order orderCart(Integer id, OrderUtil util) {

		Order order = getCart(id, Status.CART);
		
		if (order == null) {
			return null;
		}
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		order.setDateOfOrder(dtf.format(LocalDateTime.now()).toString());
		order.setStatus(Status.ORDERED);
		order.setPoints(util.getPoints());
		order.setPrice(util.getPrice());
		order.setNote(util.getNote());
		
		return order;
	}
	
	public Order addDeliveryId(Integer id, Integer deliveryId) {
		Order order = orders.values().stream()
				.filter(o -> o.getClientID() == id)
				.findFirst().orElse(null);

		if (order.getDeliveryID() == deliveryId) {
			return null;
		}
		order.setDeliveryID(deliveryId);
		return order;
		
	}
	
	public Order setStatus(Integer id, Status status) {
		Order order = orders.get(id);
		order.setStatus(status);
		return order;
	}
	
	public Order getCart(Integer id, Status status) {
		Order order = orders.values().stream()
				.filter(o -> o.getClientID() == id)
				.filter(o -> o.getStatus() == status)
				.findFirst().orElse(null);

		if (order == null) {
			return null;
		}
		return order;
	}

	public List<Order> getMyOrders(Integer id) {
		List<Order> myOrders = new ArrayList<>();
		
		orders.values().stream()
		.filter(o -> o.getClientID() == id)
		.filter(o -> o.getStatus() != Status.CART)
		.forEach(myOrders::add);

		return myOrders;
	}

	private void loadOrders() {

		List<String> lines = fileUtil.read(path + ORDERS);
		StringTokenizer st;

		for (String line : lines) {
			line = line.trim();
			if(line.equals("") || line == null) continue;
			st = new StringTokenizer(line, ";");
			Integer id = Integer.parseInt(st.nextToken().trim());
			String dateOfOrder = st.nextToken().trim();
			Integer deliveryID = Integer.parseInt(st.nextToken().trim());
			Integer clientID = Integer.parseInt(st.nextToken().trim());
			Status status = Status.valueOf(st.nextToken().trim());
			String note = st.nextToken().trim();
			Double price = Double.parseDouble(st.nextToken().trim());
			Integer points = Integer.parseInt(st.nextToken().trim());
			Boolean deleted = st.nextToken().trim() == "true" ? true : false;
			HashMap<Integer, Integer> articles = new HashMap<>();
			while (st.hasMoreTokens()) {
				String[] article = st.nextToken().trim().split("--");
				articles.put(Integer.parseInt(article[0]), Integer.parseInt(article[1]));
			}
			orders.put(id, new Order(id, dateOfOrder, deliveryID, clientID, status, note, price, points, deleted, articles));
		}

	}
	
	private void savePeriodically() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			fileUtil.saveAll(path + ORDERS, allToFile());
			//System.out.println("orders saved");
		}, 5, 5, TimeUnit.SECONDS);
	}

	private List<String> allToFile() {
		List<String> lines = new ArrayList<>();
		
		orders.values().forEach(o -> lines.add(o.toFile()));
		
		return lines;
	}

	public Order findById(Integer id) {
		return orders.get(id);
	}

	public void editOrder(Order order) {
		
		Order o = findById(order.getId());
		o.setClientID(order.getClientID());
		o.setDeliveryID(o.getDeliveryID());
		o.setNote(order.getNote());
		
	}

	public List<Order> getWorkOrders(Integer id) {
		return orders.values().stream()
				.filter(o -> o.getStatus() != Status.CART && o.getStatus() != Status.DELIVERED)
				.filter(o -> o.getStatus() == Status.ORDERED || o.getDeliveryID() == id)
				.collect(Collectors.toList());
	}

	public Order changeStatus(Integer id, Integer deliveryId) {
		Order o = orders.get(id);
		
		if(o.getStatus() == Status.ORDERED) {
			o.setStatus(Status.IN_DELIVERY);
			o.setDeliveryID(deliveryId);
			return o;
		}
		if(o.getDeliveryID() == deliveryId) {
			if(o.getStatus() == Status.IN_DELIVERY) {
			o.setStatus(Status.DELIVERED);
			return o;
		}
		}
		return null;
	}

	public Order cancel(Integer id, Integer integer) {
		Order o = orders.get(id);
		if(o.getDeliveryID() == integer) {
			o.setStatus(Status.CANCELLED);
		} else {
			return null;
		}
		
		return o;
	}

	public Boolean removeArticleFromCart(Integer uId, Integer id) {
		
		Order o = getMyOrder(uId);
		
		if(o.getArticles().get(id) == null)
			return false;
		o.getArticles().remove(id);
		
		return true;
	}

	public List<Order> getAll() {
		return orders.values().stream().collect(Collectors.toList());
	}
	
	public void delete(Integer id) {
		orders.get(id).setDeleted(!orders.get(id).getDeleted());
	}
	
}
