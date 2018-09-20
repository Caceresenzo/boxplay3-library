package caceresenzo.libs.boxplay.store.video.implementations;

import caceresenzo.libs.boxplay.base.ElementStatus;
import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;

public class MovieVideoStoreElement extends BaseVideoStoreElement {
	
	/* Variables */
	private final SimpleVideoStoreElement parentGroup;
	private final int episode, releaseDate, runningTime;
	private final long fileSize;
	private final ElementStatus status;
	private final String url;
	
	/* Constructor */
	public MovieVideoStoreElement(SimpleVideoStoreElement parentGroup, String title, String imageUrl, BigIntegerBitSet tags, int episode, int releaseDate, int runningTime, long fileSize, ElementStatus status, String url) {
		super(NO_ID, title, imageUrl, tags);
		
		this.parentGroup = parentGroup;
		this.episode = episode;
		this.releaseDate = releaseDate;
		this.runningTime = runningTime;
		this.fileSize = fileSize;
		this.status = status;
		this.url = url;
	}
	
	/**
	 * @return Parent group of this movie
	 */
	public SimpleVideoStoreElement getParentGroup() {
		return parentGroup;
	}
	
	/**
	 * @return Episode in the serie of movie
	 */
	public int getEpisode() {
		return episode;
	}
	
	/**
	 * @return Release date of the video
	 */
	public int getReleaseDate() {
		return releaseDate;
	}
	
	/**
	 * @return Rounded video length in minute
	 */
	public int getRunningTime() {
		return runningTime;
	}
	
	/**
	 * @return File size in byte
	 */
	public long getFileSize() {
		return fileSize;
	}
	
	/**
	 * @return Video status: {@link ElementStatus#AVAILABLE} or {@link ElementStatus#UNAVAILABLE}
	 */
	public ElementStatus getStatus() {
		return status;
	}
	
	/**
	 * @return Direct file url
	 */
	public String getUrl() {
		return url;
	}
	
	/* toString */
	@Override
	public String toString() {
		return "MovieVideoStoreElement[parent=" + super.toString() + ", groupParent=" + parentGroup + ", episode=" + episode + ", releaseDate=" + releaseDate + ", runningTime=" + runningTime + ", fileSize=" + fileSize + ", status=" + status + ", url=\"" + url + "\"]";
	}
	
}