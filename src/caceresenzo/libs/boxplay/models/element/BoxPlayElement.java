package caceresenzo.libs.boxplay.models.element;

import java.util.HashMap;
import java.util.Map;

public class BoxPlayElement extends Imagable {
	
	public static final String IDENTIFIER_BASE = "boxplay//";
	
	protected static HashMap<Object, Object> INSTANCES = new HashMap<Object, Object>();
	
	protected String identifier;
	protected String slug;
	
	protected BoxPlayElement() {
		this(null);
	}
	
	protected BoxPlayElement(String identifier) {
		this.identifier = identifier;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	protected void applyIdentifier(BoxPlayElement element) {
		applyIdentifier(element.toString(), element);
	}
	
	protected void applyIdentifier(String identifier, BoxPlayElement element) {
		this.identifier = identifier;
		
		register(identifier, element);
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
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[identifier=\"" + identifier + "\\\"]";
	}
	
	public static String formatName(String input) {
		return input.replaceAll("[ ]", ".").toLowerCase();
	}
	
	public static Map<Object, Object> getInstances() {
		return INSTANCES;
	}
	
}