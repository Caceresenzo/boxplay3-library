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
	
	/* Variables */
	private List<SearchCapability> capabilities;
	
	/**
	 * Constructor of ProviderSearchCapability
	 * 
	 * @param capabilities
	 *            Array of SearchCapability that must be supported by the site
	 * @throws IllegalArgumentException
	 *             If capabilities is empty or null
	 */
	public ProviderSearchCapability(SearchCapability... capabilities) {
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
	
	public static ProviderSearchCapability fromArray(SearchCapability... capabilities) {
		return new ProviderSearchCapability(capabilities);
	}
	
	/**
	 * Enum class referencing all supported capability available for providers.
	 * 
	 * @author Enzo CACERES
	 */
	public static enum SearchCapability implements Serializable {
		/* Parent */
		VIDEO(ContentViewerType.VIDEO), //
		MUSIC(ContentViewerType.MUSIC), //
		IMAGE(ContentViewerType.IMAGE), //
		
		/* Video */
		ANIME(ContentViewerType.VIDEO, VIDEO), //
		DRAMA(ContentViewerType.VIDEO, VIDEO), //
		SERIES(ContentViewerType.VIDEO, VIDEO), //
		MOVIE(ContentViewerType.VIDEO, VIDEO), //
		HENTAI(ContentViewerType.VIDEO, VIDEO), //
		TOKUSATSU(ContentViewerType.VIDEO, VIDEO), //
		
		/* Audio */
		OST(ContentViewerType.MUSIC, MUSIC), //
		
		/* Image */
		MANGA(ContentViewerType.IMAGE, IMAGE), //
		
		/* Other */
		DEFAULT(null); //
		
		/* Variables */
		private final ContentViewerType viewerType;
		private final SearchCapability[] parents;
		
		/* Constructor */
		private SearchCapability(ContentViewerType viewerType, SearchCapability... parents) {
			this.viewerType = viewerType;
			this.parents = parents;
		}
		
		/**
		 * Get the supposed viewer used to display this result.
		 * 
		 * @return Needed {@link ContentViewerType}.
		 */
		public ContentViewerType getViewerType() {
			return viewerType;
		}
		
		/**
		 * Get {@link SearchCapability} parent that are supposed the be able to be used as a common replacer.
		 * 
		 * @return Parent array.
		 */
		public SearchCapability[] getParents() {
			return parents;
		}
		
		/**
		 * Get a {@link SearchCapability} instance by a string.
		 * 
		 * @param string
		 *            Target string.
		 * @return {@link SearchCapability} if found, {@link #DEFAULT} if not.
		 */
		public static SearchCapability fromString(String string) {
			for (SearchCapability capability : values()) {
				if (capability.toString().equalsIgnoreCase(string)) {
					return capability;
				}
			}
			
			return DEFAULT;
		}
	}
	
}