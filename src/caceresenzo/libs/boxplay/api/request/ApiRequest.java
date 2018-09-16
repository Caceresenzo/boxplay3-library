package caceresenzo.libs.boxplay.api.request;

import caceresenzo.libs.boxplay.api.ApiResponse;

/**
 * Abstract class used to implements other {@link ApiRequest}
 * 
 * @author Enzo CACERES
 */
public abstract class ApiRequest<T> {
	/* Constants */
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;
	
	/* Url method */
	protected String urlFormat;
	
	/* Constructor */
	protected ApiRequest(String urlFormat) {
		this.urlFormat = urlFormat;
	}
	
	/**
	 * Used to choose witch HTTP method will be used to fetch informations<br>
	 * Must {@link Override} if this is different of {@link #METHOD_GET}
	 * 
	 * @return {@link #METHOD_GET} or {@link #METHOD_GET}
	 */
	public int getFetchMethod() {
		return METHOD_GET;
	}
	
	/**
	 * Return a formatted string to be used when calling the api<br>
	 * Must {@link Override} if your url have arguments
	 * 
	 * @return Formatted Url
	 */
	public String forge() {
		if (urlFormat.contains("%s")) {
			throw new IllegalStateException("This url have argument, please Override his function in the corresponding request");
		}
		
		return urlFormat;
	}
	
	/**
	 * Process a response
	 * 
	 * @param apiResponse
	 *            Response from the API
	 * @return Some objects that are decided by the class origin
	 */
	public abstract T processResponse(ApiResponse apiResponse);
	
}