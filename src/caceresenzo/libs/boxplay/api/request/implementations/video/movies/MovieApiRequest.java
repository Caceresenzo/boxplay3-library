package caceresenzo.libs.boxplay.api.request.implementations.video.movies;

import java.util.Map;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.base.ElementStatus;
import caceresenzo.libs.boxplay.store.video.implementations.MovieVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;
import caceresenzo.libs.parse.ParseUtils;

public class MovieApiRequest extends ApiRequest<MovieVideoStoreElement> {
	
	/* Constants */
	public static final String JSON_KEY_GROUP = "group";
	public static final String JSON_KEY_GROUP_ITEM_ID = "id";
	public static final String JSON_KEY_GROUP_ITEM_TITLE = "title";
	public static final String JSON_KEY_GROUP_ITEM_IMAGE_URL = "image";
	public static final String JSON_KEY_GROUP_ITEM_TAGS = "tags";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_IMAGE_URL = "image";
	public static final String JSON_KEY_EPISODE = "episode";
	public static final String JSON_KEY_RELEASE_DATE = "release_date";
	public static final String JSON_KEY_RUNNING_TIME = "running_time";
	public static final String JSON_KEY_LANGUAGE = "language";
	public static final String JSON_KEY_FILE_SIZE = "size";
	public static final String JSON_KEY_STATUS = "status";
	public static final String JSON_KEY_URL = "url";
	
	/* Variables */
	private final long movieId;
	
	/* Constructor */
	public MovieApiRequest(long movieId) {
		super("movies/%s");
		
		this.movieId = movieId;
	}
	
	@Override
	public String forge() {
		return String.format(urlFormat, movieId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MovieVideoStoreElement processResponse(ApiResponse apiResponse) {
		if (apiResponse.isUsable()) {
			Map<String, Object> dataMap = (Map<String, Object>) apiResponse.getResponse();
			
			Map<String, Object> groupMap = (Map<String, Object>) dataMap.get(JSON_KEY_GROUP);
			long groupId = ParseUtils.parseLong(groupMap.get(JSON_KEY_GROUP_ITEM_ID), NO_ID);
			String groupTitle = (String) groupMap.get(JSON_KEY_GROUP_ITEM_TITLE);
			String groupImageUrl = (String) groupMap.get(JSON_KEY_GROUP_ITEM_IMAGE_URL);
			String groupTagsMask = (String) groupMap.get(JSON_KEY_GROUP_ITEM_TAGS);
			
			SimpleVideoStoreElement parentGroup = null;
			if (groupId != NO_ID) {
				parentGroup = new SimpleVideoStoreElement(groupId, groupTitle, groupImageUrl, BigIntegerBitSet.fromHex(groupTagsMask));
			}
			
			String title = (String) dataMap.get(JSON_KEY_TITLE);
			String imageUrl = (String) dataMap.get(JSON_KEY_IMAGE_URL);
			int episode = ParseUtils.parseInt(dataMap.get(JSON_KEY_EPISODE), 0);
			int releaseDate = ParseUtils.parseInt(dataMap.get(JSON_KEY_RELEASE_DATE), 0);
			int runningTime = ParseUtils.parseInt(dataMap.get(JSON_KEY_RUNNING_TIME), 0);
			long fileSize = ParseUtils.parseLong(dataMap.get(JSON_KEY_FILE_SIZE), 0);
			ElementStatus status = ElementStatus.fromString((String) dataMap.get(JSON_KEY_STATUS));
			String url = (String) dataMap.get(JSON_KEY_URL);
			
			return new MovieVideoStoreElement(parentGroup, title, imageUrl, BigIntegerBitSet.fromHex(groupTagsMask), episode, releaseDate, runningTime, fileSize, status, url);
		}
		
		return null;
	}
	
}