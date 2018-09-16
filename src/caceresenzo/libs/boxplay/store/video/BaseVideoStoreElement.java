package caceresenzo.libs.boxplay.store.video;

import caceresenzo.libs.boxplay.store.BaseStoreElement;

public class BaseVideoStoreElement extends BaseStoreElement {
	
	private final long id;
	private final String title, imageUrl;
	
	public BaseVideoStoreElement(long id, String title, String imageUrl) {
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
	}
	
	public long getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
}