package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;

public class GenericMangaLelChapterExtractor extends MangaChapterContentExtractor {
	
	public static final String FORMAT_URL_IMAGE = "https://www.manga-lel.com//uploads/manga/%s/chapters/%s/%s";

	public static final String JSON_KEY_ITEM_EXTERNAL = "external";
	public static final String JSON_KEY_ITEM_PAGE_SLUG = "page_slug";
	public static final String JSON_KEY_ITEM_PAGE_IMAGE = "page_image";

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		String html = getStaticHelper().downloadPage(chapterUrl);
		
		if (html == null) {
			return urls;
		}
		
		String[] urlSplit = chapterUrl.split("\\/");
		String manga = urlSplit[urlSplit.length - 2];
		String chapter = urlSplit[urlSplit.length - 1];
		
		JsonArray jsonArray; // Don't even need to sort
		try {
			jsonArray = (JsonArray) new JsonParser().parse(extractJsonImageData(html));
		} catch (JsonException exception) {
			return urls;
		}
		
		for (Object arrayItem : jsonArray) {
			Map<String, Object> itemMap = (Map<String, Object>) arrayItem;
			
			String pageImage = (String) itemMap.get(JSON_KEY_ITEM_PAGE_IMAGE);
			
			if (pageImage != null) {
				urls.add(String.format(FORMAT_URL_IMAGE, manga, chapter, pageImage));
			}
		}
		
		return urls;
	}
	
	/**
	 * Extract a json container containing all image informations
	 * 
	 * @param html
	 *            Source html
	 * @return A json string
	 */
	public static String extractJsonImageData(String html) {
		return getStaticHelper().extract("var pages[\\s]*=[\\s]*(.*?)[\\s]*;", html);
	}
	
}