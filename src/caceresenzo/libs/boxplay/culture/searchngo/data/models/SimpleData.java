package caceresenzo.libs.boxplay.culture.searchngo.data.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import caceresenzo.libs.json.JsonAware;
import caceresenzo.libs.json.JsonObject;

/**
 * Base class for data.
 * 
 * @author Enzo CACERES
 */
public class SimpleData implements Serializable, JsonAware {
	
	/* Json Key */
	public static final String JSON_KEY_CLASS = "class";
	public static final String JSON_KEY_KIND = "kind";
	public static final String JSON_KEY_OBJECT = "object";
	
	/* Constants */
	public static final String REQUIRE_HTTP_HEADERS_COMPLEMENT = "require_http_headers";
	
	/* Variables */
	private final String kind;
	private Map<String, Object> complements;
	
	/* Constructor */
	public SimpleData(String kind) {
		this.kind = kind;
	}
	
	/** Create the complements map if it hasn't been created yet. */
	protected void checkComplements() {
		if (!hasInitializedComplements()) {
			complements = new HashMap<>();
		}
	}
	
	/** @return Weather or not the complements map is <code>null</code> or not. */
	public boolean hasInitializedComplements() {
		return complements != null;
	}
	
	/**
	 * Add a complements data to this data instance.
	 * 
	 * @param key
	 *            Compement's key.
	 * @param value
	 *            Compement's data.
	 * @return Itself.
	 */
	public SimpleData complements(String key, Object value) {
		checkComplements();
		
		if (!complements.containsKey(key)) {
			complements.put(key, value);
		}
		
		return this;
	}
	
	/**
	 * Get a complement by its key.
	 * 
	 * @param key
	 *            Compement's key.
	 * @param defaultValue
	 *            Returned value if complement is <code>null</code>.
	 * @return Found value or <code>null</code> if not found.
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
	 * Same as {@link #getComplement(String, Object)} but will return null if not found.
	 * 
	 * @param key
	 *            Compement's key.
	 * @return Found value or <code>null</code> if not found.
	 * @see #getComplement(String, Object) getComplement(key, defaultValue)
	 */
	public Object getComplement(String key) {
		return getComplement(key, null);
	}
	
	/** @return Data's kind. */
	public String getKind() {
		return kind;
	}
	
	/** @return A {@link JsonObject json object} supposed to represent this {@link SimpleData} instance. */
	public JsonObject toJsonObject() {
		return new JsonObject();
	}
	
	@Override
	public String toJsonString() {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.put(JSON_KEY_CLASS, getClass().getSimpleName());
		jsonObject.put(JSON_KEY_KIND, kind);
		jsonObject.put(JSON_KEY_OBJECT, toJsonObject());
		
		return jsonObject.toJsonString();
	}
	
}