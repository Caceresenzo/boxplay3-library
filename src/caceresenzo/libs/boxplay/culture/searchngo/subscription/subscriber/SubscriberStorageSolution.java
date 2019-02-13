package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.boxplay.mylist.MyListable;
import caceresenzo.libs.cryptography.MD5;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

public class SubscriberStorageSolution {
	
	/* Constant */
	public static final String EXTENSION = "json";
	
	/* Json Keys */
	public static final String JSON_KEY_CONTENT = "content";
	public static final String JSON_KEY_DATE = "date";
	public static final String JSON_KEY_URL = "url";
	
	/* Variables */
	protected File baseDirectory;
	
	/* Constructor */
	/**
	 * Create a storage solution.
	 * 
	 * @param baseDirectory
	 *            Base directory where file should be stored.
	 */
	public SubscriberStorageSolution(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}
	
	/**
	 * Check if the file given by the {@link #toFile(SearchAndGoResult)} function exists or not.
	 * 
	 * @param result
	 *            Target result to test.
	 * @return If the file exists.
	 * @see #toFile(SearchAndGoResult)
	 */
	public boolean hasStorage(SearchAndGoResult result) {
		return toFile(result).exists();
	}
	
	/**
	 * Get items that are stored locally. If no file are present, it will return an empty list.
	 * 
	 * @param result
	 *            Target result.
	 * @return A {@link List} of {@link SubscriptionItem} reconstructed from local file.
	 */
	public List<SubscriptionItem> getLocalStorageItems(SearchAndGoResult result) {
		List<SubscriptionItem> items = new ArrayList<>();
		
		if (hasStorage(result)) {
			File resultFile = toFile(result);
			
			try {
				String local = StringUtils.fromFile(resultFile);
				JsonArray jsonArray = (JsonArray) new JsonParser().parse(local);
				
				for (Object item : jsonArray) {
					JsonObject itemMap = (JsonObject) item;
					
					String content = itemMap.getString(JSON_KEY_CONTENT);
					String date = itemMap.getString(JSON_KEY_DATE);
					String url = itemMap.getString(JSON_KEY_URL);
					
					items.add(new SubscriptionItem(result.getParentProvider()) //
							.setContent(content) //
							.setDate(date) //
							.setUrl(url) //
					);
				}
			} catch (Exception exception) {
				try {
					resultFile.delete();
				} catch (Exception exception2) {
					;
				}
			}
		}
		
		return items;
	}
	
	/**
	 * Update items in the local storage.<br>
	 * The target file will be deleted and re-created.
	 * 
	 * @param result
	 *            Target result.
	 * @param items
	 *            Items to update.
	 */
	public void updateLocalStorageItems(SearchAndGoResult result, List<SubscriptionItem> items) {
		File resultFile = toFile(result);
		
		try {
			resultFile.getParentFile().mkdirs();
			if (resultFile.exists()) {
				resultFile.delete();
			}
			resultFile.createNewFile();
			
			JsonArray jsonArray = new JsonArray();
			for (SubscriptionItem item : items) {
				JsonObject itemMap = new JsonObject();
				
				itemMap.put(JSON_KEY_CONTENT, item.getContent());
				itemMap.put(JSON_KEY_DATE, item.getDate());
				itemMap.put(JSON_KEY_URL, item.getUrl());
				
				jsonArray.add(itemMap);
			}
			
			FileUtils.writeStringToFile(jsonArray.toJsonString(), resultFile);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	/**
	 * Convert a {@link SearchAndGoResult} to a unique string.<br>
	 * By default its a unique string (from {@link MyListable}) encoded with {@link MD5}.
	 * 
	 * @param result
	 *            Target result.
	 * @return A file placed in the {@link #baseDirectory}, and with his {@link #EXTENSION}.
	 */
	public File toFile(SearchAndGoResult result) {
		return new File(baseDirectory, MD5.silentMd5(result.toUniqueString()) + "." + EXTENSION);
	}
	
	/**
	 * Call {@link #compareWithLocal(List, boolean)} with <code>autoUpdate</code> as <code>true</code>.
	 * 
	 * @param result
	 *            Target result.
	 * @param items
	 *            {@link List} of {@link SubscriptionItem} to compare.
	 * @param comparator
	 *            {@link Comparator} used to sort {@link SubscriptionItem} from the oldest to the newest.
	 * @return The items that were not already present in the local storage.
	 * @see #compareWithLocal(SearchAndGoResult, List, Comparator, boolean)
	 */
	public List<SubscriptionItem> compareWithLocal(SearchAndGoResult result, List<SubscriptionItem> items, Comparator<SubscriptionItem> comparator) {
		return compareWithLocal(result, items, comparator, true);
	}
	
	/**
	 * Compare a {@link List} of {@link SubscriptionItem} with the local storage.<br>
	 * It will create a new {@link List} of the items that were not already present and return it.<br>
	 * Returned result will be sorted like this: the laster it has been release, the lastest index it will be in the {@link List}.
	 * 
	 * @param result
	 *            Target result.
	 * @param items
	 *            {@link List} of {@link SubscriptionItem} to compare.
	 * @param comparator
	 *            {@link Comparator} used to sort {@link SubscriptionItem} from the oldest to the newest.
	 * @param autoUpdate
	 *            If it should automatically update the local storage.
	 * @return A {@link List} of newest {@link SubscriptionItem}.
	 * @throws NullPointerException
	 *             If the <code>result</code> is null.
	 * @throws NullPointerException
	 *             If the <code>items</code> {@link List} is null.
	 * @throws NullPointerException
	 *             If the <code>comparator</code> is null.
	 * @see #updateLocalStorageItems(SearchAndGoResult, List)
	 */
	public List<SubscriptionItem> compareWithLocal(SearchAndGoResult result, List<SubscriptionItem> items, Comparator<SubscriptionItem> comparator, boolean autoUpdate) {
		Objects.requireNonNull(result, "Result can't be null.");
		Objects.requireNonNull(items, "Items can't be null.");
		Objects.requireNonNull(comparator, "Compartor can't be null.");
		
		List<SubscriptionItem> localItems = getLocalStorageItems(result);
		List<SubscriptionItem> newestItems = new ArrayList<>();
		
		newestItems.addAll(items);
		if (!localItems.isEmpty()) {
			for (SubscriptionItem item : localItems) {
				String itemContent = item.getContent();
				
				for (SubscriptionItem subItem : items) {
					String subItemContent = subItem.getContent();
					
					if (itemContent.equals(subItemContent)) {
						newestItems.remove(subItem);
						break;
					}
				}
			}
		}
		
		if (autoUpdate) {
			updateLocalStorageItems(result, items);
		}
		
		Collections.sort(newestItems, comparator);
		
		return newestItems;
	}
	
}