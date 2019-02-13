package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;

public abstract class Subscriber {
	
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
				List<SubscriptionItem> newItems = storageSolution.compareWithLocal(result, resolvedItems, createSubscriptionItemComparator());
				
				if (!newItems.isEmpty()) {
					onNewContent(newItems.get(0), callback);
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
	 * @param item
	 *            Lastest item available.
	 * @param callback
	 *            Progression callback.
	 */
	protected void onNewContent(SubscriptionItem item, SubscriberCallback callback) {
		callback.onNewContent(item);
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
		 * @param item
		 *            This will alaways be the lastest item (excluding items in local storage).
		 */
		void onNewContent(SubscriptionItem item);
		
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