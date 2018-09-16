package caceresenzo.libs.boxplay.api.request.implementations.video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;

public abstract class VideoListApiRequest<T extends BaseVideoStoreElement> extends ApiRequest<List<T>> {
	
	public static final String JSON_KEY_ID = "id";
	public static final String JSON_KEY_TITLE = "title";
	public static final String JSON_KEY_IMAGE_URL = "image";
	
	protected VideoListApiRequest(String urlFormat) {
		super(urlFormat);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> processResponse(ApiResponse apiResponse) {
		List<T> items = new ArrayList<>();
		
		if (apiResponse.isUsable()) {
			List<Map<String, Object>> videoMaps = (List<Map<String, Object>>) apiResponse.getResponse();
			
			for (Map<String, Object> videoMap : videoMaps) {
				long id = (long) videoMap.get(JSON_KEY_ID);
				String title = (String) videoMap.get(JSON_KEY_TITLE);
				String imageUrl = (String) videoMap.get(JSON_KEY_IMAGE_URL);
				
				items.add(createItem(id, title, imageUrl));
			}
		}
		
		return items;
	}
	
	public abstract T createItem(long id, String title, String imageUrl);
	
}