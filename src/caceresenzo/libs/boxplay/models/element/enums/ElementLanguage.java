package caceresenzo.libs.boxplay.models.element.enums;

public enum ElementLanguage {

	FR, //
	EN, //
	ENSUBFR, //
	JPSUBFR, //
	SPEECHLESS, //
	UNKNOWN, //
	;
	
	private String language;
	
	private ElementLanguage() {
		this.language = toString();
	}
	
	public void initializeLanguageValue(String value) {
		this.language = value;
	}
	
	public String getLanguageString() {
		return language;
	}
	
	public static ElementLanguage fromString(String string) {
		for (ElementLanguage type : ElementLanguage.values()) {
			if (type.toString().equalsIgnoreCase(string)) {
				return type;
			}
		}
		
		return UNKNOWN;
	}
	
}