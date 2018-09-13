package caceresenzo.libs.boxplay.common.extractor.text.novel;

import caceresenzo.libs.boxplay.common.extractor.text.TextContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;

public abstract class NovelChapterContentExtractor extends TextContentExtractor {
	
	/**
	 * Abstract function to extends, extract a novel chapter content by its item
	 * 
	 * @param chapterItem
	 *            Target charter item to extract
	 * @return Extracted novel, get the format by {@link #getSupposedExtractedTextFormat()}
	 */
	public abstract String extractNovel(ChapterItemResultData chapterItem);
	
}