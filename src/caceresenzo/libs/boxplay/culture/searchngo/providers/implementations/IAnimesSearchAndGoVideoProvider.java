package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.IHentaiVideoContentProvider;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericOpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

public class IAnimesSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider, IHentaiVideoContentProvider {
	
	/* Static */
	public static final Map<String, AdditionalDataType> COMMON_DATA_CORRESPONDANCES = new HashMap<>();
	
	static {
		COMMON_DATA_CORRESPONDANCES.put("Titre alternatif :", AdditionalDataType.ALTERNATIVE_NAME);
		COMMON_DATA_CORRESPONDANCES.put("Titre original :", AdditionalDataType.ORIGINAL_NAME);
		COMMON_DATA_CORRESPONDANCES.put("Genre :", AdditionalDataType.GENDERS);
		COMMON_DATA_CORRESPONDANCES.put("Date de Diffusion :", AdditionalDataType.RELEASE_DATE);
		COMMON_DATA_CORRESPONDANCES.put("Durée par épisode :", AdditionalDataType.DURATION); /* Seems not to work */
		COMMON_DATA_CORRESPONDANCES.put("Studio d'animation :", AdditionalDataType.ANIMATION_STUDIO);
		COMMON_DATA_CORRESPONDANCES.put("Sous licence :", AdditionalDataType.UNDER_LICENSE);
		COMMON_DATA_CORRESPONDANCES.put("Editeurs :", AdditionalDataType.PUBLISHERS);
	}
	
	/* Variables */
	private final String searchUrlFormat;
	private boolean allowHentai = false;
	
	/* Constructor */
	public IAnimesSearchAndGoVideoProvider() {
		super("I-ANIMES", "https://www.ianimes.co");
		
		this.searchUrlFormat = getSiteUrl() + "/resultat+%s.html";
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new ProviderSearchCapability.SearchCapability[] { SearchCapability.VIDEO, SearchCapability.ANIME, SearchCapability.MOVIE, SearchCapability.SERIES, SearchCapability.HENTAI, });
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String html = getHelper().downloadPageCache(String.format(searchUrlFormat, searchQuery.toUpperCase().replace(" ", "+")));
		
		if (!StringUtils.validate(html)) {
			return result;
		}
		
