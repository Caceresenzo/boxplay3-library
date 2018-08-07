package caceresenzo.libs.boxplay.models.store.music.enums;

public enum MusicRessourceType {
	
	ALBUM("ressource/album"), //
	MUSIC("ressource/music"), //
	UNKNOWN("ressource/unknown"), //
	;
	
	private final String fileType;
	
	private MusicRessourceType(String fileType) {
		this.fileType = fileType;
	}
	
	public boolean hasMoreMusic() {
		return (this == ALBUM);
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public static MusicRessourceType fromString(String string) {
		for (MusicRessourceType type : MusicRessourceType.values()) {
			if (type.getFileType().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return UNKNOWN;
	}
	
}