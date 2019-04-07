package caceresenzo.libs.boxplay.culture.searchngo.data.models.content.completed;

import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;

/**
 * Same thing as a classic {@link VideoItemResultData} but this one already contain all of player's url found on his page
 * 
 * @author Enzo CACERES
 */
public class CompletedVideoItemResultData extends VideoItemResultData {
	
	/* Variables */
	private final List<String> playerUrls;
	private boolean requireMoreProcessing;
	
	/**
	 * Constructor, same as {@link VideoItemResultData} but with player's urls ready to be extracted.<br>
	 * Also, no need to provider any page url for this item, it will be considered as null.
	 * 
	 * @param playerUrls
	 *            Player's URL ready to be extracted.
	 */
	public CompletedVideoItemResultData(IVideoContentProvider videoContentProvider, String name, List<String> playerUrls) {
		super(videoContentProvider, null, name);
		
		this.playerUrls = playerUrls;
	}
	
	/**
	 * If call, this item should be processed again before getting real link.
	 * 
	 * @return Itself.
	 */
	public CompletedVideoItemResultData requireMoreProcessing() {
		requireMoreProcessing = true;
		
		return this;
	}
	
	/** @return Weather or not this item require more processing. */
	public boolean isMoreProcessingRequired() {
		return requireMoreProcessing;
	}
	
	/** @return {@link List} of urls ready to be extracted. */
	public List<String> getPlayerUrls() {
		return playerUrls;
	}
	
	/** @return {@link List} of urls ready to be extracted as a {@link String} array. */
	public String[] getPlayerUrlsAsArray() {
		String[] array = new String[playerUrls.size()];
		return playerUrls.toArray(array);
	}
	
	@Override
	public String getUrl() {
		throw new IllegalStateException("Get a direct url from a CompletedVideoItemResultData is not possible.");
	}
	
	@Override
	public String toString() {
		return "CompletedVideoItemResultData[playerUrls=" + playerUrls + ", name=" + name + "]";
	}
	
}