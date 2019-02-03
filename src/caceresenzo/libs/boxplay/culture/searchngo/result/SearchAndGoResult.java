package caceresenzo.libs.boxplay.culture.searchngo.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.models.element.Imagable;
import caceresenzo.libs.boxplay.mylist.MyListable;
import caceresenzo.libs.boxplay.mylist.binder.ListItemBinder;
import caceresenzo.libs.boxplay.mylist.binder.implementations.JsonListItemBinder;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.string.StringUtils;

/**
 * Class containing some displayable information for the frond-end
 * 
 * @author Enzo CACERES
 */
public class SearchAndGoResult extends Imagable implements MyListable {
	
	/* Constants */
	public static final int NO_SCORE = -1;
	
	/* Variables */
	private SearchAndGoProvider parentProvider;
	private String name, url;
	private SearchCapability type;
	private Map<String, String> requireHeaders;
	
	private int score;
	private String description;
	
	private String subscriberTargetUrl;
	
	/**
	 * Create a new {@link SearchAndGoResult} instance
	 * 
	 * @param parentProvider
	 *            Parent {@link SearchAndGoProvider} used to create this item
	 * @param name
	 *            Name (to display)
	 * @param url
	 *            Url found (used as a key for exemple)
	 */
	public SearchAndGoResult(SearchAndGoProvider parentProvider, String name, String url) {
		this(parentProvider, name, url, null, SearchCapability.DEFAULT);
	}
	
	/**
	 * Create a new {@link SearchAndGoResult} instance
	 * 
	 * @param parentProvider
	 *            Parent {@link SearchAndGoProvider} used to create this item
	 * @param name
	 *            Name (to display)
	 * @param url
	 *            Url found (used as a key for exemple)
	 * @param imageUrl
	 *            Directly add a image url
	 */
	public SearchAndGoResult(SearchAndGoProvider parentProvider, String name, String url, String imageUrl) {
		this(parentProvider, name, url, imageUrl, SearchCapability.DEFAULT);
	}
	
	/**
	 * Create a new {@link SearchAndGoResult} instance
	 * 
	 * @param parentProvider
	 *            Parent {@link SearchAndGoProvider} used to create this item
	 * @param name
	 *            Name (to display)
	 * @param url
	 *            Url found (used as a key for exemple)
	 * @param imageUrl
	 *            Directly add a image url
	 * @param type
	 *            {@link SearchCapability] type of the result
	 */
	public SearchAndGoResult(SearchAndGoProvider parentProvider, String name, String url, String imageUrl, SearchCapability type) {
		this.parentProvider = parentProvider;
		this.name = name;
		this.url = url;
		this.imageUrl = imageUrl;
		this.type = type;
		
		this.score = NO_SCORE;
	}
	
	/**
	 * Get the {@link SearchAndGoProvider} that class were create with
	 * 
	 * @return The parent {@link SearchAndGoProvider}
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
	
	/**
	 * Says if the score value has been already modified or not
	 * 
	 * @return True of false
	 */
	public boolean hasAlreadyBeenRated() {
		return score != -1;
	}
	
	/**
	 * Get the rating score of this result
	 * 
	 * @return The rating score
	 */
	public int score() {
		return score;
	}
	
	/**
	 * Set a new rating score
	 * 
	 * @param score
	 *            Target new rating score
	 * @return Itself
	 */
	public SearchAndGoResult score(int score) {
		this.score = score;
		return this;
	}
	
	/**
	 * Check if the description attached to this result is valid.
	 * 
	 * @return Validity state
	 */
	public boolean hasDescription() {
		return StringUtils.validate(description);
	}
	
	/**
	 * Attach a description to this result.
	 * 
	 * @param description
	 *            Target description.
	 * @return Itself
	 */
	public SearchAndGoResult describe(String description) {
		this.description = description;
		
		return this;
	}
	
	/**
	 * @return Attached description of this result
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Create a new map if necessary
	 */
	private void checkRequireHeaders() {
		if (requireHeaders == null) {
			requireHeaders = new HashMap<>();
		}
	}
	
	/**
	 * Get the actual headers
	 * 
	 * @return {@link #requireHeaders} or null
	 */
	public Map<String, String> getRequireHeaders() {
		return requireHeaders;
	}
	
