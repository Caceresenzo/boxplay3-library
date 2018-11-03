package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVidozaVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;
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
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

public class FullStreamCoSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	/* Constants */
	public static final String VIDEO_NAME_FORMAT = "%S %S";
	
	/* Additionals Data Keys: for Movies */
	protected static final String ADDITIONAL_DATA_KEY_RELEASE_DATE_AND_DURATION = "Date de sortie";
	protected static final String ADDITIONAL_DATA_KEY_DIRECTOR = "Réalisateur";
	protected static final String ADDITIONAL_DATA_KEY_GENDERS = "Catégorie";
	protected static final String ADDITIONAL_DATA_KEY_ACTORS = "Avec";
	protected static final String ADDITIONAL_DATA_KEY_QUALITY = "Film en Version";
	protected static final String ADDITIONAL_DATA_KEY_RESUME = "Résumé Du Film";
	
	/* Additionals Data Keys: for Series */
	protected static final String ADDITIONAL_DATA_KEY_SERIES_AUTHOR = "Créé par";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_ACTORS = "Avec";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_RELEASE_DATE = "Année de création";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_GENDERS = "Genre";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_STATUS = "Statut";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_CHANNEL = "Chaîne";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_VERSION = "Version";
	protected static final String ADDITIONAL_DATA_KEY_SERIES_RESUME = "Résumé de la série";
	
	/* Additionals Data Correspondance: for Series */
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE_SERIES = new EnumMap<>(AdditionalDataType.class);
	
	private final String searchBaseUrl;
	
	public FullStreamCoSearchAndGoVideoProvider() {
		super("Full-Stream.co", "https://full-stream.co/");
		
		this.searchBaseUrl = getSiteUrl() + "/index.php?do=search";
		
		ADDITIONAL_DATA_CORRESPONDANCE.put(AdditionalDataType.ACTORS, ADDITIONAL_DATA_KEY_ACTORS);
		
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.AUTHORS, ADDITIONAL_DATA_KEY_SERIES_AUTHOR);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.ACTORS, ADDITIONAL_DATA_KEY_SERIES_ACTORS);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.RELEASE_DATE, ADDITIONAL_DATA_KEY_SERIES_RELEASE_DATE);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.GENDERS, ADDITIONAL_DATA_KEY_SERIES_GENDERS);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.STATUS, ADDITIONAL_DATA_KEY_SERIES_STATUS);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.CHANNELS, ADDITIONAL_DATA_KEY_SERIES_CHANNEL);
		ADDITIONAL_DATA_CORRESPONDANCE_SERIES.put(AdditionalDataType.VERSION, ADDITIONAL_DATA_KEY_SERIES_VERSION);
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
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String htmlContainer = extractInformationContainer(html);
		
		if (html == null || html.isEmpty() || htmlContainer == null || htmlContainer.isEmpty()) {
			return additionals;
		}
		
		/*
		 * Movie
		 */
		
		/* Common */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonInformation(htmlContainer, dataKey);
			if (extractedData != null) {
				additionals.add(new AdditionalResultData(type, extractedData.trim()));
			}
		}
		
		/* Release Date & Duration (sometimes with Country) */
		String extractedReleaseDateAndDurationData = extractCommonInformation(htmlContainer, ADDITIONAL_DATA_KEY_RELEASE_DATE_AND_DURATION);
		if (extractedReleaseDateAndDurationData != null) {
			Matcher matcher = getHelper().regex("(.*?)\\((.*?)\\)$", extractedReleaseDateAndDurationData);
			
			if (matcher.find()) {
				String extractedReleaseDateData = matcher.group(1);
				if (extractedReleaseDateData != null) {
					additionals.add(new AdditionalResultData(AdditionalDataType.RELEASE_DATE, extractedReleaseDateData));
				}
				
				String extractedDurationData = matcher.group(2);
				if (extractedDurationData != null) {
					String duration = extractedDurationData;
					
					if (duration.contains("(")) {
						String[] split = duration.split("\\(");
						
						additionals.add(new AdditionalResultData(AdditionalDataType.COUNTRY, split[0].replace(")", "").trim()));
						
						duration = split[1];
					}
					
					additionals.add(new AdditionalResultData(AdditionalDataType.DURATION, duration.trim()));
				}
			}
		}
		
		/* Director */
		String extractedDirectorData = extractCommonInformation(htmlContainer, ADDITIONAL_DATA_KEY_DIRECTOR);
		if (extractedDirectorData != null) {
			String director = getHelper().extract("\\<span.*?\\>[\\s]*\\<big\\>[\\s]*\\<span.*?\\>[\\s]*(.*?)[\\s]*\\<\\/span\\>", extractedDirectorData);
			
			if (director != null) {
				if (director.contains("(")) {
					director = director.split("\\(")[0].trim();
				}
				
				if (director != null) { // In case of
					additionals.add(new AdditionalResultData(AdditionalDataType.DIRECTOR, director));
				}
			}
		}
		
		/* Genders */
		String extractedGendersData = extractCommonInformation(htmlContainer, ADDITIONAL_DATA_KEY_GENDERS);
		if (extractedGendersData != null) {
			Matcher matcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, extractedGendersData);
			
			List<CategoryResultData> categories = new ArrayList<>();
			
			while (matcher.find()) {
				String url = matcher.group(1);
				String name = matcher.group(2);
				
				categories.add(new CategoryResultData(url, name));
			}
			
			if (!categories.isEmpty()) {
				additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
			}
		}
		
		/* Quality */
		String extractedQualityData = extractCommonInformation(htmlContainer, ADDITIONAL_DATA_KEY_QUALITY);
		if (extractedQualityData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.QUALITY, getHelper().extractStringFromHtml("span", extractedQualityData)));
		}
		
		/* Resume */
		String extractedResumeData = getHelper().extract(String.format("\\<strong\\>[\\s]*%s[\\s]*<\\/strong><\\/span><\\/strong>:[\\s]*(.*?)<\\/ul>", ADDITIONAL_DATA_KEY_RESUME), htmlContainer);
		if (extractedResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResumeData));
		}
		
		/*
		 * Series
		 */
		
		/* Common */
		for (Entry<AdditionalDataType, String> entry : ADDITIONAL_DATA_CORRESPONDANCE_SERIES.entrySet()) {
			AdditionalDataType type = entry.getKey();
			String dataKey = entry.getValue();
			
			String extractedData = extractCommonSeriesInformation(htmlContainer, dataKey);
			
			if (extractedData != null) {
				switch (dataKey) {
					/* Author */
					case ADDITIONAL_DATA_KEY_SERIES_AUTHOR: {
						UrlResultData author = getHelper().extractUrlFromHtml(extractedData);
						
						if (author != null) {
							additionals.add(new AdditionalResultData(AdditionalDataType.AUTHORS, author));
						}
						
						break;
					}
					
					/* Actors */
					case ADDITIONAL_DATA_KEY_SERIES_ACTORS: {
						Matcher matcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, extractedData);
						
						List<String> actors = new ArrayList<>();
						
						while (matcher.find()) {
							String name = matcher.group(2);
							
							actors.add(name);
						}
						
						if (!actors.isEmpty()) {
							StringBuilder builder = new StringBuilder();
							
							Iterator<String> iterator = actors.iterator();
							while (iterator.hasNext()) {
								String next = iterator.next();
								
								builder.append(next);
								
								if (iterator.hasNext()) {
									builder.append(", ");
								}
							}
							
							additionals.add(new AdditionalResultData(AdditionalDataType.ACTORS, builder.toString()));
						}
						
						break;
					}
					
					/* Genders */
					case ADDITIONAL_DATA_KEY_SERIES_GENDERS: {
						String[] genders = extractedData.split("\\.");
						
						List<CategoryResultData> categories = new ArrayList<>();
						
						for (String gender : genders) {
							if (StringUtils.validate(gender)) {
								categories.add(new CategoryResultData(gender));
							}
						}
						
						if (!categories.isEmpty()) {
							additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
						}
						
						break;
					}
					
					/* Common */
					default: {
						additionals.add(new AdditionalResultData(type, extractedData.replace(".", " ").trim()));
						break;
					}
				}
			}
		}
		
		/* Resume */
		String extractedSerieResumeData = getHelper().extract(String.format("\\<h5>[\\s]*%s[\\s]*:[\\s]*\\<\\/h5\\>[\\s\\t\\n]*(.*?)[\\s\\t\\n]*\\<\\/ul\\>", ADDITIONAL_DATA_KEY_SERIES_RESUME), htmlContainer);
		if (extractedSerieResumeData != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, AdditionalResultData.escapeHtmlChar(extractedSerieResumeData)));
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
		
		Matcher tabMatcher = getHelper().regex("\\<div\\sstyle=\\\"float:.*?;overflow:hidden;margin:.*?;width:140px;text-align:center\\\"\\>[\\s\\t\\n]*\\<div\\sclass=\\\".*?-tab\\\"\\>\\<i\\sclass=\\\"fa\\sfa-play-circle-o\\\"\\>\\<\\/i\\>[\\s]*(.*?)[\\s]*\\<\\/div\\>(.*?)\\<\\/div\\>", html);
		
		while (tabMatcher.find()) {
			String language = tabMatcher.group(1);
			String episodeHtmlContainer = tabMatcher.group(2);
			
			List<FullStreamNuVideoEpisodeItem> episodeItems = new ArrayList<>();
			
			switch (result.getType()) {
				/* Series */
				case SERIES: {
					Matcher seriesEpisodeMatcher = getHelper().regex("\\<a.*?data-rel=\\\"(.*?)\\\".*?\\>\\<i.*?\\/i\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>", episodeHtmlContainer);
					
					while (seriesEpisodeMatcher.find()) {
						String episodeString = seriesEpisodeMatcher.group(2);
						String dataRelation = seriesEpisodeMatcher.group(1);
						
						episodeItems.add(FullStreamNuVideoEpisodeItem.createWithSeries(episodeString, dataRelation));
					}
					
					if (episodeItems.isEmpty()) {
						continue;
					}
					
					Matcher playerUrlContainerMatcher = getHelper().regex("\\<div\\sid=\\\"(.*?)\\\"\\sclass=\\\"fullsfeature\\\"\\>[\\s\\t\\n]*\\<div\\sclass=\\\"selink\\\"\\>[\\s\\t\\n]*\\<ul\\sclass=\\\"btnss\\\"\\>(.*?)\\<\\/ul\\>[\\s\\t\\n]*\\<div\\sstyle=\\\".*?\\\"\\>\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>[\\s\\t\\n]*\\<\\/div\\>", html);
					
					while (playerUrlContainerMatcher.find()) {
						String dataRelation = playerUrlContainerMatcher.group(1);
						String urlContainer = playerUrlContainerMatcher.group(2);
						
						FullStreamNuVideoEpisodeItem actualItem = null;
						
						for (FullStreamNuVideoEpisodeItem episodeItem : episodeItems) {
							if (episodeItem.getDataRelation().equals(dataRelation)) {
								actualItem = episodeItem;
								break;
							}
						}
						
						if (actualItem == null || urlContainer == null || urlContainer.isEmpty()) { // Can append
							continue;
						}
						
						List<String> urls = new ArrayList<>();
						
						Matcher urlMatcher = getHelper().regex("\\<li\\>(.*?)\\<\\/li\\>", urlContainer);
						
						while (urlMatcher.find()) {
							String htmlLinkElement = urlMatcher.group(1);
							
							if (htmlLinkElement != null) {
								UrlResultData urlResultData = getHelper().extractUrlFromHtml(htmlLinkElement);
								
								urls.add(urlResultData.getTargetUrl());
							}
						}
						
						if (!urls.isEmpty()) {
							String episode = String.format(VIDEO_NAME_FORMAT, AdditionalResultData.escapeHtmlChar(actualItem.getEpisodeString()), language);
							
							additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new CompletedVideoItemResultData(this, episode, urls)));
						}
					}
					
					break;
				}
				
				/* Movie */
				case MOVIE: {
					Matcher movieEpisodeMatcher = getHelper().regex("\\<a.*?onclick=\\\"(.*?)\\\".*?\\>\\<i.*?\\/i\\>[\\s]*(.*?)[\\s]*\\<\\/a\\>", episodeHtmlContainer);
					
					while (movieEpisodeMatcher.find()) {
						String episodeString = movieEpisodeMatcher.group(2);
						String onClickJavascript = movieEpisodeMatcher.group(1);
						
						episodeItems.add(FullStreamNuVideoEpisodeItem.createWithMovie(episodeString, onClickJavascript));
					}
					
					if (episodeItems.isEmpty()) {
						continue;
					}
					
					for (FullStreamNuVideoEpisodeItem episodeItem : episodeItems) {
						String extractedSource = episodeItem.extractJavascriptUrl();
						
						if (extractedSource != null) {
							String episode = String.format(VIDEO_NAME_FORMAT, episodeItem.getEpisodeString(), language);
							
							additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new CompletedVideoItemResultData(this, episode, Arrays.asList(extractedSource))));
						}
					}
					
					break;
				}
				
				/* Other */
				default: {
					break; // Untreatable for the moment
				}
			}
		}
		
		return additionals;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		return ((CompletedVideoItemResultData) videoItemResult).getPlayerUrlsAsArray();
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
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
	
	public static List<FullStreamNuItem> extractVideoFromHtml(String html) {
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
	 * Extract main information container
	 * 
	 * @param html
	 *            Video page html
	 * @return Html container
	 */
	public static String extractInformationContainer(String html) {
		return getStaticHelper().extract("\\<div\\sclass=\\\"music-details\\\"\\>(.*?)\\<\\/div\\>", html);
	}
	
	/**
	 * Extract common information (for movies)
	 * 
	 * @param htmlContainer
	 *            Html container
	 * @param key
	 *            Data key
	 * @return Extracted data
	 */
	public static String extractCommonInformation(String htmlContainer, String key) {
		return getStaticHelper().extract(String.format("\\<strong\\>\\<span.*?\\>[\\s]*\\<strong\\>[\\s]*%s[\\s]*<\\/strong>\\<\\/span\\>\\<\\/strong\\>:[\\s]*(.*?)[\\s]*\\<hr \\/>", key), htmlContainer);
	}
	
	/**
	 * Extract common information (for series)
	 * 
	 * @param htmlContainer
	 *            Html container
	 * @param key
	 *            Data key
	 * @return Extracted data
	 */
	public static String extractCommonSeriesInformation(String htmlContainer, String key) {
		return getStaticHelper().extract(String.format("\\<h5>[\\s]*%s[\\s]*:[\\s]*(.*?)\\<\\/h5\\>", key), htmlContainer);
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
	
	/**
	 * Simple information container
	 * 
	 * <br>
	 * <br>
	 * Exemple: <code>&lt;a onclick=&quot;javascript:return false;&quot; href=&quot;#&quot; title=&quot;Episode 1&quot; data-rel=&quot;episode1&quot; class=&quot;fstab&quot;&gt;&lt;i class=&quot;fa fa-youtube-play&quot;&gt; &lt;/i&gt; Episode 1 &lt;/a&gt;</code>
	 * 
	 * <br>
	 * <br>
	 * Using the <code>&lt;a&gt;String&lt;/a&gt;</code> and the data-rel attribute (series)
	 * 
	 * <br>
	 * <br>
	 * Using the <code>&lt;a&gt;String&lt;/a&gt;</code> and the onClick attribute (movie)
	 * 
	 * @author Enzo CACERES
	 */
	public static class FullStreamNuVideoEpisodeItem {
		private final String episodeString, dataRelation, onClickJavascript;
		
		private FullStreamNuVideoEpisodeItem(String episodeString, String dataRelation, String onClickJavascript) {
			this.episodeString = episodeString;
			this.dataRelation = dataRelation;
			this.onClickJavascript = onClickJavascript;
		}
		
		public String getEpisodeString() {
			return episodeString;
		}
		
		public String getDataRelation() {
			return dataRelation;
		}
		
		public String getOnClickJavascript() {
			return onClickJavascript;
		}
		
		public String extractJavascriptUrl() {
			if (onClickJavascript == null) {
				return null;
			}
			
			Matcher functionMatcher = getStaticHelper().regex("(.*?)\\(\\'(.*?)\\'\\);", onClickJavascript);
			
			if (functionMatcher.find()) {
				String function = functionMatcher.group(1);
				String source = functionMatcher.group(2);
				
				switch (function) {
					/* Apply source directly */
					case "gen": {
						return source;
					}
					
					/* Flashx.tv */
					case "aw": {
						return "https://www.flashx.tv/embed-" + source + ".html";
					}
					
					/* Openload */
					case "ae": {
						return "https://openload.co/embed/" + source;
					}
					
					/* Vidoza */
					case "tq": {
						return "https://vidoza.net/embed-" + source + ".html";
					}
					
					/* ESTREAM */
					case "bg": {
						return "https://estream.to/embed-" + source + ".html";
					}
					
					/* Vidlox */
					case "jh": {
						return "https://vidlox.me/embed-" + source + ".html";
					}
					
					/* MyStream */
					case "mg3": {
						return "https://mystream.la/embed-" + source + ".html";
					}
					
					/* Streamango */
					case "yu": {
						return "https://streamango.com/embed/" + source;
					}
					
					/* EasyVid */
					case "yru": {
						return "https://easyvid.org/embed-" + source + "-600x360.html";
					}
					
					/* Unknown */
					default: {
						break;
					}
				}
			}
			
			return null;
		}
		
		public static FullStreamNuVideoEpisodeItem createWithSeries(String episodeString, String dataRelation) {
			return new FullStreamNuVideoEpisodeItem(episodeString, dataRelation, null);
		}
		
		public static FullStreamNuVideoEpisodeItem createWithMovie(String episodeString, String onClickJavascript) {
			return new FullStreamNuVideoEpisodeItem(episodeString, null, onClickJavascript);
		}
	}
	
}