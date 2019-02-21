package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.SubscriberManager;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.SubscriberStorageSolution;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations.RssSubscriber;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations.SimpleItemComparatorSubscriber;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.test.SimpleTestUnits;
import caceresenzo.libs.thread.ThreadUtils;

public class SubscriberTestUnits extends SimpleTestUnits {
	
	public static SubscriberStorageSolution createTestStorageSolution() {
		return new SubscriberStorageSolution(new File("./test/subscription/subscribers/"));
	}
	
	public static void dumpList(List<SubscriptionItem> items, String message) {
		Logger.info(" + - - - - - - - - - -");
		Logger.info(" | %s", message);
		Logger.info(" + - - - - - - - - - -");
		
		for (SubscriptionItem item : items) {
			Logger.info(" | --> %s", item);
		}
		
		Logger.info(" + - - - - - - - - - -");
	}
	
	public static class StorageSolutionTestUnit extends SubscriberTestUnits {
		
		public static void main(String[] args) {
			SubscriberStorageSolution storageSolution = createTestStorageSolution();
			
			SearchAndGoProvider provider = null;
			
			SearchAndGoResult result = new SearchAndGoResult(provider, "Hello", "hello_world");
			List<SubscriptionItem> items = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				items.add(new SubscriptionItem(provider) //
						.setContent("hello " + i) //
						.setDate("Fri, 01 Jan 2019 01:01:0" + i + " +0000") //
				);
			}
			
			storageSolution.updateLocalStorageItems(result, items);
			
			dumpList(storageSolution.getLocalStorageItems(result), "LOCAL STORAGE");
			
			/* New Content */
			for (int i = 10; i < 12; i++) {
				items.add(new SubscriptionItem(provider) //
						.setContent("new hello " + i) //
						.setDate("Fri, 01 Jan 2019 01:01:0" + i + " +0000") //
				);
			}
			
			dumpList(storageSolution.compareWithLocal(result, items, false), "COMPARED ITEMS");
		}
		
	}
	
	public static abstract class RealCaseTestUnit<T extends Subscriber> implements Subscriber.SubscriberCallback {
		
		protected SubscriberStorageSolution storageSolution;
		protected SubscriberManager subscriberManager;
		
		public RealCaseTestUnit() {
			this.storageSolution = createTestStorageSolution();
			this.subscriberManager = new SubscriberManager(storageSolution);
			
			test();
		}
		
		public abstract void test();
		
		public SearchAndGoResult createDummySearchAndGoResult(String subscriptionUrl) {
			return new SearchAndGoResult(ProviderManager.JETANIME.create(), "Tensei Shitara Slime Datta Ken", "https://www.jetanime.co/anime/tensei-shitara-slime-datta-ken/") //
					.subscribableAt(createSubscriber(), subscriptionUrl) //
			;
		}
		
		public Subscriber createSubscriber() {
			try {
				return (Subscriber) ((Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
		
		public void onNewContent(SubscriptionItem item) {
		}
		
		@Override
		public void onNewContent(List<SubscriptionItem> items, SubscriptionItem lastestItem) {
			Logger.info("Lastest item -> %s", lastestItem.getContent());
			
			for (SubscriptionItem subscriptionItem : items) {
				Logger.info("New item -> %s", subscriptionItem.getContent());
			}
		}
		
		@Override
		public void onException(SearchAndGoResult result, Exception exception) {
			Logger.exception(exception, "With result: %s", result.toUniqueString());
		}
		
	}
	
	public static class RealCaseRssTestUnit extends RealCaseTestUnit<RssSubscriber> {
		
		/** Remove some items after getting the newest one to alaways get new items, only for test purposes. */
		public static final boolean DEBUG_REMOVE_SOME_ITEMS = true;
		
		@Override
		public void test() {
			SearchAndGoResult result = createDummySearchAndGoResult("https://www.jetanime.co/rss/tensei-shitara-slime-datta-ken/");
			
			subscriberManager.load(Arrays.asList(result)).fetchAll(this);
			
			if (DEBUG_REMOVE_SOME_ITEMS) {
				List<SubscriptionItem> localItems = storageSolution.getLocalStorageItems(result);
				int countToRemove = Math.min(localItems.size(), 3);
				for (int i = 0; i < countToRemove; i++) {
					localItems.remove(0);
				}
				Logger.info("Removed %s [lastest] item(s) for test purposes.", countToRemove);
				
				storageSolution.updateLocalStorageItems(result, localItems);
			}
		}
		
		public static void main(String[] args) {
			new RealCaseRssTestUnit();
		}
		
	}
	
	public static class RealCaseSimpleItemComparatorTestUnit extends RealCaseTestUnit<SimpleItemComparatorSubscriber> {
		
		@Override
		public void test() {
			SearchAndGoResult result = createDummySearchAndGoResult("https://www.jetanime.co/anime/tensei-shitara-slime-datta-ken/");
			
			for (int i = 0; i < 10; i++) {
				subscriberManager.load(Arrays.asList(result)).fetchAll(this);
				
				/* Wait file unlock */
				ThreadUtils.sleep(50L);
			}
		}
		
		@Override
		public Subscriber createSubscriber() {
			return new SimpleItemComparatorSubscriber(true);
		}
		
		public static void main(String[] args) {
			new RealCaseSimpleItemComparatorTestUnit();
		}
		
	}
	
}