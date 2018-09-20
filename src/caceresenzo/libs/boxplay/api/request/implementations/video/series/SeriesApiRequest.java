package caceresenzo.libs.boxplay.api.request.implementations.video.series;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SeriesVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.series.SeriesSeasonVideoStoreElement;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;
import caceresenzo.libs.parse.ParseUtils;

public class SeriesApiRequest extends ApiRequest<SeriesVideoStoreElement> {
	
	/* Connstants */
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_IMAGE_URL = "image";
	public static final String JSON_KEY_TAGS = "tags";
	public static final String JSON_KEY_SEASONS = "seasons";
	public static final String JSON_KEY_SEASONS_ITEMS_ID = "id";
	public static final String JSON_KEY_SEASONS_ITEMS_TITLE = "title";
	public static final String JSON_KEY_SEASONS_ITEMS_IMAGE_URL = "image";
	public static final String JSON_KEY_SEASONS_ITEMS_TAGS = "tags";
	
	/* Variables */
	private final long seriesId;
	
	/* Constructor */
	public SeriesApiRequest(long seriesId) {
		super("series/%s");
		
		this.seriesId = seriesId;
	}
	
	@Override
	public String forge() {
		return String.format(urlFormat, seriesId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SeriesVideoStoreElement processResponse(ApiResponse apiResponse) {
		if (apiResponse.isUsable()) {
			Map<String, Object> dataMap = (Map<String, Object>) apiResponse.getResponse();
			
			long id = ParseUtils.parseLong(dataMap.get(JSON_KEY_ID), 0);
			String title = (String) dataMap.get(JSON_KEY_TITLE);
			String imageUrl = (String) dataMap.get(JSON_KEY_IMAGE_URL);
			String tagsMask = (String) dataMap.get(JSON_KEY_TAGS);
			List<SeriesSeasonVideoStoreElement> seasons = new ArrayList<>();
			
			List<Map<String, Object>> seasonsMap = (List<Map<String, Object>>) dataMap.get(JSON_KEY_SEASONS);
			for (Map<String, Object> seasonMap : seasonsMap) {
				long seasonId = ParseUtils.parseLong(seasonMap.get(JSON_KEY_SEASONS_ITEMS_ID), NO_ID);
				String seasonTitle = (String) seasonMap.get(JSON_KEY_SEASONS_ITEMS_TITLE);
				String seasonImageUrl = (String) seasonMap.get(JSON_KEY_SEASONS_ITEMS_IMAGE_URL);
				String seasonTags = (String) seasonMap.get(JSON_KEY_SEASONS_ITEMS_TAGS);
				
				if (seasonId != NO_ID) {
					seasons.add(new SeriesSeasonVideoStoreElement(seasonId, seasonTitle, seasonImageUrl, BigIntegerBitSet.fromHex(seasonTags)));
				}
			}
			
			return new SeriesVideoStoreElement(id, title, imageUrl, BigIntegerBitSet.fromHex(tagsMask), seasons);
		}
		
		return null;
	}
	
}