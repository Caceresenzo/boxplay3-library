package caceresenzo.libs.boxplay.culture.searchngo.providers;

import caceresenzo.libs.boxplay.culture.searchngo.providers.implementations.JetAnimeSearchAndGoProvider;

public enum ProviderManager {
	
	JETANIME(JetAnimeSearchAndGoProvider.class);
	
	private Class<? extends SearchAndGoProvider> providerClass;
	
	private ProviderManager(Class<? extends SearchAndGoProvider> providerClass) {
		this.providerClass = providerClass;
	}
	
	public SearchAndGoProvider create() {
		return SearchAndGoProvider.createContext(providerClass);
	}
	
}