		/* The fuck, that website got such ugly code that my eyes burn */
		/* Regex: https://regex101.com/r/9UDeVq/4 */
		Matcher itemMatcher = getHelper().regex("\\<td\\salign\\=\\\"center\\\"\\>[\\s]*\\<table.*?\\>[\\s]*\\<tr.*?\\>[\\s]*\\<td.*?\\>[\\s]*\\<center\\>[\\s]*\\<span.*?>[\\s]*\\<titre6\\>[\\s]*(.*?)[\\s]*\\<\\/titre6\\>[\\s]*\\<\\/center\\>[\\s]*\\<\\/span\\>[\\s]*\\<\\/td\\>\\<\\/tr\\>\\<tr.*?\\>\\<td.*?\\>\\<center\\>\\<div\\sstyle\\=\\\"background:[\\s]*url\\(\\'(.*?)\\'\\)\\;.*?\\\"\\>\\<img\\ssrc\\=\\\"img\\/(.*?)\\..*?\\\".*?\\>\\<\\/div\\>\\<\\/center\\>\\<\\/td\\>\\<\\/tr\\>\\<tr\\>\\<td.*?\\>\\<center\\>.*?\\<\\/center\\>\\<\\/td\\>\\<td.*?\\>\\<center\\>.*?\\<\\/center\\>.*?\\<\\/td\\>\\<\\/tr\\>\\<td.*?\\>\\<center\\>\\<a\\shref\\=\\'(.*?)\\'.*?\\>.*?\\<\\/a\\>\\<center\\>\\<\\/td\\>\\<\\/tr\\>\\<\\/table\\>.*?\\<\\/td\\>", html);
		while (itemMatcher.find()) {
			String name = itemMatcher.group(1);
			String subImageUrl = itemMatcher.group(2);
			String typeHint = itemMatcher.group(3);
			String url = getSiteUrl() + "/" + itemMatcher.group(4);
			
			SearchCapability type;
			switch (typeHint.toLowerCase()) {
				case "anime": {
					type = SearchCapability.ANIME;
					break;
				}
				
				case "film": {
					type = SearchCapability.MOVIE;
					break;
				}
				
				case "serie": {
					type = SearchCapability.SERIES;
					break;
				}
				
				case "hider1": {
					type = SearchCapability.HENTAI;
					break;
				}
				
				default: {
					/* Unknown type, ignore */
					continue;
				}
			}
			
			String imageUrl;
			switch (type) {
				case HENTAI: {
					if (subImageUrl.startsWith(subImageUrl)) {
						imageUrl = subImageUrl;
						
						/* Break only if subImageUrl is already a direct image url, else, use default statement */
						break;
					}
				}
				
				default: {
					imageUrl = getSiteUrl() + "/" + subImageUrl;
					break;
				}
			}
			
			if (!allowHentai && type.equals(SearchCapability.HENTAI)) {
				continue;
			}
			
			if (StringUtils.validate(name)) {
				result.put(url, new SearchAndGoResult(this, name, url, imageUrl, type));
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		
		switch (result.getType()) {
			case ANIME:
			case SERIES:
			case HENTAI: {
				String htmlContainer = getHelper().extract("\\<table\\swidth\\=\\\"100%\\\"\\sCELLPADDING\\=\\\"5\\\"\\sclass\\=\\\"bordures\\\"\\>\\<center\\>\\<tr\\>\\<td\\sstyle\\='.*?'\\>\\<img\\ssrc\\='.*?'.*?\\>\\<\\/img\\>\\<\\/td\\>\\<td.*?\\>\\<br\\>(.*?)\\<table\\sstyle\\=\\\"width\\:\\s100%\\\"\\>.*?\\<\\/td\\>\\<td\\sstyle\\=\\\"width\\:\\s25%\\\"\\>\\<center\\>\\<\\/center\\>\\<\\/td\\>\\<td\\sstyle\\=\\\"width\\:\\s50%\\\"\\>\\<center\\>.*?\\<\\/center\\>\\<\\/td\\>\\<\\/tr>\\<\\/table\\>", html);
				String bigImageSubUrl = getHelper().extract("\\<div\\sstyle\\=\\\"background\\:\\surl\\('(.*?)'\\)\\;\\sbackground-size\\:\\s100%\\s100%\\;\\sbackground-position:center;\\sbackground-repeat:\\sno-repeat;\\sheight:420px;\\swidth:700px;border-radius:8px;overflow:hidden;\\\"\\>\\<img\\ssrc\\=\\\".*?\\\".*?\\>\\<\\/div\\>", html);
				
				/* Thumbnail */
				if (StringUtils.validate(bigImageSubUrl)) {
					String bigImageUrl = getSiteUrl() + "/" + bigImageSubUrl;
					
					additionals.add(new AdditionalResultData(AdditionalDataType.THUMBNAIL, bigImageUrl));
				}
				
				/* Resume */
				String extractedResume = getHelper().extract("\\<legend\\>\\<headline15\\>(?:&nbsp;){3}Synopsis(?:&nbsp;){3}\\<\\/headline15\\>\\<\\/legend\\>\\<font.*?\\>(.*?)\\<\\/font\\>\\<br\\>\\<\\/fieldset\\>", html);
				if (extractedResume != null) {
					additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume));
				}
				
				if (!StringUtils.validate(htmlContainer)) {
					return additionals;
				}
				
				/* Common */
				Matcher htmlItemMatcher = getHelper().regex("(?:\\&nbsp\\;){6}\\<font\\scolor\\='.*?'\\>[\\s]*(.*?)[\\s]*\\<\\/font\\>\\<font\\scolor\\='.*?'.*?\\>[\\s]*(.*?)[\\s]*\\<\\/font\\><br>", htmlContainer);
				while (htmlItemMatcher.find()) {
					String type = htmlItemMatcher.group(1);
					AdditionalDataType correspondingType = COMMON_DATA_CORRESPONDANCES.get(type.trim());
					String rawContent = AdditionalResultData.escapeHtmlChar(HtmlCommonExtractor.escapeUnicode(htmlItemMatcher.group(2)));
					
					if (correspondingType == null) {
						continue;
					}
					
					Object processedContent = rawContent;
					switch (correspondingType) {
						case GENDERS: {
							List<CategoryResultData> categories = new ArrayList<>();
							
							for (String category : rawContent.split(",")) {
								String name = category.trim();
								
								if (StringUtils.validate(name)) {
									categories.add(new CategoryResultData(name));
								}
							}
							
							if (!categories.isEmpty()) {
								processedContent = categories;
							}
							break;
						}
						
						default: {
							/* Not handled, so using rawContent by default */
							break;
						}
					}
					
					if (processedContent != null) {
						additionals.add(new AdditionalResultData(correspondingType, processedContent));
					}
				}
				break;
			}
			
			case MOVIE: {
				/* Resume */
				String extractedResume = getHelper().extract("\\<headline11\\>\\<font\\scolor\\=\\'.*?\\'\\ssize\\=\\'[\\d]*\\'.*?\\>Synopsis\\<\\/font\\>\\<\\/headline11\\>\\<br\\>\\<img.*?\\>\\<\\/img\\>\\<br\\>\\<headline11\\>(.*?)\\<\\/headline11\\>\\<br\\>", html);
				if (extractedResume != null) {
					additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume));
				}
				break;
			}
			
			default: {
				throw new IllegalStateException("Invalid type: " + result.getType());
			}
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		switch (result.getType()) {
			case ANIME:
			case SERIES:
			case HENTAI: {
				String html = getHelper().downloadPageCache(result.getUrl());
				String htmlContainer = getHelper().extract("\\<ul\\sclass\\=\\'post_list\\sextra_posts_list\\'\\>\\<li\\sclass\\=\\\"cat_post_item-1\\sclearfix\\\"\\>(.*?)\\<\\/li\\><\\/ul\\>", html);
				
				if (!StringUtils.validate(htmlContainer)) {
					return additionals;
				}
				
				Matcher groupMatcher = getHelper().regex("\\<center>\\<a.*?\\>\\<headline11\\>(.*?)\\<\\/headline11\\>\\<\\/a\\>\\<\\/center\\>(.*?\\<\\/a\\>)\\<br\\>(\\<br\\>|$)", htmlContainer);
				while (groupMatcher.find()) {
					String groupTitle = groupMatcher.group(1);
					String groupHtmlContainer = groupMatcher.group(2);
					
					Matcher videoMatcher = getHelper().regex("\\<br\\>\\<img.*?\\>(?:&nbsp;){2}\\<a\\shref\\=\\\"(.*?)\\\".*?\\>(.*?)\\<\\/a\\>", groupHtmlContainer);
					while (videoMatcher.find()) {
						String url = videoMatcher.group(1);
						String name = videoMatcher.group(2);
						
						additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, url, String.format("%s - %s", groupTitle, name))));
					}
				}
				break;
			}
			
			case MOVIE: {
				additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_VIDEO, new VideoItemResultData(this, result.getUrl(), "STREAMING")));
				break;
			}
			
			default: {
				throw new IllegalStateException("Invalid type: " + result.getType());
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
	public void allowHentai(boolean allow) {
		this.allowHentai = allow;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		List<String> urls = new ArrayList<>();
		
		String html = getHelper().downloadPageCache(videoItemResult.getUrl());
		
		if (!StringUtils.validate(html)) {
			return new String[] { null };
		}
		
		Matcher playerContainerMatcher = getHelper().regex("\\<div\\sstyle\\=\\\"width:\\s720px;margin:\\s3px;\\sborder:\\s1px\\ssolid\\s#D9D9D9;\\sbackground-color:\\s#F1EFEF;\\s-moz-border-radius:\\s5px;\\s-webkit-border-radius:\\s5px;\\sborder-radius:\\s5px;\\s-moz-box-shadow:\\s0px\\s0px\\s8px\\srgba\\(0,\\s0,\\s0,\\s0\\.3\\);\\s-webkit-box-shadow:\\s0px\\s0px\\s8px\\srgba\\(0,\\s0,\\s0,\\s0\\.3\\)\\;\\sbox-shadow:\\s0px\\s0px\\s8px\\srgba\\(0,\\s0,\\s0,\\s0\\.3\\)\\;\".*?\\>\\<div\\sclass\\=\\\"box\\\".*?\\>\\<script\\stype\\=\\\"text\\/javascript\\\"\\>document\\.write\\(unescape\\(\\\"(.*?)\\\"\\)\\)\\;\\<\\/script\\>\\<\\/div\\>\\<div\\sclass\\=\\\"box\\\"\\>\\<table.*?Host\\s\\:\\<\\/font\\>[\\s]*\\<font\\scolor\\='.*?'\\>(.*?)\\<\\/font\\>\\<\\/titre6\\>\\<\\/center\\>\\<\\/td\\>\\<\\/tr\\>\\<\\/table\\>\\<\\/div\\>\\<\\/div\\>", html);
		while (playerContainerMatcher.find()) {
			String encodedIframeHtml = playerContainerMatcher.group(1);
			// String playerName = playerContainerMatcher.group(2);
			
			String decodedIframeHtml = new IAnimeIframeDecoderSandbox().execute(encodedIframeHtml);
			String iframeUrl = HtmlCommonExtractor.extractIframeUrlFromHtml(decodedIframeHtml);
			
			/* Doing pretty network-intensive tasks... */
			if (StringUtils.validate(iframeUrl)) {
				String resolvedHtml = Webb.create().post(iframeUrl) //
						.header(WebbConstante.HDR_USER_AGENT, WebbConstante.USER_AGENT_CHROME) //
						.header("referer", iframeUrl) //
						.param("submit.x", 0) //
						.param("submit.y", 0) //
						.asString().getBody();
				
				String extractedUrl = HtmlCommonExtractor.extractIframeUrlFromHtml(resolvedHtml);
				if (extractedUrl != null) {
					urls.add(extractedUrl.replace("\n", ""));
				}
			}
		}
		
		return urls.toArray(new String[urls.size()]);
	}
	
	@Override
	public String getWorkingCharset() {
		return CHARSET_LATIN_1;
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
	}
	
	public static class IAnimeIframeDecoderSandbox implements Sandbox<String, String> {
		@Override
		public String execute(String encoded) {
			List<String> alreadyDecoded = new ArrayList<>();
			
			String result = encoded;
			
			Matcher matcher = getStaticHelper().regex("\\%([\\w]{2})", encoded);
			while (matcher.find()) {
				String syntax = matcher.group(0);
				String hexValue = matcher.group(1);
				
				if (!alreadyDecoded.contains(syntax)) {
					alreadyDecoded.add(syntax);
					
					try {
						char character = (char) Integer.parseInt(hexValue, 16);
						
						result = result.replace(syntax, String.valueOf(character));
					} catch (Exception exception) {
						; /* Invalid number format ? */
					}
				}
			}
			
			return result;
		}
	}
	
}
