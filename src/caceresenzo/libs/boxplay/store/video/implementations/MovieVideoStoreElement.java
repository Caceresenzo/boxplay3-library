package caceresenzo.libs.boxplay.store.video.implementations;

import caceresenzo.libs.boxplay.base.ElementStatus;
import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;

public class MovieVideoStoreElement extends BaseVideoStoreElement {
	
	private final int episode, releaseDate, runningTime;
	private final long fileSize;
	private final ElementStatus status;
	private final String url;
	
	public MovieVideoStoreElement(int group, String title, String imageUrl, int episode, int releaseDate, int runningTime, long fileSize, ElementStatus status, String url) {
		super(group, title, imageUrl);
		
		this.episode = episode;
		this.releaseDate = releaseDate;
		this.runningTime = runningTime;
		this.fileSize = fileSize;
		this.status = status;
		this.url = url;
	}
	
	public int getEpisode() {
		return episode;
	}
	
	public int getReleaseDate() {
		return releaseDate;
	}
	
	public int getRunningTime() {
		return runningTime;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public ElementStatus getStatus() {
		return status;
	}
	
	public String getUrl() {
		return url;
	}
	
}