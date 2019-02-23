package caceresenzo.libs.boxplay.culture.searchngo.providers.exceptions.implementations;

import caceresenzo.libs.boxplay.culture.searchngo.providers.exceptions.ProviderException;

public class ProviderFetchFailedException extends ProviderException {
	
	public ProviderFetchFailedException() {
		super();
	}
	
	public ProviderFetchFailedException(String message) {
		super(message);
	}
	
	public ProviderFetchFailedException(Throwable cause) {
		super(cause);
	}
	
	public ProviderFetchFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
}