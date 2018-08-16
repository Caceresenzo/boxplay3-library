package caceresenzo.libs.boxplay.culture.searchngo.data.models.content;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.ViewableContent;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;

/**
 * Holder class to contain an url and a name of a Video
 * 
 * @author Enzo CACERES
 */
public class VideoItemResultData extends SimpleData implements DisplayableString, ViewableContent {
	
	private final IVideoContentProvider videoContentProvider;
	private final String url, name;
	
	/**
	 * Constructor, create a new instance with parent content provider, an url and a name
	 * 
	 * These value will be {@link String#trim()}
	 * 
	 * @param videoContentProvider
	 *            Parent provider used to call this constructor
	 * @param url
	 *            Target video url
	 * @param name
	 *            Traget video name
	 */
	public VideoItemResultData(IVideoContentProvider videoContentProvider, String url, String name) {
		this.videoContentProvider = videoContentProvider;
		this.url = url;
		this.name = name != null ? AdditionalResultData.escapeHtmlChar(name.trim()) : name;
	}
	
	/**
	 * Get the parent video content provider that has been used to generate this item
	 * 
	 * @return Parent provider
	 */
	public IVideoContentProvider getVideoContentProvider() {
		return videoContentProvider;
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