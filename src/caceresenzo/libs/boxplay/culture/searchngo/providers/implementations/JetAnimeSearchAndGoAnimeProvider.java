package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.cryptography.CloudflareUtils;

public class JetAnimeSearchAndGoAnimeProvider extends SearchAndGoProvider {
	
	private final String imageUrlFormat;
	
	public JetAnimeSearchAndGoAnimeProvider() {
		super("JetAnime", "https://www.jetanime.co");
		
		imageUrlFormat = getSiteUrl() + "/assets/imgs/%s.jpg";
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability(new SearchCapability[] { SearchCapability.ANIME });
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	public Map<String, SearchAndGoResult> processWork(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyResultMap();
		
		String html = getHelper().downloadPageCache(getSiteUrl());
		
		if (html == null) {
			return result;
		}
		
		List<JetAnimeItem> resultItems = extractAnimeFromHtml(html);
		
		for (JetAnimeItem animeItem : resultItems) {
			String url = animeItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, url.replaceAll("(\\/anime\\/|\\/)", ""));
			String name = animeItem.getName();
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				result.put(url, new SearchAndGoResult(this, animeItem.getName(), url, imageUrl, SearchCapability.ANIME).score(score));
			}
		}
		
		return result;
	}
	
	/**
	 * Extract all Anime present on the website
	 * 
	 * @param html
	 *            The downloaded html of anypage
	 * @return A list of {@link JetAnimeItem} that you can work with. That contain the full match, the url, and the name
	 */
	public static List<JetAnimeItem> extractAnimeFromHtml(String html) {
		List<JetAnimeItem> items = new ArrayList<>();
		
		final Matcher matcher = getStaticHelper().regex("\\<option.*?value=\\\"(.*?)\\\"\\>(.*?)\\<\\/option\\>", html);
		
		while (matcher.find()) {
			items.add(new JetAnimeItem(matcher.group(0).trim(), matcher.group(1).trim(), escapeEmailProtection(matcher.group(2).trim())));
		}
		
		return items;
	}
	
	/**
	 * Some name contain "an mail protection", this function will escape it
	 * 
	 * Here is an exemple: <template class="__cf_email__" data-cfemail="<some hex>">[email&#160;protected]</template>
	 * 
	 * More exemple: https://regex101.com/r/e3bosF/1
	 * 
	 * @param name
	 *            Base name, will do nothing if mail protection is not present
	 * @return A escaped name
	 */
	public static String escapeEmailProtection(String name) {
		final Matcher matcher = getStaticHelper().regex("\\<(template|span)\\sclass=\\\"__cf_email__\\\"\\sdata-cfemail=\\\"(.*?)\\\".*?(template|span)\\>", name);
		
		if (matcher.find()) {
			return name.replaceAll("\\<(template|span)\\sclass=\\\"__cf_email__\\\"\\sdata-cfemail=\\\"(.*?)\\\".*?(template|span)\\>", CloudflareUtils.decodeEmail(matcher.group(2)));
		}
		
		return name;
	}
	
	/**
	 * See {@link ResultItem}
	 * 
	 * @author Enzo CACERES
	 */
	public static class JetAnimeItem extends ResultItem {
		public JetAnimeItem(String match, String url, String name) {
			super(match, url, name);
		}
	}
	
}