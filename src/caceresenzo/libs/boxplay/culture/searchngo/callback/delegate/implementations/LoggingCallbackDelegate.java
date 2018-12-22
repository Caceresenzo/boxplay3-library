package caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.implementations;

import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.CallbackDelegate;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.logger.Logger;

/**
 * Callback delegater to output every event in the console with the {@link Logger}.
 * 
 * @author Enzo CACERES
 */
public class LoggingCallbackDelegate extends CallbackDelegate {
	
	@Override
	public void onProviderSearchStarting(SearchAndGoProvider provider) {
		Logger.info("onProviderSearchStarting(provider): %s", provider);
	}
	
	@Override
	public void onProviderSorting(SearchAndGoProvider provider) {
		Logger.info("onProviderSorting(provider): %s", provider);
	}
	
	@Override
	public void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
		Logger.info("onProviderSearchFinished(provider, workmap): %s, %s", provider, workmap);
	}
	
	@Override
	public void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
		Logger.exception(exception, "onSearchFail(provider, exception): %s, %s", provider, exception);
	}
	
	@Override
	public void onSearchStarting() {
		Logger.info("onSearchStarting()");
	}
	
	@Override
	public void onSearchSorting() {
		Logger.info("onSearchSorting()");
	}
	
	@Override
	public void onSearchFinished(Map<String, SearchAndGoResult> workmap) {
		Logger.info("onSearchFinished(workmap): %s", workmap);
	}
	
	@Override
	public void onSearchFail(Exception exception) {
		Logger.exception(exception, "onSearchFail(exception): %s", exception);
	}
	
}