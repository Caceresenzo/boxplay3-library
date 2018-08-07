package caceresenzo.libs.boxplay.models.element.enums;

public enum FileType {
	
	VIDEO("type/video"), //
	MUSIC("type/music"), //
	FILES("type/files"), //
	UNKNOWN("type/unknown"), //
	;
	
	private final String fileType;
	
	private FileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getType() {
		return fileType;
	}
	
	public static FileType fromString(String string) {
		for (FileType type : FileType.values()) {
			if (type.getType().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return UNKNOWN;
	}
	
}