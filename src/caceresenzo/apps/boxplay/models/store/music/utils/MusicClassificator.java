package caceresenzo.apps.boxplay.models.store.music.utils;

import java.util.HashMap;

import caceresenzo.apps.boxplay.models.store.music.MusicFile;

@SuppressWarnings("serial")
public class MusicClassificator extends HashMap<String, MusicFile> {
	
	private static MusicClassificator HASHMAP;
	
	private MusicClassificator() {
		; // Static only
	}
	
	public static MusicClassificator getClassificator() {
		if (HASHMAP == null) {
			HASHMAP = new MusicClassificator();
		}
		
		return HASHMAP;
	}
	
}