package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericJapScanChapterExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.requirements.implementations.CloudflareRequirement;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

public class JapScanSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	/* Constants */
	public static final String SEARCH_JSON_KEY_NAME = "name";
	public static final String SEARCH_JSON_KEY_IMAGE_URL = "image";
	public static final String SEARCH_JSON_KEY_URL = "url";
	
	/* Static */
	public static final Map<String, AdditionalDataType> COMMON_DATA_CORRESPONDANCES = new HashMap<>();
	
	static {
		COMMON_DATA_CORRESPONDANCES.put("Nom(s) Alternatif(s):", AdditionalDataType.ALTERNATIVE_NAME);
		// COMMON_DATA_CORRESPONDANCES.put("Origine:", AdditionalDataType.NULL);
		COMMON_DATA_CORRESPONDANCES.put("Statut:", AdditionalDataType.STATUS);
		COMMON_DATA_CORRESPONDANCES.put("Date Sortie:", AdditionalDataType.RELEASE_DATE);
		COMMON_DATA_CORRESPONDANCES.put("Type(s):", AdditionalDataType.TYPE);
		COMMON_DATA_CORRESPONDANCES.put("Artiste(s):", AdditionalDataType.ARTISTS);
		COMMON_DATA_CORRESPONDANCES.put("Auteur(s):", AdditionalDataType.AUTHORS);
		// COMMON_DATA_CORRESPONDANCES.put("Adaptation En Anim�:", AdditionalDataType.NULL);
		// COMMON_DATA_CORRESPONDANCES.put("Abonnement RSS:", AdditionalDataType.NULL);
	}
	
	/* Variables */
	private final String searchApiUrl;
	
	/* Constructor */
	public JapScanSearchAndGoMangaProvider() {
		super("Japscan", "https://www.japscan.to");
		
		this.searchApiUrl = getSiteUrl() + "/search/";
		
		require(CloudflareRequirement.class);
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MANGA });
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		CloudflareRequirement cloudflareRequirement = getRequirement(CloudflareRequirement.class);
		cloudflareRequirement.prepare(getSiteUrl()).executeOnlyIfNotUsable();
		
		String json = Webb.create().post(searchApiUrl) //
				.header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT) //
				.header("cookie", cloudflareRequirement.getCookiesAsString()) //
				.param("search", searchQuery) //
				.asString().getBody();
		
		if (!StringUtils.validate(json)) {
			return result;
		}
		
		List<JapscanItem> items = getMangaFromJson(json);
		
		for (JapscanItem item : items) {
			String name = item.getName();
			String url = getSiteUrl() + item.getUrl();
			String imageUrl = getSiteUrl() + item.getImageUrl();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, name, url, imageUrl, SearchCapability.MANGA).score(score).requireHeaders(cloudflareRequirement.getCookiesAsHeaderMap(null)));
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		CloudflareRequirement cloudflareRequirement = getRequirement(CloudflareRequirement.class);
		cloudflareRequirement.prepare(getSiteUrl()).executeOnlyIfNotUsable();
		
		if (!cloudflareRequirement.isUsable()) {
			return additionals;
		}
		
		String html = getHelper().downloadPageCache(result.getUrl(), cloudflareRequirement.getCookiesAsHeaderMap(null));
		
		if (!StringUtils.validate(html)) {
			return additionals;
		}
		
		/* Common */
		Matcher commonDataMatcher = getHelper().regex("\\<p\\sclass\\=\\\"mb-2\\\"\\>[\\s]*\\<span\\sclass\\=\\\"font-weight-bold\\\"\\>(.*?)\\<\\/span\\>[\\s]*(?:<i class\\=\\\".*?\\\"\\>.*?\\<\\/i\\>)*[\\s]*(.*?)[\\s]*\\<\\/p\\>", html);
		
		while (commonDataMatcher.find()) {
			String type = commonDataMatcher.group(1);
			AdditionalDataType correspondingType = COMMON_DATA_CORRESPONDANCES.get(type);
			String content = commonDataMatcher.group(2);
			
			// Logger.raw("COMMON_DATA_CORRESPONDANCES.put(\"" + type + "\", AdditionalDataType.UNKNOWN)");
			
			if (correspondingType == null) {
				/* Unknown or find something else */
				continue;
			}
			
			if (!StringUtils.validate(content)) {
				continue;
			}
			
			switch (correspondingType) {
				case ALTERNATIVE_NAME: {
					if (content.matches(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX)) {
						List<String> alternativeNames = new ArrayList<>();
						
						Matcher alternativeNameMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, content);
						
						while (alternativeNameMatcher.find()) {
							String name = alternativeNameMatcher.group(2);
							
							if (StringUtils.validate(name)) {
								alternativeNames.add(name);
							}
						}
						
						if (!alternativeNames.isEmpty()) {
							content = new AdditionalResultData(alternativeNames).convert();
						}
					}
					break;
				}
				
				default: {
					break;
				}
			}
			
			additionals.add(new AdditionalResultData(correspondingType, content));
		}
		
		/* Resume */
		String extractedResume = getHelper().extract("\\<div\\sclass\\=\\\"font-weight-bold\\\"\\>Synopsis:\\<\\/div\\>[\\s]*\\<p\\sclass\\=\\\"list-group-item\\slist-group-item-primary\\stext-justify\\\"\\>(.*?)\\<\\/p\\>", html);
		if (extractedResume != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume));
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		CloudflareRequirement cloudflareRequirement = getRequirement(CloudflareRequirement.class);
		cloudflareRequirement.prepare(getSiteUrl()).executeOnlyIfNotUsable();
		
		if (!cloudflareRequirement.isUsable()) {
			return additionals;
		}
		
		String html = getHelper().downloadPageCache(result.getUrl(), cloudflareRequirement.getCookiesAsHeaderMap(null));
		String htmlContentContainer = getHelper().extract("\\<div\\sclass\\=\\\"rounded-0\\scard-body\\\"\\>[\\s]*\\<div\\sid\\=\\\"chapters_list\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>[\\s]*\\<div\\sid\\=\\\"sidebar\\\"", html);
		
		if (!StringUtils.validate(html, htmlContentContainer)) {
			return additionals;
		}
		
		Matcher volumeHtmlContainerMatcher = getHelper().regex("(\\<h4\\sclass\\=\\\"text-truncate\\\"\\>.*?\\<\\/div\\>[\\s]*\\<\\/div\\>)", htmlContentContainer);
		
		while (volumeHtmlContainerMatcher.find()) {
			String volumeHtmlContainer = volumeHtmlContainerMatcher.group(1);
			
			String chapterListHtmlContainer = getHelper().extract("(\\<div\\sid\\=\\\"collapse-[\\d]*\\\"\\sclass\\=\\\"collapse[\\s]*\\\"\\saria-labelledby\\=\\\"heading-[\\d]*\\\"[\\s]*\\>.*?\\<\\/div\\>[\\s]*\\<\\/div\\>)", volumeHtmlContainer);
			
			String volume = getHelper().extract("\\<span.*?\\>.*?\\<i\\sclass\\=\\\"fas\\sfa-plus-circle\\\"\\>\\<\\/i\\>[\\s]*(.*?)[\\s]*\\<\\/span\\>", volumeHtmlContainer);
			
			Matcher chapterMatcher = getHelper().regex("\\<a\\sclass\\=\\\"text-dark\\\"\\shref\\=\\\"(.*?)\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>", chapterListHtmlContainer);
			
			while (chapterMatcher.find()) {
				String url = getSiteUrl() + chapterMatcher.group(1);
				String title = chapterMatcher.group(2);
				
				additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, new ChapterItemResultData(this, url, volume, title, ChapterItemResultData.ChapterType.IMAGE_ARRAY).complements(SimpleData.REQUIRE_HTTP_HEADERS_COMPLEMENT, cloudflareRequirement.getCookiesAsHeaderMap(null))));
			}
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { GenericJapScanChapterExtractor.class };
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
	/**
	 * Get manga from a json source.<br>
	 * 
	 * @param json
	 *            The downloaded json of the /search/ post
	 * @return A list of {@link JapscanItem} that you can work with.<br>
	 *         That contain the full match, the url, the name and the image url.
	 */
	public static List<JapscanItem> getMangaFromJson(String json) {
		List<JapscanItem> items = new ArrayList<>();
		
		JsonArray jsonArray;
		
		try {
			jsonArray = (JsonArray) new JsonParser().parse(json);
		} catch (Exception exception) {
			return items;
		}
		
		for (Object jsonItem : jsonArray) {
			JsonObject map = (JsonObject) jsonItem;
			
			String subUrl = map.getString(SEARCH_JSON_KEY_URL);
			String name = map.getString(SEARCH_JSON_KEY_NAME);
			String subImageUrl = map.getString(SEARCH_JSON_KEY_IMAGE_URL);
			
			items.add(new JapscanItem(subUrl, name, subImageUrl));
		}
		
		return items;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class JapscanItem extends ResultItem {
		public JapscanItem(String url, String name, String imageUrl) {
			super(null, url, name, imageUrl);
		}
	}
	
}