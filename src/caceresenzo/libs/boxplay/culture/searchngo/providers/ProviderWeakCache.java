package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import caceresenzo.libs.string.StringUtils;

public class ProviderWeakCache {
	
	/* Constants */
	public static final int MAX_CACHE_CONTENT_SIZE = 30;
	
	/* Cache */
	private static SoftReference<HashMap<String, String>> WEAK_CACHE_REFERENCE;
	
	/* Execution */
	static {
		checkInstance();
	}
	
	/* Constructor */
	private ProviderWeakCache() {
		throw new IllegalStateException("Can't instanciate");
	}
	
	/**
	 * Add value to the weak cache.<br>
	 * Please remember that this cache is volatile, sometimes the garbage collector will remove everything.<br>
	 * If any of the key of the value, is not validated with {@link StringUtils#validate(String...)}, nothing will be added to the cache.<br>
	 * <br>
	 * If the cache size is more than {@link #MAX_CACHE_CONTENT_SIZE}, the first key of the map will be deleted.
	 * 
	 * @param key
	 *            Cache data key
	 * @param value
	 *            Data to cache
	 */
	public static void push(String key, String value) {
		checkInstance();
		
		if (!StringUtils.validate(key, value)) {
			return;
		}
		
		getCacheMap().put(key, value);
		
		while (getCacheMap().size() > MAX_CACHE_CONTENT_SIZE) {
			getCacheMap().remove(getCacheMap().keySet().iterator().next());
		}
	}
	
	/**
	 * Check if cached data is available from the key.<br>
	 * This will also be checking the validity of the cached value. Sometimes, cached value are nulls but key tell that they are valid.
	 * 
	 * @param key
	 *            Cache data key
	 * @return True if has data, false if not
	 */
	public static boolean check(String key) {
		checkInstance();
		
		if (getCacheMap().containsKey(key)) {
			if (!StringUtils.validate(getCacheMap().get(key))) {
				getCacheMap().remove(key);
				
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Call {@link #check(String)} and validate the content returned by {@link #get(String)}.<br>
	 * 
	 * @param key
	 *            Cache data key
	 * @return True if cached data is valid and present, false if not
	 */
	public static boolean checkAndValidate(String key) {
		return check(key) && StringUtils.validate(get(key));
	}
	
	/**
	 * Get cached data if present, return null if no data is found with the key.
	 * 
	 * @param key
	 *            Cache data key
	 * @return Content if has data, null if not
	 */
	public static String get(String key) {
		if (check(key)) {
			return getCacheMap().get(key);
		}
		
		return null;
	}
	
	/**
	 * Clear cached values.
	 */
	public static void clear() {
		checkInstance();
		
		getCacheMap().clear();
		WEAK_CACHE_REFERENCE = null;
		checkInstance();
	}
	
	/**
	 * Get memory size of the cache and clear cached values.
	 * 
	 * @return Cache memory size.
	 */
	public static int computeMemorySizeAndDestroy() {
		checkInstance();
		
		int size = computeCacheMemorySize();
		
		clear();
		
		return size;
	}
	
	/**
	 * Get the actual cache entries count.
	 * 
	 * @return Map's size
	 */
	public static int cacheSize() {
		checkInstance();
		
		return getCacheMap().size();
	}
	
	/**
	 * Compute an aproximative octe count used by the cache.
	 * 
	 * @return Map's memory size in byte
	 */
	public static int computeCacheMemorySize() {
		checkInstance();
		
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			
			objectOutputStream.writeObject(WEAK_CACHE_REFERENCE.get());
			objectOutputStream.close();
			
			/* 82 is memory size for an empty map */
			int size = byteArrayOutputStream.size() - 82;
			byteArrayOutputStream.reset();
			byteArrayOutputStream.close();
			
			return size;
		} catch (IOException exception) {
			return -1;
		}
	}
	
	/**
	 * @return the {@link Map#toString()} function called with the cache map.
	 */
	public static String convertToString() {
		checkInstance();
		
		return getCacheMap().toString();
	}
	
	/**
	 * @return The weak cache map reference, can be null.
	 */
	private static Map<String, String> getCacheMap() {
		return WEAK_CACHE_REFERENCE.get();
	}
	
	/**
	 * Check {@link SoftReference} and {@link Map} instances validity.
	 */
	private static void checkInstance() {
		if (WEAK_CACHE_REFERENCE == null || getCacheMap() == null) {
			WEAK_CACHE_REFERENCE = new SoftReference<>(new HashMap<String, String>());
		}
	}
	
}