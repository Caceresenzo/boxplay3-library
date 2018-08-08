package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.LinkedHashMap;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

/**
 * Provider class
 * 
 * This is an abstract class that need to be extend. Once extend, do all your data-gathering for SEARCHING ONLY in the {@link #processWork(String)} function
 * 
 * @author Enzo CACERES
 */
public abstract class SearchAndGoProvider {
	
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
		Map<String, SearchAndGoResult> workmap = work(searchQuery);
		
		if (isAutosortEnabled()) {
			ResultScoreSorter.sortWorkmap(workmap, searchQuery, getHelper().getSearchEngine());
		}
		
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
	protected abstract Map<String, SearchAndGoResult> processWork(String searchQuery);
	
	/**
	 * Overridable, return an empty map for the work method
	 * 
	 * Default is an LinkedHashMap because it will kept the same order
	 * 
	 * @return An empty map
	 */
	protected Map<String, SearchAndGoResult> createEmptyResultMap() {
		return new LinkedHashMap<>();
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
	
	/**
	 * Get a static helper, but without all feature like cache
	 * 
	 * @return A freshly helper instance
	 */
	public static ProviderHelper getStaticHelper() {
		return new ProviderHelper();
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
		private String match, url, name, description, genre, view;
		
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
		 */
		public ResultItem(String match, String url, String name) {
			this(match, url, name, null, null, null);
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
		 * @param description
		 *            Description found (or not)
		 * @param genre
		 *            Genre list found (or not)
		 * @param view
		 *            View count found (or not)
		 */
		public ResultItem(String match, String url, String name, String description, String genre, String view) {
			this.match = match;
			this.url = url;
			this.name = name;
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