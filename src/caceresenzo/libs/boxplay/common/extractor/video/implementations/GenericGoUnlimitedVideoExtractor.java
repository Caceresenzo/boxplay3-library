package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

public class GenericGoUnlimitedVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED_MESSAGE = "File was deleted";
	
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
		
		try {
			String directUrl = new GoUnlimitedDirectUrlComputingSandbox().execute(html);
			
			getLogger().appendln("Direct URL: " + directUrl);
			
			if (progressCallback != null) {
				progressCallback.onFormattingResult();
			}
			
			return directUrl;
		} catch (Exception exception) {
			getLogger().appendln("Failed to extract url: " + exception.getLocalizedMessage() + " (" + exception.getClass().getSimpleName() + ")");
			getLogger().appendln(StringUtils.fromException(exception));
			
			return null;
		}
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(gounlimited\\.to).*?");
	}
	
	public static class GoUnlimitedDirectUrlComputingSandbox implements Sandbox<String, String> {
		
		@Override
		public String execute(String html) {
			String p = null;
			int a = 36, c = 88;
			String[] k = null;
			
			String extractedParameters = getStaticHelper().extract("eval\\(function\\(p\\,a\\,c\\,k\\,e\\,d\\)\\{.*?return\\sp.*?\\}\\((.*?\\))\\)\\)", html);
			
			if (!StringUtils.validate(extractedParameters)) {
				return null;
			}
			
			Matcher parameterMatcher = getStaticHelper().regex("((?<![\\\\])['\"])((?:.(?!(?<![\\\\])\\1))*.?)\\1", extractedParameters);
			if (parameterMatcher.find()) {
				p = parameterMatcher.group(2);
			}
			if (parameterMatcher.find()) {
				k = parameterMatcher.group(2).split("\\|");
			}
			
			while (c-- != 0) {
				if (c >= k.length)
					continue ;
				if (StringUtils.validate(k[c])) {
					p = p.replaceAll(String.format("\\b%s\\b", Integer.toString(c, a)), k[c]);
				}
			}
			
			return getStaticHelper().extract(".*?sources\\:\\[\\\"(.*?)\\\"\\]", p);
		}
		
	}
	
}