package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.callback.ProviderSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.callback.SearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

/**
 * Manager class to handle every provider
 * 
 * @author Enzo CACERES
 */
public class ProviderCallback {
	
	private static List<SearchCallback> searchCallbacks = new ArrayList<>();
	private static List<ProviderSearchCallback> providerSearchCallbacks = new ArrayList<>();
	
	/**
	 * Disabled constructor, static only
	 */
	private ProviderCallback() {
		;
	}
	
	public static void registerSearchallback(SearchCallback searchCallback) {
		if (!searchCallbacks.contains(searchCallback)) {
			searchCallbacks.add(searchCallback);
		}
	}
	
	public static void unregisterSearchallback(SearchCallback searchCallback) {
		if (searchCallbacks.contains(searchCallback)) {
			searchCallbacks.remove(searchCallback);
		}
	}
	
	public static void registerProviderSearchallback(ProviderSearchCallback providerSearchCallback) {
		if (!providerSearchCallbacks.contains(providerSearchCallback)) {
			providerSearchCallbacks.add(providerSearchCallback);
		}
	}
	
	public static void unregisterProviderSearchallback(ProviderSearchCallback providerSearchCallback) {
		if (providerSearchCallbacks.contains(providerSearchCallback)) {
			providerSearchCallbacks.remove(providerSearchCallback);
		}
	}
	
	public static  void onSearchStarting() {
		for (SearchCallback searchCallback : searchCallbacks) {
			searchCallback.onSearchStarting();
		}
	}
	
	public static  void onSearchFinished(Map<String, SearchAndGoResult> workmap) {
		for (SearchCallback searchCallback : searchCallbacks) {
			searchCallback.onSearchFinished(workmap);
		}
	}
	
	public static  void onSearchFail(Exception exception) {
		for (SearchCallback searchCallback : searchCallbacks) {
			searchCallback.onSearchFail(exception);
		}
	}
	
	public static  void onProviderSearchStarting(SearchAndGoProvider provider) {
		for (ProviderSearchCallback providerSearchCallback : providerSearchCallbacks) {
			providerSearchCallback.onProviderSearchStarting(provider);
		}
	}
	
	public static  void onProviderSorting(SearchAndGoProvider provider) {
		for (ProviderSearchCallback providerSearchCallback : providerSearchCallbacks) {
			providerSearchCallback.onProviderSorting(provider);
		}
	}
	
	public static  void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
		for (ProviderSearchCallback providerSearchCallback : providerSearchCallbacks) {
			providerSearchCallback.onProviderSearchFinished(provider, workmap);
		}
	}
	
	public static  void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
		for (ProviderSearchCallback providerSearchCallback : providerSearchCallbacks) {
			providerSearchCallback.onProviderFailed(provider, exception);
		}
	}
	
}