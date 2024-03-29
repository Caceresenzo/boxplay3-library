package caceresenzo.libs.boxplay.common.extractor.video;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.base.BaseVideoContentExtractor;

/**
 * Abstract class that extends {@link ContentExtractor} for more compatibility for video extraction
 * 
 * @author Enzo CACERES
 */
public abstract class VideoContentExtractor extends BaseVideoContentExtractor {
	
	/**
	 * Same as {@link #extractDirectVideoUrl(String, VideoContentExtractorProgressCallback)} but with a callback considered as null
	 * 
	 * @param url
	 *            Target video site url
	 * @return A direct video url
	 */
	public String extractDirectVideoUrl(String url) {
		return extractDirectVideoUrl(url, null);
	}
	
	/**
	 * Abstract main function to extract a direct video link from a target site
	 * 
	 * NEED A SEPARATED THREAD, this code lock itself because it needs to, so please execute it in another thread
	 * 
	 * @param url
	 *            Target video site url
	 * @param progressCallback
	 *            A callback to follow extraction step
	 * @return A direct video url
	 */
	public abstract String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback);
	
}