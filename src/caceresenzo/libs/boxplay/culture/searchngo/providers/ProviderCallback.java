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
	
	public static void registerSearchCallback(SearchCallback callback) {
		if (!searchCallbacks.contains(callback)) {
			searchCallbacks.add(callback);
		}
	}
	
	public static void unregisterSearchCallback(SearchCallback callback) {
		if (searchCallbacks.contains(callback)) {
			searchCallbacks.remove(callback);
		}
	}
	
	public static void registerProviderSearchCallback(ProviderSearchCallback callback) {
		if (!providerSearchCallbacks.contains(callback)) {
			providerSearchCallbacks.add(callback);
		}
	}
	
	public static void unregisterProviderSearchCallback(ProviderSearchCallback callback) {
		if (providerSearchCallbacks.contains(callback)) {
			providerSearchCallbacks.remove(callback);
		}
	}
	
	public static void onSearchStarting() {
		for (SearchCallback callback : searchCallbacks) {
			callback.onSearchStarting();
		}
	}
	
	public static void onSearchFinished(Map<String, SearchAndGoResult> workmap) {
		for (SearchCallback callback : searchCallbacks) {
			callback.onSearchFinished(workmap);
		}
	}
	
	public static void onSearchFail(Exception exception) {
		for (SearchCallback callback : searchCallbacks) {
			callback.onSearchFail(exception);
		}
	}
	
	public static void onProviderSearchStarting(SearchAndGoProvider provider) {
		for (ProviderSearchCallback callback : providerSearchCallbacks) {
			callback.onProviderSearchStarting(provider);
		}
	}
	
	public static void onProviderSorting(SearchAndGoProvider provider) {
		for (ProviderSearchCallback callback : providerSearchCallbacks) {
			callback.onProviderSorting(provider);
		}
	}
	
	public static void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
		for (ProviderSearchCallback callback : providerSearchCallbacks) {
			callback.onProviderSearchFinished(provider, workmap);
		}
	}
	
	public static void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
		for (ProviderSearchCallback callback : providerSearchCallbacks) {
			callback.onProviderFailed(provider, exception);
		}
	}
}