package caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.implementations;

import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.CallbackDelegate;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.logger.Logger;

/**
 * Slient delegater doing absolutly nothing.
 * 
 * @author Enzo CACERES
 */
public class OnlyExceptionLoggingCallbackDelegate extends CallbackDelegate {
	
	@Override
	public void onProviderSearchStarting(SearchAndGoProvider provider) {
		/* No operation */
	}
	
	@Override
	public void onProviderSorting(SearchAndGoProvider provider) {
		/* No operation */
	}
	
	@Override
	public void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
		/* No operation */
	}
	
	@Override
	public void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
		Logger.exception(exception, "onSearchFail(provider, exception): %s, %s", provider, exception);
	}
	
	@Override
	public void onSearchStarting() {
		/* No operation */
	}
	
	@Override
	public void onSearchSorting() {
		/* No operation */
	}
	
	@Override
	public void onSearchFinished(Map<String, SearchAndGoResult> workmap) {
		/* No operation */
	}
	
	@Override
	public void onSearchFail(Exception exception) {
		Logger.exception(exception, "onSearchFail(exception): %s", exception);
	}
	
}