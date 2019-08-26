package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.string.StringUtils;

public class GenericVeryStreamVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED_MESSAGE_SHORT = "File not found";

	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String baseUrl = getStaticHelper().extractBaseUrl(url);
		String baseUrlFormat;
		
		if (baseUrl != null) {
			// https://<domain>/stream/<video token>?mime=true
			baseUrlFormat = baseUrl + "/gettoken/%s?mime=true";
		} else {
			Exception exception = new Exception("Base url not found.");
			
			failed(true).notifyException(exception);
			getLogger().appendln("-- Failed: " + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().appendln("Parameters: ").appendln(" - BASE: " + baseUrlFormat).appendln();
		
		String html;
		try {
			getLogger().appendln("Downloading target page: " + url);
			html = Webb.create().get(url).chromeUserAgent().ensureSuccess().asString().getBody();
			
			if (!StringUtils.validate(html)) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + html.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		System.out.println(html);
		
		getLogger().separator();
		
		if (!checkStreamingAvailability(html)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: " + FILE_DELETED_MESSAGE_SHORT);
			
			return null;
		} else {
			String extractedKey = getStaticHelper().extract("\\<p style=\"\" class=\"\" id=\"videolink\">(.*?)\\<\\/p\\>", html);
			
			getLogger().appendln("Video key: " + extractedKey);
			
			if (!StringUtils.validate(extractedKey)) {
				return null;
			}
			
			if (progressCallback != null) {
				progressCallback.onFormattingResult();
			}
			
			return String.format(baseUrlFormat, extractedKey);
		}
	}
	
	/**
	 * Used to check if the target file to stream is available or not.
	 * 
	 * @param html
	 *            Source of the page.
	 * @return Weather or not the {@link #FILE_DELETED_MESSAGE_SHORT} message is not on the page.
	 */
	public boolean checkStreamingAvailability(String html) {
		if (html == null) {
			return false;
		}
		
		return !html.contains(FILE_DELETED_MESSAGE_SHORT);
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(verystream\\.com|woof\\.tube).*?");
	}
	
}