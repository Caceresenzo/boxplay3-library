package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleUrlData;
import caceresenzo.libs.json.JsonObject;

/**
 * Holder class to contain an url and a name of a video.
 * 
 * @author Enzo CACERES
 */
public class VideoItemResultData extends SimpleUrlData implements DisplayableString, ViewableContent {
	
	/* Json Key */
	public static final String JSON_KEY_CONTENT_PROVIDER_CLASS = "content_provider_class";
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_THUMBNAIL_IMAGE_URL = "thumbnail";
	public static final String JSON_KEY_RAW_VIDEO_DURATION = "duration";
	
	/* Constants */
	public static final String KIND = "item_video";
	
	/* Variables */
	private final IVideoContentProvider videoContentProvider;
	protected final String name;
	private String thumbnailImageUrl, videoDuration;
	
	/**
	 * Create a new instance with parent content provider, an url and a name.<br>
	 * These value will be {@link String#trim()}.
	 * 
	 * @param videoContentProvider
	 *            Parent provider used to call this constructor.
	 * @param url
	 *            Target video url.
	 * @param name
	 *            Traget video name.
	 */
	public VideoItemResultData(IVideoContentProvider videoContentProvider, String url, String name) {
		super(KIND, url);
		this.videoContentProvider = videoContentProvider;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
	}
	
	/** @return Parent provider that has been used to generate this item. */
	public IVideoContentProvider getVideoContentProvider() {
		return videoContentProvider;
	}
	
	/**
	 * Set a thumbnail for this video.
	 * 
	 * @param imageUrl
	 *            Target image url of the thumbnail.
	 * @return Itself.
	 */
	public VideoItemResultData thumbnail(String imageUrl) {
		this.thumbnailImageUrl = imageUrl;
		
		return this;
	}
	
	/**
	 * Set a (unparsed) duration for this video.
	 * 
	 * @param duration
	 *            Target video duration.
	 * @return Itself.
	 */
	public VideoItemResultData duration(String duration) {
		this.videoDuration = duration;
		
		return this;
	}
	
	/** @return Video's name. */
	public String getName() {
		return name;
	}
	
	/** @return Image url for the thumbnail of this video (or <code>null</code> if no one has been provided). */
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	
	/** @return Weather or not this video item has a non-<code>null</code> image url for his thumbnail. */
	public boolean hasThumbnail() {
		return thumbnailImageUrl != null;
	}
	
	/** @return Unparsed video's duration (or <code>null</code> if no one has been provided). */
	public String getVideoDuration() {
		return videoDuration;
	}
	
	/** @return Weather or not this video item has a non-<code>null</code> video duration. */
	public boolean hasDuration() {
		return videoDuration != null;
	}
	
	@Override
	public String convertToDisplayableString() {
		return getName();
	}
	
	@Override
	public ContentViewerType getContentViewerType() {
		return ContentViewerType.VIDEO;
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();
		
		jsonObject.put(JSON_KEY_CONTENT_PROVIDER_CLASS, videoContentProvider.getClass().getSimpleName());
		jsonObject.put(JSON_KEY_NAME, name);
		jsonObject.put(JSON_KEY_THUMBNAIL_IMAGE_URL, thumbnailImageUrl);
		jsonObject.put(JSON_KEY_RAW_VIDEO_DURATION, videoDuration);
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return "VideoItemResultData[url=" + url + ", name=" + name + "]";
	}
	
}