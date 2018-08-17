package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.network.Downloader;

public class GenericVidozaVideoExtractor extends VideoContentExtractor {

	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String vidozaHtml;
		try {
			vidozaHtml = Downloader.webget(url);
			
			if (vidozaHtml == null) {
				throw new NullPointerException("Download string is null.");
			}
		} catch (Exception exception) {
			failed(true).notifyException(exception);			
			return null;
		}
		
		return getStaticHelper().extract("sourcesCode\\:\\s\\[\\{\\ssrc\\:\\s\\\"(.*?)\\\"\\,", vidozaHtml);
	}
	
}
