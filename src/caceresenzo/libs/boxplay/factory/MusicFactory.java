package caceresenzo.libs.boxplay.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.element.enums.FileType;
import caceresenzo.libs.boxplay.models.store.music.MusicAlbum;
import caceresenzo.libs.boxplay.models.store.music.MusicFile;
import caceresenzo.libs.boxplay.models.store.music.MusicGroup;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicAuthorType;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicGenre;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicRessourceType;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.parse.ParseUtils;

public class MusicFactory extends AbstractFactory {
	
	public MusicFactory() {
		;
	}
	
	public void parseServerJson(MusicFactoryListener factoryListener, JsonObject jsonObject) {
		if (jsonObject == null) {
			factoryListener.onJsonNull();
			return;
		}
		JsonObject jsonFileArrayObject = (JsonObject) jsonObject.get(KEY_FILES_ROOT);
		JsonObject jsonMusicTypeArrayObject = null;
		
		for (Entry<?, ?> fileTypeEntry : jsonFileArrayObject.entrySet()) {
			if (fileTypeEntry.getKey().equals(FileType.MUSIC.getType())) {
				jsonMusicTypeArrayObject = (JsonObject) fileTypeEntry.getValue();
				break;
			}
		}
		
		if (jsonMusicTypeArrayObject == null) {
			factoryListener.onJsonMissingFileType();
			return;
		}
		
		for (Entry<?, ?> musicTypeEntry : jsonMusicTypeArrayObject.entrySet()) { // Parsing video/*
			HashMap<?, ?> musicFileTypeData = (HashMap<?, ?>) musicTypeEntry.getValue(); // JsonObject of files.type/music.""
			
			if (musicFileTypeData == null) {
				continue;
			}
			
			String groupIdentifier = (String) musicTypeEntry.getKey();
			String groupDisplay = ParseUtils.parseString(musicFileTypeData.get("display"), groupIdentifier);
			String groupAuthor = ParseUtils.parseString(musicFileTypeData.get(KEY_MUSIC_AUTHOR), groupDisplay, false);
			ElementLanguage groupLanguage = ElementLanguage.fromString((String) musicFileTypeData.get(KEY_LANGUAGE));
			MusicAuthorType musicType = MusicAuthorType.fromString((String) musicFileTypeData.get(KEY_MUSIC_TYPE));
			boolean groupRecommanded = ParseUtils.parseBoolean(musicFileTypeData.get(KEY_RECOMMENDED), false);
			String groupImageUrl = (String) musicFileTypeData.get(KEY_IMAGE_URL);
			String groupImageHdUrl = ParseUtils.parseString(musicFileTypeData.get(KEY_IMAGE_HD_URL), null);
			String groupDefaultImageUrl = ParseUtils.parseString(musicFileTypeData.get(KEY_DEFAULT_IMAGE_URL), null);
			
			// TODO: Parsing informations
			// List<String> videoInformations = ParseUtils.parseList(videoDataArrayEntry.get("informations"), new ArrayList<String>());
			
			MusicGroup group = MusicGroup //
					.instance(groupIdentifier) //
					.withDisplay(groupDisplay) //
					.withAuthors(groupAuthor) //
					.withLanguage(groupLanguage) //
					.withMusicAuthorType(musicType) //
					.isRecommended(groupRecommanded) //
					.withImageUrl(groupImageUrl) //
					.withImageHdUrl(groupImageHdUrl) //
					.withDefaultImageUrl(groupDefaultImageUrl) //
			;
			
			factoryListener.onMusicGroupCreated(group);
			
			List<MusicAlbum> albums = new ArrayList<MusicAlbum>();
			
			HashMap<?, ?> musicRessourcesArray = (HashMap<?, ?>) musicFileTypeData.get(KEY_MUSIC_RESSOURCES);
			
			if (musicRessourcesArray != null) {
				for (Entry<?, ?> musicRessourceData : musicRessourcesArray.entrySet()) {
					MusicRessourceType ressourceType = MusicRessourceType.fromString((String) musicRessourceData.getKey());
					HashMap<?, ?> ressourceArray = (HashMap<?, ?>) musicRessourceData.getValue();
					
					for (Entry<?, ?> ressourceEntry : ressourceArray.entrySet()) {
						String ressourceIdentifier = (String) ressourceEntry.getKey();
						HashMap<?, ?> ressourceData = (HashMap<?, ?>) ressourceEntry.getValue();
						
						switch (ressourceType) {
							case ALBUM: {
								String albumName = ParseUtils.parseString(ressourceData.get(KEY_MUSIC_ALBUM_NAME), groupDisplay);
								String albumAuthor = ParseUtils.parseString(ressourceData.get(KEY_MUSIC_AUTHOR), groupAuthor);
								String albumImageUrl = ParseUtils.parseString(ressourceData.get(KEY_IMAGE_URL), groupImageUrl);
								String albumImageHdUrl = ParseUtils.parseString(ressourceData.get(KEY_IMAGE_HD_URL), null);
								String albumReleaseDateString = ParseUtils.parseString(ressourceData.get(KEY_MUSIC_ALBUM_RELEASE_DATE_STRING), "--/--/--");
								ElementLanguage albumLanguage = ElementLanguage.fromString((String) ressourceData.get(KEY_LANGUAGE));
								List<MusicGenre> albumGenres = MusicGenre.getGenresFromString(ParseUtils.parseString(ressourceData.get(KEY_MUSIC_GENRE), null));
								boolean available = ParseUtils.parseBoolean(ressourceData.get(KEY_MUSIC_ALBUM_AVAILABLE), true);
								
								MusicAlbum album = (MusicAlbum) MusicAlbum //
										.instance(group, ressourceIdentifier) //
										.withTitle(albumName) //
										.withAuthors(albumAuthor) //
										.withImageUrl(albumImageUrl) //
										.withImageHdUrl(albumImageHdUrl) //
										.withReleaseDateString(albumReleaseDateString) //
										.withLanguage(albumLanguage == null ? groupLanguage : albumLanguage) //
										.withGenres(albumGenres) //
										.isAvailable(available) //
								;
								
								albums.add(album);
								
								List<MusicFile> musics = new ArrayList<MusicFile>();
								
								HashMap<?, ?> musicArray = (HashMap<?, ?>) ressourceData.get(KEY_MUSIC_ALBUM_MUSICS);
								if (musicArray != null) {
									for (Entry<?, ?> musicEntry : musicArray.entrySet()) {
										String musicIdentifier = (String) musicEntry.getKey();
										HashMap<?, ?> musicData = (HashMap<?, ?>) musicEntry.getValue();
										
										musics.add(structureMusicFile(album, musicIdentifier, musicData));
									}
								}
								Collections.sort(musics, MusicFile.COMPARATOR);
								
								album.withMusics(musics);
								break;
							}
							case MUSIC:
								break;
							
							default:
								break;
						}
					}
					
				}
			}
			// Collections.sort(seasons, VideoSeason.COMPARATOR);
			
			group.withAlbums(albums);
		}
	}
	
