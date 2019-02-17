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
	
	/* Variables */
	/** If <code>true</code>, string will only be sorted if they are the same length. **/
	private boolean sortIgnoreLength;
	
	/* Constructor */
	public SimpleItemComparatorSubscriber() {
		this.sortIgnoreLength = false;
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
	
	@Override
	public Comparator<SubscriptionItem> createSubscriptionItemComparator() {
		return new Comparator<SubscriptionItem>() {
			@Override
			public int compare(SubscriptionItem item1, SubscriptionItem item2) {
				int length1 = item1.getContent().length();
				int length2 = item2.getContent().length();
				
				if (!sortIgnoreLength && length1 == length2) {
					return item1.getContent().compareTo(item2.getContent());
				}
				
				return length1 - length2;
			}
		};
	}
	
}