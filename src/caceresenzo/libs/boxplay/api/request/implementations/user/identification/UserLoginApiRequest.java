package caceresenzo.libs.boxplay.api.request.implementations.user.identification;

import java.util.HashMap;
import java.util.Map;

import caceresenzo.libs.boxplay.api.request.implementations.user.UserApiRequest;
import caceresenzo.libs.boxplay.api.response.ApiResponse;
import caceresenzo.libs.boxplay.users.User;

public class UserLoginApiRequest extends UserApiRequest<User> {
	
	/* Constants */
	public static final String PARAMETER_USERNAME = "username";
	public static final String PARAMETER_PASSWORD = "password";
	
	public static final String JSON_KEY_USERNAME = "username";
	
	/* Variables */
	private final String username, password;
	
	/* Constructor */
	public UserLoginApiRequest(String username, String password) {
		super("login");
		
		this.username = username;
		this.password = password;
	}
	
	@Override
	public int getFetchMethod() {
		return METHOD_POST;
	}
	
	@Override
	public Map<String, Object> getPostParameters() {
		Map<String, Object> parameters = new HashMap<>();
		
		parameters.put(PARAMETER_USERNAME, username);
		parameters.put(PARAMETER_PASSWORD, password);
		
		return parameters;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public User processResponse(ApiResponse<User> apiResponse) {
		// TODO: Complete when the api will be finished
		
		if (apiResponse.isUsable()) {
			Map<String, Object> dataMap = (Map<String, Object>) apiResponse.getResponse();
			
			int id = 0;
			String token = "not null";
			String username = (String) dataMap.get(JSON_KEY_USERNAME);
			String email = "";
			
			return new User(id, token, username, email);
		}
		
		return null;
	}
	
}