	private MusicFile structureMusicFile(MusicAlbum parentAlbum, String identifier, HashMap<?, ?> data) {
		String title = ParseUtils.parseString(data.get(KEY_MUSIC_RESSOURCE_TITLE), identifier);
		int trackId = ParseUtils.parseInt(data.get(KEY_MUSIC_RESSOURCE_TRACK_ID), -1);
		String url = ParseUtils.parseString(data.get(KEY_URL), null);
		String imageUrl = ParseUtils.parseString(data.get(KEY_IMAGE_URL), parentAlbum.getImageUrl());
		String imageHdUrl = ParseUtils.parseString(data.get(KEY_IMAGE_HD_URL), null);
		String durationString = ParseUtils.parseString(data.get(KEY_MUSIC_RESSOURCE_DURATION), null);
		boolean available = ParseUtils.parseBoolean(data.get(KEY_MUSIC_RESSOURCE_AVAILABLE), parentAlbum.isAvailable());
		
		MusicFile music = ((MusicFile) MusicFile //
				.instance(parentAlbum, trackId, identifier) //
				.withTitle(title) //
				.withImageUrl(imageUrl) //
				.withImageHdUrl(imageHdUrl) //
				.isAvailable(available) //
		) //
				.withUrl(url) //
				.withDurationString(durationString);
		;
		return music;
	}
	
	public static interface MusicFactoryListener {
		
		void onJsonNull();
		
		void onJsonMissingFileType();
		
		void onMusicGroupCreated(MusicGroup group);
		
	}
	
}