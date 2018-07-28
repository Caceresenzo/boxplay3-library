package caceresenzo.libs.boxplay.models.store.music.enums;

import java.util.ArrayList;
import java.util.List;

public enum MusicGenre {
	
	AFRICAN(), //
	ASIAN(), //
	COMEDY(), //
	COUNTRY(), //
	EASY_LISTENING(), //
	ELECTRONIC(), //
	FOLK(), //
	HIP_HOP(), //
	JAZZ(), //
	LATIN(), //
	POP(), //
	RAP(), //
	ROCK(), //
	SOCA(), //
	UNKNOWN(), //
	;
	
	private MusicGenre() {
		;
	}

	public boolean isNotUnknown() {
		return this != UNKNOWN;
	}
	
	public static MusicGenre fromString(String string) {
		for (MusicGenre type : MusicGenre.values()) {
			if (type.toString().equalsIgnoreCase(string)) {
				return type;
			}
		}
		return UNKNOWN;
	}
	
	public static List<MusicGenre> getGenresFromString(String string) {
		List<MusicGenre> genres = new ArrayList<MusicGenre>();
		
		if (string != null && !string.isEmpty()) {
			MusicGenre targetGenre = null;
			
			if (!string.contains(";")) {
				targetGenre = fromString(string);
				
				if (targetGenre != UNKNOWN) {
					genres.add(targetGenre);
				}
			} else {
				for (String genre : string.split(";")) {
					targetGenre = fromString(genre);
					
					if (targetGenre != UNKNOWN) {
						genres.add(targetGenre);
					}
				}
			}
		}
		
		return genres;
	}
	
}