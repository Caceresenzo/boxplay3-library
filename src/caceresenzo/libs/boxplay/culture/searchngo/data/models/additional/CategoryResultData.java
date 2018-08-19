package caceresenzo.libs.boxplay.culture.searchngo.data.models.additional;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;

/**
 * Holder class to contain an url and a name of a Category
 * 
 * @author Enzo CACERES
 */
public class CategoryResultData extends SimpleData implements DisplayableString {
	
	private final String url, name;
	
	/**
	 * Constructor, create a new instance with a name only, url will be considered as null
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param name
	 *            Traget category name
	 */
	public CategoryResultData(String name) {
		this(null, name);
	}
	
	/**
	 * Constructor, create a new instance with an url and a name
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param url
	 *            Target category url
	 * @param name
	 *            Traget category name
	 */
	public CategoryResultData(String url, String name) {
		this.url = url != null ? url.trim() : null;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : null;
	}
	
	/**
	 * Get the url
	 * 
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Get the name
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getName();
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "CategoryResultData[url=" + url + ", name=" + name + "]";
	}
	
}