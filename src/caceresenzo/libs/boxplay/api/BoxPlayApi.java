package caceresenzo.libs.boxplay.api;

import caceresenzo.libs.boxplay.api.request.ApiRequest;
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
	public BoxPlayApi(String token) {
		this.token = token;
	}
	
	private String forgeUrl(ApiRequest<?> apiRequest) {
		return forgeUrl(apiRequest.forge());
	}
	
	private String forgeUrl(String forgedUrl) {
		return API_URL + forgedUrl + "?token=" + token;
	}
	
	public ApiResponse call(ApiRequest<?> apiRequest) {
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
				break;
			}
		}
		
		String response = null;
		try {
			response = request.ensureSuccess().asString().getBody();
		} catch (Exception exception) {
			;
		}
		
		return new ApiResponse(apiRequest, response);
	}
	
}