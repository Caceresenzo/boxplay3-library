package caceresenzo.libs.boxplay.api.request.implementations.tags;

import java.util.List;

import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.api.response.ApiResponse;
import caceresenzo.libs.boxplay.store.video.TagsCorresponder;

public class TagsApiRequest extends ApiRequest<TagsCorresponder> {
	
	public TagsApiRequest() {
		super("tags");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TagsCorresponder processResponse(ApiResponse<TagsCorresponder> apiResponse) {
		if (apiResponse.isUsable()) {
			List<String> tags = (List<String>) apiResponse.getResponse();
			
			return new TagsCorresponder(tags);
		}
		
		return null;
	}
	
}