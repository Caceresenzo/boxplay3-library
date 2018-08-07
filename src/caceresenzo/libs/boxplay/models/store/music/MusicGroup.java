package caceresenzo.libs.boxplay.models.store.music;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import caceresenzo.libs.boxplay.models.element.enums.ElementLanguage;
import caceresenzo.libs.boxplay.models.element.implementations.MusicElement;
import caceresenzo.libs.boxplay.models.store.music.enums.MusicAuthorType;

public class MusicGroup extends MusicElement {
	
	public static final String IDENTIFIER_BASE = "music:group//";
	
	private String display;
	private ElementLanguage language;
	private MusicAuthorType musicAuthorType;
	private boolean recommended = false, downloadable = true;
	private List<String> authors;
	private List<MusicAlbum> albums;
	
	public MusicGroup(String identifier) {
		super(identifier);
		
		applyIdentifier(this);
	}
	
	public MusicGroup withDisplay(String display) {
		this.display = display;
		return this;
	}
	
	public String getDisplay() {
		if (display == null) {
			display = toString();
		}
		
		return display;
	}
	
	public MusicGroup withLanguage(ElementLanguage language) {
		this.language = language;
		return this;
	}
	
	public ElementLanguage getLanguage() {
		return language;
	}
	
	public MusicGroup withMusicAuthorType(MusicAuthorType musicAuthorType) {
		this.musicAuthorType = musicAuthorType;
		return this;
	}
	
	public MusicAuthorType getMusicAuthorType() {
		return musicAuthorType;
	}
	
	public MusicGroup isRecommended(boolean recommended) {
		this.recommended = recommended;
		return this;
	}
	
	public boolean isRecommended() {
		return recommended;
	}
	
	public MusicGroup isDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
		return this;
	}
	
	public boolean isDownloadable() {
		return downloadable;
	}
	
	public MusicGroup withAuthors(String authorsString) {
		this.authors = new ArrayList<String>();
		authorsString = authorsString.trim();
		
		if (!authorsString.contains(";")) {
			authors.add(authorsString);
		} else {
			String[] authorsArray = authorsString.split(";");
			for (String author : authorsArray) {
				if (author != null && !author.isEmpty()) {
					authors.add(author);
				}
			}
		}
		
		return this;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	public String getAuthorsString() {
		Iterator<String> iterator = authors.iterator();
		
		String authorsString = "";
		while (iterator.hasNext()) {
			authorsString = iterator.next() + (iterator.hasNext() ? ";" : "");
		}
		
		return authorsString;
	}
	
	public String getAuthor() {
		return authors.get(0);
	}
	
	public MusicGroup withAlbums(List<MusicAlbum> albums) {
		this.albums = albums;
		return this;
	}
	
	public List<MusicAlbum> getAlbums() {
		return albums;
	}
	
	@Override
	public String toString() {
		return IDENTIFIER_BASE + identifier;
	}
	
	public static MusicGroup instance(String name) {
		String identifier = IDENTIFIER_BASE + name;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (MusicGroup) INSTANCES.get(identifier);
		}
		
		return new MusicGroup(name);
	}
	
}