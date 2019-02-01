package caceresenzo.libs.boxplay.culture.searchngo.subscription.update;

public class SubscriptionItem {
	
	/* Variables */
	private String content, date, url;
	
	/* Constructor */
	public SubscriptionItem() {
		;
	}
	
	public String getContent() {
		return content;
	}
	
	public SubscriptionItem setContent(String content) {
		this.content = content;
		
		return this;
	}
	
	public String getDate() {
		return date;
	}
	
	public SubscriptionItem setDate(String date) {
		this.date = date;
		
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public SubscriptionItem setUrl(String url) {
		this.url = url;
		
		return this;
	}

	@Override
	public String toString() {
		return "SubscriptionItem[content=" + content + ", date=" + date + ", url=" + url + "]";
	}
	
}