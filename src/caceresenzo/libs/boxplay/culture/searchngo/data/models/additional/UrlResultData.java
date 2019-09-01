package caceresenzo.libs.boxplay.culture.searchngo.data.models.additional;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.json.JsonObject;

/**
 * Holder class to contain a target url and a displayed string of a HTML link.
 * 
 * @author Enzo CACERES
 */
public class UrlResultData extends SimpleData implements DisplayableString {
	
	/* Json Key */
	public static final String JSON_KEY_TARGET_URL = "target_url";
	public static final String JSON_KEY_DISPLAYED_STRING = "string";
	
	/* Regex */
	public static final String EXTRATION_REGEX_FROM_HTML = "\\<a.*?href[\\s]*=[\\s]*[\\\"\\']{1}(.*?)[\\\"\\']{1}.*?\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>";
	
	/* Constants */
	public static final String KIND = "target_url";
	
	/* Variables */
	private String targetUrl, string;
	
	/**
	 * Create a new instance with a display string only, targetUrl will be considered as <code>null</code>.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param string
	 *            Displayed string.
	 */
	public UrlResultData(String string) {
		this(null, string);
	}
	
	/**
	 * Create a new instance with a targetUrl and a display string.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param targetUrl
	 *            Target url.
	 * @param string
	 *            Displayed string.
	 */
	public UrlResultData(String targetUrl, String string) {
		super(KIND);
		
		this.targetUrl = targetUrl != null ? targetUrl.trim() : null;
		this.string = string != null ? string.trim() : null;
	}
	
	/** @return Link's target url. */
	public String getTargetUrl() {
		return targetUrl;
	}
	
	/** @return Link's displayed string. */
	public String getString() {
		return string;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getString();
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();
		
		jsonObject.put(JSON_KEY_TARGET_URL, targetUrl);
		jsonObject.put(JSON_KEY_DISPLAYED_STRING, string);
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return "UrlResultData[targetUrl=" + targetUrl + ", string=" + string + "]";
	}
	
}