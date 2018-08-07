package caceresenzo.libs.boxplay.culture.searchngo;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.models.element.Imagable;

/**
 * Class containing some displayable information for the frond-end
 * 
 * @author Enzo CACERES
 */
public class SearchAndGoResult extends Imagable {
	
	private SearchAndGoProvider parentProvider;
	private String name, url;
	private SearchCapability type;
	
	public SearchAndGoResult(SearchAndGoProvider parentProvider, String name, String url) {
		this(parentProvider, name, url, null, SearchCapability.DEFAULT);
	}
	
	public SearchAndGoResult(SearchAndGoProvider parentProvider, String name, String url, String imageUrl, SearchCapability type) {
		this.parentProvider = parentProvider;
		this.name = name;
		this.url = url;
		this.imageUrl = imageUrl;
		this.type = type;
	}
	
	/**
	 * Get the provider that class were create with
	 * 
	 * @return The parent provider
	 */
	public SearchAndGoProvider getParentProvider() {
		return parentProvider;
	}
	
	/**
	 * Get a displayable name
	 * 
	 * @return A name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get a usable url, used as a key of the work map
	 * 
	 * @return An url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Get the type of this result {@link SearchCapability}
	 * 
	 * @return The linked type-capability of the result
	 */
	public SearchCapability getType() {
		return type;
	}
	
}