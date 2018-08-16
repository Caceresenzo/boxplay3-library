package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.IImageContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;

/**
 * Holder class to contain an url and a name of a Chapter
 * 
 * @author Enzo CACERES
 */
public class ChapterItemResultData extends SimpleData implements DisplayableString, ViewableContent {
	
	private final IImageContentProvider imageContentProvider;
	private final String url, name, title;
	
	/**
	 * Constructor, create a new instance with an url and a name only, title will be considered as null
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param imageContentProvider
	 *            Parent provider used to call this constructor
	 * @param url
	 *            Traget chapter url
	 * @param name
	 *            Traget chapter name
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name) {
		this(imageContentProvider, url, name, null);
	}
	
	/**
	 * Constructor, create a new instance with an url and a name
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param imageContentProvider
	 *            Parent provider used to call this constructor
	 * @param url
	 *            Target chapter url
	 * @param name
	 *            Traget chapter name
	 * @param title
	 *            Traget chapter title
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name, String title) {
		this.imageContentProvider = imageContentProvider;
		this.url = url;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
		this.title = title != null ? AdditionalResultData.escapeHtmlChar(title.trim()) : title;
	}
	
	/**
	 * Get the parent image content provider that has been used to generate this item
	 * 
	 * @return Parent provider
	 */
	public IImageContentProvider getImageContentProvider() {
		return imageContentProvider;
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