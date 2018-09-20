package caceresenzo.libs.boxplay.api.request.implementations.video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.api.request.RequestSettings;
import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.parse.ParseUtils;

public abstract class VideoListApiRequest<T extends BaseVideoStoreElement> extends ApiRequest<List<T>> {
	
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_IMAGE_URL = "image";
	public static final String JSON_KEY_TAGS_MASK = "tags";
	
	protected VideoListApiRequest(String urlFormat) {
		this(urlFormat, null);
	}
	
	protected VideoListApiRequest(String urlFormat, RequestSettings requestSettings) {
		super(urlFormat, requestSettings);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> processResponse(ApiResponse apiResponse) {
		List<T> items = new ArrayList<>();
		
		if (apiResponse.isUsable()) {
			List<Map<String, Object>> videoMaps = (List<Map<String, Object>>) apiResponse.getResponse();
			
			for (Map<String, Object> videoMap : videoMaps) {
				long id = ParseUtils.parseLong(videoMap.get(JSON_KEY_ID), NO_ID);
				String title = (String) videoMap.get(JSON_KEY_TITLE);
				String imageUrl = (String) videoMap.get(JSON_KEY_IMAGE_URL);
				String tagsMask = (String) videoMap.get(JSON_KEY_TAGS_MASK);
				
				items.add(createItem(id, title, imageUrl, tagsMask));
			}
		}
		
		return items;
	}
	
	public abstract T createItem(long id, String title, String imageUrl, String tagsMask);
	
}