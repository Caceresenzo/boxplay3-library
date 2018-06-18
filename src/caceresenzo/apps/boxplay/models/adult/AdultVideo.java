package caceresenzo.apps.boxplay.models.adult;

import caceresenzo.apps.boxplay.factory.AdultFactory.VideoOrigin;
import caceresenzo.apps.boxplay.models.element.AdultElement;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.parse.ParseUtils;

public class AdultVideo extends AdultElement {
	
	private String targetUrl, imageUrl, title;
	private int viewCount = -1;
	
	private VideoOrigin factoryCreationOrigin;
	
	protected AdultVideo(String targetUrl) {
		super("adult://url/b64:" + Base64.encodeToString(targetUrl.getBytes(), false));
		
		this.targetUrl = targetUrl;
		
		register(toString(), this);
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public AdultVideo withImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public AdultVideo withTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public AdultVideo withViewCount(int viewCount) {
		this.viewCount = viewCount;
		return this;
	}
	
	public AdultVideo withViewCount(String unformattedViewCount) {
		this.viewCount = ParseUtils.parseInt(unformattedViewCount.replace(",", "").replace(" ", ""), -1);
		return this;
	}
	
	public int getViewCount() {
		return viewCount;
	}
	
	public boolean hasViewCount() {
		return viewCount > -1;
	}
	
	public AdultVideo withFactoryCreationOrigin(VideoOrigin factoryCreationOrigin) {
		this.factoryCreationOrigin = factoryCreationOrigin;
		return this;
	}
	
	public VideoOrigin getFactoryCreationOrigin() {
		return factoryCreationOrigin;
	}
	
	@Override
	public String toString() {
		return "adult://url/b64:" + Base64.encodeToString(targetUrl.getBytes(), false);
	}
	
	public static AdultVideo instance(String targetUrl, VideoOrigin factoryCreationOrigin) {
		String identifier = "adult://url/b64:" + Base64.encodeToString(targetUrl.getBytes(), false);
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (AdultVideo) INSTANCES.get(identifier);
		}
		
		return new AdultVideo(targetUrl).withFactoryCreationOrigin(factoryCreationOrigin);
	}
	
}