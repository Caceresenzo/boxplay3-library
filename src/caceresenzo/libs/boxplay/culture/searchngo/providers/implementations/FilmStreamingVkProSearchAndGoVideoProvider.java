package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

public class FilmStreamingVkProSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	public static final String ADDITIONAL_DATA_KEY_ORIGINAL_NAME = "Titre Original:";
	public static final String ADDITIONAL_DATA_KEY_GENDERS = "Genre:";
	public static final String ADDITIONAL_DATA_KEY_QUALITY = "Qualité:";
	public static final String ADDITIONAL_DATA_KEY_VERSION = "Version:";
	public static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Année:";
	public static final String ADDITIONAL_DATA_KEY_COUNTRY = "Pays:";
	public static final String ADDITIONAL_DATA_KEY_DIRECTOR = "Directeur:";
	public static final String ADDITIONAL_DATA_KEY_ACTORS = "Acteurs:";
	public static final String ADDITIONAL_DATA_KEY_DURATION = "Durée:";
	public static final String ADDITIONAL_DATA_KEY_RESUME = "Synopsis";
	
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION = new EnumMap<>(AdditionalDataType.class);
	
	private final String searchBaseUrl;
	
	public FilmStreamingVkProSearchAndGoVideoProvider() {
		super("FILMSTREAMINGVK.pro", "http://www.filmstreamingvk.pro/");
		
		this.searchBaseUrl = getSiteUrl() + "/index.php?do=search";
		
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.ORIGINAL_NAME, ADDITIONAL_DATA_KEY_ORIGINAL_NAME);
		// ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.GENDERS, ADDITIONAL_DATA_KEY_GENDERS); // Not usable in a loop
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.QUALITY, ADDITIONAL_DATA_KEY_QUALITY);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.VERSION, ADDITIONAL_DATA_KEY_VERSION);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_RELEASE_DATE);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.COUNTRY, ADDITIONAL_DATA_KEY_COUNTRY);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.DIRECTOR, ADDITIONAL_DATA_KEY_DIRECTOR);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.ACTORS, ADDITIONAL_DATA_KEY_ACTORS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.DURATION, ADDITIONAL_DATA_KEY_DURATION);
		ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.put(AdditionalDataType.RESUME, ADDITIONAL_DATA_KEY_RESUME);
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return false;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MOVIE, SearchCapability.SERIES });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		Webb webb = Webb.create();
		webb.setDefaultHeader(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT);
		
		String html = webb //
				.post(searchBaseUrl) //
				.header("Content-Type", "application/x-www-form-urlencoded") //
				
				.param("do", "search") //
				.param("subaction", "search") //
				.param("search_start", "0") //
				.param("full_search", "0") //
				.param("result_from", "1") //
				.param("story", searchQuery) //
				
				.ensureSuccess() //
				.asString().getBody(); //
		
		if (!StringUtils.validate(html, searchQuery) || searchQuery.length() < 4) {
			return result;
		}
		
		List<VoirFilmBzItem> resultItems = extractVideoFromHtml(html);
		
		for (VoirFilmBzItem voirFilmBzItem : resultItems) {
			String url = voirFilmBzItem.getUrl();
			String imageUrl = getSiteUrl() + voirFilmBzItem.getImageUrl();
			String name = voirFilmBzItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, voirFilmBzItem.getName(), url, imageUrl, SearchCapability.VIDEO).score(score));
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
		
		/* Common */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonData(dataKey, htmlContainer);
			if (extractedData != null) {
				additionals.add(new AdditionalResultData(type, extractedData.trim()));
			}
		}
		
		/* Item that need to be url-extracted (html element <a>) */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE_FOR_URL_EXTRATCTION.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedHtmlElementData = extractCommonData(dataKey, htmlContainer);
			if (extractedHtmlElementData != null) {
				UrlResultData extractedUrlData = getHelper().extractUrlFromHtml(extractedHtmlElementData);
				
				additionals.add(new AdditionalResultData(type, extractedUrlData));
			}
		}
		
		/* Genders */
		String extractedGendersData = extractCommonData(ADDITIONAL_DATA_KEY_GENDERS, htmlContainer);
		if (extractedGendersData != null) {
			Matcher matcher = getHelper().regex(UrlResultData.EXTRATION_REGEX_FROM_HTML, extractedGendersData);
			
			List<CategoryResultData> categories = new ArrayList<>();
			
			while (matcher.find()) {
				categories.add(new CategoryResultData(matcher.group(1), AdditionalResultData.escapeHtmlChar(matcher.group(2))));
			}
			
			additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
		}
		
		/* Resume */
		String extractedResumeData = extractResumeData(htmlContainer);
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResumeData));
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		
		if (!StringUtils.validate(html)) {
			return additionals;
		}
		
		List<String> buttons = new ArrayList<>(), iframeUrls = new ArrayList<>();
		
		String extractedHtmlButtonContainer = getHelper().extract("<div\\sclass=\\\"tabs-sel\\\">(.*?)<\\/div>", html);
		if (extractedHtmlButtonContainer == null) {
			return additionals;
		}
		
		Matcher buttonMatcher = getHelper().regex("\\<span.*?\\>(.*?)\\<\\/span\\>", extractedHtmlButtonContainer);
		while (buttonMatcher.find()) {
			buttons.add(buttonMatcher.group(1));
		}
		
		Matcher iframeMatcher = getHelper().regex("\\<div\\sclass=\\\"tabs-b\\svideo-box\\\"\\>[\\s\\t\\n]*\\<iframe\\ssrc=\\\"(.*?)\\\".*?\\>\\<\\/iframe\\>[\\s\\t\\n]*\\<\\/div\\>", html);
		while (iframeMatcher.find()) {
			iframeUrls.add(iframeMatcher.group(1));
		}
		
		if (buttons.size() != iframeUrls.size()) {
			return additionals;
		}
		
		for (int i = 0; i < buttons.size(); i++) {
			String name = buttons.get(i);
			String url = iframeUrls.get(i);
			
			additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, url, name)));
		}
		
		return additionals;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		return new String[] { videoItemResult.getUrl() };
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { OpenloadVideoExtractor.class };
	}
	
	private List<VoirFilmBzItem> extractVideoFromHtml(String html) {
		List<VoirFilmBzItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass\\=\\\"mov\\\"\\>[\\s\\t\\n]*\\<div\\sclass\\=\\\"mov-i\\simg-box\\\"\\>[\\s\\t\\n]*\\<img\\ssrc\\=\\\"(.*?)\\\".*?\\/\\>[\\s\\t\\n]*\\<div\\sclass\\=\\\"mov-mask\\sflex-col\\sps-link\\\".*?\\>.*?\\<\\/div\\>[\\s\\t\\n]*\\<div\\sclass\\=\\\"mov-m\\\"\\>.*?\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<a\\sclass=\\\"mov-t\\snowrap\\\"\\shref=\\\"(.*?)\\\"\\>\\<b\\>(<center>)*(.*?)(<\\/center>)*\\<\\/b\\>\\<\\/a\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*", html);
		while (matcher.find()) {
			String imageUrl = matcher.group(1);
			String url = matcher.group(2);
			String name = matcher.group(4);
			
			items.add(new VoirFilmBzItem(matcher.group(0), url, name, imageUrl));
		}
		
		return items;
	}
	
	/**
	 * Extract common data on the VOIRFILM.bz information container
	 * 
	 * @param dataKey
	 *            Something like "Genre:" or "Qualité:", a key that will used as a line identifier
	 * @param htmlContainer
	 *            A html container, source of data
	 * @return Some extracted data, null if not found
	 */
	public static String extractCommonData(String dataKey, String htmlContainer) {
		return getStaticHelper().extract(String.format("\\<li\\>\\<div\\sclass=\\\"mov-label\\\">[\\s]*%s[\\s]*\\<\\/div\\>[\\s]*\\<div\\sclass=\\\"mov-desc\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/div\\>\\<\\/li\\>", dataKey), htmlContainer);
	}
	
	/**
	 * On VOIRFILM.bz, that will extract a container string that is a article div in html containing all information about the series/movie
	 * 
	 * @param html
	 *            The downloaded html of a series/movie page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<article\\sclass=\\\"full\\\"\\>(.*?)\\<\\/article\\>", html);
	}
	
	/**
	 * Extract the resume from the HTML INFORMATION CONTAINER
	 * 
	 * @param htmlContainer
	 *            Information container
	 * @return Extracted resume, null if not found
	 */
	public static String extractResumeData(String htmlContainer) {
		return getStaticHelper().extract(String.format("\\<div\\sclass=\\\"screenshots-full\\\"\\>[\\s\\t\\n]*\\<div\\sclass=\\\"screenshots-title\\\"\\>%s\\<\\/div\\>[\\s]*(.*?)[\\s]*\\<\\/div\\>", ADDITIONAL_DATA_KEY_RESUME), htmlContainer);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class VoirFilmBzItem extends ResultItem {
		public VoirFilmBzItem(String match, String url, String name, String imageUrl) {
			super(match, url, name, imageUrl);
		}
	}
	
}