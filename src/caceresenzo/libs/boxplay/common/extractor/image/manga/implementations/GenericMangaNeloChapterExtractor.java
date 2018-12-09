package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

/**
 * Manga Chapter extractor for mangakakalot.com
 * 
 * @author Enzo CACERES
 */
public class GenericMangaNeloChapterExtractor extends MangaChapterContentExtractor {
	
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		String html = getStaticHelper().downloadPage(chapterUrl);
		String htmlContainer = getStaticHelper().extract("\\<div\\sclass\\=\\\"vung-doc\\\"\\sid\\=\\\"vungdoc\\\"\\>(.*?)\\<\\/div\\>[\\s]*<\\/div\\>", html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return urls;
		}
		
		Matcher imageUrlMatcher = getStaticHelper().regex("\\<img\\ssrc\\=\\\"(.*?)\\\".*?alt\\=\\\".*?\\\".*?title\\=\\\".*?\\\".*?\\/\\>", htmlContainer);
		while (imageUrlMatcher.find()) {
			String imageUrl = imageUrlMatcher.group(1);
			
			urls.add(imageUrl);
		}
		
		return urls;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(manganelo\\.com).*?");
	}
	
}