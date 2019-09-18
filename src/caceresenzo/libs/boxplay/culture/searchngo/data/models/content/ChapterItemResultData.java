package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.IImageContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleUrlData;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.string.StringUtils;

/**
 * Holder class to contain an url and a name of a chapter.
 * 
 * @author Enzo CACERES
 */
public class ChapterItemResultData extends SimpleUrlData implements DisplayableString, ViewableContent {
	
	/* Json Key */
	public static final String JSON_KEY_CONTENT_PROVIDER_CLASS = "content_provider_class";
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_DISPLAYABLE = "displayable";
	public static final String JSON_KEY_TYPE = "type";
	
	/* Constants */
	public static final String KIND = "item_chapter";
	
	/* Variables */
	private final IImageContentProvider imageContentProvider;
	private final String name, title;
	private final ChapterType chapterType;
	
	/**
	 * Create a new instance with an url and a name only, title will be considered as null.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param imageContentProvider
	 *            Parent provider used to call this constructor.
	 * @param url
	 *            Target chapter url.
	 * @param name
	 *            Target chapter name.
	 * @param chapterType
	 *            Change chapter type, if null, default is {@link ChapterType#IMAGE_ARRAY}.
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name, ChapterType chapterType) {
		this(imageContentProvider, url, name, null, chapterType);
	}
	
	/**
	 * Create a new instance with an url and a name.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param imageContentProvider
	 *            Parent provider used to call this constructor.
	 * @param url
	 *            Target chapter url.
	 * @param name
	 *            Target chapter name.
	 * @param title
	 *            Target chapter title.
	 * @param chapterType
	 *            Change chapter type, if null, default is {@link ChapterType#IMAGE_ARRAY}.
	 */
	public ChapterItemResultData(IImageContentProvider imageContentProvider, String url, String name, String title, ChapterType chapterType) {
		super(url);
		this.imageContentProvider = imageContentProvider;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
		this.title = title != null ? AdditionalResultData.escapeHtmlChar(title.trim()) : title;
		this.chapterType = chapterType == null ? ChapterType.IMAGE_ARRAY : chapterType;
	}
	
	/** @return Parent provider that has been used to generate this item. */
	public IImageContentProvider getImageContentProvider() {
		return imageContentProvider;
	}
	
	/** @return Chapter's name. */
	public String getName() {
		return name;
	}
	
	/** @return Chapter's title. */
	public String getTitle() {
		return title;
	}
	
	/** @return Chapter's {@link ChapterType type}. */
	public ChapterType getChapterType() {
		return chapterType;
	}
	
	@Override
	public String convertToDisplayableString() {
		if (StringUtils.validate(title, name)) {
			return (title.trim() + " - " + name.trim()).trim();
		} else if (StringUtils.validate(title)) {
			return title.trim();
		} else if (StringUtils.validate(name)) {
			return name.trim();
		}
		
		return null;
	}
	
	@Override
	public ContentViewerType getContentViewerType() {
		return ContentViewerType.IMAGE;
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();
		
		jsonObject.put(JSON_KEY_CONTENT_PROVIDER_CLASS, imageContentProvider.getClass().getSimpleName());
		jsonObject.put(JSON_KEY_NAME, name);
		jsonObject.put(JSON_KEY_TITLE, title);
		jsonObject.put(JSON_KEY_DISPLAYABLE, convertToDisplayableString());
		jsonObject.put(JSON_KEY_TYPE, chapterType.toString());
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return "ChapterItemResultData[url=" + url + ", name=" + name + ", type=" + chapterType + "]";
	}
	
	/**
	 * Type of an {@link ChapterItemResultData}, its can target a image list or a plain text novel.
	 * 
	 * @author Enzo CACERES
	 */
	public static enum ChapterType {
		IMAGE_ARRAY, TEXT;
	}
	
}