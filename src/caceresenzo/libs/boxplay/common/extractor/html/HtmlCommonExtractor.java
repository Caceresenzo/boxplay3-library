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
	 * 
	 * Groups: 0: Full match, 1: Url, 2: Display string
	 */
	public static final String COMMON_LINK_EXTRACTION_REGEX = "\\<a.*?href=\\\"(.*?)\\\"\\>(.*?)\\<\\/a\\>";
	
	/**
	 * Already prepared regex for "li" list extraction<br>
	 * 
	 * Groups: 0: Full match, 1: Display string
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
		return ProviderHelper.getStaticHelper().extract("\\<iframe\\ssrc=\\\"(.*?)\".*?\\>\\<\\/iframe\\>", html);
	}
	
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
	
}