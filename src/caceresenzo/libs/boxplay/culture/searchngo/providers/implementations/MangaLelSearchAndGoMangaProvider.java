package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaLelChapterExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.RatingResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.StringUtils;

public class MangaLelSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION = new EnumMap<>(AdditionalDataType.class);
	
	public static final String ADDITIONAL_DATA_KEY_NAME = "h2";
	public static final String ADDITIONAL_DATA_KEY_OTHER_NAME = "Autres noms";
	public static final String ADDITIONAL_DATA_KEY_STATUS = "Statut";
	public static final String ADDITIONAL_DATA_KEY_TYPE = "Type";
	public static final String ADDITIONAL_DATA_KEY_TRADUCTION_TEAM = "Team";
	public static final String ADDITIONAL_DATA_KEY_AUTHORS = "Auteur\\(s\\)";
	public static final String ADDITIONAL_DATA_KEY_ARTISTS = "Artist\\(s\\)";
	public static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Date de sortie";
	public static final String ADDITIONAL_DATA_KEY_GENDERS = "Catégories";
	public static final String ADDITIONAL_DATA_KEY_VIEWS = "Vues";
	public static final String ADDITIONAL_DATA_KEY_RATING = "Note";
	public static final String ADDITIONAL_DATA_KEY_RESUME = "Résumé";
	
	private final String listApiUrl;
	private final String imageUrlFormat;
	
	public MangaLelSearchAndGoMangaProvider() {
		super("Manga-LEL", "https://www.manga-lel.com");
		
		this.listApiUrl = getSiteUrl() + "/changeMangaList?type=text";
		this.imageUrlFormat = getSiteUrl() + "//uploads/manga/%s/cover/cover_250x350.jpg";
		
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.NAME, ADDITIONAL_DATA_KEY_NAME); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.OTHER_NAME, ADDITIONAL_DATA_KEY_OTHER_NAME);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.STATUS, ADDITIONAL_DATA_KEY_STATUS); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.TYPE, ADDITIONAL_DATA_KEY_TYPE);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.TRADUCTION_TEAM, ADDITIONAL_DATA_KEY_TRADUCTION_TEAM);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.AUTHORS, ADDITIONAL_DATA_KEY_AUTHORS);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.ARTISTS, ADDITIONAL_DATA_KEY_ARTISTS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_RELEASE_DATE);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.GENDERS, ADDITIONAL_DATA_KEY_GENDERS); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.VIEWS, ADDITIONAL_DATA_KEY_VIEWS);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.RATING, ADDITIONAL_DATA_KEY_RATING); // Not usable in a loop
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
		
		String html = getHelper().downloadPageCache(listApiUrl);
		
		if (html == null) {
			return result;
		}
		
		List<MangaLelItem> resultItems = extractMangaFromHtml(html);
		
		for (MangaLelItem mangaLelItem : resultItems) {
			String url = mangaLelItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, url.replaceAll("(https\\:\\/\\/www\\.manga-lel\\.com\\/manga\\/|\\/)", ""));
			String name = mangaLelItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, mangaLelItem.getName(), url, imageUrl, SearchCapability.MANGA).score(score));
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
		
		/**
		 * Other
		 */
		// NAME
		String extratedNameData = getHelper().extract("\\<h2\\sclass=\\\"widget-title\\\"\\sstyle=\\\"display:\\sinline-block;\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/h2\\>", html);
		
		if (extratedNameData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.NAME, extratedNameData));
		}
		
		// STATUS
		String extractedStatusData = getHelper().extractStringFromHtml("span", extractCommonData(ADDITIONAL_DATA_KEY_STATUS, htmlContainer));
		
		if (extractedStatusData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.STATUS, extractedStatusData));
		}
		
		// GENDERS
		String extractedGendersData = extractCommonData(ADDITIONAL_DATA_KEY_GENDERS, htmlContainer);
		
		if (extractedGendersData != null) {
			Matcher matcher = getHelper().regex(UrlResultData.EXTRATION_REGEX_FROM_HTML, extractedGendersData);
			
			List<CategoryResultData> categories = new ArrayList<>();
			
			while (matcher.find()) {
				categories.add(new CategoryResultData(matcher.group(1), AdditionalResultData.escapeHtmlChar(matcher.group(2))));
			}
			
			additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
		}
		
		// RATING
		String extractedRatingHtmlContainer = extractCommonData(ADDITIONAL_DATA_KEY_RATING, htmlContainer);
		
		if (extractedRatingHtmlContainer != null) {
			final String extractRatingValueRegex = "\\<meta\\sitemprop=\\\"%s\\\"\\scontent[\\s]*=[\\s]*\\\"[\\s]*([\\d\\.]*)[\\s]*\\\"\\>";
			
			float average = ParseUtils.parseFloat(getHelper().extract(String.format(extractRatingValueRegex, "average"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
			int best = ParseUtils.parseInt(getHelper().extract(String.format(extractRatingValueRegex, "best"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
			int votes = ParseUtils.parseInt(getHelper().extract(String.format(extractRatingValueRegex, "votes"), extractedRatingHtmlContainer), RatingResultData.NO_VALUE);
			
			if (average != RatingResultData.NO_VALUE && best != RatingResultData.NO_VALUE && votes != RatingResultData.NO_VALUE) {
				additionals.add(new AdditionalResultData(AdditionalDataType.RATING, new RatingResultData(average, best, votes)));
			}
		}
		
		// RESUME
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
		
		Matcher matcher = getHelper().regex("\\<li\\sstyle=\\\"padding:\\s3px\\s0;\\\"\\sclass=\\\"volume-[\\d]*\\\"\\>[\\s\\t\\n]*\\<h5\\sclass=\\\"chapter-title-rtl\\\"\\>[\\s\\t\\n]*\\<a\\shref=\\\"(.*?)\\\">(.*?)\\<\\/a\\>.*?\\<em\\>[\\s]*(.*?)[\\s]*\\<\\/em\\>[\\s\\t\\n]*\\<\\/h5\\>[\\s\\t\\n]*\\<div\\sclass=\\\"action[\\s]*\\\"\\>[\\s\\t\\n]*\\<div.*?\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/li\\>", html);
		
		while (matcher.find()) {
			String url = matcher.group(1);
			String chapter = matcher.group(2);
			String title = matcher.group(3);
			
			additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, new ChapterItemResultData(this, url, chapter, title)));
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { GenericMangaLelChapterExtractor.class } ;
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
	/**
	 * Extract all Manga present on the website
	 * 
	 * @param html
	 *            The downloaded html of anypage
	 * @return A list of {@link MangaLelItem} that you can work with. That contain the full match, the url, and the name
	 */
	public static List<MangaLelItem> extractMangaFromHtml(String html) {
		List<MangaLelItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<a\\shref=\\\"(.*?)\\\"\\sclass=\\\"alpha-link\\\"\\>[\\s\\t\\n]*\\<h6\\sstyle=\\\".*?\\\"\\>(.*?)\\<\\/h6\\>[\\s\\t\\n]*\\<\\/a\\>", html);
		
		while (matcher.find()) {
			items.add(new MangaLelItem(matcher.group(0).trim(), matcher.group(1).trim(), getStaticHelper().escapeHtmlSpecialCharactere(matcher.group(2).trim())));
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
		return getStaticHelper().extract(String.format("\\<div\\sclass=\\\"well\\\"\\>[\\s\\t\\n]*\\<h5\\>\\<strong\\>%s\\<\\/strong\\>\\<\\/h5\\>[\\s\\t\\n]*\\<p\\>[ ]*(.*?)[ ]*\\<\\/p\\>[\\s\\t\\n]*\\<\\/div\\>", ADDITIONAL_DATA_KEY_RESUME), html);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class MangaLelItem extends ResultItem {
		public MangaLelItem(String match, String url, String name) {
			super(match, url, name);
		}
	}
	
}