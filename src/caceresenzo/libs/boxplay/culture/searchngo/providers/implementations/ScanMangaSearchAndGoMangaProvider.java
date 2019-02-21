package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.content.text.INovelContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.RatingResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData.ChapterType;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.StringUtils;

public class ScanMangaSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider, INovelContentProvider {
	
	public static final int API_RESULT_INDEX_NAME = 0;
	public static final int API_RESULT_INDEX_URL = 1;
	public static final int API_RESULT_INDEX_GENDERS_HTML = 2;
	public static final int API_RESULT_INDEX_AUTHOR = 3;
	public static final int API_RESULT_INDEX_BASE_IMAGE_URL = 4;
	public static final int API_RESULT_INDEX_LAST_CHAPTER_URL = 5;
	
	public static final String ADDITIONAL_DATA_KEY_AUTHORS = "Auteur/Artiste";
	public static final String ADDITIONAL_DATA_KEY_TYPE = "Catégorie";
	public static final String ADDITIONAL_DATA_KEY_GENDERS = "Genres";
	public static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Année";
	public static final String ADDITIONAL_DATA_KEY_PUBLISHERS = "Éditeur original";
	public static final String ADDITIONAL_DATA_KEY_LAST_CHAPTER = "Dernier chapitre";
	public static final String ADDITIONAL_DATA_KEY_STATUS = "Statut";
	public static final String ADDITIONAL_DATA_KEY_TRADUCTION_TEAM = "Team";
	public static final String ADDITIONAL_DATA_KEY_RATING = "Popularité";
	public static final String ADDITIONAL_DATA_KEY_RESUME = "Synopsis";
	
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION = new EnumMap<>(AdditionalDataType.class);
	
	private final String imageServerBaseUrl, searchApiUrlFormat;
	
	public ScanMangaSearchAndGoMangaProvider() {
		super("Scan Manga", "http://www.scan-manga.com");
		
		this.imageServerBaseUrl = getSiteUrl() + ":8080/img";
		this.searchApiUrlFormat = getSiteUrl() + "/qsearch.json?term=%s";
		
		// ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.AUTHORS, ADDITIONAL_DATA_KEY_AUTHORS); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.TYPE, ADDITIONAL_DATA_KEY_TYPE);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.GENDERS, ADDITIONAL_DATA_KEY_GENDERS); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_RELEASE_DATE);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.PUBLISHERS, ADDITIONAL_DATA_KEY_PUBLISHERS);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.LAST_CHAPTER, ADDITIONAL_DATA_KEY_LAST_CHAPTER);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.STATUS, ADDITIONAL_DATA_KEY_STATUS);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.TRADUCTION_TEAM, ADDITIONAL_DATA_KEY_TRADUCTION_TEAM);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.RATING, ADDITIONAL_DATA_KEY_RATING); // Not usable in a loop
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return ProviderSearchCapability.fromArray( //
				SearchCapability.MANGA //
		);
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
		
		if (!StringUtils.validate(jsonString)) {
			return result;
		}
		
		List<MangaScanItem> resultItems = extractMangaFromJson(jsonString);
		
		for (MangaScanItem mangaScan : resultItems) {
			String url = mangaScan.getUrl();
			String imageUrl = imageServerBaseUrl + mangaScan.getImageUrl();
			String name = mangaScan.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, name, url, imageUrl, SearchCapability.MANGA) //
						.score(score) //
						.subscribableAt(url) //
				);
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		/* Alternative Names */
		String alternativeNamesHtml = getHelper().extract("\\<div\\sclass=[\\\"\\']{1}alt_name[\\\"\\']{1}\\>(.*?)\\<\\/div\\>", html);
		if (alternativeNamesHtml != null) {
			Matcher nameMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, alternativeNamesHtml);
			
			List<String> alternativeNames = new ArrayList<>();
			
			while (nameMatcher.find()) {
				String name = HtmlCommonExtractor.escapeUnicode(nameMatcher.group(2));
				
				alternativeNames.add(name);
			}
			
