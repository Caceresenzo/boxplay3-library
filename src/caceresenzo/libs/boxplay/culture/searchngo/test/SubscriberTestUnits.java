package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.SubscriberStorageSolution;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.update.SubscriptionItem;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.test.SimpleTestUnits;

public class SubscriberTestUnits extends SimpleTestUnits {
	
	public static SubscriberStorageSolution createTestStorageSolution() {
		return new SubscriberStorageSolution(new File("./test/subscriber/")) {
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
			SubscriberStorageSolution storageSolution = new SubscriberStorageSolution(new File("./test/subscribers/")) {
			};
			
			SearchAndGoResult result = new SearchAndGoResult(null, "Hello", "hello_world");
			List<SubscriptionItem> items = new ArrayList<>();
			for (int i = 0; i < 10; i++) {
				items.add(new SubscriptionItem() //
						.setContent("hello " + i) //
						.setDate("Fri, 01 Jan 2019 01:01:0" + i + " +0000") //
				);
			}
			
			storageSolution.updateLocalStorageItems(result, items);
			
			dumpList(storageSolution.getLocalStorageItems(result), "LOCAL STORAGE");
			
			/* New Content */
			for (int i = 10; i < 12; i++) {
				items.add(new SubscriptionItem() //
						.setContent("new hello " + i) //
						.setDate("Fri, 01 Jan 2019 01:01:0" + i + " +0000") //
				);
			}
			
			dumpList(storageSolution.compareWithLocal(result, items), "COMPARED ITEMS");
		}
		
	}
	
}