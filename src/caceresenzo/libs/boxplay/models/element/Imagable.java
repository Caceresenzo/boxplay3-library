package caceresenzo.libs.boxplay.models.element;

public abstract class Imagable {
	
	protected String imageUrl, imageHdUrl, defaultImageUrl;
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public Imagable withImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	
	public String getImageHdUrl() {
		return imageHdUrl;
	}
	
	public Imagable withImageHdUrl(String imageHdUrl) {
		this.imageHdUrl = imageHdUrl;
		return this;
	}
	
	public String getDefaultImageUrl() {
		return defaultImageUrl;
	}
	
	public Imagable withDefaultImageUrl(String defaultImageUrl) {
		this.defaultImageUrl = defaultImageUrl;
		return this;
	}
	
	public String getBestImageUrl() {
		if (imageHdUrl != null) {
			return imageHdUrl;
		}
		
		if (imageUrl != null) {
			return imageUrl;
		}
		
		return defaultImageUrl;
	}
	
}