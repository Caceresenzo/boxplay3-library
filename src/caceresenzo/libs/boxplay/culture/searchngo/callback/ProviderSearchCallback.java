package caceresenzo.libs.boxplay.culture.searchngo.callback;

import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public interface ProviderSearchCallback {
	
	void onProviderSearchStarting(SearchAndGoProvider provider);
	
	void onProviderSorting(SearchAndGoProvider provider);
	
	void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap);
	
	void onProviderFailed(SearchAndGoProvider provider, Exception exception);
	
}