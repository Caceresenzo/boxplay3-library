package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.LinkedHashMap;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.SearchAndGoResult;

public abstract class SearchAndGoProvider {
	
	private final String siteName, siteUrl;
	private final ProviderSearchCapability searchCapability;
	
	private final ProviderHelper helper;
	
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
	public abstract Map<String, SearchAndGoResult> work(String searchQuery);
	
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
	 * Create a new provider context
	 * 
	 * @param className
	 *            Class of the provider
	 * @return A new context of the provider asked
	 * @throws IllegalArgumentException
	 *             If the target class hasn't been found
	 */
	public static SearchAndGoProvider createContext(Class<? extends SearchAndGoProvider> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		
		throw new IllegalArgumentException("SearchAndGoProvider not found.");
	}
	
	public static ProviderHelper getStaticHelper() {
		return new ProviderHelper();
	}
	
}