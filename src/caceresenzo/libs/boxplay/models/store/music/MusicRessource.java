package caceresenzo.libs.boxplay.models.store.music;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.element.implementations.MusicElement;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicGenre;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicRessourceType;
import caceresenzo.libs.boxplay.models.store.music.utils.MusicClassificator;

public class MusicRessource extends MusicElement {
	
	public static final String IDENTIFIER_BASE = "%s//%s/%s";
	
	protected final String localIdentifier;
	protected String title, releaseDateString;
	protected MusicGroup parentGroup;
	protected MusicRessourceType musicRessourceType;
	protected ElementLanguage language;
	protected List<MusicGenre> genres;
	protected List<String> authors;
	protected int trackId = -1;
	protected boolean available;
	
	protected MusicRessource(MusicGroup parentGroup, String localIdentifier, MusicRessourceType musicRessourceType) {
		super(parentGroup.toString() + "//" + musicRessourceType.toString().toLowerCase() + "/" + localIdentifier);
		
		this.parentGroup = parentGroup;
		this.musicRessourceType = musicRessourceType;
		this.localIdentifier = localIdentifier;
	}
	
	public MusicRessource withTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public MusicRessource withReleaseDateString(String releaseDateString) {
		this.releaseDateString = releaseDateString;
		return this;
	}
	
	public String getReleaseDateString() {
		return releaseDateString;
	}
	
	public MusicGroup getParentGroup() {
		return parentGroup;
	}
	
	public MusicRessourceType getMusicRessourceType() {
		return musicRessourceType;
	}
	
	public MusicRessource withLanguage(ElementLanguage language) {
		this.language = language;
		return this;
	}
	
	public ElementLanguage getLanguage() {
		return language;
	}
	
	public MusicRessource withGenres(List<MusicGenre> genres) {
		this.genres = genres;
		return this;
	}
	
	public MusicRessource withGenres(String genresString) {
		return withGenres(MusicGenre.getGenresFromString(genresString));
	}
	
	public List<MusicGenre> getGenres() {
		return genres;
	}
	
	public MusicRessource withAuthors(String authorsString) {
		this.authors = new ArrayList<String>();
		authorsString = authorsString.trim();
		
		if (!authorsString.contains(";")) {
			authors.add(authorsString);
			
			if (getMusicRessourceType().equals(MusicRessourceType.MUSIC)) {
				MusicClassificator.getClassificator().put(authorsString, (MusicFile) this);
			}
		} else {
			String[] authorsArray = authorsString.split(";");
			for (String author : authorsArray) {
				if (author != null && !author.isEmpty()) {
					authors.add(author);
					
					if (getMusicRessourceType().equals(MusicRessourceType.MUSIC)) {
						MusicClassificator.getClassificator().put(authorsString, (MusicFile) this);
					}
				}
			}
		}
		
		return this;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	public MusicRessource withTrackId(int trackId) {
		this.trackId = trackId;
		return this;
	}
	
	public int getTrackId() {
		return trackId;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public MusicRessource isAvailable(boolean available) {
		this.available = available;
		return this;
	}
	
	public String formatAuthor() {
		if (authors == null) {
			return "";
		}
		
		String formatted = "";
		
		Iterator<String> iterator = authors.iterator();
		
		while (iterator.hasNext()) {
			formatted += iterator.next() + (iterator.hasNext() ? ", " : "");
		}
		
		return formatted;
	}
	
	@Override
	public String toString() {
		return String.format(IDENTIFIER_BASE, parentGroup.toString(), musicRessourceType.toString().toLowerCase(), localIdentifier);
	}
	
}