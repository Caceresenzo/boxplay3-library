package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.IHentaiVideoContentProvider;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericOpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.RatingResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.FakeProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.iterator.ByteArrayIterator;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.parse.ParseUtils;
import caceresenzo.libs.string.StringUtils;

@SuppressWarnings("unused")
public class AdkamiSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider, IHentaiVideoContentProvider {
	
	/* Provider Settings: Use full page instead of adkami's search bar, will be much longer but more precise (name only) */
	public static final boolean SEARCH_USING_FULL_PAGE = false;
	
	/* Constants */
	public static final String ADDITIONAL_DATA_KEY_RELEASE_DATE = "Date: ";
	public static final String ADDITIONAL_DATA_KEY_AUTHOR = "Auteur: ";
	public static final String ADDITIONAL_DATA_KEY_STUDIO = "Studio: ";
	
	/* Variables */
	private final String searchUrlFormat, imageUrlFormat, imageMiniUrlFormat;
	private boolean hentaiAllowed = false;
	
	/* Constructor */
	public AdkamiSearchAndGoVideoProvider() {
		super("Adkami", "https://www.adkami.com");
		
		this.searchUrlFormat = getSiteUrl() + "/video?search=%s&n=&t=%s&s=&g=&order=0&d=";
		this.imageUrlFormat = "https://image.adkami.com/%s.jpg";
		this.imageMiniUrlFormat = "https://image.adkami.com/mini/%s.jpg";
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return SEARCH_USING_FULL_PAGE;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.ANIME, SearchCapability.MOVIE, SearchCapability.SERIES, SearchCapability.DRAMA, SearchCapability.HENTAI, SearchCapability.VIDEO });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		if (SEARCH_USING_FULL_PAGE) {
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/anime", SearchCapability.ANIME);
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/drama", SearchCapability.DRAMA);
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/serie", SearchCapability.SERIES);
			
			if (hentaiAllowed) {
				extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/hentai", SearchCapability.HENTAI);
			}
		} else {
			String encodedSearchQuery = URLEncoder.encode(searchQuery, "UTF-8");
			
			extractEverythingFromUrl(workmap, searchQuery, String.format(searchUrlFormat, encodedSearchQuery, 0), SearchCapability.ANIME); // Anime
			extractEverythingFromUrl(workmap, searchQuery, String.format(searchUrlFormat, encodedSearchQuery, 1), SearchCapability.SERIES); // Series
			extractEverythingFromUrl(workmap, searchQuery, String.format(searchUrlFormat, encodedSearchQuery, 5), SearchCapability.DRAMA); // Drama
			
			if (hentaiAllowed) {
				extractEverythingFromUrl(workmap, searchQuery, String.format(searchUrlFormat, encodedSearchQuery, 4), SearchCapability.HENTAI); // Hentai
			}
		}
		
		return workmap;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (html == null || html.isEmpty() || htmlContainer == null || htmlContainer.isEmpty()) {
			return additionals;
		}
		
		/* Alternative Name */
		String extractedAlternativeNameData = getHelper().extract("\\<h4\\sitemprop=\\\"alternateName\\\">(.*?)\\<\\/h4\\>", htmlContainer);
		if (extractedAlternativeNameData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.ALTERNATIVE_NAME, extractedAlternativeNameData));
		}
		
		/* Rating */
		String extractedRatingData = getHelper().extract("\\<div\\sclass=\\\"star\\\">(.*?)<\\/div>", htmlContainer);
		if (extractedRatingData != null) {
			int maxRating = ParseUtils.parseInt(getHelper().extract("\\<meta\\sitemprop=\\\"bestRating\\\"\\scontent=\\\"([\\d]*)\\\"\\>", extractedRatingData), NO_VALUE);
			float rating = ParseUtils.parseFloat(getHelper().extract("\\<span\\sitemprop=\\\"ratingValue\\\"\\>([\\d.]*)\\<\\/span\\>", extractedRatingData), NO_VALUE);
			int votesCount = ParseUtils.parseInt(getHelper().extract("\\<span\\sitemprop=\\\"ratingCount\\\"\\>([\\d]*)\\<\\/span\\>", extractedRatingData), NO_VALUE);
			
			if (maxRating != NO_VALUE && rating != NO_VALUE && votesCount != NO_VALUE) {
				additionals.add(new AdditionalResultData(AdditionalDataType.RATING, new RatingResultData(rating, maxRating, votesCount)));
			}
		}
		
		/* Description */
		String extractedResumeData = getHelper().extract("\\<p\\sclass=\\\"description\\sjustify\\\"\\sitemprop=\\\"description\\\"\\>[\\s\\t\\n]*\\<strong\\>(.*?)\\<\\/strong\\>[\\s\\t\\n]*\\<\\/p\\>", htmlContainer);
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, AdditionalResultData.escapeHtmlChar(extractedResumeData)));
		}
		
		/* Release Date */
		String extractedReleaseDateData = getHelper().extract(String.format("\\<p\\>%s\\<b\\sclass=\\\"date\\\"\\sdata-time=\\\"[\\s]*([\\d]*)[\\s]*\\\".*?\\\"\\>.*?\\<\\/b\\>\\<\\/p\\>", ADDITIONAL_DATA_KEY_RELEASE_DATE), htmlContainer);
		if (extractedReleaseDateData != null) {
			long date = ParseUtils.parseLong(extractedReleaseDateData, NO_VALUE);
			
			if (date != NO_VALUE) {
				additionals.add(new AdditionalResultData(AdditionalDataType.RELEASE_DATE, new SimpleDateFormat("yyyy").format(new Date(date * 1000))));
			}
		}
		
		/* Author */
		String extractedAuthorData = getHelper().extract(String.format("\\<p\\>%s\\<b\\sitemprop=\\\"author\\\"\\>(.*?)\\<\\/b\\>\\<\\/p\\>", ADDITIONAL_DATA_KEY_AUTHOR), htmlContainer);
		if (extractedAuthorData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.AUTHORS, extractedAuthorData));
		}
		
		/* Studio */
		String extractedStudioData = getHelper().extract(String.format("\\<p\\>%s\\<b\\sitemprop=\\\"publisher\\\"\\>(.*?)\\<\\/b\\>\\<\\/p\\>", ADDITIONAL_DATA_KEY_STUDIO), htmlContainer);
		if (extractedStudioData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.STUDIOS, extractedStudioData));
		}
		
		/* Genders */
		Matcher gendersMatcher = getHelper().regex("\\<li\\sclass=\\\"col-12\\scol-m.*?\\\".*?\\>\\<a\\shref=\\\"(.*?)\\\"\\>\\<span itemprop=\\\"genre\\\">(.*?)\\<\\/span\\>", htmlContainer);
		List<CategoryResultData> categories = new ArrayList<>();
		while (gendersMatcher.find()) {
			String url = gendersMatcher.group(1);
			String gender = gendersMatcher.group(2);
			
			categories.add(new CategoryResultData(url, gender));
		}
		if (!categories.isEmpty()) {
			additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractEpisodeContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		Matcher rowMatcher = getHelper().regex("\\<li.*?\\>(.*?)\\<\\/li\\>", htmlContainer);
		
		String actualSeason = null;
		while (rowMatcher.find()) {
			String match = rowMatcher.group(0);
			String content = rowMatcher.group(1);
			
			if (match.matches("\\<li\\sclass=\\\"saison\\\"[\\s]*\\>(.*?)\\<\\/li\\>")) {
				actualSeason = content;
			} else {
				Matcher episodeMatcher = getHelper().regex("\\<a\\shref=\\\"(.*?)\\\".*?>(.*?)\\<\\/a\\>", content);
				
				if (episodeMatcher.find()) {
					String url = episodeMatcher.group(1);
					String name = episodeMatcher.group(2);
					
					String formattedName = String.format("%s - %s", actualSeason, name).toUpperCase();
					
					additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, url, formattedName)));
				}
			}
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { GenericOpenloadVideoExtractor.class };
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		List<String> urls = new ArrayList<>();
		
		String html = getHelper().downloadPageCache(videoItemResult.getUrl());
		
		if (!StringUtils.validate(html)) {
			return new String[] { null };
		}
		
		AdkamiIframeDecoderSandbox sandbox = new AdkamiIframeDecoderSandbox();
		
		Matcher iframeMatcher = getHelper().regex("\\<iframe.*?data-src=\\\"[\\s]*(.*?)[\\s]*\\\".*?\\>\\<\\/iframe\\>", html);
		
		while (iframeMatcher.find()) {
			String baseUrl = iframeMatcher.group(1);
			
			if (baseUrl != null) {
				String sandboxedUrl = sandbox.execute(baseUrl);
				
				if (sandboxedUrl != null) {
					if (sandboxedUrl.startsWith("//")) {
						sandboxedUrl = "https:" + sandboxedUrl;
					}
					
					urls.add(sandboxedUrl);
				}
			}
		}
		
		String[] urlsArray = new String[urls.size()];
		return urls.toArray(urlsArray);
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
	}
	
	@Override
	public void allowHentai(boolean allow) {
		this.hentaiAllowed = allow;
	}
	
	public void extractEverythingFromUrl(Map<String, SearchAndGoResult> actualWorkmap, String searchQuery, String targetUrl, SearchCapability type) {
		String html = getHelper().downloadPageCache(targetUrl);
		
		if (!StringUtils.validate(html)) {
			return;
		}
		
		List<AdkamiItem> resultItems = extractVideoFromHtml(html);
		
		for (AdkamiItem adkamiItem : resultItems) {
			String url = adkamiItem.getUrl();
			String imageUrl = adkamiItem.getImageUrl().replace("mini/", "");
			String name = adkamiItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				actualWorkmap.put(url, new SearchAndGoResult(this, adkamiItem.getName(), url, imageUrl, type).score(score));
			}
		}
	}
	
	public static List<AdkamiItem> extractVideoFromHtml(String html) {
		List<AdkamiItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass=\\\"video-item-list\\\"\\>[\\s\\t\\n]*\\<span\\sclass=\\\"age[\\d]*\\\"\\stitle=\\\"[\\d]*\\\".*?\\>\\<\\/span\\>.*?\\<a\\shref=\\\"(.*?)\\\"\\>[\\s\\t\\n]*\\<img.*?(data-original|src)=\\\"(.*?)\\\".*?\\>[\\s\\t\\n]*\\<\\/a\\>[\\s\\t\\n]*\\<span class=\\\"top\\\">[\\s\\t\\n]*\\<a\\shref=\\\".*?\\\"\\>[\\s\\t\\n]*\\<span\\sclass=\\\"title\\\">(.*?)\\<\\/span\\>[\\s\\t\\n]*\\<\\/a\\>.*?[\\s\\t\\n]*\\<\\/div\\>", html);
		
		while (matcher.find()) {
			String imageUrl = matcher.group(3);
			String url = matcher.group(1);
			String name = matcher.group(4);
			
			items.add(new AdkamiItem(matcher.group(0), url, name, imageUrl));
		}
		
		return items;
	}
	
	/**
	 * @param html
	 *            The downloaded html of a video page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<div\\sclass=\\\"col-12\\sbloc\\sbloc-left\\sfiche-info\\\"\\>[\\s\\n\\t]*\\<h3\\>Description\\<\\/h3\\>(.*?)\\<\\/ul\\>[\\s\\n\\t]*\\<\\/div\\>", html, 0);
	}
	
	/**
	 * @param html
	 *            The downloaded html of a video page
	 * @return A string containing all episode in html
	 */
	public static String extractEpisodeContainer(String html) {
		return getStaticHelper().extract("\\<div\\sid=\\\"row-nav-episode\\\"\\sclass=\\\"dropdown-content\\\"\\>(.*?)\\<\\/div\\>", html, 0);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class AdkamiItem extends ResultItem {
		public AdkamiItem(String match, String url, String name, String imageUrl) {
			super(match, url, name, imageUrl);
		}
	}
	
	public static class AdkamiIframeDecoderSandbox implements Sandbox<String, String> {
		@Override
		public String execute(String baseUrl) {
			String[] split = baseUrl.split("https://www.youtube.com/embed/");
			
			if (split.length < 2) {
				return null;
			}
			
			baseUrl = split[1];
			byte[] decodedBytes = Base64.decodeFast(baseUrl);
			String result = "", key = "ETEfazefzeaZa13MnZEe";
			int index = 0;
			
			try {
				ByteArrayIterator iterator = new ByteArrayIterator(decodedBytes);
				while (iterator.hasNext()) {
					// int nextByte = Byte.toUnsignedInt(iterator.next()); // Too advanced for older phone
					int nextByte = iterator.next() & 0xFF; // Basicly un-sign actual byte value, thanks stackoverflow
					result += (char) ((175 ^ nextByte) - (int) key.charAt(index));
					index = index > key.length() - 2 ? 0 : index + 1;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				return null;
			}
			
			return result;
		}
	}
	
}