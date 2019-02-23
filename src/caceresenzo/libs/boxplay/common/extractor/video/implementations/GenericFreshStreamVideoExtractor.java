package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.List;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.common.extractor.video.VideoQualityContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.model.VideoQuality;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.network.Downloader;
import caceresenzo.libs.string.StringUtils;

public class GenericFreshStreamVideoExtractor extends VideoQualityContentExtractor {
	
	@Override
	public List<VideoQuality> extractVideoQualities(String url, VideoContentExtractorProgressCallback progressCallback) {
		List<VideoQuality> qualities = createEmptyVideoQualityList();
		
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String html;
		try {
			getLogger().appendln("Downloading target page: " + url);
			html = Downloader.webget(url);
			
			if (html == null || html.isEmpty()) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + html.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		String informationUrl = getStaticHelper().extract("var vsuri = '(.*?)';", html);
		
		if (StringUtils.validate(informationUrl)) {
			try {
				getLogger().appendln("Request API: " + informationUrl);
				JsonObject jsonObject = Webb.create().get(informationUrl).chromeUserAgent().asJsonObject().getBody();
				
				getLogger().appendln("Parsed JSON: " + jsonObject.toString());
				
				if (progressCallback != null) {
					progressCallback.onFormattingResult();
				}
				
				for (Entry<Object, Object> entry : jsonObject.entrySet()) {
					String resolution = (String) entry.getKey();
					String directVideoUrl = (String) entry.getValue();
					
					qualities.add(new VideoQuality(resolution, directVideoUrl));
				}
				
				return qualities;
			} catch (Exception exception) {
				failed(true).notifyException(exception);
				getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			}
		}
		
		if (progressCallback != null) {
			progressCallback.onFileNotAvailable();
		}
		
		return null;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(freshstream\\.kiwi).*?");
	}
	
}