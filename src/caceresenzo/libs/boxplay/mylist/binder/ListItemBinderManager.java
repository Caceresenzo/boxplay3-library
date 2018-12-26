package caceresenzo.libs.boxplay.mylist.binder;

import java.util.HashMap;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class ListItemBinderManager {
	
	/* Static */
	private static final Map<Class<?>, ListItemBinder<?, ?>> BINDERS;
	
	static {
		BINDERS = new HashMap<>();
		
		bindBinder(SearchAndGoResult.class, new SearchAndGoResult.ItemBinder());
	}
	
	/**
	 * Bind a binder to a specific target class able to be created with this binder.
	 * 
	 * @param itemClass
	 *            Item class.
	 * @param targetBinder
	 *            Implemented {@link ListItemBinder} for the item class.
	 */
	public static void bindBinder(Class<?> itemClass, ListItemBinder<?, ?> targetBinder) {
		BINDERS.put(itemClass, targetBinder);
	}
	
	/**
	 * Check if a class has a corresponding binder.
	 * 
	 * @param itemClass
	 *            Target item class.
	 * @return <code>{@link #getCorrespondingBinder(Class)} != null</code>
	 */
	public static boolean hasCorrespondingBinder(Class<?> itemClass) {
		return getCorrespondingBinder(itemClass) != null;
	}
	
	/**
	 * Get a corresponding binder for an object class.
	 * 
	 * @param itemClass
	 *            Target item class.
	 * @return Corresponding {@link ListItemBinder}, null if not found.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ListItemBinder<?, T> getCorrespondingBinder(Class<T> itemClass) {
		return (ListItemBinder<?, T>) BINDERS.get(itemClass);
	}
	
}