package caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.implementations;

import java.util.ArrayList;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.item.SubscriptionItem;
import caceresenzo.libs.boxplay.culture.searchngo.subscription.subscriber.Subscriber;
import caceresenzo.libs.rss.Feed;
import caceresenzo.libs.rss.FeedMessage;
import caceresenzo.libs.rss.RssFeedParser;

public class RssSubscriber extends Subscriber {
	
	@Override
	public List<SubscriptionItem> resolveItems(SearchAndGoResult result) {
		List<SubscriptionItem> resolvedItems = new ArrayList<>();
		
		try {
			RssFeedParser parser = new RssFeedParser(downloadResult(result));
			Feed feed = parser.readFeed();
			
			for (FeedMessage message : feed.getMessages()) {
				resolvedItems.add(new SubscriptionItem(result.getParentProvider()) //
						.setContent(message.getTitle()) //
						.setDate(message.getPublicationDate()) //
						.setUrl(message.getLink()) //
				);
			}
		} catch (Exception exception) {
			;
		}
		
		return resolvedItems;
	}
	
}