package caceresenzo.libs.boxplay.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.factory.MusicFactory;
import caceresenzo.libs.boxplay.factory.MusicFactory.MusicFactoryListener;
import caceresenzo.libs.boxplay.models.store.music.MusicAlbum;
import caceresenzo.libs.boxplay.models.store.music.MusicFile;
import caceresenzo.libs.boxplay.models.store.music.MusicGroup;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.network.Downloader;

public class MusicTest {
	
	public static void main(String[] args) throws IOException, JsonException {
		MusicTest videoTest = new MusicTest();
		videoTest.initialize();
		videoTest.callFactory();
	}
	
	private MusicFactory videoFactory = new MusicFactory();
	
	private List<MusicGroup> groups;
	
	private JsonObject serverJsonData;
	
	public void initialize() throws IOException, JsonException {
		groups = new ArrayList<MusicGroup>();
		
		String content = Downloader.getUrlContent("https://caceres.freeboxos.fr:583/share/n9npNCIdbJr1Wbq8/Android/data/boxplay_3.json");
		serverJsonData = new JsonObject((Map<?, ?>) new JsonParser().parse(new StringReader(content)));
		
		// File output = new File("./output.txt");
		// output.createNewFile();
		// System.setOut(new PrintStream(new FileOutputStream(output)));
	}
	
	public void callFactory() {
		groups.clear();
		
		videoFactory.parseServerJson(new MusicFactoryListener() {
			@Override
			public void onJsonNull() {
				Logger.error("Json null");
			}
			
			@Override
			public void onJsonMissingFileType() {
				System.out.println("onJsonMissingFileType");
				
			}
			
			@Override
			public void onMusicGroupCreated(MusicGroup group) {
				groups.add(group);
			}
		}, serverJsonData);
		
		int mode = 1;
		
		switch (mode) {
			case 0:
				for (MusicGroup group : groups) {
					System.out.println(group.getIdentifier() + " [ " + group.getDisplay() + " ]");
					
					if (group.getAlbums() != null) {
						for (MusicAlbum album : group.getAlbums()) {
							System.out.println("\tALBUM " + album.getTitle() + ":" + " [ " + album.getTitle() + " ]");
							
							// for (VideoFile video : album.getVideos()) {
							// System.out.println("\t\tEPISODE: " + video.getEpisodeValue() + " (available: " + video.isAvailable() + ")");
							// }
						}
					}
				}
				break;
			case 1:
				for (MusicGroup group : groups) {
					System.out.println(group.toString());
					
					if (group.getAlbums() != null) {
						for (MusicAlbum album : group.getAlbums()) {
							System.out.println(album.toString());
							
							if (album.getMusics() != null) {
								for (MusicFile music : album.getMusics()) {
									System.out.println(music.toString());
								}
							}
						}
					}
				}
				break;
		}
	}
	
}