package caceresenzo.apps.boxplay.models.store.music;

import java.util.ArrayList;
import java.util.List;

import caceresenzo.apps.boxplay.models.store.music.enums.MusicRessourceType;

public class MusicAlbum extends MusicRessource {
	
	private List<MusicFile> musics;
	
	public MusicAlbum(MusicGroup parentGroup, String title) {
		super(parentGroup, title, MusicRessourceType.ALBUM);
		
		register(toString(), this);
	}
	
	public MusicAlbum withMusics(List<MusicFile> musics) {
		this.musics = musics;
		return this;
	}
	
	public MusicAlbum withMusic(MusicFile music) {
		List<MusicFile> musics = new ArrayList<MusicFile>();
		musics.add(music);
		this.musics = musics;
		return this;
	}
	
	public List<MusicFile> getMusics() {
		return musics;
	}
	
	@Override
	public String toString() {
		return super.toString(); // The the MusicRessource one
	}
	
	public static MusicAlbum instance(MusicGroup parentGroup, String localIdentifier) {
		String identifier = parentGroup.toString() + "//" + MusicRessourceType.ALBUM.toString().toLowerCase() + "/" + localIdentifier;
		
		if (INSTANCES.containsKey(identifier) && INSTANCES.get(identifier) != null) {
			return (MusicAlbum) INSTANCES.get(identifier);
		}
		
		return new MusicAlbum(parentGroup, localIdentifier);
	}
	
}