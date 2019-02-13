package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
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
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.test.SimpleTestUnits;

public class SubscriberTestUnits extends SimpleTestUnits {
	
	public static SubscriberStorageSolution createTestStorageSolution() {
		return new SubscriberStorageSolution(new File("./test/subscription/subscribers/")) {
		};
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
			
			dumpList(storageSolution.compareWithLocal(result, items, new RssSubscriber().createSubscriptionItemComparator()), "COMPARED ITEMS");
		}
		
	}
	
	public static class RealCaseRssTestUnit extends SubscriberTestUnits {
		
		/**
		 * Remove some items after getting the newest one to alaways get new items, only for test purposes.
		 */
		public static final boolean DEBUG_REMOVE_SOME_ITEMS = true;
		
		public static void main(String[] args) {
			SubscriberStorageSolution storageSolution = createTestStorageSolution();
			
			SearchAndGoProvider provider = ProviderManager.JETANIME.create();
			
			SearchAndGoResult result = new SearchAndGoResult(provider, "Tensei Shitara Slime Datta Ken", "https://www.jetanime.co/anime/tensei-shitara-slime-datta-ken/");
			result.subscribableAt("https://www.jetanime.co/rss/tensei-shitara-slime-datta-ken/");
			
			SubscriberManager subscriberManager = new SubscriberManager(storageSolution);
			subscriberManager.load(Arrays.asList(result)).fetchAll(new Subscriber.SubscriberCallback() {
				@Override
				public void onNewContent(SubscriptionItem item) {
					Logger.info("New content -> %s", item.getContent());
				}
				
				@Override
				public void onException(SearchAndGoResult result, Exception exception) {
					Logger.exception(exception, "With result: %s", result.toUniqueString());
				}
			});
			
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
		
	}
	
}