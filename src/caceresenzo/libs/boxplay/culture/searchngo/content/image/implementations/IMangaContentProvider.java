package caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations;

import caceresenzo.libs.boxplay.culture.searchngo.content.image.IImageContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;

public interface IMangaContentProvider extends IImageContentProvider {
	
	/**
	 * Extract a usable link for the extractor behind
	 * 
	 * @param videoItemResult
	 *            Target video result
	 * @return A direct page url for the extractor to work
	 */
	String extractMangaPageUrl(ChapterItemResultData chapterItemResult);
	
}