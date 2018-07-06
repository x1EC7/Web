package dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import beans.User;
import enums.Role;
import util.FileUtil;

public class UserDAO {
	private Map<String, User> users = new HashMap<>();

	private final String USERS = "/users.txt";

	private String path;

	private FileUtil fileUtil;

	public UserDAO() {
		fileUtil = new FileUtil();
	}
	/***
	 * @param contextPath Putanja do aplikacije u Tomcatu. Može se pristupiti samo
	 *                    iz servleta.
	 */
	public UserDAO(String contextPath) {
		this.path = contextPath;
		fileUtil = new FileUtil();
		loadUsers();
		savePeriodically();
	}

	public Boolean takenUsername(String username) {
		return users.containsKey(username);
	}
	
	public Boolean takenEmail(String email) {
		return users.values().stream()
		.filter( u -> u.getEmail().equals(email))
		.findFirst().orElse(null) != null;
	}

	public User register(User user) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		user.setDateOfRegistration(dtf.format(LocalDateTime.now()).toString());
		user.setRole(Role.USER);
		user.setPoints(0);
		user.setId(users.size());
		users.put(user.getUsername(), user);
		return user;
	}

	public User find(String username, String password) {
		if (!users.containsKey(username)) {
			return null;
		}
		User user = users.get(username);
		if (!user.getPassword().equals(password)) {
			return null;
		}
		return user;
	}

	public Collection<User> findAll() {
		return users.values();
	}

	private void loadUsers() {
		List<String> lines = fileUtil.read(path + USERS);
		StringTokenizer st;

		for (String line : lines) {
			st = new StringTokenizer(line, ";");
			if(line.equals("") || line == null) continue;
				
			while (st.hasMoreTokens()) {
				Integer id = Integer.parseInt(st.nextToken().trim());
				String username = st.nextToken().trim();
				String password = st.nextToken().trim();
				String firstName = st.nextToken().trim();
				String lastName = st.nextToken().trim();
				Role role = Role.valueOf(st.nextToken().trim());
				String phoneNo = st.nextToken().trim();
				String email = st.nextToken().trim();
				String dateOfRegistration = st.nextToken().trim();
				Integer points = Integer.parseInt(st.nextToken().trim());
				points = points > 10 ? 10 : points < 0 ? 0 : points;
				users.put(username, new User(id, username, password, firstName, lastName, role, phoneNo, email,
						dateOfRegistration, points));
			}
		}

	}
	
	private List<String> allToFile(){
		List<String> all = new ArrayList<>();
		
		users.values().forEach(u -> all.add(u.toFile()));
		
		return all;
		
	}
	
	private void savePeriodically() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			fileUtil.saveAll(path + USERS, allToFile());
			//System.out.println("users saved");
			
		}, 5, 5, TimeUnit.SECONDS);
	}
	public List<User> getAll() {
		return new ArrayList<User>(users.values());
	}
	public User findById(Integer id) {
		return users.values().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
	}
	public void updatePoints(String username, Integer points) {
		User u = users.get(username);
		u.setPoints(u.getPoints() - points);
	}
	public void addPoint(Integer id) {
		User user = users.values().stream()
				.filter(u -> u.getId() == id)
				.findFirst().orElse(null);
		user.setPoints(user.getPoints() > 9 ? 10 : user.getPoints()+1);
		
	}
	public void updateUser(User user) {
		User change = users.values().stream()
				.filter(u -> u.getId() == user.getId())
				.findFirst().orElse(null);
		String uname = change.getUsername();
		change.setUsername(user.getUsername());
		change.setFirstName(user.getFirstName());
		change.setLastName(user.getLastName());
		change.setRole(user.getRole());
		users.remove(uname);
		users.put(change.getUsername(), change);
		
	}

}
