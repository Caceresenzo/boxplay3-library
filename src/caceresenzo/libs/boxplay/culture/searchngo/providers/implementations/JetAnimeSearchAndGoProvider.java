package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.cryptography.CloudflareUtils;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;

public class JetAnimeSearchAndGoProvider extends SearchAndGoProvider {
	
	private final String imageUrlFormat;
	
	public JetAnimeSearchAndGoProvider() {
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
	public Map<String, SearchAndGoResult> work(String searchQuery) {
		Map<String, SearchAndGoResult> result = createEmptyResultMap();
		
		String html = getHelper().downloadPageCache(getSiteUrl());
		
		if (html == null) {
			return result;
		}
		
		List<JetAnimeItem> animeItems = extractAnimeFromHtml(html);
		
		for (JetAnimeItem animeItem : animeItems) {
			String url = animeItem.getUrl();
			String imageUrl = String.format(imageUrlFormat, url.replaceAll("(\\/anime\\/|\\/)", ""));
			String name = animeItem.getName();
			
			boolean canPut = true;
			
			if (!(searchQuery == null || searchQuery.isEmpty())) {
				String[] parts = searchQuery.toUpperCase().split(" ");
				
				boolean match = true;
				for (String part : parts) {
					if (!name.toUpperCase().contains(part)) {
						match = false;
						break;
					}
				}
				
				canPut = match;
			}
			
			if (canPut) {
				result.put(url, new SearchAndGoResult(this, animeItem.getName(), url, imageUrl, SearchCapability.ANIME));
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
	 * Item information class
	 * 
	 * It contain, a full regex match, an url, and a name
	 */
	public static class JetAnimeItem {
		private String match, url, name;
		
		/**
		 * Create new instance of a JetAnimeItem
		 * 
		 * @param match
		 *            Full matcher match
		 * @param url
		 *            Url found
		 * @param name
		 *            Name found
		 */
		public JetAnimeItem(String match, String url, String name) {
			this.match = match;
			this.url = url;
			this.name = name;
		}
		
		public String getMatch() {
			return match;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getName() {
			return name;
		}
	}
	
}