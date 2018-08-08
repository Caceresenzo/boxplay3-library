package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.network.Downloader;

/**
 * Helper class to provide content more quickly
 * 
 * @author Enzo CACERES
 */
public class ProviderHelper {
	
	private SearchAndGoProvider parentProvider;
	
	private SearchEngine searchEngine;
	
	private Map<Object, Object> cache;
	
	/**
	 * Create a non-contextualized helper, some function will not be available, like cache
	 */
	public ProviderHelper() {
		this(null);
	}
	
	/**
	 * Create a contextualized helper
	 * 
	 * @param parentProvider
	 *            ParentHelper used to create this helper
	 */
	public ProviderHelper(SearchAndGoProvider parentProvider) {
		this.parentProvider = parentProvider;
		
		this.searchEngine = new SearchEngine();
		
		if (parentProvider != null) {
			this.cache = new HashMap<>();
		}
	}
	
	/**
	 * Return local Search Engine instance
	 * 
	 * @return An instance
	 */
	public SearchEngine getSearchEngine() {
		return searchEngine;
	}
	
	/**
	 * Allow you to get a cache version if the parent provider support it
	 * 
	 * If the page is missing from the cache, it will be added when il will be download for the first time
	 * 
	 * @param url
	 *            Target url
	 * @return Target content
	 * @throws IllegalArgumentException
	 *             If the parent provider is null (like in static mode)
	 */
	public String downloadPageCache(String url) {
		checkProviderValidity();
		
		if (parentProvider.isCacheSupported() && cache.containsKey(url)) {
			return (String) cache.get(url);
		}
		
		String content = downloadPage(url);
		
		if (parentProvider.isCacheSupported()) {
			cache.put(url, content);
		}
		
		return content;
	}
	
	/**
	 * Download a page content
	 * 
	 * If any error append when downloading, it will be just ignored, and will return null
	 * 
	 * @param url
	 *            Target url
	 * @return Target content
	 */
	public String downloadPage(String url) {
		try {
			return Downloader.webget(url);
		} catch (Exception exception) {
			return null;
		}
	}
	
	/**
	 * Quickly reset the cache
	 * 
	 * @return Himself, so you can do more function behind
	 */
	public ProviderHelper resetCache() {
		checkProviderValidity();
		
		cache.clear();
		
		return this;
	}
	
	/**
	 * Faster regex code (just quick access, not quicker code)
	 * 
	 * @param regex
	 *            Your regex
	 * @param content
	 *            Target content
	 * @return A compiled matcher
	 */
	public Matcher regex(String regex, String content) {
		return Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL).matcher(content);
	}
	
	/**
	 * Some string found online are html-encoded, use this function to decode a string
	 * 
	 * @param source
	 *            The source string
	 * @return Escaped string
	 */
	public String escapeHtmlSpecialCharactere(String source) {
		return source; // TODO: Finish this function
	}
	
	/**
	 * Internal function to throw an exception if the parent provider is null
	 * 
	 * @throws IllegalArgumentException
	 *             if no valid parent provider is available
	 */
	private void checkProviderValidity() {
		if (parentProvider == null) {
			throw new IllegalArgumentException("This method can't be used without a parent provider");
		}
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "ProviderHelper[cache.size=" + cache.size() + "]";
	}
	
}