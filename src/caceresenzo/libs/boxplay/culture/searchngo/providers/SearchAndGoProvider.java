package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.content.IContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;

/**
 * Provider class
 * 
 * This is an abstract class that need to be extend. Once extend, do all your data-gathering for SEARCHING ONLY in the {@link #processWork(String)} function
 * 
 * @author Enzo CACERES
 */
public abstract class SearchAndGoProvider implements IContentProvider {
	
	public static final int NO_VALUE = Integer.MIN_VALUE;
	
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE = new EnumMap<>(AdditionalDataType.class);
	
	private final String siteName, siteUrl;
	private final ProviderSearchCapability searchCapability;
	
	private final ProviderHelper helper;
	
	private boolean autosort;
	
	/**
	 * Constructor
	 * 
	 * @param siteName
	 *            Site name
	 * @param siteUrl
	 *            Site base url
	 */
	protected SearchAndGoProvider(String siteName, String siteUrl) {
		this.siteName = siteName;
		this.siteUrl = siteUrl;
		this.searchCapability = createSearchCapability();
		
		this.helper = new ProviderHelper(this);
		
		this.autosort = true;
	}
	
	/**
	 * Used to create the search capability instance
	 * 
	 * @return Exemple: new ProviderSearchCapability(new SearchCapability[] { SearchCapability.ANIME });
	 */
	protected abstract ProviderSearchCapability createSearchCapability();
	
	/**
	 * Get Provider's Search Capability
	 * 
	 * @return The instance
	 */
	public ProviderSearchCapability getSearchCapability() {
		return searchCapability;
	}
	
	/**
	 * Get the Provider's special Helper
	 * 
	 * @return An Helper instance
	 */
	public ProviderHelper getHelper() {
		return helper;
	}
	
	/**
	 * Main function called when starting to work
	 * 
	 * This function will use Internet, please call it in another thread!!!
	 * 
	 * @param searchQuery
	 *            If any, that an user input for searching custom content
	 * @return A map containing all result found. The map's key is the unique identifier, and the value is the result
	 * 
	 */
	public Map<String, SearchAndGoResult> work(String searchQuery) {
		Map<String, SearchAndGoResult> workmap;
		
		ProviderCallback.onProviderSearchStarting(this);
		
		try {
			workmap = processWork(searchQuery);
		} catch (Exception exception) {
			ProviderCallback.onProviderFailed(this, exception);
			return createEmptyWorkMap();
		}
		
		if (isAutosortEnabled()) {
			ProviderCallback.onProviderSorting(this);
			ResultScoreSorter.sortWorkmap(workmap, searchQuery, getHelper().getSearchEngine());
		}
		
		ProviderCallback.onProviderSearchFinished(this, workmap);
		
		return workmap;
	}
	
	/**
	 * Abstract function to ovveride, please don't call this function directly, autosort will not be supported
	 * 
	 * More info at {@link #work(String)}
	 * 
	 * @param searchQuery
	 *            User query
	 * @return A map containing all result found.
	 * 
	 */
	protected abstract Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception;
	
	/**
	 * Return an empty map for the {@link #work(String)} method
	 * 
	 * Default is an LinkedHashMap because it will kept the same order
	 * 
	 * @return An empty map
	 */
	protected static Map<String, SearchAndGoResult> createEmptyWorkMap() {
		return new LinkedHashMap<>();
	}
	
	/**
	 * Allow ui to get more data about a result
	 * 
	 * @param result
	 *            Target result
	 * @return A list of data, sorted by cardinal enum
	 */
	public List<AdditionalResultData> fetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = processFetchMoreData(result);
		
		if (isAutosortEnabled()) {
			Collections.sort(additionals, new Comparator<AdditionalResultData>() {
				@Override
				public int compare(AdditionalResultData additionalResultData1, AdditionalResultData additionalResultData2) {
					return additionalResultData1.getType().ordinal() - additionalResultData2.getType().ordinal();
				}
			});
		}
		
