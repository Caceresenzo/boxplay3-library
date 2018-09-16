package caceresenzo.libs.boxplay.api.request.implementations.video.series;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;

public class SeriesApiRequest extends ApiRequest {
	
	private int seriesId;
	
	public SeriesApiRequest(int seriesId) {
		super("series/%s");
		
		this.seriesId = seriesId;
	}
	
	@Override
	public String forge() {
		return String.format(urlFormat, seriesId);
	}

	@Override
	public Object processResponse(ApiResponse apiResponse) {
		// TODO Auto-generated method stub
		return null;
	}
	
}