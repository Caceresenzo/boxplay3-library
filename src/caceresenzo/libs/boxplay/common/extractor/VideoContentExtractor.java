package caceresenzo.libs.boxplay.common.extractor;

/**
 * Abstract class that extends {@link ContentExtractor} for more compatibility for video extraction
 * 
 * @author Enzo CACERES
 */
public abstract class VideoContentExtractor extends ContentExtractor {
	
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
	
	/**
	 * Basic interface to follow progress of the extractor
	 * 
	 * @author Enzo CACERES
	 */
	public static interface VideoContentExtractorProgressCallback {
		
		/**
		 * Called when a page is being downloading
		 * 
		 * @param targetUrl
		 *            Page url
		 */
		void onDownloadingUrl(String targetUrl);
		
		/**
		 * Called when stream is not available, maybe he got deleted
		 */
		void onStreamingNotAvailable();
		
		/**
		 * Called when link extraction is appening
		 */
		void onExtractingLink();
		
		/**
		 * Called when the finish link is formatting
		 */
		void onFormattingResult();
		
	}
	
	public static class StreamingNotAvailableException extends ExtractorRuntimeException {
		private static final long serialVersionUID = 1L;
		
	}
	
}