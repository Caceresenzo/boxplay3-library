package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.CallbackDelegate;
import caceresenzo.libs.boxplay.culture.searchngo.callback.delegate.implementations.SilentCallbackDelegate;
import caceresenzo.libs.boxplay.culture.searchngo.content.IContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.exceptions.ProviderException;
import caceresenzo.libs.boxplay.culture.searchngo.requirements.BaseSystemRequirement;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.http.client.webb.Request;
import caceresenzo.libs.http.client.webb.Webb;

/**
 * Provider class.<br>
 * This is an abstract class that need to be extend.<br>
 * Once extend, do all your data-gathering for SEARCHING ONLY in the {@link #processWork(String)} function.
 * 
 * @author Enzo CACERES
 */
public abstract class SearchAndGoProvider implements IContentProvider {
	
	/* Constants */
	public static final int NO_VALUE = Integer.MIN_VALUE;
	public static final byte NO_BYTE_VALUE = Byte.MIN_VALUE;
	
	public static final String CHARSET_UTF_8 = "UTF-8";
	public static final String CHARSET_LATIN_1 = "ISO-8859-1";
	
	public static final byte MAP_INDEX_NORMAL = 1;
	public static final byte MAP_INDEX_TO_EXTRACT = 1;
	
	/* Deprecated */
	@Deprecated /* TODO Change most of the older system */
	protected final Map<AdditionalDataType, String> ADDITIONAL_DATA_CORRESPONDANCE = new EnumMap<>(AdditionalDataType.class);
	
	/* Variables */
	private final String siteName, siteUrl;
	private final ProviderSearchCapability searchCapability;
	private final ProviderHelper helper;
	private boolean autosort;
	private List<BaseSystemRequirement> requirements;
	private byte selectedCorrespondenceMapIndex;
	private Map<Byte, Map<AdditionalDataType, String>> dataCorrespondence;
	
	/* Constructor */
	protected SearchAndGoProvider(String siteName, String siteUrl) {
		this.siteName = siteName;
		this.siteUrl = siteUrl;
		this.searchCapability = createSearchCapability();
		
		this.helper = new ProviderHelper(this);
		
		this.autosort = true;
		
		this.selectedCorrespondenceMapIndex = NO_BYTE_VALUE;
		
		initialize();
	}
	
	/** Called at the end of the constructor. */
	protected void initialize() {
		;
	}
	
	/** @return Create a {@link ProviderSearchCapability} instance that will be used to help the user chose what he want to search. */
	protected abstract ProviderSearchCapability createSearchCapability();
	
	/** @return Stored search capabilities created with {@link #createSearchCapability()}. */
	public ProviderSearchCapability getSearchCapability() {
		return searchCapability;
	}
	
	/** @return Get the provider's special {@link ProviderHelper}. */
	public ProviderHelper getHelper() {
		return helper;
	}
	
	/**
	 * Add to the local provider's requirements list a specified requirement.
	 * 
	 * @param requirementClass
	 *            Target requirement class.
	 */
	protected void require(Class<? extends BaseSystemRequirement> requirementClass) {
		if (requirements == null) {
			requirements = new ArrayList<>();
		}
		
		try {
			requirements.add(requirementClass.newInstance());
		} catch (Exception exception) {
			throw new IllegalStateException(exception);
		}
	}
	
