package caceresenzo.libs.boxplay.culture.searchngo.subscription.item;

import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public class SubscriptionItem {
	
	/* Variables */
	private final SearchAndGoProvider parentProvider;
	private String content, date, url;
	
	/* Constructor */
	public SubscriptionItem(SearchAndGoProvider parentProvider) {
		this.parentProvider = parentProvider;
	}
	
	/**
	 * @return The content of this {@link SubscriptionItem}.
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * Set a content for this {@link SubscriptionItem}.
	 * 
	 * @param content
	 *            New content.
	 * @return <code>this</code> for method chaining (fluent API).
	 */
	public SubscriptionItem setContent(String content) {
		this.content = content;
		
		return this;
	}
	
	/**
	 * @return A date string for this {@link SubscriptionItem}.
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Set a date string for this {@link SubscriptionItem}.
	 * 
	 * @param date
	 *            New date string.
	 * @return <code>this</code> for method chaining (fluent API).
	 */
	public SubscriptionItem setDate(String date) {
		this.date = date;
		
		return this;
	}
	
	/**
	 * @return The url (that is supposed to reach the real item) of this {@link SubscriptionItem}.
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Set a url for this item.
	 * 
	 * @param url
	 *            New url.
	 * @return <code>this</code> for method chaining (fluent API).
	 */
	public SubscriptionItem setUrl(String url) {
		this.url = url;
		
		return this;
	}
	
	/**
	 * Convert this {@link SubscriptionItem} to a very basic {@link SearchAndGoResult} instance.
	 * 
	 * @return A simple {@link SearchAndGoResult} with really basic information.
	 */
	public SearchAndGoResult toSearchAndGoResult() {
		return new SearchAndGoResult(parentProvider, content, url);
	}
	
	@Override
	public String toString() {
		return "SubscriptionItem[content=\"" + content + "\", date=\"" + date + "\", url=\"" + url + "\"]";
	}
	
}