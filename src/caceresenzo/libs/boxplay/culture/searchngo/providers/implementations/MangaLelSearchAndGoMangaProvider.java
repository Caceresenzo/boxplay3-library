package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;

public class MangaLelSearchAndGoMangaProvider extends SearchAndGoProvider {
	
	private final String listApiUrl;
	private final String imageUrlFormat;
	
	public MangaLelSearchAndGoMangaProvider() {
		super("Manga-LEL", "https://www.manga-lel.com");
		
		listApiUrl = getSiteUrl() + "/changeMangaList?type=text";
		imageUrlFormat = getSiteUrl() + "//uploads/manga/%s/cover/cover_250x350.jpg";
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
		Map<String, SearchAndGoResult> result = createEmptyResultMap();
		
		String html = getHelper().downloadPageCache(listApiUrl);
		
		if (html == null) {
			return result;
		}
		
		List<MangaLelItem> resultItems = extractAnimeFromHtml(html);
		// Logger.info("resultItems: " + resultItems.size());
		
		for (MangaLelItem mangaLelItem : resultItems) {
			String url = mangaLelItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, url.replaceAll("(https\\:\\/\\/www\\.manga-lel\\.com\\/manga\\/|\\/)", ""));
			String name = mangaLelItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, mangaLelItem.getName(), url, imageUrl, SearchCapability.MANGA).score(score));
			}
		}
		
		return result;
	}
	
	/**
	 * Extract all Manga present on the website
	 * 
	 * @param html
	 *            The downloaded html of anypage
	 * @return A list of {@link MangaLelItem} that you can work with. That contain the full match, the url, and the name
	 */
	public static List<MangaLelItem> extractAnimeFromHtml(String html) {
		List<MangaLelItem> items = new ArrayList<>();
		
		final Matcher matcher = getStaticHelper().regex("\\<a\\shref=\\\"(.*?)\\\"\\sclass=\\\"alpha-link\\\"\\>[\\s\\t\\n]*\\<h6\\sstyle=\\\".*?\\\"\\>(.*?)\\<\\/h6\\>[\\s\\t\\n]*\\<\\/a\\>", html);
		
		while (matcher.find()) {
			items.add(new MangaLelItem(matcher.group(0).trim(), matcher.group(1).trim(), getStaticHelper().escapeHtmlSpecialCharactere(matcher.group(2).trim())));
		}
		
		return items;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class MangaLelItem extends ResultItem {
		public MangaLelItem(String match, String url, String name) {
			super(match, url, name);
		}
	}
	
}