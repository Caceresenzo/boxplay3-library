package caceresenzo.libs.boxplay.interfaces;

import caceresenzo.libs.boxplay.models.element.BoxPlayElement;

public interface Imagable<T extends BoxPlayElement> {
	
	String imageUrl = null;
	
	public T applyImageUrl(String url);
	
	public String getImageUrl();
	
}