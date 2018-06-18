package caceresenzo.apps.boxplay.models.music.enums;

public enum MusicAuthorType {
	
	AUTHOR(), //
	BAND(), //
	UNKNOWN(), //
	;
	
	private MusicAuthorType() {
		;
	}
	
	public static MusicAuthorType fromString(String string) {
		for (MusicAuthorType type : MusicAuthorType.values()) {
			if (type.toString().equalsIgnoreCase(string)) {
				return type;
			}
		}
		
		return UNKNOWN;
	}
	
}