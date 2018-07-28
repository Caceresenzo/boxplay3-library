package caceresenzo.libs.boxplay.models.store.video;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import caceresenzo.libs.boxplay.models.element.VideoElement;

public class VideoSeason extends VideoElement {
	
	public static final Comparator<VideoSeason> COMPARATOR = new Comparator<VideoSeason>() {
		@Override
		public int compare(VideoSeason season1, VideoSeason season2) {
			return season1.getSeasonValue().compareTo(season2.getSeasonValue());
		}
	};
	
	private String title, imageUrl, imageHdUrl, urlFormat, seasonValue;
	private VideoGroup parentGroup;
	private int episodesDigitSupportValue;
	private List<VideoFile> videos;
	private boolean watched = false;
	
	protected VideoSeason(VideoGroup parentGroup, String seasonValue) {
		super(parentGroup.toString() + "//season" + seasonValue);
		
		this.parentGroup = parentGroup;
		this.seasonValue = seasonValue;
		
		register(title = toString(), this);
	}
	
	public VideoGroup getParentGroup() {
		return parentGroup;
	}
	
	public String getSeasonValue() {
		return seasonValue;
	}
	
	public VideoSeason title(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public VideoSeason withImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public VideoSeason withImageHdUrl(String imageHdUrl) {
		this.imageHdUrl = imageHdUrl;
		return this;
	}
	
	public String getImageHdUrl() {
		return imageHdUrl;
	}
	
	public VideoSeason withUrlFormat(String urlFormat) {
		this.urlFormat = urlFormat;
		return this;
	}
	
	public String getUrlFormat() {
		return urlFormat;
	}
	
	public VideoSeason withEpisodesDigitSupportValue(int digitSupportValue) {
		this.episodesDigitSupportValue = digitSupportValue;
		return this;
	}
	
	public int getEpisodesDigitSupportValue() {
		return episodesDigitSupportValue;
	}
	
	public void withVideos(List<VideoFile> videos) {
		this.videos = videos;
	}
	
	public void withVideo(VideoFile video) {
		List<VideoFile> videos = new ArrayList<VideoFile>();
		videos.add(video);
		this.videos = videos;
	}
	
	public List<VideoFile> getVideos() {
		return videos;
	}
	
	public void asWatched(boolean watched) {
		this.watched = watched;
	}
	
	public boolean isWatched() {
		return watched;
	}
	
	@Override
	public String toString() {
		return parentGroup.toString() + "//season" + seasonValue;
	}
	
	public static VideoSeason instance(VideoGroup parentGroup, String seasonValue) {
		String identifier = parentGroup.toString() + "//season" + seasonValue;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (VideoSeason) INSTANCES.get(identifier);
		}
		
		return new VideoSeason(parentGroup, seasonValue);
	}
	
}