package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;

/**
 * Class for holding information about the capacity and the capability of a provider
 * 
 * @author Enzo CACERES
 */
public class ProviderSearchCapability implements Serializable {
	
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
	public static enum SearchCapability implements Serializable {
		ANIME(ContentViewerType.VIDEO), //
		SERIES(ContentViewerType.VIDEO), //
		VIDEO(ContentViewerType.VIDEO), //
		MANGA(ContentViewerType.IMAGE), //
		MOVIE(ContentViewerType.VIDEO), //
		DEFAULT(null); //
		
		private ContentViewerType viewerType;
		
		private SearchCapability(ContentViewerType viewerType) {
			this.viewerType = viewerType;
		}
		
		/**
		 * Get the supposed viewer used to display this result
		 * 
		 * @return The ViewerType
		 */
		public ContentViewerType getViewerType() {
			return viewerType;
		}
	}
	
}