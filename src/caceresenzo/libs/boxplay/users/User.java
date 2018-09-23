package caceresenzo.libs.boxplay.users;

import caceresenzo.libs.boxplay.base.BaseBoxPlayElement;
import caceresenzo.libs.string.StringUtils;

public class User extends BaseBoxPlayElement {
	
	private final int id;
	private final String token, username, email;
	
	public User(int id, String token, String username, String email) {
		this.id = id;
		this.token = token;
		this.username = username;
		this.email = email;
		
		if (!StringUtils.validate(token)) {
			throw new IllegalArgumentException("The token is not valid. (empty or null)");
		}
	}
	
	public int getId() {
		return id;
	}
	
	public String getIdentificationToken() {
		return token;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public String toString() {
		return "User[id=" + id + ", token=" + token + ", username=" + username + ", email=" + email + "]";
	}
	
}