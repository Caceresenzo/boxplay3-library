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
	
	private final List<String> playerUrls;
	
	/**
	 * Constructor, same as {@link VideoItemResultData} but with player's urls ready to be extracted
	 * 
	 * Also, no need to provider any page url for this item, it will be considered as null.
	 * 
	 * @param playerUrls
	 *            Player's URL ready to be extracted
	 */
	public CompletedVideoItemResultData(IVideoContentProvider videoContentProvider, String name, List<String> playerUrls) {
		super(videoContentProvider, null, name);
		
		this.playerUrls = playerUrls;
	}
	
	/**
	 * Get already get player urls
	 * 
	 * @return {@link List} of urls ready to be extracted
	 */
	public List<String> getPlayerUrls() {
		return playerUrls;
	}
	
	/**
	 * Get already get player urls
	 * 
	 * @return {@link List} of urls ready to be extracted as a {@link String} array
	 */
	public String[] getPlayerUrlsAsArray() {
		String[] array = new String[playerUrls.size()];
		return playerUrls.toArray(array);
	}
	
	@Override
	public String getUrl() {
		throw new IllegalStateException("Get a direct url from a CompletedVideoItemResultData is not possible.");
	}
	
}