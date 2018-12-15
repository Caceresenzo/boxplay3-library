package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

public class GenericVidloxVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED_MESSAGE = "Error. The video was deleted.";
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String html;
		try {
			getLogger().appendln("Downloading target page: " + url);
			html = Webb.create().get(url) //
					.header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT) //
					.ensureSuccess() //
					.asString().getBody();
			
			if (!StringUtils.validate(html)) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + html.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		if (html.contains(FILE_DELETED_MESSAGE)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: " + FILE_DELETED_MESSAGE);
			
			return null;
		}
		
		if (progressCallback != null) {
			progressCallback.onExtractingLink();
		}
		
		String allSources = getStaticHelper().extract("sources\\:\\s\\[(.*?)\\]", html);
		
		if (!StringUtils.validate(allSources)) {
			getLogger().appendln("No source found.");
			
			return null;
		}
		
		getLogger().appendln("Found sources: " + allSources);
		
		Matcher sourceMatcher = getStaticHelper().regex("\"(.*?)\"", allSources);
		while (sourceMatcher.find()) {
			String source = sourceMatcher.group(1);
			
			getLogger().append("Checking: " + source + " -- ");
			
			/* Match first, hight, compatible file */
			if (source.endsWith(".mp4")) {
				getLogger().appendln("COMPATIBLE, RETURNING");
				
				if (progressCallback != null) {
					progressCallback.onFormattingResult();
				}
				
				return source;
			} else {
				getLogger().appendln("NOT COMPATIBLE");
			}
		}
		
		return null;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(vidlox\\.tv|vidlox\\.me).*?");
	}
	
}