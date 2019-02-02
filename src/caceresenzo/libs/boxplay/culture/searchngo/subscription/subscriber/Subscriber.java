package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber;

import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.update.SubscriptionItem;

public abstract class Subscriber {
	
	/* Variables */
	private SearchAndGoProvider provider;
	
	/* Constructor */
	/**
	 * Create a new {@link Subscriber}.
	 * 
	 * @param provider
	 *            Parent provider for this {@link Subscriber}.
	 */
	public Subscriber(SearchAndGoProvider provider) {
		this.provider = provider;
	}
	
	public void fetch(SubscriberStorageSolution storageSolution, SearchAndGoResult result, SubscriberCallback callback) throws Exception {
		List<SubscriptionItem> resolvedItems = resolveItems(result);
		
		if (resolvedItems != null) {
			if (storageSolution.hasStorage(result)) {
				List<SubscriptionItem> newItems = storageSolution.compareWithLocal(result, resolvedItems);
				
				if (!newItems.isEmpty()) {
					onNewContent(newItems.get(newItems.size() - 1), callback);
				}
			} else {
				storageSolution.updateLocalStorageItems(result, resolvedItems);
			}
		}
	}
	
	public abstract List<SubscriptionItem> resolveItems(SearchAndGoResult result) throws Exception;
	
	protected void onNewContent(SubscriptionItem update, SubscriberCallback callback) {
		if (update != null) {
			callback.onNewContent(update);
		}
	}
	
	protected void cleanUp(SubscriberStorageSolution storageSolution, SearchAndGoResult result) {
		try {
			storageSolution.toFile(result).delete();
		} catch (Exception exception) {
			;
		}
	}
	
	protected String downloadResult(SearchAndGoResult result) {
		return ProviderHelper.getStaticHelper().downloadPage(result.getSubscriberTargetUrl(), result.getRequireHeaders(), result.getParentProvider().getWorkingCharset());
	}
	
	public interface SubscriberCallback {
		
		void onNewContent(SubscriptionItem item);

		void onException(SearchAndGoResult result, Exception exception);
		
	}
	
}