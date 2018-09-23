package caceresenzo.libs.boxplay.api.response;

public enum ApiResponseStatus {
	/* List */
	ERR_INVALID_PAGE, //
	ERR_INVALID_LIMIT, //
	
	/* Movies */
	ERR_MOVIE_NOT_FOUND, //
	ERR_MOVIE_NOT_AVAILABLE, //
	ERR_MOVIE_LIST_UNAVAILABLE, //
	
	/* Series */
	ERR_SERIES_NOT_FOUND, //
	ERR_SERIES_NOT_AVAILABLE, //
	ERR_SERIES_LIST_UNAVAILABLE, //
	
	/* Animes */
	ERR_ANIMES_NOT_FOUND, //
	ERR_ANIMES_NOT_AVAILABLE, //
	ERR_ANIMES_LIST_UNAVAILABLE, //
	
	/* Season */
	ERR_SEASON_NOT_FOUND, //
	
	/* Login */
	ERR_USER_LOGIN, //
	ERR_USER_NOT_FOUND, //
	
	/* Register */
	ERR_USER_REGISTER, //
	ERR_USER_ALREADY_EXIST, //
	
	/* Identification */
	ERR_USER_USER_INVALID, //
	ERR_USER_INVALID_FORMAT, //
	
	/* Other */
	OK, //
	UNKNOWN; //
	
	/**
	 * @return True if the status is an error
	 */
	public boolean isError() {
		return toString().startsWith("ERR_");
	}
	
	/**
	 * Get a {@link ApiResponseStatus} from a {@link String}<br>
	 * If the status if unknown, the value {@link ApiResponseStatus#UNKNOWN} will be returned
	 * 
	 * @param source
	 *            Source string
	 * @return Corresponding {@link ApiResponseStatus}
	 */
	public static ApiResponseStatus fromString(String source) {
		for (ApiResponseStatus status : values()) {
			if (source.equalsIgnoreCase(status.toString())) {
				return status;
			}
		}
		
		return UNKNOWN;
	}
}