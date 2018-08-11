package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;

/**
 * Holder class to contain an url and a name of a Chapter
 * 
 * @author Enzo CACERES
 */
public class ChapterItemResultData implements DisplayableString, ViewableContent {
	
	private final String url, name, title;
	
	/**
	 * Constructor, create a new instance with an url and a name only, title will be considered as null
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param url
	 *            Traget chapter url
	 * @param name
	 *            Traget chapter name
	 */
	public ChapterItemResultData(String url, String name) {
		this(url, name, null);
	}
	
	/**
	 * Constructor, create a new instance with an url and a name
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param url
	 *            Target chapter url
	 * @param name
	 *            Traget chapter name
	 * @param title
	 *            Traget chapter title
	 */
	public ChapterItemResultData(String url, String name, String title) {
		this.url = url;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
		this.title = title != null ? AdditionalResultData.escapeHtmlChar(title.trim()) : title;
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
	
	/**
	 * Get the chapter's title
	 * 
	 * @return The title
	 */
	public String getTitle() {
		return title;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getName() + (getTitle() != null && !getTitle().isEmpty() ? " - " + getTitle() : "");
	}
	
	@Override
	public ContentViewerType getContentViewerType() {
		return ContentViewerType.IMAGE;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "ChapterItemResultData[url=" + url + ", name=" + name + "]";
	}
	
}