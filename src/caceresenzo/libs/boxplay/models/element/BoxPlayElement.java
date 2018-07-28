package caceresenzo.libs.boxplay.models.element;

import java.util.HashMap;

public class BoxPlayElement {
	
	protected static HashMap<Object, Object> INSTANCES = new HashMap<Object, Object>();
	
	protected String identifier;
	protected String slug;
	
	protected BoxPlayElement(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getSlug() {
		return slug;
	}
	
	protected void register(Object key, Object value) {
		INSTANCES.put(key, value);
		
		this.slug = getIdentifier().replace("group://", "").replace("_", "-").replace("--", "-");
		
		if (this.slug.endsWith("-")) {
			this.slug = this.slug.substring(0, this.slug.length() - 1);
		}
	}
	
	public static HashMap<Object, Object> getInstances() {
		return INSTANCES;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[identifier=\"" + identifier + "\\\"]";
	}
	
	public static String formatName(String input) {
		return input.replaceAll("[ ]", ".").toLowerCase();
	}
	
}