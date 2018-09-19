package caceresenzo.libs.boxplay.base;

public enum ElementStatus {
	
	AVAILABLE, UNAVAILABLE, UNKNOWN;
	
	public static ElementStatus fromString(String source) {
		for (ElementStatus status : ElementStatus.values()) {
			if (status.toString().equalsIgnoreCase(source)) {
				return status;
			}
		}
		
		return UNKNOWN;
	}
	
}