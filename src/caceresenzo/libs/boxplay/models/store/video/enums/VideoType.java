package caceresenzo.libs.boxplay.models.store.video.enums;

public enum VideoType {
	
	EPISODE("type/episode", "E"), //
	OAV("type/oav", "OAV"), //
	SPECIAL("type/special", "SPECIAL"), //
	MOVIE("type/movie", "MOVIE"), //
	OTHER("type/other", "OTHER"), //
	UNKNOWN("type/unknown", "UNKNOWN"), //
	;
	
	private static int videoTypeIncrement = 1;
	
	private final String fileType, format;
	private final int priority;
	
	private VideoType(String fileType, String format) {
		this.fileType = fileType;
		this.format = format;
		this.priority = avaliablePriority();
	}
	
	private int avaliablePriority() {
		return videoTypeIncrement++;
	}
	
	public String getType() {
		return fileType;
	}
	
	public String getFormat() {
		return format;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public static VideoType fromString(String string) {
		return fromString(string, false);
	}
	
	public static VideoType fromString(String string, boolean useFormatToo) {
		for (VideoType type : VideoType.values()) {
			if (type.getType().equalsIgnoreCase(string) || (useFormatToo && type.getFormat().equalsIgnoreCase(string))) {
				return type;
			}
		}
		return UNKNOWN;
	}
	
}