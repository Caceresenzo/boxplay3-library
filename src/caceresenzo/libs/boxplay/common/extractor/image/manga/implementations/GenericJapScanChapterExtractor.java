package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.requirements.implementations.CloudflareRequirement;
import caceresenzo.libs.string.StringUtils;

/**
 * Manga Chapter extractor for the Japscan site.
 * 
 * @author Enzo CACERES
 */
public class GenericJapScanChapterExtractor extends MangaChapterContentExtractor {
	
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		CloudflareRequirement cloudflareRequirement = new CloudflareRequirement();
		cloudflareRequirement.prepare(HtmlCommonExtractor.extractBaseFromUrl(chapterUrl)).executeOnlyIfNotUsable();
		
		if (!cloudflareRequirement.isUsable()) {
			return urls;
		}
		
		String html = getStaticHelper().downloadPage(chapterUrl, cloudflareRequirement.getCookiesAsHeaderMap(null), "UTF-8");
		String baseCdnUrl = extractBaseImagesDirectoryUrl(html);
		List<String> images = extractImagesFiles(html);
		
		if (!StringUtils.validate(html, baseCdnUrl) && !images.isEmpty()) {
			return urls;
		}
		
		for (String imageFile : images) {
			urls.add(baseCdnUrl + imageFile);
		}
		
		return urls;
	}
	
	/**
	 * Image files are in a CDN, but you will need to get the base path to get easy access to all files (that you will have to extract too).<br>
	 * Here is an exemple: <code>https://cdn.japscan.cc/lel/&lt;manga&gt;/&lt;some&gt;/&lt;file&gt;.jpg</code> *
	 * 
	 * @param html
	 *            Chapter's html page to extract from
	 * @return Extracted base image file url, null if not found
	 */
	public static String extractBaseImagesDirectoryUrl(String html) {
		if (!StringUtils.validate(html)) {
			return null;
		}
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sid\\=\\\"image\\\"\\sdata-src\\=\\\"(.*?)\\\".*?\\>[\\s]*\\<\\/div\\>", html);
		
		if (matcher.find()) {
			String actualImageUrl = matcher.group(1);
			
			String[] urlParts = actualImageUrl.split("\\/");
			
			if (urlParts.length == 1) {
				/* Seems like something is wrong... */
				return null;
			}
			
			StringBuilder urlBuilder = new StringBuilder();
			
			for (int i = 0; i < urlParts.length; i++) {
				String part = urlParts[i];
				
				if (i + 1 != urlParts.length) {
					urlBuilder.append(part).append("/");
				}
			}
			
			return urlBuilder.toString();
		}
		
		return null;
	}
	
	/**
	 * Extract all images for a chapter, but without base (cdn) url added to it.
	 * 
	 * @param html
	 *            Chapter's html page to extract from
	 * @return List of image file
	 */
	public static List<String> extractImagesFiles(String html) {
		List<String> images = new ArrayList<>();
		
		String htmlList = getStaticHelper().extract("\\<select\\sid\\=\\\"pages\\\"\\>(.*?)\\<\\/select\\>", html);
		
		if (!StringUtils.validate(html, htmlList)) {
			return images;
		}
		
		Matcher itemMatcher = getStaticHelper().regex("\\<option.*?data\\-img\\=\\\"(.*?)\".*?\\>.*?\\<\\/option\\>", htmlList);
		
		while (itemMatcher.find()) {
			String imageFile = itemMatcher.group(1);
			
			images.add(imageFile);
		}
		
		return images;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(japscan\\.to).*?");
	}
	
}