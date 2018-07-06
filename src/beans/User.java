package beans;

import enums.Role;

public class User {

	private Integer id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Role role;
	private String phoneNo;
	private String email;
	private String dateOfRegistration;
	private Integer points;

	public User() {
	}

	public User(Integer id, String username, String password, String firstName, String lastName, Role role,
			String phoneNo, String email, String dateOfRegistration, Integer points) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
		this.phoneNo = phoneNo;
		this.email = email;
		this.dateOfRegistration = dateOfRegistration;
		this.points = points;
	}

	public final Integer getId() {
		return id;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public final Role getRole() {
		return role;
	}

	public final void setRole(Role role) {
		this.role = role;
	}

	public final String getPhoneNo() {
		return phoneNo;
	}

	public final void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public final String getDateOfRegistration() {
		return dateOfRegistration;
	}

	public final void setDateOfRegistration(String dateOfRegistration) {
		this.dateOfRegistration = dateOfRegistration;
	}

	public final Integer getPoints() {
		return points;
	}

	public final void setPoints(Integer points) {
		this.points = points;
	}

	public String toFile() {
		return System.getProperty("line.separator") + id + ";" + username + ";" + password + ";" + firstName + ";"
				+ lastName + ";" + role.toString() + ";" + phoneNo + ";" + email + ";" + dateOfRegistration + ";"
				+ points;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", role=" + role + ", phoneNo=" + phoneNo + ", email=" + email
				+ ", dateOfRegistration=" + dateOfRegistration + ", points=" + points + "]";
	}

}
