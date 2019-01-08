package caceresenzo.libs.boxplay.common.extractor.video.model;

/**
 * Model class for video quality.
 * 
 * @author Enzo CACERES
 */
public class VideoQuality {
	
	/* Variables */
	private final String resolution, videoUrl;
	
	/* Constructor */
	public VideoQuality(String resolution, String videoUrl) {
		this.resolution = resolution;
		this.videoUrl = videoUrl;
	}
	
	/**
	 * @return Video's resolution, unformatted.
	 */
	public String getResolution() {
		return resolution;
	}
	
	/**
	 * @return Video's quality direct url.
	 */
	public String getVideoUrl() {
		return videoUrl;
	}
	
	/* To String */
	@Override
	public String toString() {
		return "VideoQuality[resolution=" + resolution + ", videoUrl=\"" + videoUrl + "\"]";
	}
	
	/* Hash Code */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());
		result = prime * result + ((videoUrl == null) ? 0 : videoUrl.hashCode());
		return result;
	}
	
	/* Equals */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VideoQuality other = (VideoQuality) obj;
		if (resolution == null) {
			if (other.resolution != null)
				return false;
		} else if (!resolution.equals(other.resolution))
			return false;
		if (videoUrl == null) {
			if (other.videoUrl != null)
				return false;
		} else if (!videoUrl.equals(other.videoUrl))
			return false;
		return true;
	}
	
}