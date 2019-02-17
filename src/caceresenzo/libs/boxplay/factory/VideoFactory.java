package caceresenzo.libs.boxplay.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.element.enums.FileType;
import caceresenzo.libs.boxplay.models.store.video.VideoFile;
import caceresenzo.libs.boxplay.models.store.video.VideoGroup;
import caceresenzo.libs.boxplay.models.store.video.VideoSeason;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoFileType;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoType;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.parse.ParseUtils;

@Deprecated
public class VideoFactory extends AbstractFactory {
	
	public VideoFactory() {
		;
	}
	
	@SuppressWarnings("unchecked")
	public void parseServerJson(VideoFactoryListener factoryListener, JsonObject jsonObject) {
		if (jsonObject == null) {
			factoryListener.onJsonNull();
			return;
		}
		JsonObject jsonFileArrayObject = (JsonObject) jsonObject.get(KEY_FILES_ROOT);
		JsonObject jsonVideoFileTypeArrayObject = null;
		
		for (Entry<?, ?> fileTypeEntry : jsonFileArrayObject.entrySet()) {
			if (fileTypeEntry.getKey().equals(FileType.VIDEO.getType())) {
				jsonVideoFileTypeArrayObject = (JsonObject) fileTypeEntry.getValue();
				break;
			}
		}
		
		if (jsonVideoFileTypeArrayObject == null) {
			factoryListener.onJsonMissingFileType();
			return;
		}
		
		for (Entry<?, ?> videoTypeEntry : jsonVideoFileTypeArrayObject.entrySet()) { // Parsing video/*
			VideoFileType videoFileType = VideoFileType.fromString((String) videoTypeEntry.getKey());
			HashMap<?, ?> videoDataEntry = (HashMap<?, ?>) videoTypeEntry.getValue(); // JsonObject of files.type/video.video/*
			
			if (videoDataEntry != null && videoFileType != null) {
				for (Entry<?, ?> videoArrayEntry : videoDataEntry.entrySet()) { // Parsing name for group creation
					HashMap<?, ?> videoDataArrayEntry = (HashMap<?, ?>) videoArrayEntry.getValue();
					
					String groupIdentifier = (String) videoArrayEntry.getKey();
					String groupTitle = ParseUtils.parseString(videoDataArrayEntry.get(KEY_TITLE), groupIdentifier);
					ElementLanguage groupLanguage = ElementLanguage.fromString((String) videoDataArrayEntry.get(KEY_LANGUAGE));
					boolean groupRecommanded = ParseUtils.parseBoolean(videoDataArrayEntry.get(KEY_RECOMMENDED), false);
					String groupImageUrl = (String) videoDataArrayEntry.get(KEY_IMAGE_URL);
					String groupUrlFormat = (String) videoDataArrayEntry.get(KEY_URL_FORMAT);
					int groupSeasonsDigitSupportValue = ParseUtils.parseInt(videoDataArrayEntry.get(KEY_SEASONS_DIGIT_SUPPORT_VALUE), DEFAULT_SEASONS_DIGIT_SUPPORT_VALUE);
					int groupEpisodesDigitSupportValue = ParseUtils.parseInt(videoDataArrayEntry.get(KEY_EPISODES_DIGIT_SUPPORT_VALUE), DEFAULT_EPISODES_DIGIT_SUPPORT_VALUE);
					
					// TODO: Parsing informations
					// List<String> videoInformations = ParseUtils.parseList(videoDataArrayEntry.get("informations"), new ArrayList<String>());
					
					VideoGroup group = VideoGroup //
							.instance(groupIdentifier) //
							.withTitle(groupTitle) //
							.withLanguage(groupLanguage) //
							.recommended(groupRecommanded) //
							.withGroupImageUrl(groupImageUrl) //
							.withSeasonsDigitSupportValue(groupSeasonsDigitSupportValue) //
							.withEpisodesDigitSupportValue(groupEpisodesDigitSupportValue) //
							.withVideoFileType(videoFileType) //
					;
					factoryListener.onVideoGroupCreated(group);
					
					List<VideoSeason> seasons = new ArrayList<VideoSeason>();
					
					switch (videoFileType) {
						case ANIME:
						case SERIE:
							HashMap<?, ?> videoSeasonsArray = (HashMap<?, ?>) videoDataArrayEntry.get(KEY_SEASONS);
							
							if (videoSeasonsArray != null) {
								for (Entry<?, ?> videoSeasonArrayEntry : videoSeasonsArray.entrySet()) {
									int seasonNumber = ParseUtils.parseInt(videoSeasonArrayEntry.getKey(), IMPOSSIBLE_VALUE);
									HashMap<?, ?> seasonDataArray = (HashMap<?, ?>) videoSeasonArrayEntry.getValue(); // Season[x]
									
									if (seasonNumber == IMPOSSIBLE_VALUE) {
										factoryListener.onVideoSeasonInvalidSeason((String) videoSeasonArrayEntry.getKey());
										return;
									}
									
									String seasonValue = String.valueOf(seasonNumber);
									while (String.valueOf(seasonValue).length() < groupSeasonsDigitSupportValue) {
										seasonValue = "0" + seasonValue;
									}
									
									String seasonTitle = ParseUtils.parseString(seasonDataArray.get(KEY_TITLE), groupTitle);
									String seasonImageUrl = ParseUtils.parseString(seasonDataArray.get(KEY_IMAGE_URL), groupImageUrl);
									String seasonImageHdUrl = ParseUtils.parseString(seasonDataArray.get(KEY_IMAGE_HD_URL), null);
									String seasonUrlFormat = ParseUtils.parseString(seasonDataArray.get(KEY_URL_FORMAT), groupUrlFormat);
									int seasonEpisodesDigitSupportValue = ParseUtils.parseInt(videoDataArrayEntry.get(KEY_EPISODES_DIGIT_SUPPORT_VALUE), groupEpisodesDigitSupportValue);
									
									VideoSeason season = VideoSeason //
											.instance(group, seasonValue) //
											.title(seasonTitle) //
											.withImageUrl(seasonImageUrl) //
											.withImageHdUrl(seasonImageHdUrl) //
											.withUrlFormat(seasonUrlFormat) //
											.withEpisodesDigitSupportValue(seasonEpisodesDigitSupportValue) //
									;
									seasons.add(season);
									
									List<VideoFile> episodes = new ArrayList<VideoFile>();
									HashMap<?, ?> videoEpisodesArray = (HashMap<?, ?>) seasonDataArray.get("episodes");
									
									if (videoEpisodesArray != null) {
										for (Entry<?, ?> videoEpisodeArrayEntry : videoEpisodesArray.entrySet()) {
											HashMap<?, ?> videoEpisodeTypeContentArray = (HashMap<?, ?>) videoEpisodeArrayEntry.getValue();
											
											VideoType videoType = VideoType.fromString((String) videoEpisodeArrayEntry.getKey());
											String videoUrlFormat = ParseUtils.parseString(videoDataArrayEntry.get(KEY_URL_FORMAT), seasonUrlFormat);
											
											String avaliablePattern = (String) videoEpisodeTypeContentArray.get(KEY_AVALIABLE_PATTERN);
											String unavaliablePattern = (String) videoEpisodeTypeContentArray.get(KEY_UNAVALIABLE_PATTERN);
											
											episodes.addAll(parseEpisodeData(avaliablePattern, season, videoFileType, videoType, groupLanguage, videoUrlFormat, groupEpisodesDigitSupportValue, true));
											episodes.addAll(parseEpisodeData(unavaliablePattern, season, videoFileType, videoType, groupLanguage, videoUrlFormat, groupEpisodesDigitSupportValue, false));
											
											Collections.sort(episodes, VideoFile.COMPARATOR);
											
											HashMap<String, HashMap<String, Object>> override = (HashMap<String, HashMap<String, Object>>) videoEpisodeTypeContentArray.get(KEY_OVERRIDE);
											if (override != null) {
												for (Entry<String, HashMap<String, Object>> videoOverrideArrayEntry : override.entrySet()) {
													int videoOverrideEpisodeValue = ParseUtils.parseInt(videoOverrideArrayEntry.getKey(), IMPOSSIBLE_VALUE);
													HashMap<String, Object> videoOverrideEpisodeMap = (HashMap<String, Object>) videoOverrideArrayEntry.getValue();
													
													if (videoOverrideEpisodeValue == IMPOSSIBLE_VALUE || videoOverrideEpisodeMap == null) {
														continue;
													}
													
													for (VideoFile video : episodes) {
														int videoValue = ParseUtils.parseInt(video.getRawEpisodeValue(), IMPOSSIBLE_VALUE);
														
														if (videoValue == IMPOSSIBLE_VALUE) {
															continue;
														}
														
														if (videoValue == videoOverrideEpisodeValue) {
															String videoOverrideDirectUrl = ParseUtils.parseString(videoOverrideEpisodeMap.get(KEY_OVERRIDE_URL), video.getUrl());
															ElementLanguage videoOverrideLanguage = ElementLanguage.fromString((String) videoOverrideEpisodeMap.get(KEY_LANGUAGE));
															
															video.withUrl(videoOverrideDirectUrl);
															video.withLanguage(videoOverrideLanguage.equals(ElementLanguage.UNKNOWN) ? groupLanguage : videoOverrideLanguage);
															continue;
														}
													}
												}
											}
										}
									}
									
									season.withVideos(episodes);
								}
							}
							break;
						
						case ANIMEMOVIE:
						case MOVIE:
							String seasonTitle = (String) videoDataArrayEntry.get(KEY_TITLE);
							String seasonImageUrl = (String) videoDataArrayEntry.get(KEY_IMAGE_URL);
							String seasonImageHdUrl = (String) videoDataArrayEntry.get(KEY_IMAGE_HD_URL);
							
							String videoUrl = (String) videoDataArrayEntry.get(KEY_URL);
							boolean videoAvailable = ParseUtils.parseBoolean(videoDataArrayEntry.get("avaliable"), true);
							
							VideoSeason season = VideoSeason //
									.instance(group, "0") //
									.title(seasonTitle) //
									.withImageUrl(seasonImageUrl) //
									.withImageHdUrl(seasonImageHdUrl) //
							;
							
							VideoFile video = VideoFile //
									.instance(season, VideoType.EPISODE, "0") //
									.withFileType(videoFileType) //
									.withLanguage(groupLanguage) //
									.withUrl(videoUrl) //
									.setAvailable(videoAvailable)//
							;
							
							List<VideoFile> videos = new ArrayList<VideoFile>();
							videos.add(video);
							
							season.withVideos(videos);
							seasons.add(season);
							break;
						
						default:
						case UNKNOWN:
							// Ignored
							break;
					}
					Collections.sort(seasons, VideoSeason.COMPARATOR);
					
					group.withSeasons(seasons);
				}
				
			}
		}
	}
	
