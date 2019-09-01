package caceresenzo.libs.boxplay.culture.searchngo.data.models;

import caceresenzo.libs.json.JsonObject;

public class SimpleUrlData extends SimpleData {
	
	/* Json Key */
	public static final String JSON_KEY_URL = "url";
	
	/* Constants */
	public static final String DEFAULT_KIND = "url";
	
	/* Variables */
	protected final String url;
	
	/* Constructor */
	public SimpleUrlData(String url) {
		this(DEFAULT_KIND, url);
	}
	
	/* Constructor */
	public SimpleUrlData(String kind, String url) {
		super(kind);
		
		this.url = url;
	}
	
	/** @return The url. */
	public String getUrl() {
		return url;
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();
		
		jsonObject.put(JSON_KEY_URL, url);
		
		return jsonObject;
	}
	
}