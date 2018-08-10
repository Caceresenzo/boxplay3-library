package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.network.Downloader;

/**
 * Helper class to provide content more quickly
 * 
 * @author Enzo CACERES
 */
public class ProviderHelper {
	
	protected static ProviderHelper STATIC_HELPER = new ProviderHelper();
	
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
		
		if (parentProvider.isCacheSupported() && cache.containsKey(url) && cache.get(url) != null) {
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
			return Downloader.webget(url, Charset.forName("UTF-8"));
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
	 * Allow basic html extraction like for h1, h2, ..., p, .... elements
	 * 
	 * @param html
	 *            Source html
	 * @return Extracted string if found, null if not
	 */
	public String extractStringFromHtml(String htmlElement, String html) {
		return extract(String.format("\\<%s.*?\\>(.*?)\\<\\/%s\\>", htmlElement, htmlElement), html);
	}
	
	/**
	 * Allow you to extract some <a>...</a> data from html, but will only return the first
	 * 
	 * Html special char will also be escaped (See {@link AdditionalResultData#escapeHtmlChar(String)} for more details)
	 * 
	 * @param html
	 *            Source html
	 * @return Url data, or null if not found
	 */
	public UrlResultData extractUrlFromHtml(String html) {
		Matcher matcher = regex(UrlResultData.EXTRATION_REGEX_FROM_HTML, html);
		
		if (matcher.find()) {
			return new UrlResultData(matcher.group(1), AdditionalResultData.escapeHtmlChar(matcher.group(2)));
		}
		
		return null;
	}
	
	/**
	 * Do the same thing as {{@link #extract(String, String, int)}
	 * 
	 * But use 1 as default grounp
	 * 
	 * @param regex
	 *            Your regex
	 * @param content
	 *            Target content
	 * @return Found string, or null if not found
	 */
	public String extract(String regex, String content) {
		return extract(regex, content, 1);
	}
	
	/**
	 * Quickly extract first find of a match
	 * 
	 * @param regex
	 *            Your regex
	 * @param content
	 *            Target content
	 * @param group
	 *            Specific match group
	 * @return Found string, or null if not found
	 */
	public String extract(String regex, String content, int group) {
		Matcher matcher = regex(regex, content);
		
		if (matcher.find()) {
			return matcher.group(group);
		}
		
		return null;
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
		return Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL).matcher(content != null ? content : "");
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