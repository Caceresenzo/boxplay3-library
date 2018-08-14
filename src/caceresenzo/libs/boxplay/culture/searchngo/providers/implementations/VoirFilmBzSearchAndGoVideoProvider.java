package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.openload.OpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.logger.Logger;

public class VoirFilmBzSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	private final String searchBaseUrl;
	
	public VoirFilmBzSearchAndGoVideoProvider() {
		super("VOIRFILM.bz", "http://www.voirfilm.bz");
		
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
				.asString().getBody(); // ;
		
		if (html == null || searchQuery == null || searchQuery.length() < 4) {
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
		return createEmptyAdditionalResultDataList();
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		
		if (html == null || html.isEmpty()) {
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
	public String extractVideoUrl(VideoItemResultData videoItemResult) {
		return videoItemResult.getUrl();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { OpenloadVideoExtractor.class };
	}
	
	private List<VoirFilmBzItem> extractVideoFromHtml(String html) {
		List<VoirFilmBzItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass=\\\"mov\\\">[\\s\\t\\n]*\\<div\\sclass=\\\"mov-i\\simg-box\\\"\\>[\\s\\t\\n]*\\<img src=\\\"[\\s]*(.*?)[\\s]*\\\".*?\\>[\\s\\t\\n]*\\<div\\sclass=\\\"mov-mask\\sflex-col\\sps-link\\\"\\sdata-link=\\\"[\\s]*(.*?)[\\s]*\\\"\\>\\<span\\sclass=\\\"fa\\sfa-play\\\"\\>\\<\\/span\\>\\<\\/div\\>[\\s\\t\\n]*\\<div\\sclass=\\\"mov-m\\\"\\>\\<b\\>.*?\\<\\/b\\>\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>.*?\\<div\\sclass=\\\"mov-c\\snowrap\\\">[\\s]*(.*?)[\\s]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/div>", html);
		
		while (matcher.find()) {
			String imageUrl = matcher.group(1);
			String url = matcher.group(2);
			String name = matcher.group(3);
			
			items.add(new VoirFilmBzItem(matcher.group(0), url, name, imageUrl));
		}
		
		return items;
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