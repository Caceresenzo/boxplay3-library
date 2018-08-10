package caceresenzo.libs.boxplay.culture.searchngo.data.models;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;

/**
 * Holder class to contain a target url and a display string of a html link (<a>)
 * 
 * @author Enzo CACERES
 */
public class UrlResultData implements DisplayableString {
	
	public static final String EXTRATION_REGEX_FROM_HTML = "\\<a.*?href[\\s]*=[\\s]*\\\"(.*?)\".*?\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>";
	
	private String targetUrl, string;
	
	/**
	 * Constructor, create a new instance with a display string only, targetUrl will be considered as null
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param targetUrl
	 *            Target url
	 * @param string
	 *            Display string
	 */
	public UrlResultData(String string) {
		this(null, string);
	}
	
	/**
	 * Constructor, create a new instance with a targetUrl and a display string
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param targetUrl
	 *            Target url
	 * @param string
	 *            Display string
	 */
	public UrlResultData(String targetUrl, String string) {
		this.targetUrl = targetUrl != null ? targetUrl.trim() : null;
		this.string = string != null ? string.trim() : null;
	}
	
	/**
	 * Get the target url
	 * 
	 * @return The target url
	 */
	public String getTargetUrl() {
		return targetUrl;
	}
	
	/**
	 * Get the displayed string
	 * 
	 * @return The string
	 */
	public String getString() {
		return string;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getString();
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "UrlResultData[targetUrl=" + targetUrl + ", string=" + string + "]";
	}
	
}