package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.Arrays;
import java.util.List;

/**
 * Class for holding information about the capacity and the capability of a provider
 * 
 * @author Enzo CACERES
 */
public class ProviderSearchCapability {
	
	private List<SearchCapability> capabilities;
	
	/**
	 * Constructor of ProviderSearchCapability
	 * 
	 * @param capabilities
	 *            Array of SearchCapability that must be supported by the site
	 * @throws IllegalArgumentException
	 *             If capabilities is empty or null
	 */
	public ProviderSearchCapability(SearchCapability[] capabilities) {
		if (capabilities == null || capabilities.length == 0) {
			throw new IllegalArgumentException("Capability array can't be null or empty");
		}
		
		this.capabilities = Arrays.asList(capabilities);
	}
	
	/**
	 * 
	 * 
	 * @param array
	 * @return
	 */
	public boolean search(SearchCapability[] array) {
		return search(Arrays.asList(array));
	}
	
	/**
	 * 
	 * 
	 * @param list
	 * @return
	 */
	public boolean search(List<SearchCapability> list) {
		for (SearchCapability searchCapability : list) {
			if (search(searchCapability)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param capability
	 * @return
	 */
	public boolean search(SearchCapability capability) {
		return capabilities.contains(capability);
	}
	
	/**
	 * Enum class referencing all supported capability available for providers
	 * 
	 * @author Enzo CACERES
	 */
	public static enum SearchCapability {
		ANIME, SERIES, MANGA, MOVIE, DEFAULT;
		
		private SearchCapability() {
			;
		}
	}
	
}