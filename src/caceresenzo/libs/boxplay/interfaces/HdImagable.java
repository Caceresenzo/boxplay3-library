package caceresenzo.libs.boxplay.interfaces;

import caceresenzo.libs.boxplay.models.element.BoxPlayElement;

public interface HdImagable<T extends BoxPlayElement> {
	
	String hdImageUrl = null;
	
	public T applyHdImageUrl(String url);
	
	public String getHdImageUrl();
	
}