package caceresenzo.libs.boxplay.culture.searchngo.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for data
 * 
 * @author Enzo CACERES
 */
public class SimpleData implements Serializable {
	
	public static final String REQUIRE_HTTP_HEADERS_COMPLEMENT = "require_http_headers";
	
	/* Maps */
	private Map<String, Object> complements;
	
	/**
	 * Private function to check if the maps hasn't been created yet
	 */
	private void checkComplements() {
		if (!hasInitializedComplements()) {
			complements = new HashMap<>();
		}
	}
	
	/**
	 * Check if the complement map has already been initialized or not
	 * 
	 * @return If the {@link #complements} map is null
	 */
	public boolean hasInitializedComplements() {
		return complements != null;
	}
	
	/**
	 * Add a complements data to this data instance
	 * 
	 * @param key
	 *            The key of your data
	 * @param value
	 *            The value of your complements
	 * @return Itself
	 */
	public SimpleData complements(String key, Object value) {
		checkComplements();
		
		if (!complements.containsKey(key)) {
			complements.put(key, value);
		}
		
		return this;
	}
	
	/**
	 * Get a complements by its key
	 * 
	 * @param key
	 *            Your complement key
	 * @param defaultValue
	 *            Returned value if complement not found
	 * @return Complement or default value
	 */
	public Object getComplement(String key, Object defaultValue) {
		if (!hasInitializedComplements()) {
			return defaultValue;
		}
		
		if (complements.containsKey(key)) {
			return complements.get(key);
		}
		
		return defaultValue;
	}
	
	/**
	 * Same as {@link #getComplement(String, Object)} but will return null if not found
	 * 
	 * @param key
	 *            Your complement key
	 * @return Complement or null
	 */
	public Object getComplement(String key) {
		return getComplement(key, null);
	}
	
}