package caceresenzo.libs.boxplay.api;

import java.util.Map.Entry;

import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.users.User;
import caceresenzo.libs.http.client.webb.Request;
import caceresenzo.libs.http.client.webb.Webb;

public class BoxPlayApi {
	
	/* Constants */
	public static final String API_BASE_URL = "https://api.boxplay.io";
	public static final int API_VERSION = 1;
	
	public static final String API_URL = API_BASE_URL + "/v" + API_VERSION + "/";
	
	/* Token */
	private final String token;
	
	/* Constructor */
	public BoxPlayApi(User user) {
		this(user.getIdentificationToken());
	}
	
	/* Constructor */
	public BoxPlayApi(String token) {
		this.token = token;
	}
	
	/**
	 * Forge the url with a {@link ApiRequest}
	 * 
	 * @param apiRequest
	 *            Source
	 * @return A new forged url with the {@link BoxPlayApi} token
	 */
	private String forgeUrl(ApiRequest<?> apiRequest) {
		return forgeUrl(apiRequest.forge());
	}
	
	/**
	 * Forge the url with a {@link ApiRequest}
	 * 
	 * @param forgedUrl
	 *            Already forged url (like with a {@link ApiRequest} for exemple)
	 * @return A new forged url with the {@link BoxPlayApi} token
	 */
	private String forgeUrl(String forgedUrl) {
		return API_URL + forgedUrl + "?token=" + token;
	}
	
	/**
	 * Call the api
	 * 
	 * @param apiRequest
	 *            Used {@link ApiRequest} that will be used to fetch the result
	 * @return The response of the api under the form of an {@link ApiResponse}
	 */
	public <T> ApiResponse<T> call(ApiRequest<T> apiRequest) {
		String forgedUrl = forgeUrl(apiRequest);
		
		if (apiRequest.getRequestSettings() != null) {
			forgedUrl = apiRequest.getRequestSettings().createUrl(forgedUrl);
		}
		
		Webb webb = Webb.create(true);
		Request request;
		
		switch (apiRequest.getFetchMethod()) {
			default:
			case ApiRequest.METHOD_GET: {
				request = webb.get(forgedUrl);
				break;
			}
			
			case ApiRequest.METHOD_POST: {
				request = webb.post(forgedUrl);
				
				for (Entry<String, Object> parameter : apiRequest.getPostParameters().entrySet()) {
					request.param(parameter.getKey(), parameter.getValue());
				}
				break;
			}
		}
		
		String response = null;
		try {
			response = request.ensureSuccess().asString().getBody();
		} catch (Exception exception) {
			;
		}
		
		return new ApiResponse<T>(apiRequest, response);
	}
	
}