			if (!alternativeNames.isEmpty()) {
				additionals.add(new AdditionalResultData(AdditionalDataType.ALTERNATIVE_NAME, alternativeNames));
			}
		}
		
		/* Information Container */
		List<String> keys = extractDataList(htmlContainer, "contenu_titres_fiche_technique");
		List<String> values = extractDataList(htmlContainer, "contenu_texte_fiche_technique");
		
		if (keys.size() == values.size()) {
			Map<String, String> dataMap = new LinkedHashMap<>();
			
			for (int index = 0; index < keys.size(); index++) {
				String key = keys.get(index);
				String value = values.get(index);
				
				dataMap.put(key, value);
			}
			
			if (!dataMap.isEmpty()) {
				/* Common */
				for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
					AdditionalDataType type = entry.getKey();
					String dataMapKey = entry.getValue();
					
					String value = dataMap.get(dataMapKey);
					
					if (value != null) {
						additionals.add(new AdditionalResultData(type, value));
					}
				}
				
				/* Url-extraction needed common */
				for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.entrySet()) {
					AdditionalDataType type = entry.getKey();
					String dataMapKey = entry.getValue();
					
					UrlResultData urlResultData = getHelper().extractUrlFromHtml(dataMap.get(dataMapKey));
					
					if (urlResultData != null && StringUtils.validate(urlResultData.getString())) {
						additionals.add(new AdditionalResultData(type, urlResultData));
					}
				}
				
				/* Authors */
				String extractedAuthorsData = dataMap.get(ADDITIONAL_DATA_KEY_AUTHORS);
				if (extractedAuthorsData != null) {
					Matcher urlMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, extractedAuthorsData);
					
					String authors = "";
					
					while (urlMatcher.find()) {
						String name = urlMatcher.group(2);
						
						authors += name;
					}
					
					if (!StringUtils.validate(authors)) {
						additionals.add(new AdditionalResultData(AdditionalDataType.AUTHORS, authors));
					}
				}
				
				/* Genders */
				String extractedGendersData = dataMap.get(ADDITIONAL_DATA_KEY_GENDERS);
				if (extractedGendersData != null) {
					Matcher genderMatcher = getHelper().regex("<a.*?\\<span\\>.*?\\<\\/span\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>", extractedGendersData);
					
					List<CategoryResultData> genders = new ArrayList<>();
					
					while (genderMatcher.find()) {
						String name = genderMatcher.group(1);
						
						if (name != null) {
							genders.add(new CategoryResultData(name));
						}
					}
					
					if (!genders.isEmpty()) {
						additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, genders));
					}
				}
				
				/* Rating */
				String extractedRatingData = dataMap.get(ADDITIONAL_DATA_KEY_RATING);
				if (extractedRatingData != null) {
					String extractedRatingHtmlContainer = getHelper().extract("\\<section\\sitemprop\\=[\\\"\\']{1}aggregateRating[\\\"\\']{1}\\sitemscope\\sitemtype\\=[\\\"\\']{1}http\\:\\/\\/schema\\.org\\/AggregateRating[\\\"\\']{1}\\>(.*?)\\<\\/section\\>", extractedRatingData);
					
					if (extractedRatingHtmlContainer != null) {
						final String extractRatingValueRegex = "\\<span\\sitemprop=\\\"%s\\\"\\scontent=\\\"[\\d\\.]*\\\"\\>([\\d]*)\\<\\/span\\>";
						
						float average = ParseUtils.parseFloat(getHelper().extract(String.format(extractRatingValueRegex, "ratingValue"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
						int best = ParseUtils.parseInt(getHelper().extract(String.format(extractRatingValueRegex, "bestRating"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
						int votes = ParseUtils.parseInt(getHelper().extract(String.format(extractRatingValueRegex, "ratingCount"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
						
						if (average != RatingResultData.NO_VALUE && best != RatingResultData.NO_VALUE && votes != RatingResultData.NO_VALUE) {
							additionals.add(new AdditionalResultData(AdditionalDataType.RATING, new RatingResultData(average, best, votes)));
						}
					}
				}
			}
		}
		
		String extractedResumeData = extractResumeData(html);
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResumeData));
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
		
		ChapterType chapterType = ChapterType.IMAGE_ARRAY;
		if (result.getName().endsWith("(Novel)")) {
			chapterType = ChapterType.TEXT;
		}
		
		Matcher volumeContainerMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}volume_manga\\sborder_radius_contener[\\'\\\"]{1}\\srole=[\\'\\\"]{1}navigation[\\'\\\"]{1}\\>(.*?)\\<\\/div\\>[\\s\\n\\t]*\\<\\/div\\>[\\s\\n\\t]*\\<\\/div\\>", html);
		
		while (volumeContainerMatcher.find()) {
			String htmlVolumeContainer = volumeContainerMatcher.group(1);
			
			Matcher volumeBookImageMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}cover_volume_manga[\\'\\\"]{1}>[\\s\\t\\n]*<img.*?src=[\\'\\\"]{1}(.*?)[\\'\\\"]{1}.*?\\>[\\s\\t\\n]*\\<\\/div>", htmlVolumeContainer);
			if (volumeBookImageMatcher.find()) {
				// TODO: Do something with the volume book image
			}
			
			Matcher volumeBookMatcher = getHelper().regex("\\<div\\sclass=[\\'\\\"]{1}titre_volume_manga[\\'\\\"]{1}\\>[\\s\\t\\n]*\\<h3\\>(.*?)\\<\\/h3\\>.*?\\<\\/div\\>", htmlVolumeContainer);
			if (!volumeBookMatcher.find()) {
				continue;
			}
			
			String volume = volumeBookMatcher.group(1).toUpperCase();
			
			Matcher chaptersListMatcher = getHelper().regex("\\<ul\\>(.*?)\\<\\/ul\\>", htmlVolumeContainer);
			if (!chaptersListMatcher.find()) {
				continue;
			}
			
			String htmlChaptersList = chaptersListMatcher.group(1);
			List<ChapterItemResultData> extractedChapter = new ArrayList<>();
			
			Matcher chapterMatcher = getHelper().regex("\\<li\\sclass=[\\'\\\"]{1}chapitre[\\'\\\"]{1}.*?\\>[\\s\\t\\n]*\\<div\\sclass=[\\'\\\"]{1}chapitre_nom[\\'\\\"]{1}\\>\\<a\\s.*?href=[\\'\\\"]{1}(.*?)[\\'\\\"]{1}>(.*?)\\<\\/a\\>(.*?)\\<\\/div\\>.*?\\<\\/li\\>", htmlChaptersList);
			while (chapterMatcher.find()) {
				String url = chapterMatcher.group(1);
				String chapterTitle = chapterMatcher.group(2).toUpperCase() + chapterMatcher.group(3);
				
				Map<String, String> headers = new HashMap<>();
				// headers.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
				// headers.put("Accept-Encoding", "gzip, deflate");
				// headers.put("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
				// headers.put("Cache-Control", "no-cache");
				// headers.put("Connection", "keep-alive");
				// headers.put("DNT", "1");
				// headers.put("Host", "lei.scan-manga.com:8080");
				// headers.put("Pragma", "no-cache");
				headers.put("Referer", url);
				// headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
				
				extractedChapter.add((ChapterItemResultData) new ChapterItemResultData(this, url, volume, chapterTitle, chapterType).complements(SimpleData.REQUIRE_HTTP_HEADERS_COMPLEMENT, headers));
			}
			
			/* Disabled series are sometimes differents, because they got their (reader) url removed */
			if (extractedChapter.isEmpty()) {
				Matcher disabledChapterMatcher = getHelper().regex("\\<li\\sclass=[\\'\\\"]{1}chapitre[\\'\\\"]{1}.*?\\>[\\s\\t\\n]*\\<div\\sclass=[\\'\\\"]{1}chapitre_nom[\\'\\\"]{1}\\>\\<strong\\>(.*?)\\<\\/strong\\>(.*?)\\<\\/div\\>.*?\\<\\/li\\>", htmlChaptersList);
				while (disabledChapterMatcher.find()) {
					String chapterTitle = disabledChapterMatcher.group(1).toUpperCase() + disabledChapterMatcher.group(2);
					
					extractedChapter.add(new ChapterItemResultData(this, null, volume, chapterTitle, chapterType));
				}
			}
			
			if (!extractedChapter.isEmpty()) {
				for (ChapterItemResultData chapterItem : extractedChapter) {
					additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, chapterItem));
				}
			}
			
		}
		
		return additionals;
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
			
			if (url.contains("/auteur-") || url.contains("/team-")) {
				continue;
			}
			
			items.add(new MangaScanItem(url, name, imageUrl));
		}
		
		return items;
	}
	
	/**
	 * On Scan-Manga, that will extract a container string that is a div html container, containing all information about the manga
	 * 
	 * @param html
	 *            The downloaded html of a manga page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<div\\sclass=[\\\"\\']{1}fiche_technique\\sfolder_corner[\\\"\\']{1}\\>(.*?)\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>", html);
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
		return getStaticHelper().extract("\\<div\\sclass=[\\\"\\']{1}synopsis_manga\\sborder_radius_contener\\sfolder_corner[\\\"\\']{1}\\>.*?\\<p\\sitemprop=[\\\"\\']{1}articleBody[\\\"\\']{1}\\>(.*?)\\<\\/p\\>.*?\\<\\/div\\>", html);
	}
	
	/**
	 * On the Scan-Manga, information are separated in two html list put side by side with html
	 * 
	 * This will extract one list, choosen from its css class key
	 * 
	 * @param htmlContainer
	 *            Target html container to do extraction
	 * @param htmlClassKey
	 *            Target css key used to be side by side
	 * @return A list containing all item of the target html list
	 */
	public static List<String> extractDataList(String htmlContainer, String htmlClassKey) {
		List<String> list = new ArrayList<>();
		
		String extractedListHtml = getStaticHelper().extract(String.format("\\<div\\sclass=[\\\"\\']{1}%s[\\\"\\']{1}\\>[\\s\\t\\n]*\\<ul\\>(.*?)\\<\\/ul\\>[\\s\\t\\n]*\\<\\/div\\>", htmlClassKey), htmlContainer);
		
		if (extractedListHtml != null) {
			Matcher matcher = getStaticHelper().regex(HtmlCommonExtractor.COMMON_LIST_EXTRACTION_REGEX, extractedListHtml);
			
			while (matcher.find()) {
				String data = AdditionalResultData.escapeHtmlChar(matcher.group(1));
				
				list.add(data);
			}
		}
		
		return list;
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