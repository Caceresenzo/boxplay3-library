package caceresenzo.apps.boxplay.models.video;

import java.util.Comparator;
import java.util.List;

import caceresenzo.apps.boxplay.models.element.VideoElement;
import caceresenzo.apps.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.apps.boxplay.models.video.enums.VideoFileType;
import caceresenzo.libs.parse.ParseUtils;

public class VideoGroup extends VideoElement {
	
	public static final Comparator<VideoGroup> COMPARATOR = new Comparator<VideoGroup>() {
		@Override
		public int compare(VideoGroup group1, VideoGroup group2) {
			return group1.getTitle().compareTo(group2.getTitle());
		}
	};
	
	private String title, groupImageUrl;
	private ElementLanguage language;
	private boolean recommended, watching = false;
	private int episodesDigitSupportValue, seasonsDigitSupportValue;
	private VideoFileType videoFileType;
	private List<VideoSeason> seasons;
	
	protected VideoGroup(String identifier) {
		super(identifier);
		
		register(title = toString(), this);
	}
	
	public VideoGroup withTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public VideoGroup withLanguage(ElementLanguage language) {
		this.language = language;
		return this;
	}
	
	public ElementLanguage getLanguage() {
		return language;
	}
	
	public VideoGroup recommended(boolean recommended) {
		this.recommended = recommended;
		return this;
	}
	
	public boolean isRecommended() {
		return recommended;
	}
	
	public VideoGroup setAsWatching(boolean watching) {
		this.watching = watching;
		return this;
	}
	
	public boolean isWatching() {
		return watching;
	}
	
	public VideoGroup withGroupImageUrl(String groupImageUrl) {
		this.groupImageUrl = groupImageUrl;
		return this;
	}
	
	public String getGroupImageUrl() {
		return groupImageUrl;
	}
	
	public VideoGroup withSeasonsDigitSupportValue(int digitSupportValue) {
		this.seasonsDigitSupportValue = digitSupportValue;
		return this;
	}
	
	public int getSeasonsDigitSupportValue() {
		return seasonsDigitSupportValue;
	}
	
	public VideoGroup withEpisodesDigitSupportValue(int digitSupportValue) {
		this.episodesDigitSupportValue = digitSupportValue;
		return this;
	}
	
	public int getEpisodesDigitSupportValue() {
		return episodesDigitSupportValue;
	}
	
	public VideoGroup withVideoFileType(VideoFileType videoFileType) {
		this.videoFileType = videoFileType;
		return this;
	}
	
	public VideoFileType getVideoFileType() {
		return videoFileType;
	}
	
	public VideoGroup withSeasons(List<VideoSeason> seasons) {
		// Collections.sort(seasons, VideoSeason.COMPARATOR);
		this.seasons = seasons;
		return this;
	}
	
	public List<VideoSeason> getSeasons() {
		return seasons;
	}
	
	public boolean hasSeason() { // If season 0 don't exists, so its a serie/anime
		if (seasons.size() == 0) {
			return false;
		}
		
		return ParseUtils.parseInt(seasons.get(0).getSeasonValue(), 0) != 0;
	}
	
	@Override
	public String toString() {
		return "video:group//" + identifier;
	}
	
	public static VideoGroup instance(String name) {
		String identifier = "video:group//" + name;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (VideoGroup) INSTANCES.get(identifier);
		}
		
		return new VideoGroup(name);
	}
}