package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.Subscribable;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations.SimpleItemComparatorSubscriber;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.reversing.cloudflare.CloudflareUtils;
import caceresenzo.libs.string.StringUtils;

@SuppressWarnings("unused")
public class JetAnimeSearchAndGoAnimeProvider extends SearchAndGoProvider implements IVideoContentProvider, Subscribable {
	
	/* Constants */
	public static final String ADDITIONAL_DATA_KEY_NAME = "Nom:";
	public static final String ADDITIONAL_DATA_KEY_ORIGINAL_NAME = "Nom original:";
	public static final String ADDITIONAL_DATA_KEY_ALTERNATIVE_NAME = "Nom Alternatif:";
	public static final String ADDITIONAL_DATA_KEY_GENDERS = "Genre\\(s\\):";
	public static final String ADDITIONAL_DATA_KEY_STATUS = "Statut:";
	public static final String ADDITIONAL_DATA_KEY_AUTHORS = "Auteur\\(s\\):";
	public static final String ADDITIONAL_DATA_KEY_STUDIOS = "Studio\\(s\\):";
	public static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Date de Sortie:";
	public static final String ADDITIONAL_DATA_KEY_RESUME = "Synopsis:";
	
	/* Variables */
	private final String imageUrlFormat, rssUrlFormat;
	
	/* Constructor */
	public JetAnimeSearchAndGoAnimeProvider() {
		super("JetAnime", "https://www.jetanime.co");
		
		imageUrlFormat = getSiteUrl() + "/assets/imgs/%s.jpg";
		rssUrlFormat = getSiteUrl() + "/rss/%s/";
		
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
		return ProviderSearchCapability.fromArray( //
				SearchCapability.ANIME //
		);
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String html = getHelper().downloadPageCache(getSiteUrl());
		
		if (!StringUtils.validate(html)) {
			return result;
		}
		
		List<JetAnimeItem> resultItems = extractAnimeFromHtml(html);
		
		for (JetAnimeItem animeItem : resultItems) {
			String slug = animeItem.getUrl().replaceAll("(\\/anime\\/|\\/)", "");
			
			String url = getSiteUrl() + animeItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, slug);
			String name = animeItem.getName();
			// String subscriptionUrl = String.format(rssUrlFormat, slug);
			String subscriptionUrl = url;
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, animeItem.getName(), url, imageUrl, SearchCapability.ANIME) //
						.score(score) //
						.subscribableAt(subscriptionUrl) //
				);
			}
		}
		
		return result;
	}
	
	@Override
	public boolean isAdvancedDownloaderNeeded() {
		return true;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonData(dataKey, htmlContainer);
			if (extractedData != null) {
				String trimExtractedData = extractedData.trim();
				Object formattedData = trimExtractedData;
				
				if (type.equals(AdditionalDataType.GENDERS)) {
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
		
		if (!StringUtils.validate(html)) {
			return additionals;
		}
		
		Matcher containerMatcher = getHelper().regex("\\<div\\sid=\\\"collapsecategory[\\d]*\\\"\\sclass=\\\"panel-collapse\\scollapse\\s(in)*\\\"\\srole=\\\"tabpanel\\\"\\saria-labelledby=\\\"category[\\d]*\\\"\\>(.*?)\\<\\/div\\>", html);
		
		while (containerMatcher.find()) {
			String htmlContainer = containerMatcher.group(2);
			
			Matcher itemMatcher = getHelper().regex("\\<a\\sclass=\\\"list-group-item[\\s]*[active]*\\\"\\shref=\\\"(.*?)\\\"\\>[\\s\\t\\n]*(\\<i.*?\\>\\<\\/i\\>)*[\\s]*(.*?)[\\s]*\\<\\/a\\>", htmlContainer);
			
			while (itemMatcher.find()) {
				String url = getSiteUrl() + itemMatcher.group(1);
				String video = itemMatcher.group(3);
				
				additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, url, video)));
			}
		}
		
		return additionals;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		
		String html = getHelper().downloadPageCache(videoItemResult.getUrl());
		String formContainer = getHelper().extract("\\<form\\>[\\s]*(\\<input\\stype\\=\\\"hidden\\\".*?)\\<\\/form\\>", html);
		
		if (!StringUtils.validate(html, formContainer)) {
			return new String[] { null };
		}
		
		List<String> parts = new ArrayList<>();
		
		Matcher partMatcher = getHelper().regex("\\<input\\stype\\=\\\"hidden\\\"\\svalue\\=\\\"(.*?)\\\"[\\s]*\\/\\>", formContainer);
		
		while (partMatcher.find()) {
			String part = partMatcher.group(1);
			
			if (!StringUtils.validate(part)) {
				/* Don't allow any error, if one is bad, all will be bad */
				
				return new String[] { null };
			}
			
			parts.add(part);
		}
		
		return new String[] { HtmlCommonExtractor.extractIframeUrlFromHtml(new JetAnimeIframeDecoderSandbox().execute(parts)) };
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return false;
	}
	
	@Override
	public Subscriber createSubscriber() {
		return new SimpleItemComparatorSubscriber(true);
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
	
	/**
	 * This sandbox allow url decoding from base64 encoded part available on JetAnime's site
	 * 
	 * @author Enzo CACERE
	 */
	public static class JetAnimeIframeDecoderSandbox implements Sandbox<String, List<String>> {
		
		@Override
		public String execute(List<String> parts) {
			Collections.reverse(parts);
			
			StringBuilder bigPartBuilder = new StringBuilder();
			
			for (int index = 0; index < parts.size(); index++) {
				String part = parts.get(index);
				
				if (5 >= index) {
					bigPartBuilder.append(new StringBuilder(part).reverse().toString());
				} else {
					bigPartBuilder.append(part);
				}
			}
			
			char[] characters = bigPartBuilder.toString().toCharArray();
			
			for (int i = 0; i < characters.length; i++) {
				char character = characters[i];
				
				if (Character.isUpperCase(character)) {
					characters[i] = Character.toLowerCase(character);
				} else {
					characters[i] = Character.toUpperCase(character);
				}
			}
			
			StringBuilder encodedContentBuilder = new StringBuilder();
			
			for (char character : characters) {
				encodedContentBuilder.append(character);
			}
			
			return new String(Base64.decode(encodedContentBuilder.toString()));
		}
	}
	
}