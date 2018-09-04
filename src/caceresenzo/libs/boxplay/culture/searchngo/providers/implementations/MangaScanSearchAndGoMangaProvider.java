package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaScanChapterExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

public class MangaScanSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	public static final int API_RESULT_INDEX_NAME = 0;
	public static final int API_RESULT_INDEX_URL = 1;
	public static final int API_RESULT_INDEX_GENDERS_HTML = 2;
	public static final int API_RESULT_INDEX_AUTHOR = 3;
	public static final int API_RESULT_INDEX_BASE_IMAGE_URL = 4;
	public static final int API_RESULT_INDEX_LAST_CHAPTER_URL = 5;
	
	public static final String API_IMAGE_REDIRECTOR_URL_FORMAT = "http://caceresenzo.esy.es/api/v3/helper/mangascan/image.php?url=%s";
	
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION = new EnumMap<>(AdditionalDataType.class);
	
	public static final String ADDITIONAL_DATA_KEY_NAME = "h2";
	
	private final String imageServerBaseUrl, searchApiUrlFormat;
	
	public MangaScanSearchAndGoMangaProvider() {
		super("Manga Scan", "http://www.scan-manga.com");
		
		this.imageServerBaseUrl = getSiteUrl() + ":8080/img";
		this.searchApiUrlFormat = getSiteUrl() + "/qsearch.json?term=%s";
		
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.NAME, ADDITIONAL_DATA_KEY_NAME); // Not usable in a loop
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MANGA });
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String jsonString = Webb.create().get(String.format(searchApiUrlFormat, URLEncoder.encode(searchQuery))) //
				.header("Host", "www.scan-manga.com") //
				.header("Connection", "keep-alive") //
				.header("Pragma", "no-cache") //
				.header("Cache-Control", "no-cache") //
				.header("Accept", "application/json, text/javascript, */*; q=0.01") //
				.header("DNT", "1") //
				.header("X-Requested-With", "XMLHttpRequest") //
				.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36") //
				.header("Referer", "http://www.scan-manga.com/scanlation/liste_des_series.html") //
				.header("Accept-Encoding", "gzip, deflate") //
				.header("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7") //
				.asString().getBody();
		
		if (jsonString == null) {
			return result;
		}
		
		List<MangaScanItem> resultItems = extractMangaFromJson(jsonString);
		
		for (MangaScanItem mangaScan : resultItems) {
			String url = mangaScan.getUrl();
			String imageUrl = imageServerBaseUrl + mangaScan.getImageUrl();
			String name = mangaScan.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, name, url, imageUrl, SearchCapability.MANGA).score(score));
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (html == null || html.isEmpty() || htmlContainer == null || htmlContainer.isEmpty()) {
			return additionals;
		}
		
		/**
		 * Common
		 */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonData(dataKey, htmlContainer);
			
			if (extractedData != null) {
				if (type.equals(AdditionalDataType.TYPE)) {
					extractedData = StringUtils.capitalize(extractedData);
				}
				
				additionals.add(new AdditionalResultData(type, extractedData.trim()));
			}
		}
		
		/**
		 * Item that need to be url-extracted (html element <a>)
		 */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedHtmlElementData = extractCommonData(dataKey, htmlContainer);
			
			if (extractedHtmlElementData != null) {
				UrlResultData extractedUrlData = getHelper().extractUrlFromHtml(extractedHtmlElementData);
				
				additionals.add(new AdditionalResultData(type, extractedUrlData));
			}
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		
		if (html == null || html.isEmpty()) {
			return additionals;
		}
		
		Matcher volumeContainerMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}volume_manga\\sborder_radius_contener[\\'\\\"]{1}\\srole=[\\'\\\"]{1}navigation[\\'\\\"]{1}\\>(.*?)\\<\\/div\\>[\\s\\n\\t]*\\<\\/div\\>[\\s\\n\\t]*\\<\\/div\\>", html);
		
		while (volumeContainerMatcher.find()) {
			String htmlVolumeContainer = volumeContainerMatcher.group(1);
			
			Matcher volumeBookImageMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}cover_volume_manga[\\'\\\"]{1}>[\\s\\t\\n]*<img.*?src=[\\'\\\"]{1}(.*?)[\\'\\\"]{1}.*?\\>[\\s\\t\\n]*\\<\\/div>", htmlVolumeContainer);
			if (volumeBookImageMatcher.find()) {
				// TODO: Do something with the volume book image
			}
			
			Matcher volumeBookMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}titre_volume_manga[\\'\\\"]{1}\\>[\\s\\t\\n]*\\<h3\\>(.*?)\\<\\/h3\\>[\\s\\t\\n]*\\<span.*?>[\\(]*(.*?)[\\)]\\<\\/span\\>\\<\\/div\\>", htmlVolumeContainer);
			if (!volumeBookMatcher.find()) {
				continue;
			}
			
			String volume = volumeBookMatcher.group(1).toUpperCase();
			String volumeStatus = volumeBookMatcher.group(2);
			
			Matcher chaptersListMatcher = getHelper().regex("\\<ul\\>(.*?)\\<\\/ul\\>", htmlVolumeContainer);
			if (!chaptersListMatcher.find()) {
				continue;
			}
			
			String htmlChaptersList = chaptersListMatcher.group(1);
			
			Matcher chapterMatcher = getHelper().regex("\\<li\\sclass=[\\'\\\"]{1}chapitre[\\'\\\"]{1}.*?\\>[\\s\\t\\n]*\\<div\\sclass=[\\'\\\"]{1}chapitre_nom[\\'\\\"]{1}\\>\\<a\\s.*?href=[\\'\\\"]{1}(.*?)[\\'\\\"]{1}>(.*?)\\<\\/a\\>(.*?)\\<\\/div\\>.*?\\<\\/li\\>", htmlChaptersList);
			while (chapterMatcher.find()) {
				String url = chapterMatcher.group(1);
				String chapterTitle = chapterMatcher.group(2).toUpperCase() + chapterMatcher.group(3);
				
				additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, new ChapterItemResultData(this, url, volume, chapterTitle)));
			}
			
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { GenericMangaScanChapterExtractor.class };
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
	/**
	 * Extract all Manga present on the api json result
	 * 
	 * @param jsonString
	 *            The downloaded json of query
	 * @return A list of {@link MangaScanItem} that you can work with. That contain the full match, the url, the imageUrl, and the name
	 */
	@SuppressWarnings("unchecked")
	public static List<MangaScanItem> extractMangaFromJson(String jsonString) {
		List<MangaScanItem> items = new ArrayList<>();
		
		JsonArray json;
		try {
			json = (JsonArray) new JsonParser().parse(jsonString);
		} catch (JsonException exception) {
			return items;
		}
		
		for (Object jsonItem : json) {
			if (!(jsonItem instanceof List)) {
				continue;
			}
			
			List<Object> list = (List<Object>) jsonItem;
			
			String name = String.valueOf(list.get(API_RESULT_INDEX_NAME));
			String imageUrl = String.valueOf(list.get(API_RESULT_INDEX_BASE_IMAGE_URL));
			String url = String.valueOf(list.get(API_RESULT_INDEX_URL));
			
			items.add(new MangaScanItem(url, name, imageUrl));
		}
		
		return items;
	}
	
	/**
	 * On Manga-LEL, that will extract a container string that is a dl div shit in html containing all information about the manga
	 * 
	 * @param html
	 *            The downloaded html of a manga page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<dl\\sclass=\\\"dl-horizontal\\\"\\>(.*?)\\<\\/dl\\>", html);
	}
	
	/**
	 * Extract common data on the Manga-LEL page
	 * 
	 * @param dataKey
	 *            Something like "Type" or "Team", a key that will used as a line identifier
	 * @param htmlContainer
	 *            A html container, source of data
	 * @return Some extracted data, null if not found
	 */
	public static String extractCommonData(String dataKey, String htmlContainer) {
		return getStaticHelper().extract(String.format("\\<dt\\>[\\s\\t\\n]*%s[\\s\\t\\n]*\\<\\/dt\\>[\\s\\t\\n]*\\<dd\\>[\\s\\t\\n]*(.*?)[\\s\\t\\n]*\\<\\/dd\\>", dataKey), htmlContainer);
	}
	
	/**
	 * Extract the resume from the HTML MAIN PAGE, for some reason, Manga-LEL display it a different div
	 * 
	 * @param html
	 *            Main (manga) page html
	 * @return Extracted resume, null if not found
	 */
	public static String extractResumeData(String html) {
		return getStaticHelper().extract(String.format("\\<div\\sclass=\\\"well\\\"\\>[\\s\\t\\n]*\\<h5\\>\\<strong\\>%s\\<\\/strong\\>\\<\\/h5\\>[\\s\\t\\n]*\\<p\\>[ ]*(.*?)[ ]*\\<\\/p\\>[\\s\\t\\n]*\\<\\/div\\>", "ADDITIONAL_DATA_KEY_RESUME"), html);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class MangaScanItem extends ResultItem {
		public MangaScanItem(String url, String name, String imageUrl) {
			super(null, url, name, imageUrl);
		}
	}
	
}