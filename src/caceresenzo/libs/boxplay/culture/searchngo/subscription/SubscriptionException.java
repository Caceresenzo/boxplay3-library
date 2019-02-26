package caceresenzo.libs.boxplay.culture.searchngo.subscription;

public class SubscriptionException extends RuntimeException {
	
	/** @see RuntimeException#RuntimeException() */
	public SubscriptionException() {
		super();
	}
	
	/** @see RuntimeException#RuntimeException(String) */
	public SubscriptionException(String message) {
		super(message);
	}
	
	/** @see RuntimeException#RuntimeException(Throwable) */
	public SubscriptionException(Throwable cause) {
		super(cause);
	}
	
	/** @see RuntimeException#RuntimeException(String, Throwable) */
	public SubscriptionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/** @see RuntimeException#RuntimeException(String, Throwable, boolean, boolean) */
	public SubscriptionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
}