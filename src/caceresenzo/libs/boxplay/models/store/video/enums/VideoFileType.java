package caceresenzo.libs.boxplay.models.store.video.enums;

public enum VideoFileType {
	
	SERIE("video/serie"), //
	MOVIE("video/movie"), //
	ANIME("video/anime"), //
	ANIMEMOVIE("video/animemovie"), //
	UNKNOWN("video/unknown"), //
	;
	
	private final String fileType;
	
	private VideoFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public boolean hasEpisode() {
		return (this == SERIE || this == ANIME);
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public static VideoFileType fromString(String string) {
		for (VideoFileType type : VideoFileType.values()) {
			if (type.getFileType().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return UNKNOWN;
	}
	
}