package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVidozaVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;

public class FullStreamNuSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	private final String searchBaseUrl;
	
	public FullStreamNuSearchAndGoVideoProvider() {
		super("Full-Stream.nu", "https://film.full-stream.nu");
		
		this.searchBaseUrl = getSiteUrl() + "/index.php?do=search";
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
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		/* 43 = Series and 2 = Movies (or inversed) // Adding conbination of the 2 pages (bad i know) */
		String html = makeRequestByCategory(searchQuery, "43") + makeRequestByCategory(searchQuery, "2");
		
		List<FullStreamNuItem> resultItems = extractVideoFromHtml(html);
		
		for (FullStreamNuItem fullStreamNuItem : resultItems) {
			String match = fullStreamNuItem.getMatch();
			String url = fullStreamNuItem.getUrl();
			String imageUrl = fullStreamNuItem.getImageUrl();
			String name = fullStreamNuItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				SearchCapability type = SearchCapability.VIDEO;
				if (match.contains("Series")) {
					type = SearchCapability.SERIES;
				} else if (match.contains("Film")) {
					type = SearchCapability.MOVIE;
				}
				
				workmap.put(url, new SearchAndGoResult(this, fullStreamNuItem.getName(), url, imageUrl, type).score(score));
			}
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
	
	@Override
	public String extractVideoPageUrl(VideoItemResultData videoItemResult) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { OpenloadVideoExtractor.class, GenericVidozaVideoExtractor.class };
	}
	
	private String makeRequestByCategory(String searchQuery, String category) {
		Webb webb = Webb.create();
		webb.setDefaultHeader(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT);
		
		return webb //
				.post(searchBaseUrl) //
				.header("Content-Type", "application/x-www-form-urlencoded") //
				
				.param("do", "search") //
				.param("subaction", "search") //
				.param("search_start", "0") //
				.param("full_search", "1") //
				.param("result_from", "1") //
				.param("story", searchQuery) // ") //
				.param("titleonly", "0") //
				.param("searchuser", "") //
				.param("replyless", "0") //
				.param("replylimit", "0") //
				.param("searchdate", "0") //
				.param("beforeafter", "after") //
				.param("sortby", "date") //
				.param("resorder", "desc") //
				.param("showposts", "0") //
				.param("catlist[]", category) //
				
				.ensureSuccess() //
				.asString().getBody(); // ;;
	}
	
	private List<FullStreamNuItem> extractVideoFromHtml(String html) {
		List<FullStreamNuItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass=\\\"fullstream\\sfullstreaming\\\"\\>[\\s\\t\\n]*\\<img\\ssrc=\\\"[\\s]*(.*?)[\\s]*\\\".*?\\>.*?[\\s\\t\\n]*\\<h3\\sclass=\\\"mov-title\\\">\\<a\\shref=\\\"[\\s]*(.*?)[\\s]*\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>\\<\\/h3\\>.*?\\<a\\shref=\".*?\\\"\\sclass=\\\"fullinfo\\\"\\>Regarder\\<\\/a\\>[\\s\\t\\n]*\\<\\/div\\>", html);
		
		while (matcher.find()) {
			String imageUrl = matcher.group(1);
			String url = matcher.group(2);
			String name = matcher.group(3);
			
			items.add(new FullStreamNuItem(matcher.group(0), url, name, imageUrl));
		}
		
		return items;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class FullStreamNuItem extends ResultItem {
		public FullStreamNuItem(String match, String url, String name, String imageUrl) {
			super(match, url, name, imageUrl);
		}
	}
	
}