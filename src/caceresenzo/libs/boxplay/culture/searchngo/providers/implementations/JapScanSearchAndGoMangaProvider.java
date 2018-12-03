package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.html.HtmlCommonExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.string.StringUtils;

public class JapScanSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	/* Constants */
	/**
	 * TODO Documentation, group 1: cell content
	 */
	public static final String REGEX_CELL_EXTRACTION = "\\<div\\sclass\\=\\\"cell\\\"\\>(.*?)\\<\\/div\\>";
	
	/* Static */
	public static final Map<String, AdditionalDataType> COMMON_DATA_CORRESPONDANCES = new HashMap<>();
	
	static {
		COMMON_DATA_CORRESPONDANCES.put("Auteur", AdditionalDataType.AUTHORS);
		COMMON_DATA_CORRESPONDANCES.put("Nom Alternatif", AdditionalDataType.ALTERNATIVE_NAME);
		COMMON_DATA_CORRESPONDANCES.put("Sortie Initial", AdditionalDataType.RELEASE_DATE);
		COMMON_DATA_CORRESPONDANCES.put("Genre", AdditionalDataType.TYPE);
		COMMON_DATA_CORRESPONDANCES.put("Fansubs", AdditionalDataType.TRADUCTION_TEAM);
		COMMON_DATA_CORRESPONDANCES.put("Statut", AdditionalDataType.STATUS);
	}
	
	/* Variables */
	private final String listApiUrl;
	
	/* Constructor */
	public JapScanSearchAndGoMangaProvider() {
		super("Japscan", "https://www.japscan.cc");
		
		this.listApiUrl = getSiteUrl() + "/mangas/";
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.MANGA });
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyWorkMap();
		
		String html = getHelper().downloadPageCache(listApiUrl);
		
		if (!StringUtils.validate(html)) {
			return result;
		}
		
		List<JapscanItem> items = extractMangaFromHtml(html);
		
		for (JapscanItem item : items) {
			String url = getSiteUrl() + item.getUrl();
			String imageUrl = null;
			String name = item.getName();
			String description = item.getMoreContent();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, item.getName(), url, imageUrl, SearchCapability.MANGA).score(score).describe(description));
			}
		}
		
		return result;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		
		if (!StringUtils.validate(html)) {
			return additionals;
		}
		
		/* Common */
		Matcher commonDataHtmlContainerMatcher = getHelper().regex("\\<div\\sclass\\=\\\"table\\\"\\>[\\s]*\\<div\\sclass\\=\\\"thead\\\"\\>(.*?)\\<\\/div\\>[\\s]*\\<div\\sclass\\=\\\"row\\\"\\>(.*?)[\\s]*\\<\\/div\\>[\\s]*\\<\\/div\\>[\\s]*<h2", html);
		if (commonDataHtmlContainerMatcher.find()) {
			String extractedKeysHtmlContainer = commonDataHtmlContainerMatcher.group(1);
			String extractedValuesHtmlContainer = commonDataHtmlContainerMatcher.group(2);
			
			if (StringUtils.validate(extractedKeysHtmlContainer, extractedValuesHtmlContainer)) {
				List<String> keys = new ArrayList<>();
				List<Object> values = new ArrayList<>();
				
				Matcher keysMatcher = getHelper().regex(REGEX_CELL_EXTRACTION, extractedKeysHtmlContainer);
				Matcher valuesMatcher = getHelper().regex(REGEX_CELL_EXTRACTION, extractedValuesHtmlContainer);
				
				while (keysMatcher.find()) {
					String key = keysMatcher.group(1);
					
					keys.add(key.trim());
				}
				
				while (valuesMatcher.find()) {
					String value = valuesMatcher.group(1);
					
					values.add(value);
				}
				
				if (keys.size() == values.size()) {
					for (int i = 0; i < keys.size(); i++) {
						String key = keys.get(i);
						Object value = values.get(i);
						AdditionalDataType correspondingDataType = COMMON_DATA_CORRESPONDANCES.get(key);
						
						if (correspondingDataType == null) {
							continue;
						}
						
						switch (correspondingDataType) {
							case TRADUCTION_TEAM: {
								/* If it has a link, escape it */
								if (String.valueOf(value).matches(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX)) {
									value = getHelper().extract(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, (String) value, 2);
								}
								break;
							}
							
							default: {
								break;
							}
						}
						
						additionals.add(new AdditionalResultData(correspondingDataType, value));
					}
				}
			}
		}
		
		/* Resume */
		String extractedResume = getHelper().extract("\\<div\\sid\\=\\\"synopsis\\\"\\>[\\s]*(.*?)[\\s]*\\<\\/div\\>", html);
		if (extractedResume != null) {
			additionals.add(new AdditionalResultData(AdditionalDataType.RESUME, extractedResume));
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html = getHelper().downloadPageCache(result.getUrl());
		String chaptersHtmlContainer = getHelper().extract("\\<h2\\sclass\\=\\\"bg-header\\\"\\>(?:Liste\\sDes\\sChapitres)\\<\\/h2\\>[\\s]*\\<div\\sid\\=\\\"liste_chapitres\\\"\\>(.*?)\\<\\/div\\>", html);
		
		if (!StringUtils.validate(html, chaptersHtmlContainer)) {
			return additionals;
		}
		
		Matcher volumeHtmlContainerMatcher = getHelper().regex("\\<h2\\>(.*?)\\<\\/h2\\>[\\s]*\\<ul\\>(.*?)\\<\\/ul\\>", chaptersHtmlContainer);
		
		while (volumeHtmlContainerMatcher.find()) {
			String volume = volumeHtmlContainerMatcher.group(1);
			String chaptersHtmlList = volumeHtmlContainerMatcher.group(2);
			
			Matcher chaptersMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LIST_EXTRACTION_REGEX, chaptersHtmlList);
			
			while (chaptersMatcher.find()) {
				String chapterHtmlLink = chaptersMatcher.group(1);
				
				Matcher chapterLinkMatcher = getHelper().regex(HtmlCommonExtractor.COMMON_LINK_EXTRACTION_REGEX, chapterHtmlLink);
				
				if (chapterLinkMatcher.find()) {
					String url = getSiteUrl() + chapterLinkMatcher.group(1);
					String chapter = chapterLinkMatcher.group(2);
					
					additionals.add(new AdditionalResultData(AdditionalDataType.ITEM_CHAPTER, new ChapterItemResultData(this, url, volume, chapter, ChapterItemResultData.ChapterType.IMAGE_ARRAY)));
				}
			}
		}
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { null };
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
	/**
	 * Extract all Manga present on the website.<br>
	 * <br>
	 * Regex: <a href="https://regex101.com/r/1v25Cd/1">regex101</a>
	 * 
	 * @param html
	 *            The downloaded html of list page
	 * @return A list of {@link JapscanItem} that you can work with.<br>
	 *         That contain the full match, the url, the name, and a little bit more content such as the manga type and if it is always airing.
	 */
	public static List<JapscanItem> extractMangaFromHtml(String html) {
		List<JapscanItem> items = new ArrayList<>();
		
		Matcher matcher = getStaticHelper().regex("\\<div\\sclass\\=\\\"row\\\"\\>[\\s]*\\<div\\sclass\\=\\\"cell\\\"\\>\\<a\\shref=\\\"(.*?)\\\"\\>(.*?)\\<\\/a\\>\\<\\/div\\>[\\s]*\\<div\\sclass\\=\\\"cell\\\"\\>(.*?)\\<\\/div\\>[\\s]*\\<div\\sclass\\=\\\"cell\\\"\\>(.*?)\\<\\/div\\>[\\s]*\\<div\\sclass\\=\\\"cell\\\"\\>.*?\\<\\/div\\>[\\s]*\\<\\/div\\>", html);
		
		while (matcher.find()) {
			String match = matcher.group(0);
			String suburl = matcher.group(1);
			String name = matcher.group(2);
			String moreContent = matcher.group(3) + "\n" + matcher.group(4);
			
			items.add(new JapscanItem(match, suburl, name, moreContent));
		}
		
		return items;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class JapscanItem extends ResultItem {
		private String moreContent;
		
		public JapscanItem(String match, String url, String name, String moreContent) {
			super(match, url, name);
			
			this.moreContent = moreContent;
		}
		
		public String getMoreContent() {
			return moreContent;
		}
	}
	
}