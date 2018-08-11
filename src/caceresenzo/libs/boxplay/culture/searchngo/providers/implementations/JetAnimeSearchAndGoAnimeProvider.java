package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
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
		
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.NAME, ADDITIONAL_DATA_KEY_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.ORIGINAL_NAME, ADDITIONAL_DATA_KEY_ORIGINAL_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.ALTERNATIVE_NAME, ADDITIONAL_DATA_KEY_ALTERNATIVE_NAME);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.GENDERS, ADDITIONAL_DATA_KEY_GENDERS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.STATUS, ADDITIONAL_DATA_KEY_STATUS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.AUTHORS, ADDITIONAL_DATA_KEY_AUTHORS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.STUDIOS, ADDITIONAL_DATA_KEY_STUDIOS);
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_RELEASE_DATE);
		// ADDITIONAL_DATA_CORRESPONDANCE.put(ResultDataType.RESUME, ADDITIONAL_DATA_KEY_RESUME); // Not usable in a loop
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
			String imageUrl = String.format(imageUrlFormat, animeItem.getUrl().replaceAll("(\\/anime\\/|\\/)", ""));
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
		
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonData(dataKey, htmlContainer);
			
			if (extractedData != null) {
				String trimExtractedData = extractedData.trim();
				Object formattedData = trimExtractedData;
				
				if (type.equals(AdditionalDataType.GENDERS) && trimExtractedData.contains(",")) {
					List<CategoryResultData> categories = new ArrayList<>();
					
					if (trimExtractedData.contains(",")) {
						for (String split : trimExtractedData.split(",")) {
							categories.add(new CategoryResultData(split));
						}
					} else {
						categories.add(new CategoryResultData(trimExtractedData));
					}
					
					formattedData = categories;
				}
				
				additionals.add(new AdditionalResultData(type, formattedData));
			}
		}
		
		String extractedResumeData = extractResumeData(htmlContainer);
		
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResumeData.trim()));
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
		
		Matcher containerMatcher = getHelper().regex("\\<div\\sid=\\\"collapsecategory[\\d]*\\\"\\sclass=\\\"panel-collapse\\scollapse\\s(in)*\\\"\\srole=\\\"tabpanel\\\"\\saria-labelledby=\\\"category[\\d]*\\\"\\>(.*?)\\<\\/div\\>", html);
		
		while (containerMatcher.find()) {
			String htmlContainer = containerMatcher.group(2);
			
			Matcher itemMatcher = getHelper().regex("\\<a\\sclass=\\\"list-group-item[\\s]*[active]*\\\"\\shref=\\\"(.*?)\\\"\\>[\\s\\t\\n]*(\\<i.*?\\>\\<\\/i\\>)*[\\s]*(.*?)[\\s]*\\<\\/a\\>", htmlContainer);
			
			while (itemMatcher.find()) {
				String url = getSiteUrl() + itemMatcher.group(1);
				String video = itemMatcher.group(3);
				
				additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(url, video)));
			}
		}
		
		return additionals;
	}
	
	@Override
	protected Comparator<AdditionalResultData> getContentComparator() {
		return new Comparator<AdditionalResultData>() {
			@Override
			public int compare(AdditionalResultData o1, AdditionalResultData o2) {
				return 0;
			}
		};
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
		
		return getStaticHelper().extract("\\<div\\sclass=\\\"clearfix\\\"\\>(.*?)\\<\\/div\\>", html);
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
		return getStaticHelper().extract(String.format("\\<p\\>\\<span\\sclass=\\\"bold\\\"\\>%s\\<\\/span\\>[\\s]*(.*?)[\\s]*\\<\\/p\\>", dataKey), htmlContainer);
	}
	
	/**
	 * Extract the resume from the htmlContainer, for some reason, JetAnime display it a different format
	 * 
	 * @param htmlContainer
	 *            A html container, source of data
	 * @return Extracted resume, null if not found
	 */
	public static String extractResumeData(String htmlContainer) {
		return getStaticHelper().extract(String.format("\\<span\\sclass=\\\"bold\\\">%s\\<\\/span\\>[\\s]*(.*?)[\\s]*\\z", ADDITIONAL_DATA_KEY_RESUME), htmlContainer);
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