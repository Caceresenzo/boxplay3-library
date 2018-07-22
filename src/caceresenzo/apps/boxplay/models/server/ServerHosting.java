package caceresenzo.apps.boxplay.models.server;

import java.util.Comparator;
import java.util.Map;

import caceresenzo.apps.boxplay.models.element.ServerElement;

public class ServerHosting extends ServerElement {
	
	public static final String DEFAULT_LOCALE = "en";

	public static final Comparator<ServerHosting> COMPARATOR = new Comparator<ServerHosting>() {
		@Override
		public int compare(ServerHosting hosting1, ServerHosting hosting2) {
			return hosting1.getPosition() - hosting2.getPosition();
		}
	};
	
	private String name, startingStringUrl, iconUrl, imageUrl;
	private int position;
	private Map<String, String> displayTranslation, descriptionTranslation;
	private boolean asDefault;
	
	private ServerHosting(String name) {
		super(name);
		
		this.name = name;
		
		register(toString(), this);
	}
	
	public ServerHosting withName(String name) {
		this.name = name;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public ServerHosting withStartingStringUrl(String startingStringUrl) {
		this.startingStringUrl = startingStringUrl;
		return this;
	}
	
	public String getStartingStringUrl() {
		return startingStringUrl;
	}
	
	public ServerHosting withIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
		return this;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public ServerHosting withImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public ServerHosting withPosition(int position) {
		this.position = position;
		return this;
	}
	
	public int getPosition() {
		return position;
	}
	
	public ServerHosting withDisplayTranslation(Map<String, String> displayTranslation) {
		this.displayTranslation = displayTranslation;
		return this;
	}
	
	public String getDisplayTranslation(String locale) {
		return getFromMap(displayTranslation, locale);
	}
	
	public ServerHosting withDescriptionTranslation(Map<String, String> descriptionTranslation) {
		this.descriptionTranslation = descriptionTranslation;
		return this;
	}
	
	public String getDescriptionTranslation(String locale) {
		return getFromMap(descriptionTranslation, locale);
	}
	
	private String formatLocale(Object object) {
		if (object == null) {
			return DEFAULT_LOCALE;
		}
		
		return String.valueOf(object).toLowerCase();
	}
	
	private String getFromMap(Map<String, String> source, String locale) {
		if (source.containsKey(formatLocale(locale))) {
			return source.get(locale);
		}
		
		return null;
	}
	
	public ServerHosting asDefault(boolean asDefault) {
		this.asDefault = asDefault;
		return this;
	}
	
	public boolean asDefault() {
		return asDefault;
	}
	
	@Override
	public String toString() {
		return "server:hosting//" + name;
	}
	
	public static ServerHosting instance(String name) {
		String identifier = "server:hosting//" + name;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (ServerHosting) INSTANCES.get(identifier);
		}
		
		return new ServerHosting(name);
	}
	
}