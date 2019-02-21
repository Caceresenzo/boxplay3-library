package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.list.ListUtils;

public abstract class Subscriber {
	
	/* Variable */
	private boolean shouldReverseList;
	
	/* Constructor */
	/**
	 * @param shouldReverseList
	 *            Weather or not the list should be reversed after being processed.
	 */
	public Subscriber(boolean shouldReverseList) {
		this.shouldReverseList = shouldReverseList;
	}
	
	/**
	 * Fetch a specific <code>result</code>.<br>
	 * If a new item is available, the delegate function {@link #onNewContent(SubscriptionItem, SubscriberCallback) onNewContent()} will be called.
	 * 
	 * @param storageSolution
	 *            Storage solution that will be used.
	 * @param result
	 *            Target result to fetch.
	 * @param callback
	 *            Progression callback.
	 * @throws NullPointerException
	 *             If the <code>result</code> is null.
	 * @throws NullPointerException
	 *             If the <code>callback</code> is null.
	 * @throws Exception
	 *             If anything (not handled) goes wrong.
	 */
	public void fetch(SubscriberStorageSolution storageSolution, SearchAndGoResult result, SubscriberCallback callback) throws Exception {
		Objects.requireNonNull(result, "Can't fetch new items from a null result.");
		Objects.requireNonNull(callback, "Callback can't be null.");
		
		List<SubscriptionItem> resolvedItems = resolveItems(result);
		
		if (resolvedItems != null) {
			if (storageSolution.hasStorage(result)) {
				if (isListShouldBeReversed()) {
					Collections.reverse(resolvedItems);
				}
				
				if (isItemSortingNeeded()) {
					Collections.sort(resolvedItems, createSubscriptionItemComparator());
				}
				
				SubscriptionItem lastestItem = ListUtils.getLastestItem(resolvedItems);
				List<SubscriptionItem> newItems = storageSolution.compareWithLocal(result, resolvedItems);
				
				if (!newItems.isEmpty()) {
					onNewContent(callback, newItems, lastestItem);
				}
			} else {
				storageSolution.updateLocalStorageItems(result, resolvedItems);
			}
		}
	}
	
	/**
	 * Abstract function. Resolve <code>result</code>'s {@link SubscriptionItem} here.
	 * 
	 * @param result
	 *            Target result.
	 * @return A {@link List} of {@link SubscriptionItem} available for this <code>result</code>.
	 * @throws Exception
	 *             If anything (not handled) goes wrong.
	 */
	public abstract List<SubscriptionItem> resolveItems(SearchAndGoResult result) throws Exception;
	
	/**
	 * Callback delegate function that can be intercepted.
	 * 
	 * @param items
	 *            {@link List} of the newest item available.
	 * @param callback
	 *            Progression callback.
	 * @see SubscriberCallback#onNewContent(List, SubscriptionItem)
	 */
	protected void onNewContent(SubscriberCallback callback, List<SubscriptionItem> items, SubscriptionItem lastestItem) {
		callback.onNewContent(items, lastestItem);
	}
	
	/**
	 * Clean up everything that a result generated.<br>
	 * Meant to be call when you have removed the item from the list.
	 * 
	 * @param storageSolution
	 *            Target storage solution used in the first place.
	 * @param result
	 *            Target result to clean up.
	 */
	public void cleanUp(SubscriberStorageSolution storageSolution, SearchAndGoResult result) {
		try {
			storageSolution.toFile(result).delete();
		} catch (Exception exception) {
			;
		}
	}
	
	/**
	 * Download the {@link SearchAndGoResult#getSubscriberTargetUrl() subscriber target url} of the <code>result</code>.
	 * 
	 * @param result
	 *            Target result.
	 * @return Target's content.
	 * @see ProviderHelper#downloadPage(String, java.util.Map, String)
	 */
	protected String downloadResult(SearchAndGoResult result) {
		return ProviderHelper.getStaticHelper().downloadPage(result.getSubscriberTargetUrl(), result.getRequireHeaders(), result.getParentProvider().getWorkingCharset());
	}
	
	/**
	 * @return Weather or not the item should be sorted while processing. Default value is <code>true</code>.
	 */
	public boolean isItemSortingNeeded() {
		return true;
	}
	
	/**
	 * @return Weather or not the {@link List} should be reversed after the processing.
	 */
	public boolean isListShouldBeReversed() {
		return shouldReverseList;
	}
	
	/**
	 * @return Weather or not the final name that is notified to the user need to be reformatted or not.
	 */
	public boolean shouldNameBeReformatted() {
		return true;
	}
	
	/**
	 * Create a {@link Comparator} that will be used to sort {@link SubscriptionItem} from the older to the newest.<br>
	 * By default this is the date that is used to compare items.
	 * 
	 * @return A {@link Comparator} instance.
	 */
	public Comparator<SubscriptionItem> createSubscriptionItemComparator() {
		return new Comparator<SubscriptionItem>() {
			@SuppressWarnings("deprecation")
			@Override
			public int compare(SubscriptionItem o1, SubscriptionItem o2) {
				try {
					return new Date(o1.getDate()).compareTo(new Date(o2.getDate()));
				} catch (Exception exception) {
					exception.printStackTrace();
					return 0;
				}
			}
		};
	}
	
	/**
	 * Progression callback for the {@link Subscriber} fetching process.
	 * 
	 * @author Enzo CACERES
	 * @see Subscriber#fetch(SubscriberStorageSolution, SearchAndGoResult, SubscriberCallback)
	 */
	public interface SubscriberCallback {
		
		/**
		 * Called when a new item is available.
		 * 
		 * @param items
		 *            Newest items available (excluding items in local storage).
		 */
		void onNewContent(List<SubscriptionItem> items, SubscriptionItem lastestItem);
		
		/**
		 * Called when an unhandled exception has occured.
		 * 
		 * @param result
		 *            Target result.
		 * @param exception
		 *            Throws exception.
		 */
		void onException(SearchAndGoResult result, Exception exception);
		
	}
	
}