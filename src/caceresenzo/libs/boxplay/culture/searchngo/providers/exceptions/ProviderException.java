package caceresenzo.libs.boxplay.culture.searchngo.providers.exceptions;

public class ProviderException extends RuntimeException {
	
	/** @see RuntimeException#RuntimeException() */
	public ProviderException() {
		super();
	}
	
	/** @see RuntimeException#RuntimeException(String) */
	public ProviderException(String message) {
		super(message);
	}
	
	/** @see RuntimeException#RuntimeException(Throwable) */
	public ProviderException(Throwable cause) {
		super(cause);
	}
	
	/** @see RuntimeException#RuntimeException(String, Throwable) */
	public ProviderException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/** @see RuntimeException#RuntimeException(String, Throwable, boolean, boolean) */
	public ProviderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}
