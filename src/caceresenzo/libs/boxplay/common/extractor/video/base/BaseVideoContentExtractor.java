package caceresenzo.libs.boxplay.common.extractor.video.base;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;

public abstract class BaseVideoContentExtractor extends ContentExtractor {

	
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
		void onFileNotAvailable();
		
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