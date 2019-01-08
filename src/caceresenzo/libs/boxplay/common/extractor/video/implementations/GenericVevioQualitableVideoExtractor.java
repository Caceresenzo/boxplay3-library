package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.common.extractor.video.QualitableVideoContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.model.VideoQuality;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

/**
 * Video quality extractor for the site VEVio.
 * 
 * @author Enzo CACERES
 */
public class GenericVevioQualitableVideoExtractor extends QualitableVideoContentExtractor {
	
	/* Constants */
	public static final String API_URL_FORMAT = "https://vev.io/api/serve/video/%s";
	
	/* Json Keys */
	public static final String JSON_KEY_QUALITIES = "qualities";
	
	@Override
	public List<VideoQuality>  extractVideoQualities(String url, VideoContentExtractorProgressCallback progressCallback) {
		List<VideoQuality> qualities = createEmptyVideoQualityList();
		
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String videoCode = getStaticHelper().extract("^.*?\\/([^\\/]+?)$", url);
		String fetchUrl = String.format(API_URL_FORMAT, videoCode);
		
		getLogger().appendln("Video code: " + videoCode);
		
		JsonObject json;
		try {
			getLogger().appendln("Downloading target page: " + fetchUrl);
			String html = Webb.create(true).post(fetchUrl) //
					.header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT) //
					.ensureSuccess() //
					.asString().getBody();
			
			if (!StringUtils.validate(html)) {
				throw new NullPointerException("Download string is null.");
			}
			
			json = (JsonObject) new JsonParser().parse(html);
			
			getLogger().appendln("-- Finished > size=" + html.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		if (progressCallback != null) {
			progressCallback.onExtractingLink();
		}
		
		if (!json.containsKey(JSON_KEY_QUALITIES)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: No qualities found.");
			
			return null;
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> qualitiesMap = (Map<String, String>) json.get(JSON_KEY_QUALITIES);
		
		for (Entry<String, String> qualityEntry : qualitiesMap.entrySet()) {
			String resolution = qualityEntry.getKey();
			String videoUrl = qualityEntry.getValue();
			
			qualities.add(new VideoQuality(resolution, videoUrl));
		}
		
		return qualities;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(vev\\.io).*?");
	}
	
}