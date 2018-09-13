package caceresenzo.libs.boxplay.common.extractor.text.novel.implementations;

import caceresenzo.libs.boxplay.common.extractor.text.novel.NovelChapterContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;

public class GenericScanMangaNovelChapterExtractor extends NovelChapterContentExtractor {
	
	@Override
	public String extractNovel(ChapterItemResultData chapterItem) {
		String html = getStaticHelper().downloadPage(chapterItem.getUrl());
		
		if (html == null) {
			return null;
		}
		
		String extractedNovelHtml = getStaticHelper().extract("\\<article.*?\\sclass=[\\'\\\"]{1}aLN[\\'\\\"]{1}\\>(.*?)\\<\\/article\\>", html);
		if (extractedNovelHtml != null) {
			return extractedNovelHtml;
		}
		
		return null;
	}
	
	@Override
	public TextFormat getSupposedExtractedTextFormat() {
		return TextFormat.HTML;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(scan-manga\\.com).*?");
	}
	
}