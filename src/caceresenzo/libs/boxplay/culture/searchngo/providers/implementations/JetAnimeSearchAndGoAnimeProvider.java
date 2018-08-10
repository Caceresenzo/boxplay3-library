package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.ResultDataType;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.cryptography.CloudflareUtils;

public class JetAnimeSearchAndGoAnimeProvider extends SearchAndGoProvider {
	
	protected static final String ADDITIONAL_DATA_KEY_NAME = "Nom:";
	protected static final String ADDITIONAL_DATA_KEY_ORIGINAL_NAME = "Nom original:";
	protected static final String ADDITIONAL_DATA_KEY_ALTERNATIVE_NAME = "Nom Alternatif:";
	protected static final String ADDITIONAL_DATA_KEY_GENDERS = "Genre\\(s\\):";
	protected static final String ADDITIONAL_DATA_KEY_STATUS = "Statut:";
	protected static final String ADDITIONAL_DATA_KEY_AUTHORS = "Auteur\\(s\\):";
	protected static final String ADDITIONAL_DATA_KEY_STUDIOS = "Studio\\(s\\):";
	protected static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Date de Sortie:";
	protected static final String ADDITIONAL_DATA_KEY_RESUME = "Synopsis:";
	
	private final String imageUrlFormat;
	
	public JetAnimeSearchAndGoAnimeProvider() {
		super("JetAnime", "https://www.jetanime.co");
		
		imageUrlFormat = getSiteUrl() + "/assets/imgs/%s.jpg";
		
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.NAME, ADDITIONAL_DATA_KEY_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.ORIGINAL_NAME, ADDITIONAL_DATA_KEY_ORIGINAL_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.ALTERNATIVE_NAME, ADDITIONAL_DATA_KEY_ALTERNATIVE_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.GENDERS, ADDITIONAL_DATA_KEY_GENDERS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.STATUS, ADDITIONAL_DATA_KEY_STATUS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.AUTHORS, ADDITIONAL_DATA_KEY_AUTHORS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.STUDIOS, ADDITIONAL_DATA_KEY_STUDIOS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_RELEASE_DATE);
		ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.RESUME, ADDITIONAL_DATA_KEY_RESUME);
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.ANIME });
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String html = getHelper().downloadPageCache(getSiteUrl());
		
		if (html == null) {
			return result;
		}
		
		List<JetAnimeItem> resultItems = extractAnimeFromHtml(html);
		
		for (JetAnimeItem animeItem : resultItems) {
			String url = getSiteUrl() + animeItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, url.replaceAll("(\\/anime\\/|\\/)", ""));
			String name = animeItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, animeItem.getName(), url, imageUrl, SearchCapability.ANIME).score(score));
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
		
		for (Entry<ResultDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			ResultDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			if (type.equals(ResultDataType.RESUME)) {
				continue;
			}
			
			String extractedData = extractCommonData(dataKey, htmlContainer);
			
			if (extractedData != null) {
				String trimExtractedData = extractedData.trim();
				Object formattedData = trimExtractedData;
				
				if (type.equals(ResultDataType.GENDERS) && trimExtractedData.contains(",")) {
					List<String> strings = new ArrayList<>();
					for (String split : trimExtractedData.split(",")) {
						strings.add(split.trim());
					}
					
					formattedData = strings;
				}
				
				additionals.add(new AdditionalResultData(type, formattedData));
			}
		}
		
		String extractedResumeData = extractResumeData(htmlContainer);
		
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(ResultDataType.RESUME, extractedResumeData.trim()));
		}
		
		return additionals;
	}
	
	/**
	 * Extract all Anime present on the website
	 * 
	 * @param html
	 *            The downloaded html of anypage
	 * @return A list of {@link JetAnimeItem} that you can work with. That contain the full match, the url, and the name
	 */
	public static List<JetAnimeItem> extractAnimeFromHtml(String html) {
		List<JetAnimeItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<option.*?value=\\\"(.*?)\\\"\\>(.*?)\\<\\/option\\>", html);
		
		while (matcher.find()) {
			items.add(new JetAnimeItem(matcher.group(0).trim(), matcher.group(1).trim(), escapeEmailProtection(matcher.group(2).trim())));
		}
		
		return items;
	}
	
	/**
	 * Some name contain "an mail protection", this function will escape it
	 * 
	 * Here is an exemple: <template class="__cf_email__" data-cfemail="<some hex>">[email&#160;protected]</template>
	 * 
	 * More exemple: https://regex101.com/r/e3bosF/1
	 * 
	 * @param name
	 *            Base name, will do nothing if mail protection is not present
	 * @return A escaped name
	 */
	public static String escapeEmailProtection(String name) {
		Matcher matcher = getStaticHelper().regex("\\<(template|span)\\sclass=\\\"__cf_email__\\\"\\sdata-cfemail=\\\"(.*?)\\\".*?(template|span)\\>", name);
		
		if (matcher.find()) {
			return name.replaceAll("\\<(template|span)\\sclass=\\\"__cf_email__\\\"\\sdata-cfemail=\\\"(.*?)\\\".*?(template|span)\\>", CloudflareUtils.decodeEmail(matcher.group(2)));
		}
		
		return name;
	}
	
	/**
	 * On JetAnime, that will extract a container string that is a div in html containing all information about the anime
	 * 
	 * @param html
	 *            The downloaded html of a anime page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		if (html == null || html.isEmpty()) {
			return null;
		}
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass=\\\"clearfix\\\"\\>(.*?)\\<\\/div\\>", html);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return null;
	}
	
	/**
	 * Extract common data on the JetAnime page
	 * 
	 * @param dataKey
	 *            Something like "Name:" or "Status:", a key that will used as a line identifier
	 * @param htmlContainer
	 *            A html container, source of data
	 * @return Some extracted data, null if not found
	 */
	public static String extractCommonData(String dataKey, String htmlContainer) {
		Matcher matcher = getStaticHelper().regex(String.format("\\<p\\>\\<span\\sclass=\\\"bold\\\"\\>%s\\<\\/span\\>[\\s]*(.*?)[\\s]*\\<\\/p\\>", dataKey), htmlContainer);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return null;
	}
	
	/**
	 * Extract the resume from the htmlContainer, for some reason, JetAnime display it a different format
	 * 
	 * @param htmlContainer
	 *            A html container, source of data
	 * @return Extracted resume, null if not found
	 */
	public static String extractResumeData(String htmlContainer) {
		Matcher matcher = getStaticHelper().regex(String.format("\\<span\\sclass=\\\"bold\\\">%s\\<\\/span\\>[\\s]*(.*?)[\\s]*\\z", ADDITIONAL_DATA_KEY_RESUME), htmlContainer);
		
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return null;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class JetAnimeItem extends ResultItem {
		public JetAnimeItem(String match, String url, String name) {
			super(match, url, name);
		}
	}
	
}