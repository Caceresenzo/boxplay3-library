package caceresenzo.libs.boxplay.common.extractor.html;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.StringUtils;

/**
 * Helper class for common html extraction
 * 
 * @author Enzo CACERES
 */
public class HtmlCommonExtractor {
	
	/**
	 * Already prepared regex for "a" link extraction<br>
	 * Groups:<br>
	 * <ul>
	 * <li>0: Full match</li>
	 * <li>1: Url</li>
	 * <li>2: Display string</li>
	 * </ul>
	 */
	public static final String COMMON_LINK_EXTRACTION_REGEX = "\\<a.*?href=[\\\"\\'](.*?)[\\\"\\']\\>(.*?)\\<\\/a\\>";
	
	/**
	 * Already prepared regex for "li" list extraction<br>
	 * Groups:<br>
	 * <ul>
	 * <li>0: Full match</li>
	 * <li>1: Display string</li>
	 * </ul>
	 */
	public static final String COMMON_LIST_EXTRACTION_REGEX = "\\<li.*?\\>(.*?)\\<\\/li\\>";
	
	public static final String COMMON_JS_FUNCTION_EXTRACT_HTML = "(function() { return (document.getElementsByTagName('html')[0].innerHTML); })();";
	
	/**
	 * Disabled constructor, static only
	 */
	private HtmlCommonExtractor() {
		;
	}
	
	/**
	 * Get the src tag of an iframe from html
	 * 
	 * @param html
	 *            Source html
	 * @return Target url, null if not found
	 */
	public static String extractIframeUrlFromHtml(String html) {
		return ProviderHelper.getStaticHelper().extract("\\<iframe.*?src=[\\\"\\'](.*?)[\\\"\\'].*?\\>.*?\\<\\/iframe\\>", html);
	}
	
	/**
	 * Escape a lot of the unicode by programmatically finding them and escaping them with some regex.
	 * 
	 * @param string
	 *            Source string
	 * @return Escaped string
	 */
	public static String escapeUnicode(String string) {
		if (!StringUtils.validate(string)) {
			return string; // Null or empty
		}
		
		try {
			string = new String(string.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			;
		}
		
		Matcher unicodeMatcher = ProviderHelper.getStaticHelper().regex("\\&\\#([\\d]*)[\\;]*", string);
		while (unicodeMatcher.find()) {
			int charactere = ParseUtils.parseInt(unicodeMatcher.group(1), -1);
			
			if (charactere != -1) {
				string = string.replace(unicodeMatcher.group(0), String.valueOf((char) charactere));
			}
		}
		
		return string;
	}
	
	/**
	 * Get the base of a target url, this will return only the domain name with his http prefix, ignoring everything after it.
	 * 
	 * @param url
	 *            Target url
	 * @return Url base
	 */
	public static String extractBaseFromUrl(String url) {
		return ProviderHelper.getStaticHelper().extract("(http[s]*:\\/\\/.*?\\/)", url);
	}
	
	/**
	 * Use it with a {@link String#replaceAll(String, String)} to escape tag that you don't want.<br>
	 * This will create a little regex capable to remove only the tag with his arrows and will also remove all of his attributes like class, id...
	 * 
	 * @param tag
	 *            Target tag you want to remove
	 * @return Regex to use with {@link String#replaceAll(String, String)}
	 */
	public static String createTagReplacer(String tag) {
		return String.format("\\<[\\/]{0,1}%s.*?\\>", tag);
	}
	
}