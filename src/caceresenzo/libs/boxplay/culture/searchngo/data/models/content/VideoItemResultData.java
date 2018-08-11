package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;

/**
 * Holder class to contain an url and a name of a Video
 * 
 * @author Enzo CACERES
 */
public class VideoItemResultData implements DisplayableString, ViewableContent {
	
	private final String url, name;
	
	/**
	 * Constructor, create a new instance with an url and a name
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param url
	 *            Target video url
	 * @param name
	 *            Traget video name
	 */
	public VideoItemResultData(String url, String name) {
		this.url = url;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
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
	
	@Override
	public ContentViewerType getContentViewerType() {
		return ContentViewerType.VIDEO;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "VideoItemResultData[url=" + url + ", name=" + name + "]";
	}
	
}