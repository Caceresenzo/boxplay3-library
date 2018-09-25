package caceresenzo.libs.boxplay.api.response;

import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.json.JsonAware;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.parse.ParseUtils;

/**
 * {@link ApiResponse} created for parsing content returned by the API
 * 
 * @author Enzo CACERES
 */
public class ApiResponse<T> {
	
	/* Constants */
	public static final String JSON_KEY_SUCCESS = "success";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_RESPONSE = "response";
	public static final String JSON_KEY_MESSAGE = "message";
	
	/* Source */
	private final ApiRequest<T> sourceRequest;
	private final String rawResponse;
	
	/* Response */
	private boolean success;
	private ApiResponseStatus status;
	private String errorMessage;
	private JsonAware response;
	
	/**
	 * Create a new reponse
	 * 
	 * @param sourceRequest
	 *            {@link ApiRequest} used to fetch this response
	 * @param response
	 *            Source {@link String} json
	 */
	public ApiResponse(ApiRequest<T> sourceRequest, String response) {
		this.sourceRequest = sourceRequest;
		this.rawResponse = response;
		
		try {
			JsonObject json = (JsonObject) new JsonParser().parse(response);
			
			this.success = ParseUtils.parseBoolean(json.get(JSON_KEY_SUCCESS), false);
			this.status = ApiResponseStatus.fromString((String) json.get(JSON_KEY_STATUS));
			
			if (status.isError()) {
				this.errorMessage = ParseUtils.parseString(json.get(JSON_KEY_MESSAGE), null);
			}
			
			this.response = (JsonAware) new JsonParser().parse(ParseUtils.parseString(json.get(JSON_KEY_RESPONSE), null));
		} catch (Exception exception) {
			this.success = false;
			this.status = ApiResponseStatus.UNKNOWN;
		}
	}
	
	/**
	 * Tell you if the {@link ApiResponse} is a success and the {@link JsonAware} response is not null<br>
	 * After this check passed, you should be able to work without risking any {@link NullPointerException} caused by response
	 * 
	 * @return The equivalent of <code>(isSuccess() && getResponse() != null)</code>
	 */
	public boolean isUsable() {
		return isSuccess() && getResponse() != null;
	}
	
	/**
	 * @return The {@link ApiRequest} used to fetch this {@link ApiResponse}
	 */
	public ApiRequest<T> getSourceRequest() {
		return sourceRequest;
	}
	
	/**
	 * @return The raw json {@link String} returned by the API
	 */
	public String getRawResponse() {
		return rawResponse;
	}
	
	/**
	 * Tell you if this request is a success<br>
	 * If not, check the error with the {@link #getStatus()} method
	 * 
	 * @return Success state of the request
	 */
	public boolean isSuccess() {
		return success;
	}
	
	/**
	 * @return The status of the response
	 */
	public ApiResponseStatus getStatus() {
		return status;
	}
	
	/**
	 * @return The usable content returned by the API
	 */
	public JsonAware getResponse() {
		return response;
	}
	
	/**
	 * If the {@link ApiResponseStatus} of the is an error, this will, sometimes, provide additionnal information
	 * 
	 * @return Optional error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public T selfProcess() {
		return sourceRequest.processResponse(this);
	}
	
}