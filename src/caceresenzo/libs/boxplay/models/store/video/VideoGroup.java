package caceresenzo.libs.boxplay.models.store.video;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.element.implementations.VideoElement;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoFileType;
import caceresenzo.libs.boxplay.mylist.MyListable;
import caceresenzo.libs.parse.ParseUtils;

public class VideoGroup extends VideoElement implements MyListable {
	
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
		return seasons == null ? new ArrayList<VideoSeason>() : seasons;
	}
	
	public boolean hasSeason() { // If season 0 don't exists, so its a serie/anime
		if (seasons == null || seasons.isEmpty()) {
			return false;
		}
		
		return ParseUtils.parseInt(seasons.get(0).getSeasonValue(), 0) != 0;
	}
	
	@Override
	public String toUniqueString() {
		return getSlug();
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