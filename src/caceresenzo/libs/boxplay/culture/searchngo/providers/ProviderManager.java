package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.AdkamiSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.AnimeUltimeSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.FilmStreamingVkProSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.FullStreamCoSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.JapScanSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.JetAnimeSearchAndGoAnimeProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.MangaLelSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.MangaNeloSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.ScanMangaSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategy;

/**
 * Manager class to easily create context and get all provider currently available
 * 
 * @author Enzo CACERES
 */
public enum ProviderManager {
	
	/* Anime */
	JETANIME(JetAnimeSearchAndGoAnimeProvider.class), //
	ADKAMI(AdkamiSearchAndGoVideoProvider.class), //
	ANIMEULTIME(AnimeUltimeSearchAndGoVideoProvider.class), //
	// IANIMES(IAnimesSearchAndGoVideoProvider.class), /* Broken, cloudflare bypass is not working */
	
	/* Movie / Series */
	FILMSTREAMINGVK_PRO(FilmStreamingVkProSearchAndGoVideoProvider.class), //
	FULLSTREAM_CO(FullStreamCoSearchAndGoVideoProvider.class), //
	// HDSS_TO(HdssToSearchAndGoVideoProvider.class), /* Broken, no extractor available */
	
	/* Manga */
	MANGALEL(MangaLelSearchAndGoMangaProvider.class), //
	SCANMANGA(ScanMangaSearchAndGoMangaProvider.class), //
	JAPSCAN(JapScanSearchAndGoMangaProvider.class), //
	MANGANELO(MangaNeloSearchAndGoMangaProvider.class); //
	
	/* Variables */
	private Class<? extends SearchAndGoProvider> providerClass;
	
	/* Constructor */
	private ProviderManager(Class<? extends SearchAndGoProvider> providerClass) {
		this.providerClass = providerClass;
	}
	
	/**
	 * Create a new {@link SearchAndGoProvider} instance.
	 * 
	 * @return The {@link SearchAndGoProvider} instance
	 */
	public SearchAndGoProvider create() {
		return SearchAndGoProvider.createContext(providerClass);
	}
	
	/**
	 * @return Attached class for this {@link ProviderManager}.
	 */
	public Class<? extends SearchAndGoProvider> getProviderClass() {
		return providerClass;
	}
	
	/**
	 * Create every {@link SearchAndGoProvider} actually available.
	 * 
	 * @return A list containing all {@link SearchAndGoProvider} instanced
	 */
	public static List<SearchAndGoProvider> createAll() {
		return createAll(EnumSet.noneOf(ProviderFlags.class));
	}
	
	/**
	 * Create every {@link SearchAndGoProvider} and return it in a list.
	 * 
	 * @return A list of all {@link SearchAndGoProvider} available
	 */
	public static List<SearchAndGoProvider> createAll(Set<ProviderFlags> flags) {
		List<SearchAndGoProvider> providers = new ArrayList<>();
		
		for (ProviderManager manager : ProviderManager.values()) {
			if (ProviderFlags.test(manager, flags)) {
				providers.add(manager.create());
			}
		}
		
		return providers;
	}
	
	/**
	 * Apply the same {@link SearchStrategy} to a list of {@link SearchAndGoProvider}.
	 * 
	 * @param providers
	 *            Your {@link SearchAndGoProvider} list
	 * @param newSearchStrategy
	 *            The new {@link SearchStrategy} you want to apply
	 */
	public static void applySearchStrategy(List<SearchAndGoProvider> providers, SearchStrategy newSearchStrategy) {
		for (SearchAndGoProvider provider : providers) {
			provider.getHelper().getSearchEngine().searchStrategy(newSearchStrategy);
		}
	}
	
	/**
	 * Get a {@link ProviderManager} instance by his {@link SearchAndGoProvider} class name.
	 * 
	 * @param className
	 *            Target class name.
	 * @return {@link ProviderManager} if found, null if not.
	 */
	public static ProviderManager fromClass(String className) {
		for (ProviderManager manager : values()) {
			if (manager.getProviderClass().getSimpleName().equals(className)) {
				return manager;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a {@link ProviderManager} instance by a string.
	 * 
	 * @param string
	 *            Target string.
	 * @return {@link ProviderManager} if found, null if not.
	 */
	public static ProviderManager fromString(String string) {
		for (ProviderManager manager : values()) {
			if (manager.toString().equalsIgnoreCase(string)) {
				return manager;
			}
		}
		
		return null;
	}
	
}