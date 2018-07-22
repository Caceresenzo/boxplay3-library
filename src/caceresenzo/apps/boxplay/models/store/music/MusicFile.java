package caceresenzo.apps.boxplay.models.store.music;

import java.util.Comparator;

import caceresenzo.apps.boxplay.models.store.music.enums.MusicRessourceType;
import caceresenzo.libs.parse.ParseUtils;

public class MusicFile extends MusicRessource {
	
	public static final Comparator<MusicFile> COMPARATOR = new Comparator<MusicFile>() {
		@Override
		public int compare(MusicFile file1, MusicFile file2) {
			if (file1.getTrackId() == -1 || file2.getTrackId() == -1) {
				return file1.getTitle().compareTo(file2.getTitle());
			} else {
				// if (file1.getTrackId() > file2.getTrackId()) {
				// return +1;
				// } else if (file1.getTrackId() < file2.getTrackId()) {
				// return -1;
				// } else {
				// return 0;
				// }
				return file1.getTrackId() - file2.getTrackId();
			}
		}
	};
	
	private String url;
	private MusicAlbum parentAlbum;
	private String durationString;
	
	public MusicFile(MusicAlbum parentAlbum, int trackId, String localIdentifier) {
		super(parentAlbum.getParentGroup(), localIdentifier, MusicRessourceType.MUSIC);
		
		this.parentAlbum = parentAlbum;
		this.trackId = trackId;
		
		register(toString(), this);
	}
	
	public MusicFile withUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getUrl() {
		return url;
	}
	
	public MusicAlbum getParentAlbum() {
		return parentAlbum;
	}
	
	public MusicFile withDurationString(String durationString) {
		this.durationString = durationString;
		return this;
	}
	
	public String getDurationString() {
		return durationString;
	}
	
	@Override
	public String formatAuthor() {
		if (authors == null && parentAlbum != null) {
			authors = parentAlbum.getAuthors();
		}
		
		return super.formatAuthor();
	}
	
	@Override
	public String toString() {
		return parentAlbum.toString() + "//" + "track" + trackId + "/" + localIdentifier;
	}
	
	public static MusicFile instance(MusicAlbum parentAlbum, int trackId, String localIdentifier) {
		String identifier = parentAlbum.toString() + "//" + "track" + trackId + "/" + localIdentifier;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (MusicFile) INSTANCES.get(identifier);
		}
		
		return new MusicFile(parentAlbum, trackId, localIdentifier);
	}
	
	public String formatDuration() { // Parsing duration (pretty heavy)
		String[] durationUnits = durationString.split(":");
		String duration = "";
		for (int i = 0; i < durationUnits.length; i++) {
			int intValue = ParseUtils.parseInt(durationUnits[i], 0);
			
			if (intValue == 0) {
				continue;
			}
			
			switch (i) {
				case 0:
					duration += intValue + "h";
					// duration += intValue + STRING_MUSIC_DURATION_HOUR; // + (intValue > 1 ? "s" : "")
					break;
				case 1:
					duration += intValue + "m";
					// duration += intValue + STRING_MUSIC_DURATION_MINUTE; // + (intValue > 1 ? "s" : "")
					break;
				case 2:
					duration += intValue + "s";
					// duration += intValue + STRING_MUSIC_DURATION_SECONDE; // + (intValue > 1 ? "s" : "")
					break;
			}
		}
		
		return duration;
	}
	
}