	private List<VideoFile> parseEpisodeData(String episodePattern, VideoSeason season, VideoFileType fileType, VideoType videoType, ElementLanguage language, String urlFormat, int digitSupportValue, boolean available) {
		List<VideoFile> episodes = new ArrayList<VideoFile>();
		
		String[] patternSplit = String.valueOf(episodePattern).split(",");
		
		if (episodePattern != null && patternSplit != null) {
			for (String pattern : patternSplit) {
				int start = 1, end = 1;
				if (pattern.contains("-")) {
					String[] patternBound = pattern.split("-");
					start = ParseUtils.parseInt(patternBound[0], 1);
					end = ParseUtils.parseInt(patternBound[1], 1);
				} else {
					start = ParseUtils.parseInt(pattern, 1);
					end = start;
				}
				
				for (int j = start; j <= end; j++) {
					String episodeFormatted = String.valueOf(j);
					while (String.valueOf(episodeFormatted).length() < digitSupportValue) {
						episodeFormatted = "0" + episodeFormatted;
					}
					
					VideoFile video = VideoFile //
							.instance(season, videoType, episodeFormatted) //
							.withFileType(fileType) //
							.withLanguage(language) //
							.withUrl(urlFormat.replace(REPLACE_EPISODE_FORMATTED, episodeFormatted).replace(REPLACE_VIDEO_FILE_TYPE, videoType.getFormat())) //
							.setAvailable(available) //
					;
					
					episodes.add(video);
				}
			}
		}
		return episodes;
	}
	
	public static interface VideoFactoryListener {
		
		void onJsonNull();
		
		void onJsonMissingFileType();
		
		void onVideoSeasonInvalidSeason(String element);
		
		void onVideoGroupCreated(VideoGroup group);
		
	}
	
}