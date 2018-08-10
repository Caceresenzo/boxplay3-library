package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.Arrays;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.viewer.ViewerType;

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
	 * Search if a array of capabilities is available in this instance
	 * 
	 * @param array
	 *            The capabilities
	 * @return True or false
	 */
	public boolean search(SearchCapability[] array) {
		return search(Arrays.asList(array));
	}
	
	/**
	 * Search if a list of capabilities is available in this instance
	 * 
	 * @param list
	 *            The capabilities
	 * @return True or false
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
	 * Search if the capability is available in this instance
	 * 
	 * @param capability
	 *            The capability
	 * @return True or false
	 */
	public boolean search(SearchCapability capability) {
		return capabilities.contains(capability);
	}
	
	/**
	 * To string method
	 */
	@Override
	public String toString() {
		return "ProviderSearchCapability[capabilities=" + capabilities + "]";
	}
	
	/**
	 * Enum class referencing all supported capability available for providers
	 * 
	 * @author Enzo CACERES
	 */
	public static enum SearchCapability {
		ANIME(ViewerType.VIDEO), //
		SERIES(ViewerType.VIDEO), //
		MANGA(ViewerType.IMAGE), //
		MOVIE(ViewerType.VIDEO), //
		DEFAULT(null); //
		
		private ViewerType viewerType;
		
		private SearchCapability(ViewerType viewerType) {
			this.viewerType = viewerType;
		}
		
		/**
		 * Get the supposed viewer used to display this result
		 * 
		 * @return The ViewerType
		 */
		public ViewerType getViewerType() {
			return viewerType;
		}
	}
	
}