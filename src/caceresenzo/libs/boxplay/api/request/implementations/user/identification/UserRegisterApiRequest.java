package caceresenzo.libs.boxplay.api.request.implementations.user.identification;

import java.util.HashMap;
import java.util.Map;

import caceresenzo.libs.boxplay.api.request.implementations.user.UserApiRequest;
import caceresenzo.libs.boxplay.api.response.ApiResponse;

public class UserRegisterApiRequest extends UserApiRequest<ApiResponse<?>> {
	
	/* Constants */
	public static final String PARAMETER_USERNAME = "username";
	public static final String PARAMETER_EMAIL = "email";
	public static final String PARAMETER_PASSWORD = "password";
	
	public static final String JSON_KEY_USERNAME = "username";
	
	/* Variables */
	private final String username, email, password;
	
	/* Constructor */
	public UserRegisterApiRequest(String username, String email, String password) {
		super("register");
		
		this.username = username;
		this.email = email;
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
		parameters.put(PARAMETER_EMAIL, email);
		parameters.put(PARAMETER_PASSWORD, password);
		
		return parameters;
	}
	
	@Override
	public ApiResponse<ApiResponse<?>> processResponse(ApiResponse<ApiResponse<?>> apiResponse) {
		return apiResponse;
	}
	
}