	/**
	 * Same has {@link #getRequireHeaders()}, but this time get a {@link Map} with {@link String} as keys and {@link Object} as values.
	 * 
	 * @return {@link #requireHeaders} or null.
	 * @see #getRequireHeaders()
	 */
	public Map<String, Object> getRequireHeaders2() {
		Map<String, Object> convertedMap = null;
		if (requireHeaders != null) {
			convertedMap = new HashMap<>();
			
			for (Entry<String, String> entry : requireHeaders.entrySet()) {
				convertedMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		return convertedMap;
	}
	
	/**
	 * Add a single header value to the list
	 * 
	 * @param key
	 *            Header key
	 * @param value
	 *            Header value
	 * @return Itself
	 */
	public SearchAndGoResult requireHeader(String key, Object value) {
		checkRequireHeaders();
		
		requireHeaders.put(key, String.valueOf(value));
		
		return this;
	}
	
	/**
	 * Add a full map that will be used in the header when doing the request
	 * 
	 * @param requireHeaders
	 *            Target map you want to add
	 * @return Itself
	 */
	public SearchAndGoResult requireHeaders(Map<String, String> requireHeaders) {
		checkRequireHeaders();
		
		this.requireHeaders.putAll(requireHeaders);
		
		return this;
	}
	
	/**
	 * Make this {@link SearchAndGoResult} subscribable.
	 * 
	 * @param url
	 *            Target subscribable url (like RSS or directly html).
	 */
	public void subscribableAt(String url) {
		this.subscriberTargetUrl = url;
	}
	
	/**
	 * @return If this {@link SearchAndGoResult} is ready to be subscribed.
	 * @see #subscribableAt(Class, String)
	 */
	public boolean isSubscribable() {
		return subscriberTargetUrl != null;
	}
	
	public String getSubscriberTargetUrl() {
		return subscriberTargetUrl;
	}
	
	@Override
	public String toUniqueString() {
		return String.format("%s:%s", type, name);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ListItemBinder createCompatibleBinder() {
		return new SearchAndGoResult.ItemBinder();
	}
	
	@Override
	public String toString() {
		return "SearchAndGoResult[parentProvider=" + parentProvider + ", name=" + name + ", url=" + url + ", type=" + type + ", score=" + score + "]";
	}
	
	/**
	 * Item binder for {@link SearchAndGoResult}.
	 * 
	 * @author Enzo CACERES
	 */
	public static class ItemBinder extends JsonListItemBinder<SearchAndGoResult> {
		
		/* Kind */
		public static final String KIND = SearchAndGoResult.class.getSimpleName();
		
		/* Json keys */
		public static final String JSON_KEY_PARENT_PROVIDER = "parent_provider";
		public static final String JSON_KEY_NAME = "name";
		public static final String JSON_KEY_IMAGE_URL = "image_url";
		public static final String JSON_KEY_URL = "url";
		public static final String JSON_KEY_TYPE = "type";
		
		@Override
		protected JsonObject convert(SearchAndGoResult item) {
			JsonObject jsonObject = new JsonObject();
			
			jsonObject.put(JSON_KEY_PARENT_PROVIDER, item.getParentProvider().getSourceManager().toString());
			jsonObject.put(JSON_KEY_NAME, item.getName());
			jsonObject.put(JSON_KEY_IMAGE_URL, item.getImageUrl());
			jsonObject.put(JSON_KEY_URL, item.getUrl());
			jsonObject.put(JSON_KEY_TYPE, item.getType().toString());
			
			return jsonObject;
		}
		
		@Override
		protected SearchAndGoResult restore(JsonObject source) {
			ProviderManager parentManager = ProviderManager.fromString(source.getString(JSON_KEY_PARENT_PROVIDER));
			
			if (parentManager == null) {
				return null;
			}
			
			SearchAndGoProvider parentProvider = parentManager.create();
			String name = source.getString(JSON_KEY_NAME);
			String imageUrl = source.getString(JSON_KEY_IMAGE_URL);
			String url = source.getString(JSON_KEY_URL);
			SearchCapability type = SearchCapability.fromString(source.getString(JSON_KEY_TYPE));
			
			return new SearchAndGoResult(parentProvider, name, url, imageUrl, type);
		}
		
		@Override
		public String getKind() {
			return KIND;
		}
	}
	
}