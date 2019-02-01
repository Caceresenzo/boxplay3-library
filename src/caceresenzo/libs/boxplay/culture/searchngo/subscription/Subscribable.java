package caceresenzo.libs.boxplay.culture.searchngo.subscription;

import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;

public interface Subscribable {
	
	/**
	 * @return An instance of a {@link Subscriber} compatible with the implemented {@link SearchAndGoProvider}.
	 */
	public Subscriber createSubscriber();
	
}