package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericOpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.UrlResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.completed.CompletedVideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.string.StringUtils;

/**
 * Hdss.to video provider<br>
 * <br>
 * Video extraction regex: <a href="https://regex101.com/r/2wf6RM/1">regex101.com/r/2wf6RM/1</a><br>
 * Video (searched) description regex: <a href="https://regex101.com/r/pBUoOy/1">regex101.com/r/pBUoOy/1</a><br>
 * 
 * @author Enzo CACERES
 */
public class HdssToSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	/* Variables */
	private final String searchUrlFormat;
	
	/* Constructor */
	public HdssToSearchAndGoVideoProvider() {
		super("HDSS.to", "https://hdss.to");
		
		this.searchUrlFormat = getSiteUrl() + "/search/%s/";
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return false;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MOVIE, SearchCapability.VIDEO });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		String html = getHelper().downloadPageCache(String.format(searchUrlFormat, getHelper().encodeUrl(searchQuery.replace(" ", "+"))));
		
		if (!StringUtils.validate(html)) {
			return workmap;
		}
		
		List<HdssItem> resultItems = extractVideoFromHtml(html);
		
		for (HdssItem item : resultItems) {
			String url = item.getUrl();
			String imageUrl = item.getImageUrl();
			String name = item.getName();
			
			String moreContent = item.getMoreContent();
			StringBuilder descriptionBuilder = new StringBuilder();
			
			/* Better visiblity enclosure */
			{
				String resume = getHelper().extract("\\<p\\>(?:Synopsis[\\s]*)*(.*?)\\<\\/p\\>", moreContent);
				if (resume != null) {
					descriptionBuilder.append(resume);
				}
			}
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				SearchAndGoResult searchAndGoResult = new SearchAndGoResult(this, item.getName(), url, imageUrl, SearchCapability.MOVIE).score(score);
				
				if (StringUtils.validate(descriptionBuilder.toString())) {
					searchAndGoResult.describe(descriptionBuilder.toString());
				}
				
				workmap.put(url, searchAndGoResult);
			}
		}
		
		return workmap;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		/* Name */
		String extractedName = getHelper().extract("\\<h1\\sclass\\=\\\"Title\\\"\\>(.*?)\\<\\/h1\\>", htmlContainer);
		if (extractedName != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.NAME, extractedName));
		}
		
		/* Release Date */
		String extractedReleaseDate = getHelper().extract("\\<span\\sclass\\=\\\"Date\\\">(.*?)\\<\\/span\\>", htmlContainer);
		if (extractedReleaseDate != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RELEASE_DATE, extractedReleaseDate));
		}
		
		/* Quality */
		String extractedQuality = getHelper().extract("\\<span\\sclass\\=\\\"Qlty\\\">(.*?)\\<\\/span\\>", htmlContainer);
		if (extractedQuality != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.QUALITY, extractedQuality));
		}
		
		/* Duration */
		String extractedDuration = getHelper().extract("\\<span\\sclass\\=\\\"Time\\\">(.*?)\\<\\/span\\>", htmlContainer);
		if (extractedDuration != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.DURATION, extractedDuration));
		}
		
		/* Views */
		String extractedViews = getHelper().extract("\\<span\\sclass\\=\\\"Views.*?\\\"\\>(.*?)(?:[\\s]*views)\\<\\/span\\>", htmlContainer);
		if (extractedViews != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.VIEWS, extractedViews.replace(" ", "")));
		}
		
		/* Description Container */
		String descriptionHtmlContainer = getHelper().extract("\\<div\\sclass\\=\\\"Description\\\"\\>(.*?)\\<\\/div\\>[\\s]*\\<footer\\>", htmlContainer);
		if (descriptionHtmlContainer != null) {
			/* Resume */
			String extractedResume = getHelper().extract("\\<p\\>(?:Synopsis[\\s]*)*(.*?)\\<\\/p\\>", descriptionHtmlContainer);
			if (extractedResume != null) {
				additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume.replaceAll("\\<[\\/]*a.*?\\>", "")));
			}
			
			/* Genders */
			String extractedGenders = getHelper().extract("\\<p\\sclass\\=\\\"Genre\\\"\\>\\<span\\>.*?\\<\\/span\\>[\\s]*(.*?)\\<\\/p\\>", descriptionHtmlContainer);
			if (extractedGenders != null) {
				Matcher gendersMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, extractedGenders);
				
				List<CategoryResultData> categories = new ArrayList<>();
				
				while (gendersMatcher.find()) {
					String url = gendersMatcher.group(1);
					String name = gendersMatcher.group(2);
					
					categories.add(new CategoryResultData(url, name));
				}
				
				if (!categories.isEmpty()) {
					additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
				}
			}
			
			/* Directors */
			String extractedDirectors = getHelper().extract("\\<p\\sclass\\=\\\"Genre\\\"\\>\\<span\\>.*?\\<\\/span\\>[\\s]*(.*?)\\<\\/p\\>", descriptionHtmlContainer);
			if (extractedDirectors != null) {
				Matcher directorsMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, extractedDirectors);
				
				List<UrlResultData> directors = new ArrayList<>();
				
				while (directorsMatcher.find()) {
					String url = directorsMatcher.group(1);
					String name = directorsMatcher.group(2);
					
					directors.add(new UrlResultData(url, name));
				}
				
				if (!directors.isEmpty()) {
					additionals.add(new AdditionalResultData(AdditionalDataType.DIRECTOR, directors));
				}
			}
			
			/* Actors */
			String extractedActors = getHelper().extract("\\<section\\sclass\\=\\\"CastCn\\\"\\>.*?\\<ul.*?\\>(.*?)\\<\\/ul\\>.*?\\<\\/section\\>", descriptionHtmlContainer);
			if (extractedActors != null) {
				Matcher actorsItemMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LIST_EXTRACTION_REGEX, extractedActors);
				
				List<UrlResultData> actors = new ArrayList<>();
				
				while (actorsItemMatcher.find()) {
					String itemContent = actorsItemMatcher.group(1);
					
					Matcher actorMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, itemContent);
					
					if (actorMatcher.find()) {
						String url = actorMatcher.group(1);
						String name = actorMatcher.group(2);
						
						actors.add(new UrlResultData(url, name));
					}
				}
				
				if (!actors.isEmpty()) {
					additionals.add(new AdditionalResultData(AdditionalDataType.ACTORS, actors));
				}
			}
			
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractVideoOptionsContainer(html);
		
		if (!StringUtils.validate(html, htmlContainer)) {
			return additionals;
		}
		
		Map<String, String> videoOptions = new HashMap<>();
		
		Matcher videoOptionsMatcher = getHelper().regex("\\<div\\sid\\=\\\"(.*?)\\\"\\sclass\\=\\\"Video.*?\\\"\\>(.*?)\\<\\/div\\>", htmlContainer);
		
		while (videoOptionsMatcher.find()) {
			String option = videoOptionsMatcher.group(1);
			String playerUrl = HtmlCommonExtractor.extractIframeUrlFromHtml(new String(Base64.decode(videoOptionsMatcher.group(2))));
			
			videoOptions.put(option, playerUrl);
		}
		
		if (videoOptions.isEmpty()) {
			return additionals;
		}
		
		String extractedOptionsNameHtmlContainer = getHelper().extract("\\<ul\\sclass\\=\\\"ListOptions\\\"\\>(.*?)\\<\\/ul\\>", htmlContainer);
		if (extractedOptionsNameHtmlContainer != null) {
			Matcher optionsNameMatcher = getHelper().regex("\\<li\\sclass\\=\\\"OptionBx.*?\\\"\\sdata-VidOpt\\=\\\"(.*?)\\\"\\>(.*?)\\<\\/li\\>", extractedOptionsNameHtmlContainer);
			
			if (optionsNameMatcher.find()) {
				String match = optionsNameMatcher.group(0);
				String correspondingSourceKey = optionsNameMatcher.group(1);
				String playerUrl = videoOptions.get(correspondingSourceKey);
				
				if (StringUtils.validate(correspondingSourceKey, playerUrl)) {
					String itemName = correspondingSourceKey;
					
					String extractedOptionSimpleName = getHelper().extract("\\<div\\sclass\\=\\\"Optntl\\\"\\>(.*?)\\<\\/div\\>", match);
					String extractedOptionQuality = getHelper().extract("\\<p.*?\\>(.*?)\\<\\/p\\>", match);
					
					if (StringUtils.validate(extractedOptionSimpleName, extractedOptionQuality)) {
						itemName = String.format("%s - %s", extractedOptionSimpleName.replaceAll("\\<[\\/]*span.*?\\>", ""), extractedOptionQuality);
					}
					
					additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new CompletedVideoItemResultData(this, itemName, Arrays.asList(playerUrl))));
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
		if (videoItemResult instanceof CompletedVideoItemResultData) {
			return ((CompletedVideoItemResultData) videoItemResult).getPlayerUrlsAsArray();
		}
		
		return new String[] { videoItemResult.getUrl() };
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
	}
	
	public static List<HdssItem> extractVideoFromHtml(String html) {
		List<HdssItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("<li\\sclass\\=\\\"TPostMv\\\"\\>[\\s\\t\\n]*\\<article\\sclass\\=\\\"TPost\\sB\\\"\\>[\\s\\t\\n]*\\<a\\shref\\=\\\"(.*?)\\\"\\>.*?\\<img.*?data-wpfc-original-src\\=\\\"(.*?)\\\".*?\\>.*?\\<div\\sclass\\=\\\"Title\\\"\\>(.*?)\\<\\/div\\>(.*?)\\<\\/article\\>[\\s\\t\\n]*\\<\\/li\\>", html);
		
		while (matcher.find()) {
			String url = matcher.group(1);
			String name = matcher.group(3);
			String imageUrl = matcher.group(2);
			String moreContent = matcher.group(4);
			
			if (!imageUrl.startsWith("http:")) {
				imageUrl = "http:" + imageUrl;
			}
			
			items.add(new HdssItem(matcher.group(0), url, name, imageUrl, moreContent));
		}
		
		return items;
	}
	
	/**
	 * @param html
	 *            The downloaded html of a video page
	 * @return A string containing all information in html
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<main\\>.*?\\<article\\sclass\\=\\\"TPost\\sA\\sSingle\\\"\\>(.*?)\\<\\/section\\>[\\s]*<\\/main>", html, 0);
	}
	
	/**
	 * @param html
	 *            The downloaded html of a video page
	 * @return A string containing all video options in html
	 */
	public static String extractVideoOptionsContainer(String html) {
		return getStaticHelper().extract("\\<div\\sclass\\=\\\"VideoPlayer\\\"\\>(.*?)\\<\\/section\\>", html, 0);
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class HdssItem extends ResultItem {
		
		private final String moreContent;
		
		public HdssItem(String match, String url, String name, String imageUrl, String moreContent) {
			super(match, url, name, imageUrl);
			
			this.moreContent = moreContent;
		}
		
		public String getMoreContent() {
			return moreContent;
		}
	}
	
}