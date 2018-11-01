package caceresenzo.libs.boxplay.test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.factory.VideoFactory;
import caceresenzo.libs.boxplay.factory.VideoFactory.VideoFactoryListener;
import caceresenzo.libs.boxplay.models.store.video.VideoFile;
import caceresenzo.libs.boxplay.models.store.video.VideoGroup;
import caceresenzo.libs.boxplay.models.store.video.VideoSeason;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoFileType;
import caceresenzo.libs.boxplay.models.store.video.enums.VideoType;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

@SuppressWarnings("all")
public class VideoTest {
	
	public static void main(String[] args) throws IOException, JsonException {
		VideoTest videoTest = new VideoTest();
		videoTest.initialize();
		videoTest.callFactory();
	}
	
	private VideoFactory videoFactory = new VideoFactory();
	
	private List<VideoGroup> groups;
	
	private JsonObject serverJsonData;
	
	public void initialize() throws IOException, JsonException {
		groups = new ArrayList<VideoGroup>();
		
		String content = StringUtils.fromFile(new File("boxplay_3.json"));
		serverJsonData = new JsonObject((Map<?, ?>) new JsonParser().parse(new StringReader(content)));
		
		// File output = new File("./request.sql");
		// output.createNewFile();
		// System.setOut(new PrintStream(new FileOutputStream(output)));
	}
	
