package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;

public class GenericMangaScanChapterExtractor extends MangaChapterContentExtractor {

	@Override
	public List<String> getImageUrls(String chapterUrl) {
		return createEmptyUrlList();
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(scan-manga\\.com).*?");
	}
	
}