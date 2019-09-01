package caceresenzo.libs.boxplay.culture.searchngo.data.models.additional;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.json.JsonObject;

/**
 * Holder class to contain an url and a name of a Category
 * 
 * @author Enzo CACERES
 */
public class CategoryResultData extends SimpleData implements DisplayableString {
	
	/* Json Key */
	public static final String JSON_KEY_URL = "url";
	public static final String JSON_KEY_NAME = "name";
	
	/* Constants */
	public static final String KIND = "category";
	
	/* Variables */
	private final String url, name;
	
	/**
	 * Constructor, create a new instance with a name only, url will be considered as <code>null</code>.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param name
	 *            Traget category name.
	 */
	public CategoryResultData(String name) {
		this(null, name);
	}
	
	/**
	 * Constructor, create a new instance with an url and a name.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param url
	 *            Target category url.
	 * @param name
	 *            Traget category name.
	 */
	public CategoryResultData(String url, String name) {
		super(KIND);
		
		this.url = url != null ? url.trim() : null;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : null;
	}
	
	/** @return Category's url. */
	public String getUrl() {
		return url;
	}
	
	/** @return Category's name. */
	public String getName() {
		return name;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getName();
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();

		jsonObject.put(JSON_KEY_URL, url);
		jsonObject.put(JSON_KEY_NAME, name);
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return "CategoryResultData[url=" + url + ", name=" + name + "]";
	}
	
}