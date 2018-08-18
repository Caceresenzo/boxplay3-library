package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.FakeProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class AdkamiSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	public static final boolean SEARCH_USING_FULL_PAGE = false;
	
	private final String searchUrlFormat, imageUrlFormat, imageMiniUrlFormat;
	
	public AdkamiSearchAndGoVideoProvider() {
		super("Adkami", "https://www.adkami.com");
		
		this.searchUrlFormat = getSiteUrl() + "/video?search=%s";
		this.imageUrlFormat = "https://image.adkami.com/%s.jpg";
		this.imageMiniUrlFormat = "https://image.adkami.com/mini/%s.jpg";
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return SEARCH_USING_FULL_PAGE;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.ANIME, SearchCapability.MOVIE, SearchCapability.SERIES, SearchCapability.HENTAI, SearchCapability.VIDEO });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		if (SEARCH_USING_FULL_PAGE) {
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/anime", SearchCapability.ANIME);
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/drama", SearchCapability.VIDEO);
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/serie", SearchCapability.SERIES);
			extractEverythingFromUrl(workmap, searchQuery, "https://www.adkami.com/hentai", SearchCapability.HENTAI);
		} else {
			extractEverythingFromUrl(workmap, searchQuery, String.format(searchUrlFormat, URLEncoder.encode(searchQuery, "UTF-8")), SearchCapability.VIDEO);
		}
		
		return workmap;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		return createEmptyAdditionalResultDataList();
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		return createEmptyAdditionalResultDataList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { OpenloadVideoExtractor.class };
	}
	
	@Override
	public String extractVideoPageUrl(VideoItemResultData videoItemResult) {
		return null;
	}
	
	public void extractEverythingFromUrl(Map<String, SearchAndGoResult> actualWorkmap, String searchQuery, String targetUrl, SearchCapability type) {
		String html = FakeProvider.getFakeProvider().getHelper().downloadPageCache(targetUrl); // Using a fake provider
		
		if (html == null) {
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
	
	private List<AdkamiItem> extractVideoFromHtml(String html) {
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
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class AdkamiItem extends ResultItem {
		public AdkamiItem(String match, String url, String name, String imageUrl) {
			super(match, url, name, imageUrl);
		}
	}
	
}