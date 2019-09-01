package caceresenzo.libs.boxplay.models.element;

import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;

public abstract class Imagable extends SimpleData {

	/* Constants */
	public static final String KIND = "target_url";
	
	/* Variables */
	protected String imageUrl, imageHdUrl, defaultImageUrl;
	
	/* Constructor */
	public Imagable() {
		super(KIND);
	}
	
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