package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.network.Downloader;
import caceresenzo.libs.string.StringUtils;

public class GenericAnimeUltimateVideoExtractor extends VideoContentExtractor {
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String htmlPage;
		try {
			getLogger().appendln("Downloading target page: " + url);
			htmlPage = Downloader.webget(url);
			
			if (!StringUtils.validate(htmlPage)) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + url.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		String videoSource = getStaticHelper().extract("\\<meta\\sitemprop\\=\\\"contentURL\\\"\\scontent\\=\\\"(.*?)\\\"[\\s]*\\/\\>", htmlPage);
		
		if (videoSource == null) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: File not availabe, or no player is present on the page.");
			return null;
		}
		
		getLogger().appendln("Extracted source from page: " + videoSource);
		
		return videoSource;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(anime-ultime\\.net).*?");
	}
	
}