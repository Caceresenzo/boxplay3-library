package caceresenzo.libs.boxplay.common.extractor.html;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;

/**
 * Helper class for common html extraction
 * 
 * @author Enzo CACERES
 */
public class HtmlCommonExtractor {
	
	public static final String COMMON_LINK_EXTRACTION_REGEX = "\\<a.*?href=\\\"(.*?)\\\"\\>(.*?)\\<\\/a\\>";
	
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
	
}