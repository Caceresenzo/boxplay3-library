package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;

/**
 * Manga Chapter extractor for the Japscan site.
 * 
 * @author Enzo CACERES
 */
public class GenericJapScanChapterExtractor extends MangaChapterContentExtractor {
	
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		return urls;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(japscan\\.cc).*?");
	}
	
}