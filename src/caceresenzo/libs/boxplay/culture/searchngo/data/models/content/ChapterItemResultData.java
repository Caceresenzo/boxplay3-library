package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.IImageContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleUrlData;

/**
 * Holder class to contain an url and a name of a Chapter
 * 
 * @author Enzo CACERES
 */
public class ChapterItemResultData extends SimpleUrlData implements DisplayableString, ViewableContent {
	
	private final IImageContentProvider imageContentProvider;
	private final String name, title;
	private final ChapterType chapterType;
	
	/**
	 * Constructor, create a new instance with an url and a name only, title will be considered as null
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param imageContentProvider
	 *            Parent provider used to call this constructor
	 * @param url
	 *            Target chapter url
	 * @param name
	 *            Target chapter name
	 * @param chapterType
	 *            Change chapter type, if null, default is {@link ChapterType#IMAGE_ARRAY}
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name, ChapterType chapterType) {
		this(imageContentProvider, url, name, null, chapterType);
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
	 *            Target chapter name
	 * @param title
	 *            Target chapter title
	 * @param chapterType
	 *            Change chapter type, if null, default is {@link ChapterType#IMAGE_ARRAY}
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name, String title, ChapterType chapterType) {
		super(url);
		this.imageContentProvider = imageContentProvider;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
		this.title = title != null ? AdditionalResultData.escapeHtmlChar(title.trim()) : title;
		this.chapterType = chapterType == null ? ChapterType.IMAGE_ARRAY : chapterType;
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
	
	/*
	 * Get the {@link ChapterType} of this item
	 * 
	 * @return Chapter type
	 */
	public ChapterType getChapterType() {
		return chapterType;
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
		return "ChapterItemResultData[url=" + url + ", name=" + name + ", type=" + chapterType + "]";
	}
	
	/**
	 * Type of an {@link ChapterItemResultData}, its can target a image list or a plain text novel
	 * 
	 * @author Enzo CACERES
	 */
	public static enum ChapterType {
		IMAGE_ARRAY, TEXT;
	}
	
}