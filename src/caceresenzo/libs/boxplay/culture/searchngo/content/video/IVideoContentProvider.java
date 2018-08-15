package caceresenzo.libs.boxplay.culture.searchngo.content.video;

import caceresenzo.libs.boxplay.culture.searchngo.content.IContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;

public interface IVideoContentProvider extends IContentProvider {
	
	/**
	 * Extract a usable link for the extractor behind
	 * 
	 * @param videoItemResult
	 *            Target video result
	 * @return A direct page url for the extractor to work
	 */
	String extractVideoPageUrl(VideoItemResultData videoItemResult);
	
}