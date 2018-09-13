package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

/**
 * Manga Chapter extractor for the site Scan-Manga
 * 
 * @author Enzo CACERES
 */
public class GenericScanMangaChapterExtractor extends MangaChapterContentExtractor {
	
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		String html = getStaticHelper().downloadPage(chapterUrl);
		
		if (html == null) {
			Logger.info("html is null");
			return urls;
		}
		
		String baseImageUrl = extractBaseUrlFormatter(html);
		String jsCode = extractImagesSubUrlsJsContainer(html);
		
		if (!StringUtils.validate(baseImageUrl, jsCode)) {
			Logger.info("string not valid: %s %s", StringUtils.validate(baseImageUrl), StringUtils.validate(jsCode));
			return urls;
		}
		
		Matcher imageSubUrlMatcher = getStaticHelper().regex("[\\w]{4,}\\[[\\d]*\\][\\s]*\\=[\\s]*\\\"(.*?)\\\"\\;", jsCode);
		while (imageSubUrlMatcher.find()) {
			String imageSubUrl = imageSubUrlMatcher.group(1);
			
			if (imageSubUrl != null) {
				urls.add(String.format(baseImageUrl, imageSubUrl));
				// urls.add(String.format("http://88.125.214.43/scan-manga-image.php?url=%s&referer=%s", URLEncoder.encode(String.format(baseImageUrl, imageSubUrl)), URLEncoder.encode(chapterUrl)));
			}
		}
		
		return urls;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(scan-manga\\.com).*?");
	}
	
	/**
	 * On Scan-Manga, all sub-urls to image are contains in a array written in js, this will extract the js code containing these informations
	 * 
	 * @param html
	 *            Manga html page
	 * @return A extracted string of js code, or null if not found
	 */
	public static String extractImagesSubUrlsJsContainer(String html) {
		return getStaticHelper().extract("new\\sArray\\;var\\scpl[\\s]*=[\\s]*new\\sArray\\;(.*?)\\;check[\\s]*=[\\s]*true\\;", html);
	}
	
	/**
	 * On Scan-Manga, the base url chnage between chapter and manga, no base is contant.<br>
	 * This will extract a base url that you will need to {@link String#format(String, Object...)} with the sub-image-url
	 * 
	 * @param html
	 *            Manga html page
	 * @return A extracted base url, reformatted, or null if not found
	 */
	public static String extractBaseUrlFormatter(String html) {
		try {
			int retry = 0;
			
			while (true) {
				String baseUrl = extractBaseUrlFormatter(html, retry++);
				
				if (baseUrl != null) {
					return baseUrl;
				}
			}
		} catch (IllegalStateException exception) {
			return null;
		}
	}
	
	private static String extractBaseUrlFormatter(String html, int retry) {
		switch (retry) {
			case 0: {
				Matcher baseUrlMatcher = getStaticHelper().regex("\\$\\([\\\"\\']{1}#preload[\\\"\\']{1}\\)\\.prop\\([\\\"\\']{1}src[\\\"\\']{1}\\,[\\\"\\']{1}(.*?)[\\\"\\']{1}\\+.*?\\[id_page\\]\\)\\;", html);
				
				/* First one */
				if (baseUrlMatcher.find()) {
					return (baseUrlMatcher.group(1) + "%s").trim();
				}
				
				break;
			}
			
			case 1: {
				String extractedUrl = getStaticHelper().extract("for\\s\\(var\\s.*?\\sin\\s.*?\\)\\s\\{.*?\\=\\s[\\'\\\"]{1}(.*?)[\\'\\\"]{1}.*?\\}", html);
				
				if (extractedUrl != null) {
					return (extractedUrl + "%s").trim();
				}
				
				break;
			}
			
			default: {
				throw new IllegalStateException("Max retry count exceed.");
			}
		}
		
		return null;
	}
	
}