	public void callFactory() {
		groups.clear();
		
		videoFactory.parseServerJson(new VideoFactoryListener() {
			@Override
			public void onJsonNull() {
				Logger.error("Json null");
			}
			
			@Override
			public void onVideoGroupCreated(VideoGroup videoGroup) {
				groups.add(videoGroup);
			}
			
			@Override
			public void onJsonMissingFileType() {
				System.out.println("onJsonMissingFileType");
				
			}
			
			@Override
			public void onVideoSeasonInvalidSeason(String element) {
				System.out.println("onVideoSeasonInvalidSeason: " + element);
				
			}
		}, serverJsonData);
		
		Map<String, Integer> groupIdentifiers = new HashMap<String, Integer>();
		groupIdentifiers.put("freddy-krueger", 1);
		groupIdentifiers.put("kingsman", 2);
		groupIdentifiers.put("les-tuche", 3);
		groupIdentifiers.put("doctor-who", 4);
		groupIdentifiers.put("kono-subarashii-sekai-ni-shukufuku-wo", 5);
		groupIdentifiers.put("no-game-no-life", 6);
		groupIdentifiers.put("shingeki-no-kyojin", 7);
		groupIdentifiers.put("rezero-kara-hajimeru-isekai-seikatsu", 8);
		groupIdentifiers.put("boku-no-hero-academia", 9);
		groupIdentifiers.put("sword-art-online", 10);
		groupIdentifiers.put("seitokai-yakuindomo", 11);
		groupIdentifiers.put("d-frag", 12);
		groupIdentifiers.put("assassination-classroom", 13);
		groupIdentifiers.put("nichijou", 14);
		groupIdentifiers.put("one-punch-man", 15);
		groupIdentifiers.put("a-slient-voice", 16);
		groupIdentifiers.put("breaking-bad", 17);
		groupIdentifiers.put("south-park", 18);
		groupIdentifiers.put("cable-girl", 19);
		groupIdentifiers.put("rick-and-morty", 20);
		groupIdentifiers.put("young-sheldon", 21);
		groupIdentifiers.put("futurama", 22);
		groupIdentifiers.put("la-casa-de-papel", 23);
		
		Map<String, Integer> seasonIdentifiers = new HashMap<String, Integer>();
		seasonIdentifiers.put("Kono Subarashii Sekai ni Shukufuku wo!_____1", 1);
		seasonIdentifiers.put("Kono Subarashii Sekai ni Shukufuku wo! 2_____2", 2);
		seasonIdentifiers.put("No Game, No Life_____1", 3);
		seasonIdentifiers.put("Shingeki no Kyojin 2nd Season_____2", 4);
		seasonIdentifiers.put("Re:Zero kara Hajimeru Isekai Seikatsu_____1", 5);
		seasonIdentifiers.put("Boku no Hero Academia_____1", 6);
		seasonIdentifiers.put("Boku no Hero Academia_____2", 7);
		seasonIdentifiers.put("Boku no Hero Academia_____3", 8);
		seasonIdentifiers.put("Sword Art Online_____1", 9);
		seasonIdentifiers.put("Seitokai Yakuindomo_____1", 10);
		seasonIdentifiers.put("Seitokai Yakuindomo*_____2", 11);
		seasonIdentifiers.put("D-Frag!_____1", 12);
		seasonIdentifiers.put("Assassination Classroom_____1", 13);
		seasonIdentifiers.put("Assassination Classroom_____2", 14);
		seasonIdentifiers.put("Nichijou - My Ordinary Life_____1", 15);
		seasonIdentifiers.put("One Punch-Man_____1", 16);
		seasonIdentifiers.put("Breaking Bad_____1", 17);
		seasonIdentifiers.put("South Park_____1", 18);
		seasonIdentifiers.put("South Park_____2", 19);
		seasonIdentifiers.put("South Park_____3", 20);
		seasonIdentifiers.put("South Park_____4", 21);
		seasonIdentifiers.put("Cable Girl_____1", 22);
		seasonIdentifiers.put("Rick and Morty_____1", 23);
		seasonIdentifiers.put("Rick and Morty_____2", 24);
		seasonIdentifiers.put("Rick and Morty_____3", 25);
		seasonIdentifiers.put("Young Sheldon_____1", 26);
		seasonIdentifiers.put("Futurama_____1", 27);
		seasonIdentifiers.put("Futurama_____2", 28);
		seasonIdentifiers.put("Futurama_____3", 29);
		seasonIdentifiers.put("Futurama_____4", 30);
		seasonIdentifiers.put("Futurama_____5", 31);
		seasonIdentifiers.put("Futurama_____6", 32);
		seasonIdentifiers.put("Futurama_____7", 33);
		seasonIdentifiers.put("La Casa de Papel_____1", 34);
		seasonIdentifiers.put("La Casa de Papel_____2", 35);
		seasonIdentifiers.put("Doctor Who (2005)_____4", 36);
		seasonIdentifiers.put("Doctor Who (2005)_____5", 37);
		
		int mode = 7;
		
		switch (mode) {
			case 0:
				for (VideoGroup group : groups) {
					System.out.println(group.getIdentifier() + " [ " + group.getTitle() + " ]");
					
					for (VideoSeason season : group.getSeasons()) {
						System.out.println("\tSAISON " + season.getSeasonValue() + ":" + " [ " + season.getTitle() + " ]");
						
						for (VideoFile video : season.getVideos()) {
							System.out.println("\t\tEPISODE: " + video.getEpisodeValue() + " (available: " + video.isAvailable() + ")");
						}
					}
				}
				break;
			case 1:
				for (VideoGroup group : groups) {
					System.out.println(group.toString());
					
					for (VideoSeason season : group.getSeasons()) {
						System.out.println(season.toString());
						
						for (VideoFile video : season.getVideos()) {
							System.out.println(video.toString() + " [url= " + video.getUrl() + "]");
						}
					}
				}
				break;
			case 2: {
				String format = "INSERT INTO `video_group` (`slug`, `title`, `image`) VALUES ('%s', '%s', '%s');";
				
				for (VideoGroup group : groups) {
					// System.out.println(group.toString());
					
					String slug = group.getIdentifier().replace("video:group//", "").replace("_", "-").replace("--", "-");
					String title = group.getTitle();
					String imageUrl = group.getGroupImageUrl();
					
					if (slug.endsWith("-")) {
						slug = slug.substring(0, slug.length() - 1);
					}
					
					System.out.println(String.format(format, slug, title, imageUrl));
					
					// try {
					// Downloader.downloadFile(new File(slug + ".png"), imageUrl);
					// } catch (IOException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					
					// for (VideoSeason season : group.getSeasons()) {
					// System.out.println(season.toString());
					//
					// for (VideoFile video : season.getVideos()) {
					// System.out.println(video.toString() + " [url= " + video.getUrl() + "]");
					// }
					// }
				}
				break;
			}
			case 3: {
				String format = "INSERT INTO `video_series_season`(`title`, `season`, `image`, `status`, `groupID`) VALUES ('%s', %s, '%s', '%s', %s);";
				
				for (VideoGroup group : groups) {
					// System.out.println(group.toString());
					
					if (!(group.getVideoFileType().equals(VideoFileType.ANIME) || group.getVideoFileType().equals(VideoFileType.SERIE))) {
						continue; // Disable movies
					}
					
					String slug = group.getSlug();
					
					for (VideoSeason season : group.getSeasons()) {
						// System.out.println(season.toString());
						
						String title = season.getTitle();
						int seasonValue = Integer.parseInt(season.getSeasonValue());
						String imageUrl = season.getImageHdUrl() != null ? season.getImageHdUrl() : season.getImageUrl();
						String status = "AVAILABLE";
						int groupId = (int) getOrDefault(groupIdentifiers, slug, Integer.MIN_VALUE);
						
						System.out.println(String.format(format, title, seasonValue, imageUrl, status, groupId));
						
						// for (VideoFile video : season.getVideos()) {
						// System.out.println(video.toString() + " [url= " + video.getUrl() + "]");
						// }
					}
				}
				break;
			}
			case 4: {
				String format = "INSERT INTO `video_series_episode` (`url`, `episode`, `status`, `seasonID`) VALUES ('%s', '%s', '%s', %s);";
				
				for (VideoGroup group : groups) {
					// System.out.println(group.toString());
					
					if (!(group.getVideoFileType().equals(VideoFileType.ANIME) || group.getVideoFileType().equals(VideoFileType.SERIE))) {
						continue; // Disable movies
					}
					
					String slug = group.getSlug();
					
					for (VideoSeason season : group.getSeasons()) {
						// System.out.println(season.toString());
						
						int seasonValue = Integer.parseInt(season.getSeasonValue());
						int groupId = (int) getOrDefault(groupIdentifiers, slug, Integer.MIN_VALUE);
						int seasonId = (int) getOrDefault(seasonIdentifiers, season.getTitle() + "_____" + seasonValue, Integer.MIN_VALUE);
						
						for (VideoFile video : season.getVideos()) {
							// System.out.println(video.toString() + " [url= " + video.getUrl() + "]");
							
							String url = video.getUrl();
							String episode = video.getEpisodeValue();
							String status = video.isAvailable() ? "AVAILABLE" : "UNAVAILABLE";
							
							System.out.println(String.format(format, url, episode, status, seasonId));
						}
					}
				}
				break;
			}
			case 5: {
				String format = "INSERT INTO `video_movie` (`slug`, `title`, `coverImage`, `image`, `releaseDate`, `status`, `url`, `groupID`) VALUES ('%s', '%s', '%s', '%s', '0000-00-00', '%s', '%s', %s)";
				
				for (VideoGroup group : groups) {
					// System.out.println(group.toString());
					
					if (!(group.getVideoFileType().equals(VideoFileType.ANIMEMOVIE) || group.getVideoFileType().equals(VideoFileType.MOVIE))) {
						continue; // Disable series
					}
					
					if (group.hasSeason()) {
						continue; // What the fuck, you are not supposed to be here
					}
					
					VideoSeason season = group.getSeasons().get(0);
					
					String slug = group.getSlug();
					
					String title = season.getTitle();
					int seasonValue = Integer.parseInt(season.getSeasonValue());
					String imageUrl = season.getImageUrl();
					String bigImageUrl = season.getImageHdUrl();
					String status = "AVAILABLE";
					String url = season.getVideos().get(0).getUrl();
					int groupId = (int) getOrDefault(groupIdentifiers, slug, Integer.MIN_VALUE);
					
					System.out.println(String.format(format, slug, title, imageUrl, bigImageUrl, status, url, groupId));
					
				}
				break;
			}
			
			case 6: {
				String format = "INSERT INTO `bp_group`(`id`, `parent_id`, `title`, `image`, `type`) VALUES (%s, %s,'%s','%s','%s');";
				
				int parent = 4;
				
				for (VideoGroup group : groups) {
					// System.out.println("GROUP: " + group.getTitle());
					// if (!group.getTitle().contains("Black Mirror")) {
					// continue;
					// }
					
					if (!group.getVideoFileType().hasEpisode()) {
						continue;
					}
					
					int actualGroup = parent++;
					System.out.println(String.format(format, actualGroup, "NULL", group.getTitle(), String.format("https://assets.boxplay.io/img/groups/%s.jpg", group.getSlug()), "ROOT"));
					
					for (VideoSeason season : group.getSeasons()) {
						String seasonTitle = season.getTitle() + " S" + season.getSeasonValue();
						int actualSeason = parent++;
						
						System.out.println(String.format(format, actualSeason, actualGroup, seasonTitle, String.format("https://assets.boxplay.io/img/series/%s/season-%s.jpg", group.getSlug(), season.getSeasonValue()), "SEASON"));
						
						int episode = 1;
						VideoType oldVideoType = VideoType.EPISODE;
						for (VideoFile video : season.getVideos()) {
							if (oldVideoType != video.getVideoType()) {
								episode = 1;
							}
							oldVideoType = video.getVideoType();
							
							String episodeFormat = "INSERT INTO `bp_video`(`group_id`, `type`, `title`, `episode`, `release_date`, `language`, `size`, `status`, `url`) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
							
							String episodeValue = video.getEpisodeValue().replace(video.getVideoType().toString() + "/", "");
							String videoTitle = seasonTitle + video.getVideoType().getFormat() + episodeValue;
							String url = String.format("https://dl.boxplay.io/series/%s/season-%s/%s-%s.mp4", group.getSlug(), season.getSeasonValue(), video.getVideoType().toString().toLowerCase(), episodeValue);
							
							String language;
							switch (video.getLanguage()) {
								case FR: {
									language = "VF";
									break;
								}
								
								default:
								case EN: {
									language = "VO";
									break;
								}
								
								case JPSUBFR:
								case ENSUBFR: {
									language = "VOSTFR";
									break;
								}
							}
							
							System.out.println(String.format(episodeFormat, actualSeason, "EPISODE", videoTitle, episode++, "0000", language, 0, (video.isAvailable() ? "" : "UN") + "AVAILABLE", url));
						}
					}
				}
				
				break;
			}
			
			case 7: {
				break;
			}
		}
	}
	
	public void generateMysqlSyntax() {
		
	}
	
	public static Object getOrDefault(Map<?, ?> map, Object key, Object defaultValue) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		
		return defaultValue;
	}
	
}