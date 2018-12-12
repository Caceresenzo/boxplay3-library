package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.IHentaiVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.string.StringUtils;

public class IAnimesSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider, IHentaiVideoContentProvider {
	
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
			String imageUrl = getSiteUrl() + "/" + itemMatcher.group(2);
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
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		return additionals;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return new Class[] { null };
	}
	
	@Override
	public void allowHentai(boolean allow) {
		this.allowHentai = allow;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		return new String[] { null };
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
	}
	
}
