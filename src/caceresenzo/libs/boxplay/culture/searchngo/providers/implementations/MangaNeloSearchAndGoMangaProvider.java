package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaNeloChapterExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.RatingResultData;
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
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.StringUtils;

public class MangaNeloSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	/* Constants */
	public static final String SEARCH_JSON_KEY_NAME = "name";
	public static final String SEARCH_JSON_KEY_SUB_URL = "nameunsigned";
	public static final String SEARCH_JSON_KEY_IMAGE_URL = "image";
	public static final String SEARCH_JSON_KEY_LAST_CHAPTER = "lastchapter";
	
	/* Static */
	public static final Map<String, AdditionalDataType> COMMON_DATA_CORRESPONDANCES = new HashMap<>();
	
	static {
		COMMON_DATA_CORRESPONDANCES.put("Alternative", AdditionalDataType.ALTERNATIVE_NAME);
		COMMON_DATA_CORRESPONDANCES.put("Author(s)", AdditionalDataType.AUTHORS);
		COMMON_DATA_CORRESPONDANCES.put("Status", AdditionalDataType.STATUS);
		COMMON_DATA_CORRESPONDANCES.put("Last updated", AdditionalDataType.LAST_UPDATED);
		COMMON_DATA_CORRESPONDANCES.put("TransGroup", AdditionalDataType.TRADUCTION_TEAM);
		COMMON_DATA_CORRESPONDANCES.put("View", AdditionalDataType.VIEWS);
		COMMON_DATA_CORRESPONDANCES.put("Genres", AdditionalDataType.GENDERS);
		COMMON_DATA_CORRESPONDANCES.put("Rating", AdditionalDataType.RATING);
	}
	
	/* Variables */
	private final String searchApiUrl;
	
	/* Constructor */
	public MangaNeloSearchAndGoMangaProvider() {
		super("MangaNelo", "https://manganelo.com");
		
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
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = getHelper().extract("\\<ul\\sclass\\=\\\"manga-info-text\\\"\\>(.*?)\\<div\\sstyle\\=\\\"clear\\:[\\s]*both\\;\\\"\\>\\<\\/div\\>", html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		/* Common */
		Matcher additionalItemsMatcher = getHelper().regex("\\<li.*?\\>(.*?)[\\s]*\\:[\\s]*(.*?)[\\s]*\\<\\/li\\>", htmlContainer);
		while (additionalItemsMatcher.find()) {
			String type = additionalItemsMatcher.group(1);
			AdditionalDataType correspondingType = COMMON_DATA_CORRESPONDANCES.get(type);
			String rawContent = additionalItemsMatcher.group(2);
			
			if (!StringUtils.validate(type, rawContent)) {
				continue;
			}
			
			if (correspondingType == null) {
				for (Entry<String, AdditionalDataType> entry : COMMON_DATA_CORRESPONDANCES.entrySet()) {
					String suffix = entry.getKey();
					AdditionalDataType dataType = entry.getValue();
					
					if (type.endsWith(suffix)) {
						correspondingType = dataType;
						
						break;
					}
				}
				
				if (correspondingType == null) {
					/* Unknown */
					continue;
				}
			}
			
			Object processedContent = null;
			switch (correspondingType) {
				case ALTERNATIVE_NAME: {
					processedContent = rawContent.replace("</span>", "");
					break;
				}
				
				case LAST_UPDATED:
				case VIEWS:
				case STATUS: {
					processedContent = rawContent.trim();
					break;
				}
				
				case AUTHORS: {
					List<String> categories = new ArrayList<>();
					
					Matcher authorsMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, rawContent);
					while (authorsMatcher.find()) {
						String name = authorsMatcher.group(2);
						
						categories.add(name);
					}
					
					if (!categories.isEmpty()) {
						processedContent = categories;
					}
					break;
				}
				
				case GENDERS: {
					List<CategoryResultData> categories = new ArrayList<>();
					
					Matcher gendersMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, rawContent);
					while (gendersMatcher.find()) {
						String url = gendersMatcher.group(1);
						String name = gendersMatcher.group(2);
						
						categories.add(new CategoryResultData(url, name));
					}
					
					if (!categories.isEmpty()) {
						processedContent = categories;
					}
					break;
				}
				
				case RATING: {
					/* Go to the next one */
					if (additionalItemsMatcher.find()) {
						String htmlRawContent = additionalItemsMatcher.group(2);
						
						final String metaRegexFormat = "\\<em\\sproperty\\=\\\"v:%s\\\"\\>(.*?)\\<\\/em\\>";
						
						float average = ParseUtils.parseFloat(getHelper().extract(String.format(metaRegexFormat, "average"), htmlRawContent), NO_VALUE);
						int best = ParseUtils.parseInt(getHelper().extract(String.format(metaRegexFormat, "best"), htmlRawContent), NO_VALUE);
						int votes = ParseUtils.parseInt(getHelper().extract(String.format(metaRegexFormat, "votes"), htmlRawContent), NO_VALUE);
						
						if (average != NO_VALUE && best != NO_VALUE && votes != NO_VALUE) {
							processedContent = new RatingResultData(average, best, votes);
						}
					}
					
					break;
				}
				
				default: {
					continue;
				}
			}
			
			if (processedContent != null) {
				additionals.add(new AdditionalResultData(correspondingType, processedContent));
			}
		}
		
		/* Resume */
		String extractedResume = getHelper().extract("\\<div\\sid\\=\\\"noidungm\\\"\\sstyle\\=\\\".*?\\>[\\s]*\\<h2\\>\\<p.*?\\<\\/p\\>\\<\\/h2\\>(.*?)\\<\\/div\\>", htmlContainer);
		if (extractedResume != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume));
		}
		
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