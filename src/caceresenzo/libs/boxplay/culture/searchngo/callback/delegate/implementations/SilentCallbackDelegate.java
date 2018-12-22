package caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.implementations;

import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.CallbackDelegate;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

/**
 * Slient delegater doing absolutly nothing.
 * 
 * @author Enzo CACERES
 */
public class SilentCallbackDelegate extends CallbackDelegate {
	
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
		/* No operation */
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
		/* No operation */
	}
	
}