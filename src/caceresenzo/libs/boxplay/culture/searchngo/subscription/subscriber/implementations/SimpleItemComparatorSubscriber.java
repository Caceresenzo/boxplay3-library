package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleUrlData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;

public class SimpleItemComparatorSubscriber extends Subscriber {
	
	/* Constructor */
	public SimpleItemComparatorSubscriber(boolean shouldReverseList) {
		super(shouldReverseList);
	}

	@Override
	public List<SubscriptionItem> resolveItems(SearchAndGoResult result) throws Exception {
		List<SubscriptionItem> resolvedItems = new ArrayList<>();
		
		try {
			List<AdditionalResultData> items = result.getParentProvider().fetchContent(result);
			
			for (AdditionalResultData item : items) {
				SimpleUrlData urlData = (SimpleUrlData) item.getData();
				
				String content;
				if (urlData instanceof VideoItemResultData) {
					content = ((VideoItemResultData) urlData).getName();
				} else if (urlData instanceof ChapterItemResultData) {
					content = ((ChapterItemResultData) urlData).convertToDisplayableString();
				} else {
					continue;
				}
				
				resolvedItems.add(new SubscriptionItem(result.getParentProvider()) //
						.setContent(content) //
						.setUrl(urlData.getUrl()) //
				);
			}
		} catch (Exception exception) {
			;
		}
		
		return resolvedItems;
	}
	
	/** Keep the original order from the provider */
	@Override
	public boolean isItemSortingNeeded() {
		return false;
	}
	
	@Override
	public Comparator<SubscriptionItem> createSubscriptionItemComparator() {
		throw new IllegalStateException("Comparator not available.");
	}
	
}