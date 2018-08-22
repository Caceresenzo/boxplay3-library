package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.network.Downloader;

public class GenericVidozaVideoExtractor extends VideoContentExtractor {
	
	public static final String ERROR_FILE_DELETED = "File was deleted";
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String vidozaHtml;
		try {
			getLogger().appendln("Downloading target page: " + url);
			vidozaHtml = Downloader.webget(url);
			
			if (vidozaHtml == null || vidozaHtml.isEmpty()) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + url.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		if (vidozaHtml.equals(ERROR_FILE_DELETED)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: " + ERROR_FILE_DELETED);
			
			return null;
		} else {
			String videoSource = getStaticHelper().extract("sourcesCode\\:\\s\\[\\{\\ssrc\\:\\s\\\"(.*?)\\\"\\,", vidozaHtml);
			
			getLogger().appendln("Extracted source from page: " + videoSource);
			
			return videoSource;
		}
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(vidoza\\.net).*?");
	}
	
}