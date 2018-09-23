package caceresenzo.libs.boxplay.api.request.implementations.user;

import caceresenzo.libs.boxplay.api.request.ApiRequest;

public abstract class UserApiRequest<T> extends ApiRequest<T> {
	
	/* Constants */
	public static final String USERNAME_PATTERN = "^[\\w\\d-]{4,16}$";
	
	protected UserApiRequest(String urlFormat) {
		super("users/" + urlFormat);
	}
	
}