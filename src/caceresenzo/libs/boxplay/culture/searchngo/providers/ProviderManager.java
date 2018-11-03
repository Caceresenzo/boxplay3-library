package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.AdkamiSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.FullStreamCoSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.JetAnimeSearchAndGoAnimeProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.MangaLelSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.ScanMangaSearchAndGoMangaProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.FilmStreamingVkProSearchAndGoVideoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategy;

/**
 * Manager class to easily create context and get all provider currently available
 * 
 * @author Enzo CACERES
 */
public enum ProviderManager {
	
	JETANIME(JetAnimeSearchAndGoAnimeProvider.class), //
	ADKAMI(AdkamiSearchAndGoVideoProvider.class), //
	FILMSTREAMINGVK_PRO(FilmStreamingVkProSearchAndGoVideoProvider.class), //
	FULLSTREAM_CO(FullStreamCoSearchAndGoVideoProvider.class), //
	MANGALEL(MangaLelSearchAndGoMangaProvider.class), //
	SCANMANGA(ScanMangaSearchAndGoMangaProvider.class); //
	
	private Class<? extends SearchAndGoProvider> providerClass;
	
	private ProviderManager(Class<? extends SearchAndGoProvider> providerClass) {
		this.providerClass = providerClass;
	}
	
	/**
	 * Create a new {@link SearchAndGoProvider} instance
	 * 
	 * @return The {@link SearchAndGoProvider} instance
	 */
	public SearchAndGoProvider create() {
		return SearchAndGoProvider.createContext(providerClass);
	}
	
	public static List<SearchAndGoProvider> createAll() {
		return createAll(EnumSet.noneOf(ProviderFlags.class));
	}
	
	/**
	 * Create every {@link SearchAndGoProvider} and return it in a list
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
	 * Apply the same {@link SearchStrategy} to a list of {@link SearchAndGoProvider}
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
	
}