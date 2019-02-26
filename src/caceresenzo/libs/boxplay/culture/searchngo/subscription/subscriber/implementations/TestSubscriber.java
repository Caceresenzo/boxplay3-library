package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations;

import java.util.Comparator;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;

public class TestSubscriber extends Subscriber {
	
	/* Variables */
	private final List<SubscriptionItem> items;
	
	/* Constructor */
	public TestSubscriber(boolean shouldReverseList, List<SubscriptionItem> items) {
		super(shouldReverseList);
		
		this.items = items;
	}
	
	@Override
	public List<SubscriptionItem> resolveItems(SearchAndGoResult result) throws Exception {
		return items;
	}
	
	@Override
	public boolean isItemSortingNeeded() {
		return false;
	}
	
	@Override
	public Comparator<SubscriptionItem> createSubscriptionItemComparator() {
		throw new IllegalStateException("Item don't need to be sorted.");
	}
	
}