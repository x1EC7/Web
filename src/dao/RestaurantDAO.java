package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import beans.Restaurant;
import enums.Category;
import util.FileUtil;

public class RestaurantDAO {

	private final String RESTAURANTS = "/restaurants.txt";
	private final String FAVOURITES = "/favourites.txt";

	private Map<Integer, Restaurant> restaurants = new HashMap<>();
	private Map<Integer, List<Integer>> favourites = new HashMap<>();
	
	private String path;
	private FileUtil fileUtil;
	
	public RestaurantDAO() {
		
	}
	
	public RestaurantDAO(String contextPath) {
		this.path = contextPath;
		fileUtil = new FileUtil();
		loadRestaurants();
		loadFavourites();
		savePeriodically();
	}
	
	private void savePeriodically() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			fileUtil.saveAll(path + RESTAURANTS, allToFile());
			//System.out.println("restaurants saved");
			fileUtil.saveAll(path + FAVOURITES, favToFile());
			//System.out.println("favourites saved");
		}, 5, 5, TimeUnit.SECONDS);
	}

	private List<String> favToFile() {
		List<String> favs = new ArrayList<>();
		
		favourites.forEach((k, v) -> {
			String str = System.getProperty("line.separator") + k;
			for(Integer tmp : v)
				str += ";" + tmp;
			favs.add(str);
		});
		
		
		return favs;
	}

	private List<String> allToFile() {
		List<String> rest = new ArrayList<>();
		
		restaurants.values().forEach(r -> rest.add(r.toFile()));
		
		return rest;
	}

	public List<Restaurant> getMyFavourites(Integer id){
		List<Restaurant> favs = new ArrayList<>();
		
		if(favourites.get(id) == null) {
			favourites.put(id, new ArrayList<>());
		}
		
		restaurants.values().stream()
		.filter(r -> favourites.get(id).contains(r.getId()))
		.filter(r -> !r.getDeleted())
		.forEach(favs::add);
		
		return favs;
		
	}
	
	public List<Restaurant> addFavourite(Integer myId, Integer restaurantId) {

		if(favourites.get(myId) == null) {
			favourites.put(myId, new ArrayList<>());
		}
		
		Integer temp = favourites.get(myId).stream().filter(f -> f.equals(restaurantId)).findFirst().orElse(null);
		
		if(temp == null) {
			favourites.get(myId).add(restaurantId);
		} else {
			favourites.get(myId).remove(restaurantId);
		}
		
		return getMyFavourites(myId);
	}

	private void loadFavourites() {
		List<String> lines = fileUtil.read(path + FAVOURITES);
		StringTokenizer st;
		
		for(String line : lines) {
			line = line.trim();
			if(line.equals("") || line == null) continue;
			
			st = new StringTokenizer(line, ";");
			Integer id = Integer.parseInt(st.nextToken().trim());
			List<Integer> favs = new ArrayList<>();
			while(st.hasMoreTokens()) {
				favs.add(Integer.parseInt(st.nextToken().trim()));
			}
			favourites.put(id, favs);
		}
	}

	public Map<Integer,Restaurant> getAllRestaurants(){
		return restaurants;
	}
	
	public Restaurant findById(Integer id) {
		return restaurants.get(id);
	}
	
	public Restaurant addRestaurant(Restaurant restaurant) {
		
		restaurant.setId(restaurants.size());
		restaurant.setDeleted(false);
		restaurants.put(restaurant.getId(), restaurant);
		return restaurant;
	}
	
	private void loadRestaurants() {
		List<String> lines = fileUtil.read(path + RESTAURANTS);
		StringTokenizer st;

		for(String line : lines) {
			line = line.trim();
			if(line.equals("") || line == null) continue;
			st = new StringTokenizer(line, ";");
			while(st.hasMoreTokens()) {
				Integer id = Integer.parseInt(st.nextToken().trim());
				String name = st.nextToken().trim();
				String address = st.nextToken().trim();
				Category category = Category.valueOf(st.nextToken().trim());
				Boolean deleted = st.nextToken().trim().equalsIgnoreCase("true") ? true : false;
				restaurants.put(id, new Restaurant(id, name, address, category, null, deleted));
			}
		}
	}

	public void deleteRestaurant(Integer id) {
		Restaurant restaurant = findById(id);
				
		restaurant.setDeleted(!restaurant.getDeleted());
	
		
	}

	public List<Restaurant> getAll() {
		return restaurants.values().stream().collect(Collectors.toList());
	}

	public void editRestaurant(Restaurant restaurant) {
		
		Restaurant  r = findById(restaurant.getId());
		r.setName(restaurant.getName());
		r.setAddress(restaurant.getAddress());
		r.setCategory(restaurant.getCategory());
		
	}
	
	
	
}
