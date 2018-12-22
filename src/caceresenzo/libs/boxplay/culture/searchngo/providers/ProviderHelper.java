package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.http.client.webb.Request;
import caceresenzo.libs.http.client.webb.Response;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.network.Downloader;

/**
 * Helper class to provide content more quickly
 * 
 * @author Enzo CACERES
 */
public class ProviderHelper implements Serializable {
	
	/* Statics */
	protected static ProviderHelper STATIC_HELPER = new ProviderHelper();
	
	/* Variables */
	private SearchAndGoProvider parentProvider;
	
	private SearchEngine searchEngine;
	
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
	 * Encode some data to be used in an url.<br>
	 * The encoding used is UTF-8.<br>
	 * <br>
	 * If encoding is not available, the original data will be returned.
	 * 
	 * @param data
	 *            Target data to encode
	 * @return Encoded data
	 */
	public String encodeUrl(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (Exception exception) {
			return data;
		}
	}
	
	/**
	 * See {@link #downloadPageCache(String, Map)} for more info
	 * 
	 * With this function, headers will be null
	 * 
	 * @param url
	 *            Target url
	 * @return Page content
	 */
	public String downloadPageCache(String url) {
		return downloadPageCache(url, null);
	}
	
	/**
	 * Allow you to get a cache version if the parent provider support it
	 * 
	 * If the page is missing from the cache, it will be added when il will be download for the first time
	 * 
	 * @param url
	 *            Target url
	 * @param headers
	 *            Custom headers, ignored if null
	 * @return Page content
	 * @throws IllegalArgumentException
	 *             If the parent provider is null (like in static mode)
	 */
	public String downloadPageCache(String url, Map<String, String> headers) {
		checkProviderValidity();
		
		if (parentProvider.isCacheSupported() && ProviderWeakCache.checkAndValidate(url)) {
			return ProviderWeakCache.get(url);
		}
		
		String content = downloadPage(url, headers, parentProvider.getWorkingCharset());
		
		if (parentProvider.isCacheSupported()) {
			ProviderWeakCache.push(url, content);
		}
		
		return content;
	}
	
	/**
	 * See {@link #downloadPage(String, Map)} for more info.<br>
	 * With this function, headers will be null.
	 * 
	 * @param url
	 *            Target url
	 * @return Page content
	 */
	public String downloadPage(String url) {
		return downloadPage(url, null, "UTF-8");
	}
	
	/**
	 * Download a page content.<br>
	 * If any error append when downloading, it will be just ignored, and will return null.
	 * 
	 * @param url
	 *            Target url
	 * @param headers
	 *            Custom headers, ignored if null
	 * @return Page content
	 */
	public String downloadPage(String url, Map<String, String> headers, String charset) {
		try {
			if (parentProvider != null && parentProvider.isAdvancedDownloaderNeeded()) {
				Request request = Webb.create(!parentProvider.isSslNeeded()).get(url) //
						.chromeUserAgent() //
						.followRedirects(parentProvider.downloadShouldFollowRedirects()) //
						.header(WebbConstante.HDR_ACCEPT_ENCODING, "gzip;q=0,deflate,sdch");
				
				if (headers != null) {
					for (Entry<String, String> entry : headers.entrySet()) {
						request.header(entry.getKey(), entry.getValue());
					}
				}
				
				Response<String> response = request.asString();
				
				if (response.isSuccess()) {
					return response.getBody();
				} else {
					if (response.getErrorBody() == null) {
						return null;
					}
					
					return String.valueOf(response.getErrorBody());
				}
			} else {
				return Downloader.webget(url, headers, Charset.forName(charset));
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Quickly reset the cache.
	 * 
	 * @return Himself, so you can do more function behind
	 */
	public ProviderHelper resetCache() {
		checkProviderValidity();
		
		ProviderWeakCache.clear();
		
		return this;
	}
	
	/**
	 * Allow basic html extraction like for h1, h2, ..., p, .... elements.
	 * 
	 * @param html
	 *            Source html
	 * @return Extracted string if found, null if not
	 */
	public String extractStringFromHtml(String htmlElement, String html) {
		return extract(String.format("\\<%s.*?\\>(.*?)\\<\\/%s\\>", htmlElement, htmlElement), html);
	}
	
	/**
	 * Allow you to extract some <a>...</a> data from html, but will only return the first.<br>
	 * Html special char will also be escaped (See {@link AdditionalResultData#escapeHtmlChar(String)} for more details).
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
	 * Do the same thing as {{@link #extract(String, String, int)}.<br>
	 * But use 1 as default group0
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
	 * Quickly extract first find of a match.
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
	 * Faster regex code (just quick access, not quicker code).
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
	 * Some string found online are html-encoded, use this function to decode a string.
	 * 
	 * @param source
	 *            The source string
	 * @return Escaped string
	 */
	public String escapeHtmlSpecialCharacters(String source) {
		return source; // TODO: Finish this function
	}
	
	/**
	 * Internal function to throw an exception if the parent provider is null.
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
	 * Get the instance of the static helper.
	 * 
	 * @return Static helper
	 */
	public static ProviderHelper getStaticHelper() {
		return STATIC_HELPER;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "ProviderHelper[cacheSize=" + ProviderWeakCache.cacheSize() + "]";
	}
	
}