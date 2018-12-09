package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaNeloChapterExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData.ChapterType;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

public class MangaNeloSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	/* Constants */
	public static final String SEARCH_JSON_KEY_NAME = "name";
	public static final String SEARCH_JSON_KEY_SUB_URL = "nameunsigned";
	public static final String SEARCH_JSON_KEY_IMAGE_URL = "image";
	public static final String SEARCH_JSON_KEY_LAST_CHAPTER = "lastchapter";
	
	/* Variables */
	private final String searchApiUrl;
	
	public MangaNeloSearchAndGoMangaProvider() {
		super("Manga Nelo", "https://manganelo.com");
		
		this.searchApiUrl = getSiteUrl() + "/home_json_search";
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MANGA });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String jsonString = Webb.create().post(searchApiUrl) //
				.header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT) //
				.param("searchword", searchQuery.replace(" ", "_")) //
				.param("search_style", "tentruyen") //
				.asString().getBody();
		
		Logger.info(jsonString);
		
		if (!StringUtils.validate(jsonString)) {
			return result;
		}
		
		JsonArray apiResultJsonArray;
		try {
			apiResultJsonArray = (JsonArray) new JsonParser().parse(jsonString);
		} catch (Exception exception) {
			return result;
		}
		
		for (Object item : apiResultJsonArray) {
			JsonObject map = (JsonObject) item;
			
			String name = map.getString(SEARCH_JSON_KEY_NAME).replaceAll(HtmlCommonExtractor.createTagReplacer("span"), "");
			String url = getSiteUrl() + "/manga/" + map.getString(SEARCH_JSON_KEY_SUB_URL);
			String imageUrl = map.getString(SEARCH_JSON_KEY_IMAGE_URL);
			String description = map.getString(SEARCH_JSON_KEY_LAST_CHAPTER);
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, name, url, imageUrl, SearchCapability.MANGA).score(score).describe(description));
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = getHelper().extract("\\<div\\sid\\=\\\"chapter\\\"\\sclass\\=\\\"chapter\\\"\\>[\\s]*\\<div\\sclass\\=\\\"manga-info-chapter\\\"\\>[\\s]*\\<div\\sclass\\=\\\"row\\stitle-list-chapter\\\"\\>(.*?\\<\\/div\\>)[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>", html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		Matcher chapterMatcher = getHelper().regex("\\<div\\sclass\\=\\\"row\\\"\\>[\\s]*\\<span\\>\\<a\\shref\\=\\\"(.*?)\\\".*?\\>(.*?)\\<\\/a\\>\\<\\/span\\>[\\s]*\\<span\\>(.*?)\\<\\/span\\>[\\s]*\\<span\\>(.*?)\\<\\/span\\>[\\s]*\\<\\/div\\>", htmlContainer);
		while (chapterMatcher.find()) {
			String url = chapterMatcher.group(1);
			String chapter = chapterMatcher.group(2);
			// String views = chapterMatcher.group(3);
			// String publishDate = chapterMatcher.group(4);
			
			additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, new ChapterItemResultData(this, url, null, chapter, ChapterType.IMAGE_ARRAY)));
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { GenericMangaNeloChapterExtractor.class };
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
}
