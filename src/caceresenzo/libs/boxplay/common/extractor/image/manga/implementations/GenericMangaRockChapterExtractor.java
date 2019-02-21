package caceresenzo.libs.boxplay.common.extractor.image.manga.implementations;

import java.util.List;

import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.MangaRockSearchAndGoMangaProvider;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;

/**
 * Manga Chapter image link getter for the Manga Rock API Endpoint.
 * 
 * @author Enzo CACERES
 */
public class GenericMangaRockChapterExtractor extends MangaChapterContentExtractor {
	
	/* Constants */
	public static final String MRI_IMAGE_DECODER_URL_FORMAT = "https://mri-image-decoder.now.sh/?url=%s";
	
	@Override
	public List<String> getImageUrls(String chapterUrl) {
		List<String> urls = createEmptyUrlList();
		
		JsonArray jsonArray;
		try {
			JsonObject responseJsonObject = (JsonObject) new JsonParser().parse(getStaticHelper().downloadPage(chapterUrl));
			
			MangaRockSearchAndGoMangaProvider.ensureRequestSuccess(responseJsonObject);
			
			jsonArray = responseJsonObject.getJsonArray("data");
		} catch (Exception exception) {
			return urls;
		}
		
		for (Object object : jsonArray) {
			String mriFileUrl = (String) object;
			
			urls.add(String.format(MRI_IMAGE_DECODER_URL_FORMAT, mriFileUrl));
		}
		
		return urls;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(api\\.mangarockhd\\.com).*?");
	}
	
}