package caceresenzo.libs.boxplay.common.extractor.image.manga;

import java.util.ArrayList;
import java.util.List;

public abstract class MangaChapterContentExtractor extends MangaContentExtractor {
	
	/**
	 * Get a list of sorted image url
	 * 
	 * @param chapterUrl
	 *            Target chapter page url
	 * @return A list of image url
	 */
	public abstract List<String> getImageUrls(String chapterUrl);
	
	/**
	 * Create an empty url list
	 * 
	 * @return A new list
	 */
	protected List<String> createEmptyUrlList() {
		return new ArrayList<>();
	}
	
}