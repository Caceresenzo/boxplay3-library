package caceresenzo.libs.boxplay.api;

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
	private ResponseStatus status;
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
			this.status = ResponseStatus.fromString(String.valueOf(JSON_KEY_STATUS));
			
			this.response = (JsonAware) new JsonParser().parse(ParseUtils.parseString(json.get(JSON_KEY_RESPONSE), null));
			
			if (status.isError()) {
				this.errorMessage = ParseUtils.parseString(json.get(JSON_KEY_MESSAGE), null);
			}
		} catch (Exception exception) {
			this.success = false;
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
	public ResponseStatus getStatus() {
		return status;
	}
	
	/**
	 * @return The usable content returned by the API
	 */
	public JsonAware getResponse() {
		return response;
	}
	
	/**
	 * If the {@link ResponseStatus} of the is an error, this will, sometimes, provide additionnal information
	 * 
	 * @return Optional error message
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public T selfProcess() {
		return sourceRequest.processResponse(this);
	}
	
	public static enum ResponseStatus {
		OK, //
		ERR_MOVIE_NOT_FOUND, //
		ERR_MOVIE_LIST_UNAVAILABLE, //
		ERR_SERIES_NOT_FOUND, //
		ERR_SEASON_NOT_FOUND, //
		ERR_SERIES_LIST_UNAVAILABLE, //
		ERR_INVALID_PAGE, //
		ERR_INVALID_LIMIT, //
		ERR_USER_LOGIN, //
		ERR_USER_REGISTER, //
		UNKNOWN; //
		
		/**
		 * @return True if the status is an error
		 */
		public boolean isError() {
			return toString().startsWith("ERR_");
		}
		
		/**
		 * Get a {@link ResponseStatus} from a {@link String}<br>
		 * If the status if unknown, the value {@link ResponseStatus#UNKNOWN} will be returned
		 * 
		 * @param source
		 *            Source string
		 * @return Corresponding {@link ResponseStatus}
		 */
		public static ResponseStatus fromString(String source) {
			for (ResponseStatus status : values()) {
				if (status.toString().equalsIgnoreCase(source)) {
					return status;
				}
			}
			
			return UNKNOWN;
		}
	}
	
}