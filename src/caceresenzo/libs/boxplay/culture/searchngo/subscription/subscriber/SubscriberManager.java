package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.Subscribable;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber.SubscriberCallback;

public class SubscriberManager {
	
	/* Variables */
	private SubscriberStorageSolution storageSolution;
	private Map<Class<? extends Subscribable>, Subscriber> subscribers;
	private List<SearchAndGoResult> results;
	
	/* Constructor */
	/**
	 * Create a {@link SubscriberManager} instance.
	 * 
	 * @param storageSolution
	 *            Target storage solution to handle file management.
	 * @throws NullPointerException
	 *             If the <code>storageSolution</code> is null.
	 */
	public SubscriberManager(SubscriberStorageSolution storageSolution) {
		this.storageSolution = Objects.requireNonNull(storageSolution, "Storage solution can't be null");
		this.subscribers = new HashMap<>();
	}
	
	/**
	 * Do the first processing of the {@link List} of {@link SearchAndGoResult} that you want to fetch.<br>
	 * This will {@link Subscribable} item from non-{@link Subscribable} ones. (removing them from a copied list.)<br>
	 * <br>
	 * While looping through all items, {@link Subscriber} instance will be created and as one-per-provider class. No duplicate will be used. Using this technic will allow custom implementations of {@link Subscriber} to be used.<br>
	 * 
	 * @param searchAndGoResults
	 *            Target items you want to fetch.
	 * @return <code>this</code> for method chaining (fluent API).
	 */
	public SubscriberManager load(List<SearchAndGoResult> searchAndGoResults) {
		this.results = new ArrayList<>(searchAndGoResults);
		
		for (SearchAndGoResult result : searchAndGoResults) {
			SearchAndGoProvider parentProvider = result.getParentProvider();
			
			if (parentProvider instanceof Subscribable && result.isSubscribable()) {
				Subscribable subscribable = (Subscribable) parentProvider;
				Class<? extends Subscribable> clazz = subscribable.getClass();
				
				if (!subscribers.containsKey(clazz)) {
					subscribers.put(clazz, subscribable.createSubscriber());
				}
			} else {
				if (!result.hasCustomSubscriber()) {
					results.remove(result);
				}
			}
		}
		
		return this;
	}
	
	/**
	 * Start fetching all items previously loaded.
	 * 
	 * @param callback
	 *            Progression callback.
	 * @throws NullPointerException
	 *             If you forgot to call load() before.
	 * @throws NullPointerException
	 *             If the <code>callback</code> is null.
	 * @see #load(List)
	 */
	public void fetchAll(SubscriberCallback callback) {
		Objects.requireNonNull(results, "Result list is null. Have you call load() before?");
		Objects.requireNonNull(callback, "Callback can't be null.");
		
		for (SearchAndGoResult result : results) {
			Subscriber subscribable = subscribers.get(result.getParentProvider().getClass());
			
			if (result.hasCustomSubscriber()) {
				subscribable = result.getCustomSubscriber();
			}
			
			try {
				subscribable.fetch(storageSolution, result, callback);
			} catch (Exception exception) {
				callback.onException(result, exception);
			}
		}
	}
	
}