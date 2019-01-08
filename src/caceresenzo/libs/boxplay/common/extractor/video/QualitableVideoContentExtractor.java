package caceresenzo.libs.boxplay.common.extractor.video;

import java.util.ArrayList;
import java.util.List;

import caceresenzo.libs.boxplay.common.extractor.video.base.BaseVideoContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.model.VideoQuality;

/**
 * Abstract class to allow video extraction with site that provider multiple qualities.
 * 
 * @author Enzo CACERES
 */
public abstract class QualitableVideoContentExtractor extends BaseVideoContentExtractor {
	
	/**
	 * Same as {@link #extractVideoQualities(String, VideoContentExtractorProgressCallback)} but with a callback considered as null.
	 * 
	 * @param url
	 *            Target video site url.
	 * @return A list of found video qualities.
	 */
	public List<VideoQuality> extractVideoQualities(String url) {
		return extractVideoQualities(url, null);
	}
	
	/**
	 * Abstract main function to extract a direct video link from a target site.
	 * 
	 * @param url
	 *            Target video site url.
	 * @param progressCallback
	 *            A callback to follow extraction step.
	 * @return A list of found video qualities.
	 */
	public abstract List<VideoQuality> extractVideoQualities(String url, VideoContentExtractorProgressCallback progressCallback);
	
	/**
	 * @return A new {@link ArrayList} empty instance.
	 */
	protected List<VideoQuality> createEmptyVideoQualityList() {
		return new ArrayList<>();
	}
	
}