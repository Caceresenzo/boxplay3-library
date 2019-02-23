package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleUrlData;

/**
 * Holder class to contain an url and a name of a Video
 * 
 * @author Enzo CACERES
 */
public class VideoItemResultData extends SimpleUrlData implements DisplayableString, ViewableContent {
	
	/* Variables */
	private final IVideoContentProvider videoContentProvider;
	protected final String name;
	private String thumbnailImageUrl, videoDuration;
	
	/**
	 * Constructor, create a new instance with parent content provider, an url and a name.<br>
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
		super(url);
		this.videoContentProvider = videoContentProvider;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
	}
	
	/**
	 * Get the parent video content provider that has been used to generate this item.
	 * 
	 * @return Parent provider.
	 */
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
	
	/**
	 * Get video's name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get video's thumbnail image url (if any).
	 * 
	 * @return Image url for the thumbnail of this video.
	 */
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	
	/**
	 * @return Weather or not this video item has a non-<code>null</code> image url for his thumbnail.
	 */
	public boolean hasThumbnail() {
		return thumbnailImageUrl != null;
	}
	
	/**
	 * Get video's duration (if any).
	 * 
	 * @return Unparsed video duration for this video.
	 */
	public String getVideoDuration() {
		return videoDuration;
	}
	
	/**
	 * @return Weather or not this video item has a non-<code>null</code> video duration.
	 */
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
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "VideoItemResultData[url=" + url + ", name=" + name + "]";
	}
	
}