	/**
	 * Get requirement instance by its class.
	 * 
	 * @param requirementClass
	 *            Target requirement class.
	 * @return Target instance.
	 * @throws IllegalStateException
	 *             If you try to get a requirement without calling {@link #require(Class)} in the constructor before.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends BaseSystemRequirement> T getRequirement(Class<T> requirementClass) {
		if (requirements != null) {
			for (BaseSystemRequirement requirement : requirements) {
				if (requirement.getClass().equals(requirementClass)) {
					return (T) requirement;
				}
			}
		}
		
		throw new IllegalStateException("Can't get a requirement that has not been required before.");
	}
	
	/**
	 * Main function called when starting to work.<br>
	 * This function will use Internet, please call it in another thread.
	 * 
	 * @param searchQuery
	 *            If any, that an user input for searching custom content.
	 * @param callbackDelegate
	 *            Callback delegater
	 * @return A map containing all result found. The map's key is the unique identifier, and the value is the result.
	 */
	public Map<String, SearchAndGoResult> work(String searchQuery, CallbackDelegate callbackDelegate) {
		Map<String, SearchAndGoResult> workmap;
		
		callbackDelegate.onProviderSearchStarting(this);
		
		try {
			workmap = processWork(searchQuery);
		} catch (Exception exception) {
			throw new ProviderException(exception);
		}
		
		if (isAutosortEnabled()) {
			callbackDelegate.onProviderSorting(this);
			ResultScoreSorter.sortWorkmap(workmap, searchQuery, getHelper().getSearchEngine());
		}
		
		callbackDelegate.onProviderSearchFinished(this, workmap);
		
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
	 * Please process information as they come (order) on the website
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
	 * Return if the provider need an advanced downloader to work, FALSE by default
	 * 
	 * @return If you want to use {@link Webb}
	 */
	public boolean isAdvancedDownloaderNeeded() {
		return false;
	}
	
	/**
	 * Return if the download should follow redirects.
	 * 
	 * @return If you want to use {@link Request#followRedirects(boolean)}.
	 */
	public boolean downloadShouldFollowRedirects() {
		return true;
	}
	
	/**
	 * Return if the provider need to use SSL excryption to work, <code>true</code> by default
	 * 
	 * @return If you want to kept using SSL.
	 */
	public boolean isSslNeeded() {
		return true;
	}
	
	/** @return Provider site's displayable name. */
	public String getSiteName() {
		return siteName;
	}
	
	/** @return Provider site's url. */
	public String getSiteUrl() {
		return siteUrl;
	}
	
	/** @return An array of the main language available on the site. */
	public ProviderLanguage.Language[] getMainLanguages() {
		return new ProviderLanguage.Language[] { ProviderLanguage.Language.FRENCH };
	}
	
	/**
	 * Get what charset the provider will use the most.<br>
	 * Default value is {@link #CHARSET_UTF_8}.
	 * 
	 * @return Used charset.
	 */
	public String getWorkingCharset() {
		return CHARSET_UTF_8;
	}
	
	/** @return Weather or not the auto-sorting is enabled. */
	public boolean isAutosortEnabled() {
		return autosort;
	}
	
	/** Enable auto-sorting. */
	public void autosort() {
		autosort(true);
	}
	
	/**
	 * Enable or disable auto-sorting. Default value is <code>true</code>.
	 * 
	 * @param autosort
	 *            New value.
	 * @return Itself.
	 */
	public SearchAndGoProvider autosort(boolean autosort) {
		this.autosort = autosort;
		
		return this;
	}
	
	/**
	 * Get the source {@link ProviderManager} used to create this instance of a {@link SearchAndGoProvider}.
	 * 
	 * @return Target {@link ProviderManager}
	 * @throws IllegalStateException
	 *             If the provider don't have a source {@link ProviderManager}. Should not happen.
	 */
	public ProviderManager getSourceManager() {
		ProviderManager manager = ProviderManager.fromClass(this.getClass().getSimpleName());
		
		if (manager == null) {
			throw new IllegalStateException("This provider don't have a source manager.");
		}
		
		return manager;
	}
	
	/**
	 * Select a correspondence map to be able to {@link #registerCorrespondence(AdditionalDataType, String) register} data type on it.
	 * 
	 * @param index
	 *            Target index to select.
	 */
	protected void selectMapIndex(byte index) {
		this.selectedCorrespondenceMapIndex = index;
	}
	
	/**
	 * Register a data correspondence here.<br>
	 * This don't do anything and it is completly optional, if the code don't have a correct implementation able to use this system.
	 * 
	 * @param dataType
	 *            Target {@link AdditionalDataType} to register.
	 * @param correspondence
	 *            Correspondence identifier.<br>
	 *            Used to easely get the <code>dataType</code> from this unique identifier.
	 * @throws IllegalStateException
	 *             If no correspondence map is selected.
	 * @see #selectMapIndex(byte)
	 */
	protected void registerCorrespondence(AdditionalDataType dataType, String correspondence) {
		if (selectedCorrespondenceMapIndex == NO_BYTE_VALUE) {
			throw new IllegalStateException("No map index selected.");
		}
		
		checkDataCorrespondenceMap();
		
		Map<AdditionalDataType, String> correspondenceMap = dataCorrespondence.get(selectedCorrespondenceMapIndex);
		if (correspondenceMap == null) {
			dataCorrespondence.put(selectedCorrespondenceMapIndex, correspondenceMap = new EnumMap<>(AdditionalDataType.class));
		}
		
		correspondenceMap.put(dataType, correspondence);
	}
	
	/**
	 * Get the content of a previously registered correspondence map.
	 * 
	 * @param index
	 *            Index of the correspondence map.
	 * @return Target correspondence instance.
	 * @throws IllegalStateException
	 *             If no correspondence map has previously been registered with this index.
	 * @see #registerCorrespondence(AdditionalDataType, String)
	 */
	protected Map<AdditionalDataType, String> getCorrespondenceMap(byte index) {
		checkDataCorrespondenceMap();
		
		Map<AdditionalDataType, String> correspondenceMap = dataCorrespondence.get(selectedCorrespondenceMapIndex);
		if (correspondenceMap == null) {
			throw new IllegalStateException("No map registered with this index.");
		}
		
		return correspondenceMap;
	}
	
	/**
	 * Create the correspondence map if the actual is null.
	 */
	private void checkDataCorrespondenceMap() {
		if (dataCorrespondence == null) {
			dataCorrespondence = new HashMap<>();
		}
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
	 * Same as {@link #provide(List, String, boolean, CallbackDelegate)} but with <code>ignoreException</code> set to <code>true</code>.
	 * 
	 * @see #provide(List, String, boolean, CallbackDelegate, boolean)
	 */
	public static Map<String, SearchAndGoResult> provide(List<SearchAndGoProvider> providers, String query, boolean autosort, CallbackDelegate callbackDelegate) throws Exception {
		return provide(providers, query, autosort, callbackDelegate, true);
	}
	
	/**
	 * Start fetching item from a <code>query</code> and a {@link List} of {@link SearchAndGoProvider}.
	 * 
	 * @param providers
	 *            A {@link List} of {@link SearchAndGoProvider} that will be used.
	 * @param query
	 *            Query string to search item.
	 * @param autosort
	 *            Weather or not a final sorting on all item will be applied.
	 * @param callbackDelegate
	 *            Callback deletagor to follow process.<br>
	 *            If <code>null</code>, an instance of a {@link SilentCallbackDelegate} will be used.
	 * @param ignoreException
	 *            If any exception throws by the {@link SearchAndGoProvider} have to be ignored or not.
	 * @return A {@link Map} of fetched result, can't be null.<br>
	 *         Keys are item's url, and values are the actual {@link SearchAndGoResult} coming from the {@link SearchAndGoProvider}.
	 * @throws Exception
	 *             If anything goes wrong.
	 */
	public static Map<String, SearchAndGoResult> provide(List<SearchAndGoProvider> providers, String query, boolean autosort, CallbackDelegate callbackDelegate, boolean ignoreException) throws Exception {
		if (callbackDelegate == null) {
			callbackDelegate = new SilentCallbackDelegate();
		}
		
		callbackDelegate.onSearchStarting();
		
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		try {
			for (SearchAndGoProvider provider : providers) {
				try {
					workmap.putAll(provider.work(query, callbackDelegate));
				} catch (Exception exception) {
					callbackDelegate.onProviderFailed(provider, exception);
					
					if (!ignoreException) {
						throw exception;
					}
				}
			}
		} catch (Exception exception) {
			callbackDelegate.onSearchFail(exception);
			throw exception;
		}
		
		if (autosort) {
			ResultScoreSorter.sortWorkmap(workmap, query, new SearchEngine());
		}
		
		callbackDelegate.onSearchFinished(workmap);
		
		return workmap;
	}
	
	/**
	 * @return The static {@link ProviderHelper} instance, but without feature like caching.
	 * @see ProviderHelper#STATIC_HELPER
	 */
	public static ProviderHelper getStaticHelper() {
		return ProviderHelper.STATIC_HELPER;
	}
	
	@Override
	public String toString() {
		return "SearchAndGoProvider[siteName=" + siteName + ", siteUrl=" + siteUrl + ", searchCapability=" + searchCapability + ", helper=" + helper + "]";
	}
	
	/**
	 * Item information class.<br>
	 * It contain, a full regex match, an url, a name and a lot of other thing.
	 */
	public static class ResultItem {
		private String match, url, name, imageUrl, description, genre, view;
		
		public ResultItem(String match, String url, String name) {
			this(match, url, name, null, null, null, null);
		}
		
		public ResultItem(String match, String url, String name, String imageUrl) {
			this(match, url, name, imageUrl, null, null, null);
		}
		
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