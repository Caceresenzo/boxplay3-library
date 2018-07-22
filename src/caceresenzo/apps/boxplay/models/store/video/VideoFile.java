package caceresenzo.apps.boxplay.models.store.video;

import java.util.Comparator;

import caceresenzo.apps.boxplay.models.element.VideoElement;
import caceresenzo.apps.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.apps.boxplay.models.store.video.enums.VideoFileType;
import caceresenzo.apps.boxplay.models.store.video.enums.VideoType;

public class VideoFile extends VideoElement {
	
	public static final Comparator<VideoFile> COMPARATOR = new Comparator<VideoFile>() {
		@Override
		public int compare(VideoFile file1, VideoFile file2) {
			if (file1.getVideoType().getPriority() > file2.getVideoType().getPriority()) {
				return +1 + file1.getEpisodeValue().compareTo(file2.getEpisodeValue());
			} else if (file1.getVideoType().getPriority() < file2.getVideoType().getPriority()) {
				return -1;
			} else {
				return file1.getEpisodeValue().compareTo(file2.getEpisodeValue());
			}
		}
	};
	
	private String episodeValue, url;
	private VideoSeason parentSeason;
	private VideoFileType fileType;
	private VideoType videoType;
	private ElementLanguage language;
	private boolean available, watched;
	private long savedTime, duration;
	
	protected VideoFile(VideoSeason parentSeason, VideoType videoType, String episodeValue) {
		super(parentSeason.toString() + "//" + videoType.toString().toLowerCase() + "/" + episodeValue);
		
		this.parentSeason = parentSeason;
		this.videoType = videoType;
		this.episodeValue = episodeValue;
		
		register(toString(), this);
	}
	
	public VideoSeason getParentSeason() {
		return parentSeason;
	}
	
	public VideoType getVideoType() {
		return videoType;
	}
	
	public String getEpisodeValue() {
		return videoType + "/" + episodeValue;
	}
	
	public String getRawEpisodeValue() {
		return episodeValue;
	}
	
	public VideoFile withUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public VideoFile withFileType(VideoFileType fileType) {
		this.fileType = fileType;
		return this;
	}
	
	public VideoFileType getFileType() {
		return fileType;
	}
	
	public VideoFile withLanguage(ElementLanguage language) {
		this.language = language;
		return this;
	}
	
	public ElementLanguage getLanguage() {
		return language;
	}
	
	public VideoFile setAvailable(boolean available) {
		this.available = available;
		return this;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public VideoFile asWatched(boolean watched) {
		this.watched = watched;
		return this;
	}
	
	public boolean isWatched() {
		return watched;
	}
	
	public VideoFile newSavedTime(long savedTime) {
		this.savedTime = savedTime;
		return this;
	}
	
	public long getSavedTime() {
		return savedTime;
	}
	
	public VideoFile newDuration(long duration) {
		this.duration = duration;
		return this;
	}
	
	public long getDuration() {
		return duration;
	}
	
	@Override
	public String toString() {
		return parentSeason.toString() + "//" + videoType.toString().toLowerCase() + "/" + episodeValue;
	}
	
	public static VideoFile instance(VideoSeason parentSeason, VideoType videoType, String episodeValue) {
		String identifier = parentSeason.toString() + "//" + videoType.toString().toLowerCase() + "/" + episodeValue;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (VideoFile) INSTANCES.get(identifier);
		}
		
		return new VideoFile(parentSeason, videoType, episodeValue);
	}
	
}
