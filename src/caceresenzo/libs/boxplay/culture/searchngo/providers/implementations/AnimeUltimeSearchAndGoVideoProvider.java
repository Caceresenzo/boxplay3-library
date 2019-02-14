package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericAnimeUltimateVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.Subscribable;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations.SimpleItemComparatorSubscriber;
import caceresenzo.libs.http.client.webb.Response;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbUtils;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

@SuppressWarnings("unused")
public class AnimeUltimeSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	/* Variables */
	private final String searchPostUrl;
	
	/* Constructor */
	public AnimeUltimeSearchAndGoVideoProvider() {
		super("Anime-Ultime", "http://www.anime-ultime.net");
		
		searchPostUrl = getSiteUrl() + "/search-0-1";
	}
	
	@Override
	public String getWorkingCharset() {
		return CHARSET_LATIN_1;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return ProviderSearchCapability.fromArray( //
				SearchCapability.ANIME, //
				SearchCapability.DRAMA, //
				/* SearchCapability.OST, */
				SearchCapability.TOKUSATSU //
		);
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return false;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String html = Webb.create().post(searchPostUrl) //
				.chromeUserAgent() //
				.param("search", searchQuery) //
				.asString() //
				.getBody(); //
		
		String htmlContainer = extractMainSearchResultHtmlContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return result;
		}
		
		for (SearchCapability capability : new SearchCapability[] { SearchCapability.ANIME, SearchCapability.DRAMA, /* SearchCapability.OST, */ SearchCapability.TOKUSATSU }) {
			List<AnimeUltimeItem> resultItems = extractItemsFromHtmlByType(htmlContainer, capability);
			
			for (AnimeUltimeItem item : resultItems) {
				String url = getSiteUrl() + "/" + item.getUrl();
				String name = HtmlCommonExtractor.escapeUnicode(item.getName().replaceAll("<[\\/]*b>", ""));
				String imageUrl = getSiteUrl() + "/" + item.getImageUrl();
				String type = item.getVideoType();
				
				int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
				if (score != 0) {
					result.put(url, new SearchAndGoResult(this, name, url, imageUrl, capability).score(score).describe(type));
				}
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String mainHtmlContainer = extractMainPageHtmlContainer(html);
		String itemInformationHtmlContainer = extractItemInformationContainer(mainHtmlContainer);
		
		if (!StringUtils.validate(html, mainHtmlContainer, itemInformationHtmlContainer)) {
			return additionals;
		}
		
		additionals.add(new AdditionalResultData(AdditionalDataType.SIMPLE_HTML, itemInformationHtmlContainer));
		
		return additionals;
	}
	
	public static String extractResume(String[] itemInformationHtmlContainerArray) {
		String goodPart = null;
		
		for (String data : itemInformationHtmlContainerArray) {
			if (data != null && (data.contains("Synopsis") || !data.contains("<span "))) {
				goodPart = data;
				break;
			}
		}
		
		if (!StringUtils.validate(goodPart)) {
			return null;
		}
		
		return goodPart.replaceAll("\\<.*?[\\/]*.*?\\>", "").replaceAll("Synopsis[\\s]*[\\:]*", "").replace("\n", " ").trim();
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String mainHtmlContainer = extractMainPageHtmlContainer(html);
		
		if (!StringUtils.validate(html, mainHtmlContainer)) {
			return additionals;
		}
		
		Matcher containerMatcher = getHelper().regex("\\<div\\sid\\=\\\"table\\\"\\>[\\s\\t\\n]*\\<table.*?\\>(.*?)\\<\\/table\\>[\\s\\t\\n]*\\<\\/div\\>", mainHtmlContainer);
		while (containerMatcher.find()) {
			String htmlContainer = containerMatcher.group(1);
			
			Matcher rowMatcher = getHelper().regex("\\<tr\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<td.*?\\>(.*?)\\<\\/td\\>[\\s\\n\\t]*\\<\\/tr>", htmlContainer);
			while (rowMatcher.find()) {
				String date = rowMatcher.group(2);
				String video = rowMatcher.group(3);
				String fansub = rowMatcher.group(4);
				String uploader = rowMatcher.group(5);
				String links = rowMatcher.group(6);
				
				Matcher streamLinkMatcher = getHelper().regex("[\\s]\\<a.*?href\\=\\\"(.*?)\\\".*?\\>.*?(?:Stream).*?<\\/a>", links);
				if (streamLinkMatcher.find()) {
					String pageUrl = getSiteUrl() + "/" + streamLinkMatcher.group(1);
					String extractedFansub = getHelper().extractStringFromHtml("a", fansub);
					
					String betterVideoName = null;
					if (video.toUpperCase().contains(result.getName().toUpperCase())) {
						betterVideoName = video.toUpperCase().replace(result.getName().toUpperCase(), "");
						
						if (!StringUtils.validate(betterVideoName)) { /* In case of we have removed everything... */
							betterVideoName = null;
						}
					}
					
					additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, pageUrl, String.format("[%s] - %s", extractedFansub, betterVideoName != null ? betterVideoName : video))));
				}
				
			}
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
		return new Class[] { GenericAnimeUltimateVideoExtractor.class };
	}
	
	public static List<AnimeUltimeItem> extractItemsFromHtmlByType(String mainSearchResultHtmlContainer, SearchCapability type) {
		List<AnimeUltimeItem> items = new ArrayList<>();
		
		String typeHtmlContainer = extractSearchResultTypeHtmlContainter(mainSearchResultHtmlContainer, type);
		
		if (!StringUtils.validate(typeHtmlContainer)) {
			return items;
		}
		
		Matcher matcher = getStaticHelper().regex("\\<tr\\>.*?\\<a\\shref\\=\\\"(.*?)\\\".*?\\\".*?(?:montre\\().*?src\\=.*?img\\=(.*?)[\\s]*\\/>.*?>(.*?)\\<\\/a\\>.*?\\<\\/td>[\\s\\t\\n]*\\<td.*?\\\"\\>(.*?)\\<\\/td\\>.*?\\<\\/tr\\>", typeHtmlContainer);
		
		while (matcher.find()) {
			String fullMatch = matcher.group(0);
			String pageUrl = matcher.group(1).trim();
			String name = matcher.group(3).trim();
			String imageUrl = matcher.group(2).trim();
			String videoType = matcher.group(4).trim();
			
			items.add(new AnimeUltimeItem(fullMatch, pageUrl, name, imageUrl, videoType));
		}
		
		return items;
	}
	
	public static String extractItemInformationContainer(String mainSearchResultHtmlContainer) {
		if (!StringUtils.validate(mainSearchResultHtmlContainer)) {
			return null;
		}
		
		return getStaticHelper().extract("\\<img.*?\\/\\>\\<br[\\s]*\\/\\>\\<br[\\s]*\\/\\>\\<br[\\s]*\\/\\>[\\s\\t\\n]*(.*?)[\\s\\t\\n]*\\<br[\\s]*\\/\\>\\<br[\\s]*\\/\\>\\<br[\\s]*\\/\\>[\\s\\t\\n]*\\<div\\sid\\=\\\"table\\\"\\>", mainSearchResultHtmlContainer);
	}
	
	public static String extractSearchResultTypeHtmlContainter(String mainSearchResultHtmlContainer, SearchCapability type) {
		if (!StringUtils.validate(mainSearchResultHtmlContainer)) {
			return null;
		}
		
		return getStaticHelper().extract(String.format("\\<div\\sclass\\=\\\"principal\\\"\\>[\\s\\t\\n]*\\<div\\sclass\\=\\\"title\\\"\\>.*?[\\s](?i)%s(?-i)[\\s].*?\\<\\/div\\>\\<br[\\s]*\\/\\>[\\s\\t\\n]*\\<table.*?\\>(.*?)\\<\\/table>[\\s\\t\\n]*\\<\\/div\\>", type.toString()), mainSearchResultHtmlContainer);
	}
	
	public static String extractMainSearchResultHtmlContainer(String html) {
		if (!StringUtils.validate(html)) {
			return null;
		}
		
		return getStaticHelper().extract("\\<div\\sid\\=\\\"contain_main\\\"\\>[\\s\\t\\n]*\\<div\\sid\\=\\\"main\\\"\\>[\\s\\t\\n]*\\<div\\sclass\\=\\\"title\\\"\\>.*?\\<\\/div\\>(.*?)[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<div\\sid\\=\\\"footer\\\"\\>", html);
	}
	
	public static String extractMainPageHtmlContainer(String html) {
		if (!StringUtils.validate(html)) {
			return null;
		}
		
		return getStaticHelper().extract("\\<div.*?class\\=\\\"principal_contain[\\s]*\\\".*?\\>(.*?)\\<div\\sid\\=\\\"footer[\\s]*\\\"\\>", html);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class AnimeUltimeItem extends ResultItem {
		private String videoType;
		
		public AnimeUltimeItem(String match, String url, String name, String imageUrl, String videoType) {
			super(match, url, name, imageUrl);
			
			this.videoType = videoType;
		}
		
		public String getVideoType() {
			return videoType;
		}
	}
	
}