		return additionals;
	}
	
	/**
	 * Abstract function to ovveride, please don't call this function directly, autosort will not be supported
	 * 
	 * More info at {@link #fetchMoreData(String)}
	 * 
	 * 
	 * @param result
	 *            Target result
	 * @return A list of data
	 */
	protected abstract List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result);
	
	/**
	 * Allow ui to get more content about a result
	 * 
	 * @param result
	 *            Target result
	 * @return A list of data, sorted by cardinal enum
	 */
	public List<AdditionalResultData> fetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = processFetchContent(result);
		
		if (isAutosortEnabled()) {
			Collections.sort(additionals, getContentComparator());
		}
		
		return additionals;
	}
	
	/**
	 * Abstract function to ovveride, please don't call this function directly, autosort will not be supported
	 * 
	 * More info at {@link #fetchContent(String)}
	 * 
	 * 
	 * @param result
	 *            Target result
	 * @return A list of data
	 */
	protected abstract List<AdditionalResultData> processFetchContent(SearchAndGoResult result);
	
	/**
	 * Return the comparator for sorting more content items
	 * 
	 * @return Custom comparator
	 */
	protected Comparator<AdditionalResultData> getContentComparator() {
		return new Comparator<AdditionalResultData>() {
			@Override
			public int compare(AdditionalResultData result1, AdditionalResultData result2) {
				return 0;
			}
		};
	}
	
	/**
	 * Return an empty list for the {@link #fetchMoreData(SearchAndGoResult)} method
	 * 
	 * @return An empty list
	 */
	protected static List<AdditionalResultData> createEmptyAdditionalResultDataList() {
		return new ArrayList<>();
	}
	
	/**
	 * Every title present on the site are accessible on any page
	 * 
	 * Exemple: JetAnime
	 * 
	 * @return If its possible or not
	 */
	public boolean canExtractEverythingOnce() {
		return false;
	}
	
	/**
	 * Return if the site support cache, TRUE by default
	 * 
	 * @return If the cache is supported
	 */
	public boolean isCacheSupported() {
		return true;
	}
	
	/**
	 * Get the site name
	 * 
	 * @return Name
	 */
	public String getSiteName() {
		return siteName;
	}
	
	/**
	 * Get the site url
	 * 
	 * @return Url
	 */
	public String getSiteUrl() {
		return siteUrl;
	}
	
	/**
	 * Get if the auto-sort is enabled or not
	 * 
	 * @return True or false
	 */
	public boolean isAutosortEnabled() {
		return autosort;
	}
	
	/**
	 * Enable auto-sorting
	 */
	public void autosort() {
		autosort(true);
	}
	
	/**
	 * Enable or disable auto-sorting
	 * 
	 * Default: TRUE
	 * 
	 * @param autosort
	 *            New value
	 */
	public void autosort(boolean autosort) {
		this.autosort = autosort;
	}
	
	/**
	 * Create a new provider context
	 * 
	 * @param className
	 *            Class of the provider
	 * @return A new context of the provider asked
	 * @throws IllegalArgumentException
	 *             If any error append
	 */
	public static SearchAndGoProvider createContext(Class<? extends SearchAndGoProvider> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception exception) {
			throw new IllegalArgumentException(exception);
		}
	}
	
	public static Map<String, SearchAndGoResult> provide(List<SearchAndGoProvider> providers, String query, boolean autosort) throws Exception {
		ProviderCallback.onSearchStarting();
		
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		try {
			for (SearchAndGoProvider provider : providers) {
				workmap.putAll(provider.work(query));
			}
		} catch (Exception exception) {
			ProviderCallback.onSearchFail(exception);
			throw exception;
		}
		
		if (autosort) {
			ResultScoreSorter.sortWorkmap(workmap, query, new SearchEngine());
		}
		
		ProviderCallback.onSearchFinished(workmap);
		
		return workmap;
	}
	
	/**
	 * Get a static helper, but without all feature like cache
	 * 
	 * @return A freshly helper instance
	 */
	public static ProviderHelper getStaticHelper() {
		return ProviderHelper.STATIC_HELPER;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "SearchAndGoProvider[siteName=" + siteName + ", siteUrl=" + siteUrl + ", searchCapability=" + searchCapability + ", helper=" + helper + "]";
	}
	
	/**
	 * Item information class
	 * 
	 * It contain, a full regex match, an url, and a name
	 */
	protected static class ResultItem {
		private String match, url, name, imageUrl, description, genre, view;
		
		/**
		 * Create new instance of a ResultItem
		 * 
		 * This constructor don't have imageUrl, description, genre, and view
		 * 
		 * @param match
		 *            Full matcher match
		 * @param url
		 *            Url found
		 * @param name
		 *            Name found
		 */
		public ResultItem(String match, String url, String name) {
			this(match, url, name, null, null, null, null);
		}
		
		/**
		 * Create new instance of a ResultItem
		 * 
		 * This constructor don't have description, genre, and view
		 * 
		 * @param match
		 *            Full matcher match
		 * @param url
		 *            Url found
		 * @param name
		 *            Name found
		 * @param imageUrl
		 *            Image url found
		 */
		public ResultItem(String match, String url, String name, String imageUrl) {
			this(match, url, name, imageUrl, null, null, null);
		}
		
		/**
		 * Create new instance of a ResultItem
		 * 
		 * @param match
		 *            Full matcher match
		 * @param url
		 *            Url found
		 * @param name
		 *            Name found
		 * @param imageUrl
		 *            Image url found (or not)
		 * @param description
		 *            Description found (or not)
		 * @param genre
		 *            Genre list found (or not)
		 * @param view
		 *            View count found (or not)
		 */
		public ResultItem(String match, String url, String name, String imageUrl, String description, String genre, String view) {
			this.match = match;
			this.url = url;
			this.name = name;
			this.imageUrl = imageUrl;
			this.description = description;
			this.genre = genre;
			this.view = view;
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
		
		public String getImageUrl() {
			return imageUrl;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getGenre() {
			return genre;
		}
		
		public String getView() {
			return view;
		}
	}
	
}